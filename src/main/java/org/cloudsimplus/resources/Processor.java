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

import org.cloudsimplus.core.Machine;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.vms.Vm;

/// A Central Processing Unit (CPU) of a [Machine] ([Host], [Vm] or a future container),
/// having one or more cores (cores) with the same MIPS capacity.
/// Instead of keeping one object per core, it keeps just the number of cores in each [CoreStatus].
///
/// The [capacity][#getCapacity()] is the total number of cores (in any status),
/// the [available resource][#getAvailableResource()] is the number of [free][CoreStatus#FREE] cores
/// and the [allocated resource][#getAllocatedResource()] is the number of [busy][CoreStatus#BUSY] cores.
/// The capacity and the number of cores in each status are always whole cores, never a fraction of a core.
///
/// The [MIPS][#getMips()] is the speed of each core.
/// A limit on how much of the cores a machine can use (such as a VM CPU cap or a container CPU quota)
/// is not part of the processor, but defined by a [CpuCap].
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.0
public sealed interface Processor extends ResourceManageable permits ProcessorSimple, ProcessorNull {
    /// An attribute that implements the Null Object Design Pattern for [Processor] objects.
    Processor NULL = new ProcessorNull();

    /// @return the [Machine] to which the processor belongs to.
    Machine<?> getMachine();

    /// @return the individual MIPS of each core.
    double getMips();

    /// Sets the individual MIPS of each core.
    /// @param newMips the new MIPS of each core
    void setMips(double newMips);

    /// @return the current total MIPS capacity of the processor.
    /// How it's computed is defined by each implementation.
    double getTotalMips();

    /// @param status the status to get the number of cores
    /// @return the number of cores in a given status.
    long getCores(CoreStatus status);

    /// @return the number of [free][CoreStatus#FREE] cores, which can be allocated.
    default long getFreeCores() {
        return getCores(CoreStatus.FREE);
    }

    /// @return the number of [busy][CoreStatus#BUSY] cores.
    default long getBusyCores() {
        return getCores(CoreStatus.BUSY);
    }

    /// @return the number of working (non-failed) cores.
    default long getWorkingCores() {
        return getCapacity() - getCores(CoreStatus.FAILED);
    }

    /// @return the number of failed cores.
    default long getFailedCores() {
        return getCores(CoreStatus.FAILED);
    }

    /// Moves a given number of cores from one status to another.
    /// @param from the current status of the cores to move
    /// @param to the new status of the cores
    /// @param cores the number of cores to move
    /// @return true if the cores were moved;
    ///         false if the number of cores is not positive,
    ///         both statuses are equal or there aren't enough cores in the `from` status
    boolean moveCores(CoreStatus from, CoreStatus to, long cores);
}
