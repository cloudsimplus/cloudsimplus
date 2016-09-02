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
import org.cloudbus.cloudsim.schedulers.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Ram;

/**
 * An example that execute exactly as the 
 * {@link SharingHostPEsUsingVmSchedulerTimeShared},
 * however the host uses an {@link VmSchedulerSpaceShared},
 * showing how half of the VMs will fail to be placed due to
 * lack of available Host PEs.
 * 
 * Considering this scenario where we have 1 hosts with 2 PEs
 * and 4 VMs each one requesting these 2 PEs.
 * This CloudSim paper below states in the section 3.2 that when using a 
 * VmSchedulerSpaceShared, one VM will execute first and after it finishes,
 * the other one will start executing.
 * However, the DatacenterBroker fails in allocating a host for 2 VMs
 * due to lack of available PEs. 
 * Once each one of the 2 host PEs has a capacity of 1000 MIPS, even defining
 * that each one of the 4 VMs requires just 500 MIPS, the allocation
 * of 2 VMs fails.
 * 
 * <ul>
 *  <li><a href="http://arxiv.org/abs/0903.2525">R. N. Calheiros, R. Ranjan, C. A. F. De Rose, and R. Buyya, 
 * “CloudSim: A Novel Framework for Modeling and Simulation of Cloud Computing 
 * Infrastructures and Services,” arXiv preprint arXiv:0903.2525. arXiv, p. 9, 2009</a>.</li>
 * </ul>
 * 
 * 
 * @author Manoel Campos da Silva Filho
 */
public class SharingHostPEsUsingVmSchedulerSpaceShared {
    /**
     * Capacity of each CPU core (in Million Instructions per Second).
     */
    private static final double HOST_MIPS = 1000;
    /**
     * Number of processor elements (CPU cores) of each host.
     */
    private static final int HOST_PES_NUM = 2;
    
    /**
     * The total MIPS capacity across all the Host PEs.
     */
    private static final double HOST_TOTAL_MIPS_CAPACITY = HOST_MIPS*HOST_PES_NUM;
    
    /**
     * The length of each created cloudlet in Million Instructions (MI).
     */
    private static final long CLOUDLET_LENGTH = 10000;
    
    /**
     * Number of VMs to create.
     */
    private static final int NUMBER_OF_VMS = HOST_PES_NUM*2;

    private static final double VM_MIPS = HOST_TOTAL_MIPS_CAPACITY/NUMBER_OF_VMS;
    
    
    /**
     *  Virtual Machine Monitor name.
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
        new SharingHostPEsUsingVmSchedulerSpaceShared();
    }

    /**
     * Default constructor where the simulation is built.
     */
    public SharingHostPEsUsingVmSchedulerSpaceShared() {
        Log.printFormattedLine("Starting %s Example ...", getClass().getSimpleName());
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

            createAndSubmitVmsAndCloudlets(broker0);

            /*Starts the simulation and waits all cloudlets to be executed*/
            CloudSim.startSimulation();
            
            //Finishes the simulation
            CloudSim.stopSimulation();
            
            /*Prints results when the simulation is over
            (you can use your own code here to print what you want from this cloudlet list)*/
            List<Cloudlet> finishedCloudlets = broker0.getCloudletsFinishedList();
            CloudletsTableBuilderHelper.print(new TextTableBuilder(), finishedCloudlets);
            Log.printFormattedLine("%s Example finished!", getClass().getSimpleName());
        } catch (Exception e) {
            Log.printFormattedLine("Unexpected errors happened: %s", e.getMessage());
        }
    }

    private void createAndSubmitVmsAndCloudlets(DatacenterBroker broker0) {
        for(int i = 0; i < NUMBER_OF_VMS; i++){
            Vm vm = createVm(broker0, VM_MIPS, 1);
            this.vmList.add(vm);
            
            /*Creates a cloudlet that represents an application to be run inside a VM.*/
            Cloudlet cloudlet = createCloudlet(broker0, vm);
            this.cloudletList.add(cloudlet);
        }
        
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);
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
        int  ram = 2048; // host memory (MB)
        long storage = 1000000; // host storage
        long bw = 10000;
        
        List<Pe> cpuCoresList = new ArrayList<>();
        /*Creates the Host's CPU cores and defines the provisioner
        used to allocate each core for requesting VMs.*/
        for(int i = 0; i < HOST_PES_NUM; i++){
            cpuCoresList.add(new PeSimple(i, new PeProvisionerSimple(HOST_MIPS)));
        }
        
        return new HostSimple(numberOfCreatedHosts++,
                new ResourceProvisionerSimple<>(new Ram(ram)),
                new ResourceProvisionerSimple<>(new Bandwidth(bw)),
                storage, cpuCoresList,
                new VmSchedulerSpaceShared(cpuCoresList));
    }

    private Vm createVm(DatacenterBroker broker, double mips, int pesNumber) {
        long storage = 10000; // vm image size (MB)
        int  ram = 512; // vm memory (MB)
        long bw = 1000; // vm bandwidth 
        
        return new VmSimple(numberOfCreatedVms++, 
                broker.getId(), mips, pesNumber, ram, bw, storage,
                VMM, new CloudletSchedulerTimeShared());
    }

    private Cloudlet createCloudlet(DatacenterBroker broker, Vm vm) {
        long fileSize = 300; //Size (in bytes) before execution
        long outputSize = 300; //Size (in bytes) after execution
        int  numberOfCpuCores = vm.getNumberOfPes(); //cloudlet will use all the VM's CPU cores
        
        //Defines how CPU, RAM and Bandwidth resources are used
        //Sets the same utilization model for all these resources.
        UtilizationModel utilization = new UtilizationModelFull();
        
        Cloudlet cloudlet
                = new CloudletSimple(
                        numberOfCreatedCloudlets++, CLOUDLET_LENGTH, numberOfCpuCores, 
                        fileSize, outputSize, 
                        utilization, utilization, utilization);
        cloudlet.setUserId(broker.getId());
        cloudlet.setVmId(vm.getId());
        
        return cloudlet;
    }

}
