package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.util.tablebuilder.CloudletsTableBuilderHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * An example showing how to terminate the simulation at a given time, before its natural end.
 * The example creates 4 Cloudlets that will run sequentially using a {@link CloudletSchedulerSpaceShared}.
 * However, at the middle of the simulation, after just 2 of these 4 Cloudlets have finished,
 * the simulation will be interrupeted, avoiding the other 2 Cloudlets to run.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 *
 * @see CloudSim#terminateAt(double)
 */
public class TerminateSimulationAtGivenTime {
    private static final int TIME_TO_FINISH_SIMULATION = 21;

    private final CloudSim simulation;
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
        new TerminateSimulationAtGivenTime();
    }

    /**
     * Default constructor that builds the simulation.
     */
    public TerminateSimulationAtGivenTime() {
        Log.printFormattedLine("Starting %s Example ...", getClass().getSimpleName());
        this.vmList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();
        this.simulation = new CloudSim();

        Datacenter datacenter0 = createDatacenter();

        /*Creates a Broker accountable for submission of VMs and Cloudlets
        on behalf of a given cloud user (customer).*/
        DatacenterBroker broker0 = new DatacenterBrokerSimple(simulation);

        Vm vm0 = createVm(broker0);
        this.vmList.add(vm0);
        broker0.submitVmList(vmList);

        for(int i = 0; i < 4; i++) {
            Cloudlet cloudlet = createCloudlet(broker0, vm0);
            this.cloudletList.add(cloudlet);
        }
        broker0.submitCloudletList(cloudletList);

        /* Schedule the simulation termination to the 21st second,
         * after the 2 first Cloudlets have finished.
         * By this way, the other 2 ones will not execute.
         * */
        simulation.terminateAt(TIME_TO_FINISH_SIMULATION);

        /* Starts the simulation and waits all cloudlets to be executed. */
        simulation.start();

        /*Prints results when the simulation is over
        (you can use your own code here to print what you want from this cloudlet list)*/
        List<Cloudlet> finishedCloudlets = broker0.getCloudletsFinishedList();
        new CloudletsTableBuilderHelper(finishedCloudlets).build();
        Log.printConcatLine(getClass().getSimpleName(), " Example finished!");
    }

    private DatacenterSimple createDatacenter() {
        List<Host> hostList = new ArrayList<>();
        Host host0 = createHost();
        hostList.add(host0);

        //Defines the characteristics of the data center
        double cost = 3.0; // the cost of using processing in this switches
        double costPerMem = 0.05; // the cost of using memory in this switches
        double costPerStorage = 0.001; // the cost of using storage in this switches
        double costPerBw = 0.0; // the cost of using bw in this switches

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
        pesList.add(new PeSimple(0, new PeProvisionerSimple(mips)));

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
        int    pesNumber = 1; // number of CPU cores

        return new VmSimple(numberOfCreatedVms++, mips, pesNumber)
                .setBroker(broker)
                .setRam(ram)
                .setBw(bw)
                .setSize(storage)
                .setCloudletScheduler(new CloudletSchedulerSpaceShared());
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
                        numberOfCreatedCloudlets++, length, numberOfCpuCores)
                        .setCloudletFileSize(fileSize)
                        .setCloudletOutputSize(outputSize)
                        .setUtilizationModel(utilization)
                        .setBroker(broker)
                        .setVm(vm);

        return cloudlet;
    }

}
