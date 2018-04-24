package org.cloudbus.cloudsim.power.models;

import java.util.stream.IntStream;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.VmSimple;
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

    static Host createHostWithOneVm(){
        final Host host = HostPowerTest.createPowerHost(10);
        host.createVm(new VmSimple(0, 1000, 1));
        return host;
    }


}
