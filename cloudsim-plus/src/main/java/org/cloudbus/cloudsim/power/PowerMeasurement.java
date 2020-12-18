package org.cloudbus.cloudsim.power;

import org.cloudbus.cloudsim.power.models.PowerModel;

import java.util.Objects;

/**
 * Power consumption measurement produced by a {@link PowerModel},
 * consisting of a static and a dynamic fraction (in Watts).
 * This measurement is performed on the entity the PowerModel is assigned to.
 *
 * <p>This is an <b>immutable</b> class providing operations
 * such as {@link #add(PowerMeasurement)} and {@link #multiply(double)} that returns a new instance.</p>
 * @since CloudSim Plus 6.0.0
 */
public class PowerMeasurement {

    /** @see #getStaticPower() */
    private double staticPower;

    /** @see #getDynamicPower() */
    private double dynamicPower;

    /**
     * Instantiates a power measurement with a given static and dynamic power consumption.
     * @param staticPower power (in watts) the entity consumes when idle
     * @param dynamicPower power (in watts) the entity consumes according to its load
     */
    public PowerMeasurement(final double staticPower, final double dynamicPower) {
        this.staticPower = staticPower;
        this.dynamicPower = dynamicPower;
    }

    /**
     * Instantiates a power measurement with zero static and dynamic power consumption.
     */
    public PowerMeasurement() {
        this(0, 0);
    }

    /**
     * Gets the total power consumed by the entity (in Watts)
     * @return
     */
    public double getTotalPower() {
        return staticPower + dynamicPower;
    }

    /**
     * Gets the static power the entity consumes even if it's idle (in Watts).
     * @return
     */
    public double getStaticPower() {
        return staticPower;
    }

    /**
     * Gets the dynamic power the entity consumes according to its load (in Watts).
     * @return
     */
    public double getDynamicPower() {
        return dynamicPower;
    }

    /**
     * Adds up the values from the given measurement and this one,
     * returning a new instance.
     * @param measurement another measurement to add its values with this instance
     * @return the new instance with the added up values
     */
    public PowerMeasurement add(final PowerMeasurement measurement) {
        Objects.requireNonNull(measurement, "measurement cannot be null");
        return new PowerMeasurement(
            staticPower + measurement.getStaticPower(),
            dynamicPower + measurement.getDynamicPower()
        );
    }

    /**
     * Multiplies the values from this measurement by a given factor,
     * returning a new instance.
     * @param factor the factor to multiply the values of this measurement
     * @return the new instance with the multiplied values
     */
    public PowerMeasurement multiply(final double factor) {
        return new PowerMeasurement(
            staticPower * factor,
            dynamicPower * factor
        );
    }
}
