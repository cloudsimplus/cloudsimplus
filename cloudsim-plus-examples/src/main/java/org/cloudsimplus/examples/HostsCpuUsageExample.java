/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */
package org.cloudsimplus.examples;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudsimplus.util.tablebuilder.CloudletsTableBuilderHelper;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Ram;

/**
 * A simple example showing how to create a datacenter with two hosts,
 * with one Vm in each one, and run 1 cloudlet in each Vm.
 * At the end, it shows the total CPU utilization of hosts
 * into a datacenter.
 *
 * Cloudlets run in VMs with different MIPS requirements. They will
 * take different times to complete the execution depending on the requested VM
 * performance.
 *
 * @author Manoel Campos da Silva Filho
 */
public class HostsCpuUsageExample {
    private static List<Cloudlet> cloudletList;
    private static List<Vm> vmlist;
    private static List<HostDynamicWorkloadSimple> hostList;
    private static CloudSim simulation;

    /**
     * Creates main() to run this example
     * @param args
     */
    public static void main(String[] args) {
        Log.printFormattedLine("Starting %s...", HostsCpuUsageExample.class.getSimpleName());

        // First step: Initialize the CloudSim package. It should be called
        // before creating any entities.
        int num_user = 1;   // number of cloud users
        boolean trace_flag = false;  // mean trace events

        // Initialize the CloudSim library
        simulation = new CloudSim(num_user, trace_flag);

        // Second step: Create Datacenters
        //Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
        @SuppressWarnings("unused")
        Datacenter datacenter0 = createDatacenter();

        //Third step: Create Broker
        DatacenterBroker broker = createBroker();

        //Fourth step: Create one virtual machine
        vmlist = new ArrayList<>();

        //VM description
        int vmid = -1;
        int mips = 1000;
        long size = 10000; //image size (MB)
        int ram = 2048; //vm memory (MB)
        long bw = 1000;
        int pesNumber = 1; //number of cpus

        //create two VMs
        Vm vm1 = new VmSimple(++vmid, mips, pesNumber)
            .setRam(ram).setBw(bw).setSize(size)
            .setCloudletScheduler(new CloudletSchedulerTimeShared())
            .setBroker(broker);


        //the second VM will have twice the priority of VM1 and so will receive twice CPU time
        Vm vm2 = new VmSimple(++vmid, mips*2, pesNumber)
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
        long length = 10000;
        long fileSize = 300;
        long outputSize = 300;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        Cloudlet cloudlet1 = new CloudletSimple(++id, length, pesNumber)
            .setCloudletFileSize(fileSize)
            .setCloudletOutputSize(outputSize)
            .setUtilizationModel(utilizationModel)
            .setBroker(broker)
            .setVmId(vm1.getId());

        Cloudlet cloudlet2 = new CloudletSimple(++id, length, pesNumber)
            .setCloudletFileSize(fileSize)
            .setCloudletOutputSize(outputSize)
            .setUtilizationModel(utilizationModel)
            .setBroker(broker)
            .setVmId(vm2.getId());

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
        final double finishTime = simulation.start();

        simulation.stop();

        // Final step: Print results when simulation is over
        List<Cloudlet> newList = broker.getCloudletsFinishedList();

        showCpuUtilizationForAllHosts();

        new CloudletsTableBuilderHelper(newList).build();
        Log.printFormattedLine("%s finished!", HostsCpuUsageExample.class.getSimpleName());
    }

    /**
     * Shows CPU utilization of all hosts into a given datacenter.
     *
     */
    private static void showCpuUtilizationForAllHosts() {
        Log.printLine("\nHosts CPU utilization history for the entire simulation period");
        int numberOfUsageHistoryEntries = 0;
        final double interval = 1;
        for (HostDynamicWorkloadSimple host : hostList) {
            for(HostStateHistoryEntry history: host.getStateHistory()){
                    numberOfUsageHistoryEntries++;
                    Log.printConcatLine(
                            " Time: ", history.getTime(),
                            "\tHost: ", host.getId(),
                            "\t\tCPU Utilization (MIPS): ", history.getAllocatedMips());
            }
            Log.printLine("--------------------------------------------------");
        }
        if(numberOfUsageHistoryEntries == 0)
            Log.printLine(" No CPU usage history was found");
    }

    private static Datacenter createDatacenter() {
        // Here are the steps needed to create a DatacenterSimple:
        // 1. We need to create a list to store our machine
        hostList = new ArrayList<>();

        // 2. A Machine contains one or more PEs or CPUs/Cores.
        // In this example, it will have only one core.
        List<Pe> peList1 = new ArrayList<>();

        int mips = 1200;

        // 3. Create PEs and add these into a list.
        peList1.add(new PeSimple(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

        //4. Create Hosts with its id and list of PEs and add them to the list of machines
        int hostId = -1;
        long ram = 2048; //host memory (MB)
        long storage = 1000000; //host storage (MB)
        long bw = 10000; //Megabits/s

        HostDynamicWorkloadSimple host1 = new HostDynamicWorkloadSimple(++hostId, storage, peList1);
        host1
            .setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
            .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(bw)))
            .setVmScheduler(new VmSchedulerTimeShared());
        hostList.add(host1);

        //create another machine in the Data center
        List<Pe> peList2 = new ArrayList<>();
        peList2.add(new PeSimple(0, new PeProvisionerSimple(mips * 2)));

        HostDynamicWorkloadSimple host2 = new HostDynamicWorkloadSimple(++hostId, storage, peList2);
        host2
            .setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
            .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(bw)))
            .setVmScheduler(new VmSchedulerTimeShared());
        hostList.add(host2);

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
    private static DatacenterBroker createBroker() {
        return new DatacenterBrokerSimple(simulation);
    }
}
