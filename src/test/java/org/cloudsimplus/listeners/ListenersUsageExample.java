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

import lombok.NonNull;

import java.util.List;

/// Shows how a class uses [Listeners] to store its listeners in immutable lists.
/// It's used as an external `{@snippet}` in the [Listeners] documentation.
/// Since it's in the test sources, the example code is compiled and checked by the build,
/// and [ListenersTest] runs it.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
final class ListenersUsageExample {
    // @start region="usage"
    private List<EventListener<EventInfo>> onEventListeners = List.of();

    ListenersUsageExample addOnEventListener(@NonNull final EventListener<EventInfo> listener) {
        onEventListeners = Listeners.add(onEventListeners, listener);
        return this;
    }

    boolean removeOnEventListener(@NonNull final EventListener<EventInfo> listener) {
        final var oldList = onEventListeners;
        onEventListeners = Listeners.remove(oldList, listener);
        return onEventListeners.size() != oldList.size();
    }

    void notifyOnEventListeners(final double time) {
        for (final var listener : onEventListeners) { // for-each: reads the field once
            listener.update(EventInfo.of(listener, time));
        }
    }
    // @end
}
