/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudbus.cloudsim.allocationpolicies;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A Worst Fit VmAllocationPolicy implementation that chooses, as
 * the host for a VM, that one with the least number of PEs in use,
 * which are enough for the VM.
 *
 * <p>This is a really computationally complex policy since the worst-case complexity
 * to allocate a Host for a VM is O(N), where N is the number of Hosts.
 * Such an implementation is not appropriate for large scale scenarios.
 * <b>Additionally, such a policy may increase resource idleness.</b></p>
 *
 * <p><b>NOTE: This policy doesn't perform optimization of VM allocation by means of VM migration.</b></p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.6.0
 *
 * @see VmAllocationPolicyFirstFit
 * @see VmAllocationPolicyBestFit
 */
public class VmAllocationPolicyWorstFit extends VmAllocationPolicyAbstract {
    /**
     * Gets the first suitable host from the {@link #getHostList()}
     * that has the least number of PEs in use (i.e. the most number of free PEs).
     * @return an {@link Optional} containing a suitable Host to place the VM or an empty {@link Optional} if not found
     */
    @Override
    protected Optional<Host> defaultFindHostForVm(final Vm vm) {
        final Comparator<Host> activeComparator = Comparator.comparing(Host::isActive);
        final Comparator<Host> comparator = activeComparator.thenComparingLong(Host::getFreePesNumber);

        final Stream<Host> stream = isParallelHostSearchEnabled() ? getHostList().stream().parallel() : getHostList().stream();
        return stream
                .filter(host -> host.isSuitableForVm(vm))
                .max(comparator);
    }

}
