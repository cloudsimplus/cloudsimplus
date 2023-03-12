package org.cloudbus.cloudsim.power.models;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.util.MathUtil;

/**
 * Abstract implementation of a host power model.
 *
 * @since CloudSim Plus 6.0.0
 * @see org.cloudbus.cloudsim.power.PowerMeter
 */
@Accessors(makeFinal = false)
public abstract class PowerModelHost implements PowerModel {
    /**
     * An attribute that implements the Null Object Design Pattern for {@link PowerModelHost}
     * objects.
     */
    public static final PowerModelHost NULL = new PowerModelHostNull();

    /**
     * The Host this PowerModel is collecting power consumption measurements from.
     */
    @Getter @Setter
    private Host host;

    /**
     * Get the delay (in seconds) for starting up the {@link Host}.
     */
    @Getter
    private double startupDelay;

    /**
     * The delay (in seconds) for shutting down the {@link Host}.
     */
    @Getter
    private double shutDownDelay;

    /**
     * The power consumed (in Watts) for starting up the {@link Host}.
     */
    @Getter
    private double startupPower;

    /**
     * The power consumed (in Watts) for shutting down the {@link Host}.
     */
    @Getter
    private double shutDownPower;

    /**
     * The total power consumed (in Watts) during all the times the {@link Host} was powered on.
     * If the Host has never started up, returns zero.
     */
    @Getter
    private double totalStartupPower;

    /**
     * The total power consumed (in Watts) during all the times the {@link Host} was powered off.
     * If the Host has never started up then shutdown, returns zero.
     */
    @Getter
    private double totalShutDownPower;

    /**
     * The total time (in seconds) the {@link Host} spent during startup.
     * If the Host starts up multiple times, the time spent is summed up.
     * @see #getTotalStartups()
     */
    @Getter
    private double totalStartupTime;

    /**
     * The total time (in seconds) the {@link Host} spent during shut down.
     * If the Host shuts down multiple times, the time spent is summed up.
     */
    @Getter
    private double totalShutDownTime;

    /**
     * The number of times the Host has started up.
     * @see #getTotalStartupTime()
     */
    @Getter
    private int totalStartups;

    /**
     * Checks if a power value (in Watts) is valid.
     * @param power the value to validate
     * @param fieldName the name of the field/variable storing the value
     * @return the given power if it's valid
     * @throws IllegalArgumentException when the value is smaller than 1
     */
    protected static double validatePower(final double power, final String fieldName) {
        MathUtil.nonNegative(power, fieldName);

        final var s = "%s must be in watts. A value smaller than 1 may indicate you're trying to give a percentage value instead.";
        if(power < 1){
            throw new IllegalArgumentException(s.formatted(fieldName));
        }

        return power;
    }

    /**
     * Computes the hosts power usage in Watts (W) at a certain degree of utilization.
     * Mainly for backwards compatibility.
     *
     * @param utilizationFraction the utilization percentage (between [0 and 1]) of the host.
     * @return the power supply in Watts (W)
     * @throws IllegalArgumentException if utilizationFraction is not between [0 and 1]
     */
    public final double getPower(final double utilizationFraction) {
        MathUtil.percentage(utilizationFraction, "utilizationFraction");
        return getPowerInternal(utilizationFraction);
    }

    /**
     * @see #getPower(double)
     */
    protected abstract double getPowerInternal(double utilizationFraction);

    /**
     * Set the delay (in seconds) for starting up the {@link Host}.
     */
    public PowerModelHost setStartupDelay(final double delay) {
        this.startupDelay = MathUtil.nonNegative(delay, "Startup Delay");
        return this;
    }

    /**
     * Set the delay (in seconds) for shutting down the {@link Host}.
     */
    public PowerModelHost setShutDownDelay(final double delay) {
        this.shutDownDelay = MathUtil.nonNegative(delay, "Shutdown Delay");
        return this;
    }

    /**
     * Set the power consumed (in Watts) for starting up the {@link Host}.
     */
    public PowerModelHost setStartupPower(final double power) {
        this.startupPower = validatePower(power, "Power");
        return this;
    }

    /**
     * Set the power consumed (in Watts) for shutting down the {@link Host}.
     */
    public PowerModelHost setShutDownPower(final double power) {
        this.shutDownPower = validatePower(power, "Power");
        return this;
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
     * After the Host is powered off, adds the consumed power to the total shutdown power.
     * If the Host is powered on/off multiple times, that power consumed is summed up.
     */
    public void addShutDownTotals() {
        totalShutDownPower += shutDownPower;
        totalShutDownTime += shutDownDelay;
    }
}
