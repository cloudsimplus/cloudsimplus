package org.cloudbus.cloudsim.utilizationmodels;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.mocks.CloudSimMocker;
import org.cloudbus.cloudsim.util.Conversion;
import org.junit.Test;

import java.util.List;
import java.util.function.Function;
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
    public static final int NUMBER_TIMES_TO_TEST_UTILIZATION = 10;

    @Test
    public void testGetUtilization_defaultConstructor() {
        System.out.println("getUtilization");
        final double utilizationPercentageIncrement = 0.1, initialUtilization = 0;
        UtilizationModelDynamic instance = createUtilizationModel(utilizationPercentageIncrement, initialUtilization);
        checkUtilization(initialUtilization, utilizationPercentageIncrement, instance);
    }

    @Test
    public void testGetUtilization_twoParamConstructorDecreasingUtilization() {
        System.out.println("getUtilization");
        final double utilizationPercentageIncrement = -0.1, initialUtilization = 0.5;
        UtilizationModelDynamic instance = createUtilizationModel(utilizationPercentageIncrement, initialUtilization);
        checkUtilization(initialUtilization, utilizationPercentageIncrement, instance);
    }

    private UtilizationModelDynamic createUtilizationModel(double utilizationPercentageIncrement, double initialUtilization, int initialSimulationTime) {
        List<Integer> times = IntStream.rangeClosed(initialSimulationTime, NUMBER_TIMES_TO_TEST_UTILIZATION).mapToObj(i -> i).collect(toList());
        CloudSim simulation = CloudSimMocker.createMock(mocker -> mocker.clock(times));

        Function<UtilizationModelDynamic, Double> incrementFunction =
            um -> {
                return um.getUtilization() + um.getTimeSpan() * utilizationPercentageIncrement;
            };

        UtilizationModelDynamic um = new UtilizationModelDynamic(initialUtilization);

        um
          .setUtilizationUpdateFunction(incrementFunction)
          .setSimulation(simulation);

        return um;
    }

    private UtilizationModelDynamic createUtilizationModel(double utilizationPercentageIncrement, double initialUtilization) {
        return createUtilizationModel(utilizationPercentageIncrement, initialUtilization, 0);
    }

    @Test
    public void testGetUtilization_oneParamConstructor() {
        System.out.println("getUtilization");
        final double utilizationPercentageIncrement = 0.2, initialUtilization = 0;
        UtilizationModelDynamic instance = createUtilizationModel(utilizationPercentageIncrement, initialUtilization);
        checkUtilization(initialUtilization, utilizationPercentageIncrement, instance);
    }

    @Test
    public void testGetUtilization_twoParamConstructor() {
        System.out.println("getUtilization");
        final double utilizationPercentageIncrement = 0.2, initialUtilization = 0.5;
        UtilizationModelDynamic instance = createUtilizationModel(utilizationPercentageIncrement, initialUtilization);
        checkUtilization(initialUtilization, utilizationPercentageIncrement, instance);
    }

    @Test
    public void testGetUtilization_twoParamConstructorAndMaxUtilization() {
        System.out.println("getUtilization");
        final double utilizationPercentageIncrement = 0.2, initialUtilization = 0.5;
        final double maxUtilizationPercentage = 0.7;
        UtilizationModelDynamic instance = createUtilizationModel(utilizationPercentageIncrement, initialUtilization);
        checkUtilization(
            initialUtilization, utilizationPercentageIncrement,
            maxUtilizationPercentage, instance);
    }

    private void checkUtilization(final double initialUtilization, final double utilizationPercentageIncrement, UtilizationModelDynamic instance) {
        checkUtilization(initialUtilization, utilizationPercentageIncrement,
            Conversion.HUNDRED_PERCENT, instance);
    }

    private void checkUtilization(final double initialUtilization,
                                  final double utilizationPercentageIncrement,
                                  final double maxUtilizationPercentage,
                                  UtilizationModelDynamic instance)
    {
        instance.setMaxResourceUtilization(maxUtilizationPercentage);
        IntStream.rangeClosed(0, NUMBER_TIMES_TO_TEST_UTILIZATION).forEach(time -> {
            double expResult =
                computeExpectedUtilization(
                    time, initialUtilization,
                    utilizationPercentageIncrement,
                    maxUtilizationPercentage);
            double result = instance.getUtilization(time);
            String msg = String.format("The utilization at time %d", time);
            assertEquals(msg, expResult, result, 0.001);
        });
    }

    private double computeExpectedUtilization(double time, double initialUtilizationPercentage,
                                              double utilizationPercentageIncrement, double maxUtilizationPercentage) {
        final double utilizationPercentage =
            initialUtilizationPercentage + (time * utilizationPercentageIncrement);

        if (utilizationPercentageIncrement >= 0)
            return Math.min(utilizationPercentage, maxUtilizationPercentage);

        return Math.max(0, utilizationPercentage);
    }

    @Test
    public void testGetUtilization1() {
        System.out.println("getUtilizationIncrementPerSecond");
        final double increment = 0.1;
        final int initialTime = 1;
        UtilizationModelDynamic instance = createUtilizationModel(increment, 0, initialTime);
        assertEquals(increment, instance.getUtilization(initialTime), 0.0);
    }

    @Test
    public void testGetUtilization_NegativeIncrement() {
        final double increment = -0.1, initialUsage = 1;
        final double expResult = 0.9;
        final int initialTime = 1;
        UtilizationModelDynamic instance = createUtilizationModel(increment, initialUsage, initialTime);
        assertEquals(expResult, instance.getUtilization(initialTime), 0.0);
    }

    @Test
    public void testConstructor_zeroUtilizationPercentageIncrementPerSecond() {
        final double increment = 0, initialUsage = 0.1;
        final int initialTime = 1;
        UtilizationModelDynamic instance = createUtilizationModel(increment, initialUsage, initialTime);
        assertEquals(initialUsage, instance.getUtilization(initialTime), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_utilizationPercentageIncrementPerSecondLowerThanMinusOne() {
        new UtilizationModelDynamic(-1.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_UtilizationPercentageIncrementGreaterThan1() {
        new UtilizationModelDynamic(1.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_NegativeInitialValue() {
        new UtilizationModelDynamic(Unit.PERCENTAGE, -1.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_InitialValueGreaterThan1() {
        new UtilizationModelDynamic(Unit.PERCENTAGE, 2);
    }

    @Test
    public void testTwoParamsConstructor_zeroInitialUtilization() {
        UtilizationModelDynamic instance;
        double expResult = 0;
        instance = new UtilizationModelDynamic(0);
        assertEquals(expResult, instance.getUtilization(), 0.0);
    }

    @Test
    public void testSetMaxResourceUsagePercentage() {
        System.out.println("setMaxResourceUsagePercentage");

        UtilizationModelDynamic instance = new UtilizationModelDynamic();
        assertEquals(
            Conversion.HUNDRED_PERCENT,
            instance.getMaxResourceUtilization(), 0);

        final double maxResourceUsagePercentage = 0.9;
        instance.setMaxResourceUtilization(maxResourceUsagePercentage);
        assertEquals(maxResourceUsagePercentage, instance.getMaxResourceUtilization(), 0);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetMaxResourceUsagePercentage_valueGreaterThanOne() {
        UtilizationModelDynamic instance = new UtilizationModelDynamic();
        instance.setMaxResourceUtilization(1.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetMaxResourceUsagePercentage_negativeValue() {
        UtilizationModelDynamic instance = new UtilizationModelDynamic();
        instance.setMaxResourceUtilization(-1);
        instance.setMaxResourceUtilization(-0.1);
    }

}
