/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.utilizationmodels;

import org.cloudsimplus.distributions.ContinuousDistribution;
import org.cloudsimplus.distributions.UniformDistr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

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
        final UniformDistr prng = new NegativePrng();
        utilizationModel.setRandomGenerator(prng);
        /*Even if the PRNG always return a negative value (-1 in this case),
        * the UtilizationModel must get its absolute value to
        * return the resource utilization.*/
        final double expected = 1;
        final double result = utilizationModel.getUtilization();
        assertEquals(expected, result);
    }

    /**
     * Since the {@link UtilizationModelStochastic#getUtilization(double)} may just
     * return previously generated utilization value for a given time or it can
     * generate an utilization value if the method was never called with a given time,
     * we check if, when calling the method with a time that was previously used,
     * if it returns the same value instead of generating a new random one.
     */
    @Test
    public void testUtilizationCallSequence() {
        final int MAX_TIME = 4;

        /* Just calls the getUtilization for some number of times to ensure the generated
        utilization values are stored internally.*/
        IntStream.range(0, MAX_TIME).forEach(time -> utilizationModel.getUtilization(time));

        /*Calls the getUtilization again to check if the same values generated previously are being returned,
        instead of generating new random values*/
        IntStream.range(0, MAX_TIME).forEach(time -> {
            final double expected = utilizationModel.getUtilizationHistory(time);
            assertEquals(expected, utilizationModel.getUtilization(time), "Utilization for time " + time);
        });

        /*Even if calling the getUtilization() again with random time values
        * in the range of values used before, it has to return the same
        * values stored in the history map, instead
        * of generating new utilization values randomly.*/
        final ContinuousDistribution prng = new UniformDistr(0, MAX_TIME);
        for (int i = 0; i < MAX_TIME; i++) {
            final int time = (int)prng.sample();
            final double expected = utilizationModel.getUtilizationHistory(time);
            assertEquals(expected, utilizationModel.getUtilization(time), "Utilization for time " + time);
        }
    }

    /**
     * A Pseudo Random Number Generator (PRNG) that always returns -1.
     */
    private final class NegativePrng extends UniformDistr{
        @Override
        public double sample() {
            return -1;
        }
    }
}
