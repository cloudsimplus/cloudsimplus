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
import org.cloudbus.cloudsim.util.TableBuilderHelper;
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
    public static final int    HOST_PES = 8;
    public static final int    MAX_VMS_PER_HOST = 2;

    public static final double COST = 3.0; // the cost of using processing in this resource
    public static final double COST_PER_MEM = 0.05; // the cost of using memory in this resource
    public static final double COST_PER_STORAGE = 0.001; // the cost of using storage in this resource
    public static final double COST_PER_BW = 0.0; // the cost of using bw in this resource
    
    public static final int HOST_MIPS = 1;
    public static final int HOST_RAM = 2048; // host memory (MB)
    public static final long HOST_STORAGE = 1000000; // host storage
    public static final long HOST_BW = 10000;

    public static final int  VM_MIPS = 1;
    public static final long VM_SIZE = 10000; // image size (MB)
    public static final int  VM_RAM = 512; // vm memory (MB)
    public static final long VM_BW = 1000;
    public static final int  VM_PES_NUMBER = HOST_PES / MAX_VMS_PER_HOST;
    
    public static final int NUMBER_OF_APP_CLOUDLETS = 1;

    public static final int NETCLOUDLET_PES_NUMBER = 4;
    public static final int NETCLOUDLET_FILE_SIZE = 300;
    public static final int NETCLOUDLET_OUTPUT_SIZE = 300;
    public static final int NETCLOUDLET_RAM = 100;
    public static final int NETCLOUDLET_TASK_COMMUNICATION_LENGTH = 1;

    private List<NetworkVm> vmlist;
    private NetworkDatacenter datacenter;
    private NetDatacenterBroker broker;
    private List<AppCloudlet> appCloudletList;
    
    /**
     * Creates, starts, stops the simulation and shows results.
     */
    public NetworkVmsExampleAppCloudletAbstract() {
        Log.printFormattedLine("Starting %s...", this.getClass().getSimpleName());
        try {
            int num_user = 1; // number of cloud users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false; // mean trace events

            CloudSim.init(num_user, calendar, trace_flag);

            this.datacenter = createDatacenter("Datacenter_0");
            this.broker = createBroker();
            this.vmlist = createVMs();
            this.broker.submitVmList(vmlist);
            this.appCloudletList = createAppCloudlets();
            for(AppCloudlet app: this.appCloudletList){
                this.broker.submitCloudletList(app.getNetworkCloudletList());
            }
            
            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            // Final step: Print results when simulation is over
            List<Cloudlet> newList = broker.getCloudletsFinishedList();
            TableBuilderHelper.print(new TextTableBuilder(), newList);
            Log.printFormattedLine("%s finished!", this.getClass().getSimpleName());
            Log.printFormattedLine("numberofcloudlet " + newList.size());
            for(NetworkHost host: datacenter.<NetworkHost>getHostList()){
                Log.printFormattedLine("Host %d Data transfered %d",
                        host.getId(), host.getTotalDataTransferBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
    }    

    /**
     * Creates the datacenter.
     *
     * @param name the name
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
        try {
            NetworkDatacenter newDatacenter = 
                    new NetworkDatacenter(
                            name, characteristics, 
                            new NetworkVmAllocationPolicy(hostList), 
                            storageList, 0);
            // Create Internal Datacenter network
            createNetwork(newDatacenter);
            return newDatacenter;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
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
     * Creates the broker.
     *
     * @return the datacenter broker
     */
    protected final NetDatacenterBroker createBroker() {
        try {
            NetDatacenterBroker newBroker = new NetDatacenterBroker("Broker");
            return newBroker;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void createNetwork(NetworkDatacenter datacenter) {
        EdgeSwitch[] edgeSwitches = new EdgeSwitch[1];
        for (int i = 0; i < edgeSwitches.length; i++) {
            edgeSwitches[i] = new EdgeSwitch("Edge" + i, datacenter);
            datacenter.switchMap.put(edgeSwitches[i].getId(), edgeSwitches[i]);
        }
        
        for (NetworkHost host : datacenter.<NetworkHost>getHostList()) {
            int switchNum = host.getId() / edgeSwitches[0].getPorts();
            edgeSwitches[switchNum].getHostList().put(host.getId(), host);
            datacenter.hostToSwitchMap.put(host.getId(), edgeSwitches[switchNum].getId());
            host.setEdgeSwitch(edgeSwitches[switchNum]);
        }
    }

    /**
     * Creates virtual machines in a datacenter
     *
     * @return 
     */
    protected final List<NetworkVm> createVMs() {
        final int numberOfVms = getDatacenterHostList().size() * MAX_VMS_PER_HOST;
        final List<NetworkVm> vmList = new ArrayList<>();
        for (int i = 0; i < numberOfVms; i++) {
            NetworkVm vm = 
                new NetworkVm(i, 
                    broker.getId(), VM_MIPS, VM_PES_NUMBER, VM_RAM, VM_BW, VM_SIZE, VMM, 
                    new NetworkCloudletSpaceSharedScheduler(datacenter));
            vmList.add(vm);
        }
        return vmList;
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
        List<NetworkVm> vmList = new ArrayList<>();
        int numOfExistingVms = vmlist.size();
        UniformDistr rand = new UniformDistr(0, numOfExistingVms, 5);
        for (int i = 0; i < numberOfVmsToSelect; i++) {
            int vmId = (int) rand.sample();
            NetworkVm vm = VmList.getById(vmlist, vmId);
            vmList.add(vm);
        }
        return vmList;
    }

    public List<NetworkVm> getVmlist() {
        return vmlist;
    }

    public NetworkDatacenter getDatacenter() {
        return datacenter;
    }

    public NetDatacenterBroker getBroker() {
        return broker;
    }
    
    /**
     * Creates a list of {@link AppCloudlet} for the given broker.
     * 
     * @return 
     */
    protected final List<AppCloudlet> createAppCloudlets() {
        List<AppCloudlet> list = new ArrayList<>();
        int currentAppCloudletId = 0;
        
        // generate Application execution Requests
        for (int i = 0; i < NUMBER_OF_APP_CLOUDLETS; i++) {
            list.add(new AppCloudlet(currentAppCloudletId));
            currentAppCloudletId++;
        }
        
        for (AppCloudlet app : list) {
            app.setNetworkCloudletList(createNetworkCloudlets(app));
        }
        
        return list;
    }

    /**
     * Creates a list of {@link NetworkCloudlet} that together represents the distributed
     * processes of a given {@link AppCloudlet}.
     * @param app
     * @return 
     */
    protected abstract List<NetworkCloudlet> createNetworkCloudlets(AppCloudlet app);
}
