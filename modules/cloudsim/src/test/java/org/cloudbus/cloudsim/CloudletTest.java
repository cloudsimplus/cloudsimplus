/*
 * Title:        CloudSim Toolkiimport static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;
c) 2009-2010, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.LinkedList;
import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * @author		Anton Beloglazov
 * @since		CloudSim Toolkit 2.0
 */
public class CloudletTest {

	private static final long CLOUDLET_LENGTH = 1000;
	private static final long CLOUDLET_FILE_SIZE = 300;
	private static final long CLOUDLET_OUTPUT_SIZE = 300;

	private static final int PES_NUMBER = 2;

	private Cloudlet cloudlet;
	private UtilizationModel utilizationModelCpu;
	private UtilizationModel utilizationModelRam;
	private UtilizationModel utilizationModelBw;

	@Before
	public void setUp() throws Exception {
		utilizationModelCpu = new UtilizationModelStochastic();
		utilizationModelRam = new UtilizationModelStochastic();
		utilizationModelBw = new UtilizationModelStochastic();
		cloudlet = new Cloudlet(0, CLOUDLET_LENGTH, PES_NUMBER, CLOUDLET_FILE_SIZE, CLOUDLET_OUTPUT_SIZE,
				utilizationModelCpu, utilizationModelRam, utilizationModelBw);
	}

	@Test
	public void testCloudlet() {
		assertEquals(CLOUDLET_LENGTH, cloudlet.getCloudletLength(), 0);
		assertEquals(CLOUDLET_LENGTH * PES_NUMBER, cloudlet.getCloudletTotalLength(), 0);
		assertEquals(CLOUDLET_FILE_SIZE, cloudlet.getCloudletFileSize());
		assertEquals(CLOUDLET_OUTPUT_SIZE, cloudlet.getCloudletOutputSize());
		assertEquals(PES_NUMBER, cloudlet.getNumberOfPes());
		assertSame(utilizationModelCpu, cloudlet.getUtilizationModelCpu());
		assertSame(utilizationModelRam, cloudlet.getUtilizationModelRam());
		assertSame(utilizationModelBw, cloudlet.getUtilizationModelBw());
	}
        
	@Test
	public void testSetNetServiceLevel() {
            final int invalid0 = 0;
            Assert.assertFalse(
                    "Cloudlet.setNetServiceLevel should return false", 
                    cloudlet.setNetServiceLevel(invalid0));

            final int invalidNegative = -1;
            Assert.assertFalse(
                    "Cloudlet.setNetServiceLevel should return false", 
                    cloudlet.setNetServiceLevel(invalidNegative));

            final int valid = 1;
            Assert.assertTrue(
                    "Cloudlet.setNetServiceLevel should return true", 
                    cloudlet.setNetServiceLevel(valid));
        }
        
	@Test
	public void testSetSubmissionTime() {
            Cloudlet c = new Cloudlet(0, 1000, 1, 0, 0, null, null, null);
            
            //Cloudlet has not assigned to a datacenter yet
            Assert.assertFalse(c.setSubmissionTime(1));
            
            //Assign cloudlet to a datacenter
            final int resourceId = 1, cost = 1;
            c.setResourceParameter(resourceId, cost);
            
            Assert.assertTrue(c.setSubmissionTime(1));
        }
        
	@Test
	public void testSetExecParam() {
            Cloudlet c = new Cloudlet(0, 1000, 1, 0, 0, null, null, null);
            
            //Cloudlet has not assigned to a datacenter yet
            Assert.assertFalse(c.setExecParam(1, 2));
            
            //Assign cloudlet to a datacenter
            final int resourceId = 1, cost = 1;
            c.setResourceParameter(resourceId, cost);
            
            Assert.assertTrue(c.setExecParam(1, 2));
        }
        
	@Test
	public void testSetCloudletStatus() {
            Cloudlet c = new Cloudlet(0, 1000, 1, 0, 0, null, null, null);
            c.setCloudletStatus(Cloudlet.Status.CREATED);
            //The status is the same of the current cloudlet status (the request has not effect)
            Assert.assertFalse(c.setCloudletStatus(Cloudlet.Status.CREATED));
            
            //Actually changing to a new status
            Assert.assertTrue(c.setCloudletStatus(Cloudlet.Status.QUEUED));
            
            final Cloudlet.Status newStatus = Cloudlet.Status.CANCELED;
            Assert.assertTrue(c.setCloudletStatus(newStatus));
            assertEquals(newStatus, c.getStatus());
            
            //Trying to change to the same current status (the request has not effect)
            Assert.assertFalse(c.setCloudletStatus(newStatus));
        }

        @Test
	public void testGetCloudletFinishedSoFar() {
            final long length = 1000;
            Cloudlet c = new Cloudlet(0, length, 1, 0, 0, null, null, null);
            
            assertEquals(0, c.getCloudletFinishedSoFar());
            
            final int resourceId = 1, cost = 1;
            c.setResourceParameter(resourceId, cost);
            final long finishedSoFar = length/10;
            c.setCloudletFinishedSoFar(finishedSoFar);
            assertEquals(finishedSoFar, c.getCloudletFinishedSoFar());
            
            c.setCloudletFinishedSoFar(length);
            assertEquals(length, c.getCloudletFinishedSoFar());
        }
        
	@Test
	public void testIsFinished() {
            final long length = 1000;
            Cloudlet c = new Cloudlet(0, length, 1, 0, 0, null, null, null);
            
            Assert.assertFalse(c.isFinished());
            
            final int resourceId = 1, cost = 1;
            c.setResourceParameter(resourceId, cost);
            final long finishedSoFar = length/10;
            c.setCloudletFinishedSoFar(finishedSoFar);
            Assert.assertFalse(c.isFinished());
            
            c.setCloudletFinishedSoFar(length);
            Assert.assertTrue(c.isFinished());
        }        

	@Test
	public void testSetClassType() {
            final int invalid0 = 0;
            Assert.assertFalse(
                    "Cloudlet.setClassType should return false", 
                    cloudlet.setClassType(invalid0));

            final int invalidNegative = -1;
            Assert.assertFalse(
                    "Cloudlet.setClassType should return false", 
                    cloudlet.setClassType(invalidNegative));

            final int valid = 1;
            Assert.assertTrue(
                    "Cloudlet.setClassType should return true", 
                    cloudlet.setClassType(valid));
        }

        @Test
	public void testHasReserved() {
            cloudlet.setReservationId(Cloudlet.NOT_ASSIGNED);
            Assert.assertFalse("Cloudlet.hasReserved should be false", cloudlet.hasReserved());

            final int reservationId = 1;
            cloudlet.setReservationId(reservationId);
            Assert.assertTrue("Cloudlet.hasReserved should be true", cloudlet.hasReserved());
        }
        
	@Test
	public void testGetCloudletStatusString() {
            Cloudlet c = new Cloudlet(0, 0, 0, 0, 0, null, null, null);
            
            c.setCloudletStatus(Cloudlet.Status.CREATED);
            assertEquals("CREATED", c.getCloudletStatusString());

            c.setCloudletStatus(Cloudlet.Status.READY);
            assertEquals("READY", c.getCloudletStatusString());

            c.setCloudletStatus(Cloudlet.Status.INEXEC);
            assertEquals("INEXEC", c.getCloudletStatusString());

            c.setCloudletStatus(Cloudlet.Status.SUCCESS);
            assertEquals("SUCCESS", c.getCloudletStatusString());

            c.setCloudletStatus(Cloudlet.Status.QUEUED);
            assertEquals("QUEUED", c.getCloudletStatusString());

            c.setCloudletStatus(Cloudlet.Status.FAILED);
            assertEquals("FAILED", c.getCloudletStatusString());

            c.setCloudletStatus(Cloudlet.Status.CANCELED);
            assertEquals("CANCELED", c.getCloudletStatusString());

            c.setCloudletStatus(Cloudlet.Status.PAUSED);
            assertEquals("PAUSED", c.getCloudletStatusString());

            c.setCloudletStatus(Cloudlet.Status.RESUMED);
            assertEquals("RESUMED", c.getCloudletStatusString());

            c.setCloudletStatus(Cloudlet.Status.FAILED_RESOURCE_UNAVAILABLE);
            assertEquals("FAILED_RESOURCE_UNAVAILABLE", c.getCloudletStatusString());
	}

        @Test
	public void testGetUtilizationOfCpu() {
		assertEquals(utilizationModelCpu.getUtilization(0), cloudlet.getUtilizationOfCpu(0), 0);
	}

	@Test
	public void testGetUtilizationOfRam() {
		assertEquals(utilizationModelRam.getUtilization(0), cloudlet.getUtilizationOfRam(0), 0);
	}

	@Test
	public void testGetUtilizationOfBw() {
		assertEquals(utilizationModelBw.getUtilization(0), cloudlet.getUtilizationOfBw(0), 0);
	}

	@Test
	public void testCloudletAlternativeConstructor1() {
		cloudlet = new Cloudlet(0, CLOUDLET_LENGTH, PES_NUMBER, CLOUDLET_FILE_SIZE, CLOUDLET_OUTPUT_SIZE,
				utilizationModelCpu, utilizationModelRam, utilizationModelBw, true, new LinkedList<String>());
		testCloudlet();
		testGetUtilizationOfCpu();
		testGetUtilizationOfRam();
		testGetUtilizationOfBw();
	}

	@Test
	public void testCloudletAlternativeConstructor2() {
		cloudlet = new Cloudlet(0, CLOUDLET_LENGTH, PES_NUMBER, CLOUDLET_FILE_SIZE, CLOUDLET_OUTPUT_SIZE,
				utilizationModelCpu, utilizationModelRam, utilizationModelBw, new LinkedList<String>());
		testCloudlet();
		testGetUtilizationOfCpu();
		testGetUtilizationOfRam();
		testGetUtilizationOfBw();
	}

}
