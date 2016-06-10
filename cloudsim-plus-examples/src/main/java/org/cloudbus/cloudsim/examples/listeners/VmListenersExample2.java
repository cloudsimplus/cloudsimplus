package org.cloudbus.cloudsim.examples.listeners;

/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */
import org.cloudbus.cloudsim.util.CloudletsTableBuilderHelper;
import org.cloudbus.cloudsim.util.TextTableBuilder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSimple;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterSimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.HostSimple;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.listeners.EventListener;
import org.cloudbus.cloudsim.listeners.HostToVmEventInfo;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Ram;

/**
 * A simple example showing how to create a data center with 1 host and place 2 VMs 
 * to run 1 cloudlet each one, and receive notifications when a Host is allocated or
 * deallocated to each Vm. 
 * 
 * The example uses the new Vm listeners to get these
 * notifications while the simulation is running. It also shows how to
 * reuse the same listener object to different VMs.
 *
 * @see
 * Vm#setOnHostAllocationListener(org.cloudbus.cloudsim.listeners.EventListener)
 * @see
 * Vm#setOnHostDeallocationListener(org.cloudbus.cloudsim.listeners.EventListener)
 * @see EventListener
 *
 * @author Manoel Campos da Silva Filho
 */
public class VmListenersExample2 {
    /**
     * Number of VMs to create.
     */
    private static final int NUMBER_OF_VMS = 4; 

    /**
     * Number of Processor Elements (CPU Cores) of each Host.
     */
    private static final int HOST_PES_NUMBER = NUMBER_OF_VMS; 

    /**
     * Number of Processor Elements (CPU Cores) of each VM and cloudlet.
     */
    private static final int VM_PES_NUMBER = 1;     
    
    /**
     * The Virtual Machine Monitor (VMM) used by hosts to manage VMs.
     */
    private static final String VMM = "Xen"; 
    
    private final List<Host> hostList;
    private final List<Vm> vmList;
    private final List<Cloudlet> cloudletList;
    private final DatacenterBroker broker;
    private final Datacenter datacenter;
    
    /**
     * The listener object that will be created in order to be notified when
     * a host is allocated for a VM. The same listener is used for all created VMs.
     * @see #createVmListeners() 
     */
    private EventListener<HostToVmEventInfo> onHostAllocationListener;
    
    /**
     * The listener object that will be created in order to be notified when
     * a host is deallocated for a VM. The same listener is used for all created VMs.
     * @see #createVmListeners() 
     */
    private EventListener<HostToVmEventInfo> onHostDeallocationListener;

    /**
     * Starts the example execution, calling the class constructor\
     * to build and run the simulation.
     * 
     * @param args command line parameters
     */
    public static void main(String[] args) {
        Log.printFormattedLine("Starting %s ...", VmListenersExample2.class.getSimpleName());
        try {
            new VmListenersExample2();
            Log.printFormattedLine("%s finished!", VmListenersExample2.class.getSimpleName());        
        } catch (Exception e) {
            Log.printFormattedLine("Unwanted errors happened: %s", e.getMessage());
        }
    }

    /**
     * Default constructor that builds and starts the simulation.
     */
    public VmListenersExample2() {
        int numberOfUsers = 1; // number of cloud users/customers (brokers)
        Calendar calendar = Calendar.getInstance();
        CloudSim.init(numberOfUsers, calendar);
        
        this.hostList = new ArrayList<>();
        this.vmList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();
        this.datacenter = createDatacenter("Datacenter_0");
        this.broker = new DatacenterBrokerSimple("Broker");
        
        createVmListeners();
        createAndSubmitVmsAndCloudlets();
        runSimulationAndPrintResults();
    }

    /**
     * Creates VMs and the cloudlets for each one, submitting
     * the list of VMs and Cloudlets to the broker.
     * 
     * @see #createVm(int) 
     */
    private void createAndSubmitVmsAndCloudlets() {
        for(int i = 0; i < NUMBER_OF_VMS; i++){
            Vm vm = createVm(i);
            this.vmList.add(vm);
            
            Cloudlet cloudlet = createCloudlet(i, vm);
            this.cloudletList.add(cloudlet);
        }
        
        this.broker.submitVmList(vmList);
        this.broker.submitCloudletList(cloudletList);        
    }

    /**
     * Creates all VM listeners to be used by every VM created.
     * 
     * @see #createVm(int) 
     */
    private void createVmListeners() {
        /*Creates the listener object that will be notified when a host is allocated to a VM.
        All VMs will use this same listener.*/
        this.onHostAllocationListener = new EventListener<HostToVmEventInfo>() {
            @Override
            public void update(HostToVmEventInfo evt) {
                Log.printFormattedLine(
                        "\t#EventListener: Host %d allocated to Vm %d at time %.2f",
                        evt.getHost().getId(), evt.getVm().getId(), evt.getTime());
            }
        };
        
        /*Creates the listener object that will be notified when a host is deallocated for a VM.
        All VMs will use this same listener.*/
        this.onHostDeallocationListener = new EventListener<HostToVmEventInfo>() {
            @Override
            public void update(HostToVmEventInfo evt) {
                Log.printFormattedLine(
                        "\t#EventListener: Vm %d moved/removed from Host %d at time %.2f",
                        evt.getVm().getId(), evt.getHost().getId(), evt.getTime());
            }
        };
    }

    /**
     * Creates a VM with pre-defined configuration.
     * 
     * @param id the VM id
     * @return the created VM
     */
    private Vm createVm(int id) {
        int mips = 1000;
        long size = 10000; // image size (MB)
        int ram = 512; // vm memory (MB)
        long bw = 1000;
        
        Vm vm = new VmSimple(
                id, broker.getId(), mips, VM_PES_NUMBER, ram, bw, size,
                VMM, new CloudletSchedulerTimeShared());
        
        /*Sets the listener to intercept allocation of a Host to the Vm.*/
        vm.setOnHostAllocationListener(onHostAllocationListener);

        /*Sets the listener to intercept deallocation of a Host for the Vm.*/
        vm.setOnHostDeallocationListener(onHostDeallocationListener);
        
        return vm;
    }

    /**
     * Creates a cloudlet with pre-defined configuration.
     * 
     * @param id Cloudlet id
     * @param vm vm to run the cloudlet
     * @return the created cloudlet
     */
    private Cloudlet createCloudlet(int id, Vm vm) {
        long length = 400000;  //in MI (Million Instructions)
        long fileSize = 300;
        long outputSize = 300;
        UtilizationModel utilizationModel = new UtilizationModelFull();
        Cloudlet cloudlet
                = new CloudletSimple(id, length, VM_PES_NUMBER, fileSize,
                        outputSize, utilizationModel, utilizationModel,
                        utilizationModel);
        cloudlet.setUserId(broker.getId());
        cloudlet.setVmId(vm.getId());
        return cloudlet;
    }

    /**
     * Creates a datacenter with pre-defined configuration.
     *
     * @param name the datacenter name
     * @return the created datacenter
     */
    private Datacenter createDatacenter(String name) {
        Host host = createHost(0);
        hostList.add(host); 

        String arch = "x86"; // system architecture
        String os = "Linux"; // operating system
        double time_zone = 10.0; // time zone this resource located
        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this datacenter
        double costPerBw = 0.0; // the cost of using bw in this resource
        LinkedList<FileStorage> storageList = new LinkedList<>(); // we are not adding SAN devices by now

        DatacenterCharacteristics characteristics = new DatacenterCharacteristicsSimple(
                arch, os, VMM, hostList, time_zone, cost, costPerMem,
                costPerStorage, costPerBw);

        return new DatacenterSimple(name, characteristics, 
                new VmAllocationPolicySimple(hostList), storageList, 0);
    }

    /**
     * Creates a host with pre-defined configuration.
     * 
     * @param id The Host id
     * @return the created host
     */
    private Host createHost(int id) {
        List<Pe> peList = new ArrayList<>();
        int mips = 1000;
        for(int i = 0; i < HOST_PES_NUMBER; i++){
            peList.add(new PeSimple(i, new PeProvisionerSimple(mips)));
        }
        int ram = 2048; // host memory (MB)
        long storage = 1000000; // host storage
        long bw = 10000;
        
        return new HostSimple(id,
                new ResourceProvisionerSimple<>(new Ram(ram)),
                new ResourceProvisionerSimple<>(new Bandwidth(bw)),
                storage, peList, new VmSchedulerTimeShared(peList));
    }
    
    private void runSimulationAndPrintResults() {
        CloudSim.startSimulation();
        CloudSim.stopSimulation();

        List<Cloudlet> newList = broker.getCloudletsFinishedList();
        CloudletsTableBuilderHelper.print(new TextTableBuilder(), newList);
    }    
}
