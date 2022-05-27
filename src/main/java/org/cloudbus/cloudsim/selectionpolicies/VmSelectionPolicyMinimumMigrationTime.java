/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.selectionpolicies;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Comparator.comparingLong;

/**
 * A VM selection policy that selects for migration the VM with Minimum Migration Time (MMT).
 *
 * <br>If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:<br>
 *
 * <ul>
 * <li><a href="https://doi.org/10.1002/cpe.1867">Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley and Sons, Ltd, New York, USA, 2012</a>
 * </ul>
 *
 * @author Anton Beloglazov
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
         * It should also consider the VM size.*/
        final Comparator<Vm> vmComparator = comparingLong(vm -> vm.getRam().getCapacity());
        final Predicate<Vm> vmPredicate = Vm::isInMigration;
        return migratableVms
                     .stream()
                     .filter(vmPredicate.negate())
                     .min(vmComparator);
	}
}
