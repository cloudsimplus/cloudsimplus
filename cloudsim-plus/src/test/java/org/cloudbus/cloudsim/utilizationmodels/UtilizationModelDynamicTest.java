package org.cloudbus.cloudsim.utilizationmodels;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.mocks.CloudSimMocker;
import org.cloudbus.cloudsim.util.Conversion;
import org.junit.Test;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.cloudbus.cloudsim.utilizationmodels.UtilizationModel.Unit;
import static org.junit.Assert.assertEquals;

/**
 * @author Manoel Campos da Silva Filho
 */
public class UtilizationModelDynamicTest {

    /**
     * The number of seconds that the utilization will be tested.
     * For each second from 0 to this number, a {@link UtilizationModelDynamic#getUtilization(double)}
     * will be called to test the expected value.
     */
    public static final int NUM_TIMES_TEST_USAGE = 10;

    @Test
    public void testGetUtilization_defaultConstructor() {
        final double usagePercentInc = 0.1, initialUtilization = 0;
        final UtilizationModelDynamic instance = createUtilizationModel(usagePercentInc, initialUtilization);
        checkUtilization(initialUtilization, usagePercentInc, instance);
    }

    @Test
    public void testGetUtilization_twoParamConstructorDecreasingUtilization() {
        final double usagePercentInc = -0.1, initialUtilization = 0.5;
        final UtilizationModelDynamic instance = createUtilizationModel(usagePercentInc, initialUtilization);
        checkUtilization(initialUtilization, usagePercentInc, instance);
    }

    private UtilizationModelDynamic createUtilizationModel(double usagePercentInc, double initUsage, int initSimulationTime) {
        final List<Integer> times = IntStream.rangeClosed(initSimulationTime, NUM_TIMES_TEST_USAGE).mapToObj(i -> i).collect(toList());
        final CloudSim simulation = CloudSimMocker.createMock(mocker -> mocker.clock(times));

        final UtilizationModelDynamic utilizationModel = new UtilizationModelDynamic(initUsage);
        utilizationModel
          .setUtilizationUpdateFunction(um -> um.getUtilization() + um.getTimeSpan() * usagePercentInc)
          .setSimulation(simulation);

        return utilizationModel;
    }

    private UtilizationModelDynamic createUtilizationModel(double usagePercentInc, double initUsage) {
        return createUtilizationModel(usagePercentInc, initUsage, 0);
    }

    @Test
    public void testGetUtilization_oneParamConstructor() {
        final double usagePercentInc = 0.2, initUsage = 0;
        final UtilizationModelDynamic instance = createUtilizationModel(usagePercentInc, initUsage);
        checkUtilization(initUsage, usagePercentInc, instance);
    }

    @Test
    public void testGetUtilization_twoParamConstructor() {
        final double usagePercentInc = 0.2, initUsage = 0.5;
        final UtilizationModelDynamic instance = createUtilizationModel(usagePercentInc, initUsage);
        checkUtilization(initUsage, usagePercentInc, instance);
    }

    @Test
    public void testGetUtilization_twoParamConstructorAndMaxUtilization() {
        final double usagePercentInc = 0.2, initUsage = 0.5;
        final double maxUsagePercent = 0.7;
        final UtilizationModelDynamic instance = createUtilizationModel(usagePercentInc, initUsage);
        checkUtilization(
            initUsage, usagePercentInc,
            maxUsagePercent, instance);
    }

    private void checkUtilization(final double initUsage, final double usagePercentInc, UtilizationModelDynamic instance) {
        checkUtilization(initUsage, usagePercentInc,
            Conversion.HUNDRED_PERCENT, instance);
    }

    private void checkUtilization(final double initUsage,
                                  final double usagePercentInc,
                                  final double maxUsagePercent,
                                  UtilizationModelDynamic instance)
    {
        instance.setMaxResourceUtilization(maxUsagePercent);
        IntStream.rangeClosed(0, NUM_TIMES_TEST_USAGE).forEach(time -> {
            final double expResult =
                computeExpectedUtilization(
                    time, initUsage,
                    usagePercentInc,
                    maxUsagePercent);
            final double result = instance.getUtilization(time);
            final String msg = String.format("The utilization at time %d", time);
            assertEquals(msg, expResult, result, 0.001);
        });
    }

    private double computeExpectedUtilization(double time, double initialUtilizationPercentage,
                                              double usagePercentInc, double maxUsagePercent) {
        final double utilizationPercentage =
            initialUtilizationPercentage + (time * usagePercentInc);

        if (usagePercentInc >= 0) {
            return Math.min(utilizationPercentage, maxUsagePercent);
        }

        return Math.max(0, utilizationPercentage);
    }

    @Test
    public void testGetUtilization1() {
        final double increment = 0.1;
        final int initialTime = 1;
        final UtilizationModelDynamic instance = createUtilizationModel(increment, 0, initialTime);
        assertEquals(increment, instance.getUtilization(initialTime), 0.0);
    }

    @Test
    public void testGetUtilization_NegativeIncrement() {
        final double increment = -0.1, initialUsage = 1;
        final double expResult = 0.9;
        final int initialTime = 1;
        final UtilizationModelDynamic instance = createUtilizationModel(increment, initialUsage, initialTime);
        assertEquals(expResult, instance.getUtilization(initialTime), 0.0);
    }

    @Test
    public void testConstructor_zeroUtilizationPercentageIncrementPerSecond() {
        final double increment = 0, initialUsage = 0.1;
        final int initialTime = 1;
        final UtilizationModelDynamic instance = createUtilizationModel(increment, initialUsage, initialTime);
        assertEquals(initialUsage, instance.getUtilization(initialTime), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_utilizationPercentageIncrementPerSecondLowerThanMinusOne() {
        new UtilizationModelDynamic(-1.1);
    }

    public void testConstructor_UtilizationPercentageIncrementGreaterThan1() {
        final double initialUtilizationPercent = 1.1;
        UtilizationModelDynamic um = new UtilizationModelDynamic(initialUtilizationPercent);
        assertEquals(initialUtilizationPercent, um.getUtilization(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_NegativeInitialValue() {
        new UtilizationModelDynamic(Unit.PERCENTAGE, -1.1);
    }

    public void constructor_InitialValueGreaterThan1() {
        final int initialUtilization = 2;
        UtilizationModelDynamic um = new UtilizationModelDynamic(Unit.PERCENTAGE, initialUtilization);
        assertEquals(initialUtilization, um.getUtilization(), 0);
    }

    @Test
    public void testTwoParamsConstructor_zeroInitialUtilization() {
        final double expResult = 0;
        final UtilizationModelDynamic instance = new UtilizationModelDynamic(0);
        assertEquals(expResult, instance.getUtilization(), 0.0);
    }

    @Test
    public void testSetMaxResourceUsagePercentage() {
        final UtilizationModelDynamic instance = new UtilizationModelDynamic();
        assertEquals(
            Conversion.HUNDRED_PERCENT,
            instance.getMaxResourceUtilization(), 0);

        final double maxResourceUsagePercentage = 0.9;
        instance.setMaxResourceUtilization(maxResourceUsagePercentage);
        assertEquals(maxResourceUsagePercentage, instance.getMaxResourceUtilization(), 0);

    }

    public void testSetMaxResourceUsagePercentage_valueGreaterThanOne() {
        final UtilizationModelDynamic instance = new UtilizationModelDynamic();
        final double maxResourceUsagePercentage = 1.1;
        instance.setMaxResourceUtilization(maxResourceUsagePercentage);
        assertEquals(maxResourceUsagePercentage, instance.getMaxResourceUtilization());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetMaxResourceUsagePercentage_negativeValue() {
        final UtilizationModelDynamic instance = new UtilizationModelDynamic();
        instance.setMaxResourceUtilization(-1);
        instance.setMaxResourceUtilization(-0.1);
    }
}
