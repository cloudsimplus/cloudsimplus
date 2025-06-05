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
import org.cloudsimplus.resources.ResourceManageable;
import org.cloudsimplus.schedulers.cloudlet.CloudletScheduler;

/**
 * An interface that represents data to be passed
 * to {@link EventListener} objects that are registered to be notified
 * when a {@link CloudletScheduler} <b>is not able to allocate the amount of resource a {@link Cloudlet}
 * is requesting due to lack of available capacity</b>.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 5.4.3
 */
public interface CloudletResourceAllocationFailEventInfo extends CloudletEventInfo {
    /**
     * @return the class of the resource the Cloudlet is requesting.
     */
    Class<? extends ResourceManageable> getResourceClass();

    /**
     * {@return amount of resources being requested and which is not currently available}
     * The unit depends on the type of the {@link #getResourceClass()} resource.
     */
    long getRequestedAmount();

    /**
     * {@return amount of resource amount that was available before allocating for the Cloudlet}
     * The unit depends on the type of the {@link #getResourceClass()} resource.
     */
    long getAvailableAmount();

    @Override
    EventListener<CloudletResourceAllocationFailEventInfo> getListener();

    /**
     * Gets a {@code EventInfo} instance from the given parameters.
     *
     * @param listener the listener to be notified about the event
     * @param cloudlet the Cloudlet requesting the resource
     * @param resourceClass the class of the resource the Cloudlet is requesting
     * @param requestedAmount the requested resource amount (the unit depends on the resource requested)
     * @param availableAmount the amount of resource amount that was available before allocating
     *                        for the Cloudlet (the unit depends on the resource requested)
     * @param time the time the event happened
     */
    static CloudletResourceAllocationFailEventInfo of(
        final EventListener<CloudletResourceAllocationFailEventInfo> listener,
        final Cloudlet cloudlet,
        final Class<? extends ResourceManageable> resourceClass,
        final long requestedAmount,
        final long availableAmount,
        final double time)
    {
        return new CloudletResourceAllocationFailEventInfo() {
            @Override public EventListener<CloudletResourceAllocationFailEventInfo> getListener() { return listener; }
            @Override public Cloudlet getCloudlet() { return cloudlet; }
            @Override public Class<? extends ResourceManageable> getResourceClass() { return resourceClass; }
            @Override public long getRequestedAmount() { return requestedAmount; }
            @Override public long getAvailableAmount() { return availableAmount; }
            @Override public double getTime() { return time; }
        };
    }
}
