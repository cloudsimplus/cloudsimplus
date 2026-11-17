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

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.faultinjection.HostFaultInjection;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.placementpolicies.VmDependencyGraph;
import org.cloudsimplus.placementpolicies.VmPlacementPolicy;
import org.cloudsimplus.placementpolicies.VmPlacementPolicyBestFit;
import org.cloudsimplus.placementpolicies.VmPlacementPolicyDependencyCriticality;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/// An Integration Test (IT) that places the two replicas of a database and a service
/// that calls it, fails the Host of the first replica with [HostFaultInjection],
/// and checks what [VmDependencyGraph] reports as broken.
/// With [VmPlacementPolicyDependencyCriticality] the database survives (the single-replica
/// caller may fail with it); with [VmPlacementPolicyBestFit], which stacks both replicas, it doesn't.
@SuppressWarnings("PMD.ClassNamingConventions") // The IT suffix is what makes maven-failsafe-plugin run the class.
final class VmPlacementPolicyDependencyCriticalityIT {
    private static final int HOSTS = 4;
    private static final int HOST_CORES = 8;
    private static final int MIPS = 1000;
    private static final double FAILURE_TIME = 30;

    @Test
    void dependencyCriticalityKeepsTheDatabaseUpAfterAHostFailure() {
        final var result = run(VmPlacementPolicyDependencyCriticality::new);
        assertNotEquals(result.db1().getHost(), result.db2().getHost());
        assertTrue(result.db1().isFailed());
        assertFalse(result.db2().isFailed());
        assertFalse(result.broken().contains("db"), "the database keeps one working replica");
    }

    @Test
    void bestFitLosesTheDatabaseAndItsCaller() {
        final var result = run(graph -> new VmPlacementPolicyBestFit());
        assertEquals(result.db1().getHost(), result.db2().getHost());
        assertTrue(result.db2().isFailed());
        assertEquals(Set.of("db", "api"), result.broken());
    }

    @Test
    void faultDomainKeepsTheDatabaseUpAfterARackFailure() {
        final var result = run(graph -> new VmPlacementPolicyDependencyCriticality(graph)
            .setFaultDomain(host -> host.getId() / 2), true);
        assertNotEquals(result.db1().getHost().getId() / 2, result.db2().getHost().getId() / 2);
        assertFalse(result.db2().isFailed());
        assertFalse(result.broken().contains("db"), "the database keeps a replica in the other rack");
    }

    private record Result(Vm db1, Vm db2, Set<String> broken) {}

    private static Result run(final Function<VmDependencyGraph, VmPlacementPolicy> policyFactory) {
        return run(policyFactory, false);
    }

    /// @param failRack whether to fail both Hosts of the first replica's rack (Hosts 2k and 2k+1)
    ///                 instead of only its Host
    private static Result run(final Function<VmDependencyGraph, VmPlacementPolicy> policyFactory, final boolean failRack) {
        final var simulation = new CloudSimPlus();
        final List<Host> hosts = new ArrayList<>();
        for (int i = 0; i < HOSTS; i++) {
            final Host host = HostSimple.of(16_000, 100_000, 100_000, new CoreCapacity(HOST_CORES, MIPS));
            host.setVmScheduler(new VmSchedulerTimeShared());
            hosts.add(host);
        }

        final var graph = new VmDependencyGraph();
        final Datacenter datacenter = DatacenterSimple.of(simulation, hosts, policyFactory.apply(graph));
        datacenter.setSchedulingInterval(10);
        final var broker = new DatacenterBrokerSimple(simulation);
        final Vm db1 = vm(0);
        final Vm db2 = vm(1);
        final Vm api = vm(2);
        graph.addVm("db", db1).addVm("db", db2).addVm("api", api).addDependency("api", "db");
        final List<Vm> vms = List.of(db1, db2, api);
        broker.submitVmList(vms);
        broker.submitCloudletList(vms.stream().map(VmPlacementPolicyDependencyCriticalityIT::cloudlet).toList());

        /* No random failures: the test fails the Host itself at a fixed time. */
        final var faults = new HostFaultInjection(datacenter) {
            @Override
            protected void startInternal() {/**/}
        };
        final boolean[] injected = {false};
        simulation.listeners().addOnClockTick(info -> {
            if (injected[0] || info.getTime() < FAILURE_TIME) {
                return;
            }

            injected[0] = true;
            final Host first = db1.getHost();
            if (failRack) {
                final long rack = first.getId() / 2;
                hosts.stream().filter(h -> h.getId() / 2 == rack).forEach(faults::generateHostFault);
            } else {
                faults.generateHostFault(first);
            }
        });
        simulation.terminateAt(FAILURE_TIME * 3);
        simulation.start();

        assertTrue(injected[0], "the failure must have been injected");
        final List<Vm> failed = vms.stream().filter(Vm::isFailed).toList();
        return new Result(db1, db2, graph.brokenServices(failed));
    }

    private static Vm vm(final int id) {
        final Vm vm = VmSimple.of(id, new CoreCapacity(2, MIPS));
        vm.setRam(1024).setBw(100).setSize(1000);
        return vm;
    }

    private static Cloudlet cloudlet(final Vm vm) {
        final Cloudlet cloudlet = CloudletSimple.of(-10_000, 2);
        cloudlet.setUtilizationModelCpu(new UtilizationModelDynamic(0.5))
                .setUtilizationModelRam(new UtilizationModelDynamic(0.1))
                .setUtilizationModelBw(new UtilizationModelDynamic(0.1));
        cloudlet.setVm(vm);
        return cloudlet;
    }
}
