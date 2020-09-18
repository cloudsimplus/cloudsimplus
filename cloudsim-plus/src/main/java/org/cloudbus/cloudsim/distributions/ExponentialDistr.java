/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.distributions;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * A Pseudo-Random Number Generator following the
 * <a href="https://en.wikipedia.org/wiki/Exponential_distribution">Exponential
 * distribution</a>.
 *
 * @author Marcos Dias de Assuncao
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class ExponentialDistr extends ExponentialDistribution implements ContinuousDistribution {
    /** @see #isApplyAntitheticVariates() */
    private boolean applyAntitheticVariates;
    private long seed;

    /**
     * Creates a exponential Pseudo-Random Number Generator (RNG).
     *
     * <p>Internally, it relies on the {@link JDKRandomGenerator},
     * a wrapper for the {@link java.util.Random} class
     * that doesn't have high-quality randomness properties
     * but is very fast.</p>
     *
     * @param mean the mean for the distribution.
     * @see #ExponentialDistr(double, long, RandomGenerator)
     */
    public ExponentialDistr(final double mean) {
        this(mean, StatisticalDistribution.defaultSeed());
    }

    /**
     * Creates a exponential Pseudo-Random Number Generator (RNG).
     *
     * <p>Internally, it relies on the {@link JDKRandomGenerator},
     * a wrapper for the {@link java.util.Random} class
     * that doesn't have high-quality randomness properties
     * but is very fast.</p>
     *
     * @param mean the mean for the distribution.
     *
     * @param seed the seed to be used.
     * @see #ExponentialDistr(double, long, RandomGenerator)
     */
    public ExponentialDistr(final double mean, final long seed) {
        this(mean, seed, StatisticalDistribution.newDefaultGen(seed));
    }

    /**
     * Creates a exponential Pseudo-Random Number Generator (RNG).
     * @param mean the mean for the distribution.
     * @param seed the seed <b>already used</b> to initialize the Pseudo-Random Number Generator
     * @param rng the actual Pseudo-Random Number Generator that will be the base
     *            to generate random numbers following a continuous distribution.
     */
    public ExponentialDistr(final double mean, final long seed, final RandomGenerator rng) {
        super(rng, mean);
        if(seed < 0){
            throw new IllegalArgumentException("Seed cannot be negative");
        }
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
    public ExponentialDistr setApplyAntitheticVariates(final boolean applyAntitheticVariates) {
        this.applyAntitheticVariates = applyAntitheticVariates;
        return this;
    }

    @Override
    public double originalSample() {
        return super.sample();
    }
}
