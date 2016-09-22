package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.listeners.EventListener;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.easymock.EasyMock;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class CloudletTest {
    
    @Test
    public void testIsBoundedToVm() {
        Cloudlet cloudlet = createCloudlet(0);
        assertFalse(cloudlet.isBoundedToVm());
        cloudlet.setVmId(-1);
        assertFalse(cloudlet.isBoundedToVm());
        cloudlet.setVmId(0);
        assertTrue(cloudlet.isBoundedToVm());
        cloudlet.setVmId(1);
        assertTrue(cloudlet.isBoundedToVm());
    }

    private static CloudletSimple createCloudlet(int id) {
        return new CloudletSimple(id, 1, 1, 1, 1, 
                UtilizationModel.NULL, UtilizationModel.NULL, UtilizationModel.NULL);
    }
    
    @Test
    public void testNullObject(){
        assertFalse(Cloudlet.NULL.addRequiredFile(""));
        assertFalse(Cloudlet.NULL.deleteRequiredFile(""));
        assertEquals(0, Cloudlet.NULL.getAccumulatedBwCost(), 0);
        assertEquals(0, Cloudlet.NULL.getActualCPUTime(0), 0);
        assertEquals(0, Cloudlet.NULL.getActualCPUTime(), 0);
        assertEquals(0, Cloudlet.NULL.getClassType(), 0);
        assertEquals(0, Cloudlet.NULL.getCloudletFileSize(), 0);
        assertEquals(0, Cloudlet.NULL.getCloudletFinishedSoFar(), 0);
        assertEquals(0, Cloudlet.NULL.getCloudletFinishedSoFar(), 0);
        assertEquals("", Cloudlet.NULL.getCloudletHistory());
        assertEquals(0, Cloudlet.NULL.getId(), 0);
        assertEquals(0, Cloudlet.NULL.getCloudletLength(), 0);
        assertEquals(0, Cloudlet.NULL.getCloudletOutputSize(), 0);
        assertEquals(Cloudlet.Status.FAILED, Cloudlet.NULL.getCloudletStatus());
        assertEquals(Cloudlet.Status.FAILED, Cloudlet.NULL.getStatus());
        assertEquals("", Cloudlet.NULL.getCloudletStatusString());
        assertEquals(0, Cloudlet.NULL.getCloudletTotalLength(), 0);
        assertEquals(0, Cloudlet.NULL.getCostPerBw(), 0);
        assertEquals(0, Cloudlet.NULL.getCostPerSec(), 0);
        assertEquals(0, Cloudlet.NULL.getCostPerSec(0), 0);
        assertEquals(0, Cloudlet.NULL.getExecStartTime(), 0);
        assertEquals(0, Cloudlet.NULL.getNetServiceLevel(), 0);
        assertEquals(0, Cloudlet.NULL.getNumberOfPes(), 0);
        assertEquals(0, Cloudlet.NULL.getProcessingCost(), 0);
        assertTrue(Cloudlet.NULL.getRequiredFiles().isEmpty());
        assertEquals(0, Cloudlet.NULL.getReservationId(), 0);
        assertEquals(0, Cloudlet.NULL.getDatacenterId(), 0);
        assertEquals(0, Cloudlet.NULL.getDatacenterArrivalTime(), 0);
        assertEquals(0, Cloudlet.NULL.getSubmissionTime(0), 0);
        assertEquals(0, Cloudlet.NULL.getUserId(), 0);
        assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelBw());
        assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelCpu());
        assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelRam());
        assertEquals(0, Cloudlet.NULL.getUtilizationOfBw(0), 0);
        assertEquals(0, Cloudlet.NULL.getUtilizationOfCpu(0), 0);
        assertEquals(0, Cloudlet.NULL.getUtilizationOfRam(0), 0);
        Cloudlet.NULL.setVmId(100);
        assertEquals(Cloudlet.NOT_ASSIGNED, Cloudlet.NULL.getVmId(), 0);
        assertEquals(0, Cloudlet.NULL.getWaitingTime(), 0);
        assertEquals(0, Cloudlet.NULL.getWallClockTimeInLastExecutedDatacenter(), 0);
        assertEquals(0, Cloudlet.NULL.getWallClockTime(0), 0);
        assertFalse(Cloudlet.NULL.hasReserved());
        assertFalse(Cloudlet.NULL.isFinished());
        assertFalse(Cloudlet.NULL.requiresFiles());
        assertFalse(Cloudlet.NULL.setClassType(0));
        assertFalse(Cloudlet.NULL.setCloudletLength(0));
        assertFalse(Cloudlet.NULL.setCloudletStatus(Cloudlet.Status.SUCCESS));
        Cloudlet.NULL.setExecStartTime(100);
        assertEquals(0, Cloudlet.NULL.getExecStartTime(), 0);
        assertFalse(Cloudlet.NULL.setNetServiceLevel(0));
        assertFalse(Cloudlet.NULL.setNumberOfPes(0));
        assertFalse(Cloudlet.NULL.setReservationId(0));
        assertEquals(Cloudlet.NOT_ASSIGNED, Cloudlet.NULL.registerArrivalOfCloudletIntoDatacenter(), 0);
        assertSame(EventListener.NULL, Cloudlet.NULL.getOnCloudletFinishEventListener());
        EventListener listener = EasyMock.createMock(EventListener.class);
        EasyMock.replay(listener);
        Cloudlet.NULL.setOnCloudletFinishEventListener(listener);
        assertSame(EventListener.NULL, Cloudlet.NULL.getOnCloudletFinishEventListener());
        
        Cloudlet.NULL.setUserId(10);
        assertEquals(0, Cloudlet.NULL.getUserId(), 0);
        
        Cloudlet.NULL.setSubmissionDelay(10);
        assertEquals(0, Cloudlet.NULL.getSubmissionDelay(), 0);
        
        assertEquals(0, Cloudlet.NULL.getDatacenterId(), 0);
        assertEquals(0, Cloudlet.NULL.getCostPerSec(), 0);
        
        Cloudlet.NULL.assignCloudletToDatacenter(1, 1, 1);
        assertEquals(0, Cloudlet.NULL.getDatacenterId(), 0);
        assertEquals(0, Cloudlet.NULL.getCostPerSec(), 0);
        assertEquals(0, Cloudlet.NULL.getCostPerBw(), 0);

        UtilizationModel um = EasyMock.createMock(UtilizationModel.class);
        EasyMock.replay(um);
        Cloudlet.NULL.setUtilizationModelBw(um);
        assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelBw());
        
        Cloudlet.NULL.setUtilizationModelCpu(um);
        assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelCpu());
        
        Cloudlet.NULL.setUtilizationModelRam(um);
        assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelRam());
        
        assertFalse(Cloudlet.NULL.isBoundedToVm());
    }
    
}
