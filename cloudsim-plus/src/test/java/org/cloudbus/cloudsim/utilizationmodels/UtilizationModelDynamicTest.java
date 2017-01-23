package org.cloudbus.cloudsim.utilizationmodels;

import java.util.stream.IntStream;

import org.cloudbus.cloudsim.util.Conversion;
import org.junit.Test;
import static org.cloudbus.cloudsim.utilizationmodels.UtilizationModel.Unit;
import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class UtilizationModelDynamicTest {

    @Test
    public void testGetUtilization_defaultConstructor() {
        System.out.println("getUtilization");
        final double utilizationPercentageIncrement = 0.1, initialUtilization = 0;
        UtilizationModelDynamic instance = new UtilizationModelDynamic(initialUtilization);
        instance.setUtilizationIncrementFunction((timeSpan, initialUsage) -> initialUsage + timeSpan*utilizationPercentageIncrement);
        checkUtilization(initialUtilization, utilizationPercentageIncrement, instance);
    }

    @Test
    public void testGetUtilization_twoParamConstructorDecreasingUtilization() {
        System.out.println("getUtilization");
        final double utilizationPercentageIncrement = -0.1, initialUtilization = 0.5;
        UtilizationModelDynamic instance = createUtilizationModel(utilizationPercentageIncrement, initialUtilization);
        checkUtilization(initialUtilization, utilizationPercentageIncrement, instance);
    }

    private UtilizationModelDynamic createUtilizationModel(double utilizationPercentageIncrement, double initialUtilization) {
        return new UtilizationModelDynamic(initialUtilization)
            .setUtilizationIncrementFunction((timeSpan, initialUsage) -> initialUsage + timeSpan*utilizationPercentageIncrement);
    }

    @Test
    public void testGetUtilization_oneParamConstructor() {
        System.out.println("getUtilization");
        final double utilizationPercentageIncrement = 0.2, initialUtilization = 0;
        UtilizationModelDynamic instance =
                new UtilizationModelDynamic(initialUtilization);
        instance.setUtilizationIncrementFunction((timeSpan, initialUsage) -> initialUsage + timeSpan*utilizationPercentageIncrement);
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
        IntStream.rangeClosed(0, 400).forEach(time -> {
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
            double utilizationPercentageIncrement, double maxUtilizationPercentage){
        final double utilizationPercentage =
                initialUtilizationPercentage + (time * utilizationPercentageIncrement);

        if(utilizationPercentageIncrement >= 0)
            return  Math.min(utilizationPercentage, maxUtilizationPercentage);

        return Math.max(0, utilizationPercentage);
    }

    @Test
    public void testGetUtilization1() {
        System.out.println("getUtilizationIncrementPerSecond");
        double increment = 0.1;
        UtilizationModelDynamic instance = createUtilizationModel(increment, 0);
        assertEquals(increment, instance.getUtilization(1), 0.0);
    }

    @Test
    public void testGetUtilization_NegativeIncrement() {
        double increment = -0.1, initialUsage = 1;
        double expResult = 0.9;
        UtilizationModelDynamic instance = createUtilizationModel(increment, initialUsage);
        assertEquals(expResult, instance.getUtilization(1), 0.0);
    }

    @Test
    public void testConstructor_zeroUtilizationPercentageIncrementPerSecond() {
        double increment = 0, initialUsage = 0.1;
        UtilizationModelDynamic instance = createUtilizationModel(increment, initialUsage);
        assertEquals(initialUsage, instance.getUtilization(1), 0.0);
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
        new UtilizationModelDynamic(Unit.PERCENTAGE,-1.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_InitialValueGreaterThan1() {
        new UtilizationModelDynamic(Unit.PERCENTAGE,2);
    }

    @Test
    public void testTwoParamsConstructor_zeroInitialUtilization() {
        UtilizationModelDynamic instance;
        double expResult = 0;
        instance = new UtilizationModelDynamic(0);
        assertEquals(expResult, instance.getInitialUtilization(), 0.0);
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
