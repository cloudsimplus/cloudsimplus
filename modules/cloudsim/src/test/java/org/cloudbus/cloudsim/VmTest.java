package org.cloudbus.cloudsim;

import java.util.Collections;
import org.cloudbus.cloudsim.listeners.EventListener;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.schedulers.CloudletScheduler;
import org.junit.Test;
import static org.junit.Assert.*;
import org.cloudbus.cloudsim.resources.ResourceManageable;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class VmTest {
    @Test
    public void testNullObject(){
        final Vm instance = Vm.NULL;
        
        instance.addStateHistoryEntry(null);
        assertTrue(instance.getStateHistory().isEmpty());

        assertEquals(CloudletScheduler.NULL, instance.getCloudletScheduler());
        
        assertEquals(0, instance.getCurrentAllocatedSize());
        assertEquals(0, instance.getCurrentRequestedMaxMips(), 0);
        assertEquals(0, instance.getCurrentRequestedTotalMips(), 0);
        assertEquals(0, instance.getMips(), 0);
        assertEquals(0, instance.getNumberOfPes());
        
        assertEquals(ResourceManageable.NULL_DOUBLE, instance.getResource(null));
        
        assertEquals(0, instance.getTotalUtilizationOfCpu(0), 0);
        assertEquals(0, instance.getTotalUtilizationOfCpuMips(0), 0);
        
        instance.setInMigration(true);
        assertFalse(instance.isInMigration());
        
        instance.setBeingInstantiated(true);
        assertFalse(instance.isBeingInstantiated());
        
        assertFalse(instance.setBw(1000));
        assertEquals(0, instance.getBw());
        
        instance.setCurrentAllocatedBw(1000);
        assertEquals(0, instance.getCurrentAllocatedBw());
        assertEquals(0, instance.getCurrentRequestedBw());
        
        instance.setCurrentAllocatedMips(null);
        assertTrue(instance.getCurrentAllocatedMips().isEmpty());
        assertTrue(instance.getCurrentRequestedMips().isEmpty());
        
        instance.setCurrentAllocatedRam(1000);
        assertEquals(0, instance.getCurrentAllocatedRam());
        assertEquals(0, instance.getCurrentRequestedRam());
        
        instance.setHost(PowerHost.NULL);
        assertEquals(Host.NULL, instance.getHost());
        
        instance.setOnHostAllocationListener(null);
        assertEquals(EventListener.NULL, instance.getOnHostAllocationListener());

        instance.setOnHostDeallocationListener(null);
        assertEquals(EventListener.NULL, instance.getOnHostDeallocationListener());

        instance.setOnVmCreationFailureListener(null);
        assertEquals(EventListener.NULL, instance.getOnVmCreationFailureListener());
        
        instance.setOnUpdateVmProcessingListener(null);
        assertEquals(EventListener.NULL, instance.getOnUpdateVmProcessingListener());
        
        assertFalse(instance.setRam(1000));
        assertEquals(0, instance.getRam());
        
        assertFalse(instance.setSize(1000));
        assertEquals(0, instance.getSize());

        instance.setUid("123");
        assertEquals("", instance.getUid());
        assertEquals(0, instance.getId());
        assertEquals(0, instance.getUserId(), 0);
        assertEquals("", instance.getVmm());
        
        assertEquals(0, instance.updateVmProcessing(0, Collections.EMPTY_LIST), 0);
    }
    
}
