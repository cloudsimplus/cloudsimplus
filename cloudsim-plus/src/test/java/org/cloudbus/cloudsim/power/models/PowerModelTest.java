package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.junit.Test;

import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

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
        IntStream.range(0, 10000).forEach(usage -> assertEquals(msg, EXPECTED_POWER, instance.getPower(usage), 0.0));

    }

    /* default */ static Host createHostWithOneVm(){
        final Host host = HostPowerTest.createPowerHost(10);
        host.createVm(new VmSimple(0, 1000, 1));
        return host;
    }

    /**
     * Assigns a Host to a given PowerModel.
     * @param powerModel the PowerModel to assign a Host to
     * @param <T> the PowerModel generic class
     * @return the given PowerModel after has been assigned a Host
     */
    /* default */ static <T extends PowerModel> T assignHostForPowerModel(final T powerModel){
        powerModel.setHost(PowerModelTest.createHostWithOneVm());
        return powerModel;
    }
}
