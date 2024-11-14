/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.cloudlets.network;

import org.cloudsimplus.resources.Pe;

/**
 * A processing task that can be executed by a {@link NetworkCloudlet}
 * in a single {@link Pe}.
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
 */
public class CloudletExecutionTask extends CloudletTask {

    /** @see #getLength() */
    private long length;

    /** @see #getTotalExecutedLength() */
    private long totalExecutedLength;

    /**
     * Creates a new task.
     * @param id id to assign to the task
     * @param executionLength the execution length of the task (in MI)
     */
    public CloudletExecutionTask(final int id, final long executionLength) {
        super(id);
        this.length = executionLength;
    }

    /**
     * Gets the execution length of the task (in MI).
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
     *
     * @param partialFinishedMI the partial executed length of this Cloudlet (in MI)
     * @return {@inheritDoc}
     */
    public boolean process(final long partialFinishedMI) {
        if(partialFinishedMI <= 0) {
            return false;
        }

        final long maxLengthToAdd = Math.min(partialFinishedMI, length - totalExecutedLength);
        this.totalExecutedLength += maxLengthToAdd;
        setFinished(this.totalExecutedLength == length);
        return true;
    }
}
