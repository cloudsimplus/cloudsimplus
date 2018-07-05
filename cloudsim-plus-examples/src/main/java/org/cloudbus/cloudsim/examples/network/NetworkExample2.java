/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.examples.network;

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
import org.cloudbus.cloudsim.network.topologies.BriteNetworkTopology;
import org.cloudbus.cloudsim.network.topologies.NetworkTopology;
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

import java.util.ArrayList;
import java.util.List;

/**
 * A simple example showing how to create 2 datacenters with 1 host and a
 * network topology each and run 2 cloudlets on them.
 */
public class NetworkExample2 {
    private List<Cloudlet> cloudletList;
    private List<Vm> vmlist;
    private CloudSim simulation;

    /**
     * Starts the example.
     *
     * @param args
     */
    public static void main(String[] args) {
        new NetworkExample2();
    }

    public NetworkExample2() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());

        // First step: Initialize the CloudSim package.
        simulation = new CloudSim();

        // Second step: Create Datacenters
        //Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
        Datacenter datacenter0 = createDatacenter();
        Datacenter datacenter1 = createDatacenter();

        //Third step: Create Broker
        DatacenterBroker broker = createBroker();

        //Fourth step: Create one virtual machine
        vmlist = new ArrayList<>();

        //VM description
        int vmid = -1;
        int mips = 250;
        long size = 10000; //image size (Megabyte)
        int ram = 512; //vm memory (Megabyte)
        long bw = 1000;
        int pesNumber = 1; //number of cpus

        //create two VMs
        Vm vm1 = new VmSimple(++vmid, mips, pesNumber)
            .setRam(ram).setBw(bw).setSize(size)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());

        //the second VM will have twice the priority of VM1 and so will receive twice CPU time
        Vm vm2 = new VmSimple(++vmid, mips, pesNumber)
            .setRam(ram).setBw(bw).setSize(size)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());

        //add the VMs to the vmList
        vmlist.add(vm1);
        vmlist.add(vm2);

        //submit vm list to the broker
        broker.submitVmList(vmlist);

        //Fifth step: Create two Cloudlets
        cloudletList = new ArrayList<>();

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
                .setUtilizationModel(utilizationModel);

        Cloudlet cloudlet2 =
            new CloudletSimple(++id, length, pesNumber)
                .setFileSize(fileSize)
                .setOutputSize(outputSize)
                .setUtilizationModel(utilizationModel);

        //add the cloudlets to the list
        cloudletList.add(cloudlet1);
        cloudletList.add(cloudlet2);

        //submit cloudlet list to the broker
        broker.submitCloudletList(cloudletList);

        //bind the cloudlets to the vms. This way, the broker
        // will submit the bound cloudlets only to the specific VM
        broker.bindCloudletToVm(cloudlet1, vm1);
        broker.bindCloudletToVm(cloudlet2, vm2);

        //Sixth step: configure network
        //load the network topology file
        NetworkTopology networkTopology = BriteNetworkTopology.getInstance("topology.brite");
        simulation.setNetworkTopology(networkTopology);

        //maps CloudSim entities to BRITE entities
        //Datacenter0 will correspond to BRITE node 0
        int briteNode = 0;
        networkTopology.mapNode(datacenter0.getId(), briteNode);

        //Datacenter1 will correspond to BRITE node 2
        briteNode = 2;
        networkTopology.mapNode(datacenter1.getId(), briteNode);

        //Broker will correspond to BRITE node 3
        briteNode = 3;
        networkTopology.mapNode(broker.getId(), briteNode);

        // Sixth step: Starts the simulation
        simulation.start();

        // Final step: Print results when simulation is over
        List<Cloudlet> newList = broker.getCloudletFinishedList();
        new CloudletsTableBuilder(newList).build();
        System.out.println(getClass().getSimpleName() + " finished!");
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

        // 3. Create PEs and add these into a list
        peList.add(new PeSimple(mips, new PeProvisionerSimple())); // need to store Pe id and MIPS Rating

        //4. Create HostSimple with its id and list of PEs and add them to the list of machines
        int hostId = 0;
        int ram = 2048; //host memory (Megabyte)
        long storage = 1000000; //host storage
        long bw = 10000;

        Host host = new HostSimple(ram, bw, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());
        hostList.add(host);

        // 6. Finally, we need to create a DatacenterSimple object.
        return new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
    }

    //We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
    //to the specific rules of the simulated scenario
    private DatacenterBroker createBroker() {
        return new DatacenterBrokerSimple(simulation);
    }
}
