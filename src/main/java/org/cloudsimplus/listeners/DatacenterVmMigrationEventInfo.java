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

import lombok.Getter;
import lombok.NonNull;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.HostSuitability;
import org.cloudsimplus.vms.Vm;

/**
 * An interface that represents data to be passed
 * to {@link EventListener} objects that are registered to be notified
 * when a {@link Vm} migration is successful or not.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.0.3
 */
@Getter
public final class DatacenterVmMigrationEventInfo implements VmDatacenterEventInfo {
    /**
     * The VM that started a migration process.
     */
    private final Vm vm;

    private final double time;

    /**
     * Information about the suitability of the Host for the given VM.
     */
    private final HostSuitability suitability;

    private final EventListener<DatacenterVmMigrationEventInfo> listener;

    private DatacenterVmMigrationEventInfo(
        @NonNull final Vm vm,
        @NonNull final HostSuitability suitability,
        @NonNull final EventListener<DatacenterVmMigrationEventInfo> listener)
    {
        this.vm = vm;
        this.time = vm.getSimulation().clock();
        this.suitability = suitability;
        this.listener = listener;
    }

    @Override
    public Datacenter getDatacenter() {
        return vm.getHost().getDatacenter();
    }

    /**
     * @return true or false to indicate if the VM was successfully migrated or not.
     */
    public boolean isMigrationSuccessful() {
        return suitability.fully();
    }

    /**
     * Gets a {@code DatacenterVmMigrationEventInfo} instance from the given parameters.
     * The {@link #getDatacenter() Datacenter} attribute is defined as the {@link Datacenter} where the {@link Vm}
     * is running and the {@link #getTime()} is the current simulation time.
     * @param listener the listener to be notified about the event
     * @param vm the {@link Vm} that fired the event
     * @param suitability information about the suitability of the Host for the given VM.
     */
    public static DatacenterVmMigrationEventInfo of(final EventListener<DatacenterVmMigrationEventInfo> listener, final Vm vm, final HostSuitability suitability) {
        return new DatacenterVmMigrationEventInfo(vm, suitability, listener);
    }
}
