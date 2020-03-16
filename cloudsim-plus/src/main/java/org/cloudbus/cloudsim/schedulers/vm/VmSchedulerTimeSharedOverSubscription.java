/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.vm;

import org.cloudbus.cloudsim.vms.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A Time-Shared VM Scheduler which allows over-subscription. In other
 * words, the scheduler still enables allocating into a Host, VMs which require more CPU
 * MIPS than there is available. If the Host has at least the number of PEs a VM
 * requires, the VM will be allowed to run into it.
 *
 * <p>The scheduler doesn't in fact allocates more MIPS for Virtual PEs (vPEs)
 * than there is in the physical PEs. It just reduces the allocated
 * amount according to the available MIPS.
 * This is an over-subscription, resulting in performance degradation
 * because less MIPS may be allocated than the required by a VM.
 * </p>
 *
 * @author Anton Beloglazov
 * @author Rodrigo N. Calheiros
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 3.0
 */
public class VmSchedulerTimeSharedOverSubscription extends VmSchedulerTimeShared {
    private static final Logger LOGGER = LoggerFactory.getLogger(VmSchedulerTimeSharedOverSubscription.class.getSimpleName());

    /**
     * Creates a time-shared over-subscription VM scheduler.
     */
    public VmSchedulerTimeSharedOverSubscription(){
        this(DEFAULT_VM_MIGRATION_CPU_OVERHEAD);
    }

    /**
     * Creates a time-shared over-subscription VM scheduler, defining a CPU overhead for VM migration.
     *
     * @param vmMigrationCpuOverhead the percentage of Host's CPU usage increase when a
     * VM is migrating in or out of the Host. The value is in scale from 0 to 1 (where 1 is 100%).
     */
    public VmSchedulerTimeSharedOverSubscription(final double vmMigrationCpuOverhead){
        super(vmMigrationCpuOverhead);
    }

    /**
     * Checks if a list of MIPS requested by a VM is allowed to be allocated or not.
     * When there isn't the amount of requested MIPS available, this {@code VmScheduler}
     * allows to allocate what is available for the requesting VM,
     * allocating less that is requested.
     *
     * <p>This way, the only situation when it will not allow
     * the allocation of MIPS for a VM is when the number of PEs
     * required is greater than the total number of physical PEs.
     * Even when there is not available MIPS at all, it allows
     * the allocation of MIPS for the VM by reducing the allocation
     * of other VMs.</p>
     *
     * @param vm {@inheritDoc}
     * @param requestedMips {@inheritDoc}
     * @return true if the requested MIPS List is allowed to be allocated to the VM, false otherwise
     * @see #allocateMipsShareForVm(Vm, List)
     */
    @Override
    protected boolean isSuitableForVmInternal(final Vm vm, final List<Double> requestedMips){
        return getHost().getWorkingPesNumber() >= requestedMips.size();
    }

    @Override
    protected void allocateMipsShareForVm(final Vm vm, final List<Double> requestedMipsReduced) {
        if(requestedMipsReduced.isEmpty()){
            return;
        }

        final double totalRequestedMips = requestedMipsReduced.get(0) * requestedMipsReduced.size();
        if (getTotalAvailableMips() >= totalRequestedMips) {
            super.allocateMipsShareForVm(vm, requestedMipsReduced);
            return;
        }

        redistributeMipsDueToOverSubscription();
    }

    /**
     * Redistribute the allocation of MIPs among all VMs when the total
     * MIPS requested by all of them is higher than the total available MIPS.
     * This way, it reduces the MIPS allocated to all VMs in order to
     * enable all MIPS requests to be fulfilled.
     *
     * <p>Updates the Map containing the list of allocated MIPS by all VMs,
     * reducing the amount requested according to a scaling factor.
     * This is performed when the amount of total requested MIPS by all VMs
     * is higher than the total available MIPS. The reduction
     * of the MIPS requested by all VMs enables all requests to be fulfilled.</p>
     * @see #getAllocatedMipsMap()
     */
    private void redistributeMipsDueToOverSubscription() {
        // First, we calculate the scaling factor - the MIPS allocation for all VMs will be scaled proportionally
        final Map<Vm, List<Double>> mipsMapRequestedReduced = getNewTotalRequestedMipsByAllVms();

        final double scalingFactor = getVmsMipsScalingFactor(mipsMapRequestedReduced);

        getAllocatedMipsMap().clear();
        for (final Entry<Vm, List<Double>> entry : mipsMapRequestedReduced.entrySet()) {
            final Vm vm = entry.getKey();
            List<Double> updatedMipsAllocation = getMipsShareToAllocate(vm, entry.getValue());
            updatedMipsAllocation = getMipsShareToAllocate(updatedMipsAllocation, scalingFactor);
            putAllocatedMipsMap(vm, updatedMipsAllocation);
        }
    }

    /**
     * Gets the factor that will be used to reduce the amount of MIPS allocated to each vPE.
     * When the total amount of MIPS requested by all VMs is greater than the total
     * MIPS capacity of the Host, the MIPS requested by VMs is reduced to fit
     * into the Host. Otherwise, the method returns 1 (100%) to indicate the
     * MIPS requested will not be changed.
     *
     * @param mipsMapRequestedReduced the map of MIPS requested by each VM, after being
     *                                adjusted to avoid allocating more MIPS for a vPE
     *                                than there is in the physical PE
     * @return the scaling factor to apply for VMs requested MIPS (a percentage value in scale from 0 to 1)
     * @see #getMipsShareRequestedReduced(Vm, List)
     */
    private double getVmsMipsScalingFactor(final Map<Vm, List<Double>> mipsMapRequestedReduced) {
        final double totalMipsCapacity = getHost().getTotalMipsCapacity();
        final double totalMipsToAllocateForAllVms = getTotalMipsToAllocateForAllVms(mipsMapRequestedReduced);
        return Math.min(1, totalMipsCapacity / totalMipsToAllocateForAllVms);
    }

    /**
     * Generate a new Map containing the list of requested MIPS by all VMs,
     * ensuring the MIPS requested for each vPE doesn't exceeds
     * the capacity of each Physical PE.
     *
     * @return the new map of requested MIPS for all VMs
     * @see #getRequestedMipsMap()
     */
    private Map<Vm, List<Double>> getNewTotalRequestedMipsByAllVms() {
        final Map<Vm, List<Double>> mipsMapRequestedReduced = new HashMap<>(getRequestedMipsMap().entrySet().size());
        for (final Entry<Vm, List<Double>> entry : getRequestedMipsMap().entrySet()) {
            final Vm vm = entry.getKey();
            final List<Double> requestedMipsReduced = getMipsShareRequestedReduced(entry.getKey(), entry.getValue());
            mipsMapRequestedReduced.put(vm, requestedMipsReduced);
        }

        return mipsMapRequestedReduced;
    }

    /**
     * Gets the total MIPS that will be allocated to all VMs.
     * For VMs that are migrating into the Host,
     * just the {@link #getVmMigrationCpuOverhead()}
     * will be allocated, representing just the CPU migration overhead
     * while the VM is in migration process.
     *
     * @param mipsMapRequestedReduced the map of MIPS requested by each VM, after being
     *                                adjusted to avoid allocating more MIPS for a vPE
     *                                than there is in the physical PE
     * @return the total MIPS to be allocated for all VMs, considering the
     * VMs migrating into the Host.
     * @see #getMipsShareRequestedReduced(Vm, List)
     */
    private double getTotalMipsToAllocateForAllVms(final Map<Vm, List<Double>> mipsMapRequestedReduced){
        return mipsMapRequestedReduced.entrySet()
            .stream()
            .mapToDouble(this::getMipsToBeAllocatedForVmPes)
            .sum();
    }

    /**
     * Gets the total MIPS to be allocated to a VM (across all vPEs),
     * considering if the VM is migrating into the Host.
     * In this case, just a percentage of the total required MIPS will
     * be in fact allocated to representing the CPU migration overhead.
     * @param entry a Map entry containing a VM and the List of MIPS required by its vPEs
     * @return the sum of required MIPS by all vPEs, considering the VMs
     * in migration process to the Host.
     */
    private double getMipsToBeAllocatedForVmPes(final Map.Entry<Vm, List<Double>> entry){
        final double requiredMipsByThisVm = entry.getValue().stream().reduce(0.0, Double::sum);
        if (getHost().getVmsMigratingIn().contains(entry.getKey())) {
            /*
            the destination host only experiences a percentage of the migrating VM's MIPS
            which is the migration CPU overhead.
            */
            return requiredMipsByThisVm * getVmMigrationCpuOverhead();
        }

        return requiredMipsByThisVm;
    }
}
