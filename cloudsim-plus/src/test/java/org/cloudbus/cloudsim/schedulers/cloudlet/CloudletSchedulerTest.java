package org.cloudbus.cloudsim.schedulers.cloudlet;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class CloudletSchedulerTest {

    @Test
    public void testNullObject() {
        System.out.println("cloudletCancel");
        final CloudletScheduler instance = CloudletScheduler.NULL;
        assertEquals(Cloudlet.NULL, instance.cloudletCancel(0));

        instance.cloudletFinish(null);
        assertEquals(0, instance.cloudletResume(0), 0);
        assertEquals(0, instance.cloudletSubmit(null, 0), 0);
        assertEquals(0, instance.cloudletSubmit(null), 0);

        assertFalse(instance.cloudletPause(0));
        assertEquals(0, instance.getCloudletStatus(0));
        assertTrue(instance.getCloudletFinishedList().isEmpty());
        assertTrue(instance.getCurrentMipsShare().isEmpty());
        assertTrue(instance.getCurrentRequestedMips().isEmpty());
        assertEquals(0, instance.getCurrentRequestedUtilizationOfBw(), 0);
        assertEquals(0, instance.getCurrentRequestedUtilizationOfRam(), 0);
        assertEquals(Cloudlet.NULL, instance.removeNextFinishedCloudlet());
        assertEquals(0, instance.getPreviousTime(), 0);
        assertEquals(0, instance.getTotalCurrentAllocatedMipsForCloudlet(null, 0), 0);
        assertEquals(0, instance.getTotalCurrentAvailableMipsForCloudlet(null, null), 0);
        assertEquals(0, instance.getTotalCurrentRequestedMipsForCloudlet(null, 0), 0);
        assertEquals(0, instance.getTotalUtilizationOfCpu(0), 0);
        assertFalse(instance.hasFinishedCloudlets());
        assertEquals(Cloudlet.NULL, instance.getCloudletToMigrate());
        assertEquals(0, instance.runningCloudletsNumber());
        assertEquals(0, instance.updateVmProcessing(0, null), 0);
    }

}
