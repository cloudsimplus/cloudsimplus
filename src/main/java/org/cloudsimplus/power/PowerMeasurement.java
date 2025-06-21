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
package org.cloudsimplus.power;

import lombok.Getter;
import lombok.NonNull;
import org.cloudsimplus.power.models.PowerModel;

/**
 * Power consumption measurement produced by a {@link PowerModel},
 * consisting of a static and a dynamic fraction (in Watts).
 * This measurement is performed on the entity that the PowerModel assigned to.
 *
 * <p>This is an <b>immutable</b> class providing operations
 * such as {@link #add(PowerMeasurement)} and {@link #multiply(double)} that return a new instance.</p>
 * @since CloudSim Plus 6.0.0
 */
@Getter
public class PowerMeasurement {

    /**
     * The static power the entity consumes even if it's idle (in Watts).
     */
    private final double staticPower;

    /**
     * The dynamic power the entity consumes, according to its CPU load (in Watts).
     */
    private final double dynamicPower;

    /**
     * Instantiates a power measurement with given static and dynamic power consumption.
     * @param staticPower power (in watts) the entity consumes when idle
     * @param dynamicPower power (in watts) the entity consumes, according to its CPU load
     */
    public PowerMeasurement(final double staticPower, final double dynamicPower) {
        this.staticPower = staticPower;
        this.dynamicPower = dynamicPower;
    }

    /**
     * Instantiates a power measurement with zero static and dynamic power consumption.
     */
    public PowerMeasurement() {
        this(0, 0);
    }

    /**
     * @return the total power consumed by the entity (in Watts)
     */
    public double getTotalPower() {
        return staticPower + dynamicPower;
    }

    /**
     * Adds up the values from the given measurement and this one,
     * returning a new instance.
     * @param measurement another measurement to add its values with this instance
     * @return the new instance with the added up values
     */
    public PowerMeasurement add(@NonNull final PowerMeasurement measurement) {
        return new PowerMeasurement(
            staticPower + measurement.getStaticPower(),
            dynamicPower + measurement.getDynamicPower()
        );
    }

    /**
     * Multiplies the values from this measurement by a given factor,
     * returning a new instance.
     * @param factor the factor to multiply the values of this measurement
     * @return the new instance with the multiplied values
     */
    public PowerMeasurement multiply(final double factor) {
        return new PowerMeasurement(staticPower * factor, dynamicPower * factor);
    }
}
