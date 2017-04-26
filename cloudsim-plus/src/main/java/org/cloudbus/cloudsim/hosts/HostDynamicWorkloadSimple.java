/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.hosts;

import org.cloudbus.cloudsim.lists.PeList;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmStateHistoryEntry;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A host supporting dynamic workloads and performance degradation.
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class HostDynamicWorkloadSimple extends HostSimple implements HostDynamicWorkload {
    /**
     * The utilization mips.
     */
    private double utilizationMips;

    /**
     * The previous utilization mips.
     */
    private double previousUtilizationMips;

    /**
     * The host utilization state history.
     */
    private final List<HostStateHistoryEntry> stateHistory;

    /**
     * Creates a host.
     * @param ram the RAM capacity in Megabytes
     * @param bw the Bandwidth (BW) capacity in Megabits/s
     * @param storage the storage capacity in Megabytes
     * @param peList the host's {@link Pe} list
     *
     */
    public HostDynamicWorkloadSimple(long ram, long bw, long storage, List<Pe> peList) {
        super(ram, bw, storage, peList);
        setUtilizationMips(0);
        setPreviousUtilizationMips(0);
        stateHistory = new LinkedList<>();
    }

    /**
     * Creates a host with the given parameters.
     *
     * @param id the id
     * @param ramProvisioner the ram provisioner
     * @param bwProvisioner the bw provisioner
     * @param storage the storage capacity
     * @param peList the host's PEs list
     * @param vmScheduler the VM scheduler
     *
     * @deprecated Use the other available constructors with less parameters
     * and set the remaining ones using the respective setters.
     * This constructor will be removed in future versions.
     */
    @Deprecated
    public HostDynamicWorkloadSimple(
            int id,
            ResourceProvisioner ramProvisioner,
            ResourceProvisioner bwProvisioner,
            long storage,
            List<Pe> peList,
            VmScheduler vmScheduler)
    {
        this(ramProvisioner.getCapacity(), bwProvisioner.getCapacity(), storage, peList);
        setRamProvisioner(ramProvisioner);
        setBwProvisioner(bwProvisioner);
        setVmScheduler(vmScheduler);
    }

    @Override
    public double updateProcessing(double currentTime) {
        final double smallerTime = super.updateProcessing(currentTime);
        setPreviousUtilizationMips(getUtilizationOfCpuMips());
        setUtilizationMips(0);
        double hostTotalRequestedMips = 0;

        for (final Vm vm : getVmList()) {
            final double totalRequestedMips = vm.getCurrentRequestedTotalMips();

            showVmResourceUsageOnHost(vm);
            final double totalAllocatedMips = addVmResourceUsageToHistoryIfNotInMigration(currentTime, vm);

            setUtilizationMips(getUtilizationOfCpuMips() + totalAllocatedMips);
            hostTotalRequestedMips += totalRequestedMips;
        }

        addStateHistoryEntry(currentTime, getUtilizationOfCpuMips(), hostTotalRequestedMips, getUtilizationOfCpuMips() > 0);

        return smallerTime;
    }

    private double addVmResourceUsageToHistoryIfNotInMigration(double currentTime, Vm vm) {
        double totalAllocatedMips = getVmScheduler().getTotalAllocatedMipsForVm(vm);
        if (getVmsMigratingIn().contains(vm)) {
            Log.printFormattedLine("%.2f: [Host #" + getId() + "] VM #" + vm.getId()
                    + " is being migrated to Host #" + getId(), getSimulation().clock());
            return totalAllocatedMips;
        }

        final double totalRequestedMips = vm.getCurrentRequestedTotalMips();
        if (totalAllocatedMips + 0.1 < totalRequestedMips) {
            Log.printFormattedLine("%.2f: [Host #" + getId() + "] Under allocated MIPS for VM #" + vm.getId()
                    + ": %.2f", getSimulation().clock(), totalRequestedMips - totalAllocatedMips);
        }

        final VmStateHistoryEntry entry = new VmStateHistoryEntry(
                currentTime,
                totalAllocatedMips,
                totalRequestedMips,
                (vm.isInMigration() && !getVmsMigratingIn().contains(vm)));
        vm.addStateHistoryEntry(entry);

        if (vm.isInMigration()) {
            Log.printFormattedLine(
                    "%.2f: [Host #" + getId() + "] VM #" + vm.getId() + " is in migration",
                    getSimulation().clock());
            totalAllocatedMips /= 0.9; // performance degradation due to migration - 10%
        }

        return totalAllocatedMips;
    }

    private void showVmResourceUsageOnHost(Vm vm) {
        final double totalRequestedMips = vm.getCurrentRequestedTotalMips();
        final double totalAllocatedMips = getVmScheduler().getTotalAllocatedMipsForVm(vm);
        if (!Log.isDisabled() && vm.getHost() != Host.NULL) {
            getDatacenter().println(String.format(
                    "%.2f: [Host #" + getId() + "] Total allocated MIPS for VM #" + vm.getId()
                    + " (Host #" + vm.getHost().getId()
                    + ") is %.2f, was requested %.2f out of total %.2f (%.2f%%)",
                    getSimulation().clock(),
                    totalAllocatedMips,
                    totalRequestedMips,
                    vm.getMips(),
                    totalRequestedMips / vm.getMips() * 100));

            final List<Pe> pes = getVmScheduler().getPesAllocatedForVM(vm);
            final StringBuilder pesString = new StringBuilder();
            pes.forEach(pe -> 
                pesString.append(
                        String.format(" PE #%d: %d.",
                                pe.getId(),
                                pe.getPeProvisioner().getAllocatedResourceForVm(vm)))
            );
            
            getDatacenter().println(String.format(
                    "%.2f: [Host #" + getId() + "] MIPS for VM #" + vm.getId() + " by working PEs ("
                    + getNumberOfWorkingPes()+ " * " + getVmScheduler().getPeCapacity() + ")."
                    + pesString,
                    getSimulation().clock()));
        }
    }

    @Override
    public List<Vm> getFinishedVms() {
        return getVmList().stream()
            .filter(vm -> !vm.isInMigration())
            .filter(vm -> vm.getCurrentRequestedTotalMips() == 0)
            .collect(Collectors.toList());
    }

    @Override
    public double getMaxUtilization() {
        return PeList.getMaxUtilization(getPeList());
    }

    @Override
    public double getMaxUtilizationAmongVmsPes(Vm vm) {
        return PeList.getMaxUtilizationAmongVmsPes(getPeList(), vm);
    }

    @Override
    public long getUtilizationOfRam() {
        return getRamProvisioner().getTotalAllocatedResource();
    }

    @Override
    public long getUtilizationOfBw() {
        return getBwProvisioner().getTotalAllocatedResource();
    }

    @Override
    public double getUtilizationOfCpu() {
        return computeCpuUtilizationPercent(getUtilizationOfCpuMips());
    }

    @Override
    public double getPreviousUtilizationOfCpu() {
        return computeCpuUtilizationPercent(getPreviousUtilizationMips());
    }
    
    private double computeCpuUtilizationPercent(double mipsUsage){
        final double totalMips = getTotalMipsCapacity();
        if(totalMips == 0){
            return 0;
        }
        
        final double utilization = mipsUsage / totalMips;
        return (utilization > 1 && utilization < 1.01 ? 1 : utilization);
    }
    
    @Override
    public double getUtilizationOfCpuMips() {
        return utilizationMips;
    }

    /**
     * Sets the utilization mips.
     *
     * @param utilizationMips the new utilization mips
     */
    protected final void setUtilizationMips(double utilizationMips) {
        this.utilizationMips = utilizationMips;
    }

    @Override
    public double getPreviousUtilizationMips() {
        return previousUtilizationMips;
    }

    /**
     * Sets the previous utilization of CPU in mips.
     *
     * @param previousUtilizationMips the new previous utilization of CPU in
     * mips
     */
    protected final void setPreviousUtilizationMips(double previousUtilizationMips) {
        this.previousUtilizationMips = previousUtilizationMips;
    }

    @Override
    public List<HostStateHistoryEntry> getStateHistory() {
        return Collections.unmodifiableList(stateHistory);
    }

    @Override
    public void addStateHistoryEntry(double time, double allocatedMips, double requestedMips, boolean isActive) {
        final HostStateHistoryEntry newState = new HostStateHistoryEntry(time, allocatedMips, requestedMips, isActive);
        if (!stateHistory.isEmpty()) {
            final HostStateHistoryEntry previousState = stateHistory.get(stateHistory.size() - 1);
            if (previousState.getTime() == time) {
                stateHistory.set(stateHistory.size() - 1, newState);
                return;
            }
        }

        stateHistory.add(newState);
    }

}
