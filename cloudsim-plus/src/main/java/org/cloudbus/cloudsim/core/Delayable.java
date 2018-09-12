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
     * Gets the delay (in seconds) that a {@link DatacenterBroker} has to include
     * when submitting the object, in order that it will be assigned
     * to a VM only after this delay has expired.
     *
     * @return the submission delay
     */
    double getSubmissionDelay();

    /**
     * Sets the delay (in seconds) that a {@link DatacenterBroker} has to include
     * when submitting the object, in order that it will be assigned
     * to a VM only after this delay has expired. The delay should be greater or equal to zero.
     *
     * @param submissionDelay the amount of seconds from the current simulation
     * time that the object will wait to be submitted
     */
    void setSubmissionDelay(double submissionDelay);
}
