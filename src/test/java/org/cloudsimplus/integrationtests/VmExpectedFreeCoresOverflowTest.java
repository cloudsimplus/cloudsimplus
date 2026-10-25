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
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.cloudsimplus.resources.Processor.freeCores;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/// Verifies that `expectedFreeCores` does not overflow
/// beyond the total VM cores when a Cloudlet requires more cores
/// than the VM has available.
///
/// @author Theodoros Aslanidis
/// @see <a href="https://github.com/cloudsimplus/cloudsimplus/issues/546">Issue #546</a>
public final class VmExpectedFreeCoresOverflowTest {
    // Constants that matter to the test.
    private static final int VM_CORES = 2;
    private static final int CLOUDLET_CORES_HIGHER_THAN_VM_CAPACITY = 3;

    // Attributes used across the entire test class
    private static final int HOST_MIPS = 1000;
    private CloudSimPlus simulation;
    private VmSimple vm;

    @BeforeEach
    public void setUp() {
        // Values for attributes that don't matter to the test
        final int cloudletLen = 10_000;
        final int hRAM = 2048;
        final int hBW = 10_000;
        final int hStorage = 1_000_000;
        final int vmRAM = 512;
        final int vmBW = 1000;
        final int vmStorage = 10_000;

        simulation = new CloudSimPlus();
        final var host = HostSimple.of(hRAM, hBW, hStorage, new CoreCapacity(4, HOST_MIPS));
        DatacenterSimple.of(simulation, List.of(host));

        final var broker = new DatacenterBrokerSimple(simulation);
        vm = (VmSimple) VmSimple.of(new CoreCapacity(VM_CORES, HOST_MIPS));
        vm.setRam(vmRAM).setBw(vmBW).setSize(vmStorage);
        broker.submitVmList(List.of(vm));

        final var cloudlet = CloudletSimple.of(cloudletLen, CLOUDLET_CORES_HIGHER_THAN_VM_CAPACITY);
        broker.submitCloudletList(List.of(cloudlet));
    }

    /// When a Cloudlet requires more cores than the VM has,
    /// `expectedFreeCores` must never exceed the total VM cores
    /// after the Cloudlet finishes.
    @Test
    public void expectedFreeCoresDoesNotExceedVmCoresAfterCloudletFinish() {
        assumeTrue(CLOUDLET_CORES_HIGHER_THAN_VM_CAPACITY > VM_CORES,
            "This test is only valid if the Cloudlet requires more CPU cores than the VM has");

        simulation.start();
        assertTrue(vm.getExpectedFreeCores() <= vm.getProcessor().getCores(),
            "expectedFreeCores (%d) should not exceed total VM CPU cores (%d)"
                .formatted(vm.getExpectedFreeCores(), vm.getProcessor().getCores()));

        assertEquals(VM_CORES, vm.getExpectedFreeCores(),
            "expectedFreeCores should equal total VM CPU cores after all Cloudlets finish");

        assertEquals(VM_CORES, freeCores(vm),
            "The number of free cores should equal total VM CPU cores after all Cloudlets finish");
    }
}
