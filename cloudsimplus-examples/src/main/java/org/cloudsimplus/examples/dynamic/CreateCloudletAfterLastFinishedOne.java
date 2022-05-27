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
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * An example showing how to dynamically create one Cloudlet after the previous one finishes.
 * It stops creating Cloudlets when the number reaches {@link #CLOUDLETS}.
 *
 * <p>This example uses CloudSim Plus Listener features to intercept when
 * the a Cloudlet finishes its execution to then request
 * the creation of a new Cloudlet. It uses the Java 8 Lambda Functions features
 * to pass a listener to the mentioned Cloudlet, by means of the
 * {@link Cloudlet#addOnFinishListener(EventListener)} method.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 2.2.0
 */
public class CreateCloudletAfterLastFinishedOne {
    private static final int HOSTS = 2;
    private static final int VMS = 4;
    private static final int HOST_PES_NUMBER = 4;
    private static final int VM_PES_NUMBER = 2;
    private static final int CLOUDLETS = VMS*VM_PES_NUMBER;

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
        new CreateCloudletAfterLastFinishedOne();
    }

    /**
     * Default constructor that builds and starts the simulation.
     */
    private CreateCloudletAfterLastFinishedOne() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());
        simulation = new CloudSim();

        this.hostList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();
        this.datacenter = createDatacenter();
        this.broker = new DatacenterBrokerSimple(simulation);

        this.vmList = createAndSubmitVms();
        createAndSubmitOneCloudlet();

        runSimulationAndPrintResults();
        System.out.println("Starting " + getClass().getSimpleName());
        System.out.println(getClass().getSimpleName() + " finished!");
    }

    private void runSimulationAndPrintResults() {
        simulation.start();
        List<Cloudlet> cloudlets = broker.getCloudletFinishedList();
        new CloudletsTableBuilder(cloudlets).build();
    }

    private List<Vm> createAndSubmitVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            list.add(createVm());
        }

        broker.submitVmList(list);

        return list;
    }

    /**
     * Creates a VM with pre-defined configuration.
     *
     * @return the created VM
     *
     */
    private Vm createVm() {
        final int mips = 1000;

        return new VmSimple(mips, VM_PES_NUMBER)
            .setRam(512).setBw(1000).setSize(10000)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());
    }

    /**
     * Creates and submit one Cloudlet,
     * defining an Event Listener that is notified when such a Cloudlet
     * is finished in order to create another one.
     * Cloudlets stop to be created when the
     * number of Cloudlets reaches {@link #CLOUDLETS}.
     */
    private void createAndSubmitOneCloudlet() {
        final int id = cloudletList.size();
        final long length = 10000; //in number of Million Instructions (MI)
        final int pesNumber = 1;
        Cloudlet cloudlet = new CloudletSimple(id, length, pesNumber)
            .setFileSize(300)
            .setOutputSize(300)
            .setUtilizationModel(new UtilizationModelFull());

        cloudletList.add(cloudlet);

        if(cloudletList.size() < CLOUDLETS){
            cloudlet.addOnFinishListener(this::cloudletFinishListener);
        }

        broker.submitCloudlet(cloudlet);
    }

    private void cloudletFinishListener(final CloudletVmEventInfo info) {
        System.out.printf(
            "\t# %.2f: Requesting creation of new Cloudlet after %s finishes executing.%n",
            info.getTime(), info.getCloudlet());
        createAndSubmitOneCloudlet();
    }

    /**
     * Creates a Datacenter with pre-defined configuration.
     *
     * @return the created Datacenter
     */
    private Datacenter createDatacenter() {
        for (int i = 0; i < HOSTS; i++) {
            hostList.add(createHost(i));
        }

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
