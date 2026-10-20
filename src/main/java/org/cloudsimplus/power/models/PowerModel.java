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

import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.power.PowerMeasurement;

/**
 * A common interface for implementing models of power consumption
 * for entities such as {@link Datacenter}s and {@link Host}s.
 * A PowerModel computes the current power usage (in Watts) of an entity during the simulation.
 * @since CloudSim Plus 6.0.0
 */
public interface PowerModel {
    /**
     * @return the entity's current power usage as a {@link PowerMeasurement} object,
     * which can hold additional information like static and dynamic fraction
     * of power usage.
     * @see #getPower()
     */
    PowerMeasurement getPowerMeasurement();

    /**
     * @return the entity's current total power usage as a double value,
     * representing the Watts consumed.
     * @see #getPowerMeasurement()
     */
    default double getPower() {
        return getPowerMeasurement().getTotalPower();
    }
}

