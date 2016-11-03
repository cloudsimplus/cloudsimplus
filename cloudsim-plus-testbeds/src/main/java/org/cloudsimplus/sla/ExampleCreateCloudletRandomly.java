/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.sla;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSimple;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.DatacenterSimple;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.HostSimple;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.CloudletsTableBuilderHelper;
import org.cloudbus.cloudsim.util.TextTableBuilder;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

/**
 *
 * @author raysaoliveira
 *
 * This simple example show how to create cloudlets randomly using poisson
 * distribution.
 */
public class ExampleCreateCloudletRandomly {

    /**
     * List of Cloudlet .
     */
    private final List<Cloudlet> cloudletList;

    /**
     * List of Vms
     */
    private final List<Vm> vmlist;

    /**
     * Average number of customers that arrives per minute. The value of 0.4
     * customers per minute means that 1 customer will arrive at every 2.5
     * minutes. It means that 1 minute / 0.4 customer per minute = 1 customer at
     * every 2.5 minutes. This is the interarrival time (in average).
     */
    private static final double MEAN_CUSTOMERS_ARRIVAL_PER_MINUTE = 0.4;

    /**
     * Number of simulations to run.
     */
    private static final int NUMBER_OF_SIMULATIONS = 1;

    /**
     * The maximum time that a Cloudlet can arrive. Between the first simulation
     * minute and this time, different Cloudlets can arrive.
     */
    private final int MAX_TIME_FOR_CLOUDLET_ARRIVAL = 100;

    /**
     * Create Vms
     *
     * @param userId
     * @param vms
     * @return list de vms
     */
    private List<Vm> createVM(int userId, int vms) {
        //Creates a container to store VMs. This list is passed to the broker later
        List<Vm> list = new LinkedList<>();
        //VM Parameters
        int vmid = 0;
        long size = 10000; //image size (MB)
        int ram = 512; //vm memory (MB)
        int mips = 1000;
        long bw = 1000;
        int pesNumber = 1; //number of cpus
        String vmm = "Xen"; //VMM name

        //create VMs
        Vm[] vm = new Vm[vms];

        for (int i = 0; i < vms; i++) {
            vm[i] = new VmSimple(i, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            //for creating a VM with a space shared scheduling policy for cloudlets:
            //vm[i] = VmSimple(i, userId, mips, pesNumber, ram, bw, size, priority, vmm, new CloudletSchedulerSpaceShared());
            list.add(vm[i]);
            vmid++;
        }
        return list;
    }

    public static void main(String[] args) {
        Log.printFormattedLine(" Starting... ");
        try {
            for (int i = 0; i < NUMBER_OF_SIMULATIONS; i++) {
                new ExampleCreateCloudletRandomly();
            }
            Log.printFormattedLine("... finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
    }

    public ExampleCreateCloudletRandomly() {

        int num_user = 1; // number of cloud users
        Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current date and time.
        boolean trace_flag = false; // trace events

        CloudSim.init(num_user, calendar, trace_flag);

        // Second step: Create Datacenters
        Datacenter datacenter0 = createDatacenter("Datacenter_0");

        // Third step: Create Broker
        DatacenterBroker broker = createBroker();
        int brokerId = broker.getId();
       
        //create cloudlet randomly
        cloudletList = new ArrayList<>();
        long seed = System.currentTimeMillis();
        //creates a poisson process that checks the arrival of 1 (k) cloudlet
        //1 is the default value for k
        PoissonProcess poisson = new PoissonProcess(MEAN_CUSTOMERS_ARRIVAL_PER_MINUTE, seed);
        int totalArrivedCustomers = 0;
        int cloudletId = 0;
        for (int minute = 0; minute < MAX_TIME_FOR_CLOUDLET_ARRIVAL; minute++) {
            if (poisson.haveKEventsHappened()) { //Have k Cloudlets arrived?
                totalArrivedCustomers += poisson.getK();
                Cloudlet cloudlet = createCloudlet(cloudletId++, brokerId);
                cloudlet.setSubmissionDelay(minute);
                cloudletList.add(cloudlet);

                System.out.printf(
                        "%d cloudlets arrived at minute %d\n",
                        poisson.getK(), minute, poisson.probabilityToArriveNextKEvents());
            }
        }
        
        System.out.printf("\n\t%d cloudlets have arrived\n", totalArrivedCustomers);

        broker.submitCloudletList(cloudletList);

        vmlist = createVM(brokerId, totalArrivedCustomers);

        // submit vm list to the broker
        broker.submitVmList(vmlist);

        CloudSim.startSimulation();
        CloudSim.stopSimulation();

        //Final step: Print results when simulation is over
        List<Cloudlet> newList = broker.getCloudletsFinishedList();
        new CloudletsTableBuilderHelper(new TextTableBuilder(), newList);

    }

    private Cloudlet createCloudlet(int cloudletId, int brokerId) {
        long length = 1000;
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();
        Cloudlet cloudlet = new CloudletSimple(cloudletId, length, pesNumber, fileSize,
                outputSize, utilizationModel, utilizationModel,
                utilizationModel);
        cloudlet.setUserId(brokerId);
        return cloudlet;
    }

    private static Datacenter createDatacenter(String name) {

        // Here are the steps needed to create a PowerDatacenter:
        // 1. We need to create a list to store
        // our machine
        List<Host> hostList = new ArrayList<>();

        // 2. A Machine contains one or more PEs or CPUs/Cores.
        // In this example, it will have only one core.
        List<Pe> peList = new ArrayList<Pe>();

        int mips = 30000000;

        // 3. Create PEs and add these into a list.
        peList.add(new PeSimple(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
        // 4. Create Host with its id and list of PEs and add them to the list
        // of machines
        int hostId = 0;
        int ram = 1000000; // host memory (MB)
        long storage = 100000000; // host storage
        long bw = 3000000;

        hostList.add(new HostSimple(
                hostId,
                new ResourceProvisionerSimple<>(new Ram(ram)),
                new ResourceProvisionerSimple<>(new Bandwidth(bw)),
                storage,
                peList,
                new VmSchedulerTimeShared(peList)
        )
        ); // This is our machine

        // 5. Create a DatacenterCharacteristics object that stores the
        // properties of a data center: architecture, OS, list of
        // Machines, allocation policy: time- or space-shared, time zone
        // and its price (G$/Pe time unit).
        String arch = "x86"; // system architecture
        String os = "Linux"; // operating system
        String vmm = "Xen";
        double time_zone = 10.0; // time zone this resource located
        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this
        // resource
        double costPerBw = 0.0; // the cost of using bw in this resource
        LinkedList<FileStorage> storageList = new LinkedList<>(); // we are not adding SAN
        // devices by now

        DatacenterCharacteristics characteristics = new DatacenterCharacteristicsSimple(
                arch, os, vmm, hostList, time_zone, cost, costPerMem,
                costPerStorage, costPerBw);

        // 6. Finally, we need to create a PowerDatacenter object.
        Datacenter datacenter = null;
        try {
            datacenter = new DatacenterSimple(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datacenter;
    }

    /**
     * Creates the broker.
     *
     * @return the datacenter broker
     */
    private static DatacenterBroker createBroker() {
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBrokerSimple("Broker");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return broker;
    }

    /**
     * @return the MAX_TIME_FOR_CLOUDLET_ARRIVAL
     */
    public int getMAX_TIME_FOR_CLOUDLET_ARRIVAL() {
        return MAX_TIME_FOR_CLOUDLET_ARRIVAL;
    }

}
