package org.cloudbus.cloudsim.datacenters;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class DatacenterTest {

    @Test
    public void testNullObject() {
        final Datacenter instance = Datacenter.NULL;
        assertEquals(0, instance.addFile(null));
        assertEquals(-1, instance.getId());
        assertEquals(0, instance.getSchedulingInterval(), 0);
        assertTrue(instance.getHostList().isEmpty());
        Assert.assertEquals(Host.NULL, instance.getHost(0));
        assertEquals(VmAllocationPolicy.NULL, instance.getVmAllocationPolicy());
        assertTrue(instance.getVmList().isEmpty());
    }

}
