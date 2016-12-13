/**
 * CloudSim Plus: A highly-extensible and easier-to-use Framework for Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
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
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerCompletelyFair;
import org.cloudsimplus.util.tablebuilder.PriorityCloudletsTableBuilderHelper;

/**
 * An example that uses an implementation of the {@link CloudletSchedulerCompletelyFair Completely Fair Scheduler}
 * used in the Linux Kernel for scheduling of Cloudlets execution inside a Vm.
 *
 * @author Manoel Campos da Silva Filho
 * @see <a href="https://en.wikipedia.org/wiki/Completely_Fair_Scheduler">Completely Fair Scheduler (CFS)</a>
 * @since CloudSim Plus 1.0
 *
 */
public class LinuxCompletelyFairSchedulerExample {
    private static final int HOSTS_NUMBER = 1;
    private static final double HOST_MIPS = 1000; //in MIPS
    private static final int HOST_PES = 3;
    private static final int VMS_NUMBER = 1;
    private static final int VM_PES = HOST_PES;
    private static final double VM_MIPS = HOST_MIPS;
    private static final int CLOUDLETS_NUMBER = HOST_PES*2;
    private static final int CLOUDLET_PES = 1;
    private static final int CLOUDLET_LEN = 10000; //in MI

    private final CloudSim simulation;
    private List<Cloudlet> cloudletList;
    private List<Vm> vmList;

    private int numberOfCreatedCloudlets = 0;
    private int numberOfCreatedVms = 0;
    private int numberOfCreatedHosts = 0;

    /**
     * Starts the simulation.
     * @param args
     */
    public static void main(String[] args) {
        new LinuxCompletelyFairSchedulerExample();
    }

    /**
     * Default constructor where the simulation is built.
     */
    public LinuxCompletelyFairSchedulerExample() {
        Log.printFormattedLine("Starting %s...", getClass().getSimpleName());
        //Number of cloud customers
        int numberOfCloudUsers = 1;

        simulation = new CloudSim();

        Datacenter datacenter0 = createDatacenter();

        DatacenterBroker broker0 = new DatacenterBrokerSimple(simulation);

        createAndSubmitVms(broker0);
        createAndSubmitCloudlets(broker0);
        for(int i = 0; i < CLOUDLETS_NUMBER/2; i++){
            cloudletList.get(i).setPriority(4);
        }

        simulation.start();

        List<Cloudlet> finishedCloudlets = broker0.getCloudletsFinishedList();
        new PriorityCloudletsTableBuilderHelper(finishedCloudlets).build();
        Log.printFormattedLine("%s finished!", getClass().getSimpleName());
    }

    private void createAndSubmitCloudlets(DatacenterBroker broker0) {
        this.cloudletList = new ArrayList<>(CLOUDLETS_NUMBER);
        for(int i = 0; i < CLOUDLETS_NUMBER; i++){
            this.cloudletList.add(createCloudlet(broker0));
        }
        broker0.submitCloudletList(cloudletList);
    }

    private void createAndSubmitVms(DatacenterBroker broker0) {
        this.vmList = new ArrayList<>(VMS_NUMBER);
        for(int i = 0; i < VMS_NUMBER; i++){
            this.vmList.add(createVm(broker0));
        }
        broker0.submitVmList(vmList);
    }

    private DatacenterSimple createDatacenter() {
        List<Host> hostList = new ArrayList<>(HOSTS_NUMBER);
        for(int i = 0; i < HOSTS_NUMBER; i++){
            hostList.add(createHost());
        }

        //Defines the characteristics of the data center
        final double cost = 3.0; // the cost of using processing in this switches
        final double costPerMem = 0.05; // the cost of using memory in this switches
        final double costPerStorage = 0.001; // the cost of using storage in this switches
        final double costPerBw = 0.0; // the cost of using bw in this switches

        DatacenterCharacteristics characteristics =
            new DatacenterCharacteristicsSimple(hostList)
                .setCostPerSecond(cost)
                .setCostPerMem(costPerMem)
                .setCostPerStorage(costPerStorage)
                .setCostPerBw(costPerBw);

        return new DatacenterSimple(simulation, characteristics, new VmAllocationPolicySimple());
    }

    private Host createHost() {
        final long ram = 2048; // host memory (MB)
        final long storage = 1000000; // host storage
        final long bw = 10000;

        List<Pe> peList = createHostPesList(HOST_MIPS);

       return new HostSimple(numberOfCreatedHosts++, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
            .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(bw)))
            .setVmScheduler(new VmSchedulerTimeShared());

    }

    private List<Pe> createHostPesList(double mips) {
        List<Pe> cpuCoresList = new ArrayList<>(HOST_PES);
        for(int i = 0; i < HOST_PES; i++){
            cpuCoresList.add(new PeSimple(i, new PeProvisionerSimple(mips)));
        }

        return cpuCoresList;
    }

    private Vm createVm(DatacenterBroker broker) {
        final long   storage = 10000; // vm image size (MB)
        final int    ram = 512; // vm memory (MB)
        final long   bw = 1000; // vm bandwidth

        return new VmSimple(numberOfCreatedVms++, VM_MIPS, VM_PES)
            .setRam(ram).setBw(bw).setSize(storage)
            .setCloudletScheduler(new CloudletSchedulerCompletelyFair())
            .setBroker(broker);
    }

    private Cloudlet createCloudlet(DatacenterBroker broker) {
        final long fileSize = 300; //Size (in bytes) before execution
        final long outputSize = 300; //Size (in bytes) after execution

        //Defines how CPU, RAM and Bandwidth resources are used
        //Sets the same utilization model for all these resources.
        UtilizationModel utilization = new UtilizationModelFull();

        Cloudlet cloudlet
            = new CloudletSimple(numberOfCreatedCloudlets++, CLOUDLET_LEN, CLOUDLET_PES)
                .setCloudletFileSize(fileSize)
                .setCloudletOutputSize(outputSize)
                .setUtilizationModel(utilization)
                .setBroker(broker);
        return cloudlet;
    }
}

