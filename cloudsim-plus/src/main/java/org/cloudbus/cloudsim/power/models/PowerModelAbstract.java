package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.hosts.power.PowerHost;

import java.util.Objects;

/**
 * An abstract implementation of a {@link PowerModel}.
 *
 * @author raysaoliveira
 * @since CloudSim Plus 1.2.0
 */
public abstract class PowerModelAbstract implements PowerModel {
    private PowerHost host;

    @Override
    public PowerHost getHost() {
        return host;
    }

    @Override
    public final void setHost(PowerHost host) {
        Objects.requireNonNull(host);
        this.host = host;
    }

    @Override
    public final double getPower(double utilization) throws IllegalArgumentException {
		if (utilization < 0 || utilization > 1) {
			throw new IllegalArgumentException(
                String.format(
                    "Utilization value must be between 0 and 1. The given value was %.2f",
                    utilization));
		}

        /**
         * If the Host is not active and there are running VMs, it means
         * that a shutdown request was sent (setting the Host active attribute to false).
         * However, the Host will be powered off just when running VMs finish.
         * If there aren't VMs and the Host is not active anymore,
         * its power consumption is zero.
         */
		if(getHost().getVmList().isEmpty() && !getHost().isActive()){
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
}
