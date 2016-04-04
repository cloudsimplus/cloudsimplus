/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.examples.network;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSimple;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.network.datacenter.EdgeSwitch;
import org.cloudbus.cloudsim.network.datacenter.NetDatacenterBroker;
import org.cloudbus.cloudsim.network.datacenter.NetworkCloudlet;
import org.cloudbus.cloudsim.network.datacenter.NetworkConstants;
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
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

/**
 * A simple example showing how to create a Network datacenter with 1 host and a
 * network topology and and run 1 cloudlet on it.
 *
 * @author raysaoliveira
 */
public class NetworkExample5 {

    /**
     * The cloudlet list.
     */
    private static List<Cloudlet> cloudletList;

    /**
     * The vmlist.
     */
    private static List<NetworkVm> vmlist;

    private static final String ARCH = "x86"; // system architecture
    private static final String OS = "Linux"; // operating system
    private static final String VMM = "Xen";
    private static final double TIME_ZONE = 10.0; // time zone this resource located
    private static final double COST = 3.0; // the cost of using processing in this resource
    private static final double COST_PER_MEM = 0.05; // the cost of using memory in this resource
    private static final double COST_PER_STORAGE = 0.001; // the cost of using storage in this resource
    private static final double COST_PER_BW = 0.0; // the cost of using bw in this resource

    private static List<NetworkVm> createNetVM(int userId, int vms) {
        //Creates a container to store VMs. This list is passed to the broker later
        List<NetworkVm> list = new LinkedList<>();

        //VM Parameters
        long size = 10000; //image size (MB)
        int ram = 512; //vm memory (MB)
        int mips = 1000;
        long bw = 1000;
        int pesNumber = NetworkConstants.hostPEs / NetworkConstants.maxVmsPerHost;
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

   /* private static List<NetworkCloudlet> createNetCloudlet(int userId, int cloudlets) {
        // Creates a container to store Cloudlets
        List<NetworkCloudlet> list = new LinkedList<>();

        //cloudlet parameters
        long length = 1000;
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        NetworkCloudlet[] cloudlet = new NetworkCloudlet[cloudlets];

        for (int i = 0; i < cloudlets; i++) {
            cloudlet[i] = new NetworkCloudlet(i, cloudletLength, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            // setting the owner of these Cloudlets
            cloudlet[i].setUserId(userId);
            list.add(cloudlet[i]);
        }

        return list;
    }
*/
    /**
     * Creates main() to run this example
     */
    public static void main(String[] args) {
        Log.printFormattedLine("Starting %s...", NetworkExample5.class.getSimpleName());
        try {
            // First step: Initialize the CloudSim package. It should be called
            // before creating any entities.
            int num_user = 1;   // number of cloud users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;  // mean trace events

            // Initialize the CloudSim library
            CloudSim.init(num_user, calendar, trace_flag);

            //Setter autoCreateVmsInNetDatacenterBroker as false
           // NetworkConstants.autoCreateVmsInNetDatacenterBroker = false;

            //Second step: Create Network Datacenter
            NetworkDatacenter datacenter0 = createDatacenter("Datacenter_0");
            int data = datacenter0.getId();
            //Third step: Create Network Broker
            NetDatacenterBroker broker = createBroker();
            broker.setLinkDC(datacenter0);
            int brokerId = broker.getId();

            //4 step: Criar Vms
            vmlist = createNetVM(brokerId, 10);

            // submit vm list to the broker
            broker.submitVmList(vmlist);

            // Sixth step: Starts the simulation
            CloudSim.startSimulation();
            CloudSim.stopSimulation();

        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
    }

    /* Creates the datacenter.
     *
     * @param name the name
     *
     * @return the datacenter
     */
    private static NetworkDatacenter createDatacenter(String name) {
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
        final int numberOfHosts
                = NetworkConstants.EdgeSwitchPorts
                * NetworkConstants.AggregationSwitchPorts
                * NetworkConstants.RootSwitchPorts;

        for (int i = 0; i < numberOfHosts; i++) {
            List<Pe> peList = createPEs(8, mips);
            // 4. Create Host with its id and list of PEs and add them to
            // the list of machines
            hostList.add(new NetworkHost(i,
                    new ResourceProvisionerSimple(new Ram(ram)),
                    new ResourceProvisionerSimple(new Bandwidth(bw)),
                    storage, peList,
                    new VmSchedulerTimeShared(peList)));
        }

        // 5. Create a DatacenterCharacteristics object that stores the
        // properties of a data center: architecture, OS, list of
        // Machines, allocation policy: time- or space-shared, time zone
        // and its price (G$/Pe time unit).
        LinkedList<FileStorage> storageList = new LinkedList<>();

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                ARCH, OS, VMM, hostList, TIME_ZONE, COST,
                COST_PER_MEM, COST_PER_STORAGE, COST_PER_BW);

        // 6. Finally, we need to create a NetworkDatacenter object.
        NetworkDatacenter datacenter = null;
        try {
            datacenter = new NetworkDatacenter(
                    name, characteristics,
                    new NetworkVmAllocationPolicy(hostList),
                    storageList, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create Internal Datacenter network
        createNetwork(datacenter);
        return datacenter;
    }

    public static List<Pe> createPEs(final int numberOfPEs, final int mips) {
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
    private static NetDatacenterBroker createBroker() {
        try {
            return new NetDatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void createNetwork(NetworkDatacenter dc) {
        EdgeSwitch edgeSwitches[] = new EdgeSwitch[1];

        for (int i = 0; i < edgeSwitches.length; i++) {
            edgeSwitches[i] = new EdgeSwitch("Edge" + i, NetworkConstants.EDGE_SWITCHES_NUMBER, dc);
            dc.Switchlist.put(edgeSwitches[i].getId(), edgeSwitches[i]);
        }

        for (NetworkHost host : dc.<NetworkHost>getHostList()) {
            host.bandwidth = NetworkConstants.EdgeSwitchDownlinkBW;
            int switchNum = host.getId() / NetworkConstants.EdgeSwitchPorts;
            edgeSwitches[switchNum].hostlist.put(host.getId(), host);
            dc.HostToSwitchid.put(host.getId(), edgeSwitches[switchNum].getId());
            host.setSwitch(edgeSwitches[switchNum]);
            List<NetworkHost> hostList = host.getSwitch().finTimeHostMap.get(0D);
            if (hostList == null) {
                hostList = new ArrayList<>();
                host.getSwitch().finTimeHostMap.put(0D, hostList);
            }
            hostList.add(host);
        }
    }
}
