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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Checks that an `addOn...Listeners(List)` method of an object (such as a Cloudlet)
/// stores the listeners in the same list used by its `removeOn...Listener` method,
/// and that objects sharing a list don't affect each other.
/// This keeps each `addOn...Listeners` method in sync with the single-listener methods for the same event.
///
/// @param <O> the type of the object that has the listeners
/// @param <L> the type of the listeners
/// @param factory creates a new object
/// @param addAll calls the `addOn...Listeners(List)` method of an object
/// @param remove calls the `removeOn...Listener` method of an object for the same event
/// @param listener the listener to add and remove
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
public record SharedListenersAssert<O, L>(
    Supplier<O> factory, BiConsumer<O, List<L>> addAll, BiPredicate<O, L> remove, L listener)
{
    /// Checks that removing a listener from an object doesn't affect another object sharing the same list.
    public void assertRemovingFromOneObjectDoesNotAffectOther() {
        final var first = factory.get();
        final var second = factory.get();
        final var shared = List.of(listener);
        addAll.accept(first, shared);
        addAll.accept(second, shared);

        assertTrue(remove.test(first, listener), "listener should be stored in the list used by the remove method");
        assertFalse(remove.test(first, listener), "listener should be removed from the first object");
        assertTrue(remove.test(second, listener), "the second object should keep the shared listener");
    }

    /// Checks that a mutable list is copied, so changing it later doesn't affect the object.
    public void assertMutableListIsCopied() {
        final var object = factory.get();
        final var mutable = new ArrayList<>(List.of(listener));
        addAll.accept(object, mutable);
        mutable.clear();

        assertTrue(remove.test(object, listener), "clearing the given list should not remove the listener from the object");
    }
}
