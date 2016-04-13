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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.network.NetworkTopology;
import org.cloudbus.cloudsim.network.TopologicalLink;
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
import org.cloudbus.cloudsim.util.TableBuilderHelper;
import org.cloudbus.cloudsim.util.TextTableBuilder;
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
    private static List<NetworkCloudlet> cloudletList;

    /**
     * The vmlist.
     */
    private static List<NetworkVm> vmlist;
    private static int numberOfVms = 10;

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
    
    private static double TotalCost(NetworkDatacenter nt, NetworkVm vm, NetworkCloudlet cloudlet){
        
        double bw = nt.getCharacteristics().getCostPerBw(); // * vm.getBw();
        double storage = nt.getCharacteristics().getCostPerStorage(); // * vm.getSize();
        double memory = nt.getCharacteristics().getCostPerMem(); //*  vm.getRam();
        double cpu = nt.getCharacteristics().getCostPerSecond(); //* vm.getTotalUtilizationOfCpu(5);
        double mi = nt.getCharacteristics().getCostPerMi();
        
        double bwVm = bw * vm.getBw();
        System.out.println("BW-VM -" + bwVm);
        
        double totalcost = bw + storage + memory + cpu + mi;
       /*
        System.out.println("\n BW:" + bw + " BW da VM: " + vm.getBw());
        System.out.println("\n Storage:" + storage + " STORAGE da VM: " + vm.getSize());
        System.out.println("\n memory:" + memory + " memory da VM: " + vm.getRam());
        System.out.println("\n cpu:" + cpu + " cpu da VM: " + vm.getTotalUtilizationOfCpu(5));
        System.out.println("\n MI:" + mi);
        */
        return totalcost;        
    }
    
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

            //Second step: Create Network Datacenter
            NetworkDatacenter datacenter0 = createDatacenter("Datacenter_0");
            int datacenterId = datacenter0.getId();
            //Third step: Create Network Broker
            NetDatacenterBroker broker = createBroker();
            broker.setLinkDC(datacenter0);
            int brokerId = broker.getId();

            //fourth step: Criar Vms
            vmlist = createNetVM(brokerId, numberOfVms);

            // submit vm list to the broker
            broker.submitVmList(vmlist);

            //fift step: Create cloudlets
            cloudletList = new LinkedList<>();
            NetworkCloudlet cl;
            for (int i = 0; i < numberOfVms; i++) {
                long length = 4;
                long fileSize = 300;
                long outputSize = 300;
                long memory = 256;
                int pesNumber = 4;
                UtilizationModel utilizationModel = new UtilizationModelFull();
                // HPCCloudlet cl=new HPCCloudlet();
                cl = new NetworkCloudlet(
                        NetworkConstants.currentCloudletId,
                        length,
                        pesNumber,
                        fileSize,
                        outputSize,
                        memory,
                        utilizationModel,
                        utilizationModel,
                        utilizationModel);
                // setting the owner of these Cloudlets
                NetworkConstants.currentCloudletId++;
                cl.setUserId(brokerId);
                cl.submittime = CloudSim.clock();
                cl.currStagenum = -1;
                cloudletList.add(cl);
            }
            broker.submitCloudletList(cloudletList);
            
            NetworkTopology.buildNetworkTopology("topology.brite");
      
            int briteNode = 0;
            NetworkTopology.mapNode(datacenterId, briteNode);
                       
            // Sixth step: Starts the simulation
            CloudSim.startSimulation();
            CloudSim.stopSimulation();
   
            //show the list of linkList
            Iterator<TopologicalLink> iterator = NetworkTopology.getTopologycalGraph().getLinkIterator();
            System.out.println("\n *** Lista de Links  *** \n");
            while ( iterator.hasNext()) {
                TopologicalLink link =  iterator.next();
                System.out.println( "SrcNodeID:" + link.getSrcNodeID() + "\t DstNodeid:" + link.getDestNodeID() + "\t Delay:" + link.getLinkDelay() + "\tBw:" + link.getLinkBw());
            }
            
            //show the cost price
            //double cost = TotalCost(datacenter0);
            //System.out.println("\n Total Cost - Customer SLA: " + cost);
           
            //Response Time
            //double rp = cl.getFinishTime() - cl.getSubmissionTime();
            
            
            List<NetworkCloudlet> newList = broker.getCloudletReceivedList();
            TableBuilderHelper.print(new TextTableBuilder(), newList);
            Log.printFormattedLine("%s finished!", NetworkExample5.class.getSimpleName());
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
