package org.cloudbus.cloudsim.cloudlets;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmTestUtil;
import org.cloudsimplus.listeners.EventListener;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(0, Cloudlet.NULL.getPriority());
        assertEquals(0, Cloudlet.NULL.getFileSize());
        assertEquals(-1, Cloudlet.NULL.getId());
        assertEquals(0, Cloudlet.NULL.getLength());
        assertEquals(0, Cloudlet.NULL.getOutputSize());
        assertEquals(0, Cloudlet.NULL.getTotalLength());

        assertEquals(0, Cloudlet.NULL.getNetServiceLevel());
        assertEquals(0, Cloudlet.NULL.getNumberOfPes());

        assertSame(DatacenterBroker.NULL, Cloudlet.NULL.getBroker());

        assertEquals(Vm.NULL, Cloudlet.NULL.getVm());
        assertFalse(Cloudlet.NULL.requiresFiles());
        assertEquals(Cloudlet.NOT_ASSIGNED, Cloudlet.NULL.registerArrivalInDatacenter());
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
        assertEquals(0, Cloudlet.NULL.getFinishedLengthSoFar());
        assertEquals(Datacenter.NULL, Cloudlet.NULL.getLastDatacenter());
        assertEquals(0, Cloudlet.NULL.getLastDatacenterArrivalTime());
        assertFalse(Cloudlet.NULL.isFinished());
        Cloudlet.NULL.assignToDatacenter(Datacenter.NULL);
        assertEquals(Datacenter.NULL, Cloudlet.NULL.getLastDatacenter());

    }

    @Test
    public void testNullObjectTimes(){
        assertAll(
            () -> assertEquals(0, Cloudlet.NULL.getArrivalTime(Datacenter.NULL)),
            () -> assertEquals(0, Cloudlet.NULL.getActualCpuTime(Datacenter.NULL)),
            () -> assertEquals(0, Cloudlet.NULL.getActualCpuTime()),
            () -> assertEquals(0, Cloudlet.NULL.getExecStartTime()),
            () -> assertEquals(0, Cloudlet.NULL.getWaitingTime()),
            () -> assertEquals(0, Cloudlet.NULL.getWallClockTimeInLastExecutedDatacenter()),
            () -> assertEquals(0, Cloudlet.NULL.getWallClockTime(Datacenter.NULL)),
            () -> Cloudlet.NULL.setSubmissionDelay(10),
            () -> assertEquals(0, Cloudlet.NULL.getSubmissionDelay())
        );
    }

    @Test
    public void testNullObjectUtilization1() {
        assertAll(
            () -> assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelBw()),
            () -> assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelCpu()),
            () -> assertSame(UtilizationModel.NULL, Cloudlet.NULL.getUtilizationModelRam()),
            () -> assertEquals(0, Cloudlet.NULL.getUtilizationOfBw(0)),
            () -> assertEquals(0, Cloudlet.NULL.getUtilizationOfCpu(0)),
            () -> assertEquals(0, Cloudlet.NULL.getUtilizationOfRam(0))
        );
    }

    @Test
    public void testNullObjectUtilization2(){
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
        assertAll(
            () -> assertEquals(0, Cloudlet.NULL.getAccumulatedBwCost()),
            () -> assertEquals(0, Cloudlet.NULL.getCostPerBw()),
            () -> assertEquals(0, Cloudlet.NULL.getCostPerSec()),
            () -> assertEquals(0, Cloudlet.NULL.getCostPerSec(Datacenter.NULL)),
            () -> assertEquals(0, Cloudlet.NULL.getTotalCost())
        );
    }


}
