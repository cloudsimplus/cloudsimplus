/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.schedulers.vm;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;


/// A Virtual Machine Monitor (VMM), also called Hypervisor, that allocates one or more
/// cores from a [Host] to a [Vm], **and doesn't allow sharing of cores between VMs**.
/// The allocated cores will be used until the VM finishes running.
/// If there aren't enough free cores as required by a VM, or the available cores
/// don't have enough capacity, the allocation fails. In the case of fail, no
/// core is allocated to the requesting VM.
///
/// This scheduler is conventionally used when there is an SLA to be met by the cloud provider.
/// More affordable services usually don't allocate CPUs exclusively and may use time-shared schedulers.
///
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 1.0
public class VmSchedulerSpaceShared extends VmSchedulerAbstract {

    /// Creates a space-shared VM scheduler.
    public VmSchedulerSpaceShared() {
        this(DEF_VM_MIGRATION_CPU_OVERHEAD);
    }

    /// Creates a space-shared VM scheduler, defining a CPU overhead for VM migration.
    ///
    /// @param vmMigrationCpuOverhead the percentage of Host's CPU usage increase when a
    /// VM is migrating in or out the Host. The value is in scale from 0 to 1 (where 1 is 100%).
    public VmSchedulerSpaceShared(final double vmMigrationCpuOverhead){
        super(vmMigrationCpuOverhead);
    }

    @Override
    protected boolean isSuitableForVmInternal(final Vm vm, final CoreCapacity requestedMips) {
        return getHost().getProcessor().getFreeCores() >= requestedMips.cores() && isPeCapacityEnough(requestedMips);
    }

    /// Checks if each Host core has enough MIPS capacity for each core requested by a VM.
    /// @param requestedMips a MIPS share requested by a VM
    /// @return true if the core capacity is enough or no core is requested, false otherwise
    private boolean isPeCapacityEnough(final CoreCapacity requestedMips) {
        return requestedMips.cores() == 0 || requestedMips.mips() <= getPeCapacity();
    }

    @Override
    public boolean allocatePesForVmInternal(final Vm vm, final CoreCapacity requestedMips) {
        if(!isSuitableForVmInternal(vm, requestedMips)){
            return false;
        }

        ((VmSimple)vm).setAllocatedMips(requestedMips);
        return true;
    }

    @Override
    protected long deallocatePesFromVmInternal(final Vm vm, final int coresToRemove) {
        final var vmSimple = (VmSimple) vm;
        return removePesFromVm(vm, vmSimple.getAllocatedMips(), vmSimple::setAllocatedMips, coresToRemove);
    }
}
