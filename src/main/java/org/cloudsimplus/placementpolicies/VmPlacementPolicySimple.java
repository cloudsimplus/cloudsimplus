/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.placementpolicies;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.Processor;
import org.cloudsimplus.vms.Vm;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Comparator.comparing;
import org.jspecify.annotations.Nullable;

/// A VmPlacementPolicy implementation that chooses, as
/// the host for a VM, that one with the fewest cores in use.
/// **It is therefore a Worst-Fit policy**, allocating each VM into the host with the highest number of available cores.
///
/// This is a really computationally complex policy since the worst-case complexity
/// to place a VM is O(N), where N is the number of Hosts.
/// Such an implementation is not appropriate for large scale scenarios.
///
/// **NOTE: This policy doesn't perform optimization of VM allocation using VM migration.**
///
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 1.0
///
/// @see VmPlacementPolicyFirstFit
/// @see VmPlacementPolicyBestFit
public class VmPlacementPolicySimple extends VmPlacementPolicyAbstract {
    /// Creates a VmPlacementPolicy.
    public VmPlacementPolicySimple() {
        super();
    }

    /// Creates a VmPlacementPolicy, changing the [Function] to select a Host for a Vm
    /// in order to define a different policy.
    ///
    /// @param findHostForVmFunction a [Function] to select a Host for a given Vm.
    /// @see VmPlacementPolicy#setFindHostForVmFunction(java.util.function.BiFunction)
    public VmPlacementPolicySimple(@Nullable final BiFunction<VmPlacementPolicy, Vm, Optional<Host>> findHostForVmFunction) {
        super(findHostForVmFunction);
    }

    /// Gets the first suitable host from the [#getHostList()] that has the fewest number of used cores (i.e, higher free cores).
    /// @return an [Optional] containing a suitable Host to place the VM or an empty [Optional] if not found
    @Override
    protected Optional<Host> defaultFindHostForVm(final Vm vm) {
        final Comparator<Host> comparator = comparing(Host::isActive).thenComparingLong(Processor::freeCores);

        final var hostStream = isParallelHostSearchEnabled() ? getHostList().stream().parallel() : getHostList().stream();
        return hostStream.filter(host -> host.isSuitableForVm(vm)).max(comparator);
    }
}
