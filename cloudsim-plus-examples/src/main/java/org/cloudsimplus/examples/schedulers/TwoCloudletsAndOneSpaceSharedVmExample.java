/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudsimplus.examples.schedulers;

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
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple example showing how to create a data center with 1 host, 1 VM and
 * run 2 cloudlets on it that will run sequentially: first one cloudlet executes
 * until complete, after the other one do the same. Once there is only one
 * cloudlet running on the VM by time, each one uses all VM's CPU capacity while
 * executing. By this way, one cloudlet finishes prior to the other, but the
 * execution time (the time using the processor) is the same. Using the cloudlet
 * space shared scheduler, the cloudlet is not interrupted when it starts to run
 * (because the non-preemptive nature of the scheduler).
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class TwoCloudletsAndOneSpaceSharedVmExample {
    private List<Cloudlet> cloudletList;
    private List<Vm> vmlist;
    private CloudSim simulation;

    /**
     * Creates main() to run this example.
     *
     * @param args the args
     */
    public static void main(String[] args) {
        new TwoCloudletsAndOneSpaceSharedVmExample();
    }

    public TwoCloudletsAndOneSpaceSharedVmExample(){
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());

        // First step: Initialize the CloudSim package.
        simulation = new CloudSim();

        // Second step: Create Datacenters
        // Datacenters are the resource providers in CloudSim. We need at
        // list one of them to run a CloudSim simulation
        Datacenter datacenter0 = createDatacenter();

        // Third step: Create Broker
        DatacenterBroker broker = new DatacenterBrokerSimple(simulation);

        // Fourth step: Create one virtual machine
        vmlist = new ArrayList<>();

        // VM description
        int vmid = 0;
        int mips = 1000;
        long size = 10000; // image size (Megabyte)
        int ram = 512; // vm memory (Megabyte)
        long bw = 1000;
        int pesNumber = 1; // number of cpus

        Vm vm = new VmSimple(vmid, mips, pesNumber)
            .setRam(ram).setBw(bw).setSize(size)
            .setCloudletScheduler(new CloudletSchedulerSpaceShared());
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
            .setFileSize(fileSize)
            .setOutputSize(outputSize)
            .setUtilizationModel(utilizationModel)
            .setVm(vm);
        cloudletList.add(cloudlet1);

        Cloudlet cloudlet2 = new CloudletSimple(++id, length, pesNumber)
            .setFileSize(fileSize)
            .setOutputSize(outputSize)
            .setUtilizationModel(utilizationModel)
            .setVm(vm);
        cloudletList.add(cloudlet2);

        // submit cloudlet list to the broker
        broker.submitCloudletList(cloudletList);

        // Sixth step: Starts the simulation
        simulation.start();

        //Final step: Print results when simulation is over
        List<Cloudlet> newList = broker.getCloudletFinishedList();
        new CloudletsTableBuilder(newList).build();
        System.out.println(getClass().getSimpleName() + " finished!");
    }
    /**
     * Creates the Datacenter.
     *
     * @return the Datacenter
     */
    private Datacenter createDatacenter() {
        // Here are the steps needed to create a DatacenterSimple:
        // 1. We need to create a list to store
        // our machine
        List<Host> hostList = new ArrayList<>();

        // 2. A Machine contains one or more PEs or CPUs/Cores.
        // In this example, it will have only one core.
        List<Pe> peList = new ArrayList<>();

        long mips = 1000;

        // 3. Create PEs and add these into a list.
        peList.add(new PeSimple(mips, new PeProvisionerSimple())); // need to store Pe id and MIPS Rating

        // 4. Create HostSimple with its id and list of PEs and add them to the list of machines
        final long ram = 20000; //in Megabytes
        final long bw = 100000; //in Megabytes
        final long storage = 10000000; //in Megabites/s
        final int id = hostList.size();
        Host host = new HostSimple(ram, bw, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());

        hostList.add(host);

        // 6. Finally, we need to create a DatacenterSimple object.
        return new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
    }

}
