package org.cloudbus.cloudsim.examples;

/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */
import org.cloudsimplus.util.tablebuilder.CloudletsTableBuilderHelper;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.VmSchedulerSpaceShared;

/**
 * An example showing how to delay the submission of cloudlets.
 * Even there is enough resources to run all cloudlets simultaneously,
 * the example delays the creation and execution of some cloudlets inside a VM,
 * simulating the dynamic arrival of cloudlets.
 * It first creates a set of cloudlets without delay and
 * another set of cloudlets all with the same submission delay.
 *
 * @author Manoel Campos da Silva Filho
 */
public class DynamicCloudletsArrival1 {
    /**
     * Number of Processor Elements (CPU Cores) of each Host.
     */
    private static final int HOST_PES_NUMBER = 4;

    /**
     * Number of Processor Elements (CPU Cores) of each VM and cloudlet.
     */
    private static final int VM_PES_NUMBER = HOST_PES_NUMBER;

    /**
     * Number of Cloudlets to create simultaneously.
     * Other cloudlets will be enqueued.
     */
    private static final int NUMBER_OF_CLOUDLETS = VM_PES_NUMBER*2;

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
     * Starts the example execution, calling the class constructor\
     * to build and run the simulation.
     *
     * @param args command line parameters
     */
    public static void main(String[] args) {
        try {
            new DynamicCloudletsArrival1();
        } catch (Exception e) {
            Log.printFormattedLine("Simulation finished due to unexpected error: %s", e);
        }
    }

    /**
     * Default constructor that builds and starts the simulation.
     */
    public DynamicCloudletsArrival1() {
        int numberOfUsers = 1; // number of cloud users/customers (brokers)
        Calendar calendar = Calendar.getInstance();
        Log.printFormattedLine("Starting %s ...", getClass().getSimpleName());
        CloudSim.init(numberOfUsers, calendar);

        this.hostList = new ArrayList<>();
        this.vmList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();
        this.datacenter = createDatacenter("Datacenter0");
        this.broker = new DatacenterBrokerSimple("Broker0");

        Vm vm = createAndSubmitVmAndCloudlets();

        /*Defines a delay of 5 seconds and creates another group of cloudlets
        that will start executing inside a VM only after this delay expires.*/
        double submissionDelay = 5;
        //createAndSubmitCloudlets(vm, submissionDelay);

        runSimulationAndPrintResults();
        Log.printFormattedLine("%s finished!", getClass().getSimpleName());
    }

    private void runSimulationAndPrintResults() {
        CloudSim.startSimulation();
        CloudSim.stopSimulation();

        List<Cloudlet> cloudlets = broker.getCloudletsFinishedList();
        new CloudletsTableBuilderHelper(cloudlets).build();
    }

    /**
     * Creates cloudlets and submit them to the broker, applying
     * a submission delay for each one (simulating the dynamic cloudlet arrival).
     *
     * @param vm Vm to run the cloudlets to be created
     * @param submissionDelay the delay the broker has to include when submitting the Cloudlets
     *
     * @see #createCloudlet(int, Vm, DatacenterBroker)
     */
    private void createAndSubmitCloudlets(Vm vm, double submissionDelay) {
        int cloudletId = cloudletList.size();
        List<Cloudlet> list = new ArrayList<>(NUMBER_OF_CLOUDLETS);
        for(int i = 0; i < NUMBER_OF_CLOUDLETS; i++){
            Cloudlet cloudlet = createCloudlet(cloudletId++, vm, broker);
            list.add(cloudlet);
        }

        broker.submitCloudletList(list, submissionDelay);
        cloudletList.addAll(list);
    }

    /**
     * Creates one Vm and a group of cloudlets to run inside it,
     * and submit the Vm and its cloudlets to the broker.
     * @return the created VM
     *
     * @see #createVm(int, org.cloudbus.cloudsim.brokers.DatacenterBroker)
     */
    private Vm createAndSubmitVmAndCloudlets() {
        List<Vm> list = new ArrayList<>();
        Vm vm = createVm(this.vmList.size(), broker);
        list.add(vm);

        broker.submitVmList(list);
        this.vmList.addAll(list);

        //Submit cloudlets without delay
        createAndSubmitCloudlets(vm, 0);
        return vm;
    }

    /**
     * Creates a VM with pre-defined configuration.
     *
     * @param id the VM id
     * @param broker the broker that will be submit the VM
     * @return the created VM
     *
     */
    private Vm createVm(int id, DatacenterBroker broker) {
        int mips = 1000;
        long size = 10000; // image size (MB)
        int ram = 512; // vm memory (MB)
        long bw = 1000;
        Vm vm = new VmSimple(id, mips, VM_PES_NUMBER)
            .setRam(ram).setBw(bw).setSize(size)
            .setCloudletScheduler(new CloudletSchedulerTimeShared())
            .setBroker(broker);

        return vm;
    }

    /**
     * Creates a cloudlet with pre-defined configuration.
     *
     * @param id Cloudlet id
     * @param vm vm to run the cloudlet
     * @param broker the broker that will submit the cloudlets
     * @return the created cloudlet
     */
    private Cloudlet createCloudlet(int id, Vm vm, DatacenterBroker broker) {
        long fileSize = 300;
        long outputSize = 300;
        long length = 10000; //in number of Million Instructions (MI)
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();
        Cloudlet cloudlet = new CloudletSimple(id, length, pesNumber)
            .setCloudletFileSize(fileSize)
            .setCloudletOutputSize(outputSize)
            .setUtilizationModel(utilizationModel)
            .setBroker(broker)
            .setVmId(vm.getId());

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

        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this datacenter
        double costPerBw = 0.0; // the cost of using bw in this resource

        DatacenterCharacteristics characteristics =
            new DatacenterCharacteristicsSimple(hostList)
                .setCostPerSecond(cost)
                .setCostPerMem(costPerMem)
                .setCostPerStorage(costPerStorage)
                .setCostPerBw(costPerBw);

        return new DatacenterSimple(name, characteristics, new VmAllocationPolicySimple(hostList));
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
        long ram = 2048; // host memory (MB)
        long storage = 1000000; // host storage (MB)
        long bw = 10000; //Megabits/s

       return new HostSimple(id, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
            .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(bw)))
            .setVmScheduler(new VmSchedulerSpaceShared(peList));
        
    }
}
