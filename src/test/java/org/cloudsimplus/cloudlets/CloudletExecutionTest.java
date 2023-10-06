package org.cloudsimplus.cloudlets;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Manoel Campos da Silva Filho
 */
class CloudletExecutionTest {
    private final CloudletExecution instance = new CloudletExecution(Cloudlet.NULL);

    @Test
    public void testTimeSpanIntegerTime(){
        instance.setLastProcessingTime(0);
        final double expected = 10;
        assertEquals(expected, instance.processingTimeSpan(expected), 0.01);
    }

    @Test
    public void testTimeSpanLessThan1(){
        instance.setLastProcessingTime(0);
        final double expected = 0.6;
        assertEquals(expected, instance.processingTimeSpan(expected), 0.01);
    }

    @Test
    public void testTimeSpanGreaterThan1AndLessThan2(){
        instance.setLastProcessingTime(0);
        final double expected = 1.7;
        assertEquals(expected, instance.processingTimeSpan(expected), 0.01);
    }
}
