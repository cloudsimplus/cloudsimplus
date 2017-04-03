package org.cloudbus.cloudsim.cloudlets;

import org.cloudbus.cloudsim.datacenters.Datacenter;

/**
 * Internal class that keeps track of Cloudlet's movement in different
 * {@link Datacenter Datacenters}. Each time a cloudlet is run on a given Datacenter, the cloudlet's
 * execution history on each Datacenter is registered at {@link CloudletAbstract#getLastExecutionInDatacenterInfo()}
 */
final class ExecutionInDatacenterInfo {
    protected static final ExecutionInDatacenterInfo NULL = new ExecutionInDatacenterInfo();

    private double arrivalTime;
    private double wallClockTime;
    private double actualCpuTime;
    private double costPerSec;
    private long finishedSoFar;
    private Datacenter datacenter;

    ExecutionInDatacenterInfo() {
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

    void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    /**
     * The time this Cloudlet resides in a Datacenter (from arrival time
     * until departure time, that may include waiting time).
     */
    double getWallClockTime() {
        return wallClockTime;
    }

    void setWallClockTime(double wallClockTime) {
        this.wallClockTime = wallClockTime;
    }

    /**
     * The total time the Cloudlet spent being executed in a Datacenter.
     */
    double getActualCpuTime() {
        return actualCpuTime;
    }

    void setActualCpuTime(double actualCpuTime) {
        this.actualCpuTime = actualCpuTime;
    }

    /**
     * Cost per second a Datacenter charge to execute this Cloudlet.
     */
    double getCostPerSec() {
        return costPerSec;
    }

    void setCostPerSec(double costPerSec) {
        this.costPerSec = costPerSec;
    }

    /**
     * Cloudlet's length finished so far (in MI).
     */
    long getFinishedSoFar() {
        return finishedSoFar;
    }

    void setFinishedSoFar(long finishedSoFar) {
        this.finishedSoFar = finishedSoFar;
    }

    /**
     * a Datacenter where the Cloudlet will be executed
     */
    Datacenter getDatacenter() {
        return datacenter;
    }

    void setDatacenter(Datacenter datacenter) {
        this.datacenter = datacenter;
    }
}
