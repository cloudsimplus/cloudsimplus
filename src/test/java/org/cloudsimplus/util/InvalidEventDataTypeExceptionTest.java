package org.cloudsimplus.util;

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.core.SimEntity;
import org.cloudsimplus.core.events.CloudSimEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Manoel Campos da Silva Filho
 */
class InvalidEventDataTypeExceptionTest {
    private static final String TAG_NAME = "CLOUDLET_UPDATE_ATTRIBUTES";
    private static final String REQUIRED_CLASS = "Runnable";

    private SimEntity sender;
    private SimEntity receiver;

    @BeforeEach
    void setUp() {
        final var simulation = new CloudSimPlus();
        sender = new DatacenterBrokerSimple(simulation).setName("Sender");
        receiver = new DatacenterBrokerSimple(simulation).setName("Receiver");
    }

    @Test
    void messageIncludesDataTypeAndEventSourceAndDestination() {
        final var evt = createEvent(new CloudletSimple(1000, 1));
        final var expected =
            "0.00: CLOUDLET_UPDATE_ATTRIBUTES event data must be a Runnable but it was CloudletSimple. " +
            "Event sent from Sender to Receiver.";
        assertEquals(expected, newException(evt).getMessage());
    }

    @Test
    void messageShowsNullWhenEventHasNoData() {
        final var evt = createEvent(null);
        final var expected =
            "0.00: CLOUDLET_UPDATE_ATTRIBUTES event data must be a Runnable but it was null. " +
            "Event sent from Sender to Receiver.";
        assertEquals(expected, newException(evt).getMessage());
    }

    private CloudSimEvent createEvent(final Object data) {
        return new CloudSimEvent(0, sender, receiver, CloudSimTag.CLOUDLET_UPDATE_ATTRIBUTES, data);
    }

    private static InvalidEventDataTypeException newException(final CloudSimEvent evt) {
        return new InvalidEventDataTypeException(evt, TAG_NAME, REQUIRED_CLASS);
    }
}
