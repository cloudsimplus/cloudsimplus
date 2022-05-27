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
package org.cloudsimplus.examples.simulationstatus;

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
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * An example showing how to terminate the simulation at a given time, before its natural end.
 * The example creates 4 Cloudlets that will run sequentially using a {@link CloudletSchedulerSpaceShared}.
 * However, at the middle of the simulation, after just 2 of these 4 Cloudlets have finished,
 * the simulation will be interrupeted, avoiding the other 2 Cloudlets to run.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 *
 * @see CloudSim#terminateAt(double)
 */
public class TerminateSimulationAtGivenTimeExample {
    private static final int TIME_TO_FINISH_SIMULATION = 21;

    private final CloudSim simulation;
    private List<Cloudlet> cloudletList;
    private List<Vm> vmList;
    private int numberOfCreatedCloudlets = 0;
    private int numberOfCreatedVms = 0;

    public static void main(String[] args) {
        new TerminateSimulationAtGivenTimeExample();
    }

    /**
     * Default constructor that builds the simulation.
     */
    private TerminateSimulationAtGivenTimeExample() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());
        this.simulation = new CloudSim();

        this.vmList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();

        Datacenter datacenter0 = createDatacenter();

        /*Creates a Broker accountable for submission of VMs and Cloudlets
        on behalf of a given cloud user (customer).*/
        DatacenterBroker broker0 = new DatacenterBrokerSimple(simulation);

        Vm vm0 = createVm(broker0);
        this.vmList.add(vm0);
        broker0.submitVmList(vmList);

        for(int i = 0; i < 4; i++) {
            Cloudlet cloudlet = createCloudlet(broker0, vm0);
            this.cloudletList.add(cloudlet);
        }
        broker0.submitCloudletList(cloudletList);

        /* Schedule the simulation termination to the 21st second,
         * after the 2 first Cloudlets have finished.
         * By this way, the other 2 ones will not execute.
         * */
        simulation.terminateAt(TIME_TO_FINISH_SIMULATION);

        /* Starts the simulation and waits all cloudlets to be executed. */
        simulation.start();

        /*Prints results when the simulation is over
        (you can use your own code here to print what you want from this cloudlet list)*/
        List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();
        System.out.println(getClass().getSimpleName() + " finished!");
    }

    private DatacenterSimple createDatacenter() {
        List<Host> hostList = new ArrayList<>();
        Host host0 = createHost();
        hostList.add(host0);

        return new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
    }

    private Host createHost() {
        final long  mips = 1000; // capacity of each CPU core (in Million Instructions per Second)
        final long  ram = 2048; // in Megabytes
        final long storage = 1000000; // in Megabytes
        final long bw = 10000; //in Megabits/s

        final List<Pe> peList = new ArrayList<>(); //List of CPU cores

        /*Creates the Host's CPU cores and defines the provisioner
        used to allocate each core for requesting VMs.*/
        peList.add(new PeSimple(mips, new PeProvisionerSimple()));

        return new HostSimple(ram, bw, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());
    }

    private Vm createVm(DatacenterBroker broker) {
        final long mips = 1000;
        final int    pesNumber = 1; // number of CPU cores

        return new VmSimple(numberOfCreatedVms++, mips, pesNumber)
                .setRam(512)
                .setBw((long) 1000)
                .setSize((long) 10000)
                .setCloudletScheduler(new CloudletSchedulerSpaceShared());
    }

    private Cloudlet createCloudlet(DatacenterBroker broker, Vm vm) {
        final long length = 10000; //in Million Instructions (MI)
        final long  numberOfCpuCores = vm.getNumberOfPes(); //cloudlet will use all the VM's CPU cores

        return new CloudletSimple(
                numberOfCreatedCloudlets++, length, numberOfCpuCores)
                .setFileSize(300)
                .setOutputSize(300)
                .setUtilizationModel(new UtilizationModelFull())
                .setVm(vm);
    }
}
