package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.hosts.Host;

import java.util.Objects;

/**
 * An abstract implementation of a {@link PowerModel}.
 *
 * @author raysaoliveira
 * @since CloudSim Plus 1.2.0
 */
public abstract class PowerModelAbstract implements PowerModel {
    private Host host;

    @Override
    public Host getHost() {
        return host;
    }

    @Override
    public final void setHost(final Host host) {
        this.host = Objects.requireNonNull(host);
    }

    @Override
    public double getPower() {
        return getPower(host.getUtilizationOfCpu());
    }

    @Override
    public final double getPower(final double utilization) throws IllegalArgumentException {
		if (utilization < 0 || utilization > 1) {
			throw new IllegalArgumentException(
                String.format(
                    "Utilization value must be between 0 and 1. The given value was %.2f",
                    utilization));
		}

		if(!host.isActive()){
		    return 0;
        }

        return getPowerInternal(utilization);
    }

    /**
     * An internal method to be implemented by sub classes
     * to get the power consumption for the current CPU utilization.
     * <p>The basic parameter validation is performed by the {@link #getPower(double)} method.</p>
     *
     * @param utilization the utilization percentage (between [0 and 1]) of a
     * resource that is critical for power consumption.
     * @return the power consumption
     * @throws IllegalArgumentException when the utilization percentage is not
     * between [0 and 1]
     */
    protected abstract double getPowerInternal(double utilization) throws IllegalArgumentException;

    @Override
    public double getEnergyLinearInterpolation(
        final double fromUtilization,
        final double toUtilization,
        final double time)
    {
        if(!host.isActive()) {
            return 0;
        }

        final double fromPower = getPower(fromUtilization);
        final double toPower = getPower(toUtilization);
        return (fromPower + (toPower - fromPower) / 2) * time;
    }
}
