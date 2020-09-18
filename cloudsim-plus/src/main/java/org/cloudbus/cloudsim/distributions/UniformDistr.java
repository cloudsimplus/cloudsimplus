/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.distributions;

import org.apache.commons.lang3.Range;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * A Pseudo-Random Number Generator (RNG) following the
 * <a href="https://en.wikipedia.org/wiki/Uniform_distribution_(continuous)">
 * Uniform continuous distribution</a>.
 *
 * @author Marcos Dias de Assuncao
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class UniformDistr extends UniformRealDistribution implements ContinuousDistribution {
    /** @see #isApplyAntitheticVariates() */
    private boolean applyAntitheticVariates;
    private long seed;

    /**
     * Creates a uniform Pseudo-Random Number Generator (RNG)
     * that generates values between [0 and 1[ using the current time as seed.
     *
     * <p>Internally, it relies on the {@link JDKRandomGenerator},
     * a wrapper for the {@link java.util.Random} class
     * that doesn't have high-quality randomness properties
     * but is very fast.</p>
     *
     * @see #UniformDistr(double, double, long, RandomGenerator)
     */
    public UniformDistr() {
        this(0, 1);
    }

    /**
     * Creates a uniform Pseudo-Random Number Generator (RNG)
     * that generates values between [0 and 1[ using a given seed.
     *
     * <p>Internally, it relies on the {@link JDKRandomGenerator},
     * a wrapper for the {@link java.util.Random} class
     * that doesn't have high-quality randomness properties
     * but is very fast.</p>
     *
     * @param seed the seed to initialize the Pseudo-Random Number Generator.
     *
     * @see #UniformDistr(double, double, long, RandomGenerator)
     */
    public UniformDistr(final long seed) {
        this(0, 1, seed);
    }

    /**
     * Creates a uniform Pseudo-Random Number Generator (RNG)
     * that generates values between [0 and 1[ using a given seed.
     *
     * <p>Internally, it relies on the {@link JDKRandomGenerator},
     * a wrapper for the {@link java.util.Random} class
     * that doesn't have high-quality randomness properties
     * but is very fast.</p>
     *
     * @param seed the seed <b>already used</b> to initialize the Pseudo-Random Number Generator
     * @param rng the actual Pseudo-Random Number Generator that will be the base
     *            to generate random numbers following a continuous distribution.
     *
     * @see #UniformDistr(double, double, long, RandomGenerator)
     */
    public UniformDistr(final long seed, final RandomGenerator rng) {
        this(0, 1, seed, rng);
    }

    /**
     * Creates a uniform Pseudo-Random Number Generator (RNG)
     * that produces values between a given {@link Range},
     * using the current time as seed.
     *
     * <p>Internally, it relies on the {@link JDKRandomGenerator},
     * a wrapper for the {@link java.util.Random} class
     * that doesn't have high-quality randomness properties
     * but is very fast.</p>
     *
     * @param range the {@link Range} to generate random values in between
     *
     * @see #UniformDistr(double, double, long, RandomGenerator)
     */
    public UniformDistr(final Range<Double> range) {
        this(range, StatisticalDistribution.defaultSeed());
    }

    /**
     * Creates a uniform Pseudo-Random Number Generator (RNG)
     * that produces values between a given {@link Range}.
     *
     * <p>Internally, it relies on the {@link JDKRandomGenerator},
     * a wrapper for the {@link java.util.Random} class
     * that doesn't have high-quality randomness properties
     * but is very fast.</p>
     *
     * @param range the {@link Range} to generate random values in between
     * @param seed the seed to initialize the Pseudo-Random Number Generator
     *
     * @see #UniformDistr(double, double, long, RandomGenerator)
     */
    public UniformDistr(final Range<Double> range, final long seed) {
        this(range.getMinimum(), range.getMaximum()+1, seed);
    }

    /**
     * Creates a uniform Pseudo-Random Number Generator (RNG)
     * that produces values between a min (inclusive) and max (exclusive),
     * using the current time as seed.
     *
     * <p>Internally, it relies on the {@link JDKRandomGenerator},
     * a wrapper for the {@link java.util.Random} class
     * that doesn't have high-quality randomness properties
     * but is very fast.</p>
     *
     * @param minInclusive minimum value to generate (inclusive)
     * @param maxExclusive maximum value to generate (exclusive)
     *
     * @see #UniformDistr(double, double, long, RandomGenerator)
     */
    public UniformDistr(final double minInclusive, final double maxExclusive) {
        this(minInclusive, maxExclusive, StatisticalDistribution.defaultSeed());
    }

    /**
     * Creates a uniform Pseudo-Random Number Generator (RNG).
     *
     * <p>Internally, it relies on the {@link JDKRandomGenerator},
     * a wrapper for the {@link java.util.Random} class
     * that doesn't have high-quality randomness properties
     * but is very fast.</p>
     *
     * @param minInclusive minimum value to generate (inclusive)
     * @param maxExclusive maximum value to generate (exclusive)
     * @param seed the seed to initialize the Pseudo-Random Number Generator.
     *
     * @see #UniformDistr(double, double, long, RandomGenerator)
     */
    public UniformDistr(final double minInclusive, final double maxExclusive, final long seed) {
        this(minInclusive, maxExclusive, seed, StatisticalDistribution.newDefaultGen(seed));
    }

    /**
     * Creates a uniform Pseudo-Random Number Generator (RNG).
     * @param minInclusive minimum value to generate (inclusive)
     * @param maxExclusive maximum value to generate (exclusive)
     * @param seed the seed <b>already used</b> to initialize the Pseudo-Random Number Generator
     * @param rng the actual Pseudo-Random Number Generator that will be the base
     *            to generate random numbers following a continuous distribution.
     */
    public UniformDistr(final double minInclusive, final double maxExclusive, final long seed, final RandomGenerator rng) {
        super(rng, minInclusive, maxExclusive);
        if(seed < 0){
            throw new IllegalArgumentException("Seed cannot be negative");
        }

        this.seed = seed;
        applyAntitheticVariates = false;
    }

    @Override
    public double sample() {
        return applyAntitheticVariates ? 1 - super.sample() : super.sample();
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
    public UniformDistr setApplyAntitheticVariates(final boolean applyAntitheticVariates) {
        this.applyAntitheticVariates = applyAntitheticVariates;
	    return this;
    }

    @Override
    public double originalSample() {
        return super.sample();
    }

    @Override
    public void reseedRandomGenerator(final long seed) {
        super.reseedRandomGenerator(seed);
        this.seed = seed;
    }
}
