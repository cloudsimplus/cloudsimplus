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
package org.cloudsimplus.core;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.Listeners;
import org.jspecify.annotations.Nullable;

import java.util.List;

/// Manages and notifies the [EventListener]s of a [Simulation].
///
/// This is a short-lived view/facade created on demand by [Simulation#listeners()]
/// and not stored as a field on the [Simulation], so it adds **0 bytes** of retained heap memory.
/// The underlying listener lists are stored directly on [CloudSimPlus] and initialized with
/// the shared immutable `List.of()`.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
/// @see Simulation#listeners()
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class SimulationListeners {
    /// A Null Object instance that ignores all listener registrations.
    public static final SimulationListeners NULL = new SimulationListeners(null);

    /// The simulation whose listener lists are managed by this view.
    /// Declared as [CloudSimPlus] rather than [Simulation] to access its package-private
    /// listener list getters/setters directly without `instanceof` checks on every call,
    /// and marked `@Nullable` only to support [#NULL] (since [Simulation#NULL] doesn't extend [CloudSimPlus]).
    @Nullable
    private final CloudSimPlus simulation;

    /// Adds an [EventListener] object that will be notified when the simulation starts.
    ///
    /// @param listener the event listener to add
    /// @return this `SimulationListeners` instance
    public SimulationListeners addOnSimulationStart(final EventListener<EventInfo> listener) {
        if (simulation != null) {
            simulation.setOnSimulationStartListeners(Listeners.add(simulation.getOnSimulationStartListeners(), listener));
        }
        return this;
    }

    /// Removes a listener from the `onSimulationStart` listener list.
    ///
    /// @param listener the listener to remove
    /// @return true if the listener was found and removed, false otherwise
    public boolean removeOnSimulationStart(final EventListener<EventInfo> listener) {
        if (simulation == null) {
            return false;
        }
        final var oldList = simulation.getOnSimulationStartListeners();
        simulation.setOnSimulationStartListeners(Listeners.remove(oldList, listener));
        return simulation.getOnSimulationStartListeners().size() != oldList.size();
    }

    /// Adds an [EventListener] object that will be notified when the simulation is paused.
    /// When this Listener is notified, it will receive an [EventInfo] informing
    /// the time the pause occurred.
    ///
    /// This object is just information about the event
    /// that happened. In fact, it isn't generated an actual [SimEvent] for a pause event
    /// because there is no need for that.
    ///
    /// @param listener the event listener to add
    /// @return this `SimulationListeners` instance
    public SimulationListeners addOnSimulationPause(final EventListener<EventInfo> listener) {
        if (simulation != null) {
            simulation.setOnSimulationPauseListeners(Listeners.add(simulation.getOnSimulationPauseListeners(), listener));
        }
        return this;
    }

    /// Removes a listener from the `onSimulationPause` listener list.
    ///
    /// @param listener the listener to remove
    /// @return true if the listener was found and removed, false otherwise
    public boolean removeOnSimulationPause(final EventListener<EventInfo> listener) {
        if (simulation == null) {
            return false;
        }
        final var oldList = simulation.getOnSimulationPauseListeners();
        simulation.setOnSimulationPauseListeners(Listeners.remove(oldList, listener));
        return simulation.getOnSimulationPauseListeners().size() != oldList.size();
    }

    /// Adds an [EventListener] object that will be notified when any event
    /// is processed by CloudSim Plus. When this Listener is notified, it will receive
    /// the [SimEvent] that was processed.
    ///
    /// @param listener the event listener to add
    /// @return this `SimulationListeners` instance
    public SimulationListeners addOnEventProcessing(final EventListener<SimEvent> listener) {
        if (simulation != null) {
            simulation.setOnEventProcessingListeners(Listeners.add(simulation.getOnEventProcessingListeners(), listener));
        }
        return this;
    }

    /// Removes a listener from the `onEventProcessing` listener list.
    ///
    /// @param listener the listener to remove
    /// @return true if the listener was found and removed, false otherwise
    public boolean removeOnEventProcessing(final EventListener<SimEvent> listener) {
        if (simulation == null) {
            return false;
        }
        final var oldList = simulation.getOnEventProcessingListeners();
        simulation.setOnEventProcessingListeners(Listeners.remove(oldList, listener));
        return simulation.getOnEventProcessingListeners().size() != oldList.size();
    }

    /// Adds an [EventListener] object that will be notified every time when the
    /// simulation clock advances.
    /// **WARNING:** Notifications are sent in an integer interval to avoid notification flood.
    /// Thus, if the clock changes, for instance, from 1.0, to 1.1, 2.0, 2.1, 2.2, 2.5 and then 3.2,
    /// notifications will just be sent for the times 1, 2 and 3 that represent the integer
    /// part of the simulation time.
    ///
    /// @param listener the event listener to add
    /// @return this `SimulationListeners` instance
    public SimulationListeners addOnClockTick(final EventListener<EventInfo> listener) {
        if (simulation != null) {
            simulation.setOnClockTickListeners(Listeners.add(simulation.getOnClockTickListeners(), listener));
        }
        return this;
    }

    /// Removes a listener from the `onClockTick` listener list.
    ///
    /// @param listener the listener to remove
    /// @return true if the listener was found and removed, false otherwise
    public boolean removeOnClockTick(final EventListener<? extends EventInfo> listener) {
        if (simulation == null) {
            return false;
        }
        final var oldList = simulation.getOnClockTickListeners();
        simulation.setOnClockTickListeners(Listeners.remove(oldList, listener));
        return simulation.getOnClockTickListeners().size() != oldList.size();
    }

    /// Notifies all registered `onSimulationStart` listeners when the simulation starts.
    static void notifyOnSimulationStart(final Simulation simulation) {
        final var sim = (CloudSimPlus) simulation;
        if (sim.getOnSimulationStartListeners().isEmpty() || sim.clock() <= 0) {
            return;
        }

        final var listeners = sim.getOnSimulationStartListeners();
        // Since the simulation starts just once, clear the listeners to avoid them being notified again
        sim.setOnSimulationStartListeners(List.of());
        for (final var listener : listeners) {
            listener.update(EventInfo.of(listener, sim.clock()));
        }
    }

    /// Notifies all registered `onSimulationPause` listeners when the simulation is paused.
    static void notifyOnSimulationPause(final Simulation simulation) {
        final var sim = (CloudSimPlus) simulation;
        for (final var listener : sim.getOnSimulationPauseListeners()) {
            sim.setOnSimulationPauseListeners(Listeners.removeIfOneTime(sim.getOnSimulationPauseListeners(), listener));
            listener.update(EventInfo.of(listener, sim.clock()));
        }
    }

    /// Notifies all registered `onEventProcessing` listeners when an event is processed.
    static void notifyOnEventProcessing(final Simulation simulation, final SimEvent evt) {
        final var sim = (CloudSimPlus) simulation;
        for (final var listener : sim.getOnEventProcessingListeners()) {
            sim.setOnEventProcessingListeners(Listeners.removeIfOneTime(sim.getOnEventProcessingListeners(), listener));
            listener.update(evt);
        }
    }

    /// Notifies all registered `onClockTick` listeners when the simulation clock advances.
    static void notifyOnClockTick(final Simulation simulation, final double clock) {
        final var sim = (CloudSimPlus) simulation;
        for (final var listener : sim.getOnClockTickListeners()) {
            sim.setOnClockTickListeners(Listeners.removeIfOneTime(sim.getOnClockTickListeners(), listener));
            listener.update(EventInfo.of(listener, clock));
        }
    }
}
