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
package org.cloudsimplus.resources;

import lombok.Getter;
import lombok.experimental.Accessors;

/// The status of the cores of a [Processor].
/// Each status declares its behaviour in the constructor,
/// so that a new status doesn't compile until it says how it behaves.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
@Accessors(fluent = true) @Getter
public enum CoreStatus {
    /// The core is working and free for allocation.
    FREE(true, true),

    /// The core is working and allocated, so it's busy processing some workload.
    BUSY(true, false),

    /// The core is failed, so it can't process any workload at this moment.
    FAILED(false, false);

    /// Indicates if a core in this status is working (not failed).
    private final boolean working;

    /// Indicates if a core in this status can be allocated.
    private final boolean allocatable;

    CoreStatus(final boolean working, final boolean allocatable) {
        this.working = working;
        this.allocatable = allocatable;
    }
}
