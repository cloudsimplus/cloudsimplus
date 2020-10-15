package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.power.PowerMeasurement;

/**
 * Power model for data center hosts.
 */
public class PowerModelHost extends PowerModel {

    public static PowerModelHost NULL = new PowerModelHost(0 ,0) {
        @Override public PowerMeasurement getPowerMeasurement() { return new PowerMeasurement(); }
    };

    private Host host;

    private double staticPower;
    private double maxPower;

    /**
     * Instantiates a {@link PowerModelHost} by specifying its static and max power usage.
     *
     * @param maxPower power (in watts) the host consumes under full load.
     * @param staticPower power (in watts) the host consumes when idle.
     */
    public PowerModelHost(final double maxPower, final double staticPower) {
        if (maxPower < staticPower) {
            throw new IllegalArgumentException("maxPower has to be bigger than staticPower");
        }
        this.maxPower = maxPower;
        this.staticPower = staticPower;
    }

    @Override
    public PowerMeasurement getPowerMeasurement() {
        if(!getHost().isActive()){
            return new PowerMeasurement();
        }

        double utilizationFraction = host.getCpuMipsUtilization() / host.getTotalMipsCapacity();
        return new PowerMeasurement(staticPower, (maxPower - staticPower) * utilizationFraction);
    }

    /**
     * Computes the hosts power usage in Watts (W) at a certain degree of utilization.
     * Mainly for backwards compatibility.
     *
     * @param utilizationFraction the utilization percentage (between [0 and 1]) of
     * the host.
     * @return the power supply in Watts (W)
     * @throws IllegalArgumentException if utilizationFraction is not between [0 and 1]
     */
    public double getPower(double utilizationFraction) throws IllegalArgumentException {
        if (utilizationFraction < 0 || utilizationFraction > 1) {
            throw new IllegalArgumentException("utilizationFraction has to be between [0 and 1]");
        }
        return staticPower + (maxPower - staticPower) * utilizationFraction;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

}
