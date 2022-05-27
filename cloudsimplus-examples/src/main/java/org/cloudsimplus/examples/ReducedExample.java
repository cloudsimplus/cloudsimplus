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

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.List;

/**
 * An example showing a very minimal code required to create
 * a simulation scenario in CloudSim Plus.
 *
 * <p><b>NOTICE: This example is not intended to be reused and we strongly recommend
 * you not doing that. Since all the code is inside a single method,
 * that is completely unappropriated.</b></p>
 *
 * <p>This code has the <b>only intention</b> to show how it's simpler
 * and easier to create cloud computing simulations using CloudSim Plus.</p>
 *
 * <p>If you want a basic, but organized and reusable first example,
 * see the {@link BasicFirstExample}.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 * @see BasicFirstExample
 */
class ReducedExample {
    public static void main(String[] args) {
        //tag::cloudsim-plus-reduced-example[]
        //Enables just some level of log messages
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        //Creates a CloudSim object to initialize the simulation.
        final var simulation = new CloudSim();

        //Creates a Broker that will act on behalf of a cloud user (customer).
        final var broker0 = new DatacenterBrokerSimple(simulation);

        //Host configuration
        final long ram = 10000; //in Megabytes
        final long storage = 100000; //in Megabytes
        final long bw = 100000; //in Megabits/s

        //Creates one Hosts with a specific list of CPU cores (PEs).
        //Uses a PeProvisionerSimple by default to provision PEs for VMs.
        //Uses ResourceProvisionerSimple by default for RAM and BW provisioning
        //Uses VmSchedulerSpaceShared by default for VM scheduling
        final var host0 = new HostSimple(ram, bw, storage, List.of(new PeSimple(20000)));

        //Creates one Datacenter with a list of Hosts
        //Uses a VmAllocationPolicySimple by default to allocate VMs
        final var dc0 = new DatacenterSimple(simulation, List.of(host0));

        //Creates one Vm to run applications (Cloudlets).
        //Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets.
        final var vm0 = new VmSimple(1000, 1);
        vm0.setRam(1000).setBw(1000).setSize(1000);
        final var vmList = List.of(vm0);

        //Creates two Cloudlets that represent applications to be run inside a Vm.
        //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
        final var utilizationModel = new UtilizationModelDynamic(0.5);
        final var cloudlet0 = new CloudletSimple(10000, 1, utilizationModel);
        final var cloudlet1 = new CloudletSimple(10000, 1, utilizationModel);
        final var cloudletList = List.of(cloudlet0, cloudlet1);

        /*Requests the broker to create the Vms and Cloudlets.
        It selects the Host to place each Vm and a Vm to run each Cloudlet.*/
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        /*Starts the simulation and waits all cloudlets to be executed, automatically
        stopping when there is no more events to process.*/
        simulation.start();

        /*Prints the results when the simulation is over (one can use his/her own code
        here to print what he/she wants from this cloudlet list). */
        new CloudletsTableBuilder(broker0.getCloudletFinishedList()).build();
        //end::cloudsim-plus-reduced-example[]
    }
}
