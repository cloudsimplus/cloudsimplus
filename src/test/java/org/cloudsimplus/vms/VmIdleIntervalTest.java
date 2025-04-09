package org.cloudsimplus.vms;

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.mocks.CloudSimMocker;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.Preconditions;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link Vm#getIdleInterval()}
 * @author Manoel Campos
 */
class VmIdleIntervalTest {
    private final Vm vm = VmTestUtil.createVm(0, 2);

    /**
     * Idle time just starts to count when the VM is created.
     */
    @Test
    void getIdleIntervalIsZeroWhenVmWasNotCreatedToIndicateItWasNeverIdle() {
        Preconditions.condition(vm.getLastBusyTime() == -1, "Vm should be idle");
        assertEquals(0, vm.getIdleInterval());
    }

    @Test
    void getIdleInterval() {
        final double clockTime = 5.0;
        final double lastBusyTime = 1.0;

        final CloudSimPlus simulation = CloudSimMocker.createMock(mocker -> mocker.clock(clockTime));
        final var broker = new DatacenterBrokerSimple(simulation);

        vm.setLastBusyTime(lastBusyTime);
        vm.setBroker(broker);

        final double expected = 4.0;
        assertEquals(expected, vm.getIdleInterval());
    }

    @Test
    void getNoIdleIntervalWhenClockIsNotZero() {
        final double clockTime = 5.0;

        final CloudSimPlus simulation = CloudSimMocker.createMock(mocker -> mocker.clock(clockTime));
        final var broker = new DatacenterBrokerSimple(simulation);
        vm.setBroker(broker);

        final double expected = 0;
        assertEquals(expected, vm.getIdleInterval());
    }
}
