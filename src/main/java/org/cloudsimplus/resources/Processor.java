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
/// having one or more cores with the same MIPS capacity.
/// Instead of keeping one object per core, it keeps just the number of cores in each [CoreStatus].
///
/// The [capacity][#getCapacity()] is the total number of cores (in any status),
/// the [available resource][#getFree()] is the number of [free][CoreStatus#FREE] cores
/// and the [allocated resource][#getOccupied()] is the number of [busy][CoreStatus#BUSY] cores.
/// The capacity and the number of cores in each status are always whole cores, never a fraction of a core.
/// The processor always keeps `capacity == free + busy + failed`
/// (so `capacity == available + allocated` only if there are no failed cores).
/// When all cores are failed, there is no free core, so [#isFull()] returns true.
///
/// All changes in the number of cores in each status go through [#moveCores(CoreStatus, CoreStatus, long)]:
/// the [ResourceManageable] allocation methods (such as [#occupy(long)])
/// just move cores between the free and busy statuses.
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

    /// Gets the total number of cores of a machine's processor.
    /// It avoids spreading `machine.getProcessor().getCores()` chains
    /// across client classes, such as in `vmList.stream().mapToLong(Processor::cores)`.
    /// @param machine the [Machine] to get the number of cores
    /// @return the total number of cores, in any status (including failed cores)
    /// @see #getCores()
    static long cores(final Machine<?> machine) {
        return machine.getProcessor().getCores();
    }

    /// @param machine the [Machine] to get the number of working cores
    /// @return the number of working (non-failed) cores of the machine's processor
    /// @see #cores(Machine)
    /// @see #getWorkingCores()
    static long workingCores(final Machine<?> machine) {
        return machine.getProcessor().getWorkingCores();
    }

    /// @param machine the [Machine] to get the number of free cores
    /// @return the number of [free][CoreStatus#FREE] cores of the machine's processor
    /// @see #cores(Machine)
    /// @see #getFreeCores()
    static long freeCores(final Machine<?> machine) {
        return machine.getProcessor().getFreeCores();
    }

    /// @param machine the [Machine] to get the MIPS of each core
    /// @return the individual MIPS of each core of the machine's processor
    /// @see #cores(Machine)
    /// @see #getMips()
    static double mips(final Machine<?> machine) {
        return machine.getProcessor().getMips();
    }

    /// @return the individual MIPS of each core.
    double getMips();

    /// Sets the individual MIPS of each core.
    /// It can only be called before the processor's [Machine] has started,
    /// since MIPS shares allocated after that (such as the ones of VMs placed into a Host)
    /// depend on the current MIPS. Changing the MIPS while the simulation runs
    /// (such as for Dynamic Voltage and Frequency Scaling, DVFS) must be done by an operation
    /// that also reallocates such shares.
    /// @param newMips the new MIPS of each core
    /// @throws IllegalStateException if the processor's Machine has already started
    void setMips(double newMips);

    /// @return the current total MIPS capacity of the processor,
    /// which is the [MIPS of each core][#getMips()] multiplied by the [number of working cores][#getWorkingCores()].
    double getTotalMips();

    /// @return a [CoreCapacity] with the [total number of cores][#getCores()] and the [MIPS of each core][#getMips()]
    /// (such as to create another processor with the same configuration).
    default CoreCapacity toCoreCapacity() {
        return new CoreCapacity(getCores(), getMips());
    }

    /// @return the total number of cores, in any status (including failed cores).
    /// It's the same as the processor [capacity][#getCapacity()].
    default long getCores() {
        return getCapacity();
    }

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

    /// @return the MIPS of each core that can run work, or 0 if all cores have failed.
    default double getWorkingCoreMips() {
        return getWorkingCores() == 0 ? 0 : getMips();
    }

    /// @return the number of failed cores.
    default long getFailedCores() {
        return getCores(CoreStatus.FAILED);
    }

    /// Sets a given number of working cores as [failed][CoreStatus#FAILED].
    /// [Free][CoreStatus#FREE] cores fail first: since the processor just counts its cores in each status,
    /// a failed busy core is replaced by a free one (like a hypervisor moving the work to another core).
    /// Only when there are no free cores left, [busy][CoreStatus#BUSY] cores fail.
    /// That way, the number of busy cores doesn't change while there are free cores.
    /// @param cores the number of cores to fail
    /// @return the number of cores that failed, which is the given number limited to the number of working cores
    default long failCores(final long cores) {
        final long failures = Math.min(Math.max(cores, 0), getWorkingCores());
        final long freeFailures = Math.min(failures, getFreeCores());
        moveCores(CoreStatus.FREE, CoreStatus.FAILED, freeFailures);
        moveCores(CoreStatus.BUSY, CoreStatus.FAILED, failures - freeFailures);
        return failures;
    }

    /// Sets all working cores (free and busy) as [failed][CoreStatus#FAILED].
    default void failAllCores() {
        failCores(getWorkingCores());
    }

    /// Sets all [failed][CoreStatus#FAILED] cores as [free][CoreStatus#FREE].
    default void recoverAllCores() {
        moveCores(CoreStatus.FAILED, CoreStatus.FREE, getFailedCores());
    }

    /// Moves a given number of cores from one status to another.
    /// @param from the current status of the cores to move
    /// @param to the new status of the cores
    /// @param cores the number of cores to move
    /// @return true if the cores were moved;
    ///         false if the number of cores is not positive,
    ///         both statuses are equal, there aren't enough cores in the `from` status
    ///         or the cores in the `from` status can't be [allocated][CoreStatus#allocatable()] (when moving to busy)
    boolean moveCores(CoreStatus from, CoreStatus to, long cores);
}
