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

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.vms.Vm;

import static org.cloudsimplus.util.MathUtil.nonNegative;

/// A number of CPU cores, each one with the same MIPS capacity.
/// It represents the capacity of a [Processor] (such as the one of a [Host] or [Vm])
/// or the MIPS requested by or allocated to a [Vm].
///
/// Since it's immutable, objects that keep a capacity (such as a [Processor])
/// should copy its values instead of storing the object.
///
/// @param cores the number of cores
/// @param mips the MIPS capacity of each core
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
public record CoreCapacity(long cores, double mips) {
    /// A capacity with no cores and no MIPS.
    public static final CoreCapacity NULL = new CoreCapacity(0, 0);

    /// The smallest capacity a [Processor] can have: a single core with no MIPS
    /// (such as for machines created with no resources).
    public static final CoreCapacity MINIMUM = new CoreCapacity(1, 0);

    public CoreCapacity {
        nonNegative(cores, "Number of cores");
        nonNegative(mips, "MIPS");
    }

    /// Creates a capacity with 1 core having a given MIPS capacity.
    /// @param mips the MIPS capacity of the core
    public CoreCapacity(final double mips) {
        this(1, mips);
    }

    /// @return the total MIPS capacity of all cores.
    public double totalMips() {
        return cores * mips;
    }

    /// @return true if there are no cores or no MIPS capacity; false otherwise
    public boolean isEmpty() {
        return cores == 0 || mips == 0;
    }

    /// Gets a capacity without a given number of cores.
    /// It doesn't remove more cores than there are.
    /// @param count the number of cores to remove
    /// @return a new capacity without the removed cores, or this one if no core was removed
    public CoreCapacity withoutCores(final long count) {
        final long removed = Math.min(nonNegative(count, "Number of cores to remove"), cores);
        return withCores(cores - removed);
    }

    /// Gets a capacity with a given number of cores and the same MIPS of each core.
    /// @param newCores the number of cores of the new capacity
    /// @return this capacity if it already has the given number of cores; a new one otherwise
    public CoreCapacity withCores(final long newCores) {
        return newCores == cores ? this : new CoreCapacity(newCores, mips);
    }

    /// Gets a capacity having at most a given number of cores.
    /// @param maxCores the maximum number of cores
    /// @return this capacity if it doesn't have more cores than the given maximum;
    ///         a new one with that maximum otherwise
    public CoreCapacity limitCores(final long maxCores) {
        return cores <= maxCores ? this : withCores(maxCores);
    }

    /// Gets a capacity where each core has at most a given MIPS.
    /// @param maxMips the maximum MIPS of each core
    /// @return this capacity if its MIPS doesn't exceed the given maximum;
    ///         a new one with that maximum otherwise
    public CoreCapacity limitMips(final double maxMips) {
        return mips <= maxMips ? this : new CoreCapacity(cores, maxMips);
    }

    /// Gets a capacity with the MIPS of each core multiplied by a given factor.
    /// @param factor the factor to multiply the MIPS of each core (such as a percentage from 0 to 1)
    /// @return this capacity if the factor is 1; a new one otherwise
    public CoreCapacity scaleMips(final double factor) {
        return factor == 1 ? this : new CoreCapacity(cores, mips * factor);
    }
}
