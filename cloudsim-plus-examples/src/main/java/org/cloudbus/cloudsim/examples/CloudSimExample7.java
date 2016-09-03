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
import org.cloudbus.cloudsim.util.CloudletsTableBuilderHelper;
import org.cloudbus.cloudsim.util.TextTableBuilder;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Ram;

/**
 * An example showing how to pause and resume the simulation, and create
 * simulation entities (a DatacenterBroker in this example) dynamically.
 */
public class CloudSimExample7 {

    /**
     * The cloudlet list.
     */
    private static List<Cloudlet> cloudletList;

    /**
     * The vmlist.
     */
    private static List<Vm> vmlist;

    static List<Vm> createVM(int userId, int numberOfVms, int idShift) {
        //Creates a container to store VMs. This list is passed to the broker later
        List<Vm> list = new LinkedList<>();

        //VM Parameters
        long size = 10000; //image size (MB)
        int ram = 512; //vm memory (MB)
        int mips = 250;
        long bw = 1000;
        int pesNumber = 1; //number of cpus
        String vmm = "Xen"; //VMM name

        //create VMs
        Vm[] vm = new Vm[numberOfVms];

        for (int i = 0; i < numberOfVms; i++) {
            vm[i] = new VmSimple(idShift + i, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            list.add(vm[i]);
        }

        return list;
    }

    static List<Cloudlet> createCloudlet(int userId, int numberOfCloudlets, int idShift) {
        // Creates a container to store Cloudlets
        List<Cloudlet> list = new LinkedList<>();

        //cloudlet parameters
        long length = 40000;
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        Cloudlet[] cloudlet = new Cloudlet[numberOfCloudlets];

        for (int i = 0; i < numberOfCloudlets; i++) {
            cloudlet[i] = new CloudletSimple(idShift + i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            // setting the owner of these Cloudlets
            cloudlet[i].setUserId(userId);
            list.add(cloudlet[i]);
        }

        return list;
    }

    /**
     * Runs the example.
     *
     * @param args
     */
    public static void main(String[] args) {
        Log.printLine("Starting CloudSimExample7...");

        try {
            // First step: Initialize the CloudSim package. It should be called
            // before creating any entities.
            int num_user = 2;   // number of grid users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;  // mean trace events

            // Initialize the CloudSim library
            CloudSim.init(num_user, calendar, trace_flag);

            // Second step: Create Datacenters
            //Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
            @SuppressWarnings("unused")
            Datacenter datacenter0 = createDatacenter("Datacenter_0");
            @SuppressWarnings("unused")
            Datacenter datacenter1 = createDatacenter("Datacenter_1");

            //Third step: Create Broker
            DatacenterBroker broker = createBroker("Broker_0");
            int brokerId = broker.getId();

            //Fourth step: Create VMs and Cloudlets and send them to broker
            vmlist = createVM(brokerId, 5, 0); //creating 5 vms
            cloudletList = createCloudlet(brokerId, 10, 0); // creating 10 cloudlets

            broker.submitVmList(vmlist);
            broker.submitCloudletList(cloudletList);

            ThreadMonitor monitor = new ThreadMonitor();
            monitor.start();
            Thread.sleep(1000);

            // Fifth step: Starts the simulation
            CloudSim.startSimulation();

            // Final step: Print results when simulation is over
            List<Cloudlet> newList = broker.getCloudletsFinishedList();

            CloudSim.stopSimulation();

            CloudletsTableBuilderHelper.print(
                    new TextTableBuilder(
                            "\n#Broker " + broker.getName() + " received cloudlets."),
                    newList);
            if (monitor.getBroker() != null) {
                newList = monitor.getBroker().getCloudletsFinishedList();
                CloudletsTableBuilderHelper.print(
                        new TextTableBuilder(
                                "\n#Broker " + monitor.getBroker().getName() + " received cloudlets."),
                        newList);
            }

            Log.printLine("CloudSimExample7 finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }
    }

    private static Datacenter createDatacenter(String name) {

        // Here are the steps needed to create a PowerDatacenter:
        // 1. We need to create a list to store one or more
        //    Machines
        List<Host> hostList = new ArrayList<>();

        // 2. A Machine contains one or more PEs or CPUs/Cores. Therefore, should
        //    create a list to store these PEs before creating
        //    a Machine.
        List<Pe> peList1 = new ArrayList<>();

        int mips = 1000;

        // 3. Create PEs and add these into the list.
        //for a quad-core machine, a list of 4 PEs is required:
        peList1.add(new PeSimple(0, new PeProvisionerSimple(mips))); 
        peList1.add(new PeSimple(1, new PeProvisionerSimple(mips)));
        peList1.add(new PeSimple(2, new PeProvisionerSimple(mips)));
        peList1.add(new PeSimple(3, new PeProvisionerSimple(mips)));

        //Another list, for a dual-core machine
        List<Pe> peList2 = new ArrayList<>();

        peList2.add(new PeSimple(0, new PeProvisionerSimple(mips)));
        peList2.add(new PeSimple(1, new PeProvisionerSimple(mips)));

        //4. Create Hosts with its id and list of PEs and add them to the list of machines
        int hostId = -1;
        int ram = 16384; //host memory (MB)
        long storage = 1000000; //host storage
        long bw = 10000;

        hostList.add(new HostSimple(
            ++hostId,
            new ResourceProvisionerSimple(new Ram(ram)),
            new ResourceProvisionerSimple(new Bandwidth(bw)),
            storage,
            peList1,
            new VmSchedulerTimeShared(peList1)
        )); // This is our first machine

        hostList.add(new HostSimple(
            ++hostId,
            new ResourceProvisionerSimple(new Ram(ram)),
            new ResourceProvisionerSimple(new Bandwidth(bw)),
            storage,
            peList2,
            new VmSchedulerTimeShared(peList2)
        )); // Second machine

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
        double costPerStorage = 0.1;	// the cost of using storage in this resource
        double costPerBw = 0.1;			// the cost of using bw in this resource
        List<FileStorage> storageList = new LinkedList<>();	//we are not adding SAN devices by now

        DatacenterCharacteristics characteristics = new DatacenterCharacteristicsSimple(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

        // 6. Finally, we need to create a PowerDatacenter object.
        return new DatacenterSimple(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
    }

    //We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
    //to the specific rules of the simulated scenario
    static DatacenterBroker createBroker(String name) {
        return new DatacenterBrokerSimple(name);
    }
}

/**
 * A thread that will create a new broker at 200 clock time.
 */
class ThreadMonitor extends Thread {
    /**
     * The DatacenterBroker created inside the thread.
     */
    private DatacenterBroker broker = null;

    @Override
    public void run() {
        CloudSim.pauseSimulation(200);

        while (true) {
            if (CloudSim.isPaused()) {
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.printLine("\n\n\n" + CloudSim.clock() + ": The simulation is paused for 5 sec \n\n");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        broker = CloudSimExample7.createBroker("Broker_1");

        //Create VMs and Cloudlets and send them to broker
        //creating 5 vms
        List<Vm> vmlist = CloudSimExample7.createVM(broker.getId(), 5, 100);

        //creating 5 cloudlets
        List<Cloudlet> cloudletList = CloudSimExample7.createCloudlet(broker.getId(), 5, 100);

        broker.submitVmList(vmlist);
        broker.submitCloudletList(cloudletList);

        CloudSim.resumeSimulation();
    }

    public DatacenterBroker getBroker() {
        return broker;
    }
};
