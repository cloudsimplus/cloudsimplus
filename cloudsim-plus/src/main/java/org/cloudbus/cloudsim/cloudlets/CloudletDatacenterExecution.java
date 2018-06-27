package org.cloudbus.cloudsim.cloudlets;

import org.cloudbus.cloudsim.datacenters.Datacenter;

/**
 * Internal class that keeps track of Cloudlet's movement in different
 * {@link Datacenter Datacenters}. Each time a cloudlet is run on a given Datacenter, the cloudlet's
 * execution history on each Datacenter is registered at {@link CloudletAbstract#getLastExecutionInDatacenterInfo()}
 */
final class CloudletDatacenterExecution {
    protected static final CloudletDatacenterExecution NULL = new CloudletDatacenterExecution();

    private double arrivalTime;
    private double wallClockTime;
    private double actualCpuTime;
    private double costPerSec;
    private long finishedSoFar;
    private Datacenter datacenter;

    CloudletDatacenterExecution() {
        this.datacenter = Datacenter.NULL;
        this.arrivalTime = Cloudlet.NOT_ASSIGNED;
    }

    /**
     * Cloudlet's submission (arrival) time to a Datacenter
     * or {@link Cloudlet#NOT_ASSIGNED} if the Cloudlet was not assigned to a Datacenter yet.
     */
    double getArrivalTime() {
        return arrivalTime;
    }

    void setArrivalTime(final double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    /**
     * Gets the time this Cloudlet resides in a Datacenter (from arrival time
     * until departure time, that may include waiting time).
     * @return the wall-clock time
     * @see <a href="https://en.wikipedia.org/wiki/Elapsed_real_time">Elapsed real time (wall-clock time)</a>
     */
    double getWallClockTime() {
        return wallClockTime;
    }

    /**
     * Sets the time this Cloudlet resides in a Datacenter (from arrival time
     * until departure time, that may include waiting time).
     * @param wallClockTime the wall-clock time to set
     * @see <a href="https://en.wikipedia.org/wiki/Elapsed_real_time">Elapsed real time (wall-clock time)</a>
     */
    void setWallClockTime(final double wallClockTime) {
        this.wallClockTime = wallClockTime;
    }

    /**
     * The total time the Cloudlet spent being executed in a Datacenter.
     */
    double getActualCpuTime() {
        return actualCpuTime;
    }

    void setActualCpuTime(final double actualCpuTime) {
        this.actualCpuTime = actualCpuTime;
    }

    /**
     * Cost per second a Datacenter charge to execute this Cloudlet.
     */
    double getCostPerSec() {
        return costPerSec;
    }

    void setCostPerSec(final double costPerSec) {
        this.costPerSec = costPerSec;
    }

    /**
     * Cloudlet's length finished so far (in MI).
     */
    long getFinishedSoFar() {
        return finishedSoFar;
    }

    /**
     * Adds the partial length of this Cloudlet that has executed so far in this Datacenter (in MI).
     *
     * @param partialFinishedMI the partial executed length of this Cloudlet (in MI)
     *                          from the last time span (the last time the Cloudlet execution was updated)
     */
    void addFinishedSoFar(final long partialFinishedMI) {
        this.finishedSoFar += partialFinishedMI;
    }

    /**
     * a Datacenter where the Cloudlet will be executed
     */
    Datacenter getDatacenter() {
        return datacenter;
    }

    void setDatacenter(final Datacenter datacenter) {
        this.datacenter = datacenter;
    }
}
