/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.vm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.resources.Resource;

/**
 * An interface that represents the policy used by a
 * Virtual Machine Monitor (VMM) to share processing power of a PM among VMs
 * running in a host.  Each host has to use is own instance of a
 * VmScheduler that will so schedule the allocation of host's PEs for
 * VMs running on it.
 *
 * <p>It also implements the Null Object Design
 * Pattern in order to start avoiding {@link NullPointerException}
 * when using the {@link VmScheduler#NULL} object instead
 * of attributing {@code null} to {@link VmScheduler} variables.</p>
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface VmScheduler {

    /**
     * An attribute that implements the Null Object Design Pattern for {@link VmScheduler}
     * objects.
     */
    VmScheduler NULL = new VmSchedulerNull();

    /**
     * Requests the allocation of PEs for a VM.
     *
     * @param vm the vm to allocate PEs to
     * @param mipsShareRequested the list of MIPS share to be allocated to a VM
     * @return true if the PEs were allocated to the VM, false otherwise
     *
     * @pre $none
     * @post $none
     *
     * @todo @author manoelcampos All implementing classes don't consider the
     * situation when a Vm already has allocated MIPS and the method is
     * called again. In this case, what is supposed to do? Increase the current
     * allocation or change it? I think that the obvious action is to change the
     * allocation, however, the implementations aren't working to deal this
     * situation. For that, they have to use some method such as
     * {@link Resource#isResourceAmountAvailable(long)}
     * to first check if the difference from the current allocated mips and the
     * requested one is available. Currently the implementations wrongly check
     * if the total requested mips is available, while only the difference has
     * to be checked. It has to be added some tests to check this issue.
     */
    boolean allocatePesForVm(Vm vm, List<Double> mipsShareRequested);

    /**
     * Requests the allocation of PEs for a VM, according
     * to the number of PEs and MIPS defined by VM attributes.
     *
     * @param vm the vm to allocate PEs to
     * @return true if the PEs were allocated to the VM, false otherwise
     *
     * @pre $none
     * @post $none
     */
    boolean allocatePesForVm(Vm vm);

    /**
     * Releases PEs allocated to all the VMs of the host the VmScheduler
     * is associated to. After that, all PEs will be available to be used on
     * demand for requesting VMs.
     *
     * @pre $none
     * @post $none
     */
    void deallocatePesForAllVms();

    /**
     * Releases all PEs allocated to a VM. After that, the PEs may be used on demand
     * by other VMs.
     *
     * @param vm the vm to deallocate PEs from
     * @pre $none
     * @post $none
     */
    void deallocatePesFromVm(Vm vm);

    /**
     * Releases a given number of PEs from a VM. After that, the PEs may be used on demand
     * by other VMs.
     *
     * @param vm the vm to deallocate PEs from
     * @param pesToRemove number of PEs to deallocate
     * @pre $none
     * @post $none
     */
    void deallocatePesFromVm(Vm vm, int pesToRemove);

    /**
     * Gets the MIPS share of each host's Pe that is allocated to a given VM.
     *
     * @param vm the vm to get the MIPS share
     * @return
     * @pre $none
     * @post $none
     */
    List<Double> getAllocatedMipsForVm(Vm vm);

    /**
     * Gets the amount of MIPS that is free.
     *
     * @return
     */
    double getAvailableMips();

    /**
     * Checks if the PM using this scheduler has enough MIPS capacity
     * to host a given VM.
     *
     * @param vm the vm to check if there is enough available resource on the PM to host it
     *
     * @return true, if it is possible to allocate the the VM into the host; false otherwise
     */
    boolean isSuitableForVm(Vm vm);

    /**
     * Checks if the PM using this scheduler has enough MIPS capacity
     * to host a given VM.
     *
     * @param vmMipsList a List with the MIPS capacity required by each VM PE
     *
     * @return true, if it is possible to allocate the the VM into the host; false otherwise
     */
    boolean isSuitableForVm(List<Double> vmMipsList);

    /**
     * Gets the maximum available MIPS among all the host's PEs.
     *
     * @return
     */
    double getMaxAvailableMips();

    /**
     * Gets PE capacity in MIPS.
     *
     * @return
     * @todo It considers that all PEs have the same capacity, what has been
     * shown doesn't be assured. The peList received by the VmScheduler can be
     * heterogeneous PEs.
     */
    long getPeCapacity();

    /**
     * Gets the list of working PEs from the Host, <b>which excludes failed PEs</b>.
     *
     * @param <T> the generic type
     * @return
     *
     */
    <T extends Pe> List<T> getWorkingPeList();

    /**
     * Gets the list of PEs allocated for a VM.
     *
     * @param vm the VM to get the allocated PEs
     * @return
     */
    List<Pe> getPesAllocatedForVM(Vm vm);

    /**
     * Gets the total allocated MIPS for a VM along all its allocated PEs.
     *
     * @param vm the VM to get the total allocated MIPS
     * @return
     */
    double getTotalAllocatedMipsForVm(Vm vm);

    /**
     * Defines the percentage of Host's CPU usage increase when a
     * VM is migrating in or out of the Host.
     * The value is in scale from 0 to 1 (where 1 is 100%).
     *
     * @return the Host's CPU migration overhead percentage.
     */
    double getVmMigrationCpuOverhead();

    /**
     * Gets the host that the VmScheduler get the list of PEs to allocate to VMs.
     * @return
     */
    Host getHost();

    /**
     * Sets the host that the VmScheduler get the list of PEs to allocate to VMs.
     * A host for the VmScheduler is set when the VmScheduler is set to a given host.
     * Thus, the host is in charge to set itself to a VmScheduler.
     * @param host the host to be set
     * @return
     * @throws IllegalArgumentException when the scheduler already is assigned to another Host, since
     * each Host must have its own scheduler
     * @throws NullPointerException when the host parameter is null
     */
    VmScheduler setHost(Host host);
}
