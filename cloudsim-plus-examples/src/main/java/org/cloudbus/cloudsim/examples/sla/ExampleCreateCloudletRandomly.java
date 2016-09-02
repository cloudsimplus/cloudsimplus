/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudbus.cloudsim.examples.sla;

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
 */
public class ExampleCreateCloudletRandomly {

    /**
     * The cloudlet list.
     */
    private List<Cloudlet> cloudletList;

    /**
     * The vmlist.
     */
    private List<Vm> vmlist;

    /**
     * Number of simulations to run.
     */
    private static final int NUMBER_OF_SIMULATIONS = 1000;

    /**
     * Time length of each simulation in minutes.
     */
    private static final int SIMULATION_TIME_LENGHT = 25;

    /**
     * Average number of customers that arrives per minute. The value of 0.4
     * customers per minute means that 1 customer will arrive at every 2.5
     * minutes. It means that 1 minute / 0.4 customer per minute = 1 customer at
     * every 2.5 minutes. This is the interarrival time (in average).
     */
    private static final double MEAN_CUSTOMERS_ARRIVAL_PER_MINUTE = 0.4;

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
        }
        return list;
    }

    /**
     * Create cloudlets
     *
     * @param userId
     * @param cloudlets
     * @return
     */
    private List<Cloudlet> createCloudlet(int userId, int cloudlets) {
        // Creates a container to store Cloudlets
        List<Cloudlet> list = new LinkedList<>();
        //cloudlet parameters
        long length = 1000;
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();
        Cloudlet[] cloudlet = new Cloudlet[cloudlets];

        for (int i = 0; i < cloudlets; i++) {
            cloudlet[i] = new CloudletSimple(
                    i, length, pesNumber, fileSize, outputSize,
                    utilizationModel, utilizationModel, utilizationModel);
            // setting the owner of these Cloudlets
            cloudlet[i].setUserId(userId);
            list.add(cloudlet[i]);
        }
        return list;
    }

    public static void main(String[] args) {
        Log.printFormattedLine(" Starting... ");
        try {
            new ExampleCreateCloudletRandomly();
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

        vmlist = createVM(brokerId, 3);

        // submit vm list to the broker
        broker.submitVmList(vmlist);

        List<Cloudlet> list = new LinkedList<>();
        //cloudlet parameters
        long length = 1000;
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();
        Cloudlet cloudlet = null;
        //poisson

        int customersArrivedInAllSimulations = 0;
        PoissonProcess poisson = null;

        for (int i = 0; i < NUMBER_OF_SIMULATIONS; i++) {
            long seed = System.currentTimeMillis();
            poisson = new PoissonProcess(MEAN_CUSTOMERS_ARRIVAL_PER_MINUTE, seed);
            System.out.printf("Simulation number %d\n", i);
            customersArrivedInAllSimulations += runSimulation(poisson, false);
            cloudlet.setSubmissionDelay(CloudSim.clock());
            cloudletList.add(cloudlet);
        }
        
        double mean = customersArrivedInAllSimulations/(double)NUMBER_OF_SIMULATIONS;
        System.out.printf("\nArrived cloudlets average after %d simulations: %.2f\n", NUMBER_OF_SIMULATIONS, mean);

        broker.submitCloudletList(cloudletList);

        CloudSim.startSimulation();
        CloudSim.stopSimulation();

        //Final step: Print results when simulation is over
        List<Cloudlet> newList = broker.getCloudletsFinishedList();
        CloudletsTableBuilderHelper.print(new TextTableBuilder(), newList);

        Log.printFormattedLine("... finished!");
    }

    private static Datacenter createDatacenter(String name) {

        // Here are the steps needed to create a PowerDatacenter:
        // 1. We need to create a list to store
        // our machine
        List<Host> hostList = new ArrayList<Host>();

        // 2. A Machine contains one or more PEs or CPUs/Cores.
        // In this example, it will have only one core.
        List<Pe> peList = new ArrayList<Pe>();

        int mips = 1000;

        // 3. Create PEs and add these into a list.
        peList.add(new PeSimple(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

        // 4. Create Host with its id and list of PEs and add them to the list
        // of machines
        int hostId = 0;
        int ram = 2048; // host memory (MB)
        long storage = 1000000; // host storage
        long bw = 10000;

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
     * Simulates the arrival of customers for a given time period.
     *
     * @param poisson the PoissonProcess object that will compute the customer
     * arrivals probabilities
     * @param showCustomerArrivals if the arrival of each customer has to be
     * shown
     * @return the number of arrived customers
     */
    private static int runSimulation(PoissonProcess poisson, boolean showCustomerArrivals) {
        int totalArrivedCustomers = 0;

        /*We want to check the probability of 1 customer to arrive at each
         single minute. The default k value is 1, so we dont need to set it.*/
        for (int minute = 0; minute < SIMULATION_TIME_LENGHT; minute++) {
            if (poisson.haveKEventsHappened()) {
                totalArrivedCustomers += poisson.getK();
                if (showCustomerArrivals) {
                    System.out.printf(
                            "%d customers arrived at minute %d\n",
                            poisson.getK(), minute, poisson.probabilityToArriveNextKEvents());
                }
            }
        }
        return totalArrivedCustomers;
    }

}
