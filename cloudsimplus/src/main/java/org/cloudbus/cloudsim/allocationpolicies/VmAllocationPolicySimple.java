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
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Comparator.comparing;

/**
 * A VmAllocationPolicy implementation that chooses, as
 * the host for a VM, that one with the fewest PEs in use.
 * <b>It is therefore a Worst Fit policy</b>, allocating each VM into the host with most available PEs.
 *
 * <p>This is a really computationally complex policy since the worst-case complexity
 * to allocate a Host for a VM is O(N), where N is the number of Hosts.
 * Such an implementation is not appropriate for large scale scenarios.</p>
 *
 * <p><b>NOTE: This policy doesn't perform optimization of VM allocation by means of VM migration.</b></p>
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 *
 * @see VmAllocationPolicyFirstFit
 * @see VmAllocationPolicyBestFit
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
     */
    @Override
    protected Optional<Host> defaultFindHostForVm(final Vm vm) {
        final Comparator<Host> comparator = comparing(Host::isActive).thenComparingLong(Host::getFreePesNumber);

        final var hostStream = isParallelHostSearchEnabled() ? getHostList().stream().parallel() : getHostList().stream();
        return hostStream.filter(host -> host.isSuitableForVm(vm)).max(comparator);
    }

}
