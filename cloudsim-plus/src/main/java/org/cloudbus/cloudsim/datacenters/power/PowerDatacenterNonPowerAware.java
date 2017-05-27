/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.datacenters.power;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.hosts.power.PowerHostSimple;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.predicates.PredicateType;
import org.cloudbus.cloudsim.resources.FileStorage;

/**
 * PowerDatacenterNonPowerAware is a class that represents a <b>non-power</b>
 * aware data center in the context of power-aware simulations.
 *
 * <br/>If you are using any algorithms, policies or workload included in the
 * power package please cite the following paper:<br/>
 *
 * <ul>
 * <li><a href="http://dx.doi.org/10.1002/cpe.1867">Anton Beloglazov, and
 * Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of
 * Virtual Machines in Cloud Data Centers", Concurrency and Computation:
 * Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John
 * Wiley & Sons, Ltd, New York, USA, 2012</a>
 * </ul>
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 * @todo There are lots of duplicated code from PowerDatacenter
 */
public class PowerDatacenterNonPowerAware extends PowerDatacenter {
    /**
     * Creates a Datacenter.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @param characteristics the Datacenter characteristics
     * @param vmAllocationPolicy the vm provisioner
     *
     */
    public PowerDatacenterNonPowerAware(
        CloudSim simulation,
        DatacenterCharacteristics characteristics,
        VmAllocationPolicy vmAllocationPolicy)
    {
        super(simulation, characteristics, vmAllocationPolicy);
    }

    /**
     * Creates a Datacenter with the given parameters.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @param characteristics the Datacenter characteristics
     * @param vmAllocationPolicy the vm provisioner
     * @param storageList the storage list
     * @param schedulingInterval the scheduling interval
     *
     * @deprecated Use the other available constructors with less parameters
     * and set the remaining ones using the respective setters.
     * This constructor will be removed in future versions.
     */
    @Deprecated
    public PowerDatacenterNonPowerAware(
            CloudSim simulation,
            DatacenterCharacteristics characteristics,
            VmAllocationPolicy vmAllocationPolicy,
            List<FileStorage> storageList,
            double schedulingInterval)
    {
        this(simulation, characteristics, vmAllocationPolicy);
        setStorageList(storageList);
        setSchedulingInterval(schedulingInterval);
    }

    @Override
    protected double updateCloudletProcessing() {
        if (getLastCloudletProcessingTime() == -1 || getLastCloudletProcessingTime() == getSimulation().clock()) {
            getSimulation().cancelAll(getId(), new PredicateType(CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT));
            schedule(getId(), getSchedulingInterval(), CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT);
            return Double.MAX_VALUE;
        }

        final double currentTime = getSimulation().clock();

        if (currentTime > getLastProcessTime()) {
            Log.printLine("\n");
            final double dcPowerUsageForTimeSpan = getDatacenterPowerUsageForTimeSpan();
            Log.printFormattedLine("\n%.2f: Consumed energy is %.2f W*sec\n", getSimulation().clock(), dcPowerUsageForTimeSpan);

            Log.printLine("\n\n--------------------------------------------------------------\n\n");
            final double nextCloudletFinishTime = getNextCloudletFinishTime(currentTime);

            setPower(getPower() + dcPowerUsageForTimeSpan);

            checkCloudletsCompletionForAllHosts();

            removeFinishedVmsFromEveryHost();
            Log.printLine();

            migrateVmsOutIfMigrationIsEnabled();
            scheduleUpdateOfCloudletsProcessingForFutureTime(nextCloudletFinishTime);
            setLastProcessTime(currentTime);
            return nextCloudletFinishTime;
        }

        return Double.MAX_VALUE;
    }

    /**
     * Schedules the next update of Cloudlets in this Host for a future time.
     *
     * @param nextCloudletFinishTime the time to schedule the update of Cloudlets in this Host, that is the expected
     *                               time of the next finishing Cloudlet among all existing Hosts.
     * @see #getNextCloudletFinishTime(double)
     */
    private void scheduleUpdateOfCloudletsProcessingForFutureTime(double nextCloudletFinishTime) {
        if (nextCloudletFinishTime != Double.MAX_VALUE) {
            getSimulation().cancelAll(getId(), new PredicateType(CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT));
            // getSimulation().cancelAll(getId(), CloudSim.SIM_ANY);
            send(getId(), getSchedulingInterval(), CloudSimTags.VM_UPDATE_CLOUDLET_PROCESSING_EVENT);
        }
    }

    /**
     * Performs requested migration of VMs to another Hosts if migration from this Host is enabled.
     */
    private void migrateVmsOutIfMigrationIsEnabled() {
        if (isMigrationsEnabled()) {
            final Map<Vm, Host> migrationMap
                    = getVmAllocationPolicy().optimizeAllocation(getVmList());

            for (final Entry<Vm, Host> entry : migrationMap.entrySet()) {
                final Host targetHost = entry.getValue();
                final Host oldHost = entry.getKey().getHost();

                if (oldHost.equals(Host.NULL)) {
                    Log.printFormattedLine(
                        "%.2f: Migration of VM #%d to Host #%d is started",
                        getSimulation().clock(),
                        entry.getKey().getId(),
                        targetHost.getId());
                } else {
                    Log.printFormattedLine(
                        "%.2f: Migration of VM #%d from Host #%d to Host #%d is started",
                        getSimulation().clock(),
                        entry.getKey().getId(),
                        oldHost.getId(),
                        targetHost.getId());
                }

                targetHost.addMigratingInVm(entry.getKey());
                incrementMigrationCount();

                final double delay = timeToMigrateVm(entry.getKey(), targetHost);
                send(getId(), delay, CloudSimTags.VM_MIGRATE, entry);
            }
        }
    }

    /**
     * Gets the expected finish time of the next Cloudlet to finish in any of the existing Hosts.
     *
     * @param currentTime the current simulation time
     * @return the expected finish time of the next finishing Cloudlet or {@link Double#MAX_VALUE} if not
     * Cloudlet is running.
     */
    private double getNextCloudletFinishTime(double currentTime) {
        double minTime = Double.MAX_VALUE;
        for (final PowerHostSimple host : this.<PowerHostSimple>getHostList()) {
            Log.printFormattedLine("\n%.2f: Host #%d", getSimulation().clock(), host.getId());
            final double nextCloudletFinishTime = host.updateProcessing(currentTime);
            minTime = Math.min(nextCloudletFinishTime, minTime);
        }

        return minTime;
    }

    /**
     * Gets the total power consumed by all Hosts of the Datacenter since the last time the processing
     * of Cloudlets in this Host was updated.
     *
     * @return the total power consumed by all Hosts in the elapsed time span
     */
    private double getDatacenterPowerUsageForTimeSpan() {
        final double timeSpan = getSimulation().clock() - getLastProcessTime();
        double datacenterPowerUsageForTimeSpan = 0;
        for(PowerHostSimple host : this.<PowerHostSimple>getHostList()) {
            Log.printFormattedLine("%.2f: Host #%d", getSimulation().clock(), host.getId());

            final double hostPower = getHostConsumedPowerForTimeSpan(host, timeSpan);
            datacenterPowerUsageForTimeSpan += hostPower;

            println(String.format(
                    "%.2f: Host #%d utilization is %.2f%%",
                    getSimulation().clock(),
                    host.getId(),
                    host.getUtilizationOfCpu() * 100));
            println(String.format(
                    "%.2f: Host #%d energy is %.2f W*sec",
                    getSimulation().clock(),
                    host.getId(),
                    hostPower));
        }

        return datacenterPowerUsageForTimeSpan;
    }

    /**
     * Gets the power consumed by a given Host for a specific time span.
     *
     * @param host the Host to get the consumed power for the time span
     * @param timeSpan the time elapsed since the last update of cloudlets processing
     * @return
     */
    private double getHostConsumedPowerForTimeSpan(PowerHostSimple host, final double timeSpan) {
        double hostPower;

        try {
            hostPower = host.getMaxPower() * timeSpan;
        } catch (RuntimeException e) {
            hostPower = 0;
        }
        return hostPower;
    }

}
