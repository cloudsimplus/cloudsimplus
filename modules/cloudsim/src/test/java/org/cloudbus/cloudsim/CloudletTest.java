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
	public void testGetCloudletStatusString() {
            Cloudlet cloudlet = new Cloudlet(0, 0, 0, 0, 0, null, null, null);
            
            cloudlet.setCloudletStatus(Cloudlet.Status.CREATED);
            assertEquals("CREATED", cloudlet.getCloudletStatusString());

            cloudlet.setCloudletStatus(Cloudlet.Status.READY);
            assertEquals("READY", cloudlet.getCloudletStatusString());

            cloudlet.setCloudletStatus(Cloudlet.Status.INEXEC);
            assertEquals("INEXEC", cloudlet.getCloudletStatusString());

            cloudlet.setCloudletStatus(Cloudlet.Status.SUCCESS);
            assertEquals("SUCCESS", cloudlet.getCloudletStatusString());

            cloudlet.setCloudletStatus(Cloudlet.Status.QUEUED);
            assertEquals("QUEUED", cloudlet.getCloudletStatusString());

            cloudlet.setCloudletStatus(Cloudlet.Status.FAILED);
            assertEquals("FAILED", cloudlet.getCloudletStatusString());

            cloudlet.setCloudletStatus(Cloudlet.Status.CANCELED);
            assertEquals("CANCELED", cloudlet.getCloudletStatusString());

            cloudlet.setCloudletStatus(Cloudlet.Status.PAUSED);
            assertEquals("PAUSED", cloudlet.getCloudletStatusString());

            cloudlet.setCloudletStatus(Cloudlet.Status.RESUMED);
            assertEquals("RESUMED", cloudlet.getCloudletStatusString());

            cloudlet.setCloudletStatus(Cloudlet.Status.FAILED_RESOURCE_UNAVAILABLE);
            assertEquals("FAILED_RESOURCE_UNAVAILABLE", cloudlet.getCloudletStatusString());
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
