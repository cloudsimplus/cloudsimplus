package org.cloudbus.cloudsim.schedulers.cloudlet;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class CloudletSchedulerTest {

    @Test
    public void testNullObject() {
        final CloudletScheduler instance = CloudletScheduler.NULL;
        assertAll(
            () -> assertEquals(Cloudlet.NULL, instance.cloudletCancel(null)),
            () -> assertEquals(0, instance.cloudletResume(null)),
            () -> assertEquals(0, instance.cloudletSubmit(null, 0)),
            () -> assertEquals(0, instance.cloudletSubmit(null)),
            () -> assertFalse(instance.cloudletPause(null)),
            () -> assertTrue(instance.getCloudletFinishedList().isEmpty()),
            () -> assertEquals(0, instance.getCurrentRequestedBwPercentUtilization()),
            () -> assertEquals(0, instance.getCurrentRequestedRamPercentUtilization()),
            () -> assertEquals(0, instance.getPreviousTime()),
            () -> assertEquals(0, instance.getRequestedCpuPercentUtilization(0)),
            () -> assertFalse(instance.hasFinishedCloudlets()),
            () -> assertEquals(0, instance.updateProcessing(0, null))
        );
    }

}
