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

import static java.util.Comparator.comparingDouble;

/**
 * A VM selection policy that selects for migration the VM with Minimum Utilization (MU)
 * of CPU.
 *
 * <p>If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:
 * <ul>
 * <li><a href="https://doi.org/10.1002/cpe.1867">Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley and Sons, Ltd, New York, USA, 2012</a></li>
 * </ul>
 * </p>
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 3.0
 */
public class VmSelectionPolicyMinimumUtilization implements VmSelectionPolicy {
    @Override
    public Optional<Vm> getVmToMigrate(final Host host) {
        final List<Vm> migratableVms = host.getMigratableVms();
        if (migratableVms.isEmpty()) {
            return Optional.empty();
        }

        final Predicate<Vm> inMigration = Vm::isInMigration;
        final Comparator<Vm> vmCpuUsageComparator = comparingDouble(Vm::getCpuPercentUtilization);
        return migratableVms.stream()
                            .filter(inMigration.negate())
                            .min(vmCpuUsageComparator);
    }

}
