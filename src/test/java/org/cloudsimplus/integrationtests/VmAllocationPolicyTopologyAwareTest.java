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

import ch.qos.logback.classic.Level;
import org.cloudsimplus.allocationpolicies.VmAllocationPolicyTopologyAware;
import org.cloudsimplus.allocationpolicies.VmAllocationPolicyTopologyAware.Policy;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.TopologyAwareHost;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.util.Log;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end integration test for {@link VmAllocationPolicyTopologyAware}
 * and {@link TopologyAwareHost}.
 *
 * <p>Each test wires up a {@link DatacenterSimple} backed by topology-tagged
 * hosts, drives a single broker through a small VM submission, and asserts
 * the placement decisions match the active {@link Policy}:</p>
 *
 * <ul>
 *   <li><b>COST_OPTIMIZED</b> — VM lands on the cheapest viable host.</li>
 *   <li><b>RACK_ANTI_AFFINITY</b> — replica-set VMs land on distinct racks;
 *       a placement that would violate the constraint fails.</li>
 *   <li><b>AVAILABILITY_ZONE_SPREAD</b> — replica-set VMs spread across AZs.</li>
 *   <li><b>GEOGRAPHIC_SPREAD</b> — replica-set VMs spread across regions.</li>
 *   <li><b>LATENCY_AWARE</b> — VM lands on the host closest to its peers.</li>
 * </ul>
 *
 * <p>Replica-set membership is encoded via {@link Vm#setDescription(String)}
 * so the policy's {@code replicaSetOf} function can read it back without any
 * external bookkeeping.</p>
 */
public class VmAllocationPolicyTopologyAwareTest {

    private static final int HOST_MIPS = 1_000;
    private static final int HOST_PES = 4;
    private static final long HOST_RAM = 8_192;
    private static final long HOST_BW = 10_000;
    private static final long HOST_STORAGE = 100_000;

    private static final int VM_MIPS = 1_000;
    private static final int VM_PES = 1;
    private static final int VM_RAM = 512;

    private CloudSimPlus simulation;

    @BeforeAll
    static void beforeAll() {
        Log.setLevel(Level.OFF);
    }

    // ------------------------------------------------------------------
    // COST_OPTIMIZED
    // ------------------------------------------------------------------

    @Test
    void costOptimized_picksCheapestViableHost() {
        simulation = new CloudSimPlus();

        final var cheap   = newHost().setRackId("r1").setCostPerHour(0.30);
        final var medium  = newHost().setRackId("r2").setCostPerHour(0.70);
        final var pricey  = newHost().setRackId("r3").setCostPerHour(1.20);

        final var policy = new VmAllocationPolicyTopologyAware(Policy.COST_OPTIMIZED);
        new DatacenterSimple(simulation, List.of(cheap, medium, pricey), policy);

        final var broker = new DatacenterBrokerSimple(simulation);
        final var vm = newVm();
        broker.submitVm(vm);

        runOnce();

        assertSame(cheap, vm.getHost(), "VM should land on the cheapest host");
    }

    @Test
    void costOptimized_skipsCheapestWhenItLacksCapacity() {
        simulation = new CloudSimPlus();

        // Cheapest host has only 1 PE — too small for the 2-PE VM below.
        final var tinyButCheap = newHostWithPes(1).setCostPerHour(0.10);
        final var bigAndMedium = newHost().setCostPerHour(0.50);
        final var bigAndPricey = newHost().setCostPerHour(0.90);

        final var policy = new VmAllocationPolicyTopologyAware(Policy.COST_OPTIMIZED);
        new DatacenterSimple(simulation, List.of(tinyButCheap, bigAndMedium, bigAndPricey), policy);

        final var broker = new DatacenterBrokerSimple(simulation);
        final var vm = new VmSimple(VM_MIPS, 2);
        vm.setRam(VM_RAM).setBw(1_000).setSize(10_000);
        broker.submitVm(vm);

        runOnce();

        assertSame(bigAndMedium, vm.getHost(),
            "Capacity filter must run before cost scoring — tiny cheap host is skipped");
    }

    // ------------------------------------------------------------------
    // RACK_ANTI_AFFINITY
    // ------------------------------------------------------------------

    @Test
    void rackAntiAffinity_replicaSetVmsLandOnDistinctRacks() {
        simulation = new CloudSimPlus();

        final var hostA = newHost().setRackId("rack-1");
        final var hostB = newHost().setRackId("rack-2");
        final var hostC = newHost().setRackId("rack-3");

        final var policy = new VmAllocationPolicyTopologyAware(Policy.RACK_ANTI_AFFINITY)
            .setReplicaSetOf(byDescription());
        new DatacenterSimple(simulation, List.of(hostA, hostB, hostC), policy);

        final var broker = new DatacenterBrokerSimple(simulation);
        final List<Vm> replicas = newReplicaSet("payments", 3);
        broker.submitVmList(replicas);

        runOnce();

        final Set<String> racks = replicas.stream()
            .map(vm -> ((TopologyAwareHost) vm.getHost()).getRackId())
            .collect(Collectors.toSet());
        assertEquals(3, racks.size(), "Each replica must occupy a distinct rack");
    }

    @Test
    void rackAntiAffinity_failsToPlaceWhenNoFreshRackRemains() {
        simulation = new CloudSimPlus();

        // Only two racks for a four-replica request → 4th VM has no valid rack.
        final var rackA1 = newHost().setRackId("rack-A");
        final var rackA2 = newHost().setRackId("rack-A"); // duplicate rack on purpose
        final var rackB1 = newHost().setRackId("rack-B");

        final var policy = new VmAllocationPolicyTopologyAware(Policy.RACK_ANTI_AFFINITY)
            .setReplicaSetOf(byDescription());
        new DatacenterSimple(simulation, List.of(rackA1, rackA2, rackB1), policy);

        final var broker = new DatacenterBrokerSimple(simulation);
        final List<Vm> replicas = newReplicaSet("orders", 3);
        broker.submitVmList(replicas);

        runOnce();

        final long placed = replicas.stream().filter(VmAllocationPolicyTopologyAwareTest::wasPlaced).count();
        assertEquals(2, placed,
            "Only two replicas should be placed — one per available rack; the third has no rack left");

        final long unplaced = replicas.stream().filter(vm -> !wasPlaced(vm)).count();
        assertEquals(1, unplaced, "One replica must fail to place under strict rack anti-affinity");
    }

    @Test
    void rackAntiAffinity_unrelatedVmCanShareRack() {
        simulation = new CloudSimPlus();

        final var hostA = newHost().setRackId("rack-1");
        final var hostB = newHost().setRackId("rack-2");

        final var policy = new VmAllocationPolicyTopologyAware(Policy.RACK_ANTI_AFFINITY)
            .setReplicaSetOf(byDescription());
        new DatacenterSimple(simulation, List.of(hostA, hostB), policy);

        final var broker = new DatacenterBrokerSimple(simulation);
        final List<Vm> payments = newReplicaSet("payments", 2);
        final Vm orders = newVm();
        orders.setDescription("orders"); // different replica set
        broker.submitVmList(payments);
        broker.submitVm(orders);

        runOnce();

        // payments fully placed across both racks
        assertTrue(payments.stream().allMatch(VmAllocationPolicyTopologyAwareTest::wasPlaced));
        // orders must place — it's in a different replica set, so the rack filter doesn't apply
        assertTrue(wasPlaced(orders),
            "Anti-affinity is per replica set; an unrelated VM can share a rack");
    }

    // ------------------------------------------------------------------
    // AVAILABILITY_ZONE_SPREAD
    // ------------------------------------------------------------------

    @Test
    void availabilityZoneSpread_distributesEvenlyAcrossAzs() {
        simulation = new CloudSimPlus();

        final List<Host> hosts = new ArrayList<>();
        for (final String az : List.of("az-a", "az-b", "az-c")) {
            // two hosts per AZ, on different racks
            hosts.add(newHost().setAvailabilityZone(az).setRackId(az + "-r1"));
            hosts.add(newHost().setAvailabilityZone(az).setRackId(az + "-r2"));
        }

        final var policy = new VmAllocationPolicyTopologyAware(Policy.AVAILABILITY_ZONE_SPREAD)
            .setReplicaSetOf(byDescription());
        new DatacenterSimple(simulation, hosts, policy);

        final var broker = new DatacenterBrokerSimple(simulation);
        final List<Vm> replicas = newReplicaSet("cache", 3);
        broker.submitVmList(replicas);

        runOnce();

        final Set<String> azs = replicas.stream()
            .map(vm -> ((TopologyAwareHost) vm.getHost()).getAvailabilityZone())
            .collect(Collectors.toSet());
        assertEquals(3, azs.size(), "Each replica should land in a different AZ");
    }

    // ------------------------------------------------------------------
    // GEOGRAPHIC_SPREAD
    // ------------------------------------------------------------------

    @Test
    void geographicSpread_distributesAcrossRegions() {
        simulation = new CloudSimPlus();

        final List<Host> hosts = new ArrayList<>();
        for (final String region : List.of("us-east", "us-west", "eu-west")) {
            hosts.add(newHost().setRegion(region));
            hosts.add(newHost().setRegion(region));
        }

        final var policy = new VmAllocationPolicyTopologyAware(Policy.GEOGRAPHIC_SPREAD)
            .setReplicaSetOf(byDescription());
        new DatacenterSimple(simulation, hosts, policy);

        final var broker = new DatacenterBrokerSimple(simulation);
        final List<Vm> replicas = newReplicaSet("global-api", 3);
        broker.submitVmList(replicas);

        runOnce();

        final Set<String> regions = replicas.stream()
            .map(vm -> ((TopologyAwareHost) vm.getHost()).getRegion())
            .collect(Collectors.toSet());
        assertEquals(3, regions.size(), "Replicas should span all three regions");
    }

    // ------------------------------------------------------------------
    // LATENCY_AWARE
    // ------------------------------------------------------------------

    @Test
    void latencyAware_picksHostClosestToPlacedPeer() {
        simulation = new CloudSimPlus();

        // peerHost has only 1 PE — exactly enough to host the peer, then full.
        // That forces the frontend to choose between nearHost and farHost.
        final var peerHost = newHostWithPes(1).setRegion("us-east");
        final var nearHost = newHost().setRegion("us-east");
        final var farHost  = newHost().setRegion("eu-west");

        // Explicit latencies to peerHost: 2ms from nearHost, 90ms from farHost.
        nearHost.putLatency(peerHost.getId(), 2.0);
        farHost.putLatency(peerHost.getId(), 90.0);

        final Vm peer = newVm();
        peer.setDescription("backend");

        final var policy = new VmAllocationPolicyTopologyAware(Policy.LATENCY_AWARE)
            .setPeersOf(vm -> "frontend".equals(vm.getDescription())
                ? List.of(peer)
                : Collections.<Vm>emptyList());

        // peerHost first so the (peer-less) peer ties at score 0 across hosts and
        // Stream.min picks the first encountered — peerHost.
        new DatacenterSimple(simulation, List.of(peerHost, nearHost, farHost), policy);

        final var broker = new DatacenterBrokerSimple(simulation);
        broker.submitVm(peer);

        final Vm frontend = newVm();
        frontend.setDescription("frontend");
        broker.submitVm(frontend);

        runOnce();

        assertTrue(wasPlaced(peer), "Peer VM must be placed first");
        assertSame(peerHost, peer.getHost(), "Peer should fill the small peerHost");
        // peerHost is full → frontend chooses between nearHost (2ms) and farHost (90ms).
        assertSame(nearHost, frontend.getHost(),
            "LATENCY_AWARE must pick the host with the lowest summed latency to peers");
    }

    // ------------------------------------------------------------------
    // helpers
    // ------------------------------------------------------------------

    private void runOnce() {
        simulation.terminateAt(1.0);
        simulation.start();
    }

    private TopologyAwareHost newHost() {
        return newHostWithPes(HOST_PES);
    }

    private TopologyAwareHost newHostWithPes(final int pes) {
        final List<Pe> peList = range(0, pes)
            .mapToObj(__ -> (Pe) new PeSimple(HOST_MIPS))
            .toList();
        return new TopologyAwareHost(HOST_RAM, HOST_BW, HOST_STORAGE, peList);
    }

    private Vm newVm() {
        final var vm = new VmSimple(VM_MIPS, VM_PES);
        vm.setRam(VM_RAM).setBw(1_000).setSize(10_000);
        return vm;
    }

    private List<Vm> newReplicaSet(final String name, final int count) {
        final List<Vm> out = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            final Vm vm = newVm();
            vm.setDescription(name);
            out.add(vm);
        }
        return out;
    }

    /**
     * After {@code simulation.start()} returns, {@link Vm#isCreated()} flips
     * back to {@code false} when the broker tears down idle VMs at shutdown,
     * but {@link Vm#getHost()} still reflects the host the VM was placed on.
     * Use this for post-run placement assertions.
     */
    private static boolean wasPlaced(final Vm vm) {
        return vm.getHost() != null && vm.getHost() != Host.NULL;
    }

    private static Function<Vm, String> byDescription() {
        return vm -> vm.getDescription() == null ? "" : vm.getDescription();
    }
}
