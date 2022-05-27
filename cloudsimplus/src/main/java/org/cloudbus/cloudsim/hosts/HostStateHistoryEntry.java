/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2011, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.hosts;

/**
 * Keeps historic CPU utilization data about a host.
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.1.2
 * @param time          the time the data in this history entry is related to
 * @param allocatedMips the total MIPS allocated from all PEs of the Host, to running VMs, at the recorded time
 * @param requestedMips the total MIPS requested by running VMs to all PEs of the Host at the recorded time
 * @param active        if the Host is active at the given time
 */
public record HostStateHistoryEntry(double time, double allocatedMips, double requestedMips, boolean active) {

    /**
     * Gets the time the data in this history entry is related to.
     *
     * @return
     */
    public double time() {
        return time;
    }

    /**
     * Gets the total MIPS allocated from all PEs of the Host, to running VMs, at the recorded time.
     *
     * @return the allocated mips
     */
    public double allocatedMips() {
        return allocatedMips;
    }

    /**
     * Gets the total MIPS requested by running VMs to all PEs of the Host at the recorded time.
     *
     * @return the requested mips
     */
    public double requestedMips() {
        return requestedMips;
    }

    /**
     * Gets the percentage (in scale from 0 to 1) of allocated MIPS from the total requested.
     *
     * @return
     */
    public double percentUsage() {
        return requestedMips > 0 ? allocatedMips / requestedMips : 0;
    }

    /**
     * Checks if the Host is/was active at the recorded time.
     *
     * @return true if is active, false otherwise
     */
    public boolean active() {
        return active;
    }

    @Override
    public String toString() {
        final var msg = "Time: %6.1f | Requested: %10.0f MIPS | Allocated: %10.0f MIPS | Used: %3.0f%% Host Active: %s%n";
        return String.format(msg, time, requestedMips, allocatedMips, percentUsage() * 100, active);
    }
}
