/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.examples.network;

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
import org.cloudbus.cloudsim.network.topologies.BriteNetworkTopology;
import org.cloudbus.cloudsim.network.topologies.NetworkTopology;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.SanStorage;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple example showing how to create a Datacenter with 1 host and a network
 * topology, running 1 cloudlet on it.
 * There is just one VM with a single PE of 250 MIPS.
 * The Cloudlet requires 1 PE and has a length of 40000 MI.
 * This way, the Cloudlet will take 160 seconds to finish (40000/250).
 *
 * <p>The Cloudlet is not requiring any files from a {@link SanStorage},
 * but since a network topology is defined from the file topology.brite,
 * communication delay between network elements is simulated,
 * causing the Cloudlet to start executing just after a few seconds.</p>
 */
public class NetworkExample1 {
    private static final String NETWORK_TOPOLOGY_FILE = "topology.brite";
    private static final int VM_PES = 1;
    private final Datacenter datacenter0;
    private final DatacenterBroker broker;

    private List<Cloudlet> cloudletList;
    private List<Vm> vmlist;
    private CloudSim simulation;

    /**
     * Starts the example.
     *
     * @param args
     */
    public static void main(String[] args) {
        new NetworkExample1();
    }

    private NetworkExample1() {
        //Enables just some level of log messages.
        //Make sure to import org.cloudsimplus.util.Log;
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());

        vmlist = new ArrayList<>();
        cloudletList = new ArrayList<>();
        simulation = new CloudSim();

        datacenter0 = createDatacenter();
        broker = createBroker();
        configureNetwork();

        createAndSubmitVms(broker);
        createAndSubmitCloudlets(broker);

        simulation.start();

        List<Cloudlet> newList = broker.getCloudletFinishedList();
        new CloudletsTableBuilder(newList).build();
        System.out.println(getClass().getSimpleName() + " finished!");
    }

    private void configureNetwork() {
        //load the network topology file
        NetworkTopology networkTopology = BriteNetworkTopology.getInstance(NETWORK_TOPOLOGY_FILE);
        simulation.setNetworkTopology(networkTopology);

        //maps CloudSim entities to BRITE entities
        //Datacenter will correspond to BRITE node 0
        int briteNode = 0;
        networkTopology.mapNode(datacenter0.getId(), briteNode);

        //Broker will correspond to BRITE node 3
        briteNode = 3;
        networkTopology.mapNode(broker.getId(), briteNode);
    }

    private void createAndSubmitCloudlets(DatacenterBroker broker) {
        final long length = 40000;
        final long fileSize = 300;
        final long outputSize = 300;
        //The RAM, CPU and Bandwidth UtilizationModel.
        final UtilizationModel utilizationModel = new UtilizationModelFull();

        Cloudlet cloudlet1 =
            new CloudletSimple(length, VM_PES)
                .setFileSize(fileSize)
                .setOutputSize(outputSize)
                .setUtilizationModel(utilizationModel);

        //add the cloudlet to the list
        cloudletList.add(cloudlet1);

        //submit cloudlet list to the broker
        broker.submitCloudletList(cloudletList);
    }

    private void createAndSubmitVms(DatacenterBroker broker) {
        final int mips = 250;
        final long size = 10000; //image size (Megabyte)
        final int ram = 512; //vm memory (Megabyte)
        final long bw = 1000; //in Megabits/s
        Vm vm1 = new VmSimple(mips, VM_PES)
                .setRam(ram).setBw(bw).setSize(size)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());

        vmlist.add(vm1);

        broker.submitVmList(vmlist);
    }

    private Datacenter createDatacenter() {
        List<Host> hostList = new ArrayList<>();
        List<Pe> peList = new ArrayList<>();

        final long mips = 1000;
        peList.add(new PeSimple(mips, new PeProvisionerSimple()));

        final long ram = 2048; // in Megabytes
        final long storage = 1000000; // in Megabytes
        final long bw = 10000; //in Megabits/s

        Host host = new HostSimple(ram, bw, storage, peList);
        hostList.add(host);

        return new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
    }

    /**
     * Creates a DatacenterBroker.
     * We strongly encourage users to develop their own broker policies,
     * to submit vms and cloudlets according to the specific rules of the simulated scenario.
     */
    private DatacenterBroker createBroker() {
        return new DatacenterBrokerSimple(simulation);
    }
}
