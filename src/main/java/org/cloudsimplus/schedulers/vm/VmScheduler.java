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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// An interface that represents the policy used by a
/// Virtual Machine Monitor (VMM) to share the processing power of a [Host] between [Vm]s.
/// Each Host has to use its own instance of a
/// VmScheduler that will so schedule the allocation of host's cores for
/// VMs running on it.
///
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.0
public sealed interface VmScheduler permits VmSchedulerAbstract, VmSchedulerNull {
    Logger LOGGER = LoggerFactory.getLogger(VmScheduler.class.getSimpleName());

    /// The default percentage (in scale from \[0..1\]) to define the CPU overhead of VM migration
    /// if one is not explicitly set.
    /// @see #getVmMigrationCpuOverhead()
    double DEF_VM_MIGRATION_CPU_OVERHEAD = 0.1;

    /// An attribute that implements the Null Object Design Pattern for [VmScheduler] objects.
    VmScheduler NULL = new VmSchedulerNull();

    /// Requests the allocation of cores for a VM.
    ///
    /// @param vm the VM to allocate cores to
    /// @param requestedMips the MIPS share to be allocated to a VM
    /// @return true if the cores were allocated to the VM, false otherwise
    ///
    /// When a VM already has allocated MIPS, the new allocation replaces the previous one.
    ///
    /// @TODO When a VM already has allocated MIPS, the suitability check still considers
    /// the total requested MIPS, while only the difference from the current allocation
    /// should be checked. See https://github.com/cloudsimplus/cloudsimplus/issues/588
    boolean allocateCoresForVm(Vm vm, CoreCapacity requestedMips);

    /// Releases all cores allocated to a VM. After that, the cores may be used on demand
    /// by other VMs.
    ///
    /// @param vm the VM to deallocate cores from
    void deallocateCoresFromVm(Vm vm);

    /// Updates the number of busy Host cores according to the cores allocated to the VMs
    /// placed into or migrating to the Host.
    /// The Host calls it after its VM lists change or after it recovers from a failure.
    void updateHostBusyCores();

    /// Changes the number of cores of a VM placed into the Host,
    /// such as for vertical CPU scaling or when Host cores fail.
    /// It updates, in a single operation, the VM [Processor][org.cloudsimplus.resources.Processor],
    /// the VM MIPS shares, the Host cores and the VM [CloudletScheduler][org.cloudsimplus.schedulers.cloudlet.CloudletScheduler].
    ///
    /// @param vm the VM to change the number of cores
    /// @param delta the number of cores to add (if positive) or remove (if negative)
    /// @return true if the number of cores was changed;
    ///         false if it was not possible (such as when the Host doesn't have room for more cores
    ///         or the VM doesn't have the number of cores to remove), so nothing is changed
    boolean resizeVmCores(Vm vm, long delta);

    /// Gets the MIPS share of each Host's core that is allocated to a given VM.
    ///
    /// @param vm the vm to get the MIPS share
    /// @return the VM allocated MIPS share
    CoreCapacity getAllocatedMips(Vm vm);

    /// {@return the total amount of MIPS that is currently free}
    /// If there are VMs migrating into the Host,
    /// their requested MIPS will already be allocated,
    /// reducing the total available MIPS.
    double getTotalAvailableMips();

    /// @return the total MIPS allocated to VMs at the Host,
    /// which is the [Host MIPS capacity][org.cloudsimplus.hosts.Host#getTotalMipsCapacity()]
    /// minus the [total available MIPS][#getTotalAvailableMips()].
    double getTotalAllocatedMips();

    /// Gets the MIPS share requested by a VM
    /// @param vm the VM to get the List of requested MIPS
    /// @return VM requested MIPS share
    CoreCapacity getRequestedMips(Vm vm);

    /// Checks if the PM using this scheduler has enough MIPS capacity
    /// to host a given VM.
    ///
    /// @param vm the VM to check if there is enough available resource capacity on the PM to host it
    /// @return true, if it is possible to allocate the VM into the host; false otherwise
    /// @see #isSuitableForVm(Vm)
    boolean isSuitableForVm(Vm vm);

    /// Checks if the MIPS share requested by a VM can be allocated or not.
    /// Depending on the `VmScheduler` implementation, the return value
    /// of this method may have different effects:
    ///
    /// - true: requested MIPS can be allocated, partial or totally;
    /// - false: requested MIPS cannot be allocated because there is no availability at all,
    ///   or there is just a partial amount of the requested MIPS available and the
    ///   `VmScheduler` implementation doesn't allow allocating less than the
    ///   VM is requesting. If less than the required MIPS is allocated to a VM,
    ///   it will cause performance degradation.
    ///   Such a situation defines an over-subscription situation
    ///   which just specific `VmSchedulers` accept.
    ///
    /// @param vm the [Vm] to check if there are enough MIPS to allocate to
    /// @param requestedMips the MIPS share requested by a VM
    /// @return true if the requested MIPS share is allowed to be allocated to the VM, false otherwise
    boolean isSuitableForVm(Vm vm, CoreCapacity requestedMips);

    /// {@return the actual total allocated MIPS for a VM along all its allocated cores}
    /// If the VM is migrating into the Host, then just a fraction
    /// of the requested MIPS is actually allocated, representing
    /// the overhead of the migration process.
    ///
    /// The MIPS requested by the VM are just actually allocated
    /// after the migration is completed.
    ///
    /// @param vm the VM to get the total allocated MIPS
    /// @see #getVmMigrationCpuOverhead()
    double getTotalAllocatedMipsForVm(Vm vm);

    /// Gets the max percentage of CPU a VM migrating out of this Host can use.
    /// Since there may be an overhead associated with the migration process
    /// (if the [CPU overhead for VM migration][#getVmMigrationCpuOverhead()] is greater than 0),
    /// during the migration, the amount of MIPS the VM can use is reduced due to this overhead.
    ///
    /// @return the max percentage of CPU usage during migration (in scale from [0 to 1], where 1 is 100%)
    double getMaxCpuUsagePercentDuringOutMigration();

    /// Defines the percentage of Host's CPU usage increase when a
    /// VM is migrating in or out the Host.
    ///
    /// @return the Host's CPU migration overhead percentage, in scale from 0 to 1 (where 1 is 100%)
    double getVmMigrationCpuOverhead();

    /// @return the Host that the VmScheduler get the list of cores to allocate to VMs.
    Host getHost();

    /// Sets the Host that the VmScheduler get the list of cores to allocate to VMs.
    /// A Host for the VmScheduler is defined when the VmScheduler is set to a given Host.
    /// Thus, the Host is in charge to set itself to a VmScheduler.
    /// @param host the Host to be set
    /// @return this scheduler
    /// @throws IllegalArgumentException when the scheduler already is assigned to another Host, since
    /// each Host must have its own scheduler
    /// @throws NullPointerException when the host parameter is null
    VmScheduler setHost(Host host);
}
