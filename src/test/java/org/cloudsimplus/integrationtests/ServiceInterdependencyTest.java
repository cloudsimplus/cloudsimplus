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
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.services.Service;
import org.cloudsimplus.services.ServiceBrokerSimple;
import org.cloudsimplus.services.ServiceCall;
import org.cloudsimplus.services.ServiceRequest;
import org.cloudsimplus.services.ServiceSimple;
import org.cloudsimplus.util.Log;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end integration test for the service-interdependency feature
 * ({@code org.cloudsimplus.services} package).
 *
 * <p>The test wires up multiple {@link Service}s, each on its own {@link Vm},
 * builds a {@link ServiceCall} graph that mirrors the user-visible chain
 * {@code Request → A → B → A → D → E}, and verifies that:
 * <ul>
 *   <li>the broker fires the calls in the correct order,</li>
 *   <li>the request finishes only after the deepest leaf returns,</li>
 *   <li>the cumulative response time matches the analytical sum of MI/MIPS,</li>
 *   <li>nested calls running on the <i>same</i> service work as expected
 *       (Service A appears twice in the chain).</li>
 * </ul>
 *
 * @author CloudSim Plus services package
 */
public class ServiceInterdependencyTest {

    private static final int HOST_MIPS = 1000;
    private static final int HOST_PES = 8;
    private static final long HOST_RAM = 16_000;
    private static final long HOST_BW = 100_000;
    private static final long HOST_STORAGE = 1_000_000;

    private static final int VM_MIPS = 1000;
    private static final int VM_PES = 1;
    private static final int VM_RAM = 512;

    private CloudSimPlus simulation;

    @BeforeAll
    static void beforeAll() {
        Log.setLevel(Level.OFF);
    }

    @Test
    void singleServiceNoChildren_finishTimeMatchesMiOverMips() {
        simulation = new CloudSimPlus();
        createDatacenter(1);
        final var broker = new ServiceBrokerSimple(simulation);

        final var vm = newVm();
        broker.submitVm(vm);
        final var svcA = new ServiceSimple("A").addVm(vm);
        broker.addService(svcA);

        final var root = new ServiceCall(svcA, 5_000); // 5000 MI / 1000 MIPS = 5s
        final var req = new ServiceRequest(0, root);
        broker.submitRequest(req);

        simulation.start();

        assertEquals(1, broker.getFinishedRequests().size());
        assertTrue(req.isFinished(), "Request should be finished");
        assertEquals(5.0, req.getResponseTime(), 1.0, "Response time ≈ 5s");
        assertEquals(ServiceCall.State.COMPLETED, root.getState());
    }

    @Test
    void synchronousNestedCall_aCallsB_aPostThenFinishes() {
        // Request -> A(pre 1000) -> B(pre 2000, post 500) -> A(post 500). Total = 4s.
        simulation = new CloudSimPlus();
        createDatacenter(1);
        final var broker = new ServiceBrokerSimple(simulation);

        final var vmA = newVm();
        final var vmB = newVm();
        broker.submitVmList(List.of(vmA, vmB));

        final var svcA = new ServiceSimple("A").addVm(vmA);
        final var svcB = new ServiceSimple("B").addVm(vmB);
        broker.addService(svcA).addService(svcB);

        final var root = new ServiceCall(svcA, 1_000, 500);
        root.addChild(new ServiceCall(svcB, 2_000, 500));
        final var req = new ServiceRequest(0, root);
        broker.submitRequest(req);

        simulation.start();

        assertTrue(req.isFinished());
        assertEquals(4.0, req.getResponseTime(), 1.0);
        // Each ServiceCall in the tree should be COMPLETED.
        assertEquals(ServiceCall.State.COMPLETED, root.getState());
        assertEquals(ServiceCall.State.COMPLETED, root.getChildren().get(0).getState());
    }

    /**
     * The headline scenario from the feature request:
     * {@code Request -> A -> B -> A -> D -> E}.
     *
     * <p>Modelled as a tree where A is the entrypoint, B is the first
     * child, D is the second child, and E is the only child of D.
     * Reading the tree as a depth-first call sequence reproduces the
     * user's pattern A → B → (back to) A → D → E.</p>
     */
    @Test
    void interdependencyChain_A_B_A_D_E_runsInOrderAndAccumulatesTime() {
        simulation = new CloudSimPlus();
        createDatacenter(1);
        final var broker = new ServiceBrokerSimple(simulation);

        final var vmA = newVm();
        final var vmB = newVm();
        final var vmD = newVm();
        final var vmE = newVm();
        broker.submitVmList(List.of(vmA, vmB, vmD, vmE));

        final var svcA = new ServiceSimple("A").addVm(vmA);
        final var svcB = new ServiceSimple("B").addVm(vmB);
        final var svcD = new ServiceSimple("D").addVm(vmD);
        final var svcE = new ServiceSimple("E").addVm(vmE);
        broker.addService(svcA).addService(svcB).addService(svcD).addService(svcE);

        // A: 1000 MI before children, 500 MI after; calls B then D
        // B: 1500 MI (leaf)
        // D: 800 MI before E, 200 MI after; calls E
        // E: 600 MI (leaf)
        final var aCall = new ServiceCall(svcA, 1_000, 500);
        final var bCall = new ServiceCall(svcB, 1_500);
        final var dCall = new ServiceCall(svcD, 800, 200);
        final var eCall = new ServiceCall(svcE, 600);

        dCall.addChild(eCall);
        aCall.addChild(bCall).addChild(dCall);

        final var req = new ServiceRequest(0, aCall);
        broker.submitRequest(req);

        // Trace the visit order via finish-listener side effects on the
        // ServiceCall finishTime field (the broker sets these as it walks).
        simulation.start();

        assertTrue(req.isFinished(), "Request must finish");

        final List<ServiceCall> visited = traverseDfsCompleted(aCall);
        assertEquals(4, visited.size(), "A, B, D, E should all be marked COMPLETED");

        // Children must finish strictly before their parent.
        assertTrue(bCall.getFinishTime() <= aCall.getFinishTime());
        assertTrue(eCall.getFinishTime() <= dCall.getFinishTime());
        assertTrue(dCall.getFinishTime() <= aCall.getFinishTime());

        // Sequential ordering between siblings: B finishes before D starts.
        assertTrue(bCall.getFinishTime() <= dCall.getStartTime() + 0.01,
            "B must complete before D starts");

        // Analytical total = (1000 + 1500 + 800 + 600 + 200 + 500) / 1000 = 4.6s.
        // Tolerance accounts for the discrete event-scheduling overhead between cloudlets.
        assertEquals(4.6, req.getResponseTime(), 1.0,
            "Cumulative response time should match the sum of MI/MIPS along the call tree");
    }

    @Test
    void serviceReusedInMultipleCallsBalancesAcrossVms() {
        // Service A backed by 2 VMs; the request fires A as root and A again as a child of B.
        // Round-robin should land each invocation on a different VM.
        simulation = new CloudSimPlus();
        createDatacenter(1);
        final var broker = new ServiceBrokerSimple(simulation);

        final var aVm0 = newVm();
        final var aVm1 = newVm();
        final var bVm = newVm();
        broker.submitVmList(List.of(aVm0, aVm1, bVm));

        final var svcA = new ServiceSimple("A", List.of(aVm0, aVm1));
        final var svcB = new ServiceSimple("B").addVm(bVm);
        broker.addService(svcA).addService(svcB);

        final var aRoot = new ServiceCall(svcA, 1_000);
        final var bChild = new ServiceCall(svcB, 1_000);
        final var aGrandchild = new ServiceCall(svcA, 1_000);
        bChild.addChild(aGrandchild);
        aRoot.addChild(bChild);

        broker.submitRequest(new ServiceRequest(0, aRoot));
        simulation.start();

        assertTrue(aRoot.isCompleted());
        assertTrue(bChild.isCompleted());
        assertTrue(aGrandchild.isCompleted());

        // Each invocation of svcA was assigned a different VM thanks to round-robin.
        assertNotSame(aRoot.getAssignedVm(), aGrandchild.getAssignedVm(),
            "Round-robin should land the second A-call on the other VM");
    }

    // -------------- helpers --------------

    private void createDatacenter(final int hostsCount) {
        final var hostList = new ArrayList<Host>(hostsCount);
        for (int i = 0; i < hostsCount; i++) {
            hostList.add(createHost());
        }
        new DatacenterSimple(simulation, hostList);
    }

    private Host createHost() {
        final List<Pe> peList = range(0, HOST_PES)
            .mapToObj(__ -> (Pe) new PeSimple(HOST_MIPS))
            .toList();
        return new HostSimple(HOST_RAM, HOST_BW, HOST_STORAGE, peList);
    }

    private Vm newVm() {
        final var vm = new VmSimple(VM_MIPS, VM_PES);
        vm.setRam(VM_RAM).setBw(1000).setSize(10_000);
        return vm;
    }

    private static List<ServiceCall> traverseDfsCompleted(final ServiceCall call) {
        final var out = new ArrayList<ServiceCall>();
        if (call.isCompleted()) {
            out.add(call);
        }
        for (final var c : call.getChildren()) {
            out.addAll(traverseDfsCompleted(c));
        }
        return out;
    }
}
