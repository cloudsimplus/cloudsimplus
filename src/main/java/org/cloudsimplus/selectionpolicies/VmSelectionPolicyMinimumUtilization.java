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

import static java.util.Comparator.comparingDouble;

/**
 * A VM selection policy that selects for migration the {@link Vm} with the Minimum Utilization (MU) of CPU.
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
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
