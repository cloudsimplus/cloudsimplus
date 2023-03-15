package org.cloudsimplus.utilizationmodels;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class UtilizationModelFullTest {
    private static final int EXPECTED_UTILIZATION = 1;

    @Test
    public void testGetUtilizationForDifferentTimes() {
        final UtilizationModelFull instance = new UtilizationModelFull();
        final String msg =
                "For any time (even a randomly defined one), the utilization has to be equals to %d"
                .formatted(EXPECTED_UTILIZATION);
        for (int time = 0; time < 10000; time++) {
            assertEquals(EXPECTED_UTILIZATION, instance.getUtilization(time), msg);
        }
    }

}
