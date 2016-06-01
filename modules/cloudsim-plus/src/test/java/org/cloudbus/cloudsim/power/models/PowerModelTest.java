package org.cloudbus.cloudsim.power.models;

import java.util.stream.IntStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class PowerModelTest {
    private static final double EXPECTED_POWER = 0;
    
    @Test
    public void testNullObject() {
        System.out.println("testNullObject");
        final PowerModel instance = PowerModel.NULL;
        
        String msg = 
            String.format(
                "For any time (even a randomly defined one), the power usage has to be equals to %.2f", 
                EXPECTED_POWER);
        IntStream.range(0, 10000).forEach(utilization -> {
            assertEquals(msg, 
                    EXPECTED_POWER, instance.getPower(utilization), 0.0);
        });

    }

    
}
