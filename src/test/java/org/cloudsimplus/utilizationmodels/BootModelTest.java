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
package org.cloudsimplus.utilizationmodels;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/// @author Manoel Campos da Silva Filho
class BootModelTest {
    private static final double CPU_UTILIZATION = 0.5;
    private static final double RAM_UTILIZATION = 0.25;

    private final UtilizationModel cpuModel = new UtilizationModelDynamic(CPU_UTILIZATION);
    private final UtilizationModel ramModel = new UtilizationModelDynamic(RAM_UTILIZATION);
    private final BootModel bootModel = new BootModel(cpuModel, ramModel);

    @Test
    void percentUtilization() {
        assertEquals(CPU_UTILIZATION, bootModel.getCpuPercentUtilization());
        assertEquals(RAM_UTILIZATION, bootModel.getRamPercentUtilization());
    }

    @Test
    void sameModelForCpuAndRam() {
        final var model = new BootModel(cpuModel);
        assertSame(cpuModel, model.cpuModel());
        assertSame(cpuModel, model.ramModel());
    }

    @Test
    void nullUsesNoResources() {
        assertEquals(0, BootModel.NULL.getCpuPercentUtilization());
        assertEquals(0, BootModel.NULL.getRamPercentUtilization());
    }

    @Test
    void withSameModelReturnsSameObject() {
        assertSame(bootModel, bootModel.withCpuModel(cpuModel));
        assertSame(bootModel, bootModel.withRamModel(ramModel));
    }

    @Test
    void withNewModelReturnsNewObjectAndKeepsOriginal() {
        final BootModel changed = bootModel.withCpuModel(ramModel);
        assertEquals(new BootModel(ramModel, ramModel), changed);
        assertSame(cpuModel, bootModel.cpuModel());
    }

    @Test
    void nullModelIsRejected() {
        final var model = new BootModel(UtilizationModel.NULL);
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> new BootModel(null)),
            () -> assertThrows(NullPointerException.class, () -> new BootModel(UtilizationModel.NULL, null)),
            () -> assertThrows(NullPointerException.class, () -> new BootModel(null, UtilizationModel.NULL)),
            () -> assertThrows(NullPointerException.class, () -> model.withCpuModel(null)),
            () -> assertThrows(NullPointerException.class, () -> model.withRamModel(null))
        );
    }
}
