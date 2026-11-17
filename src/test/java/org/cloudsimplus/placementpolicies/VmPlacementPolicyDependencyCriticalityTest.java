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
package org.cloudsimplus.placementpolicies;

import org.cloudsimplus.core.Simulation;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimpleTest;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmTestUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// @author Prawal Pokharel
public class VmPlacementPolicyDependencyCriticalityTest {
    private static final int HOST_MIPS = 1000;
    private static final int HOST_RAM = 100_000;
    private static final int HOST_BW = 1_000_000;
    private static final int HOST_STORAGE = 1_000_000;

    @Test
    public void replicasOfACriticalServiceGoToDifferentHosts() {
        final var graph = new VmDependencyGraph();
        final Vm db1 = vm(0, 2);
        final Vm db2 = vm(1, 2);
        final Vm app = vm(2, 2);
        graph.addVm("db", db1).addVm("db", db2).addVm("app", app).addDependency("app", "db");
        final var policy = policy(new VmPlacementPolicyDependencyCriticality(graph), 8, 8);

        assertTrue(policy.placeVm(db1).fully());
        assertTrue(policy.placeVm(db2).fully());
        assertNotEquals(db1.getHost(), db2.getHost());
    }

    @Test
    public void bestFitStacksTheSameReplicas() {
        final Vm db1 = vm(0, 2);
        final Vm db2 = vm(1, 2);
        final var policy = policy(new VmPlacementPolicyBestFit(), 8, 8);

        assertTrue(policy.placeVm(db1).fully());
        assertTrue(policy.placeVm(db2).fully());
        assertEquals(db1.getHost(), db2.getHost(), "the motivating case: BestFit ignores replicas");
    }

    @Test
    public void lambdaOneOnlyConsidersEnergyAndPacksReplicas() {
        final var graph = new VmDependencyGraph();
        final Vm db1 = vm(0, 2);
        final Vm db2 = vm(1, 2);
        graph.addVm("db", db1).addVm("db", db2).addDependency("app", "db");
        final var policy = policy(new VmPlacementPolicyDependencyCriticality(graph, 1.0), 8, 8);

        assertTrue(policy.placeVm(db1).fully());
        assertTrue(policy.placeVm(db2).fully());
        assertEquals(db1.getHost(), db2.getHost());
    }

    @Test
    public void vmOutsideTheGraphFillsAHostInUse() {
        final var graph = new VmDependencyGraph();
        final var policy = policy(new VmPlacementPolicyDependencyCriticality(graph), 8, 8, 8);
        final Host used = policy.getHostList().get(2);
        final Vm first = vm(0, 2);
        assertTrue(policy.placeVm(first, used).fully());

        final Vm second = vm(1, 2);
        assertTrue(policy.placeVm(second).fully());
        assertEquals(used, second.getHost());
    }

    @Test
    public void singleReplicaJoinsTheHostWhereItsCallerAlreadyIs() {
        final var graph = new VmDependencyGraph();
        final Vm caller = vm(0, 2);
        final Vm callee = vm(1, 2);
        final Vm other = vm(2, 2);
        graph.addVm("caller", caller).addVm("callee", callee).addVm("other", other)
             .addDependency("caller", "callee").addDependency("x", "other");
        final var policy = policy(new VmPlacementPolicyDependencyCriticality(graph), 8, 8);
        final Host callerHost = policy.getHostList().get(0);
        final Host otherHost = policy.getHostList().get(1);
        assertTrue(policy.placeVm(caller, callerHost).fully());
        assertTrue(policy.placeVm(other, otherHost).fully());

        /* If callerHost fails, the caller is already broken,
         * so placing the callee there adds less risk than on otherHost. */
        assertTrue(policy.placeVm(callee).fully());
        assertEquals(callerHost, callee.getHost());
    }

    @Test
    public void faultDomainKeepsReplicasInDifferentRacks() {
        final var graph = new VmDependencyGraph();
        final Vm db1 = vm(0, 2);
        final Vm db2 = vm(1, 2);
        graph.addVm("db", db1).addVm("db", db2).addDependency("app", "db");
        final var policy = policy(new VmPlacementPolicyDependencyCriticality(graph), 8, 8, 8, 8);
        policy.setFaultDomain(host -> host.getId() / 2);

        assertTrue(policy.placeVm(db1).fully());
        assertTrue(policy.placeVm(db2).fully());
        assertNotEquals(db1.getHost().getId() / 2, db2.getHost().getId() / 2);
    }

    @Test
    public void withoutFaultDomainReplicasMayShareARack() {
        final var graph = new VmDependencyGraph();
        final Vm db1 = vm(0, 2);
        final Vm db2 = vm(1, 2);
        graph.addVm("db", db1).addVm("db", db2).addDependency("app", "db");
        final var policy = policy(new VmPlacementPolicyDependencyCriticality(graph), 8, 8, 8, 8);

        assertTrue(policy.placeVm(db1).fully());
        assertTrue(policy.placeVm(db2).fully());
        assertNotEquals(db1.getHost(), db2.getHost());
        assertEquals(db1.getHost().getId() / 2, db2.getHost().getId() / 2, "BestFit tie-break picks the next Host");
    }

    @Test
    public void replicasInOneRackStillGoToDifferentHosts() {
        final var graph = new VmDependencyGraph();
        final Vm db1 = vm(0, 2);
        final Vm db2 = vm(1, 2);
        graph.addVm("db", db1).addVm("db", db2).addDependency("app", "db");
        final var policy = policy(new VmPlacementPolicyDependencyCriticality(graph), 8, 8);
        policy.setFaultDomain(host -> "rack");

        assertTrue(policy.placeVm(db1).fully());
        assertTrue(policy.placeVm(db2).fully());
        assertNotEquals(db1.getHost(), db2.getHost());
    }

    @Test
    public void noSuitableHostReturnsEmpty() {
        final var graph = new VmDependencyGraph();
        final var policy = policy(new VmPlacementPolicyDependencyCriticality(graph), 2);
        assertFalse(policy.placeVm(vm(0, 4)).fully());
    }

    @Test
    public void lambdaOutsideRangeThrows() {
        final var graph = new VmDependencyGraph();
        assertThrows(IllegalArgumentException.class, () -> new VmPlacementPolicyDependencyCriticality(graph, -0.1));
        assertThrows(IllegalArgumentException.class, () -> new VmPlacementPolicyDependencyCriticality(graph, 1.1));
    }

    @Test
    public void addedPowerIncludesStaticPowerOnlyForAnEmptyHost() {
        final var policy = policy(new VmPlacementPolicyDependencyCriticality(new VmDependencyGraph()), 8);
        final Host host = policy.getHostList().getFirst();
        // Hosts without a power model are assumed to use half of their power when idle (a warning is logged)
        assertEquals(0.5 + 0.5 * 2 / 8.0, policy.addedPowerFraction(host, vm(0, 2)), 1e-9);
        assertTrue(policy.placeVm(vm(1, 2), host).fully());
        assertEquals(0.5 * 2 / 8.0, policy.addedPowerFraction(host, vm(2, 2)), 1e-9);
    }

    private static Vm vm(final int id, final int cores) {
        return VmTestUtil.createVm(id, HOST_MIPS, cores);
    }

    private static <T extends VmPlacementPolicyAbstract> T policy(final T policy, final int... coresByHost) {
        final List<Host> hosts = new ArrayList<>(coresByHost.length);
        for (int i = 0; i < coresByHost.length; i++) {
            hosts.add(HostSimpleTest.createHostSimple(i, coresByHost[i], HOST_MIPS, HOST_RAM, HOST_BW, HOST_STORAGE));
        }

        policy.setDatacenter(DatacenterSimple.of(Simulation.NULL, hosts));
        return policy;
    }
}
