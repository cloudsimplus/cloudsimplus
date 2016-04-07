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
        checkNullObject(Resource.NULL_DOUBLE);
        checkNullObject(Resource.NULL_INT);
        checkNullObject(Resource.NULL_LONG);
    }

    private void checkNullObject(Resource instance) {
        assertFalse(instance.setCapacity(null));
        assertFalse(instance.allocateResource(null));
        assertFalse(instance.setAllocatedResource(null));
        assertFalse(instance.deallocateResource(null));
        assertEquals(0.0, instance.deallocateAllResources().doubleValue(), 0.0);
        assertFalse(instance.isResourceAmountBeingUsed(null));
        assertFalse(instance.isSuitable(null));
    }
    
}
