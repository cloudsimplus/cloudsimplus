/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.distributions;

import org.apache.commons.lang3.Range;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.statistics.distribution.UniformContinuousDistribution;

/**
 * A Pseudo-Random Number Generator (RNG) following the
 * <a href="https://en.wikipedia.org/wiki/Uniform_distribution_(continuous)">
 * Uniform continuous distribution</a>.
 *
 * @author Marcos Dias de Assuncao
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class UniformDistr extends ContinuousDistributionAbstract {
    /**
     * Creates a uniform Pseudo-Random Number Generator (PRNG)
     * that generates values between [0 and 1[ using the current time as seed.
     *
     * <p>Internally, it uses the {@link StatisticalDistribution#newDefaultGen(long) default PRNG}.</p>
     *
     * @see #UniformDistr(double, double, long, UniformRandomProvider)
     */
    public UniformDistr() {
        this(0, 1);
    }

    /**
     * Creates a uniform Pseudo-Random Number Generator (PRNG)
     * that generates values between [0 and 1[ using a given seed.
     *
     * <p>Internally, it uses the {@link StatisticalDistribution#newDefaultGen(long) default PRNG}.</p>
     *
     * @param seed the seed to initialize the Pseudo-Random Number Generator.
     *
     * @see #UniformDistr(double, double, long, UniformRandomProvider)
     */
    public UniformDistr(final long seed) {
        this(0, 1, seed);
    }

    /**
     * Creates a uniform Pseudo-Random Number Generator (PRNG)
     * that generates values between [0 and 1[ using a given seed.
     *
     * <p>Internally, it uses the {@link StatisticalDistribution#newDefaultGen(long) default PRNG}.</p>
     *
     * @param seed the seed <b>already used</b> to initialize the Pseudo-Random Number Generator
     * @param rng the actual Pseudo-Random Number Generator that will be the base
     *            to generate random numbers following a continuous distribution.
     *
     * @see #UniformDistr(double, double, long, UniformRandomProvider)
     */
    public UniformDistr(final long seed, final UniformRandomProvider rng) {
        this(0, 1, seed, rng);
    }

    /**
     * Creates a uniform Pseudo-Random Number Generator (PRNG)
     * that produces values between a given {@link Range},
     * using the current time as seed.
     *
     * <p>Internally, it uses the {@link StatisticalDistribution#newDefaultGen(long) default PRNG}.</p>
     *
     * @param range the {@link Range} to generate random values in between
     *
     * @see #UniformDistr(double, double, long, UniformRandomProvider)
     */
    public UniformDistr(final Range<Double> range) {
        this(range, StatisticalDistribution.defaultSeed());
    }

    /**
     * Creates a uniform Pseudo-Random Number Generator (PRNG)
     * that produces values between a given {@link Range}.
     *
     * <p>Internally, it uses the {@link StatisticalDistribution#newDefaultGen(long) default PRNG}.</p>
     *
     * @param range the {@link Range} to generate random values in between
     * @param seed the seed to initialize the Pseudo-Random Number Generator
     *
     * @see #UniformDistr(double, double, long, UniformRandomProvider)
     */
    public UniformDistr(final Range<Double> range, final long seed) {
        this(range.getMinimum(), range.getMaximum()+1, seed);
    }

    /**
     * Creates a uniform Pseudo-Random Number Generator (PRNG)
     * that produces values between a min (inclusive) and max (exclusive),
     * using the current time as seed.
     *
     * <p>Internally, it uses the {@link StatisticalDistribution#newDefaultGen(long) default PRNG}.</p>
     *
     * @param minInclusive minimum value to generate (inclusive)
     * @param maxExclusive maximum value to generate (exclusive)
     *
     * @see #UniformDistr(double, double, long, UniformRandomProvider)
     */
    public UniformDistr(final double minInclusive, final double maxExclusive) {
        this(minInclusive, maxExclusive, StatisticalDistribution.defaultSeed());
    }

    /**
     * Creates a uniform Pseudo-Random Number Generator (PRNG).
     *
     * <p>Internally, it uses the {@link StatisticalDistribution#newDefaultGen(long) default PRNG}.</p>
     *
     * @param minInclusive minimum value to generate (inclusive)
     * @param maxExclusive maximum value to generate (exclusive)
     * @param seed the seed to initialize the Pseudo-Random Number Generator.
     *
     * @see #UniformDistr(double, double, long, UniformRandomProvider)
     */
    public UniformDistr(final double minInclusive, final double maxExclusive, final long seed) {
        this(minInclusive, maxExclusive, seed, StatisticalDistribution.newDefaultGen(seed));
    }

    /**
     * Creates a uniform Pseudo-Random Number Generator (PRNG).
     * @param minInclusive minimum value to generate (inclusive)
     * @param maxExclusive maximum value to generate (exclusive)
     * @param seed the seed <b>already used</b> to initialize the Pseudo-Random Number Generator
     * @param rng the actual Pseudo-Random Number Generator that will be the base
     *            to generate random numbers following a continuous distribution.
     */
    public UniformDistr(final double minInclusive, final double maxExclusive, final long seed, final UniformRandomProvider rng) {
        super(UniformContinuousDistribution.of(minInclusive, maxExclusive), seed, rng);
    }
}
