package org.cloudbus.cloudsim.examples;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * A minimal example showing how to create a data center with 2 {@link Host}.
 * It creates 2 {@link Vm} and runs 2
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

    /**
     * Starts the example.
     * @param args
     */
    public static void main(String[] args) {
        new CloudSimExample0();
    }

    /**
     * Default constructor that builds the simulation.
     */
    public CloudSimExample0() {
        //Enables just some level of log messages.//Make sure to import org.cloudsimplus.util.Log; //Log.setLevel(ch.qos.logback.classic.Level.WARN);
        System.out.println("Starting " + getClass().getSimpleName());
        this.simulation = new CloudSim();

        Datacenter datacenter0 = createDatacenter();

        /*Creates a Broker accountable for submission of VMs and Cloudlets
        on behalf of a given cloud user (customer).*/
        DatacenterBroker broker0 = new DatacenterBrokerSimple(simulation);

        this.vmList = new ArrayList<>(VMS);
        this.cloudletList = new ArrayList<>(VMS);

        /*
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
        List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();
        System.out.println(getClass().getSimpleName() + " finished!");
    }

    private DatacenterSimple createDatacenter() {
        List<Host> hostList = new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }

        return new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
    }

    private Host createHost() {
        final long mips = 1000; // capacity of each CPU core (in Million Instructions per Second)
        final long ram = 2048; // host memory (Megabyte)
        final long storage = 1000000; // host storage (Megabyte)
        final long bw = 10000; //in Megabits/s

        List<Pe> pesList = new ArrayList<>(); //List of CPU cores

        /*Creates the Host's CPU cores and defines the provisioner
        used to allocate each core for requesting VMs.*/
        for (int i = 0; i < 2; i++) {
            pesList.add(new PeSimple(mips, new PeProvisionerSimple()));
        }

        return new HostSimple(ram, bw, storage, pesList)
                .setRamProvisioner(new ResourceProvisionerSimple())
                .setBwProvisioner(new ResourceProvisionerSimple())
                .setVmScheduler(new VmSchedulerTimeShared());
    }

    private Vm createVm(DatacenterBroker broker) {
        final long   mips = 1000;
        final long   storage = 10000; // vm image size (Megabyte)
        final int    ram = 512; // vm memory (Megabyte)
        final long   bw = 1000; // vm bandwidth (Megabits/s)
        final long   pesNumber = 2; // number of CPU cores

        return new VmSimple(vmList.size(), mips, pesNumber)
                .setRam(ram)
                .setBw(bw)
                .setSize(storage)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());
    }

    private Cloudlet createCloudlet(DatacenterBroker broker, Vm vm) {
        final long length = 10000; //in Million Instruction (MI)
        final long fileSize = 300; //Size (in bytes) before execution
        final long outputSize = 300; //Size (in bytes) after execution
        final int  numberOfCpuCores = 2; //cloudlet will use all the VM's CPU cores

        //Defines how CPU, RAM and Bandwidth resources are used
        //Sets the same utilization model for all these resources.
        UtilizationModel utilization = new UtilizationModelFull();

        Cloudlet cloudlet
                = new CloudletSimple(
                        cloudletList.size(), length, numberOfCpuCores)
                        .setFileSize(fileSize)
                        .setOutputSize(outputSize)
                        .setUtilizationModel(utilization)
                        .setVm(vm);

        return cloudlet;
    }

}
