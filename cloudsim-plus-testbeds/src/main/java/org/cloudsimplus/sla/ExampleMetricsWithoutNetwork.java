package org.cloudsimplus.sla;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSimple;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.DatacenterSimple;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.HostSimple;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.util.CloudletsTableBuilderHelper;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

/**
 *
 * @author RaysaOliveira
 *
 * This example show an simple example using metrics of quality of service
 * without network.
 */
public class ExampleMetricsWithoutNetwork {

    /**
     * The cloudlet list.
     */
    private final List<Cloudlet> cloudletList;

    /**
     * The vmlist.
     */
    private final List<Vm> vmlist;

    /**
     * Creates Vms
     *
     * @param userId broker id
     * @param vms amount of vms to criate
     * @return list de vms
     */
    private List<Vm> createVM(int userId, int vms) {

        //Creates a container to store VMs.
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
            //vm[i] = new VmSimple(i, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
            list.add(vm[i]);
        }
        return list;
    }

    /**
     * Creates cloudlets
     *
     * @param userId broker id
     * @param cloudlets to criate
     * @return list of cloudlets
     */
    private List<Cloudlet> createCloudlet(int userId, int cloudlets) {
        // Creates a container to store Cloudlets
        List<Cloudlet> list = new LinkedList<>();

        //Cloudlet Parameters 
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

    /**
     * Calculates the cost price of resources
     *
     * @param datacenter
     * @param vmCost
     * @param totalCost
     * @return
     */
    /*
    private List<Double> totalCostPrice(Datacenter datacenter, List<Vm> vmlist) {
        double memoryDataCenterVm,bwDataCenterVm, miDataCenterVm, storageDataCenterVm;
        List<Double> totalCost = new LinkedList<>();
        int numberOfVms = datacenter.getCharacteristics().getHostList().size() * MAX_VMS_PER_HOST;
        for (Vm vms : vmlist) {
            memoryDataCenterVm = ((datacenter.getCharacteristics().getCostPerMem()) * vms.getRam() * numberOfVms);
            bwDataCenterVm = ((datacenter.getCharacteristics().getCostPerBw()) * vms.getBw() * numberOfVms);
            miDataCenterVm = ((datacenter.getCharacteristics().getCostPerMi()) * vms.getMips() * numberOfVms);
            storageDataCenterVm = ((datacenter.getCharacteristics().getCostPerStorage()) * vms.getSize() * numberOfVms);

            totalCost += memoryDataCenterVm + bwDataCenterVm + miDataCenterVm + storageDataCenterVm;

        }
        return totalCost;
    }*/

    /**
     * Shows the response time of cloudlets
     *
     * @param cloudlets to calculate the response time
     * @return responseTime
     */
    private double responseTimeCloudlet(List<Cloudlet> cloudlet) {

        double responseTime = 0;
        for (Cloudlet cloudlets : cloudlet) {
            responseTime = cloudlets.getFinishTime() - cloudlets.getDatacenterArrivalTime();
        }
        return responseTime;

    }

    /**
     * Shows the cpu utilization
     *
     * @param cloudlet to calculate the utilization
     * @return cpuUtilization
     */
    private double cpuUtilization(List<Cloudlet> cloudlet) {
        double cpuTime = 0;
        for (Cloudlet cloudlets : cloudlet) {
            cpuTime += cloudlets.getActualCPUTime();
        }
        return cpuTime;
    }

    /**
     * Shows the utilization resources (BW, CPU, RAM) in percentage
     *
     * @param cloudlet to calculate the utilization resources
     * @param time calculates in given time
     * @return utilizationResources
     */
    public double utilizationResources(List<Cloudlet> cloudlet, double time) {
        double utilizationResources = 0, bw, cpu, ram;
        for (Cloudlet cloudlets : cloudlet) {
            bw = cloudlets.getUtilizationOfBw(time);
            cpu = cloudlets.getUtilizationOfCpu(time);
            ram = cloudlets.getUtilizationOfRam(time);
            utilizationResources += bw + cpu + ram;

        }
        return utilizationResources;
    }

    /**
     * Shows the wait time of cloudlets
     *
     * @param cloudlet list of cloudlets
     * @return the waitTime
     */
    public double waitTime(List<Cloudlet> cloudlet) {
        double waitTime = 0;
        for (Cloudlet cloudlets : cloudlet) {
            waitTime += cloudlets.getWaitingTime();
        }
        return waitTime;
    }

    /*
     public static double throughput() {
     //pegar o dowlink BW do edge, pois as Vms estao conectadas nele
     return 1;
     }
     */
    
    /**
     * main()
     *
     * @param args the args
     */
    public static void main(String[] args) {
        Log.printFormattedLine(" Starting... ");
        try {
            new ExampleMetricsWithoutNetwork();
        } catch (Exception e) {
            Log.printFormattedLine("Simulation finished due to unexpected error: %s", e);
        }
    }

    public ExampleMetricsWithoutNetwork() {
        //  Initialize the CloudSim package. 
        int num_user = 1; // number of cloud users
        Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current date and time.
        boolean trace_flag = false; // trace events

        CloudSim.init(num_user, calendar, trace_flag);

        //Create Datacenters
        Datacenter datacenter0 = createDatacenter("Datacenter_0");

        //Create Broker
        DatacenterBroker broker = createBroker();
        int brokerId = broker.getId();

        vmlist = createVM(brokerId, 12);

        // submit vm list to the broker
        broker.submitVmList(vmlist);

        cloudletList = createCloudlet(brokerId, 12);

        // submit cloudlet list to the broker
        broker.submitCloudletList(cloudletList);

        // Sixth step: Starts the simulation
        CloudSim.startSimulation();
        CloudSim.stopSimulation();
        System.out.println("______________________________________________________");
        System.out.println("\n\t\t - System Métrics - \n ");

        //responseTime
        double responseT = responseTimeCloudlet(cloudletList);
        System.out.printf("\t** Response Time of Cloudlets - %.2f %n", responseT);

        //cpu time
        double cpuTime = cpuUtilization(cloudletList);
        System.out.printf("\t** Time CPU %% - %.2f %n ", (cpuTime * 100) / 100);

        //utilization resource
        double time = CloudSim.clock();
        double utilizationresources = utilizationResources(cloudletList, time);
        System.out.printf("\t** Utilization Resources %%  (Bw-CPU-Ram) - %.2f %n", utilizationresources / 100);

        //wait time
        double waitTime = waitTime(cloudletList);
        System.out.printf("\t** Wait Time - %.2f %n", waitTime);

       // total cost
        // double totalPrice = totalCostPrice(datacenter0, vmlist);
        // System.out.printf("\t** Total Cost Price (memory, bw, mips, storage)- %.2f %n", totalPrice);
        System.out.println("______________________________________________________");

        //Final step: Print results when simulation is over
        List<Cloudlet> newList = broker.getCloudletsFinishedList();
        new CloudletsTableBuilderHelper(newList).build();

        Log.printFormattedLine("... finished!");
    }

    /**
     * Creates the datacenter.
     *
     * @param name the name
     *
     * @return the datacenter
     */
    private static Datacenter createDatacenter(String name) {

        // Here are the steps needed to create a PowerDatacenter:
        // 1. We need to create a list to store
        // our machine
        List<Host> hostList = new ArrayList<>();

        // 2. A Machine contains one or more PEs or CPUs/Cores.
        // In this example, it will have only one core.
        List<Pe> peList = new ArrayList<>();

        int mips = 14000;

        // 3. Create PEs and add these into a list.
        peList.add(new PeSimple(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

        // 4. Create Host with its id and list of PEs and add them to the list
        // of machines
        int hostId = 0;
        int ram = 8192; // host memory (MB)
        long storage = 1000000; // host storage
        long bw = 100000;

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
        return new DatacenterSimple(name, characteristics, 
                new VmAllocationPolicySimple(hostList), storageList, 0);
    }

    /**
     * Creates the broker.
     *
     * @return the datacenter broker
     */
    private static DatacenterBroker createBroker() {
        return new DatacenterBrokerSimple("Broker");
    }
}
