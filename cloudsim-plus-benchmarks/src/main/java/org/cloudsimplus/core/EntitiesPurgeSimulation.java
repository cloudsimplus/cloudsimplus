/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudsimplus.core;

import ch.qos.logback.classic.Level;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.util.TimeUtil;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

/**
 * An experiment to assess the time the simulation takes
 * when enabling or disabling purging of finished entities.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.0.0
 * @see CloudSim#CloudSim(boolean)
 */
public class EntitiesPurgeSimulation {
    private static final int HOST_PES = 8;
    private static final int VM_PES = 4;

    private static final int CLOUDLET_PES = 2;
    private static final int CLOUDLET_LENGTH = 10_000;

    private final int hostsNumber;
    private final int brokersNumber;
    private final int vmsByBroker;
    private final int cloudletsByBroker;

    private final CloudSim simulation;
    private final boolean purgeEvents;
    private List<DatacenterBroker> brokerList;
    private Datacenter datacenter0;

    private double lastProcessTimeSec;
    private int lastBrokerIndex;

    public static void main(String[] args) {
        final int hosts = 100;
        final int brokers = 10;
        new EntitiesPurgeSimulation(hosts, brokers, false);
        new EntitiesPurgeSimulation(hosts, brokers, true);
    }

    private EntitiesPurgeSimulation(final int hostsNumber, final int brokersNumber, final boolean purgeEntities) {
        Log.setLevel(Level.OFF);
        final double startTime = TimeUtil.currentTimeSecs();

        this.purgeEvents = purgeEntities;
        this.hostsNumber = hostsNumber;
        this.brokersNumber = brokersNumber;
        this.vmsByBroker = hostsNumber * 20;
        this.cloudletsByBroker = vmsByBroker * 4;

        simulation = new CloudSim(purgeEntities);
        datacenter0 = createDatacenter();
        brokerList = createBrokers();
        //simulation.addOnClockTickListener(this::onClockTickListener);

        simulation.start();
        final double endTimeSec = TimeUtil.elapsedSeconds(startTime);
        final String purge = purgeEntities ? "Entities Purging enabled" : "Entities Purging disabled";
        System.out.printf("Finished %s with %s in %s%n", getClass().getSimpleName(), purge, TimeUtil.secondsToStr(endTimeSec));
    }

    private void onClockTickListener(final EventInfo evt) {
        if(evt.getTime() < TimeUtil.daysToSeconds(2) && evt.getTime() - lastProcessTimeSec >= 60){
            final DatacenterBroker broker = brokerList.get(lastBrokerIndex);
            final List<Vm> vmList = createVms(broker, 10);
            vmList.forEach(vm -> createCloudlets(broker, 5));
            lastProcessTimeSec = evt.getTime();
            lastBrokerIndex = (lastBrokerIndex+1) % brokersNumber;
        }
    }

    private List<DatacenterBroker> createBrokers() {
        return IntStream.range(0, brokersNumber)
                        .mapToObj(i -> new DatacenterBrokerSimple(simulation))
                        .peek(this::createVmsAndCloudlets)
                        .collect(toList());
    }

    private void createVmsAndCloudlets(final DatacenterBroker broker) {
        createVms(broker, vmsByBroker);
        createCloudlets(broker, cloudletsByBroker);
    }

    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(hostsNumber);
        for(int i = 0; i < hostsNumber; i++) {
            Host host = createHost();
            hostList.add(host);
        }

        return new DatacenterSimple(simulation, hostList);
    }

    private Host createHost() {
        final List<Pe> peList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(1000));
        }

        final long ram = 2048; //in Megabytes
        final long bw = 10000; //in Megabits/s
        final long storage = 1000000; //in Megabytes

        return new HostSimple(ram, bw, storage, peList);
    }

    private List<Vm> createVms(final DatacenterBroker broker, final int total) {
        final List<Vm> list = IntStream.range(0, total)
                                       .mapToObj(i -> createVm())
                                       .collect(toCollection(() -> new ArrayList<>(total)));

        broker.submitVmList(list);
        return list;
    }

    private Vm createVm() {
        final Vm vm = new VmSimple(1000, VM_PES);
        vm.setRam(512).setBw(1000).setSize(10000);
        return vm;
    }

    private void createCloudlets(final DatacenterBroker broker, final int cloudletsByBroker, final Vm vm) {
        final List<Cloudlet> list = IntStream.range(0, cloudletsByBroker)
                                             .mapToObj(i -> createCloudlet(vm))
                                             .collect(toCollection(() -> new ArrayList<>(cloudletsByBroker)));

        broker.submitCloudletList(list);
    }

    private void createCloudlets(final DatacenterBroker broker, final int total) {
        createCloudlets(broker, total, Vm.NULL);
    }

    private Cloudlet createCloudlet(final Vm vm) {
        final UtilizationModelDynamic model = new UtilizationModelDynamic(0.5);
        final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, model);
        cloudlet.setSizes(1024).setVm(vm);
        return cloudlet;
    }
}
