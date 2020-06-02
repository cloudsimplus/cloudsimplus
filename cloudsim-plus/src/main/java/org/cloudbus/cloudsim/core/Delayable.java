package org.cloudbus.cloudsim.core;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * Defines methods for an object that its execution can be delayed by some time
 * when it is submitted to a to a {@link Datacenter} by a {@link DatacenterBroker}.
 *
 * @see Vm
 * @see Cloudlet
 *
 * @author Manoel Campos da Silva Filho
 */
public interface Delayable {
    /**
     * Gets the time (in seconds) that a {@link DatacenterBroker} will wait
     * to request the creation of the object.
     * This is a relative time from the current simulation time.
     *
     * @return the submission delay (in seconds)
     */
    double getSubmissionDelay();

    /**
     * Sets the time (in seconds) that a {@link DatacenterBroker} will wait
     * to request the creation of the object.
     * This is a relative time from the current simulation time.
     *
     * @param submissionDelay the amount of seconds from the current simulation
     * time that the object will wait to be submitted
     */
    void setSubmissionDelay(double submissionDelay);
}
