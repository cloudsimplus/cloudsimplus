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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.Listeners;
import org.cloudsimplus.listeners.VmDatacenterEventInfo;
import org.cloudsimplus.listeners.VmHostEventInfo;
import org.jspecify.annotations.Nullable;

import java.util.List;

/// Manages and notifies the [EventListener]s of a [Vm].
///
/// This is a short-lived view/facade created on demand by [Vm#listeners()]
/// and not stored as a field on the [Vm], so it adds **0 bytes** of retained heap memory.
/// The underlying listener lists are stored directly on [VmAbstract]: they start as the
/// shared immutable `List.of()` and can also share user-provided immutable lists across many
/// VMs via the `addOn...(List)` methods.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
/// @see Vm#listeners()
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class VmListeners {
    /// A Null Object instance that ignores all listener registrations.
    public static final VmListeners NULL = new VmListeners(null);

    /// The VM whose listener lists are managed by this view.
    /// Declared as [VmAbstract] rather than [Vm] to access its package-private
    /// listener list getters/setters directly without `instanceof` checks on every call,
    /// and marked `@Nullable` only to support [#NULL] (since [Vm#NULL] doesn't extend [VmAbstract]).
    @Nullable
    private final VmAbstract vm;

    /// Adds a listener object that will be notified when a [Host]
    /// is allocated to the Vm, that is, when the Vm is placed for the first
    /// time into a Host or when it's migrated to another Host.
    ///
    /// @param listener the listener to add
    /// @return this `VmListeners` instance
    /// @see #addOnMigrationStart(EventListener)
    public VmListeners addOnHostPlacement(final EventListener<VmHostEventInfo> listener) {
        if (vm != null) {
            vm.setOnHostPlacementListeners(Listeners.add(vm.getOnHostPlacementListeners(), listener));
        }
        return this;
    }

    /// Adds a list of listeners that will be notified when a [Host] is allocated to the Vm.
    ///
    /// The list can be shared by many VMs, to reduce memory use:
    /// create it once with `List.of(...)` and pass the same list to all VMs (e.g. inside a loop).
    /// If this VM has no listeners of this type, it stores the given list itself,
    /// so it uses no memory for its own list.
    /// Changes later made in one VM don't affect the others.
    /// A mutable list is copied, so changing it later doesn't affect any VM.
    /// The other `addOn...(List)` methods work the same way.
    ///
    /// {@snippet :
    /// // Created once, outside the loop
    /// final List<EventListener<VmHostEventInfo>> placementListeners = List.of(this::onHostPlacement);
    /// for (int i = 0; i < VMS; i++) {
    ///     final var vm = VmSimple.of(MIPS, PES);
    ///     vm.listeners().addOnHostPlacement(placementListeners);
    ///     vmList.add(vm);
    /// }
    /// }
    ///
    /// @param listeners the listeners to add
    /// @return this `VmListeners` instance
    /// @see #addOnHostPlacement(EventListener)
    public VmListeners addOnHostPlacement(final List<EventListener<VmHostEventInfo>> listeners) {
        if (vm != null) {
            vm.setOnHostPlacementListeners(Listeners.addAll(vm.getOnHostPlacementListeners(), listeners));
        }
        return this;
    }

    /// Removes a listener from the `onHostPlacement` listener list.
    ///
    /// @param listener the listener to remove
    /// @return true if the listener was found and removed, false otherwise
    public boolean removeOnHostPlacement(final EventListener<VmHostEventInfo> listener) {
        if (vm == null) {
            return false;
        }
        final var oldList = vm.getOnHostPlacementListeners();
        vm.setOnHostPlacementListeners(Listeners.remove(oldList, listener));
        return vm.getOnHostPlacementListeners().size() != oldList.size();
    }

    /// Adds a listener object that will be notified when a VM starts migrating to a target [Host].
    /// When the listener is notified, it receives a [VmHostEventInfo] object
    /// informing the target Host where the VM is being migrated.
    ///
    /// @param listener the listener to add
    /// @return this `VmListeners` instance
    /// @see #addOnHostPlacement(EventListener)
    public VmListeners addOnMigrationStart(final EventListener<VmHostEventInfo> listener) {
        if (vm != null) {
            vm.setOnMigrationStartListeners(Listeners.add(vm.getOnMigrationStartListeners(), listener));
        }
        return this;
    }

    /// Adds a list of listeners that will be notified when the VM starts migrating to a target [Host].
    /// The list can be shared by many VMs, to reduce memory use.
    ///
    /// @param listeners the listeners to add (see [#addOnHostPlacement(List)] for details)
    /// @return this `VmListeners` instance
    /// @see #addOnMigrationStart(EventListener)
    public VmListeners addOnMigrationStart(final List<EventListener<VmHostEventInfo>> listeners) {
        if (vm != null) {
            vm.setOnMigrationStartListeners(Listeners.addAll(vm.getOnMigrationStartListeners(), listeners));
        }
        return this;
    }

    /// Removes a listener from the `onMigrationStart` listener list.
    ///
    /// @param listener the listener to remove
    /// @return true if the listener was found and removed, false otherwise
    public boolean removeOnMigrationStart(final EventListener<VmHostEventInfo> listener) {
        if (vm == null) {
            return false;
        }
        final var oldList = vm.getOnMigrationStartListeners();
        vm.setOnMigrationStartListeners(Listeners.remove(oldList, listener));
        return vm.getOnMigrationStartListeners().size() != oldList.size();
    }

    /// Adds a listener object that will be notified when a VM finishes migrating to a target [Host].
    /// When the listener is notified, it receives a [VmHostEventInfo] object
    /// informing the target Host where the VM has just migrated.
    ///
    /// @param listener the listener to add
    /// @return this `VmListeners` instance
    public VmListeners addOnMigrationFinish(final EventListener<VmHostEventInfo> listener) {
        if (vm != null) {
            vm.setOnMigrationFinishListeners(Listeners.add(vm.getOnMigrationFinishListeners(), listener));
        }
        return this;
    }

    /// Adds a list of listeners that will be notified when the VM finishes migrating to a target [Host].
    /// The list can be shared by many VMs, to reduce memory use.
    ///
    /// @param listeners the listeners to add (see [#addOnHostPlacement(List)] for details)
    /// @return this `VmListeners` instance
    /// @see #addOnMigrationFinish(EventListener)
    public VmListeners addOnMigrationFinish(final List<EventListener<VmHostEventInfo>> listeners) {
        if (vm != null) {
            vm.setOnMigrationFinishListeners(Listeners.addAll(vm.getOnMigrationFinishListeners(), listeners));
        }
        return this;
    }

    /// Removes a listener from the `onMigrationFinish` listener list.
    ///
    /// @param listener the listener to remove
    /// @return true if the listener was found and removed, false otherwise
    public boolean removeOnMigrationFinish(final EventListener<VmHostEventInfo> listener) {
        if (vm == null) {
            return false;
        }
        final var oldList = vm.getOnMigrationFinishListeners();
        vm.setOnMigrationFinishListeners(Listeners.remove(oldList, listener));
        return vm.getOnMigrationFinishListeners().size() != oldList.size();
    }

    /// Adds a listener object that will be notified when the Vm is moved/removed from a [Host].
    ///
    /// @param listener the listener to add
    /// @return this `VmListeners` instance
    public VmListeners addOnHostRemoval(final EventListener<VmHostEventInfo> listener) {
        if (vm != null) {
            vm.setOnHostRemovalListeners(Listeners.add(vm.getOnHostRemovalListeners(), listener));
        }
        return this;
    }

    /// Adds a list of listeners that will be notified when the Vm is moved/removed from a [Host].
    /// The list can be shared by many VMs, to reduce memory use.
    ///
    /// @param listeners the listeners to add (see [#addOnHostPlacement(List)] for details)
    /// @return this `VmListeners` instance
    /// @see #addOnHostRemoval(EventListener)
    public VmListeners addOnHostRemoval(final List<EventListener<VmHostEventInfo>> listeners) {
        if (vm != null) {
            vm.setOnHostRemovalListeners(Listeners.addAll(vm.getOnHostRemovalListeners(), listeners));
        }
        return this;
    }

    /// Removes a listener from the `onHostRemoval` listener list.
    ///
    /// @param listener the listener to remove
    /// @return true if the listener was found and removed, false otherwise
    public boolean removeOnHostRemoval(final EventListener<VmHostEventInfo> listener) {
        if (vm == null) {
            return false;
        }
        final var oldList = vm.getOnHostRemovalListeners();
        vm.setOnHostRemovalListeners(Listeners.remove(oldList, listener));
        return vm.getOnHostRemovalListeners().size() != oldList.size();
    }

    /// Adds a listener object that will be notified when the Vm fails in
    /// being placed, due to lack of a [Host] with enough resources in a specific [Datacenter].
    ///
    /// The [DatacenterBroker] is accountable for receiving the notification from the
    /// Datacenter and notifying the Listeners.
    ///
    /// @param listener the listener to add
    /// @return this `VmListeners` instance
    public VmListeners addOnCreationFailure(final EventListener<VmDatacenterEventInfo> listener) {
        if (vm != null) {
            vm.setOnCreationFailureListeners(Listeners.add(vm.getOnCreationFailureListeners(), listener));
        }
        return this;
    }

    /// Adds a list of listeners that will be notified when the Vm fails in being placed,
    /// due to lack of a [Host] with enough resources in a specific [Datacenter].
    /// The list can be shared by many VMs, to reduce memory use.
    ///
    /// @param listeners the listeners to add (see [#addOnHostPlacement(List)] for details)
    /// @return this `VmListeners` instance
    /// @see #addOnCreationFailure(EventListener)
    public VmListeners addOnCreationFailure(final List<EventListener<VmDatacenterEventInfo>> listeners) {
        if (vm != null) {
            vm.setOnCreationFailureListeners(Listeners.addAll(vm.getOnCreationFailureListeners(), listeners));
        }
        return this;
    }

    /// Removes a listener from the `onCreationFailure` listener list.
    ///
    /// @param listener the listener to remove
    /// @return true if the listener was found and removed, false otherwise
    public boolean removeOnCreationFailure(final EventListener<VmDatacenterEventInfo> listener) {
        if (vm == null) {
            return false;
        }
        final var oldList = vm.getOnCreationFailureListeners();
        vm.setOnCreationFailureListeners(Listeners.remove(oldList, listener));
        return vm.getOnCreationFailureListeners().size() != oldList.size();
    }

    /// Adds a listener object that will be notified every time
    /// the processing of the Vm is updated in its [Host].
    ///
    /// @param listener the listener to add
    /// @return this `VmListeners` instance
    public VmListeners addOnUpdateProcessing(final EventListener<VmHostEventInfo> listener) {
        if (vm != null) {
            vm.setOnUpdateProcessingListeners(Listeners.add(vm.getOnUpdateProcessingListeners(), listener));
        }
        return this;
    }

    /// Adds a list of listeners that will be notified every time
    /// the processing of the Vm is updated in its [Host].
    /// The list can be shared by many VMs, to reduce memory use.
    ///
    /// @param listeners the listeners to add (see [#addOnHostPlacement(List)] for details)
    /// @return this `VmListeners` instance
    /// @see #addOnUpdateProcessing(EventListener)
    public VmListeners addOnUpdateProcessing(final List<EventListener<VmHostEventInfo>> listeners) {
        if (vm != null) {
            vm.setOnUpdateProcessingListeners(Listeners.addAll(vm.getOnUpdateProcessingListeners(), listeners));
        }
        return this;
    }

    /// Removes a listener from the `onUpdateProcessing` listener list.
    ///
    /// @param listener the listener to remove
    /// @return true if the listener was found and removed, false otherwise
    public boolean removeOnUpdateProcessing(final EventListener<VmHostEventInfo> listener) {
        if (vm == null) {
            return false;
        }
        final var oldList = vm.getOnUpdateProcessingListeners();
        vm.setOnUpdateProcessingListeners(Listeners.remove(oldList, listener));
        return vm.getOnUpdateProcessingListeners().size() != oldList.size();
    }

    /// Notifies all registered `onHostPlacement` listeners when the [Vm] is placed into a [Host].
    ///
    /// **This method is used just internally and must not be called directly.**
    public static void notifyOnHostPlacement(final Vm vm) {
        final var v = (VmAbstract) vm;
        for (final var listener : v.getOnHostPlacementListeners()) {
            v.setOnHostPlacementListeners(Listeners.removeIfOneTime(v.getOnHostPlacementListeners(), listener));
            listener.update(VmHostEventInfo.of(listener, v));
        }
    }

    /// Notifies all registered `onHostRemoval` listeners when the [Vm] is moved/removed from a [Host].
    ///
    /// @param deallocatedHost the [Host] the [Vm] was moved/removed from
    static void notifyOnHostRemoval(final Vm vm, final Host deallocatedHost) {
        final var v = (VmAbstract) vm;
        for (final var listener : v.getOnHostRemovalListeners()) {
            v.setOnHostRemovalListeners(Listeners.removeIfOneTime(v.getOnHostRemovalListeners(), listener));
            listener.update(VmHostEventInfo.of(listener, v, deallocatedHost));
        }
    }

    /// Notifies all registered `onMigrationStart` listeners when the VM starts migration to a target Host.
    ///
    /// **This method is used just internally and must not be called directly.**
    ///
    /// @param targetHost the Host the VM is migrating to
    public static void notifyOnMigrationStart(final Vm vm, final Host targetHost) {
        final var v = (VmAbstract) vm;
        for (final var listener : v.getOnMigrationStartListeners()) {
            v.setOnMigrationStartListeners(Listeners.removeIfOneTime(v.getOnMigrationStartListeners(), listener));
            listener.update(VmHostEventInfo.of(listener, v, targetHost));
        }
    }

    /// Notifies all registered `onMigrationFinish` listeners when the VM finishes migration to a target Host.
    ///
    /// **This method is used just internally and must not be called directly.**
    ///
    /// @param targetHost the Host the VM has just migrated to
    public static void notifyOnMigrationFinish(final Vm vm, final Host targetHost) {
        final var v = (VmAbstract) vm;
        for (final var listener : v.getOnMigrationFinishListeners()) {
            v.setOnMigrationFinishListeners(Listeners.removeIfOneTime(v.getOnMigrationFinishListeners(), listener));
            listener.update(VmHostEventInfo.of(listener, v, targetHost));
        }
    }

    /// Notifies all registered `onCreationFailure` listeners when the Vm fails in
    /// being placed for lack of a [Host] with enough resources in a specific [Datacenter].
    ///
    /// **This method is used just internally and must not be called directly.**
    ///
    /// @param failedDatacenter the Datacenter where the VM creation failed
    public static void notifyOnCreationFailure(final Vm vm, final Datacenter failedDatacenter) {
        final var v = (VmAbstract) vm;
        for (final var listener : v.getOnCreationFailureListeners()) {
            v.setOnCreationFailureListeners(Listeners.removeIfOneTime(v.getOnCreationFailureListeners(), listener));
            listener.update(VmDatacenterEventInfo.of(listener, v, failedDatacenter));
        }
    }

    /// Notifies all registered `onUpdateProcessing` listeners when the processing of the Vm is updated in its [Host].
    static void notifyOnUpdateProcessing(final Vm vm) {
        final var v = (VmAbstract) vm;
        for (final var listener : v.getOnUpdateProcessingListeners()) {
            v.setOnUpdateProcessingListeners(Listeners.removeIfOneTime(v.getOnUpdateProcessingListeners(), listener));
            listener.update(VmHostEventInfo.of(listener, v));
        }
    }
}
