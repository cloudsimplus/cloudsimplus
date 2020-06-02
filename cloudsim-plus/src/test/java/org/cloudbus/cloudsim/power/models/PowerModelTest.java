package org.cloudbus.cloudsim.power.models;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        for (int usage = 0; usage < 10000; usage++) {
            assertEquals(EXPECTED_POWER, instance.getPower(usage), msg);
        }

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
