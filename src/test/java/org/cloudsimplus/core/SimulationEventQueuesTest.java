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

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.core.events.CloudSimEvent;
import org.cloudsimplus.core.events.SimEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
class SimulationEventQueuesTest {
    private static final int TAG = CloudSimTag.NONE;
    private static final int OTHER_TAG = CloudSimTag.VM_CREATE_ACK;

    private final CloudSimPlus simulation = new CloudSimPlus();
    private final CloudSimEntity src = new DatacenterBrokerSimple(simulation);
    private final CloudSimEntity otherSrc = new DatacenterBrokerSimple(simulation);
    private final CloudSimEntity dest = new DatacenterBrokerSimple(simulation);
    private final SimulationEventQueues queues = new SimulationEventQueues();

    private static SimEvent newEvent(final SimEntity src, final SimEntity dest, final int tag) {
        return new CloudSimEvent(0, src, dest, tag, null);
    }

    /// {@return an event added to the deferred queue, since its destination isn't waiting for events}
    private SimEvent addDeferredEvent() {
        final var evt = newEvent(src, dest, TAG);
        queues.processSendEvent(evt);
        return evt;
    }

    private SimEvent addFutureEvent(final SimEntity src, final int tag) {
        final var evt = newEvent(src, dest, tag);
        queues.addFutureEvent(evt);
        return evt;
    }

    @Test
    void findFirstDeferredKeepsTheEventInTheQueue() {
        final var evt = addDeferredEvent();
        assertSame(evt, queues.findFirstDeferred(dest, Simulation.ANY_EVT));
        assertSame(evt, queues.findFirstDeferred(dest, Simulation.ANY_EVT));
    }

    @Test
    void selectRemovesTheEventFromTheQueue() {
        final var evt = addDeferredEvent();
        assertSame(evt, queues.select(dest, Simulation.ANY_EVT));
        assertSame(SimEvent.NULL, queues.select(dest, Simulation.ANY_EVT));
    }

    @Test
    void selectIgnoresEventsToOtherEntities() {
        addDeferredEvent();
        assertSame(SimEvent.NULL, queues.select(otherSrc, Simulation.ANY_EVT));
    }

    @Test
    void cancelRemovesOnlyTheFirstMatchingEventFromTheSource() {
        final var otherSrcEvt = addFutureEvent(otherSrc, TAG);
        final var evt1 = addFutureEvent(src, TAG);
        addFutureEvent(src, TAG);

        assertSame(evt1, queues.cancel(src, Simulation.ANY_EVT));
        assertEquals(2, queues.getNumberOfFutureEvents(Simulation.ANY_EVT));
        assertTrue(queues.isThereAnyFutureEvt(otherSrcEvt::equals));
    }

    @Test
    void cancelReturnsNullObjectWhenNoEventMatches() {
        addFutureEvent(otherSrc, TAG);
        assertSame(SimEvent.NULL, queues.cancel(src, Simulation.ANY_EVT));
    }

    @Test
    void cancelAllRemovesOnlyMatchingEventsFromTheSource() {
        final var otherSrcEvt = addFutureEvent(otherSrc, TAG);
        final var otherTagEvt = addFutureEvent(src, OTHER_TAG);
        addFutureEvent(src, TAG);
        addFutureEvent(src, TAG);

        assertTrue(queues.cancelAll(src, evt -> evt.getTag() == TAG));

        assertEquals(2, queues.getNumberOfFutureEvents(Simulation.ANY_EVT));
        assertTrue(queues.isThereAnyFutureEvt(otherSrcEvt::equals));
        assertTrue(queues.isThereAnyFutureEvt(otherTagEvt::equals));
    }

    @Test
    void cancelAllReturnsFalseAndKeepsEventsWhenNoneMatches() {
        addFutureEvent(otherSrc, TAG);
        addFutureEvent(src, OTHER_TAG);

        assertFalse(queues.cancelAll(src, evt -> evt.getTag() == TAG));

        assertEquals(2, queues.getNumberOfFutureEvents(Simulation.ANY_EVT));
    }

    @Test
    void pauseEntityHoldsTheEntityAndAddsAFutureEvent() {
        queues.pauseEntity(src, 1);
        assertEquals(SimEntity.State.HOLDING, src.getState());
        assertEquals(SimEvent.Type.HOLD_DONE, queues.firstFutureEvent().getType());
    }

    @Test
    void sendEventMatchingWaitPredicateMakesEntityRunnableInsteadOfDeferringTheEvent() {
        queues.wait(dest, evt -> evt.getTag() == TAG);
        queues.processSendEvent(newEvent(src, dest, TAG));

        assertEquals(SimEntity.State.RUNNABLE, dest.getState());
        assertSame(SimEvent.NULL, queues.findFirstDeferred(dest, Simulation.ANY_EVT));
    }

    @Test
    void sendEventNotMatchingWaitPredicateIsDeferredAndEntityKeepsWaiting() {
        queues.wait(dest, evt -> evt.getTag() == TAG);
        final var evt = newEvent(src, dest, OTHER_TAG);
        queues.processSendEvent(evt);

        assertEquals(SimEntity.State.WAITING, dest.getState());
        assertSame(evt, queues.findFirstDeferred(dest, Simulation.ANY_EVT));
    }

    @Test
    void sendEventToNullEntityThrowsException() {
        final var evt = newEvent(src, SimEntity.NULL, TAG);
        assertThrows(IllegalArgumentException.class, () -> queues.processSendEvent(evt));
    }

    @Test
    void nullSimulationUsesNullEvents() {
        final var events = Simulation.NULL.events();
        assertSame(SimulationEvents.NULL, events);
        assertSame(SimEvent.NULL, events.cancel(src, Simulation.ANY_EVT));
        assertFalse(events.cancelAll(src, Simulation.ANY_EVT));
        assertSame(SimEvent.NULL, events.select(dest, Simulation.ANY_EVT));
        assertSame(SimEvent.NULL, events.findFirstDeferred(dest, Simulation.ANY_EVT));
        assertEquals(0, events.getNumberOfFutureEvents(Simulation.ANY_EVT));
        assertFalse(events.isThereAnyFutureEvt(Simulation.ANY_EVT));
    }
}
