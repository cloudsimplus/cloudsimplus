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

/// A [Processor] implementation that can be used by any [Machine],
/// keeping only counters for the number of cores in each [CoreStatus].
/// The number of [free][CoreStatus#FREE] cores is the [available resource][#getAvailableResource()],
/// the number of [failed][CoreStatus#FAILED] cores is stored in a field and the
/// number of [busy][CoreStatus#BUSY] cores is the remaining ones.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
public final class ProcessorSimple extends ResourceManageableAbstract implements Processor {
    @Getter
    private final Machine<?> machine;

    /// The individual MIPS of each core.
    @Getter
    private double mips;

    /// @see #getFailedCores()
    private long failedCores;

    /// Instantiates a Processor for a given Machine.
    ///
    /// @param machine the [Machine] to which the processor will belong to
    /// @param cores number of cores (the processor [capacity][#getCapacity()])
    /// @param coreMips MIPS of each core
    public ProcessorSimple(@NonNull final Machine<?> machine, final long cores, final double coreMips) {
        super(cores, "Unit");
        this.machine = machine;
        setMips(coreMips);
    }

    @Override
    public double getTotalMips() {
        return getMips() * getWorkingCores();
    }

    @Override
    public void setMips(final double newMips) {
        this.mips = MathUtil.nonNegative(newMips, "MIPS");
    }

    @Override
    public long getFailedCores() {
        return failedCores;
    }

    /// @return the number of [busy][CoreStatus#BUSY] cores
    @Override
    public long getAllocatedResource() {
        return getCapacity() - getAvailableResource() - failedCores;
    }

    /// Sets the number of cores of the Processor.
    /// @param cores the number of cores to set
    /// @return true if the capacity was set;
    ///         false if it's lower than the number of busy and failed cores
    @Override
    public boolean setCapacity(final long cores) {
        if(cores <= 0){
            throw new IllegalArgumentException("The Processor's number of cores must be greater than 0.");
        }

        if(cores < getAllocatedResource() + failedCores) {
            return false;
        }

        return super.setCapacity(cores);
    }

    @Override
    public long deallocateAllResources() {
        final long previousAllocated = getAllocatedResource();
        setAvailableResource(getWorkingCores());
        return previousAllocated;
    }

    @Override
    public long getCores(@NonNull final CoreStatus status) {
        return switch (status) {
            case FREE -> getAvailableResource();
            case BUSY -> getAllocatedResource();
            case FAILED -> failedCores;
        };
    }

    @Override
    public boolean moveCores(@NonNull final CoreStatus from, @NonNull final CoreStatus to, final long cores) {
        if(cores <= 0 || from == to || getCores(from) < cores) {
            return false;
        }

        sumCores(from, -cores);
        sumCores(to, cores);
        return true;
    }

    /// Sums a given number of cores (negative or positive) to the counter of a given status.
    /// Since the number of busy cores is the remaining ones, nothing is stored for them.
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
            getClass().getSimpleName(), getCapacity(), getAvailableResource(), getAllocatedResource(), failedCores);
    }
}
