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
package org.cloudsimplus.examples.performance;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyFirstFit;
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
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.TimeUtil;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.util.Log;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toCollection;
import static org.cloudbus.cloudsim.util.TimeUtil.secondsToStr;

/**
 * An example that enables you to play with different parameters to
 * check how they may drastically impact simulation performance.
 * That includes the constants provided in this example
 * (mainly the {@link #SCHEDULING_INTERVAL}),
 * and the type of {@link VmAllocationPolicy} and {@link CloudletScheduler}
 * created.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.5.0
 */
public class PerformanceExample1 {
    /**
     * Setting the scheduling interval as -1 drastically reduces simulation time,
     * but cloudlets processing is just updated when a cloudlet finishes,
     * which may reduce simulation accuracy.
     * This parameter is a trade-off between performance and accuracy.
     * @see Datacenter#getSchedulingInterval()
     */
    private static final double SCHEDULING_INTERVAL = TimeUtil.hoursToSeconds(1);
    private static final int HOSTS = 50_000;
    private static final int HOST_PES = 64;

    private static final int VMS = HOSTS * 2;
    private static final int VM_PES = 4;

    private static final int CLOUDLET_PES = 2;
    private static final long CLOUDLET_LENGTH = 10_000_000_000L;

    /**
     * The percentage of RAM and BW each Cloudlet will request during all simulation execution
     * (in scale from 0 to 1).
     */
    private static final double RAM_BW_RESOURCE_UTILIZATION_PERCENT = 0.2;

    /**
     * A {@link Supplier} Function that returns a new instance of a specific {@link CloudletScheduler}.
     */
    private final Supplier<CloudletSchedulerTimeShared> cloudletSchedulerSupplier = CloudletSchedulerTimeShared::new;

    private final VmAllocationPolicyFirstFit vmAllocationPolicy = new VmAllocationPolicyFirstFit();

    private final CloudSim simulation;
    private final DatacenterBroker broker0;
    private final List<Vm> vmList;
    private final List<Cloudlet> cloudletList;

    public static void main(String[] args) {
        new PerformanceExample1();
    }

    private PerformanceExample1() {
        final double startSecs = TimeUtil.currentTimeSecs();
        System.out.printf("Start time: %s%n", LocalTime.now());
        Log.setLevel(ch.qos.logback.classic.Level.WARN);

        simulation = new CloudSim();
        createDatacenter();
        broker0 = new DatacenterBrokerSimple(simulation);
        cloudletList = new ArrayList<>(VMS);
        vmList = createVms();
        final String scheduling = SCHEDULING_INTERVAL > 0 ? TimeUtil.secondsToStr(SCHEDULING_INTERVAL) : String.valueOf(SCHEDULING_INTERVAL);
        System.out.printf(
            "%s -> Hosts: %,d VMs: %,d Cloudlets: %,d Cloudlet Length (MI): %,d Scheduling Interval: %s%n",
            CloudSim.VERSION, HOSTS, VMS, cloudletList.size(),
            CLOUDLET_LENGTH, scheduling);
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        System.out.printf(
            "Execution time: %s Simulated time: %s Finished Cloudlets: %d%n",
            secondsToStr(TimeUtil.elapsedSeconds(startSecs)),
            secondsToStr(simulation.clock()),
            finishedCloudlets.size());
    }

    private Datacenter createDatacenter() {
        final List<Host> hostList =
            IntStream.range(0, HOSTS)
                     .mapToObj(i -> createHost())
                     .collect(toCollection(() -> new ArrayList<>(HOSTS)));

        return new DatacenterSimple(simulation, hostList)
            .setVmAllocationPolicy(vmAllocationPolicy)
            .setSchedulingInterval(SCHEDULING_INTERVAL);
    }

    private Host createHost() {
        final List<Pe> peList =
            IntStream.range(0, HOST_PES)
                     .mapToObj(i -> new PeSimple(1000))
                     .collect(toCollection(() -> new ArrayList<>(HOST_PES)));

        final long ram = 2048; //in Megabytes
        final long bw = 10000; //in Megabits/s
        final long storage = 1000000; //in Megabytes
        final Host host = new HostSimple(ram, bw, storage, peList);
        host.setVmScheduler(new VmSchedulerTimeShared());
        return host;
    }

    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            Vm vm =
                new VmSimple(i, 1000, VM_PES)
                    .setCloudletScheduler(cloudletSchedulerSupplier.get())
                    .setRam(512).setBw(1000).setSize(10000);

            list.add(vm);
            cloudletList.add(createCloudlet(vm));
        }

        return list;
    }

    private Cloudlet createCloudlet(final Vm vm) {
        final UtilizationModel ramBwModel = new UtilizationModelDynamic(RAM_BW_RESOURCE_UTILIZATION_PERCENT);
        final Cloudlet cloudlet =
            new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES)
                .setFileSize(1024)
                .setOutputSize(1024)
                .setUtilizationModelBw(ramBwModel)
                .setUtilizationModelRam(ramBwModel)
                .setUtilizationModelCpu(new UtilizationModelFull())
                .setVm(vm);
        return cloudlet;
    }
}
