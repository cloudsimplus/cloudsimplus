package org.cloudsimplus.autoscaling;

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.mocks.CloudSimMocker;
import org.cloudsimplus.resources.Ram;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmAbstract;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.Preconditions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class VerticalVmScalingSimpleTest {
    private final CloudSimPlus sim = CloudSimMocker.createMock(CloudSimMocker::isRunning);
    private final DatacenterBrokerSimple broker = new DatacenterBrokerSimple(sim);
    private final VmAbstract vm = new VmSimple(1, 1000, 2);

    @Test
    void vmCreateConfiguresVerticalScaling() {
        final var verticalScaling = new VerticalVmScalingSimple(Ram.class, 0.1);
        Preconditions.condition(Vm.NULL.equals(verticalScaling.getVm()), "The VerticalVmScalingSimple instance should not have a VM set yet");
        Preconditions.condition(vm.getResources().isEmpty(), "The VM should not have any resources yet");

        /*
        Sets the broker that links the VM to the mocked simulation object, which will mimic a running simulation
        by mocking the isRunning() method.
        */
        vm.setBroker(broker);
        vm.setRamVerticalScaling(verticalScaling);
        vm.setCreated(true);

        assertEquals(vm, verticalScaling.getVm());
        assertFalse(vm.getResources().isEmpty());
    }
}
