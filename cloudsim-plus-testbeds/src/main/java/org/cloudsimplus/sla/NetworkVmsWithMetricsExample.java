/**
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.sla;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.network.CloudletExecutionTask;
import org.cloudbus.cloudsim.cloudlets.network.CloudletReceiveTask;
import org.cloudbus.cloudsim.cloudlets.network.CloudletSendTask;
import org.cloudbus.cloudsim.cloudlets.network.CloudletTask;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.network.switches.EdgeSwitch;
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet;
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter;
import org.cloudbus.cloudsim.hosts.network.NetworkHost;
import org.cloudbus.cloudsim.network.switches.Switch;
import org.cloudbus.cloudsim.vms.network.NetworkVm;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

/**
 * An example showing how to create throughput metric in the NetworkDatacenter.
 *
 * @author raysaoliveira
 */
public class NetworkVmsWithMetricsExample {

    private final List<NetworkCloudlet> cloudletList;
    private final List<NetworkVm> vmlist;
    private final NetworkDatacenter datacenter0;
    private final CloudSim cloudsim;
    private int currentNetworkCloudletId = -1;

    private static final int CLOUDLET_EXECUTION_TASK_LENGTH = 4000;
    private static final long PACKET_DATA_LENGTH_IN_BYTES = 1000;
    private static final int NUMBER_OF_PACKETS_TO_SEND = 1;
    private static final long TASK_RAM = 100;


    public static void main(String[] args) {
        Log.printFormattedLine(" Starting... ");
        try {
            new NetworkVmsWithMetricsExample();
        } catch (IOException e) {
            Log.printFormattedLine("Simulation finished due to unexpected error: %s", e);
        }
    }

    private NetworkVmsWithMetricsExample() throws IOException {
        cloudsim = new CloudSim();

        datacenter0 = createDatacenter();

        DatacenterBroker broker0 = new DatacenterBrokerSimple(cloudsim);

        vmlist = createVM(broker0, 5);
        broker0.submitVmList(vmlist);

        cloudletList = createNetworkCloudlets(broker0);
        broker0.submitCloudletList(cloudletList);
        for (NetworkCloudlet cl : cloudletList) {
            System.out.println(" #\n -> cls: " + cl.getLength());
        }

        cloudsim.start();
        List<Cloudlet> newList = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(newList).build();

        final double throughput = getThroughput(datacenter0);
        System.out.println("\n-------------------------------------------");
        System.out.println("\t Throughput : " + throughput);

        Log.printFormattedLine("... finished!");
    }

    /**
     * Calculates the throughput of the NetworkDatacenter
     *
     * @param datacenter
     * @return throughput
     */
    private double getThroughput(NetworkDatacenter datacenter) {
        double downlinkBw = 0.0;
        for (Switch edgeSwitch : datacenter.getEdgeSwitch()) {
            downlinkBw = edgeSwitch.getDownlinkBandwidth() / cloudsim.clock();
        }

        return downlinkBw;
    }

    /**
     * Create NetworkVms
     *
     * @param broker the broker that acts on behalf of a user
     * @param vms number of VMs to create
     * @return list de vms
     */
    private List<NetworkVm> createVM(DatacenterBroker broker, int vms) {
        //Creates a container to store VMs. This list is passed to the broker later
        List<NetworkVm> list = new ArrayList<>(vms);
        //VM Parameters
        long size = 10000; //image size (MEGABYTE)
        int ram = 512; //vm memory (MEGABYTE)
        int mips = 1000;
        long bw = 1000;
        int pesNumber = 2; //number of cpus

        for (int i = 0; i < vms; i++) {
            NetworkVm vm = new NetworkVm(i, mips, pesNumber);
            vm.setRam(ram)
                    .setBw(bw)
                    .setSize(size)
                    .setCloudletScheduler(new CloudletSchedulerTimeShared());
            list.add(vm);
        }
        return list;
    }

    /**
     * Create NetworkCloudlets
     *
     * @param broker
     * @param cloudlets
     * @return
     */
    private NetworkCloudlet createCloudlet(DatacenterBroker broker, int cloudlets, NetworkVm vm) {
        long length = 4000;
        int pesNumber = 2;
        long fileSize = 300;
        long outputSize = 300;
        long memory = 512;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        NetworkCloudlet cloudlet = new NetworkCloudlet(++currentNetworkCloudletId, length, pesNumber);
        cloudlet
                .setMemory(memory)
                .setFileSize(fileSize)
                .setOutputSize(outputSize)
                .setUtilizationModel(utilizationModel);
        cloudlet.setVm(vm);

       return cloudlet;
    }

      /**
     * Creates a list of {@link NetworkCloudlet} that together represents the
     * distributed processes of a given fictitious application.
     *
     * @param broker broker to associate the NetworkCloudlets
     * @return the list of create NetworkCloudlets
     */
    private List<NetworkCloudlet> createNetworkCloudlets(DatacenterBroker broker) {
        final int numberOfCloudlets = 2;
        List<NetworkCloudlet> networkCloudletList = new ArrayList<>(numberOfCloudlets);

        for (int i = 0; i < numberOfCloudlets; i++) {
            networkCloudletList.add(createCloudlet(broker, numberOfCloudlets, vmlist.get(i)));
        }

        //NetworkCloudlet 0 Tasks
        addExecutionTask(networkCloudletList.get(0));
        addSendTask(networkCloudletList.get(0), networkCloudletList.get(1));

        //NetworkCloudlet 1 Tasks
        addReceiveTask(networkCloudletList.get(1), networkCloudletList.get(0));
        addExecutionTask(networkCloudletList.get(1));

        return networkCloudletList;
    }

    /**
     * Adds a send task to list of tasks of the given {@link NetworkCloudlet}.
     *
     * @param sourceCloudlet the {@link NetworkCloudlet} to add the task to
     * @param destinationCloudlet the destination where to send or from which is
     * expected to receive data
     */
    private void addSendTask(
            NetworkCloudlet sourceCloudlet,
            NetworkCloudlet destinationCloudlet) {
        CloudletSendTask task = new CloudletSendTask(sourceCloudlet.getTasks().size());
        task.setMemory(TASK_RAM);
        sourceCloudlet.addTask(task);
        for (int i = 0; i < NUMBER_OF_PACKETS_TO_SEND; i++) {
            task.addPacket(destinationCloudlet, PACKET_DATA_LENGTH_IN_BYTES);
        }
    }

    /**
     * Adds a receive task to list of tasks of the given
     * {@link NetworkCloudlet}.
     *
     * @param cloudlet the {@link NetworkCloudlet} that the task will belong to
     * @param sourceCloudlet the cloudlet where it is expected to receive
     * packets from
     */
    private void addReceiveTask(NetworkCloudlet cloudlet, NetworkCloudlet sourceCloudlet) {
        CloudletReceiveTask task = new CloudletReceiveTask(
                cloudlet.getTasks().size(), sourceCloudlet.getVm());
        task.setMemory(TASK_RAM);
        task.setNumberOfExpectedPacketsToReceive(NUMBER_OF_PACKETS_TO_SEND);
        cloudlet.addTask(task);
    }

    /**
     * Adds an execution task to list of tasks of the given
     * {@link NetworkCloudlet}.
     *
     * @param netCloudlet the {@link NetworkCloudlet} to add the task
     */
    private static void addExecutionTask(NetworkCloudlet netCloudlet) {
        CloudletTask task = new CloudletExecutionTask(
                netCloudlet.getTasks().size(), CLOUDLET_EXECUTION_TASK_LENGTH);
        task.setMemory(TASK_RAM);
        netCloudlet.addTask(task);
    }

    /**
     * Creates the NetworkDatacenter.
     *
     * @return the Datacenter
     */
    protected final NetworkDatacenter createDatacenter() {
        List<Host> hostList = new ArrayList<>();
        List<Pe> peList = new ArrayList<>();
        final int mips = 8000;
        for (int i = 0; i < 4; i++) {
            peList.add(new PeSimple(mips, new PeProvisionerSimple()));
        }
        int ram = 4096; // host memory (MEGABYTE)
        long storage = 1000000; // host storage
        long bw = 1000;

        for (int i = 0; i < 4; i++) {
            Host host = new NetworkHost(ram, bw, storage, peList);
            host.setRamProvisioner(new ResourceProvisionerSimple())
                    .setBwProvisioner(new ResourceProvisionerSimple())
                    .setVmScheduler(new VmSchedulerTimeShared());

            hostList.add(host);
        }

        DatacenterCharacteristics characteristics
                = new DatacenterCharacteristicsSimple(hostList);

        NetworkDatacenter datacenter
                = new NetworkDatacenter(
                        cloudsim, characteristics, new VmAllocationPolicySimple());
        createNetwork(datacenter);
        return datacenter;
    }

    /**
     * Creates internal Datacenter network.
     *
     * @param datacenter dc where the network will be created
     */
    protected void createNetwork(NetworkDatacenter datacenter) {
        EdgeSwitch[] edgeSwitches = new EdgeSwitch[1];
        for (int i = 0; i < edgeSwitches.length; i++) {
            edgeSwitches[i] = new EdgeSwitch(cloudsim, datacenter);
            datacenter.addSwitch(edgeSwitches[i]);
        }

        for (NetworkHost host : datacenter.<NetworkHost>getHostList()) {
            int switchNum = host.getId() / edgeSwitches[0].getPorts();
            edgeSwitches[switchNum].connectHost(host);
            host.setEdgeSwitch(edgeSwitches[switchNum]);
        }
    }

}
