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

import ch.qos.logback.classic.Level;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.util.Log;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Smoke test running a full simulation through the
 * {@link ServiceBrokerSimple} to verify the chain
 * {@code Request → A → B → A → D → E} executes end-to-end.
 *
 * <p>Lives in the unit-test source set (not under {@code integrationtests/})
 * so it runs as part of the default {@code mvn test} suite.</p>
 */
class ServiceBrokerSimpleTest {

    @BeforeAll
    static void quietLogs() {
        Log.setLevel(Level.OFF);
    }

    @Test
    void chainA_B_A_D_E_finishesWithCorrectOrderingAndTimings() {
        final var simulation = new CloudSimPlus();

        // One large host that can fit four 1-PE VMs.
        final List<Pe> peList = List.of(
            new PeSimple(1000), new PeSimple(1000),
            new PeSimple(1000), new PeSimple(1000),
            new PeSimple(1000), new PeSimple(1000));
        final Host host = new HostSimple(8_000, 100_000, 1_000_000, peList);
        new DatacenterSimple(simulation, List.of(host));

        final var broker = new ServiceBrokerSimple(simulation);

        // 4 VMs, one per service.
        final Vm vmA = new VmSimple(1000, 1).setRam(512).setBw(1000).setSize(10_000);
        final Vm vmB = new VmSimple(1000, 1).setRam(512).setBw(1000).setSize(10_000);
        final Vm vmD = new VmSimple(1000, 1).setRam(512).setBw(1000).setSize(10_000);
        final Vm vmE = new VmSimple(1000, 1).setRam(512).setBw(1000).setSize(10_000);
        broker.submitVmList(List.of(vmA, vmB, vmD, vmE));

        final var svcA = new ServiceSimple("A").addVm(vmA);
        final var svcB = new ServiceSimple("B").addVm(vmB);
        final var svcD = new ServiceSimple("D").addVm(vmD);
        final var svcE = new ServiceSimple("E").addVm(vmE);
        broker.addService(svcA).addService(svcB).addService(svcD).addService(svcE);

        // A: 1000 MI before children, 500 MI after, calls B then D.
        // B: 1500 MI (leaf).
        // D: 800 MI before children, 200 MI after, calls E.
        // E: 600 MI (leaf).
        final var aCall = new ServiceCall(svcA, 1_000, 500);
        final var bCall = new ServiceCall(svcB, 1_500);
        final var dCall = new ServiceCall(svcD, 800, 200);
        final var eCall = new ServiceCall(svcE, 600);
        dCall.addChild(eCall);
        aCall.addChild(bCall).addChild(dCall);

        final var req = new ServiceRequest(0, aCall);
        broker.submitRequest(req);

        simulation.start();

        assertTrue(req.isFinished(), "Request must finish");
        assertEquals(ServiceCall.State.COMPLETED, aCall.getState());
        assertEquals(ServiceCall.State.COMPLETED, bCall.getState());
        assertEquals(ServiceCall.State.COMPLETED, dCall.getState());
        assertEquals(ServiceCall.State.COMPLETED, eCall.getState());

        // Strict ordering: a child finishes before its parent does;
        // a sibling fires after the previous sibling completes.
        assertTrue(eCall.getFinishTime() <= dCall.getFinishTime() + 1e-9);
        assertTrue(dCall.getFinishTime() <= aCall.getFinishTime() + 1e-9);
        assertTrue(bCall.getFinishTime() <= dCall.getStartTime() + 0.5,
            "B must finish before D starts (sequential children)");

        // Sum of MI/MIPS along the call tree = 4.6s (allow event-scheduling slack).
        assertEquals(4.6, req.getResponseTime(), 1.0);
    }
}
