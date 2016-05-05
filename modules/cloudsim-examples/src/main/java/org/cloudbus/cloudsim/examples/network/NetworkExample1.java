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
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSimple;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterSimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.HostSimple;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.network.NetworkTopology;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.util.TableBuilderHelper;
import org.cloudbus.cloudsim.util.TextTableBuilder;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Ram;

/**
 * A simple example showing how to create
 * a datacenter with 1 host and a network
 * topology and and run 1 cloudlet on it.
 */
public class NetworkExample1 {

    /** The cloudlet list. */
    private static List<Cloudlet> cloudletList;

    /** The vmlist. */
    private static List<Vm> vmlist;

    /**
     * Creates main() to run this example
     * @param args
     */
    public static void main(String[] args) {
            Log.printFormattedLine("Starting %s...", NetworkExample1.class.getSimpleName());
            try {
                    // First step: Initialize the CloudSim package. It should be called
                    // before creating any entities.
                    int num_user = 1;   // number of cloud users
                    Calendar calendar = Calendar.getInstance();
                    boolean trace_flag = false;  // mean trace events

                    // Initialize the CloudSim library
                    CloudSim.init(num_user, calendar, trace_flag);

                    // Second step: Create Datacenters
                    //Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
                    Datacenter datacenter0 = createDatacenter("Datacenter_0");

                    //Third step: Create Broker
                    DatacenterBroker broker = createBroker();
                    int brokerId = broker.getId();

                    //Fourth step: Create one virtual machine
                    vmlist = new ArrayList<>();

                    //VM description
                    int vmid = 0;
                    int mips = 250;
                    long size = 10000; //image size (MB)
                    int ram = 512; //vm memory (MB)
                    long bw = 1000;
                    int pesNumber = 1; //number of cpus
                    String vmm = "Xen"; //VMM name

                    //create VM
                    Vm vm1 = new VmSimple(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());

                    //add the VM to the vmList
                    vmlist.add(vm1);

                    //submit vm list to the broker
                    broker.submitVmList(vmlist);


                    //Fifth step: Create one Cloudlet
                    cloudletList = new ArrayList<>();

                    //Cloudlet properties
                    int id = 0;
                    long length = 40000;
                    long fileSize = 300;
                    long outputSize = 300;
                    UtilizationModel utilizationModel = new UtilizationModelFull();

                    Cloudlet cloudlet1 = new CloudletSimple(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                    cloudlet1.setUserId(brokerId);

                    //add the cloudlet to the list
                    cloudletList.add(cloudlet1);

                    //submit cloudlet list to the broker
                    broker.submitCloudletList(cloudletList);

                    //Sixth step: configure network
                    //load the network topology file
                    NetworkTopology.buildNetworkTopology("topology.brite");

                    //maps CloudSim entities to BRITE entities
                    //Datacenter will correspond to BRITE node 0
                    int briteNode=0;
                    NetworkTopology.mapNode(datacenter0.getId(),briteNode);

                    //Broker will correspond to BRITE node 3
                    briteNode=3;
                    NetworkTopology.mapNode(broker.getId(),briteNode);


                    // Seventh step: Starts the simulation
                    CloudSim.startSimulation();

                    // Final step: Print results when simulation is over
                    List<Cloudlet> newList = broker.getCloudletsFinishedList();

                    CloudSim.stopSimulation();

                    TableBuilderHelper.print(new TextTableBuilder(), newList);
                    Log.printFormattedLine("%s finished!", NetworkExample1.class.getSimpleName());
            }
            catch (Exception e) {
                    e.printStackTrace();
                    Log.printLine("The simulation has been terminated due to an unexpected error");
            }
    }

    private static Datacenter createDatacenter(String name){

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
            int hostId=0;
            int ram = 2048; //host memory (MB)
            long storage = 1000000; //host storage
            long bw = 10000;

            hostList.add(new HostSimple(
                                    hostId,
                                    new ResourceProvisionerSimple(new Ram(ram)),
                                    new ResourceProvisionerSimple(new Bandwidth(bw)),
                                    storage,
                                    peList,
                                    new VmSchedulerTimeShared(peList)
                            )
                    ); // This is our machine

            // 5. Create a DatacenterCharacteristics object that stores the
            //    properties of a data center: architecture, OS, list of
            //    Machines, allocation policy: time- or space-shared, time zone
            //    and its price (G$/Pe time unit).
            String arch = "x86";      // system architecture
            String os = "Linux";          // operating system
            String vmm = "Xen";
            double time_zone = 10.0;         // time zone this resource located
            double cost = 3.0;              // the cost of using processing in this resource
            double costPerMem = 0.05;		// the cost of using memory in this resource
            double costPerStorage = 0.001;	// the cost of using storage in this resource
            double costPerBw = 0.0;			// the cost of using bw in this resource
            List<FileStorage> storageList = new LinkedList<>();	//we are not adding SAN devices by now

            DatacenterCharacteristics characteristics = new DatacenterCharacteristicsSimple (
                            arch, os, vmm, hostList, time_zone, cost, costPerMem,
                            costPerStorage, costPerBw);


            // 6. Finally, we need to create a DatacenterSimple object.
            Datacenter datacenter = null;
            try {
                    datacenter = new DatacenterSimple(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
            } catch (Exception e) {
                    e.printStackTrace();
            }

            return datacenter;
    }

    //We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
    //to the specific rules of the simulated scenario
    private static DatacenterBroker createBroker(){

            DatacenterBroker broker = null;
            try {
                    broker = new DatacenterBrokerSimple("Broker");
            } catch (Exception e) {
                    e.printStackTrace();
                    return null;
            }
            return broker;
    }
}
