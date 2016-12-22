package org.cloudbus.cloudsim.cloudlets;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimpleTest;
import org.cloudsimplus.listeners.EventListener;
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
        assertFalse(cloudlet.isBindToVm());
        cloudlet.setVm(Vm.NULL);
        assertFalse(cloudlet.isBindToVm());
        cloudlet.setVm(VmSimpleTest.createVm(0, 1));
        assertTrue(cloudlet.isBindToVm());
        cloudlet.setVm(VmSimpleTest.createVm(1, 1));
        assertTrue(cloudlet.isBindToVm());
    }

    private static CloudletSimple createCloudlet(int id) {
        return new CloudletSimple(id, 1, 1);
    }

    @Test
    public void testNullObject(){
        assertFalse(Cloudlet.NULL.addRequiredFile(""));
        assertFalse(Cloudlet.NULL.deleteRequiredFile(""));
        assertEquals(0, Cloudlet.NULL.getAccumulatedBwCost(), 0);
        assertEquals(0, Cloudlet.NULL.getActualCpuTime(Datacenter.NULL), 0);
        assertEquals(0, Cloudlet.NULL.getActualCpuTime(), 0);
        assertEquals(0, Cloudlet.NULL.getPriority(), 0);
        assertEquals(0, Cloudlet.NULL.getFileSize(), 0);
        assertEquals(0, Cloudlet.NULL.getFinishedLengthSoFar(), 0);
        assertEquals(0, Cloudlet.NULL.getFinishedLengthSoFar(), 0);
        assertEquals("", Cloudlet.NULL.getHistory());
        assertEquals(-1, Cloudlet.NULL.getId());
        assertEquals(0, Cloudlet.NULL.getLength(), 0);
        assertEquals(0, Cloudlet.NULL.getOutputSize(), 0);
        assertEquals(Cloudlet.Status.FAILED, Cloudlet.NULL.getStatus());
        assertEquals(Cloudlet.Status.FAILED, Cloudlet.NULL.getStatus());
        assertEquals(0, Cloudlet.NULL.getTotalLength(), 0);
        assertEquals(0, Cloudlet.NULL.getCostPerBw(), 0);
        assertEquals(0, Cloudlet.NULL.getCostPerSec(), 0);
        assertEquals(0, Cloudlet.NULL.getCostPerSec(Datacenter.NULL), 0);
        assertEquals(0, Cloudlet.NULL.getExecStartTime(), 0);
        assertEquals(0, Cloudlet.NULL.getNetServiceLevel(), 0);
        assertEquals(0, Cloudlet.NULL.getNumberOfPes(), 0);
        assertEquals(0, Cloudlet.NULL.getTotalCost(), 0);
        assertTrue(Cloudlet.NULL.getRequiredFiles().isEmpty());
        assertEquals(-1, Cloudlet.NULL.getReservationId());
        assertEquals(Datacenter.NULL, Cloudlet.NULL.getLastDatacenter());
        assertEquals(0, Cloudlet.NULL.getLastDatacenterArrivalTime(), 0);
        assertEquals(0, Cloudlet.NULL.getArrivalTime(Datacenter.NULL), 0);
        assertSame(DatacenterBroker.NULL, Cloudlet.NULL.getBroker());
        assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelBw());
        assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelCpu());
        assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelRam());
        assertEquals(0, Cloudlet.NULL.getUtilizationOfBw(0), 0);
        assertEquals(0, Cloudlet.NULL.getUtilizationOfCpu(0), 0);
        assertEquals(0, Cloudlet.NULL.getUtilizationOfRam(0), 0);
        assertEquals(Vm.NULL, Cloudlet.NULL.getVm());
        assertEquals(0, Cloudlet.NULL.getWaitingTime(), 0);
        assertEquals(0, Cloudlet.NULL.getWallClockTimeInLastExecutedDatacenter(), 0);
        assertEquals(0, Cloudlet.NULL.getWallClockTime(Datacenter.NULL), 0);
        assertFalse(Cloudlet.NULL.isReserved());
        assertFalse(Cloudlet.NULL.isFinished());
        assertFalse(Cloudlet.NULL.requiresFiles());
        assertFalse(Cloudlet.NULL.setStatus(Cloudlet.Status.SUCCESS));
        Cloudlet.NULL.setExecStartTime(100);
        assertEquals(0, Cloudlet.NULL.getExecStartTime(), 0);
        assertFalse(Cloudlet.NULL.setNetServiceLevel(0));
        assertFalse(Cloudlet.NULL.setReservationId(0));
        assertEquals(Cloudlet.NOT_ASSIGNED, Cloudlet.NULL.registerArrivalInDatacenter(), 0);
        assertFalse(Cloudlet.NULL.removeOnCloudletFinishListener(null));
        EventListener listener = EasyMock.createMock(EventListener.class);
        EasyMock.replay(listener);
        Cloudlet.NULL.addOnCloudletFinishListener(listener);
        Cloudlet.NULL.setSubmissionDelay(10);
        assertEquals(0, Cloudlet.NULL.getSubmissionDelay(), 0);

        assertEquals(0, Cloudlet.NULL.getCostPerSec(), 0);

        Cloudlet.NULL.assignToDatacenter(Datacenter.NULL);
        assertEquals(Datacenter.NULL, Cloudlet.NULL.getLastDatacenter());
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

        assertFalse(Cloudlet.NULL.isBindToVm());
    }

}
