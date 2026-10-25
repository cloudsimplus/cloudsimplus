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

import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.cloudsimplus.resources.Processor.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/// Checks that the static [Processor] methods taking a [org.cloudsimplus.core.Machine]
/// return the same values as the machine's processor.
/// @author Manoel Campos da Silva Filho
class ProcessorTest {
    private static final long CORES = 4;
    private static final double MIPS = 1000;

    private VmSimple vm;

    @BeforeEach
    void setUp() {
        vm = (VmSimple) VmSimple.of(new CoreCapacity(CORES, MIPS));
        final var processor = vm.getProcessor();
        processor.moveCores(CoreStatus.FREE, CoreStatus.BUSY, 1);
        processor.moveCores(CoreStatus.FREE, CoreStatus.FAILED, 1);
    }

    @Test
    void testCores() {
        assertEquals(CORES, cores(vm));
    }

    @Test
    void testWorkingCores() {
        assertEquals(CORES - 1, workingCores(vm));
    }

    @Test
    void testFreeCores() {
        assertEquals(CORES - 2, freeCores(vm));
    }

    @Test
    void testMips() {
        assertEquals(MIPS, mips(vm));
    }
}
