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
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudsimplus.util.tablebuilder.CloudletsTableBuilderHelper;
import org.cloudsimplus.util.tablebuilder.TextTableBuilder;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Ram;

/**
 * An example showing how to pause and resume the simulation, and create
 * simulation entities (a DatacenterBroker in this example) dynamically.
 */
public class CloudSimExample7 {
    private static List<Cloudlet> cloudletList;
    private static List<Vm> vmlist;
    private static CloudSim simulation;

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
            boolean trace_flag = false;  // mean trace events

            // Initialize the CloudSim library
            simulation = new CloudSim(num_user, trace_flag);

            // Second step: Create Datacenters
            //Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
            @SuppressWarnings("unused")
            Datacenter datacenter0 = createDatacenter();
            @SuppressWarnings("unused")
            Datacenter datacenter1 = createDatacenter();

            //Third step: Create Broker
            DatacenterBroker broker = createBroker();

            //Fourth step: Create VMs and Cloudlets and send them to broker
            vmlist = createVM(broker, 5, 0); //creating 5 vms
            cloudletList = createCloudlet(broker, 10, 0); // creating 10 cloudlets

            broker.submitVmList(vmlist);
            broker.submitCloudletList(cloudletList);

            ThreadMonitor monitor = new ThreadMonitor(simulation);
            monitor.start();
            Thread.sleep(1000);

            // Fifth step: Starts the simulation
            simulation.start();

            // Final step: Print results when simulation is over
            List<Cloudlet> newList = broker.getCloudletsFinishedList();

            simulation.stop();

            new CloudletsTableBuilderHelper(newList)
                 .setPrinter(
                    new TextTableBuilder("\n#Broker " + broker.getName() + " received cloudlets."))
                 .build();

            if (monitor.getBroker() != null) {
                newList = monitor.getBroker().getCloudletsFinishedList();
                new CloudletsTableBuilderHelper(newList)
                    .setPrinter(
                        new TextTableBuilder("\n#Broker " + monitor.getBroker().getName() + " received cloudlets."))
                    .build();
            }

            Log.printLine("CloudSimExample7 finished!");
        } catch (InterruptedException | RuntimeException e) {
            Log.printFormattedLine("Simulation finished due to unexpected error: %s", e);
        }
    }

    static List<Vm> createVM(DatacenterBroker broker, int numberOfVms, int idShift) {
        //Creates a container to store VMs. This list is passed to the broker later
        List<Vm> list = new ArrayList<>(numberOfVms);

        //VM Parameters
        long size = 10000; //image size (MB)
        int ram = 512; //vm memory (MB)
        int mips = 250;
        long bw = 1000;
        int pesNumber = 1; //number of cpus

        //create VMs
        for (int i = 0; i < numberOfVms; i++) {
            Vm vm = new VmSimple(idShift+i, mips, pesNumber)
                .setRam(ram).setBw(bw).setSize(size)
                .setCloudletScheduler(new CloudletSchedulerTimeShared())
                .setBroker(broker);
            list.add(vm);
        }

        return list;
    }

    static List<Cloudlet> createCloudlet(DatacenterBroker broker, int numberOfCloudlets, int idShift) {
        // Creates a container to store Cloudlets
        List<Cloudlet> list = new ArrayList<>(numberOfCloudlets);

        //cloudlet parameters
        long length = 40000;
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        for (int i = 0; i < numberOfCloudlets; i++) {
            Cloudlet cloudlet = new CloudletSimple(idShift + i, length, pesNumber)
                                .setCloudletFileSize(fileSize)
                                .setCloudletOutputSize(outputSize)
                                .setUtilizationModel(utilizationModel)
                                .setBroker(broker);
            list.add(cloudlet);
        }

        return list;
    }


    private static Datacenter createDatacenter() {
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
        long ram = 16384; //host memory (MB)
        long storage = 1000000; //host storage (MB)
        long bw = 10000; //Megabits/s

        Host host1 = new HostSimple(++hostId, storage, peList1)
            .setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
            .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(bw)))
            .setVmScheduler(new VmSchedulerTimeShared(peList1));
        hostList.add(host1);

        Host host2 = new HostSimple(++hostId, storage, peList2)
            .setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
            .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(bw)))
            .setVmScheduler(new VmSchedulerTimeShared(peList2));
        hostList.add(host2);

        // 5. Create a DatacenterCharacteristics object that stores the
        //    properties of a data center: architecture, OS, list of
        //    Machines, allocation policy: time- or space-shared, time zone
        //    and its price (G$/Pe time unit).
        double cost = 3.0;              // the cost of using processing in this resource
        double costPerMem = 0.05;		// the cost of using memory in this resource
        double costPerStorage = 0.1;	// the cost of using storage in this resource
        double costPerBw = 0.1;			// the cost of using bw in this resource

        DatacenterCharacteristics characteristics =
            new DatacenterCharacteristicsSimple(hostList)
                .setCostPerSecond(cost)
                .setCostPerMem(costPerMem)
                .setCostPerStorage(costPerStorage)
                .setCostPerBw(costPerBw);

        // 6. Finally, we need to create a PowerDatacenter object.
        return new DatacenterSimple(simulation, characteristics, new VmAllocationPolicySimple());
    }

    //We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
    //to the specific rules of the simulated scenario
    static DatacenterBroker createBroker() {
        return new DatacenterBrokerSimple(simulation);
    }
}

/**
 * A thread that will create a new broker at 200 clock time.
 */
class ThreadMonitor extends Thread {
    private final CloudSim simulation;
    /**
     * The DatacenterBroker created inside the thread.
     */
    private DatacenterBroker broker = null;

    public ThreadMonitor(CloudSim simulation){
        this.simulation = simulation;
    }

    @Override
    public void run() {
        simulation.pause(200);

        while (true) {
            if (simulation.isPaused()) {
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.printLine("\n\n\n" + simulation.clock() + ": The simulation is paused for 5 sec \n\n");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        broker = CloudSimExample7.createBroker();

        //Create VMs and Cloudlets and send them to broker
        //creating 5 vms
        List<Vm> vmlist = CloudSimExample7.createVM(broker, 5, 100);

        //creating 5 cloudlets
        List<Cloudlet> cloudletList = CloudSimExample7.createCloudlet(broker, 5, 100);

        broker.submitVmList(vmlist);
        broker.submitCloudletList(cloudletList);

        simulation.resume();
    }

    public DatacenterBroker getBroker() {
        return broker;
    }
};
