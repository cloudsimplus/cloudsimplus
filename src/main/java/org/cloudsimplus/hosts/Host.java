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
import org.cloudsimplus.provisioners.ResourceProvisioner;
import org.cloudsimplus.resources.Bandwidth;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.Pe.Status;
import org.cloudsimplus.resources.Ram;
import org.cloudsimplus.resources.ResourceManageable;
import org.cloudsimplus.schedulers.vm.VmScheduler;
import org.cloudsimplus.vms.HostResourceStats;
import org.cloudsimplus.vms.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * An interface to be implemented by each class that provides
 * Physical Machines (Hosts) features.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public sealed interface Host
    extends PhysicalMachine, Comparable<Host>, PowerAware<PowerModelHost>, ResourceStatsComputer<HostResourceStats>
    permits HostAbstract, HostNull
{
    Logger LOGGER = LoggerFactory.getLogger(Host.class.getSimpleName());

    /**
     * The default value for the {@link #getIdleShutdownDeadline()}.
     * This value indicates that the Host won't be shutdown when becoming idle.
     */
    double DEF_IDLE_SHUTDOWN_DEADLINE = -1;

    /**
     * An attribute that implements the Null Object Design Pattern for {@link Host} objects.
     */
    Host NULL = new HostNull();

    /**
     * @return the {@link Datacenter} where the host is placed.
     */
    Datacenter getDatacenter();

    /**
     * Sets the {@link Datacenter} where the host is placed.
     *
     * @param datacenter the new data center to move the host
     */
    void setDatacenter(Datacenter datacenter);

    /**
     * Checks if the Host is suitable for a Vm
     * (if it has enough resources to meet the Vm requirements)
     * and the Host has not failed.
     *
     * @param vm the Vm to check
     * @return true if is suitable for Vm, false otherwise
     * @see #getSuitabilityFor(Vm)
     */
    boolean isSuitableForVm(Vm vm);

    /**
     * Checks if the host is suitable for a Vm
     * (if it has enough resources to meet the Vm requirements)
     * and the Host has not failed,
     * <b>providing fine-grained information
     * about the suitability of each Host resource</b>.
     *
     * @param vm the Vm to check
     * @return a {@link HostSuitability} object
     * indicating the Host's resources that
     * are suitable or not for the given Vm.
     * @see #setLazySuitabilityEvaluation(boolean)
     */
    HostSuitability getSuitabilityFor(Vm vm);

    /**
     * @return true if the Host is powered-on, false otherwise.
     * @see #isFinished()
     */
    boolean isActive();

    /**
     * @return true if the host is powered-off, false otherwise
     * @see #isActive()
     */
    @Override
    boolean isFinished();

    /**
     * @return true if the Host has ever started sometime,
     * i.e., if it was active sometime in the simulation execution;
     * false otherwise.
     */
    boolean hasEverStarted();

    /**
     * Requests the Host to be powered on or off.
     * If there is no {@link #getStartupDelay()}
     * or {@link #getShutDownDelay()} (which is the default),
     * those operations will happen immediately.
     *
     * <p>If the Host is set to be powered off while it has VMs running,
     * it is simulated a scheduled shutdown, so that those VMs will finish,
     * but new ones won't be submitted to this Host.</p>
     *
     * @param activate true to power on, false to power off
     * @return this Host instance
     * @throws IllegalStateException when trying to activate a {@link #isFailed() failed} host.
     * @see #setPowerModel(PowerModelHost)
     * @see #shutdown()
     */
    Host setActive(boolean activate);

    /**
     * Sends a request to shut down the Host.
     */
    @Override
    void shutdown();

    /**
     * Gets the list of VMs migrating into this host.
     *
     * @param <T> the generic type
     * @return the VMs migrating in
     */
    <T extends Vm> Set<T> getVmsMigratingIn();

    /**
     * @return true if there is any VM migrating in or out this host; false otherwise.
     */
    boolean hasMigratingVms();

    /**
     * Try to add a Vm migrating into the current Host
     * if there are enough resources for it.
     * In this case, the resources are allocated
     * and the Vm is added to the {@link #getVmsMigratingIn()} List.
     * Otherwise, the Vm is not added.
     *
     * @param vm the Vm to add to the migrating-in list
     * @return true if the Vm was migrated in;
     *         false if the Host doesn't have enough resources to place the Vm
     */
    boolean addMigratingInVm(Vm vm);

    /**
     * @return a <b>read-only</b> list of VMs migrating out from the Host.
     */
    Set<Vm> getVmsMigratingOut();

    /**
     * Adds a {@link Vm} to the list of VMs migrating out this Host.
     * @param vm the Vm to be added
     * @return true if the Vm was added, false otherwise
     */
    boolean addVmMigratingOut(Vm vm);

    /**
     * Removes a {@link Vm} from the list of VMs migrating out this Host.
     * @param vm the Vm to be removed
     * @return true if the Vm was removed, false otherwise
     */
    boolean removeVmMigratingOut(Vm vm);

    /**
     * Reallocate VMs migrating into this Host. Gets the VMs in the migrating-in queue
     * and allocates them on the Host.
     */
    void reallocateMigratingInVms();

    /**
     * @return total MIPS capacity of {@link Pe}s which are not {@link Status#FAILED}.
     */
    @Override
    double getTotalMipsCapacity();

    /**
     * @return the current total amount of available MIPS at the Host.
     */
    double getTotalAvailableMips();

    /**
     * @return the total allocated MIPS at the Host.
     */
    double getTotalAllocatedMips();

    /**
     * {@return the total allocated MIPS for a VM across all its PEs}
     * @param vm the Vm to get its total allocated MIPS
     */
    double getTotalAllocatedMipsForVm(Vm vm);

    /**
     * Removes a VM migrating into this Host from the migrating-in list,
     * so that the VM can be actually placed into the Host
     * and the migration process finished.
     *
     * @param vm the Vm to remove
     */
    void removeMigratingInVm(Vm vm);

    /**
     * @return the list of all {@link Pe}s of the host, including failed PEs.
     * @see #getWorkingPeList()
     */
    List<Pe> getPeList();

    /**
     * @return the list of working (non-failed) {@link Pe}s of the Host.
     */
    List<Pe> getWorkingPeList();

    /**
     * @return the list of {@link Pe}s of the Host that are {@link Status#BUSY}.
     */
    List<Pe> getBusyPeList();

    /**
     *  @return the list of free (non-failed) {@link Pe}s of the Host.
     */
    List<Pe> getFreePeList();

    /**
     * @return the number of {@link Pe}s that are free to be used by any VM.
     */
    int getFreePesNumber();

    /**
     * @return the number of {@link Pe}s that are working (non-failed).
     */
    int getWorkingPesNumber();

    /**
     * @return the number of {@link Pe}s that are {@link Pe.Status#BUSY}.
     */
    int getBusyPesNumber();

    /**
     * @return the current percentage (from 0..1) of used (busy) {@link Pe}s,
     * according to the {@link #getPesNumber() total number of PEs}.
     * @see #getBusyPesPercent(boolean)
     */
    double getBusyPesPercent();

    /**
     * Gets the current percentage of used (busy) {@link Pe}s,
     * according to the {@link #getPesNumber() total number of PEs}.
     * @param hundredScale true for getting the return in the 0..100 scale;
     *                     false for the 0..1 scale.
     * @return the percentage of busy PEs in the defined scale (according to the hundredScale parameter).
     * @see #getBusyPesPercent()
     */
    double getBusyPesPercent(boolean hundredScale);

    /**
     * @return the number of {@link Pe}s that have failed.
     */
    int getFailedPesNumber();

    /**
     * @return the total free storage available at the Host in Megabytes.
     */
    long getAvailableStorage();

    /**
     * @return the bandwidth (BW) provisioner with capacity in Megabits/s.
     */
    ResourceProvisioner getBwProvisioner();

    /**
     * Sets the bandwidth (BW) provisioner with capacity in Megabits/s.
     *
     * @param bwProvisioner the new bw provisioner
     */
    Host setBwProvisioner(ResourceProvisioner bwProvisioner);

    /**
     * @return the ram provisioner with capacity in Megabytes.
     */
    ResourceProvisioner getRamProvisioner();

    /**
     * Sets the ram provisioner with capacity in Megabytes.
     *
     * @param ramProvisioner the new ram provisioner
     */
    Host setRamProvisioner(ResourceProvisioner ramProvisioner);

    /**
     * Gets a list of VMs currently assigned to the Host.
     *
     * @param <T> The generic type
     * @return the read-only current VM list
     */
    <T extends Vm> List<T> getVmList();

    /**
     * Gets a <b>read-only</b> list of all VMs that have been created into the Host
     * during the entire simulation.
     * This way, this method returns a historic list of created VMs,
     * including those already destroyed.
     *
     * @param <T> The Vm generic type
     * @return the read-only created VM historic list
     */
    <T extends Vm> List<T> getVmCreatedList();

    /**
     * @return the policy for allocation of host {@link Pe}s to VMs to schedule VM execution.
     */
    VmScheduler getVmScheduler();

    /**
     * Sets the policy for allocation of host {@link Pe}s to VMs to schedule VM
     * execution. The host also sets itself to the given scheduler.
     * It also sets the Host itself to the given scheduler.
     *
     * @param vmScheduler the vm scheduler to set
     * @return this Host
     */
    Host setVmScheduler(VmScheduler vmScheduler);

    /**
     * @return the first Host startup time (in seconds) or -1 if the Host has never been powered on.
     * @see #getStartTime()
     * @see #setActive(boolean)
     */
    double getFirstStartTime();

    /**
     * @return the last time the Host was shut down (in seconds) or -1 if the Host is active.
     */
    @Override
    double getFinishTime();

    /**
     * Sets the Host shut down time.
     * @param shutdownTime the time to set (in seconds)
     * @see #getFinishTime()
     */
    @Override
    Startable setFinishTime(double shutdownTime);

    /**
     * @return the elapsed time (in seconds) since the last power on.
     * @see #getUpTimeHours()
     * @see #getTotalUpTime()
     * @see #getTotalUpTimeHours()
     */
    double getUpTime();

    /**
     * @return the elapsed time (in hours) since the last power on.
     * @see #getUpTime()
     * @see #getTotalUpTime()
     * @see #getTotalUpTimeHours()
     */
    double getUpTimeHours();

    /**
     * {@return the total time (in seconds) the Host stayed active (powered on)}
     * Since the Host can be powered on and off according to the demand,
     * this method returns the sum of all intervals the Host was active.
     *
     * @see #setActive(boolean)
     * @see #setIdleShutdownDeadline(double)
     * @see #getTotalUpTimeHours()
     * @see #getUpTime()
     * @see #getUpTimeHours()
     */
    double getTotalUpTime();

    /**
     * {@return the total time (in hours) the Host stayed active (powered on)}
     * Since the Host can be powered on and off according to the demand,
     * this method returns the sum of all intervals the Host was active.
     *
     * @see #setActive(boolean)
     * @see #setIdleShutdownDeadline(double)
     * @see #getTotalUpTime()
     * @see #getUpTime()
     * @see #getUpTimeHours()
     */
    double getTotalUpTimeHours();

    /**
     * {@return the deadline to shut down the Host (in seconds) when it becomes idle}
     * This is the time interval after the Host becomes idle that it will be shut down.
     * @see #DEF_IDLE_SHUTDOWN_DEADLINE
     */
    double getIdleShutdownDeadline();

    /**
     * Sets the deadline to shut down the Host (in seconds) when it becomes idle.
     * This is the time interval after the Host becomes idle that it will be shut down.
     *
     * @param deadline the deadline to shut down the Host after it becomes idle (in seconds).
     *                 A negative value disables idle host shutdown.
     * @see #DEF_IDLE_SHUTDOWN_DEADLINE
     * @see #getIdleShutdownDeadline()
     */
    Host setIdleShutdownDeadline(double deadline);

    /**
     * Checks if the host has failed or is working properly.
     *
     * @return true, if the host PEs have failed; false otherwise
     */
    boolean isFailed();

    /**
     * Sets the Host state to "failed" or "working".
     *
     * @param failed true to set the Host to "failed", false to set to "working"
     * @return true if the Host status was changed, false otherwise
     */
    boolean setFailed(boolean failed);

    /**
     * Updates the processing of VMs running on this Host,
     * that makes the processing of Cloudlets inside such VMs to be updated.
     *
     * @param currentTime the current time
     * @return the predicted completion time of the earliest finishing cloudlet
     * (which is a relative delay from the current simulation time),
     * or {@link Double#MAX_VALUE} if there is no next Cloudlet to execute
     */
    double updateProcessing(double currentTime);

    /**
     * Try to allocate resources to a new VM in the Host.
     *
     * @param vm Vm being started
     * @return a {@link HostSuitability} to indicate if the Vm was placed into the host or not
     * (if the Host doesn't have enough resources to allocate the Vm)
     * @see #isLazySuitabilityEvaluation()
     */
    HostSuitability createVm(Vm vm);

    /**
     * Try to allocate resources to a new temporary VM in the Host.
     * The method is used only to book resources for a given VM.
     * If Hosts are being chosen to migrate a set of VMs,
     * when a Host is selected for a given VM, using this method:
     * - the resources are reserved,
     * - then, when the next Vm is selected for the same Host,
     *   the previous reserved resources were already reduced from the available amount.
     * This way, if it was possible to place just one Vm into that Host,
     * with the booking, no other VM will be selected to it.
     *
     * @param vm Vm being started
     * @return a {@link HostSuitability} to indicate if the Vm was placed into the Host or not
     * (if the Host doesn't have enough resources to allocate the Vm)
     * TODO: https://github.com/cloudsimplus/cloudsimplus/issues/94
     */
    HostSuitability createTemporaryVm(Vm vm);

    /**
     * Adds a listener object that will be notified every time the host is <b>powered on</b>.
     *
     * @param listener the Listener to add
     * @return this Host
     */
    Host addOnStartupListener(EventListener<HostEventInfo> listener);

    /**
     * Removes a Listener object from the OnStartup List.
     * @param listener the Listener to remove
     * @return true if the Listener was removed, false otherwise
     */
    boolean removeOnStartupListener(EventListener<HostEventInfo> listener);

    /**
     * Adds a listener object that will be notified every time the host is <b>powered off</b>.
     *
     * @param listener the Listener to add
     * @return this Host
     */
    Host addOnShutdownListener(EventListener<HostEventInfo> listener);

    /**
     * Removes a Listener object from the OnShutdown List.
     * @param listener the Listener to remove
     * @return true if the Listener was removed, false otherwise
     */
    boolean removeOnShutdownListener(EventListener<HostEventInfo> listener);

    /**
     * Adds a listener object that will be notified every time
     * the host updates the processing of all its {@link Vm VMs}.
     *
     * @param listener the OnUpdateProcessingListener to add
     * @return this Host
     * @see #updateProcessing(double)
     */
    Host addOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> listener);

    /**
     * Removes a Listener object from the OnUpdateProcessing List.
     *
     * @param listener the listener to remove
     * @return true if the listener was found and removed, false otherwise
     * @see #updateProcessing(double)
     */
    boolean removeOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> listener);

    /**
     * Sets the {@link CloudSimPlus} instance that represents the simulation the Host is related to.
     * Such an attribute has to be set by the {@link Datacenter} that the host belongs to.
     * @param simulation The CloudSimPlus instance that represents the simulation the Host is related
     */
    Host setSimulation(Simulation simulation);

    /**
     * Gets the {@link ResourceProvisioner}s that manages a Host's resource
     * such as {@link Ram}, {@link Bandwidth} and {@link Pe}.
     * @param resourceClass the class of the resource to get its provisioner
     * @return the {@link ResourceProvisioner} for the given resource class
     */
    ResourceProvisioner getProvisioner(Class<? extends ResourceManageable> resourceClass);

    /**
     * @return the current percentage (between [0 and 1]) of CPU capacity (MIPS) used by all running VMs.
     * It represents the actual percentage of MIPS allocated.
     */
    double getCpuPercentUtilization();

    /**
     * @return the percentage (between [0 and 1]) of CPU capacity (MIPS) requested by all running VMs at the current time.
     * It represents the percentage of MIPS requested,
     * which may be higher than the percentage used (allocated)
     * due to lack of capacity.
     */
    double getCpuPercentRequested();

    /**
     * {@inheritDoc}
     * It relies on the utilization statistics from its VMs to provide the overall Host's CPU utilization.
     * However, for this method to return any data, you need to enable
     * the statistics computation for every VM it owns.
     * @return {@inheritDoc}
     * @see #enableUtilizationStats()
     */
    HostResourceStats getCpuUtilizationStats();

    /**
     * {@inheritDoc}
     * It iterates over all existing VMs, enabling the statistics computation on everyone.
     * Keep in mind that when a Host is created, it has no VMs.
     * Therefore, you need to call this method for every VM if you are enabling
     * the computation before the simulation starts and VM placement is performed.
     * @see Vm#enableUtilizationStats()
     */
    void enableUtilizationStats();

    /**
     * @return the current total utilization of CPU in MIPS,
     * considering the usage of all its PEs.
     */
    double getCpuMipsUtilization();

    /**
     * @return the current utilization of bw (in Megabits/s).
     */
    long getBwUtilization();

    /**
     * @return the current utilization of memory (in Megabytes).
     */
    long getRamUtilization();

    /**
     * @return the {@link PowerModelHost} used by the host to define how it consumes power.
     * A Host just provides power usage data if a PowerModel is set.
     */
    PowerModelHost getPowerModel();

    /**
     * Sets the {@link PowerModelHost} used by the Host
     * to define how it consumes power.
     * A Host just provides power usage data if a PowerModel is set.
     *
     * @param powerModel the {@link PowerModelHost} to set
     */
    PowerAware<PowerModelHost> setPowerModel(PowerModelHost powerModel);

    /**
     * Enables or disables the storage of Host state history.
     * @param enable true to enable, false to disable
     * @see #getStateHistory()
     */
    Host setStateHistoryEnabled(boolean enable);

    /**
     * @return if Host state history is being collected and stored.
     */
    boolean isStateHistoryEnabled();

    /**
     * @return a <b>read-only</b> Host state history.
     * This List is just populated if {@link #isStateHistoryEnabled()}
     *
     * @see #setStateHistoryEnabled(boolean)
     */
    List<HostStateHistoryEntry> getStateHistory();

    /**
     * @return the List of VMs that have finished executing.
     */
    List<Vm> getFinishedVms();

    /**
     * @return the list of VMs that can be migrated from the Host.
     */
    List<Vm> getMigratableVms();

    /**
     * Checks if the suitability evaluation of this Host for a given Vm
     * is to be performed lazily by methods such as {@link #isSuitableForVm(Vm)}.
     * See {@link #setLazySuitabilityEvaluation(boolean)} for important details.
     *
     * @return true if the lazy evaluation is enabled, false otherwise
     */
    boolean isLazySuitabilityEvaluation();

    /**
     * Defines if the suitability evaluation of this Host for a given Vm
     * is to be performed lazily by methods such as {@link #isSuitableForVm(Vm)}.
     * It means that the method will return as soon as some resource requirement is not met.
     * This way, the suitability for other VM requirements is not evaluated.
     * This laziness improves performance but provides less information
     * when calling {@link #getSuitabilityFor(Vm)}.
     * @param lazySuitabilityEvaluation true to enable lazy evaluation, false to disable
     */
    Host setLazySuitabilityEvaluation(boolean lazySuitabilityEvaluation);
}
