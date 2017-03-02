package org.cloudbus.cloudsim.examples.network.applications;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.cloudlets.network.CloudletExecutionTask;
import org.cloudbus.cloudsim.cloudlets.network.CloudletReceiveTask;
import org.cloudbus.cloudsim.cloudlets.network.CloudletSendTask;
import org.cloudbus.cloudsim.cloudlets.network.CloudletTask;
import org.cloudbus.cloudsim.network.switches.EdgeSwitch;
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter;
import org.cloudbus.cloudsim.hosts.network.NetworkHost;
import org.cloudbus.cloudsim.vms.network.NetworkVm;
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
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
    private static final double COST = 3.0; // the cost of using processing in this resource
    private static final double COST_PER_MEM = 0.05; // the cost of using memory in this resource
    private static final double COST_PER_STORAGE = 0.001; // the cost of using storage in this resource
    private static final double COST_PER_BW = 0.0; // the cost of using bw in this resource

    private static final int  NUMBER_OF_HOSTS = 2;
    private static final int  HOST_MIPS = 1000;
    private static final int  HOST_PES = 4;
    private static final int  HOST_RAM = 2048; // host memory (MEGABYTE)
    private static final long HOST_STORAGE = 1000000; // host storage
    private static final long HOST_BW = 10000;

    private static final int  CLOUDLET_EXECUTION_TASK_LENGTH = 4000;
    private static final int  CLOUDLET_FILE_SIZE = 300;
    private static final int  CLOUDLET_OUTPUT_SIZE = 300;
    private static final long PACKET_DATA_LENGTH_IN_BYTES = 1000;
    private static final int  NUMBER_OF_PACKETS_TO_SEND = 1;
    private static final long TASK_RAM = 100;

    private final CloudSim simulation;

    private List<NetworkVm> vmList;
    private List<NetworkCloudlet> cloudletList;
    private NetworkDatacenter datacenter;
    private DatacenterBroker broker;

    private int currentNetworkCloudletId = -1;

    /**
     * Starts the execution of the example.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new NetworkVmsExample1();
    }

    /**
     * Creates, starts, stops the simulation and shows results.
     */
    public NetworkVmsExample1() {
        Log.printFormattedLine("Starting %s...", getClass().getSimpleName());
        simulation = new CloudSim();

        datacenter = createDatacenter();
        broker = new DatacenterBrokerSimple(simulation);
        vmList = createAndSubmitVMs(broker);
        cloudletList = createNetworkCloudlets(broker);
        broker.submitCloudletList(cloudletList);

        simulation.start();

        showSimulationResults();
    }

    private void showSimulationResults() {
        List<Cloudlet> newList = broker.getCloudletsFinishedList();
        new CloudletsTableBuilder(newList).build();

        for(NetworkHost host: datacenter.<NetworkHost>getHostList()){
            Log.printFormatted("\nHost %d data transferred: %d bytes",
                    host.getId(), host.getTotalDataTransferBytes());
        }

        Log.printFormattedLine("\n\n%s finished!", getClass().getSimpleName());
    }

    /**
     * Creates the Datacenter.
     *
     * @return the Datacenter
     */
    private NetworkDatacenter createDatacenter() {
        List<Host> hostList = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_HOSTS; i++) {
            Host host = createHost(i);
            hostList.add(host);
        }

        List<FileStorage> storageList = new ArrayList<>();
        DatacenterCharacteristics characteristics =
                new DatacenterCharacteristicsSimple(hostList)
                    .setCostPerSecond(COST)
                    .setCostPerMem(COST_PER_MEM)
                    .setCostPerStorage(COST_PER_STORAGE)
                    .setCostPerBw(COST_PER_BW);

        NetworkDatacenter newDatacenter =
            new NetworkDatacenter(simulation, characteristics, new VmAllocationPolicySimple());
        newDatacenter.setSchedulingInterval(5);

        createNetwork(newDatacenter);
        return newDatacenter;
    }

    private Host createHost(int id) {
        List<Pe> peList = createPEs(HOST_PES, HOST_MIPS);
        return new NetworkHost(id, HOST_STORAGE, peList)
            .setRamProvisioner(new ResourceProvisionerSimple(new Ram(HOST_RAM)))
            .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(HOST_BW)))
            .setVmScheduler(new VmSchedulerTimeShared());
    }

    private List<Pe> createPEs(final int numberOfPEs, final long mips) {
        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < numberOfPEs; i++) {
            peList.add(new PeSimple(mips, new PeProvisionerSimple()));
        }
        return peList;
    }

    /**
     * Creates internal Datacenter network.
     * @param datacenter Datacenter where the network will be created
     */
    private void createNetwork(NetworkDatacenter datacenter) {
        EdgeSwitch[] edgeSwitches = new EdgeSwitch[1];
        for (int i = 0; i < edgeSwitches.length; i++) {
            edgeSwitches[i] = new EdgeSwitch(simulation, datacenter);
            datacenter.addSwitch(edgeSwitches[i]);
        }

        for (NetworkHost host : datacenter.<NetworkHost>getHostList()) {
            int switchNum = host.getId() / edgeSwitches[0].getPorts();
            edgeSwitches[switchNum].connectHost(host);
            host.setEdgeSwitch(edgeSwitches[switchNum]);
        }
    }

    /**
     * Creates a list of virtual machines in a Datacenter for a given broker
     * and submit the list to the broker.
     *
     * @param broker The broker that will own the created VMs
     * @return the list of created VMs
     */
    private List<NetworkVm> createAndSubmitVMs(DatacenterBroker broker) {
        final List<NetworkVm> list = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_HOSTS; i++) {
            NetworkVm vm = createVm(i, broker);
            list.add(vm);
        }

        broker.submitVmList(list);
        return list;
    }

    private NetworkVm createVm(int id, DatacenterBroker broker) {
        NetworkVm vm = new NetworkVm (id, HOST_MIPS, HOST_PES);
        vm.setRam(HOST_RAM).setBw(HOST_BW).setSize(HOST_STORAGE)
            .setCloudletScheduler(new CloudletSchedulerTimeShared())
            .setBroker(broker);
        return vm;
    }

    /**
     * Creates a list of {@link NetworkCloudlet} that together represents the distributed
     * processes of a given fictitious application.
     *
     * @param broker broker to associate the NetworkCloudlets
     * @return the list of create NetworkCloudlets
     */
    private List<NetworkCloudlet> createNetworkCloudlets(DatacenterBroker broker) {
        final int numberOfCloudlets = 2;
        List<NetworkCloudlet> networkCloudletList = new ArrayList<>(numberOfCloudlets);

        for(int i = 0; i < numberOfCloudlets; i++){
            networkCloudletList.add(createNetworkCloudlet(vmList.get(i), broker));
        }

        //NetworkCloudlet 0 Tasks
        addExecutionTask(networkCloudletList.get(0));
        addSendTask(networkCloudletList.get(0), networkCloudletList.get(1));

        //NetworkCloudlet 1 Tasks
        addReceiveTask(networkCloudletList.get(1), networkCloudletList.get(0));
        addExecutionTask(networkCloudletList.get(1));

        return networkCloudletList;
    }

    /**
     * Creates a {@link NetworkCloudlet}.
     *
     * @param vm the VM that will run the created {@link NetworkCloudlet)
     * @param broker the broker that will own the create NetworkCloudlet
     * @return
     */
    private NetworkCloudlet createNetworkCloudlet(NetworkVm vm, DatacenterBroker broker) {
        UtilizationModel utilizationModel = new UtilizationModelFull();
        NetworkCloudlet netCloudlet = new NetworkCloudlet(++currentNetworkCloudletId, 1, HOST_PES);
        netCloudlet
                .setMemory(TASK_RAM)
                .setFileSize(CLOUDLET_FILE_SIZE)
                .setOutputSize(CLOUDLET_OUTPUT_SIZE)
                .setUtilizationModel(utilizationModel);
        netCloudlet.setBroker(broker);
        netCloudlet.setVm(vm);

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
            NetworkCloudlet destinationCloudlet)
    {
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
                cloudlet.getTasks().size(), sourceCloudlet.getVm());
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
                netCloudlet.getTasks().size(), CLOUDLET_EXECUTION_TASK_LENGTH);
        task.setMemory(TASK_RAM);
        netCloudlet.addTask(task);
    }


}
