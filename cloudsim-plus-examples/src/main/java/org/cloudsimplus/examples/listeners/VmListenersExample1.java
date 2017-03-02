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
import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
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
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudsimplus.listeners.EventListener;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Ram;

/**
 * A simple example showing how to create a data center with 1 host and run 1
 * cloudlet on it, and receive notifications when a Host is allocated or
 * deallocated to each Vm. It is also show how to be notified when
 * no suitable host is found to place a VM.
 *
 * The example uses the new Vm listeners to get these
 * notifications while the simulation is running.
 *
 * @see Vm#addOnHostAllocationListener(EventListener)
 * @see Vm#addOnHostDeallocationListener(EventListener)
 * @see Vm#addOnCreationFailureListener(EventListener)
 * @see EventListener
 *
 * @author Manoel Campos da Silva Filho
 */
public class VmListenersExample1 {
    /**
     * Number of Processor Elements (CPU Cores) of each Host.
     */
    private static final int HOST_PES_NUMBER = 1;

    /**
     * Number of Processor Elements (CPU Cores) of each VM and cloudlet.
     */
    private static final int VM_PES_NUMBER = HOST_PES_NUMBER;

    private final List<Host> hostList;
    private final List<Vm> vmList;
    private final List<Cloudlet> cloudletList;
    private final DatacenterBroker broker;
    private final Datacenter datacenter;
    private final CloudSim simulation;

    /**
     * Starts the example execution, calling the class constructor\
     * to build and run the simulation.
     *
     * @param args command line parameters
     */
    public static void main(String[] args) {
        Log.printFormattedLine("Starting %s ...", VmListenersExample1.class.getSimpleName());
        new VmListenersExample1();
        Log.printFormattedLine("%s finished!", VmListenersExample1.class.getSimpleName());
    }

    /**
     * Default constructor that builds and starts the simulation.
     */
    public VmListenersExample1() {
        simulation = new CloudSim();

        this.hostList = new ArrayList<>();
        this.vmList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();
        this.datacenter = createDatacenter();
        this.broker = new DatacenterBrokerSimple(simulation);

        createAndSubmitVms();
        createAndSubmitCloudlets();

        runSimulationAndPrintResults();
    }

    private void runSimulationAndPrintResults() {
        simulation.start();

        List<Cloudlet> finishedCloudlets = broker.getCloudletsFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();
    }

    /**
     * Create cloudlets and submit them to the broker.
     */
    private void createAndSubmitCloudlets() {
        Cloudlet cloudlet0 = createCloudlet(0, vmList.get(0));
        this.cloudletList.add(cloudlet0);

        /*This cloudlet will not be run because vm1 will not be placed
        due to lack of a suitable host.*/
        Cloudlet cloudlet1 = createCloudlet(1, vmList.get(1));
        this.cloudletList.add(cloudlet1);

        this.broker.submitCloudletList(cloudletList);
    }

    /**
     * Creates VMs and submit them to the broker.
     */
    private void createAndSubmitVms() {
        Vm vm0 = createVm(0);

        /* Sets the Listener to intercept allocation of a Host to the Vm.
         * The Listener is created using Java 8 Lambda Expressions.
        */
        vm0.addOnHostAllocationListener(eventInfo -> Log.printFormattedLine(
                "\n\t#EventListener: Host %d allocated to Vm %d at time %.2f\n",
                eventInfo.getHost().getId(), eventInfo.getVm().getId(), eventInfo.getTime()));

        /* Sets the listener to intercept deallocation of a Host for the Vm.
         * The Listener is created using Java 8 Lambda Expressions.
        */
        vm0.addOnHostDeallocationListener(eventInfo -> Log.printFormattedLine(
                "\n\t#EventListener: Vm %d moved/removed from Host %d at time %.2f\n",
                eventInfo.getVm().getId(), eventInfo.getHost().getId(), eventInfo.getTime()));

        /* This VM will not be place due to lack of a suitable host.
         * The Listener is created using Java 8 Lambda Expressions.
         */
        Vm vm1 = createVm(1);
        vm1.addOnCreationFailureListener(eventInfo -> Log.printFormattedLine(
                "\n\t#EventListener: Vm %d could not be placed into any host of Datacenter %d at time %.2f due to lack of a host with enough resources.\n",
                eventInfo.getVm().getId(), eventInfo.getDatacenter().getId(), eventInfo.getTime()));

        this.vmList.add(vm0);
        this.vmList.add(vm1);
        this.broker.submitVmList(vmList);
    }

    /**
     * Creates a VM with pre-defined configuration.
     *
     * @param id the VM id
     * @return the created VM
     */
    private Vm createVm(int id) {
        int mips = 1000;
        long size = 10000; // image size (MEGABYTE)
        int ram = 512; // vm memory (MEGABYTE)
        long bw = 1000;

        return new VmSimple(id, mips, VM_PES_NUMBER)
            .setRam(ram).setBw(bw).setSize(size)
            .setCloudletScheduler(new CloudletSchedulerTimeShared())
            .setBroker(broker);
    }

    /**
     * Creates a cloudlet with pre-defined configuration.
     *
     * @param id Cloudlet id
     * @param vm vm to run the cloudlet
     * @return the created cloudlet
     */
    private Cloudlet createCloudlet(int id, Vm vm) {
        long length = 400000;  //in MI (Million Instructions)
        long fileSize = 300;
        long outputSize = 300;
        UtilizationModel utilizationModel = new UtilizationModelFull();
        return new CloudletSimple(id, length, VM_PES_NUMBER)
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
            .setVmScheduler(new VmSchedulerTimeShared());

    }
}
