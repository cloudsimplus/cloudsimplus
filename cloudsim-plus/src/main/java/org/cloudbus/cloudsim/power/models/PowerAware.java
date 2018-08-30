package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.resources.Pe;

/**
 * An interface for power-aware components such as {@link Datacenter}
 * and {@link PowerModel}.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 2.2.1
 */
public interface PowerAware {
    /**
     * Gets the current power consumption in Watt-Second (Ws).
     * For this moment, it only computes the power consumed by {@link Pe}s.
     *
     * @return the power consumption in Watt-Second (Ws)
     * @see #getPowerInKWattsHour()
     */
    double getPower();

    /**
     * Gets the current power consumption in Kilowatt-hour (kWh).
     * For this moment, it only computes the power consumed by {@link Pe}s.
     *
     * @return the power consumption Kilowatt-hour (kWh)
     * @see #getPower()
     */
    default double getPowerInKWattsHour() {
        return wattsSecToKWattsHour(getPower());
    }

    /**
     * Converts from Watts-Second to Kilowatt-hour (kWh).
     * @param power the value in Watts-Second
     * @return the value converted to Kilowatt-hour (kWh)
     */
    static double wattsSecToKWattsHour(final double power) {
        return power / (3600.0 * 1000.0);
    }

}
