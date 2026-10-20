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

import static org.junit.jupiter.api.Assertions.assertEquals;

/// @author Manoel Campos da Silva Filho
public class UtilizationModelNullTest {
    private static final int EXPECTED_UTILIZATION = 0;

    @Test
    public void testGetUtilizationForDifferentTimes() {
        final UtilizationModel instance = UtilizationModel.NULL;
        final String msg =
                "For any time (even a randomly defined one), the utilization has to be equals to %d"
                .formatted(EXPECTED_UTILIZATION);

        for (int time = 0; time < 10000; time++) {
            assertEquals(EXPECTED_UTILIZATION, instance.getUtilization(time), msg);
        }
    }
}
