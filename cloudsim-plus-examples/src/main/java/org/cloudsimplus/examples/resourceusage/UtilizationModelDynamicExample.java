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
package org.cloudsimplus.examples.resourceusage;

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
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * An example that uses a {@link UtilizationModelDynamic} to define how a Cloudlet
 * uses the VM CPU in order to use just 50% of CPU capacity (MIPS)
 * all the time. That makes the Cloudlet spend the double of the expected
 * time to finish.
 *
 * @author raysaoliveira
 * @since CloudSim Plus 1.0
 */
public class UtilizationModelDynamicExample {
    private static final int HOSTS = 1;
    private static final int VMS = 1;
    private static final int CLOUDLETS_PER_VM = 1;

    private final CloudSim simulation;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;

    /**
     * Starts the example.
     * @param args
     */
    public static void main(String[] args) {
        new UtilizationModelDynamicExample();
    }

    /**
     * Default constructor that builds the simulation.
     */
    public UtilizationModelDynamicExample() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());
        this.simulation = new CloudSim();

        Datacenter datacenter0 = createDatacenter();

        /*Creates a Broker accountable for submission of VMs and Cloudlets
        on behalf of a given cloud user (customer).*/
        DatacenterBroker broker0 = new DatacenterBrokerSimple(simulation);

        this.vmList = new ArrayList<>(VMS);
        this.cloudletList = new ArrayList<>(VMS);

        /**
         * Creates VMs and one Cloudlet for each VM.
         */
        for (int i = 0; i < VMS; i++) {
            Vm vm = createVm(broker0);
            this.vmList.add(vm);
            for (int j = 0; j < CLOUDLETS_PER_VM; j++) {
                /*Creates a Cloudlet that represents an application to be run inside a VM.*/
                Cloudlet cloudlet = createCloudlet(broker0, vm);
                this.cloudletList.add(cloudlet);
            }
        }
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        /* Starts the simulation and waits all cloudlets to be executed. */
        simulation.start();

        /*Prints results when the simulation is over
        (you can use your own code here to print what you want from this cloudlet list)*/
        List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();
        System.out.println(getClass().getSimpleName() + " finished!");
    }

    private DatacenterSimple createDatacenter() {
        List<Host> hostList = new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }

        return new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
    }

    private Host createHost() {
        long  mips = 1000; // capacity of each CPU core (in Million Instructions per Second)
        long  ram = 2048; // host memory (Megabyte)
        long storage = 1000000; // host storage (Megabyte)
        long bw = 10000; //in Megabits/s

        List<Pe> peList = new ArrayList<>(); //List of CPU cores

        /*Creates the Host's CPU cores and defines the provisioner
        used to allocate each core for requesting VMs.*/
        for (int i = 0; i < 2; i++) {
            peList.add(new PeSimple(mips, new PeProvisionerSimple()));
        }

        return new HostSimple(ram, bw, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
                .setVmScheduler(new VmSchedulerTimeShared());
    }

    private Vm createVm(DatacenterBroker broker) {
        long   mips = 1000;
        long   storage = 10000; // vm image size (Megabyte)
        int    ram = 512; // vm memory (Megabyte)
        long   bw = 1000; // vm bandwidth (Megabits/s)
        int    pesNumber = 2; // number of CPU cores

        return new VmSimple(vmList.size(), mips, pesNumber)
                .setRam(ram)
                .setBw(bw)
                .setSize(storage)
                .setCloudletScheduler(new CloudletSchedulerTimeShared());
    }

    private Cloudlet createCloudlet(DatacenterBroker broker, Vm vm) {
        long length = 10000; //in Million Structions (MI)
        long fileSize = 300; //Size (in bytes) before execution
        long outputSize = 300; //Size (in bytes) after execution
        long numberOfCpuCores = vm.getNumberOfPes(); //cloudlet will use all the VM's CPU cores

        //Defines that the Cloudlet will use all the VM's RAM and Bandwidth.
        UtilizationModel utilizationFull = new UtilizationModelFull();

        /* Defines that the Cloudlet will use just 50% of the vPEs' MIPS capacity.
        *  Virtual PEs or simply vPEs are the PEs allocated to a given VM,
        *  which will be used to run Cloudlets.*/
        UtilizationModel utilizationHalfCapacity = new UtilizationModelDynamic(0.5);
        Cloudlet cloudlet
                = new CloudletSimple(
                        cloudletList.size(), length, numberOfCpuCores)
                        .setFileSize(fileSize)
                        .setOutputSize(outputSize)
                        .setUtilizationModelBw(utilizationFull)
                        .setUtilizationModelRam(utilizationFull)
                        .setUtilizationModelCpu(utilizationHalfCapacity)
                        .setVm(vm);

        return cloudlet;
    }

}
