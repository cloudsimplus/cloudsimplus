/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.cloudlets.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;

/**
 * NetworkCloudlet class extends Cloudlet to support simulation of complex
 * applications. Each NetworkCloudlet represents a task of the application.
 * Each task consists of several tasks.
 *
 * <p>
 * Please refer to following publication for more details:
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
 * @TODO @author manoelcampos See how to implement the NULL pattern for this class.
 */
public class NetworkCloudlet extends CloudletSimple {

    /**
     * The index of the active running task or -1 if no task has started yet.
     */
    private int currentTaskNum;

    /**
     * All tasks which cloudlet execution.
     */
    private final List<CloudletTask> tasks;

    /**
     * @see #getMemory()
     */
    private long memory;

    /**
     * Creates a NetworkCloudlet with no priority and file size and output size equal to 1.
     *
     * @param id the unique ID of this cloudlet
     * @param cloudletLength the length or size (in MI) of this cloudlet to be executed in a VM
     * @param pesNumber the pes number
     * @pre id >= 0
     * @pre cloudletLength >= 0.0
     * @post $none
     */
    public NetworkCloudlet(final int id,  final long cloudletLength,  final int pesNumber) {
        super(id, cloudletLength, pesNumber);
        this.currentTaskNum = -1;
        this.memory = 0;
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a NetworkCloudlet with the given parameters.
     *
     * @param id the unique ID of this cloudlet
     * @param cloudletLength the length or size (in MI) of this cloudlet to be executed in a VM
     * @param pesNumber the pes number
     * @param cloudletFileSize the file size (in bytes) of this cloudlet <tt>BEFORE</tt> submitting to a Datacenter
     * @param cloudletOutputSize the file size (in bytes) of this cloudlet <tt>AFTER</tt> finish executing by a VM
     * @param memory the amount of memory
     * @param utilizationModelCpu the utilization model of CPU
     * @param utilizationModelRam the utilization model of RAM
     * @param utilizationModelBw  the utilization model of BW
     *
     * @deprecated Use the other available constructors with less parameters
     * and set the remaining ones using the respective setters.
     * This constructor will be removed in future versions.
     *
     */
    @Deprecated
    public NetworkCloudlet(
            int id,
            final long cloudletLength,
            int pesNumber,
            long cloudletFileSize,
            long cloudletOutputSize,
            long memory,
            UtilizationModel utilizationModelCpu,
            UtilizationModel utilizationModelRam,
            UtilizationModel utilizationModelBw)
    {
        this(id, cloudletLength, pesNumber);
        this.setFileSize(cloudletFileSize)
            .setOutputSize(cloudletOutputSize)
            .setUtilizationModelCpu(utilizationModelCpu)
            .setUtilizationModelRam(utilizationModelRam)
            .setUtilizationModelBw(utilizationModelBw);
        this.memory = memory;
    }

    public double getNumberOfTasks() {
        return tasks.size();
    }

    /**
     *
     * @return a read-only list of cloudlet's tasks.
     */
    public List<CloudletTask> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Gets the Cloudlet's RAM memory.
     *
     * @TODO Required, allocated, used memory? It doesn't appear to be used.
     */
    public long getMemory() {
        return memory;
    }

    /**
     * Sets the Cloudlet's RAM memory.
     * @param memory amount of RAM to set
     *
     */
    public NetworkCloudlet setMemory(long memory) {
        this.memory = memory;
        return this;
    }

    /**
     * Checks if the some Cloudlet Task has started yet.
     *
     * @return true if some task has started, false otherwise
     */
    public boolean isTasksStarted() {
        return currentTaskNum > -1;
    }

    /**
     * Change the current task to the next one in order
     * to start executing it, if the current task is finished.
     *
     * @param nextTaskStartTime the time that the next task will start
     * @return true if the current task finished and the next one was started, false otherwise
     */
    public boolean startNextTaskIfCurrentIsFinished(double nextTaskStartTime){
        /**
         * @todo @author manoelcampos CloudletTask should implement
         * Null Object Pattern to avoid these null checks.
         */
        final CloudletTask nextTask = getNextTaskIfCurrentIfFinished();
        if(nextTask == null){
            return false;
        }

        nextTask.setStartTime(nextTaskStartTime);
        return true;
    }

    /**
     * Gets the current task.
     *
     * @return
     */
    public CloudletTask getCurrentTask() {
        if (currentTaskNum < 0 || currentTaskNum >= tasks.size()) {
            return null;
        }

        return tasks.get(currentTaskNum);
    }

    /**
     * Gets the next task in the list if the current task is finished.
     * @return the next task or null if the current task is already the last one
     * or it is not finished yet.
     */
    protected CloudletTask getNextTaskIfCurrentIfFinished(){
        if(getCurrentTask() != null && !getCurrentTask().isFinished())
            return null;

        if(this.currentTaskNum <= tasks.size()-1)
            this.currentTaskNum++;

        return getCurrentTask();
    }

    @Override
    public boolean isFinished() {
        final boolean allTasksFinished = tasks.stream().allMatch(CloudletTask::isFinished);
        return super.isFinished() && allTasksFinished;
    }

    /**
     * {@inheritDoc}
     * <p>The length of a NetworkCloudlet is the
     * length sum of all its {@link CloudletExecutionTask}'s.</p>
     * @return the length sum of all {@link CloudletExecutionTask}'s
     */
    @Override
    public long getLength() {
        return getTasks().stream()
                .filter(t -> t instanceof CloudletExecutionTask)
                .mapToLong(t -> ((CloudletExecutionTask)t).getLength())
                .sum();
    }

    @Override
    public boolean setFinishedLengthSoFar(long length) {
        return super.setFinishedLengthSoFar(length);
    }

    /**
     * Adds a task to the {@link #getTasks() task list}
     * and links the task to the NetworkCloudlet.
     *
     * @param task Task to be added
     * @return the NetworkCloudlet instance
     */
    public NetworkCloudlet addTask(CloudletTask task) {
        task.setCloudlet(this);
        tasks.add(task);
        return this;
    }

    protected long numberOfExecutionTasks() {
        return getTasks().stream().filter(t -> t instanceof CloudletExecutionTask).count();
    }

}
