/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.vms;

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.mocks.CloudSimMocker;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.Preconditions;

import static org.junit.jupiter.api.Assertions.assertEquals;

/// Tests for [Vm#getIdleInterval()]
/// @author Manoel Campos
class VmIdleIntervalTest {
    private final Vm vm = VmTestUtil.createVm(0, 2);

    /// Idle time just starts to count when the VM is created.
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
