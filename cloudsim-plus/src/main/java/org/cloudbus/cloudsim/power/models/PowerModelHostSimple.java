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

        if(maxPower < 0){
            throw new IllegalArgumentException("maxPower cannot be negative");
        }

        if(staticPower < 0){
            throw new IllegalArgumentException("staticPower cannot be negative");
        }

        if(maxPower < 1){
            throw new IllegalArgumentException("maxPower is in watts. A value smaller than 1 may indicate you're trying to give a percentage value instead.");
        }

        if(staticPower < 1){
            throw new IllegalArgumentException("staticPower is in watts. A value smaller than 1 may indicate you're trying to give a percentage value instead.");
        }

        this.maxPower = maxPower;
        this.staticPower = staticPower;
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

}
