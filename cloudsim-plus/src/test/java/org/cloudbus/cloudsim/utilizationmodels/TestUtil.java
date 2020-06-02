package org.cloudbus.cloudsim.utilizationmodels;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.mocks.CloudSimMocker;
import org.cloudbus.cloudsim.util.Conversion;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * An utility class used by {@link UtilizationModelDynamic} tests.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.0.0
 */
final class TestUtil {
    /**
     * The number of seconds that the utilization will be tested.
     * For each second from 0 to this number, a {@link UtilizationModelDynamic#getUtilization(double)}
     * will be called to test the expected value.
     */
    private static final int NUM_TIMES_TEST_USAGE = 10;

    /**
     * A private constructor to avoid class instantiation.
     */
    private TestUtil(){/**/}

    /* default */ static UtilizationModelDynamic createUtilizationModel(
        final double usagePercentInc,
        final double initUsage,
        final int initSimulationTime)
    {
        final List<Integer> times = IntStream.rangeClosed(initSimulationTime, NUM_TIMES_TEST_USAGE)
            .boxed()
            .collect(toList());
        final CloudSim simulation = CloudSimMocker.createMock(mocker -> mocker.clock(times));

        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(initUsage);
        utilizationModel
            .setUtilizationUpdateFunction(model -> model.getUtilization() + model.getTimeSpan() * usagePercentInc)
            .setSimulation(simulation);

        return utilizationModel;
    }

    /* default */ static UtilizationModelDynamic createUtilizationModel(final double usagePercentInc, final double initUsage) {
        return createUtilizationModel(usagePercentInc, initUsage, 0);
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

    public static void checkUtilization(
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
            final String msg = String.format("The utilization at time %d", time);
            assertEquals(expResult, result, 0.001, msg);
        }
    }

    /* default */ static void checkUtilization(
        final double initUsage,
        final double usagePercentInc,
        final UtilizationModelDynamic instance)
    {
        TestUtil.checkUtilization(initUsage, usagePercentInc, Conversion.HUNDRED_PERCENT, instance);
    }
}
