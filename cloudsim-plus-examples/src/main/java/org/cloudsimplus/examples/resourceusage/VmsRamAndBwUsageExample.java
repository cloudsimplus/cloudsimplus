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
package org.cloudsimplus.examples.resourceusage;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;

import java.util.*;

/**
 * Shows how to use the {@link Simulation#addOnClockTickListener(EventListener) onClockTick Listener}
 * to keep track os simulation clock and store VM's RAM and BW utilization along the time.
 * CloudSim Plus already has built-in features to obtain VM's CPU utilization.
 * Check {@link org.cloudsimplus.examples.power.PowerExample}.
 *
 * <p>The example uses the CloudSim Plus {@link EventListener} feature
 * to enable monitoring the simulation and dynamically collect RAM and BW usage.
 * It relies on
 * <a href="https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html">Java 8 Method References</a>
 * to set a method to be called for {@link Simulation#addOnClockTickListener(EventListener) onClockTick events}.
 * It enables getting notifications when the simulation clock advances, then creating and submitting new cloudlets.
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.1.2
 *
 * @see VmsCpuUsageExample
 * @see org.cloudsimplus.examples.power.PowerExample
 */
public class VmsRamAndBwUsageExample {
    /**
     * @see Datacenter#getSchedulingInterval()
     */
    private static final int SCHEDULING_INTERVAL = 1;

    private static final int HOSTS = 4;
    private static final int HOST_PES = 8;

    private static final int VMS = 2;
    private static final int VM_PES = 4;

    private static final int CLOUDLETS = 5;
    private static final int CLOUDLET_PES = 2;
    private static final int CLOUDLET_LENGTH = 10000;

    private final CloudSim simulation;
    private final DatacenterBroker broker0;
    private final List<Vm> vmList;
    private final List<Cloudlet> cloudletList;
    private final Datacenter datacenter0;

    /**
     * A map to store RAM utilization history for every VM.
     * Each key is a VM and each value is another map.
     * This entire data structure is usually called a multi-map.
     *
     * Such an internal map stores RAM utilization for a VM.
     * The keys of this internal map are the time the utilization was collected (in seconds)
     * and the value the utilization percentage (from 0 to 1).
     */
    private final Map<Vm, Map<Double, Double>> allVmsRamUtilizationHistory;

    /** @see #allVmsRamUtilizationHistory */
    private final Map<Vm, Map<Double, Double>> allVmsBwUtilizationHistory;

    public static void main(String[] args) {
        new VmsRamAndBwUsageExample();
    }

    private VmsRamAndBwUsageExample() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        simulation = new CloudSim();
        datacenter0 = createDatacenter();

        broker0 = new DatacenterBrokerSimple(simulation);

        vmList = createVms();
        cloudletList = createCloudlets();
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        allVmsRamUtilizationHistory = initializeUtilizationHistory();
        allVmsBwUtilizationHistory = initializeUtilizationHistory();
        simulation.addOnClockTickListener(this::onClockTickListener);

        simulation.start();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();

        printVmListResourceUtilizationHistory();
    }

    /**
     * Prints the RAM and BW utilization history of every Vm.
     */
    private void printVmListResourceUtilizationHistory() {
        System.out.println();
        for (Vm vm : vmList) {
            printVmUtilizationHistory(vm);
        }
    }

    /**
     * Prints the RAM and BW utilization history of a given Vm.
     */
    private void printVmUtilizationHistory(Vm vm) {
        System.out.println(vm + " RAM and BW utilization history");
        System.out.println("----------------------------------------------------------------------------------");

        //A set containing all resource utilization collected times
        final Set<Double> timeSet = allVmsRamUtilizationHistory.get(vm).keySet();

        final Map<Double, Double> vmRamUtilization = allVmsRamUtilizationHistory.get(vm);
        final Map<Double, Double> vmBwUtilization = allVmsBwUtilizationHistory.get(vm);

        for (final double time : timeSet) {
            System.out.printf(
                "Time: %10.1f secs | RAM Utilization: %10.2f%% | BW Utilization: %10.2f%%%n",
                time, vmRamUtilization.get(time) * 100, vmBwUtilization.get(time) * 100);
        }

        System.out.printf("----------------------------------------------------------------------------------%n%n");
    }


    /**
     * Initializes a map that will store utilization history for
     * some resource (such as RAM or BW) of every VM.
     * It also creates an empty internal map to store
     * the resource utilization for every VM along the simulation execution.
     * The internal map for every VM will be empty.
     * They are filled inside the {@link #onClockTickListener(EventInfo)}.
     */
    private Map<Vm, Map<Double, Double>> initializeUtilizationHistory() {
        //TreeMap sorts entries based on the key
        final Map<Vm, Map<Double, Double>> map = new HashMap<>(VMS);

        for (Vm vm : vmList) {
            map.put(vm, new TreeMap<>());
        }

        return map;
    }

    /**
     * Keeps track of simulation clock.
     * Every time the clock changes, this method is called.
     * To enable this method to be called at a defined
     * interval, you need to set the {@link Datacenter#setSchedulingInterval(double) scheduling interval}.
     *
     * @param evt information about the clock tick event
     * @see #SCHEDULING_INTERVAL
     */
    private void onClockTickListener(final EventInfo evt) {
        collectVmResourceUtilization(this.allVmsRamUtilizationHistory, Ram.class);
        collectVmResourceUtilization(this.allVmsBwUtilizationHistory, Bandwidth.class);
    }

    /**
     * Collects the utilization percentage of a given VM resource for every VM.
     * CloudSim Plus already has built-in features to obtain VM's CPU utilization.
     * Check {@link org.cloudsimplus.examples.power.PowerExample}.
     *
     * @param allVmsUtilizationHistory the map where the collected utilization for every VM will be stored
     * @param resourceClass the kind of resource to collect its utilization (usually {@link Ram} or {@link Bandwidth}).
     */
    private void collectVmResourceUtilization(final Map<Vm, Map<Double, Double>> allVmsUtilizationHistory, Class<? extends ResourceManageable> resourceClass) {
        for (Vm vm : vmList) {
            /*Gets the internal resource utilization map for the current VM.
            * The key of this map is the time the usage was collected (in seconds)
            * and the value the percentage of utilization (from 0 to 1). */
            final Map<Double, Double> vmUtilizationHistory = allVmsUtilizationHistory.get(vm);
            vmUtilizationHistory.put(simulation.clock(), vm.getResource(resourceClass).getPercentUtilization());
        }
    }

    /**
     * Creates a Datacenter and its Hosts.
     */
    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }

        final Datacenter dc = new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
        dc.setSchedulingInterval(SCHEDULING_INTERVAL);
        return dc;
    }

    private Host createHost() {
        List<Pe> peList = new ArrayList<>(HOST_PES);
        //List of Host's CPUs (Processing Elements, PEs)
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(1000, new PeProvisionerSimple()));
        }

        final long ram = 2048; //in Megabytes
        final long bw = 10000; //in Megabits/s
        final long storage = 1000000; //in Megabytes
        Host host = new HostSimple(ram, bw, storage, peList);
        host
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());
        return host;
    }

    /**
     * Creates a list of VMs.
     */
    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            list.add(createVm(VM_PES));
        }
        return list;
    }

    private Vm createVm(final int pes) {
        return new VmSimple(1000, pes)
            .setRam(1000).setBw(1000).setSize(10000)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());
    }

    /**
     * Creates a list of Cloudlets.
     */
    private List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        for (int i = 0; i < CLOUDLETS; i++) {
            list.add(createCloudlet());
        }

        return list;
    }

    /**
     * Creates a Cloudlet with specific {@link UtilizationModel} for RAM, BW and CPU.
     * You can change the method to use any {@link UtilizationModel} you want.
     * @return
     */
    private Cloudlet createCloudlet() {
        UtilizationModelDynamic ramUtilizationModel = new UtilizationModelDynamic(0.2);
        UtilizationModelDynamic bwUtilizationModel = new UtilizationModelDynamic(0.1);

        ramUtilizationModel.setUtilizationUpdateFunction(this::utilizationUpdate);
        bwUtilizationModel.setUtilizationUpdateFunction(this::utilizationUpdate);

        return new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES)
            .setFileSize(1024)
            .setOutputSize(1024)
            .setUtilizationModelCpu(new UtilizationModelFull())
            .setUtilizationModelRam(ramUtilizationModel)
            .setUtilizationModelBw(bwUtilizationModel);
    }

    /**
     * Defines how the Cloudlet's utilization of RAM and BW will increase along the simulation time.
     * @return the updated utilization for RAM or BW
     * @see #createCloudlet()
     */
    private double utilizationUpdate(UtilizationModelDynamic utilizationModel) {
        return utilizationModel.getUtilization() + utilizationModel.getTimeSpan() * 0.01;
    }
}
