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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceStatsTest {
    @Test
    void statisticsAreNaNWhenNoSampleWasCollected() {
        final var stats = new VmResourceStats(Vm.NULL, vm -> 0.5);
        assertTrue(stats.isEmpty());
        assertEquals(0, stats.count());
        assertTrue(Double.isNaN(stats.getMin()), "min");
        assertTrue(Double.isNaN(stats.getMax()), "max");
        assertTrue(Double.isNaN(stats.getMean()), "mean");
        assertTrue(Double.isNaN(stats.getStandardDeviation()), "standard deviation");
        assertTrue(Double.isNaN(stats.getVariance()), "variance");
    }
}
