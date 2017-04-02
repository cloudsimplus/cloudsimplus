package org.cloudbus.cloudsim.allocationpolicies.power;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.vms.Vm;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class PowerVmAllocationPolicySimpleTest {

    @Test
    public void testOptimizeAllocation() {
        final List<PowerHost> hosts = new ArrayList<>();
        hosts.add(PowerHost.NULL);
        final PowerVmAllocationPolicySimple instance = new PowerVmAllocationPolicySimple();

        assertNotNull(instance.optimizeAllocation(null));

        final List<Vm> vmList = new ArrayList<>();
        assertNotNull(instance.optimizeAllocation(vmList));
        assertTrue(instance.optimizeAllocation(vmList).isEmpty());
        vmList.add(Vm.NULL);
        assertTrue(instance.optimizeAllocation(vmList).isEmpty());
    }

}
