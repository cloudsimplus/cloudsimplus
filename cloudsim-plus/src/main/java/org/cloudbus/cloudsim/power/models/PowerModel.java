package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.power.PowerMeasurement;

/**
 * Common interface for PowerModels.
 *
 * A PowerModel computes the current power usage of an entity during the simulation.
 */
public abstract class PowerModel {

    /**
     * Returns the entity's current power usage as a PowerMeasurement,
     * which can hold additional information like static and dynamic fraction
     * of power usage.
     */
    public abstract PowerMeasurement getPowerMeasurement();

    /**
     * Returns the entity's current total power usage as a double.
     */
    public double getPower() {
        return getPowerMeasurement().getTotalUsage();
    }

}

