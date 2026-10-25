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
package org.cloudsimplus.autoscaling;

import org.cloudsimplus.resources.CoreCapacity;
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
    private final VmAbstract vm = (VmAbstract) VmSimple.of(1, new CoreCapacity(2, 1000));

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
