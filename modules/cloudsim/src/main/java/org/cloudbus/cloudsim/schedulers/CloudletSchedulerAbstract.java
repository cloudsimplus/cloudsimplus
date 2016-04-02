/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.ResCloudlet;

/**
 * Implements the basic features of a {@link CloudletScheduler}, representing
 * the policy of scheduling performed by a virtual machine to run its
 * {@link Cloudlet Cloudlets}. So, classes extending this must execute
 * Cloudlets. The interface for cloudlet management is also implemented in this
 * class. Each VM has to have its own instance of a CloudletScheduler.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public abstract class CloudletSchedulerAbstract implements CloudletScheduler {

    /**
     * @see #getPreviousTime()
     */
    private double previousTime;

    /**
     * @see #getCurrentMipsShare()
     */
    private List<Double> currentMipsShare;

    /**
     * @see #getCloudletWaitingList()
     */
    protected List<? extends ResCloudlet> cloudletWaitingList;

    /**
     * @see #getCloudletExecList()
     */
    protected List<? extends ResCloudlet> cloudletExecList;

    /**
     * @see #getCloudletPausedList()
     */
    protected List<? extends ResCloudlet> cloudletPausedList;

    /**
     * @see #getCloudletFinishedList()
     */
    protected List<? extends ResCloudlet> cloudletFinishedList;

    /**
     * @see #getCloudletFailedList()
     */
    protected List<? extends ResCloudlet> cloudletFailedList;

    /**
     * Creates a new CloudletScheduler object. A CloudletScheduler must be
     * created before starting the actual simulation.
     *
     * @pre $none
     * @post $none
     */
    public CloudletSchedulerAbstract() {
        setPreviousTime(0.0);
        cloudletWaitingList = new ArrayList<>();
        cloudletExecList = new ArrayList<>();
        cloudletPausedList = new ArrayList<>();
        cloudletFinishedList = new ArrayList<>();
        cloudletFailedList = new ArrayList<>();
    }

    /**
     * Gets the previous time when the scheduler updated the processing of
     * cloudlets it is managing.
     *
     * @return the previous time
     */
    @Override
    public double getPreviousTime() {
        return previousTime;
    }

    /**
     * Sets the previous time when the scheduler updated the processing of
     * cloudlets it is managing.
     *
     * @param previousTime the new previous time
     */
    protected final void setPreviousTime(double previousTime) {
        this.previousTime = previousTime;
    }

    /**
     * Sets the list of current mips share available for the VM using the
     * scheduler.
     *
     * @param currentMipsShare the new current mips share
     * @see #getCurrentMipsShare()
     */
    protected void setCurrentMipsShare(List<Double> currentMipsShare) {
        this.currentMipsShare = currentMipsShare;
    }

    /**
     * Gets the list of current mips capacity from the VM that will be made
     * available to the scheduler. This mips share will be allocated to
     * Cloudlets as requested.
     *
     * @return the current mips share list, where each item represents the MIPS
     * capacity of a {@link Pe}. that is available to the scheduler.
     *
     */
    @Override
    public List<Double> getCurrentMipsShare() {
        return currentMipsShare;
    }

    /**
     * Gets the list of cloudlet waiting to be executed on the VM.
     *
     * @param <T> the generic type
     * @return the cloudlet waiting list
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends ResCloudlet> List<T> getCloudletWaitingList() {
        return (List<T>) cloudletWaitingList;
    }

    /**
     * Sets the list of cloudlet waiting to be executed on the VM.
     *
     * @param <T> the generic type
     * @param cloudletWaitingList the cloudlet waiting list
     */
    protected <T extends ResCloudlet> void setCloudletWaitingList(List<T> cloudletWaitingList) {
        this.cloudletWaitingList = cloudletWaitingList;
    }

    /**
     * Gets the list of cloudlets being executed on the VM.
     *
     * @param <T> the generic type
     * @return the cloudlet exec list
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends ResCloudlet> List<T> getCloudletExecList() {
        return (List<T>) cloudletExecList;
    }

    /**
     * Sets the list of cloudlets being executed on the VM.
     *
     * @param <T> the generic type
     * @param cloudletExecList the new cloudlet exec list
     */
    protected <T extends ResCloudlet> void setCloudletExecList(List<T> cloudletExecList) {
        this.cloudletExecList = cloudletExecList;
    }

    /**
     * Gets the list of paused cloudlets.
     *
     * @param <T> the generic type
     * @return the cloudlet paused list
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends ResCloudlet> List<T> getCloudletPausedList() {
        return (List<T>) cloudletPausedList;
    }

    /**
     * Sets the list of paused cloudlets.
     *
     * @param <T> the generic type
     * @param cloudletPausedList the new cloudlet paused list
     */
    protected <T extends ResCloudlet> void setCloudletPausedList(List<T> cloudletPausedList) {
        this.cloudletPausedList = cloudletPausedList;
    }

    /**
     * Gets the list of finished cloudlets.
     *
     * @param <T> the generic type
     * @return the cloudlet finished list
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends ResCloudlet> List<T> getCloudletFinishedList() {
        return (List<T>) cloudletFinishedList;
    }

    /**
     * Sets the list of finished cloudlets.
     *
     * @param <T> the generic type
     * @param cloudletFinishedList the new cloudlet finished list
     */
    protected <T extends ResCloudlet> void setCloudletFinishedList(List<T> cloudletFinishedList) {
        this.cloudletFinishedList = cloudletFinishedList;
    }

    /**
     * Gets the list of failed cloudlets.
     *
     * @param <T> the generic type
     * @return the cloudlet failed list.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends ResCloudlet> List<T> getCloudletFailedList() {
        return (List<T>) cloudletFailedList;
    }

    /**
     * Sets the list of failed cloudlets.
     *
     * @param <T> the generic type
     * @param cloudletFailedList the new cloudlet failed list.
     */
    protected <T extends ResCloudlet> void setCloudletFailedList(List<T> cloudletFailedList) {
        this.cloudletFailedList = cloudletFailedList;
    }
}
