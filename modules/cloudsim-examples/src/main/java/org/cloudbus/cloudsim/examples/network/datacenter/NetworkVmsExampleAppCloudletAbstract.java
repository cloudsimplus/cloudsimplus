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
    public static final int HOST_PES = 8;
    public static final int MAX_VMS_PER_HOST = 2;

    public static final double COST = 3.0; // the cost of using processing in this resource
    public static final double COST_PER_MEM = 0.05; // the cost of using memory in this resource
    public static final double COST_PER_STORAGE = 0.001; // the cost of using storage in this resource
    public static final double COST_PER_BW = 0.0; // the cost of using bw in this resource
    
    public static final int NUMBER_OF_APP_CLOUDLETS = 100;
    public static final int NUMBER_OF_VMS_FOR_EACH_APPCLOUDLET = 3;

    public static final int NETCLOUDLET_PES_NUMBER = 4;
    public static final int NETCLOUDLET_FILE_SIZE = 300;
    public static final int NETCLOUDLET_OUTPUT_SIZE = 300;
    public static final int NETCLOUDLET_RAM = 100;
    public static final int NETCLOUDLET_TASK_COMMUNICATION_LENGTH = 1;

    private List<NetworkVm> vmlist;
    private NetworkDatacenter datacenter;
    private NetDatacenterBroker broker;
    
    /**
     * Creates, starts, stops and show the simulation results.
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
            
            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            // Final step: Print results when simulation is over
            List<Cloudlet> newList = broker.getCloudletReceivedList();
            TableBuilderHelper.print(new TextTableBuilder(), newList);
            Log.printFormattedLine("%s finished!", this.getClass().getSimpleName());
            Log.printFormattedLine("numberofcloudlet " + newList.size() + " Cached "
                    + NetDatacenterBroker.cachedCloudlet);
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
        // Here are the steps needed to create a Datacenter:
        // 1. We need to create a list to store
        // our machine
        List<Host> hostList = new ArrayList<>();
        // 2. A Machine contains one or more PEs or CPUs/Cores.
        // In this example, it will have only one core.
        // List<Pe> peList = new ArrayList<Pe>();
        int mips = 1;
        // 4. Create Host with its id and list of PEs and add them to the list
        // of machines
        int ram = 2048; // host memory (MB)
        long storage = 1000000; // host storage
        long bw = 10000;
        final int numberOfHosts = EdgeSwitch.Ports * AggregateSwitch.Ports * RootSwitch.Ports;
        for (int i = 0; i < numberOfHosts; i++) {
            List<Pe> peList = createPEs(HOST_PES, mips);
            // 4. Create Host with its id and list of PEs and add them to
            // the list of machines
            hostList.add(new NetworkHost(i, new ResourceProvisionerSimple(new Ram(ram)), new ResourceProvisionerSimple(new Bandwidth(bw)), storage, peList, new VmSchedulerTimeShared(peList)));
        }
        // 5. Create a DatacenterCharacteristics object that stores the
        // properties of a data center: architecture, OS, list of
        // Machines, allocation policy: time- or space-shared, time zone
        // and its price (G$/Pe time unit).
        LinkedList<FileStorage> storageList = new LinkedList<>();
        DatacenterCharacteristics characteristics = new DatacenterCharacteristicsSimple (ARCH, OS, VMM, hostList, TIME_ZONE, COST, COST_PER_MEM, COST_PER_STORAGE, COST_PER_BW);
        // 6. Finally, we need to create a NetworkDatacenter object.
        try {
            NetworkDatacenter newDatacenter = new NetworkDatacenter(name, characteristics, new NetworkVmAllocationPolicy(hostList), storageList, 0);
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
            newBroker.setNetworkDatacenter(datacenter);
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
            host.bandwidth = EdgeSwitch.DownlinkBW;
            int switchNum = host.getId() / EdgeSwitch.Ports;
            edgeSwitches[switchNum].hostList.put(host.getId(), host);
            datacenter.hostToSwitchMap.put(host.getId(), edgeSwitches[switchNum].getId());
            host.setSwitch(edgeSwitches[switchNum]);
            List<NetworkHost> hostList = host.getSwitch().finTimeHostMap.get(0D);
            if (hostList == null) {
                hostList = new ArrayList<>();
                host.getSwitch().finTimeHostMap.put(0D, hostList);
            }
            hostList.add(host);
        }
    }

    /**
     * Creates virtual machines in a datacenter
     *
     * @return 
     */
    protected final List<NetworkVm> createVMs() {
        final int numberOfVms = broker.getNetworkDatacenter().getHostList().size() * MAX_VMS_PER_HOST;
        final int mips = 1;
        final long size = 10000; // image size (MB)
        final int ram = 512; // vm memory (MB)
        final long bw = 1000;
        final int pesNumber = HOST_PES / MAX_VMS_PER_HOST;
        final List<NetworkVm> vmList = new ArrayList<>();
        for (int i = 0; i < numberOfVms; i++) {
            NetworkVm vm = 
                new NetworkVm(i, 
                    broker.getId(), mips, pesNumber, ram, bw, size, VMM, 
                    new NetworkCloudletSpaceSharedScheduler(broker.getNetworkDatacenter()));
            vmList.add(vm);
        }
        return vmList;
    }

    /**
     * Randomly select the number of VMs defined by
     * {@link AppCloudlet#getNumberOfVmsToUse()}, from the list
     * of created VMs, to be used by the NetworkCloudlets of the given AppCloudlet.
     * 
     * @param broker the broker where to get the existing VM list
     * @param app The AppCloudlet to select VMs to
     * @return The list of randomly selected VMs
     */
    protected List<Vm> randomlySelectVmsForAppCloudlet(NetDatacenterBroker broker, AppCloudlet app) {
        List<Vm> vmList = new ArrayList<>();
        int numOfExistingVms = broker.getNetworkDatacenter().getVmList().size();
        UniformDistr rand = new UniformDistr(0, numOfExistingVms, 5);
        for (int i = 0; i < app.getNumberOfVmsToUse(); i++) {
            int vmId = (int) rand.sample();
            Vm vm = VmList.getById(broker.getNetworkDatacenter().getVmList(), vmId);
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
     * @param broker
     * @return 
     */
    protected List<AppCloudlet> createAppCloudlets(NetDatacenterBroker broker) {
        List<AppCloudlet> appCloudletList = new ArrayList<>();
        int currentAppCloudletId = 0;
        
        // generate Application execution Requests
        for (int i = 0; i < NUMBER_OF_APP_CLOUDLETS; i++) {
            appCloudletList.add(
                    new AppCloudlet(
                            currentAppCloudletId, 0, 
                            NUMBER_OF_VMS_FOR_EACH_APPCLOUDLET));
            currentAppCloudletId++;
        }
        
        for (AppCloudlet app : appCloudletList) {
            List<Vm> vmList = randomlySelectVmsForAppCloudlet(broker, app);
            app.setNetworkCloudletList(createNetworkCloudlets(app, vmList));
        }
        
        return appCloudletList;
    }

    /**
     * Creates a list of {@link NetworkCloudlet} that together represents the distributed
     * processes of a given {@link AppCloudlet}.
     * @param app
     * @param vmList
     * @return 
     */
    protected abstract List<NetworkCloudlet> createNetworkCloudlets(AppCloudlet app, List<Vm> vmList);
}
