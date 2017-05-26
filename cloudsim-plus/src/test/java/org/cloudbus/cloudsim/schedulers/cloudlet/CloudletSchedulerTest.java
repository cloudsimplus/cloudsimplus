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
        assertEquals(0, instance.getCurrentRequestedBwPercentUtilization(), 0);
        assertEquals(0, instance.getCurrentRequestedRamPercentUtilization(), 0);
        assertEquals(0, instance.getPreviousTime(), 0);
        assertEquals(0, instance.getAllocatedMipsForCloudlet(null, 0), 0);
        assertEquals(0, instance.getRequestedMipsForCloudlet(null, 0), 0);
        assertEquals(0, instance.getRequestedCpuPercentUtilization(0), 0);
        assertFalse(instance.hasFinishedCloudlets());
        assertEquals(Cloudlet.NULL, instance.getCloudletToMigrate());
        assertEquals(0, instance.runningCloudletsNumber());
        assertEquals(0, instance.updateProcessing(0, null), 0);
    }

}
