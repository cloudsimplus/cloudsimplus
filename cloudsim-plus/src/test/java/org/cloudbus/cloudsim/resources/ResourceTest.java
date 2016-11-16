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
        checkNullObject(ResourceManageable.NULL);
    }

    private void checkNullObject(ResourceManageable instance) {
        assertFalse(instance.setCapacity(0));
        assertFalse(instance.allocateResource(0));
        assertFalse(instance.setAllocatedResource(0));
        assertFalse(instance.deallocateResource(0));
        assertEquals(0, instance.deallocateAllResources(), 0.0);
        assertFalse(instance.isResourceAmountBeingUsed(0));
        assertFalse(instance.isSuitable(0));
    }

}
