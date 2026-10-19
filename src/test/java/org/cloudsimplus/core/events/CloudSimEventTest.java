package org.cloudsimplus.core.events;

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.core.SimEntity;
import org.cloudsimplus.mocks.CloudSimMocker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Manoel Campos da Silva Filho
 */
class CloudSimEventTest {
    private static final double CLOCK = 5;
    private static final double DELAY = 3;
    private static final int TAG = CloudSimTag.CLOUDLET_SUBMIT;
    private static final String DATA = "data";

    private SimEntity entity;

    @BeforeEach
    void setUp() {
        final var simulation = CloudSimMocker.createMock(mocker -> mocker.clock(CLOCK));
        entity = new DatacenterBrokerSimple(simulation);
    }

    @Test
    void timeIsCurrentClockPlusDelay() {
        final var event = new CloudSimEvent(DELAY, entity, TAG, DATA);
        assertEquals(CLOCK + DELAY, event.getTime());
    }

    @Test
    void negativeDelayIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new CloudSimEvent(-1, entity, TAG, DATA));
    }

    /**
     * Checks that a copy keeps the absolute time of the original event,
     * instead of adding it to the current simulation time again.
     * @see <a href="https://github.com/cloudsimplus/cloudsimplus/issues/561">Issue #561</a>
     */
    @Test
    void copyKeepsTimeOfOriginalEvent() {
        final var original = new CloudSimEvent(DELAY, entity, TAG, DATA);
        final var copy = new CloudSimEvent(original);

        assertEquals(original.getTime(), copy.getTime());
        assertEquals(original.getType(), copy.getType());
        assertSame(original.getSource(), copy.getSource());
        assertSame(original.getDestination(), copy.getDestination());
        assertEquals(original.getTag(), copy.getTag());
        assertSame(original.getData(), copy.getData());
    }
}
