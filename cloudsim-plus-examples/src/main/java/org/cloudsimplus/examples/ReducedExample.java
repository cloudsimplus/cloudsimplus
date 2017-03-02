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

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
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
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * An example showing the most minimal code required to create
 * a simulation scenario in CloudSim Plus.
 *
 * <p><b>NOTICE: This example is not intended to be reused and we strongly recommend
 * you not doing that, since all the code was put inside a single method, the main
 * method, that is completely unappropriated.</b></p>
 *
 * <p>This code has the only intention to show how it is simpler
 * and easier to create cloud computing simulations using CloudSim Plus.</p>
 *
 * <p>If you want a first basic, but yet organized and reusable example,
 * see the {@link BasicFirstExample}.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
class ReducedExample {
    public static void main(String[] args) {
        //tag::cloudsim-plus-reduced-example[]
        //Creates a CloudSim object to initialize the simulation.
        CloudSim cloudsim = new CloudSim();

        //Creates a Broker that will act on behalf of a cloud user (customer).
        DatacenterBroker broker0 = new DatacenterBrokerSimple(cloudsim);

        //Creates one Hosts with a specific list of CPU cores (PEs).
        List<Host> hostList = new ArrayList<>(1);
        List<Pe> hostPes = new ArrayList<>(1);
        hostPes.add(new PeSimple(20000, new PeProvisionerSimple()));
        Host host0 = new HostSimple(0, 100000, hostPes);
        host0.setRamProvisioner(new ResourceProvisionerSimple(new Ram(10000)))
             .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(100000)))
             .setVmScheduler(new VmSchedulerSpaceShared());
        hostList.add(host0);

        //Creates one Datacenter with a list of Hosts.
        DatacenterCharacteristics characts = new DatacenterCharacteristicsSimple(hostList);
        VmAllocationPolicy vmAllocationPolicy = new VmAllocationPolicySimple();
        Datacenter dc0 = new DatacenterSimple(cloudsim, characts, vmAllocationPolicy);

        //Creates one Vm to run applications (Cloudlets).
        List<Vm> vmList = new ArrayList<>(1);
        Vm vm0 = new VmSimple(0, 1000, 1);
        vm0.setRam(1000).setBw(1000).setSize(1000)
           .setBroker(broker0)
           .setCloudletScheduler(new CloudletSchedulerSpaceShared());
        vmList.add(vm0);

        //Creates two Cloudlets that represent applications to be run inside a Vm.
        List<Cloudlet> cloudlets = new ArrayList<>(1);
        Cloudlet cloudlet0 = new CloudletSimple(0, 10000, 1);
        cloudlet0.setBroker(broker0).setUtilizationModel(new UtilizationModelFull());
        Cloudlet cloudlet1 = new CloudletSimple(1, 10000, 1);
        cloudlet1.setBroker(broker0).setUtilizationModel(new UtilizationModelFull());
        cloudlets.add(cloudlet0);
        cloudlets.add(cloudlet1);

        /*Requests the broker to create the Vms and Cloudlets.
        It selects the Host to place each Vm and a Vm to run each Cloudlet.*/
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudlets);

        /*Starts the simulation and waits all cloudlets to be executed, automatically
        stopping when there is no more events to process.*/
        cloudsim.start();

        /*Prints results when the simulation is over (one can use his/her own code
        here to print what he/she wants from this cloudlet list)*/
        new CloudletsTableBuilder(broker0.getCloudletsFinishedList()).build();
        //end::cloudsim-plus-reduced-example[]
    }
}
