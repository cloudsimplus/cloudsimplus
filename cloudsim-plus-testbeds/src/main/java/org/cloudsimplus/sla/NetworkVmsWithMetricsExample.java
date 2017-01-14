/**
 * CloudSim Plus: A highly-extensible and easier-to-use Framework for Modeling
 * and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 * Copyright (C) 2015-2016 Universidade da Beira Interior (UBI, Portugal) and
 * the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO,
 * Brazil).
 *
 * This file is part of CloudSim Plus.
 *
 * CloudSim Plus is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * CloudSim Plus is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.sla;

import java.io.FileNotFoundException;
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
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.network.switches.EdgeSwitch;
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet;
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter;
import org.cloudbus.cloudsim.hosts.network.NetworkHost;
import org.cloudbus.cloudsim.network.switches.Switch;
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
 * An example showing how to create throughput metric in the NetworkDatacenter.
 * 
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

    private NetworkVmsWithMetricsExample() throws FileNotFoundException, IOException {
        cloudsim = new CloudSim();
        
        datacenter0 = createDatacenter();

        DatacenterBroker broker = new DatacenterBrokerSimple(cloudsim);

        vmlist = createVM(broker, 5);
        broker.submitVmList(vmlist);
     
        cloudletList = createCloudlet(broker, 10);
        broker.submitCloudletList(cloudletList);

        cloudsim.start();
      
        double throughput = throughput(datacenter0, cloudsim);
        System.out.println("\n-------------------------------------------");
        System.out.println("\t Throughput : " + throughput);

        List<Cloudlet> newList = broker.getCloudletsFinishedList();
        new CloudletsTableBuilderHelper(newList).build();

        Log.printFormattedLine("... finished!");
    }

    /**
     * Calculates the throughput of the NetworkDatacenter
     * @param datacenter
     * @param simulation
     * @return throughput
     */
    private double throughput(NetworkDatacenter datacenter, CloudSim simulation) {
        double downlinkBw = 0.0;
        for (Switch edgeSwitch : datacenter.getEdgeSwitch()) {
            downlinkBw = edgeSwitch.getDownlinkBandwidth() / simulation.clock();
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
        peList.add(new PeSimple(mips, new PeProvisionerSimple())); // need to store Pe id and MIPS Rating

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

        DatacenterCharacteristics characteristics
                = new DatacenterCharacteristicsSimple(hostList);

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
