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
package org.cloudsimplus.builders;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.VmDatacenterEventInfo;
import org.cloudsimplus.listeners.VmHostEventInfo;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.schedulers.cloudlet.CloudletScheduler;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

/// A Builder class to create [Vm] objects using the default
/// values defined in [Vm] class.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.0
///
/// @see VmSimple#setDefaultRamCapacity(long)
/// @see VmSimple#setDefaultBwCapacity(long)
/// @see VmSimple#setDefaultStorageCapacity(long)
@Accessors(chain = true)
public class VmBuilder implements Builder {
    /// A [Function] used to create VMs.
    /// It receives the number of cores and the MIPS capacity of each core for the VM it will create.
    @Setter @NonNull private Function<CoreCapacity, Vm> vmCreationFunction;
    @Getter @Setter private double mips = 1000;

    /// Default number of CPU cores of each VM to create.
    @Getter @Setter private long cores = 1;
    private int numberOfCreatedVms;
    private final DatacenterBrokerSimple broker;
    @Setter @Nullable private Supplier<CloudletScheduler> cloudletSchedulerSupplier;
    @Setter @NonNull private EventListener<VmHostEventInfo> onHostPlacementListener = EventListener.NULL;
    @Setter @NonNull private EventListener<VmHostEventInfo> onHostRemovalListener = EventListener.NULL;
    @Setter @NonNull private EventListener<VmDatacenterEventInfo> onVmCreationFailureListener = EventListener.NULL;
    @Setter @NonNull private EventListener<VmHostEventInfo> onUpdateVmProcessingListener = EventListener.NULL;

    public VmBuilder(final DatacenterBrokerSimple broker) {
        this.broker = Objects.requireNonNull(broker);
        this.numberOfCreatedVms = 0;
        this.vmCreationFunction = this::defaultVmCreationFunction;
    }

    /// Creates and submits one VM to its broker.
    /// @return this builder
    public VmBuilder createAndSubmit() {
        return createAndSubmit(1);
    }

    /// Creates and submits a list of VM to its broker.
    /// @return this builder
    public VmBuilder createAndSubmit(final int amount) {
        final List<Vm> vms = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            final Vm vm = vmCreationFunction.apply(new CoreCapacity(cores, mips));
            if(cloudletSchedulerSupplier != null) {
                vm.setCloudletScheduler(cloudletSchedulerSupplier.get());
            }

            vm.addOnHostPlacementListener(onHostPlacementListener)
              .addOnHostRemovalListener(onHostRemovalListener)
              .addOnCreationFailureListener(onVmCreationFailureListener)
              .addOnUpdateProcessingListener(onUpdateVmProcessingListener)
              .setBroker(broker);
            vms.add(vm);
        }
        broker.submitVmList(vms);
        return this;
    }

    public List<Vm> getVms() {
        return broker.getVmWaitingList();
    }

    private Vm defaultVmCreationFunction(final CoreCapacity capacity) {
        return new VmSimple(numberOfCreatedVms++, capacity);
    }

    public Vm getVmById(final int id) {
        return broker.getVmWaitingList().stream()
            .filter(vm -> vm.getId() == id)
            .findFirst().orElse(Vm.NULL);
    }
}
