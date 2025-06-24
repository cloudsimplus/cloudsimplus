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

import static java.util.Comparator.comparingLong;

/**
 * A VM selection policy that selects for migration the {@link Vm} with the Minimum Migration Time (MMT).
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 3.0
 */
public class VmSelectionPolicyMinimumMigrationTime implements VmSelectionPolicy {
	@Override
	public Optional<Vm> getVmToMigrate(final Host host) {
		final List<Vm> migratableVms = host.getMigratableVms();
		if (migratableVms.isEmpty()) {
			return Optional.empty();
		}

        /* TODO It must compute the migration time based on the current RAM usage, not the capacity.
         * It should also consider the VM size. See DatacenterSimple.timeToMigrateVm*/
        final Comparator<Vm> vmComparator = comparingLong(vm -> vm.getRam().getCapacity());
        final Predicate<Vm> vmPredicate = Vm::isInMigration;
        return migratableVms
                     .stream()
                     .filter(vmPredicate.negate())
                     .min(vmComparator);
	}
}
