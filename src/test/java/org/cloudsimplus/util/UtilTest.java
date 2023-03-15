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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Manoel Campos da Silva Filho
 */
class UtilTest {

    @Test
    void newSquareMatrixSize() {
        final int size = 10;
        final var matrix = Util.newSquareMatrix(size);
        assertEquals(size, matrix.length);
        assertEquals(size, matrix[0].length);
    }

    @Test
    void newZeroedMatrix() {
        final int size = 10;
        final var matrix = Util.newSquareMatrix(size);
        final var msg = "There is trash at position %d,%d instead of zero: %.2f";
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                assertEquals(0.0, matrix[i][j], msg.formatted(i, j, matrix[i][j]));
            }
        }
    }

    @Test
    void newMatrixWithDefaultValue() {
        final int size = 10;
        final double defaultVal = Double.MAX_VALUE;
        final var matrix = Util.newSquareMatrix(size, defaultVal);
        final var msg = "Value at position %d,%d is not as expected";
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                assertEquals(defaultVal, matrix[i][j], msg.formatted(i, j));
            }
        }
    }

}
