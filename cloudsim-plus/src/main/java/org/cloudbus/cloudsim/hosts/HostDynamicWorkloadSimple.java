/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.hosts;

import org.cloudbus.cloudsim.hosts.power.PowerHostUtilizationHistory;
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
 * @todo This class is confusing is just appears to be duplicating code with the {@link PowerHostUtilizationHistory}.
 * It appears to be a non-power aware Host that just stores usage store,
 * while the other Host has these two behaviors.
 */
public class HostDynamicWorkloadSimple extends HostSimple implements HostDynamicWorkload {

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
        setPreviousUtilizationMips(getUtilizationOfCpuMips());
        final double smallerTime = super.updateProcessing(currentTime);
        double hostTotalRequestedMips = 0;

        for (final Vm vm : getVmList()) {
            final double totalRequestedMips = vm.getCurrentRequestedTotalMips();

            showVmResourceUsageOnHost(vm);
            final double totalAllocatedMips = addVmResourceUseToHistoryIfNotMigratingIn(vm, currentTime);

            hostTotalRequestedMips += totalRequestedMips;
        }

        addStateHistoryEntry(currentTime, getUtilizationOfCpuMips(), hostTotalRequestedMips,getUtilizationOfCpuMips() > 0);

        return smallerTime;
    }

    /**
     * Adds the VM resource usage to the History if the VM is not migrating into the Host.
     * @param vm the VM to add its usage to the history
     * @param currentTime the current simulation time
     * @return the total allocated MIPS for the given VM
     */
    private double addVmResourceUseToHistoryIfNotMigratingIn(Vm vm, double currentTime) {
        double totalAllocatedMips = getVmScheduler().getTotalAllocatedMipsForVm(vm);
        if (getVmsMigratingIn().contains(vm)) {
            Log.printFormattedLine("%.2f: [" + this + "] " + vm
                    + " is migrating in", getSimulation().clock());
            return totalAllocatedMips;
        }

        final double totalRequestedMips = vm.getCurrentRequestedTotalMips();
        if (totalAllocatedMips + 0.1 < totalRequestedMips) {
            final String reason = getVmsMigratingOut().contains(vm) ? "migration overhead" : "capacity unavailability";
            final double notAllocatedMipsByPe = (totalRequestedMips - totalAllocatedMips)/vm.getNumberOfPes();
            Log.printFormattedLine(
                "%.2f: [%s] %.0f MIPS not allocated for each one of the %d PEs from %s due to %s.",
                getSimulation().clock(), this, notAllocatedMipsByPe, vm.getNumberOfPes(), vm, reason);
        }

        final VmStateHistoryEntry entry = new VmStateHistoryEntry(
                currentTime,
                totalAllocatedMips,
                totalRequestedMips,
                vm.isInMigration() && !getVmsMigratingIn().contains(vm));
        vm.addStateHistoryEntry(entry);

        if (vm.isInMigration()) {
            Log.printFormattedLine(
                    "%.2f: [" + this + "] " + vm + " is migrating out ",
                    getSimulation().clock());
            totalAllocatedMips /= getVmScheduler().getMaxCpuUsagePercentDuringOutMigration();
        }

        return totalAllocatedMips;
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

    private void showVmResourceUsageOnHost(Vm vm) {
        if (Log.isDisabled() || vm.getHost() == Host.NULL) {
            return;
        }

        final double totalRequestedMips = vm.getCurrentRequestedTotalMips();
        final double totalAllocatedMips = getVmScheduler().getTotalAllocatedMipsForVm(vm);

        getDatacenter().println(String.format(
                "%.2f: [" + this + "] Total allocated MIPS for " + vm
                + " (" + vm.getHost()
                + ") is %.2f. Vm requested %.2f out of its total %.2f MIPS (%.2f%%)",
                getSimulation().clock(),
                totalAllocatedMips,
                totalRequestedMips,
                vm.getTotalMipsCapacity(),
                totalRequestedMips / vm.getTotalMipsCapacity() * 100));

        final List<Pe> pes = getVmScheduler().getPesAllocatedForVm(vm);
        final StringBuilder pesString = new StringBuilder();
        pes.forEach(pe ->
            pesString.append(
                    String.format(" PE #%d: %d.",
                            pe.getId(),
                            pe.getPeProvisioner().getAllocatedResourceForVm(vm)))
        );

        getDatacenter().println(String.format(
                "%.2f: [" + this + "] MIPS for " + vm + " working PEs ("
                + getNumberOfWorkingPes()+ " * " + getVmScheduler().getPeCapacity() + "): "
                + pesString,
                getSimulation().clock()));
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
    public double getPreviousUtilizationOfCpu() {
        return computeCpuUtilizationPercent(getPreviousUtilizationMips());
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

}
