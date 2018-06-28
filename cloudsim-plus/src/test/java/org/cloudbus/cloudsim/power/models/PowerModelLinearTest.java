/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.power.models;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author	Anton Beloglazov
 * @since	CloudSim Toolkit 2.0
 */
public class PowerModelLinearTest {

    private static final double MAX_POWER = 250;
    private static final double STATIC_POWER_PERCENT = 0.7;

    private PowerModelLinear powerModel;

    @Before
    public void setUp() throws Exception {
        powerModel = new PowerModelLinear(MAX_POWER, STATIC_POWER_PERCENT);
        powerModel.setHost(PowerModelTest.createHostWithOneVm());
    }

    @Test
    public void testGetMaxPower() {
        assertEquals(MAX_POWER, powerModel.getMaxPower(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPowerArgumentLessThenZero() throws IllegalArgumentException {
        powerModel.getPower(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPowerArgumentLargerThenOne() throws IllegalArgumentException {
        powerModel.getPower(2);
    }

    @Test
    public void testGetPowerForZeroUsage() {
        assertEquals(175, powerModel.getPower(0.0), 0);
    }

    @Test
    public void testGetPowerForHundredPercentUsage() {
        assertEquals(MAX_POWER, powerModel.getPower(1.0), 0);
    }

    @Test
    public void testGetPowerForCustomUsage() {
        assertEquals(MAX_POWER * STATIC_POWER_PERCENT + ((MAX_POWER - MAX_POWER * STATIC_POWER_PERCENT) / 100) * 0.5 * 100, powerModel.getPower(0.5), 0);
    }

}
