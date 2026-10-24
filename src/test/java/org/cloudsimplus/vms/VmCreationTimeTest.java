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
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.CoreCapacity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.Preconditions;

import java.util.List;

import static org.cloudsimplus.mocks.CloudSimMocker.createMock;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/// Tests for [Vm#getCreationTime()] and [Vm#getCreationWaitTime()]
/// @author Manoel Campos
class VmCreationTimeTest {
    private final VmSimple vm = VmTestUtil.createVm(0, 2);
    private final DatacenterBrokerSimple broker = new DatacenterBrokerSimple(new CloudSimPlus());

    @BeforeEach void setUp() { broker.submitVm(vm); }

    /// Creation wait time is zero when the VM is not created yet.
    @Test
    void noCreationWaitTimeForNotCreatedVmAndFirstSubmission() {
        Preconditions.condition(vm.isFinished(), "Vm must not be created");
        assertEquals(-1, vm.getCreationTime());
        assertEquals( 0, vm.getCreationWaitTime());
    }

    /// Creation wait time is zero when the VM is created right away after submission.
    @Test
    void noCreationWaitTimeForCreatedVmAndFirstSubmission() {
        vm.setCreated(true);
        assertEquals(0, vm.getCreationTime());
        assertEquals(0, vm.getCreationWaitTime());
    }

    /// Tests that simulate the advance of the simulation clock by using a mocked [CloudSimPlus] instance.
    @Nested
    class ClockRunningSimulation {
        private final List<Double> clockTimes = List.of(0.0, 5.0, 10.0, 12.0, 16.0); // list of times for consecutive simulation.clock() calls
        private final CloudSimPlus simulation = createMock(mocker -> mocker.clock(clockTimes));
        private final DatacenterBrokerSimple broker = new DatacenterBrokerSimple(simulation);
        private final HostSimple host = new HostSimple(1000, 1000, 1000, new CoreCapacity(1, 1000));

        @Test
        void creationTimeForFirstAndSecondVmSubmission() {
            assertDoesNotThrow(() -> {
                firstVmSubmission();
                secondVmSubmission();
            });
        }

        private void firstVmSubmission() {
            // Vm will be submitted at time 0 by calling simulation.clock()
            broker.submitVm(vm);
            Preconditions.condition(vm.getBrokerArrivalTime() == 0.0, "Vm must be submitted at time 0");

            vm.setCreated(true); // will be created at time 5 by calling simulation.clock()
            final double expectedTime = 5.0;
            assertEquals(expectedTime, vm.getCreationTime());
            assertEquals(expectedTime, vm.getCreationWaitTime()); // time span
        }

        private void secondVmSubmission() {
            host.destroyVm(vm);  // will be destroyed at time 10 by calling simulation.clock()
            broker.submitVm(vm); // will be submitted at time 12 by calling simulation.clock()

            assertEquals(12, vm.getBrokerArrivalTime());

            vm.setCreated(true); // will be created   at time 16 by calling simulation.clock()
            assertEquals(16, vm.getCreationTime());
            assertEquals( 4, vm.getCreationWaitTime()); // time span
        }
    }
}
