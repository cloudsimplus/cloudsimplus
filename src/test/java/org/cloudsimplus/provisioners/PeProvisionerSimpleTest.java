/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.provisioners;

import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author	Anton Beloglazov
 * @since	CloudSim Toolkit 2.0
 */
public class PeProvisionerSimpleTest {

    private static final double MIPS = 1000;
    public static final double THREE_FORTH_MIPS = MIPS * 3 / 4.0;
    private static final long HALF_MIPS = (long)(MIPS / 2.0);
    private static final long ONE_FORTH_MIPS = (long)(MIPS / 4.0);

    private PeProvisionerSimple peProvisioner;

    @BeforeEach
    public void setUp() throws Exception {
        peProvisioner = new PeProvisionerSimple();
        new PeSimple(MIPS, peProvisioner);
    }

    @Test
    public void testGetAvailableMips() {
        assertEquals(MIPS, peProvisioner.getAvailableResource());
    }

    @Test
    public void testGetTotalAllocatedMips() {
        assertEquals(0, peProvisioner.getTotalAllocatedResource());
    }

    @Test
    public void testGetUtilization() {
        assertEquals(0, peProvisioner.getUtilization());
    }

    @Test
    public void testAllocateMipsForVm() {
        final Vm vm0 = VmTestUtil.createVm(0, HALF_MIPS, 1);
        final Vm vm1 = VmTestUtil.createVm(1, HALF_MIPS, 1);
        final Vm vm2 = VmTestUtil.createVm(2, HALF_MIPS, 2);

        assertAll(
            () -> assertTrue(peProvisioner.allocateResourceForVm(vm0, HALF_MIPS)),
            () -> assertEquals(HALF_MIPS, peProvisioner.getAvailableResource()),
            () -> assertEquals(HALF_MIPS, peProvisioner.getTotalAllocatedResource()),
            () -> assertEquals(0.5, peProvisioner.getUtilization()),

            () -> assertTrue(peProvisioner.allocateResourceForVm(vm1, ONE_FORTH_MIPS)),
            () -> assertEquals(ONE_FORTH_MIPS, peProvisioner.getAvailableResource()),
            () -> assertEquals(THREE_FORTH_MIPS, peProvisioner.getTotalAllocatedResource()),
            () -> assertEquals(0.75, peProvisioner.getUtilization()),

            () -> assertFalse(peProvisioner.allocateResourceForVm(vm2, HALF_MIPS)),
            () -> assertEquals(ONE_FORTH_MIPS, peProvisioner.getAvailableResource()),
            () -> assertEquals(THREE_FORTH_MIPS, peProvisioner.getTotalAllocatedResource()),
            () -> assertEquals(0.75, peProvisioner.getUtilization())
        );

        peProvisioner.deallocateResourceForVm(vm0);
        peProvisioner.deallocateResourceForVm(vm1);

        assertAll(
            () -> assertTrue(peProvisioner.allocateResourceForVm(vm2, ONE_FORTH_MIPS)),
            () -> assertEquals(THREE_FORTH_MIPS, peProvisioner.getAvailableResource()),
            () -> assertEquals(ONE_FORTH_MIPS, peProvisioner.getTotalAllocatedResource()),
            () -> assertEquals(0.25, peProvisioner.getUtilization()),

            //Allocating the same amount doesn't change anything
            () -> assertTrue(peProvisioner.allocateResourceForVm(vm2, ONE_FORTH_MIPS)),
            () -> assertEquals(THREE_FORTH_MIPS, peProvisioner.getAvailableResource()),
            () -> assertEquals(ONE_FORTH_MIPS, peProvisioner.getTotalAllocatedResource()),
            () -> assertEquals(0.25, peProvisioner.getUtilization())
        );
    }

    @Test
    public void testDeallocateMipsForVM() {
        final Vm vm1 = VmTestUtil.createVm(0, HALF_MIPS, 1);
        final Vm vm2 = VmTestUtil.createVm(1, HALF_MIPS, 1);

        peProvisioner.allocateResourceForVm(vm1, HALF_MIPS);
        peProvisioner.allocateResourceForVm(vm2, ONE_FORTH_MIPS);
        assertEquals(ONE_FORTH_MIPS, peProvisioner.getAvailableResource());

        peProvisioner.deallocateResourceForVm(vm1);
        assertEquals(THREE_FORTH_MIPS, peProvisioner.getAvailableResource());

        peProvisioner.deallocateResourceForVm(vm2);
        assertEquals(MIPS, peProvisioner.getAvailableResource());
    }

}
