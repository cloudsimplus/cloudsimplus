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
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmTestUtil;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/// @author Manoel Campos da Silva Filho
class VmSelectionPolicyTest {
    /// The default implementation returns the VM selected without the filter only if the filter accepts it,
    /// so a policy that doesn't override it never selects the same VM again.
    @Test
    void defaultFilteredSelectionReturnsTheSelectedVmOnlyIfAccepted() {
        final Vm vm = VmTestUtil.createVm(0, 1);
        final VmSelectionPolicy policy = host -> Optional.of(vm);
        final var host = Host.NULL;

        assertEquals(Optional.of(vm), policy.getVmToMigrate(host, candidate -> true));
        assertEquals(Optional.empty(), policy.getVmToMigrate(host, candidate -> !candidate.equals(vm)));
    }
}
