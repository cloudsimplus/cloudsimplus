/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.cloudlet;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletExecution;
import org.cloudbus.cloudsim.network.VmPacket;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.schedulers.cloudlet.network.CloudletTaskScheduler;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.listeners.CloudletResourceAllocationFailEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/**
 * An interface to be implemented by each class that provides a policy
 * of scheduling performed by a virtual machine to run its {@link Cloudlet Cloudlets}.
 * Each VM has to have its own instance of a CloudletScheduler.
 *
 * <p>It also implements the Null Object
 * Design Pattern in order to start avoiding {@link NullPointerException} when
 * using the {@link CloudletScheduler#NULL} object instead of attributing {@code null} to
 * {@link CloudletScheduler} variables.</p>
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface CloudletScheduler extends Serializable {
    Logger LOGGER = LoggerFactory.getLogger(CloudletScheduler.class.getSimpleName());

    /**
     * An attribute that implements the Null Object Design Pattern for {@link CloudletScheduler}
     * objects.
     */
    CloudletScheduler NULL = new CloudletSchedulerNull();

    /**
     * Sets a cloudlet as failed.
     *
     * @param cloudlet ID of the cloudlet to set as failed
     * @return the failed cloudlet or {@link Cloudlet#NULL} if not found
     */
    Cloudlet cloudletFail(Cloudlet cloudlet);

    /**
     * Cancels execution of a cloudlet.
     *
     * @param cloudlet the cloudlet being canceled
     * @return the canceled cloudlet or {@link Cloudlet#NULL} if not found
     */
    Cloudlet cloudletCancel(Cloudlet cloudlet);

    /**
     * Sets the status of a Cloudlet to {@link Cloudlet.Status#READY}
     * so that it can start executing as soon as possible.
     *
     * @param cloudlet the cloudlet to be started
     * @return $true if cloudlet was set to ready, $false otherwise
     */
    boolean cloudletReady(Cloudlet cloudlet);

    /**
     * Pauses execution of a cloudlet.
     *
     * @param cloudlet the cloudlet being paused
     * @return $true if cloudlet was paused, $false otherwise
     */
    boolean cloudletPause(Cloudlet cloudlet);

    /**
     * Resumes execution of a paused cloudlet.
     *
     * @param cloudlet the cloudlet being resumed
     * @return expected finish time of the cloudlet, 0.0 if queued or not found in the
     * paused list
     */
    double cloudletResume(Cloudlet cloudlet);

    /**
     * Receives a cloudlet to be executed in the VM managed by this scheduler.
     *
     * @param cloudlet the submitted cloudlet
     * @param fileTransferTime time required to move the required files from the SAN to the VM
     * @return expected finish time of this cloudlet (considering the time to transfer required
     * files from the Datacenter to the Vm), or 0 if it is in a waiting queue
     */
    double cloudletSubmit(Cloudlet cloudlet, double fileTransferTime);

    /**
     * Receives an cloudlet to be executed in the VM managed by this scheduler.
     *
     * @param cloudlet the submitted cloudlet
     * @return expected finish time of this cloudlet (considering the time to transfer required
     * files from the Datacenter to the Vm), or 0 if it is in a waiting queue
     */
    double cloudletSubmit(Cloudlet cloudlet);

    /**
     * Gets a <b>read-only</b> List of cloudlets being executed on the VM.
     *
     * @return the cloudlet execution list
     */
    List<CloudletExecution> getCloudletExecList();

    /**
     * Gets a <b>read-only</b> List of cloudlet waiting to be executed on the VM.
     *
     * @return the cloudlet waiting list
     */
    List<CloudletExecution> getCloudletWaitingList();

    /**
     * Gets a <b>read-only</b> List of all cloudlets which are either <b>waiting</b> or <b>executing</b> on the VM.
     *
     * @return the list of waiting and executing cloudlets
     */
    List<Cloudlet> getCloudletList();

    /**
     * Gets a list of finished cloudlets.
     *
     * @return the cloudlet finished list
     */
    List<CloudletExecution> getCloudletFinishedList();

    /**
     * Checks if there <b>aren't</b> cloudlets <b>waiting</b> or <b>executing</b> inside the Vm.
     *
     * @return true if there aren't <b>waiting</b> or <b>executing</b> Cloudlets, false otherwise.
     */
    boolean isEmpty();

    /**
     * Releases a given number of PEs from a VM.
     *
     * @param pesToRemove number of PEs to deallocate
     */
    void deallocatePesFromVm(int pesToRemove);

    /**
     /**
     * Gets the current utilization percentage of Bandwidth that the running Cloudlets are requesting (in scale from 0 to 1).
     *
     * @return the BW utilization percentage from 0 to 1 (where 1 is 100%)
     */
    double getCurrentRequestedBwPercentUtilization();

    /**
     * Gets the current utilization percentage of RAM that the running Cloudlets are requesting (in scale from 0 to 1).
     *
     * @return the RAM utilization percentage from 0 to 1 (where 1 is 100%)
     */
    double getCurrentRequestedRamPercentUtilization();

    /**
     * Gets the previous time when the scheduler updated the processing of
     * cloudlets it is managing.
     *
     * @return the previous time
     */
    double getPreviousTime();

    /**
     * Gets total CPU utilization percentage of all cloudlets,
     * according to CPU UtilizationModel of each one (in scale from 0 to 1,
     * where 1 is 100%).
     *
     * @param time the time to get the current CPU utilization
     * @return the total CPU utilization percentage
     */
    double getRequestedCpuPercentUtilization(double time);

    /**
     * Informs if there is any cloudlet that finished to execute in the VM managed by this scheduler.
     *
     * @return $true if there is at least one finished cloudlet; $false otherwise
     */
    boolean hasFinishedCloudlets();

    /**
     * Gets the {@link CloudletTaskScheduler} that will be used by this CloudletScheduler to process
     * {@link VmPacket}s to be sent or received by the Vm that is assigned to the
     * current CloudletScheduler.
     *
     * @return the CloudletTaskScheduler for this CloudletScheduler or {@link CloudletTaskScheduler#NULL} if this scheduler
     * will not deal with packets transmission.
     */
    CloudletTaskScheduler getTaskScheduler();

    /**
     * Sets the {@link CloudletTaskScheduler} that will be used by this CloudletScheduler to process
     * {@link VmPacket}s to be sent or received by the Vm that is assigned to the
     * current CloudletScheduler. The Vm from the CloudletScheduler is also set to the CloudletTaskScheduler.
     *
     * <p><b>This attribute usually doesn't need to be set manually. See the note at the {@link CloudletTaskScheduler} interface for more details.</b></p>
     *
     * @param taskScheduler the CloudletTaskScheduler to set for this CloudletScheduler or {@link CloudletTaskScheduler#NULL} if this scheduler
     * will not deal with packets transmission.
     */
    void setTaskScheduler(CloudletTaskScheduler taskScheduler);

    /**
     * Checks if there is a {@link CloudletTaskScheduler} assigned to this CloudletScheduler
     * in order to enable tasks execution and dispatching packets from and to the Vm of this CloudletScheduler.
     * @return
     */
    boolean isThereTaskScheduler();

    /**
     * Updates the processing of cloudlets inside the Vm running under management of this scheduler.
     *
     * @param currentTime current simulation time
     * @param mipsShare list with MIPS share of each Pe available to the scheduler
     * @return the predicted completion time of the earliest finishing cloudlet
     * (which is a relative delay from the current simulation time),
     * or {@link Double#MAX_VALUE} if there is no next Cloudlet to execute
     */
    double updateProcessing(double currentTime, List<Double> mipsShare);

    /**
     * Gets the Vm that uses the scheduler.
     * @return
     */
    Vm getVm();

    /**
     * Sets the Vm that will use the scheduler.
     * It is not required to manually set a Vm for the scheduler,
     * since a {@link Vm} sets itself to the scheduler when the scheduler
     * is assigned to the Vm.
     *
     * @param vm the Vm to set
     * @throws IllegalArgumentException when the scheduler already is assigned to another Vm, since
     * each Vm must have its own scheduler
     * @throws NullPointerException when the vm parameter is null
     */
    void setVm(Vm vm) ;

    /**
     * Gets the number of currently used {@link Pe}'s.
     * @return
     */
    long getUsedPes();

    /**
     * Gets the number of PEs currently not being used.
     * @return
     */
    long getFreePes();

    /**
     * Adds a Cloudlet to the list of finished Cloudlets that have been returned to its
     * {@link DatacenterBroker}.
     * @param cloudlet the Cloudlet to be added
     */
	void addCloudletToReturnedList(Cloudlet cloudlet);

    /**
     * Clears the internal state of the scheduler
     */
    void clear();

    /**
     * Adds a listener object that will be notified every time
     * a {@link CloudletScheduler} <b>is not able to allocated the amount of resource a {@link Cloudlet}
     * is requesting due to lack of available capacity.
     *
     * @param listener the Listener to add
     * @return
     */
    CloudletScheduler addOnCloudletResourceAllocationFail(EventListener<CloudletResourceAllocationFailEventInfo> listener);

    /**
     * Removes a Listener object from the registered List.
     * @param listener the Listener to remove
     * @return true if the Listener was removed, false otherwise
     */
    boolean removeOnCloudletResourceAllocationFail(EventListener<CloudletResourceAllocationFailEventInfo> listener);
}
