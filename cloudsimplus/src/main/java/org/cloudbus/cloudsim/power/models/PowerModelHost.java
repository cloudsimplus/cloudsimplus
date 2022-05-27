package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.hosts.Host;

import java.util.Objects;

/**
 * Abstract implementation of a host power model.
 *
 * @since CloudSim Plus 6.0.0
 * @see org.cloudbus.cloudsim.power.PowerMeter
 */
public abstract class PowerModelHost implements PowerModel {
    /**
     * An attribute that implements the Null Object Design Pattern for {@link PowerModelHost}
     * objects.
     */
    public static final PowerModelHost NULL = new PowerModelHostNull();

    private Host host;

    /**
     * @see #getStartupDelay()
     */
    private double startupDelay;

    /**
     * @see #getShutDownDelay()
     */
    private double shutDownDelay;

    /**
     * @see #getStartupPower()
     */
    private double startupPower;

    /** @see #getTotalStartupPower() */
    private double totalStartupPower;

    /**
     * @see #getShutDownPower()
     */
    private double shutDownPower;

    /** @see #getTotalShutDownPower() */
    private double totalShutDownPower;

    /** @see #getTotalStartupTime() */
    private double totalStartupTime;

    /** @see #getTotalShutDownTime() */
    private double totalShutDownTime;

    /** @see #getTotalStartups() */
    private int totalStartups;

    /**
     * Checks if a power value (in Watts) is valid.
     * @param power the value to validate
     * @param fieldName the name of the field/variable storing the value
     * @return the given power if it's valid
     * @throws IllegalArgumentException when the value is smaller than 1
     */
    protected static double validatePower(final double power, final String fieldName) {
        positive(power, fieldName);

        if(power < 1){
            throw new IllegalArgumentException(
                fieldName +
                    " must be in watts. A value smaller than 1 may indicate you're trying to give a percentage value instead.");
        }

        return power;
    }

    protected static double positive(final double value, final String fieldName) {
        if (value < 0)
            throw new IllegalArgumentException(fieldName + " cannot be negative.");

        return value;
    }

    /**
     * Gets the Host this PowerModel is collecting power consumption measurements from.
     *
     * @return
     */
    public Host getHost() {
        return host;
    }

    /**
     * Sets the Host this PowerModel will collect power consumption measurements from.
     *
     * @param host the Host to set
     * @return
     */
    public void setHost(final Host host) {
        this.host = Objects.requireNonNull(host);
    }

    /**
     * Computes the hosts power usage in Watts (W) at a certain degree of utilization.
     * Mainly for backwards compatibility.
     *
     * @param utilizationFraction the utilization percentage (between [0 and 1]) of the host.
     * @return the power supply in Watts (W)
     * @throws IllegalArgumentException if utilizationFraction is not between [0 and 1]
     */
    public abstract double getPower(double utilizationFraction) throws IllegalArgumentException;

    /**
     * Get the delay (in seconds) for starting up the {@link Host}.
     */
    public double getStartupDelay() {
        return startupDelay;
    }

    /**
     * Set the delay (in seconds) for starting up the {@link Host}.
     */
    public PowerModelHost setStartupDelay(final double delay) {
        this.startupDelay = positive(delay, "Delay");
        return this;
    }

    /**
     * Get the delay (in seconds) for shutting down the {@link Host}.
     */
    public double getShutDownDelay() {
        return shutDownDelay;
    }

    /**
     * Set the delay (in seconds) for shutting down the {@link Host}.
     */
    public PowerModelHost setShutDownDelay(final double delay) {
        this.shutDownDelay = positive(delay, "Delay");
        return this;
    }

    /**
     * Get the power consumed (in Watts) for starting up the {@link Host}.
     */
    public double getStartupPower() {
        return startupPower;
    }

    /**
     * Set the power consumed (in Watts) for starting up the {@link Host}.
     */
    public PowerModelHost setStartupPower(final double power) {
        this.startupPower = validatePower(power, "Power");
        return this;
    }

    /**
     * Get the power consumed (in Watts) for shutting down the {@link Host}.
     */
    public double getShutDownPower() {
        return shutDownPower;
    }

    /**
     * Set the power consumed (in Watts) for shutting down the {@link Host}.
     */
    public PowerModelHost setShutDownPower(final double power) {
        this.shutDownPower = validatePower(power, "Power");
        return this;
    }

    /**
     * Get the total power consumed (in Watts) during all the times the {@link Host} was powered on.
     * If the Host has never started up, returns zero.
     */
    public double getTotalStartupPower() {
        return totalStartupPower;
    }

    /**
     * After the Host is powered on, adds the consumed power to the total startup power.
     * If the Host is powered on/off multiple times, that power consumed is summed up.
     */
    public void addStartupTotals() {
        totalStartupPower += startupPower;
        totalStartupTime += startupDelay;
        totalStartups++;
    }

    /**
     * Get the total power consumed (in Watts) during all the times the {@link Host} was powered off.
     * If the Host has never started up then shutdown, returns zero.
     */
    public double getTotalShutDownPower() {
        return totalShutDownPower;
    }

    /**
     * After the Host is powered off, adds the consumed power to the total shutdown power.
     * If the Host is powered on/off multiple times, that power consumed is summed up.
     */
    public void addShutDownTotals() {
        totalShutDownPower += shutDownPower;
        totalShutDownTime += shutDownDelay;
    }

    /**
     * Gets the number of times the Host has started up.
     * @return
     * @see #getTotalStartupTime()
     */
    public int getTotalStartups() {
        return totalStartups;
    }

    /**
     * Get the total time (in seconds) the {@link Host} spent during startup.
     * If the Host starts up multiple times, the time spent is summed up.
     * @see #getTotalStartups()
     */
    public double getTotalStartupTime() {
        return totalStartupTime;
    }

    /**
     * Get the total time (in seconds) the {@link Host} spent during shut down.
     * If the Host shuts down multiple times, the time spent is summed up.
     */
    public double getTotalShutDownTime() {
        return totalShutDownTime;
    }
}
