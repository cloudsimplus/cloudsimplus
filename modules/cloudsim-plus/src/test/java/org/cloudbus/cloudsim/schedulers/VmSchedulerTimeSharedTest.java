/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 * 
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.schedulers;

import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSimpleTest;

import org.cloudbus.cloudsim.lists.PeList;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class VmSchedulerTimeSharedTest {

    private static final double MIPS = 1000;
    private VmSchedulerTimeShared vmScheduler;
    private List<Pe> peList;
    private Vm vm0;
    private Vm vm1;

    @Before
    public void setUp() throws Exception {
        peList = new ArrayList<>();
        peList.add(new PeSimple(0, new PeProvisionerSimple(MIPS)));
        peList.add(new PeSimple(1, new PeProvisionerSimple(MIPS)));
        vmScheduler = new VmSchedulerTimeShared(peList);
        vm0 = VmSimpleTest.createVm(0, MIPS / 4, 2);
        vm1 = VmSimpleTest.createVm(1, MIPS / 2, 2);
    }

    @Test
    public void testIsSuitableForVm() {
        Vm vm0 = VmSimpleTest.createVm(0, MIPS / 4, 2);
        vm0.setBeingInstantiated(true);
        Vm vm1 = VmSimpleTest.createVm(1, MIPS / 2, 2);
        vm1.setBeingInstantiated(true);
        Vm vm2 = VmSimpleTest.createVm(2, MIPS * 2, 2);
        vm2.setBeingInstantiated(true);
        assertTrue(vmScheduler.isSuitableForVm(vm0));
        assertTrue(vmScheduler.isSuitableForVm(vm1));
        assertFalse(vmScheduler.isSuitableForVm(vm2));
    }

    @Test
    public void testInit() {
        assertSame(peList, vmScheduler.getPeList());
        assertEquals(PeList.getTotalMips(peList), vmScheduler.getAvailableMips(), 0);
        assertEquals(PeList.getTotalMips(peList), vmScheduler.getMaxAvailableMips(), 0);
        assertEquals(0, vmScheduler.getTotalAllocatedMipsForVm(vm0), 0);
    }

    @Test
    public void testAllocatePesForVm() {
        List<Double> mipsShare1 = new ArrayList<>();
        mipsShare1.add(MIPS / 4);

        assertTrue(vmScheduler.allocatePesForVm(vm0, mipsShare1));

        assertEquals(PeList.getTotalMips(peList) - MIPS / 4, vmScheduler.getAvailableMips(), 0);
        assertEquals(PeList.getTotalMips(peList) - MIPS / 4, vmScheduler.getMaxAvailableMips(), 0);
        assertEquals(MIPS / 4, vmScheduler.getTotalAllocatedMipsForVm(vm0), 0);

        List<Double> mipsShare2 = new ArrayList<>();
        mipsShare2.add(MIPS / 2);
        mipsShare2.add(MIPS / 8);

        assertTrue(vmScheduler.allocatePesForVm(vm1, mipsShare2));

        assertEquals(
            PeList.getTotalMips(peList) - MIPS / 4 - MIPS / 2 - MIPS / 8,
            vmScheduler.getAvailableMips(),
            0);
        assertEquals(
            PeList.getTotalMips(peList) - MIPS / 4 - MIPS / 2 - MIPS / 8,
            vmScheduler.getMaxAvailableMips(),
            0);
        assertEquals(MIPS / 2 + MIPS / 8, vmScheduler.getTotalAllocatedMipsForVm(vm1), 0);

        vmScheduler.deallocatePesForAllVms();

        assertEquals(PeList.getTotalMips(peList), vmScheduler.getAvailableMips(), 0);
        assertEquals(PeList.getTotalMips(peList), vmScheduler.getMaxAvailableMips(), 0);
        assertEquals(0, vmScheduler.getTotalAllocatedMipsForVm(vm1), 0);
    }

    @Test
    public void testAllocatePes_forVmMigrationIn() {
        vm0.setInMigration(true);
        vm1.setInMigration(true);

        List<Double> mipsShare1 = new ArrayList<>();
        mipsShare1.add(MIPS / 4);

        assertTrue(vmScheduler.allocatePesForVm(vm0, mipsShare1));

        assertEquals(PeList.getTotalMips(peList) - MIPS / 4, vmScheduler.getAvailableMips(), 0);
        assertEquals(PeList.getTotalMips(peList) - MIPS / 4, vmScheduler.getMaxAvailableMips(), 0);
        assertEquals(0.9 * MIPS / 4, vmScheduler.getTotalAllocatedMipsForVm(vm0), 0);

        List<Double> mipsShare2 = new ArrayList<>();
        mipsShare2.add(MIPS / 2);
        mipsShare2.add(MIPS / 8);

        assertTrue(vmScheduler.allocatePesForVm(vm1, mipsShare2));

        assertEquals(
            PeList.getTotalMips(peList) - MIPS / 4 - MIPS / 2 - MIPS / 8,
            vmScheduler.getAvailableMips(),
            0);
        assertEquals(
            PeList.getTotalMips(peList) - MIPS / 4 - MIPS / 2 - MIPS / 8,
            vmScheduler.getMaxAvailableMips(),
            0);
        assertEquals(0.9 * MIPS / 2 + 0.9 * MIPS / 8, vmScheduler.getTotalAllocatedMipsForVm(vm1), 0);

        vmScheduler.deallocatePesForAllVms();

        assertEquals(PeList.getTotalMips(peList), vmScheduler.getAvailableMips(), 0);
        assertEquals(PeList.getTotalMips(peList), vmScheduler.getMaxAvailableMips(), 0);
        assertEquals(0, vmScheduler.getTotalAllocatedMipsForVm(vm1), 0);
    }

    @Test
    public void testAllocatePes_forVmMigrationOut() {
        vmScheduler = new VmSchedulerTimeShared(peList);
        final double vmMips = MIPS / 4;
        Vm vm0 = VmSimpleTest.createVm(0, vmMips, 2);
        vmScheduler.getVmsMigratingOut().add(vm0.getUid());

        List<Double> mipsShare = new ArrayList<>();
        mipsShare.add(vmMips);

        vmScheduler.allocatePesForVm(vm0, mipsShare);
        assertTrue(vmScheduler.getVmsMigratingOut().isEmpty());
    }
    
}
