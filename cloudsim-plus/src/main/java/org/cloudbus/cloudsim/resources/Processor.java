/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudbus.cloudsim.resources;

import org.cloudbus.cloudsim.vms.Vm;

/**
 * A Central Unit Processing (CPU) attached to a {@link Vm} and which can have multiple
 * cores ({@link Pe}s). It's a also called a Virtual CPU (vCPU).
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public final class Processor extends ResourceManageableAbstract {
    public static final Processor NULL = new Processor();
    private Vm vm;

    /** @see #getMips() */
    private double mips;

    /**
     * Instantiates a Processor for a given VM.
     *
     * @param vm the {@link Vm} the processor will belong to
     * @param pesMips MIPS of each {@link Pe}
     * @param numberOfPes number of {@link Pe}s
     */
    public Processor(Vm vm, double pesMips, long numberOfPes) {
        super(numberOfPes);
        this.vm = vm;
        setMips(pesMips);
    }

    private Processor(){
        super(0);
    }

    /**
     * Gets the sum of MIPS from all {@link Pe}s.
     * @return
     */
    public double getTotalMips(){
        return getMips()* getCapacity();
    }

    /**
     * Gets the individual MIPS of each {@link Pe}.
     * @return
     */
    public double getMips() {
        return mips;
    }

    /**
     * Sets the individual MIPS of each {@link Pe}.
     * @param newMips the new MIPS of each PE
     * @pre newMips >= 0
     */
    public void setMips(double newMips) {
        if(newMips < 0) {
            throw new IllegalArgumentException("MIPS cannot be negative");
        }

        this.mips = newMips;
    }

    /**
     * Gets the number of {@link Pe}s of the Processor
     * @return
     */
    @Override
    public long getCapacity() {
        return super.getCapacity();
    }

    /**
     * Gets the number of free PEs.
     * @return
     */
    @Override
    public long getAvailableResource() {
        return super.getAvailableResource();
    }

    /**
     * Gets the number of used PEs.
     * @return
     */
    @Override
    public long getAllocatedResource() {
        return super.getAllocatedResource();
    }

    /**
     * Sets the number of {@link Pe}s of the Processor
     * @param numberOfPes the number of PEs to set
     * @return
     */
    @Override
    public boolean setCapacity(long numberOfPes) {
        if(numberOfPes < 0){
            throw new IllegalArgumentException("The Processsor's number of PEs cannot be negative.");
        }
        return super.setCapacity(numberOfPes);
    }

    /**
     * Gets the {@link Vm} the processor belongs to.
     * @return
     */
    public Vm getVm() {
        return vm;
    }
}
