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
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * An example showing how to submit VMs to the broker with different delays.
 * This way, Cloudlets bound to such delayed VMs just start
 * executing after the respective VM is placed into some Host.
 * Since submitted cloudlets are not explicitly delayed, you can see in results
 * that, at the end, they are delayed due to VM delay.
 *
 * <p>Finally, considering there aren't enough hosts for all VMs, this example shows
 * how to make the broker to destroy idle VMs after a while to open room for new VMs.
 * Check {@link #createBroker()} for details.
 * </p>
 *
 * @author Fabian Mastenbroek
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 7.2.1
 */
public class DynamicVmsArrival2 {
    private static final int HOSTS = 1;

    /**
     * Number of Processor Elements (CPU Cores) of each Host.
     */
    private static final int HOST_PES_NUMBER = 4;

    /**
     * Number of Processor Elements (CPU Cores) of each VM and cloudlet.
     */
    private static final int VM_PES_NUMBER = 4;

    /**
     * Number of Cloudlets to create simultaneously.
     * Other cloudlets will be enqueued.
     */
    private static final int CLOUDLETS_NUMBER = VM_PES_NUMBER;

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
        new DynamicVmsArrival2();
    }

    /**
     * Default constructor that builds and starts the simulation.
     */
    private DynamicVmsArrival2() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());
        simulation = new CloudSim();

        this.hostList = new ArrayList<>();
        this.vmList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();
        this.datacenter = createDatacenter();
        this.broker = createBroker();

        createAndSubmitVmsAndCloudlets();

        runSimulationAndPrintResults();
        System.out.println(getClass().getSimpleName() + " finished!");
    }

    /**
     * Creates a broker that destroys idle VMs after a while.
     * Since we aren't creating enough hosts for all VMs,
     * we need to destroy idle VMs to open room for new ones.
     * By default, VM destruction just happens when the broker shuts down.
     * @return
     */
    private DatacenterBroker createBroker() {
        return new DatacenterBrokerSimple(simulation).setVmDestructionDelay(1.0);
    }

    private void runSimulationAndPrintResults() {
        simulation.start();
        final var cloudletList = broker.getCloudletFinishedList();
        new CloudletsTableBuilder(cloudletList).build();
    }

    private void createAndSubmitVmsAndCloudlets() {
        final var newVmList = new ArrayList<Vm>(CLOUDLETS_NUMBER);
        final var newCloudletList = new ArrayList<Cloudlet>(CLOUDLETS_NUMBER);

        for (int i = 0; i < CLOUDLETS_NUMBER; i++) {
            final var vm = createVm(i);
            vm.setSubmissionDelay(i * 100);
            newVmList.add(vm);

            final var cloudlet = createCloudlet(i, vm);
            newCloudletList.add(cloudlet);
        }

        broker.submitVmList(newVmList);
        broker.submitCloudletList(newCloudletList);

        this.vmList.addAll(newVmList);
        this.cloudletList.addAll(newCloudletList);
    }

    /**
     * Creates a VM with pre-defined configuration.
     *
     * @param id the VM id
     * @return the created VM
     */
    private Vm createVm(final int id) {
        final int mips = 1000;
        final long size = 10000; // image size (Megabyte)
        final int ram = 512; // vm memory (Megabyte)
        final long bw = 1000;

        return new VmSimple(id, mips, VM_PES_NUMBER).setRam(ram).setBw(bw).setSize(size);
    }

    /**
     * Creates a cloudlet with pre-defined configuration.
     *
     * @param id Cloudlet id
     * @param vm vm to run the cloudlet
     * @return the created cloudlet
     */
    private Cloudlet createCloudlet(final int id, final Vm vm) {
        final long fileSize = 300;
        final long outputSize = 300;
        final long length = 10000; //in number of Million Instructions (MI)
        final int pesNumber = 1;
        final var utilizationModel = new UtilizationModelFull();

        return new CloudletSimple(id, length, pesNumber)
            .setFileSize(fileSize)
            .setOutputSize(outputSize)
            .setUtilizationModel(utilizationModel)
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
        final long mips = 1000;
        for(int i = 0; i < HOST_PES_NUMBER; i++){
            peList.add(new PeSimple(mips));
        }
        final long ram = 2048; // in Megabytes
        final long storage = 1000000; // in Megabytes
        final long bw = 10000; //in Megabits/s

        return new HostSimple(ram, bw, storage, peList);
    }
}
