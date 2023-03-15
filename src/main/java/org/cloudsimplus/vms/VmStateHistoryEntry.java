/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2011, The University of Melbourne, Australia
 */
package org.cloudsimplus.vms;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Historic data about requests and allocation of MIPS for a given VM over the time.
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.1.2
 */
@Getter @EqualsAndHashCode
public class VmStateHistoryEntry {
    /**
     * The time the state information is being collected (in seconds).
     */
    private double time;

    /**
     * The allocated MIPS.
     */
    private double allocatedMips;

    /**
     * The requested MIPS.
     */
    private double requestedMips;

    /**
     * Checks if the Vm is in migration for the current history.
     */
    private boolean inMigration;

    /**
     * Instantiates a VmStateHistoryEntry
     *
     * @param time the time the state information is being collected.
     * @param allocatedMips the allocated mips
     * @param requestedMips the requested mips
     * @param inMigration if the VM was in migration for that time
     */
    public VmStateHistoryEntry(final double time, final double allocatedMips, final double requestedMips, final boolean inMigration) {
        setTime(time);
        setAllocatedMips(allocatedMips);
        setRequestedMips(requestedMips);
        setInMigration(inMigration);
    }

    /**
     * Sets the time the state information is being collected (in seconds).
     *
     * @param time the new time
     */
    protected final void setTime(final double time) {
        this.time = time;
    }

    /**
     * Sets the allocated mips.
     *
     * @param allocatedMips the new allocated mips
     */
    protected final void setAllocatedMips(final double allocatedMips) {
        this.allocatedMips = allocatedMips;
    }

    /**
     * Sets the requested mips.
     *
     * @param requestedMips the new requested mips
     */
    protected final void setRequestedMips(final double requestedMips) {
        this.requestedMips = requestedMips;
    }

    /**
     * Defines if the Vm is in migration for the current history.
     *
     * @param inMigration true if the Vm is in migration, false otherwise
     */
    protected final void setInMigration(final boolean inMigration) {
        this.inMigration = inMigration;
    }
}
