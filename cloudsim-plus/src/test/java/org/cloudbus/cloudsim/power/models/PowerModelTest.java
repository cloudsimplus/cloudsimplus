package org.cloudbus.cloudsim.power.models;

import java.util.stream.IntStream;

import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.hosts.power.PowerHostTest;
import org.cloudbus.cloudsim.vms.power.PowerVm;
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
        final PowerModel instance = PowerModel.NULL;
        final String msg =
            String.format(
                "For any time (even a randomly defined one), the power usage has to be equals to %.2f",
                EXPECTED_POWER);
        IntStream.range(0, 10000).forEach(utilization -> {
            assertEquals(msg,
                    EXPECTED_POWER, instance.getPower(utilization), 0.0);
        });

    }

    static PowerHost createHostWithOneVm(){
        final PowerHost host = PowerHostTest.createPowerHost(0, 10);
        host.createVm(new PowerVm(0, 1000, 1));
        return host;
    }


}
