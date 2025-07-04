package org.cloudsimplus.power.models;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.power.PowerMeter;

/**
 * Provides a model for defining {@link Host} power consumption.
 *
 * @since CloudSim Plus 6.0.0
 * @see PowerMeter
 */
public sealed interface PowerModelHost
    extends PowerModel
    permits PowerModelHostAbstract, PowerModelHostNull
{
    /**
     * An attribute that implements the Null Object Design Pattern for {@link PowerModelHost} objects.
     */
    PowerModelHost NULL = new PowerModelHostNull();

    /**
     * Computes the hosts power usage in Watts (W) at a certain degree of utilization.
     * Mainly for backwards compatibility.
     *
     * @param utilizationFraction the utilization percentage (between [0 and 1]) of the host.
     * @return the power supply in Watts (W)
     * @throws IllegalArgumentException if utilizationFraction is not between [0 and 1]
     */
    double getPower(double utilizationFraction);

    /**
     * Set the power consumed (in Watts) for starting up the {@link Host}.
     */
    PowerModelHost setStartupPower(double power);

    /**
     * Set the power consumed (in Watts) for shutting down the {@link Host}.
     */
    PowerModelHost setShutDownPower(double power);

    /**
     * After the Host is powered on, adds the consumed power to the total startup power.
     * If the Host is powered on/off multiple times, that power consumed is summed up.
     */
    void addStartupTotals();

    /**
     * After the Host is powered off, adds the consumed power to the total shutdown power.
     * If the Host is powered on/off multiple times, that power consumed is summed up.
     */
    void addShutDownTotals();

    /**
     * The Host this PowerModel is collecting power consumption measurements from.
     */
    Host getHost();

    /**
     * The power consumed (in Watts) for starting up the {@link Host}.
     */
    double getStartupPower();

    /**
     * The power consumed (in Watts) for shutting down the {@link Host}.
     */
    double getShutDownPower();

    /**
     * The total power consumed (in Watts) during all the times the {@link Host} was powered on.
     * If the Host has never started up, returns zero.
     */
    double getTotalStartupPower();

    /**
     * The total power consumed (in Watts) during all the times the {@link Host} was powered off.
     * If the Host has never started up then shutdown, returns zero.
     */
    double getTotalShutDownPower();

    /**
     * The total time (in seconds) the {@link Host} spent during startup.
     * If the Host starts up multiple times, the time spent is summed up.
     * @see #getTotalStartups()
     * @see Host#getStartupDelay()
     */
    double getTotalStartupTime();

    /**
     * The total time (in seconds) the {@link Host} spent during shut down.
     * If the Host shuts down multiple times, the time spent is summed up.
     * @see Host#getShutDownDelay()
     */
    double getTotalShutDownTime();

    /**
     * The number of times the Host has started up.
     * @see #getTotalStartupTime()
     */
    int getTotalStartups();

    PowerModelHost setHost(org.cloudsimplus.hosts.Host host);
}
