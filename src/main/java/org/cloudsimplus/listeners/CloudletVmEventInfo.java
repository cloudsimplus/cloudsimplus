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

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.vms.Vm;

/**
 * An interface that represents data to be passed
 * to {@link EventListener} objects that are registered to be notified
 * when some events happen for a given {@link Cloudlet}
 * running inside a {@link Vm}.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 *
 * @see Cloudlet#addOnUpdateProcessingListener(EventListener)
 * @see Cloudlet#addOnFinishListener(EventListener)
 */
public interface CloudletVmEventInfo extends CloudletEventInfo, VmEventInfo {
    /**
     * Gets a {@code CloudletVmEventInfo} instance from the given parameters.
     * The {@link #getTime()} is the current simulation time.
     *
     * @param listener the listener to be notified about the event
     * @param cloudlet the {@link Cloudlet} that fired the event
     * @param vm the {@link Vm} where the Cloudlet is or was running into,
     *            depending on the fired event, such as the
     *            {@link Cloudlet#addOnUpdateProcessingListener(EventListener) OnUpdateCloudletProcessing}
     *           or {@link Cloudlet#addOnFinishListener(EventListener) OnCloudletFinish}
     */
    static CloudletVmEventInfo of(final EventListener<? extends EventInfo> listener, final Cloudlet cloudlet, final Vm vm){
        return of(listener, cloudlet.getSimulation().clock(), cloudlet, vm);
    }

    /**
     * Gets a {@code CloudletVmEventInfo} instance from the given parameters.
     * The {@link #getVm() Vm} attribute is defined as the {@link Vm} where the {@link Cloudlet}
     * is running.
     *
     * @param time the time the event happened
     * @param cloudlet the {@link Cloudlet} that fired the event
     * @see #of(EventListener, Cloudlet, Vm)
     */
    static CloudletVmEventInfo of(final EventListener<? extends EventInfo> listener, final double time, final Cloudlet cloudlet){
        return of(listener, time, cloudlet, cloudlet.getVm());
    }

    /**
     * Gets a {@code CloudletVmEventInfo} instance from the given parameters.
     * The {@link #getVm() Vm} attribute is defined as the {@link Vm} where the {@link Cloudlet}
     * is running and the {@link #getTime()} is the current simulation time.
     *
     * @param cloudlet the {@link Cloudlet} that fired the event
     * @see #of(EventListener, Cloudlet, Vm)
     */
    static CloudletVmEventInfo of(final EventListener<? extends EventInfo> listener, final Cloudlet cloudlet){
        return of(listener, cloudlet, cloudlet.getVm());
    }

    /**
     * Gets a {@code CloudletVmEventInfo} instance from the given parameters.
     *
     * @param listener the listener to be notified about the event
     * @param time the time the event happened
     * @param cloudlet the {@link Cloudlet} that fired the event
     * @param vm the {@link Vm} where the Cloudlet is or was running into,
     *            depending on the fired event, such as the
     *            {@link Cloudlet#addOnUpdateProcessingListener(EventListener) OnUpdateCloudletProcessing}
     *           or {@link Cloudlet#addOnFinishListener(EventListener) OnCloudletFinish}
     */
    static CloudletVmEventInfo of(final EventListener<? extends EventInfo> listener, final double time, final Cloudlet cloudlet, final Vm vm){
        return new CloudletVmEventInfo() {
            @Override public Cloudlet getCloudlet() { return cloudlet; }
            @Override public Vm getVm() { return vm; }
            @Override public double getTime() { return time; }
            @Override public EventListener<? extends EventInfo> getListener() { return listener; }
        };
    }
}
