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
package org.cloudsimplus.schedulers.cloudlet;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.listeners.CloudletResourceShortageEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.Listeners;
import org.cloudsimplus.resources.ResourceManageable;
import org.jspecify.annotations.Nullable;

import static org.cloudsimplus.listeners.CloudletResourceShortageEventInfo.of;

/// Manages and notifies the [EventListener]s of a [CloudletScheduler].
///
/// This is a short-lived view/facade created on demand by [CloudletScheduler#listeners()]
/// and not stored as a field on the [CloudletScheduler], so it adds **0 bytes** of retained heap memory.
/// The underlying listener list is stored directly on [CloudletSchedulerAbstract] and initialized with
/// the shared immutable `List.of()`.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
/// @see CloudletScheduler#listeners()
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class CloudletSchedulerListeners {
    /// A Null Object instance that ignores all listener registrations.
    public static final CloudletSchedulerListeners NULL = new CloudletSchedulerListeners(null);

    /// The scheduler whose listener list is managed by this view.
    /// Declared as [CloudletSchedulerAbstract] rather than [CloudletScheduler] to access its package-private
    /// listener list getter/setter directly without `instanceof` checks on every call,
    /// and marked `@Nullable` only to support [#NULL] (since [CloudletScheduler#NULL] doesn't extend [CloudletSchedulerAbstract]).
    @Nullable
    private final CloudletSchedulerAbstract scheduler;

    /// Adds a listener object that will be notified every time
    /// a [CloudletScheduler] is not able to allocate the amount of resource a [Cloudlet]
    /// is requesting, due to lack of available capacity.
    ///
    /// @param listener the Listener to add
    /// @return this `CloudletSchedulerListeners` instance
    public CloudletSchedulerListeners addOnResourceShortage(final EventListener<CloudletResourceShortageEventInfo> listener) {
        if (scheduler != null) {
            scheduler.setResourceAllocationFailListeners(Listeners.add(scheduler.getResourceAllocationFailListeners(), listener));
        }
        return this;
    }

    /// Removes a listener object from the `onResourceShortage` listener list.
    ///
    /// @param listener the Listener to remove
    /// @return true if the Listener was removed, false otherwise
    public boolean removeOnResourceShortage(final EventListener<CloudletResourceShortageEventInfo> listener) {
        if (scheduler == null) {
            return false;
        }
        final var oldList = scheduler.getResourceAllocationFailListeners();
        scheduler.setResourceAllocationFailListeners(Listeners.remove(oldList, listener));
        return scheduler.getResourceAllocationFailListeners().size() != oldList.size();
    }

    /// Notifies all registered `onResourceShortage` listeners when a Cloudlet requests more resources than available.
    static void notifyOnResourceShortage(
        final CloudletScheduler scheduler,
        final ResourceManageable resource,
        final Cloudlet cloudlet,
        final long requested,
        final long available)
    {
        final var s = (CloudletSchedulerAbstract) scheduler;
        for (final var listener : s.getResourceAllocationFailListeners()) {
            s.setResourceAllocationFailListeners(Listeners.removeIfOneTime(s.getResourceAllocationFailListeners(), listener));
            listener.update(of(listener, cloudlet, resource.getClass(), requested, available, s.getVm().getSimulation().clock()));
        }
    }
}
