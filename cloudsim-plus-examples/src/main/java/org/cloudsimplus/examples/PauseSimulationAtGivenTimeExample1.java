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

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
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
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * An example showing how to pause the simulation at a given time in order to collect
 * some partial results. In this example, such results are the cloudlets that have finished so far.
 * The example creates 4 Cloudlets that will run sequentially using a {@link CloudletSchedulerSpaceShared}.
 * The pause is scheduled before the simulation starts, that is the easier way to perform
 * such an action.
 *
 * <p>This example uses CloudSim Plus Listener features to intercept when
 * the simulation was paused, allowing to collect the desired data.
 * This example uses the Java 8 Lambda Functions features
 * to pass a listener to a {@link CloudSim} instance, by means of the
 * {@link CloudSim#addOnSimulationPausedListener(EventListener)} method.
 * However, the same feature can be used for Java 7 passing an anonymous class
 * that implements {@code EventListener<EventInfo>}.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 *
 * @see CloudSim#pause(double)
 * @see CloudSim#addOnSimulationPausedListener(EventListener)
 * @see EventListener
 */
public class PauseSimulationAtGivenTimeExample1 {
    /**
     * The interval in which the Datacenter will schedule events.
     * As lower is this interval, sooner the processing of VMs and Cloudlets
     * is updated and you will get more notifications about the simulation execution.
     * However, as higher is this value, it can affect the simulation performance.
     *
     * <p>For this example, a large schedule interval such as 15 will make that just
     * at every 15 seconds the processing of Cloudlets is updated.
     * Consider that each Cloudlet takes 10 seconds to finish and that
     * the simulation is paused at time 22. With a scheduling interval of 15 seconds,
     * at the time 22, the Cloudlets execution will be updated just 1 time, that means
     * if we get the list of finished cloudlets at time 22, it will not be updated yet with
     * the second Cloudlet that finished at time 20.</p>
     *
     * <p>Considering this characteristics, the scheduling interval
     * was set to a lower value to get updates as soon as possible.
     * For more details, see {@link Datacenter#getSchedulingInterval()}.</p>
     */
    public static final int SCHEDULING_INTERVAL = 1;

    private final CloudSim simulation;
    private final DatacenterBrokerSimple broker;
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
        new PauseSimulationAtGivenTimeExample1();
    }

    /**
     * Default constructor that builds the simulation.
     */
    public PauseSimulationAtGivenTimeExample1() {
        Log.printFormattedLine("Starting %s ...", getClass().getSimpleName());
        this.vmList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();
        this.simulation = new CloudSim();

        Datacenter datacenter0 = createDatacenter();

        /*
        Creates a Broker accountable for submission of VMs and Cloudlets
        on behalf of a given cloud user (customer).
        */
        this.broker = new DatacenterBrokerSimple(simulation);

        Vm vm0 = createVm();
        this.vmList.add(vm0);
        this.broker.submitVmList(vmList);

        for(int i = 0; i < 4; i++) {
            Cloudlet cloudlet = createCloudlet(vm0);
            this.cloudletList.add(cloudlet);
        }

        this.broker.submitCloudletList(cloudletList);

        /*
        Requests the simulation to be paused at the 22nd second.
        The method schedules the simulation pause for a future time and returns immediately.
        Since the start() method blocks untiil the simulation is finished,
        the request to pause the simulation at a given time has to be scheduled
        before the simulation start.
        Otherwise, more complex mechanisms have to be used
        to schedule a pause when the simulation is already running.
        */
        this.simulation.pause(22);

        /*
        * Sets a Listener that will be notified when the simulation is paused.
        * Realise that it is being used Java 8 Lambda Expressions to define a Listener
        * that will be executed only when the simulation is paused.
        * */
        this.simulation
            .addOnSimulationPausedListener(pauseInfo -> printCloudletsFinishedSoFarAndResumeSimulation(pauseInfo));

        /* Starts the simulation and waits all cloudlets to be executed. */
        this.simulation.start();

        /*Prints results when the simulation is over
        (you can use your own code here to print what you want from this cloudlet list)*/
        printsListOfFinishedCloudlets("Finished cloudlets after simulation is complete");

        Log.printConcatLine(getClass().getSimpleName(), " finished!");
    }

    private void printCloudletsFinishedSoFarAndResumeSimulation(EventInfo pauseInfo) {
        Log.printFormattedLine("\n#Simulation paused at %.2f second", pauseInfo.getTime());
        printsListOfFinishedCloudlets("Cloudlets Finished So Far");
        this.simulation.resume();
    }

    private void printsListOfFinishedCloudlets(String title) {
        //Gets the list of cloudlets finished so far a prints
        new CloudletsTableBuilder(broker.getCloudletFinishedList())
            .setTitle(title)
            .build();
    }

    /**
     * Checks if the Cloudlet that had its processing updated reached 50% of execution.
     * If so, request the simulation interruption.
     * @param event object containing data about the happened event
     */
    private void onClouletProcessingUpdate(CloudletVmEventInfo event) {
        if(event.getCloudlet().getFinishedLengthSoFar() >= event.getCloudlet().getLength()/2.0){
            Log.printFormattedLine("Cloudlet %d reached 50% of execution. Intentionally requesting termination of the simulation at time %.2f",
                event.getCloudlet().getId(), simulation.clock());
            simulation.terminate();
        }
    }

    private DatacenterSimple createDatacenter() {
        List<Host> hostList = new ArrayList<>();
        Host host0 = createHost();
        hostList.add(host0);

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

        DatacenterSimple dc = new DatacenterSimple(simulation, characteristics, new VmAllocationPolicySimple());
        dc.setSchedulingInterval(SCHEDULING_INTERVAL);
        return dc;
    }

    private Host createHost() {
        long mips = 1000; // capacity of each CPU core (in Million Instructions per Second)
        long ram = 2048; // host memory (MEGABYTE)
        long storage = 1000000; // host storage (MEGABYTE)
        long bw = 10000; //in Megabits/s

        List<Pe> peList = new ArrayList<>(); //List of CPU cores

        /*Creates the Host's CPU cores and defines the provisioner
        used to allocate each core for requesting VMs.*/
        peList.add(new PeSimple(mips, new PeProvisionerSimple()));

        return new HostSimple(ram, bw, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
                .setVmScheduler(new VmSchedulerTimeShared());
    }

    private Vm createVm() {
        long   mips = 1000;
        long   storage = 10000; // vm image size (MEGABYTE)
        int    ram = 512; // vm memory (MEGABYTE)
        long   bw = 1000; // vm bandwidth (Megabits/s)
        int    pesNumber = 1; // number of CPU cores

        return new VmSimple(numberOfCreatedVms++, mips, pesNumber)
                .setRam(ram)
                .setBw(bw)
                .setSize(storage)
                .setCloudletScheduler(new CloudletSchedulerSpaceShared());
    }

    private Cloudlet createCloudlet(Vm vm) {
        long length = 10000; //in Million Structions (MI)
        long fileSize = 300; //Size (in bytes) before execution
        long outputSize = 300; //Size (in bytes) after execution
        long  numberOfCpuCores = vm.getNumberOfPes(); //cloudlet will use all the VM's CPU cores

        //Defines how CPU, RAM and Bandwidth resources are used
        //Sets the same utilization model for all these resources.
        UtilizationModel utilization = new UtilizationModelFull();

        return new CloudletSimple(
                numberOfCreatedCloudlets++, length, numberOfCpuCores)
                .setFileSize(fileSize)
                .setOutputSize(outputSize)
                .setUtilizationModel(utilization)
                .setVm(vm);
    }

}
