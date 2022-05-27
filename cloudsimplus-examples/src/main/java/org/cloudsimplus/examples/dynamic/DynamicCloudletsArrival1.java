/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudsimplus.examples.dynamic;

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
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.TextTableColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * An example showing how to delay the submission of cloudlets.
 * Although there is enough resources to run all cloudlets simultaneously,
 * the example delays the creation and execution of some cloudlets inside a VM,
 * simulating the dynamic arrival of cloudlets.
 * It first creates a set of cloudlets without delay and
 * another set of cloudlets all with the same submission delay.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class DynamicCloudletsArrival1 {
    /**
     * Number of Processor Elements (CPU Cores) of each Host.
     */
    private static final int HOST_PES_NUMBER = 8;

    /**
     * Number of Processor Elements (CPU Cores) of each VM and cloudlet.
     */
    private static final int VM_PES_NUMBER = HOST_PES_NUMBER/2;

    /**
     * Number of Cloudlets to create simultaneously.
     * Other cloudlets will be enqueued.
     */
    private static final int NUMBER_OF_CLOUDLETS = VM_PES_NUMBER*2;

    private final List<Host> hostList;
    private final List<Vm> vmList;
    private final List<Cloudlet> cloudletList;
    private final DatacenterBroker broker;
    private final Datacenter datacenter;
    private final CloudSim simulation;

    /**
     * Starts the example execution, calling the class constructor\
     * to build and run the simulation.
     *
     * @param args command line parameters
     */
    public static void main(String[] args) {
        new DynamicCloudletsArrival1();
    }

    /**
     * Default constructor that builds and starts the simulation.
     */
    private DynamicCloudletsArrival1() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());
        simulation = new CloudSim();

        this.hostList = new ArrayList<>();
        this.vmList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();
        this.datacenter = createDatacenter();
        this.broker = new DatacenterBrokerSimple(simulation);

        createAndSubmitVmAndCloudlets(0);

        /*Defines a delay of 5 seconds and creates another group of cloudlets
        that will start executing inside a VM only after this delay expires.*/
        Vm vm = createVm();
        Cloudlet cl = createCloudlet(vm);
        vm.setSubmissionDelay(5);
        cl.setSubmissionDelay(5);

        broker.submitVm(vm);
        broker.submitCloudlet(cl);

        runSimulationAndPrintResults();
        System.out.println(getClass().getSimpleName() + " finished!");
    }

    private void runSimulationAndPrintResults() {
        simulation.start();
        List<Cloudlet> cloudlets = broker.getCloudletFinishedList();
        new CloudletsTableBuilder(cloudlets)
            .addColumn(7, new TextTableColumn("VM Arrived", "Time"), cl -> cl.getVm().getArrivedTime())
            .addColumn(8, new TextTableColumn("VM Creation", "Time"), cl -> cl.getVm().getCreationTime())
            .addColumn(9, new TextTableColumn("VM Wait", "Time"), cl -> cl.getVm().getWaitTime())
            .build();
    }

    /**
     * Creates cloudlets and submit them to the broker, applying
     * a submission delay for each one (simulating the dynamic cloudlet arrival).
     *
     * @param vm Vm to run the cloudlets to be created
     * @param submissionDelay the delay the broker has to include when submitting the Cloudlets
     *
     * @see #createCloudlet(Vm)
     */
    private void createAndSubmitCloudlets(Vm vm, double submissionDelay) {
        int cloudletId = cloudletList.size();
        List<Cloudlet> list = new ArrayList<>(NUMBER_OF_CLOUDLETS);
        for(int i = 0; i < NUMBER_OF_CLOUDLETS; i++){
            Cloudlet cloudlet = createCloudlet(vm);
            list.add(cloudlet);
        }

        broker.submitCloudletList(list, submissionDelay);
        cloudletList.addAll(list);
    }

    /**
     * Creates one Vm and a group of cloudlets to run inside it,
     * and submit the Vm and its cloudlets to the broker.
     * @see #createVm()
     */
    private void createAndSubmitVmAndCloudlets(final double submissionDelay) {
        List<Vm> list = new ArrayList<>();
        Vm vm = createVm();
        list.add(vm);

        broker.submitVmList(list, submissionDelay);
        this.vmList.addAll(list);

        //Submit cloudlets without delay
        createAndSubmitCloudlets(vm, submissionDelay);
    }

    private Vm createVm() {
        int mips = 1000;
        long size = 10000; // image size (Megabyte)
        int ram = 512; // vm memory (Megabyte)
        long bw = 1000;

        return new VmSimple(mips, VM_PES_NUMBER)
            .setRam(ram).setBw(bw).setSize(size)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());
    }

    /**
     * Creates a cloudlet with pre-defined configuration.
     *
     * @param vm vm to run the cloudlet
     * @return the created cloudlet
     */
    private Cloudlet createCloudlet(Vm vm) {
        long fileSize = 300;
        long outputSize = 300;
        long length = 10000; //in number of Million Instructions (MI)
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();
        UtilizationModel utilizationModelDynamic = new UtilizationModelDynamic(0.05);
        Cloudlet cloudlet = new CloudletSimple(length, pesNumber)
            .setFileSize(fileSize)
            .setOutputSize(outputSize)
            .setUtilizationModelCpu(utilizationModel)
            .setUtilizationModelBw(utilizationModelDynamic)
            .setUtilizationModelRam(utilizationModelDynamic)
            .setVm(vm);

        return cloudlet;
    }

    /**
     * Creates a Datacenter with pre-defined configuration.
     *
     * @return the created Datacenter
     */
    private Datacenter createDatacenter() {
        Host host = createHost(0);
        hostList.add(host);
        return new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
    }

    /**
     * Creates a host with pre-defined configuration.
     *
     * @param id The Host id
     * @return the created host
     */
    private Host createHost(int id) {
        List<Pe> peList = new ArrayList<>();
        long mips = 1000;
        for(int i = 0; i < HOST_PES_NUMBER; i++){
            peList.add(new PeSimple(mips, new PeProvisionerSimple()));
        }
        long ram = 2048; // host memory (Megabyte)
        long storage = 1000000; // host storage (Megabyte)
        long bw = 10000; //Megabits/s

       return new HostSimple(ram, bw, storage, peList)
           .setRamProvisioner(new ResourceProvisionerSimple())
           .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerSpaceShared());

    }
}
