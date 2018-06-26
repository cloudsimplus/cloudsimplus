/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.cloudlets;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterMocker;
import org.cloudbus.cloudsim.mocks.CloudSimMocker;
import org.cloudbus.cloudsim.mocks.Mocks;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelStochastic;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author	Anton Beloglazov
 * @since	CloudSim Toolkit 2.0
 */
public class CloudletSimpleTest {
    public static final long CLOUDLET_LENGTH = 1000;
    public static final long CLOUDLET_FILE_SIZE = 1000;
    public static final int CLOUDLET_OUTPUT_SIZE = 1000;

    public static final int PES_NUMBER = 2;

    private CloudletSimple cloudlet;
    private UtilizationModel utilizationModelCpu;
    private UtilizationModel utilizationModelRam;
    private UtilizationModel utilizationModelBw;

    @Before
    public void setUp() {
        utilizationModelCpu = new UtilizationModelStochastic();
        utilizationModelRam = new UtilizationModelStochastic();
        utilizationModelBw = new UtilizationModelStochastic();
        cloudlet = new CloudletSimple(0, CLOUDLET_LENGTH, PES_NUMBER);
        cloudlet.setFileSize(CLOUDLET_FILE_SIZE)
                .setOutputSize(CLOUDLET_OUTPUT_SIZE)
                .setUtilizationModelCpu(utilizationModelCpu)
                .setUtilizationModelRam(utilizationModelRam)
                .setUtilizationModelBw(utilizationModelBw);
    }

    @Test
    public void testCloudlet() {
        assertEquals(CLOUDLET_LENGTH, cloudlet.getLength(), 0);
        assertEquals(CLOUDLET_LENGTH * PES_NUMBER, cloudlet.getTotalLength(), 0);
        assertEquals(CLOUDLET_FILE_SIZE, cloudlet.getFileSize());
        assertEquals(CLOUDLET_OUTPUT_SIZE, cloudlet.getOutputSize());
        assertEquals(PES_NUMBER, cloudlet.getNumberOfPes());
        assertSame(utilizationModelCpu, cloudlet.getUtilizationModelCpu());
        assertSame(utilizationModelRam, cloudlet.getUtilizationModelRam());
        assertSame(utilizationModelBw, cloudlet.getUtilizationModelBw());
    }

    @Test
    public void testAddOnCloudletFinishEventListener() {
        final EventListener<CloudletVmEventInfo> listener = (info) -> {};
        cloudlet.addOnFinishListener(listener);
        assertTrue(cloudlet.removeOnFinishListener(listener));
    }

    @Test(expected = NullPointerException.class)
    public void testAddOnCloudletFinishEventListener_Null() {
        cloudlet.addOnFinishListener(null);
    }

    @Test
    public void testRemoveOnCloudletFinishEventListener() {
        final EventListener<CloudletVmEventInfo> listener = (info) -> {};
        cloudlet.addOnFinishListener(listener);
        assertTrue(cloudlet.removeOnFinishListener(listener));
    }

    @Test
    public void testRemoveOnCloudletFinishEventListener_Null() {
        cloudlet.addOnFinishListener(e->{});
        assertFalse(cloudlet.removeOnFinishListener(null));
    }

    @Test
    public void testGetWaitingTime() {
        final double arrivalTime = 0.0, execStartTime = 10.0;
        final CloudSim cloudsim = CloudSimMocker.createMock(mocker -> {
            mocker.clock(arrivalTime);
        });

        final CloudletSimple cloudlet = createCloudlet();
        cloudlet.setBroker(Mocks.createMockBroker(cloudsim));
        assertEquals(0, cloudlet.getWaitingTime(), 0);
        cloudlet.assignToDatacenter(Datacenter.NULL);
        final double expectedWaitingTime = execStartTime - arrivalTime;
        cloudlet.registerArrivalInDatacenter();
        cloudlet.setExecStartTime(execStartTime);
        assertEquals(expectedWaitingTime, cloudlet.getWaitingTime(), 0);
    }

    @Test
    public void testAssignCloudletToDataCenter_recodLogEnabledDatacenterNotAssigned() {
        final CloudletSimple cloudlet = createCloudlet(0);
        cloudlet.assignToDatacenter(Datacenter.NULL);
        assertEquals(Datacenter.NULL, cloudlet.getLastDatacenter());
    }

    @Test
    public void testAssignCloudletToDataCenter_recodLogEnabledDatacenterAlreadAssigned() {
        final CloudletSimple cloudlet = createCloudlet(0);
        cloudlet.assignToDatacenter(Datacenter.NULL);

        cloudlet.assignToDatacenter(Datacenter.NULL);
        assertEquals(Datacenter.NULL, cloudlet.getLastDatacenter());
    }

    @Test
    public void testGetExecStartTime() {
        final CloudletSimple cloudlet = createCloudlet();
        assertEquals(0, cloudlet.getExecStartTime(), 0);

        cloudlet.assignToDatacenter(Datacenter.NULL);
        final int execStartTime = 10;
        cloudlet.registerArrivalInDatacenter();
        cloudlet.setExecStartTime(execStartTime);
        assertEquals(execStartTime, cloudlet.getExecStartTime(), 0);
    }

    @Test
    public void testGetDatacenterArrivalTime() {
        final double submissionTime = 1;
        final CloudSim cloudsim = CloudSimMocker.createMock(mocker -> {
            mocker.clock(submissionTime);
        });

        final CloudletSimple cloudlet = createCloudlet();
        cloudlet.setBroker(Mocks.createMockBroker(cloudsim));
        assertEquals(Cloudlet.NOT_ASSIGNED, cloudlet.getLastDatacenterArrivalTime(), 0);

        cloudlet.assignToDatacenter(Datacenter.NULL);
        cloudlet.registerArrivalInDatacenter();
        assertEquals(submissionTime, cloudlet.getLastDatacenterArrivalTime(), 0);
    }

    @Test
    public void testGetWallClockTime() {
        final CloudletSimple cloudlet = createCloudlet();
        assertEquals(0, cloudlet.getWallClockTimeInLastExecutedDatacenter(), 0);

        cloudlet.assignToDatacenter(Datacenter.NULL);
        final double arrivalTime = 0.0, execStartTime = 10.0;
        CloudSimMocker.createMock(mocker -> mocker.clock(arrivalTime));

        cloudlet.registerArrivalInDatacenter();
        cloudlet.setExecStartTime(execStartTime);
        final double wallClockTime = execStartTime + 20.0;
        cloudlet.setWallClockTime(wallClockTime, wallClockTime);
        assertEquals(wallClockTime, cloudlet.getWallClockTimeInLastExecutedDatacenter(), 0);
    }

    @Test
    public void testGetActualCPUTime() {
        final double submissionTime = 0, execStartTime = 10;
        final double simulationClock = 100;
        final double actualCpuTime = simulationClock - execStartTime;

        final CloudSim cloudsim = CloudSimMocker.createMock(mocker -> {
            mocker.clock(submissionTime);
            mocker.clock(simulationClock);
        });

        final CloudletSimple cloudlet = createCloudlet();
        cloudlet.setBroker(Mocks.createMockBroker(cloudsim));
        assertEquals(Cloudlet.NOT_ASSIGNED, cloudlet.getActualCpuTime(), 0);

        cloudlet.assignToDatacenter(Datacenter.NULL);
        cloudlet.registerArrivalInDatacenter();
        cloudlet.setExecStartTime(execStartTime);
        cloudlet.setStatus(Cloudlet.Status.SUCCESS);
        assertEquals(actualCpuTime, cloudlet.getActualCpuTime(), 0);

        EasyMock.verify(cloudsim);
    }

    @Test
    public void testGetProcessingCost() {
        final double costPerCpuSec = 4, costPerByteOfBw = 2;
        final Datacenter dc = DatacenterMocker.createMock(mocker -> {
            mocker.getCharacteristics().times(2);
            mocker.getCostPerSecond(costPerCpuSec).once();
            mocker.getCostPerBw(costPerByteOfBw).once();
        });

        final Cloudlet cloudlet = createCloudlet(0, 10000, 2);
        final double inputTransferCost = CLOUDLET_FILE_SIZE * costPerByteOfBw;
        final double outputTransferCost = CLOUDLET_OUTPUT_SIZE * costPerByteOfBw;

        final double cpuCost = 40;
        final double totalCost = inputTransferCost + cpuCost + outputTransferCost;
        cloudlet.assignToDatacenter(dc);
        cloudlet.setWallClockTime(10, 10);
        assertEquals(totalCost, cloudlet.getTotalCost(), 0);
    }

    @Test
    public void testGetPriority() {
        final int expected = 8;
        cloudlet.setPriority(expected);
        assertEquals(expected, cloudlet.getPriority(), 0);
    }

    @Test
    public void testAddCloudletFinishedSoFar() {
        final CloudletSimple cloudlet = createCloudlet();
        assertEquals(0, cloudlet.getFinishedLengthSoFar(), 0);

        cloudlet.assignToDatacenter(Datacenter.NULL);
        final long cloudletFinishedSoFar = cloudlet.getLength() / 2;
        assertTrue(cloudlet.addFinishedLengthSoFar(cloudletFinishedSoFar));
        assertEquals(cloudletFinishedSoFar, cloudlet.getFinishedLengthSoFar(), 0);
        assertFalse(cloudlet.addFinishedLengthSoFar(-1));
        assertEquals(cloudletFinishedSoFar, cloudlet.getFinishedLengthSoFar(), 0);
    }

    @Test
    public void testAddCloudletFinishedSoFar_WhenValueIsLowerThanLen() {
        final CloudletSimple cloudlet = createCloudlet();
        cloudlet.assignToDatacenter(Datacenter.NULL);
        final long cloudletFinishedSoFar = cloudlet.getLength() / 2;
        assertTrue(cloudlet.addFinishedLengthSoFar(cloudletFinishedSoFar));
        assertEquals(cloudletFinishedSoFar, cloudlet.getFinishedLengthSoFar(), 0);
    }

    @Test
    public void testAddCloudletFinishedSoFar_WhenValueIsHigherThanLen() {
        final CloudletSimple cloudlet = createCloudlet();
        cloudlet.assignToDatacenter(Datacenter.NULL);
        final long cloudletFinishedSoFar = cloudlet.getLength() / 2;
        cloudlet.addFinishedLengthSoFar(cloudletFinishedSoFar);
        cloudlet.addFinishedLengthSoFar(cloudletFinishedSoFar*3);
        assertEquals(cloudlet.getLength(), cloudlet.getFinishedLengthSoFar(), 0);
    }

    @Test
    public void testAddCloudletFinishedSoFar_lengthParamGreaterThanCloudletLength() {
        final CloudletSimple cloudlet = createCloudlet();
        final long expected = cloudlet.getLength();
        cloudlet.addFinishedLengthSoFar(expected*2);
        assertEquals(expected, cloudlet.getLength(), 0);
    }

    @Test
    public void testGetDatacenterId() {
        final CloudletSimple cloudlet = createCloudlet(0);
        assertEquals(Datacenter.NULL, cloudlet.getLastDatacenter());

        cloudlet.assignToDatacenter(Datacenter.NULL);
        assertEquals(Datacenter.NULL, cloudlet.getLastDatacenter());
    }

    @Test
    public void testGetCostPerSec() {
        final CloudletSimple cloudlet = createCloudlet();
        assertEquals(0, cloudlet.getCostPerSec(), 0);

        cloudlet.assignToDatacenter(Datacenter.NULL);
        assertEquals(0, cloudlet.getCostPerSec(), 0);
    }

    @Test
    public void testSetValidCloudletLength() {
        final int expected = 1000;
        cloudlet.setLength(expected);
        assertEquals(expected, cloudlet.getLength());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetCloudletLengthToZero() {
        cloudlet.setLength(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetCloudletLengthToNegative() {
        cloudlet.setLength(-1);
    }

    @Test
    public void testSetValidNumberOfPes() {
        final int expected = 2;
        cloudlet.setNumberOfPes(expected);
        assertEquals(expected, cloudlet.getNumberOfPes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNumberOfPesToZero() {
        cloudlet.setNumberOfPes(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNumberOfPesToNegative() {
        cloudlet.setNumberOfPes(-1);
    }

    @Test
    public void testSetNetServiceLevel() {
        int valid = 1;
        final String trueMsg = "Cloudlet.setNetServiceLevel should return true";
        final String falseMsg = "Cloudlet.setNetServiceLevel should return false";

        assertTrue(trueMsg, cloudlet.setNetServiceLevel(valid));
        assertEquals(valid, cloudlet.getNetServiceLevel());

        final int invalid0 = 0;
        assertFalse(falseMsg, cloudlet.setNetServiceLevel(invalid0));
        assertEquals(valid, cloudlet.getNetServiceLevel());

        final int invalidNegative = -1;
        assertFalse(falseMsg, cloudlet.setNetServiceLevel(invalidNegative));
        assertEquals(valid, cloudlet.getNetServiceLevel());

        valid = 2;
        assertTrue(trueMsg, cloudlet.setNetServiceLevel(valid));
        assertEquals(valid, cloudlet.getNetServiceLevel());
    }

    static CloudletSimple createCloudlet() {
        return createCloudlet(0);
    }

    static CloudletSimple createCloudlet(final int id) {
        return createCloudlet(id, new UtilizationModelFull());
    }

    private static CloudletSimple createCloudlet(
        final int id, UtilizationModel cpuRamAndBwUtilizationModel) {
        return createCloudlet(id, cpuRamAndBwUtilizationModel,
                cpuRamAndBwUtilizationModel,
                cpuRamAndBwUtilizationModel);
    }

    public static CloudletSimple createCloudlet(
        final int id, long length, int numberOfPes) {
        final UtilizationModel um = new UtilizationModelFull();
        return createCloudlet(id, um, um, um, length, numberOfPes);
    }

    private static CloudletSimple createCloudlet(final int id,
            UtilizationModel utilizationModelCPU,
            UtilizationModel utilizationModelRAM,
            UtilizationModel utilizationModelBW)
    {
        return createCloudlet(
                id, utilizationModelCPU, utilizationModelRAM, utilizationModelBW,
                CLOUDLET_LENGTH, 1);
    }

    public static CloudletSimple createCloudlet(final int id, int numberOfPes) {
        return createCloudlet(id, CLOUDLET_LENGTH, numberOfPes);
    }

    private static CloudletSimple createCloudlet(final int id,
                                                 UtilizationModel utilizationModelCPU,
                                                 UtilizationModel utilizationModelRAM,
                                                 UtilizationModel utilizationModelBW,
                                                 long length, int numberOfPes)
    {
        final CloudletSimple cloudlet = new CloudletSimple(id, length, numberOfPes);
        final CloudSim cloudsim = CloudSimMocker.createMock(mocker -> {
            mocker.clock(0).anyTimes();
        });

        cloudlet
            .setFileSize(CLOUDLET_FILE_SIZE)
            .setOutputSize(CLOUDLET_OUTPUT_SIZE)
            .setUtilizationModelCpu(utilizationModelCPU)
            .setUtilizationModelRam(utilizationModelRAM)
            .setUtilizationModelBw(utilizationModelBW);
        cloudlet.setBroker(Mocks.createMockBroker(cloudsim));
        return cloudlet;
    }

    public static CloudletSimple createCloudletWithOnePe(final int id) {
        return createCloudlet(id, CLOUDLET_LENGTH, 1);
    }

    public static CloudletSimple createCloudletWithOnePe(final int id, long length) {
        return createCloudlet(id, length, 1);
    }

    /**
     * Creates a Cloudlet with id equals to 0.
     *
     * @param length the length of the Cloudlet to create
     * @param numberOfPes the number of PEs of the Cloudlet to create
     * @return the created Cloudlet
     */
    public static CloudletSimple createCloudlet0(long length, int numberOfPes) {
        return createCloudlet(0, length, numberOfPes);
    }

    @Test
    public void testSetUtilizationModels() {
        final CloudletSimple c = createCloudlet();
        assertNotNull(c.getUtilizationModelCpu());
        assertNotNull(c.getUtilizationModelRam());
        assertNotNull(c.getUtilizationModelBw());
    }

    @Test(expected = NullPointerException.class)
    public void testSetUtilizationModelBw_null() {
        final CloudletSimple c = createCloudlet();
        c.setUtilizationModelBw(null);
    }

    @Test(expected = NullPointerException.class)
    public void testSetUtilizationModelRam_null() {
        final CloudletSimple c = createCloudlet();
        c.setUtilizationModelRam(null);
    }

    @Test(expected = NullPointerException.class)
    public void testSetUtilizationModelCpu_null() {
        final CloudletSimple c = createCloudlet();
        c.setUtilizationModelCpu(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNew_nullUtilizationModel() {
        createCloudlet(0, null);
    }

    @Test
    public void testSetExecParam() {
        final CloudletSimple c = createCloudlet();
        //Cloudlet has not assigned to a datacenter yet
        assertFalse(c.setWallClockTime(1, 2));

        //Assign cloudlet to a datacenter
        c.assignToDatacenter(Datacenter.NULL);
        assertTrue(c.setWallClockTime(1, 2));
    }

    @Test
    public void testSetCloudletStatus() {
        final CloudletSimple c = createCloudlet();
        c.setStatus(CloudletSimple.Status.INSTANTIATED);
        //The status is the same of the current cloudlet status (the request has not effect)
        assertFalse(c.setStatus(CloudletSimple.Status.INSTANTIATED));

        //Actually changing to a new status
        assertTrue(c.setStatus(CloudletSimple.Status.QUEUED));

        final CloudletSimple.Status newStatus = CloudletSimple.Status.CANCELED;
        assertTrue(c.setStatus(newStatus));
        assertEquals(newStatus, c.getStatus());

        //Trying to change to the same current status (the request has not effect)
        assertFalse(c.setStatus(newStatus));
    }


    @Test
    public void testGetCloudletFinishedSoFar() {
        final long length = 1000;
        final CloudletSimple c = createCloudlet();

        assertEquals(0, c.getFinishedLengthSoFar());

        c.assignToDatacenter(Datacenter.NULL);
        final long finishedSoFar = length / 10;
        c.addFinishedLengthSoFar(finishedSoFar);
        assertEquals(finishedSoFar, c.getFinishedLengthSoFar());

        c.addFinishedLengthSoFar(length);
        assertEquals(length, c.getFinishedLengthSoFar());
    }

    @Test
    public void testIsFinished() {
        final long length = 1000;
        final CloudletSimple c = createCloudlet();

        assertFalse(c.isFinished());

        c.assignToDatacenter(Datacenter.NULL);
        final long finishedSoFar = length / 10;
        c.addFinishedLengthSoFar(finishedSoFar);
        assertFalse(c.isFinished());

        c.addFinishedLengthSoFar(length);
        assertTrue(c.isFinished());
    }

    @Test
    public void testSetPriority() {
        final int zero = 0;
	    cloudlet.setPriority(zero);
        assertEquals(zero, cloudlet.getPriority());

        final int negative = -1;
        cloudlet.setPriority(negative);
	    assertEquals(negative, cloudlet.getPriority());

        final int one = 1;
        cloudlet.setPriority(one);
	    assertEquals(one, cloudlet.getPriority());
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
}
