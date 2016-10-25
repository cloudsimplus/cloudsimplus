package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerCompletelyFair;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.CloudletsTableBuilderHelper;
import org.cloudbus.cloudsim.util.TextTableBuilder;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

import java.util.*;

/**
 * An example that uses a basic implementation of the
 * {@link CloudletSchedulerCompletelyFair},
 * the default scheduler used for most tasks on recent Linux Kernel.
 *
 * @author Manoel Campos da Silva Filho
 * @see <a href="https://en.wikipedia.org/wiki/Completely_Fair_Scheduler">Completely Fair Scheduler (CFS)</a>
 */
public class LinuxProcessScheduler {
    /**
     * Virtual Machine Monitor name.
     */
    private static final String VMM = "Xen";

    private final List<Cloudlet> cloudletList;
    private List<Vm> vmList;

    /**
     * Number of cloudlets created so far.
     */
    private int numberOfCreatedCloudlets = 0;
    /**
     * Number of VMs created so far.
     */
    private int numberOfCreatedVms = 0;
    /**
     * Number of hosts created so far.
     */
    private int numberOfCreatedHosts = 0;

    private static final int HOSTS_TO_CREATE = 1;
    private static final int VMS_TO_CREATE = 1;
    private static final int HOST_PES = 2;
    private static final int VM_PES = HOST_PES;

    private static final int CLOUDLETS_TO_CREATE = VM_PES*2;
    private static final int CLOUDLET_PES = 1;

    /**
     * Starts the simulation.
     * @param args
     */
    public static void main(String[] args) {
        new LinuxProcessScheduler();
    }

    /**
     * Default constructor where the simulation is built.
     */
    public LinuxProcessScheduler() {
        Log.printFormattedLine("Starting %s ...", getClass().getSimpleName());
        this.vmList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();
        int numberOfCloudUsers = 1;
        boolean traceEvents = false;

        CloudSim.init(numberOfCloudUsers, Calendar.getInstance(), traceEvents);
        Datacenter datacenter0 = createDatacenter("Datacenter0");
        DatacenterBrokerSimple broker0 = new DatacenterBrokerSimple("Broker0");

        createAndSubmitVms(broker0);
        createAndSubmitCloudlets(broker0);

        CloudSim.startSimulation();
        CloudSim.stopSimulation();

        List<Cloudlet> finishedCloudlets = broker0.getCloudletsFinishedList();
        CloudletsTableBuilderHelper.print(new TextTableBuilder(), finishedCloudlets);

        Log.printFormattedLine("\n%s finished!", getClass().getSimpleName());
    }

	private void createAndSubmitCloudlets(DatacenterBrokerSimple broker0) {
		for(int i = 0; i < CLOUDLETS_TO_CREATE; i++){
		    cloudletList.add(createCloudlet(broker0, CLOUDLET_PES));
		}
		broker0.submitCloudletList(cloudletList);
	}

	private void createAndSubmitVms(DatacenterBrokerSimple broker0) {
		vmList = new ArrayList<>(VMS_TO_CREATE);
		for(int i = 0; i < VMS_TO_CREATE; i++){
		    vmList.add(createVm(broker0, VM_PES));
		}
		broker0.submitVmList(vmList);
	}

    private DatacenterSimple createDatacenter(String name) {
        List<Host> hostList = new ArrayList<>();
        for(int i = 0; i < HOSTS_TO_CREATE; i++) {
            hostList.add(createHost());
        }

        //Defines the characteristics of the data center
        String arch = "x86"; // system architecture of datacenter hosts
        String os = "Linux"; // operating system of datacenter hosts
        double time_zone = 10.0; // time zone where the datacenter is located
        double cost = 3.0; // the cost of using processing in this datacenter
        double costPerMem = 0.05; // the cost of using memory in this datacenter
        double costPerStorage = 0.001; // the cost of using storage in this datacenter
        double costPerBw = 0.0; // the cost of using bw in this datacenter
        LinkedList<FileStorage> storageList = new LinkedList<>(); // we are not adding SAN devices

        DatacenterCharacteristics characteristics =
            new DatacenterCharacteristicsSimple(
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
        for(int i = 0; i < HOST_PES; i++)
            cpuCoresList.add(new PeSimple(i, new PeProvisionerSimple(mips)));

        return new HostSimple(numberOfCreatedHosts++,
                new ResourceProvisionerSimple<>(new Ram(ram)),
                new ResourceProvisionerSimple<>(new Bandwidth(bw)),
                storage, cpuCoresList,
                new VmSchedulerTimeShared(cpuCoresList));
    }

    private Vm createVm(DatacenterBroker broker, int pesNumber) {
        double mips = 1000;
        long   storage = 10000; // vm image size (MB)
        int    ram = 512; // vm memory (MB)
        long   bw = 1000; // vm bandwidth

        return new VmSimple(numberOfCreatedVms++,
                broker.getId(), mips, pesNumber, ram, bw, storage,
                VMM, new CloudletSchedulerCompletelyFair());
    }

    private Cloudlet createCloudlet(DatacenterBroker broker, int numberOfPes) {
        long length = 500000; //in Million Structions (MI)
        long fileSize = 300; //Size (in bytes) before execution
        long outputSize = 300; //Size (in bytes) after execution

        //Defines how CPU, RAM and Bandwidth resources are used
        //Sets the same utilization model for all these resources.
        UtilizationModel utilization = new UtilizationModelFull();

        Cloudlet cloudlet
                = new CloudletSimple(
                        numberOfCreatedCloudlets++, length, numberOfPes,
                        fileSize, outputSize,
                        utilization, utilization, utilization);
        cloudlet.setUserId(broker.getId());

        return cloudlet;
    }
}
