package org.cloudbus.cloudsim.brokers;

import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.listeners.EventListener;

public class VmCreationRetry {

    public static int DEF_CURRENT_VM_CREATION_RETRIES = 1;

    private double failedVmsRetryDelay;

    private int maxVmCreationRetries;

    private int currentVmCreationRetries;

    /** @see #getVmCreationRequests() */
    private int vmCreationRequests;

    public VmCreationRetry() {
        this.failedVmsRetryDelay = 5;
        this.currentVmCreationRetries = 1;
        this.maxVmCreationRetries = (int) failedVmsRetryDelay;
        this.vmCreationRequests = 0;
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
    public double getFailedVmsRetryDelay() {
        return failedVmsRetryDelay;
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
     * @param failedVmsRetryDelay
     */
    public void setFailedVmsRetryDelay(double failedVmsRetryDelay) {
        this.failedVmsRetryDelay = failedVmsRetryDelay;
    }

    public int getMaxVmCreationRetries() {
        return maxVmCreationRetries;
    }

    public void setMaxVmCreationRetries(int maxVmCreationRetries) {
        this.maxVmCreationRetries = maxVmCreationRetries;
    }

    /**
     * Checks if the broker has to retry allocating VMs
     * that couldn't be placed due to lack of suitable Hosts.
     * @return
     */
    public boolean isRetryFailedVms() {
        return failedVmsRetryDelay > 0 && currentVmCreationRetries < maxVmCreationRetries;
    }

    public void incrementCurrentVmCreationRetries() {
        this.currentVmCreationRetries++;
    }

    public int getCurrentVmCreationRetries() {
        return currentVmCreationRetries;
    }

    public void setCurrentVmCreationRetries(int currentVmCreationRetries) {
        this.currentVmCreationRetries = currentVmCreationRetries;
    }

    /**
     * Gets the number of VM creation requests.
     *
     * @return the number of VM creation requests
     */
    public int getVmCreationRequests() {
        return vmCreationRequests;
    }

    public void setVmCreationRequests(int vmCreationRequests) {
        this.vmCreationRequests = vmCreationRequests;
    }

}
