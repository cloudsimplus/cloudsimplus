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
package org.cloudsimplus.examples.listeners;

/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.TextTableBuilder;
import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.VmHostEventInfo;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;

/**
 * A simple example showing how to create a data center with 1 host that has
 * capacity to run just one Vm at a time. The example thus shows
 * how to parallelize the execution of multiple VMs.
 * By this way, it creates one Vm a time and enqueue the creation of
 * the next VMs.
 * It uses the new VM listeners to be notified when a host
 * is deallocated for a VM (in this case meaning that the VM has finished
 * executing) in order to place the next VM to run inside the host.
 *
 * @see Vm#addOnHostDeallocationListener(EventListener)
 * @see EventListener
 *
 * @author Manoel Campos da Silva Filho
 */
public class VmListenersExample3_DynamicVmCreation {
    /**
     * Number of Processor Elements (CPU Cores) of each Host.
     */
    private static final int HOST_PES_NUMBER = 2;

    /**
     * Number of Processor Elements (CPU Cores) of each VM and cloudlet.
     */
    private static final int VM_PES_NUMBER = HOST_PES_NUMBER;

    /**
     * Number of Cloudlets to create simultaneously.
     * Other cloudlets will be enqueued.
     */
    private static final int NUMBER_OF_CLOUDLETS = VM_PES_NUMBER;

    /**
     * Total number of VMs to create along the simulation.
     */
    private static final int TOTAL_NUMBER_OF_VMS = 3;

    private final List<Host> hostList;
    private final List<Vm> vmList;
    private final List<Cloudlet> cloudletList;
    private final List<DatacenterBroker> brokerList;
    private final Datacenter datacenter;
    private final CloudSim simulation;

    /**
     * Number of VMs that have finished executing all their cloudlets.
     */
    private int numberOfFinishedVms = 0;

    /**
     * Starts the example execution, calling the class constructor\
     * to build and run the simulation.
     *
     * @param args command line parameters
     */
    public static void main(String[] args) {
        Log.printFormattedLine("Starting %s ...", VmListenersExample3_DynamicVmCreation.class.getSimpleName());
        new VmListenersExample3_DynamicVmCreation();
        Log.printFormattedLine("%s finished!", VmListenersExample3_DynamicVmCreation.class.getSimpleName());
    }

    /**
     * Default constructor that builds and starts the simulation.
     */
    public VmListenersExample3_DynamicVmCreation() {
        simulation = new CloudSim();

        this.hostList = new ArrayList<>();
        this.brokerList = new ArrayList<>();
        this.vmList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();
        this.datacenter = createDatacenter();

        //Creates the first VM and its cloudlets
        createAndSubmitVmForNewBroker();

        /*Try to create another VM that will fail due to lack of available host PE,
        just to show that VMs have to be executed sequentially.
        See the log messages to check that the VM creation in fact fails.
        The next VMs will be created when the listener is notified about
        the deallocation of a host to a VM (that means the VM has finished executing).*/
        createAndSubmitVmForNewBroker();

        runSimulationAndPrintResults();
    }

    /**
     * A handler method that will be called when a VM
     * is removed from a host (in this case meaning that it has finished executing
     * its cloudlets). All VMs will use this same listener.
     *
     * @param eventInfo information about the happened event
     *
     * @see #createVm(int, DatacenterBroker)
     */
    private void onHostDeallocationListener(VmHostEventInfo eventInfo) {
        numberOfFinishedVms++;
        Log.printFormatted(
                "\t#EventListener: Vm %d finished running all its cloudlets at time %.0f. ",
                eventInfo.getVm().getId(), eventInfo.getTime());
        Log.printFormattedLine("VMs finished so far: %d", numberOfFinishedVms);

        createNextVmIfNotReachedMaxNumberOfVms();
    }

    /**
     * After a VM finishes executing, creates another one
     * if the number of created VMs is less than {@link #TOTAL_NUMBER_OF_VMS}.
     * By this way, it allows the sequential execution of several VMs into a host
     * that doesn't have enough PEs to execute them all simultaneously.
     */
    private void createNextVmIfNotReachedMaxNumberOfVms() {
        if(numberOfFinishedVms < TOTAL_NUMBER_OF_VMS) {
            Vm vm = createAndSubmitVmForNewBroker();
            Log.printFormattedLine("\tCreated VM %d at time %.0f", vm.getId(), simulation.clock());
        }
    }

    private void runSimulationAndPrintResults() {
        simulation.start();

        List<Cloudlet> cloudlets;
        String title;
        for(DatacenterBroker broker: brokerList){
            cloudlets = broker.getCloudletsFinishedList();
            title = broker.getName() + (cloudlets.size() > 0 ? "" : " (for the failed VM)");
            new CloudletsTableBuilder(cloudlets)
                    .setPrinter(new TextTableBuilder(title))
                    .build();
        }
    }

    /**
     * Creates cloudlets and submit them to the broker.
     * @param broker The DatacenterBroker that will submit the cloudlets
     * @param vm Vm to run the cloudlets to be created
     *
     * @see #createCloudlet(int, Vm, DatacenterBroker)
     */
    private void createAndSubmitCloudlets(DatacenterBroker broker, Vm vm) {
        int cloudletId = cloudletList.size();
        List<Cloudlet> list = new ArrayList<>(NUMBER_OF_CLOUDLETS);
        for(int i = 0; i < NUMBER_OF_CLOUDLETS; i++){
            Cloudlet cloudlet = createCloudlet(cloudletId++, vm, broker);
            list.add(cloudlet);
        }

        broker.submitCloudletList(list);
        cloudletList.addAll(list);
    }

    /**
     * Creates one Vm and a group of cloudlets to run inside it,
     * and submit the Vm and its cloudlets to a new broker.
     * @return the created VM
     * @see #createVm(int, org.cloudbus.cloudsim.brokers.DatacenterBroker)
     */
    private Vm createAndSubmitVmForNewBroker() {
        List<Vm> list = new ArrayList<>();
        DatacenterBroker broker = new DatacenterBrokerSimple(simulation);
        Vm vm = createVm(this.vmList.size(), broker);
        list.add(vm);

        broker.submitVmList(list);
        this.vmList.addAll(list);

        createAndSubmitCloudlets(broker, vm);
        this.brokerList.add(broker);
        return vm;
    }

    /**
     * Creates a VM with pre-defined configuration.
     *
     * @param id the VM id
     * @param broker the broker that will be submit the VM
     * @return the created VM
     *
     * @see #onHostDeallocationListener(VmHostEventInfo)
     */
    private Vm createVm(int id, DatacenterBroker broker) {
        int mips = 1000;
        long size = 10000; // image size (MEGABYTE)
        int ram = 512; // vm memory (MEGABYTE)
        long bw = 1000;
        return new VmSimple(id, mips, VM_PES_NUMBER)
            .setRam(ram).setBw(bw).setSize(size)
            .setCloudletScheduler(new CloudletSchedulerSpaceShared())
            .setBroker(broker)
            .addOnHostDeallocationListener(this::onHostDeallocationListener);
    }

    /**
     * Creates a cloudlet with pre-defined configuration.
     *
     * @param id Cloudlet id
     * @param vm vm to run the cloudlet
     * @param broker the broker that will submit the cloudlets
     * @return the created cloudlet
     */
    private Cloudlet createCloudlet(int id, Vm vm, DatacenterBroker broker) {
        long fileSize = 300;
        long outputSize = 300;
        long length = 10000; //in number of Million Instructions (MI)
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        return new CloudletSimple(id, length, pesNumber)
              .setFileSize(fileSize)
              .setOutputSize(outputSize)
              .setUtilizationModel(utilizationModel)
              .setBroker(broker)
              .setVm(vm);
    }

    /**
     * Creates a Datacenter with pre-defined configuration.
     *
     * @return the created Datacenter
     */
    private Datacenter createDatacenter() {
        Host host = createHost(0);
        hostList.add(host);

        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this Datacenter
        double costPerBw = 0.0; // the cost of using bw in this resource

        DatacenterCharacteristics characteristics =
            new DatacenterCharacteristicsSimple(hostList)
                .setCostPerSecond(cost)
                .setCostPerMem(costPerMem)
                .setCostPerStorage(costPerStorage)
                .setCostPerBw(costPerBw);

        return new DatacenterSimple(simulation, characteristics, new VmAllocationPolicySimple());
    }

    /**
     * Creates a host with pre-defined configuration.
     *
     * @param id The Host id
     * @return the created host
     */
    private Host createHost(int id) {
        List<Pe> peList = new ArrayList<>();
        long mips = 1000;
        for(int i = 0; i < HOST_PES_NUMBER; i++){
            peList.add(new PeSimple(mips, new PeProvisionerSimple()));
        }

        long ram = 2048; // host memory (MEGABYTE)
        long storage = 1000000; // host storage (MEGABYTE)
        long bw = 10000; //Megabits/s

        return new HostSimple(id, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
            .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(bw)))
            .setVmScheduler(new VmSchedulerSpaceShared());
    }
}
