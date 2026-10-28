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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostEventInfo;
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;
import org.cloudsimplus.listeners.Listeners;
import org.cloudsimplus.vms.Vm;
import org.jspecify.annotations.Nullable;

import java.util.List;

/// Manages and notifies the [EventListener]s of a [Host].
///
/// This is a short-lived view/facade created on demand by [Host#listeners()]
/// and not stored as a field on the [Host], so it adds **0 bytes** of retained heap memory.
/// The underlying listener lists are stored directly on [HostAbstract]: they start as the
/// shared immutable `List.of()` and can also share user-provided immutable lists across many
/// Hosts via the `addOn...(List)` methods.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
/// @see Host#listeners()
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class HostListeners {
    /// A Null Object instance that ignores all listener registrations.
    public static final HostListeners NULL = new HostListeners(null);

    /// The Host whose listener lists are managed by this view.
    /// Declared as [HostAbstract] rather than [Host] to access its package-private
    /// listener list getters/setters directly without `instanceof` checks on every call,
    /// and marked `@Nullable` only to support [#NULL] (since [Host#NULL] doesn't extend [HostAbstract]).
    @Nullable
    private final HostAbstract host;

    /// Adds a listener object that will be notified every time the host is **powered on**.
    ///
    /// @param listener the Listener to add
    /// @return this `HostListeners` instance
    public HostListeners addOnStartup(final EventListener<HostEventInfo> listener) {
        if (host != null) {
            host.setOnStartupListeners(Listeners.add(host.getOnStartupListeners(), listener));
        }
        return this;
    }

    /// Adds a list of listeners that will be notified every time the host is **powered on**.
    /// The list can be shared by many Hosts, to reduce memory use.
    ///
    /// @param listeners the listeners to add (see [#addOnUpdateProcessing(List)] for details)
    /// @return this `HostListeners` instance
    /// @see #addOnStartup(EventListener)
    public HostListeners addOnStartup(final List<EventListener<HostEventInfo>> listeners) {
        if (host != null) {
            host.setOnStartupListeners(Listeners.addAll(host.getOnStartupListeners(), listeners));
        }
        return this;
    }

    /// Removes a listener object from the `onStartup` listener list.
    ///
    /// @param listener the Listener to remove
    /// @return true if the Listener was removed, false otherwise
    public boolean removeOnStartup(final EventListener<HostEventInfo> listener) {
        if (host == null) {
            return false;
        }
        final var oldList = host.getOnStartupListeners();
        host.setOnStartupListeners(Listeners.remove(oldList, listener));
        return host.getOnStartupListeners().size() != oldList.size();
    }

    /// Adds a listener object that will be notified every time the host is **powered off**.
    ///
    /// @param listener the Listener to add
    /// @return this `HostListeners` instance
    public HostListeners addOnShutdown(final EventListener<HostEventInfo> listener) {
        if (host != null) {
            host.setOnShutdownListeners(Listeners.add(host.getOnShutdownListeners(), listener));
        }
        return this;
    }

    /// Adds a list of listeners that will be notified every time the host is **powered off**.
    /// The list can be shared by many Hosts, to reduce memory use.
    ///
    /// @param listeners the listeners to add (see [#addOnUpdateProcessing(List)] for details)
    /// @return this `HostListeners` instance
    /// @see #addOnShutdown(EventListener)
    public HostListeners addOnShutdown(final List<EventListener<HostEventInfo>> listeners) {
        if (host != null) {
            host.setOnShutdownListeners(Listeners.addAll(host.getOnShutdownListeners(), listeners));
        }
        return this;
    }

    /// Removes a listener object from the `onShutdown` listener list.
    ///
    /// @param listener the Listener to remove
    /// @return true if the Listener was removed, false otherwise
    public boolean removeOnShutdown(final EventListener<HostEventInfo> listener) {
        if (host == null) {
            return false;
        }
        final var oldList = host.getOnShutdownListeners();
        host.setOnShutdownListeners(Listeners.remove(oldList, listener));
        return host.getOnShutdownListeners().size() != oldList.size();
    }

    /// Adds a listener object that will be notified every time
    /// the host updates the processing of all its [VMs][Vm].
    ///
    /// @param listener the listener to add
    /// @return this `HostListeners` instance
    public HostListeners addOnUpdateProcessing(final EventListener<HostUpdatesVmsProcessingEventInfo> listener) {
        if (host != null) {
            host.setOnUpdateProcessingListeners(Listeners.add(host.getOnUpdateProcessingListeners(), listener));
        }
        return this;
    }

    /// Adds a list of listeners that will be notified every time
    /// the host updates the processing of all its [VMs][Vm].
    ///
    /// The list can be shared by many Hosts, to reduce memory use:
    /// create it once with `List.of(...)` and pass the same list to all Hosts (e.g. inside a loop).
    /// If this Host has no listeners of this type, it stores the given list itself,
    /// so it uses no memory for its own list.
    /// Changes later made in one Host don't affect the others.
    /// A mutable list is copied, so changing it later doesn't affect any Host.
    /// The other `addOn...(List)` methods work the same way.
    ///
    /// @param listeners the listeners to add
    /// @return this `HostListeners` instance
    /// @see #addOnUpdateProcessing(EventListener)
    public HostListeners addOnUpdateProcessing(final List<EventListener<HostUpdatesVmsProcessingEventInfo>> listeners) {
        if (host != null) {
            host.setOnUpdateProcessingListeners(Listeners.addAll(host.getOnUpdateProcessingListeners(), listeners));
        }
        return this;
    }

    /// Removes a listener object from the `onUpdateProcessing` listener list.
    ///
    /// @param listener the listener to remove
    /// @return true if the listener was found and removed, false otherwise
    public boolean removeOnUpdateProcessing(final EventListener<HostUpdatesVmsProcessingEventInfo> listener) {
        if (host == null) {
            return false;
        }
        final var oldList = host.getOnUpdateProcessingListeners();
        host.setOnUpdateProcessingListeners(Listeners.remove(oldList, listener));
        return host.getOnUpdateProcessingListeners().size() != oldList.size();
    }

    /// Notifies all registered `onStartup` listeners when the Host powers on.
    static void notifyOnStartup(final Host host) {
        final var h = (HostAbstract) host;
        for (final var listener : h.getOnStartupListeners()) {
            h.setOnStartupListeners(Listeners.removeIfOneTime(h.getOnStartupListeners(), listener));
            listener.update(HostEventInfo.of(listener, h, h.getSimulation().clock()));
        }
    }

    /// Notifies all registered `onShutdown` listeners when the Host powers off.
    static void notifyOnShutdown(final Host host) {
        final var h = (HostAbstract) host;
        for (final var listener : h.getOnShutdownListeners()) {
            h.setOnShutdownListeners(Listeners.removeIfOneTime(h.getOnShutdownListeners(), listener));
            listener.update(HostEventInfo.of(listener, h, h.getSimulation().clock()));
        }
    }

    /// Notifies all registered `onUpdateProcessing` listeners when the Host updates VMs processing.
    static void notifyOnUpdateProcessing(final Host host, final double nextSimulationTime) {
        final var h = (HostAbstract) host;
        for (final var listener : h.getOnUpdateProcessingListeners()) {
            h.setOnUpdateProcessingListeners(Listeners.removeIfOneTime(h.getOnUpdateProcessingListeners(), listener));
            listener.update(HostUpdatesVmsProcessingEventInfo.of(listener, h, nextSimulationTime));
        }
    }
}
