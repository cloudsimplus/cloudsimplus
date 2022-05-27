/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.examples.network;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.network.topologies.BriteNetworkTopology;
import org.cloudbus.cloudsim.network.topologies.NetworkTopology;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple example showing how to create a Datacenter with 1 host and a network
 * topology, running 1 cloudlet on it. Here, instead of using a BRITE file
 * describing the links, they are just inserted in the code.
 *
 * <p>The example defines a given latency for communication with the created broker.
 * Since the broker receives multiple messages (such as for VM and Cloudlet creation),
 * cloudlets start running just after such multiple messages are received.
 * If 3 messages are sent to the broker for starting a Cloudlet,
 * the total delay is the network latency multiplied by 3.</p>
 */
public class NetworkExample4 {
    private static final int VM_PES = 1;

    /** In Megabits/s. */
    private static final double NETWORK_BW = 10.0;

    /** In seconds. */
    private static final double NETWORK_LATENCY = 10.0;

    private final DatacenterBroker broker;
    private final Datacenter datacenter0;

    private final List<Cloudlet> cloudletList;
    private final List<Vm> vmList;
    private final CloudSim simulation;

    public static void main(String[] args) {
        new NetworkExample4();
    }

    private NetworkExample4() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());

        vmList = new ArrayList<>();
        cloudletList = new ArrayList<>();

        simulation = new CloudSim();
        datacenter0 = createDatacenter();
        broker = new DatacenterBrokerSimple(simulation);
        configureNetwork();

        createAndSubmitVms();
        createAndSubmitCloudlets();

        simulation.start();

        new CloudletsTableBuilder(broker.getCloudletFinishedList()).build();
        System.out.println(getClass().getSimpleName() + " finished!");
    }

    private void configureNetwork() {
        //Configure network by mapping CloudSim entities to BRITE entities
        final NetworkTopology networkTopology = new BriteNetworkTopology();
        simulation.setNetworkTopology(networkTopology);
        networkTopology.addLink(datacenter0, broker, NETWORK_BW, NETWORK_LATENCY);
    }

    private void createAndSubmitCloudlets() {
        final long length = 100_000;
        final long fileSize = 1000; // in bytes
        final long outputSize = 1000;  // in bytes
        final UtilizationModel utilizationModel = new UtilizationModelFull();

        final Cloudlet cloudlet1 =
            new CloudletSimple(length, VM_PES)
                .setFileSize(fileSize)
                .setOutputSize(outputSize)
                .setUtilizationModel(utilizationModel);

        cloudletList.add(cloudlet1);
        broker.submitCloudletList(cloudletList);
    }

    private void createAndSubmitVms() {
        final int mips = 1000;
        final long size = 10000; //image size (Megabyte)
        final int ram = 512; //vm memory (Megabyte)
        final long bw = 1000;

        final Vm vm1 = new VmSimple(mips, VM_PES)
            .setRam(ram).setBw(bw).setSize(size);

        vmList.add(vm1);
        broker.submitVmList(vmList);
    }

    private Datacenter createDatacenter() {
        List<Host> hostList = new ArrayList<>();
        List<Pe> peList = new ArrayList<>();

        final long mips = 1000;
        peList.add(new PeSimple(mips));

        long ram = 2048; //host memory (Megabyte)
        long storage = 1000000; //host storage (Megabyte)
        long bw = 10000; //Megabits/s

        final Host host = new HostSimple(ram, bw, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());
        hostList.add(host);

        final Datacenter dc = new DatacenterSimple(simulation, hostList);
        dc.getCharacteristics()
            .setCostPerSecond(3.0)
            .setCostPerMem(0.05)
            .setCostPerStorage(0.1)
            .setCostPerBw(0.1);

        return dc;
    }
}
