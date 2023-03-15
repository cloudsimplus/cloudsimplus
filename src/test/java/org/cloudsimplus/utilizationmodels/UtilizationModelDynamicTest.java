package org.cloudsimplus.utilizationmodels;

import org.cloudsimplus.util.Conversion;
import org.junit.jupiter.api.Test;

import static org.cloudsimplus.utilizationmodels.UtilizationModel.Unit;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Manoel Campos da Silva Filho
 */
public class UtilizationModelDynamicTest {

    @Test
    public void testGetUtilizationWhenDefaultConstructor() {
        final double usagePercentInc = 0.1;
        final double initialUtilization = 0;
        final var instance = TestUtility.createUtilizationModel(usagePercentInc, initialUtilization);
        TestUtility.checkUtilization(initialUtilization, usagePercentInc, instance);
    }

    @Test
    public void testGetUtilizationWhenTwoParamConstructorAndDecreasingUtilization() {
        final double usagePercentInc = -0.1;
        final double initialUtilization = 0.5;
        final var instance = TestUtility.createUtilizationModel(usagePercentInc, initialUtilization);
        TestUtility.checkUtilization(initialUtilization, usagePercentInc, instance);
    }

    @Test
    public void testGetUtilizationWhenOneParamConstructor() {
        final double usagePercentInc = 0.2;
        final double initUsage = 0;
        final var instance = TestUtility.createUtilizationModel(usagePercentInc, initUsage);
        TestUtility.checkUtilization(initUsage, usagePercentInc, instance);
    }

    @Test
    public void testGetUtilizationWhenTwoParamConstructor() {
        final double usagePercentInc = 0.2;
        final double initUsage = 0.5;
        final var instance = TestUtility.createUtilizationModel(usagePercentInc, initUsage);
        TestUtility.checkUtilization(initUsage, usagePercentInc, instance);
    }

    @Test
    public void testGetUtilizationWhenTwoParamConstructorAndMaxUtilization() {
        final double usagePercentInc = 0.2;
        final double initUsage = 0.5;
        final double maxUsagePercent = 0.7;
        final var instance = TestUtility.createUtilizationModel(usagePercentInc, initUsage);
        TestUtility.checkUtilization(initUsage, usagePercentInc, maxUsagePercent, instance);
    }

    @Test
    public void testGetUtilization1() {
        final double increment = 0.1;
        final int initialTime = 1;
        final var instance = TestUtility.createUtilizationModel(increment, 0, initialTime);
        assertEquals(increment, instance.getUtilization(initialTime));
    }

    @Test
    public void testGetUtilizationWhenNegativeIncrement() {
        final double increment = -0.1;
        final double initialUsage = 1;
        final double expResult = 0.9;
        final int initialTime = 1;
        final var instance = TestUtility.createUtilizationModel(increment, initialUsage, initialTime);
        assertEquals(expResult, instance.getUtilization(initialTime));
    }

    @Test
    public void testConstructorWhenZeroUtilizationPercentageIncrementPerSecond() {
        final double increment = 0;
        final double initialUsage = 0.1;
        final int initialTime = 1;
        final var instance = TestUtility.createUtilizationModel(increment, initialUsage, initialTime);
        assertEquals(initialUsage, instance.getUtilization(initialTime));
    }

    @Test()
    public void testConstructorWhenUtilizationPercentageIncrementPerSecondLowerThanMinusOne() {
        assertThrows(IllegalArgumentException.class, () -> new UtilizationModelDynamic(-1.1));
    }

    /**
     * Despite the initial utilization is greater than 1,
     * since {@link UtilizationModel#isOverCapacityRequestAllowed()}
     * default value is false,
     * the max percent utilization allowed is 1 (100%)-
     */
    @Test
    public void testConstructorWhenUtilizationPercentageIncrementGreaterThan1() {
        final double initialUtilizationPercent = 1.1;
        final double maxUtilizationPercent = 1.0;
        final var model = new UtilizationModelDynamic(initialUtilizationPercent);
        assertEquals(maxUtilizationPercent, model.getUtilization());
    }

    @Test
    public void testConstructorWhenUtilizationPercentageIncrementGreaterThan1OverCapacity() {
        final double initialUtilizationPercent = 1.1;
        final var model = new UtilizationModelDynamic(initialUtilizationPercent);
        model.setOverCapacityRequestAllowed(true);
        assertEquals(initialUtilizationPercent, model.getUtilization());
    }

    @Test()
    public void testConstructorWhenNegativeInitialValue() {
        assertThrows(IllegalArgumentException.class, () -> new UtilizationModelDynamic(Unit.PERCENTAGE, -1.1));
    }

    /**
     * Despite the initial utilization is greater than 1,
     * since {@link UtilizationModel#isOverCapacityRequestAllowed()}
     * default value is false,
     * the max percent utilization allowed is 1 (100%)-
     */
    @Test
    public void testConstructorWhenInitialValueGreaterThan1() {
        final int initialUtilization = 2;
        final int maxUtilization = 1;
        final var model = new UtilizationModelDynamic(Unit.PERCENTAGE, initialUtilization);
        assertEquals(maxUtilization, model.getUtilization());
    }

    @Test
    public void testConstructorWhenInitialValueGreaterThan1NoOverCapacityAllowed() {
        final int initialUtilization = 2;
        final var model = new UtilizationModelDynamic(Unit.PERCENTAGE, initialUtilization);
        model.setOverCapacityRequestAllowed(true);
        assertEquals(initialUtilization, model.getUtilization());
    }

    @Test
    public void testTwoParamsConstructorWhenZeroInitialUtilization() {
        final double expResult = 0;
        final var instance = new UtilizationModelDynamic(0);
        assertEquals(expResult, instance.getUtilization());
    }

    @Test
    public void testSetMaxResourceUsagePercentage() {
        final var instance = new UtilizationModelDynamic();
        assertEquals(Conversion.HUNDRED_PERCENT, instance.getMaxResourceUtilization());

        final double maxResourceUsagePercentage = 0.9;
        instance.setMaxResourceUtilization(maxResourceUsagePercentage);
        assertEquals(maxResourceUsagePercentage, instance.getMaxResourceUtilization());
    }

    @Test
    public void testSetMaxResourceUsagePercentageWhenValueGreaterThanOne() {
        final var instance = new UtilizationModelDynamic();
        final double maxResourceUsagePercentage = 1.1;
        instance.setMaxResourceUtilization(maxResourceUsagePercentage);
        assertEquals(maxResourceUsagePercentage, instance.getMaxResourceUtilization());
    }

    @Test()
    public void testSetMaxResourceUsagePercentageWhenNegativeValue() {
        final var instance = new UtilizationModelDynamic();
        assertAll(
            () -> assertThrows(IllegalArgumentException.class, () -> instance.setMaxResourceUtilization(-1)),
            () -> assertThrows(IllegalArgumentException.class, () -> instance.setMaxResourceUtilization(-0.1))
        );
    }
}
