/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.cloudlets.network;

import org.cloudbus.cloudsim.cloudlets.CloudletExecutionInfo;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.Identificable;

/**
 * Represents one of many tasks that can be executed by a {@link NetworkCloudlet}.
 *
 * <p>Please refer to following publication for more details:
 * <ul>
 * <li>
 * <a href="http://dx.doi.org/10.1109/UCC.2011.24">
 * Saurabh Kumar Garg and Rajkumar Buyya, NetworkCloudSim: Modelling Parallel
 * Applications in Cloud Simulations, Proceedings of the 4th IEEE/ACM
 * International Conference on Utility and Cloud Computing (UCC 2011, IEEE CS
 * Press, USA), Melbourne, Australia, December 5-7, 2011.
 * </a>
 * </ul>
 * </p>
 *
 * @author Saurabh Kumar Garg
 * @since CloudSim Toolkit 1.0
 *
 * @TODO @author manoelcampos Classes {@link CloudletTask}, {@link Cloudlet}
 * and {@link CloudletExecutionInfo} share a common set of attributes that would be defined by a common interface.
 */
public abstract class CloudletTask implements Identificable {
    private boolean finished = false;

    /**
     * @see #getId()
     */
    private int id;

    /**
     * @see #getStartTime()
     */
    private double startTime;

    /**
     * @see #getFinishTime()
     */
    private double finishTime;

    /**
     * @see #getMemory()
     */
    private long memory;

    /**
     * @see #getCloudlet()
     */
    private NetworkCloudlet cloudlet;

    /**
     * Creates a new task.
     * @param id task id
     */
    public CloudletTask(int id) {
        super();
        this.id = id;
        this.startTime = -1;
        this.finishTime = -1;
        this.memory = 0;
    }

    /**
     * Gets the id of the CloudletTask.
     * @return
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the CloudletTask.
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the memory amount used by the task.
     * @return
     */
    public long getMemory() {
        return memory;
    }

    /**
     * Sets the memory amount used by the task.
     * @param memory the memory amount to set
     */
    public void setMemory(long memory) {
        this.memory = memory;
    }

    /**
     *
     * @return the time the task started executing, or -1 if not started yet.
     */
    public double getStartTime() {
        return startTime;
    }

    /**
     * Sets the time the task started executing.
     * @param startTime the start time to set
     */
    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the NetworkCloudlet that the task belongs to.
     * @return
     */
    public NetworkCloudlet getCloudlet() {
        return cloudlet;
    }

    public void setCloudlet(NetworkCloudlet cloudlet) {
        this.cloudlet = cloudlet;
    }

    /**
     * Indicates if the task is finished or not.
     *
     * @return true if the task has finished
     */
    public boolean isFinished(){
        return finished;
    }

    /**
     * Sets the task as finished or not
     * @param finished true to set the task as finished, false otherwise
     * @throws RuntimeException when the task is already finished and you try to set it as unfinished
     */
    protected void setFinished(boolean finished){
        if(this.finished && !finished)
            throw new RuntimeException("The task is already finished. You cannot set it as unfinished.");

        //If the task was not finished before and try to set it to finished,
        //stores the finishTime
        if(!this.finished && finished)
            finishTime = cloudlet.getSimulation().clock();

        this.finished = finished;
    }

    /**
     *
     * @return the time the task spent executing, or -1 if not finished yet
     */
    public double getExecutionTime(){
        return (finished ? finishTime - startTime : -1);
    }

    /**
     *
     * @return the time the task finished or -1 if not finished yet.
     */
    public double getFinishTime() {
        return finishTime;
    }

}
