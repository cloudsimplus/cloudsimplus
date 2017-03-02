/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.examples;

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
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudsimplus.builders.tables.TextTableBuilder;

/**
 * A simple example showing how to create 2 datacenters with 1 host each and run
 * cloudlets of 2 users on them.
 */
public class CloudSimExample5 {
    private List<Cloudlet> cloudletList1;
    private List<Cloudlet> cloudletList2;
    private List<Vm> vmlist1;
    private List<Vm> vmlist2;
    private CloudSim simulation;

    /**
     * Starts the example.
     *
     * @param args
     */
    public static void main(String[] args) {
        new CloudSimExample5();
    }

    public CloudSimExample5() {
        Log.printFormattedLine("Starting %s...", getClass().getSimpleName());
        // First step: Initialize the CloudSim package. It should be called
        // before creating any entities.

        // Initialize the CloudSim library
        simulation = new CloudSim();

        // Second step: Create Datacenters
        //Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
        @SuppressWarnings("unused")
        Datacenter datacenter0 = createDatacenter();
        @SuppressWarnings("unused")
        Datacenter datacenter1 = createDatacenter();

        //Third step: Create Brokers
        DatacenterBroker broker1 = new DatacenterBrokerSimple(simulation);
        DatacenterBroker broker2 = new DatacenterBrokerSimple(simulation);

        //Fourth step: Create one virtual machine for each broker/user
        vmlist1 = new ArrayList<>();
        vmlist2 = new ArrayList<>();

        //VM description
        int vmid = -1;
        int mips = 250;
        long size = 10000; //image size (MEGABYTE)
        int ram = 512; //vm memory (MEGABYTE)
        long bw = 1000;
        int pesNumber = 1; //number of cpus

        //create two VMs: the first one belongs to user1
        Vm vm1 = new VmSimple(++vmid, mips, pesNumber)
            .setRam(ram).setBw(bw).setSize(size)
            .setCloudletScheduler(new CloudletSchedulerTimeShared())
            .setBroker(broker1);


        //the second VM: this one belongs to user2
        Vm vm2 = new VmSimple(++vmid, mips, pesNumber)
            .setRam(ram).setBw(bw).setSize(size)
            .setCloudletScheduler(new CloudletSchedulerTimeShared())
            .setBroker(broker2);

        //add the VMs to the vmlists
        vmlist1.add(vm1);
        vmlist2.add(vm2);

        //submit vm list to the broker
        broker1.submitVmList(vmlist1);
        broker2.submitVmList(vmlist2);

        //Fifth step: Create two Cloudlets
        cloudletList1 = new ArrayList<>();
        cloudletList2 = new ArrayList<>();

        //Cloudlet properties
        int id = -1;
        long length = 40000;
        long fileSize = 300;
        long outputSize = 300;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        Cloudlet cloudlet1 =
                new CloudletSimple(++id, length, pesNumber)
                    .setFileSize(fileSize)
                    .setOutputSize(outputSize)
                    .setUtilizationModel(utilizationModel)
                    .setBroker(broker1);

        Cloudlet cloudlet2 =
            new CloudletSimple(++id, length, pesNumber)
                .setFileSize(fileSize)
                .setOutputSize(outputSize)
                .setUtilizationModel(utilizationModel)
                .setBroker(broker2);

        //add the cloudlets to the lists: each cloudlet belongs to one user
        cloudletList1.add(cloudlet1);
        cloudletList2.add(cloudlet2);

        //submit cloudlet list to the brokers
        broker1.submitCloudletList(cloudletList1);
        broker2.submitCloudletList(cloudletList2);

        // Sixth step: Starts the simulation
        simulation.start();

        // Final step: Print results when simulation is over
        List<Cloudlet> newList1 = broker1.getCloudletsFinishedList();
        List<Cloudlet> newList2 = broker2.getCloudletsFinishedList();

        new CloudletsTableBuilder(newList1)
                .setPrinter(new TextTableBuilder(broker1.getName()))
                .build();
        new CloudletsTableBuilder(newList2)
                .setPrinter(new TextTableBuilder(broker2.getName()))
                .build();
        Log.printFormattedLine("%s finished!", getClass().getSimpleName());
    }

    private Datacenter createDatacenter() {
        // Here are the steps needed to create a DatacenterSimple:
        // 1. We need to create a list to store
        //    our machine
        List<Host> hostList = new ArrayList<>();

        // 2. A Machine contains one or more PEs or CPUs/Cores.
        // In this example, it will have only one core.
        List<Pe> peList = new ArrayList<>();

        long mips = 1000;

        // 3. Create PEs and add these into a list.
        peList.add(new PeSimple(mips, new PeProvisionerSimple())); // need to store Pe id and MIPS Rating

        //4. Create HostSimple with its id and list of PEs and add them to the list of machines
        int hostId = 0;
        int ram = 2048; //host memory (MEGABYTE)
        long storage = 1000000; //host storage
        long bw = 10000;

        Host host = new HostSimple(++hostId, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
            .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(bw)))
            .setVmScheduler(new VmSchedulerSpaceShared());
        hostList.add(host);

        // 5. Create a DatacenterCharacteristics object that stores the
        //    properties of a data center: architecture, OS, list of
        //    Machines, allocation policy: time- or space-shared, time zone
        //    and its price (G$/Pe time unit).
        double cost = 3.0;              // the cost of using processing in this resource
        double costPerMem = 0.05;		// the cost of using memory in this resource
        double costPerStorage = 0.001;	// the cost of using storage in this resource
        double costPerBw = 0.0;			// the cost of using bw in this resource

        DatacenterCharacteristics characteristics =
            new DatacenterCharacteristicsSimple(hostList)
                .setCostPerSecond(cost)
                .setCostPerMem(costPerMem)
                .setCostPerStorage(costPerStorage)
                .setCostPerBw(costPerBw);

        // 6. Finally, we need to create a DatacenterSimple object.
        return new DatacenterSimple(simulation, characteristics, new VmAllocationPolicySimple());
    }

}
