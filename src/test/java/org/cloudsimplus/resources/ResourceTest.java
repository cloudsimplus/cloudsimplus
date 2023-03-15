package org.cloudsimplus.resources;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class ResourceTest {

    @Test
    public void testNullObject() {
        final ResourceManageable instance = ResourceManageable.NULL;
        assertAll(
            () -> assertFalse(instance.setCapacity(0)),
            () -> assertFalse(instance.allocateResource(0)),
            () -> assertFalse(instance.setAllocatedResource(0)),
            () -> assertFalse(instance.deallocateResource(0)),
            () -> assertEquals(0, instance.deallocateAllResources()),
            () -> assertFalse(instance.isResourceAmountBeingUsed(0)),
            () -> assertFalse(instance.isSuitable(0))
        );
    }

}
