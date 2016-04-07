/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.provisioners;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSimpleTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author	Anton Beloglazov
 * @since	CloudSim Toolkit 2.0
 */
public class PeProvisionerSimpleTest {

    private static final double MIPS = 1000;

    private PeProvisionerSimple peProvisioner;

    @Before
    public void setUp() throws Exception {
        peProvisioner = new PeProvisionerSimple(MIPS);
    }

    @Test
    public void testGetMips() {
        assertEquals(MIPS, peProvisioner.getMips(), 0);
    }

    @Test
    public void testGetAvailableMips() {
        assertEquals(MIPS, peProvisioner.getAvailableMips(), 0);
    }

    @Test
    public void testGetTotalAllocatedMips() {
        assertEquals(0, peProvisioner.getTotalAllocatedMips(), 0);
    }

    @Test
    public void testGetUtilization() {
        assertEquals(0, peProvisioner.getUtilization(), 0);
    }

    @Test
    public void testAllocateMipsForVm() {
        Vm vm1 = VmSimpleTest.createVm(0, MIPS / 2, 1);
        Vm vm2 = VmSimpleTest.createVm(1, MIPS / 2, 1);
        Vm vm3 = VmSimpleTest.createVm(2, MIPS / 2, 2);

        assertTrue(peProvisioner.allocateMipsForVm(vm1, MIPS / 2));
        assertEquals(MIPS / 2, peProvisioner.getAvailableMips(), 0);
        assertEquals(MIPS / 2, peProvisioner.getTotalAllocatedMips(), 0);
        assertEquals(0.5, peProvisioner.getUtilization(), 0);

        assertTrue(peProvisioner.allocateMipsForVm(vm2, MIPS / 4));
        assertEquals(MIPS / 4, peProvisioner.getAvailableMips(), 0);
        assertEquals(MIPS * 3 / 4, peProvisioner.getTotalAllocatedMips(), 0);
        assertEquals(0.75, peProvisioner.getUtilization(), 0);

        assertFalse(peProvisioner.allocateMipsForVm(vm3, MIPS / 2));
        assertEquals(MIPS / 4, peProvisioner.getAvailableMips(), 0);
        assertEquals(MIPS * 3 / 4, peProvisioner.getTotalAllocatedMips(), 0);
        assertEquals(0.75, peProvisioner.getUtilization(), 0);

        peProvisioner.deallocateMipsForVm(vm1);
        peProvisioner.deallocateMipsForVm(vm2);

        assertTrue(peProvisioner.allocateMipsForVm(vm3, MIPS / 4));
        assertEquals(MIPS * 3 / 4, peProvisioner.getAvailableMips(), 0);
        assertEquals(MIPS / 4, peProvisioner.getTotalAllocatedMips(), 0);
        assertEquals(0.25, peProvisioner.getUtilization(), 0);

        assertTrue(peProvisioner.allocateMipsForVm(vm3, MIPS / 4));
        assertEquals(MIPS / 2, peProvisioner.getAvailableMips(), 0);
        assertEquals(MIPS / 2, peProvisioner.getTotalAllocatedMips(), 0);
        assertEquals(0.5, peProvisioner.getUtilization(), 0);

        List<Double> mipsArray = new ArrayList<>();
        mipsArray.add(MIPS / 2.0);
        mipsArray.add(MIPS / 2.0);

        assertTrue(peProvisioner.allocateMipsForVm(vm3, mipsArray));
        assertEquals(0, peProvisioner.getAvailableMips(), 0);
        assertEquals(MIPS, peProvisioner.getTotalAllocatedMips(), 0);
        assertEquals(1, peProvisioner.getUtilization(), 0);
    }

    @Test
    public void testGetAllocatedMiVmTestm() {
        Vm vm1 = VmSimpleTest.createVm(0, MIPS / 2, 1);
        Vm vm2 = VmSimpleTest.createVm(1, MIPS / 2, 1);
        Vm vm3 = VmSimpleTest.createVm(2, MIPS / 2, 2);

        assertNull(peProvisioner.getAllocatedMipsForVm(vm1));
        assertEquals(0, peProvisioner.getAllocatedMipsForVmByVirtualPeId(vm1, 0), 0);

        assertTrue(peProvisioner.allocateMipsForVm(vm1, MIPS / 2));
        List<Double> allocatedMips1 = new ArrayList<>();
        allocatedMips1.add(MIPS / 2);
        assertTrue(allocatedMips1.equals(peProvisioner.getAllocatedMipsForVm(vm1)));
        assertEquals(MIPS / 2, peProvisioner.getAllocatedMipsForVmByVirtualPeId(vm1, 0), 0);
        assertEquals(0, peProvisioner.getAllocatedMipsForVmByVirtualPeId(vm1, 1), 0);
        assertEquals(MIPS / 2, peProvisioner.getTotalAllocatedMipsForVm(vm1), 0);

        assertTrue(peProvisioner.allocateMipsForVm(vm2, MIPS / 4));
        List<Double> allocatedMips2 = new ArrayList<>();
        allocatedMips2.add(MIPS / 4);
        assertTrue(allocatedMips2.equals(peProvisioner.getAllocatedMipsForVm(vm2)));
        assertEquals(MIPS / 4, peProvisioner.getAllocatedMipsForVmByVirtualPeId(vm2, 0), 0);
        assertEquals(MIPS / 4, peProvisioner.getTotalAllocatedMipsForVm(vm2), 0);

        peProvisioner.deallocateMipsForVm(vm1);
        peProvisioner.deallocateMipsForVm(vm2);

        assertTrue(peProvisioner.allocateMipsForVm(vm3, MIPS / 4));
        List<Double> allocatedMips3 = new ArrayList<>();
        allocatedMips3.add(MIPS / 4);
        assertTrue(allocatedMips3.equals(peProvisioner.getAllocatedMipsForVm(vm3)));
        assertEquals(MIPS / 4, peProvisioner.getAllocatedMipsForVmByVirtualPeId(vm3, 0), 0);
        assertEquals(MIPS / 4, peProvisioner.getTotalAllocatedMipsForVm(vm3), 0);

        assertTrue(peProvisioner.allocateMipsForVm(vm3, MIPS / 4));
        allocatedMips3.add(MIPS / 4);
        assertTrue(allocatedMips3.equals(peProvisioner.getAllocatedMipsForVm(vm3)));
        assertEquals(MIPS / 4, peProvisioner.getAllocatedMipsForVmByVirtualPeId(vm3, 0), 0);
        assertEquals(MIPS / 4, peProvisioner.getAllocatedMipsForVmByVirtualPeId(vm3, 1), 0);
        assertEquals(MIPS / 2, peProvisioner.getTotalAllocatedMipsForVm(vm3), 0);

        List<Double> allocatedMips4 = new ArrayList<>();
        allocatedMips4.add(MIPS / 2.0);
        allocatedMips4.add(MIPS);
        assertFalse(peProvisioner.allocateMipsForVm(vm3, allocatedMips4));

        List<Double> allocatedMips5 = new ArrayList<>();
        allocatedMips5.add(MIPS / 2.0);
        allocatedMips5.add(MIPS / 2.0);
        assertTrue(peProvisioner.allocateMipsForVm(vm3, allocatedMips5));
        assertTrue(allocatedMips5.equals(peProvisioner.getAllocatedMipsForVm(vm3)));
        assertEquals(MIPS / 2, peProvisioner.getAllocatedMipsForVmByVirtualPeId(vm3, 0), 0);
        assertEquals(MIPS / 2, peProvisioner.getAllocatedMipsForVmByVirtualPeId(vm3, 1), 0);
        assertEquals(MIPS, peProvisioner.getTotalAllocatedMipsForVm(vm3), 0);

        peProvisioner.deallocateMipsForVm(vm1);
        peProvisioner.deallocateMipsForVm(vm2);
        peProvisioner.deallocateMipsForVm(vm3);

        assertNull(peProvisioner.getAllocatedMipsForVm(vm1));
        assertNull(peProvisioner.getAllocatedMipsForVm(vm2));
        assertNull(peProvisioner.getAllocatedMipsForVm(vm3));

        assertEquals(0, peProvisioner.getAllocatedMipsForVmByVirtualPeId(vm1, 0), 0);
        assertEquals(0, peProvisioner.getAllocatedMipsForVmByVirtualPeId(vm2, 0), 0);
        assertEquals(0, peProvisioner.getAllocatedMipsForVmByVirtualPeId(vm3, 0), 0);
        assertEquals(0, peProvisioner.getAllocatedMipsForVmByVirtualPeId(vm3, 1), 0);

        assertEquals(0, peProvisioner.getTotalAllocatedMipsForVm(vm1), 0);
        assertEquals(0, peProvisioner.getTotalAllocatedMipsForVm(vm2), 0);
        assertEquals(0, peProvisioner.getTotalAllocatedMipsForVm(vm3), 0);

        assertEquals(MIPS, peProvisioner.getAvailableMips(), 0);
    }

    @Test
    public void testDeallocateMipsForVM() {
        Vm vm1 = VmSimpleTest.createVm(0, MIPS / 2, 1);
        Vm vm2 = VmSimpleTest.createVm(1, MIPS / 2, 1);

        peProvisioner.allocateMipsForVm(vm1, MIPS / 2);
        peProvisioner.allocateMipsForVm(vm2, MIPS / 4);

        assertEquals(MIPS / 4, peProvisioner.getAvailableMips(), 0);

        peProvisioner.deallocateMipsForVm(vm1);

        assertEquals(MIPS * 3 / 4, peProvisioner.getAvailableMips(), 0);

        peProvisioner.deallocateMipsForVm(vm2);

        assertEquals(MIPS, peProvisioner.getAvailableMips(), 0);
    }

}
