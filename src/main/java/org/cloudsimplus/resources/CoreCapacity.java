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

import lombok.NonNull;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.utilizationmodels.BootModel;
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

    public CoreCapacity {
        nonNegative(cores, "Number of cores");
        nonNegative(mips, "MIPS");
    }

    /// Creates a capacity with 1 core having a given MIPS capacity.
    /// @param mips the MIPS capacity of the core
    public CoreCapacity(final double mips) {
        this(1, mips);
    }

    /// Creates a capacity with the number of cores and MIPS of a given [Processor], with no [CpuCap].
    /// @param processor the processor to get its capacity
    /// @return the new capacity
    public static CoreCapacity of(@NonNull final Processor processor) {
        return of(processor, CpuCap.NONE);
    }

    /// Creates a capacity with the number of cores and MIPS of a given [Processor], limited by a [CpuCap].
    /// @param processor the processor to get its capacity
    /// @param cap the CPU cap that limits the MIPS of each processor core
    /// @return the new capacity
    public static CoreCapacity of(@NonNull final Processor processor, @NonNull final CpuCap cap) {
        return of(processor, cap, 1);
    }

    /// Creates a capacity with the number of cores and MIPS of a given [Processor], limited by a [CpuCap],
    /// that is used during a VM boot process.
    /// @param processor the VM processor
    /// @param cap the CPU cap that limits the MIPS of each processor core
    /// @param bootModel the VM boot model that defines how the boot process uses resources
    /// @return the new capacity
    public static CoreCapacity of(@NonNull final Processor processor, @NonNull final CpuCap cap, @NonNull final BootModel bootModel) {
        return of(processor, cap, bootModel.getCpuPercentUtilization());
    }

    /// Creates a capacity using a percentage of the MIPS of a given [Processor], limited by a [CpuCap].
    /// @param processor the processor to get its capacity
    /// @param cap the CPU cap that limits the MIPS of each processor core
    /// @param cpuPercent the percentage of the MIPS of each core to use (from 0 to 1)
    /// @return the new capacity
    private static CoreCapacity of(final Processor processor, final CpuCap cap, final double cpuPercent) {
        return new CoreCapacity(processor.getCapacity(), cap.limitCoreMips(processor) * cpuPercent);
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
        return removed == 0 ? this : new CoreCapacity(cores - removed, mips);
    }
}
