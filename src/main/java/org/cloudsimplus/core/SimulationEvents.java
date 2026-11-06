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

import org.cloudsimplus.core.events.SimEvent;

import java.util.function.Predicate;

/// Finds, selects and cancels the events of a [Simulation], and makes entities wait or pause.
/// It's accessed by [Simulation#events()].
/// Events are sent by entities using [SimEntity#schedule(SimEvent)] and the other `schedule*()` methods.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
/// @see Simulation#events()
public sealed interface SimulationEvents permits SimulationEventQueues, SimulationEventsNull {
    /// An attribute that implements the Null Object Design Pattern for [SimulationEvents] objects.
    SimulationEvents NULL = new SimulationEventsNull();

    /// Cancels the first event from the future event queue that matches a given [Predicate]
    /// and was sent by a given entity, then removes it from the queue.
    ///
    /// @param src entity that scheduled the event
    /// @param predicate   the event selection predicate
    /// @return the removed event or [SimEvent#NULL] if not found
    SimEvent cancel(SimEntity src, Predicate<SimEvent> predicate);

    /// Cancels all events from the future event queue that matches a given [Predicate]
    /// and were sent by a given entity, then removes those from the queue.
    ///
    /// @param src entity that scheduled the event
    /// @param predicate   the event selection predicate
    /// @return true if at least one event has been canceled; false otherwise
    boolean cancelAll(SimEntity src, Predicate<SimEvent> predicate);

    /// Selects the first deferred event that matches a given predicate
    /// and removes it from the queue.
    ///
    /// @param dest entity that the event has to be sent to
    /// @param predicate    the event selection predicate
    /// @return the removed event or [SimEvent#NULL] if not found
    SimEvent select(SimEntity dest, Predicate<SimEvent> predicate);

    /// Find the first-deferred event matching a [Predicate].
    ///
    /// @param dest entity that the event has to be sent to
    /// @param predicate    the event selection predicate
    /// @return the first matched event or [SimEvent#NULL] if not found
    SimEvent findFirstDeferred(SimEntity dest, Predicate<SimEvent> predicate);

    /// Sets the state of an entity to [SimEntity.State#WAITING],
    /// making it to wait for events that satisfy a given predicate.
    /// Only such events will be passed to the entity.
    /// This is done to avoid unnecessary context switch.
    ///
    /// @param src entity that scheduled the event
    /// @param predicate   the event selection predicate
    void wait(CloudSimEntity src, Predicate<SimEvent> predicate);

    /// Pauses an entity for some time.
    /// @param src   entity to be paused
    /// @param delay the time period for which the entity will be inactive
    void pauseEntity(SimEntity src, double delay);

    /// {@return the number of events in the future queue which match a given predicate}
    /// @param predicate the predicate to filter the list of future events.
    long getNumberOfFutureEvents(Predicate<SimEvent> predicate);

    /// Checks if there is any event in the future queue that matches a given predicate.
    ///
    /// @param predicate the predicate to select the desired events
    /// @return true if any event matching the given predicate is found, false otherwise
    boolean isThereAnyFutureEvt(Predicate<SimEvent> predicate);
}
