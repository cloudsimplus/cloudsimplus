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
package org.cloudsimplus.core;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.vms.Vm;

/**
 * Represents objects that may have a defined lifetime set (such as {@link Cloudlet} and {@link Vm}),
 * indicating how long they must live.
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 8.2.0
 */
public interface Lifetimed extends Startable {
    /**
     * Gets lifetime which indicates the maximum execution time
     * @see #setLifeTime(double)
     * @return the lifeTime (in seconds) or {@link Double#MAX_VALUE} indicating no lifeTime is set
     */
    double getLifeTime();

    /**
     * Sets the lifetime which indicates the maximum execution time.
     * The lifetime must be larger than {@link Datacenter#getSchedulingInterval()}.
     *
     * @param lifeTime lifeTime to set (in seconds) or {@link Double#MAX_VALUE} to indicate there is no lifeTime
     */
    Lifetimed setLifeTime(double lifeTime);

    /**
     * @return true to indicate the lifetime is reached, false otherwise.
     * @see #setLifeTime(double)
     */
    default boolean isLifeTimeReached(){
        return getTotalExecutionTime() >= getLifeTime();
    }
}
