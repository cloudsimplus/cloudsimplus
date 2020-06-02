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
package org.cloudsimplus.examples.power;

import ch.qos.logback.classic.Level;
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
import org.cloudbus.cloudsim.power.models.PowerAware;
import org.cloudbus.cloudsim.power.models.PowerModelLinear;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;

/**
 * An example to show how the accuracy of power consumption may change
 * according to different Datacenter scheduling intervals.
 * As lower is its value, higher is the power consumption accuracy
 * because power consumption data will be collected in smaller intervals.
 *
 * <p>CloudSim Plus has a very accurate and consistent power consumption computation.
 * This way, you can see in this example that results just change
 * when a scheduling interval is set with a value equals to the time the last
 * Cloudlet finishes.
 *
 * <p>You are strongly encouraged to firstly check the {@link PowerExample} to understand the details.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @author Alexandre Henrique Teixeira Dias
 * @since CloudSim Plus 4.0.0
 */
public class PowerExampleSchedulingInterval {
    private static final int HOSTS = 1;
    private static final int HOST_PES = 8;

    private static final int VMS = 2;
    private static final int VM_PES = 4;

    private static final int CLOUDLETS = 4;
    private static final int CLOUDLET_PES = 2;
    private static final int CLOUDLET_LENGTH = 50000;

    /**
     * Defines the minimum percentage of power a Host uses,
     * even when it's idle.
     */
    private static final double STATIC_POWER_PERCENT = 0.7;

    /**
     * The max number of watt-second (Ws) of power a Host uses.
     */
    private static final int MAX_POWER_WATTS_SEC = 50;

    private final int schedulingInterval;
    private boolean showAllHostUtilizationHistoryEntries;

    private CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    private List<Host> hostList;

    public static void main(String[] args) {
        Log.setLevel(Level.WARN);
        final int[] SCHEDULING_INTERVALS_SECS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 100};
        for(final int interval : SCHEDULING_INTERVALS_SECS) {
            new PowerExampleSchedulingInterval(interval, false);
        }
    }

    /**
     * Instantiates and run the example with a specific configuration.
     * @param schedulingInterval the {@link Datacenter#getSchedulingInterval()} (in seconds)
     * @param showAllHostUtilizationHistoryEntries show all host CPU utilization history or just when the values change
     */
    private PowerExampleSchedulingInterval(
        final int schedulingInterval,
        final boolean showAllHostUtilizationHistoryEntries)
    {
        this.showAllHostUtilizationHistoryEntries = showAllHostUtilizationHistoryEntries;
        simulation = new CloudSim();
        hostList = new ArrayList<>(HOSTS);
        this.schedulingInterval = schedulingInterval;
        datacenter0 = createDatacenterSimple();
        broker0 = new DatacenterBrokerSimple(simulation);

        vmList = createVms();
        cloudletList = createCloudlets();
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();

        System.out.println("------------------------------- SIMULATION FOR SCHEDULING INTERVAL = " + schedulingInterval+" -------------------------------");
        //new CloudletsTableBuilder(broker0.getCloudletFinishedList()).build();
        printHostsCpuUtilizationAndPowerConsumption();
        System.out.println();
    }

    /**
     * <p>The Host CPU Utilization History is only computed
     * if VMs utilization history is enabled by calling
     * {@code vm.getUtilizationHistory().enable()}
     * </p>*
     */
    private void printHostsCpuUtilizationAndPowerConsumption() {
        System.out.println();
        for (final Host host : hostList) {
            printHostCpuUtilizationAndPowerConsumption(host);
        }
    }

    private void printHostCpuUtilizationAndPowerConsumption(final Host host) {
        System.out.printf("Host %d CPU utilization and power consumption%n", host.getId());
        final Map<Double, DoubleSummaryStatistics> utilizationPercentHistory = host.getUtilizationHistory();
        double totalWattsSec = 0;
        double prevUtilizationPercent = -1, prevWattsSec = -1;
        //time difference from the current to the previous line in the history
        double utilizationHistoryTimeInterval;
        double prevTime=0;
        for (Map.Entry<Double, DoubleSummaryStatistics> entry : utilizationPercentHistory.entrySet()) {
            utilizationHistoryTimeInterval = entry.getKey() - prevTime;
            //The total Host's CPU utilization for the time specified by the map key
            final double utilizationPercent = entry.getValue().getSum();
            final double watts = host.getPowerModel().getPower(utilizationPercent);
            //Energy consumption in the time interval
            final double wattsSec = watts*utilizationHistoryTimeInterval;
            //Energy consumption in the entire simulation time
            totalWattsSec += wattsSec;
            //only prints when the next utilization is different from the previous one, or it's the first one
            if(showAllHostUtilizationHistoryEntries || prevUtilizationPercent != utilizationPercent || prevWattsSec != wattsSec) {
                System.out.printf(
                    "\tTime %8.2f | CPU Utilization %6.2f%% | Power Consumption: %8.0f Watts * %.0f Secs = %.0f Watt-Sec%n",
                    entry.getKey(), utilizationPercent * 100, watts, utilizationHistoryTimeInterval, wattsSec);
            }
            prevUtilizationPercent = utilizationPercent;
            prevWattsSec = wattsSec;
            prevTime = entry.getKey();
        }

        System.out.printf(
            "Total Host %d Power Consumption in %.0f secs: %.0f Watt-Sec (%.5f KWatt-Hour)%n",
            host.getId(), simulation.clock(), totalWattsSec, PowerAware.wattsSecToKWattsHour(totalWattsSec));
        final double powerWattsSecMean = totalWattsSec / simulation.clock();
        System.out.printf(
            "Mean %.2f Watt-Sec for %d usage samples (%.5f KWatt-Hour)%n",
            powerWattsSecMean, utilizationPercentHistory.size(), PowerAware.wattsSecToKWattsHour(powerWattsSecMean));
    }

    private Datacenter createDatacenterSimple() {
        for(int i = 0; i < HOSTS; i++) {
            Host host = createPowerHost();
            hostList.add(host);
        }

        final Datacenter dc = new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
        dc.setSchedulingInterval(schedulingInterval);
        return dc;
    }

    private Host createPowerHost() {
        final List<Pe> peList = new ArrayList<>(HOST_PES);
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(1000, new PeProvisionerSimple()));
        }

        final long ram = 2048; //in Megabytes
        final long bw = 10000; //in Megabits/s
        final long storage = 1000000; //in Megabytes

        final Host host = new HostSimple(ram, bw, storage, peList);
        host.setPowerModel(new PowerModelLinear(MAX_POWER_WATTS_SEC, STATIC_POWER_PERCENT));
        host
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(new VmSchedulerTimeShared());
        return host;
    }

    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            final Vm vm = new VmSimple(i, 1000, VM_PES);
            vm.setRam(512).setBw(1000).setSize(10000);
            vm.setCloudletScheduler(new CloudletSchedulerTimeShared());
            vm.getUtilizationHistory().enable();
            list.add(vm);
        }

        return list;
    }

    private List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        final UtilizationModel utilization = new UtilizationModelDynamic(0.2);
        for (int i = 0; i < CLOUDLETS; i++) {
            //Sets half of the cloudlets with the defined length and the other half with the double of it
            final long length = i < CLOUDLETS/2 ? CLOUDLET_LENGTH : CLOUDLET_LENGTH*2;
            Cloudlet cloudlet =
                new CloudletSimple(i, length, CLOUDLET_PES)
                    .setFileSize(1024)
                    .setOutputSize(1024)
                    .setUtilizationModelCpu(new UtilizationModelFull())
                    .setUtilizationModelRam(utilization)
                    .setUtilizationModelBw(utilization);
            list.add(cloudlet);
        }

        return list;
    }
}
