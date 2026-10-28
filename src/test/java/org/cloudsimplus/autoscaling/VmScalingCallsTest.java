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

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.mocks.CloudSimMocker;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.resources.Ram;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmAbstract;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

/// Checks that a [VmAbstract] calls its scaling objects directly at every processing update
/// (instead of registering them as update processing listeners).
/// The scaling objects are spies whose check is stubbed, since the real check requires a Host and a Datacenter.
class VmScalingCallsTest {
    private static final CoreCapacity CAPACITY = new CoreCapacity(2, 1000);
    private static final double SCALING_FACTOR = 0.1;

    private final CloudSimPlus sim = CloudSimMocker.createMock(CloudSimMocker::isRunning);
    private final DatacenterBrokerSimple broker = new DatacenterBrokerSimple(sim);
    private VmAbstract vm;
    private VerticalVmScalingSimple scaling;

    @BeforeEach
    void setUp() {
        vm = newVm(1);
        scaling = newScalingSpy();
    }

    private VmAbstract newVm(final int id) {
        final var newVm = (VmAbstract) VmSimple.of(id, CAPACITY);
        newVm.setBroker(broker);
        return newVm;
    }

    private static VerticalVmScalingSimple newScalingSpy() {
        final var scalingSpy = spy(new VerticalVmScalingSimple(Ram.class, SCALING_FACTOR));
        doReturn(false).when(scalingSpy).requestUpScalingIfPredicateMatches(anyDouble());
        return scalingSpy;
    }

    private void updateProcessing() {
        vm.updateProcessing(0, CAPACITY);
    }

    @Test
    void scalingSetBeforeCreationIsLinkedAndCalledAtEachProcessingUpdate() {
        vm.setRamVerticalScaling(scaling);
        vm.setCreated(true);
        updateProcessing();
        updateProcessing();

        assertSame(vm, scaling.getVm());
        verify(scaling, times(2)).requestUpScalingIfPredicateMatches(anyDouble());
    }

    @Test
    void scalingSetAfterCreationIsLinkedAndCalled() {
        vm.setCreated(true);
        vm.setCpuVerticalScaling(scaling);
        updateProcessing();

        assertSame(vm, scaling.getVm());
        verify(scaling).requestUpScalingIfPredicateMatches(anyDouble());
    }

    @Test
    void vmCreatedAgainCallsScalingOncePerProcessingUpdate() {
        vm.setBwVerticalScaling(scaling);
        vm.setCreated(true);
        vm.setCreated(false);
        vm.setCreated(true);
        updateProcessing();

        verify(scaling).requestUpScalingIfPredicateMatches(anyDouble());
    }

    @Test
    void scalingIsNotLinkedBeforeVmCreation() {
        vm.setRamVerticalScaling(scaling);
        assertSame(Vm.NULL, scaling.getVm());
    }

    @Test
    void scalingLinkedToAnotherVmThrowsExceptionWhenSetOnCreatedVm() {
        final var otherVm = newVm(2);
        otherVm.setRamVerticalScaling(scaling);
        otherVm.setCreated(true);

        vm.setCreated(true);
        assertThrows(IllegalArgumentException.class, () -> vm.setRamVerticalScaling(scaling));
    }

    @Test
    void scalingLinkedToAnotherVmThrowsExceptionWhenVmIsCreated() {
        final var otherVm = newVm(2);
        otherVm.setRamVerticalScaling(scaling);
        otherVm.setCreated(true);

        vm.setRamVerticalScaling(scaling);
        assertThrows(IllegalArgumentException.class, () -> vm.setCreated(true));
    }
}
