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
import java.util.List;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Ram;

/**
 * A simple example showing how to create a data center with 1 host, 1 vm and
 * run 2 cloudlets on it that will compete for the VM's CPU.
 * The VM's CPU is shared among the cloudlets using a time shared scheduler,
 * that performs a preemptive scheduling. By this way, all the 2 cloudlets
 * will have a quantum to use the VM's CPU. As they have the same length,
 * they start and finish together. Thus, the execution time is the double
 * compared to running each cloudlet in its own VM or using a space shared
 * scheduler.
 *
 * Based on the {@link CloudSimExample1}
 *
 * @author Manoel Campos da Silva Filho
 */
public class TwoCloudletsAndOneTimeSharedVm {
    private static List<Cloudlet> cloudletList;
    private static List<Vm> vmlist;
    private static CloudSim simulation;

    /**
     * Creates main() to run this example.
     *
     * @param args the args
     */
    public static void main(String[] args) {
        Log.printFormattedLine("Starting %s ...", TwoCloudletsAndOneTimeSharedVm.class.getSimpleName());

        // First step: Initialize the CloudSim package. It should be called before creating any entities.
        int num_user = 1; // number of cloud users
        boolean trace_flag = false; // trace events

        simulation = new CloudSim(num_user, trace_flag);

        // Second step: Create Datacenters
        // Datacenters are the resource providers in CloudSim. We need at
        // list one of them to run a CloudSim simulation
        Datacenter datacenter0 = createDatacenter();

        // Third step: Create Broker
        DatacenterBroker broker = createBroker();

        // Fourth step: Create one virtual machine
        vmlist = new ArrayList<>();

        // VM description
        int vmid = 0;
        int mips = 1000;
        long size = 10000; // image size (MB)
        int ram = 512; // vm memory (MB)
        long bw = 1000;
        int pesNumber = 1; // number of cpus

        Vm vm = new VmSimple(vmid, mips, pesNumber)
            .setRam(ram).setBw(bw).setSize(size)
            .setCloudletScheduler(new CloudletSchedulerTimeShared())
            .setBroker(broker);

        vmlist.add(vm);

        // submit vm list to the broker
        broker.submitVmList(vmlist);

        // Fifth step: Create one Cloudlet
        cloudletList = new ArrayList<>();

        // Cloudlet properties
        int id = -1;
        long length = 10000;
        long fileSize = 300;
        long outputSize = 300;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        Cloudlet cloudlet1 = new CloudletSimple(++id, length, pesNumber)
                    .setCloudletFileSize(fileSize)
                    .setCloudletOutputSize(outputSize)
                    .setUtilizationModel(utilizationModel)
                    .setBroker(broker)
                    .setVm(vm);
        cloudletList.add(cloudlet1);

        Cloudlet cloudlet2 = new CloudletSimple(++id, length, pesNumber)
            .setCloudletFileSize(fileSize)
            .setCloudletOutputSize(outputSize)
            .setUtilizationModel(utilizationModel)
            .setBroker(broker)
            .setVm(vm);
        cloudletList.add(cloudlet2);

        // submit cloudlet list to the broker
        broker.submitCloudletList(cloudletList);

        // Sixth step: Starts the simulation
        simulation.start();

        simulation.stop();

        //Final step: Print results when simulation is over
        List<Cloudlet> newList = broker.getCloudletsFinishedList();
        new CloudletsTableBuilderHelper(newList).build();
        Log.printFormattedLine("%s finished!", TwoCloudletsAndOneTimeSharedVm.class.getSimpleName());
    }

    /**
     * Creates the switches.
     *
     * @return the switches
     */
    private static Datacenter createDatacenter() {
        // Here are the steps needed to create a DatacenterSimple:
        // 1. We need to create a list to store
        // our machine
        List<Host> hostList = new ArrayList<>();

        // 2. A Machine contains one or more PEs or CPUs/Cores.
        // In this example, it will have only one core.
        List<Pe> peList = new ArrayList<>();

        int mips = 1000;

        // 3. Create PEs and add these into a list.
        peList.add(new PeSimple(0, new PeProvisionerSimple(mips)));

        // 4. Create HostSimple with its id and list of PEs and add them to the list
        // of machines
        int hostId = 0;
        long ram = 2048; // host memory (MB)
        long storage = 1000000; // host storage (MB)
        long bw = 10000; //Megabits/s

        Host host = new HostSimple(hostId, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
            .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(bw)))
            .setVmScheduler(new VmSchedulerTimeShared());

        hostList.add(host);


        // 5. Create a DatacenterCharacteristics object that stores the
        // properties of a data center: architecture, OS, list of
        // Machines, allocation policy: time- or space-shared, time zone
        // and its price (G$/Pe time unit).
        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this
        // resource
        double costPerBw = 0.0; // the cost of using bw in this resource

        DatacenterCharacteristics characteristics =
            new DatacenterCharacteristicsSimple(hostList)
                .setCostPerSecond(cost)
                .setCostPerMem(costPerMem)
                .setCostPerStorage(costPerStorage)
                .setCostPerBw(costPerBw);

        // 6. Finally, we need to create a DatacenterSimple object.
        return new DatacenterSimple(simulation, characteristics, new VmAllocationPolicySimple());
    }

    // We strongly encourage users to develop their own broker policies, to
    // submit vms and cloudlets according
    // to the specific rules of the simulated scenario
    /**
     * Creates the broker.
     *
     * @return the switches broker
     */
    private static DatacenterBroker createBroker() {
        return new DatacenterBrokerSimple(simulation);
    }

}
