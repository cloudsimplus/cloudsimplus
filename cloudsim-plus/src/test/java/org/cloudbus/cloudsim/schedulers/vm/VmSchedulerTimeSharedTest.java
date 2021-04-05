/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.schedulers.vm;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.ResourceProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.MipsShare;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class VmSchedulerTimeSharedTest {
    private static final double MIPS = 1000;
    private static final int VM_PES_NUMBER = 2;
    private VmScheduler vmScheduler;
    private Vm vm0;
    private Vm vm1;

    private VmScheduler createVmScheduler(final double mips, final int pesNumber) {
        final VmSchedulerTimeShared scheduler = new VmSchedulerTimeShared();
        final List<Pe> peList = new ArrayList<>(pesNumber);
        LongStream.range(0, pesNumber).forEach(i -> peList.add(new PeSimple(mips, new PeProvisionerSimple())));
        final Host host = new HostSimple(2048, 20000, 20000, peList);
        host
            .setRamProvisioner(new ResourceProvisionerSimple())
            .setBwProvisioner(new ResourceProvisionerSimple())
            .setVmScheduler(scheduler)
            .setId(0);
        return scheduler;
    }

    @BeforeEach
    public void setUp() {
        vmScheduler = createVmScheduler(MIPS,  VM_PES_NUMBER);
        vm0 = VmTestUtil.createVm(0, MIPS / 4, VM_PES_NUMBER);
        vm1 = VmTestUtil.createVm(1, MIPS / 2, VM_PES_NUMBER);
    }

    @Test
    public void testInit() {
        final List<Pe> peList = vmScheduler.getHost().getWorkingPeList();
        assertAll(
            () -> assertEquals(2000, vmScheduler.getTotalAvailableMips()),
            () -> assertEquals(0, vmScheduler.getTotalAllocatedMipsForVm(vm0))
        );
    }

    @Test
    public void testIsSuitableForVm0() {
        final Vm vm0 = VmTestUtil.createVm(0, MIPS / 4, 2);
        vm0.setCreated(false);
        assertTrue(vmScheduler.isSuitableForVm(vm0));
    }

    @Test
    public void testIsSuitableForVm1() {
        final Vm vm1 = VmTestUtil.createVm(1, MIPS / 2, 2);
        vm1.setCreated(false);
        assertTrue(vmScheduler.isSuitableForVm(vm1));
    }

    @Test
    public void testIsSuitableForVm2() {
        final Vm vm2 = VmTestUtil.createVm(2, MIPS * 2, 2);
        vm2.setCreated(false);
        assertFalse(vmScheduler.isSuitableForVm(vm2));
    }

    @Test
    public void testAllocatePesForVm() {
        assertTrue(vmScheduler.allocatePesForVm(vm0, new MipsShare(250)));
        assertEquals(1750, vmScheduler.getTotalAvailableMips());
        assertEquals(250, vmScheduler.getTotalAllocatedMipsForVm(vm0));

        assertTrue(vmScheduler.allocatePesForVm(vm1, new MipsShare(2,300)));

        assertEquals(1150, vmScheduler.getTotalAvailableMips());
        assertEquals(600, vmScheduler.getTotalAllocatedMipsForVm(vm1));

        vmScheduler.deallocatePesForAllVms();

        assertEquals(2000, vmScheduler.getTotalAvailableMips());
        assertEquals(0, vmScheduler.getTotalAllocatedMipsForVm(vm1));
    }

    @Test
    public void testAllocatePesWhenVmMigrationIn() {
        vm0.setInMigration(true);

        vmScheduler.getHost().addMigratingInVm(vm0);
        assertEquals(1500, vmScheduler.getTotalAvailableMips());
        /*While the VM is being migrated, just 10% of its requested MIPS is allocated,
        * representing the CPU migration overhead.*/
        assertEquals(50, vmScheduler.getTotalAllocatedMipsForVm(vm0));

        vmScheduler.deallocatePesForAllVms();

        assertEquals(2000, vmScheduler.getTotalAvailableMips());
        assertEquals(0, vmScheduler.getTotalAllocatedMipsForVm(vm1));
    }

    @Test
    public void testAllocatePesWhenVmMigrationOut() {
        vmScheduler = createVmScheduler(MIPS, 2);
        final double vmMips = MIPS / 4;
        final Vm vm0 = VmTestUtil.createVm(0, vmMips, 2);
        vmScheduler.getHost().addVmMigratingOut(vm0);

        vmScheduler.allocatePesForVm(vm0, new MipsShare(vmMips));
        assertTrue(vmScheduler.getHost().getVmsMigratingOut().isEmpty());
    }

    @Test
    public void testDeallocatePartialPesFromVm() {
        final int HOST_PES = 8;
        final int VM_PES = 4;
        vmScheduler = createVmScheduler(MIPS, HOST_PES);
        final Vm vm = VmTestUtil.createVm(0, MIPS, VM_PES);

        vmScheduler.allocatePesForVm(vm0, new MipsShare(VM_PES, MIPS));
        vmScheduler.deallocatePesFromVm(vm, 2);
        final int expectedBusyPes = 2;
        assertEquals(expectedBusyPes, vmScheduler.getHost().getBusyPeList().size());
    }

    @Test
    public void testDeallocateAllPesFromVmOneArgMethod() {
        final int HOST_PES = 8;
        final int VM_PES = 4;
        vmScheduler = createVmScheduler(MIPS, HOST_PES);
        vm0 = VmTestUtil.createVm(0, MIPS, VM_PES);

        vmScheduler.allocatePesForVm(vm0, new MipsShare(VM_PES, MIPS));
        vmScheduler.deallocatePesFromVm(vm0);
        final int expectedBusyPes = 0;
        assertEquals(expectedBusyPes, vmScheduler.getHost().getBusyPeList().size());
    }

    @Test
    public void testDeallocateAllPesFromVmTwoArgsMethod() {
        final int HOST_PES = 8;
        final int VM_PES = 4;
        vmScheduler = createVmScheduler(MIPS, HOST_PES);
        vm0 = VmTestUtil.createVm(0, MIPS, VM_PES);


        vmScheduler.allocatePesForVm(vm0, new MipsShare(VM_PES, MIPS));
        vmScheduler.deallocatePesFromVm(vm0, VM_PES);
        final int expectedBusyPes = 0;
        assertEquals(expectedBusyPes, vmScheduler.getHost().getBusyPeList().size());
    }

    @Test
    public void testTryDeallocateMorePesThanAllocated() {
        final int HOST_PES = 8;
        vmScheduler = createVmScheduler(MIPS, HOST_PES);

        final MipsShare mipsShare = new MipsShare(vm0.getNumberOfPes(), MIPS);

        vmScheduler.allocatePesForVm(vm0, mipsShare);
        vmScheduler.allocatePesForVm(vm1, mipsShare);
        //Try to remove more PEs than it's allocated (only the allocated PEs have to be deallocated)
        vmScheduler.deallocatePesFromVm(vm0, HOST_PES);
        //Since only the PEs for vm0 were deallocated, the PEs from vm1 have to be busy yet
        final long expectedBusyPes = vm1.getNumberOfPes();
        assertEquals(expectedBusyPes, vmScheduler.getHost().getBusyPeList().size(), "Number of busy Host PEs:");
    }
}
