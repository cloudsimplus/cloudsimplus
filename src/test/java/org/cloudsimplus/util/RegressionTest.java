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
package org.cloudsimplus.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/// Tests [Regression].
/// The expected values were computed with the previous implementation,
/// which was based on Apache Commons Math 3, to make sure the results didn't change.
class RegressionTest {
    private static final double DELTA = 1e-12;
    private static final double[] UTILIZATION = {0.5, 0.6, 0.55, 0.7, 0.65, 0.8, 0.75, 0.9, 0.85, 1.0};

    @Test
    void loessParameterEstimates() {
        final double[] expected = {0.4533333333333333, 0.050303030303030294};
        assertArrayEquals(expected, Regression.getLoessParameterEstimates(UTILIZATION), DELTA);
    }

    @Test
    void robustLoessParameterEstimates() {
        final double[] expected = {0.4533333333333333, 0.050303030303030294};
        assertArrayEquals(expected, Regression.getRobustLoessParameterEstimates(UTILIZATION), DELTA);
    }

    @Test
    void robustLoessParameterEstimatesWithOutlier() {
        final double[] utilization = {0.2, 0.2, 0.2, 0.2, 0.9, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.25};
        final double[] expected = {0.29772727272727273, -0.005419580419580411};
        assertArrayEquals(expected, Regression.getRobustLoessParameterEstimates(utilization), DELTA);
    }

    @Test
    void correlationCoefficients() {
        final double[][] data = {
            {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8},
            {0.15, 0.22, 0.35, 0.41, 0.48, 0.66, 0.69, 0.83},
            {0.9, 0.3, 0.6, 0.2, 0.8, 0.1, 0.7, 0.4}
        };
        final List<Double> expected = List.of(0.9878909961786226, 0.9881691056476157, 0.19292688218702558);
        final List<Double> actual = Regression.correlationCoefficients(data);

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i), DELTA, "Coefficient " + i);
        }
    }
}
