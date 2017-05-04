/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.cloudlet;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletExecutionInfo;
import org.cloudbus.cloudsim.network.VmPacket;
import org.cloudbus.cloudsim.schedulers.cloudlet.network.PacketScheduler;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.resources.Pe;

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

    /**
     * An attribute that implements the Null Object Design Pattern for {@link CloudletScheduler}
     * objects.
     */
    CloudletScheduler NULL = new CloudletSchedulerNull();

    /**
     * Cancels execution of a cloudlet.
     *
     * @param cloudletId ID of the cloudlet being canceled
     * @return the canceled cloudlet or {@link Cloudlet#NULL} if not found
     * @pre $none
     * @post $none
     */
    Cloudlet cloudletCancel(int cloudletId);

    /**
     * Processes a finished cloudlet.
     *
     * @param rcl finished cloudlet
     * @pre rcl != $null
     * @post $none
     */
    void cloudletFinish(CloudletExecutionInfo rcl);

    /**
     * Pauses execution of a cloudlet.
     *
     * @param cloudletId ID of the cloudlet being paused
     * @return $true if cloudlet paused, $false otherwise
     * @pre $none
     * @post $none
     */
    boolean cloudletPause(int cloudletId);

    /**
     * Resumes execution of a paused cloudlet.
     *
     * @param cloudletId ID of the cloudlet being resumed
     * @return expected finish time of the cloudlet, 0.0 if queued or not found in the
     * paused list
     * @pre $none
     * @post $none
     */
    double cloudletResume(int cloudletId);

    /**
     * Receives an cloudlet to be executed in the VM managed by this scheduler.
     *
     * @param cl the submitted cloudlet
     * @param fileTransferTime time required to move the required files from the SAN to the VM
     * @return expected finish time of this cloudlet (considering the time to transfer required
     * files from the Datacenter to the Vm), or 0 if it is in a waiting queue
     * @pre cl != null
     * @post $none
     */
    double cloudletSubmit(Cloudlet cl, double fileTransferTime);

    /**
     * Receives an cloudlet to be executed in the VM managed by this scheduler.
     *
     * @param cl the submited cloudlet
     * @return expected finish time of this cloudlet (considering the time to transfer required
     * files from the Datacenter to the Vm), or 0 if it is in a waiting queue
     * @pre cl != null
     * @post $none
     */
    double cloudletSubmit(Cloudlet cl);

    /**
     * Gets a <b>read-only</b> List of cloudlets being executed on the VM.
     *
     * @return the cloudlet execution list
     */
    List<CloudletExecutionInfo> getCloudletExecList();

    /**
     * Gets a <b>read-only</b> List of cloudlet waiting to be executed on the VM.
     *
     * @return the cloudlet waiting list
     */
    List<CloudletExecutionInfo> getCloudletWaitingList();
    
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
    List<CloudletExecutionInfo> getCloudletFinishedList();

    /**
     * Gets the status of a cloudlet with a given id.
     *
     * @param cloudletId ID of the cloudlet to get the status
     * @return status of the cloudlet if it was found, otherwise, returns -1
     * @pre $none
     * @post $none
     */
    int getCloudletStatus(int cloudletId);

    /**
     * Gets the list of current mips capacity from the VM that will be
     * made available to the scheduler. This mips share will be allocated
     * to Cloudlets as requested.
     *
     * @return the current mips share list, where each item represents
     * the MIPS capacity of a {@link Pe}. that is available to the scheduler.
     *
     */
    List<Double> getCurrentMipsShare();

    /**
     * Gets the current requested MIPS.
     *
     * @return the current requested MIPS
     */
    List<Double> getCurrentRequestedMips();
    
    /**
     * Releases a given number of PEs from a VM. 
     *
     * @param vm the vm to deallocate PEs from
     * @param pesToRemove number of PEs to deallocate
     */
    void deallocatePesFromVm(Vm vm, int pesToRemove);      

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
     * Gets the current allocated MIPS for cloudlet.
     *
     * @param rcl the rcl
     * @param time the time
     * @return the current allocated mips for cloudlet
     */
    double getAllocatedMipsForCloudlet(CloudletExecutionInfo rcl, double time);

    /**
     * Gets the total current mips that a Cloudlet can use for each PE
     * it requires. Thus, the value returned by the method indicates the
     * amount of MIPS each Cloudlet PE can use.
     *
     * @param rcl the rcl
     * @param mipsShare the mips share
     * @return the total current mips available for each Cloudlet PE
     */
    double getTotalCurrentAvailableMipsForCloudlet(CloudletExecutionInfo rcl, List<Double> mipsShare);

    /**
     * Gets the current requested MIPS for a given cloudlet.
     *
     * @param rcl the rcl
     * @param time the time
     * @return the current requested mips for the given cloudlet
     */
    double getRequestedMipsForCloudlet(CloudletExecutionInfo rcl, double time);

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
     * @pre $none
     * @post $none
     */
    boolean hasFinishedCloudlets();

    /**
     * Gets the {@link PacketScheduler} that will be used by this CloudletScheduler to process
     * {@link VmPacket}s to be sent or received by the Vm that is assigned to the
     * current CloudletScheduler.
     *
     * @return the PacketScheduler for this CloudletScheduler or {@link PacketScheduler#NULL} if this scheduler
     * will not deal with packets transmission.
     */
    PacketScheduler getPacketScheduler();

    /**
     * Sets the {@link PacketScheduler} that will be used by this CloudletScheduler to process
     * {@link VmPacket}s to be sent or received by the Vm that is assigned to the
     * current CloudletScheduler. The Vm from the CloudletScheduler is also set to the PacketScheduler.
     *
     * <p><b>This attribute usually doesn't need to be set manually. See the note at the {@link PacketScheduler} interface for more details.</b></p>
     *
     * @param packetScheduler the PacketScheduler to set for this CloudletScheduler or {@link PacketScheduler#NULL} if this scheduler
     * will not deal with packets transmission.
     */
    void setPacketScheduler(PacketScheduler packetScheduler);

    /**
     * Checks if there is a packet scheduler assigned to this CloudletScheduler
     * in order to enable dispatching packets from and to the Vm of this CloudletScheduler.
     * @return
     */
    boolean isTherePacketScheduler();

    /**
     * Returns one cloudlet to migrate to another Vm.
     * How the migrating cloudlet is select is defined by each
     * class implementing this interface.
     *
     * @return one running cloudlet
     * @pre $none
     * @post $none
     * @todo @author manoelcampos Despite there is this method, it is not being
     * used anywhere and Cloudlet migration is not in fact supported.
     * Actually, in a real scenario, application migration is a tough
     * issue, once it has to deal with configuration and data migration,
     * dependencies, etc. Further, I don't think it is a reasonable approach
     * to follow. Vm migration makes more sense because you deal it as a
     * black box, not having to be concerned with any internal data or
     * configurations. You just move the entire VM to another host.
     * There is the {@link CloudSimTags#CLOUDLET_MOVE} that is used in the
     * {@link DatacenterSimple} class, but the event
     * is not being sent anywhere. The CloudSim forum has 3 questions about
     * Cloudlet migration only. It shows that this features is not
     * highly required and in fact. Even for migration of parallel workloads
     * such as Map-Reduce, data has to be migrated with the application.
     */
    Cloudlet getCloudletToMigrate();

    /**
     * Returns the number of cloudlets running in the virtual machine.
     *
     * @return number of cloudlets running
     * @pre $none
     * @post $none
     */
    int runningCloudletsNumber();

    /**
     * Updates the processing of cloudlets inside the Vm running under management of this scheduler.
     *
     * @param currentTime current simulation time
     * @param mipsShare list with MIPS share of each Pe available to the scheduler
     * @return the predicted completion time of the earliest finishing cloudlet
     * (which is a relative delay from the current simulation time),
     * or {@link Double#MAX_VALUE} if there is no next Cloudlet to execute
     * @pre currentTime >= 0
     * @post $none
     */
    double updateVmProcessing(double currentTime, List<Double> mipsShare);

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
	 * Checks if a Cloudlet can be added to the execution list or not.
	 * Each CloudletScheduler can define a different policy to
	 * indicate if a Cloudlet can be added to the execution list
	 * or not at the moment this method is called.
	 *
	 * <p>For instance, time-shared implementations can put all
	 * Cloudlets in the execution list, once it uses a preemptive policy
	 * that shares the CPU time between all running Cloudlets,
	 * even there are more Cloudlets than the number of CPUs.
	 * That is, it might always add new Cloudlets to the execution list.
	 * </p>
	 *
	 * <p>On the other hand, space-shared schedulers do not share
	 * the same CPUs between different Cloudlets. In this type of
	 * scheduler, a CPU is only allocated to a Cloudlet when the previous
	 * Cloudlet finished its entire execution.
	 * That is, it might not always add new Cloudlets to the execution list.</p>
	 *
	 * @param cloudlet Cloudlet to check if it can be added to the execution list
	 * @return true if the Cloudlet can be added to the execution list, false otherwise
	 */
	boolean canAddCloudletToExecutionList(CloudletExecutionInfo cloudlet);

    /**
     * Gets a <b>read-only</b> list of Cloudlets that finished executing and were returned the their broker.
     * A Cloudlet is returned to to notify the broker about the end of its execution.
     * @return
     */
	Set<Cloudlet> getCloudletReturnedList();

    /**
     * Checks if a Cloudlet has finished and was returned to its {@link DatacenterBroker}.
     *
     * @param cloudlet the Cloudlet to be checked
     * @return true if the Cloudlet has finished and was returned to the broker, falser otherwise
     */
	boolean isCloudletReturned(Cloudlet cloudlet);

    /**
     * Adds a Cloudlet to the list of finished Cloudlets that have been returned to its
     * {@link DatacenterBroker}.
     * @param cloudlet the Cloudlet to be added
     */
	void addCloudletToReturnedList(Cloudlet cloudlet);
}
