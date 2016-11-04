/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.sla;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSimple;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.DatacenterSimple;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.HostSimple;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.util.CloudletsTableBuilderHelper;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

/**
 * This simple example shows how to use a fault injector.
 *
 * @author raysaoliveira
 */
public class ExampleUsingFaultInjector {
    private static final int HOSTS_NUMBER = 4;
    private static final int HOST_PES = 5;
    private static final int VMS_NUMBER = HOSTS_NUMBER;
    private static final int VM_PES = HOST_PES;
    private static final int CLOUDLETS_NUMBER = VMS_NUMBER*VM_PES;
    private static final int CLOUDLET_PES = 1;

    /**
     * The cloudlet list.
     */
    private final List<Cloudlet> cloudletList;

    private static List<Host> hostList;
    
    /**
     * The vmlist.
     */
    private final List<Vm> vmlist;

    /**
     * Creates Vms
     *
     * @param userId broker id
     * @param vms amount of vms to criate
     * @return list de vms
     */
    private List<Vm> createVM(int userId, int vms) {
        //Creates a container to store VMs.
        List<Vm> list = new ArrayList<>();

        //VM Parameters
        long size = 10000; //image size (MB)
        int ram = 512; //vm memory (MB)
        int mips = 1000;
        long bw = 1000;
        String vmm = "Xen"; //VMM name

        //create VMs
        Vm[] vm = new Vm[vms];

        for (int i = 0; i < vms; i++) {
            vm[i] = new VmSimple(i, userId, mips, VM_PES, 
                                 ram, bw, size, vmm, 
                                 new CloudletSchedulerTimeShared());
            //for creating a VM with a space shared scheduling policy for cloudlets:
            //vm[i] = new VmSimple(i, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
            list.add(vm[i]);
        }
        return list;
    }

    /**
     * Creates cloudlets
     *
     * @param userId broker id
     * @param cloudlets to criate
     * @return list of cloudlets
     */
    private List<Cloudlet> createCloudlet(int userId, int cloudlets) {
        // Creates a container to store Cloudlets
        List<Cloudlet> list = new LinkedList<>();

        //Cloudlet Parameters 
        long length = 10000;
        long fileSize = 300;
        long outputSize = 300;
        UtilizationModel utilizationModel = new UtilizationModelFull();
        Cloudlet[] cloudlet = new Cloudlet[cloudlets];
        
        for (int i = 0; i < cloudlets; i++) {
            cloudlet[i] = new CloudletSimple(
                    i, length, CLOUDLET_PES, fileSize, outputSize,
                    utilizationModel, utilizationModel, utilizationModel);
            // setting the owner of these Cloudlets
            cloudlet[i].setUserId(userId);
            list.add(cloudlet[i]);
        }
        return list;
    }

    /**
     * main()
     *
     * @param args the args
     */
    public static void main(String[] args) {
        Log.printFormattedLine(" Starting... ");
        try {
            new ExampleUsingFaultInjector();
        } catch (Exception e) {
            Log.printFormattedLine("Simulation finished due to unexpected error: %s", e);
        }
    }

    public ExampleUsingFaultInjector() {
        //  Initialize the CloudSim package. 
        int num_user = 1; // number of cloud users
        Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current date and time.
        boolean trace_flag = false; // trace events

        CloudSim.init(num_user, calendar, trace_flag);

        //Create Datacenters
        Datacenter datacenter0 = createDatacenter("Datacenter_0");

        for (Host host : datacenter0.getHostList()) {
            //create a new intance of fault and start it.
            HostFaultInjection fault = new HostFaultInjection("FaultInjection" + host.getId());
            fault.setHost(host);
        }

        //Create Broker
        DatacenterBroker broker = new DatacenterBrokerSimple("Broker");
        int brokerId = broker.getId();

        vmlist = createVM(brokerId, VMS_NUMBER);

        // submit vm list to the broker
        broker.submitVmList(vmlist);

        cloudletList = createCloudlet(brokerId, CLOUDLETS_NUMBER);

        // submit cloudlet list to the broker
        broker.submitCloudletList(cloudletList);

        // Sixth step: Starts the simulation
        CloudSim.startSimulation();
        CloudSim.stopSimulation();

        //Final step: Print results when simulation is over
        List<Cloudlet> newList = broker.getCloudletsFinishedList();
        new CloudletsTableBuilderHelper(newList).build();

        Log.printFormattedLine("... finished!");
    }

    /**
     * Creates the datacenter.
     *
     * @param name the name
     *
     * @return the datacenter
     */
    private Datacenter createDatacenter(String name) {
        hostList = new ArrayList<>();

        int mips = 10000;
        int hostId = 0;
        int ram = 8192; // host memory (MB)
        long storage = 1000000; // host storage
        long bw = 100000;

        for (int i = 0; i < HOSTS_NUMBER; i++) {
            List<Pe> peList = createHostPesList(HOST_PES, mips); 
            getHostList().add(new HostSimple(
                    hostId,
                    new ResourceProvisionerSimple<>(new Ram(ram)),
                    new ResourceProvisionerSimple<>(new Bandwidth(bw)),
                    storage,
                    peList,
                    new VmSchedulerTimeShared(peList)
            ));
            hostId++;
        }// This is our machine

        String arch = "x86"; // system architecture
        String os = "Linux"; // operating system
        String vmm = "Xen";
        double time_zone = 10.0; // time zone this resource located
        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this
        // resource
        double costPerBw = 0.0; // the cost of using bw in this resource
        LinkedList<FileStorage> storageList = new LinkedList<>(); // we are not adding SAN
        // devices by now

        DatacenterCharacteristics characteristics = new DatacenterCharacteristicsSimple(
                arch, os, vmm, getHostList(), time_zone, cost, costPerMem,
                costPerStorage, costPerBw);

        return new DatacenterSimple(name, characteristics, 
                new VmAllocationPolicySimple(getHostList()), storageList, 0);
    }

    public List<Pe> createHostPesList(int hostPes, int mips) {
        List<Pe> peList = new ArrayList<>();
        for(int i=0; i < hostPes; i++)
            peList.add(new PeSimple(i, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
        return peList;
    }

    /**
     * @return the hostList
     */
    public List<Host> getHostList() {
        return hostList;
    }
}
