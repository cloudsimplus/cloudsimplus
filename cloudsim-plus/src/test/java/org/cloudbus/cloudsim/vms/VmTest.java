package org.cloudbus.cloudsim.vms;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class VmTest {
    @Test
    public void testVmNullObject(){
        final Vm instance = Vm.NULL;

        instance.addStateHistoryEntry(null);
        assertTrue(instance.getStateHistory().isEmpty());
        assertEquals(CloudletScheduler.NULL, instance.getCloudletScheduler());
        assertFalse(instance.isInMigration());
        assertFalse(instance.isCreated());
        assertEquals(Host.NULL, instance.getHost());
        assertEquals("", instance.getUid());
        assertEquals(-1, instance.getId());
        assertSame(DatacenterBroker.NULL, instance.getBroker());
        assertEquals("", instance.getVmm());
        assertEquals(0, instance.updateProcessing(0, Collections.EMPTY_LIST), 0);
    }

    @Test
    public void testVmNullObjectListeners(){
        final Vm instance = Vm.NULL;
        instance.addOnHostAllocationListener(null);
        assertFalse(instance.removeOnHostAllocationListener(null));

        instance.addOnHostDeallocationListener(null);
        assertFalse(instance.removeOnHostDeallocationListener(null));

        instance.addOnCreationFailureListener(null);
        assertFalse(instance.removeOnCreationFailureListener(null));

        instance.addOnUpdateProcessingListener(null);
        assertFalse(instance.removeOnUpdateProcessingListener(null));
    }

    @Test
    public void testVmNullObjectResources(){
        final Vm instance = Vm.NULL;
        assertEquals(0, instance.getBw().getAllocatedResource());
        assertEquals(0, instance.getCurrentRequestedMaxMips(), 0);
        assertEquals(0, instance.getCurrentRequestedTotalMips(), 0);
        assertEquals(0, instance.getMips(), 0);
        assertEquals(0, instance.getNumberOfPes());
        assertEquals(0, instance.getCpuPercentUsage(0), 0);
        assertEquals(0, instance.getTotalCpuMipsUsage(0), 0);
        assertEquals(0, instance.getBw().getCapacity());
        assertEquals(0, instance.getBw().getAllocatedResource());
        assertEquals(0, instance.getCurrentRequestedBw());
        assertTrue(instance.getCurrentRequestedMips().isEmpty());
        assertEquals(0, instance.getCurrentRequestedRam());
        assertEquals(0, instance.getRam().getCapacity());
        assertEquals(0, instance.getRam().getAllocatedResource());
        assertEquals(0, instance.getStorage().getCapacity());
    }

}
