package org.cloudbus.cloudsim.hosts;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.vms.Vm;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class HostTest {

    @Test
    public void testNullObject() {
        final Host instance = Host.NULL;
        final Vm vm = Vm.NULL;

        assertTrue(instance.getAllocatedMipsForVm(vm).isEmpty());
        assertEquals(0, instance.getAvailableMips(), 0);
        assertEquals(0, instance.getAvailableStorage(), 0);

        assertEquals(0, instance.getBw().getCapacity(), 0);
        assertEquals(ResourceProvisioner.NULL, instance.getBwProvisioner());
        assertEquals(0, instance.getMaxAvailableMips(), 0);
        assertEquals(0, instance.getNumberOfFreePes(), 0);
        assertEquals(0, instance.getNumberOfPes(), 0);
        assertEquals(0, instance.getRam().getCapacity(), 0);
        assertEquals(ResourceProvisioner.NULL, instance.getRamProvisioner());
        assertEquals(0, instance.getStorage().getCapacity(), 0);
        assertEquals(0, instance.getTotalAllocatedMipsForVm(vm), 0);
        assertEquals(0, instance.getTotalMipsCapacity(), 0);
        assertSame(Vm.NULL, instance.getVm(0,0));
        assertSame(VmScheduler.NULL, instance.getVmScheduler());
        assertFalse(instance.isFailed());
        assertFalse(instance.isSuitableForVm(vm));

        assertTrue(instance.getPeList().isEmpty());
        assertTrue(instance.getVmsMigratingIn().isEmpty());

        assertSame(Datacenter.NULL, instance.getDatacenter());
        assertEquals(-1, instance.getId());
        assertTrue(instance.getVmList().isEmpty());
    }
}
