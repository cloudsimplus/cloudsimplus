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
package org.cloudsimplus.examples.brokers;

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
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A example showing how to create VMs and Cloudlets
 * for multiple customers, each one represented
 * by a {@link DatacenterBroker} object.
 *
 * <p>It creates Cloudlets with different lengths to enable
 * them to finish in different times.
 * It also uses the {@link DatacenterBroker#setVmDestructionDelayFunction(Function)}
 * method to define a {@link Function} which will be used
 * to get the time delay a VM will be destroyed after becoming idle.
 * Setting a delay before destroying an idle VM
 * gives dynamically arrived Cloudltes the opportunity to possibly
 * run inside such a VM. In this case, the VM stay idle for a
 * period of time to balance the load of arrived Cloudlets
 * or even to enable fault tolerance.</p>
 *
 * <p>See the {@link DatacenterBroker#DEF_VM_DESTRUCTION_DELAY}
 * for details about the default behaviour.</p>
 *
 * <p>For details about Fault Injection, check the {@link org.cloudsimplus.faultinjection} package.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.2.2
 * @see MultipleBrokers1
 */
public class MultipleBrokers2 {
    /**
     * @see Datacenter#getSchedulingInterval()
     */
    private static final int SCHEDULING_INTERVAL = 5;
    private static final int HOSTS = 2;
    private static final int HOST_PES = 8;

    private static final int BROKERS = 2;

    private static final int VMS = 2;
    private static final int VM_PES = 4;

    private static final int CLOUDLETS = 2;
    private static final int CLOUDLET_PES = 2;
    private static final int CLOUDLET_LENGTH = 10000;

    private final CloudSim simulation;
    private List<DatacenterBroker> brokers;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;

    public static void main(String[] args) {
        new MultipleBrokers2();
    }

    private MultipleBrokers2() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        simulation = new CloudSim();
        datacenter0 = createDatacenter();
        brokers = createBrokers();

        vmList = new ArrayList<>(BROKERS*VMS);
        cloudletList = new ArrayList<>(CLOUDLETS*VMS);
        createVmsAndCloudlets();

        /*
         * Static or dynamically submits a Cloudlet.
         * Choose the way you want to use by commenting one of the two lines below.
         * Any way you chose, for the configuration of this example,
         * the results will be exactly the same.
         * These lines of code below are used just to show the different
         * ways to simulate arrival of Cloudlets at different times.
         * See the documentation of the methods called below for more details.
         */
        //submitCloudletWithDelay();
        simulation.addOnClockTickListener(this::dynamicCloudletArrival);

        simulation.start();
        printResults();
    }

    /**
     * Statically submits a Cloudlet with a specific delay,
     * meaning it will start executing only after the defined time.
     * The Cloudlet is submitted before the simulation starts.
     */
    private void submitCloudletWithDelay() {
        final Cloudlet c = createCloudlet(cloudletList.size(), CLOUDLET_LENGTH);
        c.setSubmissionDelay(35);
        brokers.get(1).submitCloudlet(c);
    }

    /**
     * Dynamically submits a cloudlet during simulation execution.
     * This method is called every time when the simulation clock
     * increases.
     * The created Cloudlet is submitted only at a specific simulation time.
     *
     * <p>Realize the interval in which this method is called is defined
     * according to the {@link #SCHEDULING_INTERVAL}.</p>
     *
     * <p>Different from the {@link #submitCloudletWithDelay()} method,
     * which creates all Cloudtets when the simulation starts and make them wait
     * to start executing, this method is called dynamically during
     * simulation. It defines internally, when
     * we want the Cloudlet to be submitted.
     * The method is called by the {@link CloudSim} object
     * because an {@link EventListener} was added to be
     * notified when the simulation clock changes.
     * This way it allows, between uncountable possibilities,
     * to define specific conditions when a new Cloudlet should be submitted
     * to a broker.
     * </p>
     *
     * <p>The dynamic way of submitting Cloudlets is more flexible because it allows you to define
     * any condition you want inside a method such as this one.
     * That condition may include any other variables besides the time.</p>
     *
     * @param e information about the generated event
     * @return
     * @see Datacenter#getSchedulingInterval()
     * @see CloudSim#addOnClockTickListener(EventListener)
     */
    private void dynamicCloudletArrival(EventInfo e) {
        if((int)e.getTime() != 35) {
            return;
        }

        final Cloudlet c = createCloudlet(cloudletList.size(), CLOUDLET_LENGTH);
        final DatacenterBroker broker = brokers.get(1);
        System.out.printf("%.2f: \t\t\tDynamically submitting %s to %s%n", e.getTime(),  c, broker);
        broker.submitCloudlet(c);
    }

    /**
     * Creates VMs and Cloudlets for each DatacenterBroker.
     * It enables you to define when a broker should destroy
     * an idle VM, according to a given
     * {@link DatacenterBroker#setVmDestructionDelayFunction(Function) VM Destruction Delay Function}.
     *
     * <p>See <a href="https://github.com/manoelcampos/cloudsim-plus/issues/99">Issue #99</a> for more details.</p>
     *
     * @see DatacenterBroker#setVmDestructionDelayFunction(Function)
     */
    private void createVmsAndCloudlets() {
        int i = 0;
        for (DatacenterBroker broker : brokers) {
            broker.setVmDestructionDelayFunction(vm -> 10.0);
            vmList.addAll(createAndSubmitVms(broker));
            cloudletList.addAll(createAndSubmitCloudlets(broker, CLOUDLET_LENGTH*CLOUDLETS*i++));
        }
    }

    private void printResults() {
        for (DatacenterBroker broker : brokers) {
            new CloudletsTableBuilder(broker.getCloudletFinishedList())
                .setTitle(broker.getName())
                .build();
        }

        System.out.println();
        for (Vm vm : vmList) {
            System.out.printf("Vm %d Broker %d -> Start Time: %.0f Stop Time: %.0f Total Execution Time: %.0f%n",
                vm.getId(), vm.getBroker().getId(),
                vm.getStartTime(), vm.getStopTime(), vm.getTotalExecutionTime());
        }
        System.out.println();
    }

    private List<DatacenterBroker> createBrokers() {
        final List<DatacenterBroker> list = new ArrayList<>(BROKERS);
        for(int i = 0; i < BROKERS; i++) {
            list.add(new DatacenterBrokerSimple(simulation));
        }

        return list;
    }

    /**
     * Creates a Datacenter and its Hosts.
     */
    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost();
            hostList.add(host);
        }

        final Datacenter dc = new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
        dc.setSchedulingInterval(SCHEDULING_INTERVAL);
        return dc;
    }

    private Host createHost() {
        List<Pe> peList = new ArrayList<>(HOST_PES);
        //List of Host's CPUs (Processing Elements, PEs)
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(1000, new PeProvisionerSimple()));
        }

        final long ram = 2048; //in Megabytes
        final long bw = 10000; //in Megabits/s
        final long storage = 1000000; //in Megabytes
        ResourceProvisioner ramProvisioner = new ResourceProvisionerSimple();
        ResourceProvisioner bwProvisioner = new ResourceProvisionerSimple();
        VmScheduler vmScheduler = new VmSchedulerTimeShared();
        Host host = new HostSimple(ram, bw, storage, peList);
        host
            .setRamProvisioner(ramProvisioner)
            .setBwProvisioner(bwProvisioner)
            .setVmScheduler(vmScheduler);
        return host;
    }

    /**
     * Creates a list of VMs.
     */
    private List<Vm> createAndSubmitVms(DatacenterBroker broker) {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            Vm vm =
                new VmSimple(vmList.size()+i, 1000, VM_PES)
                    .setRam(512).setBw(1000).setSize(10000)
                    .setCloudletScheduler(new CloudletSchedulerTimeShared());

            list.add(vm);
        }

        broker.submitVmList(list);

        return list;
    }

    /**
     * Creates a list of Cloudlets.
     */
    private List<Cloudlet> createAndSubmitCloudlets(DatacenterBroker broker, final int initialLength) {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        for (int i = 1; i <= CLOUDLETS; i++) {
            int length = initialLength + CLOUDLET_LENGTH * i;
            Cloudlet cloudlet = createCloudlet(cloudletList.size() + i - 1, length);
            list.add(cloudlet);
        }

        broker.submitCloudletList(list);

        return list;
    }

    private Cloudlet createCloudlet(final int id, final int length) {
        UtilizationModel utilization = new UtilizationModelFull();
        final Cloudlet cloudlet = new CloudletSimple(id, length, CLOUDLET_PES);
        cloudlet
            .setFileSize(1024)
            .setOutputSize(1024)
            .setUtilizationModel(utilization);
        return cloudlet;
    }
}
