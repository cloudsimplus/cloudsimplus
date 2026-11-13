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

import org.cloudsimplus.distributions.UniformDistr;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.placementpolicies.migration.*;
import org.cloudsimplus.selectionpolicies.VmSelectionPolicy;
import org.cloudsimplus.selectionpolicies.VmSelectionPolicyMinimumMigrationTime;
import org.cloudsimplus.selectionpolicies.VmSelectionPolicyMinimumUtilization;
import org.cloudsimplus.selectionpolicies.VmSelectionPolicyRandomSelection;
import org.cloudsimplus.vms.Vm;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparingLong;
import static org.cloudsimplus.integrationtests.VmMigrationScenario.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

/// Characterization tests for the VM migration decisions of the
/// [VmPlacementPolicyMigrationAbstract] subclasses and the [VmSelectionPolicy] implementations.
///
/// Each test runs a small simulation and checks the migration map returned by
/// [VmPlacementPolicyMigrationAbstract#getOptimizedPlacementMap(List)]
/// at the first check that plans any migration: which VMs must migrate and to which Hosts.
/// The expected values were taken from the current implementation,
/// so that changes to how the migration map is computed (such as #94) can show they keep the same decisions.
///
/// Only the first map is checked: it comes straight from the initial placement,
/// while later maps also depend on how the migrations requested before went.
///
/// A Host is overloaded according to the MIPS its VMs use,
/// but underloaded according to the MIPS its VMs request (their full capacity).
/// See [VmMigrationScenario] for the simulation each test runs.
public final class VmMigrationPlacementIT {
    private static final double DYNAMIC_THRESHOLD_SAFETY = 1.2;
    private static final long RANDOM_SEED = 12345;

    /// Time of the first migration check: the first scheduling interval plus the minimum time between events.
    private static final double FIRST_CHECK_TIME = 5.1;

    /// The result when no migration is ever planned.
    private static final PlannedMap NO_MIGRATION = new PlannedMap(0);

    /// A migration map returned by the policy: the time of the check
    /// and each planned migration, sorted by VM id.
    private record PlannedMap(double time, List<String> migrations) {
        PlannedMap(final double time, final String... migrations) {
            this(time, List.of(migrations));
        }
    }

    /// Host 0 uses 95% of its CPU (overloaded). Hosts 1 and 2 use about 50%.
    /// The VMs in Host 0 have different CPU utilizations, so each selection policy picks a different one.
    private static final List<HostSpec> OVERLOAD_HOSTS = List.of(new HostSpec(), new HostSpec(), new HostSpec());
    private static final List<VmSpec> OVERLOAD_VMS = List.of(
        new VmSpec(2, 1.0, 0), new VmSpec(2, 1.0, 0), new VmSpec(2, 0.9, 0),
        new VmSpec(1, 1.0, 0), new VmSpec(1, 0.8, 0),
        new VmSpec(2, 1.0, 1), new VmSpec(2, 1.0, 1),
        new VmSpec(2, 1.0, 2), new VmSpec(2, 0.9, 2));

    /// Host 0 is overloaded and one of its VMs (VM 0) must leave.
    /// Each placement policy picks a different target:
    /// - Host 1: the first suitable one (first fit);
    /// - Host 2: the one using the most MIPS (best fit);
    /// - Host 3: the one with the lowest power increase (default policy).
    ///   That increase is the power for the MIPS allocated after the placement minus the current power,
    ///   which comes from the MIPS used. Host 3 uses all the MIPS it allocates and has the most cores;
    /// - Host 4: the one using the fewest MIPS (worst fit).
    private static final List<HostSpec> TARGET_CHOICE_HOSTS = List.of(
        new HostSpec(4), new HostSpec(), new HostSpec(), new HostSpec(2 * HOST_CORES), new HostSpec());
    private static final List<VmSpec> TARGET_CHOICE_VMS = List.of(
        new VmSpec(1, 1.0, 0), new VmSpec(1, 1.0, 0), new VmSpec(1, 1.0, 0), new VmSpec(1, 1.0, 0),
        new VmSpec(2, 1.0, 1), new VmSpec(2, 0.5, 1),
        new VmSpec(2, 1.0, 2), new VmSpec(2, 1.0, 2), new VmSpec(1, 1.0, 2),
        new VmSpec(4, 1.0, 3),
        new VmSpec(2, 0.5, 4));

    /// Host 0's VMs must leave until it isn't overloaded: VM 4 (the lowest utilization), then VM 2.
    @Test
    void overloadedHostSelectsVmsUntilNotOverloaded() {
        final var policy = staticThreshold(new VmSelectionPolicyMinimumUtilization());
        assertEquals(new PlannedMap(
            FIRST_CHECK_TIME,
            "VM 2: Host 0 -> Host 1",
            "VM 4: Host 0 -> Host 1"), firstPlannedMap(OVERLOAD_HOSTS, OVERLOAD_VMS, policy));
    }

    /// Host 0 is overloaded and 2 of its VMs must leave to get it below the threshold.
    /// Host 1 only has RAM for one more VM, so just one of them can move there.
    @Test
    void targetHostReceivesOnlyTheVmsItFits() {
        final var hosts = List.of(new HostSpec(), new HostSpec(HOST_CORES, 2 * VM_RAM));
        final var vms = new ArrayList<VmSpec>();
        for (int i = 0; i < HOST_CORES; i++) {
            vms.add(new VmSpec(1, 1.0, 0));
        }

        vms.add(new VmSpec(4, 1.0, 1));
        final var policy = staticThreshold(new VmSelectionPolicyMinimumUtilization());
        assertEquals(new PlannedMap(FIRST_CHECK_TIME, "VM 0: Host 0 -> Host 1"), firstPlannedMap(hosts, vms, policy));
    }

    /// Hosts 0 and 1 are overloaded and one VM must leave each of them.
    /// Host 2 only has RAM for one more VM, so just VM 0 (the most used) can move there.
    /// VM 2 can't go to Host 0: the resources of VM 0 aren't free there until its migration finishes.
    /// The VMs from each Host have different utilizations, so the order they are placed is fixed.
    @Test
    void overloadedHostsCompeteForOneTarget() {
        final var hosts = List.of(new HostSpec(4), new HostSpec(4), new HostSpec(HOST_CORES, 2 * VM_RAM));
        final var vms = List.of(
            new VmSpec(2, 1.0, 0), new VmSpec(2, 1.0, 0),
            new VmSpec(2, 0.95, 1), new VmSpec(2, 0.95, 1),
            new VmSpec(2, 1.0, 2));
        final var policy = staticThreshold(new VmSelectionPolicyMinimumUtilization());
        assertEquals(new PlannedMap(FIRST_CHECK_TIME, "VM 0: Host 0 -> Host 2"), firstPlannedMap(hosts, vms, policy));
    }

    /// Host 0 is underloaded and its VMs fit into Host 1.
    @Test
    void underloadedHostVmsAreConsolidated() {
        final var policy = staticThreshold(new VmSelectionPolicyMinimumUtilization());
        assertEquals(new PlannedMap(
            FIRST_CHECK_TIME,
            "VM 0: Host 0 -> Host 1",
            "VM 1: Host 0 -> Host 1"), firstPlannedMap(consolidationHosts(HOST_RAM), consolidationVms(), policy));
    }

    /// Host 0 is underloaded, but Host 1 only has RAM for one of its 2 VMs,
    /// so no VM is planned to leave Host 0.
    @Test
    void underloadedHostMigrationIsCancelledWhenAVmHasNoTarget() {
        final var policy = staticThreshold(new VmSelectionPolicyMinimumUtilization());
        assertEquals(NO_MIGRATION, firstPlannedMap(consolidationHosts(2 * VM_RAM), consolidationVms(), policy));
    }

    /// Host 0 is overloaded and Host 3 is underloaded in the same check.
    /// VM 9, from Host 3, goes to Host 2, since Host 1 already receives the VMs from Host 0.
    @Test
    void overloadedAndUnderloadedHostsInTheSameCheck() {
        final var hosts = new ArrayList<>(OVERLOAD_HOSTS);
        hosts.add(new HostSpec());
        final var vms = new ArrayList<>(OVERLOAD_VMS);
        vms.add(new VmSpec(1, 0.5, 3));
        final var policy = staticThreshold(new VmSelectionPolicyMinimumUtilization());
        assertEquals(new PlannedMap(
            FIRST_CHECK_TIME,
            "VM 2: Host 0 -> Host 1",
            "VM 4: Host 0 -> Host 1",
            "VM 9: Host 3 -> Host 2"),
            firstPlannedMap(hosts, vms, policy));
    }

    @Test
    void defaultPlacementSelectsTheHostWithTheLowestPowerIncrease() {
        final var policy = staticThreshold(new VmSelectionPolicyMinimumUtilization());
        assertEquals(new PlannedMap(FIRST_CHECK_TIME, "VM 0: Host 0 -> Host 3"), firstPlannedMap(TARGET_CHOICE_HOSTS, TARGET_CHOICE_VMS, policy));
    }

    @Test
    void firstFitPlacement() {
        final var policy = new VmPlacementPolicyMigrationFirstFitStaticThreshold(
            new VmSelectionPolicyMinimumUtilization(), OVER_UTILIZATION_THRESHOLD);
        assertEquals(new PlannedMap(FIRST_CHECK_TIME, "VM 0: Host 0 -> Host 1"), firstPlannedMap(TARGET_CHOICE_HOSTS, TARGET_CHOICE_VMS, policy));
    }

    @Test
    void bestFitPlacement() {
        final var policy = new VmPlacementPolicyMigrationBestFitStaticThreshold(
            new VmSelectionPolicyMinimumUtilization(), OVER_UTILIZATION_THRESHOLD);
        assertEquals(new PlannedMap(FIRST_CHECK_TIME, "VM 0: Host 0 -> Host 2"), firstPlannedMap(TARGET_CHOICE_HOSTS, TARGET_CHOICE_VMS, policy));
    }

    @Test
    void worstFitPlacement() {
        final var policy = new VmPlacementPolicyMigrationWorstFitStaticThreshold(
            new VmSelectionPolicyMinimumUtilization(), OVER_UTILIZATION_THRESHOLD);
        assertEquals(new PlannedMap(FIRST_CHECK_TIME, "VM 0: Host 0 -> Host 4"), firstPlannedMap(TARGET_CHOICE_HOSTS, TARGET_CHOICE_VMS, policy));
    }

    /// [VmPlacementPolicyMigrationDynamicUpperThresholdFirstFit#computeHostUtilizationMeasure(Host)] is always 0,
    /// so the upper threshold is always 1 and no Host is ever overloaded.
    /// That is why it's checked with an underloaded Host.
    @Test
    void dynamicUpperThresholdPlacement() {
        final var fallback = staticThreshold(new VmSelectionPolicyMinimumUtilization());
        final var policy = new VmPlacementPolicyMigrationDynamicUpperThresholdFirstFit(
            new VmSelectionPolicyMinimumUtilization(), DYNAMIC_THRESHOLD_SAFETY, fallback);
        assertEquals(new PlannedMap(
            FIRST_CHECK_TIME,
            "VM 0: Host 0 -> Host 1",
            "VM 1: Host 0 -> Host 1"), firstPlannedMap(consolidationHosts(HOST_RAM), consolidationVms(), policy));
    }

    @Test
    void minimumMigrationTimeSelection() {
        final var policy = staticThreshold(new VmSelectionPolicyMinimumMigrationTime());
        assertEquals(new PlannedMap(FIRST_CHECK_TIME, "VM 0: Host 0 -> Host 1"), firstPlannedMap(OVERLOAD_HOSTS, OVERLOAD_VMS, policy));
    }

    @Test
    void randomSelection() {
        final var policy = staticThreshold(new VmSelectionPolicyRandomSelection(new UniformDistr(RANDOM_SEED)));
        assertEquals(new PlannedMap(FIRST_CHECK_TIME, "VM 1: Host 0 -> Host 1"), firstPlannedMap(OVERLOAD_HOSTS, OVERLOAD_VMS, policy));
    }

    /// Host 0 has 2 cores for each one its VMs request, so it's underloaded.
    /// Host 1 is half requested and has the given RAM.
    private static List<HostSpec> consolidationHosts(final long host1Ram) {
        return List.of(new HostSpec(2 * HOST_CORES), new HostSpec(HOST_CORES, host1Ram));
    }

    private static List<VmSpec> consolidationVms() {
        return List.of(new VmSpec(1, 0.3, 0), new VmSpec(1, 0.2, 0), new VmSpec(4, 1.0, 1));
    }

    /// Runs a simulation, placing the VMs into the given Hosts at the start,
    /// then letting the given policy migrate them.
    /// @return the first non-empty migration map returned by the policy,
    ///         or [#NO_MIGRATION] if the policy never plans a migration
    private static PlannedMap firstPlannedMap(
        final List<HostSpec> hostSpecs, final List<VmSpec> vmSpecs, final VmPlacementPolicyMigrationAbstract policy)
    {
        final var plannedMaps = new ArrayList<PlannedMap>();
        VmMigrationScenario.of(hostSpecs, vmSpecs, recordPlannedMaps(policy, plannedMaps)).simulation().start();
        return plannedMaps.isEmpty() ? NO_MIGRATION : plannedMaps.getFirst();
    }

    /// {@return a spy of the given policy that adds each non-empty migration map it returns to a List}
    @SuppressWarnings("unchecked")
    private static VmPlacementPolicyMigrationAbstract recordPlannedMaps(
        final VmPlacementPolicyMigrationAbstract policy, final List<PlannedMap> plannedMaps)
    {
        final var spyPolicy = spy(policy);
        doAnswer(invocation -> {
            final var map = (Map<Vm, Host>) invocation.callRealMethod();
            if (!map.isEmpty()) {
                plannedMaps.add(new PlannedMap(spyPolicy.getDatacenter().getSimulation().clock(), migrationsToStrings(map)));
            }

            return map;
        }).when(spyPolicy).getOptimizedPlacementMap(any());
        return spyPolicy;
    }

    private static List<String> migrationsToStrings(final Map<Vm, Host> map) {
        return map.entrySet().stream()
            .sorted(Map.Entry.comparingByKey(comparingLong(Vm::getId)))
            .map(entry -> "VM %d: Host %d -> Host %d".formatted(entry.getKey().getId(), entry.getKey().getHost().getId(), entry.getValue().getId()))
            .toList();
    }
}
