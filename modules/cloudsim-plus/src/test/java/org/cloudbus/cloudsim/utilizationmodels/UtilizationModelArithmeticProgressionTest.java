package org.cloudbus.cloudsim.utilizationmodels;

import java.util.stream.IntStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class UtilizationModelArithmeticProgressionTest {
    
    @Test
    public void testGetUtilization_defaultConstructor() {
        System.out.println("getUtilization");
        final double utilizationPercentageIncrement = 0.1, initialUtilization = 0;
        UtilizationModelArithmeticProgression instance = new UtilizationModelArithmeticProgression();
        checkUtilization(initialUtilization, utilizationPercentageIncrement, instance);
    }

    @Test
    public void testGetUtilization_twoParamConstructorDecreasingUtilization() {
        System.out.println("getUtilization");
        final double utilizationPercentageIncrement = -0.1, initialUtilization = 0.5;
        UtilizationModelArithmeticProgression instance = 
                new UtilizationModelArithmeticProgression(utilizationPercentageIncrement, initialUtilization);
        checkUtilization(initialUtilization, utilizationPercentageIncrement, instance);
    }

    @Test
    public void testGetUtilization_oneParamConstructor() {
        System.out.println("getUtilization");
        final double utilizationPercentageIncrement = 0.2, initialUtilization = 0;
        UtilizationModelArithmeticProgression instance = 
                new UtilizationModelArithmeticProgression(utilizationPercentageIncrement);
        checkUtilization(initialUtilization, utilizationPercentageIncrement, instance);
    }

    @Test
    public void testGetUtilization_twoParamConstructor() {
        System.out.println("getUtilization");
        final double utilizationPercentageIncrement = 0.2, initialUtilization = 0.5;
        UtilizationModelArithmeticProgression instance = 
                new UtilizationModelArithmeticProgression(utilizationPercentageIncrement, initialUtilization);
       checkUtilization(initialUtilization, utilizationPercentageIncrement, instance);
    }
    
    @Test
    public void testGetUtilization_twoParamConstructorAndMaxUtilization() {
        System.out.println("getUtilization");
        final double utilizationPercentageIncrement = 0.2, initialUtilization = 0.5;
        final double maxUtilizationPercentage = 0.7;
        UtilizationModelArithmeticProgression instance = 
                new UtilizationModelArithmeticProgression(
                        utilizationPercentageIncrement, initialUtilization);
       checkUtilization(
               initialUtilization, utilizationPercentageIncrement, 
               maxUtilizationPercentage, instance);
    }


    private void checkUtilization(final double initialUtilization, final double utilizationPercentageIncrement, UtilizationModelArithmeticProgression instance) {
        checkUtilization(initialUtilization, utilizationPercentageIncrement, 
                UtilizationModelArithmeticProgression.HUNDRED_PERCENT, instance);
    }
    
    private void checkUtilization(final double initialUtilization, 
            final double utilizationPercentageIncrement, 
            final double maxUtilizationPercentage, 
            UtilizationModelArithmeticProgression instance) {
        instance.setMaxResourceUsagePercentage(maxUtilizationPercentage);
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
    public void testGetSetUtilizationPercentageIncrementPerSecond() {
        System.out.println("getUtilizationPercentageIncrementPerSecond");
        double expResult = 0.1;
        UtilizationModelArithmeticProgression instance = 
                new UtilizationModelArithmeticProgression(expResult);
        assertEquals(expResult, instance.getUtilizationPercentageIncrementPerSecond(), 0.0);
    }

    @Test
    public void testGetSetUtilizationPercentageIncrementPerSecond_defaultValue() {
        UtilizationModelArithmeticProgression instance =
                new UtilizationModelArithmeticProgression();
        assertEquals(UtilizationModelArithmeticProgression.ONE_PERCENT, 
                instance.getUtilizationPercentageIncrementPerSecond(), 0.0);
    }

    @Test
    public void testConstructor_negativeUtilizationPercentageIncrementPerSecond() {
        double expResult = -0.1;
        UtilizationModelArithmeticProgression instance = 
                new UtilizationModelArithmeticProgression(expResult);
        assertEquals(expResult, instance.getUtilizationPercentageIncrementPerSecond(), 0.0);
    }

    @Test
    public void testConstructor_zeroUtilizationPercentageIncrementPerSecond() {
        double zero = 0;
        UtilizationModelArithmeticProgression instance = 
                new UtilizationModelArithmeticProgression(zero);
        assertEquals(zero, instance.getUtilizationPercentageIncrementPerSecond(), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_utilizationPercentageIncrementPerSecondLowerThanMinusOne() {
        new UtilizationModelArithmeticProgression(-1.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_UtilizationPercentageIncrementGreaterThan1() {
        new UtilizationModelArithmeticProgression(1.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetInitialUtilization() {
        System.out.println("getInitialUtilization");
        UtilizationModelArithmeticProgression instance = new UtilizationModelArithmeticProgression();
        assertEquals(0, instance.getInitialUtilization(), 0.0);
        
        new UtilizationModelArithmeticProgression(0, -1.1);
        new UtilizationModelArithmeticProgression(0, -1.0);
        new UtilizationModelArithmeticProgression(0, -0.1);
        new UtilizationModelArithmeticProgression(0, 1.1);
    }

    @Test
    public void testTwoParamsConstructor_positiveInitialUtilization() {
        UtilizationModelArithmeticProgression instance;
        double expResult = 0.1;
        instance = new UtilizationModelArithmeticProgression(0, expResult);
        assertEquals(expResult, instance.getInitialUtilization(), 0.0);
    }

    @Test
    public void testTwoParamsConstructor_zeroInitialUtilization() {
        UtilizationModelArithmeticProgression instance;
        double expResult = 0;
        instance = new UtilizationModelArithmeticProgression(0, expResult);
        assertEquals(expResult, instance.getInitialUtilization(), 0.0);
    }

    @Test
    public void testSetMaxResourceUsagePercentage() {
        System.out.println("setMaxResourceUsagePercentage");
        
        UtilizationModelArithmeticProgression instance = new UtilizationModelArithmeticProgression();
        assertEquals(
                UtilizationModelArithmeticProgression.HUNDRED_PERCENT, 
                instance.getMaxResourceUsagePercentage(), 0);
        
        final double maxResourceUsagePercentage = 0.9;
        instance.setMaxResourceUsagePercentage(maxResourceUsagePercentage);
        assertEquals(maxResourceUsagePercentage, instance.getMaxResourceUsagePercentage(), 0);
        
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetMaxResourceUsagePercentage_valueGreaterThanOne() {
        UtilizationModelArithmeticProgression instance = new UtilizationModelArithmeticProgression();
        instance.setMaxResourceUsagePercentage(1.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetMaxResourceUsagePercentage_negativeValue() {
        UtilizationModelArithmeticProgression instance = new UtilizationModelArithmeticProgression();
        instance.setMaxResourceUsagePercentage(-1);
        instance.setMaxResourceUsagePercentage(-0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetMaxResourceUsagePercentage_zeroValue() {
        UtilizationModelArithmeticProgression instance = new UtilizationModelArithmeticProgression();
        instance.setMaxResourceUsagePercentage(0);
    }
    
}
