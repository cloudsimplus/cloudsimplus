/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.resources;

import junit.framework.Assert;
import org.cloudbus.cloudsim.provisioners.PeProvisioner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe.Status;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;

/**
 * @author	Anton Beloglazov
 * @since	CloudSim Toolkit 2.0
 */
public class PeTest {

    private static final double MIPS = 1000;
    private PeProvisionerSimple peProvisioner;

    @Before
    public void setUp() throws Exception {
        peProvisioner = new PeProvisionerSimple(MIPS);
    }

    private PeSimple createPe() {
        peProvisioner = new PeProvisionerSimple(MIPS);
        return new PeSimple(0, peProvisioner);
    }

    private PeSimple createPe(PeProvisioner peProvisioner) {
        return new PeSimple(0, peProvisioner);
    }

    @Test
    public void testGetPeProvisioner() {
        PeSimple pe = createPe();
        assertSame(peProvisioner, pe.getPeProvisioner());
        assertEquals(MIPS, pe.getPeProvisioner().getAvailableMips(), 0);
    }

    @Test
    public void testSetId() {
        PeSimple pe = createPe();
        assertEquals(0, pe.getId());
        pe.setId(1);
        assertEquals(1, pe.getId());
    }

    @Test
    public void testSetMips() {
        PeSimple pe = createPe();
        assertEquals(MIPS, pe.getMips(), 0);
        pe.setMips(MIPS / 2);
        assertEquals(MIPS / 2, pe.getMips(), 0);
    }

    @Test
    public void testSetStatus() {
        PeSimple pe = createPe();
        assertEquals(PeSimple.Status.FREE, pe.getStatus());
        pe.setStatus(PeSimple.Status.BUSY);
        assertEquals(PeSimple.Status.BUSY, pe.getStatus());
        pe.setStatus(PeSimple.Status.FAILED);
        assertEquals(PeSimple.Status.FAILED, pe.getStatus());
        pe.setStatus(PeSimple.Status.FREE);
        assertEquals(PeSimple.Status.FREE, pe.getStatus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetPeProvisioner_null() {
        PeSimple pe = createPe();
        pe.setPeProvisioner(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewPe_nullPeProvisioner() {
        createPe(null);
    }
    
    @Test
    public void testNullObject(){
        assertEquals(0, Pe.NULL.getId(), 0);
        assertEquals(0, Pe.NULL.getMips(), 0);
        assertEquals(Status.FAILED, Pe.NULL.getStatus());
        assertFalse(Pe.NULL.setMips(1000));
        assertEquals(0, Pe.NULL.getPeProvisioner().getMips(), 0);
        
        //setters haven't any effect on Null Object Design Pattern
        Pe.NULL.setStatus(Status.FREE);
        assertEquals(Status.FAILED, Pe.NULL.getStatus());
    }
}
