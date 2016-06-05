/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.lists;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSimpleTest;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * @author		Anton Beloglazov
 * @since		CloudSim Toolkit 2.0
 */
public class PeListTest {

    private static final double MIPS = 1000;
    private List<Pe> peList;

    @Before
    public void setUp() throws Exception {
        peList = new ArrayList<>();

        peList.add(new PeSimple(0, new PeProvisionerSimple(MIPS)));
        peList.add(new PeSimple(1, new PeProvisionerSimple(MIPS)));
    }

    @Test
    public void testGetMips() {
        assertEquals(MIPS, PeList.getMips(peList, 0), 0);
        assertEquals(MIPS, PeList.getMips(peList, 1), 0);
        assertEquals(-1, PeList.getMips(peList, 2));
    }

    @Test
    public void testGetTotalMips() {
        assertEquals(MIPS * peList.size(), PeList.getTotalMips(peList), 0);
    }

    @Test
    public void testSetPeStatus() {
        assertEquals(2, PeList.getNumberOfFreePes(peList));
        assertEquals(0, PeList.getNumberOfBusyPes(peList));
        assertTrue(PeList.setPeStatus(peList, 0, PeSimple.Status.BUSY));
        assertEquals(PeSimple.Status.BUSY, PeList.getById(peList, 0).getStatus());
        assertEquals(1, PeList.getNumberOfFreePes(peList));
        assertEquals(1, PeList.getNumberOfBusyPes(peList));
        assertTrue(PeList.setPeStatus(peList, 1, PeSimple.Status.BUSY));
        assertEquals(PeSimple.Status.BUSY, PeList.getById(peList, 1).getStatus());
        assertEquals(0, PeList.getNumberOfFreePes(peList));
        assertEquals(2, PeList.getNumberOfBusyPes(peList));
        assertFalse(PeList.setPeStatus(peList, 2, PeSimple.Status.BUSY));
        assertEquals(0, PeList.getNumberOfFreePes(peList));
        assertEquals(2, PeList.getNumberOfBusyPes(peList));
    }

    @Test
    public void testSetStatusFailed() {
        assertEquals(PeSimple.Status.FREE, PeList.getById(peList, 0).getStatus());
        assertEquals(PeSimple.Status.FREE, PeList.getById(peList, 1).getStatus());
        PeList.setStatusFailed(peList, true);
        assertEquals(PeSimple.Status.FAILED, PeList.getById(peList, 0).getStatus());
        assertEquals(PeSimple.Status.FAILED, PeList.getById(peList, 1).getStatus());
        PeList.setStatusFailed(peList, false);
        assertEquals(PeSimple.Status.FREE, PeList.getById(peList, 0).getStatus());
        assertEquals(PeSimple.Status.FREE, PeList.getById(peList, 1).getStatus());

        PeList.setStatusFailed(peList, "test", 0, true);
        assertEquals(PeSimple.Status.FAILED, PeList.getById(peList, 0).getStatus());
        assertEquals(PeSimple.Status.FAILED, PeList.getById(peList, 1).getStatus());
        PeList.setStatusFailed(peList, "test", 0, false);
        assertEquals(PeSimple.Status.FREE, PeList.getById(peList, 0).getStatus());
        assertEquals(PeSimple.Status.FREE, PeList.getById(peList, 1).getStatus());
    }

    @Test
    public void testFreePe() {
        assertSame(peList.get(0), PeList.getFreePe(peList));
        PeList.setPeStatus(peList, 0, PeSimple.Status.BUSY);
        assertSame(peList.get(1), PeList.getFreePe(peList));
        PeList.setPeStatus(peList, 1, PeSimple.Status.BUSY);
        assertNull(PeList.getFreePe(peList));
    }

    @Test
    public void testGetMaxUtilization() {
        Vm vm0 = VmSimpleTest.createVm(0, MIPS / 2, 1);
        Vm vm1 = VmSimpleTest.createVm(1, MIPS / 2, 1);

        assertTrue(peList.get(0).getPeProvisioner().allocateMipsForVm(vm0, MIPS / 3));
        assertTrue(peList.get(1).getPeProvisioner().allocateMipsForVm(vm1, MIPS / 5));

        assertEquals((MIPS / 3) / MIPS, PeList.getMaxUtilization(peList), 0.001);
    }

    @Test
    public void testGetMaxUtilizationAmongVmsPes() {
        Vm vm0 = VmSimpleTest.createVm(0, MIPS / 2, 1);
        Vm vm1 = VmSimpleTest.createVm(1, MIPS / 2, 1);

        assertTrue(peList.get(0).getPeProvisioner().allocateMipsForVm(vm0, MIPS / 3));
        assertTrue(peList.get(1).getPeProvisioner().allocateMipsForVm(vm1, MIPS / 5));

        assertEquals((MIPS / 3) / MIPS, PeList.getMaxUtilizationAmongVmsPes(peList, vm0), 0.001);
        assertEquals((MIPS / 5) / MIPS, PeList.getMaxUtilizationAmongVmsPes(peList, vm1), 0.001);
    }

}
