/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.schedulers.vm;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.allocators.ResourceAllocatorSimple;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;
import org.cloudsimplus.vms.VmTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

/// @author Anton Beloglazov
/// @since CloudSim Toolkit 2.0
public class VmSchedulerTimeSharedTest {
    private static final double MIPS = 1000;
    private static final int VM_CORES_NUMBER = 2;
    private VmScheduler vmScheduler;
    private VmSimple vm0;
    private VmSimple vm1;

    private VmScheduler createVmScheduler(final double mips, final int cores) {
        final var scheduler = new VmSchedulerTimeShared();
        final Host host = new HostSimple(2048, 20000, 20000, new CoreCapacity(cores, mips));
        host
            .setRamAllocator(new ResourceAllocatorSimple())
            .setBwAllocator(new ResourceAllocatorSimple())
            .setVmScheduler(scheduler)
            .setId(0);
        return scheduler;
    }

    @BeforeEach
    public void setUp() {
        vmScheduler = createVmScheduler(MIPS,  VM_CORES_NUMBER);
        vm0 = VmTestUtil.createVm(0, MIPS / 4, VM_CORES_NUMBER);
        vm1 = VmTestUtil.createVm(1, MIPS / 2, VM_CORES_NUMBER);
    }

    /// When VMs share Host cores, the number of cores allocated to VMs is greater than the Host cores.
    /// The Host must keep its cores busy while any VM still uses them.
    @Test
    public void testHostCoresStayBusyWhileVmsShareThem() {
        vmScheduler.getHost().getVmList().add(vm0);
        vmScheduler.getHost().getVmList().add(vm1);
        final var hostProcessor = vmScheduler.getHost().getProcessor();

        assertTrue(vmScheduler.allocateCoresForVm(vm0, new CoreCapacity(VM_CORES_NUMBER, MIPS / 4)));
        assertTrue(vmScheduler.allocateCoresForVm(vm1, new CoreCapacity(VM_CORES_NUMBER, MIPS / 2)));
        assertEquals(VM_CORES_NUMBER, hostProcessor.getBusyCores());

        vmScheduler.deallocateCoresFromVm(vm0);
        assertEquals(VM_CORES_NUMBER, hostProcessor.getBusyCores());

        vmScheduler.deallocateCoresFromVm(vm1);
        assertEquals(0, hostProcessor.getBusyCores());
    }

    @Test
    public void testResizeVmCores() {
        vmScheduler.getHost().getVmList().add(vm1);
        assertTrue(vmScheduler.allocateCoresForVm(vm1, vm1.getCurrentRequestedMips()));

        assertTrue(vmScheduler.resizeVmCores(vm1, -1));
        assertAll(
            () -> assertEquals(VM_CORES_NUMBER - 1, vm1.getProcessor().getCores()),
            () -> assertEquals(VM_CORES_NUMBER - 1, vmScheduler.getAllocatedMips(vm1).cores())
        );

        assertTrue(vmScheduler.resizeVmCores(vm1, 1));
        assertAll(
            () -> assertEquals(VM_CORES_NUMBER, vm1.getProcessor().getCores()),
            () -> assertEquals(VM_CORES_NUMBER, vmScheduler.getAllocatedMips(vm1).cores())
        );
    }

    /// Removing a core from a VM whose cores are all busy must free a busy core
    /// and remove it from the VM, keeping the VM busy cores within its capacity.
    @Test
    public void testResizeVmCoresWhenAllVmCoresAreBusy() {
        vmScheduler.getHost().getVmList().add(vm1);
        assertTrue(vmScheduler.allocateCoresForVm(vm1, vm1.getCurrentRequestedMips()));
        vm1.getProcessor().setOccupied(VM_CORES_NUMBER);

        assertTrue(vmScheduler.resizeVmCores(vm1, -1));
        assertAll(
            () -> assertEquals(VM_CORES_NUMBER - 1, vm1.getProcessor().getCores()),
            () -> assertEquals(VM_CORES_NUMBER - 1, vm1.getProcessor().getBusyCores())
        );
    }

    @Test
    public void testResizeVmCoresFailsWhenHostHasNoRoom() {
        vmScheduler.getHost().getVmList().add(vm1);
        assertTrue(vmScheduler.allocateCoresForVm(vm1, vm1.getCurrentRequestedMips()));
        assertFalse(vmScheduler.resizeVmCores(vm1, VM_CORES_NUMBER));
        assertEquals(VM_CORES_NUMBER, vm1.getProcessor().getCores());
        assertEquals(VM_CORES_NUMBER, vmScheduler.getAllocatedMips(vm1).cores());
    }

    @Test
    public void testResizeVmCoresFailsWhenRemovingMoreCoresThanVmHas() {
        vmScheduler.getHost().getVmList().add(vm1);
        assertTrue(vmScheduler.allocateCoresForVm(vm1, vm1.getCurrentRequestedMips()));
        assertFalse(vmScheduler.resizeVmCores(vm1, -(VM_CORES_NUMBER + 1)));
        assertEquals(VM_CORES_NUMBER, vm1.getProcessor().getCores());
    }

    @Test
    public void testInit() {
        assertAll(
            () -> assertEquals(2000, vmScheduler.getTotalAvailableMips()),
            () -> assertEquals(0, vmScheduler.getTotalAllocatedMipsForVm(vm0))
        );
    }

    @Test
    public void testIsSuitableForVm0() {
        final var vm0 = VmTestUtil.createVm(0, MIPS / 4, 2);
        vmScheduler.getHost().getVmList().add(vm0);

        vm0.setCreated(false);
        assertTrue(vmScheduler.isSuitableForVm(vm0));
    }

    @Test
    public void testIsSuitableForVm1() {
        final var vm1 = VmTestUtil.createVm(1, MIPS / 2, 2);
        vmScheduler.getHost().getVmList().add(vm1);

        vm1.setCreated(false);
        assertTrue(vmScheduler.isSuitableForVm(vm1));
    }

    @Test
    public void testIsSuitableForVm2() {
        final var vm2 = VmTestUtil.createVm(2, MIPS * 2, 2);
        vmScheduler.getHost().getVmList().add(vm2);

        vm2.setCreated(false);
        assertFalse(vmScheduler.isSuitableForVm(vm2));
    }

    @Test
    public void testAllocateCoresForVm() {
        vmScheduler.getHost().getVmList().add(vm0);
        vmScheduler.getHost().getVmList().add(vm1);

        assertTrue(vmScheduler.allocateCoresForVm(vm0, new CoreCapacity(250)));
        assertEquals(1750, vmScheduler.getTotalAvailableMips());
        assertEquals(250, vmScheduler.getTotalAllocatedMipsForVm(vm0));

        assertTrue(vmScheduler.allocateCoresForVm(vm1, new CoreCapacity(2,300)));

        assertEquals(1150, vmScheduler.getTotalAvailableMips());
        assertEquals(850, vmScheduler.getTotalAllocatedMips());
        assertEquals(600, vmScheduler.getTotalAllocatedMipsForVm(vm1));

        vmScheduler.deallocateCoresFromVm(vm0);
        vmScheduler.deallocateCoresFromVm(vm1);

        assertEquals(2000, vmScheduler.getTotalAvailableMips());
        assertEquals(0, vmScheduler.getTotalAllocatedMips());
        assertEquals(0, vmScheduler.getTotalAllocatedMipsForVm(vm1));
    }

    @Test
    public void testAllocateCoresWhenVmMigrationIn() {
        final Vm vm = VmTestUtil.createVm(0, MIPS / 4, VM_CORES_NUMBER);
        vm.setInMigration(true);

        vmScheduler.getHost().addMigratingInVm(vm);
        assertEquals(1500, vmScheduler.getTotalAvailableMips());
        /*While the VM is being migrated, just 10% of its requested MIPS is allocated,
        * representing the CPU migration overhead.*/
        assertEquals(50, vmScheduler.getTotalAllocatedMipsForVm(vm));

        vmScheduler.deallocateCoresFromVm(vm);

        assertEquals(2000, vmScheduler.getTotalAvailableMips());
        assertEquals(0, vmScheduler.getTotalAllocatedMipsForVm(vm1));
    }

    @Test
    public void testAllocateCoresWhenVmMigrationOut() {
        vmScheduler = createVmScheduler(MIPS, 2);
        vmScheduler.getHost().getVmList().add(vm0);

        final double vmMips = MIPS / 4;
        final Vm vm0 = VmTestUtil.createVm(0, vmMips, 2);
        vmScheduler.getHost().addVmMigratingOut(vm0);

        vmScheduler.allocateCoresForVm(vm0, new CoreCapacity(vmMips));
        assertTrue(vmScheduler.getHost().getVmsMigratingOut().isEmpty());
    }

    @Test
    public void testDeallocatePartialCoresFromVm() {
        final int HOST_CORES = 8;
        final int VM_CORES = 4;
        final var vm = VmTestUtil.createVm(0, MIPS, VM_CORES);
        vmScheduler = createVmScheduler(MIPS, HOST_CORES);
        final Host host = vmScheduler.getHost();
        vm.setHost(host);
        host.getVmList().add(vm);

        vmScheduler.allocateCoresForVm(vm, new CoreCapacity(VM_CORES, MIPS));
        ((VmSchedulerAbstract) vmScheduler).deallocateCoresFromVm(vm, 2);
        final int expectedBusyCores = 2;
        assertEquals(expectedBusyCores, host.getProcessor().getBusyCores());
    }

    @Test
    public void testDeallocateAllCoresFromVmOneArgMethod() {
        final int HOST_CORES = 8;
        final int VM_CORES = 4;
        vm0 = VmTestUtil.createVm(0, MIPS, VM_CORES);
        vmScheduler = createVmScheduler(MIPS, HOST_CORES);
        vmScheduler.getHost().getVmList().add(vm0);

        vmScheduler.allocateCoresForVm(vm0, new CoreCapacity(VM_CORES, MIPS));
        vmScheduler.deallocateCoresFromVm(vm0);
        final int expectedBusyCores = 0;
        assertEquals(expectedBusyCores, vmScheduler.getHost().getProcessor().getBusyCores());
    }

    @Test
    public void testDeallocateAllCoresFromVmTwoArgsMethod() {
        final int HOST_CORES = 8;
        final int VM_CORES = 4;
        vm0 = VmTestUtil.createVm(0, MIPS, VM_CORES);
        vmScheduler = createVmScheduler(MIPS, HOST_CORES);
        vmScheduler.getHost().getVmList().add(vm0);

        vmScheduler.allocateCoresForVm(vm0, new CoreCapacity(VM_CORES, MIPS));
        ((VmSchedulerAbstract) vmScheduler).deallocateCoresFromVm(vm0, VM_CORES);
        final int expectedBusyCores = 0;
        assertEquals(expectedBusyCores, vmScheduler.getHost().getProcessor().getBusyCores());
    }

    @Test
    public void testTryDeallocateMoreCoresThanAllocated() {
        final int HOST_CORES = 8;
        vmScheduler = createVmScheduler(MIPS, HOST_CORES);
        final Host host = vmScheduler.getHost();
        vm0.setHost(host);
        vm1.setHost(host);
        host.getVmList().add(vm0);
        host.getVmList().add(vm1);

        final var mipsShare = new CoreCapacity(vm0.getProcessor().getCores(), MIPS);

        vmScheduler.allocateCoresForVm(vm0, mipsShare);
        vmScheduler.allocateCoresForVm(vm1, mipsShare);
        //Try to remove more cores than it's allocated (only the allocated cores have to be deallocated)
        ((VmSchedulerAbstract) vmScheduler).deallocateCoresFromVm(vm0, HOST_CORES);
        //Since only the cores for vm0 were deallocated, the cores from vm1 have to be busy yet
        final long expectedBusyCores = vm1.getProcessor().getCores();
        assertEquals(expectedBusyCores, host.getProcessor().getBusyCores(), "Number of busy Host CPU cores:");
    }
}
