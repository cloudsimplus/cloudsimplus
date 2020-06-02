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
 */
public final class HostStateHistoryEntry {

    /**
     * @see #getTime()
     */
    private final double time;

    /**
     * @see #getAllocatedMips()
     */
    private final double allocatedMips;

    /**
     * @see #getRequestedMips()
     */
    private final double requestedMips;

    /**
     * @see #isActive()
     */
    private final boolean active;

    /**
     * Instantiates a host state history entry.
     *
     * @param time          the time the data in this history entry is related to
     * @param allocatedMips the total MIPS allocated from all PEs of the Host, to running VMs, at the recorded time
     * @param requestedMips the total MIPS requested by running VMs to all PEs of the Host at the recorded time
     * @param active        if the Host is active at the given time
     */
    public HostStateHistoryEntry(final double time, final double allocatedMips, final double requestedMips, final boolean active) {
        this.time = time;
        this.allocatedMips = allocatedMips;
        this.requestedMips = requestedMips;
        this.active = active;
    }

    /**
     * Gets the time the data in this history entry is related to.
     *
     * @return
     */
    public double getTime() {
        return time;
    }

    /**
     * Gets the total MIPS allocated from all PEs of the Host, to running VMs, at the recorded time.
     *
     * @return the allocated mips
     */
    public double getAllocatedMips() {
        return allocatedMips;
    }

    /**
     * Gets the total MIPS requested by running VMs to all PEs of the Host at the recorded time.
     *
     * @return the requested mips
     */
    public double getRequestedMips() {
        return requestedMips;
    }

    /**
     * Gets the percentage (in scale from 0 to 1) of allocated MIPS from the total requested.
     * @return
     */
    public double getPercentUsage(){
        return requestedMips > 0 ? allocatedMips/requestedMips : 0;
    }

    /**
     * Checks if the Host is/was active at the recorded time.
     *
     * @return true if is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return String.format("Time: %6.1f | Requested: %10.0f MIPS | Allocated: %10.0f MIPS | Used: %3.0f%% Host Active: %s%n",
                            time, requestedMips, allocatedMips, getPercentUsage()*100, active);
    }
}
