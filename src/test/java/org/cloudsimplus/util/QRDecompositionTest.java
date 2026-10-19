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

class QRDecompositionTest {
    private static final double DELTA = 1e-12;

    @Test
    void solvesSquareSystemExactly() {
        // 2x + y = 3 and x + 3y = 5
        final var qr = new QRDecomposition(new double[][]{{2, 1}, {1, 3}});
        assertArrayEquals(new double[]{0.8, 1.4}, qr.solve(new double[]{3, 5}), DELTA);
    }

    @Test
    void solvesOverdeterminedSystemByLeastSquares() {
        // Fits y = β0 + β1·x to the points (1, 1), (2, 2) and (3, 2): the best line is y = 2/3 + 0.5x
        final double[][] design = {{1, 1}, {1, 2}, {1, 3}};
        final var qr = new QRDecomposition(design);
        assertArrayEquals(new double[]{2.0 / 3.0, 0.5}, qr.solve(new double[]{1, 2, 2}), DELTA);
    }

    @Test
    void solvesWhenFirstColumnIsNegative() {
        // Checks the sign handling of the Householder reflection
        final var qr = new QRDecomposition(new double[][]{{-2, 1}, {-1, 3}});
        assertArrayEquals(new double[]{-0.8, 1.4}, qr.solve(new double[]{3, 5}), DELTA);
    }

    @Test
    void detectsLinearlyDependentColumns() {
        // The second column is twice the first one
        final var qr = new QRDecomposition(new double[][]{{1, 2}, {2, 4}, {3, 6}});
        assertFalse(qr.isFullRank());
        assertThrows(IllegalStateException.class, () -> qr.solve(new double[]{1, 2, 3}));
    }

    @Test
    void detectsZeroColumn() {
        final var qr = new QRDecomposition(new double[][]{{1, 0}, {2, 0}, {3, 0}});
        assertFalse(qr.isFullRank());
    }

    @Test
    void independentColumnsAreFullRank() {
        assertTrue(new QRDecomposition(new double[][]{{1, 1}, {1, 2}, {1, 3}}).isFullRank());
    }

    @Test
    void doesNotChangeTheGivenMatrixAndVector() {
        final double[][] matrix = {{1, 1}, {1, 2}, {1, 3}};
        final double[] vector = {1, 2, 2};
        new QRDecomposition(matrix).solve(vector);
        assertArrayEquals(new double[][]{{1, 1}, {1, 2}, {1, 3}}, matrix);
        assertArrayEquals(new double[]{1, 2, 2}, vector);
    }

    @Test
    void rejectsInvalidMatrices() {
        assertThrows(IllegalArgumentException.class, () -> new QRDecomposition(new double[0][0]));
        assertThrows(IllegalArgumentException.class, () -> new QRDecomposition(new double[][]{{1, 2}, {3}}));
        assertThrows(IllegalArgumentException.class, () -> new QRDecomposition(new double[][]{{1, 2, 3}, {4, 5, 6}}));
    }

    @Test
    void rejectsVectorWithWrongLength() {
        final var qr = new QRDecomposition(new double[][]{{1, 1}, {1, 2}, {1, 3}});
        assertThrows(IllegalArgumentException.class, () -> qr.solve(new double[]{1, 2}));
    }
}
