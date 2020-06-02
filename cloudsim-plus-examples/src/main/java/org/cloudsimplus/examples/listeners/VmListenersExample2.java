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
package org.cloudsimplus.examples.listeners;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.VmHostEventInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple example showing how to create a data center with 1 host and place 2 VMs
 * to run 1 cloudlet each one, and receive notifications when a Host is allocated or
 * deallocated to each Vm.
 *
 * The example uses the new Vm listeners to get these
 * notifications while the simulation is running. It also shows how to
 * reuse the same listener object to different VMs.
 *
 * @see Vm#addOnHostAllocationListener(EventListener)
 * @see Vm#addOnHostDeallocationListener(EventListener)
 * @see EventListener
 *
 * @author Manoel Campos da Silva Filho
 */
public class VmListenersExample2 {
    /**
     * Number of VMs to create.
     */
    private static final int NUMBER_OF_VMS = 4;

    /**
     * Number of Processor Elements (CPU Cores) of each Host.
     */
    private static final int HOST_PES_NUMBER = NUMBER_OF_VMS;

    /**
     * Number of Processor Elements (CPU Cores) of each VM and cloudlet.
     */
    private static final int VM_PES_NUMBER = 1;

    private final List<Host> hostList;
    private final List<Vm> vmList;
    private final List<Cloudlet> cloudletList;
    private final DatacenterBroker broker;
    private final Datacenter datacenter;
    private final CloudSim simulation;

    /**
     * The listener object that will be created in order to be notified when
     * a host is allocated for a VM. The same listener is used for all created VMs.
     * @see #createVmListeners()
     */
    private EventListener<VmHostEventInfo> onHostAllocationListener;

    /**
     * The listener object that will be created in order to be notified when
     * a host is deallocated for a VM. The same listener is used for all created VMs.
     * @see #createVmListeners()
     */
    private EventListener<VmHostEventInfo> onHostDeallocationListener;

    /**
     * Starts the example execution, calling the class constructor\
     * to build and run the simulation.
     *
     * @param args command line parameters
     */
    public static void main(String[] args) {
        new VmListenersExample2();
    }

    /**
     * Default constructor that builds and starts the simulation.
     */
    private VmListenersExample2() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());
        simulation = new CloudSim();

        this.hostList = new ArrayList<>();
        this.vmList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();
        this.datacenter = createDatacenter();
        this.broker = new DatacenterBrokerSimple(simulation);

        createVmListeners();
        createAndSubmitVmsAndCloudlets();
        runSimulationAndPrintResults();
        System.out.println(getClass().getSimpleName() + " finished!");
    }

    /**
     * Creates VMs and the cloudlets for each one, submitting
     * the list of VMs and Cloudlets to the broker.
     *
     * @see #createVm(int)
     */
    private void createAndSubmitVmsAndCloudlets() {
        for(int i = 0; i < NUMBER_OF_VMS; i++){
            Vm vm = createVm(i);
            this.vmList.add(vm);

            Cloudlet cloudlet = createCloudlet(i, vm);
            this.cloudletList.add(cloudlet);
        }

        this.broker.submitVmList(vmList);
        this.broker.submitCloudletList(cloudletList);
    }

    /**
     * Creates all VM listeners to be used by every VM created.
     *
     * @see #createVm(int)
     */
    private void createVmListeners() {
        /*
        Creates the listener object that will be notified when a host is allocated to a VM.
        All VMs will use this same listener.
        The Listener is created using Java 8 Lambda Expressions.
        */
        this.onHostAllocationListener = eventInfo -> System.out.printf(
                "\t#EventListener: Host %d allocated to Vm %d at time %.2f%n",
                eventInfo.getHost().getId(), eventInfo.getVm().getId(), eventInfo.getTime());

        /*
        Creates the listener object that will be notified when a host is deallocated for a VM.
        All VMs will use this same listener.
        The Listener is created using Java 8 Lambda Expressions.
        */
        this.onHostDeallocationListener = evt -> System.out.printf(
                "\t#EventListener: Vm %d moved/removed from Host %d at time %.2f%n",
                evt.getVm().getId(), evt.getHost().getId(), evt.getTime());
    }

    /**
     * Creates a VM with pre-defined configuration.
     *
     * @param id the VM id
     * @return the created VM
     */
    private Vm createVm(int id) {
        int mips = 1000;
        long size = 10000; // image size (Megabyte)
        int ram = 512; // vm memory (Megabyte)
        long bw = 1000;

        Vm vm = new VmSimple(id, mips, VM_PES_NUMBER)
            .setRam(ram).setBw(bw).setSize(size)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());

        /*Adds the listener to intercept allocation of a Host to the Vm.*/
        vm.addOnHostAllocationListener(this.onHostAllocationListener);

        /*Adds the listener to intercept deallocation of a Host for the Vm.*/
        vm.addOnHostDeallocationListener(this.onHostDeallocationListener);

        return vm;
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

        return new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
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
        long ram = 2048; // host memory (Megabyte)
        long storage = 1000000; // host storage (Megabyte)
        long bw = 10000; //Megabits/s

        return new HostSimple(ram, bw, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());
    }

    private void runSimulationAndPrintResults() {
        simulation.start();

        List<Cloudlet> newList = broker.getCloudletFinishedList();
        new CloudletsTableBuilder(newList).build();
    }
}
