/**
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudsimplus.sla;

import org.cloudbus.cloudsim.distributions.PoissonDistr;
import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

/**
 * This simple example show how to create cloudlets randomly using poisson
 * distribution.
 *
 * @author raysaoliveira
 */
public class CreateCloudletRandomlyExample {

    /**
     * List of Cloudlet .
     */
    private final List<Cloudlet> cloudletList;

    /**
     * List of Vms
     */
    private final List<Vm> vmlist;

    /**
     * Average number of customers that arrives per minute. The value of 0.4
     * customers per minute means that 1 customer will arrive at every 2.5
     * minutes. It means that 1 minute / 0.4 customer per minute = 1 customer at
     * every 2.5 minutes. This is the interarrival time (in average).
     */
    private static final double MEAN_CUSTOMERS_ARRIVAL_PER_MINUTE = 0.4;

    /**
     * Number of simulations to run.
     */
    private static final int NUMBER_OF_SIMULATIONS = 1;

    /**
     * The maximum time that a Cloudlet can arrive. Between the first simulation
     * minute and this time, different Cloudlets can arrive.
     */
    private final int MAX_TIME_FOR_CLOUDLET_ARRIVAL = 100;
    private final CloudSim cloudsim;

    /**
     * Create Vms
     *
     * @param broker
     * @param vms
     * @return list de vms
     */
    private List<Vm> createVM(DatacenterBroker broker, int vms) {
        //Creates a container to store VMs. This list is passed to the broker later
        List<Vm> list = new ArrayList<>(vms);
        //VM Parameters
        long size = 10000; //image size (MEGABYTE)
        int ram = 512; //vm memory (MEGABYTE)
        int mips = 1000;
        long bw = 1000;
        int pesNumber = 1; //number of cpus
        String vmm = "Xen"; //VMM name

        for (int i = 0; i < vms; i++) {
            Vm vm = new VmSimple(i, mips, pesNumber)
                    .setRam(ram).setBw(bw).setSize(size)
                    .setCloudletScheduler(new CloudletSchedulerTimeShared());
            list.add(vm);
        }
        return list;
    }

    public static void main(String[] args) {
        Log.printFormattedLine(" Starting... ");
        for (int i = 0; i < NUMBER_OF_SIMULATIONS; i++) {
            new CreateCloudletRandomlyExample();
        }
        Log.printFormattedLine("... finished!");
    }

    public CreateCloudletRandomlyExample() {
        int num_user = 1; // number of cloud users

        this.cloudsim = new CloudSim();

        // Second step: Create Datacenters
        Datacenter datacenter0 = createDatacenter();

        // Third step: Create Broker
        DatacenterBroker broker = createBroker();

        //create cloudlet randomly
        cloudletList = new ArrayList<>();
        long seed = System.currentTimeMillis();
        //creates a poisson process that checks the arrival of 1 (k) cloudlet
        //1 is the default value for k
        PoissonDistr poisson = new PoissonDistr(MEAN_CUSTOMERS_ARRIVAL_PER_MINUTE, seed);
        int totalArrivedCustomers = 0;
        int cloudletId = 0;
        for (int minute = 0; minute < MAX_TIME_FOR_CLOUDLET_ARRIVAL; minute++) {
            if (poisson.eventsHappened()) { //Have k Cloudlets arrived?
                totalArrivedCustomers += poisson.getK();
                Cloudlet cloudlet = createCloudlet(cloudletId++, broker);
                cloudlet.setSubmissionDelay(minute);
                cloudletList.add(cloudlet);

                System.out.printf("%d cloudlets arrived at minute %d\n", poisson.getK(), minute);
            }
        }

        System.out.printf("\n\t%d cloudlets have arrived\n", totalArrivedCustomers);

        broker.submitCloudletList(cloudletList);

        vmlist = createVM(broker, totalArrivedCustomers);

        // submit vm list to the broker
        broker.submitVmList(vmlist);

        cloudsim.start();

        //Final step: Print results when simulation is over
        List<Cloudlet> newList = broker.getCloudletFinishedList();
        new CloudletsTableBuilder(newList).build();

    }

    private Cloudlet createCloudlet(int cloudletId, DatacenterBroker broker) {
        long length = 1000;
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();
        Cloudlet cloudlet = new CloudletSimple(cloudletId, length, pesNumber)
                .setFileSize(fileSize)
                .setOutputSize(outputSize)
                .setUtilizationModel(utilizationModel);
        return cloudlet;
    }

    private Datacenter createDatacenter() {
        // Here are the steps needed to create a PowerDatacenter:
        // 1. We need to create a list to store
        // our machine
        List<Host> hostList = new ArrayList<>();

        // 2. A Machine contains one or more PEs or CPUs/Cores.
        // In this example, it will have only one core.
        List<Pe> peList = new ArrayList<>();

        long mips = 30000000;

        // 3. Create PEs and add these into a list.
        peList.add(new PeSimple(mips, new PeProvisionerSimple())); // need to store Pe id and MIPS Rating
        // 4. Create Host with its id and list of PEs and add them to the list
        // of machines
        int hostId = 0;
        int ram = 1000000; // host memory (MEGABYTE)
        long storage = 100000000; // host storage
        long bw = 3000000;

        Host host = new HostSimple(ram, bw, storage, peList)
                .setRamProvisioner(new ResourceProvisionerSimple())
                .setBwProvisioner(new ResourceProvisionerSimple())
                .setVmScheduler(new VmSchedulerTimeShared());

        hostList.add(host);

        // 5. Create a DatacenterCharacteristics object that stores the
        // properties of a data center: architecture, OS, list of
        // Machines, allocation policy: time- or space-shared, time zone
        // and its price (G$/Pe time unit).
        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this
        // resource
        double costPerBw = 0.0; // the cost of using bw in this resource

        DatacenterCharacteristics characteristics =
                new DatacenterCharacteristicsSimple(hostList)
                .setCostPerSecond(cost)
                .setCostPerMem(costPerMem)
                .setCostPerStorage(costPerStorage)
                .setCostPerBw(costPerBw);

        return new DatacenterSimple(cloudsim, characteristics, new VmAllocationPolicySimple());
    }

    /**
     * Creates the broker.
     *
     * @return the Datacenter broker
     */
    private DatacenterBroker createBroker() {
        return new DatacenterBrokerSimple(cloudsim);
    }

    /**
     * @return the MAX_TIME_FOR_CLOUDLET_ARRIVAL
     */
    public int getMAX_TIME_FOR_CLOUDLET_ARRIVAL() {
        return MAX_TIME_FOR_CLOUDLET_ARRIVAL;
    }

}
