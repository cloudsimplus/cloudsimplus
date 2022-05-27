package org.cloudbus.cloudsim.hosts;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.vms.Vm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class HostTest {

    @Test
    public void testNullObject() {
        final Host instance = Host.NULL;
        final Vm vm = Vm.NULL;

        assertAll(
            () -> assertEquals(0, instance.getTotalAvailableMips()),
            () -> assertEquals(0, instance.getAvailableStorage()),
            () -> assertEquals(0, instance.getBw().getCapacity()),
            () -> assertEquals(ResourceProvisioner.NULL, instance.getBwProvisioner()),
            () -> assertEquals(0, instance.getFreePesNumber()),
            () -> assertEquals(0, instance.getNumberOfPes()),
            () -> assertEquals(0, instance.getRam().getCapacity()),
            () -> assertEquals(ResourceProvisioner.NULL, instance.getRamProvisioner()),
            () -> assertEquals(0, instance.getStorage().getCapacity()),
            () -> assertEquals(0, instance.getTotalAllocatedMipsForVm(vm)),
            () -> assertEquals(0, instance.getTotalMipsCapacity()),
            () -> assertSame(VmScheduler.NULL, instance.getVmScheduler()),
            () -> assertFalse(instance.isFailed()),
            () -> assertFalse(instance.isSuitableForVm(vm)),
            () -> assertTrue(instance.getPeList().isEmpty()),
            () -> assertTrue(instance.getVmsMigratingIn().isEmpty()),
            () -> assertSame(Datacenter.NULL, instance.getDatacenter()),
            () -> assertEquals(-1, instance.getId()),
            () -> assertTrue(instance.getVmList().isEmpty())
        );
    }
}
