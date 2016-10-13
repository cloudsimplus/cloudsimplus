package org.cloudbus.cloudsim.schedulers;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletExecutionInfo;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.network.datacenter.NetworkCloudletSpaceSharedScheduler;
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
 * @author Manoel Campos da Silva Filho
 */
public interface CloudletScheduler extends Serializable {

    /**
     * Cancels execution of a cloudlet.
     *
     * @param clId ID of the cloudlet being canceled
     * @return the canceled cloudlet, $null if not found
     * @pre $none
     * @post $none
     */
    Cloudlet cloudletCancel(int clId);

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
     * @param clId ID of the cloudlet being paused
     * @return $true if cloudlet paused, $false otherwise
     * @pre $none
     * @post $none
     */
    boolean cloudletPause(int clId);

    /**
     * Resumes execution of a paused cloudlet.
     *
     * @param clId ID of the cloudlet being resumed
     * @return expected finish time of the cloudlet, 0.0 if queued or not found in the
     * paused list
     * @pre $none
     * @post $none
     */
    double cloudletResume(int clId);

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
     * Gets the {@link Collection} of cloudlets being executed on the VM.
     * It is being used a more generic interface instead of {@link List}
     * to enable implementations to decide the best and more efficient data structure to
     * store the executing Cloudlets.
     *
     * @param <T> the generic type
     * @return the cloudlet exec collection
     */
    @SuppressWarnings(value = "unchecked")
    <T extends CloudletExecutionInfo> Collection<T> getCloudletExecList();

    /**
     * Gets the list of failed cloudlets.
     *
     * @param <T> the generic type
     * @return the cloudlet failed list.
     */
    @SuppressWarnings(value = "unchecked")
    <T extends CloudletExecutionInfo> List<T> getCloudletFailedList();

    /**
     * Gets the list of finished cloudlets.
     *
     * @param <T> the generic type
     * @return the cloudlet finished list
     */
    @SuppressWarnings(value = "unchecked")
    <T extends CloudletExecutionInfo> List<T> getCloudletFinishedList();

    /**
     * Gets the list of paused cloudlets.
     *
     * @param <T> the generic type
     * @return the cloudlet paused list
     */
    @SuppressWarnings(value = "unchecked")
    <T extends CloudletExecutionInfo> List<T> getCloudletPausedList();

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
     * Gets the list of cloudlet waiting to be executed on the VM.
     *
     * @param <T> the generic type
     * @return the cloudlet waiting list
     */
    @SuppressWarnings(value = "unchecked")
    <T extends CloudletExecutionInfo> List<T> getCloudletWaitingList();

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
     * Gets the current requested mips.
     *
     * @return the current mips
     */
    List<Double> getCurrentRequestedMips();

    /**
     * Gets the current requested percentage of bw
     * (in scale from 0 to 1, where 1 is 100%).
     *
     * @return the current requested bw percentage.
     */
    double getCurrentRequestedUtilizationOfBw();

    /**
     * Gets the current requested percentage of ram
     * (in scale from 0 to 1, where 1 is 100%).
     *
     * @return the current requested ram percentage.
     */
    double getCurrentRequestedUtilizationOfRam();

    /**
     * Removes the next cloudlet in the finished list and returns it.
     *
     * @return a finished cloudlet or {@link Cloudlet#NULL} if the respective list is empty
     * @pre $none
     * @post $none
     */
    Cloudlet getNextFinishedCloudlet();

    /**
     * Gets the previous time when the scheduler updated the processing of
     * cloudlets it is managing.
     *
     * @return the previous time
     */
    double getPreviousTime();

    /**
     * Gets the total current allocated mips for cloudlet.
     *
     * @param rcl the rcl
     * @param time the time
     * @return the total current allocated mips for cloudlet
     */
    double getTotalCurrentAllocatedMipsForCloudlet(CloudletExecutionInfo rcl, double time);

    /**
     * Gets the total current available mips for the Cloudlet.
     *
     * @param rcl the rcl
     * @param mipsShare the mips share
     * @return the total current mips
     * @todo In fact, this method is returning different data depending
     * of the subclass. It is expected that the way the method use to compute
     * the resulting value can be different in every subclass,
     * but is not supposed that each subclass returns a complete different
     * result for the same method of the superclass.
     * In some class such as {@link NetworkCloudletSpaceSharedScheduler},
     * the method returns the average MIPS for the available PEs,
     * in other classes such as {@link CloudletSchedulerDynamicWorkload} it returns
     * the MIPS' sum of all PEs.
     */
    double getTotalCurrentAvailableMipsForCloudlet(CloudletExecutionInfo rcl, List<Double> mipsShare);

    /**
     * Gets the total current requested mips for a given cloudlet.
     *
     * @param rcl the rcl
     * @param time the time
     * @return the total current requested mips for the given cloudlet
     */
    double getTotalCurrentRequestedMipsForCloudlet(CloudletExecutionInfo rcl, double time);

    /**
     * Gets total CPU utilization percentage of all cloudlets,
     * according to CPU UtilizationModel of each one (in scale from 0 to 1,
     * where 1 is 100%).
     *
     * @param time the time to get the current CPU utilization
     * @return the total CPU utilization percentage
     */
    double getTotalUtilizationOfCpu(double time);

    /**
     * Informs if there is any cloudlet that finished to execute in the VM managed by this scheduler.
     *
     * @return $true if there is at least one finished cloudlet; $false otherwise
     * @pre $none
     * @post $none
     */
    boolean hasFinishedCloudlets();

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
	boolean canAddCloudletToExecutionList(Cloudlet cloudlet);

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
     * {@link org.cloudbus.cloudsim.DatacenterSimple} class, but the event
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
     * Updates the processing of cloudlets running under management of this scheduler.
     *
     * @param currentTime current simulation time
     * @param mipsShare list with MIPS share of each Pe available to the scheduler
     * @return the predicted completion time of the earliest finishing cloudlet,
     * or {@link Double#MAX_VALUE} if there is no next events
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
     * @param vm
     */
    void setVm(Vm vm) ;

    /**
     * Updates the processing of a specific cloudlet of the Vm using this scheduler.
     * @param rcl The cloudlet to be its processing updated
     * @param currentTime current simulation time
     *
     */
    void updateCloudletProcessing(CloudletExecutionInfo rcl, double currentTime);

    /**
     * A property that implements the Null Object Design Pattern for {@link CloudletScheduler}
     * objects.
     */
    CloudletScheduler NULL = new CloudletScheduler() {
        @Override public Cloudlet cloudletCancel(int clId) { return Cloudlet.NULL; }
        @Override public void cloudletFinish(CloudletExecutionInfo rcl) {}
        @Override public boolean cloudletPause(int clId) { return false; }
        @Override public double cloudletResume(int clId) { return 0.0; }
        @Override public double cloudletSubmit(Cloudlet cl, double fileTransferTime){ return 0.0; }
        @Override public double cloudletSubmit(Cloudlet cl) { return 0.0; }
        @Override public <T extends CloudletExecutionInfo> Collection<T> getCloudletExecList() { return Collections.emptyList(); }
        @Override public <T extends CloudletExecutionInfo> List<T> getCloudletFailedList() { return Collections.emptyList(); }
        @Override public <T extends CloudletExecutionInfo> List<T> getCloudletFinishedList() { return Collections.emptyList(); }
        @Override public <T extends CloudletExecutionInfo> List<T> getCloudletPausedList() { return Collections.emptyList(); }
        @Override public int getCloudletStatus(int cloudletId) { return 0; }
        @Override public <T extends CloudletExecutionInfo> List<T> getCloudletWaitingList() { return Collections.emptyList(); }
        @Override public List<Double> getCurrentMipsShare() { return Collections.emptyList(); }
        @Override public List<Double> getCurrentRequestedMips() { return Collections.emptyList(); }
        @Override public double getCurrentRequestedUtilizationOfBw() { return 0.0; }
        @Override public double getCurrentRequestedUtilizationOfRam() { return 0.0; }
        @Override public Cloudlet getNextFinishedCloudlet() { return Cloudlet.NULL; }
        @Override public double getPreviousTime() { return 0.0; }
        @Override public double getTotalCurrentAllocatedMipsForCloudlet(CloudletExecutionInfo rcl, double time) { return 0.0; }
        @Override public double getTotalCurrentAvailableMipsForCloudlet(CloudletExecutionInfo rcl, List<Double> mipsShare) { return 0.0; }
        @Override public double getTotalCurrentRequestedMipsForCloudlet(CloudletExecutionInfo rcl, double time) { return 0.0; }
        @Override public double getTotalUtilizationOfCpu(double time) { return 0.0; }
        @Override public boolean hasFinishedCloudlets() { return false; }
	    @Override  public boolean canAddCloudletToExecutionList(Cloudlet cloudlet) { return false; }
	    @Override public Cloudlet getCloudletToMigrate() { return Cloudlet.NULL; }
        @Override public int runningCloudletsNumber() { return 0; }
        @Override public double updateVmProcessing(double currentTime, List<Double> mipsShare) { return 0.0; }
        @Override public Vm getVm() { return Vm.NULL; }
        @Override public void setVm(Vm vm) {}
        @Override public void updateCloudletProcessing(CloudletExecutionInfo rcl, double currentTime) {}
    };
}
