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
package org.cloudsimplus.hosts;

import org.cloudsimplus.core.CloudSimTag;

/// A request to change the capacity of a [Host] during simulation runtime,
/// sent as the data of a [CloudSimTag#HOST_UPDATE] event.
/// The capacity of a resource isn't changed when it's lower than what is in use, since VMs aren't evicted.
///
/// @param hostId the ID of the Host to update
/// @param cores the new number of CPU cores or [#KEEP_CURRENT]
/// @param ram the new RAM capacity (in MB) or [#KEEP_CURRENT]
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
public record HostCapacityUpdate(long hostId, long cores, long ram) {
    /// Indicates the capacity of a resource must not be changed.
    public static final long KEEP_CURRENT = -1;
}
