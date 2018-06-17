package org.cloudbus.cloudsim.datacenters;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.hosts.Host;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class DatacenterTest {

    @Test
    public void testNullObject() {
        final Datacenter instance = Datacenter.NULL;
        assertEquals(-1, instance.getId());
        assertEquals(0, instance.getSchedulingInterval(), 0);
        assertTrue(instance.getHostList().isEmpty());
        assertEquals(Host.NULL, instance.getHost(0));
        assertEquals(VmAllocationPolicy.NULL, instance.getVmAllocationPolicy());
        assertTrue(instance.getVmList().isEmpty());
    }

}
