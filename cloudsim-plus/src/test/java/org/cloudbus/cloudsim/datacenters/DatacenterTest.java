package org.cloudbus.cloudsim.datacenters;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.hosts.Host;
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
            () -> assertEquals(VmAllocationPolicy.NULL, instance.getVmAllocationPolicy()),
            () -> assertTrue(instance.getVmList().isEmpty())
        );
    }
}
