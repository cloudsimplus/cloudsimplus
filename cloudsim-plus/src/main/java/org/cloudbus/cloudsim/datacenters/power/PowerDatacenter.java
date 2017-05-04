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
import java.util.Objects;

import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.power.PowerHostSimple;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.cloudbus.cloudsim.core.predicates.PredicateType;
import org.cloudbus.cloudsim.resources.FileStorage;
import org.cloudbus.cloudsim.util.Log;

/**
 * PowerDatacenter is a class that enables simulation of power-aware data
 * centers.
 *
 * <br>If you are using any algorithms, policies or workload included in the
 * power package please cite the following paper:<br>
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
 */
public class PowerDatacenter extends DatacenterSimple {

    /**
     * The Datacenter consumed power.
     */
    private double power;

    /**
     * Indicates if migrations are disabled or not.
     */
    private boolean migrationsEnabled;

    /**
     * The last time submitted cloudlets were processed.
     */
    private double lastCloudletProcessingTime;

    /**
     * The VM migration count.
     */
    private int migrationCount;

    /**
     * Creates a PowerDatacenter.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @param characteristics the characteristics of the Datacenter to be created
     * @param vmAllocationPolicy the policy to be used to allocate VMs into hosts
     *
     */
    public PowerDatacenter(
        CloudSim simulation,
        DatacenterCharacteristics characteristics,
        VmAllocationPolicy vmAllocationPolicy)
    {
        super(simulation, characteristics, vmAllocationPolicy);
        setPower(0.0);
        setMigrationsEnabled(true);
        setLastCloudletProcessingTime(-1);
        setMigrationCount(0);
    }

    /**
     * Creates a PowerDatacenter with the given parameters.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @param characteristics the characteristics of the Datacenter to be created
     * @param vmAllocationPolicy the policy to be used to allocate VMs into hosts
     * @param storageList a List of storage elements, for data simulation
     * @param schedulingInterval the scheduling delay to process each Datacenter received event
     *
     * @deprecated Use the other available constructors with less parameters
     * and set the remaining ones using the respective setters.
     * This constructor will be removed in future versions.
     */
    @Deprecated
    public PowerDatacenter(
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
        final double nextSimulationTime = super.updateCloudletProcessing();
        if (nextSimulationTime == Double.MAX_VALUE){
            return nextSimulationTime;
        }

        executeVmMigrations();

        return nextSimulationTime;
    }

    private void executeVmMigrations() {
        if (!isMigrationsEnabled()) {
            return;
        }
        
        Map<Vm, Host> migrationMap =
                getVmAllocationPolicy().optimizeAllocation(getVmList());
        final double currentTime = getSimulation().clock();
        for (Entry<Vm, Host> migrate : migrationMap.entrySet()) {
            Host targetHost = migrate.getValue();
            Host oldHost = migrate.getKey().getHost();

            if (oldHost == Host.NULL) {
                println(String.format(
                        "%.2f: Migration of %s to %s is started",
                        currentTime, migrate.getKey(), targetHost));
            } else {
                println(String.format(
                        "%.2f: Migration of %s from %s to %s is started",
                        currentTime, migrate.getKey(),
                        oldHost, targetHost));
            }

            targetHost.addMigratingInVm(migrate.getKey());
            incrementMigrationCount();

            //VM migration delay = RAM / bandwidth
            /*
            We use BW / 2 to model BW available for migration purposes, the other
            half of BW is for VM communication
            around 16 seconds for 1024 MEGABYTE using 1 Gbit/s network
            */
            //@todo this computation is different from PowerDatacenterNonPowerAware and probably is duplicated with other Datacenters
            send(
                    getId(),
                    migrate.getKey().getRam().getCapacity() / ((double) targetHost.getBw().getCapacity() / (2 * 8000)),
                    CloudSimTags.VM_MIGRATE, migrate);
        }
    }

    @Override
    protected double updateVmsProcessingOfAllHosts() {
        final double currentTime = getSimulation().clock();

        println("\n--------------------------------------------------------------\n");
        println(String.format("New resource usage of Datacenter %d for the time frame starting at %.2f:", getId(), currentTime));

        final double nextCloudletFinishTime = super.updateVmsProcessingOfAllHosts();
        final double datacenterPowerUsageForTimeSpan = getDatacenterPowerUsageForTimeSpan();

        setPower(getPower() + datacenterPowerUsageForTimeSpan);
        this.getHostList().forEach(host ->
            println(String.format(
                    "%.2f: [%s] utilization is %.2f%%",
                    currentTime,
                    host,
                    host.getUtilizationOfCpu() * 100))
        );
        println();

        return nextCloudletFinishTime;
    }
    
    /**
     * Gets the total power consumed by all Hosts of the Datacenter since the last time the processing
     * of Cloudlets in this Host was updated.
     *
     * @return the total power consumed by all Hosts in the elapsed time span
     */
    private double getDatacenterPowerUsageForTimeSpan() {
        double datacenterPowerUsageForTimeSpan = 0;
        final double currentTime = getSimulation().clock();
        final double timeSpan = currentTime - getLastProcessTime();
        if (timeSpan > 0) {
            println(String.format(
                    "\nDatacenter %d energy consumption for the last time frame from %.2f to %.2f:",
                    getId(),
                    getLastProcessTime(),
                    currentTime));

            for (PowerHostSimple host : this.<PowerHostSimple>getHostList()) {
                final double previousUseOfCpu = host.getPreviousUtilizationOfCpu();
                final double utilizationOfCpu = host.getUtilizationOfCpu();
                final double timeFrameHostEnergy = host.getEnergyLinearInterpolation(
                        previousUseOfCpu, utilizationOfCpu, timeSpan);
                datacenterPowerUsageForTimeSpan += timeFrameHostEnergy;

                println(String.format(
                        "\n%.2f: [%s] utilization at %.2f was %.2f%%, now is %.2f%%",
                        currentTime,
                        host,
                        getLastProcessTime(),
                        previousUseOfCpu * 100,
                        utilizationOfCpu * 100));
                println(String.format(
                        "%.2f: [%s] energy is %.2f Watts/sec",
                        currentTime,
                        host,
                        timeFrameHostEnergy));
            }

            println(String.format(
                    "\n%.2f: Datacenter %d energy is %.2f Watts/sec\n",
                    currentTime, getId(),
                    datacenterPowerUsageForTimeSpan));
        }

        return datacenterPowerUsageForTimeSpan;
    }

    protected void removeFinishedVmsFromEveryHost() {
        for (PowerHostSimple host : this.<PowerHostSimple>getHostList()) {
            for (Vm vm : host.getFinishedVms()) {
                getVmAllocationPolicy().deallocateHostForVm(vm);
                Log.printFormattedLine(
                        String.format("%.2f: %s has been deallocated from %s", 
                               getSimulation().clock(), vm, host));
            }
        }
    }

    @Override
    protected void processVmMigrate(SimEvent ev, boolean ack) {
        if (getSimulation().clock() <= getLastProcessTime()) {
            return;
        }
        
        super.updateVmsProcessingOfAllHosts();
        super.processVmMigrate(ev, ack);
        SimEvent event = getSimulation().findFirstDeferred(getId(), new PredicateType(CloudSimTags.VM_MIGRATE));
        if (Objects.isNull(event) || event.eventTime() > getSimulation().clock()) {
            super.updateVmsProcessingOfAllHosts();
        }
    }

    @Override
    protected void processCloudletSubmit(SimEvent ev, boolean ack) {
        super.processCloudletSubmit(ev, ack);
        setLastCloudletProcessingTime(getSimulation().clock());
    }

    /**
     * Gets the power.
     *
     * @return the power
     */
    public double getPower() {
        return power;
    }

    /**
     * Sets the power.
     *
     * @param power the new power
     */
    protected final void setPower(double power) {
        this.power = power;
    }

    /**
     * Checks if PowerDatacenter has any VM in migration.
     *
     * @return
     */
    protected boolean isInMigration() {
        return getVmList().stream().anyMatch(Vm::isInMigration);
    }

    /**
     * Checks if migrations are enabled.
     *
     * @return true, if migrations are enable; false otherwise
     */
    public boolean isMigrationsEnabled() {
        return migrationsEnabled;
    }

    /**
     * Enable or disable migrations.
     *
     * @param enable true to enable migrations; false to disable
     * @return 
     */
    public final PowerDatacenter setMigrationsEnabled(boolean enable) {
        this.migrationsEnabled = enable;
        return this;
    }

    /**
     * Gets the last time submitted cloudlets were processed.
     *
     * @return true, if is cloudlet submitted
     */
    protected double getLastCloudletProcessingTime() {
        return lastCloudletProcessingTime;
    }

    /**
     * Sets the last time submitted cloudlets were processed.
     *
     * @param lastCloudletProcessingTime the new cloudlet submitted
     */
    protected final void setLastCloudletProcessingTime(double lastCloudletProcessingTime) {
        this.lastCloudletProcessingTime = lastCloudletProcessingTime;
    }

    /**
     * Gets the migration count.
     *
     * @return the migration count
     */
    public int getMigrationCount() {
        return migrationCount;
    }

    /**
     * Sets the migration count.
     *
     * @param migrationCount the new migration count
     */
    protected final void setMigrationCount(int migrationCount) {
        this.migrationCount = migrationCount;
    }

    /**
     * Increment migration count.
     */
    protected void incrementMigrationCount() {
        setMigrationCount(getMigrationCount() + 1);
    }

}
