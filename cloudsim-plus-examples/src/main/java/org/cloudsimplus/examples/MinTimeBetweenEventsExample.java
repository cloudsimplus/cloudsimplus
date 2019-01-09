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
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerCompletelyFair;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.TableColumn;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * An example showing how the {@link Simulation#getMinTimeBetweenEvents()} may affect the processing
 * of events in your simulation and cause some events to be missed.
 * This way, some Cloudlets may not be totally processed and never finish.
 * The issue happens when the time between some events is smaller than the
 * {@link Simulation#getMinTimeBetweenEvents()}.
 * Therefore, you may need to fine tune such attribute according to your simulation.
 *
 * <p>This example reproduces the configuration
 * <a href="https://github.com/manoelcampos/cloudsim-plus/issues/163">Issue #163</a>.
 * The documentation of the {@link #MIN_TIME_BETWEEN_EVENTS} constant below shows how
 * to configure the attribute to make CloudSim Plus process all events
 * in this scenario and then all submitted Cloudlets be finished.
 * </p>
 *
 * @author Natthasak Vechprasit
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.7
 */
public class MinTimeBetweenEventsExample {
    /**
     * The minimum time between an event and the previous one to get the current event to be processed.
     * <b>If two events happen in a time interval smaller than this value, the last event won't be processed</b>.
     *
     * <p>Changing this constant to the default 0.1 value, events happening with a interval
     * smaller than this value will be ignored. This way, the simulation is finished
     * without processing those events.</p>
     *
     * <p>You can check that the difference from the finish time of some Cloudlet
     * and the start time of the next one is smaller than 0.1.
     * Changing this constant to 0.01 fix the issue.</p>
     *
     * @see CloudSim#getMinTimeBetweenEvents()
     */
    private static final double MIN_TIME_BETWEEN_EVENTS = 0.1;

    private static final int HOST_PES_NUMBER = 4;
    private static final int VM_PES_NUMBER = HOST_PES_NUMBER;
    private static final int CLOUDLETS = 6;

    private final List<Host> hostList;
    private final List<Vm> vmList;
    private final List<Cloudlet> cloudletList;
    private final DatacenterBroker broker;
    private final Datacenter datacenter;
    private final CloudSim simulation;
    private final ContinuousDistribution random;

    public static void main(String[] args) {
        new MinTimeBetweenEventsExample();
    }

    private MinTimeBetweenEventsExample() {
        Log.setLevel(Level.WARN);

        System.out.println("Starting " + getClass().getSimpleName());
        simulation = new CloudSim(MIN_TIME_BETWEEN_EVENTS);

        this.hostList = new ArrayList<>();
        this.vmList = new ArrayList<>();
        this.cloudletList = new ArrayList<>();
        this.datacenter = createDatacenter();
        this.broker = new DatacenterBrokerSimple(simulation);

        Vm vm = createAndSubmitVm();
        final long seed = 1547040598054L;
        random =  new UniformDistr(seed);
        createAndSubmitCloudlets(vm);

        runSimulationAndPrintResults();
    }

    private Datacenter createDatacenter() {
        hostList.add(createHost());
        return new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
    }

    private Vm createAndSubmitVm() {
        final List<Vm> list = new ArrayList<>();
        final Vm vm = createVm(this.vmList.size());
        list.add(vm);

        broker.submitVmList(list);
        this.vmList.addAll(list);

        return vm;
    }

    private Vm createVm(final int id) {
        final int mips = 10000;
        final long size = 10000; // image size (Megabyte)
        final int ram = 4096;    // vm memory (Megabyte)
        final long bw = 1000;

        return new VmSimple(id, mips, VM_PES_NUMBER)
            .setRam(ram).setBw(bw).setSize(size)
            .setCloudletScheduler(new CloudletSchedulerCompletelyFair());
    }

    /**
     * Creates a list of Cloudlets to be run inside a given VM
     * @param vm the VM to run the Cloudlets
     */
    private void createAndSubmitCloudlets(final Vm vm) {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);

        for (int i = 0; i < CLOUDLETS; i++) {
            list.add(createCloudlet(vm));
        }

        broker.submitCloudletList(list);
        cloudletList.addAll(list);
    }

    /**
     * Creates a cloudlet with random submission delay for a given VM.
     * @param vm the VM to run the Cloudlet
     * @return the created Cloudlet
     */
    private Cloudlet createCloudlet(final Vm vm) {
        final long fileSize = 300;   //in bytes
        final long outputSize = 300; //in bytes
        final long length = 100;     //in number of Million Instructions (MI)
        final int pesNumber = 1;
        final UtilizationModel utilizationModel = new UtilizationModelDynamic(0.2);

        final Cloudlet cloudlet = new CloudletSimple(length, pesNumber)
            .setFileSize(fileSize)
            .setOutputSize(outputSize)
            .setUtilizationModelCpu(new UtilizationModelFull())
            .setUtilizationModelRam(utilizationModel)
            .setUtilizationModelBw(utilizationModel)
            .setVm(vm);

        final double delay = random.sample();
        cloudlet.setSubmissionDelay(delay);

        return cloudlet;
    }

    private Host createHost() {
        final long mips = 10000;
        final List<Pe> peList = new ArrayList<>();
        for (int i = 0; i < HOST_PES_NUMBER; i++) {
            PeSimple peSimple = new PeSimple(mips, new PeProvisionerSimple());
            peList.add(peSimple);
        }

        final long ram = 4096; // host memory (Megabyte)
        final long storage = 1000000; // host storage (Megabyte)
        final long bw = 10000; //Megabits/s

        return new HostSimple(ram, bw, storage, peList)
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerSpaceShared());
    }

    private void runSimulationAndPrintResults() {
        simulation.start();

        broker.getCloudletSubmittedList().sort(Comparator.comparingDouble(Cloudlet::getExecStartTime));
        final CloudletsTableBuilder builder = new CloudletsTableBuilder(broker.getCloudletSubmittedList());
        builder
            .column(9,  this::formatColumn)
            .column(10, this::formatColumn)
            .column(11, this::formatColumn)
            .build();

        final int notFinished = broker.getCloudletCreatedList().size() - broker.getCloudletFinishedList().size();
        System.out.println("\nDatacenter Scheduling Interval: " + datacenter.getSchedulingInterval());
        System.out.println("Min time between events: " + simulation.getMinTimeBetweenEvents());
        System.out.println("Seed for the random Cloudlet's submission delay generator: " + random.getSeed() + "\n");
        System.out.println("Cloudlets created: " +cloudletList.size() + " | Cloudlets not finished: " + notFinished);
        if(notFinished > 0){
            System.out.println("\n----------> Try decreasing the value of MIN_TIME_BETWEEN_EVENTS constant in this example! <----------\n");
        }
        System.out.println(getClass().getSimpleName() + " finished!");
    }

    private void formatColumn(final TableColumn col) {
        col.setFormat("%.2f");
    }
}
