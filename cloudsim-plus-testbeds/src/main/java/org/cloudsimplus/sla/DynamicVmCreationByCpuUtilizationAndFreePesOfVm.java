/**
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
package org.cloudsimplus.sla;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import static java.util.Comparator.comparingDouble;
import java.util.List;
import java.util.function.Predicate;

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
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.autoscaling.HorizontalVmScaling;
import org.cloudsimplus.autoscaling.HorizontalVmScalingSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.slametrics.SlaContract;

/**
 * Example of dynamic creation of VMS at runtime, respecting the CPU usage limit
 * and the free number of each VM, thus selecting an "ideal" VM for a given
 * cloudlet, which will then minimize Cloudlet TaskTimeCompletion.
 *
 * @author raysaoliveira
 */
public class DynamicVmCreationByCpuUtilizationAndFreePesOfVm {

    private static final int SCHEDULING_INTERVAL = 5;

    private final CloudSim simulation;

    /**
     * The interval to request the creation of new Cloudlets.
     */
    private static final int CLOUDLETS_CREATION_INTERVAL = SCHEDULING_INTERVAL * 3;

    private static final int HOSTS = 50;
    private static final int HOST_PES = 32;
    private static final int VMS = 5;
    private final SlaContract contract;

    private DatacenterBroker broker0;
    private List<Host> hostList;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;

    /**
     * Different lengths that will be randomly assigned to created Cloudlets.
     */
    private static final long[] CLOUDLET_LENGTHS = {20000, 40000, 14000, 10000, 10000};
    private static final int VM_PES = 2;
    private ContinuousDistribution randCloudlet;

    private int createsVms;

    /**
     * The file containing the SLA Contract in JSON format.
     */
    public static final String METRICS_FILE = "SlaMetrics.json";

    public static void main(String[] args) throws FileNotFoundException, IOException {
        Log.printFormattedLine(" Starting... ");
        new DynamicVmCreationByCpuUtilizationAndFreePesOfVm();
    }
    private int createdCloudlets;
    private int CLOUDLETS = 5;

    public DynamicVmCreationByCpuUtilizationAndFreePesOfVm() throws FileNotFoundException, IOException {

        final long seed = 1;
        randCloudlet = new UniformDistr(0, CLOUDLET_LENGTHS.length, seed);
        hostList = new ArrayList<>(HOSTS);
        vmList = new ArrayList<>(VMS);
        cloudletList = new ArrayList<>(CLOUDLET_LENGTHS.length);

        simulation = new CloudSim();

        // Reading the sla contract and taking the metric values
        this.contract = SlaContract.getInstanceFromResourcesDir(getClass(), METRICS_FILE);

        simulation.addOnClockTickListener(this::createNewCloudlets);
        simulation.addOnClockTickListener(this::printVmsCpuUsage);
        simulation.addOnClockTickListener(this::cpuUtilization);

        createDatacenter();
        broker0 = new DatacenterBrokerSimple(simulation);

        vmList.addAll(createListOfScalableVms(VMS));

        createCloudletList();

        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();

        printSimulationResults();
    }

    private void printVmsCpuUsage(EventInfo eventInfo) {
        broker0.getVmExecList().sort(Comparator.comparingInt(Vm::getId));

        System.out.println();
        broker0.getVmExecList().forEach(vm
                -> System.out.printf("####Time %.0f: Vm %d CPU usage: %.2f. SLA: %.2f.\n",
                        eventInfo.getTime(), vm.getId(),
                        vm.getCpuPercentUsage(), getCustomerMaxCpuUtilization())
        );
        System.out.println();
    }

    private double getCustomerMaxCpuUtilization() {
        return contract.getCpuUtilizationMetric().getMaxDimension().getValue();
    }

    private Cloudlet createCloudlet() {
        final int id = createdCloudlets++;
        //randomly selects a length for the cloudlet
        final long length = CLOUDLET_LENGTHS[(int) randCloudlet.sample()];
        UtilizationModel utilization = new UtilizationModelFull();
        return new CloudletSimple(id, length, 2)
                .setFileSize(1024)
                .setOutputSize(1024)
                .setUtilizationModel(utilization);

    }

    private void createCloudletList() {
        for (int i = 0; i < CLOUDLETS; i++) {
            cloudletList.add(createCloudlet());
        }
    }


    /**
     * Creates new Cloudlets at every 10 seconds up to the 50th simulation
     * second. A reference to this method is set as the {@link EventListener} to
     * the {@link Simulation#addOnClockTickListener(EventListener)}. The method
     * is then called every time the simulation clock advances.
     *
     * @param eventInfo the information about the OnClockTick event that has
     * happened
     */
    private void createNewCloudlets(EventInfo eventInfo) {
        final long time = (long) eventInfo.getTime();
        if (time > 0 && time % CLOUDLETS_CREATION_INTERVAL == 0 && time <= 50) {
            final int numberOfCloudlets = 4;
            Log.printFormattedLine("\t#Creating %d Cloudlets at time %d.", numberOfCloudlets, time);
            List<Cloudlet> newCloudlets = new ArrayList<>(numberOfCloudlets);
            for (int i = 0; i < numberOfCloudlets; i++) {
                Cloudlet cloudlet = createCloudlet();
                cloudletList.add(cloudlet);
                newCloudlets.add(cloudlet);
            }

            broker0.submitCloudletList(newCloudlets);
        }
    }

    /**
     * Creates a Datacenter and its Hosts.
     */
    private void createDatacenter() {
        for (int i = 0; i < HOSTS; i++) {
            hostList.add(createHost());
        }

        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this
        // resource
        double costPerBw = 0.0; // the cost of using bw in this resource

        DatacenterCharacteristics characteristics
                = new DatacenterCharacteristicsSimple(hostList)
                        .setCostPerSecond(cost)
                        .setCostPerMem(costPerMem)
                        .setCostPerStorage(costPerStorage)
                        .setCostPerBw(costPerBw);

        Datacenter dc0 = new DatacenterSimple(simulation, characteristics, new VmAllocationPolicySimple());
        dc0.setSchedulingInterval(SCHEDULING_INTERVAL);
    }

    private Host createHost() {
        List<Pe> pesList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            pesList.add(new PeSimple(4000, new PeProvisionerSimple()));
        }

        ResourceProvisioner ramProvisioner = new ResourceProvisionerSimple();
        ResourceProvisioner bwProvisioner = new ResourceProvisionerSimple();
        VmScheduler vmScheduler = new VmSchedulerTimeShared();
        final int id = hostList.size();
        return new HostSimple(20480, 10000, 10000, pesList)
                .setRamProvisioner(ramProvisioner)
                .setBwProvisioner(bwProvisioner)
                .setVmScheduler(vmScheduler);
    }

   /**
     * Creates a VM object.
     *
     * @return the created VM
     */
    private Vm createVm() {
        final int id = createsVms++;

        Vm vm = new VmSimple(id, 1000, VM_PES)
                .setRam(512).setBw(1000).setSize(10000)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());
        return vm;
    }

    /**
     * Creates a {@link HorizontalVmScaling} object for a given VM.
     *
     * @param vm the VM in which the Horizontal Scaling will be created
     * @see #createListOfScalableVms(int)
     */
    private void createHorizontalVmScaling(Vm vm) {
        HorizontalVmScaling horizontalScaling = new HorizontalVmScalingSimple();
        horizontalScaling
                .setVmSupplier(this::createVm)
                .setOverloadPredicate(this::isVmOverloaded);
        vm.setHorizontalScaling(horizontalScaling);
    }

    /**
     * A {@link Predicate} that checks if a given VM is overloaded or not based
     * on response time max value. A reference to this method is assigned to
     * each Horizontal VM Scaling created.
     *
     * @param vm the VM to check if it is overloaded
     * @return true if the VM is overloaded, false otherwise
     * @see #createHorizontalVmScaling(Vm)
     */
    private boolean isVmOverloaded(Vm vm) {
        return vm.getCpuPercentUsage() > getCustomerMaxCpuUtilization();
    }


       /**
     * Creates a list of initial VMs in which each VM is able to scale
     * horizontally when it is overloaded.
     *
     * @param numberOfVms number of VMs to create
     * @return the list of scalable VMs
     * @see #createHorizontalVmScaling(Vm)
     */
    private List<Vm> createListOfScalableVms(final int numberOfVms) {
        List<Vm> newList = new ArrayList<>(numberOfVms);
        for (int i = 0; i < numberOfVms; i++) {
            Vm vm = createVm();
            createHorizontalVmScaling(vm);
            newList.add(vm);
        }

        return newList;
    }

    private void printSimulationResults() {
        List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        Comparator<Cloudlet> sortByVmId = comparingDouble(c -> c.getVm().getId());
        Comparator<Cloudlet> sortByStartTime = comparingDouble(Cloudlet::getExecStartTime);
        finishedCloudlets.sort(sortByVmId.thenComparing(sortByStartTime));

        new CloudletsTableBuilder(finishedCloudlets).build();
    }

      /**
     * Shows the cpu utilization
     *
     * @param eventInfo the {@link EventInfo} containing the data about the generated event
     * @return cpuUtilization
     */
    private void cpuUtilization(EventInfo eventInfo) {
        double cpuTime = 0;
        for (Cloudlet cloudlets : cloudletList) {
            cpuTime += cloudlets.getActualCpuTime();
        }
        Log.printFormattedLine("\n\t ----> CpuTime -> %f",(cpuTime * 100) / 100);
    }
}
