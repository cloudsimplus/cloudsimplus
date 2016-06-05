package org.cloudbus.cloudsim.utilizationmodels;

import java.util.stream.IntStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class UtilizationModelZeroTest {
    
    private static final int EXPECTED_UTILIZATION = 0;
    
    @Test
    public void testGetUtilizationForDifferentTimes() {
        System.out.println("getUtilization");
        UtilizationModelZero instance = new UtilizationModelZero();
        String msg = 
            String.format(
                "For any time (even a randomly defined one), the utilization has to be equals to %d", 
                EXPECTED_UTILIZATION);
        IntStream.range(0, 10000).forEach(time -> {
            assertEquals(msg, 
                    EXPECTED_UTILIZATION, instance.getUtilization(time), 0.0);
        });
    }
    
}
