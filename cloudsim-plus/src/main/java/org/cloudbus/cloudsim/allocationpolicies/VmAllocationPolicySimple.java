/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
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
 * the host for a VM, that one with the fewest PEs in use.
 * <b>It is therefore a Worst Fit policy</b>, allocating each VM into the host with most available PEs.
 *
 * <p><b>NOTE: This policy doesn't perform optimization of VM allocation by means of VM migration.</b></p>
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class VmAllocationPolicySimple extends VmAllocationPolicyAbstract {
    /**
     * Instantiates a VmAllocationPolicySimple.
     */
    public VmAllocationPolicySimple() {
        super();
    }

    /**
     * Instantiates a VmAllocationPolicySimple, changing the {@link Function} to select a Host for a Vm
     * in order to define a different policy.
     *
     * @param findHostForVmFunction a {@link Function} to select a Host for a given Vm.
     * @see VmAllocationPolicy#setFindHostForVmFunction(java.util.function.BiFunction)
     */
    public VmAllocationPolicySimple(final BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction) {
        super(findHostForVmFunction);
    }

    /**
     * Gets the first suitable host from the {@link #getHostList()} that has the fewest number of used PEs (i.e, higher free PEs).
     * @return an {@link Optional} containing a suitable Host to place the VM or an empty {@link Optional} if not found
     *
     * @todo The number of free PEs may be taken directly from each Host in a List,
     * avoiding the use of Maps that doesn't ensure order.
     * The entries are being sorted just to ensure that
     * the results are always the same for a specific static simulation.
     * Without the sort, usually the allocation of Hosts to VMs
     * is different during debug, because of the unsorted nature of the Map.
     */
    @Override
    public Optional<Host> findHostForVm(final Vm vm) {
        final Map<Host, Long> map = getHostFreePesMap();
        return map.entrySet()
            .stream()
            .filter(e -> e.getKey().isSuitableForVm(vm))
            .max(Comparator.comparingLong(Map.Entry::getValue))
            .map(Map.Entry::getKey);
    }

}
