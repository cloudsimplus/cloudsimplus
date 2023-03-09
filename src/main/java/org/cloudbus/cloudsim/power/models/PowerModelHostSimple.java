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
package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.power.PowerMeasurement;
import org.cloudbus.cloudsim.util.MathUtil;

/**
 * Simple power model for hosts with linear power profile.
 * @since CloudSim Plus 6.0.0
 * @see org.cloudbus.cloudsim.power.PowerMeter
 */
public class PowerModelHostSimple extends PowerModelHost {
    private final double maxPower;
    private final double staticPower;

    /**
     * Instantiates a {@link PowerModelHostSimple} by specifying its static and max power usage.
     *
     * @param maxPower power (in watts) the host consumes under full load.
     * @param staticPower power (in watts) the host consumes when idle.
     */
    public PowerModelHostSimple(final double maxPower, final double staticPower) {
        super();
        if (maxPower < staticPower) {
            throw new IllegalArgumentException("maxPower has to be bigger than staticPower");
        }

        this.maxPower = validatePower(maxPower, "maxPower");
        this.staticPower = validatePower(staticPower, "staticPower");
    }

    @Override
    public PowerMeasurement getPowerMeasurement() {
        final var host = getHost();
        if(!host.isActive()){
            return new PowerMeasurement();
        }

        final double usageFraction = host.getCpuMipsUtilization() / host.getTotalMipsCapacity();
        return new PowerMeasurement(staticPower, dynamicPower(usageFraction));
    }

    /**
     * Computes the host current power usage in Watts (W) at a certain degree of utilization
     * (mainly for backwards compatibility).
     *
     * @param utilizationFraction the utilization percentage (between [0 and 1]) of the host.
     * @return the power supply in Watts (W)
     * @throws IllegalArgumentException if utilizationFraction is not between [0 and 1]
     */
    @Override
    public double getPower(final double utilizationFraction) throws IllegalArgumentException {
        MathUtil.percentage(utilizationFraction, "utilizationFraction");
        return staticPower + dynamicPower(utilizationFraction);
    }

    /**
     * Computes the dynamic power consumed according to the CPU utilization percentage.
     * @param utilizationFraction the utilization percentage (between [0 and 1]) of the host.
     * @return the dynamic power supply in Watts (W)
     */
    private double dynamicPower(final double utilizationFraction) {
        return (maxPower - staticPower) * utilizationFraction;
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
