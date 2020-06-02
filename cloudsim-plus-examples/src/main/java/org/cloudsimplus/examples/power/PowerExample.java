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
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.power.models.PowerModelLinear;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.UtilizationHistory;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;
import org.cloudsimplus.examples.resourceusage.VmsRamAndBwUsageExample;
import org.cloudsimplus.util.Log;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;

/**
 * An example to show power consumption of Hosts and VMs.
 * Realize that for this goal, you define a {@link PowerModel}
 * for each Host by calling {@code host.setPowerModel(powerModel)}.
 *
 * <p>It creates the number of cloudlets defined in
 * {@link #CLOUDLETS}. All cloudlets will required 100% of PEs they are using all the time.
 * Half of these cloudlets are created with the length defined by {@link #CLOUDLET_LENGTH}
 * and the other half will have the double of this length.
 * This way, it's possible to see that for the last half of the
 * simulation time, a Host doesn't use the entire CPU capacity,
 * and therefore doesn't consume the maximum power.</p>
 *
 * <p>However, you may notice in this case that the power usage isn't
 * half of the maximum consumption, because there is a minimum
 * amount of power to use, even if the Host is idle,
 * which is defined by {@link #STATIC_POWER_PERCENT}.
 * In the case of the {@link PowerModelLinear},
 * there is a constant power which is computed
 * and added to consumer power when it
 * is lower or equal to the minimum usage percentage.</p>
 *
 * <p>Realize that the Host CPU Utilization History is only computed
 * if VMs utilization history is enabled by calling
 * {@code vm.getUtilizationHistory().enable()}</p>
 *
 * <p>Each line in the table with CPU utilization and power consumption shows
 * the data from the time specified in the line up to the time before the value in the next line.
 * For instance, consider the scheduling interval is 10, the time in the first line is 0 and
 * it shows 100% CPU utilization and 100 Watt-Sec of power consumption.
 * Then, the next line contains data for time 10.
 * That means between time 0 and time 9 (from time 0 to 9 we have 10 samples),
 * the CPU utilization and power consumption
 * is the one provided for time 0.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.2.4
 *
 * @see VmsRamAndBwUsageExample
 * @see org.cloudsimplus.examples.resourceusage.VmsCpuUsageExample
 */
public class PowerExample {
    /**
     * Defines, between other things, the time intervals
     * to keep Hosts CPU utilization history records.
     */
    private static final int SCHEDULING_INTERVAL = 10;
    private static final int HOSTS = 2;
    private static final int HOST_PES = 8;

    private static final int VMS = 4;
    private static final int VM_PES = 4;

    private static final int CLOUDLETS = 8;
    private static final int CLOUDLET_PES = 2;
    private static final int CLOUDLET_LENGTH = 50000;

    /**
     * Defines the minimum percentage of power a Host uses,
     * even it it's idle.
     */
    private static final double STATIC_POWER_PERCENT = 0.7;

    /**
     * The max number of watt-second (Ws) of power a Host uses.
     */
    private static final int MAX_POWER_WATTS_SEC = 50;

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;
    private List<Cloudlet> cloudletList;
    private Datacenter datacenter0;
    private final List<Host> hostList;

    /**
     * If set to false, consecutive lines with the the same CPU utilization and power consumption
     * will be shown only once, at the time that such metrics started to return those values.
     * The last history line is always shown, independent of any condition.
     */
    private boolean showAllHostUtilizationHistoryEntries;


    public static void main(String[] args) {
        new PowerExample(true);
    }

    private PowerExample(boolean showAllHostUtilizationHistoryEntries) {
        Log.setLevel(Level.WARN);
        this.showAllHostUtilizationHistoryEntries = showAllHostUtilizationHistoryEntries;

        simulation = new CloudSim();
        hostList = new ArrayList<>(HOSTS);
        datacenter0 = createDatacenterSimple();
        //Creates a broker that is a software acting on behalf of a cloud customer to manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);

        vmList = createVms();
        cloudletList = createCloudlets();
        broker0.submitVmList(vmList);
        broker0.submitCloudletList(cloudletList);

        simulation.start();

        System.out.println("------------------------------- SIMULATION FOR SCHEDULING INTERVAL = " + SCHEDULING_INTERVAL+" -------------------------------");
        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();

        new CloudletsTableBuilder(finishedCloudlets).build();
        printHostsCpuUtilizationAndPowerConsumption();
        printVmsCpuUtilizationAndPowerConsumption();
    }

    /**
     * Prints the following information from VM's history:
     * <ul>
     *   <li>VM's CPU utilization relative to the total Host's CPU utilization.
     *       For instance, if there are 2 equal VMs using 100% of their CPU, the utilization
     *       of each one corresponds to 50% of the Host's CPU utilization.</li>
     *   <li>VM's power consumption relative to the total Host's power consumption.</li>
     * </ul>
     *
     * If we just get the percentage of CPU the VM is using from the Host
     * (as demonstrated above) and compute the VM power consumption we'll get an wrong value.
     *
     * <p>A Host, even if idle, may consume a static amount of power.
     * Lets say it consumes 20 watt-sec in idle state and that for each 1% of CPU use it consumes 1 watt-sec more.
     * For the 2 VMs of the example above, each one using 50% of CPU will consume 50 watt-sec.
     * That is 100 watt-sec for the 2 VMs, plus the 20 watt-sec that is static.
     * Therefore we have a total Host power consumption of 120 watt-sec.
     * </p>
     *
     * <p>
     * If we computer the power consumption for a single VM by
     * calling {@code vm.getHost().getPowerModel().getPower(hostCpuUsage)},
     * we get the 50 watt-sec consumed by the VM, plus the 20 watt-sec of static power.
     * This adds up to 70 watt-sec. If the two VMs are equal and using the same amount of CPU,
     * their power consumption would be the half of the total Host's power consumption.
     * This would be 60 watt-sec, not 70.
     *
     * This way, we have to compute VM power consumption by sharing a supposed Host static power
     * consumption with each VM, as it's being shown here.
     * Not all {@link PowerModel} have this static power consumption.
     * However, the way the VM power consumption
     * is computed here, that detail is abstracted.
     * </p>
     */
    private void printVmsCpuUtilizationAndPowerConsumption() {
        for (Vm vm : vmList) {
            System.out.println("Vm " + vm.getId() + " at Host " + vm.getHost().getId() + " CPU Usage and Power Consumption");
            System.out.println("----------------------------------------------------------------------------------------------------------------------");
            double vmPower; //watt-sec
            double utilizationHistoryTimeInterval, prevTime = 0;
            final UtilizationHistory history = vm.getUtilizationHistory();
            for (final double time : history.getHistory().keySet()) {
                utilizationHistoryTimeInterval = time - prevTime;
                vmPower = history.powerConsumption(time);
                final double wattsPerInterval = vmPower*utilizationHistoryTimeInterval;
                System.out.printf(
                    "\tTime %8.1f | Host CPU Usage: %6.1f%% | Power Consumption: %8.0f Watt-Sec * %6.0f Secs = %10.2f Watt-Sec%n",
                    time, history.getHostCpuUtilization(time) *100, vmPower, utilizationHistoryTimeInterval, wattsPerInterval);
                prevTime = time;
            }
            System.out.println();
        }
    }

    /**
     * The Host CPU Utilization History is only computed
     * if VMs utilization history is enabled by calling
     * {@code vm.getUtilizationHistory().enable()}.
     */
    private void printHostsCpuUtilizationAndPowerConsumption() {
        System.out.println();
        for (final Host host : hostList) {
            printHostCpuUtilizationAndPowerConsumption(host);
        }
    }

    private void printHostCpuUtilizationAndPowerConsumption(final Host host) {
        System.out.printf("Host %d CPU utilization and power consumption%n", host.getId());
        System.out.println("----------------------------------------------------------------------------------------------------------------------");
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
                    "\tTime %8.1f | Host CPU Usage: %6.1f%% | Power Consumption: %8.0f Watts * %6.0f Secs = %10.2f Watt-Sec%n",
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
        System.out.printf("----------------------------------------------------------------------------------------------------------------------%n%n");
    }

    /**
     * Creates a {@link Datacenter} and its {@link Host}s.
     */
    private Datacenter createDatacenterSimple() {
        for(int i = 0; i < HOSTS; i++) {
            Host host = createPowerHost();
            hostList.add(host);
        }

        final Datacenter dc = new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
        dc.setSchedulingInterval(SCHEDULING_INTERVAL);
        return dc;
    }

    private Host createPowerHost() {
        final List<Pe> peList = new ArrayList<>(HOST_PES);
        //List of Host's CPUs (Processing Elements, PEs)
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new PeSimple(1000, new PeProvisionerSimple()));
        }

        final PowerModel powerModel = new PowerModelLinear(MAX_POWER_WATTS_SEC, STATIC_POWER_PERCENT);

        final long ram = 2048; //in Megabytes
        final long bw = 10000; //in Megabits/s
        final long storage = 1000000; //in Megabytes
        final ResourceProvisioner ramProvisioner = new ResourceProvisionerSimple();
        final ResourceProvisioner bwProvisioner = new ResourceProvisionerSimple();
        final VmScheduler vmScheduler = new VmSchedulerTimeShared();

        final Host host = new HostSimple(ram, bw, storage, peList);
        host.setPowerModel(powerModel);
        host
            .setRamProvisioner(ramProvisioner)
            .setBwProvisioner(bwProvisioner)
            .setVmScheduler(vmScheduler);
        return host;
    }

    /**
     * Creates a list of VMs.
     */
    private List<Vm> createVms() {
        final List<Vm> list = new ArrayList<>(VMS);
        for (int i = 0; i < VMS; i++) {
            Vm vm = new VmSimple(i, 1000, VM_PES);
            vm.setRam(512).setBw(1000).setSize(10000)
              .setCloudletScheduler(new CloudletSchedulerTimeShared());
            vm.getUtilizationHistory().enable();
            list.add(vm);
        }

        return list;
    }

    /**
     * Creates a list of Cloudlets.
     */
    private List<Cloudlet> createCloudlets() {
        final List<Cloudlet> list = new ArrayList<>(CLOUDLETS);
        final UtilizationModel utilization = new UtilizationModelDynamic(0.2);
        for (int i = 0; i < CLOUDLETS; i++) {
            //Sets half of the cloudlets with the defined length and the other half with the double of it
            final long length = i < CLOUDLETS / 2 ? CLOUDLET_LENGTH : CLOUDLET_LENGTH * 2;
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
