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
package org.cloudsimplus.hosts;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmGroup;

/**
 * A class that stores information about the suitability of
 * a {@link Host} for placing a {@link Vm}.
 * It provides fine-grained information to indicates if the Host is suitable in
 * storage, ram, bandwidth and number of PEs required by the given Vm.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.0.2
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class HostSuitability {
    public static final HostSuitability NULL = new HostSuitability(Vm.NULL, "");

    /** Indicates if the Host has enough storage for running a VM. */
    @Setter
    private boolean forStorage;

    /** Indicates if the Host has enough RAM for running a VM. */
    @Setter
    private boolean forRam;

    /** Indicates if the Host has enough Bandwidth for running a VM. */
    @Setter
    private boolean forBw;

    /** Indicates if the Host has enough {@link Pe}s for running a VM. */
    @Setter
    private boolean forPes;

    @Getter @EqualsAndHashCode.Include
    private final Vm vm;

    @Getter
    private final Host host;

    /**
     * The reason the Host is not suitable for a VM.
     */
    private String reason;

    public HostSuitability(final Vm vm, final String reason){
        this(Host.NULL, vm, reason);
    }

    public HostSuitability(final Host host, final Vm vm){
        this(host, vm, "");
    }

    /**
     * Creates a HostSuitability object.
     *
     * @param host the Host evaluated for placing a given Vm
     * @param vm the Vm being requested to be placed in the given Host
     * @param reason the reason the Host is not suitable for a VM.
     * @see #toString()
     */
    public HostSuitability(@NonNull Host host, @NonNull final Vm vm, @NonNull final String reason){
        this.host = host;
        this.vm = vm;
        this.reason = reason;
    }

    /**
     * Update the Host suitability based on another instance,
     * making this object represent a combined suitability of
     * different VMs for a single Host.
     * If at the end the {@link #fully() suitability} is false,
     * that means the Host was not suitable for some VM(s) from other suitability instances.
     * @param other other object to copy attribute values from
     * @see VmGroup
     */
    public void setSuitability(final HostSuitability other){
        this.forPes     &= other.forPes;
        this.forRam     &= other.forRam;
        this.forBw      &= other.forBw;
        this.forStorage &= other.forStorage;
    }

    /** Checks if the Host has enough storage for running a VM.
     * @return true if it's suitable;
     *         false if it's unsuitable or this specific requirement
     *         was not even evaluated since other one was already not met.
     * @see Host#setLazySuitabilityEvaluation(boolean)
     */
    public boolean forStorage() {
        return forStorage;
    }

    /** Checks if the Host has enough RAM for running a VM.
     * @return true if it's suitable;
     *         false if it's unsuitable or this specific requirement
     *         was not even evaluated since other one was already not met.
     * @see Host#setLazySuitabilityEvaluation(boolean)
     */
    public boolean forRam() {
        return forRam;
    }

    /** Checks if the Host has enough Bandwidth for running a VM.
     * @return true if it's suitable;
     *         false if it's unsuitable or this specific requirement
     *         was not even evaluated since other one was already not met.
     * @see Host#setLazySuitabilityEvaluation(boolean)
     */
    public boolean forBw() {
        return forBw;
    }

    /** Checks if the Host has enough {@link Pe}s for running a VM.
     * @return true if it's suitable;
     *         false if it's unsuitable or this specific requirement
     *         was not even evaluated since other one was already not met.
     * @see Host#setLazySuitabilityEvaluation(boolean)
     */
    public boolean forPes() {
        return forPes;
    }

    /**
     * Checks if the Host is totally suitable for the given Vm
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
            return host + " is fully suitable for " + vm;

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

        final var hostStr = host == Host.NULL ? "" : " in " + host;
        return "%s for %s".formatted(builder.substring(0, builder.length()-1), hostStr, vm);
    }
}
