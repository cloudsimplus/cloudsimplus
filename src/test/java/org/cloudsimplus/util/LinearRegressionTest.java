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

import static org.junit.jupiter.api.Assertions.*;

/// Tests [LinearRegression].
/// The expected values were computed by the Apache Commons Math 3 regression classes
/// (`SimpleRegression` and `OLSMultipleLinearRegression`), which CloudSim Plus used before,
/// to make sure the results didn't change.
class LinearRegressionTest {
    private static final double DELTA = 1e-12;

    private static final double[] SIMPLE_X = {1, 2, 3, 4, 5, 6};
    private static final double[] SIMPLE_Y = {2.1, 3.9, 6.2, 7.8, 10.1, 12.2};

    private static final double[][] MULTIPLE_X = {
        {1, 2, 0.5}, {2, 1, 1.5}, {3, 4, 0.2}, {4, 3, 2.2}, {5, 6, 1.1}, {6, 5, 0.9}, {7, 8, 3.0}, {8, 7, 0.4}
    };
    private static final double[] MULTIPLE_Y = {3.2, 4.1, 7.9, 8.8, 11.7, 12.4, 16.9, 15.2};

    @Test
    void simpleRegressionMatchesCommonsMath() {
        final var regression = LinearRegression.of(SIMPLE_X, SIMPLE_Y);
        assertArrayEquals(new double[]{-0.01999999999999839, 2.0199999999999996}, regression.getParameterEstimates(), DELTA);
        assertEquals(14.12, regression.predict(7), DELTA);
        assertEquals(0.9982106661074994, regression.getRSquared(), DELTA);
    }

    @Test
    void multipleRegressionMatchesCommonsMath() {
        final var regression = LinearRegression.of(MULTIPLE_X, MULTIPLE_Y);
        final double[] expected = {0.3761370980772293, 1.0819215543706588, 0.909906689287445, 0.5597026983357573};
        assertArrayEquals(expected, regression.getParameterEstimates(), DELTA);
        assertEquals(0.9947402106233147, regression.getRSquared(), DELTA);
    }

    /// The two independent variables are almost equal (highly correlated).
    /// That makes the problem badly conditioned, so the coefficients are huge and very sensitive to rounding errors.
    /// The QR decomposition keeps the same accuracy as Commons Math (which also uses it).
    @Test
    void nearlyCollinearVariablesMatchCommonsMath() {
        final double[][] x = {{1, 1.0000001}, {2, 2.0000003}, {3, 2.9999998}, {4, 4.0000002}, {5, 5.0000001}, {6, 5.9999999}};
        final double[] y = {1.1, 2.0, 2.9, 4.2, 5.0, 6.1};
        final double[] expected = {-0.03516209454983026, -295510.2020148163, 295511.2207180581};

        final double[] actual = LinearRegression.of(x, y).getParameterEstimates();
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i], Math.abs(expected[i]) * 1e-6, "Parameter " + i);
        }

        assertEquals(0.9977387810194092, LinearRegression.of(x, y).getRSquared(), 1e-9);
    }

    @Test
    void perfectFitHasRSquaredOne() {
        final var regression = LinearRegression.of(new double[]{1, 2, 3, 4}, new double[]{3, 5, 7, 9});
        assertArrayEquals(new double[]{1, 2}, regression.getParameterEstimates(), DELTA);
        assertEquals(1, regression.getRSquared(), DELTA);
    }

    @Test
    void constantIndependentVariableGivesNaN() {
        final var regression = LinearRegression.of(new double[]{2, 2, 2}, new double[]{1, 2, 3});
        assertTrue(Double.isNaN(regression.getParameterEstimates()[0]));
        assertTrue(Double.isNaN(regression.getParameterEstimates()[1]));
        assertTrue(Double.isNaN(regression.getRSquared()));
    }

    @Test
    void constantDependentVariableHasNaNRSquared() {
        final var regression = LinearRegression.of(new double[]{1, 2, 3}, new double[]{5, 5, 5});
        assertArrayEquals(new double[]{5, 0}, regression.getParameterEstimates(), DELTA);
        assertTrue(Double.isNaN(regression.getRSquared()));
    }

    @Test
    void getParameterEstimatesReturnsACopy() {
        final var regression = LinearRegression.of(SIMPLE_X, SIMPLE_Y);
        regression.getParameterEstimates()[0] = 1000;
        assertNotEquals(1000, regression.getParameterEstimates()[0]);
    }

    @Test
    void rejectsInvalidData() {
        assertThrows(IllegalArgumentException.class, () -> LinearRegression.of(new double[]{1, 2, 3}, new double[]{1, 2}));
        assertThrows(IllegalArgumentException.class, () -> LinearRegression.of(new double[]{1}, new double[]{1}));
        assertThrows(IllegalArgumentException.class, () -> LinearRegression.of(new double[][]{{1, 2}, {3}, {4, 5}}, new double[]{1, 2, 3}));
        // 3 parameters (intercept and 2 coefficients) need at least 3 observations
        assertThrows(IllegalArgumentException.class, () -> LinearRegression.of(new double[][]{{1, 2}, {3, 4}}, new double[]{1, 2}));
    }

    @Test
    void predictRejectsWrongNumberOfValues() {
        final var regression = LinearRegression.of(MULTIPLE_X, MULTIPLE_Y);
        assertThrows(IllegalArgumentException.class, () -> regression.predict(1, 2));
    }
}
