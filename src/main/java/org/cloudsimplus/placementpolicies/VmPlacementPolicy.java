/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.placementpolicies;

import org.cloudsimplus.placementpolicies.migration.VmPlacementPolicyMigration;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSuitability;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import org.jspecify.annotations.Nullable;

/// An interface to be implemented by each class that represents a policy, used by
/// a [Datacenter], to choose a [Host] to place or migrate a
/// given [Vm] or [VmGroup].
///
/// The policy makes the **placement** decision (choosing the Host).
/// The selected Host then **allocates** (reserves) part of its resources for the VM
/// and **provisions** them to it.
/// See [org.cloudsimplus.allocators.ResourceAllocator] for the meaning of these terms.
///
/// The VmPlacementPolicy uses Java 8+ Functional Programming
/// to enable changing, at runtime, the policy used
/// to select a Host for a given [Vm] or [VmGroup].
///
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.0
/// @see #setFindHostForVmFunction(BiFunction)
public sealed interface VmPlacementPolicy
    permits VmPlacementPolicyAbstract, VmPlacementPolicyMigration, VmPlacementPolicyNull
{
    Logger LOGGER = LoggerFactory.getLogger(VmPlacementPolicy.class.getSimpleName());

    /// Default minimum number of Hosts to start using parallel search.
    /// @see #setHostCountForParallelSearch(int)
    int DEF_HOST_COUNT_PARALLEL_SEARCH = 20_000;

    /// A property that implements the Null Object Design Pattern for [VmPlacementPolicy]
    /// objects.
    VmPlacementPolicy NULL = new VmPlacementPolicyNull();

    /// {@return the Datacenter associated to the VmPlacementPolicy}
    Datacenter getDatacenter();

    /// Sets the Datacenter associated to the VmPlacementPolicy
    /// @param datacenter the Datacenter to set
    /// @return this VmPlacementPolicy
    VmPlacementPolicy setDatacenter(Datacenter datacenter);

    /// Tries to place a given [Vm] or [VmGroup] on a Host.
    ///
    /// @param vm the [Vm] or [VmGroup] to place on a Host
    /// @return a [HostSuitability] to indicate if the Vm was placed into the host or not
    /// (if the Host doesn't have enough resources to allocate for the Vm)
    /// @see VmGroup
    HostSuitability placeVm(Vm vm);

    /// Tries to place each [Vm] or [VmGroup] into a given List on a Host.
    /// The method should define how it will find a [suitable Host][Host#getSuitabilityFor(Vm)]
    /// for each Vm and then call [#placeVm(Vm,Host)] to actually
    /// create the VM inside the selected Host.
    ///
    /// @param vmList the List of [Vm]s or [VmGroup]s to place on a Host
    /// @return a Set of [HostSuitability] to indicate if a Vm was placed into the host or not,
    /// containing a HostSuitability object for the last Host where each VM was tried to be created;
    /// or an empty set if the Datacenter has no hosts.
    ///
    /// @see VmGroup
    Set<HostSuitability> placeVm(List<Vm> vmList);

    /// Tries to place a given [Vm] or [VmGroup] on a specified Host.
    ///
    /// @param vm the [Vm] or [VmGroup] to place on a Host
    /// @param host the Host to place the given [Vm] or [VmGroup] on
    /// @return a [HostSuitability] to indicate if the Vm was placed into the host or not
    /// (if the Host doesn't have enough resources to allocate for the Vm)
    /// @see VmGroup
    HostSuitability placeVm(Vm vm, Host host);

    /// Try to scale some Vm's resource vertically up or down, respectively if:
    ///
    /// - the Vm is overloaded and the Host where the Vm is placed has enough capacity
    /// - the Vm is underloaded
    ///
    /// The resource to be scaled is defined by the given [VerticalVmScaling] object.
    ///
    /// @param scaling the [VerticalVmScaling] object with information of which resource
    /// is being requested to be scaled
    /// @return true if the requested resource was scaled, false otherwise
    boolean scaleVmVertically(VerticalVmScaling scaling);

    /// Removes a VM from its Host, releasing the Host resources allocated for the VM.
    ///
    /// @param vm the vm to get its host released
    void removeVm(Vm vm);

    /// Sets a [BiFunction] that selects a Host for a given Vm.
    /// This Function receives the current VmPlacementPolicy and the
    /// [Vm] requesting to be placed.
    /// It then returns an [Optional]`<Host>`
    /// that may contain a suitable Host for that Vm or not.
    ///
    /// If no Function is set, the default VM selection method provided by implementing classes
    /// will be used instead.
    ///
    /// @param findHostForVmFunction the [BiFunction] to set
    VmPlacementPolicy setFindHostForVmFunction(@Nullable BiFunction<VmPlacementPolicy, Vm, Optional<Host>> findHostForVmFunction);

    /// Gets the list of Hosts available in the [Datacenter] linked to this VmPlacementPolicy,
    /// which will be used to place VMs.
    ///
    /// @param <T> the host type
    /// @return datacenter the host list
     <T extends Host> List<T> getHostList();

    /// Gets a map of optimized allocation for VMs, according to current utilization
    /// and Hosts under and overloaded conditions.
    /// The conditions that will make a new VM placement map to be proposed
    /// and returned is defined by each implementing class.
    ///
    /// @param vmList the list of VMs to be reallocated
    /// @return the new vm placement map, where each key is a VM and each value
    /// is the host where such a Vm has to be placed
    Map<Vm, Host> getOptimizedPlacementMap(List<? extends Vm> vmList);

    /// Finds a suitable Host that has enough resources to place a given VM.
    /// Internally it may use a default implementation or one [set in runtime][#setFindHostForVmFunction(BiFunction)].
    ///
    /// @param vm the vm to find a host for it
    /// @return an [Optional] containing a suitable Host to place the VM;
    /// or an empty [Optional] if no suitable Host was found
    Optional<Host> findHostForVm(Vm vm);

    /// Checks if VM migrations are supported by this VmPlacementPolicy.
    /// Realize that even if the policy allows VM migration,
    /// such operations can be dynamically enabled/disabled by the Datacenter.
    ///
    /// @return true if VM migration is supported; false otherwise.
    /// @see Datacenter#enableMigrations()
    /// @see Datacenter#disableMigrations()
    boolean isVmMigrationSupported();

    /// Checks if Host's parallel search is enabled or not.
    /// @return true if host search for VM placement must be performed in parallel;
    /// false if it's to be performed sequentially
    /// @see #setHostCountForParallelSearch(int)
    default boolean isParallelHostSearchEnabled(){
        return getHostList().size() >= getHostCountForParallelSearch();
    }

    /// {@return the minimum number of Hosts to start using parallel search}
    /// @see #isParallelHostSearchEnabled()
    int getHostCountForParallelSearch();

    /// Sets the minimum number of Hosts to start using parallel search.
    /// @param hostCountForParallelSearch the value to set (use [Integer#MAX_VALUE] to disable parallel search)
    /// @return this VmPlacementPolicy
    VmPlacementPolicy setHostCountForParallelSearch(int hostCountForParallelSearch);
}
