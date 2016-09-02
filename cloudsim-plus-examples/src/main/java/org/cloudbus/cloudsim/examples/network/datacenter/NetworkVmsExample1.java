package org.cloudbus.cloudsim.examples.network.datacenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.network.datacenter.AppCloudlet;
import org.cloudbus.cloudsim.network.datacenter.CloudletExecutionTask;
import org.cloudbus.cloudsim.network.datacenter.CloudletReceiveTask;
import org.cloudbus.cloudsim.network.datacenter.CloudletSendTask;
import org.cloudbus.cloudsim.network.datacenter.CloudletTask;
import org.cloudbus.cloudsim.network.datacenter.EdgeSwitch;
import org.cloudbus.cloudsim.network.datacenter.NetDatacenterBroker;
import org.cloudbus.cloudsim.network.datacenter.NetworkCloudletSpaceSharedScheduler;
import org.cloudbus.cloudsim.network.datacenter.NetworkDatacenter;
import org.cloudbus.cloudsim.network.datacenter.NetworkHost;
import org.cloudbus.cloudsim.network.datacenter.NetworkVm;
import org.cloudbus.cloudsim.network.datacenter.NetworkCloudlet;
import org.cloudbus.cloudsim.network.datacenter.NetworkVmAllocationPolicy;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.CloudletsTableBuilderHelper;
import org.cloudbus.cloudsim.util.TextTableBuilder;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

/**
 * A simple example showing how two {@link NetworkCloudlet}'s
 * communicate between them, each one running inside VMs of 
 * different hosts.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class NetworkVmsExample1 {
    private static final String ARCH = "x86"; // system architecture
    private static final String OS = "Linux"; // operating system
    private static final String VMM = "Xen";
    private static final double TIME_ZONE = 10.0; // time zone this resource located

    private static final double COST = 3.0; // the cost of using processing in this resource
    private static final double COST_PER_MEM = 0.05; // the cost of using memory in this resource
    private static final double COST_PER_STORAGE = 0.001; // the cost of using storage in this resource
    private static final double COST_PER_BW = 0.0; // the cost of using bw in this resource

    private static final int  NUMBER_OF_HOSTS = 2;
    private static final int  HOST_MIPS = 1000;
    private static final int  HOST_PES = 4;
    private static final int  HOST_RAM = 2048; // host memory (MB)
    private static final long HOST_STORAGE = 1000000; // host storage
    private static final long HOST_BW = 10000;

    private static final int  NETCLOUDLET_EXECUTION_TASK_LENGTH = 4000;
    private static final int  NETCLOUDLET_FILE_SIZE = 300;
    private static final int  NETCLOUDLET_OUTPUT_SIZE = 300;
    private static final long PACKET_DATA_LENGTH_IN_BYTES = 1000;
    private static final int NUMBER_OF_PACKETS_TO_SEND = 1;
    public static final long  TASK_RAM = 100;
    
    private List<NetworkVm> vmList;
    private List<NetworkCloudlet> cloudletList;
    private NetworkDatacenter datacenter;
    private NetDatacenterBroker broker;
    
    private int currentNetworkCloudletId = -1;
   
    /**
     * Creates, starts, stops the simulation and shows results.
     */
    public NetworkVmsExample1() {
        Log.printFormattedLine("Starting %s...", this.getClass().getSimpleName());
        try {
            int num_user = 1; // number of cloud users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false; 

            CloudSim.init(num_user, calendar, trace_flag);

            this.datacenter = createDatacenter("Datacenter_0");
            this.broker = new NetDatacenterBroker("Broker_0");
            this.vmList = new ArrayList<>();
                   
            this.vmList.addAll(createAndSubmitVMs(broker));
            this.cloudletList = createNetworkCloudlets(broker);
            broker.submitCloudletList(this.cloudletList);

            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            showSimulationResults();
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unexpected errors happened");
        }
    }

    private void showSimulationResults() {
        List<Cloudlet> newList = broker.getCloudletsFinishedList();
        CloudletsTableBuilderHelper.print(new TextTableBuilder(), newList);
        
        for(NetworkHost host: datacenter.<NetworkHost>getHostList()){
            Log.printFormatted("\nHost %d data transfered: %d bytes",
                    host.getId(), host.getTotalDataTransferBytes());
        }
        
        Log.printFormattedLine("\n\n%s finished!", this.getClass().getSimpleName());
    }

    /**
     * Creates the datacenter.
     *
     * @param name the datacenter name
     *
     * @return the datacenter
     */
    private NetworkDatacenter createDatacenter(String name) {
        List<Host> hostList = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_HOSTS; i++) {
            List<Pe> peList = createPEs(HOST_PES, HOST_MIPS);
            // 4. Create Host with its id and list of PEs and add them to
            // the list of machines
            hostList.add(
                new NetworkHost(i,
                    new ResourceProvisionerSimple(new Ram(HOST_RAM)),
                    new ResourceProvisionerSimple(new Bandwidth(HOST_BW)),
                    HOST_STORAGE, peList,
                    new VmSchedulerTimeShared(peList)));
        }

        // 5. Create a DatacenterCharacteristics object that stores the
        // properties of a data center: architecture, OS, list of
        // Machines, allocation policy: time- or space-shared, time zone
        // and its price (G$/Pe time unit).
        LinkedList<FileStorage> storageList = new LinkedList<>();
        DatacenterCharacteristics characteristics =
                new DatacenterCharacteristicsSimple(
                        ARCH, OS, VMM, hostList, TIME_ZONE, COST,
                        COST_PER_MEM, COST_PER_STORAGE, COST_PER_BW);
        // 6. Finally, we need to create a NetworkDatacenter object.
        NetworkDatacenter newDatacenter =
                new NetworkDatacenter(
                        name, characteristics,
                        new NetworkVmAllocationPolicy(hostList),
                        storageList, 5);
        
        createNetwork(newDatacenter);
        return newDatacenter;
    }

    private List<Pe> createPEs(final int numberOfPEs, final int mips) {
        // 2. A Machine contains one or more PEs or CPUs/Cores.
        // In this example, it will have only one core.
        // 3. Create PEs and add these into an object of PowerPeList.
        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < numberOfPEs; i++) {
            peList.add(new PeSimple(i, new PeProvisionerSimple(mips)));
        }
        return peList;
    }
    
    /**
     * Creates internal Datacenter network.
     * @param datacenter datacenter where the network will be created
     */
    private void createNetwork(NetworkDatacenter datacenter) {
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
     * Creates a list of virtual machines in a datacenter for a given broker
     * and submit the list to the broker.
     *
     * @param broker The broker that will own the created VMs
     * @return the list of created VMs
     */
    private List<NetworkVm> createAndSubmitVMs(NetDatacenterBroker broker) {
        final List<NetworkVm> list = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_HOSTS; i++) {
            NetworkVm vm =
                new NetworkVm(i,
                    broker.getId(), HOST_MIPS, HOST_PES, HOST_RAM, HOST_BW, HOST_STORAGE, VMM,
                    new NetworkCloudletSpaceSharedScheduler(datacenter));
            list.add(vm);
        }
        
        broker.submitVmList(list);
        return list;
    }

    /**
     * Creates a list of {@link NetworkCloudlet} that together represents the distributed
     * processes of a given {@link AppCloudlet}.
     * 
     * @param broker broker to associate the NetworkCloudlets
     * @return the list of create NetworkCloudlets
     */
    private List<NetworkCloudlet> createNetworkCloudlets(NetDatacenterBroker broker) {
        NetworkCloudlet networkCloudletList[] = new NetworkCloudlet[2];

        for(int i = 0; i < networkCloudletList.length; i++){
            networkCloudletList[i] = 
                    createNetworkCloudlet(vmList.get(i), broker);
        }

        //NetworkCloudlet 0 Tasks
        addExecutionTask(networkCloudletList[0]);
        addSendTask(networkCloudletList[0], networkCloudletList[1]);

        //NetworkCloudlet 1 Tasks
        addReceiveTask(networkCloudletList[1], networkCloudletList[0]);
        //addExecutionTask(networkCloudletList[1]);

        return Arrays.asList(networkCloudletList);
    }

    /**
     * Creates a {@link NetworkCloudlet}.
     *
     * @param vm the VM that will run the created {@link NetworkCloudlet)
     * @param broker the broker that will own the create NetworkCloudlet
     * @return
     */
    private NetworkCloudlet createNetworkCloudlet(NetworkVm vm, NetDatacenterBroker broker) {
        UtilizationModel utilizationModel = new UtilizationModelFull();
        NetworkCloudlet netCloudlet = new NetworkCloudlet(
                ++currentNetworkCloudletId, 1, HOST_PES,
                NETCLOUDLET_FILE_SIZE, NETCLOUDLET_OUTPUT_SIZE, TASK_RAM,
                utilizationModel, utilizationModel, utilizationModel);
        netCloudlet.setUserId(broker.getId());
        netCloudlet.setVmId(vm.getId());

        return netCloudlet;
    }

    /**
     * Adds a send task to list of tasks of the given {@link NetworkCloudlet}.
     *
     * @param sourceCloudlet the {@link NetworkCloudlet} to add the task to
     * @param destinationCloudlet the destination where to send or from which is
     * expected to receive data
     */
    private void addSendTask(
            NetworkCloudlet sourceCloudlet,
            NetworkCloudlet destinationCloudlet) {

        CloudletSendTask task = new CloudletSendTask(sourceCloudlet.getTasks().size());
        task.setMemory(TASK_RAM);
        sourceCloudlet.addTask(task);
        for(int i = 0; i < NUMBER_OF_PACKETS_TO_SEND; i++) {
            task.addPacket(destinationCloudlet, PACKET_DATA_LENGTH_IN_BYTES);
        }
    }

    /**
     * Adds a receive task to list of tasks of the given {@link NetworkCloudlet}.
     *
     * @param cloudlet the {@link NetworkCloudlet} that the task will belong to
     * @param sourceCloudlet the cloudlet where it is expected to receive packets from
     */
    private void addReceiveTask(NetworkCloudlet cloudlet, NetworkCloudlet sourceCloudlet) {
        CloudletReceiveTask task = new CloudletReceiveTask(
                cloudlet.getTasks().size(), sourceCloudlet.getVmId());
        task.setMemory(TASK_RAM);
        task.setNumberOfExpectedPacketsToReceive(NUMBER_OF_PACKETS_TO_SEND);
        cloudlet.addTask(task);
    }

    /**
     * Adds an execution task to list of tasks of the given {@link NetworkCloudlet}.
     *
     * @param netCloudlet the {@link NetworkCloudlet} to add the task
     */
    private static void addExecutionTask(NetworkCloudlet netCloudlet) {
        CloudletTask task = new CloudletExecutionTask(
                netCloudlet.getTasks().size(), NETCLOUDLET_EXECUTION_TASK_LENGTH);
        task.setMemory(TASK_RAM);
        netCloudlet.addTask(task);
    }

    /**
     * Starts the execution of the example.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new NetworkVmsExample1();
    }

    
}
