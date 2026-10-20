/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.schedulers.vm;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.schedulers.MipsShare;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;


/// A Virtual Machine Monitor (VMM), also called Hypervisor, that allocates one or more
/// [Pe]s from a [Host] to a [Vm], **and doesn't allow sharing of PEs between VMs**.
/// The allocated PEs will be used until the VM finishes running.
/// If there aren't enough free PEs as required by a VM, or the available PEs
/// don't have enough capacity, the allocation fails. In the case of fail, no
/// PE is allocated to the requesting VM.
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
    protected boolean isSuitableForVmInternal(final Vm vm, final MipsShare requestedMips) {
        return getHost().getFreePesNumber() >= requestedMips.pes() && isPeCapacityEnough(requestedMips);
    }

    /// Checks if each Host PE has enough MIPS capacity for each PE requested by a VM.
    /// @param requestedMips a MIPS share requested by a VM
    /// @return true if the PE capacity is enough or no PE is requested, false otherwise
    private boolean isPeCapacityEnough(final MipsShare requestedMips) {
        return requestedMips.pes() == 0 || requestedMips.mips() <= getPeCapacity();
    }

    @Override
    public boolean allocatePesForVmInternal(final Vm vm, final MipsShare requestedMips) {
        if(!isSuitableForVmInternal(vm, requestedMips)){
            return false;
        }

        ((VmSimple)vm).setAllocatedMips(requestedMips);
        return true;
    }

    @Override
    protected long deallocatePesFromVmInternal(final Vm vm, final int pesToRemove) {
        return removePesFromVm(vm, ((VmSimple)vm).getAllocatedMips(), pesToRemove);
    }
}
