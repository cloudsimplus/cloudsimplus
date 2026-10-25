/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.schedulers.vm;

import lombok.Getter;
import lombok.NonNull;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.resources.Processor;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmAbstract;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.cloudsimplus.resources.Processor.totalMips;


/// An abstract class for implementation of [VmScheduler]s.
///
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 1.0
@Getter
public abstract non-sealed class VmSchedulerAbstract implements VmScheduler {
    private Host host;
    private final double vmMigrationCpuOverhead;

    /// Creates a VmScheduler, defining a CPU overhead for VM migration.
    /// @param vmMigrationCpuOverhead the percentage of Host's CPU usage increase when a
    /// VM is migrating in or out the Host. The value is in scale from 0 to 1 (where 1 is 100%).
    public VmSchedulerAbstract(final double vmMigrationCpuOverhead){
        if(vmMigrationCpuOverhead < 0 || vmMigrationCpuOverhead >= 1){
            throw new IllegalArgumentException("vmMigrationCpuOverhead must be a percentage value between [0 and 1[");
        }

        setHost(Host.NULL);
        this.vmMigrationCpuOverhead = vmMigrationCpuOverhead;
    }

    @Override
    public final boolean isSuitableForVm(final Vm vm) {
        return isSuitableForVm(vm, vm.getCurrentRequestedMips());
    }

    @Override
    public final boolean isSuitableForVm(@NonNull final Vm vm, @NonNull final CoreCapacity requestedMips) {
        if(requestedMips.isEmpty()){
            LOGGER.warn(
                "{}: {}: It was requested an empty list of CPU cores for {} in {}",
                host.getSimulation().clockStr(), getClass().getSimpleName(), vm, host);
            return false;
        }


        if(host.isFailed()){
            return false;
        }

        return isSuitableForVmInternal(vm, requestedMips);
    }

    protected abstract boolean isSuitableForVmInternal(Vm vm, CoreCapacity requestedMips);

    @Override
    public final boolean allocateCoresForVm(@NonNull final Vm vm, @NonNull final CoreCapacity requestedMips) {
        if (!vm.isInMigration() && host.getVmsMigratingOut().contains(vm)) {
            host.removeVmMigratingOut(vm);
        }

        if(!isSuitableForVm(vm, requestedMips)) {
            return false;
        }

        vmAbstract(vm).setMipsShares(requestedMips, getMipsShareToAllocate(vm, requestedMips));
        updateHostBusyCores(vm);
        return true;
    }

    /// Gets the MIPS share that will in fact be allocated to a VM,
    /// after the request was checked as [suitable][#isSuitableForVmInternal(Vm, CoreCapacity)].
    /// @param vm the VM requesting MIPS
    /// @param requestedMips the MIPS share requested by the VM
    /// @return the MIPS share to allocate to the VM
    protected abstract CoreCapacity getMipsShareToAllocate(Vm vm, CoreCapacity requestedMips);

    @Override
    public final void updateHostBusyCores() {
        updateHostBusyCores(Vm.NULL);
    }

    /// Updates the number of busy Host cores according to the number of cores allocated to the
    /// [VMs on the Host][#hostVms()], plus a given VM that may not be on the Host yet
    /// (such as a VM just allocated, before the Host adds it to its VM list).
    ///
    /// The number of cores is computed from the current state each time,
    /// so it can't drift. When a scheduler shares cores among VMs (such as the time-shared one),
    /// VMs may be allocated more cores than the Host has.
    /// That way, the number of busy Host cores is limited to the number of working Host cores.
    /// @param changedVm a VM whose allocation has just changed, or [Vm#NULL]
    private void updateHostBusyCores(final Vm changedVm) {
        long cores = hostVms().mapToLong(VmSchedulerAbstract::allocatedCores).sum();
        if(changedVm != Vm.NULL && !isOnHost(changedVm)) {
            cores += allocatedCores(changedVm);
        }

        final var processor = host.getProcessor();
        processor.setOccupied(Math.min(cores, processor.getWorkingCores()));
    }

    /// @return the VMs placed into the Host plus the ones migrating into it that
    /// are not in the Host VM list yet (without duplicates).
    private Stream<Vm> hostVms() {
        final List<Vm> vmList = host.getVmList();
        final Set<Vm> vmsMigratingIn = host.getVmsMigratingIn();
        return Stream.concat(vmList.stream(), vmsMigratingIn.stream().filter(vm -> !vmList.contains(vm)));
    }

    private boolean isOnHost(final Vm vm) {
        return host.getVmList().contains(vm) || host.getVmsMigratingIn().contains(vm);
    }

    private static long allocatedCores(final Vm vm) {
        return vmAbstract(vm).getAllocatedMips().cores();
    }

    @Override
    public final boolean resizeVmCores(@NonNull final Vm vm, final long delta) {
        if(delta < 0) {
            return shrinkVmCores(vm, -delta);
        }

        return delta > 0 && growVmCores(vm, delta);
    }

    /// Removes a given number of cores from a VM, updating its processor, its MIPS shares and the Host cores.
    /// The VM [CloudletScheduler][org.cloudsimplus.schedulers.cloudlet.CloudletScheduler]
    /// gets the reduced MIPS share in its next processing update.
    /// @param vm the VM to remove cores from
    /// @param cores the number of cores to remove
    /// @return true if the cores were removed; false if the VM doesn't have that number of working cores
    private boolean shrinkVmCores(final Vm vm, final long cores) {
        if(!vm.getProcessor().freeAndRemove(cores)) {
            return false;
        }

        deallocateCoresFromVm(vm, cores);
        return true;
    }

    /// Adds a given number of cores to a VM, if the Host has room for the VM with all its new cores,
    /// reallocating the VM MIPS shares.
    /// @param vm the VM to add cores to
    /// @param cores the number of cores to add
    /// @return true if the cores were added;
    ///         false if the Host doesn't have room for them (the VM is kept as it was)
    private boolean growVmCores(final Vm vm, final long cores) {
        final var vmAbstract = vmAbstract(vm);
        final CoreCapacity currentRequest = vmAbstract.getRequestedMips();

        // Releases the current VM allocation, so that it isn't counted when checking if the new request fits
        deallocateCoresFromVm(vm);
        vm.getProcessor().addCapacity(cores);
        final CoreCapacity newRequest = vmAbstract.getMipsToRequest();
        if(isSuitableForVm(vm, newRequest)) {
            return allocateCoresForVm(vm, newRequest);
        }

        vm.getProcessor().removeCapacity(cores);
        allocateCoresForVm(vm, currentRequest);
        return false;
    }

    @Override
    public final void deallocateCoresFromVm(final Vm vm) {
        final var vmAbstract = vmAbstract(vm);
        deallocateCoresFromVm(vm, Math.max(vmAbstract.getRequestedMips().cores(), vmAbstract.getAllocatedMips().cores()));
    }

    /// Removes a given number of cores from the VM requested and allocated MIPS shares,
    /// updating the Host cores.
    /// It doesn't remove more cores than each share has.
    /// It's used only to change the VM cores together with its processor
    /// (see [#resizeVmCores(Vm, long)]) or to release all of them (see [#deallocateCoresFromVm(Vm)]).
    /// @param vm the VM to remove cores from
    /// @param coresToRemove the number of cores to remove
    protected final void deallocateCoresFromVm(final Vm vm, final long coresToRemove) {
        if(coresToRemove <= 0){
            return;
        }

        final var vmAbstract = vmAbstract(vm);
        vmAbstract.setMipsShares(
            vmAbstract.getRequestedMips().withoutCores(coresToRemove),
            vmAbstract.getAllocatedMips().withoutCores(coresToRemove));
        updateHostBusyCores(vm);
    }

    @Override
    public CoreCapacity getAllocatedMips(@NonNull final Vm vm) {
        final CoreCapacity mipsShare = vmAbstract(vm).getAllocatedMips();

        /*
        When a VM is migrating out the source Host, its allocated MIPS
        is reduced due to migration overhead.
        When it is migrating into the target Host, it also
        experiences overhead, but for the first time the VM is allocated into
        the target Host, the allocated MIPS is stored already considering this overhead.
         */
        return host.getVmsMigratingOut().contains(vm) ? getMipsShareRequestedReduced(vm, mipsShare) : mipsShare;
    }

    /// Gets an adjusted MIPS share requested by a VM, reducing every MIPS which is higher
    /// than the [MIPS of each working Host core][Processor#getWorkingCoreMips()] to that value.
    ///
    /// @param vm the VM to get the MIPS requested
    /// @param mipsShareRequested the VM requested MIPS share
    /// @return a new VM requested MIPS share with adjusted MIPS capacity
    protected CoreCapacity getMipsShareRequestedReduced(final Vm vm, final CoreCapacity mipsShareRequested){
        final double coreMips = host.getProcessor().getWorkingCoreMips();
        return mipsShareRequested.limitMips(coreMips).scaleMips(mipsPercentToRequest(vm));
    }

    @Override
    public double getTotalAllocatedMips() {
        return totalMips(host) - getTotalAvailableMips();
    }

    @Override
    public double getTotalAllocatedMipsForVm(final Vm vm) {
        return getAllocatedMips(vm).totalMips();
    }


    @Override
    public CoreCapacity getRequestedMips(final Vm vm) {
        return vmAbstract(vm).getRequestedMips();
    }

    @Override
    public double getTotalAvailableMips() {
        final double allocatedMips = hostVms().mapToDouble(this::actualVmTotalRequestedMips).sum();

        return totalMips(host) - allocatedMips;
    }

    /// Gets the sum of MIPS requested by each VM core, including
    /// the CPU overhead if the VM is migration into this Host.
    ///
    /// For instance, consider the migration overhead is 10% and
    /// the total requested MIPS of a VM is 1000 MIPS.
    /// It will be allocated just 900 MIPS, but from these values, this method
    /// returns the 1000 MIPS, which is the actual MIPS being
    /// used by the Host (900 by the VM and 100 by the migration overhead).
    ///
    /// @param vm the VM to get the requested MIPS sum
    /// @return the actual requested MIPS sum across all VM cores,
    /// including the CPU overhead of the VM is in migration to this Host
    private double actualVmTotalRequestedMips(final Vm vm) {
        final double totalVmRequestedMips = vmAbstract(vm).getAllocatedMips().totalMips();

        /* If the VM is migrating in or out this Host,
         there is a migration overhead.
         Considering the overhead is 10%, if it's migrating in,
         it's just allocated this percentage for the migration process.
         If it's migrating out, it's allocated 90% for the VM,
         and 10% for the migration process.

         The line below computes the original
         requested MIPS (which correspond to 100%) */
        return totalVmRequestedMips / mipsPercentToRequest(vm);
    }

    /// Gets the percentage of the MIPS requested by a VM
    /// that will be in fact requested to the Host, according to the VM migration
    /// status:
    ///
    /// - VM is migrating out of this Host: the MIPS requested by the VM will be reduced
    ///   according to the [CPU migration overhead][#getVmMigrationCpuOverhead()].
    ///   The Host uses the number of MIPS corresponding to the CPU overhead to perform the migration;
    /// - VM is migrating into this Host: only a fraction of its requested MIPS will be
    ///   in fact requested to the Host. This amount is computed by reducing the
    ///   [CPU migration overhead][#getVmMigrationCpuOverhead()];
    /// - VM is not in migration: 100% of its requested MIPS will be
    ///   in fact requested to the Host
    ///
    /// @param vm the VM that is requesting MIPS from the Host
    /// @return the percentage of MIPS requested by the VM that will be in fact
    /// requested to the Host (in scale from [0 to 1], where is 100%)
    protected double mipsPercentToRequest(final Vm vm) {
        if (host.getVmsMigratingIn().contains(vm)) {
            /* While the VM is migrating in,
            the destination Host only increases CPU usage according
            to the CPU migration overhead. */
            return vmMigrationCpuOverhead;
        }

        if (host.getVmsMigratingOut().contains(vm)) {
            /* While the VM is migrating out, the Host where it's migrating from
            experiences a performance degradation.
            Thus, the allocated MIPS for that VM is reduced according to the CPU migration
            overhead. */
            return getMaxCpuUsagePercentDuringOutMigration();
        }

        // VM is not migrating, thus 100% of its requested MIPS will be requested to the Host.
        return 1;
    }

    @Override
    public double getMaxCpuUsagePercentDuringOutMigration() {
        return 1 - vmMigrationCpuOverhead;
    }

    @Override
    public final VmScheduler setHost(@NonNull final Host host) {
        if(isOtherHostAssigned(host)){
            throw new IllegalStateException("VmScheduler already has a Host assigned to it. Each Host must have its own VmScheduler instance.");
        }

        this.host = host;
        return this;
    }

    /// Checks if the [VmScheduler] has a [Host] assigned that is
    /// different from the given one
    ///
    /// @param host the Host to check if the assigned scheduler's Host is different from
    /// @return true if the Host assigned to this VmScheduler is different from the given one, false otherwise
    private boolean isOtherHostAssigned(@NonNull final Host host) {
        /* It's used != instead of !equals() because when a VmScheduler is set to a Host,
         * the Host may not have an ID yet.
         * That may happen when the Host is created without an id,
         * which is set only when the Host list is assigned to a Datacenter. */
        return this.host != null && this.host != Host.NULL && host != this.host;
    }

    /// Gets a VM as a [VmAbstract], which stores the MIPS shares managed by the scheduler.
    /// @param vm the VM to get
    /// @return the VM as a VmAbstract
    protected static VmAbstract vmAbstract(final Vm vm) {
        return (VmAbstract) vm;
    }
}
