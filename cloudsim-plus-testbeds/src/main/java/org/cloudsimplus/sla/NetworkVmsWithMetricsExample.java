/**
 * CloudSim Plus: A highly-extensible and easier-to-use Framework for
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
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.network.switches.EdgeSwitch;
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet;
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter;
import org.cloudbus.cloudsim.hosts.network.NetworkHost;
import org.cloudbus.cloudsim.vms.network.NetworkVm;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.builders.tables.CloudletsTableBuilderHelper;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

/**
 * @author raysaoliveira
 */
public class NetworkVmsWithMetricsExample {

    /**
     * The cloudlet list.
     */
    private final List<NetworkCloudlet> cloudletList;

    /**
     * The vmlist.
     */
    private final List<NetworkVm> vmlist;

    /**
     * The Datacenter
     */
    NetworkDatacenter datacenter0;
    private final CloudSim cloudsim;

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
        int pesNumber = 1; //number of cpus

        for (int i = 0; i < vms; i++) {
            NetworkVm vm = new NetworkVm(i, mips, pesNumber);
            vm.setRam(ram)
                    .setBw(bw).setSize(size)
                    .setBroker(broker)
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
    private List<NetworkCloudlet> createCloudlet(DatacenterBroker broker, int cloudlets) {
        // Creates a container to store Cloudlets
        List<NetworkCloudlet> list = new ArrayList<>(cloudlets);
        //cloudlet parameters
        long length = 1000;
        int pesNumber = 1;
        long fileSize = 300;
        long outputSize = 300;
        long memory = 512;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        for (int i = 0; i < cloudlets; i++) {
            NetworkCloudlet cloudlet = new NetworkCloudlet(i, length, pesNumber);
            cloudlet.setMemory(memory)
                    .setFileSize(fileSize)
                    .setOutputSize(outputSize)
                    .setUtilizationModel(utilizationModel)
                    .setBroker(broker);
            list.add(cloudlet);
        }
        return list;
    }

    /**
     * main
     *
     * @param args
     */
    public static void main(String[] args) {
        Log.printFormattedLine(" Starting... ");
        try {
            new NetworkVmsWithMetricsExample();
        } catch (Exception e) {
            Log.printFormattedLine("Simulation finished due to unexpected error: %s", e);
        }
    }

    private NetworkVmsWithMetricsExample() {
        // First step: Initialize the CloudSim package. It should be called before creating any entities.
        int num_user = 1; // number of cloud users
        cloudsim = new CloudSim();

        // Second step: Create Datacenters
        datacenter0 = createDatacenter();

        // Third step: Create Broker
        DatacenterBroker broker = new DatacenterBrokerSimple(cloudsim);

        vmlist = createVM(broker, 5);

        // submit vm list to the broker
        broker.submitVmList(vmlist);

        /* Fifth step: Read Cloudlets from workload external file in the swf format
         WorkloadFileReader workloadFileReader = new WorkloadFileReader("src/main/java/org/cloudbus/cloudsim/examples/sla/UniLu-Gaia-2014-2.swf", 1);
         cloudletList = workloadFileReader.generateWorkload().subList(0, 1000);
         for (Cloudlet cloudlet : cloudletList) {
         cloudlet.setBroker(brokerId);
         } */
        cloudletList = createCloudlet(broker, 10);

        // submit cloudlet list to the broker
        broker.submitCloudletList(cloudletList);

        // Sixth step: Starts the simulation
        cloudsim.start();

        //Final step: Print results when simulation is over
        List<Cloudlet> newList = broker.getCloudletsFinishedList();
        new CloudletsTableBuilderHelper(newList).build();

        Log.printFormattedLine("... finished!");
    }

    /**
     * Creates the NetworkDatacenter.
     *
     * @return the Datacenter
     */
    protected final NetworkDatacenter createDatacenter() {
        // Here are the steps needed to create a PowerDatacenter:
        // 1. We need to create a list to store
        // our machine
        List<Host> hostList = new ArrayList<>();

        // 2. A Machine contains one or more PEs or CPUs/Cores.
        // In this example, it will have only one core.
        List<Pe> peList = new ArrayList<>();

        int mips = 8000;

        // 3. Create PEs and add these into a list.
        peList.add(new PeSimple(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

        // 4. Create Host with its id and list of PEs and add them to the list
        // of machines
        int hostId = 0;
        int ram = 4096; // host memory (MEGABYTE)
        long storage = 1000000; // host storage
        long bw = 10000;

        Host host = new NetworkHost(hostId, storage, peList);
        host.setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
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

        DatacenterCharacteristics characteristics
                = new DatacenterCharacteristicsSimple(hostList)
                .setCostPerSecond(cost)
                .setCostPerMem(costPerMem)
                .setCostPerStorage(costPerStorage)
                .setCostPerBw(costPerBw);

        // 6. Finally, we need to create a PowerDatacenter object.
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
