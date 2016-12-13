package org.cloudbus.cloudsim.examples.network.datacenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.brokers.network.NetworkDatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.lists.VmList;
import org.cloudbus.cloudsim.network.switches.AggregateSwitch;
import org.cloudbus.cloudsim.cloudlets.network.AppCloudlet;
import org.cloudbus.cloudsim.network.switches.EdgeSwitch;
import org.cloudbus.cloudsim.schedulers.cloudlet.network.NetworkCloudletSpaceSharedScheduler;
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter;
import org.cloudbus.cloudsim.hosts.network.NetworkHost;
import org.cloudbus.cloudsim.vms.network.NetworkVm;
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet;
import org.cloudbus.cloudsim.network.switches.RootSwitch;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.util.tablebuilder.CloudletsTableBuilderHelper;
import org.cloudsimplus.util.tablebuilder.TextTableBuilder;

/**
 * A base class for network simulation examples
 * using objects such as{@link NetworkDatacenter},
 * {@link NetworkHost}, {@link NetworkVm}
 * {@link AppCloudlet} and {@link NetworkCloudlet}.
 *
 * It provides some utilities methods to create
 * these simulation objects.
 *
 * @author Saurabh Kumar Garg
 * @author Rajkumar Buyya
 * @author Manoel Campos da Silva Filho
 */
public abstract class NetworkVmsExampleAppCloudletAbstract {
    public static final int MAX_VMS_PER_HOST = 2;

    public static final double COST = 3.0; // the cost of using processing in this resource
    public static final double COST_PER_MEM = 0.05; // the cost of using memory in this resource
    public static final double COST_PER_STORAGE = 0.001; // the cost of using storage in this resource
    public static final double COST_PER_BW = 0.0; // the cost of using bw in this resource

    public static final int  HOST_MIPS = 1000;
    public static final int  HOST_PES = 8;
    public static final int  HOST_RAM = 2048; // host memory (MB)
    public static final long HOST_STORAGE = 1000000; // host storage
    public static final long HOST_BW = 10000;

    public static final int  VM_MIPS = 1000;
    public static final long VM_SIZE = 10000; // image size (MB)
    public static final int  VM_RAM = 512; // vm memory (MB)
    public static final long VM_BW = 1000;
    public static final int  VM_PES_NUMBER = HOST_PES / MAX_VMS_PER_HOST;

    public static final int  NUMBER_OF_APP_CLOUDLETS = 1;

    public static final int  NETCLOUDLET_PES_NUMBER = VM_PES_NUMBER;
    public static final int  NETCLOUDLET_EXECUTION_TASK_LENGTH = 4000;
    public static final int  NETCLOUDLET_FILE_SIZE = 300;
    public static final int  NETCLOUDLET_OUTPUT_SIZE = 300;
    public static final long NETCLOUDLET_RAM = 100;
    private final CloudSim simulation;

    private int currentAppCloudletId = -1;

    /**
     * @see #getVmList()
     */
    private List<NetworkVm> vmList;
    private NetworkDatacenter datacenter;
    private List<NetworkDatacenterBroker> brokerList;
    private List<AppCloudlet> appCloudletList;

    /**
     * Creates, starts, stops the simulation and shows results.
     */
    public NetworkVmsExampleAppCloudletAbstract() {
        Log.printFormattedLine("Starting %s...", this.getClass().getSimpleName());
        int num_user = 1; // number of cloud users
        Calendar calendar = Calendar.getInstance();
        boolean trace_flag = false;

        simulation = new CloudSim(trace_flag);

        this.datacenter = createDatacenter();
        this.brokerList = createBrokerForEachAppCloudlet();
        this.appCloudletList = new ArrayList<>();
        this.vmList = new ArrayList<>();

        AppCloudlet app;
        for(NetworkDatacenterBroker broker: this.brokerList){
            this.vmList.addAll(createAndSubmitVMs(broker));
            app = createAppCloudletAndSubmitToBroker(broker);
            this.appCloudletList.add(app);
        }

        simulation.start();

        showSimulationResults();
    }

    private void showSimulationResults() {
        AppCloudlet app;
        NetworkDatacenterBroker broker;
        for(int i = 0; i < NUMBER_OF_APP_CLOUDLETS; i++){
            broker = brokerList.get(i);
            app = appCloudletList.get(i);
            List<Cloudlet> newList = broker.getCloudletsFinishedList();
            String caption = broker.getName() + " - AppCloudlet " + app.getId();
            new CloudletsTableBuilderHelper(newList)
                    .setPrinter(new TextTableBuilder(caption))
                    .build();
            Log.printFormattedLine(
                "Number of NetworkCloudlets for AppCloudlet %s: %d", app.getId(), newList.size());
        }

        for(NetworkHost host: datacenter.<NetworkHost>getHostList()){
            Log.printFormatted("\nHost %d data transfered: %d bytes",
                    host.getId(), host.getTotalDataTransferBytes());
        }

        Log.printFormattedLine("\n\n%s finished!", this.getClass().getSimpleName());
    }

    /**
     * Create a {@link NetworkDatacenterBroker} for each {@link AppCloudlet}.
     *
     * @return the list of created NetworkDatacenterBroker
     */
    private  List<NetworkDatacenterBroker> createBrokerForEachAppCloudlet() {
        List<NetworkDatacenterBroker> list = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_APP_CLOUDLETS; i++){
            list.add(new NetworkDatacenterBroker(simulation));
        }

        return list;
    }

    /**
     * Creates the switches.
     *
     * @return the switches
     */
    protected final NetworkDatacenter createDatacenter() {
        final int numberOfHosts = EdgeSwitch.PORTS * AggregateSwitch.PORTS * RootSwitch.PORTS;
        List<Host> hostList = new ArrayList<>(numberOfHosts);
        for (int i = 0; i < numberOfHosts; i++) {
            List<Pe> peList = createPEs(HOST_PES, HOST_MIPS);
            Host host = new NetworkHost(i, HOST_STORAGE, peList)
                    .setRamProvisioner(new ResourceProvisionerSimple(new Ram(HOST_RAM)))
                    .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(HOST_BW)))
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

    public List<Pe> createPEs(final int numberOfPEs, final int mips) {
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
     * @param datacenter switches where the network will be created
     */
    protected void createNetwork(NetworkDatacenter datacenter) {
        EdgeSwitch[] edgeSwitches = new EdgeSwitch[1];
        for (int i = 0; i < edgeSwitches.length; i++) {
            edgeSwitches[i] = new EdgeSwitch(simulation, datacenter);
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
     * Creates a list of virtual machines in a switches for a given broker
     * and submit the list to the broker.
     *
     * @param broker The broker that will own the created VMs
     * @return the list of created VMs
     */
    protected final List<NetworkVm> createAndSubmitVMs(NetworkDatacenterBroker broker) {
        final int numberOfVms = getDatacenterHostList().size() * MAX_VMS_PER_HOST;
        final List<NetworkVm> list = new ArrayList<>();
        for (int i = 0; i < numberOfVms; i++) {
            NetworkVm vm = new NetworkVm(i,VM_MIPS, VM_PES_NUMBER);
            vm.setRam(VM_RAM)
              .setBw(VM_BW)
              .setSize(VM_SIZE)
              .setBroker(broker)
              .setCloudletScheduler(new NetworkCloudletSpaceSharedScheduler(datacenter));
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
     * to be used by the NetworkCloudlets of the given AppCloudlet.
     *
     * @param broker the broker where to get the existing VM list
     * @param numberOfVmsToSelect number of VMs to selected from the existing list of VMs.
     * @return The list of randomly selected VMs
     */
    protected List<NetworkVm> randomlySelectVmsForAppCloudlet(
        NetworkDatacenterBroker broker, int numberOfVmsToSelect) {
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

    public List<NetworkDatacenterBroker> getBrokerList() {
        return brokerList;
    }

    /**
     * Creates an {@link AppCloudlet} with a list of {@link NetworkCloudlet}'s
     * and submit its NetworkCloudlets to a given Broker.
     *
     * @param broker the broker to submit the list of NetworkCloudlets of the
     * AppCloudlet.
     * @return the created AppCloudlet
     */
    protected final AppCloudlet createAppCloudletAndSubmitToBroker(NetworkDatacenterBroker broker) {
        AppCloudlet app = new AppCloudlet(++currentAppCloudletId);
        app.setNetworkCloudletList(createNetworkCloudlets(app, broker));
        broker.submitCloudletList(app.getNetworkCloudletList());
        return app;
    }

    /**
     * Creates a list of {@link NetworkCloudlet} that together represents the distributed
     * processes of a given {@link AppCloudlet}.
     *
     * @param app The AppCloudlet that the created NetworkCloudlets will belong to.
     * @param broker broker to associate the NetworkCloudlets
     * @return the list of create NetworkCloudlets
     */
    protected abstract List<NetworkCloudlet> createNetworkCloudlets(AppCloudlet app, NetworkDatacenterBroker broker);

}
