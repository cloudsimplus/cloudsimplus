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
package org.cloudsimplus.hosts;

import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.allocators.ResourceAllocator;
import org.cloudsimplus.schedulers.vm.VmScheduler;
import org.cloudsimplus.vms.Vm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.cloudsimplus.resources.Processor.totalMips;

/// @author Manoel Campos da Silva Filho
public class HostTest {

    @Test
    public void testNullObject() {
        final Host instance = Host.NULL;
        final Vm vm = Vm.NULL;

        assertAll(
            () -> assertEquals(0, instance.getVmScheduler().getTotalAvailableMips()),
            () -> assertEquals(0, instance.getStorage().getFree()),
            () -> assertEquals(0, instance.getBw().getCapacity()),
            () -> assertEquals(ResourceAllocator.NULL, instance.getBwAllocator()),
            () -> assertEquals(0, instance.getProcessor().getFreeCores()),
            () -> assertEquals(0, instance.getProcessor().getCores()),
            () -> assertEquals(0, instance.getRam().getCapacity()),
            () -> assertEquals(ResourceAllocator.NULL, instance.getRamAllocator()),
            () -> assertEquals(0, instance.getStorage().getCapacity()),
            () -> assertEquals(0, instance.getVmScheduler().getTotalAllocatedMipsForVm(vm)),
            () -> assertEquals(0, totalMips(instance)),
            () -> assertSame(VmScheduler.NULL, instance.getVmScheduler()),
            () -> assertFalse(instance.isFailed()),
            () -> assertFalse(instance.isSuitableForVm(vm)),
            () -> assertEquals(0, instance.getProcessor().getCores()),
            () -> assertTrue(instance.getVmsMigratingIn().isEmpty()),
            () -> assertSame(Datacenter.NULL, instance.getDatacenter()),
            () -> assertEquals(-1, instance.getId()),
            () -> assertTrue(instance.getVmList().isEmpty())
        );
    }
}
