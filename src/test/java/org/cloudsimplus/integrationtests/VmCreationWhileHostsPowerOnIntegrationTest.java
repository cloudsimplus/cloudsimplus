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

import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// An Integration Test (IT) to check that when no Host is powered on,
/// but some are being powered on, the broker postpones the creation of VMs
/// until a Host is powered on,
/// instead of failing to create the VMs.
///
/// @see <a href="https://github.com/cloudsimplus/cloudsimplus-examples/issues/30">Issue #30 in the examples repository</a>
final class VmCreationWhileHostsPowerOnIntegrationTest {
    private static final int TIMEOUT_SECS = 10;
    private static final int HOST_CORES = 4;
    private static final int VM_CORES = 2;
    private static final int MIPS = 1000;
    private static final int VMS = 2;
    private static final double HOST_STARTUP_DELAY = 5;
    private static final double SHORT_HOST_STARTUP_DELAY = 3;
    private static final double DELTA = 0.001;

    private CloudSimPlus simulation;
    private DatacenterBroker broker;
    private List<Vm> vmList;
    private AtomicInteger creationFailures;

    @BeforeEach
    void setUp() {
        simulation = new CloudSimPlus();
        broker = new DatacenterBrokerSimple(simulation);
        creationFailures = new AtomicInteger();
        vmList = createVms();
    }

    @Test
    @Timeout(value = TIMEOUT_SECS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    void createsVmsRightAfterHostsArePoweredOn() {
        final var dc = new DatacenterSimple(simulation, List.of(createHost(HOST_STARTUP_DELAY), createHost(HOST_STARTUP_DELAY)));

        //Postponing VM creation doesn't rely on creation retries
        broker.getVmCreation().setRetryDelay(-1);
        broker.submitVmList(vmList);
        simulation.start();

        assertVmsCreatedWithoutFailures(HOST_STARTUP_DELAY + simulation.getMinTimeBetweenEvents());
        assertEquals(0, dc.getPoweringOnHostsNumber());
    }

    /// Uses Datacenters with Hosts powering on at different times,
    /// to check VMs are created as soon as the first Host is powered on
    /// and that each Datacenter notifying the broker doesn't create VMs twice.
    @Test
    @Timeout(value = TIMEOUT_SECS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    void createsVmsOnceWhenHostsInMultipleDatacentersArePoweredOn() {
        new DatacenterSimple(simulation, List.of(createHost(HOST_STARTUP_DELAY)));
        new DatacenterSimple(simulation, List.of(createHost(SHORT_HOST_STARTUP_DELAY)));

        broker.submitVmList(vmList);
        simulation.start();

        assertTrue(broker.getVmFailedList().isEmpty());
        assertEquals(VMS, broker.getVmCreatedList().size());
        for (final var vm : vmList) {
            assertTrue(vm.getStartTime() < HOST_STARTUP_DELAY, vm + " was created only after the slowest Host was powered on");
        }
    }

    /// Submits VMs when a Host has already been powering on for some time,
    /// to check VMs wait just the remaining time for the Host to be powered on.
    @Test
    @Timeout(value = TIMEOUT_SECS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    void createsVmsSubmittedWhileHostIsPoweringOnRightAfterItIsPoweredOn() {
        final var slowHost = createHost(HOST_STARTUP_DELAY);
        final var fastHost = createHost(SHORT_HOST_STARTUP_DELAY);
        final var dc = new DatacenterSimple(simulation, List.of(slowHost, fastHost));
        fastHost.addOnStartupListener(info -> {
            //Just the slow Host is still powering on
            assertEquals(1, dc.getPoweringOnHostsNumber());
            powerOffHostAndSubmitVms(info.getHost());
        });

        simulation.start();

        assertVmsCreatedWithoutFailures(HOST_STARTUP_DELAY + simulation.getMinTimeBetweenEvents());
    }

    private void powerOffHostAndSubmitVms(final Host host) {
        host.setActive(false);
        broker.submitVmList(vmList);
    }

    private void assertVmsCreatedWithoutFailures(final double expectedVmStartTime) {
        assertEquals(0, creationFailures.get());
        assertTrue(broker.getVmFailedList().isEmpty());
        for (final var vm : vmList) {
            assertTrue(vm.isCreated() || vm.isFinished(), vm + " was not created");
            assertEquals(expectedVmStartTime, vm.getStartTime(), DELTA, vm + " start time");
        }
    }

    private static Host createHost(final double startupDelay) {
        final var host = new HostSimple(new CoreCapacity(HOST_CORES, MIPS));
        host.setStartupDelay(startupDelay);
        return host;
    }

    private List<Vm> createVms() {
        final var list = new ArrayList<Vm>(VMS);
        for (int i = 0; i < VMS; i++) {
            final var vm = new VmSimple(new CoreCapacity(VM_CORES, MIPS));
            vm.addOnCreationFailureListener(evt -> creationFailures.incrementAndGet());
            list.add(vm);
        }

        return list;
    }
}
