/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.distributions;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.statistics.distribution.LogNormalDistribution;

/**
 * A Pseudo-Random Number Generator following the
 * <a href="https://en.wikipedia.org/wiki/Log-normal_distribution">Log-normal distribution</a>.
 *
 * @author Marcos Dias de Assuncao
 * @since CloudSim Toolkit 1.0
 */
public class LogNormalDistr extends ContinuousDistributionAbstract {
    /**
     * Creates a Log-normal Pseudo-Random Number Generator (PRNG).
     *
     * @param shape the shape parameter of this distribution
     * @param scale the scale parameter of this distribution
     */
    public LogNormalDistr(final double shape, final double scale) {
        this(shape, scale, StatisticalDistribution.defaultSeed());
    }

    /**
     * Creates a Log-normal Pseudo-Random Number Generator (PRNG).
     * @param shape the shape parameter of this distribution
     * @param scale the scale parameter of this distribution
     * @param seed the seed
     */
    public LogNormalDistr(final double shape, final double scale, final long seed) {
        this(shape, scale, seed, StatisticalDistribution.newDefaultGen(seed));
    }

    /**
     * Creates a Log-normal Pseudo-Random Number Generator (PRNG).
     * @param shape the shape parameter of this distribution
     * @param scale the scale parameter of this distribution
     * @param seed the seed
     */
    public LogNormalDistr(final double shape, final double scale, final long seed, final UniformRandomProvider rng) {
        super(LogNormalDistribution.of(scale, shape), seed, rng);
    }
}
