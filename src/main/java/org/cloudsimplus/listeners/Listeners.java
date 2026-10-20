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

/// Utility methods to add and remove [EventListener]s from **immutable lists**.
/// Each change returns a new list that must replace the one stored in the owner object.
///
/// ## Why immutable lists
///
/// A listener may add or remove listeners while it's being notified
/// (a common case is a listener that removes itself, so it's called just once).
/// That happens in the same thread, since there is no parallelism inside a simulation.
/// Since the stored list is never changed, a for-each loop that is notifying listeners
/// keeps going over the old list. This way:
///
/// - no `ConcurrentModificationException` is thrown;
/// - no listener is skipped when a listener removes itself or another one;
/// - a listener added during notification is called only in the next round.
///
/// When the loop ends, the old list becomes garbage.
///
/// **Notification methods must use a for-each loop (or a local copy of the field).**
/// An indexed loop that reads the field in each step may see the new list in the middle of the loop.
///
/// ## Why `List.of()`
///
/// CloudSim Plus may create millions of objects with listener lists, but most of them never get a listener.
/// Memory kept by each object matters more than the time to add or remove a listener,
/// which is rare and mostly happens while the scenario is set up.
/// Notification loops are in the hot path, so they must not copy anything.
///
/// | Option                                               | Memory (empty / 1 listener) |
/// |------------------------------------------------------|-----------------------------|
/// | `ArrayList`                                          | 24 B / 80 B                 |
/// | `HashSet`                                            | 64 B / ~150 B               |
/// | `CopyOnWriteArrayList`                               | 56 B / 64 B                 |
/// | `Collections.unmodifiableList(Arrays.asList(array))` | 0 B / 64 B                  |
/// | **`List.of(array)` (chosen)**                        | **0 B / 16 B**              |
///
/// Why the other options weren't chosen:
/// - `ArrayList`: throws `ConcurrentModificationException`; indexed loops skip listeners.
/// - `HashSet`: same problem; iterating scans all table slots, even with 1 listener.
/// - `CopyOnWriteArrayList`: creates a lock object and an empty array for each list.
/// - `Collections.unmodifiableList(Arrays.asList(array))`: avoids 1 temporary copy,
///   but keeps 2 extra objects alive.
///
/// Why `List.of(array)` was chosen:
/// - `List.of` copies the given array once more, but that copy is short-lived garbage, which is cheap for the JVM.
///   The list that is kept uses the least memory: `List.of()` is shared by all objects,
///   and lists with 1 or 2 elements need no array.
/// - Searches are linear. Lists have very few elements, and listeners are usually lambdas
///   compared by reference, so this is faster than computing hashes.
/// - Lists keep the order in which listeners were added (`HashSet` doesn't).
///
/// ## Usage
///
/// {@snippet class=org.cloudsimplus.listeners.ListenersUsageExample region=usage}
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
public final class Listeners {
    private Listeners() {/**/}

    /// Creates a new immutable list with the given listener added at the end.
    /// @param listeners the current immutable list of listeners
    /// @param listener the listener to add
    /// @param <T> the type of the listeners
    /// @return a new list with the listener added,
    ///         or the given list if it already has the listener
    @SuppressWarnings("unchecked")
    public static <T> List<T> add(@NonNull final List<T> listeners, @NonNull final T listener) {
        if (listeners.contains(listener)) {
            return listeners;
        }

        /* toArray() copies the elements straight into the given array, which has 1 free slot for the new listener.
         * This way, there is just 1 copy (List.of() lists use System.arraycopy internally). */
        final var array = listeners.toArray(new Object[listeners.size() + 1]);
        array[listeners.size()] = listener;
        return (List<T>) List.of(array);
    }

    /// Creates a new immutable list without the given listener.
    /// @param listeners the current immutable list of listeners
    /// @param listener the listener to remove (if null, nothing is removed)
    /// @param <T> the type of the listeners
    /// @return a new list without the listener,
    ///         or the given list (the same instance) if it doesn't have the listener.
    ///         If the removed listener was the last one, returns the shared empty `List.of()`.
    @SuppressWarnings("unchecked")
    public static <T> List<T> remove(@NonNull final List<T> listeners, final Object listener) {
        // List.of() lists can't have nulls and throw a NullPointerException when searching for null
        final int index = listener == null ? -1 : listeners.indexOf(listener);
        if (index < 0) {
            return listeners;
        }

        /* Copies the elements, except the removed one, straight into the new array, with just 1 copy.
         * Using System.arraycopy for the elements before and after the index would need toArray() first,
         * making 1 extra copy. Since lists have very few listeners, calling get(i) for each one is cheap. */
        final var array = new Object[listeners.size() - 1];
        for (int i = 0, j = 0; i < listeners.size(); i++) {
            if (i != index) {
                array[j++] = listeners.get(i);
            }
        }

        return (List<T>) List.of(array);
    }
}
