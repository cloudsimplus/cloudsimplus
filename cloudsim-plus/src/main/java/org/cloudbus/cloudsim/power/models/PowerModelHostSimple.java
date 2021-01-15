package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.power.PowerMeasurement;

/**
 * Simple power model for hosts with linear power profile.
 * @since CloudSim Plus 6.0.0
 */
public class PowerModelHostSimple extends PowerModelHost {
    private double maxPower;
    private double staticPower;

    /**
     * Instantiates a {@link PowerModelHostSimple} by specifying its static and max power usage.
     *
     * @param maxPower power (in watts) the host consumes under full load.
     * @param staticPower power (in watts) the host consumes when idle.
     */
    public PowerModelHostSimple(final double maxPower, final double staticPower) {
        if (maxPower < staticPower) {
            throw new IllegalArgumentException("maxPower has to be bigger than staticPower");
        }

        this.maxPower = validatePower(maxPower, "maxPower");
        this.staticPower = validatePower(staticPower, "staticPower");
    }

    @Override
    public PowerMeasurement getPowerMeasurement() {
        if(!getHost().isActive()){
            return new PowerMeasurement();
        }

        final double utilizationFraction = getHost().getCpuMipsUtilization() / getHost().getTotalMipsCapacity();
        return new PowerMeasurement(staticPower, (maxPower - staticPower) * utilizationFraction);
    }

    /**
     * Computes the hosts power usage in Watts (W) at a certain degree of utilization.
     * Mainly for backwards compatibility.
     *
     * @param utilizationFraction the utilization percentage (between [0 and 1]) of the host.
     * @return the power supply in Watts (W)
     * @throws IllegalArgumentException if utilizationFraction is not between [0 and 1]
     */
    @Override
    public double getPower(final double utilizationFraction) throws IllegalArgumentException {
        if (utilizationFraction < 0 || utilizationFraction > 1) {
            throw new IllegalArgumentException("utilizationFraction has to be between [0 and 1]");
        }

        return staticPower + (maxPower - staticPower) * utilizationFraction;
    }

    /**
     * Checks if a power value (in Watts) is valid.
     * @param power the value to validate
     * @param fieldName the name of the field/variable storing the value
     * @return the given power if it's valid
     * @throws IllegalArgumentException when the value is smaller than 1
     */
    private static double validatePower(final double power, final String fieldName) {
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
     * Gets the maximum power (in watts) the host consumes under full load.
     * @return
     */
    public double getMaxPower() {
        return maxPower;
    }

    /**
     * Gets the static power (in watts) the host consumes when idle.
     *
     * @return
     */
    public double getStaticPower() {
        return staticPower;
    }
}
