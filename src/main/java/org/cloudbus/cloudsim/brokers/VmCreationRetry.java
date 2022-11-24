package org.cloudbus.cloudsim.brokers;

import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.listeners.EventListener;

/**
 * Keeps track of number of VM creation retries sent by a {@link DatacenterBroker}.
 * VM creation fails when the broker can't find a suitable Host
 * for VM placement.
 *
 * @author Manoel Campos da Silva Filho
 * @author sohamchari
 * @since CloudSim Plus 7.3.1
 */
public class VmCreationRetry {

    /**
     * Default number of times the broker will try to recreated failed VMs.
     */
    public static final int DEF_CURRENT_VM_CREATION_RETRIES = 5;

    /** @see #getDelay() */
    private double delay;

    /** @see #getMaxRetries() */
    private int maxRetries;

    /** @see #getCurrentRetries() */
    private int currentRetries;

    /** @see #getCreationRequests() */
    private int creationRequests;

    /**
     * Creates an object with default values.
     */
    public VmCreationRetry() {
        this.delay = 5;
        this.maxRetries = DEF_CURRENT_VM_CREATION_RETRIES;
    }

    /**
     * Creates an object with the given values for the attributes.
     * @param delay a delay (in seconds) for the broker to retry allocating VMs
     *                            that couldn't be placed due to lack of suitable active Hosts.
     * @param maxRetries the maximum number of times the broker will try to find a host to create (place) the VM.
     */
    public VmCreationRetry(final double delay, final int maxRetries) {
        this.delay = delay;
        this.maxRetries = maxRetries;
    }

    /**
     * Creates an object with all attributes equal to zero.
     * @return
     */
    public static VmCreationRetry ofZero(){
        return new VmCreationRetry(0, 0);
    }

    /**
     * Gets a delay (in seconds) for the broker to retry allocating VMs
     * that couldn't be placed due to lack of suitable active Hosts.
     *
     * @return
     * <ul>
     *  <li>a value larger than zero to indicate the broker will retry
     *  to place failed VM as soon as new VMs or Cloudlets
     *  are submitted or after the given delay.</li>
     *  <li>otherwise, to indicate failed VMs will be just added to the
     *  {@link DatacenterBroker#getVmFailedList()} and the user simulation have to deal with it.
     *  If the VM has an {@link Vm#addOnCreationFailureListener(EventListener) OnCreationFailureListener},
     *  it will be notified about the failure.</li>
     * </ul>
     */
    public double getDelay() {
        return delay;
    }

    /**
     * Sets a delay (in seconds) for the broker to retry allocating VMs
     * that couldn't be placed due to lack of suitable active Hosts.
     *
     * Setting the attribute as:
     * <ul>
     *  <li>larger than zero, the broker will retry to place failed VM as soon as new VMs or Cloudlets
     *  are submitted or after the given delay.</li>
     *  <li>otherwise, failed VMs will be just added to the {@link DatacenterBroker#getVmFailedList()}
     *  and the user simulation have to deal with it.
     *  If the VM has an {@link Vm#addOnCreationFailureListener(EventListener) OnCreationFailureListener},
     *  it will be notified about the failure.</li>
     * </ul>
     * @param delay the value to set
     */
    public void setDelay(final double delay) {
        this.delay = delay;
    }

    /**
     * Gets the maximum number of times the broker will try to find a host to create (place) the VM.
     * @return
     */
    public int getMaxRetries() {
        return maxRetries;
    }

    /**
     * Sets the maximum number of times the broker will try to find a host to create (place) the VM.
     * @param maxRetries value to set
     */
    public void setMaxRetries(final int maxRetries) {
        this.maxRetries = maxRetries;
    }

    /**
     * Checks if the broker has to retry allocating VMs
     * that couldn't be placed due to lack of suitable Hosts.
     * @return
     */
    public boolean isRetryFailedVms() {
        return delay > 0 && currentRetries < maxRetries;
    }

    /**
     * Increments the current number of times failed VMs were tried to be recreated.
     */
    public void incCurrentVmCreationRetries() {
        this.currentRetries++;
    }

    /**
     * Gets the current number of times failed VMs were tried to be recreated.
     * @return
     */
    public int getCurrentRetries() {
        return currentRetries;
    }

    /**
     * Gets the number of VM creation requests considering all submitted VMs.
     * @return
     */
    public int getCreationRequests() {
        return creationRequests;
    }

    /**
     * Resets the number of VM creation requests to the {@link #DEF_CURRENT_VM_CREATION_RETRIES default number}.
     *
     * @see #incCurrentVmCreationRetries()
     */
    public void resetCurrentVmCreationRetries() {
        this.currentRetries = DEF_CURRENT_VM_CREATION_RETRIES;
    }

    /**
     * Increments/decrements the number of VM creation requests.
     * @param value the value to increment/decrement
     * @see #resetCurrentVmCreationRetries()
     */
    public void incVmCreationRequests(final int value) {
        this.creationRequests += value;
    }
}
