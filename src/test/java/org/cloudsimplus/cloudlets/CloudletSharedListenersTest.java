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
package org.cloudsimplus.cloudlets;

import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.SharedListenersAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/// Tests the [CloudletListeners] `addOn...(List)` methods, which receive lists that can be shared by many Cloudlets.
/// Each event type has a case, to keep these methods in sync with the single-listener ones.
class CloudletSharedListenersTest {
    private static final EventListener<CloudletVmEventInfo> LISTENER = info -> {};

    static Stream<SharedListenersAssert<CloudletListeners, EventListener<CloudletVmEventInfo>>> cases() {
        return Stream.of(
            newCase(CloudletListeners::addOnStart, CloudletListeners::removeOnStart),
            newCase(CloudletListeners::addOnFinish, CloudletListeners::removeOnFinish),
            newCase(CloudletListeners::addOnUpdateProcessing, CloudletListeners::removeOnUpdateProcessing)
        );
    }

    private static SharedListenersAssert<CloudletListeners, EventListener<CloudletVmEventInfo>> newCase(
        final BiConsumer<CloudletListeners, List<EventListener<CloudletVmEventInfo>>> addAll,
        final BiPredicate<CloudletListeners, EventListener<CloudletVmEventInfo>> remove)
    {
        return new SharedListenersAssert<>(() -> CloudletTestUtil.createCloudlet().listeners(), addAll, remove, LISTENER);
    }

    @ParameterizedTest
    @MethodSource("cases")
    void removingFromOneCloudletDoesNotAffectOther(final SharedListenersAssert<CloudletListeners, EventListener<CloudletVmEventInfo>> sharedAssert) {
        sharedAssert.assertRemovingFromOneObjectDoesNotAffectOther();
    }

    @ParameterizedTest
    @MethodSource("cases")
    void mutableListIsCopied(final SharedListenersAssert<CloudletListeners, EventListener<CloudletVmEventInfo>> sharedAssert) {
        sharedAssert.assertMutableListIsCopied();
    }

    @Test
    void cloudletsSharingListNotifyListeners() {
        final var calls = new ArrayList<Long>();
        final List<EventListener<CloudletVmEventInfo>> shared = List.of(info -> calls.add(info.getCloudlet().getId()));
        final var first = CloudletTestUtil.createCloudlet(1);
        final var second = CloudletTestUtil.createCloudlet(2);
        first.listeners().addOnUpdateProcessing(shared);
        second.listeners().addOnUpdateProcessing(shared);

        CloudletListeners.notifyOnUpdateProcessing(first, 0);
        CloudletListeners.notifyOnUpdateProcessing(second, 0);
        assertEquals(List.of(1L, 2L), calls);
    }

    @Test
    void addSharedListToCloudletWithListenersKeepsBoth() {
        final var calls = new ArrayList<String>();
        final var cloudlet = CloudletTestUtil.createCloudlet();
        cloudlet.listeners().addOnUpdateProcessing(info -> calls.add("own"));
        cloudlet.listeners().addOnUpdateProcessing(List.of(info -> calls.add("shared")));

        CloudletListeners.notifyOnUpdateProcessing(cloudlet, 0);
        assertEquals(List.of("own", "shared"), calls);
    }
}
