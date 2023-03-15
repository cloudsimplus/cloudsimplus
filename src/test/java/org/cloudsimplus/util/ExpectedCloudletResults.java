package org.cloudsimplus.util;

import org.cloudsimplus.cloudlets.Cloudlet;

/**
 * A data class to store expected results about the execution of a given cloudlet
 * during unit test execution.
 * The class is supposed to be used by Unit and Integration/Functional Tests
 * in order to validate the results of a executed cloudlet.
 *
 * @author Manoel Campos da Silva Filho
 */
public final class ExpectedCloudletResults {
    /** @see #getExpectedExecTime() */
    private final double expectedExecTime;
    /** @see #getExpectedStartTime() */
    private final double expectedStartTime;
    /** @see #getExpectedFinishTime() */
    private final double expectedFinishTime;
    /** @see #getCloudlet() */
    private Cloudlet cloudlet;

    public ExpectedCloudletResults(final double execTime, final double startTime, final double finishTime) {
        this.expectedExecTime = execTime;
        this.expectedStartTime = startTime;
        this.expectedFinishTime = finishTime;
    }

    /**
     * The expected execution time of the {@link #cloudlet}.
     * @return
     */
    public double getExpectedExecTime() {
        return expectedExecTime;
    }

    /**
     * The expected start time of the {@link #cloudlet}.
     * @return
     */
    public double getExpectedStartTime() {
        return expectedStartTime;
    }

    /**
     * The expected finish time of the {@link #cloudlet}.
     * @return
     */
    public double getExpectedFinishTime() {
        return expectedFinishTime;
    }

    /**
     * @return the cloudlet to check if execution results were as expected.
     */
    public Cloudlet getCloudlet() {
        return cloudlet;
    }

    /**
     * Sets the cloudlet to check if execution results were as expected.
     * @param cloudlet
     */
    public void setCloudlet(Cloudlet cloudlet) {
        this.cloudlet = cloudlet;
    }

}
