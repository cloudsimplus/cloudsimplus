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

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies that after all Cloudlets finish execution on a VM,
 * both {@code freePesNumber} and {@code expectedFreePesNumber}
 * are correctly updated to the total number of VM PEs.
 *
 * @author Theodoros Aslanidis
 * @see <a href="https://github.com/cloudsimplus/cloudsimplus/issues/544">Issue #544</a>
 */
public final class VmFreePesAfterCloudletsFinishTest {
    // Constants that matter for the tests.
    private static final int VM_PES = 2;
    private static final int CLOUDLET_PES = 1;

    // Attributes used across the entire test class
    private CloudSimPlus simulation;
    private VmSimple vm;
    private static final int HOST_MIPS = 1000;

    @BeforeEach
    public void setUp() {
        // Values for attributes that don't matter to the test
        final int hStorage = 1_000_000;
        final int hBW = 10_000;
        final int hRAM = 2048;
        final int vmRAM = 512;
        final int vmBW = 1000;
        final int cloudletLength = 10_000;

        simulation = new CloudSimPlus();
        final var host = new HostSimple(hRAM, hBW, hStorage, List.of(newPe(), newPe()));
        new DatacenterSimple(simulation, List.of(host));

        final var broker = new DatacenterBrokerSimple(simulation);
        vm = new VmSimple(HOST_MIPS, VM_PES);
        vm.setRam(vmRAM).setBw(vmBW).setSize(hBW);
        broker.submitVmList(List.of(vm));
        final var cloudlet = new CloudletSimple(cloudletLength, CLOUDLET_PES);
        broker.submitCloudletList(List.of(cloudlet));
    }

    private static PeSimple newPe() {
        return new PeSimple(HOST_MIPS);
    }

    /**
     * After all Cloudlets finish, both freePesNumber and expectedFreePesNumber
     * must be equal to the total VM PEs.
     */
    @Test
    public void freePesEqualsVmPesAfterAllCloudletsFinish() {
        simulation.start();
        assertEquals(VM_PES, vm.getFreePesNumber(),
            "freePesNumber should equal total VM PEs after all Cloudlets finish");
        assertEquals(VM_PES, vm.getExpectedFreePesNumber(),
            "expectedFreePesNumber should equal total VM PEs after all Cloudlets finish");
    }
}
