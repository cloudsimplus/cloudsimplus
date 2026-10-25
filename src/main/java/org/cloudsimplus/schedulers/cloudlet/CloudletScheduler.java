/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.schedulers.cloudlet;

import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletExecution;
import org.cloudsimplus.listeners.CloudletResourceShortageEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.network.VmPacket;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.schedulers.cloudlet.network.CloudletTaskScheduler;
import org.cloudsimplus.utilizationmodels.UtilizationModel;
import org.cloudsimplus.vms.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/// An interface to be implemented by each class that provides a policy
/// of scheduling performed by a [Vm] to run its [Cloudlet]s.
/// Each VM must have its own instance of a CloudletScheduler.
///
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.0
public sealed interface CloudletScheduler
    extends Serializable permits CloudletSchedulerAbstract, CloudletSchedulerNull
{
    Logger LOGGER = LoggerFactory.getLogger(CloudletScheduler.class.getSimpleName());

    /// An attribute that implements the Null Object Design Pattern for [CloudletScheduler] objects.
    CloudletScheduler NULL = new CloudletSchedulerNull();

    /// Sets a cloudlet as failed.
    ///
    /// @param cloudlet cloudlet to set as failed
    /// @return the failed cloudlet or [Cloudlet#NULL] if not found
    Cloudlet cloudletFail(Cloudlet cloudlet);

    /// Cancels the execution of a cloudlet.
    ///
    /// @param cloudlet the cloudlet being canceled
    /// @return the canceled cloudlet or [Cloudlet#NULL] if not found
    Cloudlet cloudletCancel(Cloudlet cloudlet);

    /// Sets the status of a Cloudlet to [Cloudlet.Status#READY]
    /// so that it can start executing as soon as possible.
    ///
    /// @param cloudlet the cloudlet to be started
    /// @return true if cloudlet was set to ready, false otherwise
    boolean cloudletReady(Cloudlet cloudlet);

    /// Pauses the execution of a cloudlet.
    ///
    /// @param cloudlet the cloudlet being paused
    /// @return true if cloudlet was paused, false otherwise
    boolean cloudletPause(Cloudlet cloudlet);

    /// Resumes execution of a paused cloudlet.
    ///
    /// @param cloudlet the cloudlet being resumed
    /// @return expected finish time of the cloudlet, 0.0 if queued or not found in the
    /// paused list
    double cloudletResume(Cloudlet cloudlet);

    /// Receives a cloudlet to be executed in the VM managed by this scheduler.
    ///
    /// @param cloudlet the submitted cloudlet
    /// @param fileTransferTime time to move the required files from the Datacenter [SAN][org.cloudsimplus.resources.SanStorage] to the VM
    /// @return expected finish time of this cloudlet (considering the time to transfer required
    /// files from the Datacenter to the Vm), or 0 if it is in a waiting queue
    double cloudletSubmit(Cloudlet cloudlet, double fileTransferTime);

    /// Receives a cloudlet to be executed in the VM managed by this scheduler.
    ///
    /// @param cloudlet the submitted cloudlet
    /// @return expected finish time of this cloudlet (considering the time to transfer required
    /// files from the Datacenter to the Vm), or 0 if it is in a waiting queue
    double cloudletSubmit(Cloudlet cloudlet);

    /// Sets the previous time when the scheduler updated the processing of
    /// the cloudlets it is managing.
    ///
    /// @param previousTime the new previous time
    void setPreviousTime(double previousTime);

    /// @return a **read-only** List of cloudlets being executed on the VM.
    List<CloudletExecution> getCloudletExecList();

    /// Gets the list of all Cloudlets submitted for a VM so far.
    /// This can be used at the end of the simulation to know
    /// which Cloudlets have been sent to a VM.
    ///
    /// **NOTE: The history in this List is just kept
    /// if [#enableCloudletSubmittedList()] is called.**
    ///
    /// @param <T> the class of Cloudlets inside the list
    /// @return the list of all submitted Cloudlets
    <T extends Cloudlet> List<T> getCloudletSubmittedList();

    /// {@return true or false if the list of all Cloudlets submitted so far is enabled}
    /// That indicates if the scheduler keeps a history of each submitted Cloudlet.
    /// @see #getCloudletSubmittedList()
    /// @see #enableCloudletSubmittedList()
    boolean isCloudletSubmittedListEnabled();

    /// Enables the history of all Cloudlets submitted so far.
    /// @see #getCloudletSubmittedList()
    /// @see #isCloudletSubmittedListEnabled()
    CloudletScheduler enableCloudletSubmittedList();

    /// @return a **read-only** List of cloudlet waiting to be executed on the VM.
    List<CloudletExecution> getCloudletWaitingList();

    /// @return a **read-only** List of all cloudlets that are either **waiting** or **executing** on the VM.
    List<Cloudlet> getCloudletList();

    /// @return a list of finished cloudlets.
    List<CloudletExecution> getCloudletFinishedList();

    /// Checks if there **aren't** cloudlets **waiting** or **executing** inside the Vm.
    ///
    /// @return true if the scheduler has no cloudlets to execute or waiting to be executed; false otherwise
    boolean isEmpty();

     /// Gets the current utilization percentage of Bandwidth that the running Cloudlets are requesting (in scale from 0 to 1).
     /// @return the BW utilization percentage from 0 to 1 (where 1 is 100%)
    double getCurrentRequestedBwPercentUtilization();

    /// Gets the current utilization percentage of RAM that the running Cloudlets are requesting (in scale from 0 to 1).
    /// @return the RAM utilization percentage from 0 to 1 (where 1 is 100%)
    double getCurrentRequestedRamPercentUtilization();

    /// @return the previous time when the scheduler updated the processing of cloudlets it is managing.
    double getPreviousTime();

    /// Gets total CPU percentage requested (from MIPS capacity) from all cloudlets,
    /// according to CPU [UtilizationModel] of each Cloudlet.
    ///
    /// @param time the time to get the current CPU utilization
    /// @return the total CPU percentage requested (in scale from 0 to 1, where 1 is 100%)
    /// @see #getRequestedCpuPercent()
    double getRequestedCpuPercent(double time);

    /// Gets total CPU percentage requested (from MIPS capacity) from all cloudlets
    /// at the current simulation time, according to CPU [UtilizationModel] of each Cloudlet.
    /// It may be higher than the percentage used (allocated) due to lack of capacity.
    ///
    /// @return the total CPU percentage requested (in scale from 0 to 1, where 1 is 100%)
    /// @see #getRequestedCpuPercent(double)
    default double getRequestedCpuPercent() {
        return getRequestedCpuPercent(getVm().getSimulation().clock());
    }

    /// Gets total CPU utilization percentage allocated (from MIPS capacity) to all cloudlets,
    /// according to CPU [UtilizationModel] of each Cloudlet.
    ///
    /// @param time the time to get the current CPU utilization
    /// @return the total CPU utilization percentage allocated (in scale from 0 to 1, where 1 is 100%).
    double getCpuPercentUtilization(double time);

    /// Informs if there is any cloudlet that finished executing in the VM managed by this scheduler.
    /// @return true if there is at least one finished cloudlet; false otherwise
    boolean hasFinishedCloudlets();

    /// Gets the [CloudletTaskScheduler] to process cloudlet tasks namely
    /// (i) sending or receiving [VmPacket]s by the Vm assigned to the current CloudletScheduler,
    /// or (ii) scheduling execution tasks.
    ///
    /// @return the CloudletTaskScheduler for this CloudletScheduler,
    /// or [CloudletTaskScheduler#NULL] if this scheduler will not deal with packets' transmission.
    CloudletTaskScheduler getTaskScheduler();

    /// Sets the [CloudletTaskScheduler] to process cloudlet tasks namely
    /// (i) sending or receiving [VmPacket]s by the Vm assigned to the current CloudletScheduler,
    /// or (ii) scheduling execution tasks.
    /// The Vm from the CloudletScheduler is also set to the CloudletTaskScheduler.
    ///
    /// **This attribute usually doesn't need to be set manually. See the note at the [CloudletTaskScheduler] interface for more details.**
    ///
    /// @param taskScheduler the CloudletTaskScheduler to set for this CloudletScheduler,
    /// or [CloudletTaskScheduler#NULL] if this scheduler will not deal with packets' transmission.
    void setTaskScheduler(CloudletTaskScheduler taskScheduler);

    /// Checks if there is a [CloudletTaskScheduler] assigned to this CloudletScheduler
    /// to enable tasks execution and dispatching packets from and to the Vm of this CloudletScheduler.
    /// @return true if a CloudletTaskScheduler is assigned to this CloudletScheduler; false otherwise
    boolean isThereTaskScheduler();

    /// Updates the processing of cloudlets inside the Vm running under management of this scheduler.
    ///
    /// @param currentTime current simulation time
    /// @param mipsShare MIPS share of each core available to the scheduler
    /// @return the next time to update cloudlets processing
    /// (which is a relative delay from the current simulation time),
    /// or [Double#MAX_VALUE] if there is no next Cloudlet to execute
    double updateProcessing(double currentTime, CoreCapacity mipsShare);

    /// @return the Vm that uses this scheduler.
    Vm getVm();

    /// Sets the Vm that will use this scheduler.
    /// It is not required to manually set a Vm for the scheduler,
    /// since a [Vm] sets itself to the scheduler when the scheduler
    /// is assigned to the Vm.
    ///
    /// @param vm the Vm to set
    /// @throws IllegalArgumentException when the scheduler is already assigned to another Vm,
    /// since each Vm must have its own scheduler
    /// @throws NullPointerException when the vm parameter is null
    void setVm(Vm vm) ;

    /// Adds a Cloudlet to the list of finished Cloudlets that have been returned to its
    /// [DatacenterBroker].
    /// @param cloudlet the Cloudlet to be added
	void addCloudletToReturnedList(Cloudlet cloudlet);

    /// Clears the internal state of the scheduler
    void clear();

    /// Adds a listener object that will be notified every time
    /// a [CloudletScheduler] is not able to allocate the amount of resource a [Cloudlet]
    /// is requesting, due to lack of available capacity.
    ///
    /// @param listener the Listener to add
    /// @return this scheduler
    CloudletScheduler addOnCloudletResourceShortage(EventListener<CloudletResourceShortageEventInfo> listener);

    /// Removes a Listener object from the registered List.
    /// @param listener the Listener to remove
    /// @return true if the Listener was removed, false otherwise
    boolean removeOnCloudletResourceShortage(EventListener<CloudletResourceShortageEventInfo> listener);
}
