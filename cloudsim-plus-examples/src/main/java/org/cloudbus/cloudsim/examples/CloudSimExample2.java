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
import java.util.Calendar;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;

import org.cloudbus.cloudsim.CloudletSimple;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.DatacenterSimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.HostSimple;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudsimplus.util.tablebuilder.CloudletsTableBuilderHelper;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Ram;

/**
 * A simple example showing how to create a datacenter with 1 host and run 2
 * cloudlets on it. The cloudlets run in VMs with the same MIPS requirements.
 * The cloudlets will take the same time to complete the execution.
 */
public class CloudSimExample2 {
    private static List<Cloudlet> cloudletList;
    private static List<Vm> vmlist;
    /**
     * Creates main() to run this example
     *
     * @param args
     */
    public static void main(String[] args) {
        Log.printFormattedLine("Starting %s...", CloudSimExample2.class.getSimpleName());

        // First step: Initialize the CloudSim package. It should be called
        // before creating any entities.
        int num_user = 1;   // number of cloud users
        Calendar calendar = Calendar.getInstance();
        boolean trace_flag = false;  // mean trace events

        // Initialize the CloudSim library
        simulation = new CloudSim(num_user, trace_flag);

        // Second step: Create Datacenters
        //Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
        @SuppressWarnings("unused")
        DatacenterSimple datacenter0 = createDatacenter();

        //Third step: Create Broker
        DatacenterBroker broker = createBroker();

        //Fourth step: Create one virtual machine
        vmlist = new ArrayList<>();

        //VM description
        int vmid = -1;
        int mips = 250;
        long size = 10000; //image size (MB)
        int ram = 512; //vm memory (MB)
        long bw = 1000;
        int pesNumber = 1; //number of cpus

        //create two VMs
        Vm vm1 = new VmSimple(++vmid, mips, pesNumber)
            .setRam(ram).setBw(bw).setSize(size)
            .setCloudletScheduler(new CloudletSchedulerTimeShared())
            .setBroker(broker);

        Vm vm2 = new VmSimple(++vmid, mips, pesNumber)
            .setRam(ram).setBw(bw).setSize(size)
            .setCloudletScheduler(new CloudletSchedulerTimeShared())
            .setBroker(broker);

        //add the VMs to the vmList
        vmlist.add(vm1);
        vmlist.add(vm2);

        //submit vm list to the broker
        broker.submitVmList(vmlist);

        //Fifth step: Create two Cloudlets
        cloudletList = new ArrayList<>();

        //Cloudlet properties
        int id = -1;
        pesNumber = 1;
        long length = 250000;
        long fileSize = 300;
        long outputSize = 300;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        Cloudlet cloudlet1 =
            new CloudletSimple(++id, length, pesNumber)
                .setCloudletFileSize(fileSize)
                .setCloudletOutputSize(outputSize)
                .setUtilizationModel(utilizationModel)
                .setBroker(broker);

        Cloudlet cloudlet2 =
            new CloudletSimple(++id, length, pesNumber)
                .setCloudletFileSize(fileSize)
                .setCloudletOutputSize(outputSize)
                .setUtilizationModel(utilizationModel)
                .setBroker(broker);

        //add the cloudlets to the list
        cloudletList.add(cloudlet1);
        cloudletList.add(cloudlet2);

        //submit cloudlet list to the broker
        broker.submitCloudletList(cloudletList);

        //bind the cloudlets to the vms. This way, the broker
        // will submit the bound cloudlets only to the specific VM
        broker.bindCloudletToVm(cloudlet1.getId(), vm1.getId());
        broker.bindCloudletToVm(cloudlet2.getId(), vm2.getId());

        // Sixth step: Starts the simulation
        simulation.start();

        // Final step: Print results when simulation is over
        List<Cloudlet> newList = broker.getCloudletsFinishedList();

        simulation.stop();

        new CloudletsTableBuilderHelper(newList).build();
        Log.printFormattedLine("%s finished!", CloudSimExample2.class.getSimpleName());
    }

    private static CloudSim simulation;

    private static DatacenterSimple createDatacenter() {
        // Here are the steps needed to create a DatacenterSimple:
        // 1. We need to create a list to store
        //    our machine
        List<Host> hostList = new ArrayList<>();

        // 2. A Machine contains one or more PEs or CPUs/Cores.
        // In this example, it will have only one core.
        List<Pe> peList = new ArrayList<>();

        int mips = 1000;

        // 3. Create PEs and add these into a list.
        peList.add(new PeSimple(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

        //4. Create Host with its id and list of PEs and add them to the list of machines
        int hostId = 0;
        int ram = 2048; //host memory (MB)
        long storage = 1000000; //host storage
        long bw = 10000;

        Host host = new HostSimple(hostId, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
            .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(bw)))
            .setVmScheduler(new VmSchedulerTimeShared());

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
        DatacenterSimple datacenter = new DatacenterSimple(simulation, characteristics, new VmAllocationPolicySimple());
        datacenter.setSchedulingInterval(1);
        return datacenter;
    }

    /*
    We strongly encourage users to develop their own broker policies,
    to submit vms and cloudlets according
    to the specific rules of the simulated scenario
    */
    private static DatacenterBroker createBroker() {
        return new DatacenterBrokerSimple(simulation);
    }
}
