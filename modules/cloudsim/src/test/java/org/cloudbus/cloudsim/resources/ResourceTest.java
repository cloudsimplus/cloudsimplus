package org.cloudbus.cloudsim.resources;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class ResourceTest {
    
    @Test
    public void testNullObject() {
        System.out.println("testNullObject");
        checkNullObject(ResourceManageable.NULL_DOUBLE);
        checkNullObject(ResourceManageable.NULL_INT);
        checkNullObject(ResourceManageable.NULL_LONG);
    }

    private void checkNullObject(ResourceManageable instance) {
        assertFalse(instance.setCapacity(null));
        assertFalse(instance.allocateResource(null));
        assertFalse(instance.setAllocatedResource(null));
        assertFalse(instance.deallocateResource(null));
        assertEquals(0.0, instance.deallocateAllResources().doubleValue(), 0.0);
        assertFalse(instance.isResourceAmountBeingUsed(null));
        assertFalse(instance.isSuitable(null));
    }
    
}
