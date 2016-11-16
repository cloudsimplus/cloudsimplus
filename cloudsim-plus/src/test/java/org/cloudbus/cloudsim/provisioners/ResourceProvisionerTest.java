package org.cloudbus.cloudsim.provisioners;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class ResourceProvisionerTest {


    @Test
    public void testNullObject() {
        checkNullObject(ResourceProvisioner.NULL);
    }

    private void checkNullObject(ResourceProvisioner instance) {
        assertFalse(instance.allocateResourceForVm(null, 0));
        assertEquals(0.0, instance.getAllocatedResourceForVm(null), 0.0);
        assertEquals(0.0, instance.getTotalAllocatedResource(), 0.0);
        assertFalse(instance.deallocateResourceForVm(null));
        instance.deallocateResourceForAllVms();
        assertFalse(instance.isSuitableForVm(null, 0));
        assertEquals(0.0, instance.getCapacity(), 0.0);
        assertEquals(0.0, instance.getAvailableResource(), 0.0);
    }

}
