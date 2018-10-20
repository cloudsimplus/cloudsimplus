package org.cloudbus.cloudsim.utilizationmodels;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Manoel Campos da Silva Filho
 */
public class UtilizationModelNullTest {
    private static final int EXPECTED_UTILIZATION = 0;

    @Test
    public void testGetUtilizationForDifferentTimes() {
        final UtilizationModel instance = UtilizationModel.NULL;
        final String msg =
            String.format(
                "For any time (even a randomly defined one), the utilization has to be equals to %d",
                EXPECTED_UTILIZATION);

        for (int time = 0; time < 10000; time++) {
            assertEquals(EXPECTED_UTILIZATION, instance.getUtilization(time), msg);
        }
    }
}
