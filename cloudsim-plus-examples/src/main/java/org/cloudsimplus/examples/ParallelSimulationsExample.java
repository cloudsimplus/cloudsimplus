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

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.util.tablebuilder.CloudletsTableBuilderHelper;

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
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class ParallelSimulationsExample {
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
        Log.disable();  //It is mandatory to disable the Log when executing parallel simulations
        List<ParallelSimulationsExample> examples = new ArrayList<>(2);

        //Creates the first simulation scenario
        examples.add(
            new ParallelSimulationsExample("Simulation 1")
                .setHostsToCreate(4)
                .setVmsToCreate(4)
                .setCloudletsToCreate(8)
        );

        //Creates the second simulation scenario
       examples.add(
            new ParallelSimulationsExample("Simulation 2")
                .setHostsToCreate(4)
                .setVmsToCreate(4)
                .setCloudletsToCreate(16)
        );

        /*
        Uses Java 8 Streams to execute the simulation scenarios in parallel.
        The map method executes the scenario by calling the run() method and gets the number of executed cloudlets
        for each scenario (that is returned by run()).
        After all scenarios are executed and each number of executed cloudlet
        obtained, the sum() method will get such numbers from the result of the map method
        and sum it to get the total number of executed cloudlets.

        The sum is an terminal operation that just returns when all the scenarios
        have finished. As the Parallel Stream will create one thread for each
        scenarios (that is a fork operation), each simulation may finish in different times.
        Using a terminal operation such as sum() will ensure that the stream
        will wait for all threads to finish (that is a join operation) and get a final result.
        Any terminal operation can be used here just to make the stream to wait
        for all threads to finish.

        This was used because after that, we want to print results just after
        all simulations have finished.
        */
        long startTimeMilliSec = System.currentTimeMillis();
        int totalExecutedCloudlets =
            examples.parallelStream()
                .mapToInt(example -> example.run())
                .sum();

        long finishTimeMilliSec = (System.currentTimeMillis() - startTimeMilliSec);

        Log.enable();
        Log.printFormattedLine("Time to run %d simulations: %d milliseconds", examples.size(), finishTimeMilliSec);
        Log.printFormattedLine(
            "Total number of executed cloudlets in all the %d simulation scenarios: %d",
            examples.size(), totalExecutedCloudlets);

        //With the cloudlet list of all scenarios executed, print such lists
        for (ParallelSimulationsExample example : examples) {
            example.printResults();
        }
    }

    public void printResults(){
        new CloudletsTableBuilderHelper(getFinishedCloudletList())
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
        this.simulation = new CloudSim(numberOfCloudUsers);
    }

    private DatacenterSimple createDatacenter() {
        List<Host> hostList = createHosts();

        //Defines the characteristics of the data center
        double cost = 3.0; // the cost of using processing in this switches
        double costPerMem = 0.05; // the cost of using memory in this switches
        double costPerStorage = 0.001; // the cost of using storage in this switches
        double costPerBw = 0.0; // the cost of using bw in this switches

        DatacenterCharacteristics characteristics =
            new DatacenterCharacteristicsSimple(hostList)
                .setCostPerSecond(cost)
                .setCostPerMem(costPerMem)
                .setCostPerStorage(costPerStorage)
                .setCostPerBw(costPerBw);

        return new DatacenterSimple(simulation, characteristics, new VmAllocationPolicySimple());
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
        int  mips = 1000; // capacity of each CPU core (in Million Instructions per Second)
        long  ram = 2048; // host memory (MB)
        long storage = 1000000; // host storage (MB)
        long bw = 10000; //in Megabits/s
        final int numberOfPes = 4;

        List<Pe> pesList = new ArrayList<>(numberOfPes); //List of CPU cores
        for(int i = 0; i < numberOfPes; i++) {
            pesList.add(new PeSimple(i, new PeProvisionerSimple(mips)));
        }

        return new HostSimple(hostId, storage, pesList)
            .setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
            .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(bw)))
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
        double mips = 1000;
        long   storage = 10000; // vm image size (MB)
        int    ram = 512; // vm memory (MB)
        long   bw = 1000; // vm bandwidth (Megabits/s)
        int    pesNumber = 2; // number of CPU cores

        return new VmSimple(vmId, mips, pesNumber)
            .setBroker(broker)
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

        Cloudlet cloudlet
                = new CloudletSimple(cloudletId, length, numberOfCpuCores)
                        .setCloudletFileSize(fileSize)
                        .setCloudletOutputSize(outputSize)
                        .setUtilizationModel(utilization)
                        .setBroker(broker);

        return cloudlet;
    }

    /**
     * Builds the simulation scenario and starts the simulation.
     * @return the number of executed cloudlets
     */
    public int run() {
        Datacenter datacenter0 = createDatacenter();

        /*Creates a Broker accountable for submission of VMs and Cloudlets
        on behalf of a given cloud user (customer).*/
        broker = new DatacenterBrokerSimple(simulation);

        createVms();
        createCloudlets();

        broker.submitCloudletList(cloudletList);
        /*Starts the simulation and waits all cloudlets to be executed*/
        simulation.start();

        //Finishes the simulation
        simulation.stop();

        this.finishedCloudletList = broker.getCloudletsFinishedList();
        return this.finishedCloudletList.size();
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
