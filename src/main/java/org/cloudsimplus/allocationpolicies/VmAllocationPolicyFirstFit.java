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
package org.cloudsimplus.allocationpolicies;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.vms.Vm;

import java.util.List;
import java.util.Optional;

/// A [First Fit VM allocation policy](https://en.wikipedia.org/wiki/First-fit_bin_packing)
/// which finds the first Host having enough resources to place a given VM.
/// This is a high time-efficient policy with a best-case complexity O(1)
/// and a worst-case complexity O(N), where N is the number of Hosts.
/// Additionally, such a policy is resource efficient, because it performs
/// server consolidation by trying to place the maximum number of VMs
/// into the same Host to increase Host's resource usage.
///
/// **NOTES:**
///
/// - This policy doesn't perform optimization of VM allocation using VM migration.
/// - It has a low computational complexity but may return
///   an inactive Host that will be activated, while there may be active Hosts
///   suitable for the VM.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.0.0
/// @see VmAllocationPolicyBestFit
/// @see VmAllocationPolicySimple
public class VmAllocationPolicyFirstFit extends VmAllocationPolicyAbstract {
    /** The index of the last host where a VM was placed. */
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.NONE)
    private int lastHostIndex;

    @Override
    protected Optional<Host> defaultFindHostForVm(final Vm vm) {
        final List<Host> hostList = getHostList();
        /* The for loop just defines the maximum number of Hosts to try.
         * When a suitable Host is found, the method returns immediately. */
        final int maxTries = hostList.size();
        for (int i = 0; i < maxTries; i++) {
            final Host host = hostList.get(lastHostIndex);
            if (host.isSuitableForVm(vm)) {
                return Optional.of(host);
            }

            /* If it gets here, the previous Host doesn't have capacity to place the VM.
             * Then, moves to the next Host.*/
            incLastHostIndex();
        }

        return Optional.empty();
    }

    /**
     * Increment the index to move to the next Host.
     * If the end of the Host list is reached, starts from the beginning. */
    protected void incLastHostIndex() {
        lastHostIndex = ++lastHostIndex % getHostList().size();
    }
}
