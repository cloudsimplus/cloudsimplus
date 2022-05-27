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
package org.cloudsimplus.examples;

import ch.qos.logback.classic.Level;
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
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * An example showing how to run two simulation scenarios with different configurations
 * in parallel, using the CloudSim Plus exclusive features and
 * <a href="https://www.oracle.com/technical-resources/articles/java/ma14-java-se-8-streams.html">Java 8 Lambda Expressions and Parallel Streams</a>.
 * The parallel execution of simulations doesn't ensure that the overall execution time will be reduced.
 * It depends on diverse factors such as the number of simulations, the tasks that each simulation
 * executes, the  number of available physical CPUs at your machine, etc.
 *
 * <p>This is just a minimal example that runs 2 simulations with different number
 * of Hosts, VMs and Cloudlets. However, all these objects will have the same configurations
 * in the different simulations. There will be just a difference in the number of created
 * objects.</p>
 *
 * <p>To enable such a parallel simulations execution, the logging must be
 * disable (as shown in the first line of the main method) and any console
 * output should be avoided during simulation execution.
 * Further, usage of static mutable attributes must be avoided.
 * All simulation attributes must be instance attributes and one
 * simulation run (a simulation instance) should not share data with other ones.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class ParallelSimulationsExample implements Runnable {
    private final String title;
    private final CloudSim simulation;
    private DatacenterBroker broker;
    private List<Cloudlet> cloudletList;
    private List<Vm> vmList;
    private int hostsNumber;
    private int vmsNumber;
    private int cloudletsNumber;

    /**
     * Creates the simulation scenarios with different configurations and execute them,
     * printing the results for each one after all simulations have finished.
     * @param args
     */
    public static void main(String[] args) {
        /*IT IS MANDATORY TO DISABLE THE LOG WHEN EXECUTING PARALLEL SIMULATIONS TO AVOID RUNTIME EXCEPTIONS.*/
        Log.setLevel(Level.OFF);

        final var simulationList = new ArrayList<ParallelSimulationsExample>(2);

        //Creates the first simulation scenario
        simulationList.add(
            new ParallelSimulationsExample("Simulation 1")
                .setHostsNumber(4)
                .setVmsNumber(4)
                .setCloudletsNumber(8)
        );

        //Creates the second simulation scenario
       simulationList.add(
            new ParallelSimulationsExample("Simulation 2")
                .setHostsNumber(4)
                .setVmsNumber(4)
                .setCloudletsNumber(16)
        );

        final long startTimeMilliSec = System.currentTimeMillis();
        //Uses Java 8 Streams to execute the simulation scenarios in parallel.
        // tag::parallelExecution[]
        simulationList.parallelStream().forEach(ParallelSimulationsExample::run);
        // end::parallelExecution[]

        final long finishTimeMilliSec = System.currentTimeMillis() - startTimeMilliSec;

        Log.setLevel(Level.INFO);
        System.out.printf("Time to run %d simulations: %d milliseconds%n", simulationList.size(), finishTimeMilliSec);

        //Prints the cloudlet list of all executed simulations
        simulationList.forEach(ParallelSimulationsExample::printResults);
    }

    private void printResults(){
        new CloudletsTableBuilder(broker.getCloudletFinishedList())
            .setTitle(this.title)
            .build();
    }

    /**
     * Default constructor where the simulation is initialized.
     * @param title a title for the simulation scenario (just for log purposes)
     * @see #run()
     */
    private ParallelSimulationsExample(final String title) {
        this.title = title;
        this.cloudletList = new ArrayList<>();
        this.vmList = new ArrayList<>();
        this.simulation = new CloudSim();
    }

    private List<Host> createHosts() {
        final var hostList = new ArrayList<Host>(hostsNumber);
        for(int i = 0; i  < hostsNumber; i++) {
            hostList.add(createHost());
        }

        return hostList;
    }

    /**
     * Creates a Hosts using a {@link ResourceProvisionerSimple}
     * for RAM and BW.
     * @return
     */
    private Host createHost() {
        final long  mips = 1000; // capacity of each CPU core (in Million Instructions per Second)
        final long  ram = 2048; // host memory (Megabyte)
        final long  storage = 1000000; // host storage (Megabyte)
        final long  bw = 10000; // in Megabits/s
        final int   pesNumber = 4;

        final var peList = new ArrayList<Pe>(pesNumber); // List of CPU cores
        for(int i = 0; i < pesNumber; i++) {
            peList.add(new PeSimple(mips, new PeProvisionerSimple()));
        }

        return new HostSimple(ram, bw, storage, peList).setVmScheduler(new VmSchedulerTimeShared());
    }

    private void createVms() {
        this.vmList = new ArrayList<>(vmsNumber);
        for(int i = 0; i < vmsNumber; i++) {
            this.vmList.add(createVm(i));
        }

        broker.submitVmList(vmList);
    }

    /**
     * Creates a VM using a {@link CloudletSchedulerTimeShared} by default.
     * @param id
     * @return
     */
    private Vm createVm(final int id) {
        final long   mips = 1000;
        final long   storage = 10000; // vm image size (Megabyte)
        final int    ram = 512; // vm memory (Megabyte)
        final long   bw = 1000; // vm bandwidth (Megabits/s)
        final int    pesNumber = 2; // number of CPU cores

        return new VmSimple(id, mips, pesNumber)
            .setRam(ram)
            .setBw(bw)
            .setSize(storage);
    }

    private void createCloudlets() {
        this.cloudletList = new ArrayList<>(cloudletsNumber);
        for(int i = 0; i < cloudletsNumber; i++) {
            this.cloudletList.add(createCloudlet(i));
        }
    }

    private Cloudlet createCloudlet(final int id) {
        final long length = 10000; //in Million Instructions (MI)
        final long fileSize = 300; //Size (in bytes) before execution
        final long outputSize = 300; //Size (in bytes) after execution
        final int  pesNumber = 1;

        // Defines how CPU, RAM and Bandwidth resources are used.
        // Sets the same utilization model for all these resources.
        final var utilizationModelFull = new UtilizationModelFull();

        /* A utilization model for RAM and BW that uses only a fraction of the resource capacity all the time.
        * If there are 10 cloudlets for a VM, each Cloudlet will use 10% (0.1) of the VM resource capacity. */
        final var utilizationModelDynamic = new UtilizationModelDynamic(1.0/cloudletsNumber);

        return new CloudletSimple(id, length, pesNumber)
                .setFileSize(fileSize)
                .setOutputSize(outputSize)
                .setUtilizationModelCpu(utilizationModelFull)
                .setUtilizationModelBw(utilizationModelDynamic)
                .setUtilizationModelRam(utilizationModelDynamic);
    }

    /**
     * Builds the simulation scenario and starts the simulation.
     */
    @Override
    public void run() {
        final Datacenter datacenter0 = new DatacenterSimple(simulation, createHosts());

        /* Creates a Broker accountable for submission of VMs and Cloudlets
        on behalf of a given cloud user (customer). */
        broker = new DatacenterBrokerSimple(simulation);

        createVms();
        createCloudlets();

        broker.submitCloudletList(cloudletList);

        // Starts the simulation and waits all cloudlets to be executed
        simulation.start();
    }

    public ParallelSimulationsExample setCloudletsNumber(final int cloudletsNumber) {
        this.cloudletsNumber = cloudletsNumber;
        return this;
    }

    public ParallelSimulationsExample setVmsNumber(final int vmsNumber) {
        this.vmsNumber = vmsNumber;
        return this;
    }

    public ParallelSimulationsExample setHostsNumber(final int hostsNumber) {
        this.hostsNumber = hostsNumber;
        return this;
    }
}
