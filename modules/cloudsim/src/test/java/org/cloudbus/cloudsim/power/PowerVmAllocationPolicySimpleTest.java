package org.cloudbus.cloudsim.power;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Vm;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class PowerVmAllocationPolicySimpleTest {
    @Test
    public void testNew() {
        System.out.println("optimizeAllocation");
        List<PowerHost> hosts = new ArrayList<>();        
        try {
            hosts.add(PowerHost.NULL);
            new PowerVmAllocationPolicySimple(hosts);            
        } catch (Exception e) {
            fail("An exception should not be raised when creating a PowerVmAllocationPolicySimple with non empty host list");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNew_emptyHostList() {
        List<PowerHost> hosts = new ArrayList<>();
        new PowerVmAllocationPolicySimple(hosts);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNew_nullHostList() {
        new PowerVmAllocationPolicySimple(null);
    }

    @Test
    public void testOptimizeAllocation() {
        System.out.println("optimizeAllocation");
        List<PowerHost> hosts = new ArrayList<>();
        hosts.add(PowerHost.NULL);
        PowerVmAllocationPolicySimple instance = new PowerVmAllocationPolicySimple(hosts);
        
        assertNotNull(instance.optimizeAllocation(null));
        
        List<Vm> vmList = new ArrayList<>();
        assertNotNull(instance.optimizeAllocation(vmList));
        assertTrue(instance.optimizeAllocation(vmList).isEmpty());
        vmList.add(Vm.NULL);
        assertTrue(instance.optimizeAllocation(vmList).isEmpty());
    }
    
}
