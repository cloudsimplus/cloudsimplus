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
package org.cloudsimplus.core;

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Checks that `hasStarted()` keeps true after the object finishes,
/// while `isRunning()` is only true between the start and the finish.
/// See [#616](https://github.com/cloudsimplus/cloudsimplus/issues/616).
class StartedAndRunningTest {
    private static final CoreCapacity CAPACITY = new CoreCapacity(1, 1000);
    private static final int CLOUDLET_LENGTH = 10_000;

    @Test
    void hasStartedStaysTrueAfterFinishWhileIsRunningDoesNot() {
        final var simulation = new CloudSimPlus();
        final var host = HostSimple.of(CAPACITY);
        DatacenterSimple.of(simulation, List.of(host));
        final var broker = new DatacenterBrokerSimple(simulation);
        final var vm = VmSimple.of(CAPACITY);
        final var cloudlet = CloudletSimple.of(CLOUDLET_LENGTH, 1);

        final var runningStates = new ArrayList<Boolean>();
        cloudlet.listeners().addOnStart(info -> runningStates.addAll(
            List.of(broker.hasStarted(), broker.isRunning(), host.hasStarted(), host.isRunning(),
                    vm.hasStarted(), vm.isRunning(), cloudlet.hasStarted(), cloudlet.isRunning())));
        broker.submitVm(vm);
        broker.submitCloudlet(cloudlet);

        assertAll("before start",
            () -> assertFalse(broker.hasStarted()), () -> assertFalse(broker.isRunning()),
            () -> assertFalse(vm.hasStarted()), () -> assertFalse(vm.isRunning()),
            () -> assertFalse(cloudlet.hasStarted()), () -> assertFalse(cloudlet.isRunning()),
            // A Host is powered on at time 0, when it's added to the Datacenter.
            () -> assertTrue(host.hasStarted()), () -> assertTrue(host.isRunning()));

        simulation.start();
        // Hosts keep powered on after the simulation ends. Since it isn't running anymore, the Host is powered off right away.
        host.setActive(false);

        assertAll("while running",
            () -> assertFalse(runningStates.isEmpty()),
            () -> assertFalse(runningStates.contains(false)));

        assertAll("after finish",
            () -> assertTrue(broker.hasStarted()), () -> assertFalse(broker.isRunning()),
            () -> assertTrue(host.hasStarted()), () -> assertFalse(host.isRunning()),
            () -> assertTrue(vm.hasStarted()), () -> assertFalse(vm.isRunning()),
            () -> assertTrue(cloudlet.hasStarted()), () -> assertFalse(cloudlet.isRunning()));
    }
}
