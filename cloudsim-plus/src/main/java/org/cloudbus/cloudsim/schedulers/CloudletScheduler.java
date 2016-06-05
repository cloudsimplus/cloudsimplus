package org.cloudbus.cloudsim.schedulers;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.ResCloudlet;

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
    void cloudletFinish(ResCloudlet rcl);

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
     * @return expected finish time of the cloudlet, 0.0 if queued
     * @pre $none
     * @post $none
     */
    double cloudletResume(int clId);

    /**
     * Receives an cloudlet to be executed in the VM managed by this scheduler.
     *
     * @param cl the submitted cloudlet
     * @param fileTransferTime time required to move the required files from the SAN to the VM
     * @return expected finish time of this cloudlet, or 0 if it is in a waiting queue
     * @pre cl != null
     * @post $none
     */
    double cloudletSubmit(Cloudlet cl, double fileTransferTime);

    /**
     * Receives an cloudlet to be executed in the VM managed by this scheduler.
     *
     * @param cl the submited cloudlet
     * @return expected finish time of this cloudlet, or 0 if it is in a waiting queue
     * @pre cl != null
     * @post $none
     */
    double cloudletSubmit(Cloudlet cl);

    /**
     * Gets the list of cloudlets being executed on the VM.
     *
     * @param <T> the generic type
     * @return the cloudlet exec list
     */
    @SuppressWarnings(value = "unchecked")
    <T extends ResCloudlet> List<T> getCloudletExecList();

    /**
     * Gets the list of failed cloudlets.
     *
     * @param <T> the generic type
     * @return the cloudlet failed list.
     */
    @SuppressWarnings(value = "unchecked")
    <T extends ResCloudlet> List<T> getCloudletFailedList();

    /**
     * Gets the list of finished cloudlets.
     *
     * @param <T> the generic type
     * @return the cloudlet finished list
     */
    @SuppressWarnings(value = "unchecked")
    <T extends ResCloudlet> List<T> getCloudletFinishedList();

    /**
     * Gets the list of paused cloudlets.
     *
     * @param <T> the generic type
     * @return the cloudlet paused list
     */
    @SuppressWarnings(value = "unchecked")
    <T extends ResCloudlet> List<T> getCloudletPausedList();

    /**
     * Gets the status of a cloudlet.
     *
     * @param clId ID of the cloudlet
     * @return status of the cloudlet, -1 if cloudlet not found
     * @pre $none
     * @post $none
     */
    int getCloudletStatus(int clId);

    /**
     * Gets the list of cloudlet waiting to be executed on the VM.
     *
     * @param <T> the generic type
     * @return the cloudlet waiting list
     */
    @SuppressWarnings(value = "unchecked")
    <T extends ResCloudlet> List<T> getCloudletWaitingList();

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
     * Returns the next cloudlet in the finished list.
     *
     * @return a finished cloudlet or $null if the respective list is empty
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
    double getTotalCurrentAllocatedMipsForCloudlet(ResCloudlet rcl, double time);

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
    double getTotalCurrentAvailableMipsForCloudlet(ResCloudlet rcl, List<Double> mipsShare);

    /**
     * Gets the total current requested mips for a given cloudlet.
     *
     * @param rcl the rcl
     * @param time the time
     * @return the total current requested mips for the given cloudlet
     */
    double getTotalCurrentRequestedMipsForCloudlet(ResCloudlet rcl, double time);

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
     * Returns one cloudlet to migrate to another vm.
     *
     * @return one running cloudlet
     * @pre $none
     * @post $none
     */
    Cloudlet migrateCloudlet();

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
     * or 0 if there is no next events
     * @pre currentTime >= 0
     * @post $none
     */
    double updateVmProcessing(double currentTime, List<Double> mipsShare);
    
    /**
     * A property that implements the Null Object Design Pattern for {@link CloudletScheduler}
     * objects.
     */
    CloudletScheduler NULL = new CloudletScheduler() {
        @Override public Cloudlet cloudletCancel(int clId) { return Cloudlet.NULL; }
        @Override public void cloudletFinish(ResCloudlet rcl) {}
        @Override public boolean cloudletPause(int clId) { return false; }
        @Override public double cloudletResume(int clId) { return 0.0; }
        @Override public double cloudletSubmit(Cloudlet cl, double fileTransferTime){ return 0.0; }
        @Override public double cloudletSubmit(Cloudlet cl) { return 0.0; }
        @Override public <T extends ResCloudlet> List<T> getCloudletExecList() { return Collections.emptyList(); }
        @Override public <T extends ResCloudlet> List<T> getCloudletFailedList() { return Collections.emptyList(); }
        @Override public <T extends ResCloudlet> List<T> getCloudletFinishedList() { return Collections.emptyList(); }
        @Override public <T extends ResCloudlet> List<T> getCloudletPausedList() { return Collections.emptyList(); }
        @Override public int getCloudletStatus(int clId) { return 0; }
        @Override public <T extends ResCloudlet> List<T> getCloudletWaitingList() { return Collections.emptyList(); }
        @Override public List<Double> getCurrentMipsShare() { return Collections.emptyList(); }
        @Override public List<Double> getCurrentRequestedMips() { return Collections.emptyList(); }
        @Override public double getCurrentRequestedUtilizationOfBw() { return 0.0; }
        @Override public double getCurrentRequestedUtilizationOfRam() { return 0.0; }
        @Override public Cloudlet getNextFinishedCloudlet() { return Cloudlet.NULL; }
        @Override public double getPreviousTime() { return 0.0; }
        @Override public double getTotalCurrentAllocatedMipsForCloudlet(ResCloudlet rcl, double time) { return 0.0; }
        @Override public double getTotalCurrentAvailableMipsForCloudlet(ResCloudlet rcl, List<Double> mipsShare) { return 0.0; }
        @Override public double getTotalCurrentRequestedMipsForCloudlet(ResCloudlet rcl, double time) { return 0.0; }
        @Override public double getTotalUtilizationOfCpu(double time) { return 0.0; }
        @Override public boolean hasFinishedCloudlets() { return false; }
        @Override public Cloudlet migrateCloudlet() { return Cloudlet.NULL; }
        @Override public int runningCloudletsNumber() { return 0; }
        @Override public double updateVmProcessing(double currentTime, List<Double> mipsShare) { return 0.0; }
    };
}
