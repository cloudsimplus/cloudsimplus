/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.schedulers.cloudlet;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelStochastic;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.cloudlets.CloudletExecutionInfo;
import org.cloudbus.cloudsim.vms.VmSimple;
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
public class CloudletSchedulerDynamicWorkloadTest {
    private static final double MIN_USAGE_PERCENT = 0.10;
    private static final double MAX_USAGE_PERCENT = 0.90;
    private static final long CLOUDLET_LENGTH = 1000;
    private static final long CLOUDLET_FILE_SIZE = 300;
    private static final long CLOUDLET_OUTPUT_SIZE = 300;

    private static final double MIPS = CLOUDLET_LENGTH;
    private static final double HALF_MIPS = MIPS/2;    
    private static final double QUARTER_MIPS = MIPS/4;    
    private static final int PES_NUMBER = 2;

    private CloudletSchedulerDynamicWorkload cloudletScheduler;

    @Before
    public void setUp() throws Exception {
        cloudletScheduler = new CloudletSchedulerDynamicWorkload(MIPS, PES_NUMBER);
        cloudletScheduler.setVm(new VmSimple(0, MIPS, PES_NUMBER));
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
        Cloudlet cloudlet =
            new CloudletSimple(0, CLOUDLET_LENGTH, PES_NUMBER)
                .setFileSize(CLOUDLET_FILE_SIZE)
                .setOutputSize(CLOUDLET_OUTPUT_SIZE)
                .setUtilizationModel(utilizationModel);
        CloudletExecutionInfo rcl = new CloudletExecutionInfo(cloudlet);

        Map<Cloudlet, Double> underAllocatedMips = new HashMap<>();
        assertEquals(underAllocatedMips, cloudletScheduler.getUnderAllocatedMips());

        underAllocatedMips.put(rcl.getCloudlet(), HALF_MIPS);
        cloudletScheduler.updateUnderAllocatedMipsForCloudlet(rcl, HALF_MIPS);
        assertEquals(underAllocatedMips, cloudletScheduler.getUnderAllocatedMips());

        underAllocatedMips.put(rcl.getCloudlet(), MIPS);
        cloudletScheduler.updateUnderAllocatedMipsForCloudlet(rcl, HALF_MIPS);
        assertEquals(underAllocatedMips, cloudletScheduler.getUnderAllocatedMips());
    }

    @Test
    public void testGetCurrentRequestedMips() {
        UtilizationModelStochastic utilizationModel = new UtilizationModelStochastic();
        Cloudlet cloudlet =
            new CloudletSimple(0, CLOUDLET_LENGTH, PES_NUMBER)
                .setFileSize(CLOUDLET_FILE_SIZE)
                .setOutputSize(CLOUDLET_OUTPUT_SIZE)
                .setUtilizationModel(utilizationModel);
        cloudlet.assignToDatacenter(Datacenter.NULL);

        List<Double> mipsShare = createMipsShare(2,MIPS);
        cloudletScheduler.setCurrentMipsShare(mipsShare);

        assertEquals(mipsShare.size(), cloudletScheduler.getCurrentMipsShare().size(), 0);
        assertEquals(mipsShare.get(0), cloudletScheduler.getCurrentMipsShare().get(0), 0);
        assertEquals(mipsShare.get(1), cloudletScheduler.getCurrentMipsShare().get(1), 0);

        double utilization = utilizationModel.getUtilization(0);

        cloudletScheduler.cloudletSubmit(cloudlet);

        List<Double> requestedMips = createMipsShare(2,MIPS * utilization);
        assertEquals(requestedMips, cloudletScheduler.getCurrentRequestedMips());
    }

    @Test
    public void testGetTotalUtilization() {
        UtilizationModelStochastic utilizationModel = new UtilizationModelStochastic();
        Cloudlet cloudlet =
            new CloudletSimple(0, CLOUDLET_LENGTH, PES_NUMBER)
                .setFileSize(CLOUDLET_FILE_SIZE)
                .setOutputSize(CLOUDLET_OUTPUT_SIZE)
                .setUtilizationModel(utilizationModel);
        cloudlet.assignToDatacenter(Datacenter.NULL);

        List<Double> mipsShare = createMipsShare(2,MIPS);
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
        Cloudlet cloudlet =
            new CloudletSimple(0, CLOUDLET_LENGTH, PES_NUMBER)
                .setFileSize(CLOUDLET_FILE_SIZE)
                .setOutputSize(CLOUDLET_OUTPUT_SIZE)
                .setUtilizationModel(utilizationModel);
        cloudlet.assignToDatacenter(Datacenter.NULL);

        List<Double> mipsShare = createMipsShare(2,MIPS);
        cloudletScheduler.setCurrentMipsShare(mipsShare);

        cloudletScheduler.cloudletSubmit(cloudlet, 0);
        cloudletScheduler.cloudletFinish(new CloudletExecutionInfo(cloudlet));

        assertEquals(CloudletSimple.Status.SUCCESS.ordinal(), cloudletScheduler.getCloudletStatus(0));
        assertTrue(cloudletScheduler.hasFinishedCloudlets());
        assertSame(cloudlet, cloudletScheduler.removeNextFinishedCloudlet());
    }

    @Test
    public void testGetTotalCurrentMips() {
        List<Double> mipsShare = createMipsShare(2,QUARTER_MIPS);
        cloudletScheduler.setCurrentMipsShare(mipsShare);

        assertEquals(HALF_MIPS, cloudletScheduler.getTotalCurrentMips(), 0);
    }

    @Test
    public void testGetTotalCurrentMipsForCloudlet() {
        UtilizationModelStochastic utilizationModel = new UtilizationModelStochastic();
        Cloudlet cloudlet =
            new CloudletSimple(0, CLOUDLET_LENGTH, PES_NUMBER)
                .setFileSize(CLOUDLET_FILE_SIZE)
                .setOutputSize(CLOUDLET_OUTPUT_SIZE)
                .setUtilizationModel(utilizationModel);
        cloudlet.assignToDatacenter(Datacenter.NULL);
        CloudletExecutionInfo rgl = new CloudletExecutionInfo(cloudlet);

        List<Double> mipsShare = createMipsShare(4, QUARTER_MIPS);

        assertEquals(QUARTER_MIPS * PES_NUMBER,
                cloudletScheduler.getTotalCurrentAvailableMipsForCloudlet(rgl, mipsShare), 0);
    }

    @Test
    public void testGetEstimatedFinishTimeLowUtilization() {
        UtilizationModel utilizationModel = createMock(UtilizationModel.class);
        expect(utilizationModel.getUtilization(0)).andReturn(MIN_USAGE_PERCENT).anyTimes();
        replay(utilizationModel);
        testGetEstimatedFinishTime(utilizationModel);
        verify(utilizationModel);
    }

    @Test
    public void testGetEstimatedFinishTimeHighUtilization() {
        UtilizationModel utilizationModel = createMock(UtilizationModel.class);
        expect(utilizationModel.getUtilization(0)).andReturn(0.91).anyTimes();
        replay(utilizationModel);
        testGetEstimatedFinishTime(utilizationModel);
        verify(utilizationModel);
    }

    public void testGetEstimatedFinishTime(UtilizationModel utilizationModel) {
        Cloudlet cloudlet =
            new CloudletSimple(0, CLOUDLET_LENGTH, PES_NUMBER)
                .setFileSize(CLOUDLET_FILE_SIZE)
                .setOutputSize(CLOUDLET_OUTPUT_SIZE)
                .setUtilizationModel(utilizationModel);
        cloudlet.assignToDatacenter(Datacenter.NULL);
        CloudletExecutionInfo rgl = new CloudletExecutionInfo(cloudlet);

        List<Double> mipsShare = createMipsShare(4, QUARTER_MIPS);
        cloudletScheduler.setCurrentMipsShare(mipsShare);

        double utilization = utilizationModel.getUtilization(0);
        double requestedMips = getRequestedMips(utilization,QUARTER_MIPS);

        double expectedFinishTime = (double) CLOUDLET_LENGTH / requestedMips;
        double actualFinishTime = cloudletScheduler.getEstimatedFinishTimeOfCloudlet(rgl, 0);

        assertEquals(expectedFinishTime, actualFinishTime, 0);
    }

    @Test
    public void testCloudletSubmitLowUtilization() {
        UtilizationModel utilizationModel = createMock(UtilizationModel.class);
        expect(utilizationModel.getUtilization(0)).andReturn(MIN_USAGE_PERCENT).anyTimes();
        replay(utilizationModel);
        testCloudletSubmit(utilizationModel);
        verify(utilizationModel);
    }

    @Test
    public void testCloudletSubmitHighUtilization() {
        UtilizationModel utilizationModel = createMock(UtilizationModel.class);
        expect(utilizationModel.getUtilization(0)).andReturn(0.91).anyTimes();
        replay(utilizationModel);
        testCloudletSubmit(utilizationModel);
        verify(utilizationModel);
    }

    public void testCloudletSubmit(UtilizationModel utilizationModel) {
        Cloudlet cloudlet =
            new CloudletSimple(0, CLOUDLET_LENGTH, PES_NUMBER)
                .setFileSize(CLOUDLET_FILE_SIZE)
                .setOutputSize(CLOUDLET_OUTPUT_SIZE)
                .setUtilizationModel(utilizationModel);
        cloudlet.assignToDatacenter(Datacenter.NULL);

        List<Double> mipsShare = createMipsShare(4, QUARTER_MIPS);

        cloudletScheduler.setCurrentMipsShare(mipsShare);

        double utilization = utilizationModel.getUtilization(0);
        double requestedMips = getRequestedMips(utilization,QUARTER_MIPS);

        double expectedFinishTime = (double) CLOUDLET_LENGTH / requestedMips;
        double actualFinishTime = cloudletScheduler.cloudletSubmit(cloudlet);
        assertEquals(expectedFinishTime, actualFinishTime, 0.3);
    }

    private List<Double> createMipsShare(int pes, double mips) {
        List<Double> mipsShare = new ArrayList<>(pes);
        for(int i = 0; i < pes; i++){
            mipsShare.add(mips);
        }
        return mipsShare;
    }

    @Test
    public void testUpdateVmProcessingLowUtilization() {
        UtilizationModel utilizationModel = createMock(UtilizationModel.class);
        expect(utilizationModel.getUtilization(EasyMock.anyDouble())).andReturn(MIN_USAGE_PERCENT).anyTimes();
        replay(utilizationModel);
        testUpdateVmProcessing(utilizationModel);
    }

    @Test
    public void testUpdateVmProcessingHighUtilization() {
        final double usage = 0.91;
        UtilizationModel utilizationModel = createMock(UtilizationModel.class);
        expect(utilizationModel.getUtilization(0)).andReturn(usage).anyTimes();
        expect(utilizationModel.getUtilization(1.0)).andReturn(usage).anyTimes();
        expect(utilizationModel.getUtilization(CLOUDLET_LENGTH)).andReturn(usage).anyTimes();
        replay(utilizationModel);
        testUpdateVmProcessing(utilizationModel);
        verify(utilizationModel);
    }

    @Test
    public void testUpdateVmProcessingLowAndHighUtilization() {
        UtilizationModel utilizationModel = createMock(UtilizationModel.class);
        expect(utilizationModel.getUtilization(0)).andReturn(MIN_USAGE_PERCENT).anyTimes();
        expect(utilizationModel.getUtilization(1.0)).andReturn(MAX_USAGE_PERCENT).anyTimes();
        expect(utilizationModel.getUtilization(CLOUDLET_LENGTH)).andReturn(MAX_USAGE_PERCENT).anyTimes();
        replay(utilizationModel);
        testUpdateVmProcessing(utilizationModel);
        verify(utilizationModel);
    }

    public void testUpdateVmProcessing(UtilizationModel utilizationModel) {
        Cloudlet cloudlet =
            new CloudletSimple(0, CLOUDLET_LENGTH, PES_NUMBER)
                .setFileSize(CLOUDLET_FILE_SIZE)
                .setOutputSize(CLOUDLET_OUTPUT_SIZE)
                .setUtilizationModel(utilizationModel);
        cloudlet.assignToDatacenter(Datacenter.NULL);
        
        List<Double> mipsShare = createMipsShare(4, QUARTER_MIPS);

        cloudletScheduler.setCurrentMipsShare(mipsShare);
        cloudletScheduler.cloudletSubmit(cloudlet);

        double utilization1 = utilizationModel.getUtilization(0);
        double requestedMips1 = getRequestedMips(utilization1,QUARTER_MIPS);

        double expectedCompletiontime1 = ((double) CLOUDLET_LENGTH) / requestedMips1;
        double actualCompletionTime1 = cloudletScheduler.updateVmProcessing(0, mipsShare);
        assertEquals(expectedCompletiontime1, actualCompletionTime1, 0.1);

        double utilization2 = utilizationModel.getUtilization(1);
        double requestedMips2 = getRequestedMips(utilization2,QUARTER_MIPS);

        double expectedCompletiontime2 = ((CLOUDLET_LENGTH - requestedMips2 * 1)) / requestedMips2;
        double actualCompletionTime2 = cloudletScheduler.updateVmProcessing(1, mipsShare);
        assertEquals(expectedCompletiontime2, actualCompletionTime2, 0.2);
        assertFalse(cloudletScheduler.hasFinishedCloudlets());
        assertEquals(Double.MAX_VALUE, cloudletScheduler.updateVmProcessing(CLOUDLET_LENGTH, mipsShare), 0);
        assertTrue(cloudletScheduler.hasFinishedCloudlets());
    }

    private double getRequestedMips(double utilizationPercent, double mips) {
        final double requestedMips = utilizationPercent * mips;
        return Math.min(requestedMips, mips);
    }

}
