package org.cloudbus.cloudsim.examples.network.applications;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.network.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.network.NetworkHost;
import org.cloudbus.cloudsim.network.switches.EdgeSwitch;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.network.NetworkVm;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.cloudbus.cloudsim.examples.network.applications.NetworkVmExampleAbstract.getSwitchIndex;

/**
 * A simple example simulating a distributed application.
 * It show how 2 {@link NetworkCloudlet}'s communicate,
 * each one running inside VMs on different hosts.
 *
 * @author Manoel Campos da Silva Filho
 */
public class NetworkVmsExampleSimpleApp {
    private static final int NUMBER_OF_HOSTS = 2;
    private static final int HOST_MIPS = 1000;
    private static final int HOST_PES = 4;
    private static final int HOST_RAM = 2048; // host memory (Megabyte)
    private static final long HOST_STORAGE = 1000000; // host storage
    private static final long HOST_BW = 10000;

    private static final int CLOUDLET_EXECUTION_TASK_LENGTH = 4000;
    private static final int CLOUDLET_FILE_SIZE = 300;
    private static final int CLOUDLET_OUTPUT_SIZE = 300;
    private static final long PACKET_DATA_LENGTH_IN_BYTES = 1000;
    private static final int NUMBER_OF_PACKETS_TO_SEND = 1;
    private static final long TASK_RAM = 100;

    private final CloudSim simulation;

    private List<NetworkVm> vmList;
    private List<NetworkCloudlet> cloudletList;
    private NetworkDatacenter datacenter;
    private DatacenterBroker broker;

    /**
     * Starts the execution of the example.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new NetworkVmsExampleSimpleApp();
    }

    /**
     * Creates, starts, stops the simulation and shows results.
     */
    private NetworkVmsExampleSimpleApp() {
        System.out.println("Starting " + getClass().getSimpleName());
        simulation = new CloudSim();

        datacenter = createDatacenter();
        broker = new DatacenterBrokerSimple(simulation);
        vmList = createAndSubmitVMs(broker);
        cloudletList = createNetworkCloudlets();
        broker.submitCloudletList(cloudletList);

        simulation.start();

        showSimulationResults();
    }

    private void showSimulationResults() {
        List<Cloudlet> newList = broker.getCloudletFinishedList();
        new CloudletsTableBuilder(newList).build();

        for (NetworkHost host : datacenter.<NetworkHost>getHostList()) {
            System.out.printf("%nHost %d data transferred: %d bytes",
                    host.getId(), host.getTotalDataTransferBytes());
        }

        System.out.println(getClass().getSimpleName() + " finished!");
    }

    /**
     * Creates the Datacenter.
     *
     * @return the Datacenter
     */
    private NetworkDatacenter createDatacenter() {
        List<Host> hostList = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }

        NetworkDatacenter newDatacenter
                = new NetworkDatacenter(simulation, hostList, new VmAllocationPolicySimple());
        newDatacenter.setSchedulingInterval(5);

        createNetwork(newDatacenter);
        return newDatacenter;
    }

    private Host createHost() {
        List<Pe> peList = createPEs(HOST_PES, HOST_MIPS);
        return new NetworkHost(HOST_RAM, HOST_BW, HOST_STORAGE, peList)
                .setRamProvisioner(new ResourceProvisionerSimple())
                .setBwProvisioner(new ResourceProvisionerSimple())
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
     *
     * @param datacenter Datacenter where the network will be created
     */
    private void createNetwork(NetworkDatacenter datacenter) {
        EdgeSwitch[] edgeSwitches = new EdgeSwitch[1];
        for (int i = 0; i < edgeSwitches.length; i++) {
            edgeSwitches[i] = new EdgeSwitch(simulation, datacenter);
            datacenter.addSwitch(edgeSwitches[i]);
        }

        for (NetworkHost host : datacenter.<NetworkHost>getHostList()) {
            final int switchNum = getSwitchIndex(host, edgeSwitches[0].getPorts());
            edgeSwitches[switchNum].connectHost(host);
        }
    }

    /**
     * Creates a list of virtual machines in a Datacenter for a given broker and
     * submit the list to the broker.
     *
     * @param broker The broker that will own the created VMs
     * @return the list of created VMs
     */
    private List<NetworkVm> createAndSubmitVMs(DatacenterBroker broker) {
        final List<NetworkVm> list = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_HOSTS; i++) {
            NetworkVm vm = createVm(i);
            list.add(vm);
        }

        broker.submitVmList(list);
        return list;
    }

    private NetworkVm createVm(int id) {
        NetworkVm vm = new NetworkVm(id, HOST_MIPS, HOST_PES);
        vm
                .setRam(HOST_RAM)
                .setBw(HOST_BW)
                .setSize(HOST_STORAGE)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());
        return vm;
    }

    /**
     * Creates a list of {@link NetworkCloudlet} that together represents the
     * distributed processes of a given fictitious application.
     *
     * @return the list of create NetworkCloudlets
     */
    private List<NetworkCloudlet> createNetworkCloudlets() {
        final int numberOfCloudlets = 2;
        List<NetworkCloudlet> networkCloudletList = new ArrayList<>(numberOfCloudlets);

        for (int i = 0; i < numberOfCloudlets; i++) {
            networkCloudletList.add(createNetworkCloudlet(vmList.get(i)));
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
     * @return
     */
    private NetworkCloudlet createNetworkCloudlet(NetworkVm vm) {
        NetworkCloudlet netCloudlet = new NetworkCloudlet(4000, HOST_PES);
        netCloudlet
                .setMemory(TASK_RAM)
                .setFileSize(CLOUDLET_FILE_SIZE)
                .setOutputSize(CLOUDLET_OUTPUT_SIZE)
                .setUtilizationModel(new UtilizationModelFull())
                .setVm(vm)
                .setBroker(vm.getBroker());

        return netCloudlet;
    }

    /**
     * Adds an execution task to the list of tasks of the given
     * {@link NetworkCloudlet}.
     *
     * @param cloudlet the {@link NetworkCloudlet} the task will belong to
     */
    private static void addExecutionTask(NetworkCloudlet cloudlet) {
        CloudletTask task = new CloudletExecutionTask(
            cloudlet.getTasks().size(), CLOUDLET_EXECUTION_TASK_LENGTH);
        task.setMemory(TASK_RAM);
        cloudlet.addTask(task);
    }

    /**
     * Adds a send task to the list of tasks of the given {@link NetworkCloudlet}.
     *
     * @param sourceCloudlet the {@link NetworkCloudlet} from which packets will be sent
     * @param destinationCloudlet the destination {@link NetworkCloudlet} to send packets to
     */
    private void addSendTask(
        NetworkCloudlet sourceCloudlet,
        NetworkCloudlet destinationCloudlet) {
        CloudletSendTask task = new CloudletSendTask(sourceCloudlet.getTasks().size());
        task.setMemory(TASK_RAM);
        sourceCloudlet.addTask(task);
        for (int i = 0; i < NUMBER_OF_PACKETS_TO_SEND; i++) {
            task.addPacket(destinationCloudlet, PACKET_DATA_LENGTH_IN_BYTES);
        }
    }

    /**
     * Adds a receive task to the list of tasks of the given
     * {@link NetworkCloudlet}.
     *
     * @param cloudlet the {@link NetworkCloudlet} the task will belong to
     * @param sourceCloudlet the {@link NetworkCloudlet} expected to receive packets from
     */
    private void addReceiveTask(NetworkCloudlet cloudlet, NetworkCloudlet sourceCloudlet) {
        CloudletReceiveTask task = new CloudletReceiveTask(
                cloudlet.getTasks().size(), sourceCloudlet.getVm());
        task.setMemory(TASK_RAM);
        task.setExpectedPacketsToReceive(NUMBER_OF_PACKETS_TO_SEND);
        cloudlet.addTask(task);
    }

}
