/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.hosts;

import org.cloudsimplus.core.Machine;
import org.cloudsimplus.core.ResourceStatsComputer;
import org.cloudsimplus.core.Simulation;
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
 * The interface implements the Null Object Design
 * Pattern in order to start avoiding {@link NullPointerException}
 * when using the {@link Host#NULL} object instead
 * of attributing {@code null} to {@link Host} variables.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface Host extends Machine, Comparable<Host>, PowerAware<PowerModelHost>, ResourceStatsComputer<HostResourceStats> {
    Logger LOGGER = LoggerFactory.getLogger(Host.class.getSimpleName());

    /**
     * The default value for the {@link #getIdleShutdownDeadline()}.
     * This value indicates that the Host won't be shutdown when becoming idle.
     */
    double DEF_IDLE_SHUTDOWN_DEADLINE = -1;

    /**
     * An attribute that implements the Null Object Design Pattern for {@link Host}
     * objects.
     */
    Host NULL = new HostNull();

    /**
     * Gets the Datacenter where the host is placed.
     *
     * @return the data center of the host
     */
    Datacenter getDatacenter();

    /**
     * Sets the Datacenter where the host is placed.
     *
     * @param datacenter the new data center to move the host
     */
    void setDatacenter(Datacenter datacenter);

    /**
     * Checks if the host is suitable for a Vm
     * (if it has enough resources to attend the Vm)
     * and the Host is not failed.
     *
     * @param vm the Vm to check
     * @return true if is suitable for Vm, false otherwise
     * @see #getSuitabilityFor(Vm)
     */
    boolean isSuitableForVm(Vm vm);

    /**
     * Checks if the host is suitable for a Vm
     * (if it has enough resources to attend the Vm)
     * and the Host is not failed,
     * <b>providing fine-grained information
     * about each individual Host's resource suitability</b>.
     *
     * @param vm the Vm to check
     * @return a {@link HostSuitability} object containing
     * indicating the Host's resources that
     * are suitable or not for the given Vm.
     * @see #setLazySuitabilityEvaluation(boolean)
     */
    HostSuitability getSuitabilityFor(Vm vm);

    /**
     * Checks if the Host is powered-on or not.
     * @return true if the Host is powered-on, false otherwise.
     */
    boolean isActive();

    /**
     * Checks if the Host has ever started sometime,
     * i.e., if it was active sometime in the simulation execution.
     * @return
     */
    boolean hasEverStarted();

    /**
     * Requests the Host to be powered on or off.
     * If there is no {@link PowerModelHost#getStartupDelay()}
     * or {@link PowerModelHost#getShutDownDelay()} (which is the default),
     * those operations will happen immediately.
     *
     * <p>If the Host is set to be powered off while it has running VMs,
     * it is simulated a scheduled shutdown, so that those VMs will finish,
     * but new ones won't be submitted to this Host.</p>
     *
     * @param activate true to power on, false to power off
     * @return this Host instance
     * @throws IllegalStateException when trying to activate a {@link #isFailed() failed} host.
     * @see #setPowerModel(PowerModelHost)
     */
    Host setActive(boolean activate);

    /**
     * Gets the list of VMs migrating into this host.
     *
     * @param <T> the generic type
     * @return the vms migrating in
     */
    <T extends Vm> Set<T> getVmsMigratingIn();

    /**
     * Checks if there is any VMs migrating in or out this host.
     * @return
     */
    boolean hasMigratingVms();

    /**
     * Try to add a VM migrating into the current host
     * if there is enough resources for it.
     * In this case, the resources are allocated
     * and the VM added to the {@link #getVmsMigratingIn()} List.
     * Otherwise, the VM is not added.
     *
     * @param vm the vm
     * @return true if the Vm was migrated in;
     *         false if the Host doesn't have enough resources to place the Vm
     */
    boolean addMigratingInVm(Vm vm);

    /**
     * Gets a <b>read-only</b> list of VMs migrating out from the Host.
     *
     * @return
     */
    Set<Vm> getVmsMigratingOut();

    /**
     * Adds a {@link Vm} to the list of VMs migrating out from the Host.
     * @param vm the vm to be added
     * @return true if the VM wasn't into the list and was added, false otherwise
     */
    boolean addVmMigratingOut(Vm vm);

    /**
     * Adds a {@link Vm} to the list of VMs migrating out from the Host.
     * @param vm the vm to be added
     * @return
     */
    boolean removeVmMigratingOut(Vm vm);

    /**
     * Reallocate VMs migrating into the host. Gets the VM in the migrating in queue
     * and allocate them on the host.
     */
    void reallocateMigratingInVms();

    /**
     * Gets total MIPS capacity of PEs which are not {@link Status#FAILED}.
     * @return the total MIPS of working PEs
     */
    @Override
    double getTotalMipsCapacity();

    /**
     * Gets the current total amount of available MIPS at the host.
     *
     * @return the total available amount of MIPS
     */
    double getTotalAvailableMips();

    /**
     * Gets the total allocated MIPS at the host.
     *
     * @return the total allocated amount of MIPS
     */
    double getTotalAllocatedMips();

    /**
     * Gets the total allocated MIPS for a VM along all its PEs.
     *
     * @param vm the vm
     * @return the allocated mips for vm
     */
    double getTotalAllocatedMipsForVm(Vm vm);

    /**
     * Removes a VM migrating into this Host from the migrating-in list,
     * so that the VM can be actually placed into the Host
     * and the migration process finished.
     *
     * @param vm the vm
     */
    void removeMigratingInVm(Vm vm);

    /**
     * Gets the list of all Processing Elements (PEs) of the host,
     * including failed PEs.
     *
     * @return the list of all Host PEs
     * @see #getWorkingPeList()
     */
    List<Pe> getPeList();

    /**
     * Gets the list of working Processing Elements (PEs) of the host.
     * It's the list of all PEs which are not <b>FAILEd</b>.
     *
     * @return the list working (non-failed) Host PEs
     */
    List<Pe> getWorkingPeList();

    /**
     * Gets the list of working Processing Elements (PEs) of the host,
     * <b>which excludes failed PEs</b>.
     *
     * @return the list working (non-failed) Host PEs
     */
    List<Pe> getBusyPeList();

    /**
     * Gets the list of Free Processing Elements (PEs) of the host,
     * <b>which excludes failed PEs</b>.
     *
     * @return the list free (non-failed) Host PEs
     */
    List<Pe> getFreePeList();

    /**
     * Gets the number of PEs that are free to be used by any VM.
     *
     * @return the free pes number
     */
    int getFreePesNumber();

    /**
     * Gets the number of PEs that are working.
     * That is, the number of PEs that aren't FAIL.
     *
     * @return the number of working pes
     */
    int getWorkingPesNumber();

    /**
     * Gets the number of PEs that are {@link Pe.Status#BUSY}.
     * That is, the number of PEs that aren't FAIL.
     *
     * @return the number of working pes
     */
    int getBusyPesNumber();

    /**
     * Gets the current percentage (from 0..1) of used (busy) PEs,
     * according to the {@link #getPesNumber() total number of PEs}.
     * @return
     * @see #getBusyPesPercent(boolean)
     */
    double getBusyPesPercent();

    /**
     * Gets the current percentage of used (busy) PEs,
     * according to the {@link #getPesNumber() total number of PEs}.
     * @param hundredScale if true, result is provided from 0..100 scale;
     *                     otherwise, it's returned in scale from 0..1.
     * @return the percentage of busy PEs in the defined scale
     * @see #getBusyPesPercent()
     */
    double getBusyPesPercent(boolean hundredScale);

    /**
     * Gets the number of PEs that have failed.
     *
     * @return the number of failed pes
     */
    int getFailedPesNumber();

    /**
     * Gets the total free storage available at the host in Megabytes.
     *
     * @return the free storage
     */
    long getAvailableStorage();

    /**
     * Gets the bandwidth (BW) provisioner with capacity in Megabits/s.
     *
     * @return the bw provisioner
     */
    ResourceProvisioner getBwProvisioner();

    /**
     * Sets the bandwidth (BW) provisioner with capacity in Megabits/s.
     *
     * @param bwProvisioner the new bw provisioner
     */
    Host setBwProvisioner(ResourceProvisioner bwProvisioner);

    /**
     * Gets the ram provisioner with capacity in Megabytes.
     *
     * @return the ram provisioner
     */
    ResourceProvisioner getRamProvisioner();

    /**
     * Sets the ram provisioner with capacity in Megabytes.
     *
     * @param ramProvisioner the new ram provisioner
     */
    Host setRamProvisioner(ResourceProvisioner ramProvisioner);

    /**
     * Gets as list of VMs currently assigned to the host.
     *
     * @param <T> The generic type
     * @return the read-only current vm list
     */
    <T extends Vm> List<T> getVmList();

    /**
     * Gets a <b>read-only</b> list of all VMs which have been created into the host
     * during the entire simulation.
     * This way, this method returns a historic list of created VMs,
     * including those ones already destroyed.
     *
     * @param <T> The generic type
     * @return the read-only vm created historic list
     */
    <T extends Vm> List<T> getVmCreatedList();

    /**
     * Gets the policy for allocation of host PEs to VMs in order to schedule VM execution.
     *
     * @return the {@link VmScheduler}
     */
    VmScheduler getVmScheduler();

    /**
     * Sets the policy for allocation of host PEs to VMs in order to schedule VM
     * execution. The host also sets itself to the given scheduler.
     * It also sets the Host itself to the given scheduler.
     *
     * @param vmScheduler the vm scheduler to set
     * @return
     */
    Host setVmScheduler(VmScheduler vmScheduler);

    /**
     * Gets the first time the Host was powered-on (in seconds).
     * @return the first Host startup time or -1 if the Host has never been powered on
     * @see #getStartTime()
     * @see #setActive(boolean)
     */
    double getFirstStartTime();

    /**
     * Gets the last time the Host was shut down (in seconds).
     * @return the last shut downtime or -1 if the Host is active
     */
    double getShutdownTime();

    /**
     * Sets the the Host shut down time.
     * @param shutdownTime the time to set (in seconds)
     * @see #getShutdownTime()
     */
    void setShutdownTime(double shutdownTime);

    /**
     * Gets the elapsed time since the last power on.
     * @return the elapsed time (in seconds)
     * @see #getUpTimeHours()
     * @see #getTotalUpTime()
     * @see #getTotalUpTimeHours()
     */
    double getUpTime();

    /**
     * Gets the elapsed time in hours since the last power on.
     * @return the elapsed time (in hours)
     * @see #getUpTime()
     * @see #getTotalUpTime()
     * @see #getTotalUpTimeHours()
     */
    double getUpTimeHours();

    /**
     * Gets the total time the Host stayed active (powered on).
     * Since the Host can be powered on and off according to demand,
     * this method returns the sum of all intervals the Host
     * was active (in seconds).
     *
     * @return the total uptime (in seconds)
     * @see #setActive(boolean)
     * @see #setIdleShutdownDeadline(double)
     * @see #getTotalUpTimeHours()
     * @see #getUpTime()
     * @see #getUpTimeHours()
     */
    double getTotalUpTime();

    /**
     * Gets the total time the Host stayed active (powered on).
     * Since the Host can be powered on and off according to demand,
     * this method returns the sum of all intervals the Host
     * was active (in hours).
     *
     * @return the total uptime (in hours)
     * @see #setActive(boolean)
     * @see #setIdleShutdownDeadline(double)
     * @see #getTotalUpTime()
     * @see #getUpTime()
     * @see #getUpTimeHours()
     */
    double getTotalUpTimeHours();

    /**
     * Gets the deadline to shut down the Host when it becomes idle.
     * This is the time interval after the Host becoming idle that
     * it will be shutdown.
     * @see #DEF_IDLE_SHUTDOWN_DEADLINE
     * @return the idle shutdown deadline (in seconds)
     */
    double getIdleShutdownDeadline();

    /**
     * Sets the deadline to shut down the Host when it becomes idle.
     * This is the time interval after the Host becoming idle that
     * it will be shutdown.
     *
     * @param deadline the deadline to shut down the Host after it becoming idle (in seconds).
     *                 A negative value disables idle host shutdown.
     * @see #DEF_IDLE_SHUTDOWN_DEADLINE
     * @see #getIdleShutdownDeadline()
     */
    Host setIdleShutdownDeadline(double deadline);

    /**
     * Checks if the host is working properly or has failed.
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
     * that makes the processing of cloudlets inside such VMs to be updated.
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
     * Destroys a VM running in the host and removes it from the {@link #getVmList()}.
     * If the VM was not created yet, this method has no effect.
     *
     * @param vm the VM to be destroyed
     */
    void destroyVm(Vm vm);

    /**
     * Try to allocate resources to a new temporary VM in the Host.
     * The method is used only to book resources for a given VM.
     * For instance, if Hosts are being chosen to migrate a set of VMs,
     * when a Host is selected for a given VM, using this method,
     * the resources are reserved and then, when the next
     * VM is selected for the same Host, the
     * reserved resources already were reduced from the available
     * amount. This way, if it was possible to place just one Vm into that Host,
     * with the booking, no other VM will be selected to that Host.
     *
     * @param vm Vm being started
     * @return a {@link HostSuitability} to indicate if the Vm was placed into the host or not
     * (if the Host doesn't have enough resources to allocate the Vm)
     * TODO: https://github.com/cloudsimplus/cloudsimplus/issues/94
     */
    HostSuitability createTemporaryVm(Vm vm);

    /**
     * Destroys a temporary VM created into the Host to book resources.
     *
     * @param vm the VM
     * @see #createTemporaryVm(Vm)
     * @TODO: https://github.com/cloudsimplus/cloudsimplus/issues/94
     */
    void destroyTemporaryVm(Vm vm);

    /**
     * Destroys all VMs running in the host and remove them from the {@link #getVmList()}.
     */
    void destroyAllVms();

    /**
     * Adds a listener object that will be notified every time
     * the host is <b>powered on</b>.
     *
     * @param listener the Listener to add
     * @return
     */
    Host addOnStartupListener(EventListener<HostEventInfo> listener);

    /**
     * Removes a Listener object from the registered List.
     * @param listener the Listener to remove
     * @return true if the Listener was removed, false otherwise
     */
    boolean removeOnStartupListener(EventListener<HostEventInfo> listener);

    /**
     * Adds a listener object that will be notified every time
     * the host is <b>powered off</b>.
     *
     * @param listener the Listener to add
     * @return
     */
    Host addOnShutdownListener(EventListener<HostEventInfo> listener);

    /**
     * Removes a Listener object from the registered List.
     * @param listener the Listener to remove
     * @return true if the Listener was removed, false otherwise
     */
    boolean removeOnShutdownListener(EventListener<HostEventInfo> listener);

    /**
     * Adds a listener object that will be notified every time
     * the host updates the processing of all its {@link Vm VMs}.
     *
     * @param listener the OnUpdateProcessingListener to add
     * @return
     * @see #updateProcessing(double)
     */
    Host addOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> listener);

    /**
     * Removes a Listener object from the registered List.
     *
     * @param listener the listener to remove
     * @return true if the listener was found and removed, false otherwise
     * @see #updateProcessing(double)
     */
    boolean removeOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> listener);

    /**
     * Sets the CloudSimPlus instance that represents the simulation the Entity belongs
     * Such attribute has to be set by the {@link Datacenter} that the host belongs to.
     * @param simulation The CloudSimPlus instance that represents the simulation the Entity belongs
     */
    Host setSimulation(Simulation simulation);

    /**
     * Gets the {@link ResourceProvisioner}s that manages a Host resource
     * such as {@link Ram}, {@link Bandwidth} and {@link Pe}.
     * @param resourceClass the class of the resource to get its provisioner
     * @return the {@link ResourceProvisioner} for the given resource class
     */
    ResourceProvisioner getProvisioner(Class<? extends ResourceManageable> resourceClass);

    /**
     * Gets the current percentage of CPU capacity (MIPS %) used by all running VMs.
     * It represents the actual percentage of MIPS allocated.
     *
     * @return total CPU utilization percentage (between [0 and 1]) for the current time
     */
    double getCpuPercentUtilization();

    /**
     * Gets the percentage of CPU capacity (MIPS %) requested by all running VMs at the current time.
     * It represents the percentage of MIPS requested,
     * which may be higher than the percentage used (allocated)
     * due to lack of capacity.
     *
     * @return the percentage (between [0 and 1]) of CPU capacity requested
     */
    double getCpuPercentRequested();

    /**
     * {@inheritDoc}
     * It uses the utilization statistics from its VMs to provide the overall Host's CPU utilization.
     * However, for this method to return any data, you need to enable
     * the statistics computation for every VM it owns.
     * @return {@inheritDoc}
     */
    HostResourceStats getCpuUtilizationStats();

    /**
     * {@inheritDoc}
     * It iterates over all existing VMs enabling the statistics computation on every one.
     * But keep in mind that when a Host is created, it has no VM.
     * Therefore, you need to call this method for every VM if you are enabling
     * the computation before the simulation starts and VM placement is performed.
     */
    void enableUtilizationStats();

    /**
     * Gets the current total utilization of CPU in MIPS,
     * considering the usage of all its PEs.
     *
     * @return
     */
    double getCpuMipsUtilization();

    /**
     * Gets the current utilization of bw (in Megabits/s).
     *
     * @return
     */
    long getBwUtilization();

    /**
     * Gets the current utilization of memory (in Megabytes).
     *
     * @return
     */
    long getRamUtilization();

    /**
     * Gets the {@link PowerModelHost} used by the host
     * to define how it consumes power.
     * A Host just provides power usage data if a PowerModel is set.
     *
     * @return the Host's {@link PowerModelHost}
     */
    PowerModelHost getPowerModel();

    /**
     * Sets the {@link PowerModelHost} used by the host
     * to define how it consumes power.
     * A Host just provides power usage data if a PowerModel is set.
     *
     * @param powerModel the {@link PowerModelHost} to set
     * @return
     */
    PowerAware<PowerModelHost> setPowerModel(PowerModelHost powerModel);

    /**
     * Enables or disables storage of Host state history.
     * @param enable true to enable, false to disable
     * @see #getStateHistory()
     */
    Host setStateHistoryEnabled(boolean enable);

    /**
     * Checks if Host state history is being collected and stored.
     * @return
     */
    boolean isStateHistoryEnabled();

    /**
     * Gets a <b>read-only</b> host state history.
     * This List is just populated if {@link #isStateHistoryEnabled()}
     *
     * @return the state history
     * @see #setStateHistoryEnabled(boolean)
     */
    List<HostStateHistoryEntry> getStateHistory();

    /**
     * Gets the List of VMs that have finished executing.
     * @return
     */
    List<Vm> getFinishedVms();

    /**
     * Gets the list of migratable VMs from a given host.
     *
     * @return the list of migratable VMs
     */
    List<Vm> getMigratableVms();

    /**
     * Checks if the suitability evaluation of this Host for a given Vm
     * is to be performed lazily by methods such as {@link #isSuitableForVm(Vm)}.
     * It means that the method will return as soon as some resource requirement is not met
     * and the suitability for other VM requirements is not evaluated.
     * This laziness improves performance but provides less information
     * when calling {@link #getSuitabilityFor(Vm)}.
     *
     * @return true if the lazy evaluation is enabled, false otherwise
     */
    boolean isLazySuitabilityEvaluation();

    /**
     * Defines if the suitability evaluation of this Host for a given Vm
     * is to be performed lazily by methods such as {@link #isSuitableForVm(Vm)}.
     * It means that the method will return as soon as some resource requirement is not met
     * and the suitability for other VM requirements is not evaluated.
     * This laziness improves performance but provides less information
     * when calling {@link #getSuitabilityFor(Vm)}.
     */
    Host setLazySuitabilityEvaluation(boolean lazySuitabilityEvaluation);
}
