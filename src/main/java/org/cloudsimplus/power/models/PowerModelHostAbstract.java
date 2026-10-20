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

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.power.PowerMeter;
import org.cloudsimplus.util.MathUtil;

/// Abstract implementation of a [Host] [PowerModel].
///
/// @since CloudSim Plus 6.0.0
/// @see PowerMeter
@Accessors(makeFinal = false) @Getter
public abstract non-sealed class PowerModelHostAbstract implements PowerModelHost {
    @Setter
    private Host host = Host.NULL;
    private double startupPower;
    private double shutDownPower;
    private double totalStartupPower;
    private double totalShutDownPower;
    private double totalStartupTime;
    private double totalShutDownTime;
    private int totalStartups;

    /// Checks if a power value (in Watts) is valid.
    /// @param power the value to validate (in Watts)
    /// @param fieldName the name of the field/variable storing the value
    /// @return the given power if it's valid
    /// @throws IllegalArgumentException when the value is smaller than 1
    protected static double validatePower(final double power, final String fieldName) {
        MathUtil.nonNegative(power, fieldName);

        final var s = "%s must be in watts. A value smaller than 1 may indicate you're trying to give a percentage value instead.";
        if(power < 1){
            throw new IllegalArgumentException(s.formatted(fieldName));
        }

        return power;
    }

    @Override
    public final double getPower(final double utilizationFraction) {
        MathUtil.percentage(utilizationFraction, "utilizationFraction");
        return getPowerInternal(utilizationFraction);
    }

    /// @see #getPower(double)
    protected abstract double getPowerInternal(double utilizationFraction);

    @Override
    public PowerModelHost setStartupPower(final double power) {
        this.startupPower = validatePower(power, "Power");
        return this;
    }

    @Override
    public PowerModelHost setShutDownPower(final double power) {
        this.shutDownPower = validatePower(power, "Power");
        return this;
    }

    @Override
    public void addStartupTotals() {
        totalStartupPower += startupPower;
        totalStartupTime += host.getStartupDelay();
        totalStartups++;
    }

    @Override
    public void addShutDownTotals() {
        totalShutDownPower += shutDownPower;
        totalShutDownTime += host.getShutDownDelay();
    }
}
