package org.cloudbus.cloudsim.vms;

import org.cloudbus.cloudsim.core.Machine;
import org.cloudbus.cloudsim.datacenters.Datacenter;

import java.util.List;

/**
 * Stores resource utilization data for a specific {@link Machine}.
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.4
 */
public interface UtilizationHistory {
    /**
     * An attribute that implements the Null Object Design Pattern for {@link UtilizationHistory}
     * objects.
     */
    UtilizationHistory NULL = new UtilizationHistoryNull();

    /**
     * The maximum number of entries that will be stored.
     */
    int DEF_MAX_HISTORY_ENTRIES = 30;

    /**
     * Gets the utilization Median Absolute Deviation (MAD) in MIPS.
     *
     * @return
     */
    double getUtilizationMad();

    /**
     * Gets the utilization mean in MIPS.
     *
     * @return
     */
    double getUtilizationMean();

    /**
     * Gets the utilization variance in MIPS.
     *
     * @return the utilization variance in MIPS
     */
    double getUtilizationVariance();

    /**
     * Adds a CPU utilization percentage history value related to the current simulation time,
     * to the beginning of the History List.
     * <b>The value is added only if the utilization history {@link #isEnabled()}.</b>
     *
     * @param time the current simulation time
     */
    void addUtilizationHistory(double time);

    /**
     * Gets a <b>read-only</b> CPU utilization percentage history (between [0 and 1], where 1 is 100%).
     * Each value into the returned array is the CPU utilization percentage for
     * a time interval equal to the {@link Datacenter#getSchedulingInterval()}.
     *
     * <p><b>The values are stored in the reverse chronological order.</b></p>
     *
     * @return
     */
    List<Double> getHistory();

    /**
     * Gets the previous time that cloudlets were processed.
     *
     * @return
     */
    double getPreviousTime();

    /**
     * Sets the previous time that cloudlets were processed.
     *
     * @param previousTime the new previous time
     */
    void setPreviousTime(double previousTime);

    /**
     * Checks if the object is enabled to add data to the history.
     * @return
     */
    boolean isEnabled();

    /**
     * Enables the history so that utilization data can be added to it.
     */
    void enable();

    /**
     * Disables the history to avoid utilization data to be added to it.
     * That allows to reduce memory usage since no utilization
     * data will be collected.
     */
    void disable();

    /**
     * Gets the maximum number of entries to store in the history.
     * @return
     */
    int getMaxHistoryEntries();

    /**
     * Sets the maximum number of entries to store in the history.
     * @param maxHistoryEntries the value to set
     */
    void setMaxHistoryEntries(int maxHistoryEntries);

}
