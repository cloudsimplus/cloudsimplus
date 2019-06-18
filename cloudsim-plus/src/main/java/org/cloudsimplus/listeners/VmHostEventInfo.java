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
package org.cloudsimplus.listeners;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * An interface that represents data to be passed
 * to {@link EventListener} objects that are registered to be notified
 * when some events happen for a given {@link Vm} that is related to
 * some {@link Host}.
 *
 * <p>It can be used to notify Listeners when a Host is {@link Vm#addOnHostAllocationListener(EventListener)}  allocated} to or
 * {@link Vm#addOnHostDeallocationListener(EventListener)}  deallocated} from a given Vm,
 * when a Vm has its {@link Vm#addOnUpdateProcessingListener(EventListener)}  processing updated by its Host},
 * etc.
 * </p>
 **
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 *
 * @see Vm#addOnHostAllocationListener(EventListener)
 * @see Vm#addOnHostDeallocationListener(EventListener)
 * @see Vm#addOnUpdateProcessingListener(EventListener)
 */
public interface VmHostEventInfo extends VmEventInfo, HostEventInfo {
    /**
     * Gets a VmHostEventInfo instance from the given parameters.
     * The {@link #getHost() Host} attribute is defined as the {@link Host} where the {@link Vm}
     * is running and the {@link #getTime()} is the current simulation time.
     *
     * @param listener the listener to be notified about the event
     * @param vm {@link Vm} that fired the event
     */
    static VmHostEventInfo of(final EventListener<? extends EventInfo> listener, final Vm vm) {
        return of(listener, vm, vm.getHost());
    }

    /**
     * Gets a VmHostEventInfo instance from the given parameters.
     * The {@link #getTime()} is the current simulation time.
     *
     * @param listener the listener to be notified about the event
     * @param vm {@link Vm} that fired the event
     * @param host {@link Host} that the {@link Vm} is related to.
     *                         Such a Host can be that one where the Vm is or was placed,
     *                         or where the Vm was tried to be be created,
     *                         depending on the fired event, such as the
     *                         {@link Vm#addOnHostAllocationListener(EventListener)} OnHostAllocation} or
     *                         {@link Vm#addOnHostDeallocationListener(EventListener)} OnHostDeallocation}
     */
    static VmHostEventInfo of(final EventListener<? extends EventInfo> listener, final Vm vm, final Host host) {
        final double time = vm.getSimulation().clock();
        return new VmHostEventInfo() {
            @Override public Host getHost() { return host; }
            @Override public Vm getVm() { return vm; }
            @Override public double getTime() { return time; }
            @Override public EventListener<? extends EventInfo> getListener() { return listener; }
        };
    }
}
