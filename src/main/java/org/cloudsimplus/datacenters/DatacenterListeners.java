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
package org.cloudsimplus.datacenters;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSuitability;
import org.cloudsimplus.listeners.DatacenterVmMigrationEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostEventInfo;
import org.cloudsimplus.listeners.Listeners;
import org.cloudsimplus.vms.Vm;
import org.jspecify.annotations.Nullable;

import java.util.List;

/// Manages and notifies the [EventListener]s of a [Datacenter].
///
/// This is a short-lived view/facade created on demand by [Datacenter#listeners()]
/// and not stored as a field on the [Datacenter], so it adds **0 bytes** of retained heap memory.
/// The underlying listener lists are stored directly on [DatacenterSimple] and initialized with
/// the shared immutable `List.of()`.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
/// @see Datacenter#listeners()
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class DatacenterListeners {
    /// A Null Object instance that ignores all listener registrations.
    public static final DatacenterListeners NULL = new DatacenterListeners(null);

    /// The Datacenter whose listener lists are managed by this view.
    /// Declared as [DatacenterSimple] rather than [Datacenter] to access its package-private
    /// listener list getters/setters directly without `instanceof` checks on every call,
    /// and marked `@Nullable` only to support [#NULL] (since [Datacenter#NULL] doesn't extend [DatacenterSimple]).
    @Nullable
    private final DatacenterSimple datacenter;

    /// Adds an [EventListener] object that will be notified every time
    /// a new Host is available for the Datacenter, after a [CloudSimTag#HOST_ADD] request
    /// (even if the request was sent before the simulation starts).
    /// Hosts added directly by calling [Datacenter#addHost(Host)] or [Datacenter#addHostList(List)]
    /// don't notify the listeners.
    ///
    /// @param listener the event listener to add
    /// @return this `DatacenterListeners` instance
    public DatacenterListeners addOnHostAvailable(final EventListener<HostEventInfo> listener) {
        if (datacenter != null) {
            datacenter.setOnHostAvailableListeners(Listeners.add(datacenter.getOnHostAvailableListeners(), listener));
        }
        return this;
    }

    /// Removes a listener from the `onHostAvailable` listener list.
    ///
    /// @param listener the listener to remove
    /// @return true if the listener was found and removed, false otherwise
    public boolean removeOnHostAvailable(final EventListener<HostEventInfo> listener) {
        if (datacenter == null) {
            return false;
        }
        final var oldList = datacenter.getOnHostAvailableListeners();
        datacenter.setOnHostAvailableListeners(Listeners.remove(oldList, listener));
        return datacenter.getOnHostAvailableListeners().size() != oldList.size();
    }

    /// Adds an [EventListener] object that will be notified every time
    /// a VM migration is finished either successfully or not.
    ///
    /// @param listener the event listener to add
    /// @return this `DatacenterListeners` instance
    public DatacenterListeners addOnVmMigrationFinish(final EventListener<DatacenterVmMigrationEventInfo> listener) {
        if (datacenter != null) {
            datacenter.setOnVmMigrationFinishListeners(Listeners.add(datacenter.getOnVmMigrationFinishListeners(), listener));
        }
        return this;
    }

    /// Removes a listener from the `onVmMigrationFinish` listener list.
    ///
    /// @param listener the listener to remove
    /// @return true if the listener was found and removed, false otherwise
    public boolean removeOnVmMigrationFinish(final EventListener<DatacenterVmMigrationEventInfo> listener) {
        if (datacenter == null) {
            return false;
        }
        final var oldList = datacenter.getOnVmMigrationFinishListeners();
        datacenter.setOnVmMigrationFinishListeners(Listeners.remove(oldList, listener));
        return datacenter.getOnVmMigrationFinishListeners().size() != oldList.size();
    }

    /// Notifies all registered `onHostAvailable` listeners when a new Host is added at runtime.
    static void notifyOnHostAvailable(final Datacenter datacenter, final Host host) {
        final var dc = (DatacenterSimple) datacenter;
        for (final var listener : dc.getOnHostAvailableListeners()) {
            dc.setOnHostAvailableListeners(Listeners.removeIfOneTime(dc.getOnHostAvailableListeners(), listener));
            listener.update(HostEventInfo.of(listener, host, dc.getSimulation().clock()));
        }
    }

    /// Notifies all registered `onVmMigrationFinish` listeners when a VM migration completes.
    static void notifyOnVmMigrationFinish(
        final Datacenter datacenter,
        final Vm vm,
        final HostSuitability suitability)
    {
        final var dc = (DatacenterSimple) datacenter;
        for (final var listener : dc.getOnVmMigrationFinishListeners()) {
            dc.setOnVmMigrationFinishListeners(Listeners.removeIfOneTime(dc.getOnVmMigrationFinishListeners(), listener));
            listener.update(DatacenterVmMigrationEventInfo.of(listener, vm, suitability));
        }
    }
}
