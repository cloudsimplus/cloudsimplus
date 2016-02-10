/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim;

import junit.framework.Assert;
import org.cloudbus.cloudsim.provisioners.PeProvisioner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.junit.Before;
import org.junit.Test;

/**
 * @author		Anton Beloglazov
 * @since		CloudSim Toolkit 2.0
 */
public class PeTest {

	private static final double MIPS = 1000;
        private PeProvisionerSimple peProvisioner;
        
	@Before
	public void setUp() throws Exception {
            peProvisioner = new PeProvisionerSimple(MIPS);
	}
        
        private Pe createPe(){
            peProvisioner = new PeProvisionerSimple(MIPS);            
            return new Pe(0, peProvisioner);
        }

        private Pe createPe(PeProvisioner peProvisioner){
            return new Pe(0, peProvisioner);
        }

        @Test
	public void testGetPeProvisioner() {
                Pe pe = createPe();
		assertSame(peProvisioner, pe.getPeProvisioner());
		assertEquals(MIPS, pe.getPeProvisioner().getAvailableMips(), 0);
	}

	@Test
	public void testSetId() {
		Pe pe = createPe();
		assertEquals(0, pe.getId());
		pe.setId(1);
		assertEquals(1, pe.getId());
	}

	@Test
	public void testSetMips() {
		Pe pe = createPe();
		assertEquals(MIPS, pe.getMips(), 0);
		pe.setMips(MIPS / 2);
		assertEquals(MIPS / 2, pe.getMips(), 0);
	}

	@Test
	public void testSetStatus() {
		Pe pe = createPe();
		assertEquals(Pe.Status.FREE, pe.getStatus());
		pe.setStatus(Pe.Status.BUSY);
		assertEquals(Pe.Status.BUSY, pe.getStatus());
		pe.setStatus(Pe.Status.FAILED);
		assertEquals(Pe.Status.FAILED, pe.getStatus());
		pe.setStatus(Pe.Status.FREE);
		assertEquals(Pe.Status.FREE, pe.getStatus());
	}

	@Test
	public void testSetPeProvisioner() {
            try{
		createPe(null);
                Assert.fail("An exception has to be thrown when setting a null peProvisioner");
            } catch(Exception e){
            }
		
            try{
		Pe pe = createPe();
                pe.setPeProvisioner(null);
                Assert.fail("An exception has to be thrown when setting a null peProvisioner");
            } catch(Exception e){
            }
            
	}
}
