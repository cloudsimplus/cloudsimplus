/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.schedulers.vm;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;


/// A Virtual Machine Monitor (VMM), also called Hypervisor,
/// that defines a policy to allocate one or more cores from a [Host] to a [Vm],
/// and allows sharing of cores by multiple VMs.
/// **This class also implements 10% performance degradation due to VM migration.
/// It does not support resource over-subscription.**
///
/// Each Host has to use its own instance of a VmScheduler that will so
/// schedule the allocation of Host's cores for VMs running on it.
///
/// It does not perform a preemption process to move running
/// VMs to the waiting list to make room for other VMs already waiting.
/// It just imposes there isn't waiting VMs,
/// **oversimplifying** the scheduling, considering that for a given simulation
/// second _t_, the total processing capacity (in MIPS) of the processor cores (cores)
/// is equally divided by the VMs that are using them.
///
/// In CPUs enabled with [Hyper-threading technology (HT)](https://en.wikipedia.org/wiki/Hyper-threading),
/// it is possible to run up to 2 processes at the same physical CPU core.
/// However, this scheduler implementation
/// oversimplifies a possible HT feature by allowing several VMs to use a fraction of the MIPS capacity from
/// physical cores, until that the total capacity of the virtual core is allocated.
/// Consider that a virtual core is requiring 1000 MIPS but there is no physical core
/// with such a capacity. The scheduler will allocate these 1000 MIPS across several physical cores,
/// for instance, by allocating 500 MIPS from core 0, 300 from core 1 and 200 from core 2, totaling the 1000 MIPS required
/// by the virtual core.
///
/// In a real hypervisor in a Host that has Hyper-threading CPU cores,
/// one physical core can be allocated to two virtual cores (preemptively),
/// but a single virtual core can use just one physical core at a time.
///
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 1.0
public class VmSchedulerTimeShared extends VmSchedulerAbstract {

    /// Creates a time-shared VM scheduler with a default CPU overhead for VM migrations.
    public VmSchedulerTimeShared() {
        this(DEF_VM_MIGRATION_CPU_OVERHEAD);
    }

    /// Creates a time-shared VM scheduler, defining a CPU overhead for VM migration.
    ///
    /// @param vmMigrationCpuOverhead the percentage of Host's CPU usage increase when a
    /// VM is migrating in or out of the Host. The value is in scale from 0 to 1 (where 1 is 100%).
    public VmSchedulerTimeShared(final double vmMigrationCpuOverhead){
        super(vmMigrationCpuOverhead);
    }

    @Override
    public boolean allocatePesForVmInternal(final Vm vm, final CoreCapacity requestedMips) {
        return allocateMipsShareForVmInternal(vm, requestedMips);
    }

    /// Try to allocate the MIPS requested by a VM
    /// and update the allocated MIPS share.
    ///
    /// @param vm the VM to allocate MIPS to
    /// @param requestedMips the MIPS share requested by the VM
    /// @return true if successful, false otherwise
    private boolean allocateMipsShareForVmInternal(final Vm vm, final CoreCapacity requestedMips) {
        if (!isSuitableForVm(vm, requestedMips)) {
            return false;
        }

        allocateMipsShareForVm(vm, requestedMips);
        return true;
    }

    /// Performs the allocation of a MIPS share to a given VM.
    /// The actual MIPS to be allocated to the VM may be reduced
    /// if the VM is in migration, due to migration overhead.
    ///
    /// @param vm the VM to allocate MIPS to
    /// @param requestedMipsReduced the MIPS share to allocate to the VM,
    /// after it being adjusted by the [VmSchedulerAbstract#getMipsShareRequestedReduced(Vm,CoreCapacity)] method.
    /// @see VmSchedulerAbstract#getMipsShareRequestedReduced(Vm, CoreCapacity)
    protected void allocateMipsShareForVm(final Vm vm, final CoreCapacity requestedMipsReduced) {
        final var mipsShare = getMipsShareToAllocate(vm, requestedMipsReduced);
        ((VmSimple)vm).setAllocatedMips(mipsShare);
    }

    /// The non-emptiness of the list is ensured by the [VmScheduler#isSuitableForVm(Vm,CoreCapacity)] method.
    @Override
    protected boolean isSuitableForVmInternal(final Vm vm, final CoreCapacity requestedMips) {
        final double totalRequestedMips = requestedMips.totalMips();

        // This scheduler does not allow over-subscription of cores' MIPS
        return getHost().getProcessor().getWorkingCores() >= requestedMips.cores() && getTotalAvailableMips() >= totalRequestedMips;
    }

    /// Gets the actual MIPS share that will be allocated to VM's cores,
    /// considering the VM migration status.
    /// If the VM is in migration, this will cause overhead, reducing
    /// the amount of MIPS allocated to the VM.
    ///
    /// @param vm the VM requesting allocation of MIPS
    /// @param requestedMips the MIPS share requested for each vCPU
    /// @return the allocated MIPS share to the VM
    protected CoreCapacity getMipsShareToAllocate(final Vm vm, final CoreCapacity requestedMips) {
        return getMipsShareToAllocate(requestedMips, mipsPercentToRequest(vm));
    }

    /// Gets the actual MIPS share that will be allocated to VM's cores,
    /// considering the VM migration status.
    /// If the VM is in migration, this will cause overhead, reducing
    /// the amount of MIPS allocated to the VM.
    ///
    /// @param requestedMips the list of MIPS requested for each vCPU
    /// @param scalingFactor the factor that will be used to reduce the amount of MIPS
    /// allocated to each vCPU (which is a percentage value between \[0..1\]) in case the VM is in migration
    /// @return the MIPS share allocated to the VM
    protected CoreCapacity getMipsShareToAllocate(final CoreCapacity requestedMips, final double scalingFactor) {
        if(scalingFactor == 1){
            return requestedMips;
        }

        return new CoreCapacity(requestedMips.cores(), requestedMips.mips()*scalingFactor);
    }

    @Override
    protected long deallocatePesFromVmInternal(final Vm vm, final int coresToRemove) {
        final var vmSimple = (VmSimple) vm;
        return Math.max(
            removePesFromVm(vm, vmSimple.getRequestedMips(), vmSimple::setRequestedMips, coresToRemove),
            removePesFromVm(vm, vmSimple.getAllocatedMips(), vmSimple::setAllocatedMips, coresToRemove));
    }
}
