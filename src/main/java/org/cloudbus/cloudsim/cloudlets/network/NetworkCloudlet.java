/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.cloudlets.network;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.network.NetworkVm;

import java.util.*;

/**
 * NetworkCloudlet to support simulation of complex applications.
 * Each application is compounded of one or more {@link CloudletTask}s
 * for performing different kinds of processing.
 *
 * <p>
 * Please refer to following publication for more details:
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
public class NetworkCloudlet extends CloudletSimple {

    /**
     * The index of the active running task or -1 if no task has started yet.
     */
    private int currentTaskNum;

    /** @see #getTasks() */
    private final List<CloudletTask> tasks;

    /** @see #getMemory() */
    private long memory;

    /**
     * Creates a NetworkCloudlet with no priority and file size and output size equal to 1.
     *
     * @param length the length or size (in MI) of this cloudlet to be executed in a VM (check out {@link #setLength(long)})
     * @param pesNumber the number of PEs this Cloudlet requires
     */
    public NetworkCloudlet(final long length, final int pesNumber) {
        this(-1, length, pesNumber);
    }

    /**
     * Creates a NetworkCloudlet with no priority and file size and output size equal to 1.
     *
     * @param id the unique ID of this cloudlet
     * @param length the length or size (in MI) of this cloudlet to be executed in a VM (check out {@link #setLength(long)})
     * @param pesNumber the pes number
     */
    public NetworkCloudlet(final int id,  final long length, final int pesNumber) {
        super(id, length, pesNumber);
        this.currentTaskNum = -1;
        this.memory = 0;
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
     * Gets the Cloudlet's RAM memory (in Megabytes).
     * TODO Required, allocated, used memory? It doesn't appear to be used.
     */
    public long getMemory() {
        return memory;
    }

    /**
     * Sets the Cloudlet's RAM memory.
     * @param memory amount of RAM to set (in Megabytes)
     * TODO Cloudlet has the {@link #getUtilizationModelRam()} that defines
     *       how RAM is used. This way, this attribute doesn't make sense
     *       since usage of RAM is dynamic.
     *       The attribute would be used to know what is the maximum
     *       memory the cloudlet can use, but that will be the
     *       maximum VM RAM capacity or a different value
     *       defined by a UtilizationModel.
     */
    public NetworkCloudlet setMemory(final long memory) {
        this.memory = memory;
        return this;
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
     * Change the current task to the next one in order
     * to start executing it, if the current task is finished.
     *
     * @param nextTaskStartTime the time that the next task will start
     * @return true if the current task finished and the next one was started, false otherwise
     */
    public boolean startNextTaskIfCurrentIsFinished(final double nextTaskStartTime){
        return
            getNextTaskIfCurrentIfFinished()
                .map(task -> task.setStartTime(nextTaskStartTime))
                .isPresent();
    }

    /**
     * Gets an {@link Optional} containing the current task
     * or an {@link Optional#empty()} if there is no current task yet.
     * @return
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
     *         otherwise an {@link Optional#empty()} if the current task is already the last one,
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
                .filter(CloudletTask::isExecutionTask)
                .map(task -> (CloudletExecutionTask)task)
                .mapToLong(CloudletExecutionTask::getLength)
                .sum();
    }

    /**
     * Adds a task to the {@link #getTasks() task list}
     * and links the task to the NetworkCloudlet.
     *
     * @param task Task to be added
     * @return the NetworkCloudlet instance
     */
    public NetworkCloudlet addTask(final CloudletTask task) {
        Objects.requireNonNull(task);
        task.setCloudlet(this);
        tasks.add(task);
        return this;
    }

    @Override
    public NetworkVm getVm() {
        return (NetworkVm)super.getVm();
    }

    @Override
    public Cloudlet setVm(final Vm vm) {
        if(vm == Vm.NULL)
            return super.setVm(NetworkVm.NULL);

        if(vm instanceof NetworkVm)
            return super.setVm(vm);

        throw new IllegalArgumentException("NetworkCloudlet can just be executed by a NetworkVm");
    }
}
