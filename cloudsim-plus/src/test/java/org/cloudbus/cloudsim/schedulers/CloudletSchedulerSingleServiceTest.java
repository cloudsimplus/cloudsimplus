/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers;

import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelStochastic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cloudbus.cloudsim.CloudletSimple;
import org.cloudbus.cloudsim.CloudletExecutionInfo;
import org.easymock.EasyMock;

import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * @author	Anton Beloglazov
 * @since	CloudSim Toolkit 2.0
 */
public class CloudletSchedulerSingleServiceTest {
    private static final double MIN_UTILIZATION_PERCENT = 0.10; 
    private static final double MAX_UTILIZATION_PERCENT = 0.90;
    private static final long CLOUDLET_LENGTH = 1000;
    private static final long CLOUDLET_FILE_SIZE = 300;
    private static final long CLOUDLET_OUTPUT_SIZE = 300;

    private static final double MIPS = CLOUDLET_LENGTH;
    private static final int PES_NUMBER = 2;

    private CloudletSchedulerDynamicWorkload cloudletScheduler;

    @Before
    public void setUp() throws Exception {
        cloudletScheduler = new CloudletSchedulerDynamicWorkload(MIPS, PES_NUMBER);
    }

    @Test
    public void testGetNumberOfPes() {
        assertEquals(PES_NUMBER, cloudletScheduler.getNumberOfPes());
    }

    @Test
    public void testGetMips() {
        assertEquals(MIPS, cloudletScheduler.getMips(), 0);
    }

    @Test
    public void testGetUnderAllocatedMips() {
        UtilizationModelStochastic utilizationModel = new UtilizationModelStochastic();
        CloudletSimple cloudlet = new CloudletSimple(0, 
                CLOUDLET_LENGTH, PES_NUMBER, 
                CLOUDLET_FILE_SIZE, CLOUDLET_OUTPUT_SIZE,
                utilizationModel, utilizationModel, utilizationModel);
        CloudletExecutionInfo rcl = new CloudletExecutionInfo(cloudlet);

        Map<String, Double> underAllocatedMips = new HashMap<>();
        assertEquals(underAllocatedMips, cloudletScheduler.getUnderAllocatedMips());

        underAllocatedMips.put(rcl.getUid(), MIPS / 2);
        cloudletScheduler.updateUnderAllocatedMipsForCloudlet(rcl, MIPS / 2);
        assertEquals(underAllocatedMips, cloudletScheduler.getUnderAllocatedMips());

        underAllocatedMips.put(rcl.getUid(), MIPS);
        cloudletScheduler.updateUnderAllocatedMipsForCloudlet(rcl, MIPS / 2);
        assertEquals(underAllocatedMips, cloudletScheduler.getUnderAllocatedMips());
    }

    @Test
    public void testGetCurrentRequestedMips() {
        UtilizationModelStochastic utilizationModel = new UtilizationModelStochastic();
        CloudletSimple cloudlet = new CloudletSimple(0, 
                CLOUDLET_LENGTH, PES_NUMBER, 
                CLOUDLET_FILE_SIZE, CLOUDLET_OUTPUT_SIZE,
                utilizationModel, utilizationModel, utilizationModel);
        cloudlet.assignCloudletToDatacenter(0, 0, 0);

        List<Double> mipsShare = new ArrayList<>();
        mipsShare.add(MIPS);
        mipsShare.add(MIPS);
        cloudletScheduler.setCurrentMipsShare(mipsShare);

        assertEquals(mipsShare.size(), cloudletScheduler.getCurrentMipsShare().size(), 0);
        assertEquals(mipsShare.get(0), cloudletScheduler.getCurrentMipsShare().get(0), 0);
        assertEquals(mipsShare.get(1), cloudletScheduler.getCurrentMipsShare().get(1), 0);

        double utilization = utilizationModel.getUtilization(0);

        cloudletScheduler.cloudletSubmit(cloudlet);

        List<Double> requestedMips = new ArrayList<>();
        requestedMips.add(MIPS * utilization);
        requestedMips.add(MIPS * utilization);

        assertEquals(requestedMips, cloudletScheduler.getCurrentRequestedMips());
    }

    @Test
    public void testGetTotalUtilization() {
        UtilizationModelStochastic utilizationModel = new UtilizationModelStochastic();
        CloudletSimple cloudlet = new CloudletSimple(0, 
                CLOUDLET_LENGTH, PES_NUMBER, 
                CLOUDLET_FILE_SIZE, CLOUDLET_OUTPUT_SIZE,
                utilizationModel, utilizationModel, utilizationModel);
        cloudlet.assignCloudletToDatacenter(0, 0, 0);

        List<Double> mipsShare = new ArrayList<>();
        mipsShare.add(MIPS);
        mipsShare.add(MIPS);
        cloudletScheduler.setCurrentMipsShare(mipsShare);

        assertEquals(mipsShare.size(), cloudletScheduler.getCurrentMipsShare().size(), 0);
        assertEquals(mipsShare.get(0), cloudletScheduler.getCurrentMipsShare().get(0), 0);
        assertEquals(mipsShare.get(1), cloudletScheduler.getCurrentMipsShare().get(1), 0);

        double utilization = utilizationModel.getUtilization(0);

        cloudletScheduler.cloudletSubmit(cloudlet, 0);

        assertEquals(utilization, cloudletScheduler.getTotalUtilizationOfCpu(0), 0);
    }

    @Test
    public void testCloudletFinish() {
        UtilizationModelStochastic utilizationModel = new UtilizationModelStochastic();
        CloudletSimple cloudlet = new CloudletSimple(0, 
                CLOUDLET_LENGTH, PES_NUMBER, 
                CLOUDLET_FILE_SIZE, CLOUDLET_OUTPUT_SIZE,
                utilizationModel, utilizationModel, utilizationModel);
        cloudlet.assignCloudletToDatacenter(0, 0, 0);

        List<Double> mipsShare = new ArrayList<>();
        mipsShare.add(MIPS);
        mipsShare.add(MIPS);
        cloudletScheduler.setCurrentMipsShare(mipsShare);

        cloudletScheduler.cloudletSubmit(cloudlet, 0);
        cloudletScheduler.cloudletFinish(new CloudletExecutionInfo(cloudlet));

        assertEquals(CloudletSimple.Status.SUCCESS.ordinal(), cloudletScheduler.getCloudletStatus(0));
        assertTrue(cloudletScheduler.hasFinishedCloudlets());
        assertSame(cloudlet, cloudletScheduler.getNextFinishedCloudlet());
    }

    @Test
    public void testGetTotalCurrentMips() {
        List<Double> mipsShare = new ArrayList<>();
        mipsShare.add(MIPS / 4);
        mipsShare.add(MIPS / 4);
        cloudletScheduler.setCurrentMipsShare(mipsShare);

        assertEquals(MIPS / 2, cloudletScheduler.getTotalCurrentMips(), 0);
    }

    @Test
    public void testGetTotalCurrentMipsForCloudlet() {
        UtilizationModelStochastic utilizationModel = new UtilizationModelStochastic();
        CloudletSimple cloudlet = new CloudletSimple(0, 
                CLOUDLET_LENGTH, PES_NUMBER, 
                CLOUDLET_FILE_SIZE, CLOUDLET_OUTPUT_SIZE,
                utilizationModel, utilizationModel, utilizationModel);
        cloudlet.assignCloudletToDatacenter(0, 0, 0);
        CloudletExecutionInfo rgl = new CloudletExecutionInfo(cloudlet);

        List<Double> mipsShare = new ArrayList<>();
        mipsShare.add(MIPS / 4);
        mipsShare.add(MIPS / 4);
        mipsShare.add(MIPS / 4);
        mipsShare.add(MIPS / 4);

        assertEquals(MIPS / 4.0 * PES_NUMBER,
                cloudletScheduler.getTotalCurrentAvailableMipsForCloudlet(rgl, mipsShare), 0);
    }

    @Test
    public void testGetEstimatedFinishTimeLowUtilization() {
        UtilizationModel utilizationModel = createMock(UtilizationModel.class);
        expect(utilizationModel.getUtilization(0))
                .andReturn(MIN_UTILIZATION_PERCENT)
                .anyTimes();
        replay(utilizationModel);
        testGetEstimatedFinishTime(utilizationModel);
        verify(utilizationModel);
    }

    @Test
    public void testGetEstimatedFinishTimeHighUtilization() {
        UtilizationModel utilizationModel = createMock(UtilizationModel.class);
        expect(utilizationModel.getUtilization(0))
                .andReturn(0.91)
                .anyTimes();
        replay(utilizationModel);
        testGetEstimatedFinishTime(utilizationModel);
        verify(utilizationModel);
    }

    public void testGetEstimatedFinishTime(UtilizationModel utilizationModel) {
        CloudletSimple cloudlet = new CloudletSimple(0, 
                CLOUDLET_LENGTH, PES_NUMBER, 
                CLOUDLET_FILE_SIZE, CLOUDLET_OUTPUT_SIZE,
                utilizationModel, utilizationModel, utilizationModel);
        cloudlet.assignCloudletToDatacenter(0, 0, 0);
        CloudletExecutionInfo rgl = new CloudletExecutionInfo(cloudlet);

        List<Double> mipsShare = new ArrayList<>();
        mipsShare.add(MIPS / 4);
        mipsShare.add(MIPS / 4);
        mipsShare.add(MIPS / 4);
        mipsShare.add(MIPS / 4);

        cloudletScheduler.setCurrentMipsShare(mipsShare);

        double utilization = utilizationModel.getUtilization(0);
        double totalCurrentMipsForCloudlet = MIPS / 4 * PES_NUMBER;
        double requestedMips = (int) (utilization * PES_NUMBER * MIPS);
        if (requestedMips > totalCurrentMipsForCloudlet) {
            requestedMips = totalCurrentMipsForCloudlet;
        }

        double expectedFinishTime = (double) CLOUDLET_LENGTH * PES_NUMBER / requestedMips;
        double actualFinishTime = cloudletScheduler.getEstimatedFinishTime(rgl, 0);

        assertEquals(expectedFinishTime, actualFinishTime, 0);
    }

    @Test
    public void testCloudletSubmitLowUtilization() {
        UtilizationModel utilizationModel = createMock(UtilizationModel.class);
        expect(utilizationModel.getUtilization(0))
                .andReturn(MIN_UTILIZATION_PERCENT)
                .anyTimes();
        replay(utilizationModel);
        testCloudletSubmit(utilizationModel);
        verify(utilizationModel);
    }

    @Test
    public void testCloudletSubmitHighUtilization() {
        UtilizationModel utilizationModel = createMock(UtilizationModel.class);
        expect(utilizationModel.getUtilization(0))
                .andReturn(0.91)
                .anyTimes();
        replay(utilizationModel);
        testCloudletSubmit(utilizationModel);
        verify(utilizationModel);
    }

    public void testCloudletSubmit(UtilizationModel utilizationModel) {
        CloudletSimple cloudlet = 
                new CloudletSimple(0, 
                        CLOUDLET_LENGTH, PES_NUMBER, 
                        CLOUDLET_FILE_SIZE, CLOUDLET_OUTPUT_SIZE,
                        utilizationModel, utilizationModel, utilizationModel);
        cloudlet.assignCloudletToDatacenter(0, 0, 0);

        List<Double> mipsShare = new ArrayList<>();
        mipsShare.add(MIPS / 4);
        mipsShare.add(MIPS / 4);
        mipsShare.add(MIPS / 4);
        mipsShare.add(MIPS / 4);

        cloudletScheduler.setCurrentMipsShare(mipsShare);

        double utilization = utilizationModel.getUtilization(0);
        double totalCurrentMipsForCloudlet = MIPS / 4 * PES_NUMBER;
        double requestedMips = (int) (utilization * PES_NUMBER * MIPS);
        if (requestedMips > totalCurrentMipsForCloudlet) {
            requestedMips = totalCurrentMipsForCloudlet;
        }

        double expectedFinishTime = (double) CLOUDLET_LENGTH * PES_NUMBER / requestedMips;
        double actualFinishTime = cloudletScheduler.cloudletSubmit(cloudlet);

        assertEquals(expectedFinishTime, actualFinishTime, 0);
    }

    @Test
    public void testUpdateVmProcessingLowUtilization() {
        UtilizationModel utilizationModel = createMock(UtilizationModel.class);
        expect(utilizationModel.getUtilization(EasyMock.anyDouble())).andReturn(MIN_UTILIZATION_PERCENT).anyTimes();
        replay(utilizationModel);

        testUpdateVmProcessing(utilizationModel);
    }

    @Test
    public void testUpdateVmProcessingHighUtilization() {
        UtilizationModel utilizationModel = createMock(UtilizationModel.class);

        expect(utilizationModel.getUtilization(0))
                .andReturn(0.91)
                .anyTimes();

        expect(utilizationModel.getUtilization(1.0))
                .andReturn(0.91)
                .anyTimes();

        replay(utilizationModel);

        testUpdateVmProcessing(utilizationModel);

        verify(utilizationModel);
    }

    @Test
    public void testUpdateVmProcessingLowAndHighUtilization() {
        UtilizationModel utilizationModel = createMock(UtilizationModel.class);

        expect(utilizationModel.getUtilization(0))
                .andReturn(MIN_UTILIZATION_PERCENT)
                .anyTimes();

        expect(utilizationModel.getUtilization(1.0))
                .andReturn(MAX_UTILIZATION_PERCENT)
                .anyTimes();

        replay(utilizationModel);

        testUpdateVmProcessing(utilizationModel);

        verify(utilizationModel);
    }

    public void testUpdateVmProcessing(UtilizationModel utilizationModel) {
        CloudletSimple cloudlet = 
                new CloudletSimple(0, 
                        CLOUDLET_LENGTH, PES_NUMBER, 
                        CLOUDLET_FILE_SIZE, CLOUDLET_OUTPUT_SIZE,
                        utilizationModel, utilizationModel, utilizationModel);
        cloudlet.assignCloudletToDatacenter(0, 0, 0);

        List<Double> mipsShare = new ArrayList<>();
        mipsShare.add(MIPS / 4);
        mipsShare.add(MIPS / 4);
        mipsShare.add(MIPS / 4);
        mipsShare.add(MIPS / 4);

        cloudletScheduler.setCurrentMipsShare(mipsShare);
        cloudletScheduler.cloudletSubmit(cloudlet);
        double totalCurrentMipsForCloudlet = MIPS / 4 * PES_NUMBER;

        double utilization1 = utilizationModel.getUtilization(0);
        double requestedMips1 = (int) (utilization1 * PES_NUMBER * MIPS);
        if (requestedMips1 > totalCurrentMipsForCloudlet) {
            requestedMips1 = totalCurrentMipsForCloudlet;
        }

        double expectedCompletiontime1 = ((double) CLOUDLET_LENGTH * PES_NUMBER) / requestedMips1;
        double actualCompletionTime1 = cloudletScheduler.updateVmProcessing(0, mipsShare);
        assertEquals(expectedCompletiontime1, actualCompletionTime1, 0);

        double utilization2 = utilizationModel.getUtilization(1);
        double requestedMips2 = (int) (utilization2 * PES_NUMBER * MIPS);
        if (requestedMips2 > totalCurrentMipsForCloudlet) {
            requestedMips2 = totalCurrentMipsForCloudlet;
        }

        double expectedCompletiontime2 = 1.0 + ((CLOUDLET_LENGTH * PES_NUMBER - requestedMips1 * 1)) / requestedMips2;
        double actualCompletionTime2 = cloudletScheduler.updateVmProcessing(1, mipsShare);
        assertEquals(expectedCompletiontime2, actualCompletionTime2, 0);

        assertFalse(cloudletScheduler.hasFinishedCloudlets());

        assertEquals(Double.MAX_VALUE, cloudletScheduler.updateVmProcessing(CLOUDLET_LENGTH, mipsShare), 0);

        assertTrue(cloudletScheduler.hasFinishedCloudlets());
    }

}
