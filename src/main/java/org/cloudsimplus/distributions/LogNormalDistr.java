/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.distributions;

import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.cloudsimplus.util.MathUtil;

import java.io.Serial;

/**
 * A Pseudo-Random Number Generator following the
 * <a href="https://en.wikipedia.org/wiki/Log-normal_distribution">Log-normal distribution</a>.
 *
 * @author Marcos Dias de Assuncao
 * @since CloudSim Toolkit 1.0
 */
public class LogNormalDistr extends LogNormalDistribution implements ContinuousDistribution {
    @Serial
    private static final long serialVersionUID = -1023800693849880578L;

    /** @see #isApplyAntitheticVariates() */
    private boolean applyAntitheticVariates;

    private long seed;

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
    public LogNormalDistr(final double shape, final double scale, final long seed, final RandomGenerator rng) {
        super(rng, scale, shape);
        this.seed = MathUtil.nonNegative(seed, "Seed");
    }

    @Override
    public void reseedRandomGenerator(final long seed) {
        super.reseedRandomGenerator(seed);
        this.seed = seed;
    }

    @Override
    public long getSeed() {
        return seed;
    }


    @Override
    public boolean isApplyAntitheticVariates() {
        return applyAntitheticVariates;
    }

    @Override
    public LogNormalDistr setApplyAntitheticVariates(final boolean applyAntitheticVariates) {
        this.applyAntitheticVariates = applyAntitheticVariates;
        return this;
    }

    @Override
    public double originalSample() {
        return super.sample();
    }
}
