package org.cloudbus.cloudsim.examples;

import org.cloudbus.cloudsim.util.CloudletsTableBuilderHelper;
import org.cloudbus.cloudsim.util.TextTableBuilder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
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

/**
 * A minimal example showing how to create a data center with 1 host and run 1
 * cloudlet on it.
 * @author Manoel Campos da Silva Filho
 */
public class MinimalExample {
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
        new MinimalExample();
    }

    /**
     * Default constructor where the simulation is built.
     */
    public MinimalExample() {
        Log.printLine("Starting Minimal Example ...");
        try {
            this.vmList = new ArrayList<>();
            this.cloudletList = new ArrayList<>();
            //Number of cloud customers
            int numberOfCloudUsers = 1; 
            boolean traceEvents = false;
            
            CloudSim.init(numberOfCloudUsers, Calendar.getInstance(), traceEvents);

            Datacenter datacenter0 = createDatacenter("Datacenter0");

            /*Creates a Broker accountable for submission of VMs and Cloudlets
            on behalf of a given cloud user (customer).*/
            DatacenterBroker broker0 = new DatacenterBrokerSimple("Broker0");

            Vm vm0 = createVm(broker0);
            this.vmList.add(vm0);
            broker0.submitVmList(vmList);

            /*Creates a cloudlet that represents an application to be run inside a VM.*/
            Cloudlet cloudlet0 = createCloudlet(broker0, vm0);
            this.cloudletList.add(cloudlet0);
            broker0.submitCloudletList(cloudletList);

            /*Starts the simulation and waits all cloudlets to be executed*/
            CloudSim.startSimulation();
            
            //Finishes the simulation
            CloudSim.stopSimulation();
            
            /*Prints results when the simulation is over
            (you can use your own code here to print what you want from this cloudlet list)*/
            List<Cloudlet> finishedCloudlets = broker0.getCloudletsFinishedList();
            CloudletsTableBuilderHelper.print(new TextTableBuilder(), finishedCloudlets);
            Log.printLine("Minimal Example finished!");
        } catch (Exception e) {
            Log.printFormattedLine("Unexpected errors happened: %s", e.getMessage());
        }
    }

    private DatacenterSimple createDatacenter(String name) {
        List<Host> hostList = new ArrayList<>();
        Host host0 = createHost();
        hostList.add(host0); 

        //Defines the characteristics of the data center
        String arch = "x86"; // system architecture of datacenter hosts
        String os = "Linux"; // operating system of datacenter hosts
        double time_zone = 10.0; // time zone where the datacenter is located
        double cost = 3.0; // the cost of using processing in this datacenter
        double costPerMem = 0.05; // the cost of using memory in this datacenter
        double costPerStorage = 0.001; // the cost of using storage in this datacenter
        double costPerBw = 0.0; // the cost of using bw in this datacenter
        LinkedList<FileStorage> storageList = new LinkedList<>(); // we are not adding SAN devices

        DatacenterCharacteristics characteristics = new DatacenterCharacteristicsSimple(
                arch, os, VMM, hostList, time_zone, cost, costPerMem,
                costPerStorage, costPerBw);

        return new DatacenterSimple(name, characteristics, 
                new VmAllocationPolicySimple(hostList), storageList, 0);
    }    

    private Host createHost() {
        int  mips = 1000; // capacity of each CPU core (in Million Instructions per Second)
        int  ram = 2048; // host memory (MB)
        long storage = 1000000; // host storage
        long bw = 10000;
        
        List<Pe> cpuCoresList = new ArrayList<>();
        /*Creates the Host's CPU cores and defines the provisioner
        used to allocate each core for requesting VMs.*/
        cpuCoresList.add(new PeSimple(0, new PeProvisionerSimple(mips)));
        
        return new HostSimple(numberOfCreatedHosts++,
                new ResourceProvisionerSimple<>(new Ram(ram)),
                new ResourceProvisionerSimple<>(new Bandwidth(bw)),
                storage, cpuCoresList,
                new VmSchedulerTimeShared(cpuCoresList));
    }

    private Vm createVm(DatacenterBroker broker) {
        double mips = 1000;
        long   storage = 10000; // vm image size (MB)
        int    ram = 512; // vm memory (MB)
        long   bw = 1000; // vm bandwidth 
        int    pesNumber = 1; // number of CPU cores
        
        return new VmSimple(numberOfCreatedVms++, 
                broker.getId(), mips, pesNumber, ram, bw, storage,
                VMM, new CloudletSchedulerTimeShared());
    }

    private Cloudlet createCloudlet(DatacenterBroker broker, Vm vm) {
        long length = 400000; //in Million Structions (MI)
        long fileSize = 300; //Size (in bytes) before execution
        long outputSize = 300; //Size (in bytes) after execution
        int  numberOfCpuCores = vm.getNumberOfPes(); //cloudlet will use all the VM's CPU cores
        
        //Defines how CPU, RAM and Bandwidth resources are used
        //Sets the same utilization model for all these resources.
        UtilizationModel utilization = new UtilizationModelFull();
        
        Cloudlet cloudlet
                = new CloudletSimple(
                        numberOfCreatedCloudlets++, length, numberOfCpuCores, 
                        fileSize, outputSize, 
                        utilization, utilization, utilization);
        cloudlet.setUserId(broker.getId());
        cloudlet.setVmId(vm.getId());
        
        return cloudlet;
    }

}
