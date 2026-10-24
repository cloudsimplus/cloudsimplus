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

import org.cloudsimplus.vms.Vm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// @author Manoel Campos da Silva Filho
class CpuCapTest {
    private static final double CORE_MIPS = 1000;

    /// One instance of each [CpuCap] implementation, used to check the contract all of them must follow.
    static List<CpuCap> allCaps() {
        return List.of(CpuCap.NONE);
    }

    @Test
    void allImplementationsAreTested() {
        final var testedClasses = allCaps().stream().map(Object::getClass).toList();
        final var permittedClasses = Arrays.asList(CpuCap.class.getPermittedSubclasses());
        assertEquals(permittedClasses.size(), testedClasses.size());
        assertTrue(testedClasses.containsAll(permittedClasses));
    }

    @ParameterizedTest
    @MethodSource("allCaps")
    void limitCoreMipsIsBetweenZeroAndCoreMips(final CpuCap cap) {
        for (long cores = 1; cores <= 8; cores++) {
            final double limited = cap.limitCoreMips(processor(cores, CORE_MIPS));
            assertTrue(limited >= 0 && limited <= CORE_MIPS, "%s gave %s MIPS for %d cores".formatted(cap, limited, cores));
        }
    }

    @ParameterizedTest
    @MethodSource("allCaps")
    void limitTotalMipsIsLimitCoreMipsTimesWorkingCores(final CpuCap cap) {
        final var processor = processor(4, CORE_MIPS);
        processor.moveCores(CoreStatus.FREE, CoreStatus.FAILED, 1);
        assertEquals(cap.limitCoreMips(processor) * processor.getWorkingCores(), cap.limitTotalMips(processor));
    }

    /// A cap that doesn't limit the CPU must give the same total MIPS as the processor,
    /// even when some cores have failed.
    @Test
    void noneTotalMipsIsProcessorTotalMipsWithFailedCores() {
        final var processor = processor(4, CORE_MIPS);
        processor.moveCores(CoreStatus.FREE, CoreStatus.FAILED, 1);
        assertEquals(processor.getTotalMips(), CpuCap.NONE.limitTotalMips(processor));
    }

    @Test
    void noneDoesNotLimitCoreMips() {
        assertEquals(CORE_MIPS, CpuCap.NONE.limitCoreMips(processor(4, CORE_MIPS)));
        assertEquals(0, CpuCap.NONE.limitCoreMips(processor(4, 0)));
    }

    private static Processor processor(final long cores, final double mips) {
        return new ProcessorSimple(Vm.NULL, new CoreCapacity(cores, mips));
    }
}
