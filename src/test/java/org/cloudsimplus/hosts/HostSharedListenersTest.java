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
package org.cloudsimplus.hosts;

import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostEventInfo;
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;
import org.cloudsimplus.listeners.SharedListenersAssert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

/// Tests the [HostListeners] `addOn...(List)` methods, which receive lists that can be shared by many Hosts.
/// Each event type has a case, to keep these methods in sync with the single-listener ones.
class HostSharedListenersTest {
    private static final int CORES = 1;
    private static final EventListener<HostEventInfo> HOST_LISTENER = info -> {};
    private static final EventListener<HostUpdatesVmsProcessingEventInfo> UPDATE_LISTENER = info -> {};

    static Stream<SharedListenersAssert<HostListeners, ?>> cases() {
        return Stream.of(
            newCase(HostListeners::addOnStartup, HostListeners::removeOnStartup, HOST_LISTENER),
            newCase(HostListeners::addOnShutdown, HostListeners::removeOnShutdown, HOST_LISTENER),
            newCase(HostListeners::addOnUpdateProcessing, HostListeners::removeOnUpdateProcessing, UPDATE_LISTENER)
        );
    }

    private static <L> SharedListenersAssert<HostListeners, L> newCase(
        final BiConsumer<HostListeners, List<L>> addAll, final BiPredicate<HostListeners, L> remove, final L listener)
    {
        return new SharedListenersAssert<>(() -> HostSimpleTest.createHostSimple(0, CORES).listeners(), addAll, remove, listener);
    }

    @ParameterizedTest
    @MethodSource("cases")
    void removingFromOneHostDoesNotAffectOther(final SharedListenersAssert<HostListeners, ?> sharedAssert) {
        sharedAssert.assertRemovingFromOneObjectDoesNotAffectOther();
    }

    @ParameterizedTest
    @MethodSource("cases")
    void mutableListIsCopied(final SharedListenersAssert<HostListeners, ?> sharedAssert) {
        sharedAssert.assertMutableListIsCopied();
    }
}
