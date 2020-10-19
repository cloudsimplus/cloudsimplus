package org.cloudbus.cloudsim.power;

import org.cloudbus.cloudsim.power.models.PowerModel;

/**
 * Power consumption measurement produced by a {@link PowerModel},
 * consisting of a static and a dynamic fraction (in Watts).
 * This measurement is related to the entity the PowerModel is related to,
 *
 * <p>This is an <b>immutable</b> class providing operations
 * such as {@link #add(PowerMeasurement)} and {@link #multiply(double)} that returns a new instance.</p>
 * @since CloudSim Plus 6.0.0
 */
public class PowerMeasurement {

    /** @see #getStaticUsage() */
    private double staticUsage;

    /** @see #getDynamicUsage() */
    private double dynamicUsage;

    /**
     * Instantiates a power measurement with a given static and dynamic power consumption.
     */
    public PowerMeasurement(double staticUsage, double dynamicUsage) {
        this.staticUsage = staticUsage;
        this.dynamicUsage = dynamicUsage;
    }

    /**
     * Instantiates a power measurement with zero static and dynamic power consumption.
     */
    public PowerMeasurement() {
        this(0, 0);
    }

    /**
     * Gets the total power consumed by the
     * @return
     */
    public double getTotalUsage() {
        return staticUsage + dynamicUsage;
    }

    /**
     * Gets the static power the entity consumes even if it's idle (in Watts).
     * @return
     */
    public double getStaticUsage() {
        return staticUsage;
    }

    /**
     * Gets the dynamic power the entity consumes (in Watts).
     * @return
     */
    public double getDynamicUsage() {
        return dynamicUsage;
    }

    public PowerMeasurement add(PowerMeasurement measurement) {
        return new PowerMeasurement(
            staticUsage + measurement.getStaticUsage(),
            dynamicUsage + measurement.getDynamicUsage()
        );
    }

    public PowerMeasurement multiply(double factor) {
        return new PowerMeasurement(
            staticUsage * factor,
            dynamicUsage * factor
        );
    }

}
