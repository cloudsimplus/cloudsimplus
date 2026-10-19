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

/**
 * @author Manoel Campos da Silva Filho
 */
class CloudSimEntityTest {
    private static final int TAG = CloudSimTag.CLOUDLET_SUBMIT;

    /**
     * Checks that the deferred event is not lost when there is a buffered event.
     * See <a href="https://github.com/cloudsimplus/cloudsimplus/issues/562">#562</a>.
     */
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

    /**
     * An entity that just records the events it processes.
     */
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
