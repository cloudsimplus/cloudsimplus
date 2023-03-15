package org.cloudsimplus.provisioners;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class ResourceProvisionerTest {


    @Test
    public void testNullObject() {
        final ResourceProvisioner instance = ResourceProvisioner.NULL;
        assertAll(
            () -> assertFalse(instance.allocateResourceForVm(null, 0)),
            () -> assertEquals(0.0, instance.getAllocatedResourceForVm(null)),
            () -> assertEquals(0.0, instance.getTotalAllocatedResource()),
            () -> assertFalse(instance.isSuitableForVm(null, 0)),
            () -> assertEquals(0.0, instance.getCapacity()),
            () -> assertEquals(0.0, instance.getAvailableResource())
        );
    }

}
