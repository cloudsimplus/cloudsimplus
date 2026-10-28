/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.hosts;

import org.cloudsimplus.core.*;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostEventInfo;
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;
import org.cloudsimplus.power.PowerAware;
import org.cloudsimplus.power.models.PowerModelHost;
import org.cloudsimplus.allocators.ResourceAllocator;
import org.cloudsimplus.resources.Bandwidth;
import org.cloudsimplus.resources.Ram;
import org.cloudsimplus.resources.ResourceManageable;
import org.cloudsimplus.schedulers.vm.VmScheduler;
import org.cloudsimplus.vms.HostResourceStats;
import org.cloudsimplus.vms.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/// An interface to be implemented by each class that provides
/// Physical Machines (Hosts) features.
///
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.0
public sealed interface Host
    extends PhysicalMachine, Comparable<Host>, PowerAware<PowerModelHost>, ResourceStatsComputer<HostResourceStats>
    permits HostAbstract, HostNull
{
    Logger LOGGER = LoggerFactory.getLogger(Host.class.getSimpleName());

    /// The default value for the [#getIdleShutdownDeadline()].
    /// This value indicates that the Host won't be shutdown when becoming idle.
    double DEF_IDLE_SHUTDOWN_DEADLINE = -1;

    /// An attribute that implements the Null Object Design Pattern for [Host] objects.
    Host NULL = new HostNull();

    /// @return the [Datacenter] where the host is placed.
    Datacenter getDatacenter();

    /// Checks if the Host is suitable for a Vm
    /// (if it has enough resources to meet the Vm requirements)
    /// and the Host has not failed.
    ///
    /// @param vm the Vm to check
    /// @return true if is suitable for Vm, false otherwise
    /// @see #getSuitabilityFor(Vm)
    boolean isSuitableForVm(Vm vm);

    /// Checks if the host is suitable for a Vm
    /// (if it has enough resources to meet the Vm requirements)
    /// and the Host has not failed,
    /// **providing fine-grained information
    /// about the suitability of each Host resource**.
    ///
    /// @param vm the Vm to check
    /// @return a [HostSuitability] object
    /// indicating the Host's resources that
    /// are suitable or not for the given Vm.
    /// @see #setLazySuitabilityEvaluation(boolean)
    HostSuitability getSuitabilityFor(Vm vm);

    /// @return true if the Host is powered-on, false otherwise.
    /// @see #isFinished()
    boolean isActive();

    /// @return true if the host is powered-off, false otherwise
    /// @see #isActive()
    @Override
    boolean isFinished();

    /// @return true if the Host has ever started sometime,
    /// i.e., if it was active sometime in the simulation execution;
    /// false otherwise.
    boolean hasEverStarted();

    /// Requests the Host to be powered on or off.
    /// If there is no [#getStartupDelay()]
    /// or [#getShutDownDelay()] (which is the default),
    /// those operations will happen immediately.
    ///
    /// If the Host is set to be powered off while it has VMs running,
    /// it is simulated a scheduled shutdown, so that those VMs will finish,
    /// but new ones won't be submitted to this Host.
    ///
    /// @param activate true to power on, false to power off
    /// @return this Host instance
    /// @throws IllegalStateException when trying to activate a [failed][#isFailed()] host.
    /// @see #setPowerModel(PowerModelHost)
    /// @see #shutdown()
    Host setActive(boolean activate);

    /// Sends a request to shut down the Host.
    @Override
    void shutdown();

    /// Gets the list of VMs migrating into this host.
    ///
    /// @param <T> the generic type
    /// @return the VMs migrating in
    <T extends Vm> Set<T> getVmsMigratingIn();

    /// @return true if there is any VM migrating in or out this host; false otherwise.
    boolean hasMigratingVms();

    /// @return a **read-only** list of VMs migrating out from the Host.
    Set<Vm> getVmsMigratingOut();

    /// @return the bandwidth (BW) allocator with capacity in Megabits/s,
    /// as a read-only [ResourceAllocator]: only the Host changes allocations,
    /// when it [provisions][#provisionResourceForVm(Vm, Class, long)] resources to VMs.
    ResourceAllocator getBwAllocator();

    /// Sets the bandwidth (BW) allocator with capacity in Megabits/s.
    ///
    /// @param bwAllocator the new bw allocator
    Host setBwAllocator(ResourceAllocator bwAllocator);

    /// @return the ram allocator with capacity in Megabytes,
    /// as a read-only [ResourceAllocator]: only the Host changes allocations,
    /// when it [provisions][#provisionResourceForVm(Vm, Class, long)] resources to VMs.
    ResourceAllocator getRamAllocator();

    /// Sets the ram allocator with capacity in Megabytes.
    ///
    /// @param ramAllocator the new ram allocator
    Host setRamAllocator(ResourceAllocator ramAllocator);

    /// Gets a list of VMs currently assigned to the Host.
    ///
    /// @param <T> The generic type
    /// @return the read-only current VM list
    <T extends Vm> List<T> getVmList();

    /// Gets a **read-only** list of all VMs that have been created into the Host
    /// during the entire simulation.
    /// This way, this method returns a historic list of created VMs,
    /// including those already destroyed.
    ///
    /// @param <T> The Vm generic type
    /// @return the read-only created VM historic list
    <T extends Vm> List<T> getVmCreatedList();

    /// @return the policy for allocation of host cores to VMs to schedule VM execution.
    VmScheduler getVmScheduler();

    /// Sets the policy for allocation of host cores to VMs to schedule VM
    /// execution. The host also sets itself to the given scheduler.
    /// It also sets the Host itself to the given scheduler.
    ///
    /// @param vmScheduler the vm scheduler to set
    /// @return this Host
    Host setVmScheduler(VmScheduler vmScheduler);

    /// @return the first Host startup time (in seconds) or -1 if the Host has never been powered on.
    /// @see #getStartTime()
    /// @see #setActive(boolean)
    double getFirstStartTime();

    /// @return the last time the Host was shut down (in seconds) or -1 if the Host is active.
    @Override
    double getFinishTime();

    /// Sets the Host shut down time.
    /// @param shutdownTime the time to set (in seconds)
    /// @see #getFinishTime()
    @Override
    Startable setFinishTime(double shutdownTime);

    /// @return the elapsed time (in seconds) since the last power on.
    /// Use [org.cloudsimplus.util.TimeUtil#secondsToHours(double)] to get it in hours.
    /// @see #getTotalUpTime()
    double getUpTime();

    /// {@return the total time (in seconds) the Host stayed active (powered on)}
    /// Since the Host can be powered on and off according to the demand,
    /// this method returns the sum of all intervals the Host was active.
    ///
    /// Use [org.cloudsimplus.util.TimeUtil#secondsToHours(double)] to get it in hours.
    ///
    /// @see #setActive(boolean)
    /// @see #setIdleShutdownDeadline(double)
    /// @see #getUpTime()
    double getTotalUpTime();

    /// {@return the deadline to shut down the Host (in seconds) when it becomes idle}
    /// This is the time interval after the Host becomes idle that it will be shut down.
    /// @see #DEF_IDLE_SHUTDOWN_DEADLINE
    double getIdleShutdownDeadline();

    /// Sets the deadline to shut down the Host (in seconds) when it becomes idle.
    /// This is the time interval after the Host becomes idle that it will be shut down.
    ///
    /// @param deadline the deadline to shut down the Host after it becomes idle (in seconds).
    /// A negative value disables idle host shutdown.
    /// @see #DEF_IDLE_SHUTDOWN_DEADLINE
    /// @see #getIdleShutdownDeadline()
    Host setIdleShutdownDeadline(double deadline);

    /// Sets the Host ID.
    /// It returns a Host, so that other Host setters can be chained.
    ///
    /// @param id the ID to set
    /// @return this Host
    @Override
    Host setId(long id);

    /// Checks if the host has failed or is working properly.
    ///
    /// @return true, if the host cores have failed; false otherwise
    boolean isFailed();

    /// Sets the Host state to "failed" or "working".
    ///
    /// @param failed true to set the Host to "failed", false to set to "working"
    /// @return true if the Host status was changed, false otherwise
    boolean setFailed(boolean failed);

    /// Try to allocate resources to a new VM in the Host.
    /// If the VM is a [org.cloudsimplus.vms.VmGroup], each VM in the group is created
    /// (and the group is created if at least one of its VMs is).
    /// This is the only way a VM is placed into a Host, so the Host and the VM are always in sync.
    ///
    /// @param vm Vm being started
    /// @return a [HostSuitability] to indicate if the Vm was placed into the host or not
    /// (if the Host doesn't have enough resources to allocate the Vm)
    /// @see #isLazySuitabilityEvaluation()
    HostSuitability createVm(Vm vm);

    /// {@return an object to manage and notify the [EventListener]s of this Host}
    ///
    /// @see HostListeners
    HostListeners listeners();

    /// Provisions part of a Host resource, such as [Ram] or [Bandwidth], to a VM placed on this Host.
    /// It asks the [RAM][#getRamAllocator()] or [BW][#getBwAllocator()] allocator to allocate (reserve) the given amount
    /// and, only if that succeeds, delivers it to the VM, making it the VM capacity for the resource.
    /// The given amount replaces any amount previously provisioned to the VM (it isn't added to it).
    ///
    /// @param vm the VM to provision the resource to
    /// @param resourceClass the class of the resource to provision
    /// @param amount the new total amount of the resource for the VM
    /// @return true if the resource was allocated and provisioned to the VM, false otherwise
    /// @see ResourceAllocator
    boolean provisionResourceForVm(Vm vm, Class<? extends ResourceManageable> resourceClass, long amount);

    /// @return the current percentage (between \[0 and 1\]) of CPU capacity (MIPS) used by all running VMs.
    /// It represents the actual percentage of MIPS allocated.
    double getCpuPercentUtilization();

    /// {@inheritDoc}
    /// It relies on the utilization statistics from its VMs to provide the overall Host's CPU utilization.
    /// However, for this method to return any data, you need to enable
    /// the statistics computation for every VM it owns.
    /// @return {@inheritDoc}
    /// @see #enableUtilizationStats()
    HostResourceStats getCpuUtilizationStats();

    /// {@inheritDoc}
    /// It iterates over all existing VMs, enabling the statistics computation on everyone.
    /// Keep in mind that when a Host is created, it has no VMs.
    /// Therefore, you need to call this method for every VM if you are enabling
    /// the computation before the simulation starts and VM placement is performed.
    /// @see Vm#enableUtilizationStats()
    void enableUtilizationStats();

    /// @return the current total utilization of CPU in MIPS,
    /// considering the usage of all its cores.
    double getCpuMipsUtilization();

    /// @return the current utilization of bw (in Megabits/s), which is the bw actually used by all VMs.
    /// It's always lower than or equal to the bw allocated for the VMs (`getBwAllocator().getTotalAllocatedResource()`).
    long getBwUtilization();

    /// @return the current utilization of memory (in Megabytes), which is the memory actually used by all VMs.
    /// It's always lower than or equal to the memory allocated for the VMs (`getRamAllocator().getTotalAllocatedResource()`).
    long getRamUtilization();

    /// @return the [PowerModelHost] used by the host to define how it consumes power.
    /// A Host just provides power usage data if a PowerModel is set.
    PowerModelHost getPowerModel();

    /// Sets the [PowerModelHost] used by the Host
    /// to define how it consumes power.
    /// A Host just provides power usage data if a PowerModel is set.
    ///
    /// @param powerModel the [PowerModelHost] to set
    PowerAware<PowerModelHost> setPowerModel(PowerModelHost powerModel);

    /// Enables or disables the recording of the Host state history.
    /// The history object is only created when it's enabled for the first time.
    /// Disabling it keeps the entries already recorded.
    /// @param enable true to enable, false to disable
    /// @see #getStateHistory()
    Host setStateHistoryEnabled(boolean enable);

    /// @return the Host state history, which is [HostStateHistory#NULL] if it was never
    /// [enabled][#setStateHistoryEnabled(boolean)].
    HostStateHistory getStateHistory();

    /// @return the List of VMs that have finished executing.
    List<Vm> getFinishedVms();

    /// @return the list of VMs that can be migrated from the Host.
    List<Vm> getMigratableVms();

    /// Checks if the suitability evaluation of this Host for a given Vm
    /// is to be performed lazily by methods such as [#isSuitableForVm(Vm)].
    /// See [#setLazySuitabilityEvaluation(boolean)] for important details.
    ///
    /// @return true if the lazy evaluation is enabled, false otherwise
    boolean isLazySuitabilityEvaluation();

    /// Defines if the suitability evaluation of this Host for a given Vm
    /// is to be performed lazily by methods such as [#isSuitableForVm(Vm)].
    /// It means that the method will return as soon as some resource requirement is not met.
    /// This way, the suitability for other VM requirements is not evaluated.
    /// This laziness improves performance but provides less information
    /// when calling [#getSuitabilityFor(Vm)].
    /// @param lazySuitabilityEvaluation true to enable lazy evaluation, false to disable
    Host setLazySuitabilityEvaluation(boolean lazySuitabilityEvaluation);
}
