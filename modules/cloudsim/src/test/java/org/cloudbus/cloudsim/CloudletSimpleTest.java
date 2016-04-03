/*
 * Title:        CloudSim Toolkiimport static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;
c) 2009-2010, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelStochastic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.LinkedList;
import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author		Anton Beloglazov
 * @since		CloudSim Toolkit 2.0
 */
public class CloudletSimpleTest {

	private static final long CLOUDLET_LENGTH = 1000;
	private static final long CLOUDLET_FILE_SIZE = 300;
	private static final long CLOUDLET_OUTPUT_SIZE = 300;

	private static final int PES_NUMBER = 2;

	private CloudletSimple cloudlet;
	private UtilizationModel utilizationModelCpu;
	private UtilizationModel utilizationModelRam;
	private UtilizationModel utilizationModelBw;

	@Before
	public void setUp() throws Exception {
		utilizationModelCpu = new UtilizationModelStochastic();
		utilizationModelRam = new UtilizationModelStochastic();
		utilizationModelBw = new UtilizationModelStochastic();
		cloudlet = new CloudletSimple(0, CLOUDLET_LENGTH, PES_NUMBER, CLOUDLET_FILE_SIZE, CLOUDLET_OUTPUT_SIZE,
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
        
        private CloudletSimple createCloudlet(){
            final UtilizationModel cpuRamAndBwUtilizationModel = new UtilizationModelFull();
            return createCloudlet(cpuRamAndBwUtilizationModel);
        }
        
        private CloudletSimple createCloudlet(UtilizationModel cpuRamAndBwUtilizationModel){
            return new CloudletSimple(0, 1000, 1, 0, 0, 
                    cpuRamAndBwUtilizationModel, 
                    cpuRamAndBwUtilizationModel, 
                    cpuRamAndBwUtilizationModel
            );
        }

        private CloudletSimple createCloudlet(UtilizationModel utilizationModelCPU,
                UtilizationModel utilizationModelRAM,
                UtilizationModel utilizationModelBW){
            return new CloudletSimple(0, 1000, 1, 0, 0, 
                    utilizationModelCPU, 
                    utilizationModelRAM, 
                    utilizationModelBW
            );
        }

        @Test
	public void testUtilizationModels() {
            try{
                createCloudlet(null);
                Assert.fail("An exception must be thrown for trying to set a null utilization model");
            }catch (Exception e){
            } 

            CloudletSimple c = createCloudlet();
            Assert.assertNotNull(c.getUtilizationModelCpu());
            Assert.assertNotNull(c.getUtilizationModelRam());
            Assert.assertNotNull(c.getUtilizationModelBw());
            
            try{
                c.setUtilizationModelCpu(null);
                Assert.fail("An exception must be thrown for trying to set a null CPU utilization model");
            }catch (Exception e){
            } 

            try{
                c.setUtilizationModelRam(null);
                Assert.fail("An exception must be thrown for trying to set a null RAM utilization model");
            }catch (Exception e){
            } 

            try{
                c.setUtilizationModelBw(null);
                Assert.fail("An exception must be thrown for trying to set a null BW utilization model");
            }catch (Exception e){
            } 
        }
        
        @Test
	public void testSetSubmissionTime() {
            CloudletSimple c = createCloudlet();
            
            //Cloudlet has not assigned to a datacenter yet
            Assert.assertFalse(c.setSubmissionTime(1));
            
            //Assign cloudlet to a datacenter
            final int resourceId = 1, cost = 1;
            c.setResourceParameter(resourceId, cost);
            
            Assert.assertTrue(c.setSubmissionTime(1));
        }
        
	@Test
	public void testSetExecParam() {
            CloudletSimple c = createCloudlet();
            
            //Cloudlet has not assigned to a datacenter yet
            Assert.assertFalse(c.setExecParam(1, 2));
            
            //Assign cloudlet to a datacenter
            final int resourceId = 1, cost = 1;
            c.setResourceParameter(resourceId, cost);
            
            Assert.assertTrue(c.setExecParam(1, 2));
        }
        
	@Test
	public void testSetCloudletStatus() {
            CloudletSimple c = createCloudlet();
            c.setCloudletStatus(CloudletSimple.Status.CREATED);
            //The status is the same of the current cloudlet status (the request has not effect)
            Assert.assertFalse(c.setCloudletStatus(CloudletSimple.Status.CREATED));
            
            //Actually changing to a new status
            Assert.assertTrue(c.setCloudletStatus(CloudletSimple.Status.QUEUED));
            
            final CloudletSimple.Status newStatus = CloudletSimple.Status.CANCELED;
            Assert.assertTrue(c.setCloudletStatus(newStatus));
            assertEquals(newStatus, c.getStatus());
            
            //Trying to change to the same current status (the request has not effect)
            Assert.assertFalse(c.setCloudletStatus(newStatus));
        }

	@Test
	public void testAddRequiredFile() {
            CloudletSimple c = createCloudlet();
            final String files[] = {"file1.txt", "file2.txt"};
            for(String file: files){
                Assert.assertTrue("Method file should be added", 
                        c.addRequiredFile(file));  //file doesn't previously added
                Assert.assertFalse("Method file shouldn't be added", 
                        c.addRequiredFile(file)); //file already added
            }
        }

	@Test
	public void testDeleteRequiredFile() {
            CloudletSimple c = createCloudlet();
            final String files[] = {"file1.txt", "file2.txt", "file3.txt"};
            for(String file: files){
                 c.addRequiredFile(file);  
            }
            
            Assert.assertFalse(c.deleteRequiredFile("file-inexistent.txt"));
            for(String file: files){
                 Assert.assertTrue(c.deleteRequiredFile(file));
                 Assert.assertFalse(c.deleteRequiredFile(file)); //already deleted
            }
        }

	@Test
	public void testRequiredFiles() {
            CloudletSimple c = createCloudlet();
            final String files[] = {"file1.txt", "file2.txt", "file3.txt"};
            c.setRequiredFiles(null); //internally it has to creates a new instance
            Assert.assertNotNull(c.getRequiredFiles());   
            
            for(String file: files){
                 c.addRequiredFile(file);  
            }
            
            Assert.assertTrue(c.requiresFiles()); //it has required files
        }

        @Test
	public void testGetCloudletFinishedSoFar() {
            final long length = 1000;
            CloudletSimple c = createCloudlet();
            
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
            CloudletSimple c = createCloudlet();
            
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
            cloudlet.setReservationId(CloudletSimple.NOT_ASSIGNED);
            Assert.assertFalse("Cloudlet.hasReserved should be false", cloudlet.hasReserved());

            final int reservationId = 1;
            cloudlet.setReservationId(reservationId);
            Assert.assertTrue("Cloudlet.hasReserved should be true", cloudlet.hasReserved());
        }
        
	@Test
	public void testGetCloudletStatusString() {
            CloudletSimple c = createCloudlet();
            
            c.setCloudletStatus(CloudletSimple.Status.CREATED);
            assertEquals("CREATED", c.getCloudletStatusString());

            c.setCloudletStatus(CloudletSimple.Status.READY);
            assertEquals("READY", c.getCloudletStatusString());

            c.setCloudletStatus(CloudletSimple.Status.INEXEC);
            assertEquals("INEXEC", c.getCloudletStatusString());

            c.setCloudletStatus(CloudletSimple.Status.SUCCESS);
            assertEquals("SUCCESS", c.getCloudletStatusString());

            c.setCloudletStatus(CloudletSimple.Status.QUEUED);
            assertEquals("QUEUED", c.getCloudletStatusString());

            c.setCloudletStatus(CloudletSimple.Status.FAILED);
            assertEquals("FAILED", c.getCloudletStatusString());

            c.setCloudletStatus(CloudletSimple.Status.CANCELED);
            assertEquals("CANCELED", c.getCloudletStatusString());

            c.setCloudletStatus(CloudletSimple.Status.PAUSED);
            assertEquals("PAUSED", c.getCloudletStatusString());

            c.setCloudletStatus(CloudletSimple.Status.RESUMED);
            assertEquals("RESUMED", c.getCloudletStatusString());

            c.setCloudletStatus(CloudletSimple.Status.FAILED_RESOURCE_UNAVAILABLE);
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
		cloudlet = new CloudletSimple(0, CLOUDLET_LENGTH, PES_NUMBER, CLOUDLET_FILE_SIZE, CLOUDLET_OUTPUT_SIZE,
				utilizationModelCpu, utilizationModelRam, utilizationModelBw, true, new LinkedList<String>());
		testCloudlet();
		testGetUtilizationOfCpu();
		testGetUtilizationOfRam();
		testGetUtilizationOfBw();
	}

	@Test
	public void testCloudletAlternativeConstructor2() {
		cloudlet = new CloudletSimple(0, CLOUDLET_LENGTH, PES_NUMBER, CLOUDLET_FILE_SIZE, CLOUDLET_OUTPUT_SIZE,
				utilizationModelCpu, utilizationModelRam, utilizationModelBw, new LinkedList<String>());
		testCloudlet();
		testGetUtilizationOfCpu();
		testGetUtilizationOfRam();
		testGetUtilizationOfBw();
	}

}
