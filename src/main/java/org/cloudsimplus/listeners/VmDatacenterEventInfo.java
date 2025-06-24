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
package org.cloudsimplus.listeners;

import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.vms.Vm;

/**
 * An interface that represents data to be passed
 * to {@link EventListener} objects that are registered to be notified
 * when some events happen for a given {@link Vm}
 * running inside a {@link Datacenter}.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 *
 * @see Vm#addOnCreationFailureListener(EventListener)
 */
public interface VmDatacenterEventInfo extends VmEventInfo, DatacenterEventInfo {
    /**
     * Gets a {@code VmDatacenterEventInfo} instance from the given parameters.
     * The {@link #getDatacenter() Datacenter} attribute is defined as the {@link Datacenter} where the {@link Vm}
     * is running and the {@link #getTime()} is the current simulation time.
     *
     * @param listener the listener to be notified about the event
     * @param vm the {@link Vm} that fired the event
     */
    static VmDatacenterEventInfo of(final EventListener<VmDatacenterEventInfo> listener, final Vm vm) {
        return of(listener, vm, vm.getHost().getDatacenter());
    }

    /**
     * Gets a {@code VmDatacenterEventInfo} instance from the given parameters.
     * The {@link #getTime()} is the current simulation time.
     *
     * @param listener the listener to be notified about the event
     * @param vm the {@link Vm} that fired the event
     * @param datacenter {@link Datacenter} that the {@link Vm} is related to.
     *                   Such a Datacenter can be that one where the Vm is or was placed,
     *                   or where the Vm was tried to be created,
     *                   depending on the fired event, such as the
     *                   {@link Vm#addOnCreationFailureListener(EventListener)  OnVmCreationFailure}
     */
    static VmDatacenterEventInfo of(final EventListener<VmDatacenterEventInfo> listener, final Vm vm, final Datacenter datacenter) {
        final double time = vm.getSimulation().clock();
        return new VmDatacenterEventInfo() {
            @Override public Datacenter getDatacenter() { return datacenter; }
            @Override public Vm getVm() { return vm; }
            @Override public double getTime() { return time; }
            @Override public EventListener<VmDatacenterEventInfo> getListener() { return listener; }
        };
    }
}
