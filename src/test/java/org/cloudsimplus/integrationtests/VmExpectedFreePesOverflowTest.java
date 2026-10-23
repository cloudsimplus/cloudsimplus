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
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/// Verifies that `expectedFreePesNumber` does not overflow
/// beyond the total VM PEs when a Cloudlet requires more PEs
/// than the VM has available.
///
/// @author Theodoros Aslanidis
/// @see <a href="https://github.com/cloudsimplus/cloudsimplus/issues/546">Issue #546</a>
public final class VmExpectedFreePesOverflowTest {
    // Constants that matter to the test.
    private static final int VM_PES = 2;
    private static final int CLOUDLET_PES_HIGHER_THAN_VM_CAPACITY = 3;

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
        final List<Pe> peList = List.of(newPe(), newPe(), newPe(), newPe());
        final var host = new HostSimple(hRAM, hBW, hStorage, peList);
        new DatacenterSimple(simulation, List.of(host));

        final var broker = new DatacenterBrokerSimple(simulation);
        vm = new VmSimple(new CoreCapacity(VM_PES, HOST_MIPS));
        vm.setRam(vmRAM).setBw(vmBW).setSize(vmStorage);
        broker.submitVmList(List.of(vm));

        final var cloudlet = new CloudletSimple(cloudletLen, CLOUDLET_PES_HIGHER_THAN_VM_CAPACITY);
        broker.submitCloudletList(List.of(cloudlet));
    }

    private static PeSimple newPe() {
        return new PeSimple(HOST_MIPS);
    }

    /// When a Cloudlet requires more PEs than the VM has,
    /// `expectedFreePesNumber` must never exceed the total VM PEs
    /// after the Cloudlet finishes.
    @Test
    public void expectedFreePesDoesNotExceedVmPesAfterCloudletFinish() {
        assumeTrue(CLOUDLET_PES_HIGHER_THAN_VM_CAPACITY > VM_PES,
            "This test is only valid if the Cloudlet requires more PEs than the VM has");

        simulation.start();
        assertTrue(vm.getExpectedFreePesNumber() <= vm.getPesNumber(),
            "expectedFreePesNumber (%d) should not exceed total VM PEs (%d)"
                .formatted(vm.getExpectedFreePesNumber(), vm.getPesNumber()));

        assertEquals(VM_PES, vm.getExpectedFreePesNumber(),
            "expectedFreePesNumber should equal total VM PEs after all Cloudlets finish");

        assertEquals(VM_PES, vm.getFreePesNumber(),
            "freePesNumber should equal total VM PEs after all Cloudlets finish");
    }
}
