package org.cloudbus.cloudsim.examples.network.datacenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.lists.VmList;
import org.cloudbus.cloudsim.network.datacenter.AggregateSwitch;
import org.cloudbus.cloudsim.network.datacenter.AppCloudlet;
import org.cloudbus.cloudsim.network.datacenter.EdgeSwitch;
import org.cloudbus.cloudsim.network.datacenter.NetDatacenterBroker;
import org.cloudbus.cloudsim.network.datacenter.NetworkCloudletSpaceSharedScheduler;
import org.cloudbus.cloudsim.network.datacenter.NetworkDatacenter;
import org.cloudbus.cloudsim.network.datacenter.NetworkHost;
import org.cloudbus.cloudsim.network.datacenter.NetworkVm;
import org.cloudbus.cloudsim.network.datacenter.NetworkCloudlet;
import org.cloudbus.cloudsim.network.datacenter.NetworkVmAllocationPolicy;
import org.cloudbus.cloudsim.network.datacenter.RootSwitch;
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
    public static final String ARCH = "x86"; // system architecture
    public static final String OS = "Linux"; // operating system
    public static final String VMM = "Xen";
    public static final double TIME_ZONE = 10.0; // time zone this resource located
    public static final int    MAX_VMS_PER_HOST = 2;

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
    
    private int currentAppCloudletId = -1;   

    /**
     * @see #getVmList() 
     */
    private List<NetworkVm> vmList;
    private NetworkDatacenter datacenter;
    private List<NetDatacenterBroker> brokerList;
    private List<AppCloudlet> appCloudletList;
   
    /**
     * Creates, starts, stops the simulation and shows results.
     */
    public NetworkVmsExampleAppCloudletAbstract() {
        Log.printFormattedLine("Starting %s...", this.getClass().getSimpleName());
        try {
            int num_user = 1; // number of cloud users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;

            CloudSim.init(num_user, calendar, trace_flag);

            this.datacenter = createDatacenter("Datacenter_0");
            this.brokerList = createBrokerForEachAppCloudlet();
            this.appCloudletList = new ArrayList<>();
            this.vmList = new ArrayList<>();
                   
            AppCloudlet app;
            for(NetDatacenterBroker broker: this.brokerList){
                this.vmList.addAll(createAndSubmitVMs(broker));
                app = createAppCloudletAndSubmitToBroker(broker);
                this.appCloudletList.add(app);    
            }

            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            showSimulationResults();
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unexpected errors happened");
        }
    }

    private void showSimulationResults() {
        AppCloudlet app;
        NetDatacenterBroker broker;
        for(int i = 0; i < NUMBER_OF_APP_CLOUDLETS; i++){
            broker = brokerList.get(i);
            app = appCloudletList.get(i);
            List<Cloudlet> newList = broker.getCloudletsFinishedList();
            String caption = broker.getName() + " - AppCloudlet " + app.getId();
            CloudletsTableBuilderHelper.print(new TextTableBuilder(caption), newList);
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
     * Create a {@link NetDatacenterBroker} for each {@link AppCloudlet}.
     * 
     * @return the list of created NetDatacenterBroker
     */
    private  List<NetDatacenterBroker> createBrokerForEachAppCloudlet() {
        List<NetDatacenterBroker> list = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_APP_CLOUDLETS; i++){
            list.add(new NetDatacenterBroker("Broker_"+i));
        }
        
        return list;
    }

    /**
     * Creates the datacenter.
     *
     * @param name the datacenter name
     *
     * @return the datacenter
     */
    protected final NetworkDatacenter createDatacenter(String name) {
        List<Host> hostList = new ArrayList<>();
        final int numberOfHosts = EdgeSwitch.PORTS * AggregateSwitch.PORTS * RootSwitch.PORTS;
        for (int i = 0; i < numberOfHosts; i++) {
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
     * Creates a list of virtual machines in a datacenter for a given broker
     * and submit the list to the broker.
     *
     * @param broker The broker that will own the created VMs
     * @return the list of created VMs
     */
    protected final List<NetworkVm> createAndSubmitVMs(NetDatacenterBroker broker) {
        final int numberOfVms = getDatacenterHostList().size() * MAX_VMS_PER_HOST;
        final List<NetworkVm> list = new ArrayList<>();
        for (int i = 0; i < numberOfVms; i++) {
            NetworkVm vm =
                new NetworkVm(i,
                    broker.getId(), VM_MIPS, VM_PES_NUMBER, VM_RAM, VM_BW, VM_SIZE, VMM,
                    new NetworkCloudletSpaceSharedScheduler(datacenter));
            list.add(vm);
        }
        
        broker.submitVmList(list);
        return list;
    }

    private List<Host> getDatacenterHostList() {
        return datacenter.getCharacteristics().getHostList();
    }

    /**
     * Randomly select the number of VMs defined by
     * {@link #NUMBER_OF_NETCLOUDLET_FOR_EACH_APPCLOUDLET}, from the list
     * of created VMs, to be used by the NetworkCloudlets of the given AppCloudlet.
     *
     * @param broker the broker where to get the existing VM list
     * @param numberOfVmsToSelect number of VMs to selected from the existing
     * list of VMs.
     * @return The list of randomly selected VMs
     */
    protected List<NetworkVm> randomlySelectVmsForAppCloudlet(
            NetDatacenterBroker broker, int numberOfVmsToSelect) {
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

    public List<NetDatacenterBroker> getBrokerList() {
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
    protected final AppCloudlet createAppCloudletAndSubmitToBroker(NetDatacenterBroker broker) {
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
    protected abstract List<NetworkCloudlet> createNetworkCloudlets(AppCloudlet app, NetDatacenterBroker broker);

}
