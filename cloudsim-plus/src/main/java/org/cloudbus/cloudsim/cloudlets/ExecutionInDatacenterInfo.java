package org.cloudbus.cloudsim.cloudlets;

import org.cloudbus.cloudsim.datacenters.Datacenter;

/**
 * Internal class that keeps track of Cloudlet's movement in different
 * {@link Datacenter Datacenters}. Each time a cloudlet is run on a given Datacenter, the cloudlet's
 * execution history on each Datacenter is registered at {@link CloudletAbstract#getLastExecutionInDatacenterInfo()}
 */
final class ExecutionInDatacenterInfo {
    static final ExecutionInDatacenterInfo NULL = new ExecutionInDatacenterInfo();

    /**
     * Cloudlet's submission (arrival) time to a Datacenter
     * or {@link Cloudlet#NOT_ASSIGNED} if the Cloudlet was not assigned to a Datacenter yet.
     */
    double arrivalTime;

    /**
     * The time this Cloudlet resides in a Datacenter (from arrival time
     * until departure time, that may include waiting time).
     */
    double wallClockTime;

    /**
     * The total time the Cloudlet spent being executed in a Datacenter.
     */
    double actualCpuTime;

    /**
     * Cost per second a Datacenter charge to execute this Cloudlet.
     */
    double costPerSec;

    /**
     * Cloudlet's length finished so far (in MI).
     */
    long finishedSoFar;


    /**
     * a Datacenter where the Cloudlet will be executed
     */
    Datacenter dc;

    ExecutionInDatacenterInfo() {
        this.dc = Datacenter.NULL;
        this.arrivalTime = Cloudlet.NOT_ASSIGNED;
    }
}
