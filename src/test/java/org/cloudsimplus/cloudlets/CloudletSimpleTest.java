/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.cloudlets;

import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.mocks.CloudSimMocker;
import org.cloudsimplus.mocks.MocksHelper;
import org.cloudsimplus.utilizationmodels.UtilizationModel;
import org.cloudsimplus.utilizationmodels.UtilizationModelStochastic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author	Anton Beloglazov
 * @since	CloudSim Toolkit 2.0
 */
public class CloudletSimpleTest {
    static final int PES_NUMBER = 2;

    private CloudletSimple cloudlet;
    private UtilizationModel utilizationModelCpu;
    private UtilizationModel utilizationModelRam;
    private UtilizationModel utilizationModelBw;

    @BeforeEach
    public void setUp() {
        utilizationModelCpu = new UtilizationModelStochastic();
        utilizationModelRam = new UtilizationModelStochastic();
        utilizationModelBw = new UtilizationModelStochastic();
        cloudlet = new CloudletSimple(0, CloudletTestUtil.CLOUDLET_LENGTH, PES_NUMBER);
        cloudlet.setFileSize(CloudletTestUtil.CLOUDLET_FILE_SIZE)
                .setOutputSize(CloudletTestUtil.CLOUDLET_OUTPUT_SIZE);
        cloudlet.setUtilizationModelCpu(utilizationModelCpu);
        cloudlet.setUtilizationModelRam(utilizationModelRam);
        cloudlet.setUtilizationModelBw(utilizationModelBw);
    }

    @Test
    public void testCloudlet() {
        assertEquals(CloudletTestUtil.CLOUDLET_LENGTH, cloudlet.getLength());
        assertEquals(CloudletTestUtil.CLOUDLET_LENGTH * PES_NUMBER, cloudlet.getTotalLength());
        assertEquals(CloudletTestUtil.CLOUDLET_FILE_SIZE, cloudlet.getFileSize());
        assertEquals(CloudletTestUtil.CLOUDLET_OUTPUT_SIZE, cloudlet.getOutputSize());
        assertEquals(PES_NUMBER, cloudlet.getPesNumber());
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

    @Test()
    public void testAddOnCloudletFinishEventListenerWhenNull() {
        assertThrows(NullPointerException.class, () -> cloudlet.addOnFinishListener(null));
    }

    @Test
    public void testRemoveOnCloudletFinishEventListener() {
        final EventListener<CloudletVmEventInfo> listener = (info) -> {};
        cloudlet.addOnFinishListener(listener);
        assertTrue(cloudlet.removeOnFinishListener(listener));
    }

    @Test
    public void testRemoveOnCloudletFinishEventListenerWhenNull() {
        cloudlet.addOnFinishListener(e->{});
        assertFalse(cloudlet.removeOnFinishListener(null));
    }

    @Test
    public void testGetWaitingTime() {
        final double arrivalTime = 0.0, execStartTime = 10.0;
        final CloudSimPlus cloudsim = CloudSimMocker.createMock(mocker -> {
            mocker.clock(arrivalTime);
        });

        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet();
        cloudlet.setBroker(MocksHelper.createMockBroker(cloudsim));
        assertEquals(-1, cloudlet.getStartWaitTime());
        final double expectedWaitingTime = execStartTime - arrivalTime;
        cloudlet.registerArrivalInDatacenter();
        cloudlet.setStartTime(execStartTime);
        assertEquals(expectedWaitingTime, cloudlet.getStartWaitTime());
    }

    @Test
    public void testGetStartTime() {
        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet();
        assertEquals(Cloudlet.NOT_ASSIGNED, cloudlet.getStartTime());

        final int execStartTime = 10;
        cloudlet.registerArrivalInDatacenter();
        cloudlet.setStartTime(execStartTime);
        assertEquals(execStartTime, cloudlet.getStartTime());
    }

    @Test
    public void testGetActualCpuTimeSimulationNotFinished() {
        final double simulationClock = 100;

        final CloudSimPlus cloudsim = CloudSimMocker.createMock(mocker -> mocker.clock(simulationClock));
        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet();
        cloudlet.setStartTime(0);

        cloudlet.setBroker(MocksHelper.createMockBroker(cloudsim));
        assertEquals(simulationClock, cloudlet.getTotalExecutionTime());
    }

    @Test
    public void testGetActualCpuTimeSimulationFinished() {
        final double execStartTime = 10;
        final double simulationClock = 200;
        final double actualCpuTime = simulationClock - execStartTime;

        final CloudSimPlus cloudsim = CloudSimMocker.createMock(mocker -> mocker.clock(simulationClock));
        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet();

        cloudlet.setBroker(MocksHelper.createMockBroker(cloudsim));
        cloudlet.registerArrivalInDatacenter();
        cloudlet.setStartTime(execStartTime);
        cloudlet.setStatus(Cloudlet.Status.SUCCESS);
        assertEquals(actualCpuTime, cloudlet.getTotalExecutionTime());
    }

    @Test
    public void testGetPriority() {
        final int expected = 8;
        cloudlet.setPriority(expected);
        assertEquals(expected, cloudlet.getPriority());
    }

    @Test
    public void testAddCloudletFinishedSoFar() {
        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet();
        assertEquals(0, cloudlet.getFinishedLengthSoFar());

        cloudlet.registerArrivalInDatacenter();
        final long cloudletFinishedSoFar = cloudlet.getLength() / 2;
        assertTrue(cloudlet.addFinishedLengthSoFar(cloudletFinishedSoFar));
        assertEquals(cloudletFinishedSoFar, cloudlet.getFinishedLengthSoFar());
        assertFalse(cloudlet.addFinishedLengthSoFar(-1));
        assertEquals(cloudletFinishedSoFar, cloudlet.getFinishedLengthSoFar());
    }

    @Test
    public void testAddCloudletFinishedSoFarWhenValueIsLowerThanLen() {
        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet();
        cloudlet.registerArrivalInDatacenter();
        final long cloudletFinishedSoFar = cloudlet.getLength() / 2;
        assertTrue(cloudlet.addFinishedLengthSoFar(cloudletFinishedSoFar));
        assertEquals(cloudletFinishedSoFar, cloudlet.getFinishedLengthSoFar());
    }

    @Test
    public void testAddCloudletFinishedSoFarWhenValueIsHigherThanLen() {
        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet();
        cloudlet.registerArrivalInDatacenter();
        final long cloudletFinishedSoFar = cloudlet.getLength() / 2;
        cloudlet.addFinishedLengthSoFar(cloudletFinishedSoFar);
        cloudlet.addFinishedLengthSoFar(cloudletFinishedSoFar*3);
        assertEquals(cloudlet.getLength(), cloudlet.getFinishedLengthSoFar());
    }

    @Test
    public void testAddCloudletFinishedSoFarWhenLengthParamGreaterThanCloudletLength() {
        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet();
        final long expected = cloudlet.getLength();
        cloudlet.addFinishedLengthSoFar(expected*2);
        assertEquals(expected, cloudlet.getLength());
    }

    @Test
    public void testSetValidCloudletLength() {
        final int expected = 1000;
        cloudlet.setLength(expected);
        assertEquals(expected, cloudlet.getLength());
    }

    @Test()
    public void testSetCloudletLengthToZero() {
        assertThrows(IllegalArgumentException.class, () -> cloudlet.setLength(0));
    }

    @Test
    public void testSetCloudletLengthToNegative() {
        final long len = -1000;
        cloudlet.setLength(len);
        assertEquals(len, cloudlet.getLength());
    }

    @Test
    public void testSetValidPesNumber() {
        final int expected = 2;
        cloudlet.setPesNumber(expected);
        assertEquals(expected, cloudlet.getPesNumber());
    }

    @Test()
    public void testSetPesNumberToZero() {
        assertThrows(IllegalArgumentException.class, () -> cloudlet.setPesNumber(0));
    }

    @Test()
    public void testSetPesNumberToNegative() {
        assertThrows(IllegalArgumentException.class, () -> cloudlet.setPesNumber(-1));
    }

    @Test
    public void testSetNetServiceLevel() {
        final int valid0 = 0;
        cloudlet.setNetServiceLevel(valid0);
        assertEquals(valid0, cloudlet.getNetServiceLevel());

        final int valid1 = 1;
        cloudlet.setNetServiceLevel(valid1);
        assertEquals(valid1, cloudlet.getNetServiceLevel());

        final int invalidNegative = -1;
        assertThrows(IllegalArgumentException.class, () -> cloudlet.setNetServiceLevel(invalidNegative));
        assertEquals(valid1, cloudlet.getNetServiceLevel());
    }

    @Test
    public void testSetUtilizationModels() {
        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet();
        assertNotNull(cloudlet.getUtilizationModelCpu());
        assertNotNull(cloudlet.getUtilizationModelRam());
        assertNotNull(cloudlet.getUtilizationModelBw());
    }

    @Test()
    public void testSetUtilizationModelBwWhenNull() {
        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet();
        assertThrows(NullPointerException.class, () -> cloudlet.setUtilizationModelBw(null));
    }

    @Test()
    public void testSetUtilizationModelRamWhenNull() {
        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet();
        assertThrows(NullPointerException.class, () -> cloudlet.setUtilizationModelRam(null));
    }

    @Test()
    public void testSetUtilizationModelCpuWhenNull() {
        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet();
        assertThrows(NullPointerException.class, () -> cloudlet.setUtilizationModelCpu(null));
    }

    @Test()
    public void testConstructorWhenNullUtilizationModel() {
        assertThrows(NullPointerException.class, () -> CloudletTestUtil.createCloudlet(0, null));
    }

    @Test
    public void testSetCloudletStatus() {
        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet();
        cloudlet.setStatus(CloudletSimple.Status.INSTANTIATED);
        //The status is the same of the current cloudlet status (the request has not effect)
        assertFalse(cloudlet.setStatus(CloudletSimple.Status.INSTANTIATED));

        //Actually changing to a new status
        assertTrue(cloudlet.setStatus(CloudletSimple.Status.QUEUED));

        final CloudletSimple.Status newStatus = CloudletSimple.Status.CANCELED;
        assertTrue(cloudlet.setStatus(newStatus));
        assertEquals(newStatus, cloudlet.getStatus());

        //Trying to change to the same current status (the request has not effect)
        assertFalse(cloudlet.setStatus(newStatus));
    }


    @Test
    public void testGetCloudletFinishedSoFar() {
        final long length = 1000;
        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet();

        assertEquals(0, cloudlet.getFinishedLengthSoFar());

        cloudlet.registerArrivalInDatacenter();
        final long finishedSoFar = length / 10;
        cloudlet.addFinishedLengthSoFar(finishedSoFar);
        assertEquals(finishedSoFar, cloudlet.getFinishedLengthSoFar());

        cloudlet.addFinishedLengthSoFar(length);
        assertEquals(length, cloudlet.getFinishedLengthSoFar());
    }

    @Test
    public void testIsFinished() {
        final long length = 1000;
        final CloudletSimple cloudlet = CloudletTestUtil.createCloudlet();

        assertFalse(cloudlet.isFinished());

        cloudlet.registerArrivalInDatacenter();
        final long finishedSoFar = length / 10;
        cloudlet.addFinishedLengthSoFar(finishedSoFar);
        assertFalse(cloudlet.isFinished());

        cloudlet.addFinishedLengthSoFar(length);
        cloudlet.notifyOnUpdateProcessingListeners(1000);
        assertTrue(cloudlet.isFinished());
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
        assertEquals(utilizationModelCpu.getUtilization(0), cloudlet.getUtilizationOfCpu(0));
    }

    @Test
    public void testGetUtilizationOfRam() {
        assertEquals(utilizationModelRam.getUtilization(0), cloudlet.getUtilizationOfRam(0));
    }

    @Test
    public void testGetUtilizationOfBw() {
        assertEquals(utilizationModelBw.getUtilization(0), cloudlet.getUtilizationOfBw(0));
    }
}
