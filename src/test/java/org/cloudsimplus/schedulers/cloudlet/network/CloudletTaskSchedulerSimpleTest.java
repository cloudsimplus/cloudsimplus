/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.schedulers.cloudlet.network;

import org.cloudsimplus.resources.CoreCapacity;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.network.CloudletExecutionTask;
import org.cloudsimplus.cloudlets.network.CloudletReceiveTask;
import org.cloudsimplus.cloudlets.network.CloudletSendTask;
import org.cloudsimplus.cloudlets.network.NetworkCloudlet;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.network.NetworkDatacenter;
import org.cloudsimplus.hosts.network.NetworkHost;
import org.cloudsimplus.network.switches.EdgeSwitch;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudsimplus.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.utilizationmodels.UtilizationModelFull;
import org.cloudsimplus.vms.network.NetworkVm;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Checks that [CloudletReceiveTask]s of a [NetworkCloudlet]
/// with multiple parent cloudlets running in the same VM receive just the packets they expect,
/// so that the simulation doesn't get stuck (issue #537).
///
/// The scenario is a small DAG: cloudlets A and B run in VM 0 and both send one packet
/// to cloudlet C, which runs in VM 1 (in another host).
/// C has one receive task for A's packet (R_A), then another one for B's packet (R_B).
///
/// It also checks that a cloudlet waiting for packets doesn't take a share of the VM's CPU (issue #556)
/// and that a receive task stops waiting when its timeout is reached (issue #147).
/// Without a timeout, a warning must be logged once if the task waits too long.
///
/// @author Manoel Campos da Silva Filho
class CloudletTaskSchedulerSimpleTest {
    private static final int HOSTS = 2;
    private static final int HOST_PES = 2;
    private static final long MIPS = 1000;
    private static final long RAM = 2048;
    private static final long BW = 10000;
    private static final long STORAGE = 100_000;
    private static final long PACKET_BYTES = 1000;
    private static final long LENGTH = 10_000;
    private static final double MAX_SIM_TIME = 1000;
    private static final double FINISH_TIME_TOLERANCE = 1;

    /// Timeout for receive tasks (in seconds), which is longer than the time
    /// to run an execution task of [#LENGTH] MI.
    private static final double TIMEOUT = 50;
    private static final String WAIT_WARNING_MSG = "has been waiting for";

    private ListAppender<ILoggingEvent> logAppender;

    /// The level of the scheduler logger before the test, or null if it inherits the root level.
    @Nullable
    private Level previousLogLevel;

    private CloudSimPlus simulation;
    private DatacenterBroker broker;
    private List<NetworkVm> vmList;

    @BeforeEach
    void setUp() {
        simulation = new CloudSimPlus();
        simulation.terminateAt(MAX_SIM_TIME);
        createDatacenter();
        broker = new DatacenterBrokerSimple(simulation);
        vmList = IntStream.range(0, HOSTS).mapToObj(this::createVm).toList();
        broker.submitVmList(vmList);

        captureSchedulerWarnings();
    }

    /// Captures the warnings of the scheduler logger.
    /// Its level is set because other tests may change the root logger level (such as to OFF).
    private void captureSchedulerWarnings() {
        logAppender = new ListAppender<>();
        logAppender.start();
        previousLogLevel = schedulerLogger().getLevel();
        schedulerLogger().setLevel(Level.WARN);
        schedulerLogger().addAppender(logAppender);
    }

    @AfterEach
    void tearDown() {
        schedulerLogger().detachAppender(logAppender);
        schedulerLogger().setLevel(previousLogLevel);
    }

    /// A and B finish at the same time, so their packets arrive together
    /// and R_A could take both packets, making R_B wait forever.
    @Test
    void allCloudletsFinishWhenParentPacketsArriveTogether() {
        final var dag = new Dag(LENGTH, LENGTH, true);
        runSimulation(dag);
        assertAllFinished(dag);
        assertReceivedFromRightSender(dag);
    }

    /// B finishes first, so its packet arrives before A's one.
    /// R_A must not take B's packet.
    @Test
    void receiveTaskIgnoresPacketFromAnotherSenderInTheSameVm() {
        final var dag = new Dag(LENGTH, LENGTH / 2, true);
        runSimulation(dag);
        assertAllFinished(dag);
        assertReceivedFromRightSender(dag);
    }

    /// Receive tasks created without a sender cloudlet just know the source VM.
    /// Even so, each task must take only the number of packets it expects.
    @Test
    void allCloudletsFinishWhenReceiveTasksDontKnowTheSender() {
        final var dag = new Dag(LENGTH, LENGTH, false);
        runSimulation(dag);
        assertAllFinished(dag);
        assertEquals(1, dag.receiveFromA.getPacketsReceived().size());
        assertEquals(1, dag.receiveFromB.getPacketsReceived().size());
    }

    /// While C is waiting for A's packet, it must not take a share of VM 1's CPU.
    /// This way, the other 2 cloudlets in VM 1 (which has 2 cores) must get
    /// a full core each (issue #556).
    @Test
    void cloudletWaitingForPacketsDoesNotTakeCpuShare() {
        final var dag = new Dag(LENGTH * 3, LENGTH, true);
        final var cloudlet1 = createCloudlet(vmList.get(1));
        final var cloudlet2 = createCloudlet(vmList.get(1));
        addExecutionTask(cloudlet1, LENGTH);
        addExecutionTask(cloudlet2, LENGTH);

        broker.submitCloudletList(List.of(dag.a, dag.b, dag.c, cloudlet1, cloudlet2));
        simulation.start();

        assertAllFinished(dag);
        final double expectedFinishTime = LENGTH / (double) MIPS;
        assertEquals(expectedFinishTime, cloudlet1.getFinishTime(), FINISH_TIME_TOLERANCE);
        assertEquals(expectedFinishTime, cloudlet2.getFinishTime(), FINISH_TIME_TOLERANCE);
    }

    /// VM 1 uses a space-shared scheduler and has 2 cores.
    /// Receivers R1 and R2 are submitted first, take both cores and wait for packets from sender S,
    /// which is in the same VM. S stays in the waiting list since there are no free cores.
    /// Since cloudlets waiting for packets don't hold their cores, S must run
    /// and send the packets, so that all cloudlets finish without a deadlock (issue #581).
    @Test
    void spaceSharedCloudletsWaitingForPacketsReleasePesToTheSender() {
        final var vm = vmList.get(1);
        vm.setCloudletScheduler(new CloudletSchedulerSpaceShared());
        final var receiver1 = createCloudlet(vm);
        final var receiver2 = createCloudlet(vm);
        final var sender = createCloudlet(vm);

        final var receiveTask1 = addReceiveTask(receiver1, sender, true);
        final var receiveTask2 = addReceiveTask(receiver2, sender, true);
        addExecutionTask(receiver1, LENGTH);
        addExecutionTask(receiver2, LENGTH);
        addExecutionTask(sender, LENGTH);
        addSendTask(sender, receiver1);
        addSendTask(sender, receiver2);

        broker.submitCloudletList(List.of(receiver1, receiver2, sender));
        simulation.start();

        assertTrue(sender.isFinished(), "Sender didn't finish");
        assertTrue(receiver1.isFinished(), "Receiver 1 didn't finish");
        assertTrue(receiver2.isFinished(), "Receiver 2 didn't finish");
        assertEquals(1, receiveTask1.getPacketsReceived().size());
        assertEquals(1, receiveTask2.getPacketsReceived().size());
    }

    /// A never sends the packet C is waiting for.
    /// C's receive task must time out, so that C runs its next task and finishes (issue #147).
    @Test
    void receiveTaskTimesOutWhenPacketIsNeverSent() {
        final var receiveTask = submitCloudletWaitingForPacketNeverSent().setTimeout(TIMEOUT);
        final var c = receiveTask.getCloudlet();
        simulation.start();

        assertTrue(c.isFinished(), "Cloudlet C didn't finish");
        assertTrue(receiveTask.isTimedOut());
        assertTrue(receiveTask.getPacketsReceived().isEmpty());
        assertEquals(receiveTask.getStartTime() + TIMEOUT, receiveTask.getFinishTime(), FINISH_TIME_TOLERANCE);
        final double expectedFinishTime = receiveTask.getFinishTime() + LENGTH / (double) MIPS;
        assertEquals(expectedFinishTime, c.getFinishTime(), FINISH_TIME_TOLERANCE);
    }

    /// The packets arrive before the timeout, so the receive tasks must not time out.
    @Test
    void receiveTaskDoesNotTimeOutWhenPacketsArriveBeforeTimeout() {
        final var dag = new Dag(LENGTH, LENGTH, true);
        dag.receiveFromA.setTimeout(TIMEOUT);
        dag.receiveFromB.setTimeout(TIMEOUT);
        runSimulation(dag);

        assertAllFinished(dag);
        assertReceivedFromRightSender(dag);
        assertFalse(dag.receiveFromA.isTimedOut());
        assertFalse(dag.receiveFromB.isTimedOut());
    }

    /// C waits forever for a packet A never sends. Since the simulation keeps running
    /// up to [#MAX_SIM_TIME], many updates happen after the wait warning time,
    /// but the warning must be logged just once.
    @Test
    void warnsOnceWhenCloudletWaitsTooLongWithoutTimeout() {
        final var receiveTask = submitCloudletWaitingForPacketNeverSent();
        simulation.start();

        assertTrue(receiveTask.isActive());
        assertEquals(1, countWaitWarnings());
    }

    /// Two cloudlets in VM 1 wait forever for packets.
    /// The warning must be logged just for the first one.
    @Test
    void warnsJustForTheFirstTaskWaitingTooLong() {
        final var receiveTasks = submitCloudletsWaitingForPacketNeverSent(2);
        simulation.start();

        receiveTasks.forEach(task -> assertTrue(task.isActive()));
        assertEquals(1, countWaitWarnings());
    }

    /// The task has a timeout longer than the [CloudletReceiveTask#WAIT_WARNING_TIME],
    /// so it must just time out, without the wait warning.
    @Test
    void doesNotWarnWaitingTooLongWhenTaskHasTimeout() {
        final var receiveTask = submitCloudletWaitingForPacketNeverSent();
        receiveTask.setTimeout(CloudletReceiveTask.WAIT_WARNING_TIME * 2);
        simulation.start();

        assertTrue(receiveTask.isTimedOut());
        assertEquals(0, countWaitWarnings());
    }

    @Test
    void doesNotWarnWhenPacketsArriveBeforeWaitWarningTime() {
        final var dag = new Dag(LENGTH, LENGTH, true);
        runSimulation(dag);

        assertAllFinished(dag);
        assertEquals(0, countWaitWarnings());
    }

    /// Submits cloudlet A (in VM 0), which never sends a packet,
    /// and cloudlet C (in VM 1), which waits for a packet from A.
    /// @return C's receive task
    private CloudletReceiveTask submitCloudletWaitingForPacketNeverSent() {
        return submitCloudletsWaitingForPacketNeverSent(1).getFirst();
    }

    /// Submits cloudlet A (in VM 0), which never sends a packet,
    /// and a given number of cloudlets in VM 1, each one waiting for a packet from A.
    /// @param waitingCloudlets number of cloudlets waiting for a packet from A
    /// @return the receive tasks of the waiting cloudlets
    private List<CloudletReceiveTask> submitCloudletsWaitingForPacketNeverSent(final int waitingCloudlets) {
        final var a = createCloudlet(vmList.get(0));
        addExecutionTask(a, LENGTH);
        final var cloudlets = new ArrayList<NetworkCloudlet>(List.of(a));
        final var receiveTasks = new ArrayList<CloudletReceiveTask>();
        for (int i = 0; i < waitingCloudlets; i++) {
            final var c = createCloudlet(vmList.get(1));
            receiveTasks.add(addReceiveTask(c, a, true));
            addExecutionTask(c, LENGTH);
            cloudlets.add(c);
        }

        broker.submitCloudletList(cloudlets);
        return receiveTasks;
    }

    private long countWaitWarnings() {
        return logAppender.list.stream()
                               .filter(evt -> evt.getLevel() == Level.WARN)
                               .filter(evt -> evt.getFormattedMessage().contains(WAIT_WARNING_MSG))
                               .count();
    }

    private static Logger schedulerLogger() {
        return (Logger) LoggerFactory.getLogger(CloudletTaskSchedulerSimple.class.getSimpleName());
    }

    private void runSimulation(final Dag dag) {
        broker.submitCloudletList(List.of(dag.a, dag.b, dag.c));
        simulation.start();
    }

    private static void assertAllFinished(final Dag dag) {
        assertTrue(dag.a.isFinished(), "Cloudlet A didn't finish");
        assertTrue(dag.b.isFinished(), "Cloudlet B didn't finish");
        assertTrue(dag.c.isFinished(), "Cloudlet C didn't finish");
    }

    private static void assertReceivedFromRightSender(final Dag dag) {
        assertSender(dag.a, dag.receiveFromA);
        assertSender(dag.b, dag.receiveFromB);
    }

    private static void assertSender(final NetworkCloudlet expectedSender, final CloudletReceiveTask task) {
        final var pkts = task.getPacketsReceived();
        assertEquals(1, pkts.size());
        assertEquals(expectedSender, pkts.getFirst().getSenderCloudlet());
    }

    /// The cloudlets of the DAG A → C ← B and the receive tasks of C.
    private final class Dag {
        private final NetworkCloudlet a;
        private final NetworkCloudlet b;
        private final NetworkCloudlet c;
        private final CloudletReceiveTask receiveFromA;
        private final CloudletReceiveTask receiveFromB;

        /// @param lengthA length of A's execution task (in MI)
        /// @param lengthB length of B's execution task (in MI)
        /// @param receiveTasksKnowSender whether C's receive tasks are created with the sender cloudlet
        /// or just with the source VM
        Dag(final long lengthA, final long lengthB, final boolean receiveTasksKnowSender) {
            a = createCloudlet(vmList.get(0));
            b = createCloudlet(vmList.get(0));
            c = createCloudlet(vmList.get(1));

            addExecutionTask(a, lengthA);
            addSendTask(a, c);
            addExecutionTask(b, lengthB);
            addSendTask(b, c);

            receiveFromA = addReceiveTask(c, a, receiveTasksKnowSender);
            receiveFromB = addReceiveTask(c, b, receiveTasksKnowSender);
            addExecutionTask(c, LENGTH);
        }
    }

    private void createDatacenter() {
        final var hostList = IntStream.range(0, HOSTS).mapToObj(_ -> createHost()).toList();
        final var dc = new NetworkDatacenter(simulation, hostList);
        final var edgeSwitch = new EdgeSwitch(simulation, dc);
        dc.addSwitch(edgeSwitch);
        hostList.forEach(edgeSwitch::connectHost);
    }

    private NetworkHost createHost() {
        final var host = new NetworkHost(RAM, BW, STORAGE, new CoreCapacity(HOST_PES, MIPS));
        host.setVmScheduler(new VmSchedulerTimeShared());
        return host;
    }

    private NetworkVm createVm(final int id) {
        final var vm = new NetworkVm(id, new CoreCapacity(HOST_PES, MIPS));
        vm.setRam(RAM / 2).setBw(BW / 2).setSize(STORAGE / 2)
          .setCloudletScheduler(new CloudletSchedulerTimeShared());
        return vm;
    }

    private NetworkCloudlet createCloudlet(final NetworkVm vm) {
        final var cloudlet = new NetworkCloudlet(1);
        cloudlet.setFileSize(1).setOutputSize(1).setUtilizationModelCpu(new UtilizationModelFull());
        cloudlet.setVm(vm).setBroker(broker);
        return cloudlet;
    }

    private static void addExecutionTask(final NetworkCloudlet cloudlet, final long length) {
        cloudlet.addTask(new CloudletExecutionTask(cloudlet.getTasks().size(), length));
    }

    private static void addSendTask(final NetworkCloudlet sender, final NetworkCloudlet receiver) {
        final var task = new CloudletSendTask(sender.getTasks().size());
        sender.addTask(task);
        task.addPacket(receiver, PACKET_BYTES);
    }

    private static CloudletReceiveTask addReceiveTask(
        final NetworkCloudlet receiver, final NetworkCloudlet sender, final boolean knowSender)
    {
        final int id = receiver.getTasks().size();
        final var task = knowSender ?
                            new CloudletReceiveTask(id, sender.getVm(), sender) :
                            new CloudletReceiveTask(id, sender.getVm());
        task.setExpectedPacketsToReceive(1);
        receiver.addTask(task);
        return task;
    }
}
