/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.cloudlets.network;

import lombok.NonNull;
import lombok.experimental.Accessors;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.network.NetworkVm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * NetworkCloudlet to support simulation of complex applications.
 * Each application is compounded of one or more {@link CloudletTask}s
 * for performing different kinds of processing.
 *
 * <p>
 * Please refer to the following publication for more details:
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
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 *
 * TODO Check how to implement the NULL pattern for this class.
 */
@Accessors
public class NetworkCloudlet extends CloudletSimple {

    /**
     * The index of the active running task or -1 if no task has started yet.
     */
    private int currentTaskNum;

    /** @see #getTasks() */
    private final List<CloudletTask> tasks;

    /**
     * Creates a NetworkCloudlet with no priority, no file size and output size equal to 1.
     * The length of the Cloudlet is determined by the sum of its {@link #getTasks() tasks} length.
     *
     * @param pesNumber the number of {@link Pe}s this Cloudlet requires
     */
    public NetworkCloudlet(final int pesNumber) {
        this(-1, pesNumber);
    }

    /**
     * Creates a NetworkCloudlet with no priority, no file size and output size equal to 1.
     * The length of the Cloudlet is determined by the sum of its {@link #getTasks() tasks} length.
     *
     * @param id        the unique ID of this cloudlet
     * @param pesNumber the number of {@link Pe}s this Cloudlet requires
     */
    public NetworkCloudlet(final int id, final int pesNumber) {
        super(id, -1, pesNumber);
        this.currentTaskNum = -1;
        this.tasks = new ArrayList<>();
    }

    public double getNumberOfTasks() {
        return tasks.size();
    }

    /**
     * @return a read-only list of Cloudlet's tasks.
     */
    public List<CloudletTask> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Checks if some Cloudlet Task has started yet.
     *
     * @return true if some task has started, false otherwise
     */
    public boolean isTasksStarted() {
        return currentTaskNum > -1;
    }

    /**
     * Change the current task to the next one to start executing it,
     * if the current task is finished.
     *
     * @param nextTaskStartTime the time that the next task will start
     * @return true if the current task is finished and the next one has started;
     * false if current task is not finished or there aren't any more tasks to be executed.
     */
    public boolean startNextTaskIfCurrentIsFinished(final double nextTaskStartTime){
        return
            getNextTaskIfCurrentIfFinished()
                .map(task -> startTask(task, nextTaskStartTime))
                .isPresent();
    }

    private static CloudletTask startTask(final CloudletTask task, double time) {
        task.setStartTime(time);
        return task;
    }

    /**
     * @return an {@link Optional} containing the current task
     * or {@link Optional#empty()} if there is no current task yet.
     */
    public Optional<CloudletTask> getCurrentTask() {
        if (currentTaskNum < 0 || currentTaskNum >= tasks.size()) {
            return Optional.empty();
        }

        return Optional.of(tasks.get(currentTaskNum));
    }

    /**
     * Gets an {@link Optional} containing the next task in the list if the current task is finished.
     *
     * @return the next task if the current one is finished;
     *         otherwise {@link Optional#empty()} if the current task is already the last one,
     *         or it is not finished yet.
     */
    private Optional<CloudletTask> getNextTaskIfCurrentIfFinished(){
        if(getCurrentTask().filter(CloudletTask::isActive).isPresent()) {
            return Optional.empty();
        }

        if(this.currentTaskNum <= tasks.size()-1) {
            this.currentTaskNum++;
        }

        return getCurrentTask();
    }

    @Override
    public boolean isFinished() {
        final boolean allTasksFinished = tasks == null || tasks.stream().allMatch(CloudletTask::isFinished);
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
                .filter(CloudletTask::isExecutionTask)
                .map(task -> (CloudletExecutionTask)task)
                .mapToLong(CloudletExecutionTask::getLength)
                .sum();
    }

    /**
     * Adds a task to the {@link #getTasks() task list}
     * and links the task to this NetworkCloudlet.
     *
     * @param task Task to be added
     * @return this NetworkCloudlet instance
     */
    public NetworkCloudlet addTask(@NonNull final CloudletTask task) {
        task.setCloudlet(this);
        tasks.add(task);
        return this;
    }

    @Override
    public NetworkVm getVm() {
        return (NetworkVm)super.getVm();
    }

    @Override
    public NetworkCloudlet setVm(final Vm vm) {
        if(vm == Vm.NULL) {
            setVm(NetworkVm.NULL);
            return this;
        }

        if(vm instanceof NetworkVm) {
            super.setVm(vm);
            return this;
        }

        throw new IllegalArgumentException("NetworkCloudlet can just be executed by a NetworkVm");
    }
}
