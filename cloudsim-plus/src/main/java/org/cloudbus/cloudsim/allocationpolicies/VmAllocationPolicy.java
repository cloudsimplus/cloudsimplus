/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.allocationpolicies;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.autoscaling.VerticalVmScaling;

/**
 * An interface to be implemented by each class that represents a policy used by
 * a {@link Datacenter} to choose a {@link Host} to place or migrate a
 * given {@link Vm}.
 *
 * <p>The VmAllocationPolicy uses Java 8 Functional Programming
 * to enable changing, at runtime, the policy used
 * to select a Host for a given VM.</p>
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 * @see #setFindHostForVmFunction(BiFunction)
 */
public interface VmAllocationPolicy {
    /**
     * A property that implements the Null Object Design Pattern for {@link VmAllocationPolicy}
     * objects.
     */
    VmAllocationPolicy NULL = new VmAllocationPolicyNull();

    /**
     * Gets the {@link Datacenter} associated to the Allocation Policy.
     * @return
     */
    Datacenter getDatacenter();

    /**
     * Sets the Datacenter associated to the Allocation Policy
     * @param datacenter the Datacenter to set
     */
    void setDatacenter(Datacenter datacenter);

    /**
     * Allocates a host for a given VM.
     *
     * @param vm the VM to allocate a host to
     * @return $true if the host could be allocated; $false otherwise
     * @pre $none
     * @post $none
     */
    boolean allocateHostForVm(Vm vm);

    /**
     * Allocates a specified host for a given VM.
     *
     * @param vm the VM to allocate a host to
     * @param host the host to allocate to the given VM
     * @return $true if the host could be allocated; $false otherwise
     * @pre $none
     * @post $none
     */
    boolean allocateHostForVm(Vm vm, Host host);

    /**
     * Try to scale some Vm's resource vertically up or down, respectively if:
     * <ul>
     *     <li>the Vm is overloaded and the Host where the Vm is placed has enough capacity</li>
     *     <li>the Vm is underloaded</li>
     * </ul>
     *
     * The resource to be scaled is defined by the given {@link VerticalVmScaling} object.
     *
     * @param scaling the {@link VerticalVmScaling} object with information of which resource
     *                is being requested to be scaled
     * @return true if the requested resource was scaled, false otherwise
     */
    boolean scaleVmVertically(VerticalVmScaling scaling);

    /**
     * Releases the host used by a VM.
     *
     * @param vm the vm to get its host released
     * @pre $none
     * @post $none
     */
    void deallocateHostForVm(Vm vm);

    /**
     * Finds a host that has enough resources to place a given VM.
     * <b>Classes must implement this method to define how to select a Host for a given VM.</b>
     * They just have to provide a default implementation. However, this implementation can be dynamically
     * changed by calling {@link #setFindHostForVmFunction(BiFunction)}.
     *
     * @param vm the vm to find a host for it
     * @return an {@link Optional} containing a suitable Host to place the VM or an empty {@link Optional} if no suitable Host was found
     */
    Optional<Host> findHostForVm(Vm vm);

    /**
     * Gets the list of Hosts available in a {@link Datacenter}, that will be
     * used by the Allocation Policy to place VMs.
     *
     * @param <T> The generic type
     * @return the host list
     */
     <T extends Host> List<T> getHostList();

    /**
     * Gets a map of optimized allocation for VMs according to current utilization
     * and Hosts under and overloaded conditions.
     * The conditions that will make a new VM placement map to be proposed
     * and returned is defined by each implementing class.
     *
     * @param vmList the list of VMs to be reallocated
     * @return the new vm placement map, where each key is a VM and each value is the host where such a Vm has to be placed
     *
     */
    Map<Vm, Host> getOptimizedAllocationMap(List<? extends Vm> vmList);

    /**
     * Sets a {@link BiFunction} that selects a Host for a given Vm.
     * This Function receives the current VmAllocationPolicy and the
     * {@link Vm} requesting to be place.
     * It then returns an {@link Optional<Host>}
     * that may contain a suitable Host for that Vm or not.
     *
     * <p>If not Function is set, the default VM selection method provided by implementing classes
     * will be used.</p>
     *
     * @param findHostForVmFunction the {@link BiFunction} to set
     */
    void setFindHostForVmFunction(BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction);
}
