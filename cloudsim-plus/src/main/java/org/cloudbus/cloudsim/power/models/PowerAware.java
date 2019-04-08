package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.datacenters.Datacenter;

/**
 * An interface for power-aware components such as {@link Datacenter}
 * and {@link PowerModel}.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 2.2.1
 */
public interface PowerAware {
    /**
     * Gets the current power supply in Watts (w).
     *
     * @return the power supply in Watts (w)
     * @see #getPowerInKWatts()
     */
    double getPower();

    /**
     * Gets the current power supply in Kilowatts (kW).
     *
     * @return the power supply Kilowatts (kW)
     * @see #getPower()
     */
    default double getPowerInKWatts() {
        return getPower()/1000.0;
    }

    /**
     * Converts energy consumption from Watts-Second to Kilowatt-hour (kWh).
     * @param power the value in Watts-Second
     * @return the value converted to Kilowatt-hour (kWh)
     */
    static double wattsSecToKWattsHour(final double power) {
        return power / (3600.0 * 1000.0);
    }

}
