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

import org.cloudsimplus.brokers.DatacenterBroker;

/**
 * An interface for objects that have a Unique Identifier (UID)
 * that is compounded by a {@link DatacenterBroker} ID
 * and the object ID.
 *
 * @author Manoel Campos da Silva Filho
 */
public interface UniquelyIdentifiable extends Identifiable {
    /**
     * Generates an Unique Identifier (UID).
     *
     * @param brokerId the id of the {@link DatacenterBroker}
     * @param id the object id
     * @return the generated UID
     */
    static String getUid(long brokerId, long id) {
        return brokerId + "-" + id;
    }

    /**
     * @return the Unique Identifier (UID) for the entity, that is compounded by the id
     *         of a {@link DatacenterBroker} and the object id.
     */
    String getUid();
}
