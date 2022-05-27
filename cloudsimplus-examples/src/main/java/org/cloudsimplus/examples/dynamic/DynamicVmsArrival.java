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

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * An example showing how to delay the submission of VMs.
 * The first VMs are created because there are enough resources to run them.
 * The delayed VMs are created only after the first ones finish,
 * so that there will be available resources to run them.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.3.1
 */
public class DynamicVmsArrival {
    private static final int HOSTS = 2;
    /** Number of Processor Elements (CPU Cores) of each Host. */
    private static final int HOST_PES = 2;
    private static final double HOST_MIPS = 1000;

    /** Number of Processor Elements (CPU Cores) of each VM and cloudlet. */
    private static final int VM_PES = 1;

    /**
     * Number of VMs to create.
     * If you try to increase this number without increases Hosts,
     * you'll see that the additional VMs won't be created because there are no
     * suitable Hosts.
     */
    private static final int VMS = 4;

    /** Number of Cloudlets to create simultaneously. */
    private static final int CLOUDLETS = VMS;
    /** The length of each Cloudlet in number of Million Instructions (MI) */
    private static final int CLOUDLET_LENGTH = 10000;
    private static final int CLOUDLET_PES = 1;

    /**
     * The time to wait before destroying a VM after it becomes idle (in seconds).
     */
    public static final int IDLE_VM_DESTRUCTION_DELAY = 1;

    /**
     * The time the Cloudlets are expected to finish,
     * when the creation of new VMs will be requested (in seconds).
     * After at that time, there will be suitable Hosts available.
     */
    private static final double EXPECTED_CLOUDLET_FINISH_TIME = CLOUDLET_LENGTH / HOST_MIPS + IDLE_VM_DESTRUCTION_DELAY + 1;

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
        new DynamicVmsArrival();
    }

    /**
     * Default constructor that builds and starts the simulation.
     */
    private DynamicVmsArrival() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());
        simulation = new CloudSim();

        this.hostList = new ArrayList<>(HOSTS);
        this.vmList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();
        this.broker = createBroker();
        this.datacenter = createDatacenter();

        //Creates VMs with no delay, so that they are placed into Hosts when the simulation starts
        createdAndSubmitVmsAndCloudlets(0);

        //Creates VMs after the previous ones finish
        createdAndSubmitVmsAndCloudlets(EXPECTED_CLOUDLET_FINISH_TIME);

        runSimulationAndPrintResults();
        System.out.println(getClass().getSimpleName() + " finished!");
    }

    /**
     * Creates a broker defining that idle VMs will be destructed after some delay,
     * instead of waiting the broker to shutdown to destroy them.
     * This enables freeing Hosts to place new VMs.
     *
     * @return
     */
    private DatacenterBroker createBroker() {
        final var broker = new DatacenterBrokerSimple(simulation);
        broker.setVmDestructionDelay(IDLE_VM_DESTRUCTION_DELAY);
        return broker;
    }

    private void runSimulationAndPrintResults() {
        simulation.start();
        final var cloudletList = broker.getCloudletFinishedList();
        cloudletList.sort(Comparator.comparingLong(Cloudlet::getId));
        new CloudletsTableBuilder(cloudletList).build();
    }

    /**
     * Creates and submits a list of VMs and Cloudlets.
     *
     * @param submissionDelay the time to wait before requesting the VM creation
     */
    private void createdAndSubmitVmsAndCloudlets(final double submissionDelay) {
        final var newVmList = createAndSubmitVms(submissionDelay);
        this.vmList.addAll(newVmList);
        this.cloudletList.addAll(createAndSubmitCloudlets(newVmList));

        if (submissionDelay == 0) {
            System.out.printf("# Submitting %d VMs at the beginning of the simulation.%n", VMS);
            System.out.printf("# Submitting %d Cloudlets at the beginning of the simulation.%n", CLOUDLETS);
        } else {
            System.out.printf("# Submitting %d VMs after %.0f seconds.%n", VMS, submissionDelay);
            System.out.printf("# Submitting %d Cloudlets after %.0f seconds.%n", CLOUDLETS, submissionDelay);
        }
        System.out.println();
    }

    /**
     * Creates and submits a list of VMs.
     *
     * @param submissionDelay the time to wait before requesting the VM creation
     * @return
     */
    private List<Vm> createAndSubmitVms(final double submissionDelay) {
        final var newVmList = new ArrayList<Vm>(VMS);
        for (int i = 0; i < VMS; i++) {
            newVmList.add(createVm(submissionDelay));
        }

        broker.submitVmList(newVmList);
        return newVmList;
    }

    /**
     * Creates a VM with pre-defined configuration.
     *
     * @param submissionDelay the time to wait before requesting the VM creation
     * @return the created VM
     */
    private Vm createVm(final double submissionDelay) {
        final int mips = 1000;
        final long size = 10000; // image size (Megabyte)
        final int ram = 512; // vm memory (Megabyte)
        final long bw = 1000;

        final var vm = new VmSimple(mips, VM_PES);
        vm.setRam(ram)
          .setBw(bw)
          .setSize(size)
          .setSubmissionDelay(submissionDelay);
        return vm;
    }

    /**
     * Creates one Cloudlet for each VM in the given List.
     *
     * @param vms the List fo VMs to create Cloudlets to
     * @return the List of Cloudlets
     */
    private List<Cloudlet> createAndSubmitCloudlets(final List<Vm> vms) {
        final var newCloudletList = new ArrayList<Cloudlet>(CLOUDLETS);
        for (final Vm vm : vms) {
            newCloudletList.add(createCloudlet(vm));
        }

        broker.submitCloudletList(newCloudletList);
        return newCloudletList;
    }

    /**
     * Creates a cloudlet with pre-defined configuration.
     *
     * @param vm vm to run the cloudlet
     * @return the created cloudlet
     */
    private Cloudlet createCloudlet(final Vm vm) {
        final var utilizationModelFull = new UtilizationModelFull();
        final var utilizationModelDynamic = new UtilizationModelDynamic(0.2);

        return new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES)
            .setUtilizationModelCpu(utilizationModelFull)
            .setUtilizationModelRam(utilizationModelDynamic)
            .setUtilizationModelBw(utilizationModelDynamic)
            .setVm(vm);
    }

    /**
     * Creates a Datacenter with pre-defined configuration.
     *
     * @return the created Datacenter
     */
    private Datacenter createDatacenter() {
        for (int i = 0; i < HOSTS; i++) {
            hostList.add(createHost());
        }

        return new DatacenterSimple(simulation, hostList);
    }

    /**
     * Creates a host with pre-defined configuration.
     *
     * @return the created host
     */
    private Host createHost() {
        final var peList = new ArrayList<Pe>();
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(HOST_MIPS));
        }
        long ram = 2048; // host memory (Megabyte)
        long storage = 1000000; // host storage (Megabyte)
        long bw = 10000; //Megabits/s

        return new HostSimple(ram, bw, storage, peList);
    }
}
