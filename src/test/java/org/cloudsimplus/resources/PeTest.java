/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.resources;

import org.cloudsimplus.provisioners.PeProvisioner;
import org.cloudsimplus.provisioners.PeProvisionerSimple;
import org.cloudsimplus.resources.Pe.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author	Anton Beloglazov
 * @since	CloudSim Toolkit 2.0
 */
public class PeTest {

    private static final double MIPS = 1000;
    private PeProvisionerSimple peProvisioner;

    @BeforeEach
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
        assertEquals(MIPS, pe.getPeProvisioner().getAvailableResource());
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
        assertEquals(MIPS, pe.getCapacity());
        pe.setCapacity(MIPS / 2);
        assertEquals(MIPS / 2, pe.getCapacity());
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

    @Test()
    public void testSetPeProvisionerWhenNull() {
        final PeSimple pe = createPe();
        assertThrows(NullPointerException.class, () -> pe.setPeProvisioner(null));
    }

    @Test()
    public void testNewPeWhenNullProvisioner() {
        assertThrows(NullPointerException.class, () -> createPe(null));
    }

    @Test
    public void testNullObject(){
        assertAll(
            () -> assertEquals(-1, Pe.NULL.getId()),
            () -> assertEquals(0, Pe.NULL.getCapacity()),
            () -> assertEquals(Status.FAILED, Pe.NULL.getStatus()),
            () -> assertFalse(Pe.NULL.setCapacity(1000)),
            () -> assertEquals(0, Pe.NULL.getCapacity()),
            () -> assertEquals(Status.FAILED, Pe.NULL.getStatus())
        );
    }
}
