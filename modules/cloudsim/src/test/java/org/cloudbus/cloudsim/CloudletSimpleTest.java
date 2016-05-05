/*
 * Title:        CloudSim Toolkiimport static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;
c) 2009-2010, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim;

import java.util.ArrayList;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelStochastic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;

import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.listeners.EventListener;
import org.cloudbus.cloudsim.listeners.CloudletInsideVmEventInfo;
import org.easymock.EasyMock;
import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author	Anton Beloglazov
 * @since	CloudSim Toolkit 2.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CloudSim.class}) //to intercept and mock static method calls
public class CloudletSimpleTest {

    private static final long CLOUDLET_LENGTH = 1000;
    private static final long CLOUDLET_FILE_SIZE = 1000;
    private static final int CLOUDLET_OUTPUT_SIZE = 1000;

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
        cloudlet = new CloudletSimple(
                0, CLOUDLET_LENGTH, PES_NUMBER,
                CLOUDLET_FILE_SIZE, CLOUDLET_OUTPUT_SIZE,
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
    public void testSetOnCloudletFinishEventListener() {
        cloudlet.setOnCloudletFinishEventListener(null);
        assertEquals(EventListener.NULL, cloudlet.getOnCloudletFinishEventListener());
        EventListener<CloudletInsideVmEventInfo> listener = (evt) -> {};
        cloudlet.setOnCloudletFinishEventListener(listener);
        assertEquals(listener, cloudlet.getOnCloudletFinishEventListener());
    }

    @Test
    public void testGetWaitingTime() {
        CloudletSimple cloudlet = createCloudlet();
        assertEquals(0, cloudlet.getWaitingTime(), 0);
        cloudlet.assignCloudletToDatacenter(0, 0);
        final int submissionTime = 0, execStartTime = 10;
        final int expectedWaitingTime = execStartTime - submissionTime;
        cloudlet.setSubmissionTime(submissionTime);
        cloudlet.setExecStartTime(execStartTime);
        assertEquals(expectedWaitingTime, cloudlet.getWaitingTime(), 0);
    }
    
    @Test
    public void testAssignCloudletToDataCenter_recodLogEnabledDatacenterNotAssigned() {
        final int datacenterId = 0;
        CloudletSimple cloudlet = createCloudlet(datacenterId, true);
        cloudlet.assignCloudletToDatacenter(datacenterId, 0);
        assertEquals(datacenterId, cloudlet.getDatacenterId());
    }

    @Test
    public void testAssignCloudletToDataCenter_recodLogEnabledDatacenterAlreadAssigned() {
        CloudletSimple cloudlet = createCloudlet(0, true);
        cloudlet.assignCloudletToDatacenter(0, 0);

        final int datacenterId = 1;
        cloudlet.assignCloudletToDatacenter(datacenterId, 0);
        assertEquals(datacenterId, cloudlet.getDatacenterId());
    }

    @Test
    public void testGetExecStartTime() {
        CloudletSimple cloudlet = createCloudlet();
        assertEquals(0, cloudlet.getExecStartTime(), 0);

        cloudlet.assignCloudletToDatacenter(0, 0);
        final int submissionTime = 0, execStartTime = 10;
        cloudlet.setSubmissionTime(submissionTime);
        cloudlet.setExecStartTime(execStartTime);
        assertEquals(execStartTime, cloudlet.getExecStartTime(), 0);
    }

    @Test
    public void testGetSubmissionTime() {
        CloudletSimple cloudlet = createCloudlet();
        assertEquals(0, cloudlet.getSubmissionTime(), 0);

        cloudlet.assignCloudletToDatacenter(0, 0);
        final int submissionTime = 1;
        cloudlet.setSubmissionTime(submissionTime);
        assertEquals(submissionTime, cloudlet.getSubmissionTime(), 0);
    }

    @Test
    public void testGetWallClockTime() {
        CloudletSimple cloudlet = createCloudlet();
        assertEquals(0, cloudlet.getWallClockTimeInLastExecutedDatacenter(), 0);

        cloudlet.assignCloudletToDatacenter(0, 0);
        final int submissionTime = 0, execStartTime = 10;
        cloudlet.setSubmissionTime(submissionTime);
        cloudlet.setExecStartTime(execStartTime);
        final int wallClockTime = execStartTime + 20;
        cloudlet.setWallClockTime(wallClockTime, wallClockTime);
        assertEquals(wallClockTime, cloudlet.getWallClockTimeInLastExecutedDatacenter(), 0);
    }

    @Test
    public void testGetActualCPUTime() {
        final double submissionTime = 0, execStartTime = 10;
        final double simulationClock = 100;
        final double actualCpuTime = simulationClock - execStartTime;
        final int datacenterId = 0;
        //This will mock the CloudSim static method calls
        PowerMock.mockStatic(CloudSim.class);

        EasyMock.expect(CloudSim.clock()).andReturn(simulationClock);
        EasyMock.expect(CloudSim.getEntityName(datacenterId)).andReturn("datacenter" + datacenterId);
        PowerMock.replay(CloudSim.class);

        CloudletSimple cloudlet = createCloudlet();
        assertEquals(Cloudlet.NOT_ASSIGNED, cloudlet.getActualCPUTime(), 0);

        cloudlet.assignCloudletToDatacenter(datacenterId, 0);
        cloudlet.setSubmissionTime(submissionTime);
        cloudlet.setExecStartTime(execStartTime);
        cloudlet.setCloudletStatus(Cloudlet.Status.SUCCESS);
        assertEquals(actualCpuTime, cloudlet.getActualCPUTime(), 0);

        PowerMock.verify(CloudSim.class);
    }

    @Test
    public void testGetProcessingCost() {
        Cloudlet cloudlet = createCloudlet();
        final double costPerCpuSec = 4, costPerByteOfBw = 2;
        final double inputTransferCost = CLOUDLET_FILE_SIZE * costPerByteOfBw;
        final double outputTransferCost = CLOUDLET_OUTPUT_SIZE * costPerByteOfBw;

        /**
         * @todo @author manoelcampos Actually the cpu cost it not being
         * computed by the getProcessingCost() method.
         */
        final double cpuCost = 0.0;

        final double totalCost = inputTransferCost + cpuCost + outputTransferCost;
        cloudlet.assignCloudletToDatacenter(0, costPerCpuSec, costPerByteOfBw);
        assertEquals(totalCost, cloudlet.getProcessingCost(), 0);
    }

    @Test
    public void testGetClassType() {
        final int expected = 8;
        cloudlet.setClassType(expected);
        assertEquals(expected, cloudlet.getClassType(), 0);
    }

    @Test
    public void testGetCloudletHistory() {
        final int id = 1;
        Cloudlet cloudlet = createCloudlet(id, false);
        final String expected = String.format(Cloudlet.NO_HISTORY_IS_RECORDED_FOR_CLOUDLET, id);
        assertEquals(expected, cloudlet.getCloudletHistory());
        cloudlet.setUserId(1);
        assertEquals(expected, cloudlet.getCloudletHistory());

        cloudlet = createCloudlet(id, true);
        cloudlet.setUserId(1);
        Assert.assertNotSame(expected, cloudlet.getCloudletHistory());
    }

    @Test
    public void testSetCloudletFinishedSoFar() {
        CloudletSimple cloudlet = createCloudlet();
        assertEquals(0, cloudlet.getCloudletFinishedSoFar(), 0);

        cloudlet.assignCloudletToDatacenter(0, 0);
        final long cloudletFinishedSoFar = cloudlet.getCloudletLength() / 2;
        Assert.assertTrue(cloudlet.setCloudletFinishedSoFar(cloudletFinishedSoFar));
        assertEquals(cloudletFinishedSoFar, cloudlet.getCloudletFinishedSoFar(), 0);
        Assert.assertFalse(cloudlet.setCloudletFinishedSoFar(-1));
        assertEquals(cloudletFinishedSoFar, cloudlet.getCloudletFinishedSoFar(), 0);
    }

    @Test
    public void testGetDatacenterId() {
        CloudletSimple cloudlet = createCloudlet(0, true);
        assertEquals(Cloudlet.NOT_ASSIGNED, cloudlet.getDatacenterId(), 0);

        final int datacenterId = 0;
        cloudlet.assignCloudletToDatacenter(datacenterId, 0);
        assertEquals(datacenterId, cloudlet.getDatacenterId(), 0);
    }

    @Test
    public void testGetCostPerSec() {
        CloudletSimple cloudlet = createCloudlet();
        assertEquals(0, cloudlet.getCostPerSec(), 0);

        final double cost = 1;
        cloudlet.assignCloudletToDatacenter(0, cost);
        assertEquals(cost, cloudlet.getCostPerSec(), 0);
    }

    @Test
    public void testSetReservationId() {
        int expected = -1;
        assertEquals(expected, cloudlet.getReservationId());

        expected = 5;
        Assert.assertTrue(cloudlet.setReservationId(expected));
        Assert.assertEquals(expected, cloudlet.getReservationId());

        Assert.assertFalse(cloudlet.setReservationId(-1));
        Assert.assertEquals(expected, cloudlet.getReservationId());
    }

    @Test
    public void testSetCloudletLength() {
        int expected = 1000;
        Assert.assertTrue(cloudlet.setCloudletLength(expected));
        Assert.assertEquals(expected, cloudlet.getCloudletLength());

        Assert.assertFalse(cloudlet.setCloudletLength(0));
        Assert.assertEquals(expected, cloudlet.getCloudletLength());
        Assert.assertFalse(cloudlet.setCloudletLength(-1));
        Assert.assertEquals(expected, cloudlet.getCloudletLength());

        expected = 2000;
        Assert.assertTrue(cloudlet.setCloudletLength(expected));
        Assert.assertEquals(expected, cloudlet.getCloudletLength());
    }

    @Test
    public void testSetNumberOfPes() {
        int expected = 2;
        Assert.assertTrue(cloudlet.setNumberOfPes(expected));
        Assert.assertEquals(expected, cloudlet.getNumberOfPes());

        Assert.assertFalse(cloudlet.setNumberOfPes(0));
        Assert.assertEquals(expected, cloudlet.getNumberOfPes());
        Assert.assertFalse(cloudlet.setNumberOfPes(-1));
        Assert.assertEquals(expected, cloudlet.getNumberOfPes());

        expected = 4;
        Assert.assertTrue(cloudlet.setNumberOfPes(expected));
        Assert.assertEquals(expected, cloudlet.getNumberOfPes());
    }

    @Test
    public void testSetRequiredFiles() {
        cloudlet.setRequiredFiles(null);
        Assert.assertNotNull(cloudlet.getRequiredFiles());

        List<String> files = new ArrayList<>();
        files.add("file1.txt");
        cloudlet.setRequiredFiles(files);
        assertEquals(files, cloudlet.getRequiredFiles());
    }

    @Test
    public void testSetNetServiceLevel() {
        int valid = 1;
        Assert.assertTrue(
                "Cloudlet.setNetServiceLevel should return true",
                cloudlet.setNetServiceLevel(valid));
        assertEquals(valid, cloudlet.getNetServiceLevel());

        final int invalid0 = 0;
        Assert.assertFalse(
                "Cloudlet.setNetServiceLevel should return false",
                cloudlet.setNetServiceLevel(invalid0));
        assertEquals(valid, cloudlet.getNetServiceLevel());

        final int invalidNegative = -1;
        Assert.assertFalse(
                "Cloudlet.setNetServiceLevel should return false",
                cloudlet.setNetServiceLevel(invalidNegative));
        assertEquals(valid, cloudlet.getNetServiceLevel());

        valid = 2;
        Assert.assertTrue(
                "Cloudlet.setNetServiceLevel should return true",
                cloudlet.setNetServiceLevel(valid));
        assertEquals(valid, cloudlet.getNetServiceLevel());
    }

    private static CloudletSimple createCloudlet() {
        return createCloudlet(0);
    }

    public static CloudletSimple createCloudlet(final int id) {
        return createCloudlet(id, false);
    }

    private static CloudletSimple createCloudlet(final int id, boolean recordLog) {
        final UtilizationModel cpuRamAndBwUtilizationModel = new UtilizationModelFull();
        return createCloudlet(id, cpuRamAndBwUtilizationModel, recordLog);
    }

    private static CloudletSimple createCloudlet(
            final int id,
            UtilizationModel cpuRamAndBwUtilizationModel, boolean recordLog) {
        return createCloudlet(id, cpuRamAndBwUtilizationModel,
                cpuRamAndBwUtilizationModel,
                cpuRamAndBwUtilizationModel, recordLog);
    }

    private static CloudletSimple createCloudlet(final int id, UtilizationModel utilizationModelCPU,
            UtilizationModel utilizationModelRAM,
            UtilizationModel utilizationModelBW,
            boolean recordLog) {
        return new CloudletSimple(
                id, CLOUDLET_LENGTH, 1, CLOUDLET_FILE_SIZE, CLOUDLET_OUTPUT_SIZE,
                utilizationModelCPU, utilizationModelRAM, utilizationModelBW,
                recordLog
        );
    }

    @Test
    public void testSetUtilizationModels() {
        CloudletSimple c = createCloudlet();
        Assert.assertNotNull(c.getUtilizationModelCpu());
        Assert.assertNotNull(c.getUtilizationModelRam());
        Assert.assertNotNull(c.getUtilizationModelBw());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetUtilizationModelBw_null() {
        CloudletSimple c = createCloudlet();
        c.setUtilizationModelBw(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetUtilizationModelRam_null() {
        CloudletSimple c = createCloudlet();
        c.setUtilizationModelRam(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetUtilizationModelCpu_null() {
        CloudletSimple c = createCloudlet();
        c.setUtilizationModelCpu(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNew_nullUtilizationModel() {
        createCloudlet(0, null, false);
    }

    @Test
    public void testSetSubmissionTime() {
        CloudletSimple c = createCloudlet();

        //Cloudlet has not assigned to a datacenter yet
        Assert.assertFalse(c.setSubmissionTime(1));

        //Assign cloudlet to a datacenter
        final int resourceId = 1, cost = 1;
        c.assignCloudletToDatacenter(resourceId, cost);

        Assert.assertTrue(c.setSubmissionTime(1));
    }

    @Test
    public void testSetExecParam() {
        CloudletSimple c = createCloudlet();

        //Cloudlet has not assigned to a datacenter yet
        Assert.assertFalse(c.setWallClockTime(1, 2));

        //Assign cloudlet to a datacenter
        final int resourceId = 1, cost = 1;
        c.assignCloudletToDatacenter(resourceId, cost);

        Assert.assertTrue(c.setWallClockTime(1, 2));
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
        for (String file : files) {
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
        for (String file : files) {
            c.addRequiredFile(file);
        }

        Assert.assertFalse(c.deleteRequiredFile("file-inexistent.txt"));
        for (String file : files) {
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

        for (String file : files) {
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
        c.assignCloudletToDatacenter(resourceId, cost);
        final long finishedSoFar = length / 10;
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
        c.assignCloudletToDatacenter(resourceId, cost);
        final long finishedSoFar = length / 10;
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
        cloudlet
                = new CloudletSimple(
                        0, CLOUDLET_LENGTH, PES_NUMBER, CLOUDLET_FILE_SIZE, CLOUDLET_OUTPUT_SIZE,
                        utilizationModelCpu, utilizationModelRam, utilizationModelBw,
                        true, new LinkedList<>());
        testCloudlet();
        testGetUtilizationOfCpu();
        testGetUtilizationOfRam();
        testGetUtilizationOfBw();
    }

    @Test
    public void testCloudletAlternativeConstructor2() {
        cloudlet = new CloudletSimple(
                0, CLOUDLET_LENGTH, PES_NUMBER, CLOUDLET_FILE_SIZE, CLOUDLET_OUTPUT_SIZE,
                utilizationModelCpu, utilizationModelRam, utilizationModelBw,
                new LinkedList<>());
        testCloudlet();
        testGetUtilizationOfCpu();
        testGetUtilizationOfRam();
        testGetUtilizationOfBw();
    }

}
