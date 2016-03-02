package org.cloudbus.cloudsim.examples.observer;

/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.examples.util.ResultsHelper;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.util.TextTableBuilder;

/**
 * This example class implements the Observer pattern
 * in order to be notified at the exact time when a Host is allocated or deallocated to
 * each VM. It creates 2 users (brokers) and 1 VM for each one. All VMs have
 * the same capacity.
 * Each VM receives 1 cloudlet with different length (in MI).
 * 
 * <p/>
 * 
 * <b>NOTE:</b> The CloudSim log was disabled, so internal messages will be output.
 * See Log.disable() instruction here.<p/>
 * 
 * Example based on {@link org.cloudbus.cloudsim.examples.CloudSimExample1}
 * 
 * @author Manoel Campos da Silva Filho
 * @see VmAllocationPolicySimpleObservable
 */
public final class CloudSimExample10 implements VmAllocationPolicySimpleObserver {
    public static final int HOST_MIPS_BY_PE = 2000;
    public static final int  HOST_RAM = 2048; // (MB)
    public static final long HOST_STORAGE = 1000000; 
    public static final long  HOST_BW = 10000;

    public static final int  VM_MIPS = 1000;
    public static final long VM_SIZE = 10000; // image size (MB)
    public static final int  VM_RAM = 512; // vm memory (MB)
    public static final long VM_RW = 1000;
    public static final int  VM_PES = 1; // number of cpus

    /**
     * Length for cloudlets to be created. The number of elements of this array
     * defines the number of cloudlets to be created.
     */
    public static final long CLOUDLET_LENGTH[] = {10000, 500000};
    public static final long CLOUDLET_FILESIZE = 300;
    public static final long CLOUDLET_OUTPUTSIZE = 300;
    
    public static void main(String[] args) {
        new CloudSimExample10(true);
    }

    public CloudSimExample10(final boolean disableLog) {
        Log.printFormattedLine("Starting %s...", CloudSimExample10.class.getSimpleName());
        if(disableLog){
            Log.printLine("Internal CloudSim log was disabled as requested");
            Log.disable();
        }
        try {
            // First step: Initialize the CloudSim package. It should be called before creating any entities.
            int num_user = 1; // number of cloud users
            Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current date and time.

            CloudSim.init(num_user, calendar, disableLog);
            
            // Second step: Create Datacenters
            // Datacenters are the resource providers in CloudSim. We need at
            // list one of them to run a CloudSim simulation
            Datacenter datacenter0 = createDatacenter("Datacenter_0");

            final int numbefOfCloudlets = CLOUDLET_LENGTH.length;

            // Third step: Create Broker
            List<DatacenterBroker> brokers = new ArrayList<DatacenterBroker>(numbefOfCloudlets);
            for(int i = 0; i < numbefOfCloudlets; i++) {
                brokers.add(createBroker("broker"+i));
            }

            // Fourth step: Create virtual machines
            createOneVmForEachBrokerAndSubmitThem(brokers);

            // Fifth step: Create Cloudlets
            for(int i = 0; i < brokers.size(); i++)            
                createCloudletsAndSubmitToBroker(1, i, CLOUDLET_LENGTH[i], brokers.get(i));

            // Sixth step: Starts the simulation
            CloudSim.startSimulation();

            CloudSim.stopSimulation();

            Log.enable();
            //Final step: Print results when simulation is over
            for(int i = 0; i < brokers.size(); i++){
                List<Cloudlet> cloudletList = brokers.get(i).getCloudletReceivedList();
                ResultsHelper.print(new TextTableBuilder("Broker " + brokers.get(i).getId()), cloudletList);
            }
           
            Log.printFormattedLine("%s finished!", CloudSimExample10.class.getSimpleName());
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
    }

    public void createCloudletsAndSubmitToBroker(
            final int numbefOfCloudlets, final int vmId, 
            final long cloudletLength, DatacenterBroker broker) {
        List<Cloudlet> cloudletList = new ArrayList<Cloudlet>(numbefOfCloudlets);
        for(int i = 0; i < numbefOfCloudlets; i++){
            Cloudlet cloudlet = createCloudlet(i, vmId, broker.getId(), cloudletLength);
            cloudletList.add(cloudlet);
        }
        
        broker.submitCloudletList(cloudletList);
    }

    public Cloudlet createCloudlet(int cloudletId, int vmId, int brokerId, long length) {
        UtilizationModel utilizationModel = new UtilizationModelFull();
        Cloudlet cloudlet
                = new Cloudlet(cloudletId, length, VM_PES, CLOUDLET_FILESIZE,
                        CLOUDLET_OUTPUTSIZE, utilizationModel, utilizationModel,
                        utilizationModel);
        cloudlet.setUserId(brokerId);
        cloudlet.setVmId(vmId);
        return cloudlet;
    }

    public static void createOneVmForEachBrokerAndSubmitThem(List<DatacenterBroker> brokers) {        
        for(int i = 0; i < brokers.size(); i++){
            List<Vm> list = new ArrayList<Vm>(1);
            DatacenterBroker broker = brokers.get(i);
            list.add(new Vm(
                    i, broker.getId(), VM_MIPS, VM_PES, VM_RAM, VM_RW, VM_SIZE, "Xen",
                    new CloudletSchedulerTimeShared()));
            broker.submitVmList(list);
        }        
    }

    /**
     * Creates the datacenter.
     *
     * @param name the name
     * @return the datacenter
     */
    private Datacenter createDatacenter(String name) {
        // Here are the steps needed to create a PowerDatacenter:
        // 1. We need to create a list to store
        // our machine
        List<Host> hostList = new ArrayList<>();

        // 4. Create Host with its id and list of PEs and add them to the list of machines
        final int numberOfPEs = 1;
        hostList.add(createHost(0, numberOfPEs)); 

        DatacenterCharacteristics characteristics = createDatacenterCharacteristics(hostList); 

        // we are not adding SAN devices by now
        LinkedList<FileStorage> storageList = new LinkedList<>(); 
        
        // 6. Finally, we need to create a Datacenter object.
        Datacenter datacenter = null;
        try {
            datacenter = 
                new Datacenter(name, characteristics, 
                    new VmAllocationPolicySimpleObservable(this, hostList), 
                    storageList, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return datacenter;
    }

    public DatacenterCharacteristics createDatacenterCharacteristics(List<Host> hostList) {
        // 5. Create a DatacenterCharacteristics object that stores the
        // properties of a data center: architecture, OS, list of
        // Machines, allocation policy: time- or space-shared, time zone
        // and its price (G$/Pe time unit).
        String arch = "x86"; // system architecture
        String os = "Linux"; // operating system
        String vmm = "Xen";
        double time_zone = 10.0; // time zone this resource located
        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this
        // resource
        double costPerBw = 0.0; // the cost of using bw in this resource
        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem,
                costPerStorage, costPerBw);
        return characteristics;
    }

    public static Host createHost(int hostId, int numberOfPes) {
        // 2. A Machine contains one or more PEs or CPUs/Cores.
        // In this example, it will have only one core.
        List<Pe> peList = new ArrayList<>();

        // 3. Create PEs and add these into a list.
        for(int i = 0; i < numberOfPes; i++)
            peList.add(new Pe(i, new PeProvisionerSimple(HOST_MIPS_BY_PE)));

        return new Host(
                hostId,
                new ResourceProvisionerSimple(new Ram(HOST_RAM)),
                new ResourceProvisionerSimple(new Bandwidth(HOST_BW)),
                HOST_STORAGE,
                peList,
                new VmSchedulerTimeShared(peList)
        );
    }

    // We strongly encourage users to develop their own broker policies, to
    // submit vms and cloudlets according
    // to the specific rules of the simulated scenario
    /**
     * Creates the broker.
     *
     * @return the datacenter broker
     */
    private DatacenterBroker createBroker(final String name) {
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return broker;
    }

    @Override
    public void notifyAllocationOfHostToVm(double clock , Vm vm, Host host) {
        final String msg = String.format(
            " #Host %1d  allocated to  Vm %1d of User %1d at time %4.0fs - Host MIPS: %4d available mips: %4.0f", 
            host.getId(), vm.getId(), vm.getUserId(), clock, 
            host.getTotalMips(), host.getAvailableMips());

        printLogMessageAndCheckIfLogWasDisabled(msg);
    }

    @Override
    public void notifyDeallocationOfHostForVm(double clock, int vmId, int userId, Host host) {
        final String msg = String.format(
            " #Host %1d dallocated for Vm %1d of User %1d at time %4.0fs - Host MIPS: %4d available mips: %4.0f", 
            host.getId(), vmId, userId, clock, 
            host.getTotalMips(), host.getAvailableMips());

        printLogMessageAndCheckIfLogWasDisabled(msg);
    }

    public void printLogMessageAndCheckIfLogWasDisabled(final String msg) {
        final boolean wasDisabled = Log.isDisabled();
        try{
            if(wasDisabled)
                Log.enable();
            Log.printLine(msg);
        } finally {
            if(wasDisabled)
            Log.disable();
        }
    }
}
