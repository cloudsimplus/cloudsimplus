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
package org.cloudsimplus.examples.listeners;

/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristicsSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudsimplus.listeners.EventListener;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelStochastic;

/**
 * A simple example showing how to create a data center with 1 host and run
 * 1 cloudlet on it. The example uses the new Cloudlet listeners
 * to get notified every time a cloudlet has its processing updated
 * inside a Vm and then, the current cloudlet resource usage is shown.
 * The example uses the {@link UtilizationModelStochastic}
 * to define that the usage of CPU, RAM and Bandwidth is random.
 *
 * @see Cloudlet#addOnUpdateProcessingListener(EventListener)
 * @see EventListener
 *
 * @author Manoel Campos da Silva Filho
 */
public class CloudletListenersExample2_ResourceUsageAlongTime {
    /**
     * Number of Processor Elements (CPU Cores) of each Host.
     */
    private static final int HOST_PES_NUMBER = 1;

    /**
     * Number of Processor Elements (CPU Cores) of each VM and cloudlet.
     */
    private static final int VM_PES_NUMBER = HOST_PES_NUMBER;

    /**
     * Number of Cloudlets to create.
     */
    private static final int NUMBER_OF_CLOUDLETS = 2;

    private static final double DATACENTER_SCHEDULING_INTERVAL = 1;

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
        Log.printFormattedLine("Starting %s ...", CloudletListenersExample2_ResourceUsageAlongTime.class.getSimpleName());
        new CloudletListenersExample2_ResourceUsageAlongTime();
        Log.printFormattedLine("%s finished!", CloudletListenersExample2_ResourceUsageAlongTime.class.getSimpleName());
    }

    /**
     * Default constructor that builds and starts the simulation.
     */
    public CloudletListenersExample2_ResourceUsageAlongTime() {
        simulation = new CloudSim();

        this.hostList = new ArrayList<>();
        this.vmList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();
        this.datacenter = createDatacenter();
        this.broker = new DatacenterBrokerSimple(simulation);

        createAndSubmitVms();
        createAndSubmitCloudlets(this.vmList.get(0));

        runSimulationAndPrintResults();
    }

    /**
     * A Listener function that will be called everytime when a the processing of a cloudlet
     * is updated into a VM. All cloudlets will use this same listener.
     *
     * @param eventInfo information about the happened event
     * @see #createCloudlet(int, Vm, long)
     */
    private void onUpdateCloudletProcessingListener(CloudletVmEventInfo eventInfo) {
        Cloudlet c = eventInfo.getCloudlet();
        double cpuUsage = c.getUtilizationModelCpu().getUtilization(eventInfo.getTime())*100;
        double ramUsage = c.getUtilizationModelRam().getUtilization(eventInfo.getTime())*100;
        double bwUsage  = c.getUtilizationModelBw().getUtilization(eventInfo.getTime())*100;
        Log.printFormattedLine(
                "\t#EventListener: Time %.0f: Updated Cloudlet %d execution inside Vm %d",
                eventInfo.getTime(), c.getId(), eventInfo.getVm().getId());
        Log.printFormattedLine(
                "\tCurrent Cloudlet resource usage: CPU %3.0f%%, RAM %3.0f%%, BW %3.0f%%\n",
                cpuUsage,  ramUsage, bwUsage);
    }

    private void runSimulationAndPrintResults() {
        simulation.start();

        List<Cloudlet> finishedCloudlets = broker.getCloudletsFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();
    }

    /**
     * Creates cloudlets and submit them to the broker.
     * @param vm Vm to run the cloudlets to be created
     *
     * @see #createCloudlet(int, Vm, long)
     */
    private void createAndSubmitCloudlets(Vm vm) {
        int cloudletId;
        long length = 10000;
        for(int i = 0; i < NUMBER_OF_CLOUDLETS; i++){
            cloudletId = vm.getId() + i;
            Cloudlet cloudlet = createCloudlet(cloudletId, vm, length);
            this.cloudletList.add(cloudlet);
        }

        this.broker.submitCloudletList(cloudletList);
    }

    /**
     * Creates VMs and submit them to the broker.
     */
    private void createAndSubmitVms() {
        Vm vm0 = createVm(0);
        this.vmList.add(vm0);
        this.broker.submitVmList(vmList);
    }

    /**
     * Creates a VM with pre-defined configuration.
     *
     * @param id the VM id
     * @return the created VM
     */
    private Vm createVm(int id) {
        int mips = 1000;
        long size = 10000; // image size (MEGABYTE)
        int ram = 512; // vm memory (MEGABYTE)
        long bw = 1000;

        Vm vm = new VmSimple(id, mips, VM_PES_NUMBER)
                .setBroker(broker)
                .setRam(ram).setBw(bw).setSize(size)
                .setCloudletScheduler(new CloudletSchedulerSpaceShared());
        return vm;
    }

    /**
     * Creates a cloudlet with pre-defined configuration.
     *
     * @param id Cloudlet id
     * @param vm vm to run the cloudlet
     * @param length the cloudlet length in number of Million Instructions (MI)
     * @return the created cloudlet
     */
    private Cloudlet createCloudlet(int id, Vm vm, long length) {
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;

        /*Define that the utilization of CPU, RAM and Bandwidth is random.*/
        UtilizationModel cpuUtilizationModel = new UtilizationModelStochastic();
        UtilizationModel ramUtilizationModel = new UtilizationModelStochastic();
        UtilizationModel bwUtilizationModel  = new UtilizationModelStochastic();

        Cloudlet cloudlet =
            new CloudletSimple(id, length, pesNumber)
                .setFileSize(fileSize)
                .setOutputSize(outputSize)
                .setUtilizationModelCpu(cpuUtilizationModel)
                .setUtilizationModelRam(ramUtilizationModel)
                .setUtilizationModelBw(bwUtilizationModel)
                .setBroker(broker)
                .setVm(vm)
                .addOnUpdateProcessingListener(this::onUpdateCloudletProcessingListener);

        return cloudlet;
    }

    /**
     * Creates a Datacenter with pre-defined configuration.
     *
     * @return the created Datacenter
     */
    private Datacenter createDatacenter() {
        Host host = createHost(0);
        hostList.add(host);

        double cost = 3.0; // the cost of using processing in this resource
        double costPerMem = 0.05; // the cost of using memory in this resource
        double costPerStorage = 0.001; // the cost of using storage in this Datacenter
        double costPerBw = 0.0; // the cost of using bw in this resource

        DatacenterCharacteristics characteristics =
            new DatacenterCharacteristicsSimple(hostList)
                .setCostPerSecond(cost)
                .setCostPerMem(costPerMem)
                .setCostPerStorage(costPerStorage)
                .setCostPerBw(costPerBw);

        return new DatacenterSimple(
                simulation, characteristics,new VmAllocationPolicySimple())
                .setSchedulingInterval(DATACENTER_SCHEDULING_INTERVAL);
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
        long ram = 2048; // host memory (MEGABYTE)
        long storage = 1000000; // host storage (MEGABYTE)
        long bw = 10000; //Megabits/s

        return new HostSimple(id, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple(new Ram(ram)))
            .setBwProvisioner(new ResourceProvisionerSimple(new Bandwidth(bw)))
            .setVmScheduler(new VmSchedulerTimeShared());
    }
}
