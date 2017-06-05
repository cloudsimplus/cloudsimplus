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
package org.cloudsimplus.sla;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;

/**
 * This example show an simple example using metrics of quality of service
 * without network.
 *
 * @author RaysaOliveira
 */
public final class SlaMetricsWithoutNetworkExample {

    private static final int HOSTS_NUMBER = 3;
    private static final int HOST_PES = 5;
    private static final int VM_PES1 = 2;
    private static final int VM_PES2 = 4;
    private static final int TOTAL_VM_PES = VM_PES1 + VM_PES2;
    private static final int CLOUDLETS_NUMBER = HOSTS_NUMBER * TOTAL_VM_PES;
    private static final int CLOUDLET_PES = 1;

    private List<Host> hostList;
    private int lastCreatedVmId = 0;

    /**
     * The cloudlet list.
     */
    private final List<Cloudlet> cloudletList;

    /**
     * The vmlist.
     */
    private final List<Vm> vmlist;

    private double taskTimeCompletionCloudlet;
    private double cpuUtilization;
    private double waitTimeCloudlet;
    private final CloudSim cloudsim;

    /**
     * Creates Vms
     *
     * @param broker broker
     * @param numberOfPes number of PEs for each VM to be created
     * @param numberOfVms number of VMs to create
     * @return list de vms
     */
    private List<Vm> createVM(DatacenterBroker broker, int numberOfPes, int numberOfVms) {
        //Creates a container to store VMs.
        List<Vm> list = new ArrayList<>(numberOfVms);

        //VM Parameters
        int vmid = 0;
        long size = 10000; //image size (MEGABYTE)
        int ram = 512; //vm memory (MEGABYTE)
        int mips = 1000;
        long bw = 1000;

        //create VMs with differents configurations
        for (int i = 0; i < numberOfVms; i++) {
            Vm vm = new VmSimple(
                    this.lastCreatedVmId++, mips, numberOfPes)
                    .setRam(ram).setBw(bw).setSize(size)
                    .setCloudletScheduler(new CloudletSchedulerTimeShared());
            list.add(vm);
        }

        return list;
    }

    /**
     * Creates cloudlets
     *
     * @param broker broker id
     * @param cloudlets to criate
     * @return list of cloudlets
     */
    private List<Cloudlet> createCloudlet(DatacenterBroker broker, int cloudlets) {
        // Creates a container to store Cloudlets
        List<Cloudlet> list = new ArrayList<>(cloudlets);

        //Cloudlet Parameters
        long length = 1000;
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        for (int i = 0; i < cloudlets; i++) {
            Cloudlet cloudlet = new CloudletSimple(i, length, CLOUDLET_PES)
                    .setFileSize(fileSize)
                    .setOutputSize(outputSize)
                    .setUtilizationModel(utilizationModel);
            list.add(cloudlet);
        }
        return list;
    }

    /**
     * Calculates the cost price of resources (processing, bw, memory, storage)
     * of each or all of the Datacenter VMs()
     *
     * @param vmlist
     */
    private double totalCostPrice(List<Vm> vmlist) {

        VmCost vmCost;
        double totalCost = 0.0;
        for (Vm vm : vmlist) {
            vmCost = new VmCost(vm);
            totalCost = vmCost.getTotalCost();
        }
        return totalCost;

    }

    /**
     * Shows the TaskTimeCompletion of cloudlets
     *
     * @param cloudlets to calculate the TaskTimeCompletion
     * @return taskTimeCompletionCloudlet
     */
    private double taskTimeCompletionCloudletAverage(List<Cloudlet> cloudlets) {

        double taskTimeCompletion = 0, quant =0;
        for (Cloudlet cloudlet : cloudlets) {
            taskTimeCompletion += cloudlet.getFinishTime() - cloudlet.getLastDatacenterArrivalTime();
            quant = cloudletList.size();
        }
        return taskTimeCompletion/quant;

    }

    /**
     * Shows the cpu utilization
     *
     * @param cloudlet to calculate the utilization
     * @return cpuUtilization
     */
    private double cpuUtilization(List<Cloudlet> cloudlet) {
        double cpuTime = 0;
        for (Cloudlet cloudlets : cloudlet) {
            cpuTime += cloudlets.getActualCpuTime();
        }
        return (cpuTime * 100) / 100;
    }

    /**
     * Shows the utilization resources (BW, CPU, RAM) in percentage
     *
     * @param cloudlet to calculate the utilization resources
     * @return utilizationResources
     */
    public double utilizationResources(List<Cloudlet> cloudlet) {
        double utilizationResources = 0, bw, cpu, ram;
        for (Cloudlet cloudlets : cloudlet) {
            bw = cloudlets.getUtilizationOfBw();
            cpu = cloudlets.getUtilizationOfCpu();
            ram = cloudlets.getUtilizationOfRam();
            utilizationResources += bw + cpu + ram;
        }
        return (utilizationResources * 100) / 100;
    }

    /**
     * Shows the wait time of cloudlets
     *
     * @param cloudlet list of cloudlets
     * @return the waitTimeAverage
     */
    public double waitTimeAverage(List<Cloudlet> cloudlet) {
        double waitTime = 0, quant = 0;
        for (Cloudlet cloudlets : cloudlet) {
            waitTime += cloudlets.getWaitingTime();
            quant++;
        }
        return waitTime/quant;
    }

    /**
     * Gets the bandwidht total utilization of VMs.
     * @param vmlist
     * @return the totalBwUtilization
     */
    private double bwVmAverage(List<Vm> vmlist){
        double totalBwUtilization = 0;
        int quantVm = vmlist.size();
        for(Vm vm: vmlist){
            totalBwUtilization += vm.getBw().getAllocatedResource();
            System.out.println("\n\n -> " + totalBwUtilization);
        }
        return totalBwUtilization/quantVm;
    }


    public static void main(String[] args) throws FileNotFoundException {
        Log.printFormattedLine(" Starting... ");
        new SlaMetricsWithoutNetworkExample();
    }

    public SlaMetricsWithoutNetworkExample() throws FileNotFoundException {
        cloudsim = new CloudSim();

        //Create Datacenters
        Datacenter datacenter0 = createDatacenter();

        //Create Broker
        DatacenterBroker broker = createBroker();

        vmlist = new ArrayList<>();
        vmlist.addAll(createVM(broker, VM_PES1, 2));
        vmlist.addAll(createVM(broker, VM_PES2, 2));

        // submit vm list to the broker
        broker.submitVmList(vmlist);

        cloudletList = createCloudlet(broker, CLOUDLETS_NUMBER);

        // submit cloudlet list to the broker
        broker.submitCloudletList(cloudletList);

        // Sixth step: Starts the simulation
        cloudsim.start();

        System.out.println("________________________________________________________________");
        System.out.println("\n\t\t - System Métrics - \n ");

        //TaskTimeCompletion
        taskTimeCompletionCloudlet = taskTimeCompletionCloudletAverage(cloudletList);
        System.out.printf("\t** TaskTimeCompletion of Cloudlets (average) - %.2f %n", taskTimeCompletionCloudlet);

        //cpu time
        cpuUtilization = cpuUtilization(cloudletList);
        System.out.printf("\t** Utilization CPU %% - %.2f %n ", cpuUtilization);

        //utilization resource
        double time = cloudsim.clock();
        double utilizationresources = utilizationResources(cloudletList);
        System.out.printf("\t** Utilization Resources %%  (Bw-CPU-Ram) - %.2f %n", utilizationresources);

        //wait time
        waitTimeCloudlet = waitTimeAverage(cloudletList);
        System.out.printf("\t** Wait Time (average) - %.2f %n", waitTimeCloudlet);

        // total cost
        double totalCost = totalCostPrice(vmlist);
        System.out.println("\t** Total cost (memory, bw, processing, storage) - " + totalCost);

        // bw cost
        double bw = bwVmAverage(vmlist);
        System.out.println("\t** BW  " + bw);

        System.out.println("________________________________________________________________");

        //Final step: Print results when simulation is over
        List<Cloudlet> newList = broker.getCloudletFinishedList();
        new CloudletsTableBuilder(newList).build();

        Log.printFormattedLine("... finished!");
    }

    /**
     * Creates the DATACENTER.
     *
     * @return the dc
     */
    private Datacenter createDatacenter() {
        hostList = new ArrayList<>();

        int mips = 10000;
        int hostId = 0;
        int ram = 8192; // host memory (MEGABYTE)
        long storage = 1000000; // host storage
        long bw = 100000;

        for (int i = 0; i < HOSTS_NUMBER; i++) {
            List<Pe> peList = createHostPesList(HOST_PES, mips);
            Host host = new HostSimple(ram, bw, storage, peList)
                    .setRamProvisioner(new ResourceProvisionerSimple())
                    .setBwProvisioner(new ResourceProvisionerSimple())
                    .setVmScheduler(new VmSchedulerTimeShared());

            getHostList().add(host);
        }// This is our machine

        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this
        // resource
        double costPerBw = 0.0; // the cost of using bw in this resource

        DatacenterCharacteristics characteristics
                = new DatacenterCharacteristicsSimple(hostList)
                .setCostPerSecond(cost)
                .setCostPerMem(costPerMem)
                .setCostPerStorage(costPerStorage)
                .setCostPerBw(costPerBw);

        return new DatacenterSimple(cloudsim, characteristics,
                new VmAllocationPolicySimple());
    }

    public List<Pe> createHostPesList(int hostPes, int mips) {
        List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < hostPes; i++) {
            peList.add(new PeSimple(mips, new PeProvisionerSimple())); // need to store Pe id and MIPS Rating
        }
        return peList;
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
     * return the hostList
     *
     * @return
     */
    private List<Host> getHostList() {
        return hostList;
    }

}
