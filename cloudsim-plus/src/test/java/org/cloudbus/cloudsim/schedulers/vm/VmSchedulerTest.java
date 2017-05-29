package org.cloudbus.cloudsim.schedulers.vm;

import org.cloudbus.cloudsim.vms.Vm;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class VmSchedulerTest {

    @Test
    public void testNullObject() {
        final VmScheduler instance = VmScheduler.NULL;
        instance.deallocatePesForAllVms();
        instance.deallocatePesFromVm(null);
        assertFalse(instance.allocatePesForVm(null, null));
        assertTrue(instance.getAllocatedMips(null).isEmpty());
        assertEquals(0, instance.getAvailableMips(), 0);
        assertFalse(instance.isSuitableForVm(Vm.NULL));
        assertEquals(0, instance.getMaxAvailableMips(), 0);
        assertEquals(0, instance.getPeCapacity(), 0);
        assertTrue(instance.getWorkingPeList().isEmpty());
        assertTrue(instance.getPesAllocatedForVm(null).isEmpty());
        assertEquals(0, instance.getTotalAllocatedMipsForVm(null), 0);
        assertEquals(0, instance.getVmMigrationCpuOverhead(), 0);
    }

}
