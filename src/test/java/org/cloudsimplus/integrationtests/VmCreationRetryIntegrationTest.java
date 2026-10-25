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
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
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

/// An Integration Test (IT) to check that a VM that can't be placed in
/// the only existing Datacenter is retried just the maximum number of times
/// defined in the broker's [org.cloudsimplus.brokers.VmCreation],
/// instead of being retried forever.
///
/// @see <a href="https://github.com/cloudsimplus/cloudsimplus-examples/issues/31">Issue #31 in the examples repository</a>
final class VmCreationRetryIntegrationTest {
    private static final int TIMEOUT_SECS = 10;
    private static final int HOST_CORES = 4;
    private static final int VM_CORES = HOST_CORES;
    private static final int MIPS = 1000;
    private static final int VMS = 2;
    private static final int CLOUDLET_LENGTH = 10_000;
    private static final int MAX_RETRIES = 3;
    private static final double RETRY_DELAY = 5;

    private CloudSimPlus simulation;
    private DatacenterBroker broker;
    private Vm failedVm;
    private AtomicInteger creationFailures;

    @BeforeEach
    void setUp() {
        simulation = new CloudSimPlus();
        DatacenterSimple.of(simulation, List.of(createHost()));
        broker = new DatacenterBrokerSimple(simulation);
        creationFailures = new AtomicInteger();

        final var vmList = createVms();
        failedVm = vmList.getLast();
        failedVm.addOnCreationFailureListener(evt -> creationFailures.incrementAndGet());
        broker.submitVmList(vmList);
        broker.submitCloudletList(List.of(createCloudlet(vmList.getFirst())));
    }

    @Test
    @Timeout(value = TIMEOUT_SECS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    void retriesFailedVmUpToMaxRetries() {
        broker.getVmCreation().setRetryDelay(RETRY_DELAY).setMaxRetries(MAX_RETRIES);
        simulation.start();

        //The first creation attempt plus all retries
        assertEquals(MAX_RETRIES + 1, creationFailures.get());
        assertTrue(broker.getVmFailedList().contains(failedVm));
    }

    @Test
    @Timeout(value = TIMEOUT_SECS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    void doesNotRetryFailedVmWhenRetryIsDisabled() {
        broker.getVmCreation().setRetryDelay(-1);
        simulation.start();

        assertEquals(1, creationFailures.get());
        assertTrue(broker.getVmFailedList().contains(failedVm));
    }

    private static Host createHost() {
        return HostSimple.of(new CoreCapacity(HOST_CORES, MIPS));
    }

    /// Creates VMs that require all Host cores, so that only the first one can be placed.
    private static List<Vm> createVms() {
        final var vmList = new ArrayList<Vm>(VMS);
        for (int i = 0; i < VMS; i++) {
            vmList.add(VmSimple.of(new CoreCapacity(VM_CORES, MIPS)));
        }

        return vmList;
    }

    private static Cloudlet createCloudlet(final Vm vm) {
        return CloudletSimple.of(CLOUDLET_LENGTH, 1).setVm(vm);
    }
}
