package org.cloudbus.cloudsim.vms;

import org.cloudbus.cloudsim.core.Machine;
import org.cloudbus.cloudsim.datacenters.Datacenter;

import java.util.SortedMap;

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
     * Gets a <b>read-only</b> CPU utilization percentage history map
     * where each key is the time the utilization was collected and
     * each value is the utilization percentage (between [0 and 1]).
     * There will be at least one entry for each time multiple of the {@link Datacenter#getSchedulingInterval()}.
     * <b>This way, it's required to set a Datacenter scheduling interval with the desired value.</b>
     *
     * @return
     */
    SortedMap<Double, Double> getHistory();

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

    Vm getVm();
}
