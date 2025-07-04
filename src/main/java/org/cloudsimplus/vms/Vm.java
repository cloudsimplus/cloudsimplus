/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.vms;

import org.cloudsimplus.autoscaling.HorizontalVmScaling;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.*;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.TimeZoned;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.VmDatacenterEventInfo;
import org.cloudsimplus.listeners.VmHostEventInfo;
import org.cloudsimplus.resources.*;
import org.cloudsimplus.schedulers.MipsShare;
import org.cloudsimplus.schedulers.cloudlet.CloudletScheduler;
import org.cloudsimplus.utilizationmodels.BootModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Predicate;

/**
 * An interface to be implemented by each class that provides basic
 * features for Virtual Machines (VMs).
 * The interface implements the Null Object
 * Design Pattern in order to start avoiding {@link NullPointerException} when
 * using the {@link Vm#NULL} object instead of attributing {@code null} to
 * {@link Vm} variables.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public sealed interface Vm
    extends Machine<Resource>, UniquelyIdentifiable, Comparable<Vm>, CustomerEntity, TimeZoned, ResourceStatsComputer<VmResourceStats>
    permits VmAbstract, VmNull
{
    Logger LOGGER = LoggerFactory.getLogger(Vm.class.getSimpleName());

    /**
     * An attribute that implements the Null Object Design Pattern for {@link Vm}
     * objects.
     */
    Vm NULL = new VmNull();

    /**
     * Gets the Vm description, an optional text
     * which can be used to provide details about this VM.
     * @return
     */
    String getDescription();

    /**
     * Sets the Vm description, an optional text
     * which can be used to provide details about this VM.
     *
     * @param description the Vm description to set
     */
    Vm setDescription(String description);

    /**
     * Gets the {@link VmGroup group} this Vm belongs to
     * @return the {@link VmGroup} or NULL if the VM doesn't belong to a group
     */
    VmGroup getGroup();

    /**
     * Adds a VM state history entry.
     *
     * @param entry the data about the state of the VM at given time
     */
    void addStateHistoryEntry(VmStateHistoryEntry entry);

    /**
     * Gets the Cloudlet scheduler the VM uses to schedule cloudlets
     * execution.
     *
     * @return the cloudlet scheduler
     */
    CloudletScheduler getCloudletScheduler();

    /**
     * Gets the current number of free PEs.
     *
     * @return the current free pes number
     */
    long getFreePesNumber();

    /**
     * Gets the expected free pes number before the VM starts executing.
     * This value is updated as cloudlets are assigned to VMs but not submitted to the
     * broker yet for running.
     *
     * @return the expected free pes number
     */
    long getExpectedFreePesNumber();

    /**
     * Gets the current requested bw.
     *
     * @return the current requested bw
     */
    long getCurrentRequestedBw();

    /**
     * Gets a  <b>copy</b> list of current requested MIPS of each virtual {@link Pe},
     * avoiding the original list to be changed.
     *
     * @return the current requested MIPS of each Pe
     */
    MipsShare getCurrentRequestedMips();

    /**
     * Gets the current requested ram (in Megabytes).
     * @return
     */
    long getCurrentRequestedRam();

    /**
     * Gets the current requested total MIPS. It is the sum of MIPS capacity
     * requested for every virtual {@link Pe}.
     *
     * @return the current requested total MIPS
     * @see #getCurrentRequestedMips()
     */
    double getTotalCpuMipsRequested();

    /**
     * Gets the {@link Host} where the Vm is or will be placed.
     * To know if the Vm was already created inside this Host,
     * call the {@link #isCreated()} method.
     *
     * @return the Host
     * @see #isCreated()
     */
    Host getHost();

    /**
     * Adds a listener object that will be notified when a {@link Host}
     * is allocated to the Vm, that is, when the Vm is placed into a
     * given Host. That happens when the VM is placed for the first
     * time into a Host or when it's migrated to another Host.
     *
     * @param listener the listener to add
     * @return
     * @see #addOnMigrationStartListener(EventListener)
     */
    Vm addOnHostAllocationListener(EventListener<VmHostEventInfo> listener);

    /**
     * Adds a listener object that will be notified when a VM starts migrating to a target {@link Host}.
     * When the listener is notified, it receives a {@link VmHostEventInfo} object
     * informing the target Host where the VM is being migrated.
     *
     * @param listener the listener to add
     * @return
     * @see #addOnHostAllocationListener(EventListener)
     */
    Vm addOnMigrationStartListener(EventListener<VmHostEventInfo> listener);

    /**
     * Adds a listener object that will be notified when a VM finishes migrating to a target {@link Host}.
     * When the listener is notified, it receives a {@link VmHostEventInfo} object
     * informing the target Host where the VM has just migrated.
     *
     * @param listener the listener to add
     * @return
     */
    Vm addOnMigrationFinishListener(EventListener<VmHostEventInfo> listener);

    /**
     * Adds a listener object that will be notified when the Vm is moved/removed from a {@link Host}.
     *
     * @param listener the listener to add
     * @return
     */
    Vm addOnHostDeallocationListener(EventListener<VmHostEventInfo> listener);

    /**
     * Adds a listener object that will be notified when the Vm fail in
     * being placed, due to lack of a {@link Host} with enough resources in a specific {@link Datacenter}.
     * <p>The {@link DatacenterBroker} is accountable for receiving the notification from the
     * Datacenter and notifying the Listeners.</p>
     *
     * @param listener the listener to add
     * @return
     * @see #updateProcessing(double, MipsShare)
     */
    Vm addOnCreationFailureListener(EventListener<VmDatacenterEventInfo> listener);

    /**
     * Adds a listener object that will be notified every time when
     * the processing of the Vm is updated in its {@link Host}.
     *
     * @param listener the listener to add
     * @return
     * @see #updateProcessing(double, MipsShare)
     */
    Vm addOnUpdateProcessingListener(EventListener<VmHostEventInfo> listener);

    /**
     * Notifies all registered listeners when a {@link Host} is allocated to the {@link Vm}.
     *
     * <p><b>This method is used just internally and must not be called directly.</b></p>
     */
    void notifyOnHostAllocationListeners();

    /**
     * Notifies all registered listeners when the {@link Vm} is moved/removed from a {@link Host}.
     *
     * <p><b>This method is used just internally and must not be called directly.</b></p>
     * @param deallocatedHost the {@link Host} the {@link Vm} was moved/removed from
     */
    void notifyOnHostDeallocationListeners(Host deallocatedHost);

    /**
     * Notifies all registered listeners when the Vm fail in
     * being placed for lack of a {@link Host} with enough resources in a specific {@link Datacenter}.
     *
     * <p><b>This method is used just internally and must not be called directly.</b></p>
     * @param failedDatacenter the Datacenter where the VM creation failed
     */
    void notifyOnCreationFailureListeners(Datacenter failedDatacenter);

    /**
     * Removes a listener from the onMigrationStartListener List.
     *
     * @param listener the listener to remove
     * @return true if the listener was found and removed, false otherwise
     */
    boolean removeOnMigrationStartListener(EventListener<VmHostEventInfo> listener);

    /**
     * Removes a listener from the onMigrationFinishListener List.
     *
     * @param listener the listener to remove
     * @return true if the listener was found and removed, false otherwise
     */
    boolean removeOnMigrationFinishListener(EventListener<VmHostEventInfo> listener);

    /**
     * Removes a listener from the onUpdateVmProcessingListener List.
     *
     * @param listener the listener to remove
     * @return true if the listener was found and removed, false otherwise
     */
    boolean removeOnUpdateProcessingListener(EventListener<VmHostEventInfo> listener);

    /**
     * Removes a listener from the onHostAllocationListener List.
     *
     * @param listener the listener to remove
     * @return true if the listener was found and removed, false otherwise
     */
    boolean removeOnHostAllocationListener(EventListener<VmHostEventInfo> listener);

    /**
     * Removes a listener from the onHostDeallocationListener List.
     *
     * @param listener the listener to remove
     * @return true if the listener was found and removed, false otherwise
     */
    boolean removeOnHostDeallocationListener(EventListener<VmHostEventInfo> listener);

    /**
     * Removes a listener from the onVmCreationFailureListener List.
     *
     * @param listener the listener to remove
     * @return true if the listener was found and removed, false otherwise
     */
    boolean removeOnCreationFailureListener(EventListener<VmDatacenterEventInfo> listener);

    /**
     * Gets bandwidth resource (in Megabits/s) assigned to the Vm,
     * allowing to check its capacity  and usage.
     *
     * @return bandwidth resource.
     */
    @Override
    Resource getBw();

    /**
     * Gets the RAM resource assigned to the Vm,
     * allowing to check its capacity (in Megabytes) and usage.
     *
     * @return the RAM resource
     */
    @Override
    Resource getRam();

    /**
     * Gets the storage device of the VM, which represents the VM image,
     * allowing to check its capacity (in Megabytes) and usage.
     *
     * @return the storage resource
     */
    @Override
    Resource getStorage();

    /**
     * Gets a <b>read-only</b> list with the history of requests and allocation of MIPS for this VM.
     * The VM state history is just collected and stored if the Host is storing such a data.
     *
     * @return the state history
     * @see Host#setStateHistoryEnabled(boolean)
     */
    List<VmStateHistoryEntry> getStateHistory();

    /**
     * Gets the percentage of CPU capacity (MIPS %) used by all Cloudlets
     * running on this VM at the given time.
     * It represents the actual percentage of MIPS allocated for that time.
     *
     * @param time the time to get the CPU utilization percentage
     * @return total CPU utilization percentage (between [0 and 1])
     */
    double getCpuPercentUtilization(double time);

    /**
     * Gets the current percentage of CPU capacity (MIPS %) used by all Cloudlets
     * running on this VM.
     * It represents the actual percentage of MIPS allocated.
     *
     * @return total CPU utilization percentage (between [0 and 1]) for the current time
     */
    double getCpuPercentUtilization();

    /**
     * Gets the percentage of CPU capacity (MIPS %) requested by all Cloudlets running on this
     * VM at the given time. It represents the percentage of MIPS requested for that time,
     * which may be higher than the percentage used (allocated)
     * due to lack of capacity.
     *
     * @param time the time to get the CPU requested percentage
     * @return the percentage (between [0 and 1]) of CPU capacity requested for the current time
     * @see #getCpuPercentRequested()
     * @see #getCpuPercentUtilization()
     */
    double getCpuPercentRequested(double time);

    /**
     * Gets the percentage of CPU capacity (MIPS %) requested by all Cloudlets running on this
     * VM at the current time. It represents the percentage of MIPS requested,
     * which may be higher than the percentage used (allocated)
     * due to lack of capacity.
     *
     * @return the percentage (between [0 and 1]) of CPU capacity requested
     * @see #getCpuPercentRequested(double)
     * @see #getCpuPercentUtilization(double)
     */
    double getCpuPercentRequested();

    /**
     * {@inheritDoc}
     * If you enable the statistics for the Host where the VM is placed,
     * that will automatically enable the statistics for every VM on that Host.
     */
    void enableUtilizationStats();

    /**
     * Computes the relative percentage of the RAM the VM is using from the Host's total Capacity
     * for the current simulation time.
     *
     * @return the relative VM RAM usage percent (from 0 to 1)
     */
    double getHostRamUtilization();

    /**
     * Computes the relative percentage of the Bandwidth the VM is using from the Host's total capacity
     * for the current simulation time.
     *
     * @return the relative VM BW usage percent (from 0 to 1)
     */
    double getHostBwUtilization();

    /**
     * Computes the current relative percentage of the CPU the VM is using from the Host's total MIPS capacity.
     * If the capacity is 1000 MIPS and the VM is using 250 MIPS, it's equivalent to 25%
     * of the Host's capacity.
     *
     * @return the relative VM CPU usage percent (from 0 to 1)
     * @see #getHostCpuUtilization(double)
     */
    default double getHostCpuUtilization() {
        return getHostCpuUtilization(getSimulation().clock());
    }

    /**
     * Computes the relative percentage of the CPU the VM is using from the Host's total MIPS capacity
     * for the current simulation time.
     * If the capacity is 1000 MIPS and the VM is using 250 MIPS, it's equivalent to 25%
     * of the Host's capacity.
     *
     * @param time the time to get the relative VM CPU utilization
     * @return the relative VM CPU usage percent (from 0 to 1)
     */
    double getHostCpuUtilization(double time);

    /**
     * Computes what would be the relative percentage of the CPU the VM is using from a PM's total MIPS capacity,
     * considering that the VM 's CPU load is at a given percentage.
     * @param vmCpuUtilizationPercent the VM's CPU utilization percentage for a given time
     * @return the relative VM CPU usage percent (from 0 to 1)
     */
    double getExpectedHostCpuUtilization(double vmCpuUtilizationPercent);

    /**
     * Gets the current total CPU MIPS utilization of all PEs from all cloudlets running on this VM.
     *
     * @return total CPU utilization in MIPS
     * @see #getCpuPercentUtilization(double)
     *
     */
    double getTotalCpuMipsUtilization();

    /**
     * Gets the total CPU MIPS utilization of all PEs from all cloudlets running on this VM at the
     * given time.
     *
     * @param time the time to get the utilization
     * @return total CPU utilization in MIPS
     * @see #getCpuPercentUtilization(double)
     *
     */
    double getTotalCpuMipsUtilization(double time);

    /**
     * Gets the Virtual Machine Monitor (VMM) that manages the VM.
     *
     * @return VMM
     */
    String getVmm();

    /**
     * Checks if the VM was created and placed inside the {@link #getHost() Host}.
     * If so, resources required by the Vm already were provisioned.
     *
     * @return true, if it was created inside the Host, false otherwise
     */
    boolean isCreated();

    /**
     * Checks if the VM has enough capacity to run a Cloudlet.
     *
     * @param cloudlet the candidate Cloudlet to run inside the VM
     * @return true if the VM can run the Cloudlet, false otherwise
     * TODO the Method is not being called anywhere to check if a VM has
     *      enough capacity to run a Cloudlet
     */
    boolean isSuitableForCloudlet(Cloudlet cloudlet);

    /**
     * Checks if the VM is in migration process or not,
     * that is, if it is migrating in or out of a Host.
     *
     * @return
     */
    boolean isInMigration();

    /**
     * Defines if the VM is in migration process or not.
     *
     * @param migrating true to indicate the VM is migrating into a Host, false otherwise
     */
    Vm setInMigration(boolean migrating);

    /**
     * Sets the bandwidth capacity (in Megabits/s)
     *
     * @param bwCapacity new BW capacity (in Megabits/s)
     * @return
     */
    Vm setBw(long bwCapacity);

    /**
     * Sets RAM capacity in Megabytes.
     *
     * @param ramCapacity new RAM capacity
     * @return
     */
    Vm setRam(long ramCapacity);

    /**
     * Sets the storage size (capacity) of the VM image in Megabytes.
     *
     * @param size new storage size
     * @return
     */
    Vm setSize(long size);

    /**
     * Updates the processing of cloudlets running on this VM.
     *
     * @param currentTime current simulation time
     * @param mipsShare list with MIPS share of each Pe available to the
     * scheduler
     * @return the predicted completion time of the earliest finishing cloudlet
     * (which is a relative delay from the current simulation time),
     * or {@link Double#MAX_VALUE} if there is no next Cloudlet to execute
     */
    double updateProcessing(double currentTime, MipsShare mipsShare);

    /**
     * Updates the processing of cloudlets running on this VM at the current simulation time.
     *
     * @param mipsShare list with MIPS share of each Pe available to the scheduler
     * @return the predicted completion time of the earliest finishing cloudlet
     * (which is a relative delay from the current simulation time),
     * or {@link Double#MAX_VALUE} if there is no next Cloudlet to execute
     */
    double updateProcessing(MipsShare mipsShare);

    /**
     * Sets the Cloudlet scheduler the Vm uses to schedule cloudlets execution.
     * It also sets the Vm itself to the given scheduler.
     *
     * @param cloudletScheduler the cloudlet scheduler to set
     * @return
     */
    Vm setCloudletScheduler(CloudletScheduler cloudletScheduler);

    /**
     * Sets the status of VM to FAILED.
     *
     * @param failed true to indicate that the VM is failed, false to indicate it is working
     */
    void setFailed(boolean failed);

    /**
     * Checks if the Vm is failed or not.
     * @return
     * @see #isWorking()
     */
    boolean isFailed();

    /**
     * Checks if the Vm is working or failed.
     * @return
     * @see #isFailed()
     */
    boolean isWorking();

    @Override
    default boolean isIdleEnough(final double time) {
        /*If the scheduling interval is too high, the VM processing may be updated only
        * after a long time period, which may give the wrong idea that the VM is idle,
        * even if there are running Cloudlets.
        * If the broker checks whether the VM is idle, before its processing is updated,
        * it may incorrectly destroy a with running Cloudlets.
         * To avoid that, we check if the VM has running cloudlets.
        * If the VM's lastBusyTime attribute was not set recently,
        * it may be because we have a large Datacenter's scheduling interval.*/
        return getCloudletScheduler().getCloudletExecList().isEmpty() && Machine.super.isIdleEnough(time);
    }

    /**
     * Gets a {@link HorizontalVmScaling} that will check if the Vm is overloaded,
     * based on some conditions defined by a {@link Predicate} given
     * to the HorizontalVmScaling, and then request the creation of new VMs
     * to horizontally scale the Vm.
     *
     * <p><b>If no HorizontalVmScaling is set, the {@link #getBroker() Broker} will not dynamically
     * create VMs to balance arrived Cloudlets.</b></p>
     *
     * @return
     */
    HorizontalVmScaling getHorizontalScaling();

    /**
     * Sets a {@link HorizontalVmScaling} that will check if the Vm is overloaded,
     * based on some conditions defined by a {@link Predicate} given
     * to the HorizontalVmScaling, and then request the creation of new VMs
     * to horizontally scale the Vm.
     *
     * @param horizontalScaling the HorizontalVmScaling to set
     * @return
     * @throws IllegalArgumentException if the given VmScaling is already linked to a Vm.
     *         Each VM must have its own HorizontalVmScaling object or none at all.
     */
    Vm setHorizontalScaling(HorizontalVmScaling horizontalScaling) throws IllegalArgumentException;

    /**
     * Sets a {@link VerticalVmScaling} that will check if the Vm's {@link Ram} is under or overloaded,
     * based on some conditions defined by {@link Predicate}s given to the VerticalVmScaling,
     * and then request the RAM up or down scaling.
     *
     * @param ramVerticalScaling the VerticalVmScaling to set
     * @return
     * @throws IllegalArgumentException if the given VmScaling is already linked to a Vm. Each VM must have
     * its own VerticalVmScaling objects or none at all.
     */
    Vm setRamVerticalScaling(VerticalVmScaling ramVerticalScaling) throws IllegalArgumentException;

    /**
     * Sets a {@link VerticalVmScaling} that will check if the Vm's {@link Bandwidth} is under or overloaded,
     * based on some conditions defined by {@link Predicate}s given to the VerticalVmScaling,
     * and then request the Bandwidth up or down scaling.
     *
     * @param bwVerticalScaling the VerticalVmScaling to set
     * @return
     * @throws IllegalArgumentException if the given VmScaling is already linked to a Vm.
     * Each VM must have its own VerticalVmScaling objects or none at all.
     */
    Vm setBwVerticalScaling(VerticalVmScaling bwVerticalScaling) throws IllegalArgumentException;

    /**
     * Sets a {@link VerticalVmScaling} that will check if the Vm's {@link Pe} is under or overloaded,
     * based on some conditions defined by {@link Predicate}s given to the VerticalVmScaling,
     * and then request the Pe up or down scaling.
     *
     * <p>The Pe scaling is performed by adding or removing PEs to/from the VM.
     * Added PEs will have the same MIPS than the already existing ones.</p>
     *
     * @param peVerticalScaling the VerticalVmScaling to set
     * @return
     * @throws IllegalArgumentException if the given VmScaling is already linked to a Vm.
     * Each VM must have its own VerticalVmScaling objects or none at all.
     */
    Vm setPeVerticalScaling(VerticalVmScaling peVerticalScaling) throws IllegalArgumentException;

    /**
     * Gets a {@link VerticalVmScaling} that will check if the Vm's RAM is overloaded,
     * based on some conditions defined by a {@link Predicate} given
     * to the VerticalVmScaling, and then request the RAM up scaling.
     * @return
     */
    VerticalVmScaling getRamVerticalScaling();

    /**
     * Gets a {@link VerticalVmScaling} that will check if the Vm's Bandwidth is overloaded,
     * based on some conditions defined by a {@link Predicate} given
     * to the VerticalVmScaling, and then request the BW up scaling.
     * @return
     */
    VerticalVmScaling getBwVerticalScaling();

    /**
     * Gets a {@link VerticalVmScaling} that will check if the Vm's {@link Pe} is overloaded,
     * based on some conditions defined by a {@link Predicate} given
     * to the VerticalVmScaling, and then request the RAM up scaling.
     * @return
     */
    VerticalVmScaling getPeVerticalScaling();

    /**
     * Gets the {@link Processor} of this VM. It is its Virtual CPU
     * which may be compounded of multiple {@link Pe}s.
     * @return
     */
    Processor getProcessor();

   /**
     * Gets the {@link DatacenterBroker} that represents the owner of this Vm.
     *
     * @return the broker or <b>{@link DatacenterBroker#NULL}</b> if a broker has not been set yet
     */
    @Override
    DatacenterBroker getBroker();

    /**
     * Sets a {@link DatacenterBroker} that represents the owner of this Vm.
     *
     * @param broker the {@link DatacenterBroker} to set
     */
    @Override
    CustomerEntity setBroker(DatacenterBroker broker);

    /**
     * Gets the total time (in seconds) the Vm spent executing.
     * It considers the entire VM execution even if in different Hosts
     * it has possibly migrated.
     *
     * @return the VM total execution time if the VM has stopped,
     *         the time executed so far if the VM is running yet,
     *         or 0 if it hasn't started.
     */
    @Override
    double getTotalExecutionTime();

    /**
     * {@inheritDoc}
     * That is the timezone in which the VM is expected to be placed
     * (if there is a {@link Datacenter} with enough capacity available at that timezone).
     *
     * <p>To know the actual timezone where the VM is placed,
     * check the {@link Datacenter#getTimeZone() timezone of the Datacenter
     * of the Host where this is VM placed}.</p>
     *
     * @return the expected timezone to place this VM
     */
    @Override
    double getTimeZone();

    /**
     * {@inheritDoc}
     * That is the timezone in which the VM is expected to be placed
     * (if there is a {@link Datacenter} with enough capacity available at that timezone).
     *
     * @param timeZone the new expected time zone offset
     * @return
     */
    @Override
    Vm setTimeZone(double timeZone);

    /**
     * Gets the Vm {@inheritDoc}.
     * @return {@inheritDoc}
     * @see #setLifeTime(double)
     */
    @Override
    double getLifeTime();

    /**
     * {@return the VM boot model} It defines how the VM uses resources such as RAM, CPU and BW during the boot process.
     * @see #setBootModel(BootModel)
     */
    BootModel getBootModel();

    /**
     * Sets a model which defines how the VM uses resources
     * such as RAM, CPU and BW during the boot process.
     *
     * <p>If a model is not set, no resources are used during the boot time,
     * which is just simulated if a {@link #setStartupDelay(double) startup delay} is set.</p>
     * @param model the boot model to set
     * @return this
     */
    Vm setBootModel(BootModel model);

    /**
     * Sets the Vm {@inheritDoc}.
     * <p>The Vm will finish execution as soon as possible, after the given lifeTime has passed,
     * since its {@link #getStartTime()} start time}.
     * </p>
     * <p><b>NOTE:</b> If the VM has a lifeTime set, and it's smaller than the Cloudlet lifeTime,
     * the VM lifeTime is used instead of the Cloudlet one.</p>
     *
     * @param lifeTime {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    Lifetimed setLifeTime(double lifeTime);

    /**
     * {@inheritDoc}
     * This is the time the VM takes to boot up.
     *
     * <p><b>INFO:</b> When setting this attribute, call {@link #setBootModel(BootModel)} first.</p>
     * @param delay {@inheritDoc}
     * @return {@inheritDoc}
     * @see #getStartupDelay()
     */
    @Override
    ExecDelayable setStartupDelay(double delay);

    /**
     * {@return true or false} whether the VM is booting up or not.
     */
    @Override
    default boolean isStartingUp(){
        return Machine.super.isStartingUp();
    }

    /**
     * Sends a request to the Datacenter to shut down (destroy) the VM.
     * @see #setBootModel(BootModel)
     * @see #setShutDownDelay(double)
     */
    @Override
    void shutdown();
}
