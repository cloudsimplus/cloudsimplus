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
package org.cloudsimplus.benchmarks;

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
import org.cloudsimplus.util.Log;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * A benchmark that executes a simulation scenario
 * multiple times to assess the scalability of CloudSim Plus,
 * by means of simulation execution time.
 * Each simulation run increases the number of Hosts, VMs and Cloudlets.
 * That benchmark doesn't use JHM benchmarking framework.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.2.0
 */
public class SimulationTimeBenchmark {
    public static final int SIMULATIONS = 4;
    public static final int HOSTS_INCREMENT = 100;
    public static final int INITIAL_HOSTS_NUMBER = 100;
    private static final int HOST_PES = 64;

    private static final int VM_PES = 1;
    /**
     * Value used to multiply the number of VMs to get the number of Cloudlets.
     */
    private static final int VMS_MULTIPLIER = 55;

    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LENGTH = 1000;

    private final int hostsNumber;
    private final int vmsNumber;
    private final int cloudletsNumber;

    private final CloudSim simulation;
    private final int index;
    private static AtomicInteger lastIndex = new AtomicInteger(0);
    private final double finishTimeSecs;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;

    public static void main(String[] args) {
        System.out.printf("%s - %s%n%n", SimulationTimeBenchmark.class.getSimpleName(), CloudSim.VERSION);
        final double startTimeSecs = System.currentTimeMillis() / 1000.0;
        Log.setLevel(ch.qos.logback.classic.Level.ERROR);
        final List<SimulationTimeBenchmark> experiments =
            IntStream.iterate(INITIAL_HOSTS_NUMBER, hosts -> hosts + HOSTS_INCREMENT)
                     .limit(SIMULATIONS)
                     .mapToObj(SimulationTimeBenchmark::new)
                     .collect(toList());

        System.out.println();
        for (final SimulationTimeBenchmark exp : experiments) {
            System.out.printf(
                "Finished Simulation %2d: Hosts: %4d VMs: %5d Cloudlets: %6d Execution Time (secs): %8.1f%n",
                exp.index, exp.hostsNumber, exp.vmsNumber, exp.cloudletsNumber, exp.finishTimeSecs);
        }

        System.out.printf(
            "%nFinished all %d simulation runs in %s%n",
            SIMULATIONS, TimeUtil.secondsToStr(TimeUtil.elapsedSeconds(startTimeSecs)));
    }

    private SimulationTimeBenchmark(final int hostsNumber) {
        this.index = lastIndex.incrementAndGet();
        final double startTimeSecs = System.currentTimeMillis()/1000.0;
        System.out.printf("Starting simulation run %d/%d for %d hosts at %s%n", index, SIMULATIONS, hostsNumber, LocalTime.now());

        this.hostsNumber = hostsNumber;
        this.vmsNumber = hostsNumber;
        this.cloudletsNumber = vmsNumber* VMS_MULTIPLIER;
        simulation = new CloudSim();
        datacenter0 = createDatacenter();

        //Creates a broker that is a software acting on behalf a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);

        vmList = createVms();
        cloudletList = createCloudlets();
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);
        simulation.start();

        this.finishTimeSecs = TimeUtil.elapsedSeconds(startTimeSecs);
    }

    /**
     * Creates a Datacenter and its Hosts.
     */
    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(hostsNumber);
        for(int i = 0; i < hostsNumber; i++) {
            Host host = createHost();
            hostList.add(host);
        }

        //Uses a VmAllocationPolicySimple by default to allocate VMs
        return new DatacenterSimple(simulation, hostList).setSchedulingInterval(-1);
    }

    private Host createHost() {
        final List<Pe> peList = new ArrayList<>(HOST_PES);
        //List of Host's CPUs (Processing Elements, PEs)
        for (int i = 0; i < HOST_PES; i++) {
            //Uses a PeProvisionerSimple by default to provision PEs for VMs
            peList.add(new PeSimple(1000));
        }

        final long ram = 4096*HOST_PES; //in Megabytes
        final long bw = 100000*HOST_PES; //in Megabits/s
        final long storage = 1000000*HOST_PES; //in Megabytes

        /*
        Uses ResourceProvisionerSimple by default for RAM and BW provisioning
        and VmSchedulerSpaceShared for VM scheduling.
        */
        return new HostSimple(ram, bw, storage, peList);
    }

    /**
     * Creates a list of VMs.
     */
    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(vmsNumber);
        for (int i = 0; i < vmsNumber; i++) {
            //Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets
            final Vm vm = new VmSimple(1000, VM_PES);
            vm.setRam(512).setBw(1000).setSize(10000);
            list.add(vm);
        }

        return list;
    }

    /**
     * Creates a list of Cloudlets.
     */
    private List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new ArrayList<>(cloudletsNumber);

        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(0.5);

        for (int i = 0; i < cloudletsNumber; i++) {
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel);
            cloudlet.setSizes(1024);
            list.add(cloudlet);
        }

        return list;
    }
}
