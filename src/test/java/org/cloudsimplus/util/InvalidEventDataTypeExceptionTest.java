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
