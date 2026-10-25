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
package org.cloudsimplus.schedulers.vm;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.vms.VmSimple;
import org.cloudsimplus.vms.VmTestUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/// @author Manoel Campos da Silva Filho
class VmSchedulerSpaceSharedTest {
    private static final double MIPS = 1000;
    private static final int HOST_CORES = 4;
    private static final int VM_CORES = 2;

    /// While a VM migrates, both the source and the target Host reserve its cores.
    /// After the migration, only the target Host keeps them.
    @Test
    void testHostBusyCoresDuringAndAfterMigration() {
        final Host source = createHost();
        final Host target = createHost();
        final VmSimple vm = VmTestUtil.createVm(0, MIPS, VM_CORES);
        allocate(source, vm);

        target.getVmsMigratingIn().add(vm);
        assertTrue(target.getVmScheduler().allocateCoresForVm(vm, new CoreCapacity(VM_CORES, MIPS)));
        assertEquals(VM_CORES, source.getProcessor().getBusyCores(), "source busy cores during migration");
        assertEquals(VM_CORES, target.getProcessor().getBusyCores(), "target busy cores during migration");

        source.getVmScheduler().deallocateCoresFromVm(vm);
        source.getVmList().remove(vm);
        source.getVmScheduler().updateHostBusyCores();
        target.getVmsMigratingIn().remove(vm);
        allocate(target, vm);
        assertEquals(0, source.getProcessor().getBusyCores(), "source busy cores after migration");
        assertEquals(VM_CORES, target.getProcessor().getBusyCores(), "target busy cores after migration");
    }

    /// A VM both in the Host VM list and in the set of VMs migrating in must be counted once.
    /// It uses a time-shared scheduler, which reduces the MIPS allocated to a VM migrating in
    /// by the migration overhead (as the available MIPS computation expects).
    @Test
    void testVmInVmListAndMigratingInIsCountedOnce() {
        final Host host = createHost();
        host.setVmScheduler(new VmSchedulerTimeShared());
        final VmSimple vm = VmTestUtil.createVm(0, MIPS, VM_CORES);
        host.getVmsMigratingIn().add(vm);
        allocate(host, vm);

        assertEquals(VM_CORES, host.getProcessor().getBusyCores());
        assertEquals((HOST_CORES - VM_CORES) * MIPS, host.getVmScheduler().getTotalAvailableMips());
    }

    /// After removing cores from a VM and failing to add more cores than the Host has,
    /// the VM shares, the VM cores and the Host busy cores must all match.
    @Test
    void testShrinkThenFailedGrowKeepsSharesConsistent() {
        final Host host = createHost();
        final VmSimple vm = VmTestUtil.createVm(0, MIPS, VM_CORES);
        allocate(host, vm);
        final VmScheduler scheduler = host.getVmScheduler();

        assertTrue(scheduler.resizeVmCores(vm, -1));
        assertFalse(scheduler.resizeVmCores(vm, HOST_CORES));
        assertAll(
            () -> assertEquals(VM_CORES - 1, vm.getProcessor().getCores(), "VM cores"),
            () -> assertEquals(VM_CORES - 1, vm.getRequestedMips().cores(), "requested cores"),
            () -> assertEquals(VM_CORES - 1, vm.getAllocatedMips().cores(), "allocated cores"),
            () -> assertEquals(VM_CORES - 1, host.getProcessor().getBusyCores(), "Host busy cores")
        );
    }

    /// When a Host recovers from a failure, the cores of the VMs still on it become busy again.
    @Test
    void testHostRecoveryRestoresBusyCores() {
        final Host host = createHost();
        final VmSimple vm = VmTestUtil.createVm(0, MIPS, VM_CORES);
        allocate(host, vm);

        host.setFailed(true);
        host.setFailed(false);
        assertEquals(VM_CORES, host.getProcessor().getBusyCores());
        assertEquals(HOST_CORES - VM_CORES, host.getProcessor().getFreeCores());
    }

    private static void allocate(final Host host, final VmSimple vm) {
        vm.setHost(host);
        host.getVmList().add(vm);
        assertTrue(host.getVmScheduler().allocateCoresForVm(vm, new CoreCapacity(VM_CORES, MIPS)));
    }

    private static Host createHost() {
        return new HostSimple(2048, 20000, 20000, new CoreCapacity(HOST_CORES, MIPS));
    }
}
