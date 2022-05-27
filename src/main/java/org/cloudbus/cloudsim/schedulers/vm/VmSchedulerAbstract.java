/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.vm;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.schedulers.MipsShare;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * An abstract class for implementation of {@link VmScheduler}s.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public abstract class VmSchedulerAbstract implements VmScheduler {

    /**
     * @see #getHost()
     */
    private Host host;

    /**
     * @see #getVmMigrationCpuOverhead()
     */
    private final double vmMigrationCpuOverhead;

    /**
     * Creates a VmScheduler, defining a CPU overhead for VM migration.
     * @param vmMigrationCpuOverhead the percentage of Host's CPU usage increase when a
     * VM is migrating in or out of the Host. The value is in scale from 0 to 1 (where 1 is 100%).
     */
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
    public final boolean isSuitableForVm(final Vm vm, final MipsShare requestedMips) {
        if(requestedMips.isEmpty()){
            LOGGER.warn(
                "{}: {}: It was requested an empty list of PEs for {} in {}",
                host.getSimulation().clockStr(), getClass().getSimpleName(), vm, host);
            return false;
        }


        if(host.isFailed()){
            return false;
        }

        return isSuitableForVmInternal(vm, requestedMips);
    }

    protected abstract boolean isSuitableForVmInternal(Vm vm, MipsShare requestedMips);

    @Override
    public final boolean allocatePesForVm(final Vm vm) {
        return allocatePesForVm(vm, new MipsShare(vm.getProcessor()));
    }

    @Override
    public final boolean allocatePesForVm(final Vm vm, final MipsShare requestedMips) {
        if (!vm.isInMigration() && host.getVmsMigratingOut().contains(vm)) {
            host.removeVmMigratingOut(vm);
        }

        ((VmSimple)vm).setRequestedMips(new MipsShare(requestedMips));
        if(allocatePesForVmInternal(vm, requestedMips)) {
            updateHostPesStatusToBusy(vm);
            return true;
        }

        return false;
    }

    /**
     * Based on the number of PEs required by a given VM, sets the status of the same
     * number of physical PEs in its Host to BUSY.
     * @param vm the VM to set the status of its used physical PEs
     * */
    private void updateHostPesStatusToBusy(final Vm vm) {
        updateHostPesStatus(host.getFreePeList(), vm.getNumberOfPes(), Pe.Status.BUSY);
    }

    /**
     * Based on a specific number of virtual PEs, sets the status of the same
     * number of physical PEs in its Host to a given status.
     * @param peList the list of physical PEs from which the corresponding virtual PEs will have the status changed
     * @param vPesNumber the number of Virtual PEs that correspond to the number of physical PEs to have their status changed
     * @param newStatus the status to set
     */
    private void updateHostPesStatus(final List<Pe> peList, final long vPesNumber, final Pe.Status newStatus) {
        if(vPesNumber <= 0) {
            return;
        }

        final var selectedPesList = peList.stream().limit(vPesNumber).collect(toList());
        ((HostSimple)host).setPeStatus(selectedPesList, newStatus);
    }

    protected abstract boolean allocatePesForVmInternal(Vm vm, MipsShare mipsShareRequested);

    @Override
    public void deallocatePesFromVm(final Vm vm) {
        deallocatePesFromVm(vm, (int)vm.getNumberOfPes());
    }

    @Override
    public void deallocatePesFromVm(final Vm vm, final int pesToRemove) {
        if(pesToRemove <= 0 || vm.getNumberOfPes() == 0){
            return;
        }

        final long removedPes = deallocatePesFromVmInternal(vm, pesToRemove);
        updateHostUsedPesToFree(removedPes);
    }

    /**
     * Sets the status of physical PEs used by a destroyed VM to FREE.
     * That works for any kind of scheduler, such as time- and space-shared.
     * @param removedPes number of PEs actually removed from the VM
     */
    private void updateHostUsedPesToFree(final long removedPes) {
        updateHostPesStatus(host.getBusyPeList(), removedPes, Pe.Status.FREE);
    }

    /**
     * Tries to remove a given number of PEs allocated to a VM
     * @param vm the VM to remove PEs
     * @param mipsShare the VM mips share where to remove PEs from
     * @param pesToRemove the number of PEs to remove
     * @return the number of actual removed PEs
     */
    protected final long removePesFromVm(final Vm vm, final MipsShare mipsShare, final long pesToRemove) {
        return mipsShare.remove(Math.min(vm.getNumberOfPes(), pesToRemove));
    }

    protected abstract long deallocatePesFromVmInternal(Vm vm, int pesToRemove);

    @Override
    public MipsShare getAllocatedMips(final Vm vm) {
        final MipsShare mipsShare = ((VmSimple)vm).getAllocatedMips();

        /*
        When a VM is migrating out of the source Host, its allocated MIPS
        is reduced due to migration overhead.
        When it is migrating into the target Host, it also
        experiences overhead, but for the first time the VM is allocated into
        the target Host, the allocated MIPS is stored already considering this overhead.
         */
        return host.getVmsMigratingOut().contains(vm) ? getMipsShareRequestedReduced(vm, mipsShare) : mipsShare;
    }

    /**
     * Gets an adjusted List of MIPS requested by a VM, reducing every MIPS which is higher
     * than the {@link #getPeCapacity() capacity of each physical PE} to that value.
     *
     * @param vm the VM to get the MIPS requested
     * @param mipsShareRequested the VM requested MIPS List
     * @return a new VM requested MIPS share with adjusted MIPS capacity
     */
    protected MipsShare getMipsShareRequestedReduced(final Vm vm, final MipsShare mipsShareRequested){
        final double peMips = getPeCapacity();
        final long requestedPes = mipsShareRequested.pes();
        final double requestedMips = mipsShareRequested.mips();
        return new MipsShare(requestedPes, Math.min(requestedMips, peMips)*percentOfMipsToRequest(vm));
    }

    @Override
    public double getTotalAllocatedMipsForVm(final Vm vm) {
        return getAllocatedMips(vm).totalMips();
    }

    /**
     * Gets PE capacity in MIPS.
     *
     * @return
     *
     * @TODO It considers that all PEs have the same capacity, what has been
     *       shown doesn't be assured. The peList received by the VmScheduler can be
     *       heterogeneous PEs.
     */
    public long getPeCapacity() {
        return getWorkingPeList().isEmpty() ? 0 : getWorkingPeList().get(0).getCapacity();
    }

    /**
     * Gets the list of working PEs from the Host, <b>which excludes failed PEs</b>.
     *
     * @return
     *
     */
    public final List<Pe> getWorkingPeList() {
        return host.getWorkingPeList();
    }

    @Override
    public MipsShare getRequestedMips(final Vm vm) {
        return ((VmSimple)vm).getRequestedMips();
    }

    @Override
    public double getTotalAvailableMips() {
        final var vmStream = Stream.concat(host.getVmList().stream(), host.getVmsMigratingIn().stream());
        final double allocatedMips =
                vmStream
                    .map(vm -> (VmSimple)vm)
                    .mapToDouble(this::actualVmTotalRequestedMips)
                    .sum();

        return host.getTotalMipsCapacity() - allocatedMips;
    }

    /**
     * Gets the sum of MIPS requested by each VM PE, including
     * the CPU overhead if the VM is in migration to this Host.
     *
     * <p>For instance, if the migration overhead is 10% and
     * the total requested MIPS of a VM is 1000 MIPS,
     * it will be allocated just 900 MIPS, but from this values, this method
     * returns the 1000 MIPS, which is the actual MIPS being
     * used by the Host (900 by the VM and 100 by migration overhead).</p>
     *
     * @param vm
     * @return the actual requested MIPS sum across all VM PEs,
     * including the CPU overhead of the VM is in migration to this Host
     */
    private double actualVmTotalRequestedMips(final VmSimple vm) {
        final double totalVmRequestedMips = vm.getAllocatedMips().totalMips();

        /*If the VM is migrating in or out this Host,
        there is a migration overhead.
        Considering the overhead is 10%,
        if it's migrating in, it's just allocated just this 10%
        for the migration process.
        If it's migrating out, it's allocated 90% for the VM,
        and 10% for the migration process.

        The line below computes the original
        requested MIPS (which correspond to 100%)
        */
        return totalVmRequestedMips / percentOfMipsToRequest(vm);
    }

    /**
     * Gets the percentage of the MIPS requested by a VM
     * that will be in fact requested to the Host, according to the VM migration
     * status:
     *
     * <ul>
     *  <li>VM is migrating out of this Host: the MIPS requested by VM will be reduced
     *   according to the
     *   {@link #getVmMigrationCpuOverhead() CPU migration overhead}.
     *   The number of MIPS corresponding to the CPU overhead is used
     *   by the Host to perform the migration;</li>
     *  <li>VM is migrating into this Host: only a fraction of its requested MIPS will be
     *   in fact requested to the Host. This amount is computed by reducing the
     *   {@link #getVmMigrationCpuOverhead() CPU migration overhead};</li>
     *  <li>VM is not in migration: 100% of its requested MIPS will be
     *   in fact requested to the Host</li>
     * </ul>
     * @param vm the VM that is requesting MIPS from the Host
     * @return the percentage of MIPS requested by the VM that will be in fact
     * requested to the Host (in scale from [0 to 1], where  is 100%)
     */
    protected double percentOfMipsToRequest(final Vm vm) {
        if (host.getVmsMigratingIn().contains(vm)) {
            /* While the VM is migrating in,
            the destination host only increases CPU usage according
            to the CPU migration overhead.
             */
            return vmMigrationCpuOverhead;
        }

        if (host.getVmsMigratingOut().contains(vm)) {
            /* While the VM is migrating out, the host where it's migrating from
            experiences a performance degradation.
            Thus, the allocated MIPS for that VM is reduced according to the CPU migration
            overhead.*/
            return getMaxCpuUsagePercentDuringOutMigration();
        }

        //VM is not migrating, thus 100% of its requested MIPS will be requested to the Host.
        return 1;
    }

    @Override
    public double getMaxCpuUsagePercentDuringOutMigration() {
        return 1 - vmMigrationCpuOverhead;
    }

    @Override
    public double getVmMigrationCpuOverhead() {
        return vmMigrationCpuOverhead;
    }

    @Override
    public Host getHost() {
        return host;
    }

    @Override
    public final VmScheduler setHost(final Host host) {
        if(isOtherHostAssigned(requireNonNull(host))){
            throw new IllegalStateException("VmScheduler already has a Host assigned to it. Each Host must have its own VmScheduler instance.");
        }

        this.host = host;
        return this;
    }

    /**
     * Checks if the {@link VmScheduler} has a {@link Host} assigned that is
     * different from the given one
     *
     * @param host the Host to check if assigned scheduler's Host is different from
     * @return
     */
    private boolean isOtherHostAssigned(final Host host) {
        /*It's used != instead of !equals() because when a VmScheduler is set to a Host,
        * the Host may not have an ID yet.
        * That may happen when the Host is created without an id,
        * which is set only when the Host list is assigned to a Datacenter. */
        return this.host != null && this.host != Host.NULL && host != this.host;
    }
}
