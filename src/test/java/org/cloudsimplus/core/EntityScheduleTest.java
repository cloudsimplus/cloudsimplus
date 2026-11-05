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
import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.network.topologies.BriteNetworkTopology;
import org.cloudsimplus.network.topologies.NetworkTopology;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// Checks the rules of the [SimEntity] `schedule*()` methods,
/// the single way to send events.
/// See [#617](https://github.com/cloudsimplus/cloudsimplus/issues/617).
///
/// @author Manoel Campos da Silva Filho
class EntityScheduleTest {
    private static final int TAG = CloudSimTag.CLOUDLET_SUBMIT;
    private static final int INT_DATA = 10;
    private static final double LATENCY = 2;
    private static final double BANDWIDTH = 1000;

    private CloudSimPlus simulation;
    private EventRecorderEntity src;
    private EventRecorderEntity dest;

    @BeforeEach
    void setUp() {
        simulation = new CloudSimPlus();
        src = new EventRecorderEntity(simulation);
        dest = new EventRecorderEntity(simulation);
    }

    /// An int data must not be taken as the tag of an overload with a delay.
    @Test
    void intDataToAnotherEntityIsNotTakenAsTag() {
        src.schedule(dest, TAG, INT_DATA);
        simulation.start();
        assertEquals(List.of(new RecordedEvent(0, TAG, INT_DATA)), dest.processedEvents);
    }

    @Test
    void intDataToItselfIsNotTakenAsTag() {
        src.schedule(TAG, INT_DATA);
        simulation.start();
        assertEquals(List.of(new RecordedEvent(0, TAG, INT_DATA)), src.processedEvents);
    }

    /// Every overload with a delay must also take the data,
    /// so overloads with and without a delay always have different arities.
    @Test
    void everyOverloadWithDelayAlsoTakesData() {
        final var overloadsWithDelayButNoData =
            Arrays.stream(SimEntity.class.getMethods())
                  .filter(m -> m.getName().startsWith("schedule"))
                  .filter(EntityScheduleTest::hasDelay)
                  .filter(m -> !hasData(m))
                  .map(Method::toString)
                  .toList();

        assertEquals(List.of(), overloadsWithDelayButNoData);
    }

    private static boolean hasDelay(final Method method) {
        return Arrays.asList(method.getParameterTypes()).contains(double.class);
    }

    private static boolean hasData(final Method method) {
        return Arrays.asList(method.getParameterTypes()).contains(Object.class);
    }

    @Test
    void negativeDelayIsChangedToZero() {
        assertTrue(src.schedule(dest, -5, TAG, "evt"));
        simulation.start();
        assertEquals(List.of(new RecordedEvent(0, TAG, "evt")), dest.processedEvents);
    }

    @Test
    void infiniteDelayThrows() {
        assertThrows(IllegalArgumentException.class, () -> src.schedule(dest, Double.POSITIVE_INFINITY, TAG, "evt"));
    }

    @Test
    void eventToEntityOutsideSimulationIsNotSent() {
        assertFalse(src.schedule(SimEntity.NULL, TAG, "evt"));
        assertFalse(src.scheduleFirst(SimEntity.NULL, TAG, "evt"));
    }

    /// An entity from another simulation may have the same id as one in this simulation.
    @Test
    void eventToEntityFromAnotherSimulationIsNotSent() {
        final var otherSimulation = new CloudSimPlus();
        new EventRecorderEntity(otherSimulation);
        final var otherDest = new EventRecorderEntity(otherSimulation);
        assertEquals(dest.getId(), otherDest.getId());

        assertFalse(src.schedule(otherDest, TAG, "evt"));
        assertFalse(src.scheduleFirst(otherDest, TAG, "evt"));
    }

    @Test
    void eventBuiltByCallerToEntityFromAnotherSimulationIsNotSent() {
        final var otherDest = new EventRecorderEntity(new CloudSimPlus());
        assertFalse(src.schedule(new CloudSimEvent(0, src, otherDest, TAG, "evt")));
        assertFalse(src.scheduleFirst(new CloudSimEvent(0, src, otherDest, TAG, "evt")));
    }

    @Test
    void eventBuiltByCallerToEntityOutsideSimulationIsNotSent() {
        assertFalse(src.schedule(new CloudSimEvent(0, src, SimEntity.NULL, TAG, "evt")));
        assertFalse(src.scheduleFirst(new CloudSimEvent(0, src, SimEntity.NULL, TAG, "evt")));
    }

    /// The network delay must not be looked up for an invalid destination,
    /// since a topology may throw for an entity it doesn't know.
    @Test
    void networkDelayIsNotLookedUpForInvalidDestination() {
        final var topology = Mockito.mock(NetworkTopology.class);
        Mockito.when(topology.getDelay(Mockito.any(), Mockito.any())).thenThrow(IllegalArgumentException.class);
        simulation.setNetworkTopology(topology);

        final var otherDest = new EventRecorderEntity(new CloudSimPlus());
        assertFalse(src.schedule(otherDest, TAG, "evt"));
        assertFalse(src.scheduleFirst(otherDest, TAG, "evt"));
        assertFalse(src.schedule(SimEntity.NULL, TAG, "evt"));
    }

    @Test
    void networkDelayIsAddedForAnotherEntity() {
        connectEntities();
        src.schedule(dest, TAG, "evt");
        simulation.start();
        assertEquals(List.of(new RecordedEvent(LATENCY, TAG, "evt")), dest.processedEvents);
    }

    @Test
    void networkDelayIsAddedForHighPriorityEventToAnotherEntity() {
        connectEntities();
        src.scheduleFirst(dest, TAG, "evt");
        simulation.start();
        assertEquals(List.of(new RecordedEvent(LATENCY, TAG, "evt")), dest.processedEvents);
    }

    @Test
    void networkDelayIsNotAddedForItself() {
        connectEntities();
        src.schedule(TAG, "evt");
        simulation.start();
        assertEquals(List.of(new RecordedEvent(0, TAG, "evt")), src.processedEvents);
    }

    /// An event built by the caller keeps its time.
    @Test
    void networkDelayIsNotAddedForEventBuiltByCaller() {
        connectEntities();
        src.schedule(new CloudSimEvent(0, src, dest, TAG, "evt"));
        simulation.start();
        assertEquals(List.of(new RecordedEvent(0, TAG, "evt")), dest.processedEvents);
    }

    private void connectEntities() {
        final var topology = new BriteNetworkTopology();
        topology.addLink(src, dest, BANDWIDTH, LATENCY);
        simulation.setNetworkTopology(topology);
    }

    private record RecordedEvent(double time, int tag, Object data) { }

    /// An entity that just records the events with the [#TAG] it processes.
    private static final class EventRecorderEntity extends CloudSimEntity {
        private final List<RecordedEvent> processedEvents = new ArrayList<>();

        private EventRecorderEntity(final Simulation simulation) {
            super(simulation);
        }

        @Override
        protected void startInternal() {/**/}

        @Override
        public void processEvent(final SimEvent evt) {
            if (evt.getTag() == TAG) {
                processedEvents.add(new RecordedEvent(evt.getTime(), evt.getTag(), evt.getData()));
            }
        }
    }
}
