/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.hosts;

import org.cloudbus.cloudsim.core.Machine;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.Pe.Status;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmUtilizationHistory;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostEventInfo;
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

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
public interface Host extends Machine, Comparable<Host> {
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
     * Checks if the host is suitable for vm
     * (if it has enough resources to attend the VM)
     * and it's not failed.
     *
     * @param vm the vm to check
     * @return true if is suitable for vm, false otherwise
     */
    boolean isSuitableForVm(Vm vm);

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
     * Sets the powered state of the Host, to indicate if it's powered on or off.
     * When a Host is powered off, no VMs will be submitted to it.
     *
     * <p>If it is set to powered off while VMs are running inside it,
     * it is simulated a scheduled shutdown, so that, all running
     * VMs will finish, but not more VMs will be submitted to this Host.</p>
     *
     * @param activate define the Host activation status: true to power on, false to power off
     * @return this Host instance
     * @throws IllegalStateException when trying to activate a {@link #isFailed() failed} host.
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
     * Try to add a VM migrating into the current host
     * if there is enough resources for it.
     * In this case, the resources are allocated
     * and the VM added to the {@link #getVmsMigratingIn()} List.
     * Otherwise, the VM is not added.
     *
     * @param vm the vm
     * @return true if the Vm was migrated in, false if the Host doesn't have enough resources to place the Vm
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
     * Gets a <b>read-only</b> list of VMs currently assigned to the host.
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
     * Gets the last time the Host was powered-on (in seconds).
     * @return the last Host startup time or -1 if the Host has never been powered on
     * @see #getFirstStartTime()
     * @see #setActive(boolean)
     */
    double getStartTime();

    /**
     * Gets the first time the Host was powered-on (in seconds).
     * @return the first Host startup time or -1 if the Host has never been powered on
     * @see #getStartTime()
     * @see #setActive(boolean)
     */
    double getFirstStartTime();

    /**
     * Sets the Host start up time (the time it's being powered on).
     * @param startTime the time to set (in seconds)
     * @see #getStartTime()
     */
    void setStartTime(double startTime);

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
     * Gets the elapsed time since the last time the Host was powered on
     * @return the elapsed time (in seconds)
     * @see #getUpTimeHours()
     * @see #getTotalUpTime()
     * @see #getTotalUpTimeHours()
     */
    double getUpTime();

    /**
     * Gets the elapsed time since the last time the Host was powered on
     * @return the elapsed time (in hours)
     * @see #getUpTime()
     * @see #getTotalUpTime()
     * @see #getTotalUpTimeHours()
     */
    double getUpTimeHours();

    /**
     * Gets the total time the Host stayed active (powered on).
     * Since the Host can be powered on and off according to demand,
     * this method returns the sum of all interval that the Host
     * was active (in seconds).
     *
     * @return the total up time (in seconds)
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
     * this method returns the sum of all interval that the Host
     * was active (in hours).
     *
     * @return the total up time (in hours)
     * @see #setActive(boolean)
     * @see #setIdleShutdownDeadline(double)
     * @see #getTotalUpTime()
     * @see #getUpTime()
     * @see #getUpTimeHours()
     */
    double getTotalUpTimeHours();

    /**
     * Gets the deadline to shutdown the Host when it become idle.
     * This is the time interval after the Host becoming idle that
     * it will be shutdown.
     * @see #DEF_IDLE_SHUTDOWN_DEADLINE
     * @return the idle shutdown deadline (in seconds)
     */
    double getIdleShutdownDeadline();

    /**
     * Sets the deadline to shutdown the Host when it become idle.
     * This is the time interval after the Host becoming idle that
     * it will be shutdown.
     *
     * @param deadline the deadline to shutdown the Host after it becoming idle  (in seconds).
     *                 A negative value disables idle host shutdown.
     * @see #DEF_IDLE_SHUTDOWN_DEADLINE
     * @see #getIdleShutdownDeadline()
     * @return
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
     * @return $true if the VM could be started in the host; $false otherwise
     */
    boolean createVm(Vm vm);

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
     * For instance, if is being chosen Hosts to migrate a set of VMs,
     * when a Host is selected for a given VM, using this method,
     * the resources are reserved and then, when the next
     * VM is selected for the same Host, the
     * reserved resources already were reduced from the available
     * amount. This way, it it was possible to place just one Vm into that Host,
     * with the booking, no other VM will be selected to that Host.
     *
     * @param vm Vm being started
     * @return $true if the VM could be started in the host; $false otherwise
     * @TODO https://github.com/manoelcampos/cloudsim-plus/issues/94
     */
    boolean createTemporaryVm(Vm vm);

    /**
     * Destroys a temporary VM created into the Host to book resources.
     *
     * @param vm the VM
     * @see #createTemporaryVm(Vm)
     * @TODO https://github.com/manoelcampos/cloudsim-plus/issues/94
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
     * Sets the CloudSim instance that represents the simulation the Entity is related to.
     * Such attribute has to be set by the {@link Datacenter} that the host belongs to.
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     * @return
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
     * Gets current utilization of CPU in percentage (between [0 and 1]),
     * considering the usage of all its PEs..
     *
     * @return
     */
    double getCpuPercentUtilization();

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
     * <p>Gets a map containing the host CPU utilization percentage history (between [0 and 1]),
     * based on its VM utilization history.
     * Each key is a time when the data collection was performed
     * and each value is a {@link DoubleSummaryStatistics}
     * from where some operations over the CPU utilization entries for every VM inside the Host
     * can be performed. Such operations include counting, summing, averaging, etc.
     * For instance, if you call the {@link DoubleSummaryStatistics#getSum()},
     * you'll get the total Host's CPU utilization for the time specified
     * by the map key.
     * </p>
     *
     * <p>
     * There is an entry for each time multiple of the {@link Datacenter#getSchedulingInterval()}.
     * <b>This way, it's required to set a Datacenter scheduling interval with the desired value.</b>
     * </p>
     *
     * <p><b>In order to enable the Host to get utilization history,
     * its VMs' utilization history must be enabled
     * by calling {@link VmUtilizationHistory#enable() enable()} from
     * the {@link Vm#getUtilizationHistory()}.</b>
     * </p>
     *
     * @return a Map where keys are the data collection time
     * and each value is a {@link DoubleSummaryStatistics} objects
     * that provides lots of useful methods to get
     * max, min, average, count and sum of utilization values.
     *
     * @see #getUtilizationHistorySum()
     */
    SortedMap<Double, DoubleSummaryStatistics> getUtilizationHistory();

    /**
     * <p>Gets a map containing the total Host's CPU utilization (between [0 and 1])
     * along simulation time, based on its VM utilization history.
     * Each key is a time when the data collection was performed
     * and each value is the sum of all CPU utilization of the VMs running inside this Host for that time.
     * This way, the value represents the total Host's CPU utilization for each time
     * that data was collected.
     * </p>
     *
     * <p>
     * There is an entry for each time multiple of the {@link Datacenter#getSchedulingInterval()}.
     * <b>This way, it's required to set a Datacenter scheduling interval with the desired value.</b>
     * </p>
     *
     * <p><b>In order to enable the Host to get utilization history,
     * its VMs' utilization history must be enabled
     * by calling {@link VmUtilizationHistory#enable() enable()} from
     * the {@link Vm#getUtilizationHistory()}.</b>
     * </p>
     *
     * @return a Map where keys are the data collection time
     * and each value is the total Host's CPU utilization for each time.
     *
     * @see #getUtilizationHistory()
     */
    SortedMap<Double, Double> getUtilizationHistorySum();

    /**
     * Gets the {@link PowerModel} used by the host
     * to define how it consumes power.
     * A Host just provides power usage data if a PowerModel is set.
     *
     * @return the Host's {@link PowerModel}
     */
    PowerModel getPowerModel();

    /**
     * Sets the {@link PowerModel} used by the host
     * to define how it consumes power.
     * A Host just provides power usage data if a PowerModel is set.
     *
     * @param powerModel the {@link PowerModel} to set
     * @return
     */
    Host setPowerModel(PowerModel powerModel);

    double getPreviousUtilizationOfCpu();

    /**
     * Enables storing Host state history.
     * @see #getStateHistory()
     */
    void enableStateHistory();

    /**
     * Disable storing Host state history.
     * @see #getStateHistory()
     */
    void disableStateHistory();

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
     * @see #enableStateHistory()
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
}
