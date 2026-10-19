package org.cloudsimplus.schedulers.cloudlet.network;

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
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudsimplus.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.utilizationmodels.UtilizationModelFull;
import org.cloudsimplus.vms.network.NetworkVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks that {@link CloudletReceiveTask}s of a {@link NetworkCloudlet}
 * with multiple parent cloudlets running in the same VM receive just the packets they expect,
 * so that the simulation doesn't get stuck (issue #537).
 *
 * <p>The scenario is a small DAG: cloudlets A and B run in VM 0 and both send one packet
 * to cloudlet C, which runs in VM 1 (in another host).
 * C has one receive task for A's packet (R_A), then another one for B's packet (R_B).</p>
 *
 * @author Manoel Campos da Silva Filho
 */
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
    }

    /**
     * A and B finish at the same time, so their packets arrive together
     * and R_A could take both packets, making R_B wait forever.
     */
    @Test
    void allCloudletsFinishWhenParentPacketsArriveTogether() {
        final var dag = new Dag(LENGTH, LENGTH, true);
        runSimulation(dag);
        assertAllFinished(dag);
        assertReceivedFromRightSender(dag);
    }

    /**
     * B finishes first, so its packet arrives before A's one.
     * R_A must not take B's packet.
     */
    @Test
    void receiveTaskIgnoresPacketFromAnotherSenderInTheSameVm() {
        final var dag = new Dag(LENGTH, LENGTH / 2, true);
        runSimulation(dag);
        assertAllFinished(dag);
        assertReceivedFromRightSender(dag);
    }

    /**
     * Receive tasks created without a sender cloudlet just know the source VM.
     * Even so, each task must take only the number of packets it expects.
     */
    @Test
    void allCloudletsFinishWhenReceiveTasksDontKnowTheSender() {
        final var dag = new Dag(LENGTH, LENGTH, false);
        runSimulation(dag);
        assertAllFinished(dag);
        assertEquals(1, dag.receiveFromA.getPacketsReceived().size());
        assertEquals(1, dag.receiveFromB.getPacketsReceived().size());
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

    /**
     * The cloudlets of the DAG A → C ← B and the receive tasks of C.
     */
    private final class Dag {
        private final NetworkCloudlet a;
        private final NetworkCloudlet b;
        private final NetworkCloudlet c;
        private final CloudletReceiveTask receiveFromA;
        private final CloudletReceiveTask receiveFromB;

        /**
         * @param lengthA length of A's execution task (in MI)
         * @param lengthB length of B's execution task (in MI)
         * @param receiveTasksKnowSender whether C's receive tasks are created with the sender cloudlet
         *                               or just with the source VM
         */
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
        final var peList = new ArrayList<Pe>();
        IntStream.range(0, HOST_PES).forEach(_ -> peList.add(new PeSimple(MIPS)));
        final var host = new NetworkHost(RAM, BW, STORAGE, peList);
        host.setVmScheduler(new VmSchedulerTimeShared());
        return host;
    }

    private NetworkVm createVm(final int id) {
        final var vm = new NetworkVm(id, MIPS, HOST_PES);
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
