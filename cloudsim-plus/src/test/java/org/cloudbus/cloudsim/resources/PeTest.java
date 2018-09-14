/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.resources;

import org.cloudbus.cloudsim.provisioners.PeProvisioner;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe.Status;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author	Anton Beloglazov
 * @since	CloudSim Toolkit 2.0
 */
public class PeTest {

    private static final double MIPS = 1000;
    private PeProvisionerSimple peProvisioner;

    @Before
    public void setUp() throws Exception {
        peProvisioner = new PeProvisionerSimple();
    }

    private PeSimple createPe() {
        peProvisioner = new PeProvisionerSimple();
        return new PeSimple(0, MIPS, peProvisioner);
    }

    private PeSimple createPe(PeProvisioner peProvisioner) {
        return new PeSimple(0, peProvisioner);
    }

    @Test
    public void testGetPeProvisioner() {
        final PeSimple pe = createPe();
        assertSame(peProvisioner, pe.getPeProvisioner());
        assertEquals(MIPS, pe.getPeProvisioner().getAvailableResource(), 0);
    }

    @Test
    public void testSetId() {
        final PeSimple pe = createPe();
        assertEquals(0, pe.getId());
        pe.setId(1);
        assertEquals(1, pe.getId());
    }

    @Test
    public void testSetMips() {
        final PeSimple pe = createPe();
        assertEquals(MIPS, pe.getCapacity(), 0);
        pe.setCapacity(MIPS / 2);
        assertEquals(MIPS / 2, pe.getCapacity(), 0);
    }

    @Test
    public void testSetStatus() {
        final PeSimple pe = createPe();
        assertEquals(PeSimple.Status.FREE, pe.getStatus());
        pe.setStatus(PeSimple.Status.BUSY);
        assertEquals(PeSimple.Status.BUSY, pe.getStatus());
        pe.setStatus(PeSimple.Status.FAILED);
        assertEquals(PeSimple.Status.FAILED, pe.getStatus());
        pe.setStatus(PeSimple.Status.FREE);
        assertEquals(PeSimple.Status.FREE, pe.getStatus());
    }

    @Test(expected = NullPointerException.class)
    public void testSetPeProvisionerWhenNull() {
        final PeSimple pe = createPe();
        pe.setPeProvisioner(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNewPeWhenNullProvisioner() {
        createPe(null);
    }

    @Test
    public void testNullObject(){
        assertEquals(-1, Pe.NULL.getId(), 0);
        assertEquals(0, Pe.NULL.getCapacity(), 0);
        assertEquals(Status.FAILED, Pe.NULL.getStatus());
        assertFalse(Pe.NULL.setCapacity(1000));
        assertEquals(0, Pe.NULL.getCapacity(), 0);

        //setters haven't any effect on Null Object Design Pattern
        Pe.NULL.setStatus(Status.FREE);
        assertEquals(Status.FAILED, Pe.NULL.getStatus());
    }
}
