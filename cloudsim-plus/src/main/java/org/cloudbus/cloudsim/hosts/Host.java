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
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;

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
     * Checks if the host is active and is suitable for vm
     * (if it has enough resources to attend the VM).
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
     * Sets the powered state of the Host, to indicate if it's powered on or off.
     * When a Host is powered off, no VMs will be submitted to it.
     *
     * <p>If it is set to powered off while VMs are running inside it,
     * it is simulated a scheduled shutdown, so that, all running
     * VMs will finish, but not more VMs will be submitted to this Host.</p>
     *
     * @param active true to set the Host as powered on, false as powered off
     * @return
     */
    Host setActive(boolean active);

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
     * Adds a {@link Vm} to the list of VMs migrating into the Host.
     * @param vm the vm to be added
     * @return
     */
    boolean removeVmMigratingIn(Vm vm);

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
     * Removes a migrating in vm.
     *
     * @param vm the vm
     */
    void removeMigratingInVm(Vm vm);

    /**
     * Releases PEs allocated to a VM.
     *
     * @param vm the vm
     * @pre $none
     * @post $none
     */
    void deallocatePesForVm(Vm vm);

    /**
     * Gets the MIPS share of each Pe that is allocated to a given VM.
     *
     * @param vm the vm
     * @return an array containing the amount of MIPS of each pe that is available to the VM
     * @pre $none
     * @post $none
     */
    List<Double> getAllocatedMipsForVm(Vm vm);

    /**
     * Gets the total allocated MIPS for a VM along all its PEs.
     *
     * @param vm the vm
     * @return the allocated mips for vm
     */
    double getTotalAllocatedMipsForVm(Vm vm);

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
    List<Pe> getBuzyPeList();

    /**
     * Gets the list of Free Processing Elements (PEs) of the host,
     * <b>which excludes failed PEs</b>.
     *
     * @return the list free (non-failed) Host PEs
     */
    List<Pe> getFreePeList();

    /**
     * Gets the free pes number.
     *
     * @return the free pes number
     */
    int getNumberOfFreePes();

    /**
     * Gets the number of PEs that are working.
     * That is, the number of PEs that aren't FAIL.
     *
     * @return the number of working pes
     */
    long getNumberOfWorkingPes();

    /**
     * Gets the number of PEs that have failed.
     *
     * @return the number of failed pes
     */
    long getNumberOfFailedPes();

    /**
     * Gets the current amount of available MIPS at the host.
     *
     * @return the available amount of MIPS
     */
    double getAvailableMips();

    /**
     * Returns the maximum available MIPS among all the PEs of the host.
     *
     * @return max mips
     */
    double getMaxAvailableMips();

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
     * Gets a VM by its id and user.
     *
     * @param vmId the vm id
     * @param brokerId ID of VM's owner
     * @return the virtual machine object, $null if not found
     * @pre $none
     * @post $none
     */
    Vm getVm(int vmId, int brokerId);

    /**
     * Gets a <b>read-only</b> list of VMs currently assigned to the host.
     *
     * @param <T> The generic type
     * @return the read-only vm list
     */
    <T extends Vm> List<T> getVmList();

    /**
     * Gets a <b>read-only</b> list of all VMs which have been created into the host
     * during the entire simulation.
     * This way, this method returns a historic list of created VMs,
     * including those ones already destroyed.
     *
     * @param <T> The generic type
     * @return the read-only vm created list
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
     * Gets the time the Host was powered-on (in seconds).
     * @return
     */
    double getStartTime();

    /**
     * Sets the time the Host was powered-on.
     * @param startTime the time to set (in seconds)
     */
    void setStartTime(double startTime);

    /**
     * Gets the time the Host shut down.
     * @return
     */
    double getShutdownTime();

    /**
     * Sets the time the Host shut down.
     * @param shutdownTime the time to set
     */
    void setShutdownTime(double shutdownTime);

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
     * @pre currentTime >= 0.0
     * @post $none
     */
    double updateProcessing(double currentTime);

    /**
     * Try to allocate resources to a new VM in the Host.
     *
     * @param vm Vm being started
     * @return $true if the VM could be started in the host; $false otherwise
     * @pre $none
     * @post $none
     */
    boolean createVm(Vm vm);

    /**
     * Destroys a VM running in the host and removes it from the {@link #getVmList()}.
     *
     * @param vm the VM
     * @pre $none
     * @post $none
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
     * @pre $none
     * @post $none
     * @todo https://github.com/manoelcampos/cloudsim-plus/issues/94
     */
    boolean createTemporaryVm(Vm vm);

    /**
     * Destroys a temporary VM created into the Host to book resources.
     *
     * @param vm the VM
     * @pre $none
     * @post $none
     * @see #createTemporaryVm(Vm)
     * @todo https://github.com/manoelcampos/cloudsim-plus/issues/94
     */
    void destroyTemporaryVm(Vm vm);

    /**
     * Destroys all VMs running in the host and remove them from the {@link #getVmList()}.
     *
     * @pre $none
     * @post $none
     */
    void destroyAllVms();

    /**
     * Adds a listener object that will be notified every time when
     * the host updates the processing of all its {@link Vm VMs}.
     *
     * @param listener the OnUpdateProcessingListener to add
     * @return
     * @see #updateProcessing(double)
     */
    Host addOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> listener);

    /**
     * Removes a listener object from the OnUpdateProcessingListener List.
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
    double getUtilizationOfCpu();

    /**
     * Gets the current total utilization of CPU in MIPS,
     * considering the usage of all its PEs.
     *
     * @return
     */
    double getUtilizationOfCpuMips();

    /**
     * Gets the current utilization of bw (in absolute values).
     *
     * @return
     */
    long getUtilizationOfBw();

    /**
     * Gets the current utilization of memory (in absolute values).
     *
     * @return
     */
    long getUtilizationOfRam();

    /**
     * <p>Gets a map containing the host CPU utilization percentage history (between [0 and 1]),
     * based on its VM utilization history.
     * Each key is a time when the data collection was performed
     * and each value is a {@link DoubleSummaryStatistics}
     * from where some operations over the CPU utilization entries for every VM inside the Host
     * can be performed, such as counting, summing, averaging, etc.
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
     * <p>Gets a map containing the host CPU utilization percentage history (between [0 and 1]),
     * based on its VM utilization history.
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
     * and each value is a {@link DoubleSummaryStatistics} objects
     * that provides lots of useful methods to get
     * max, min, average, count and sum of utilization values.
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
