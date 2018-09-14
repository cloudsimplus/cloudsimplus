package org.cloudbus.cloudsim.utilizationmodels;

import org.cloudbus.cloudsim.util.Conversion;
import org.junit.Test;

import static org.cloudbus.cloudsim.utilizationmodels.TestUtil.checkUtilization;
import static org.cloudbus.cloudsim.utilizationmodels.TestUtil.createUtilizationModel;
import static org.cloudbus.cloudsim.utilizationmodels.UtilizationModel.Unit;
import static org.junit.Assert.assertEquals;

/**
 * @author Manoel Campos da Silva Filho
 */
public class UtilizationModelDynamicTest {

    @Test
    public void testGetUtilizationWhenDefaultConstructor() {
        final double usagePercentInc = 0.1;
        final double initialUtilization = 0;
        final UtilizationModelDynamic instance = createUtilizationModel(usagePercentInc, initialUtilization);
        checkUtilization(initialUtilization, usagePercentInc, instance);
    }

    @Test
    public void testGetUtilizationWhenTwoParamConstructorAndDecreasingUtilization() {
        final double usagePercentInc = -0.1, initialUtilization = 0.5;
        final UtilizationModelDynamic instance = createUtilizationModel(usagePercentInc, initialUtilization);
        checkUtilization(initialUtilization, usagePercentInc, instance);
    }

    @Test
    public void testGetUtilizationWhenOneParamConstructor() {
        final double usagePercentInc = 0.2, initUsage = 0;
        final UtilizationModelDynamic instance = createUtilizationModel(usagePercentInc, initUsage);
        checkUtilization(initUsage, usagePercentInc, instance);
    }

    @Test
    public void testGetUtilizationWhenTwoParamConstructor() {
        final double usagePercentInc = 0.2, initUsage = 0.5;
        final UtilizationModelDynamic instance = createUtilizationModel(usagePercentInc, initUsage);
        checkUtilization(initUsage, usagePercentInc, instance);
    }

    @Test
    public void testGetUtilizationWhenTwoParamConstructorAndMaxUtilization() {
        final double usagePercentInc = 0.2, initUsage = 0.5;
        final double maxUsagePercent = 0.7;
        final UtilizationModelDynamic instance = createUtilizationModel(usagePercentInc, initUsage);
        checkUtilization(
            initUsage, usagePercentInc,
            maxUsagePercent, instance);
    }

    @Test
    public void testGetUtilization1() {
        final double increment = 0.1;
        final int initialTime = 1;
        final UtilizationModelDynamic instance = createUtilizationModel(increment, 0, initialTime);
        assertEquals(increment, instance.getUtilization(initialTime), 0.0);
    }

    @Test
    public void testGetUtilizationWhenNegativeIncrement() {
        final double increment = -0.1, initialUsage = 1;
        final double expResult = 0.9;
        final int initialTime = 1;
        final UtilizationModelDynamic instance = createUtilizationModel(increment, initialUsage, initialTime);
        assertEquals(expResult, instance.getUtilization(initialTime), 0.0);
    }

    @Test
    public void testConstructorWhenZeroUtilizationPercentageIncrementPerSecond() {
        final double increment = 0;
        final double initialUsage = 0.1;
        final int initialTime = 1;
        final UtilizationModelDynamic instance = createUtilizationModel(increment, initialUsage, initialTime);
        assertEquals(initialUsage, instance.getUtilization(initialTime), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWhenUtilizationPercentageIncrementPerSecondLowerThanMinusOne() {
        new UtilizationModelDynamic(-1.1);
    }

    @Test
    public void testConstructorWhenUtilizationPercentageIncrementGreaterThan1() {
        final double initialUtilizationPercent = 1.1;
        final UtilizationModelDynamic model = new UtilizationModelDynamic(initialUtilizationPercent);
        assertEquals(initialUtilizationPercent, model.getUtilization(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWhenNegativeInitialValue() {
        new UtilizationModelDynamic(Unit.PERCENTAGE, -1.1);
    }

    @Test
    public void testConstructorWhenInitialValueGreaterThan1() {
        final int initialUtilization = 2;
        final UtilizationModelDynamic model = new UtilizationModelDynamic(Unit.PERCENTAGE, initialUtilization);
        assertEquals(initialUtilization, model.getUtilization(), 0);
    }

    @Test
    public void testTwoParamsConstructorWhenZeroInitialUtilization() {
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

    @Test
    public void testSetMaxResourceUsagePercentageWhenValueGreaterThanOne() {
        final UtilizationModelDynamic instance = new UtilizationModelDynamic();
        final double maxResourceUsagePercentage = 1.1;
        instance.setMaxResourceUtilization(maxResourceUsagePercentage);
        assertEquals(maxResourceUsagePercentage, instance.getMaxResourceUtilization(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetMaxResourceUsagePercentageWhenNegativeValue() {
        final UtilizationModelDynamic instance = new UtilizationModelDynamic();
        instance.setMaxResourceUtilization(-1);
        instance.setMaxResourceUtilization(-0.1);
    }
}
