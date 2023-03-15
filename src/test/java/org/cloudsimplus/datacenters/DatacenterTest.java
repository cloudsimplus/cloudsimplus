package org.cloudsimplus.datacenters;

import org.cloudsimplus.allocationpolicies.VmAllocationPolicy;
import org.cloudsimplus.hosts.Host;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class DatacenterTest {
    @Test
    public void testNullObject() {
        final Datacenter instance = Datacenter.NULL;
        assertAll(
            () -> assertEquals(-1, instance.getId()),
            () -> assertEquals(0, instance.getSchedulingInterval()),
            () -> assertTrue(instance.getHostList().isEmpty()),
            () -> assertEquals(Host.NULL, instance.getHost(0)),
            () -> assertEquals(VmAllocationPolicy.NULL, instance.getVmAllocationPolicy())
        );
    }
}
