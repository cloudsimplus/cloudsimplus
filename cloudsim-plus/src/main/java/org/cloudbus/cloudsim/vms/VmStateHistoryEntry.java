/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2011, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.vms;

/**
 * Historic data about requests and allocation of MIPS for a given VM over the time.
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.1.2
 */
public class VmStateHistoryEntry {

    /**
     * The time.
     */
    private double time;

    /**
     * The allocated mips.
     */
    private double allocatedMips;

    /**
     * The requested mips.
     */
    private double requestedMips;

    /**
     * The is in migration.
     */
    private boolean inMigration;

    /**
     * Instantiates a new VmStateHistoryEntry
     *
     * @param time the time
     * @param allocatedMips the allocated mips
     * @param requestedMips the requested mips
     * @param inMigration the is in migration
     */
    public VmStateHistoryEntry(double time, double allocatedMips, double requestedMips, boolean inMigration) {
        setTime(time);
        setAllocatedMips(allocatedMips);
        setRequestedMips(requestedMips);
        setInMigration(inMigration);
    }

    /**
     * Sets the time.
     *
     * @param time the new time
     */
    protected final void setTime(double time) {
        this.time = time;
    }

    /**
     * Gets the time.
     *
     * @return the time
     */
    public double getTime() {
        return time;
    }

    /**
     * Sets the allocated mips.
     *
     * @param allocatedMips the new allocated mips
     */
    protected final void setAllocatedMips(double allocatedMips) {
        this.allocatedMips = allocatedMips;
    }

    /**
     * Gets the allocated mips.
     *
     * @return the allocated mips
     */
    public double getAllocatedMips() {
        return allocatedMips;
    }

    /**
     * Sets the requested mips.
     *
     * @param requestedMips the new requested mips
     */
    protected final void setRequestedMips(double requestedMips) {
        this.requestedMips = requestedMips;
    }

    /**
     * Gets the requested mips.
     *
     * @return the requested mips
     */
    public double getRequestedMips() {
        return requestedMips;
    }

    /**
     * Defines if the Vm is in migration for the current history.
     *
     * @param inMigration true if the Vm is in migration, false otherwise
     */
    protected final void setInMigration(boolean inMigration) {
        this.inMigration = inMigration;
    }

    /**
     * Checks if the Vm is in migration for the current history.
     *
     * @return true if the Vm is in migration, false otherwise
     */
    public boolean isInMigration() {
        return inMigration;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof VmStateHistoryEntry)) {
            return false;
        }

        final VmStateHistoryEntry entry = (VmStateHistoryEntry)obj;

        return entry.time == this.time &&
               entry.inMigration == this.inMigration &&
               entry.allocatedMips == this.allocatedMips &&
               entry.requestedMips == this.requestedMips;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = hash(hash, toBits(this.time));
        hash = hash(hash, toBits(this.allocatedMips));
        hash = hash(hash, toBits(this.requestedMips));
        hash = hash(hash, this.inMigration ? 1 : 0);
        return hash;
    }

    private int hash(final int hash, final int value) {
        return 89 * hash + value;
    }

    private int toBits(final double value){
        return (int) (Double.doubleToLongBits(value) ^ (Double.doubleToLongBits(value) >>> 32));
    }
}
