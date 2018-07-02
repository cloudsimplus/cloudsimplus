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
package org.cloudsimplus.examples.autoscaling;

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
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.autoscaling.HorizontalVmScaling;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.autoscaling.VerticalVmScalingSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.comparingDouble;
import static org.cloudbus.cloudsim.utilizationmodels.UtilizationModel.Unit;

/**
 * An example that scales VM RAM up or down, according to current Cloudlets requests.
 * It is used a {@link UtilizationModelDynamic} object to
 * define Vm RAM usage increasingly along the time.
 * Cloudlets are created with different length, so that they will finish gradually.
 * This way, it's possible to check that RAM usage decreases along the time.
 *
 * <p>A {@link VerticalVmScaling}
 * is set to each {@link #createListOfScalableVms(int) initially created VM},
 * that will check at {@link #SCHEDULING_INTERVAL specific time intervals}
 * if a VM RAM {@link #upperRamUtilizationThreshold(Vm) is overloaded or not},
 * according to a <b>static computed utilization threshold</b>.
 * Then it requests the RAM to be scaled up.</p>
 *
 * <p>The example uses the CloudSim Plus {@link EventListener} feature
 * to enable monitoring the simulation and dynamically creating objects such as Cloudlets and VMs.
 * It relies on
 * <a href="http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/Lambda-QuickStart/index.html">Java 8 Lambda Expressions</a>
 * to create a Listener for the {@link Simulation#addOnClockTickListener(EventListener) onClockTick event}
 * to get notifications when the simulation clock advances, then creating and submitting new cloudlets.
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.2.0
 * @see VerticalVmCpuScalingExample
 */
public class VerticalVmRamScalingExample {
    /**
     * The interval in which the Datacenter will schedule events.
     * As lower is this interval, sooner the processing of VMs and Cloudlets
     * is updated and you will get more notifications about the simulation execution.
     * However, it can affect the simulation performance.
     *
     * <p>For this example, a large schedule interval such as 15 will make that just
     * at every 15 seconds the processing of VMs is updated. If a VM is overloaded, just
     * after this time the creation of a new one will be requested
     * by the VM's {@link HorizontalVmScaling Horizontal Scaling} mechanism.</p>
     *
     * <p>If this interval is defined using a small value, you may get
     * more dynamically created VMs than expected. Accordingly, this value
     * has to be trade-off.
     * For more details, see {@link Datacenter#getSchedulingInterval()}.</p>
    */
    private static final int SCHEDULING_INTERVAL = 1;

    private static final int HOSTS = 1;
    private static final int HOST_PES = 8;
    private static final int VMS = 1;
    private static final int VM_PES = 5;
    private static final int VM_RAM = 800;
    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Host> hostList;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;

    /**
     * Different lengths to be used when creating Cloudlets.
     * For each VM, one Cloudlet with each one these lengths will be created.
     * Creating Cloudlets with different lengths, since some Cloudlets will finish prior to others along the time,
     * the VM resource usage will reduce when a Cloudlet finishes.
     */
    private static final long CLOUDLET_LENGTHS[] = {40_000, 50_000, 60_000, 70_000, 80_000};

    private int createdCloudlets;
    private int createsVms;

    public static void main(String[] args) {
        new VerticalVmRamScalingExample();
    }

    /**
     * Default constructor that builds the simulation scenario and starts the simulation.
     */
    private VerticalVmRamScalingExample() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        hostList = new ArrayList<>(HOSTS);
        vmList = new ArrayList<>(VMS);
        cloudletList = new ArrayList<>(CLOUDLET_LENGTHS.length);

        simulation = new CloudSim();
        simulation.addOnClockTickListener(this::onClockTickListener);

        createDatacenter();
        broker0 = new DatacenterBrokerSimple(simulation);

        vmList.addAll(createListOfScalableVms(VMS));

        createCloudletList();
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();

        printSimulationResults();
    }

    private void onClockTickListener(EventInfo eventInfo) {
        for (Vm vm : vmList) {
            System.out.printf("\t\tTime %6.1f: Vm %d Ram Usage: %6.2f%% (%4d of %4d MB)",
                eventInfo.getTime(), vm.getId(), vm.getRam().getPercentUtilization() * 100.0,
                vm.getRam().getAllocatedResource(), vm.getRam().getCapacity());

            System.out.printf(" | Host Ram Allocation: %6.2f%% (%5d of %5d MB). Running Cloudlets: %d",
                vm.getHost().getRam().getPercentUtilization() * 100,
                vm.getHost().getRam().getAllocatedResource(),
                vm.getHost().getRam().getCapacity(), vm.getCloudletScheduler().getCloudletExecList().size());
        }
    }

    private void printSimulationResults() {
        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        final Comparator<Cloudlet> sortByVmId = comparingDouble(c -> c.getVm().getId());
        final Comparator<Cloudlet> sortByStartTime = comparingDouble(Cloudlet::getExecStartTime);
        finishedCloudlets.sort(sortByVmId.thenComparing(sortByStartTime));

        new CloudletsTableBuilder(finishedCloudlets).build();
    }

    /**
     * Creates a Datacenter and its Hosts.
     */
    private void createDatacenter() {
        for (int i = 0; i < HOSTS; i++) {
            hostList.add(createHost());
        }

        Datacenter dc0 = new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
        dc0.setSchedulingInterval(SCHEDULING_INTERVAL);
    }

    private Host createHost() {
        final List<Pe> peList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(1000, new PeProvisionerSimple()));
        }

        final long ram = 20000; //in Megabytes
        final long bw = 100000; //in Megabytes
        final long storage = 10000000; //in Megabites/s
        return new HostSimple(ram, bw, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());
    }

    /**
     * Creates a list of initial VMs in which each VM is able to scale horizontally
     * when it is overloaded.
     *
     * @param numberOfVms number of VMs to create
     * @return the list of scalable VMs
     * @see #createVerticalRamScalingForVm(Vm)
     */
    private List<Vm> createListOfScalableVms(final int numberOfVms) {
        final List<Vm> newList = new ArrayList<>(numberOfVms);
        for (int i = 0; i < numberOfVms; i++) {
            Vm vm = createVm();
            createVerticalRamScalingForVm(vm);
            newList.add(vm);
        }

        return newList;
    }

    /**
     * Creates a Vm object.
     *
     * @return the created Vm
     */
    private Vm createVm() {
        final int id = createsVms++;

        return new VmSimple(id, 1000, VM_PES)
            .setRam(VM_RAM).setBw(1000).setSize(10000)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());
    }

    /**
     * Creates a {@link VerticalVmScaling} for the RAM of a given VM.
     *
     * @param vm the VM in which the VerticalVmScaling will be created
     * @see #createListOfScalableVms(int)
     */
    private void createVerticalRamScalingForVm(Vm vm) {
        VerticalVmScalingSimple verticalRamScaling = new VerticalVmScalingSimple(Ram.class, 0.1);
        /* By uncommenting the line below, you will see that, instead of gradually
         * increasing or decreasing the RAM, when the scaling object detects
         * the RAM usage is above or below the defined thresholds,
         * it will automatically calculate the amount of RAM to add/remove to
         * move the VM from the over or underload condition.
        */
        //verticalRamScaling.setResourceScaling(new ResourceScalingInstantaneous());
        verticalRamScaling.setLowerThresholdFunction(this::lowerRamUtilizationThreshold);
        verticalRamScaling.setUpperThresholdFunction(this::upperRamUtilizationThreshold);
        vm.setRamVerticalScaling(verticalRamScaling);
    }

    /**
     * Defines the minimum RAM utilization percentage that indicates a Vm is underloaded.
     * This function is using a statically defined threshold, but it would be defined
     * a dynamic threshold based on any condition you want.
     * A reference to this method is assigned to each Vertical VM Scaling created.
     *
     * @param vm the VM to check if its RAM underloaded.
     *        The parameter is not being used internally, that means the same
     *        threshold is used for any Vm.
     * @return the lower RAM utilization threshold
     */
    private double lowerRamUtilizationThreshold(Vm vm) {
        return 0.5;
    }

    /**
     * Defines the maximum RAM utilization percentage that indicates a Vm is overloaded.
     * This function is using a statically defined threshold, but it would be defined
     * a dynamic threshold based on any condition you want.
     * A reference to this method is assigned to each Vertical VM Scaling created.
     *
     * @param vm the VM to check if its RAM is overloaded.
     *        The parameter is not being used internally, that means the same
     *        threshold is used for any Vm.
     * @return the upper RAM utilization threshold
     */
    private double upperRamUtilizationThreshold(Vm vm) {
        return 0.7;
    }

    private void createCloudletList() {
        UtilizationModelDynamic ramModel = new UtilizationModelDynamic(Unit.ABSOLUTE, 200);
        for (long length: CLOUDLET_LENGTHS) {
            cloudletList.add(createCloudlet(ramModel, length));
        }

        ramModel = new UtilizationModelDynamic(Unit.ABSOLUTE, 10);
        ramModel
            .setMaxResourceUtilization(500)
            .setUtilizationUpdateFunction(this::utilizationIncrement);
        cloudletList.get(0).setUtilizationModelRam(ramModel);
    }

    private Cloudlet createCloudlet(UtilizationModel ramUtilizationModel, long length) {
        final int id = createdCloudlets++;
        //randomly selects a length for the cloudlet
        UtilizationModel utilizationFull = new UtilizationModelFull();
        return new CloudletSimple(id, length, 1)
            .setFileSize(1024)
            .setOutputSize(1024)
            .setUtilizationModelBw(utilizationFull)
            .setUtilizationModelCpu(utilizationFull)
            .setUtilizationModelRam(ramUtilizationModel);
    }

    /**
     * Increments the RAM resource utilization, that is defined in absolute values,
     * in 10MB every second.
     *
     * @param um the Utilization Model that called this function
     * @return the new resource utilization after the increment
     */
    private double utilizationIncrement(UtilizationModelDynamic um) {
        return um.getUtilization() + um.getTimeSpan()*10;
    }
}
