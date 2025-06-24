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

import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.power.models.PowerModel;

/**
 * Interface for power-aware entities such as {@link Host}s, {@link Datacenter}s
 * and other entities that can be introduced.
 *
 * @since CloudSim Plus 6.0.0
 * @param <T> type of the {@link PowerModel} objects the entity will use.
 */
public interface PowerAware<T extends PowerModel> {
    /**
     * @return the model defining how the entity consumes power.
     */
    T getPowerModel();

    /**
     * Sets the model defining how the entity consumes power.
     * @param powerModel the model to set
     */
    PowerAware<T> setPowerModel(T powerModel);
}
