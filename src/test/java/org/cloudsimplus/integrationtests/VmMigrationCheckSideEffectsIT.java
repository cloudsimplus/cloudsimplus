/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudsimplus.integrationtests;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.selectionpolicies.VmSelectionPolicyMinimumUtilization;
import org.cloudsimplus.vms.Vm;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.cloudsimplus.integrationtests.VmMigrationScenario.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Checks that a VM migration check, which computes the VMs to migrate and their target Hosts,
/// doesn't change the VMs and Hosts before the migrations are actually requested.
/// See [cloudsimplus#94](https://github.com/cloudsimplus/cloudsimplus/issues/94).
public final class VmMigrationCheckSideEffectsIT {
    /// Host 0 is overloaded by its 2 VMs, but Host 1 has a single core,
    /// so no VM can migrate and the check runs at every scheduling interval.
    private static final List<HostSpec> NO_TARGET_HOSTS = List.of(new HostSpec(4), new HostSpec(1));
    private static final List<VmSpec> NO_TARGET_VMS = List.of(new VmSpec(2, 1.0, 0), new VmSpec(2, 1.0, 0));

    @Test
    void vmsStayInTheBrokerRunningListWhenNoMigrationIsPossible() {
        final var scenario = noTargetScenario();
        final var runningVmsCounts = new ArrayList<Integer>();
        whileCloudletsRun(scenario, () -> runningVmsCounts.add(scenario.broker().getVmExecList().size()));
        scenario.simulation().start();

        assertFalse(runningVmsCounts.isEmpty());
        assertTrue(runningVmsCounts.stream().allMatch(count -> count == NO_TARGET_VMS.size()), () -> "Running VMs along the time: " + runningVmsCounts);
    }

    @Test
    void vmsStartTimeDoesNotChangeWhenNoMigrationIsPossible() {
        final var scenario = noTargetScenario();
        final var startTimes = new ArrayList<Map<Vm, Double>>();
        scenario.broker().listeners().addOnVmsCreated(info -> startTimes.add(startTimes(scenario)));
        scenario.simulation().start();

        assertEquals(startTimes.getFirst(), startTimes(scenario));
    }

    @Test
    void hostRemovalIsOnlyNotifiedWhenVmsAreDestroyed() {
        final var scenario = noTargetScenario();
        final var removedVms = new ArrayList<Vm>();
        scenario.vms().forEach(vm -> vm.listeners().addOnHostRemoval(info -> removedVms.add(info.getVm())));
        scenario.simulation().start();

        assertEquals(new HashSet<>(scenario.vms()), new HashSet<>(removedVms), "removed VMs");
        assertEquals(scenario.vms().size(), removedVms.size(), () -> "Host removal notifications: " + removedVms);
    }

    /// VMs migrating from an underloaded Host must only be in the VM list of their source Host
    /// until their migration finishes.
    @Test
    void noVmIsInTheListOfTwoHosts() {
        final var hosts = List.of(new HostSpec(2 * HOST_CORES), new HostSpec());
        final var vms = List.of(new VmSpec(1, 0.3, 0), new VmSpec(1, 0.2, 0), new VmSpec(4, 1.0, 1));
        final var scenario = VmMigrationScenario.of(hosts, vms, staticThreshold(new VmSelectionPolicyMinimumUtilization()));
        final var duplicatedVms = new ArrayList<Vm>();
        whileCloudletsRun(scenario, () -> duplicatedVms.addAll(vmsInMoreThanOneHost(scenario)));
        scenario.simulation().start();

        assertAll(
            () -> assertTrue(duplicatedVms.isEmpty(), () -> "VMs in the list of 2 Hosts: " + duplicatedVms),
            () -> assertTrue(scenario.vms().get(0).getHost().getId() == 1, "VM 0 didn't migrate to Host 1"));
    }

    private static VmMigrationScenario noTargetScenario() {
        return VmMigrationScenario.of(NO_TARGET_HOSTS, NO_TARGET_VMS, staticThreshold(new VmSelectionPolicyMinimumUtilization()));
    }

    /// Runs an action at each clock tick after all VMs are created, until some Cloudlet finishes.
    private static void whileCloudletsRun(final VmMigrationScenario scenario, final Runnable action) {
        final var broker = scenario.broker();
        scenario.simulation().listeners().addOnClockTick(info -> {
            if (broker.getVmCreatedList().size() == scenario.vms().size() && broker.getCloudletFinishedList().isEmpty()) {
                action.run();
            }
        });
    }

    private static Map<Vm, Double> startTimes(final VmMigrationScenario scenario) {
        return scenario.vms().stream().collect(toMap(vm -> vm, Vm::getStartTime));
    }

    private static List<Vm> vmsInMoreThanOneHost(final VmMigrationScenario scenario) {
        final var vmsSeen = new HashSet<Vm>();
        return scenario.datacenter().<Host>getHostList().stream()
            .flatMap(host -> host.<Vm>getVmList().stream())
            .filter(vm -> !vmsSeen.add(vm))
            .toList();
    }
}
