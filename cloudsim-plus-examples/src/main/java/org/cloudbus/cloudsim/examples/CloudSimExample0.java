package org.cloudbus.cloudsim.examples;

import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudsimplus.util.tablebuilder.CloudletsTableBuilderHelper;
import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Ram;

/**
 * A minimal example showing how to create a data center with 2 {@link Host} with 2 {@link Vm} run 2
 * {@link Cloudlet}, inside each Vm. Each created Vm will use a {@link CloudletSchedulerTimeShared}
 * scheduler to allow sharing CPU time between its Cloudlets.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class CloudSimExample0 {
    private static final int HOSTS = 2;
    private static final int VMS = 2;
    private static final int CLOUDLETS_PER_VM = 2;

    private final CloudSim simulation;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;

    private int numberOfCreatedHosts = 0;

    /**
     * Starts the simulation.
     * @param args
     */
    public static void main(String[] args) {
        new CloudSimExample0();
    }

    /**
     * Default constructor that builds the simulation.
     */
    public CloudSimExample0() {
        Log.printLine("Starting CloudSimExample0 ...");
        this.simulation = new CloudSim();

        Datacenter datacenter0 = createDatacenter();

        /*Creates a Broker accountable for submission of VMs and Cloudlets
        on behalf of a given cloud user (customer).*/
        DatacenterBroker broker0 = new DatacenterBrokerSimple(simulation);

        this.vmList = new ArrayList<>(VMS);
        this.cloudletList = new ArrayList<>(VMS);

        /**
         * Creates VMs and one Cloudlet for each VM.
         */
        for (int i = 0; i < VMS; i++) {
            Vm vm = createVm(broker0);
            this.vmList.add(vm);
            for (int j = 0; j < CLOUDLETS_PER_VM; j++) {
                /*Creates a Cloudlet that represents an application to be run inside a VM.*/
                Cloudlet cloudlet = createCloudlet(broker0, vm);
                this.cloudletList.add(cloudlet);
            }
        }
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        /* Starts the simulation and waits all cloudlets to be executed. */
        simulation.start();

        /*Prints results when the simulation is over
        (you can use your own code here to print what you want from this cloudlet list)*/
        List<Cloudlet> finishedCloudlets = broker0.getCloudletsFinishedList();
        new CloudletsTableBuilderHelper(finishedCloudlets).build();
        Log.printLine("CloudSimExample0 finished!");
    }

    private DatacenterSimple createDatacenter() {
        List<Host> hostList = new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }

        //Defines the characteristics of the data center
        double cost = 3.0; // the cost of using processing in this Datacenter
        double costPerMem = 0.05; // the cost of using memory in this Datacenter
        double costPerStorage = 0.001; // the cost of using storage in this Datacenter
        double costPerBw = 0.0; // the cost of using bw in this Datacenter

        DatacenterCharacteristics characteristics =
            new DatacenterCharacteristicsSimple(hostList)
                .setCostPerSecond(cost)
                .setCostPerMem(costPerMem)
                .setCostPerStorage(costPerStorage)
                .setCostPerBw(costPerBw);

        return new DatacenterSimple(simulation, characteristics, new VmAllocationPolicySimple());
    }

    private Host createHost() {
        int  mips = 1000; // capacity of each CPU core (in Million Instructions per Second)
        long  ram = 2048; // host memory (MB)
        long storage = 1000000; // host storage (MB)
        long bw = 10000; //in Megabits/s

        List<Pe> pesList = new ArrayList<>(); //List of CPU cores

        /*Creates the Host's CPU cores and defines the provisioner
        used to allocate each core for requesting VMs.*/
        for (int i = 0; i < 2; i++) {
            pesList.add(new PeSimple(i, new PeProvisionerSimple(mips)));
        }

        return new HostSimple(numberOfCreatedHosts++, storage, pesList)
                .setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
                .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(bw)))
                .setVmScheduler(new VmSchedulerTimeShared());
    }

    private Vm createVm(DatacenterBroker broker) {
        double mips = 1000;
        long   storage = 10000; // vm image size (MB)
        int    ram = 512; // vm memory (MB)
        long   bw = 1000; // vm bandwidth (Megabits/s)
        int    pesNumber = 2; // number of CPU cores

        return new VmSimple(vmList.size(), mips, pesNumber)
                .setBroker(broker)
                .setRam(ram)
                .setBw(bw)
                .setSize(storage)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());
    }

    private Cloudlet createCloudlet(DatacenterBroker broker, Vm vm) {
        long length = 10000; //in Million Structions (MI)
        long fileSize = 300; //Size (in bytes) before execution
        long outputSize = 300; //Size (in bytes) after execution
        int  numberOfCpuCores = vm.getNumberOfPes(); //cloudlet will use all the VM's CPU cores

        //Defines how CPU, RAM and Bandwidth resources are used
        //Sets the same utilization model for all these resources.
        UtilizationModel utilization = new UtilizationModelFull();

        Cloudlet cloudlet
                = new CloudletSimple(
                        cloudletList.size(), length, numberOfCpuCores)
                        .setFileSize(fileSize)
                        .setOutputSize(outputSize)
                        .setUtilizationModel(utilization)
                        .setBroker(broker)
                        .setVm(vm);

        return cloudlet;
    }

}
