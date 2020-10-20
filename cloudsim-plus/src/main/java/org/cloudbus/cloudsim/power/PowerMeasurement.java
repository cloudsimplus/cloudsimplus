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

    /** @see #getStaticUsage() */
    private double staticUsage;

    /** @see #getDynamicUsage() */
    private double dynamicUsage;

    /**
     * Instantiates a power measurement with a given static and dynamic power consumption.
     * @param staticUsage power (in watts) the entity consumes when idle
     * @param dynamicUsage power (in watts) the entity consumes according to its load
     */
    public PowerMeasurement(final double staticUsage, final double dynamicUsage) {
        this.staticUsage = validatePower(staticUsage, "staticPower");
        this.dynamicUsage = validatePower(dynamicUsage, "maxPower");
    }

    /**
     * Checks if a power value (in Watts) is valid.
     * @param power the value to validate
     * @param fieldName the name of the field/variable storing the value
     * @return the given power if it's valid
     * @throws IllegalArgumentException when the value is smaller than 1
     */
    public static double validatePower(final double power, final String fieldName) {
        if (power < 0) {
            throw new IllegalArgumentException(fieldName+" cannot be negative");
        }

        if(power < 1){
            throw new IllegalArgumentException(
                fieldName +
                " must be in watts. A value smaller than 1 may indicate you're trying to give a percentage value instead.");
        }

        return power;
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
     * Gets the dynamic power the entity consumes according to its load (in Watts).
     * @return
     */
    public double getDynamicUsage() {
        return dynamicUsage;
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
            staticUsage + measurement.getStaticUsage(),
            dynamicUsage + measurement.getDynamicUsage()
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
            staticUsage * factor,
            dynamicUsage * factor
        );
    }
}
