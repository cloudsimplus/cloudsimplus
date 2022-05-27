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
package org.cloudsimplus.examples.dynamic;

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
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * An example showing how to dynamically create VMs and Cloudlets during simulation
 * executing using the exclusive CloudSim Plus {@link DatacenterBroker} implementations
 * and Listener features. Using such features, <b>it is not required to create DatacenterBrokers in runtime
 * in order to allow dynamic submission of VMs and Cloudlets.</b>
 *
 * <p>This example uses CloudSim Plus Listener features to intercept when
 * the first Cloudlet finishes its execution to then request
 * the creation of new VMs and Cloudlets. It uses the Java 8 Lambda Functions features
 * to pass a listener to the mentioned Cloudlet, by means of the
 * {@link Cloudlet#addOnFinishListener(EventListener)} method.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 *
 * @see Cloudlet#addOnFinishListener(EventListener)
 * @see EventListener
 */
public class DynamicCreationOfVmsAndCloudletsExample {
    private final CloudSim simulation;
    private final DatacenterBrokerSimple broker0;
    private List<Cloudlet> cloudletList;
    private List<Vm> vmList;
    private int numberOfCreatedCloudlets = 0;
    private int numberOfCreatedVms = 0;

    public static void main(String[] args) {
        new DynamicCreationOfVmsAndCloudletsExample();
    }

    /**
     * Default constructor that builds the simulation.
     */
    private DynamicCreationOfVmsAndCloudletsExample() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());
        this.simulation = new CloudSim();

        Datacenter datacenter0 = createDatacenter();

        /*Creates a Broker accountable for submission of VMs and Cloudlets
        on behalf of a given cloud user (customer).*/
        broker0 = new DatacenterBrokerSimple(simulation);

        final int vmsToCreate = 1;
        final int cloudletsToCreateByVm = 2;
        this.vmList = new ArrayList<>(vmsToCreate);
        this.cloudletList = new ArrayList<>(vmsToCreate*cloudletsToCreateByVm);
        createAndSubmitVmsAndCloudlets(vmsToCreate, cloudletsToCreateByVm);

        /* Assigns an EventListener to be notified when the first Cloudlets finishes executing
        * and then dynamically create a new list of VMs and Cloudlets to submit to the broker.*/
        Cloudlet cloudlet0 = this.cloudletList.get(0);
        cloudlet0.addOnFinishListener(this::submitNewVmsAndCloudletsToBroker);

        /* Starts the simulation and waits all cloudlets to be executed. */
        simulation.start();

        /*Prints results when the simulation is over
        (you can use your own code here to print what you want from this cloudlet list)*/
        List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();
        System.out.println(getClass().getSimpleName() + " finished!");
    }

    private void createAndSubmitVmsAndCloudlets(int vmsToCreate, int cloudletsToCreateForEachVm) {
        List<Vm> newVmList = new ArrayList<>(vmsToCreate);
        List<Cloudlet> newCloudletList = new ArrayList<>(vmsToCreate*cloudletsToCreateForEachVm);
        for (int i = 0; i < vmsToCreate; i++) {
            Vm vm = createVm(broker0);
            newVmList.add(vm);
            for(int j = 0; j < cloudletsToCreateForEachVm; j++) {
                Cloudlet cloudlet = createCloudlet(broker0, vm);
                newCloudletList.add(cloudlet);
            }
        }

        this.vmList.addAll(newVmList);
        this.cloudletList.addAll(newCloudletList);

        broker0.submitVmList(newVmList);
        broker0.submitCloudletList(newCloudletList);
    }

    /**
     * Dynamically creates and submits a set of VMs to the broker when
     * the first cloudlet finishes.
     * @param eventInfo information about the fired event
     */
    private void submitNewVmsAndCloudletsToBroker(CloudletVmEventInfo eventInfo) {
        final int numberOfNewVms = 2;
        final int numberOfCloudletsByVm = 4;
        System.out.printf(
            "%n\t# Cloudlet %d finished. Submitting %d new VMs to the broker%n",
            eventInfo.getCloudlet().getId(), numberOfNewVms);

        createAndSubmitVmsAndCloudlets(numberOfNewVms, numberOfCloudletsByVm);

    }

    private DatacenterSimple createDatacenter() {
        final int numberOfHosts = 1;
        List<Host> hostList = new ArrayList<>(numberOfHosts);
        for (int i = 0; i < numberOfHosts; i++) {
            Host host = createHost();
            hostList.add(host);
        }

        return new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
    }

    private Host createHost() {
        long  mips = 1000; // capacity of each CPU core (in Million Instructions per Second)
        long  ram = 2048; // host memory (Megabyte)
        long storage = 1000000; // host storage (Megabyte)
        long bw = 10000; //in Megabits/s

        final int numberOfPes = 8;
        List<Pe> peList = new ArrayList<>(numberOfPes); //List of CPU cores
        for (int i = 0; i < numberOfPes; i++) {
            peList.add(new PeSimple(mips, new PeProvisionerSimple()));
        }

        return new HostSimple(ram, bw, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
                .setVmScheduler(new VmSchedulerTimeShared());
    }

    private Vm createVm(DatacenterBroker broker) {
        long   mips = 1000;
        long   storage = 10000; // vm image size (Megabyte)
        int    ram = 512; // vm memory (Megabyte)
        long   bw = 1000; // vm bandwidth (Megabits/s)
        int    pesNumber = 1; // number of CPU cores

        return new VmSimple(numberOfCreatedVms++, mips, pesNumber)
                .setRam(ram)
                .setBw(bw)
                .setSize(storage)
                .setCloudletScheduler(new CloudletSchedulerSpaceShared());
    }

    private Cloudlet createCloudlet(DatacenterBroker broker, Vm vm) {
        long length = 10000; //in Million Instructions (MI)
        long fileSize = 300; //Size (in bytes) before execution
        long outputSize = 300; //Size (in bytes) after execution
        long numberOfCpuCores = vm.getNumberOfPes(); //cloudlet will use all the VM's CPU cores

        //Defines how CPU, RAM and Bandwidth resources are used
        //Sets the same utilization model for all these resources.
        UtilizationModel utilization = new UtilizationModelFull();

        return new CloudletSimple(
                numberOfCreatedCloudlets++, length, numberOfCpuCores)
                .setFileSize(fileSize)
                .setOutputSize(outputSize)
                .setUtilizationModel(utilization)
                .setVm(vm);
    }

}
