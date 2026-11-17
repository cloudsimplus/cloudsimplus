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

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmTestUtil;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/// @author Prawal Pokharel
public class VmDependencyGraphTest {
    private static final double DELTA = 1e-6;

    @Test
    public void criticalityMatchesReference() {
        final var graph = shopGraph().useCriticalityIndex();
        graph.setDemandHistory("db", 0.05, 0.15, 0.25, 0.35, 0.45, 0.55, 0.65, 0.75, 0.85, 0.95)
             .setDemandHistory("gw", 0.5, 0.5, 0.5)
             .setDemandHistory("orders", 0.0, 1.0, 0.3, 0.30000000000000004, 0.7, 0.2)
             .setDeclaredCriticality("db", 1.0)
             .setDeclaredCriticality("pay", 0.8);

        assertEquals(0.13534999014570656, graph.getCriticality("gw"), DELTA);
        assertEquals(0.5002371121175597, graph.getCriticality("orders"), DELTA);
        assertEquals(0.26300434064408235, graph.getCriticality("search"), DELTA);
        assertEquals(1.0, graph.getCriticality("db"), DELTA);
        assertEquals(0.4071913786501322, graph.getCriticality("pay"), DELTA);
        assertEquals(0.15923877332977035, graph.getCriticality("cache"), DELTA);
    }

    @Test
    public void everyServiceWeighsOneByDefault() {
        final var graph = shopGraph().setDeclaredCriticality("db", 1.0);
        assertEquals(1.0, graph.getCriticality("db"), DELTA);
        assertEquals(1.0, graph.getCriticality("cache"), DELTA);
    }

    @Test
    public void criticalityCanBeSetDirectly() {
        final var graph = shopGraph().setCriticality("cache", 0.9);
        assertEquals(0.9, graph.getCriticality("cache"), DELTA);
        assertEquals(0, graph.getCriticality("unknown"));
    }

    @Test
    public void downServiceBreaksEveryTransitiveCaller() {
        final var graph = new VmDependencyGraph();
        final Vm a = VmTestUtil.createVm(0, 1000, 1);
        final Vm b = VmTestUtil.createVm(1, 1000, 1);
        final Vm c = VmTestUtil.createVm(2, 1000, 1);
        final Vm d = VmTestUtil.createVm(3, 1000, 1);
        graph.addVm("a", a).addVm("b", b).addVm("c", c).addVm("d", d)
             .addDependency("a", "b").addDependency("b", "c").addDependency("a", "d");

        assertEquals(Set.of("a", "b", "c"), graph.brokenServices(List.of(c)));
        assertEquals(Set.of("a", "d"), graph.brokenServices(List.of(d)));
        assertEquals(Set.of(), graph.brokenServices(List.of()));
    }

    @Test
    public void serviceWithAnAvailableReplicaIsNotDown() {
        final var graph = new VmDependencyGraph();
        final Vm db1 = VmTestUtil.createVm(0, 1000, 1);
        final Vm db2 = VmTestUtil.createVm(1, 1000, 1);
        final Vm app = VmTestUtil.createVm(2, 1000, 1);
        graph.addVm("db", db1).addVm("db", db2).addVm("app", app).addDependency("app", "db");

        assertEquals(Set.of(), graph.brokenServices(List.of(db1)));
        assertEquals(Set.of("app", "db"), graph.brokenServices(List.of(db1, db2)));
        assertEquals("db", graph.getService(db2));
        assertEquals(List.of(db1, db2), graph.getReplicas("db"));
    }

    @Test
    public void vmIsFoundAfterItsBrokerIsSet() {
        final var graph = new VmDependencyGraph();
        final Vm vm = VmTestUtil.createVm(0, 1000, 1);
        graph.addVm("a", vm);
        // The VM hashCode includes its broker, which is only set when the VM is submitted
        vm.setBroker(new DatacenterBrokerSimple(new CloudSimPlus()));
        assertTrue(graph.contains(vm));
        assertEquals("a", graph.getService(vm));
        assertEquals(Set.of("a"), graph.brokenServices(List.of(vm)));
    }

    @Test
    public void addingTheSameVmTwiceThrows() {
        final var graph = new VmDependencyGraph();
        final Vm vm = VmTestUtil.createVm(0, 1000, 1);
        graph.addVm("a", vm);
        assertThrows(IllegalArgumentException.class, () -> graph.addVm("b", vm));
    }

    @Test
    public void invalidValuesThrow() {
        final var graph = new VmDependencyGraph();
        assertThrows(IllegalArgumentException.class, () -> graph.addDependency("a", "a"));
        assertThrows(IllegalArgumentException.class, () -> graph.setDeclaredCriticality("a", 1.5));
        assertThrows(IllegalArgumentException.class, () -> graph.setCriticality("a", -1));
        assertThrows(IllegalArgumentException.class, () -> graph.setWeights(-0.1, 0.5, 0.5));
    }

    private static VmDependencyGraph shopGraph() {
        return new VmDependencyGraph()
            .addService("gw").addService("orders").addService("search")
            .addService("db").addService("pay").addService("cache")
            .addDependency("gw", "orders").addDependency("gw", "search")
            .addDependency("orders", "db").addDependency("search", "db")
            .addDependency("orders", "pay").addDependency("pay", "db")
            .addDependency("search", "cache");
    }
}
