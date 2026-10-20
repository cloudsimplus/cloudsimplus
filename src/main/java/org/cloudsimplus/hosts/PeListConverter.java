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

import lombok.NonNull;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.Processor;

import java.util.List;

/// Converts a deprecated [Pe] list into the number of PEs and MIPS used to create
/// the [Processor] of a [Host].
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
/// @deprecated Only used by the deprecated [Host] constructors that receive a [Pe] list.
@Deprecated(forRemoval = true)
final class PeListConverter {
    private PeListConverter() {/**/}

    /// @param peList the PE list to get the number of PEs
    /// @return the number of PEs in the list
    /// @throws IllegalArgumentException if the list is empty or the PEs have different MIPS capacities
    static long pesNumber(@NonNull final List<Pe> peList) {
        validate(peList);
        return peList.size();
    }

    /// @param peList the PE list to get the MIPS from
    /// @return the MIPS capacity of each PE
    /// @throws IllegalArgumentException if the list is empty or the PEs have different MIPS capacities
    static double mips(@NonNull final List<Pe> peList) {
        validate(peList);
        return peList.getFirst().getCapacity();
    }

    private static void validate(final List<Pe> peList) {
        if (peList.isEmpty()) {
            throw new IllegalArgumentException("The PE list for a Host cannot be empty");
        }

        final long mips = peList.getFirst().getCapacity();
        if (peList.stream().anyMatch(pe -> pe.getCapacity() != mips)) {
            throw new IllegalArgumentException("All PEs of a Host must have the same MIPS capacity");
        }
    }
}
