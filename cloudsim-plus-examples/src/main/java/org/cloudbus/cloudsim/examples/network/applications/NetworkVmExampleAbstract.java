package org.cloudbus.cloudsim.examples.network.applications;

import java.util.*;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.lists.VmList;
import org.cloudbus.cloudsim.network.switches.AggregateSwitch;
import org.cloudbus.cloudsim.network.switches.EdgeSwitch;
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter;
import org.cloudbus.cloudsim.hosts.network.NetworkHost;
import org.cloudbus.cloudsim.vms.network.NetworkVm;
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet;
import org.cloudbus.cloudsim.network.switches.RootSwitch;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.TextTableBuilder;

/**
 * A base class for network simulation examples
 * using objects such as{@link NetworkDatacenter},
 * {@link NetworkHost}, {@link NetworkVm} and {@link NetworkCloudlet}.
 *
 * The class simulate applications that are compounded by a list of
 * {@link NetworkCloudlet}.
 *
 * @author Saurabh Kumar Garg
 * @author Rajkumar Buyya
 * @author Manoel Campos da Silva Filho
 */
abstract class NetworkVmExampleAbstract {
    public static final int MAX_VMS_PER_HOST = 2;

    public static final double COST = 3.0; // the cost of using processing in this resource
    public static final double COST_PER_MEM = 0.05; // the cost of using memory in this resource
    public static final double COST_PER_STORAGE = 0.001; // the cost of using storage in this resource
    public static final double COST_PER_BW = 0.0; // the cost of using bw in this resource

    public static final int  HOST_MIPS = 1000;
    public static final int  HOST_PES = 8;
    public static final int  HOST_RAM = 2048; // host memory (MEGABYTE)
    public static final long HOST_STORAGE = 1000000; // host storage
    public static final long HOST_BW = 10000;

    public static final int  VM_MIPS = 1000;
    public static final long VM_SIZE = 10000; // image size (MEGABYTE)
    public static final int  VM_RAM = 512; // vm memory (MEGABYTE)
    public static final long VM_BW = 1000;
    public static final int  VM_PES_NUMBER = HOST_PES / MAX_VMS_PER_HOST;

    /**
     * Number of fictitious applications to create.
     * Each application is just a list of {@link  NetworkCloudlet}.
     * @see #appMap
     */
    public static final int NUMBER_OF_APPS = 1;

    public static final int  NETCLOUDLET_PES_NUMBER = VM_PES_NUMBER;
    public static final int  NETCLOUDLET_EXECUTION_TASK_LENGTH = 4000;
    public static final int  NETCLOUDLET_FILE_SIZE = 300;
    public static final int  NETCLOUDLET_OUTPUT_SIZE = 300;
    public static final long NETCLOUDLET_RAM = 100;

    private final CloudSim simulation;

    /**
     * @see #getVmList()
     */
    private List<NetworkVm> vmList;
    private NetworkDatacenter datacenter;
    private List<DatacenterBroker> brokerList;

    /**
     * A Map representing a list of cloudlets from different applications.
     * Each key represents the ID of a fictitious that is composed of a list
     * of {@link  NetworkCloudlet}.
     */
    private Map<Integer, List<NetworkCloudlet>> appMap;

    /**
     * Creates, starts, stops the simulation and shows results.
     */
    NetworkVmExampleAbstract() {
        Log.printFormattedLine("Starting %s...", this.getClass().getSimpleName());
        simulation = new CloudSim();

        this.datacenter = createDatacenter();
        this.brokerList = createBrokerForEachApp();
        this.vmList = new ArrayList<>();
        this.appMap = new HashMap<>();

        int appId = -1;
        for(DatacenterBroker broker: this.brokerList){
            this.vmList.addAll(createAndSubmitVMs(broker));
            this.appMap.put(++appId, createAppAndSubmitToBroker(broker));
        }

        simulation.start();

        showSimulationResults();
    }

    private void showSimulationResults() {
        DatacenterBroker broker;
        for(int i = 0; i < NUMBER_OF_APPS; i++){
            broker = brokerList.get(i);
            List<Cloudlet> newList = broker.getCloudletFinishedList();
            String caption = broker.getName() + " - Application " + broker.getId();
            new CloudletsTableBuilder(newList)
                    .setTable(new TextTableBuilder(caption))
                    .build();
            Log.printFormattedLine(
                "Number of NetworkCloudlets for Application %s: %d", broker.getId(), newList.size());
        }

        for(NetworkHost host: datacenter.<NetworkHost>getHostList()){
            Log.printFormatted("\nHost %d data transfered: %d bytes",
                    host.getId(), host.getTotalDataTransferBytes());
        }

        Log.printFormattedLine("\n\n%s finished!", this.getClass().getSimpleName());
    }

    /**
     * Create a {@link DatacenterBroker} for each each list of {@link NetworkCloudlet}
     * that represents cloudlets that compose the same application.
     *
     * @return the list of created NetworkDatacenterBroker
     */
    private  List<DatacenterBroker> createBrokerForEachApp() {
        List<DatacenterBroker> list = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_APPS; i++){
            list.add(new DatacenterBrokerSimple(simulation));
        }

        return list;
    }

    /**
     * Creates the Datacenter.
     *
     * @return the Datacenter
     */
    protected final NetworkDatacenter createDatacenter() {
        final int numberOfHosts = EdgeSwitch.PORTS * AggregateSwitch.PORTS * RootSwitch.PORTS;
        List<Host> hostList = new ArrayList<>(numberOfHosts);
        for (int i = 0; i < numberOfHosts; i++) {
            List<Pe> peList = createPEs(HOST_PES, HOST_MIPS);
            Host host = new NetworkHost(HOST_RAM, HOST_BW, HOST_STORAGE, peList)
                    .setRamProvisioner(new ResourceProvisionerSimple())
                    .setBwProvisioner(new ResourceProvisionerSimple())
                    .setVmScheduler(new VmSchedulerTimeShared());
            hostList.add(host);
        }

        // 5. Create a DatacenterCharacteristics object that stores the
        // properties of a data center: architecture, OS, list of
        // Machines, allocation policy: time- or space-shared, time zone
        // and its price (G$/Pe time unit).
        DatacenterCharacteristics characteristics =
                new DatacenterCharacteristicsSimple(hostList)
                    .setCostPerSecond(COST)
                    .setCostPerMem(COST_PER_MEM)
                    .setCostPerStorage(COST_PER_STORAGE)
                    .setCostPerBw(COST_PER_BW);
        // 6. Finally, we need to create a NetworkDatacenter object.
        NetworkDatacenter newDatacenter =
                new NetworkDatacenter(
                        simulation, characteristics, new VmAllocationPolicySimple());
        newDatacenter.setSchedulingInterval(5);

        createNetwork(newDatacenter);
        return newDatacenter;
    }

    public List<Pe> createPEs(final int numberOfPEs, final long mips) {
        // 2. A Machine contains one or more PEs or CPUs/Cores.
        // In this example, it will have only one core.
        // 3. Create PEs and add these into an object of PowerPeList.
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
    protected void createNetwork(NetworkDatacenter datacenter) {
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
    protected final List<NetworkVm> createAndSubmitVMs(DatacenterBroker broker) {
        final int numberOfVms = getDatacenterHostList().size() * MAX_VMS_PER_HOST;
        final List<NetworkVm> list = new ArrayList<>();
        for (int i = 0; i < numberOfVms; i++) {
            NetworkVm vm = new NetworkVm(i,VM_MIPS, VM_PES_NUMBER);
            vm.setRam(VM_RAM)
              .setBw(VM_BW)
              .setSize(VM_SIZE)
              .setCloudletScheduler(new CloudletSchedulerTimeShared());
            list.add(vm);
        }

        broker.submitVmList(list);
        return list;
    }

    private List<Host> getDatacenterHostList() {
        return datacenter.getCharacteristics().getHostList();
    }

    /**
     * Randomly select a given number of VMs from the list of created VMs,
     * to be used by the NetworkCloudlets of the given application.
     *
     * @param broker the broker where to get the existing VM list
     * @param numberOfVmsToSelect number of VMs to selected from the existing list of VMs.
     * @return The list of randomly selected VMs
     */
    protected List<NetworkVm> randomlySelectVmsForApp(
        DatacenterBroker broker, int numberOfVmsToSelect) {
        List<NetworkVm> list = new ArrayList<>();
        int numOfExistingVms = this.vmList.size();
        UniformDistr rand = new UniformDistr(0, numOfExistingVms, 5);
        for (int i = 0; i < numberOfVmsToSelect; i++) {
            int vmId = (int) rand.sample();
            NetworkVm vm = VmList.getById(this.vmList, vmId);
            if(vm != Vm.NULL){
                list.add(vm);
            }
        }
        return list;
    }

    /**
     * @return List of VMs of all Brokers.
     */
    public List<NetworkVm> getVmList() {
        return vmList;
    }

    public NetworkDatacenter getDatacenter() {
        return datacenter;
    }

    public List<DatacenterBroker> getBrokerList() {
        return brokerList;
    }

    /**
     * Creates a list of {@link NetworkCloudlet}'s that represents
     * a single application and then submit the created cloudlets to a given Broker.
     *
     * @param broker the broker to submit the list of NetworkCloudlets of the application
     * @return the list of created  {@link NetworkCloudlet}'s
     */
    protected final List<NetworkCloudlet> createAppAndSubmitToBroker(DatacenterBroker broker) {
        List<NetworkCloudlet> list = createNetworkCloudlets(broker);
        broker.submitCloudletList(list);
        return list;
    }

    /**
     * Creates a list of {@link NetworkCloudlet} that together represents the distributed
     * processes of a given application.
     *
     * @param broker broker to associate the NetworkCloudlets
     * @return the list of create NetworkCloudlets
     */
    protected abstract List<NetworkCloudlet> createNetworkCloudlets(DatacenterBroker broker);

}
