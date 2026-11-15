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

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.vms.VmSimple;
import org.cloudsimplus.vms.VmTestUtil;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/// @author Manoel Campos da Silva Filho
class VmSelectionPolicyTest {
    private static final long RAM = 10_000;
    private static final long BW = 10_000;
    private static final long STORAGE = 100_000;
    private static final int HOST_CORES = 4;
    private static final double MIPS = 1000;

    /// The default implementation returns the VM the policy selects if the filter accepts it.
    /// Otherwise, it returns the first accepted migratable VM,
    /// so a policy that doesn't override it can still select more VMs from the same Host.
    @Test
    void defaultFilteredSelectionFallsBackToTheFirstAcceptedVm() {
        final var host = HostSimple.of(RAM, BW, STORAGE, new CoreCapacity(HOST_CORES, MIPS));
        final var vm0 = createdVm(host, 0);
        final var vm1 = createdVm(host, 1);
        final VmSelectionPolicy policy = ignored -> Optional.of(vm0);

        assertAll(
            () -> assertEquals(Optional.of(vm0), policy.getVmToMigrate(host, vm -> true)),
            () -> assertEquals(Optional.of(vm1), policy.getVmToMigrate(host, vm -> vm != vm0)),
            () -> assertEquals(Optional.empty(), policy.getVmToMigrate(host, vm -> false)));
    }

    private static VmSimple createdVm(final Host host, final int id) {
        final var vm = VmTestUtil.createVm(id, MIPS, 1);
        vm.setHost(host);
        vm.setCreated(true);
        host.getVmList().add(vm);
        return vm;
    }
}
