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

import lombok.NonNull;
import org.cloudsimplus.allocationpolicies.migration.VmAllocationPolicyMigrationFirstFitStaticThreshold;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSuitability;
import org.cloudsimplus.selectionpolicies.VmSelectionPolicy;
import org.cloudsimplus.vms.Vm;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/// Allocates one different underloaded Host for each submitted VM from a List (in batch),
/// if [DatacenterBroker#isBatchVmCreation()] is true. This way,
/// the broker sends a List of VMs to be created in a single event
/// (instead of sending a new event for each VM).
///
/// If that is not enabled, this implementation will place VMs one-by-one
/// following the [VmAllocationPolicyMigrationFirstFitStaticThreshold].
///
/// This implementation is similar to [VmAllocationPolicyRoundRobin],
/// but it selects active and underloaded Hosts first, then performs selection
/// of Host to place a list of VMs to reduce the underload state of various Hosts at once.
///
/// This may not be the most reasonable policy and is provided
/// just as an example on how to implement a different policy for batch VM creation
/// when the related broker attribute is enabled.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 8.5.0
/// @see DatacenterBroker#setBatchVmCreation(boolean)
public class VmAllocationPolicyBatchPlacementUnderloadedHosts extends VmAllocationPolicyMigrationFirstFitStaticThreshold {
    public VmAllocationPolicyBatchPlacementUnderloadedHosts(final VmSelectionPolicy vmSelectionPolicy) {
        super(vmSelectionPolicy);
    }

    @Override
    protected Set<HostSuitability> allocateHostForVmInternal(final @NonNull List<Vm> vmList) {
        final Comparator<Host> comparator = Comparator.comparing(Host::isActive).thenComparing(this::isUnderloaded);
        final var hosts = getHostList().stream().sorted(comparator.reversed()).toList();

        int hostIdx = 0;
        final var suitabilities = new HashSet<HostSuitability>();
        final int attemptsTotal = hosts.size();
        //Find a Host until all VMs are assigned to a Host or all Hosts are tried
        for (int vmIdx = 0, attempt = 1; vmIdx < vmList.size() && attempt <= attemptsTotal;) {
            final var host = hosts.get(hostIdx);
            final var vm = vmList.get(vmIdx);
            //When a suitable underloaded Host is found, selects it to place the VM and try the next host for next VM
            final var suitability = host.getSuitabilityFor(vm);
            suitabilities.add(suitability);
            if (suitability.fully()) {
                allocateHostForVm(vm, host);
                vmIdx++;
            } else attempt++;

            /* Always try the next host (as in a circular list),
            regardless if the current one was selected to the current VM or not,
            in order to increase the resource utilization of as many underloaded Hosts as possible. */
            hostIdx = ++hostIdx % hosts.size();
        }

        return suitabilities;
    }
}
