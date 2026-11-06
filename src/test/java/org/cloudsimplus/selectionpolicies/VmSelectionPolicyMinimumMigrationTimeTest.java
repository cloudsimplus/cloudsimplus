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
package org.cloudsimplus.selectionpolicies;

import org.cloudsimplus.datacenters.DatacenterCharacteristicsSimple;
import org.cloudsimplus.datacenters.DatacenterCharacteristics.Distribution;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.cloudsimplus.datacenters.DatacenterCharacteristics.Distribution.PRIVATE;
import static org.cloudsimplus.datacenters.DatacenterCharacteristics.Distribution.PUBLIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// @author Manoel Campos da Silva Filho
class VmSelectionPolicyMinimumMigrationTimeTest {
    private static final long LARGE = 4000;
    private static final long SMALL = 1000;

    private final VmSelectionPolicy policy = new VmSelectionPolicyMinimumMigrationTime();

    @Test
    void selectsVmWithLowestOccupiedRamInPrivateDatacenter() {
        final Vm largeCapacityIdle = createVm(LARGE, 0, LARGE);
        final Vm smallCapacityBusy = createVm(SMALL, SMALL, SMALL);

        final var host = createHost(PRIVATE, List.of(smallCapacityBusy, largeCapacityIdle));

        assertEquals(largeCapacityIdle, policy.getVmToMigrate(host).orElseThrow());
    }

    @Test
    void selectsVmWithLowestStorageInPublicDatacenter() {
        final Vm largeStorageIdle = createVm(SMALL, 0, LARGE);
        final Vm smallStorageBusy = createVm(LARGE, LARGE, SMALL);

        final var host = createHost(PUBLIC, List.of(largeStorageIdle, smallStorageBusy));

        assertEquals(smallStorageBusy, policy.getVmToMigrate(host).orElseThrow());
    }

    @Test
    void returnsEmptyWhenThereIsNoMigratableVm() {
        assertTrue(policy.getVmToMigrate(createHost(PRIVATE, List.of())).isEmpty());
    }

    private static Vm createVm(final long ramCapacity, final long occupiedRam, final long storage) {
        final var vm = Mockito.mock(VmSimple.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(vm.isInMigration()).thenReturn(false);
        Mockito.when(vm.getRam().getCapacity()).thenReturn(ramCapacity);
        Mockito.when(vm.getRam().getOccupied()).thenReturn(occupiedRam);
        Mockito.when(vm.getStorage().getCapacity()).thenReturn(storage);
        return vm;
    }

    private static Host createHost(final Distribution distribution, final List<Vm> vms) {
        final var characteristics = Mockito.mock(DatacenterCharacteristicsSimple.class);
        Mockito.when(characteristics.getDistribution()).thenReturn(distribution);

        final var datacenter = Mockito.mock(DatacenterSimple.class);
        Mockito.when(datacenter.getCharacteristics()).thenReturn(characteristics);

        final var host = Mockito.mock(HostSimple.class);
        Mockito.when(host.getDatacenter()).thenReturn(datacenter);
        Mockito.when(host.getMigratableVms()).thenReturn(vms);
        return host;
    }
}
