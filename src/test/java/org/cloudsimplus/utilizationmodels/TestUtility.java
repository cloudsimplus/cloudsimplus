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

import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.mocks.CloudSimMocker;
import org.cloudsimplus.util.Conversion;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A utility class used by {@link UtilizationModelDynamic} tests.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
final class TestUtility {
    /**
     * The number of seconds that the utilization will be tested.
     * For each second from 0 to this number, a {@link UtilizationModelDynamic#getUtilization(double)}
     * will be called to test the expected value.
     */
    private static final int NUM_TIMES_TEST_USAGE = 10;

    /**
     * A private constructor to avoid class instantiation.
     */
    private TestUtility(){/**/}

    static UtilizationModelDynamic createUtilizationModel(final double usagePercentInc, final double initUsage) {
        return createUtilizationModel(usagePercentInc, initUsage, 0);
    }

    static UtilizationModelDynamic createUtilizationModel(
        final double usagePercentInc,
        final double initUsage,
        final int initSimulationTime)
    {
        final var times = IntStream.rangeClosed(initSimulationTime, NUM_TIMES_TEST_USAGE).mapToObj(v -> v*1.0).toList();
        final CloudSimPlus simulation = CloudSimMocker.createMock(mocker -> mocker.clock(times));

        final var um = new UtilizationModelDynamic(initUsage);
        um.setUtilizationUpdateFunction(model -> model.getUtilization() + model.getTimeSpan() * usagePercentInc);
        um.setSimulation(simulation);

        return um;
    }

    static void checkUtilization(
        final double initUsage,
        final double usagePercentInc,
        final UtilizationModelDynamic instance)
    {
        checkUtilization(initUsage, usagePercentInc, Conversion.HUNDRED_PERCENT, instance);
    }

    static void checkUtilization(
        final double initUsage,
        final double usagePercentInc,
        final double maxUsagePercent,
        final UtilizationModelDynamic instance)
    {
        instance.setMaxResourceUtilization(maxUsagePercent);
        for (int time = 0; time <= NUM_TIMES_TEST_USAGE; time++) {
            final double expResult =
                computeExpectedUtilization(
                    time, initUsage,usagePercentInc, maxUsagePercent);
            final double result = instance.getUtilization(time);
            final String msg = "The utilization at time %d".formatted(time);
            assertEquals(expResult, result, 0.001, msg);
        }
    }

    private static double computeExpectedUtilization(
        final double time,
        final double initialUtilizationPercentage,
        final double usagePercentInc,
        final double maxUsagePercent)
    {
        final double utilizationPercentage =
            initialUtilizationPercentage + (time * usagePercentInc);

        if (usagePercentInc >= 0) {
            return Math.min(utilizationPercentage, maxUsagePercent);
        }

        return Math.max(0, utilizationPercentage);
    }
}
