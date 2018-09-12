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
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A VmAllocationPolicy implementation that chooses, as
 * the host for a VM, that one with the most PEs in use.
 * <b>It is therefore a Best Fit policy</b>, allocating each VM into the host with the least available PEs
 * that are enough for the VM.
 *
 * <p><b>NOTE: This policy doesn't perform optimization of VM allocation by means of VM migration.</b></p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 3.0.1
 */
public class VmAllocationPolicyBestFit extends VmAllocationPolicyAbstract {
    /**
     * Instantiates a VmAllocationPolicyBestFit.
     */
    public VmAllocationPolicyBestFit() {
        super();
    }

    /**
     * Instantiates a VmAllocationPolicyBestFit, changing the {@link Function} to select a Host for a Vm
     * in order to define a different policy.
     *
     * @param findHostForVmFunction a {@link Function} to select a Host for a given Vm.
     * @see VmAllocationPolicy#setFindHostForVmFunction(BiFunction)
     */
    public VmAllocationPolicyBestFit(final BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction) {
        super(findHostForVmFunction);
    }

    /**
     * Gets the first suitable host from the {@link #getHostList()} that has the most number of used PEs (i.e, lower free PEs).
     * @return an {@link Optional} containing a suitable Host to place the VM or an empty {@link Optional} if not found
     *
     * @todo See TODOs inside the VmAllocationPolicySimple
     */
    @Override
    public Optional<Host> findHostForVm(final Vm vm) {
        final Map<Host, Long> map = getHostFreePesMap();
        return map.entrySet()
            .stream()
            .filter(entry -> entry.getKey().isSuitableForVm(vm))
            .min(Comparator.comparingLong(Map.Entry::getValue))
            .map(Map.Entry::getKey);
    }

}
