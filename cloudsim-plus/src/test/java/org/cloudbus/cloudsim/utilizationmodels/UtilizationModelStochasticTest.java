/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.utilizationmodels;

import org.cloudbus.cloudsim.distributions.UniformDistr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author	Anton Beloglazov
 * @since	CloudSim Toolkit 2.0
 */
public class UtilizationModelStochasticTest {

    private UtilizationModelStochastic utilizationModel;

    @BeforeEach
    public void setUp() {
        utilizationModel = new UtilizationModelStochastic();
    }

    @Test
    public void testGetUtilization() {
        final double utilization0 = utilizationModel.getUtilization(0);
        final double utilization1 = utilizationModel.getUtilization(1);
        assertAll(
            () -> assertNotNull(utilization0),
            () -> assertNotNull(utilization1),
            () -> assertNotSame(utilization0, utilization1),
            () -> assertEquals(utilization0, utilizationModel.getUtilization(0)),
            () -> assertEquals(utilization1, utilizationModel.getUtilization(1))
        );
    }

    @Test
    public void testGetUtilizationNegativeRandomNumber() {
        final NegativePrng negativePrng = new NegativePrng();
        utilizationModel.setRandomGenerator(negativePrng);
        /*Even if the PRNG always return a negative value (-1 in this case),
        * the UtilizationModel must get its absolute value to
        * return the resource utilization.*/
        System.out.println("Generated Pseudo Random Number: " + negativePrng.sample());
        final double expected = 1;
        final double result = utilizationModel.getUtilization();
        assertEquals(expected, result);
    }

    /**
     * A Pseudo Random Number Generator (PRNG) that always return -1.
     */
    private final class NegativePrng extends UniformDistr{
        @Override
        public double sample() {
            return -1;
        }
    }
}
