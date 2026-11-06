/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.selectionpolicies;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.vms.Vm;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;

import static java.util.Comparator.comparingLong;
import static org.cloudsimplus.datacenters.DatacenterCharacteristics.Distribution.PRIVATE;

/// A VM selection policy that selects for migration the [Vm] with the Minimum Migration Time (MMT).
///
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 3.0
public class VmSelectionPolicyMinimumMigrationTime implements VmSelectionPolicy {
	@Override
	public Optional<Vm> getVmToMigrate(final Host host) {
		final List<Vm> migratableVms = host.getMigratableVms();
		if (migratableVms.isEmpty()) {
			return Optional.empty();
		}

        final Comparator<Vm> vmComparator = comparingLong(dataToMigrate(host));
        final Predicate<Vm> vmPredicate = Vm::isInMigration;
        return migratableVms
                     .stream()
                     .filter(vmPredicate.negate())
                     .min(vmComparator);
	}

    /// Gets a function that returns the amount of data (in MB) to transfer to migrate a VM from a given Host.
    /// All VMs share the same source Host, so the migration time just depends on that amount.
    /// The bandwidth isn't considered, since the policy selects the VM before the target Host is known.
    /// For the same reason, it considers only the source Datacenter to define the migration type.
    /// @param sourceHost the Host where the VMs are placed
    /// @return the occupied RAM for a live migration (from a private Datacenter), or the VM image size otherwise
    private static ToLongFunction<Vm> dataToMigrate(final Host sourceHost) {
        if (sourceHost.getDatacenter().getCharacteristics().getDistribution() == PRIVATE) {
            return vm -> vm.getRam().getOccupied();
        }

        return vm -> vm.getStorage().getCapacity();
    }
}
