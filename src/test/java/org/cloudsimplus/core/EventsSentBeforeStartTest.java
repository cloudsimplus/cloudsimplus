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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// Checks that events sent before the simulation starts are held and sent when it starts.
/// See [#614](https://github.com/cloudsimplus/cloudsimplus/issues/614).
///
/// @author Manoel Campos da Silva Filho
class EventsSentBeforeStartTest {
    private static final int TAG = CloudSimTag.CLOUDLET_SUBMIT;
    private static final String START_EVT = "start";
    private static final double DELAY = 5;

    private CloudSimPlus simulation;
    private EventRecorderEntity entity;

    @BeforeEach
    void setUp() {
        simulation = new CloudSimPlus();
        entity = new EventRecorderEntity(simulation);
    }

    @Test
    void eventAtTimeZeroIsProcessedAtTimeZero() {
        assertTrue(entity.schedule(TAG, "evt"));
        simulation.start();
        assertEquals(List.of(new RecordedEvent(0, "evt")), entity.eventsExceptStart());
    }

    @Test
    void delayedEventIsProcessedAtItsTime() {
        entity.schedule(DELAY, TAG, "evt");
        simulation.start();
        assertEquals(List.of(new RecordedEvent(DELAY, "evt")), entity.eventsExceptStart());
    }

    @Test
    void eventsAreProcessedAfterEntitiesStartEvents() {
        entity.schedule(TAG, "evt");
        simulation.start();
        assertEquals(START_EVT, entity.processedEvents.getFirst().data());
    }

    @Test
    void eventsKeepTheOrderTheyWereSent() {
        entity.schedule(TAG, "evt1");
        entity.schedule(TAG, "evt2");
        entity.schedule(TAG, "evt3");
        simulation.start();

        final var expected = List.of(new RecordedEvent(0, "evt1"), new RecordedEvent(0, "evt2"), new RecordedEvent(0, "evt3"));
        assertEquals(expected, entity.eventsExceptStart());
    }

    @Test
    void highPriorityEventIsProcessedBeforeOtherEventsSentBeforeStart() {
        entity.schedule(TAG, "evt");
        entity.scheduleFirstNow(entity, TAG, "first");
        simulation.start();

        final var expected = List.of(new RecordedEvent(0, "first"), new RecordedEvent(0, "evt"));
        assertEquals(expected, entity.eventsExceptStart());
    }

    @Test
    void eventSentAfterSimulationFinishesIsRejected() {
        simulation.start();
        assertFalse(entity.schedule(TAG, "evt"));
    }

    @Test
    void eventSentDirectlyToSimulationAfterItFinishesIsNotQueued() {
        simulation.start();
        simulation.sendNow(entity, entity, TAG, "evt");
        simulation.sendFirst(entity, entity, 0, TAG, "first");
        assertTrue(simulation.noFutureEvents());
    }

    private record RecordedEvent(double time, Object data) { }

    /// An entity that just records the events it processes.
    private static final class EventRecorderEntity extends CloudSimEntity {
        private final List<RecordedEvent> processedEvents = new ArrayList<>();

        private EventRecorderEntity(final Simulation simulation) {
            super(simulation);
        }

        @Override
        protected void startInternal() {
            schedule(TAG, START_EVT);
        }

        @Override
        public void processEvent(final SimEvent evt) {
            if (evt.getTag() == TAG) {
                processedEvents.add(new RecordedEvent(evt.getTime(), evt.getData()));
            }
        }

        private List<RecordedEvent> eventsExceptStart() {
            return processedEvents.stream().filter(evt -> !START_EVT.equals(evt.data())).toList();
        }
    }
}
