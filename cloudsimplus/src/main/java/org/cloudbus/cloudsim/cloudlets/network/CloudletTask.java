/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.cloudlets.network;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletExecution;
import org.cloudbus.cloudsim.core.Identifiable;

/**
 * An abstract class to be implemented by tasks that can be executed by a {@link NetworkCloudlet}.
 *
 * <p>Please refer to following publication for more details:
 * <ul>
 * <li>
 * <a href="https://doi.org/10.1109/UCC.2011.24">
 * Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel
 * Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM
 * International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS
 * Press, USA), Melbourne, Australia, December 5-7, 2011.
 * </a>
 * </li>
 * </ul>
 * </p>
 *
 * @author Saurabh Kumar Garg
 * @since CloudSim Toolkit 1.0
 *
 * TODO Classes {@link CloudletTask}, {@link Cloudlet}
 * and {@link CloudletExecution} share a common set of attributes that would be defined by a common interface.
 */
public abstract class CloudletTask implements Identifiable {
    private boolean finished;

    /** @see #getId() */
    private long id;

    /** @see #getStartTime() */
    private double startTime;

    /** @see #getFinishTime() */
    private double finishTime;

    /** @see #getMemory() */
    private long memory;

    /** @see #getCloudlet() */
    private NetworkCloudlet cloudlet;

    /**
     * Creates a new task.
     * @param id task id
     */
    public CloudletTask(final int id) {
        super();
        this.id = id;
        this.startTime = -1;
        this.finishTime = -1;
        this.memory = 0;
    }

    /**
     * Gets the id of the task.
     * @return
     */
    @Override
    public long getId() {
        return id;
    }

    /**
     * Sets the id of the task.
     * @param id the ID to set
     */
    public CloudletTask setId(final int id) {
        this.id = id;
        return this;
    }

    /**
     * Gets the memory amount used by the task (in Megabytes).
     * @return
     */
    public long getMemory() {
        return memory;
    }

    /**
     * Sets the memory amount used by the task (in Megabytes).
     * @param memory the memory amount to set
     */
    public CloudletTask setMemory(final long memory) {
        this.memory = memory;
        return this;
    }

    /**
     * @return the time the task started executing (in seconds), or -1 if not started yet.
     */
    public double getStartTime() {
        return startTime;
    }

    /**
     * Sets the time the task started executing (in seconds).
     * @param startTime the start time to set
     */
    public CloudletTask setStartTime(final double startTime) {
        this.startTime = startTime;
        return this;
    }

    /**
     * Gets the NetworkCloudlet that the task belongs to.
     * @return
     */
    public NetworkCloudlet getCloudlet() {
        return cloudlet;
    }

    public CloudletTask setCloudlet(final NetworkCloudlet cloudlet) {
        this.cloudlet = cloudlet;
        return this;
    }

    /**
     * Checks if the task is finished or not.
     *
     * @return true if the task has finished, false otherwise
     * @see #isActive()
     */
    public boolean isFinished(){
        return finished;
    }

    /**
     * Checks if the task is active (it's not finished).
     *
     * @return true if the task is active, false otherwise
     * @see #isFinished()
     */
    public boolean isActive(){
        return !isFinished();
    }

    /**
     * Sets the task as finished or not
     * @param finished true to set the task as finished, false otherwise
     * @throws RuntimeException when the task is already finished and you try to set it as unfinished
     */
    protected void setFinished(final boolean finished){
        if(this.finished && !finished) {
            throw new IllegalStateException("The task is already finished. You cannot set it as unfinished.");
        }

        //If the task wasn't finished before and try to set it to finished, stores the finishTime
        if(!this.finished && finished) {
            finishTime = cloudlet.getSimulation().clock();
        }

        this.finished = finished;
    }

    /**
     * @return the time the task spent executing (in seconds), or -1 if not finished yet
     */
    public double getExecutionTime(){
        return finished ? finishTime - startTime : -1;
    }

    /**
     * @return the time the task finished (in seconds) or -1 if not finished yet.
     */
    public double getFinishTime() {
        return finishTime;
    }

    public boolean isExecutionTask(){
        return this instanceof CloudletExecutionTask;
    }

    public boolean isSendTask(){
        return this instanceof CloudletSendTask;
    }

    public boolean isReceiveTask(){
        return this instanceof CloudletReceiveTask;
    }
}
