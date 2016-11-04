package org.cloudsimplus.sla;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.network.datacenter.EdgeSwitch;
import org.cloudbus.cloudsim.network.datacenter.NetDatacenterBroker;
import org.cloudbus.cloudsim.network.datacenter.NetworkCloudlet;
import org.cloudbus.cloudsim.network.datacenter.NetworkDatacenter;
import org.cloudbus.cloudsim.network.datacenter.NetworkHost;
import org.cloudbus.cloudsim.network.datacenter.NetworkVm;
import org.cloudbus.cloudsim.network.datacenter.NetworkVmAllocationPolicy;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudsimplus.util.tablebuilder.CloudletsTableBuilderHelper;
import org.cloudsimplus.util.tablebuilder.TextTableBuilder;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

/**
 *
 * @author raysaoliveira
 */
public class NetworkVmsExampleWithMetrics {

    /**
     * The cloudlet list.
     */
    private final List<NetworkCloudlet> cloudletList;

    /**
     * The vmlist.
     */
    private final List<NetworkVm> vmlist;

    /**
     * The datacenter
     */
    NetworkDatacenter datacenter0;
    
    /**
     * Create NetworkVms
     *
     * @param userId
     * @param vms
     * @return list de vms
     */
    private List<NetworkVm> createVM(int userId, int vms) {
        //Creates a container to store VMs. This list is passed to the broker later
        List<NetworkVm> list = new LinkedList<>();
        //VM Parameters
        long size = 10000; //image size (MB)
        int ram = 512; //vm memory (MB)
        int mips = 1000;
        long bw = 1000;
        int pesNumber = 1; //number of cpus
        String vmm = "Xen"; //VMM name

        //create VMs
        NetworkVm[] vm = new NetworkVm[vms];

        for (int i = 0; i < vms; i++) {
            vm[i] = new NetworkVm(i, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            //for creating a VM with a space shared scheduling policy for cloudlets:
            //vm[i] = VmSimple(i, userId, mips, pesNumber, ram, bw, size, priority, vmm, new CloudletSchedulerSpaceShared());
            list.add(vm[i]);
        }
        return list;
    }

    /**
     * Create NetworkCloudlets
     *
     * @param userId
     * @param cloudlets
     * @return
     */
    private List<NetworkCloudlet> createCloudlet(int userId, int cloudlets) {
        // Creates a container to store Cloudlets
        List<NetworkCloudlet> list = new LinkedList<>();
        //cloudlet parameters
        long length = 1000;
        int pesNumber = 1;
        long fileSize = 300;
        long outputSize = 300;
        long memory = 512;
        UtilizationModel utilizationModel = new UtilizationModelFull();
        NetworkCloudlet[] cloudlet = new NetworkCloudlet[cloudlets];

        
        for (int i = 0; i < cloudlets; i++) {
            cloudlet[i] = new NetworkCloudlet(i, length, pesNumber, fileSize, outputSize,memory,
                    utilizationModel, utilizationModel, utilizationModel);
            // setting the owner of these Cloudlets
            cloudlet[i].setUserId(userId);
            list.add(cloudlet[i]);
        }
        return list;
    }

    /**
     * main
     * @param args
     */
    public static void main(String[] args) {
        Log.printFormattedLine(" Starting... ");
        try {
            new NetworkVmsExampleWithMetrics();
        } catch (Exception e) {
<<<<<<< HEAD
            Log.printLine("Unwanted errors happen");
=======
            Log.printFormattedLine("Simulation finished due to unexpected error: %s", e);
>>>>>>> upstream/master
        }
    }

    private NetworkVmsExampleWithMetrics() {
        // First step: Initialize the CloudSim package. It should be called before creating any entities.
        int num_user = 1; // number of cloud users
        Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current date and time.
        boolean trace_flag = false; // trace events

        CloudSim.init(num_user, calendar, trace_flag);

        // Second step: Create Datacenters
        datacenter0 = createDatacenter("Datacenter_0");

        // Third step: Create Broker
        NetDatacenterBroker broker = createBroker();
        int brokerId = broker.getId();

        vmlist = createVM(brokerId, 5);

        // submit vm list to the broker
        broker.submitVmList(vmlist);

        /* Fifth step: Read Cloudlets from workload external file in the swf format
         WorkloadFileReader workloadFileReader = new WorkloadFileReader("src/main/java/org/cloudbus/cloudsim/examples/sla/UniLu-Gaia-2014-2.swf", 1);
         cloudletList = workloadFileReader.generateWorkload().subList(0, 1000);
         for (Cloudlet cloudlet : cloudletList) {
         cloudlet.setUserId(brokerId);
         } */
        cloudletList = createCloudlet(brokerId, 10);

        // submit cloudlet list to the broker
        broker.submitCloudletList(cloudletList);

        // Sixth step: Starts the simulation
        CloudSim.startSimulation();
        CloudSim.stopSimulation();

        //Final step: Print results when simulation is over
        List<Cloudlet> newList = broker.getCloudletsFinishedList();
<<<<<<< HEAD
        new CloudletsTableBuilderHelper(new TextTableBuilder(), newList);
=======
        new CloudletsTableBuilderHelper(newList).build();
>>>>>>> upstream/master

        Log.printFormattedLine("... finished!");
    }

    /**
     * Creates the NetworkDatacenter.
     *
     * @param name the datacenter
     *
     * @return the datacenter
     */
    protected final NetworkDatacenter createDatacenter(String name) {
<<<<<<< HEAD

=======
>>>>>>> upstream/master
        // Here are the steps needed to create a PowerDatacenter:
        // 1. We need to create a list to store
        // our machine
        List<Host> hostList = new ArrayList<>();

        // 2. A Machine contains one or more PEs or CPUs/Cores.
        // In this example, it will have only one core.
<<<<<<< HEAD
        List<Pe> peList = new ArrayList<Pe>();
=======
        List<Pe> peList = new ArrayList<>();
>>>>>>> upstream/master

        int mips = 8000;

        // 3. Create PEs and add these into a list.
        peList.add(new PeSimple(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

        // 4. Create Host with its id and list of PEs and add them to the list
        // of machines
        int hostId = 0;
        int ram = 4096; // host memory (MB)
        long storage = 1000000; // host storage
        long bw = 10000;

        hostList.add(new NetworkHost(
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
<<<<<<< HEAD
        NetworkDatacenter datacenter = null;
        try {
            datacenter = new NetworkDatacenter(name, characteristics, new NetworkVmAllocationPolicy(hostList), storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
=======
        NetworkDatacenter datacenter =
                new NetworkDatacenter(name, characteristics, 
                        new NetworkVmAllocationPolicy(hostList), storageList, 0);
>>>>>>> upstream/master
        createNetwork(datacenter);
        return datacenter;
    }

    /**
     * Creates internal Datacenter network.
     * @param datacenter datacenter where the network will be created
     */
    protected void createNetwork(NetworkDatacenter datacenter) {
        EdgeSwitch[] edgeSwitches = new EdgeSwitch[1];
        for (int i = 0; i < edgeSwitches.length; i++) {
            edgeSwitches[i] = new EdgeSwitch("Edge" + i, datacenter);
            datacenter.addSwitch(edgeSwitches[i]);
        }

        for (NetworkHost host : datacenter.<NetworkHost>getHostList()) {
            int switchNum = host.getId() / edgeSwitches[0].getPorts();
            edgeSwitches[switchNum].getHostList().put(host.getId(), host);
            datacenter.addHostToSwitch(host, edgeSwitches[switchNum]);
            host.setEdgeSwitch(edgeSwitches[switchNum]);
        }
    }

    
    /**
     * Creates the broker.
     *
     * @return the datacenter broker
     */
    private static NetDatacenterBroker createBroker() {
<<<<<<< HEAD
        NetDatacenterBroker broker = null;
        try {
            broker = new NetDatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return broker;
=======
        return new NetDatacenterBroker("Broker");
>>>>>>> upstream/master
    }
}

   /* private void totalCostPrice() {
        double memoryDataCenterVm, totalCost = 0;
        double bwDataCenterVm, miDataCenterVm, storageDataCenterVm;
        int numberOfVms = Datacenter().getCharacteristics().getHostList().size() * MAX_VMS_PER_HOST;
        for (NetworkVm vms : getVmList()) {
            memoryDataCenterVm = ((getDatacenter().getCharacteristics().getCostPerMem()) * vms.getRam() * numberOfVms);
            bwDataCenterVm = ((getDatacenter().getCharacteristics().getCostPerBw()) * vms.getBw() * numberOfVms);
            miDataCenterVm = ((getDatacenter().getCharacteristics().getCostPerMi()) * vms.getMips() * numberOfVms);
            storageDataCenterVm = ((getDatacenter().getCharacteristics().getCostPerStorage()) * vms.getSize() * numberOfVms);

            totalCost = memoryDataCenterVm + bwDataCenterVm + miDataCenterVm + storageDataCenterVm;
        }
        System.out.println("* Total Cost Price ******: " + totalCost);
    }
    
    */
    /*
     private void responseTimeCloudlet(NetworkCloudlet cloudlet) {    
     double rt = cloudlet.getFinishTime() - cloudlet.getDatacenterArrivalTime();
     System.out.println("***** Tempo de resposta CLOUDLETS - " + rt);
     } */
