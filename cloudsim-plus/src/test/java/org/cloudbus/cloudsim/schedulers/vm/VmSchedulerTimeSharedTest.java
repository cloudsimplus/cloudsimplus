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
        vmScheduler = createVmScheduler(MIPS, 2);
        vm0 = VmTestUtil.createVm(0, MIPS / 4, 2);
        vm1 = VmTestUtil.createVm(1, MIPS / 2, 2);
    }

    @Test
    public void testInit() {
        final List<Pe> peList = vmScheduler.getHost().getWorkingPeList();
        assertAll(
            () -> assertEquals(peList, vmScheduler.getWorkingPeList()),
            () -> assertEquals(2000, vmScheduler.getAvailableMips()),
            () -> assertEquals(1000, vmScheduler.getMaxAvailableMips()),
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
        final List<Double> mipsShare1 = new ArrayList<>(1);
        mipsShare1.add(250.0);

        assertTrue(vmScheduler.allocatePesForVm(vm0, mipsShare1));
        assertEquals(1750, vmScheduler.getAvailableMips());
        assertEquals(1000, vmScheduler.getMaxAvailableMips());
        assertEquals(MIPS / 4, vmScheduler.getTotalAllocatedMipsForVm(vm0));

        final List<Double> mipsShare2 = new ArrayList<>(2);
        mipsShare2.add(500.0);
        mipsShare2.add(125.0);

        assertTrue(vmScheduler.allocatePesForVm(vm1, mipsShare2));

        assertEquals(1125, vmScheduler.getAvailableMips());
        assertEquals(875, vmScheduler.getMaxAvailableMips());
        assertEquals(625, vmScheduler.getTotalAllocatedMipsForVm(vm1));

        vmScheduler.deallocatePesForAllVms();

        assertEquals(2000, vmScheduler.getAvailableMips());
        assertEquals(1000, vmScheduler.getMaxAvailableMips());
        assertEquals(0, vmScheduler.getTotalAllocatedMipsForVm(vm1));
    }

    @Test
    public void testAllocatePesWhenVmMigrationIn() {
        vm0.setInMigration(true);

        vmScheduler.getHost().addMigratingInVm(vm0);
        assertEquals(1500, vmScheduler.getAvailableMips());
        /*While the VM is being migrated, just 10% of its requested MIPS is allocated,
        * representing the CPU migration overhead.*/
        assertEquals(50, vmScheduler.getTotalAllocatedMipsForVm(vm0));

        vmScheduler.deallocatePesForAllVms();

        assertEquals(2000, vmScheduler.getAvailableMips());
        assertEquals(1000, vmScheduler.getMaxAvailableMips());
        assertEquals(0, vmScheduler.getTotalAllocatedMipsForVm(vm1));
    }

    @Test
    public void testAllocatePesWhenVmMigrationOut() {
        vmScheduler = createVmScheduler(MIPS, 2);
        final double vmMips = MIPS / 4;
        final Vm vm0 = VmTestUtil.createVm(0, vmMips, 2);
        vmScheduler.getHost().addVmMigratingOut(vm0);

        final List<Double> mipsShare = new ArrayList<>(1);
        mipsShare.add(vmMips);

        vmScheduler.allocatePesForVm(vm0, mipsShare);
        assertTrue(vmScheduler.getHost().getVmsMigratingOut().isEmpty());
    }
}
