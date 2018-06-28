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
public class PowerModelSqrtTest {

    private static final double MAX_POWER = 200;
    private static final double STATIC_POWER_PERCENT = 0.3;

    private PowerModelSqrt powerModel;

    @Before
    public void setUp() throws Exception {
        powerModel = new PowerModelSqrt(MAX_POWER, STATIC_POWER_PERCENT);
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
    public void testGetPowerZeroUsage() {
        assertEquals(60, powerModel.getPower(0.0), 0);
    }

    @Test
    public void testGetPowerHundredPercentUsage() {
        assertEquals(MAX_POWER, powerModel.getPower(1.0), 0);
    }

    @Test
    public void testGetPowerCustomUsage() {
        assertEquals(MAX_POWER * STATIC_POWER_PERCENT + (MAX_POWER - MAX_POWER * STATIC_POWER_PERCENT) / Math.sqrt(100) * Math.sqrt(0.5 * 100), powerModel.getPower(0.5), 0);
    }

}
