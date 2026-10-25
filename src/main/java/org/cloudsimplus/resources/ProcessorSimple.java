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
import lombok.NonNull;
import org.cloudsimplus.core.Machine;
import org.cloudsimplus.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// A [Processor] implementation that can be used by any [Machine],
/// keeping only counters for the number of cores in each [CoreStatus].
/// The number of [free][CoreStatus#FREE] cores is the [available resource][#getFree()],
/// the number of [failed][CoreStatus#FAILED] cores is stored in a field and the
/// number of [busy][CoreStatus#BUSY] cores is the remaining ones.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
public final class ProcessorSimple extends ResourceManageableAbstract implements Processor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorSimple.class.getSimpleName());

    /// The unit of the processor capacity.
    private static final String UNIT = "Cores";

    /// The machine the processor belongs to,
    /// used to check if the [MIPS can be changed][#setMips(double)] and for logging.
    private final Machine<?> machine;

    /// The individual MIPS of each core.
    @Getter
    private double mips;

    /// @see #getFailedCores()
    private long failedCores;

    /// Instantiates a Processor for a given Machine.
    ///
    /// @param machine the [Machine] to which the processor will belong to
    /// @param capacity the number of cores (the processor [capacity][#getCapacity()]) and the MIPS of each core.
    ///                 Its values are copied, so the object is not kept.
    /// @throws IllegalArgumentException if the number of cores is lower than 1
    public ProcessorSimple(@NonNull final Machine<?> machine, @NonNull final CoreCapacity capacity) {
        super(validateCores(capacity.cores()), UNIT);
        this.machine = machine;
        this.mips = MathUtil.nonNegative(capacity.mips(), "MIPS");
    }

    private static long validateCores(final long cores) {
        if(cores < 1){
            throw new IllegalArgumentException("A Processor must have at least 1 core.");
        }

        return cores;
    }

    @Override
    public double getTotalMips() {
        return getMips() * getWorkingCores();
    }

    @Override
    public void setMips(final double newMips) {
        if(machine.hasStarted()) {
            throw new IllegalStateException(
                "The MIPS of %s can't be changed after it has started, since MIPS shares depend on it.".formatted(machine));
        }

        this.mips = MathUtil.nonNegative(newMips, "MIPS");
    }

    @Override
    public long getFailedCores() {
        return failedCores;
    }

    /// @return the number of [busy][CoreStatus#BUSY] cores
    @Override
    public long getOccupied() {
        return getCapacity() - getFree() - failedCores;
    }

    /// Sets the number of cores of the Processor.
    /// It can be zero, such as when all cores of a VM are removed due to Host core failures.
    /// @param cores the number of cores to set
    /// @return true if the capacity was set;
    ///         false if it's negative or lower than the number of busy and failed cores
    @Override
    public boolean setCapacity(final long cores) {
        if(cores < getOccupied() + failedCores) {
            return false;
        }

        return super.setCapacity(cores);
    }

    /// Allocates a given number of free cores, making them busy.
    /// @param cores the number of cores to allocate
    /// @return true if the cores were allocated; false if there aren't enough free cores
    @Override
    public boolean occupy(final long cores) {
        return cores > 0 && getFreeCores() >= cores && moveCores(CoreStatus.FREE, CoreStatus.BUSY, cores);
    }

    /// Deallocates a given number of busy cores, making them free.
    /// @param cores the number of cores to deallocate
    /// @return true if the cores were deallocated; false if there aren't enough busy cores
    @Override
    public boolean free(final long cores) {
        return cores > 0 && getBusyCores() >= cores && moveCores(CoreStatus.BUSY, CoreStatus.FREE, cores);
    }

    /// Sets the number of busy cores, moving the difference from or to the free cores.
    /// @param busyCores the number of busy cores to set
    /// @return true if the number of busy cores was set;
    ///         false if it's negative or greater than the number of working cores
    @Override
    public boolean setOccupied(final long busyCores) {
        if(busyCores < 0 || busyCores > getWorkingCores()) {
            return false;
        }

        final long diff = busyCores - getBusyCores();
        return diff == 0 || (diff > 0 ? occupy(diff) : free(-diff));
    }

    /// Removes a given number of working cores from the processor,
    /// first deallocating busy cores if there aren't enough free cores to remove.
    /// @param cores the number of cores to remove
    /// @return true if the cores were removed; false if there aren't enough working cores
    @Override
    public boolean freeAndRemove(final long cores) {
        if(cores <= 0 || cores > getWorkingCores()) {
            return false;
        }

        final long busyToFree = cores - getFreeCores();
        if(busyToFree > 0) {
            free(busyToFree);
        }

        return setCapacity(getCapacity() - cores);
    }

    @Override
    public long freeAll() {
        final long previousAllocated = getBusyCores();
        free(previousAllocated);
        return previousAllocated;
    }

    @Override
    public long getCores(@NonNull final CoreStatus status) {
        return switch (status) {
            case FREE -> getFree();
            case BUSY -> getOccupied();
            case FAILED -> failedCores;
        };
    }

    @Override
    public boolean moveCores(@NonNull final CoreStatus from, @NonNull final CoreStatus to, final long cores) {
        if(cores <= 0 || from == to || isNotAllocatable(from, to)) {
            return false;
        }

        if(getCores(from) < cores) {
            logNotEnoughCoresToMove(from, to, cores);
            return false;
        }

        sumCores(from, -cores);
        sumCores(to, cores);
        return true;
    }

    /// Checks if a move makes cores busy from a status whose cores can't be allocated.
    /// @param from the current status of the cores to move
    /// @param to the new status of the cores
    /// @return true if the cores can't be moved to [busy][CoreStatus#BUSY]; false otherwise
    private static boolean isNotAllocatable(final CoreStatus from, final CoreStatus to) {
        return to == CoreStatus.BUSY && !from.allocatable();
    }

    private void logNotEnoughCoresToMove(final CoreStatus from, final CoreStatus to, final long cores) {
        LOGGER.warn(
            "{}: {}: Unable to move {} cores from {} to {} status, since there are just {} {} cores.",
            machine.getSimulation().clockStr(), machine, cores, from, to, getCores(from), from);
    }

    /// Sums a given number of cores (negative or positive) to the counter of a given status.
    /// Since the number of busy cores is the remaining ones, nothing is stored for them.
    ///
    /// It doesn't check if the counter of the given status has enough cores to remove
    /// (or room for the cores to add). That is validated by [#moveCores(CoreStatus, CoreStatus, long)],
    /// which is the only caller of this method. Since a move keeps the total number of cores unchanged,
    /// removing cores from a status that has enough of them ensures the other status doesn't exceed the capacity.
    /// @param status the status to change the counter
    /// @param cores the number of cores to sum
    private void sumCores(final CoreStatus status, final long cores) {
        final boolean changed = switch (status) {
            case FREE -> sumAvailableResource(cores);
            case BUSY -> true;
            case FAILED -> {
                failedCores += cores;
                yield true;
            }
        };

        if(!changed) {
            throw new IllegalStateException("Unable to change the number of %s cores by %d".formatted(status, cores));
        }
    }

    @Override
    public String toString() {
        return "%s: %d cores (free: %d, busy: %d, failed: %d)".formatted(
            getClass().getSimpleName(), getCapacity(), getFree(), getOccupied(), failedCores);
    }
}
