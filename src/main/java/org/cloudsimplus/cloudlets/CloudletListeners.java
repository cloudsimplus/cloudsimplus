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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.Listeners;
import org.cloudsimplus.vms.Vm;
import org.jspecify.annotations.Nullable;

import java.util.List;

/// Manages and notifies the [EventListener]s of a [Cloudlet].
///
/// This is a short-lived view/facade created on demand by [Cloudlet#listeners()]
/// and not stored as a field on the [Cloudlet], so it adds **0 bytes** of retained heap memory.
/// The underlying listener lists are stored directly on [CloudletAbstract]: they start as the
/// shared immutable `List.of()` and can also share user-provided immutable lists across many
/// Cloudlets via the `addOn...(List)` methods.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
/// @see Cloudlet#listeners()
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class CloudletListeners {
    /// A Null Object instance that ignores all listener registrations.
    public static final CloudletListeners NULL = new CloudletListeners(null);

    /// The Cloudlet whose listener lists are managed by this view.
    /// Declared as [CloudletAbstract] rather than [Cloudlet] to access its package-private
    /// listener list getters/setters directly without `instanceof` checks on every call,
    /// and marked `@Nullable` only to support [#NULL] (since [Cloudlet#NULL] doesn't extend [CloudletAbstract]).
    @Nullable
    private final CloudletAbstract cloudlet;

    /// Adds a listener object that will be notified when the Cloudlet starts executing in some [Vm].
    ///
    /// @param listener the listener to add
    /// @return this `CloudletListeners` instance
    public CloudletListeners addOnStart(final EventListener<CloudletVmEventInfo> listener) {
        if (cloudlet != null) {
            cloudlet.setOnStartListeners(Listeners.add(cloudlet.getOnStartListeners(), listener));
        }
        return this;
    }

    /// Adds a list of listeners that will be notified when the Cloudlet starts executing in some [Vm].
    /// The list can be shared by many Cloudlets, to reduce memory use.
    ///
    /// @param listeners the listeners to add (see [#addOnFinish(List)] for details)
    /// @return this `CloudletListeners` instance
    public CloudletListeners addOnStart(final List<EventListener<CloudletVmEventInfo>> listeners) {
        if (cloudlet != null) {
            cloudlet.setOnStartListeners(Listeners.addAll(cloudlet.getOnStartListeners(), listeners));
        }
        return this;
    }

    /// Removes a listener from the `onStart` listener list.
    ///
    /// @param listener the listener to remove
    /// @return true if the listener was found and removed; false otherwise
    public boolean removeOnStart(final EventListener<CloudletVmEventInfo> listener) {
        if (cloudlet == null) {
            return false;
        }
        final var oldList = cloudlet.getOnStartListeners();
        cloudlet.setOnStartListeners(Listeners.remove(oldList, listener));
        return cloudlet.getOnStartListeners().size() != oldList.size();
    }

    /// Adds a listener object that will be notified every time
    /// the processing of the Cloudlet is updated in its [Vm].
    ///
    /// @param listener the listener to add
    /// @return this `CloudletListeners` instance
    /// @see Cloudlet#getFinishedLengthSoFar()
    public CloudletListeners addOnUpdateProcessing(final EventListener<CloudletVmEventInfo> listener) {
        if (cloudlet != null) {
            cloudlet.setOnUpdateProcessingListeners(Listeners.add(cloudlet.getOnUpdateProcessingListeners(), listener));
        }
        return this;
    }

    /// Adds a list of listeners that will be notified every time
    /// the processing of the Cloudlet is updated in its [Vm].
    /// The list can be shared by many Cloudlets, to reduce memory use.
    ///
    /// @param listeners the listeners to add (see [#addOnFinish(List)] for details)
    /// @return this `CloudletListeners` instance
    public CloudletListeners addOnUpdateProcessing(final List<EventListener<CloudletVmEventInfo>> listeners) {
        if (cloudlet != null) {
            cloudlet.setOnUpdateProcessingListeners(Listeners.addAll(cloudlet.getOnUpdateProcessingListeners(), listeners));
        }
        return this;
    }

    /// Removes a listener from the `onUpdateProcessing` listener list.
    ///
    /// @param listener the listener to remove
    /// @return true if the listener was found and removed; false otherwise
    public boolean removeOnUpdateProcessing(final EventListener<CloudletVmEventInfo> listener) {
        if (cloudlet == null) {
            return false;
        }
        final var oldList = cloudlet.getOnUpdateProcessingListeners();
        cloudlet.setOnUpdateProcessingListeners(Listeners.remove(oldList, listener));
        return cloudlet.getOnUpdateProcessingListeners().size() != oldList.size();
    }

    /// Adds a listener object that will be notified when a Cloudlet finishes
    /// its execution at a given [Vm].
    ///
    /// @param listener the listener to add
    /// @return this `CloudletListeners` instance
    public CloudletListeners addOnFinish(final EventListener<CloudletVmEventInfo> listener) {
        if (cloudlet != null) {
            cloudlet.setOnFinishListeners(Listeners.add(cloudlet.getOnFinishListeners(), listener));
        }
        return this;
    }

    /// Adds a list of listeners that will be notified when a Cloudlet finishes
    /// its execution at a given [Vm].
    ///
    /// The list can be shared by many Cloudlets, to reduce memory use:
    /// create it once with `List.of(...)` and pass the same list to all Cloudlets (e.g. inside a loop).
    /// If this Cloudlet has no listeners of this type, it stores the given list itself,
    /// so it uses no memory for its own list.
    /// Changes later made in one Cloudlet don't affect the others.
    /// A mutable list is copied, so changing it later doesn't affect any Cloudlet.
    ///
    /// {@snippet :
    /// // Created once, outside the loop
    /// final List<EventListener<CloudletVmEventInfo>> finishListeners = List.of(this::onCloudletFinish);
    /// for (int i = 0; i < CLOUDLETS; i++) {
    ///     final var cloudlet = CloudletSimple.of(LENGTH, PES);
    ///     cloudlet.listeners().addOnFinish(finishListeners);
    ///     cloudletList.add(cloudlet);
    /// }
    /// }
    ///
    /// @param listeners the listeners to add
    /// @return this `CloudletListeners` instance
    /// @see #addOnFinish(EventListener)
    public CloudletListeners addOnFinish(final List<EventListener<CloudletVmEventInfo>> listeners) {
        if (cloudlet != null) {
            cloudlet.setOnFinishListeners(Listeners.addAll(cloudlet.getOnFinishListeners(), listeners));
        }
        return this;
    }

    /// Removes a listener from the `onFinish` listener list.
    ///
    /// @param listener the listener to remove
    /// @return true if the listener was found and removed; false otherwise
    /// @see #addOnFinish(EventListener)
    public boolean removeOnFinish(final EventListener<CloudletVmEventInfo> listener) {
        if (cloudlet == null) {
            return false;
        }
        final var oldList = cloudlet.getOnFinishListeners();
        cloudlet.setOnFinishListeners(Listeners.remove(oldList, listener));
        return cloudlet.getOnFinishListeners().size() != oldList.size();
    }

    /// Notifies all registered `onStart` listeners when the Cloudlet starts executing.
    static void notifyOnStart(final Cloudlet cloudlet, final double time) {
        if (cloudlet.getVm() == Vm.NULL) {
            return;
        }

        final var c = (CloudletAbstract) cloudlet;
        for (final var listener : c.getOnStartListeners()) {
            c.setOnStartListeners(Listeners.removeIfOneTime(c.getOnStartListeners(), listener));
            listener.update(CloudletVmEventInfo.of(listener, time, c));
        }
    }

    /// Notifies all registered `onFinish` listeners when the Cloudlet finishes executing.
    static void notifyOnFinish(final Cloudlet cloudlet) {
        final var c = (CloudletAbstract) cloudlet;
        final var listeners = c.getOnFinishListeners();
        // Cleared before notifying, so that a listener added during notification is kept for a next finish
        c.setOnFinishListeners(List.of());
        for (final var listener : listeners) {
            listener.update(CloudletVmEventInfo.of(listener, c));
        }
    }

    /// Notifies all registered `onUpdateProcessing` listeners about the update on Cloudlet processing.
    static void notifyOnUpdateProcessing(final Cloudlet cloudlet, final double time) {
        if (!(cloudlet instanceof CloudletAbstract c)) {
            return;
        }

        for (final var listener : c.getOnUpdateProcessingListeners()) {
            c.setOnUpdateProcessingListeners(Listeners.removeIfOneTime(c.getOnUpdateProcessingListeners(), listener));
            listener.update(CloudletVmEventInfo.of(listener, time, c));
        }
    }
}
