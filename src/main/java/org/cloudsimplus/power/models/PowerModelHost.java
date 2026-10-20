/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.power.models;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.power.PowerMeter;

/// Provides a model for defining [Host] power consumption.
///
/// @since CloudSim Plus 6.0.0
/// @see PowerMeter
public sealed interface PowerModelHost
    extends PowerModel
    permits PowerModelHostAbstract, PowerModelHostNull
{
    /// An attribute that implements the Null Object Design Pattern for [PowerModelHost] objects.
    PowerModelHost NULL = new PowerModelHostNull();

    /// Computes the hosts power usage in Watts (W) at a certain degree of utilization.
    /// Mainly for backwards compatibility.
    ///
    /// @param utilizationFraction the utilization percentage (between \[0 and 1\]) of the host.
    /// @return the power supply in Watts (W)
    /// @throws IllegalArgumentException if utilizationFraction is not between \[0 and 1\]
    double getPower(double utilizationFraction);

    /// Set the power consumed (in Watts) for starting up the [Host].
    PowerModelHost setStartupPower(double power);

    /// Set the power consumed (in Watts) for shutting down the [Host].
    PowerModelHost setShutDownPower(double power);

    /// After the Host is powered on, adds the consumed power to the total startup power.
    /// If the Host is powered on/off multiple times, that power consumed is summed up.
    void addStartupTotals();

    /// After the Host is powered off, adds the consumed power to the total shutdown power.
    /// If the Host is powered on/off multiple times, that power consumed is summed up.
    void addShutDownTotals();

    /// The Host this PowerModel is collecting power consumption measurements from.
    Host getHost();

    /// The power consumed (in Watts) for starting up the [Host].
    double getStartupPower();

    /// The power consumed (in Watts) for shutting down the [Host].
    double getShutDownPower();

    /// The total power consumed (in Watts) during all the times the [Host] was powered on.
    /// If the Host has never started up, returns zero.
    double getTotalStartupPower();

    /// The total power consumed (in Watts) during all the times the [Host] was powered off.
    /// If the Host has never started up then shutdown, returns zero.
    double getTotalShutDownPower();

    /// The total time (in seconds) the [Host] spent during startup.
    /// If the Host starts up multiple times, the time spent is summed up.
    /// @see #getTotalStartups()
    /// @see Host#getStartupDelay()
    double getTotalStartupTime();

    /// The total time (in seconds) the [Host] spent during shut down.
    /// If the Host shuts down multiple times, the time spent is summed up.
    /// @see Host#getShutDownDelay()
    double getTotalShutDownTime();

    /// The number of times the Host has started up.
    /// @see #getTotalStartupTime()
    int getTotalStartups();

    PowerModelHost setHost(org.cloudsimplus.hosts.Host host);
}
