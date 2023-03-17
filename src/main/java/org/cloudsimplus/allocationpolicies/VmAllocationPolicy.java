/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.allocationpolicies;

import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSuitability;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * An interface to be implemented by each class that represents a policy used by
 * a {@link Datacenter} to choose a {@link Host} to place or migrate a
 * given {@link Vm} or {@link VmGroup}.
 *
 * <p>The VmAllocationPolicy uses Java 8+ Functional Programming
 * to enable changing, at runtime, the policy used
 * to select a Host for a given {@link Vm} or {@link VmGroup}.</p>
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 * @see #setFindHostForVmFunction(BiFunction)
 */
public interface VmAllocationPolicy {
    Logger LOGGER = LoggerFactory.getLogger(VmAllocationPolicy.class.getSimpleName());

    /**
     * Default minimum number of Hosts to start using parallel search.
     * @see #setHostCountForParallelSearch(int)
     */
    int DEF_HOST_COUNT_PARALLEL_SEARCH = 20_000;

    /**
     * A property that implements the Null Object Design Pattern for {@link VmAllocationPolicy}
     * objects.
     */
    VmAllocationPolicy NULL = new VmAllocationPolicyNull();

    /**
     * Gets the {@link Datacenter} associated to the VmAllocationPolicy.
     * @return
     */
    Datacenter getDatacenter();

    /**
     * Sets the Datacenter associated to the VmAllocationPolicy
     * @param datacenter the Datacenter to set
     */
    VmAllocationPolicy setDatacenter(Datacenter datacenter);

    /**
     * Tries to allocate a host for a given {@link Vm} or {@link VmGroup}.
     *
     * @param vm the {@link Vm} or {@link VmGroup} to allocate a host to
     * @return a {@link HostSuitability} to indicate if the Vm was placed into the host or not
     * (if the Host doesn't have enough resources to allocate the Vm)
     * @see VmGroup
     */
    HostSuitability allocateHostForVm(Vm vm);

    /**
     * Tries to allocate a specified host for a given {@link Vm} or {@link VmGroup}.
     *
     * @param vm the {@link Vm} or {@link VmGroup} to allocate a host to
     * @param host the host to allocate to the given {@link Vm} or {@link VmGroup}
     * @return a {@link HostSuitability} to indicate if the Vm was placed into the host or not
     * (if the Host doesn't have enough resources to allocate the Vm)
     * @see VmGroup
     */
    HostSuitability allocateHostForVm(Vm vm, Host host);

    /**
     * Tries to allocate hosts for a collection of {@link Vm}s or {@link VmGroup}s.
     *
     * @param vmCollection the {@link Vm} or {@link VmGroup} collection to allocate hosts to
     * @return an empty list if hosts were allocated to all VMs
     *         or a list of Vms that a suitable host couldn't be found.
     * @see VmGroup
     */
    <T extends Vm> List<T> allocateHostForVm(Collection<T> vmCollection);

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
     */
    void deallocateHostForVm(Vm vm);

    /**
     * Sets a {@link BiFunction} that selects a Host for a given Vm.
     * This Function receives the current VmAllocationPolicy and the
     * {@link Vm} requesting to be place.
     * It then returns an {@code Optional<Host>}
     * that may contain a suitable Host for that Vm or not.
     *
     * <p>If not Function is set, the default VM selection method provided by implementing classes
     * will be used.</p>
     *
     * @param findHostForVmFunction the {@link BiFunction} to set
     */
    VmAllocationPolicy setFindHostForVmFunction(BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction);

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
     * @return the new vm placement map, where each key is a VM and each value
     *         is the host where such a Vm has to be placed
     *
     */
    Map<Vm, Host> getOptimizedAllocationMap(List<? extends Vm> vmList);

    /**
     * Finds a suitable host that has enough resources to place a given VM.
     * Internally it may use a default implementation or one set in runtime.
     *
     * @param vm the vm to find a host for it
     * @return an {@link Optional} containing a suitable Host to place the VM;
     *         or an empty {@link Optional} if no suitable Host was found
     * @see #setFindHostForVmFunction(BiFunction)
     */
    Optional<Host> findHostForVm(Vm vm);

    /**
     * Checks if VM migrations are supported by this VmAllocationPolicy.
     * Realize that even if the policy allows VM migration,
     * such operations can be dynamically enabled/disabled by the Datacenter.
     *
     * @return
     * @see Datacenter#enableMigrations()
     * @see Datacenter#disableMigrations()
     */
    boolean isVmMigrationSupported();

    /**
     * Checks if Host's parallel search is enabled or not.
     * @return true if a Host for a VM is to find in parallel;
     *         false if it's to be found sequentially
     * @see #setHostCountForParallelSearch(int)
     */
    default boolean isParallelHostSearchEnabled(){
        return getHostList().size() >= getHostCountForParallelSearch();
    }

    /**
     * Gets the minimum number of Hosts to start using parallel search.
     * @return
     */
    int getHostCountForParallelSearch();

    /**
     * Sets the minimum number of Hosts to start using parallel search.
     * @param hostCountForParallelSearch the value to set (use {@link Integer#MAX_VALUE} to disable parallel search)
     */
    VmAllocationPolicy setHostCountForParallelSearch(int hostCountForParallelSearch);
}
