/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.examples.network;

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
import org.cloudbus.cloudsim.network.topologies.NetworkTopology;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudsimplus.util.tablebuilder.CloudletsTableBuilderHelper;
import org.cloudsimplus.util.tablebuilder.TextTableBuilder;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Ram;

/**
 * A simple example showing how to create 2 datacenters with 1 host each and run
 * cloudlets of 2 users with network topology on them.
 */
public class NetworkExample3 {
    private static List<Cloudlet> cloudletList1;
    private static List<Cloudlet> cloudletList2;
    private static List<Vm> vmlist1;
    private static List<Vm> vmlist2;
    private static CloudSim simulation;

    /**
     * Creates main() to run this example
     *
     * @param args
     */
    public static void main(String[] args) {
        Log.printFormattedLine("Starting %s...", NetworkExample3.class.getSimpleName());
        // First step: Initialize the CloudSim package. It should be called
        // before creating any entities.
        int num_user = 2;   // number of cloud users
        boolean trace_flag = false;  // mean trace events

        // Initialize the CloudSim library
        simulation = new CloudSim(num_user, trace_flag);

        // Second step: Create Datacenters
        //Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
        Datacenter datacenter0 = createDatacenter();
        Datacenter datacenter1 = createDatacenter();

        //Third step: Create Brokers
        DatacenterBroker broker1 = createBroker(1);
        DatacenterBroker broker2 = createBroker(2);

        //Fourth step: Create one virtual machine for each broker/user
        vmlist1 = new ArrayList<>();
        vmlist2 = new ArrayList<>();

        //VM description
        int vmid = -1;
        long size = 10000; //image size (MB)
        int mips = 250;
        int ram = 512; //vm memory (MB)
        long bw = 1000;
        int pesNumber = 1; //number of cpus

        //create two VMs: the first one belongs to user1
        Vm vm1 = new VmSimple(++vmid, mips, pesNumber)
                .setBroker(broker1)
                .setRam(ram).setBw(bw).setSize(size)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());

        //the second VM: this one belongs to user2
        Vm vm2 = new VmSimple(++vmid, mips, pesNumber)
                .setBroker(broker2)
                .setRam(ram).setBw(bw).setSize(size)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());

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
                .setCloudletFileSize(fileSize)
                .setCloudletOutputSize(outputSize)
                .setUtilizationModel(utilizationModel)
                .setBroker(broker1);

        Cloudlet cloudlet2 =
            new CloudletSimple(++id, length, pesNumber)
                .setCloudletFileSize(fileSize)
                .setCloudletOutputSize(outputSize)
                .setUtilizationModel(utilizationModel)
                .setBroker(broker2);

        //add the cloudlets to the lists: each cloudlet belongs to one user
        cloudletList1.add(cloudlet1);
        cloudletList2.add(cloudlet2);

        //submit cloudlet list to the brokers
        broker1.submitCloudletList(cloudletList1);
        broker2.submitCloudletList(cloudletList2);

        //Sixth step: configure network
        //load the network topology file
        NetworkTopology.buildNetworkTopology("topology.brite");

        //maps CloudSim entities to BRITE entities
        //Datacenter0 will correspond to BRITE node 0
        int briteNode = 0;
        NetworkTopology.mapNode(datacenter0.getId(), briteNode);

        //Datacenter1 will correspond to BRITE node 2
        briteNode = 2;
        NetworkTopology.mapNode(datacenter1.getId(), briteNode);

        //Broker1 will correspond to BRITE node 3
        briteNode = 3;
        NetworkTopology.mapNode(broker1.getId(), briteNode);

        //Broker2 will correspond to BRITE node 4
        briteNode = 4;
        NetworkTopology.mapNode(broker2.getId(), briteNode);

        // Sixth step: Starts the simulation
        simulation.start();

        // Final step: Print results when simulation is over
        List<Cloudlet> newList1 = broker1.getCloudletsFinishedList();
        List<Cloudlet> newList2 = broker2.getCloudletsFinishedList();

        simulation.stop();

        new CloudletsTableBuilderHelper(newList1)
                .setPrinter(new TextTableBuilder("Broker " + broker1))
                .build();
        new CloudletsTableBuilderHelper(newList2)
                .setPrinter(new TextTableBuilder("Broker " + broker2))
                .build();
        Log.printFormattedLine("%s finished!", NetworkExample3.class.getSimpleName());
    }

    private static Datacenter createDatacenter() {
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

        //4. Create HostSimple with its id and list of PEs and add them to the list of machines
        int hostId = 0;
        int ram = 2048; //host memory (MB)
        long storage = 1000000; //host storage
        long bw = 10000;

        //in this example, the VMAllocatonPolicy in use is SpaceShared. It means that only one VM
        //is allowed to run on each Pe. As each HostSimple has only one Pe, only one VM can run on each HostSimple.
        Host host = new HostSimple(hostId, storage, peList)
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

    //We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
    //to the specific rules of the simulated scenario
    private static DatacenterBroker createBroker(int id) {
        return new DatacenterBrokerSimple(simulation);
    }

}
