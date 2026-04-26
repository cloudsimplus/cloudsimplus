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
package org.cloudsimplus.services;

import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ServiceCall}/{@link ServiceRequest}/{@link ServiceSimple}
 * data model (no simulation involved).
 */
class ServiceCallTest {

    @Test
    void serviceSimpleRoundRobinPicksVmsCyclically() {
        final var vm0 = new VmSimple(1000, 1);
        final var vm1 = new VmSimple(1000, 1);
        final var svc = new ServiceSimple("svc", java.util.List.of(vm0, vm1));

        final Vm a = svc.selectVm();
        final Vm b = svc.selectVm();
        final Vm c = svc.selectVm();
        final Vm d = svc.selectVm();

        assertSame(vm0, a);
        assertSame(vm1, b);
        assertSame(vm0, c);
        assertSame(vm1, d);
    }

    @Test
    void serviceSelectVmReturnsNullWhenNoVms() {
        final var svc = new ServiceSimple("empty");
        assertSame(Vm.NULL, svc.selectVm());
    }

    @Test
    void addVmIsIdempotent() {
        final var svc = new ServiceSimple("svc");
        final var vm = new VmSimple(1000, 1);
        svc.addVm(vm).addVm(vm);
        assertEquals(1, svc.getVms().size());
    }

    @Test
    void serviceCallRejectsNegativeLengths() {
        final var svc = new ServiceSimple("svc");
        assertThrows(IllegalArgumentException.class, () -> new ServiceCall(svc, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> new ServiceCall(svc, 0, -1));
    }

    @Test
    void serviceCallTreeReflectsAddedChildren() {
        final var svcA = new ServiceSimple("A");
        final var svcB = new ServiceSimple("B");
        final var svcD = new ServiceSimple("D");

        final var root = new ServiceCall(svcA, 100, 50);
        root.addChild(new ServiceCall(svcB, 200))
            .addChild(new ServiceCall(svcD, 300));

        assertEquals(2, root.getChildren().size());
        assertSame(svcB, root.getChildren().get(0).getService());
        assertSame(svcD, root.getChildren().get(1).getService());
        assertTrue(root.isPending());
        assertFalse(root.isCompleted());
        assertTrue(root.isRoot());
    }

    @Test
    void serviceRequestKeepsRootAndState() {
        final var svc = new ServiceSimple("svc");
        final var root = new ServiceCall(svc, 100);
        final var req = new ServiceRequest(7, root).addTag("login");

        assertEquals(7, req.getId());
        assertSame(root, req.getRootCall());
        assertFalse(req.isFinished());
        assertEquals(-1, req.getResponseTime());
        assertEquals(java.util.List.of("login"), req.getTags());
    }

    @Test
    void serviceCallReusableInDifferentNodesOfTheSameRequest() {
        // A -> B -> A -> D -> E pattern: same Service A appears twice
        // (once as root, once nested under D's sibling).
        final var svcA = new ServiceSimple("A");
        final var svcB = new ServiceSimple("B");
        final var svcD = new ServiceSimple("D");
        final var svcE = new ServiceSimple("E");

        final var aRoot = new ServiceCall(svcA, 1000, 0);
        final var bCall = new ServiceCall(svcB, 2000);
        final var dCall = new ServiceCall(svcD, 1500);
        final var eCall = new ServiceCall(svcE, 500);
        dCall.addChild(eCall);
        aRoot.addChild(bCall).addChild(dCall);

        // Same Service A may also be reused inside another node if needed.
        final var anotherA = new ServiceCall(svcA, 100);
        bCall.addChild(anotherA);

        assertSame(svcA, aRoot.getService());
        assertSame(svcA, anotherA.getService());
        assertEquals(1, bCall.getChildren().size());
        assertSame(eCall, dCall.getChildren().get(0));
    }
}
