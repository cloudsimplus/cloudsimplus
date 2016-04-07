package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class DatacenterTest {
    
    @Test
    public void testNullObject() {
        System.out.println("addFile");
        final Datacenter instance = Datacenter.NULL;
        assertEquals(0, instance.addFile(null));
        assertTrue(instance.getHostList().isEmpty());
        assertEquals(Host.NULL, instance.getHost(0));
        assertEquals(VmAllocationPolicy.NULL, instance.getVmAllocationPolicy());
        assertTrue(instance.getVmList().isEmpty());
    }
    
}
