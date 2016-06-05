package org.cloudbus.cloudsim;

import java.util.Collections;
import org.cloudbus.cloudsim.listeners.EventListener;
import org.cloudbus.cloudsim.listeners.HostUpdatesVmsProcessingEventInfo;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.schedulers.VmScheduler;
import org.easymock.EasyMock;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class HostTest {

    @Test
    public void testNullObject() {
        System.out.println("testNullObject");
        final Host instance = Host.NULL;
        final Vm vm = Vm.NULL;
        
        instance.addMigratingInVm(vm);
        assertTrue(instance.getVmsMigratingIn().isEmpty());
        instance.deallocatePesForVm(null);
        assertFalse(instance.allocatePesForVm(vm, Collections.EMPTY_LIST));
        assertTrue(instance.getAllocatedMipsForVm(vm).isEmpty());
        assertEquals(0, instance.getAvailableMips(), 0);
        assertEquals(0, instance.getAvailableStorage(), 0);
     
        assertEquals(0, instance.getBwCapacity(), 0);
        assertEquals(ResourceProvisioner.NULL_LONG, instance.getBwProvisioner());
        assertEquals(0, instance.getMaxAvailableMips(), 0);
        assertEquals(0, instance.getNumberOfFreePes(), 0);
        assertEquals(0, instance.getNumberOfPes(), 0);
        assertTrue(instance.getPeList().isEmpty());
        assertEquals(0, instance.getRamCapacity(), 0);
        assertEquals(ResourceProvisioner.NULL_INT, instance.getRamProvisioner());
        assertEquals(0, instance.getStorageCapacity(), 0);
        assertEquals(0, instance.getTotalAllocatedMipsForVm(vm), 0);
        assertEquals(0, instance.getTotalMips(), 0);
        assertEquals(Vm.NULL, instance.getVm(0,0));
        assertEquals(VmScheduler.NULL, instance.getVmScheduler());
        assertFalse(instance.isFailed());
        assertFalse(instance.isSuitableForVm(vm));
        
        instance.reallocateMigratingInVms();
        assertTrue(instance.getVmsMigratingIn().isEmpty());
        assertTrue(instance.getVmList().isEmpty());
        
        instance.removeMigratingInVm(vm);
        assertTrue(instance.getVmsMigratingIn().isEmpty());
        assertTrue(instance.getVmList().isEmpty());
        
        instance.setDatacenter(createMockDatacenter());
        assertEquals(Datacenter.NULL, instance.getDatacenter());
        
        assertFalse(instance.setFailed("", false));
        assertFalse(instance.setFailed(false));
        assertFalse(instance.setPeStatus(0, Pe.Status.FREE));
        assertEquals(0, instance.getId());
        assertEquals(0, instance.updateVmsProcessing(0), 0);
        assertFalse(instance.vmCreate(vm));
        assertTrue(instance.getVmList().isEmpty());
        
        instance.vmDestroy(vm);
        assertTrue(instance.getVmList().isEmpty());
        instance.vmDestroyAll();
        assertTrue(instance.getVmList().isEmpty());
        
        instance.setOnUpdateVmsProcessingListener(createMockListener());
        assertEquals(EventListener.NULL, instance.getOnUpdateVmsProcessingListener());
    }    

    private Datacenter createMockDatacenter() {
        Datacenter dc = EasyMock.createMock(Datacenter.class);
        EasyMock.replay(dc);
        return dc;
    }

    private EventListener<HostUpdatesVmsProcessingEventInfo> createMockListener() {
        EventListener l = EasyMock.createMock(EventListener.class);
        EasyMock.replay(l);
        return l;
    }
}
