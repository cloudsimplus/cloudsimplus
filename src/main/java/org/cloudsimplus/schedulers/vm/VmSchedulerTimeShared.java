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

import static org.cloudsimplus.resources.Processor.workingCores;

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
/// second _t_, the total processing capacity (in MIPS) of the processor cores
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

    /// A non-empty MIPS share is ensured by the [VmScheduler#isSuitableForVm(Vm,CoreCapacity)] method.
    @Override
    protected boolean isSuitableForVmInternal(final Vm vm, final CoreCapacity requestedMips) {
        final double totalRequestedMips = requestedMips.totalMips();

        // This scheduler does not allow over-subscription of cores' MIPS
        return workingCores(getHost()) >= requestedMips.cores() && getTotalAvailableMips() >= totalRequestedMips;
    }

    /// {@inheritDoc}
    /// If the VM is in migration, this will cause overhead, reducing
    /// the amount of MIPS allocated to the VM.
    ///
    /// @param vm the VM requesting allocation of MIPS
    /// @param requestedMips the MIPS share requested for each vCPU
    /// @return the allocated MIPS share to the VM
    @Override
    protected CoreCapacity getMipsShareToAllocate(final Vm vm, final CoreCapacity requestedMips) {
        return requestedMips.scaleMips(mipsPercentToRequest(vm));
    }
}
