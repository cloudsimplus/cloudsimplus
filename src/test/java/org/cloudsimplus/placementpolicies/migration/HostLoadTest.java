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
package org.cloudsimplus.placementpolicies.migration;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.schedulers.cloudlet.CloudletScheduler;
import org.cloudsimplus.schedulers.vm.VmScheduler;
import org.cloudsimplus.schedulers.vm.VmSchedulerSpaceShared;
import org.cloudsimplus.schedulers.vm.VmSchedulerTimeShared;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmTestUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// @author Manoel Campos da Silva Filho
class HostLoadTest {
    private static final int HOST_CORES = 8;
    private static final double MIPS = 1000;
    private static final long HOST_BW = 100_000;
    private static final long HOST_STORAGE = 1_000_000;
    private static final long VM_RAM = 1000;
    private static final long VM_BW = 1000;
    private static final long VM_SIZE = 1000;

    /// A time-shared scheduler shares cores among VMs, so VMs planned to a Host
    /// just need to fit the Host's MIPS, even if their cores add up to more than the Host has.
    @Test
    void timeSharedHostAcceptsPlannedVmsThatShareCores() {
        final var host = createHost(new VmSchedulerTimeShared(), 10 * VM_RAM);
        final var load = new HostLoad();
        load.addVmIn(createVm(HOST_CORES, MIPS / 2));

        assertAll(
            () -> assertTrue(load.isSuitable(host, createVm(HOST_CORES, MIPS / 2)), "VM that fits the remaining MIPS"),
            () -> assertFalse(load.isSuitable(host, createVm(HOST_CORES, MIPS * 0.6)), "VM that exceeds the remaining MIPS"));
    }

    /// A space-shared scheduler gives each core to a single VM, so the cores of VMs planned to a Host aren't free.
    @Test
    void spaceSharedHostNeedsFreeCoresBesidesThePlannedVms() {
        final var host = createHost(new VmSchedulerSpaceShared(), 10 * VM_RAM);
        final var load = new HostLoad();
        load.addVmIn(createVm(HOST_CORES - 2, MIPS));

        assertAll(
            () -> assertTrue(load.isSuitable(host, createVm(2, MIPS)), "VM using the remaining cores"),
            () -> assertFalse(load.isSuitable(host, createVm(4, MIPS)), "VM needing more cores than the remaining ones"));
    }

    @Test
    void ramOfPlannedVmsIsNotFree() {
        final var host = createHost(new VmSchedulerTimeShared(), 2 * VM_RAM);
        final var load = new HostLoad();
        load.addVmIn(createVm(1, MIPS));
        assertTrue(load.isSuitable(host, createVm(1, MIPS)), "VM using the remaining RAM");

        load.addVmIn(createVm(1, MIPS));
        assertFalse(load.isSuitable(host, createVm(1, MIPS)), "VM without RAM left");
    }

    @Test
    void removingAPlannedVmFreesWhatItReserved() {
        final var host = createHost(new VmSchedulerSpaceShared(), 2 * VM_RAM);
        final var load = new HostLoad();
        final var plannedVm = createVm(HOST_CORES, MIPS);
        load.addVmIn(plannedVm);
        assertFalse(load.isSuitable(host, createVm(HOST_CORES, MIPS)));

        load.removeVmIn(plannedVm);
        assertTrue(load.isSuitable(host, createVm(HOST_CORES, MIPS)));
    }

    private static Host createHost(final VmScheduler scheduler, final long ram) {
        return HostSimple.of(ram, HOST_BW, HOST_STORAGE, new CoreCapacity(HOST_CORES, MIPS)).setVmScheduler(scheduler);
    }

    private static Vm createVm(final int cores, final double mips) {
        return VmTestUtil.createVm(0, mips, cores, VM_RAM, VM_BW, VM_SIZE, CloudletScheduler.NULL);
    }
}
