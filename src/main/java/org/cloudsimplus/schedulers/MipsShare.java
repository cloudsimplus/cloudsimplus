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
package org.cloudsimplus.schedulers;

import lombok.NonNull;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.Processor;
import org.cloudsimplus.util.MathUtil;
import org.cloudsimplus.utilizationmodels.BootModel;

/**
 * Represents the requested or allocated MIPS (Million Instructions Per Second) capacity for a given number of {@link Pe}s from a VM.
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.2.0
 */
public class MipsShare {
    public static final MipsShare NULL = new MipsShare();

    private long pes;
    private double mips;

    /**
     * Creates an empty MIPS share, with no PEs and therefore no MIPS capacity.
     */
    public MipsShare() {
        this(0,0);
    }

    /**
     * Creates a MIPS share with 1 PE having a given MIPS capacity.
     * @param mips the allocated or requested MIPS capacity for every {@link Pe}
     */
    public MipsShare(final double mips){
        this(1, mips);
    }

    /**
     * Creates a MIPS share according to a given {@link Processor} capacity.
     * @param processor the processor to get its capacity to create the MipsShare
     */
    public MipsShare(@NonNull final Processor processor){
        this(processor.getCapacity(), processor.getMips());
    }

    /**
     * Creates a MIPS share with a defined number of PEs and MIPS capacity for each PE.
     * @param pes the number of PEs shared
     * @param mips the allocated or requested MIPS capacity for every {@link Pe}
     */
    public MipsShare(final long pes, final double mips){
        this.pes = MathUtil.nonNegative(pes, "PEs number");
        this.setMips(mips);
    }

    /**
     * A clone constructor that creates a MipsShare based on another one.
     * @param share the MIPS share to clone
     */
    public MipsShare(@NonNull final MipsShare share) {
        this(share.pes, share.mips);
    }

    /**
     * Creates a MipsShare object indicating the MIPS capacity and the number of PEs used during a VM boot process.
     * @param processor the VM processor
     * @param bootModel the VM boot model that defines how the boot process uses resources
     */
    public MipsShare(@NonNull final Processor processor, @NonNull final BootModel bootModel) {
        this(processor.getCapacity(), processor.getMips()*bootModel.getCpuPercentUtilization());
    }

    /**
     * @return the MIPS capacity for each {@link Pe}.
     */
    public double mips() {
        return mips;
    }

    public final void setMips(final double mips) {
        this.mips = MathUtil.nonNegative(mips, "MIPS");
    }

    /**
     * {@return  the number of allocated/requested PEs}
     * It indicates the size of the MIPS share.
     */
    public long pes(){
        return pes;
    }

    /**
     * Checks if there isn't MIPS capacity allocated to any PE,
     * or if some capacity was already allocated.
     * @return true if the MIPS share has no capacity, false otherwise
     */
    public boolean isEmpty(){
        return pes == 0 || mips == 0;
    }

    /**
     * @return the total MIPS capacity sum across all PEs.
     */
    public double totalMips(){
        return pes * mips;
    }

    /**
     * Removes a given number of PEs from the MIPS share.
     * It won't remove more PEs than there is available.
     * @param count number of PEs to remove
     * @return the number of actual removed PEs
     */
    public long remove(final long count) {
        MathUtil.nonNegative(count, "Number of PEs to remove");
        final long removedPes = Math.min(count, this.pes);
        this.pes -= removedPes;
        return count;
    }

    @Override
    public String toString() {
        return "MipsShare{pes=%d, mips=%s}".formatted(pes, mips);
    }
}
