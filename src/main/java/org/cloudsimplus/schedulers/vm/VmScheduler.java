/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.schedulers.vm;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.Resource;
import org.cloudsimplus.schedulers.MipsShare;
import org.cloudsimplus.vms.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An interface that represents the policy used by a
 * Virtual Machine Monitor (VMM) to share the processing power of a {@link Host} between {@link Vm}s.
 * Each Host has to use its own instance of a
 * VmScheduler that will so schedule the allocation of host's {@link Pe}s for
 * VMs running on it.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public sealed interface VmScheduler permits VmSchedulerAbstract, VmSchedulerNull {
    Logger LOGGER = LoggerFactory.getLogger(VmScheduler.class.getSimpleName());

    /**
     * The default percentage (in scale from [0..1]) to define the CPU overhead of VM migration
     * if one is not explicitly set.
     * @see #getVmMigrationCpuOverhead()
     */
    double DEF_VM_MIGRATION_CPU_OVERHEAD = 0.1;

    /**
     * An attribute that implements the Null Object Design Pattern for {@link VmScheduler} objects.
     */
    VmScheduler NULL = new VmSchedulerNull();

    /**
     * Requests the allocation of PEs for a VM.
     *
     * @param vm the VM to allocate PEs to
     * @param requestedMips the MIPS share to be allocated to a VM
     * @return true if the PEs were allocated to the VM, false otherwise
     *
     * @TODO All implementing classes don't consider the
     *       situation when a Vm already has allocated MIPS and the method is
     *       called again. In this case, what is supposed to do? Increase the current
     *       allocation or change it? I think that the obvious action is to change the
     *       allocation, however, the implementations aren't working to deal with this
     *       situation. For that, they have to use some method such as
     *       {@link Resource#isAmountAvailable(long)}
     *       to first check if the difference from the current allocated MIPS and the
     *       requested one is available. Currently the implementations wrongly check
     *       if the total requested MIPS is available, while only the difference has
     *       to be checked. It has to be added some tests to check this issue.
     */
    boolean allocatePesForVm(Vm vm, MipsShare requestedMips);

    /**
     * Requests the allocation of PEs for a VM, according
     * to the number of PEs and MIPS defined by VM attributes.
     *
     * @param vm the VM to allocate PEs to
     * @return true if the PEs were allocated to the VM, false otherwise
     */
    boolean allocatePesForVm(Vm vm);

    /**
     * Releases all PEs allocated to a VM. After that, the PEs may be used on demand
     * by other VMs.
     *
     * @param vm the VM to deallocate PEs from
     */
    void deallocatePesFromVm(Vm vm);

    /**
     * Releases a given number of PEs from a VM. After that, the PEs may be used on demand
     * by other VMs.
     *
     * @param vm the VM to deallocate PEs from
     * @param pesToRemove number of PEs to deallocate
     */
    void deallocatePesFromVm(Vm vm, int pesToRemove);

    /**
     * Gets the MIPS share of each Host's Pe that is allocated to a given VM.
     *
     * @param vm the vm to get the MIPS share
     * @return the VM allocated MIPS share
     */
    MipsShare getAllocatedMips(Vm vm);

    /**
     * {@return the total amount of MIPS that is currently free}
     * If there are VMs migrating into the Host,
     * their requested MIPS will already be allocated,
     * reducing the total available MIPS.
     */
    double getTotalAvailableMips();

    /**
     * Gets the MIPS share requested by a VM
     * @param vm the VM to get the List of requested MIPS
     * @return VM requested MIPS share
     */
    MipsShare getRequestedMips(Vm vm);

    /**
     * Checks if the PM using this scheduler has enough MIPS capacity
     * to host a given VM.
     *
     * @param vm the VM to check if there is enough available resource capacity on the PM to host it
     * @return true, if it is possible to allocate the VM into the host; false otherwise
     * @see #isSuitableForVm(Vm)
     */
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
    boolean isSuitableForVm(Vm vm, MipsShare requestedMips);

    /**
     * {@return the actual total allocated MIPS for a VM along all its allocated PEs}
     * If the VM is migrating into the Host, then just a fraction
     * of the requested MIPS is actually allocated, representing
     * the overhead of the migration process.
     *
     * <p>The MIPS requested by the VM are just actually allocated
     * after the migration is completed.</p>
     *
     * @param vm the VM to get the total allocated MIPS
     * @see #getVmMigrationCpuOverhead()
     */
    double getTotalAllocatedMipsForVm(Vm vm);

    /// Gets the max percentage of CPU a VM migrating out of this Host can use.
    /// Since there may be an overhead associated with the migration process
    /// (if the [CPU overhead for VM migration][#getVmMigrationCpuOverhead()] is greater than 0),
    /// during the migration, the amount of MIPS the VM can use is reduced due to this overhead.
    ///
    /// @return the max percentage of CPU usage during migration (in scale from [0 to 1], where 1 is 100%)
    double getMaxCpuUsagePercentDuringOutMigration();

    /**
     * Defines the percentage of Host's CPU usage increase when a
     * VM is migrating in or out the Host.
     *
     * @return the Host's CPU migration overhead percentage, in scale from 0 to 1 (where 1 is 100%)
     */
    double getVmMigrationCpuOverhead();

    /**
     * @return the Host that the VmScheduler get the list of PEs to allocate to VMs.
     */
    Host getHost();

    /**
     * Sets the Host that the VmScheduler get the list of PEs to allocate to VMs.
     * A Host for the VmScheduler is defined when the VmScheduler is set to a given Host.
     * Thus, the Host is in charge to set itself to a VmScheduler.
     * @param host the Host to be set
     * @return this scheduler
     * @throws IllegalArgumentException when the scheduler already is assigned to another Host, since
     * each Host must have its own scheduler
     * @throws NullPointerException when the host parameter is null
     */
    VmScheduler setHost(Host host);
}
