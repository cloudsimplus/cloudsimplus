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
import org.cloudsimplus.util.MathUtil;
import org.cloudsimplus.vms.Vm;

/// A virtual Central Processing Unit (vCPU) attached to a [Vm],
/// having one or more cores ([Pe]s).
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.0
@Getter
public final class Processor extends ResourceManageableAbstract {
    public static final Processor NULL = new Processor(Vm.NULL, 0);

    /**
     * The Vm to which the processor belongs to.
     */
    private final Vm vm;

    /// The individual MIPS of each [Pe].
    private double mips;

    /// Instantiates a Processor for a given VM.
    ///
    /// @param vm          the [Vm] to which the processor will belong to
    /// @param pesNumber number of [Pe]s (the processor [capacity][#getCapacity()])
    /// @param pesMips     MIPS of each [Pe]
    public Processor(final Vm vm, final long pesNumber, final double pesMips) {
        this(vm, pesNumber);
        setMips(pesMips);
    }

    private Processor(final Vm vm, final long pesNumber){
        super(pesNumber, "Unit");
        this.vm = vm;
    }

    /// @return the sum of MIPS from all [Pe]s.
    public double getTotalMips(){
        return getMips() * getCapacity();
    }

    /// Sets the individual MIPS of each [Pe].
    /// @param newMips the new MIPS of each PE
    public void setMips(final double newMips) {
        this.mips = MathUtil.nonNegative(newMips, "MIPS");
    }

    /**
     * @return the number of Pes of the Processor
     */
    @Override
    public long getCapacity() {
        return super.getCapacity();
    }

    /// Sets the number of [Pe]s of the Processor
    /// @param pesNumber the number of PEs to set
    /// @return true if the capacity was set, false otherwise
    @Override
    public boolean setCapacity(long pesNumber) {
        if(pesNumber <= 0){
            throw new IllegalArgumentException("The Processor's number of PEs must be greater than 0.");
        }
        return super.setCapacity(pesNumber);
    }

    /**
     * @return the number of available PEs that are free to be used
     */
    @Override
    public long getAvailableResource() {
        return super.getAvailableResource();
    }

    /**
     * @return the number of PEs allocated
     */
    @Override
    public long getAllocatedResource() {
        return super.getAllocatedResource();
    }
}
