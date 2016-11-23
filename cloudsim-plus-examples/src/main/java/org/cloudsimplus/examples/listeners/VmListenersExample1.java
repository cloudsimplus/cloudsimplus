package org.cloudsimplus.examples.listeners;

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
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSimple;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.schedulers.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.DatacenterToVmEventInfo;
import org.cloudsimplus.listeners.HostToVmEventInfo;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Ram;

/**
 * A simple example showing how to create a data center with 1 host and run 1
 * cloudlet on it, and receive notifications when a Host is allocated or
 * deallocated to each Vm. It is also show how to be notified when
 * no suitable host is found to place a VM.
 *
 * The example uses the new Vm listeners to get these
 * notifications while the simulation is running.
 *
 * @see Vm#setOnHostAllocationListener(EventListener)
 * @see Vm#setOnHostDeallocationListener(EventListener)
 * @see Vm#setOnVmCreationFailureListener(EventListener)
 * @see EventListener
 *
 * @author Manoel Campos da Silva Filho
 */
public class VmListenersExample1 {
    /**
     * Number of Processor Elements (CPU Cores) of each Host.
     */
    private static final int HOST_PES_NUMBER = 1;

    /**
     * Number of Processor Elements (CPU Cores) of each VM and cloudlet.
     */
    private static final int VM_PES_NUMBER = HOST_PES_NUMBER;

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
        Log.printFormattedLine("Starting %s ...", VmListenersExample1.class.getSimpleName());
        try {
            new VmListenersExample1();
            Log.printFormattedLine("%s finished!", VmListenersExample1.class.getSimpleName());
        } catch (Exception e) {
            Log.printFormattedLine("Unwanted errors happened: %s", e.getMessage());
        }
    }

    /**
     * Default constructor that builds and starts the simulation.
     */
    public VmListenersExample1() {
        int numberOfUsers = 1; // number of cloud users/customers (brokers)
        Calendar calendar = Calendar.getInstance();
        CloudSim.init(numberOfUsers, calendar);

        this.hostList = new ArrayList<>();
        this.vmList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();
        this.datacenter = createDatacenter("Datacenter_0");
        this.broker = new DatacenterBrokerSimple("Broker");

        createAndSubmitVms();
        createAndSubmitCloudlets();

        runSimulationAndPrintResults();
    }

    private void runSimulationAndPrintResults() {
        CloudSim.startSimulation();
        CloudSim.stopSimulation();

        List<Cloudlet> finishedCloudlets = broker.getCloudletsFinishedList();
        new CloudletsTableBuilderHelper(finishedCloudlets).build();
    }

    /**
     * Create cloudlets and submit them to the broker.
     */
    private void createAndSubmitCloudlets() {
        Cloudlet cloudlet0 = createCloudlet(0, vmList.get(0));
        this.cloudletList.add(cloudlet0);

        /*This cloudlet will not be run because vm1 will not be placed
        due to lack of a suitable host.*/
        Cloudlet cloudlet1 = createCloudlet(1, vmList.get(1));
        this.cloudletList.add(cloudlet1);

        this.broker.submitCloudletList(cloudletList);
    }

    /**
     * Creates VMs and submit them to the broker.
     */
    private void createAndSubmitVms() {
        Vm vm0 = createVm(0);

        /*Sets the listener to intercept allocation of a Host to the Vm.*/
        vm0.setOnHostAllocationListener(new EventListener<HostToVmEventInfo>() {
            @Override
            public void update(HostToVmEventInfo evt) {
                Log.printFormattedLine(
                        "\n\t#EventListener: Host %d allocated to Vm %d at time %.2f\n",
                        evt.getHost().getId(), evt.getVm().getId(), evt.getTime());
            }
        });

        /*Sets the listener to intercept deallocation of a Host for the Vm.*/
        vm0.setOnHostDeallocationListener(new EventListener<HostToVmEventInfo>() {
            @Override
            public void update(HostToVmEventInfo evt) {
                Log.printFormattedLine(
                        "\n\t#EventListener: Vm %d moved/removed from Host %d at time %.2f\n",
                        evt.getVm().getId(), evt.getHost().getId(), evt.getTime());
            }
        });

        /*This VM will not be place due to lack of a suitable host.*/
        Vm vm1 = createVm(1);
        vm1.setOnVmCreationFailureListener(new EventListener<DatacenterToVmEventInfo>() {
            @Override
            public void update(DatacenterToVmEventInfo evt) {
                Log.printFormattedLine(
                        "\n\t#EventListener: Vm %d could not be placed into any host of Datacenter %d at time %.2f due to lack of a host with enough resources.\n",
                        evt.getVm().getId(), evt.getDatacenter().getId(), evt.getTime());
            }
        });

        this.vmList.add(vm0);
        this.vmList.add(vm1);
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
     * @return the created cloudlet
     */
    private Cloudlet createCloudlet(int id, Vm vm) {
        long length = 400000;  //in MI (Million Instructions)
        long fileSize = 300;
        long outputSize = 300;
        UtilizationModel utilizationModel = new UtilizationModelFull();
        Cloudlet cloudlet
                = new CloudletSimple(id, length, VM_PES_NUMBER)
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

        return new DatacenterSimple(name, characteristics, new VmAllocationPolicySimple());
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
            .setVmScheduler(new VmSchedulerTimeShared(peList));

    }
}
