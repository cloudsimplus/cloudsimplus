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
package org.cloudsimplus.examples;

import ch.qos.logback.classic.Level;
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
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * An example showing how to run two simulation scenarios with different configurations
 * in parallel, using the CloudSim Plus exclusive features and Java 8 Lambda Expressions and Parallel Streams.
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
    private CloudSim simulation;
    private DatacenterBroker broker;
    private List<Cloudlet> cloudletList;
    private List<Vm> vmList;
    private List<Cloudlet> finishedCloudletList;
    private int hostsToCreate;
    private int vmsToCreate;
    private int cloudletsToCreate;

    /**
     * Creates the simulation scenarios with different configurations and execute them,
     * printing the results for each one after all simulations have finished.
     *
     * @param args
     */
    public static void main(String[] args) {
        /*IT IS MANDATORY TO DISABLE THE LOG WHEN EXECUTING PARALLEL SIMULATIONS TO AVOID RUNTIME EXCEPTIONS.*/
        Log.setLevel(Level.OFF);

        List<ParallelSimulationsExample> simulationList = new ArrayList<>(2);

        //Creates the first simulation scenario
        simulationList.add(
            new ParallelSimulationsExample("Simulation 1")
                .setHostsToCreate(4)
                .setVmsToCreate(4)
                .setCloudletsToCreate(8)
        );

        //Creates the second simulation scenario
       simulationList.add(
            new ParallelSimulationsExample("Simulation 2")
                .setHostsToCreate(4)
                .setVmsToCreate(4)
                .setCloudletsToCreate(16)
        );

        final long startTimeMilliSec = System.currentTimeMillis();
        //Uses Java 8 Streams to execute the simulation scenarios in parallel.
        // tag::parallelExecution[]
        simulationList.parallelStream().forEach(ParallelSimulationsExample::run);
        // end::parallelExecution[]

        final long finishTimeMilliSec = System.currentTimeMillis() - startTimeMilliSec;

        Log.setLevel(Level.INFO);
        System.out.printf("Time to run %d simulations: %d milliseconds\n", simulationList.size(), finishTimeMilliSec);

        //Prints the cloudlet list of all executed simulations
        simulationList.forEach(ParallelSimulationsExample::printResults);
    }

    public void printResults(){
        new CloudletsTableBuilder(getFinishedCloudletList())
            .setTitle(this.title)
            .build();
    }

    /**
     * Default constructor where the simulation is initialized.
     * @param title a title for the simulation scenario (just for log purposes)
     * @see #run()
     */
    public ParallelSimulationsExample(String title) {
        //Number of cloud customers
        int numberOfCloudUsers = 1;
        this.title = title;
        this.cloudletList = new ArrayList<>();
        this.finishedCloudletList = new ArrayList<>();
        this.vmList = new ArrayList<>();
        this.simulation = new CloudSim();
    }

    private DatacenterSimple createDatacenter() {
        List<Host> hostList = createHosts();

        return new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
    }

    private List<Host> createHosts() {
        List<Host> hostList = new ArrayList<>(hostsToCreate);
        for(int i = 0; i  < hostsToCreate; i++) {
            Host host = createHost(i);
            hostList.add(host);
        }
        return hostList;
    }

    private Host createHost(int hostId) {
        long  mips = 1000; // capacity of each CPU core (in Million Instructions per Second)
        long  ram = 2048; // host memory (Megabyte)
        long storage = 1000000; // host storage (Megabyte)
        long bw = 10000; //in Megabits/s
        final int numberOfPes = 4;

        List<Pe> peList = new ArrayList<>(numberOfPes); //List of CPU cores
        for(int i = 0; i < numberOfPes; i++) {
            peList.add(new PeSimple(mips, new PeProvisionerSimple()));
        }

        return new HostSimple(ram, bw, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());
    }

    private void createVms() {
        this.vmList = new ArrayList<>(vmsToCreate);
        for(int i = 0; i < vmsToCreate; i++) {
            Vm vm0 = createVm(broker, i);
            this.vmList.add(vm0);
        }
        broker.submitVmList(vmList);
    }

    private Vm createVm(DatacenterBroker broker, int vmId) {
        long   mips = 1000;
        long   storage = 10000; // vm image size (Megabyte)
        int    ram = 512; // vm memory (Megabyte)
        long   bw = 1000; // vm bandwidth (Megabits/s)
        int    pesNumber = 2; // number of CPU cores

        return new VmSimple(vmId, mips, pesNumber)
            .setRam(ram)
            .setBw(bw)
            .setSize(storage)
            .setCloudletScheduler(new CloudletSchedulerTimeShared());
    }

    private void createCloudlets() {
        this.cloudletList = new ArrayList<>(cloudletsToCreate);
        for(int i = 0; i < cloudletsToCreate; i++) {
            Cloudlet cloudlet = createCloudlet(broker, i);
            this.cloudletList.add(cloudlet);
        }
    }

    private Cloudlet createCloudlet(DatacenterBroker broker, int cloudletId) {
        long length = 10000; //in Million Structions (MI)
        long fileSize = 300; //Size (in bytes) before execution
        long outputSize = 300; //Size (in bytes) after execution
        int  numberOfCpuCores = 1;

        //Defines how CPU, RAM and Bandwidth resources are used
        //Sets the same utilization model for all these resources.
        UtilizationModel utilization = new UtilizationModelFull();

        return new CloudletSimple(cloudletId, length, numberOfCpuCores)
                .setFileSize(fileSize)
                .setOutputSize(outputSize)
                .setUtilizationModel(utilization);
    }

    /**
     * Builds the simulation scenario and starts the simulation.
     */
    @Override
    public void run() {
        Datacenter datacenter0 = createDatacenter();

        /*Creates a Broker accountable for submission of VMs and Cloudlets
        on behalf of a given cloud user (customer).*/
        broker = new DatacenterBrokerSimple(simulation);

        createVms();
        createCloudlets();

        broker.submitCloudletList(cloudletList);
        /*Starts the simulation and waits all cloudlets to be executed*/
        simulation.start();

        this.finishedCloudletList = broker.getCloudletFinishedList();
    }

    public int getCloudletsToCreate() {
        return cloudletsToCreate;
    }

    public ParallelSimulationsExample setCloudletsToCreate(int cloudletsToCreate) {
        this.cloudletsToCreate = cloudletsToCreate;
        return this;
    }

    public int getVmsToCreate() {
        return vmsToCreate;
    }

    public ParallelSimulationsExample setVmsToCreate(int vmsToCreate) {
        this.vmsToCreate = vmsToCreate;
        return this;
    }

    public int getHostsToCreate() {
        return hostsToCreate;
    }

    public ParallelSimulationsExample setHostsToCreate(int hostsToCreate) {
        this.hostsToCreate = hostsToCreate;
        return this;
    }

    public List<Cloudlet> getFinishedCloudletList() {
        return finishedCloudletList;
    }

    public String getTitle() {
        return title;
    }
}
