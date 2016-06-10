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
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.listeners.VmToCloudletEventInfo;
import org.cloudbus.cloudsim.listeners.EventListener;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelStochastic;

/**
 * A simple example showing how to create a data center with 1 host and run
 * 1 cloudlet on it. The example uses the new Cloudlet listeners
 * to get notified every time a cloudlet has its processing updated
 * inside a Vm and then, the current cloudlet resource usage is shown.
 * The example uses the {@link UtilizationModelStochastic} 
 * to define that the usage of CPU, RAM and Bandwidth is random.
 *
 * @see
 * Cloudlet#setOnUpdateCloudletProcessingListener(org.cloudbus.cloudsim.listeners.EventListener) 
 * @see EventListener
 *
 * @author Manoel Campos da Silva Filho
 */
public class CloudletListenersExample2_ResourceUsageAlongTime {
    /**
     * Number of Processor Elements (CPU Cores) of each Host.
     */
    private static final int HOST_PES_NUMBER = 1; 

    /**
     * Number of Processor Elements (CPU Cores) of each VM and cloudlet.
     */
    private static final int VM_PES_NUMBER = HOST_PES_NUMBER;    
    
    /**
     * Number of Cloudlets to create.
     */
    private static final int NUMBER_OF_CLOUDLETS = 2; 
    
    private static final double DATACENTER_SCHEDULING_INTERVAL = 1;

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
     * the processing of a cloudlet inside a Vm is updated. 
     * 
     * @see #createCloudletListener() 
     */
    private EventListener<VmToCloudletEventInfo> onUpdateCloudletProcessingListener;
    
    /**
     * Starts the example execution, calling the class constructor\
     * to build and run the simulation.
     * 
     * @param args command line parameters
     */
    public static void main(String[] args) {
        Log.printFormattedLine("Starting %s ...", CloudletListenersExample2_ResourceUsageAlongTime.class.getSimpleName());
        try {
            new CloudletListenersExample2_ResourceUsageAlongTime();
            Log.printFormattedLine("%s finished!", CloudletListenersExample2_ResourceUsageAlongTime.class.getSimpleName());        
        } catch (Exception e) {
            Log.printFormattedLine("Unwanted errors happened: %s", e.getMessage());
        }
    }

    /**
     * Default constructor that builds and starts the simulation.
     */
    public CloudletListenersExample2_ResourceUsageAlongTime() {
        int numberOfUsers = 1; // number of cloud users/customers (brokers)
        Calendar calendar = Calendar.getInstance();
        CloudSim.init(numberOfUsers, calendar);
        
        this.hostList = new ArrayList<>();
        this.vmList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();
        this.datacenter = createDatacenter("Datacenter_0");
        this.broker = new DatacenterBrokerSimple("Broker");

        createCloudletListener();
        createAndSubmitVms();
        createAndSubmitCloudlets(this.vmList.get(0));

        runSimulationAndPrintResults();
    }

    /**
     * Creates the listener object that will be notified when a cloudlet 
     * finishes running into a VM. All cloudlet will use this same listener.
     * 
     * @see #createCloudlet(int, org.cloudbus.cloudsim.Vm, long) 
     */
    private void createCloudletListener() {
        this.onUpdateCloudletProcessingListener = new EventListener<VmToCloudletEventInfo>() {
            @Override
            public void update(VmToCloudletEventInfo evt) {
                Cloudlet c = evt.getCloudlet();
                double cpuUsage = c.getUtilizationModelCpu().getUtilization(evt.getTime())*100;
                double ramUsage = c.getUtilizationModelRam().getUtilization(evt.getTime())*100;
                double bwUsage  = c.getUtilizationModelBw().getUtilization(evt.getTime())*100;
                Log.printFormattedLine(
                        "\t#EventListener: Time %.0f: Updated Cloudlet %d execution inside Vm %d",
                        evt.getTime(), c.getId(), evt.getVm().getId()); 
                Log.printFormattedLine(
                        "\tCurrent Cloudlet resource usage: CPU %3.0f%%, RAM %3.0f%%, BW %3.0f%%\n", 
                        cpuUsage,  ramUsage, bwUsage);                
                
            }
        };
    }

    private void runSimulationAndPrintResults() {
        CloudSim.startSimulation();
        CloudSim.stopSimulation();

        List<Cloudlet> finishedCloudlets = broker.getCloudletsFinishedList();
        CloudletsTableBuilderHelper.print(new TextTableBuilder(), finishedCloudlets);
    }

    /**
     * Creates cloudlets and submit them to the broker.
     * @param vm Vm to run the cloudlets to be created
     * 
     * @see #createCloudlet(int, org.cloudbus.cloudsim.Vm) 
     */
    private void createAndSubmitCloudlets(Vm vm) {
        int cloudletId;
        long length = 10000;
        for(int i = 0; i < NUMBER_OF_CLOUDLETS; i++){
            cloudletId = vm.getId() + i;
            Cloudlet cloudlet = createCloudlet(cloudletId, vm, length);
            this.cloudletList.add(cloudlet);
        }

        this.broker.submitCloudletList(cloudletList);
    }

    /**
     * Creates VMs and submit them to the broker.
     */
    private void createAndSubmitVms() {
        Vm vm0 = createVm(0);
        this.vmList.add(vm0);
        this.broker.submitVmList(vmList);
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
                VMM, new CloudletSchedulerSpaceShared());
        return vm;
    }

    /**
     * Creates a cloudlet with pre-defined configuration.
     * 
     * @param id Cloudlet id
     * @param vm vm to run the cloudlet
     * @param length the cloudlet length in number of Million Instructions (MI)
     * @return the created cloudlet
     */
    private Cloudlet createCloudlet(int id, Vm vm, long length) {
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        
        /*Define that the utilization of CPU, RAM and Bandwidth is random.*/
        UtilizationModel cpuUtilizationModel = new UtilizationModelStochastic();
        UtilizationModel ramUtilizationModel = new UtilizationModelStochastic();
        UtilizationModel bwUtilizationModel  = new UtilizationModelStochastic();
        
        Cloudlet cloudlet = 
            new CloudletSimple(
                id, length, pesNumber, fileSize, outputSize, 
                cpuUtilizationModel, ramUtilizationModel, bwUtilizationModel);
        
        cloudlet.setUserId(broker.getId());
        cloudlet.setVmId(vm.getId());
        cloudlet.setOnUpdateCloudletProcessingListener(onUpdateCloudletProcessingListener);
        
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
                new VmAllocationPolicySimple(hostList), storageList, DATACENTER_SCHEDULING_INTERVAL);
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
}
