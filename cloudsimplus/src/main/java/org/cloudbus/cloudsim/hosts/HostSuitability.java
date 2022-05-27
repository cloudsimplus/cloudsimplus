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
package org.cloudbus.cloudsim.hosts;

import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Objects;

/**
 * A class that stores information about the suitability of
 * a {@link Host} for placing a {@link Vm}.
 * It provides fine-grained information to indicates if the Host is suitable in
 * storage, ram, bandwidth and number of PEs required by the given Vm.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.0.2
 */
public final class HostSuitability {
    public static final HostSuitability NULL = new HostSuitability();

    private boolean forStorage;
    private boolean forRam;
    private boolean forBw;
    private boolean forPes;

    /**
     * The reason the Host is not suitable for a VM.
     */
    private String reason;

    public HostSuitability(){/**/}

    /**
     * Creates a HostSuitability object.
     * @param reason the reason the Host is not suitable for a VM.
     * @see #toString()
     */
    public HostSuitability(final String reason){
        this.reason = Objects.requireNonNull(reason);
    }

    /**
     * Update the Host suitability based on another instance,
     * making this object represent a combined suitability of
     * different VMs for a single Host.
     * If at the end the {@link #fully() suitability} is false,
     * that means the Host was not suitable for some VM(s) from other suitability instances.
     * @param other other object to copy attribute values from
     * @see org.cloudbus.cloudsim.vms.VmGroup
     */
    public void setSuitability(final HostSuitability other){
        forPes = forPes && other.forPes;
        forRam = forRam && other.forRam;
        forBw = forBw && other.forBw;
        forStorage = forStorage && other.forStorage;
    }

    /** Checks if the Host has storage suitability for the size of the VM.
     * @return true if it's suitable;
     *         false if it's unsuitable or this specific requirement
     *         was not even evaluated since other one was already not met.
     * @see Host#setLazySuitabilityEvaluation(boolean)
     */
    public boolean forStorage() {
        return forStorage;
    }

    /** Sets if the Host has disk suitability for storing the VM.
     * @param suitable true to indicate it's suitable according to VM's size requirements;
     *                 false otherwise
     * */
    HostSuitability setForStorage(final boolean suitable) {
        this.forStorage = suitable;
        return this;
    }

    /** Checks if the Host has RAM suitability for running the VM.
     * @return true if it's suitable;
     *         false if it's unsuitable or this specific requirement
     *         was not even evaluated since other one was already not met.
     * @see Host#setLazySuitabilityEvaluation(boolean)
     */
    public boolean forRam() {
        return forRam;
    }

    /** Sets if the Host has RAM suitability for running the VM.
     * @param suitable true to indicate it's suitable according to VM's RAM requirements;
     *                 false otherwise
     * */
    HostSuitability setForRam(final boolean suitable) {
        this.forRam = suitable;
        return this;
    }

    /** Checks if the Host has bandwidth suitability for running the VM.
     * @return true if it's suitable;
     *         false if it's unsuitable or this specific requirement
     *         was not even evaluated since other one was already not met.
     * @see Host#setLazySuitabilityEvaluation(boolean)
     */
    public boolean forBw() {
        return forBw;
    }

    /** Sets if the Host has bandwidth suitability for running the VM.
     * @param suitable true to indicate it's suitable according to VM's BW requirements;
     *                 false otherwise
     * */
    HostSuitability setForBw(final boolean suitable) {
        this.forBw = suitable;
        return this;
    }

    /** Checks if the Host has {@link Pe} suitability for running the VM.
     * @return true if it's suitable;
     *         false if it's unsuitable or this specific requirement
     *         was not even evaluated since other one was already not met.
     * @see Host#setLazySuitabilityEvaluation(boolean)
     */
    public boolean forPes() {
        return forPes;
    }

    /** Sets if the Host has {@link Pe} suitability for running the VM.
     * @param forPes true to indicate it's suitable according to VM's number of PEs requirements;
     *               false otherwise
     * */
    HostSuitability setForPes(final boolean forPes) {
        this.forPes = forPes;
        return this;
    }

    /**
     * Checks if the Host is totally suitable or not for the given Vm
     * in terms of required storage, ram, bandwidth and number of PEs.
     * If any of the requirements is not met, it means the host is not suitable at all.
     * @return true if all resource requirements are met, false otherwise.
     */
    public boolean fully(){
        return forStorage && forRam && forBw && forPes;
    }

    /**
     * Gets the reason the VM cannot be allocated to a host.
     * @return the reason or a empty string if the VM can be allocated.
     */
    @Override
    public String toString(){
        if(fully())
            return "Host is fully suitable for the last requested VM";

        if(reason != null)
            return reason;

        final var builder = new StringBuilder("lack of");
        if(!forPes)
            builder.append(" PEs,");
        if(!forRam)
            builder.append(" RAM,");
        if(!forStorage)
            builder.append(" Storage,");
        if(!forBw)
            builder.append(" BW,");

        return builder.substring(0, builder.length()-1);
    }
}
