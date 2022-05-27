package org.cloudbus.cloudsim.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.4.2
 */
public class TimeUtilTest {

    @Test
    public void microToMilli1(){
        final double micro = 1;
        final double expectedMilli = 0.001;
        assertEquals(expectedMilli, TimeUtil.microToMilli(micro));
    }

    @Test
    public void microToMilli1000(){
        final double micro = 1000;
        final double expectedMilli = 1;
        assertEquals(expectedMilli, TimeUtil.microToMilli(micro));
    }

}
