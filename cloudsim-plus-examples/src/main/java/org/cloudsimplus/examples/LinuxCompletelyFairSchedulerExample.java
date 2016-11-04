package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.util.CloudletsTableBuilderHelper;
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
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerCompletelyFair;

/**
 * An example that uses an implementation of the {@link CloudletSchedulerCompletelyFair Completely Fair Scheduler}
 * used in the Linux Kernel for scheduling of Cloudlets execution inside a Vm.
 * 
 * @author Manoel Campos da Silva Filho
 * @see <a href="https://en.wikipedia.org/wiki/Completely_Fair_Scheduler">Completely Fair Scheduler (CFS)</a>
 * 
 */
public class LinuxCompletelyFairSchedulerExample {
    private static final int HOSTS_NUMBER = 1;
    private static final double HOST_MIPS = 1000; //in MIPS
    private static final int HOST_PES = 3;
    private static final int VMS_NUMBER = 1;
    private static final int VM_PES = HOST_PES;
    private static final double VM_MIPS = HOST_MIPS;
    private static final int CLOUDLETS_NUMBER = HOST_PES*2;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LEN = 10000; //in MI
    
    /**
     * Virtual Machine Monitor name.
     */
    private static final String VMM = "Xen"; 
    private List<Cloudlet> cloudletList;
    private List<Vm> vmList;
    
    private int numberOfCreatedCloudlets = 0;
    private int numberOfCreatedVms = 0;
    private int numberOfCreatedHosts = 0;

    /**
     * Starts the simulation.
     * @param args 
     */
    public static void main(String[] args) {
        new LinuxCompletelyFairSchedulerExample();
    }

    /**
     * Default constructor where the simulation is built.
     */
    public LinuxCompletelyFairSchedulerExample() {
        Log.printFormattedLine("Starting %s...", getClass().getSimpleName());
        try {
            //Number of cloud customers
            int numberOfCloudUsers = 1; 
            boolean traceEvents = false;
            
            CloudSim.init(numberOfCloudUsers, Calendar.getInstance(), traceEvents);

            Datacenter datacenter0 = createDatacenter("Datacenter0");

            DatacenterBroker broker0 = new DatacenterBrokerSimple("Broker0");

            createAndSubmitVms(broker0);
            createAndSubmitCloudlets(broker0);
            for(int i = 0; i < CLOUDLETS_NUMBER/2; i++){
                cloudletList.get(i).setPriority(10);
            }

            CloudSim.startSimulation();
            CloudSim.stopSimulation();
            
            List<Cloudlet> finishedCloudlets = broker0.getCloudletsFinishedList();
            new PriorityCloudletsTableBuilderHelper(finishedCloudlets).build();
            Log.printFormattedLine("%s finished!", getClass().getSimpleName());
        } catch (RuntimeException e) {
            Log.printFormattedLine("Simulation finished due to unexpected error: %s", e);
        }
    }

    private void createAndSubmitCloudlets(DatacenterBroker broker0) {
        this.cloudletList = new ArrayList<>(CLOUDLETS_NUMBER);
        for(int i = 0; i < CLOUDLETS_NUMBER; i++){
            this.cloudletList.add(createCloudlet(broker0));
        }
        broker0.submitCloudletList(cloudletList);
    }

    private void createAndSubmitVms(DatacenterBroker broker0) {
        this.vmList = new ArrayList<>(VMS_NUMBER);
        for(int i = 0; i < VMS_NUMBER; i++){
            this.vmList.add(createVm(broker0));
        }
        broker0.submitVmList(vmList);        
    }

    private DatacenterSimple createDatacenter(String name) {
        List<Host> hostList = new ArrayList<>(HOSTS_NUMBER);
        for(int i = 0; i < HOSTS_NUMBER; i++){
            hostList.add(createHost()); 
        }

        //Defines the characteristics of the data center
        final String arch = "x86"; // system architecture of datacenter hosts
        final String os = "Linux"; // operating system of datacenter hosts
        final double time_zone = 10.0; // time zone where the datacenter is located
        final double cost = 3.0; // the cost of using processing in this datacenter
        final double costPerMem = 0.05; // the cost of using memory in this datacenter
        final double costPerStorage = 0.001; // the cost of using storage in this datacenter
        final double costPerBw = 0.0; // the cost of using bw in this datacenter
        LinkedList<FileStorage> storageList = new LinkedList<>(); // we are not adding SAN devices

        DatacenterCharacteristics characteristics = new DatacenterCharacteristicsSimple(
                arch, os, VMM, hostList, time_zone, cost, costPerMem,
                costPerStorage, costPerBw);

        return new DatacenterSimple(name, characteristics, 
                new VmAllocationPolicySimple(hostList), storageList, 0);
    }    

    private Host createHost() {
        final int  ram = 2048; // host memory (MB)
        final long storage = 1000000; // host storage
        final long bw = 10000;
        
        List<Pe> cpuCoresList = createHostPesList(HOST_MIPS);
        
        return new HostSimple(numberOfCreatedHosts++,
                new ResourceProvisionerSimple<>(new Ram(ram)),
                new ResourceProvisionerSimple<>(new Bandwidth(bw)),
                storage, cpuCoresList,
                new VmSchedulerTimeShared(cpuCoresList));
    }

    private List<Pe> createHostPesList(double mips) {
        List<Pe> cpuCoresList = new ArrayList<>(HOST_PES);
        for(int i = 0; i < HOST_PES; i++){
            cpuCoresList.add(new PeSimple(i, new PeProvisionerSimple(mips)));
        }
        
        return cpuCoresList;
    }

    private Vm createVm(DatacenterBroker broker) {
        final long   storage = 10000; // vm image size (MB)
        final int    ram = 512; // vm memory (MB)
        final long   bw = 1000; // vm bandwidth 
        
        return new VmSimple(numberOfCreatedVms++, 
                broker.getId(), VM_MIPS, VM_PES, ram, bw, storage,
                VMM, new CloudletSchedulerCompletelyFair());
    }

    private Cloudlet createCloudlet(DatacenterBroker broker) {
        final long fileSize = 300; //Size (in bytes) before execution
        final long outputSize = 300; //Size (in bytes) after execution
        
        //Defines how CPU, RAM and Bandwidth resources are used
        //Sets the same utilization model for all these resources.
        UtilizationModel utilization = new UtilizationModelFull();
        
        Cloudlet cloudlet
                = new CloudletSimple(
                        numberOfCreatedCloudlets++, CLOUDLET_LEN, CLOUDLET_PES, 
                        fileSize, outputSize, 
                        utilization, utilization, utilization);
        cloudlet.setUserId(broker.getId());
        
        return cloudlet;
    }
}

/**
 * A helper class to print cloudlets results as a table that includes
 * the Cloudlet priority as a table field.
 * 
 * @author Manoel Campos da Silva Filho
 */
class PriorityCloudletsTableBuilderHelper extends CloudletsTableBuilderHelper {
    public PriorityCloudletsTableBuilderHelper(List<? extends Cloudlet> list) {
        super(list);
    }

    @Override
    protected void createTableColumns() {
        super.createTableColumns();
        getPrinter().addColumn("Priority");
    }

    @Override
    protected void addDataToRow(Cloudlet cloudlet, List<Object> row) {
        super.addDataToRow(cloudlet, row);
        row.add(cloudlet.getPriority());
    }    
}
