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
import org.cloudsimplus.mocks.CloudSimMocker;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/// @author Manoel Campos da Silva Filho
class CloudSimEntityTest {
    private static final int TAG = CloudSimTag.CLOUDLET_SUBMIT;

    /// Checks that the deferred event is not lost when there is a buffered event.
    /// See [#562](https://github.com/cloudsimplus/cloudsimplus/issues/562).
    @Test
    void runProcessesBufferedEventThenDeferredOne() {
        final var simulation = CloudSimMocker.createMock(CloudSimMocker::isRunning);
        final var entity = new EventRecorderEntity(simulation);
        final var bufferedEvt = new CloudSimEvent(entity, TAG, "buffered");
        final var deferredEvt = new CloudSimEvent(entity, TAG, "deferred");

        when(simulation.select(eq(entity), any())).thenReturn(deferredEvt, SimEvent.NULL);
        entity.setEventBuffer(bufferedEvt);
        entity.run();

        assertEquals(List.of(bufferedEvt, deferredEvt), entity.processedEvents);
    }

    /// An entity that just records the events it processes.
    private static final class EventRecorderEntity extends CloudSimEntity {
        private final List<SimEvent> processedEvents = new ArrayList<>();

        private EventRecorderEntity(final Simulation simulation) {
            super(simulation);
        }

        @Override
        protected void startInternal() {/**/}

        @Override
        public void processEvent(final SimEvent evt) {
            processedEvents.add(evt);
        }
    }
}
