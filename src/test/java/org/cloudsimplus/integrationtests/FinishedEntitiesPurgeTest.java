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
package org.cloudsimplus.integrationtests;

import ch.qos.logback.classic.Level;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.util.Log;
import org.cloudsimplus.utilizationmodels.UtilizationModelFull;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;
import static org.junit.jupiter.api.Assertions.*;

/**
 * An integration test to assess the correctness of the simulation
 * after purging finished entities.
 * It creates a set of brokers, where each broker has the same number of VMs and cloudlets.
 * However, the 1st broker receives the same number of previous cloudlets
 * after their previous ones have finished.
 * All Cloudlets for a given broker have the same length,
 * however, cloudlets in different brokers have an arithmetically progressing length.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.0.0
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FinishedEntitiesPurgeTest {
    private static final int HOST_PES = 8;
    private static final int VM_PES = 2;

    private static final int CLOUDLET_PES = VM_PES;
    private static final long BASE_CLOUDLET_LENGTH = 1000;

    private static final double SCHEDULING_INTERVAL_SECS = 1;

    private static final int HOSTS_NUMBER = 1000;
    private static final int BROKERS_NUMBER = HOSTS_NUMBER/10;
    private static final int VMS_BY_BROKER = HOSTS_NUMBER / BROKERS_NUMBER * 4;
    private static final int CLOUDLETS_BY_BROKER = VMS_BY_BROKER;
    private static final int HOST_MIPS = 1000;

    /**
     * Total number of cloudlets created statically across all brokers, before simulation start,
     * which are expected to finish.
     */
    private static final double STATIC_CLOUDLETS_TO_FINISH = CLOUDLETS_BY_BROKER * BROKERS_NUMBER;

    /**
     * The maximum accepted difference in time results.
     */
    private static final double MAX_TIME_DELTA = 0.25;

    private CloudSimPlus simulation;
    private List<DatacenterBroker> brokerList;

    private long lastVmId;
    private long lastCloudletId;
    private boolean dynamicCloudletsSubmitted;
    private double previousEntitiesNumber;

    @BeforeAll
    void setUp() {
        buildAndStartSimulation();
    }

    @Test
    void cloudletExecTimeTime() {
        assertAll(getAllBrokersCloudletStream().map(cl -> () -> assertCloudletExecTime(cl)));
    }

    /**
     *
     * Since the cloudlets for the first broker finish in 1 second and the length
     * is increased according to {@link #BASE_CLOUDLET_LENGTH},
     * the broker order defines the time when its cloudlets are expected to finish.
     * The exception is for the dynamic submitted cloudlets for the first broker,
     * that start when its previous cloudlets finish.
     * */
    private void assertCloudletExecTime(final Cloudlet cl) {
        final double expectedCloudletFinishTime = getBrokerOrder(cl);
        final var msg = "%s on %s len %d start %.2f finish time"
            .formatted(cl, cl.getBroker(), cl.getLength(), cl.getStartTime());
        assertEquals(expectedCloudletFinishTime, cl.getTotalExecutionTime(), 0.7, msg);
    }

    /**
     * Gets the order of a broker inside its list (the broker at index 0 has order 1, and so on).
     * @param cloudlet the cloudlet to get its broker
     * @return
     */
    private long getBrokerOrder(final Cloudlet cloudlet) {
        return cloudlet.getBroker().getId() - 1;
    }

    @Test
    void finishedCloudlets() {
        assertAll(brokerList.stream().map(broker -> () -> assertExpectedFinishedCloudletList(broker)));
    }

    private void assertExpectedFinishedCloudletList(final DatacenterBroker broker) {
        final int expectedCloudletsByBroker = CLOUDLETS_BY_BROKER * (broker == brokerList.get(0) ? 2 : 1);
        final var msg = "%s finished Cloudlets".formatted(broker);
        assertEquals(expectedCloudletsByBroker, broker.getCloudletFinishedList().size(), msg);
    }

    private Stream<Cloudlet> getAllBrokersCloudletStream() {
        return brokerList.stream().flatMap(broker -> broker.getCloudletFinishedList().stream());
    }

    @Test()
    @DisplayName("Checks if brokers are being destroyed after becoming idle")
    void brokerShutdownTime() {
        assertAll(brokerList.stream().map(broker -> () -> assertBrokerShutDownTime(broker)));
    }

    /**
     * Checks if the broker shutdown time is the same as it's last finished VM.
     * The way the experiment is set, the time the broker shuts down
     * is it's id - 1. You can check that by just looking at the
     * the finish time of the broker's last cloudlet.
     * That rule doesn't apply only for the first broker,
     * that receives the double of cloudlets.
     * @param broker
     */
    private void assertBrokerShutDownTime(final DatacenterBroker broker) {
        final var msg = "%s shutdown time".formatted(broker);
        assertNotEquals(-1, broker.getShutdownTime(), 0.9, msg);
    }

    private void buildAndStartSimulation() {
        Log.setLevel(Level.WARN);
        simulation = new CloudSimPlus();
        createDatacenter();
        createBrokers();
        simulation.addOnClockTickListener(this::onClockTickListener);
        simulation.start();
    }

    private void onClockTickListener(final EventInfo evt) {
        //The statically submitted cloudlets for the first broker finish in 1 second
        final var broker0 = brokerList.get(0);
        if(evt.getTime() >= SCHEDULING_INTERVAL_SECS && !dynamicCloudletsSubmitted){
            dynamicCloudletsSubmitted = true;
            createCloudlets(broker0, BASE_CLOUDLET_LENGTH);
        } else if(evt.getTime() >= SCHEDULING_INTERVAL_SECS*2){
            /* After some time has passed since the static cloudlets submitted to the first broker
             * have finished, enable broker shutdown when it becomes idle. */
            broker0.setShutdownWhenIdle(true);
        }

        final int entitiesNumber = simulation.getEntityList().size();
        if(entitiesNumber != previousEntitiesNumber){
            this.previousEntitiesNumber = entitiesNumber;
        }
    }

    private void createBrokers() {
        this.brokerList = new ArrayList<>();
        for (int i = 0; i < BROKERS_NUMBER; i++) {
            final DatacenterBroker broker = createBroker();
            brokerList.add(broker);
            createVmsAndCloudlets(broker);
        }

        /*The first broker is not destroyed automatically when it becomes idle.
        * Only after its dynamic cloudlets are created, this configuration is set.*/
        this.brokerList.get(0).setShutdownWhenIdle(false);
    }

    /**
     * Creates a broker that destroys finished VMs immediately.
     * This way, the broker will shutdown as soon as all VMs have finished,
     * instead of waiting the end of the simulation.
     * @return
     */
    private DatacenterBroker createBroker() {
        return new DatacenterBrokerSimple(simulation).setVmDestructionDelay(0.2);
    }

    private void createVmsAndCloudlets(final DatacenterBroker broker) {
        final var vmList = createVms(broker);
        final long length = BASE_CLOUDLET_LENGTH * brokerList.size();
        final List<Cloudlet> cloudlets = vmList.stream().map(vm -> createCloudlet(vm, length)).toList();
        broker.submitCloudletList(cloudlets);
    }

    private Datacenter createDatacenter() {
        final var hostList = new ArrayList<Host>(HOSTS_NUMBER);
        for(int i = 0; i < HOSTS_NUMBER; i++) {
            final var host = createHost();
            hostList.add(host);
        }

        return new DatacenterSimple(simulation, hostList).setSchedulingInterval(SCHEDULING_INTERVAL_SECS);
    }

    private Host createHost() {
        final List<Pe> peList = IntStream.range(0, HOST_PES)
                                    .mapToObj(i -> new PeSimple(HOST_MIPS))
                                    .collect(toCollection(() -> new ArrayList<>(HOST_PES)));

        final long ram = 2048; //in Megabytes
        final long bw = 10000; //in Megabits/s
        final long storage = 1000000; //in Megabytes

        return new HostSimple(ram, bw, storage, peList);
    }

    private List<Vm> createVms(final DatacenterBroker broker) {
        final var vmList = IntStream.range(0, VMS_BY_BROKER)
                                    .mapToObj(i -> createVm())
                                    .collect(toCollection(() -> new ArrayList<>(VMS_BY_BROKER)));

        broker.submitVmList(vmList);
        return vmList;
    }

    private Vm createVm() {
        final Vm vm = new VmSimple(lastVmId++, HOST_MIPS, VM_PES);
        vm.setRam(512).setBw(1000).setSize(10000);
        return vm;
    }

    private void createCloudlets(final DatacenterBroker broker, final long length) {
        createCloudlets(broker, Vm.NULL, length);
    }

    private void createCloudlets(final DatacenterBroker broker, final Vm vm, final long length) {
        final var cloudletList = IntStream.range(0, CLOUDLETS_BY_BROKER)
                                          .mapToObj(i -> createCloudlet(vm, length))
                                          .collect(toCollection(() -> new ArrayList<>(CLOUDLETS_BY_BROKER)));

        broker.submitCloudletList(cloudletList);
    }

    private Cloudlet createCloudlet(final Vm vm, final long length) {
        final var cloudlet = new CloudletSimple(lastCloudletId++, length, CLOUDLET_PES);
        cloudlet.setUtilizationModelCpu(new UtilizationModelFull());
        cloudlet.setSizes(1024).setVm(vm);
        return cloudlet;
    }
}
