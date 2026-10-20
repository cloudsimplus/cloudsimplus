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
package org.cloudsimplus.listeners;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListenersTest {
    private static final String FIRST = "first";
    private static final String SECOND = "second";
    private static final String THIRD = "third";

    @Test
    void addToEmptyList() {
        assertEquals(List.of(FIRST), Listeners.add(List.of(), FIRST));
    }

    @Test
    void addKeepsInsertionOrder() {
        final var listeners = Listeners.add(Listeners.add(Listeners.add(List.of(), FIRST), SECOND), THIRD);
        assertEquals(List.of(FIRST, SECOND, THIRD), listeners);
    }

    @Test
    void addDoesNotChangeGivenList() {
        final var listeners = List.of(FIRST);
        Listeners.add(listeners, SECOND);
        assertEquals(List.of(FIRST), listeners);
    }

    @Test
    void addDoesNotCreateDuplicates() {
        final var listeners = List.of(FIRST, SECOND);
        assertSame(listeners, Listeners.add(listeners, FIRST));
    }

    @Test
    void addReturnsImmutableList() {
        final var listeners = Listeners.add(List.of(), FIRST);
        assertThrows(UnsupportedOperationException.class, () -> listeners.add(SECOND));
    }

    @Test
    void addNullListenerThrowsException() {
        assertThrows(NullPointerException.class, () -> Listeners.add(List.of(), null));
    }

    @Test
    void removeKeepsOrderOfRemainingListeners() {
        assertEquals(List.of(FIRST, THIRD), Listeners.remove(List.of(FIRST, SECOND, THIRD), SECOND));
    }

    @Test
    void removeDoesNotChangeGivenList() {
        final var listeners = List.of(FIRST, SECOND);
        Listeners.remove(listeners, FIRST);
        assertEquals(List.of(FIRST, SECOND), listeners);
    }

    @Test
    void removeListenerNotInListReturnsSameInstance() {
        final var listeners = List.of(FIRST, SECOND);
        assertSame(listeners, Listeners.remove(listeners, THIRD));
    }

    @Test
    void removeFromEmptyListReturnsSameInstance() {
        final List<String> listeners = List.of();
        assertSame(listeners, Listeners.remove(listeners, FIRST));
    }

    @Test
    void removeLastListenerReturnsSharedEmptyList() {
        assertSame(List.of(), Listeners.remove(List.of(FIRST), FIRST));
    }

    @Test
    void usageExampleNotifiesListenersAddedAndRemovedDuringNotification() {
        final var example = new ListenersUsageExample();
        final var calls = new ArrayList<String>();
        final EventListener<EventInfo> added = info -> calls.add("added");
        example.addOnEventListener(info -> {
            calls.add(FIRST);
            example.removeOnEventListener(info.getListener());
            example.addOnEventListener(added);
        });
        example.addOnEventListener(info -> calls.add(SECOND));

        example.notifyOnEventListeners(0);
        assertEquals(List.of(FIRST, SECOND), calls);

        example.notifyOnEventListeners(1);
        assertEquals(List.of(FIRST, SECOND, SECOND, "added"), calls);
    }

    @Test
    void usageExampleRemoveReturnsIfListenerWasRemoved() {
        final var example = new ListenersUsageExample();
        final EventListener<EventInfo> listener = info -> {};
        example.addOnEventListener(listener);

        assertTrue(example.removeOnEventListener(listener));
        assertFalse(example.removeOnEventListener(listener));
    }
}
