/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.distributions;

import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * A Pseudo-Random Number Generator following the
 * <a href="https://en.wikipedia.org/wiki/Gamma_distribution">Gamma</a>
 * distribution.
 *
 * @author Marcos Dias de Assuncao
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class GammaDistr extends GammaDistribution implements ContinuousDistribution {
    /** @see #isApplyAntitheticVariates() */
    private boolean applyAntitheticVariates;

    private long seed;

    /**
     * Creates a Gamma Pseudo-Random Number Generator (RNG) using the current time as seed.
     *
     * <p>Internally, it relies on the {@link JDKRandomGenerator},
     * a wrapper for the {@link java.util.Random} class
     * that doesn't have high-quality randomness properties
     * but is very fast.</p>
     *
     * @param shape the shape parameter of this distribution
     * @param scale the scale parameter of this distribution
     *
     * @see #GammaDistr(int, double, long, RandomGenerator)
     */
    public GammaDistr(final int shape, final double scale) {
        this(shape, scale, StatisticalDistribution.defaultSeed());
    }

    /**
     * Creates a Gamma Pseudo-Random Number Generator (RNG).
     *
     * <p>Internally, it relies on the {@link JDKRandomGenerator},
     * a wrapper for the {@link java.util.Random} class
     * that doesn't have high-quality randomness properties
     * but is very fast.</p>
     *
     * @param shape the shape parameter of this distribution
     * @param scale the scale parameter of this distribution
     * @param seed the seed
     *
     * @see #GammaDistr(int, double, long, RandomGenerator)
     */
    public GammaDistr(final int shape, final double scale, final long seed) {
        this(shape, scale, seed, StatisticalDistribution.newDefaultGen(seed));
    }

    /**
     * Creates a Gamma Pseudo-Random Number Generator (RNG).
     * @param shape the shape parameter of this distribution
     * @param scale the scale parameter of this distribution
     * @param seed the seed <b>already used</b> to initialize the Pseudo-Random Number Generator
     * @param rng the actual Pseudo-Random Number Generator that will be the base
*                  to generate random numbers following a continuous distribution.
     */
    public GammaDistr(final int shape, final double scale, final long seed, final RandomGenerator rng) {
        super(rng, shape, scale);
        this.seed = seed;
    }

    @Override
    public long getSeed() {
        return this.seed;
    }

    @Override
    public void reseedRandomGenerator(final long seed) {
        super.reseedRandomGenerator(seed);
        this.seed = seed;
    }

    @Override
    public boolean isApplyAntitheticVariates() {
        return applyAntitheticVariates;
    }

    @Override
    public GammaDistr setApplyAntitheticVariates(final boolean applyAntitheticVariates) {
        this.applyAntitheticVariates = applyAntitheticVariates;
        return this;
    }

    @Override
    public double originalSample() {
        return super.sample();
    }
}
