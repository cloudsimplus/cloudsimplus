/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.power.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author	Anton Beloglazov
 * @since	CloudSim Toolkit 2.0
 */
public class PowerModelLinearTest {

    private static final double MAX_POWER = 250;
    private static final double STATIC_POWER_PERCENT = 0.7;

    private PowerModelLinear powerModel;

    @BeforeEach
    public void setUp() {
        powerModel = new PowerModelLinear(MAX_POWER, STATIC_POWER_PERCENT);
        powerModel.setHost(PowerModelTest.createHostWithOneVm());
    }

    @Test
    public void testGetMaxPower() {
        assertEquals(MAX_POWER, powerModel.getMaxPower());
    }

    @Test()
    public void testGetPowerArgumentLessThenZero()  {
        Assertions.assertThrows(IllegalArgumentException.class, () -> powerModel.getPower(-1));
    }

    @Test()
    public void testGetPowerArgumentLargerThenOne()  {
        Assertions.assertThrows(IllegalArgumentException.class, () -> powerModel.getPower(2));
    }

    @Test
    public void testGetPowerForZeroUsage() {
        assertEquals(175, powerModel.getPower(0.0));
    }

    @Test
    public void testGetPowerForHundredPercentUsage() {
        assertEquals(MAX_POWER, powerModel.getPower(1.0));
    }

    @Test
    public void testGetPowerForCustomUsage() {
        final double expected = MAX_POWER * STATIC_POWER_PERCENT + ((MAX_POWER - MAX_POWER * STATIC_POWER_PERCENT) / 100) * 0.5 * 100;
        assertEquals(expected, powerModel.getPower(0.5));
    }

}
