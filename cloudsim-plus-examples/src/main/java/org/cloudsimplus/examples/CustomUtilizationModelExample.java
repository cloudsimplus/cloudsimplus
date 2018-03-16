/*
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
package org.cloudsimplus.examples;

import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;

/**
 * An example that uses a UtilizationModel to define how a Cloudlet
 * uses the VM CPU in order to use just 50% of CPU capacity (MIPS)
 * all the time. That makes the Cloudlet spend the double of the expected
 * time to finish.
 *
 * @author raysaoliveira
 * @since CloudSim Plus 1.0
 */
public class CustomUtilizationModelExample {
    private static final int HOSTS = 1;
    private static final int VMS = 1;
    private static final int CLOUDLETS_PER_VM = 1;

    private final CloudSim simulation;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;

    private int numberOfCreatedHosts = 0;

    /**
     * Starts the example.
     * @param args
     */
    public static void main(String[] args) {
        new CustomUtilizationModelExample();
    }

    /**
     * Default constructor that builds the simulation.
     */
    public CustomUtilizationModelExample() {
        Log.printFormattedLine("Starting %s ...", getClass().getSimpleName());
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
        Log.printFormattedLine("%s finished!", getClass().getSimpleName());
    }

    private DatacenterSimple createDatacenter() {
        List<Host> hostList = new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }

        //Defines the characteristics of the data center
        double cost = 3.0; // the cost of using processing in this Datacenter
        double costPerMem = 0.05; // the cost of using memory in this Datacenter
        double costPerStorage = 0.001; // the cost of using storage in this Datacenter
        double costPerBw = 0.0; // the cost of using bw in this Datacenter

        DatacenterCharacteristics characteristics =
            new DatacenterCharacteristicsSimple(hostList)
                .setCostPerSecond(cost)
                .setCostPerMem(costPerMem)
                .setCostPerStorage(costPerStorage)
                .setCostPerBw(costPerBw);

        return new DatacenterSimple(simulation, characteristics, new VmAllocationPolicySimple());
    }

    private Host createHost() {
        long  mips = 1000; // capacity of each CPU core (in Million Instructions per Second)
        long  ram = 2048; // host memory (MEGABYTE)
        long storage = 1000000; // host storage (MEGABYTE)
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
        long   storage = 10000; // vm image size (MEGABYTE)
        int    ram = 512; // vm memory (MEGABYTE)
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

        //Defines how RAM and Bandwidth resources are used
        UtilizationModel utilizationFull = new UtilizationModelFull();
        /*Defines that the Cloudlet will use just 50% of VM CPU capacity all the time.*/
        UtilizationModel utilizationHalfCapacity = new UtilizationModelDynamic( 0.5);
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
