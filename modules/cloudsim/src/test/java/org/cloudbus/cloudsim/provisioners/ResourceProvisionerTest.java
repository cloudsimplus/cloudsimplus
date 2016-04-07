package org.cloudbus.cloudsim.provisioners;

import org.cloudbus.cloudsim.Vm;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class ResourceProvisionerTest {
    

    @Test
    public void testNullObject() {
        checkNullObject(ResourceProvisioner.NULL_DOUBLE);
        checkNullObject(ResourceProvisioner.NULL_LONG);
        checkNullObject(ResourceProvisioner.NULL_INT);
    }

    private void checkNullObject(ResourceProvisioner instance) {
        assertFalse(instance.allocateResourceForVm(null, null));
        assertEquals(0.0, instance.getAllocatedResourceForVm(null).doubleValue(), 0.0);
        assertEquals(0.0, instance.getTotalAllocatedResource().doubleValue(), 0.0);
        assertFalse(instance.deallocateResourceForVm(null));
        instance.deallocateResourceForAllVms();
        assertFalse(instance.isSuitableForVm(null, null));
        assertEquals(0.0, instance.getCapacity().doubleValue(), 0.0);
        assertEquals(0.0, instance.getAvailableResource().doubleValue(), 0.0);
    }
    
}
