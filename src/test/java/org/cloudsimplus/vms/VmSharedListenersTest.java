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
package org.cloudsimplus.vms;

import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.SharedListenersAssert;
import org.cloudsimplus.listeners.VmDatacenterEventInfo;
import org.cloudsimplus.listeners.VmHostEventInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/// Tests the [VmListeners] `addOn...(List)` methods, which receive lists that can be shared by many VMs.
/// Each event type has a case, to keep these methods in sync with the single-listener ones.
class VmSharedListenersTest {
    private static final int CORES = 1;
    private static final EventListener<VmHostEventInfo> HOST_LISTENER = info -> {};
    private static final EventListener<VmDatacenterEventInfo> DATACENTER_LISTENER = info -> {};

    static Stream<SharedListenersAssert<VmListeners, ?>> cases() {
        return Stream.of(
            newCase(VmListeners::addOnHostPlacement, VmListeners::removeOnHostPlacement, HOST_LISTENER),
            newCase(VmListeners::addOnMigrationStart, VmListeners::removeOnMigrationStart, HOST_LISTENER),
            newCase(VmListeners::addOnMigrationFinish, VmListeners::removeOnMigrationFinish, HOST_LISTENER),
            newCase(VmListeners::addOnHostRemoval, VmListeners::removeOnHostRemoval, HOST_LISTENER),
            newCase(VmListeners::addOnUpdateProcessing, VmListeners::removeOnUpdateProcessing, HOST_LISTENER),
            newCase(VmListeners::addOnCreationFailure, VmListeners::removeOnCreationFailure, DATACENTER_LISTENER)
        );
    }

    private static <L> SharedListenersAssert<VmListeners, L> newCase(
        final BiConsumer<VmListeners, List<L>> addAll, final BiPredicate<VmListeners, L> remove, final L listener)
    {
        return new SharedListenersAssert<>(() -> VmTestUtil.createVm(0, CORES).listeners(), addAll, remove, listener);
    }

    @ParameterizedTest
    @MethodSource("cases")
    void removingFromOneVmDoesNotAffectOther(final SharedListenersAssert<VmListeners, ?> sharedAssert) {
        sharedAssert.assertRemovingFromOneObjectDoesNotAffectOther();
    }

    @ParameterizedTest
    @MethodSource("cases")
    void mutableListIsCopied(final SharedListenersAssert<VmListeners, ?> sharedAssert) {
        sharedAssert.assertMutableListIsCopied();
    }

    @Test
    void vmsSharingListNotifyListeners() {
        final var calls = new ArrayList<Long>();
        final List<EventListener<VmHostEventInfo>> shared = List.of(info -> calls.add(info.getVm().getId()));
        final var first = VmTestUtil.createVm(1, CORES);
        final var second = VmTestUtil.createVm(2, CORES);
        first.listeners().addOnHostPlacement(shared);
        second.listeners().addOnHostPlacement(shared);

        VmListeners.notifyOnHostPlacement(first);
        VmListeners.notifyOnHostPlacement(second);
        assertEquals(List.of(1L, 2L), calls);
    }
}
