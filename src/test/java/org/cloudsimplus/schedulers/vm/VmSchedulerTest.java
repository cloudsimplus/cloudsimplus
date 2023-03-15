package org.cloudsimplus.schedulers.vm;

import org.cloudsimplus.vms.Vm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class VmSchedulerTest {

    @Test
    public void testNullObject() {
        final VmScheduler instance = VmScheduler.NULL;
        assertAll(
            () -> assertFalse(instance.allocatePesForVm(null, null)),
            () -> assertTrue(instance.getAllocatedMips(null).isEmpty()),
            () -> assertEquals(0, instance.getTotalAvailableMips()),
            () -> assertFalse(instance.isSuitableForVm(Vm.NULL)),
            () -> assertEquals(0, instance.getTotalAllocatedMipsForVm(null)),
            () -> assertEquals(0, instance.getVmMigrationCpuOverhead())
        );
    }

}
