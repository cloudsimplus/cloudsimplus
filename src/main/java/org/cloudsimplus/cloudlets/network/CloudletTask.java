/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.cloudlets.network;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletExecution;
import org.cloudsimplus.core.Identifiable;

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
 * and {@link CloudletExecution} share a common set of attributes that should be defined by a common interface.
 */
@Getter @Setter
public abstract class CloudletTask implements Identifiable {
    /** The id of the task. */
    private long id;

    /**
     * The time the task started executing (in seconds), or -1 if not started yet.
     */
    private double startTime;

    /**
     * The time the task finished (in seconds) or -1 if not finished yet.
     */
    @Setter(AccessLevel.NONE)
    private double finishTime;

    /**
     * The memory amount used by the task (in Megabytes).
     */
    private long memory;

    /**
     * The NetworkCloudlet that the task belongs to.
     */
    @NonNull
    private NetworkCloudlet cloudlet;

    /**
     * Creates a task.
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
     * Checks if the task is finished or not.
     *
     * @return true if the task has finished, false otherwise
     * @see #isActive()
     */
    public boolean isFinished(){
        return finishTime > -1;
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
        if(isFinished() && !finished) {
            throw new IllegalStateException("The task is already finished. You cannot set it as unfinished.");
        }

        //If the task wasn't finished before and try to set it to finished, stores the finishTime
        if(isActive() && finished) {
            finishTime = cloudlet.getSimulation().clock();
        }
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
