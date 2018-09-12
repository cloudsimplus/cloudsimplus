package org.cloudbus.cloudsim.cloudlets;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmTestUtil;
import org.cloudsimplus.listeners.EventListener;
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
        final Cloudlet cloudlet = createCloudlet(0);
        assertFalse(cloudlet.isBindToVm());
        cloudlet.setVm(Vm.NULL);
        assertFalse(cloudlet.isBindToVm());
        cloudlet.setVm(VmTestUtil.createVm(0, 1));
        assertTrue(cloudlet.isBindToVm());
        cloudlet.setVm(VmTestUtil.createVm(1, 1));
        assertTrue(cloudlet.isBindToVm());
    }

    private static CloudletSimple createCloudlet(int id) {
        return new CloudletSimple(id, 1, 1);
    }

    @Test
    public void testNullObject(){
        assertFalse(Cloudlet.NULL.addRequiredFile(""));
        assertFalse(Cloudlet.NULL.deleteRequiredFile(""));
        assertEquals(0, Cloudlet.NULL.getPriority(), 0);
        assertEquals(0, Cloudlet.NULL.getFileSize(), 0);
        assertEquals(-1, Cloudlet.NULL.getId());
        assertEquals(0, Cloudlet.NULL.getLength(), 0);
        assertEquals(0, Cloudlet.NULL.getOutputSize(), 0);
        assertEquals(0, Cloudlet.NULL.getTotalLength(), 0);

        assertEquals(0, Cloudlet.NULL.getNetServiceLevel(), 0);
        assertEquals(0, Cloudlet.NULL.getNumberOfPes(), 0);

        assertSame(DatacenterBroker.NULL, Cloudlet.NULL.getBroker());

        assertEquals(Vm.NULL, Cloudlet.NULL.getVm());
        assertFalse(Cloudlet.NULL.requiresFiles());
        assertEquals(Cloudlet.NOT_ASSIGNED, Cloudlet.NULL.registerArrivalInDatacenter(), 0);
        assertFalse(Cloudlet.NULL.removeOnFinishListener(null));
        final EventListener listener = EasyMock.createMock(EventListener.class);
        EasyMock.replay(listener);
        Cloudlet.NULL.addOnFinishListener(listener);

        assertFalse(Cloudlet.NULL.isBindToVm());
    }

    @Test
    public void testNullObjectStatus(){
        assertFalse(Cloudlet.NULL.setStatus(Cloudlet.Status.SUCCESS));
        assertEquals(Cloudlet.Status.FAILED, Cloudlet.NULL.getStatus());
        assertTrue(Cloudlet.NULL.getRequiredFiles().isEmpty());
        assertEquals(0, Cloudlet.NULL.getFinishedLengthSoFar(), 0);
        assertEquals(Datacenter.NULL, Cloudlet.NULL.getLastDatacenter());
        assertEquals(0, Cloudlet.NULL.getLastDatacenterArrivalTime(), 0);
        assertFalse(Cloudlet.NULL.isFinished());
        Cloudlet.NULL.assignToDatacenter(Datacenter.NULL);
        assertEquals(Datacenter.NULL, Cloudlet.NULL.getLastDatacenter());

    }

    @Test
    public void testNullObjectTimes(){
        assertEquals(0, Cloudlet.NULL.getArrivalTime(Datacenter.NULL), 0);
        assertEquals(0, Cloudlet.NULL.getActualCpuTime(Datacenter.NULL), 0);
        assertEquals(0, Cloudlet.NULL.getActualCpuTime(), 0);
        assertEquals(0, Cloudlet.NULL.getExecStartTime(), 0);
        assertEquals(0, Cloudlet.NULL.getWaitingTime(), 0);
        assertEquals(0, Cloudlet.NULL.getWallClockTimeInLastExecutedDatacenter(), 0);
        assertEquals(0, Cloudlet.NULL.getWallClockTime(Datacenter.NULL), 0);
        Cloudlet.NULL.setSubmissionDelay(10);
        assertEquals(0, Cloudlet.NULL.getSubmissionDelay(), 0);
    }

    @Test
    public void testNullObjectUtilization(){
        assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelBw());
        assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelCpu());
        assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelRam());
        assertEquals(0, Cloudlet.NULL.getUtilizationOfBw(0), 0);
        assertEquals(0, Cloudlet.NULL.getUtilizationOfCpu(0), 0);
        assertEquals(0, Cloudlet.NULL.getUtilizationOfRam(0), 0);

        final UtilizationModel model = EasyMock.createMock(UtilizationModel.class);
        EasyMock.replay(model);
        Cloudlet.NULL.setUtilizationModelBw(model);
        assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelBw());

        Cloudlet.NULL.setUtilizationModelCpu(model);
        assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelCpu());

        Cloudlet.NULL.setUtilizationModelRam(model);
        assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelRam());
    }

    @Test
    public void testNullObjectCost(){
        assertEquals(0, Cloudlet.NULL.getAccumulatedBwCost(), 0);
        assertEquals(0, Cloudlet.NULL.getCostPerBw(), 0);
        assertEquals(0, Cloudlet.NULL.getCostPerSec(), 0);
        assertEquals(0, Cloudlet.NULL.getCostPerSec(Datacenter.NULL), 0);
        assertEquals(0, Cloudlet.NULL.getTotalCost(), 0);
    }


}
