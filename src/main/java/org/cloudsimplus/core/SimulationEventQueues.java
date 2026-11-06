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

import org.cloudsimplus.core.events.CloudSimEvent;
import org.cloudsimplus.core.events.DeferredQueue;
import org.cloudsimplus.core.events.EventQueue;
import org.cloudsimplus.core.events.FutureQueue;
import org.cloudsimplus.core.events.SimEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

/// Stores the event queues of a [Simulation] and implements the operations on them.
/// There is one object of this class per simulation, created by [CloudSim].
/// The rules to accept an event according to the simulation state are in [CloudSim#schedule(SimEvent)].
///
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
final class SimulationEventQueues implements SimulationEvents {
    /// The queue of events that will be sent in a future simulation time.
    private final FutureQueue future = new FutureQueue();

    /// The deferred event queue.
    private final DeferredQueue deferred = new DeferredQueue();

    /// A map of entities and predicates that define the events a given entity is waiting for.
    /// Received events are filtered based on the predicate associated with each entity
    /// so that just the resulting events are sent to the entity.
    private final Map<SimEntity, Predicate<SimEvent>> waitPredicates = new HashMap<>();

    @Override
    public void wait(final CloudSimEntity src, final Predicate<SimEvent> predicate) {
        src.setState(SimEntity.State.WAITING);
        if (predicate != Simulation.ANY_EVT) {
            // If a predicate has been used, store it in order to check incoming events that matches it
            waitPredicates.put(src, predicate);
        }
    }

    @Override
    public SimEvent select(final SimEntity dest, final Predicate<SimEvent> predicate) {
        final SimEvent evt = findFirstDeferred(dest, predicate);
        if(evt != SimEvent.NULL) {
            deferred.remove(evt);
        }

        return evt;
    }

    @Override
    public SimEvent findFirstDeferred(final SimEntity dest, final Predicate<SimEvent> predicate) {
        return filterEventsToDestinationEntity(deferred, predicate, dest).findFirst().orElse(SimEvent.NULL);
    }

    /// Gets a stream of events inside a specific queue that match a given predicate
    /// and are targeted to a specific entity.
    ///
    /// @param queue the queue to get the events from
    /// @param predicate the event selection predicate
    /// @param dest entity that the event has to be sent to
    /// @return a Stream of events from the queue
    private Stream<SimEvent> filterEventsToDestinationEntity(final EventQueue queue, final Predicate<SimEvent> predicate, final SimEntity dest) {
        return queue.stream().filter(predicate.and(evt -> evt.getDestination() == dest));
    }

    @Override
    public SimEvent cancel(final SimEntity src, final Predicate<SimEvent> predicate) {
        final SimEvent canceled =
            future.stream()
                  .filter(isEventSourceEqualsTo(predicate, src))
                  .findFirst()
                  .orElse(SimEvent.NULL);
        future.remove(canceled);
        return canceled;
    }

    @Override
    public boolean cancelAll(final SimEntity src, final Predicate<SimEvent> predicate) {
        final int previousSize = future.size();
        future.removeIf(isEventSourceEqualsTo(predicate, src));
        return previousSize < future.size();
    }

    private Predicate<SimEvent> isEventSourceEqualsTo(final Predicate<SimEvent> predicate, final SimEntity src) {
        return predicate.and(evt -> evt.getSource().equals(src));
    }

    @Override
    public void pauseEntity(final SimEntity src, final double delay) {
        future.addEvent(new CloudSimEvent(SimEvent.Type.HOLD_DONE, delay, src));
        src.setState(SimEntity.State.HOLDING);
    }

    @Override
    public long getNumberOfFutureEvents(final Predicate<SimEvent> predicate){
        return future.stream().filter(predicate).count();
    }

    @Override
    public boolean isThereAnyFutureEvt(final Predicate<SimEvent> predicate){
        return future.stream().anyMatch(predicate);
    }

    /// Passes a [SEND][SimEvent.Type#SEND] event straight to its destination entity if it's waiting for that event.
    /// Otherwise, adds the event to the deferred queue.
    /// @param evt the event to process
    void processSendEvent(final SimEvent evt) {
        if (evt.getDestination() == SimEntity.NULL) {
            throw new IllegalArgumentException("Attempt to send to a null entity detected.");
        }

        final var destEnt = (CloudSimEntity)evt.getDestination();
        if (destEnt.getState() != SimEntity.State.WAITING) {
            deferred.addEvent(evt);
            return;
        }

        final var eventPredicate = waitPredicates.get(destEnt);
        if (eventPredicate == null || eventPredicate.test(evt)) {
            destEnt.setEventBuffer(new CloudSimEvent(evt));
            destEnt.setState(SimEntity.State.RUNNABLE);
            waitPredicates.remove(destEnt);
            return;
        }

        deferred.addEvent(evt);
    }

    /// Adds an event to the future queue, in the order given by its time.
    /// @param evt the event to add
    void addFutureEvent(final SimEvent evt) {
        future.addEvent(evt);
    }

    /// Adds an event to the future queue, ahead of the other events with the same time.
    /// @param evt the event to add
    void addFutureEventFirst(final SimEvent evt) {
        future.addEventFirst(evt);
    }

    /// {@return the first event of the future queue}
    SimEvent firstFutureEvent() {
        return future.first();
    }

    /// Removes an event from the future queue.
    /// @param evt the event to remove
    void removeFutureEvent(final SimEvent evt) {
        future.remove(evt);
    }

    /// {@return true if there are no future events (the future queue is empty), false otherwise}
    boolean noFutureEvents() {
        return future.isEmpty();
    }

    /// {@return the maximum number of events that have ever existed at the same time inside the future queue}
    long getMaxFutureEventsNumber() {
        return future.getMaxEventsNumber();
    }

    /// {@return the total number of events generated in the future queue}
    long getGeneratedFutureEventsNumber() {
        return future.getSerial();
    }
}
