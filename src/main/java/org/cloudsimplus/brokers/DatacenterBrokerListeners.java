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
package org.cloudsimplus.brokers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.cloudsimplus.listeners.DatacenterBrokerEventInfo;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.Listeners;
import org.jspecify.annotations.Nullable;

/// Manages and notifies the [EventListener]s of a [DatacenterBroker].
///
/// This is a short-lived view/facade created on demand by [DatacenterBroker#listeners()]
/// and not stored as a field on the [DatacenterBroker], so it adds **0 bytes** of retained heap memory.
/// The underlying listener list is stored directly on [DatacenterBrokerAbstract] and initialized with
/// the shared immutable `List.of()`.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
/// @see DatacenterBroker#listeners()
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class DatacenterBrokerListeners {
    /// A Null Object instance that ignores all listener registrations.
    public static final DatacenterBrokerListeners NULL = new DatacenterBrokerListeners(null);

    /// The broker whose listener list is managed by this view.
    /// Declared as [DatacenterBrokerAbstract] rather than [DatacenterBroker] to access its package-private
    /// listener list getter/setter directly without `instanceof` checks on every call,
    /// and marked `@Nullable` only to support [#NULL] (since [DatacenterBroker#NULL] doesn't extend [DatacenterBrokerAbstract]).
    @Nullable
    private final DatacenterBrokerAbstract broker;

    /// Adds an [EventListener] that will be notified every time
    /// all VMs in the waiting list are created (placed) in some Host.
    ///
    /// Events are fired according to the following conditions:
    ///
    /// - if all VMs are submitted before the simulation start and all those VMs are created after starting,
    ///   then the event will be fired just once, in the entire simulation execution time, for every registered Listener;
    /// - if new VMs are submitted during simulation execution, the event may be fired multiple times.
    ///   For instance, consider new VMs are submitted during simulation execution at times 10 and 20.
    ///   If for every submission time, all VMs could be created, then every Listener will be notified 2 times
    ///   (one for VMs submitted at time 10 and other for those at time 20).
    ///
    /// If all VMs submitted at a given time cannot be created due to lack of suitable Hosts,
    /// the event will not be fired for that submission.
    ///
    /// @param listener the Listener that will be notified
    /// @return this `DatacenterBrokerListeners` instance
    /// @see DatacenterBroker#getVmWaitingList()
    public DatacenterBrokerListeners addOnVmsCreated(final EventListener<DatacenterBrokerEventInfo> listener) {
        if (broker != null) {
            broker.setOnVmsCreatedListeners(Listeners.add(broker.getOnVmsCreatedListeners(), listener));
        }
        return this;
    }

    /// Removes an [EventListener] to stop it from being notified when
    /// VMs in the waiting list are all created.
    ///
    /// @param listener the Listener that will be removed
    /// @return true if the listener was found and removed, false otherwise
    /// @see #addOnVmsCreated(EventListener)
    public boolean removeOnVmsCreated(final EventListener<? extends EventInfo> listener) {
        if (broker == null) {
            return false;
        }
        final var oldList = broker.getOnVmsCreatedListeners();
        broker.setOnVmsCreatedListeners(Listeners.remove(oldList, listener));
        return broker.getOnVmsCreatedListeners().size() != oldList.size();
    }

    /// Notifies all registered `onVmsCreated` listeners when all waiting VMs have been created.
    static void notifyOnVmsCreated(final DatacenterBroker broker) {
        final var b = (DatacenterBrokerAbstract) broker;
        if (!b.getVmWaitingList().isEmpty()) {
            return;
        }

        for (final var listener : b.getOnVmsCreatedListeners()) {
            b.setOnVmsCreatedListeners(Listeners.removeIfOneTime(b.getOnVmsCreatedListeners(), listener));
            listener.update(DatacenterBrokerEventInfo.of(listener, b));
        }

        if (b.getVmWaitingList().isEmpty()) {
            b.getVmCreation().resetCurrentRetries();
        }
    }
}
