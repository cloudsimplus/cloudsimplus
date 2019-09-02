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
import org.cloudbus.cloudsim.util.Conversion;
import org.cloudbus.cloudsim.util.TimeUtil;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelStochastic;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.util.Log;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toCollection;

/**
 * An example showing how the {@link UtilizationModelStochastic} can affect
 * the simulation time and memory utilization in large scale simulations.
 *
 * <p>There are a set of constants you can change to see how the simulation
 * is impacted. The most impacting ones are {@link #CLOUDLETS},
 * {@link #CLOUDLET_LENGTH}, {@link #SCHEDULING_INTERVAL},
 * {@link #MULTIPLE_UTILIZATION_MODELS}
 * and {@link #STORE_CLOUDLETS_CPU_UTILIZATION_HISTORY}.
 * Just play with these values to see the results.</p>
 *
 * <p>Since the utilization history Map stored by the {@link UtilizationModelStochastic}
 * is commonly not used after the simulation finishes,
 * maintaining such history is a time and memory expensive operation.
 * This way, if you don't need such a history, you can
 * disable it by setting {@link #STORE_CLOUDLETS_CPU_UTILIZATION_HISTORY} to false.
 * However, keep in mind it is a trade-off between performance and memory consumption,
 * as it's explained in {@link UtilizationModelStochastic#setHistoryEnabled(boolean)}
 * documentation.
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.3.6
 * @see <a href="https://github.com/manoelcampos/cloudsim-plus/issues/197">Issue #197 for more details</a>
 */
public class UtilizationModelStochasticPerformance {
    private static final int HOSTS = 20_000;
    private static final int HOST_PES = 16;

    private static final int VMS = HOSTS*4;
    private static final int VM_PES = 4;

    private static final int CLOUDLETS = VMS*2;
    private static final int CLOUDLET_PES = 2;
    private static final int CLOUDLET_LENGTH = 10_000;
    private static final int SCHEDULING_INTERVAL = 10;

    /**
     * Defines if one {@link UtilizationModel} must be instantiated
     * for each Cloudlet or a single instance used by all Cloudlets.
     *
     * <p>Realize that if you use a single {@link UtilizationModel} for all
     * Cloudlets, the resource utilization will usually be the same
     * for all of them.
     * Using the {@link UtilizationModelStochastic}, if two Cloudlets
     * consecutively request the current resource utilization for the same time,
     * it will be returned the same value. If they send consecutive requests for different times,
     * probably it will be returned distinct randomly generated values.</p>
     */
    private static final boolean MULTIPLE_UTILIZATION_MODELS = false;

    /**
     * See {@link UtilizationModelStochastic#isAlwaysGenerateNewRandomUtilization()}
     * for details.
     */
    private static final boolean ALWAYS_GENERATE_NEW_RANDOM_UTILIZATION = false;

    /**
     * Indicates if the {@link UtilizationModelStochastic} instances
     * will keep a map that stores the resource utilization along simulation
     * execution. Disabling this flag reduces simulation time and memory consumption.
     */
    private static final boolean STORE_CLOUDLETS_CPU_UTILIZATION_HISTORY = true;

    private static final VmAllocationPolicy VM_ALLOCATION_POLICY = new VmAllocationPolicyFirstFit();
    private static final long SEED = 123456;

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    private UtilizationModelStochastic um;

    public static void main(String[] args) {
        new UtilizationModelStochasticPerformance();
    }

    private UtilizationModelStochasticPerformance() {
        System.out.println("Starting " + this.getClass().getSimpleName() + " at " + LocalTime.now());
        final long startMillis = System.currentTimeMillis();
        Log.setLevel(ch.qos.logback.classic.Level.WARN);

        simulation = new CloudSim();
        datacenter0 = createDatacenter();

        broker0 = new DatacenterBrokerSimple(simulation);

        vmList = createVms();
        cloudletList = createCloudlets();
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();
        final double maxHeapUtilizationGB = getMaxHeapUtilizationGB();

        final double execMinutes = TimeUtil.millisecsToMinutes(System.currentTimeMillis() - startMillis);
        System.out.printf(
            "Multiple Utilization Models: %s | Store Utilization History: %s%n%n",
            MULTIPLE_UTILIZATION_MODELS, STORE_CLOUDLETS_CPU_UTILIZATION_HISTORY);

        System.out.println("| Execution time | Simulation time | Max Heap Used | VmAllocationPolicy | Hosts      | VMs        | Cloudlets  | Cloudlet Len | DC Scheduling Interval |");
        System.out.println("| ---------------|-----------------|---------------|--------------------|------------|------------|------------|--------------|------------------------|");
        System.out.printf(
            "| %10.2f min | %11.2f min | %10.2f GB | %18s | %10d | %10d | %10d | %12d | %22d |",
            execMinutes, TimeUtil.secondsToMinutes(simulation.clock()), maxHeapUtilizationGB,
            VM_ALLOCATION_POLICY.getClass().getSimpleName().substring(18),
            HOSTS, VMS, CLOUDLETS, CLOUDLET_LENGTH, SCHEDULING_INTERVAL);
    }

    /**
     * Gets the maximum number of GB ever used by the application's heap.
     * @return the max heap utilization in GB
     * @see <a href="https://www.oracle.com/webfolder/technetwork/tutorials/obe/java/gc01/index.html">Java Garbage Collection Basics (for information about heap space)</a>
     */
    private double getMaxHeapUtilizationGB() {
        final double memoryBytes =
            ManagementFactory.getMemoryPoolMXBeans()
                             .stream()
                             .filter(bean -> bean.getType() == MemoryType.HEAP)
                             .filter(bean -> bean.getName().contains("Eden Space") || bean.getName().contains("Survivor Space"))
                             .map(MemoryPoolMXBean::getPeakUsage)
                             .mapToDouble(MemoryUsage::getUsed)
                             .sum();

        return Conversion.bytesToGigaBytes(memoryBytes);
    }

    private Datacenter createDatacenter() {
        final List<Host> hostList =
                IntStream.range(0, HOSTS)
                         .mapToObj(i -> createHost())
                         .collect(toCollection(() -> new ArrayList<>(HOSTS)));

        return new DatacenterSimple(simulation, hostList, VM_ALLOCATION_POLICY).setSchedulingInterval(SCHEDULING_INTERVAL);
    }

    private Host createHost() {
        final List<Pe> peList =
            IntStream.range(0, HOST_PES)
                     .mapToObj(i -> new PeSimple(1000))
                     .collect(toCollection(() -> new ArrayList<>(HOST_PES)));

        final long ramMB = 20480;
        final long bwMbps = 10000;
        final long storageMB = 1000000;
        return new HostSimple(ramMB, bwMbps, storageMB, peList);
    }

    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            final Vm vm = new VmSimple(1000, VM_PES);
            list.add(vm);
        }

        return list;
    }

    private List<Cloudlet> createCloudlets() {
        final List<Cloudlet> cloudlets = new ArrayList<>(CLOUDLETS);
        for (int i = 0; i < CLOUDLETS; i++) {
            final Cloudlet cloudlet = new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES);
            this.um = MULTIPLE_UTILIZATION_MODELS || this.um == null ? new UtilizationModelStochastic(SEED) : this.um;
            this.um
                .setHistoryEnabled(STORE_CLOUDLETS_CPU_UTILIZATION_HISTORY)
                .setAlwaysGenerateNewRandomUtilization(ALWAYS_GENERATE_NEW_RANDOM_UTILIZATION);
            cloudlet.setUtilizationModelCpu(um).setSizes(1024);
            cloudlets.add(cloudlet);
        }

        return cloudlets;
    }
}
