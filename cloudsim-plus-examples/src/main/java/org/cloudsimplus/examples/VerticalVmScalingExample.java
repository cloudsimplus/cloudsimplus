/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import static org.cloudbus.cloudsim.utilizationmodels.UtilizationModel.Unit;
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
import java.util.function.Predicate;

import static java.util.Comparator.comparingDouble;

/**
 * An example that scale VM RAM up, according to the arrival of Cloudlets.
 * It is used a {@link UtilizationModelDynamic} object to
 * define Vm RAM usage that will increment along the time.
 * A {@link VerticalVmScaling}
 * is set to each {@link #createListOfScalableVms(int) initially created VM},
 * that will check at {@link #SCHEDULING_INTERVAL specific time intervals}
 * if a VM RAM {@link #isVmRamOverloaded(Vm) is overloaded or not} to then
 * request the RAM to be scaled up.
 *
 * <p>The example uses the CloudSim Plus {@link EventListener} feature
 * to enable monitoring the simulation and dynamically creating objects such as Cloudlets and VMs.
 * It relies on
 * <a href="http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/Lambda-QuickStart/index.html">Java 8 Lambda Expressions</a>
 * to create an Listener for the {@link Simulation#addOnClockTickListener(EventListener) onClockTick event}
 * in order to get notified when the simulation clock advances and then create and submit new cloudlets.
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.2
 */
public class VerticalVmScalingExample {
    /**
     * The interval in which the Datacenter will schedule events.
     * As lower is this interval, sooner the processing of VMs and Cloudlets
     * is updated and you will get more notifications about the simulation execution.
     * However, as higher is this value, it can affect the simulation performance.
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
    private static final int SCHEDULING_INTERVAL = 10;

    private static final int HOSTS = 1;
    private static final int HOST_PES = 8;
    private static final int VMS = 1;
    public static final int VM_PES = 5;
    public static final int VM_RAM = 1200;
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
    private static final long CLOUDLET_LENGTHS[] = {400_000, 500_000, 600_000, 700_000, 800_000};

    private int createdCloudlets;
    private int createsVms;

    public static void main(String[] args) {
        new VerticalVmScalingExample();
    }

    /**
     * Default constructor that builds the simulation scenario and starts the simulation.
     */
    public VerticalVmScalingExample() {
        /*You can remove the seed to get a dynamic one, based on current computer time.
        * With a dynamic seed you will get different results at each simulation run.*/
        final long seed = 1;
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
        vmList.forEach(vm -> {
            Log.printFormatted("\t\tTime %6.1f: Vm %d Ram Usage: %6.2f%% (%4d of %4d MB)",
                eventInfo.getTime(), vm.getId(), vm.getRam().getPercentUtilization()*100.0,
                vm.getRam().getAllocatedResource(), vm.getRam().getCapacity());
            Log.printFormattedLine(" | Host Ram Allocation: %6.2f%% (%5d of %5d MB). Running Cloudlets: %d",
                vm.getHost().getRam().getPercentUtilization()*100,
                vm.getHost().getRam().getAllocatedResource(),
                vm.getHost().getRam().getCapacity(), vm.getCloudletScheduler().getCloudletExecList().size());
        });
    }

    private void printSimulationResults() {
        List<Cloudlet> finishedCloudlets = broker0.getCloudletsFinishedList();
        Comparator<Cloudlet> sortByVmId = comparingDouble(c -> c.getVm().getId());
        Comparator<Cloudlet> sortByStartTime = comparingDouble(c -> c.getExecStartTime());
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

        DatacenterCharacteristics characteristics = new DatacenterCharacteristicsSimple(hostList);
        Datacenter dc0 = new DatacenterSimple(simulation, characteristics, new VmAllocationPolicySimple());
        dc0.setSchedulingInterval(SCHEDULING_INTERVAL);
    }

    private Host createHost() {
        List<Pe> pesList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            pesList.add(new PeSimple(1000, new PeProvisionerSimple()));
        }

        ResourceProvisioner ramProvisioner = new ResourceProvisionerSimple(new Ram(20000));
        ResourceProvisioner bwProvisioner = new ResourceProvisionerSimple(new Bandwidth(100000));
        VmScheduler vmScheduler = new VmSchedulerTimeShared();
        final int id = hostList.size();
        return new HostSimple(id, 10000000, pesList)
            .setRamProvisioner(ramProvisioner)
            .setBwProvisioner(bwProvisioner)
            .setVmScheduler(vmScheduler);
    }

    /**
     * Creates a list of initial VMs in which each VM is able to scale horizontally
     * when it is overloaded.
     *
     * @param numberOfVms number of VMs to create
     * @return the list of scalable VMs
     * @see #createVerticalVmScaling(Vm)
     */
    private List<Vm> createListOfScalableVms(final int numberOfVms) {
        List<Vm> newList = new ArrayList<>(numberOfVms);
        for (int i = 0; i < numberOfVms; i++) {
            Vm vm = createVm();
            createVerticalVmScaling(vm);
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
            .setRam(VM_RAM).setBw(1000).setSize(10000).setBroker(broker0)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());
    }

    /**
     * Creates a {@link VerticalVmScaling} for the RAM of a given VM.
     *
     * @param vm the VM in which the VerticalVmScaling will be created
     * @see #createListOfScalableVms(int)
     */
    private void createVerticalVmScaling(Vm vm) {
        VerticalVmScaling verticalScaling = new VerticalVmScalingSimple(Ram.class, 0.3);
        verticalScaling.setOverloadPredicate(this::isVmRamOverloaded);
        verticalScaling.setUnderloadPredicate(this::isVmRamUnderloaded);
        vm.setRamVerticalScaling(verticalScaling);
    }

    /**
     * A {@link Predicate} that checks if a given VM is overloaded, based on an upper RAM utilization threshold.
     * A reference to this method is assigned to each Vertical VM Scaling created.
     *
     * @param vm the VM to check if its RAM is overloaded
     * @return true if the VM RAM is overloaded, false otherwise
     * @see #createVerticalVmScaling(Vm)
     */
    private boolean isVmRamOverloaded(Vm vm) {
        return vm.getRam().getPercentUtilization() > 0.7;
    }

    /**
     * A {@link Predicate} that checks if a given VM is underloaded, based on an lower RAM utilization threshold.
     * A reference to this method is assigned to each Vertical VM Scaling created.
     *
     * @param vm the VM to check if its RAM is underloaded
     * @return true if the VM RAM is underloaded, false otherwise
     * @see #createVerticalVmScaling(Vm)
     */
    private boolean isVmRamUnderloaded(Vm vm) {
        return vm.getRam().getPercentUtilization() < 0.5;
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
            .setUtilizationModelRam(ramUtilizationModel)
            .setBroker(broker0);
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
