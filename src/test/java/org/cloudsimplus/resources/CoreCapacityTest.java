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
package org.cloudsimplus.resources;

import org.cloudsimplus.utilizationmodels.BootModel;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;
import org.cloudsimplus.vms.Vm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/// @author Manoel Campos da Silva Filho
class CoreCapacityTest {
    private static final long CORES = 4;
    private static final double MIPS = 1000;
    private final CoreCapacity capacity = new CoreCapacity(CORES, MIPS);

    @Test
    void negativeValuesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new CoreCapacity(-1, MIPS));
        assertThrows(IllegalArgumentException.class, () -> new CoreCapacity(CORES, -1));
    }

    @Test
    void oneCoreConstructor() {
        assertEquals(new CoreCapacity(1, MIPS), new CoreCapacity(MIPS));
    }

    @Test
    void totalMips() {
        assertEquals(CORES * MIPS, capacity.totalMips());
    }

    @Test
    void isEmpty() {
        assertFalse(capacity.isEmpty());
        assertTrue(CoreCapacity.NULL.isEmpty());
        assertTrue(new CoreCapacity(CORES, 0).isEmpty());
        assertTrue(new CoreCapacity(0, MIPS).isEmpty());
    }

    @Test
    void withoutCoresReturnsNewObjectAndKeepsOriginal() {
        final CoreCapacity reduced = capacity.withoutCores(1);
        assertEquals(new CoreCapacity(CORES - 1, MIPS), reduced);
        assertEquals(CORES, capacity.cores());
    }

    @Test
    void withoutCoresDoesNotRemoveMoreThanAvailable() {
        assertEquals(new CoreCapacity(0, MIPS), capacity.withoutCores(CORES + 2));
    }

    @Test
    void withoutZeroCoresReturnsSameObject() {
        assertSame(capacity, capacity.withoutCores(0));
    }

    @Test
    void withoutNegativeCoresIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> capacity.withoutCores(-1));
    }

    @Test
    void ofProcessor() {
        assertEquals(capacity, CoreCapacity.of(processor()));
        assertEquals(capacity, CoreCapacity.of(processor(), CpuCap.NONE));
    }

    @Test
    void ofProcessorDuringBoot() {
        final double bootCpuPercent = 0.5;
        final var bootModel = new BootModel(new UtilizationModelDynamic(bootCpuPercent), new UtilizationModelDynamic(0));
        assertEquals(new CoreCapacity(CORES, MIPS * bootCpuPercent), CoreCapacity.of(processor(), CpuCap.NONE, bootModel));
    }

    private Processor processor() {
        return new ProcessorSimple(Vm.NULL, capacity);
    }
}
