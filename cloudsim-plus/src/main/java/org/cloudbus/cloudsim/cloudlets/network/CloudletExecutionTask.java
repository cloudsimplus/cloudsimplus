/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.cloudlets.network;

/**
 * A processing task that can be executed by a {@link NetworkCloudlet}
 * in a single {@link org.cloudbus.cloudsim.resources.Pe}.
 * The tasks currently just execute in a sequential manner.
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
 * </ul>
 * </p>
 *
 * @author Saurabh Kumar Garg
 * @author Manoel Campos da Silva Filho
 *
 * @since CloudSim Toolkit 1.0
 *
 * @TODO @author manoelcampos Each execution task must use just a single core.
 * It may represent a thread (so the name of the class would be changed).
 * By this way, a execution task should use only one core.
 * However, tasks may be executed in parallel (considering there are multiple cores)
 * and/or sequentially.
 * This feature has to be included in the class. One proposal is
 * to create a int group attribute. All tasks (not only execution tasks)
 * that have the group equals to zero are executed sequentially (that means they
 * aren't grouped). The tasks that have the same group have to be executed
 * in parallel, one in each CPU core (PE).
 * All tasks into a group will be executed together. The next group
 * starts only when all the tasks in the prior finishes (each task
 * can have a different length, so may finish in different times).
 * The value of the group define the tasks execution order.
 * Tasks with lower group number are executed first.
 * You can have single tasks (that are not grouped) between
 * grouped tasks (defining the order that this single task executes)
 * just assigning a group number to it and making sure to not
 * add other tasks with the same group. For instance, consider the
 * tasks below, represented by their group number, for a NetworkCloudlet with 4 cores:
 * 0 0 1 1 1 1 2 3 3
 *
 * there are 2 ungrouped tasks (0) that will be executed sequentially,
 * 4 tasks of group 1 that will be executed in parallel after all ungrouped tasks,
 * there is a single task at group 2 that will be executed after the group 1
 * and finally there is 2 tasks at group 2 to be executed in parallel at the end.
 *
 * When adding a task to a NetworkCloudlet, the addTask method
 * has to check if the current number of tasks for the group (that represents parallel tasks)
 * is lower than the number of NetworkCloudlet's PEs.
 *
 */
public class CloudletExecutionTask extends CloudletTask {

    /**
     * @see #getLength()
     */
    private long length;

    /**
     * @see #getTotalExecutedLength()
     */
    private long totalExecutedLength;

    /**
     * Creates a new task.
     * @param id task id
     * @param executionLength the execution length of the task (in MI)
     */
    public CloudletExecutionTask(final int id, final long executionLength) {
        super(id);
        this.length = executionLength;
    }

    /**
     * Gets the execution length of the task (in MI).
     *
     * @return
     */
    public long getLength() {
        return length;
    }

    /**
     * Sets the execution length of the task (in MI).
     *
     * @param length the length to set
     */
    public void setLength(final long length) {
        this.length = length;
    }

    /**
     * Gets the length of this CloudletTask that has been executed so far (in MI).
     * @return
     */
    public long getTotalExecutedLength() {
        return totalExecutedLength;
    }

    /**
     * Sets a given number of MI to the {@link #getTotalExecutedLength() total
     * MI executed so far} by the cloudlet.
     * @param partialFinishedMI the partial executed length of this Cloudlet (in MI)
     * @return {@inheritDoc}
     */
    public boolean process(final long partialFinishedMI) {
        if(partialFinishedMI <= 0) {
            return false;
        }

        final long maxLengthToAdd = Math.min(partialFinishedMI, length-totalExecutedLength);
        this.totalExecutedLength += maxLengthToAdd;
        setFinished(this.totalExecutedLength == length);
        return true;
    }
}
