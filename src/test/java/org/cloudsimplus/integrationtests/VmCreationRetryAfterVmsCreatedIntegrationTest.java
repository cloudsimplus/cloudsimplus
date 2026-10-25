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
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/// An Integration Test (IT) to check that a VM submitted during simulation runtime,
/// after all previously submitted VMs were created, is retried when it can't be placed,
/// instead of being moved to the failed list right away.
///
/// The first VM uses all Host cores, so the second one can only be placed after the
/// first one finishes and is destroyed.
///
/// @see <a href="https://github.com/cloudsimplus/cloudsimplus/issues/579">Issue #579</a>
final class VmCreationRetryAfterVmsCreatedIntegrationTest {
    private static final int TIMEOUT_SECS = 10;
    private static final int HOST_CORES = 4;
    private static final int VM_CORES = HOST_CORES;
    private static final int MIPS = 1000;
    private static final int CLOUDLET_LENGTH = 10_000;
    private static final double RETRY_DELAY = 5;
    private static final double VM_DESTRUCTION_DELAY = 1;

    private final CloudSimPlus simulation = new CloudSimPlus();
    private final DatacenterBroker broker = new DatacenterBrokerSimple(simulation);
    private final Vm firstVm = VmSimple.of(new CoreCapacity(VM_CORES, MIPS));
    private final Vm lateVm = VmSimple.of(new CoreCapacity(VM_CORES, MIPS));
    private boolean lateVmSubmitted;

    @Test
    @Timeout(value = TIMEOUT_SECS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    void retriesVmSubmittedAfterPreviousVmsWereCreated() {
        DatacenterSimple.of(simulation, List.of(createHost()));
        broker.getVmCreation().setRetryDelay(RETRY_DELAY);
        //Destroys idle VMs, so that the first VM releases the Host when its Cloudlet finishes
        broker.setVmDestructionDelay(VM_DESTRUCTION_DELAY);

        broker.submitVmList(List.of(firstVm));
        broker.submitCloudletList(List.of(createCloudlet(firstVm)));
        simulation.addOnClockTickListener(this::submitLateVm);

        simulation.start();

        assertTrue(broker.getVmFailedList().isEmpty(), "No VM should have failed");
        assertTrue(broker.getVmCreatedList().contains(lateVm), lateVm + " should have been created");
    }

    /// Submits the late VM after the first one is created,
    /// when there is no other VM waiting to be created.
    private void submitLateVm(final EventInfo info) {
        if (lateVmSubmitted || !firstVm.isCreated() || info.getTime() <= firstVm.getStartTime()) {
            return;
        }

        lateVmSubmitted = true;
        broker.submitVmList(List.of(lateVm));
        broker.submitCloudletList(List.of(createCloudlet(lateVm)));
    }

    private static Host createHost() {
        return HostSimple.of(new CoreCapacity(HOST_CORES, MIPS));
    }

    private static Cloudlet createCloudlet(final Vm vm) {
        return CloudletSimple.of(CLOUDLET_LENGTH, VM_CORES).setVm(vm);
    }
}
