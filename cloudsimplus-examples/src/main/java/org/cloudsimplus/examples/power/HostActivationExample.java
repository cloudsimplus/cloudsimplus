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
package org.cloudsimplus.examples.power;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyBestFit;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.power.models.PowerModelHost;
import org.cloudbus.cloudsim.power.models.PowerModelHostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.builders.tables.TextTableColumn;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.HostEventInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * An example showing how to create a Datacenter with all Hosts powered off
 * and let the {@link VmAllocationPolicy} to power Hosts on and off according to demand.
 * For this simulation, the {@link VmAllocationPolicyBestFit} is used.
 *
 * <p>The dynamic demand for Hosts is simulated
 * by creating VMs during simulation runtime.
 * To enable this, it's required to define
 * a {@link Datacenter#setSchedulingInterval(double)} scheduling interval}.
 * It's also used a Event Listener to track the simulation clock.
 * This way, when the clock increases according to the scheduling interval,
 * a new VM is created, up to the total VMs defined in {@link #MAX_VMS}.</p>
 *
 * <p>It creates one Cloudlet for each VM and the Cloudlets are created with different lengths.
 * This enables VMs to become idle in different times.
 * To ensure idle VMs are destroyed as soon as they become idle
 * (so that we can quickly check that the number of VMs into a Host has reduced),
 * a {@link DatacenterBroker#setVmDestructionDelayFunction(Function)} is
 * set. Check the {@link #createBroker()} method for details.
 *
 * <p>Since the total number of PEs requested by all VMs is equal to 24
 * and each Host has 8 PEs, it's only used 3 Hosts to meet the demand.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.2.0
 */
public class HostActivationExample {
    /**
     * Defines, between other things, the time intervals
     * to keep Hosts CPU utilization history records.
     */
    private static final int SCHEDULING_INTERVAL = 2;

    private static final int HOSTS = 5;
    private static final int HOST_PES = 8;

    /**
     * Defines the power a Host uses, even if it's idle (in Watts).
     */
    private static final double STATIC_POWER = 35;

    /**
     * The max power a Host uses (in Watts).
     */
    private static final int MAX_POWER = 50;

    /** Indicates the time (in seconds) the Host takes to start up.
     * Setting a value larger than 0 makes the VM placement to wait for the Host initialization. */
    private static final double HOST_START_UP_DELAY = 20;

    /** Indicates the time (in seconds) the Host takes to shut down. */
    private static final double HOST_SHUT_DOWN_DELAY = 10;

    /** Indicates the power (in watts) the Host consumes for starting up. */
    private static final double HOST_START_UP_POWER = 40;

    /** Indicates the power (in watts) the Host consumes for shutting up. */
    private static final double HOST_SHUT_DOWN_POWER = 15;

    /** The deadline (in seconds) after the Host becoming idle that it will be shutdown
     * automatically.
     * @see Host#setIdleShutdownDeadline(double) */
    private static final int HOST_IDLE_SECONDS_TO_SHUTDOWN = 5;
    private static final int HOST_MIPS = 1000;

    private static final int MIN_VMS = 2;
    private static final int MAX_VMS = 4;
    private static final int VM_PES = 4;

    private static final int CLOUDLET_LENGTH = 20000;

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    private double lastClockTime;

    public static void main(String[] args) {
        new HostActivationExample();
    }

    private HostActivationExample() {
        /*Enables just some level of log messages.
          Make sure to import org.cloudsimplus.util.Log;*/
        //Log.setLevel(ch.qos.logback.classic.Level.WARN);

        simulation = new CloudSim();
        datacenter0 = createDatacenter();

        /*After Host 1 is shutdown, send new VMs to request it to startup again.*/
        datacenter0.getHost(1).addOnShutdownListener(this::hostShutdownListener);
        broker0 = createBroker();

        vmList = new ArrayList<>(MAX_VMS);
        cloudletList = new ArrayList<>(MAX_VMS);
        createAndSubmitVmsAndCloudlets(MIN_VMS);
        simulation.addOnClockTickListener(this::clockTickListener);

        simulation.start();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        finishedCloudlets.sort(Comparator.comparingLong(Cloudlet::getLength).reversed());
        new CloudletsTableBuilder(finishedCloudlets)
            .addColumn(4, new TextTableColumn("Start up time", "Seconds"), cl -> cl.getVm().getHost().getStartTime())
            .addColumn(7, new TextTableColumn("Submission delay", "Seconds"), cl -> cl.getVm().getSubmissionDelay())
            .build();
        printHostsUpTime();
    }

    /**
     * After a Host is shutdown, send new VMs to force a new Host activation.
     * This way, we can see the total power consumed during host startup and shutdown
     * is larger for those Hosts which have started more than once.
     * @param info
     */
    private void hostShutdownListener(final HostEventInfo info) {
        final List<Vm> createVmList = createAndSubmitVmsAndCloudlets(MIN_VMS*2);
        final Host host = info.getHost();
        System.out.printf(
            "%s: Sending new %d VMs after Host %d shutdown: %s.%n",
            simulation.clockStr(), createVmList.size(), host.getId(), createVmList);

        //Ensure the listener is fired only once.
        host.removeOnShutdownListener(info.getListener());
    }

    private void printHostsUpTime() {
        System.out.printf("%nHosts' up time (total time each Host was powered on)%n");
        datacenter0.getHostList().stream().filter(Host::hasEverStarted).forEach(host -> {
            final PowerModelHost powerModel = host.getPowerModel();
            System.out.printf("  Host %2d%n", host.getId());

            System.out.printf(
                "     Total Up time:  %3.0f secs |  Startup time: %3.0f secs | Startup power:  %3.0f watts%n",
                host.getTotalUpTime(), powerModel.getTotalStartupTime(), powerModel.getTotalStartupPower());

            System.out.printf(
                "     Activations:    %3d      | Shutdown time: %3.0f secs | Shutdown power: %3.0f watts%n",
                powerModel.getTotalStartups(), powerModel.getTotalShutDownTime(), powerModel.getTotalShutDownPower());

        });
    }

    private DatacenterBroker createBroker() {
        final DatacenterBrokerSimple broker = new DatacenterBrokerSimple(simulation);

        /*Indicates that idle VMs must be destroyed after some seconds.
        * This forces the Host to become idle.
        * The delay should be larger then the simulation minTimeBetweenEvents to ensure VMs are gracefully shutdown. */
        broker.setVmDestructionDelay(1.0);

        /*
         * Ensures that VMs which couldn't be created due to lack of suitable and active Hosts
         * will be retried to be placed after some time.
         */
        broker.setFailedVmsRetryDelay(HOST_START_UP_DELAY+1);

        return broker;
    }

    /**
     * Event listener which is called every time the simulation clock advances.
     * Then, if the time defined in the {@link #SCHEDULING_INTERVAL} has passed,
     * it created another VM.
     *
     * @param info information about the event happened.
     */
    private void clockTickListener(final EventInfo info) {
        final double time = Math.floor(info.getTime());
        if(time > lastClockTime && time > broker0.getFailedVmsRetryDelay() && time % SCHEDULING_INTERVAL == 0) {
            if(vmList.size() < MAX_VMS) {
                createAndSubmitVmsAndCloudlets(MIN_VMS);
            }
        }
        lastClockTime = time;
    }

    private List<Vm> createAndSubmitVmsAndCloudlets(final int vmsToCreate) {
        final List<Vm> newVmList = new ArrayList<>(vmsToCreate);
        final List<Cloudlet> newCloudletList = new ArrayList<>(vmsToCreate);

        for (int i = 0; i < vmsToCreate; i++) {
            final Vm vm = new VmSimple(HOST_MIPS, VM_PES);
            //vm.setSubmissionDelay(2.0);
            final Cloudlet cloudlet = createCloudlet(vm);
            vmList.add(vm);
            newVmList.add(vm);

            cloudletList.add(cloudlet);
            newCloudletList.add(cloudlet);
        }

        broker0.submitVmList(newVmList);
        broker0.submitCloudletList(newCloudletList);
        return newVmList;
    }

    private Datacenter createDatacenter() {
        final List<Host> hostList = new ArrayList<>(HOSTS);
        for(int i = 0; i < HOSTS; i++) {
            Host host = createHost(i);
            hostList.add(host);
        }

        final DatacenterSimple dc = new DatacenterSimple(simulation, hostList, new VmAllocationPolicyBestFit());
        dc.setSchedulingInterval(SCHEDULING_INTERVAL);
        return dc;
    }

    /**
     * Creates a Host and doesn't power it on (it will be powered on according to demand).
     * @return a new powered-off Host
     * @param id
     */
    private Host createHost(final long id) {
        final List<Pe> peList = new ArrayList<>(HOST_PES);
        //List of Host's CPUs (Processing Elements, PEs)
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(HOST_MIPS));
        }

        //Indicates if the Host will be powered on or not after creation
        final boolean activate = false;
        final Host host = new HostSimple(peList, activate);
        host.setId(id);

        final PowerModelHost powerModel = new PowerModelHostSimple(MAX_POWER, STATIC_POWER);
        powerModel.setStartupDelay(HOST_START_UP_DELAY)
                  .setShutDownDelay(HOST_SHUT_DOWN_DELAY)
                  .setStartupPower(HOST_START_UP_POWER)
                  .setShutDownPower(HOST_SHUT_DOWN_POWER);

        host.setIdleShutdownDeadline(HOST_IDLE_SECONDS_TO_SHUTDOWN)
            .setPowerModel(powerModel);
        return host;
    }

    /**
     * Creates a Cloudlet
     * @param vm the VM to run the Cloudlet
     * @return
     */
    private Cloudlet createCloudlet(Vm vm) {
        final UtilizationModel utilizationModel = new UtilizationModelDynamic(0.4);
        /* Reduces the length to let the firstly created Cloudlets to run longer,
         * ensuring that a positive length is generated (Cloudlets
         * with negative length run indefinitely until being explicitly stopped. */
        final long len = Math.abs(CLOUDLET_LENGTH - cloudletList.size()*3000);
        final Cloudlet cloudlet = new CloudletSimple(len, VM_PES);
        cloudlet.setUtilizationModelCpu(new UtilizationModelFull());
        cloudlet.setUtilizationModelRam(utilizationModel);
        cloudlet.setVm(vm);
        return cloudlet;
    }
}
