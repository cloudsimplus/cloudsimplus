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
        this(range, ContinuousDistribution.defaultSeed());
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
        this(minInclusive, maxExclusive, ContinuousDistribution.defaultSeed());
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
        this(minInclusive, maxExclusive, seed, ContinuousDistribution.newDefaultGen(seed));
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

    /**
     * Indicates if the Pseudo-Random Number Generator (RNG) applies the
     * <a href="https://en.wikipedia.org/wiki/Antithetic_variates">Antithetic Variates Technique</a> in order to reduce variance
     * of experiments using the generated numbers.
     *
     * This technique doesn't work for all the cases. However,
     * in the cases it can be applied, in order to it work, one have to
     * perform some actions. Consider an experiment that has to run "n" times.
     * The first half of these experiments has to use the seeds the developer
     * want. However, the second half of the experiments have to
     * set the applyAntitheticVariates attribute to true
     * and use the seeds of the first half of experiments.
     *
     * Thus, the first half of experiments are run using PRNGs that return
     * random numbers as U(0, 1)[seed_1], ..., U(0, 1)[seed_n].
     * The second half of experiments then uses the seeds of the first
     * half of experiments, returning random numbers as
     * 1 - U(0, 1)[seed_1], ..., 1 - U(0, 1)[seed_n].
     *
     * @return true if the technique is applied, false otherwise
     * @see #setApplyAntitheticVariates(boolean)
     */
    public boolean isApplyAntitheticVariates() {
        return applyAntitheticVariates;
    }

    /**
     * Indicates if the Pseudo-Random Number Generator (RNG) applies the
     * <a href="https://en.wikipedia.org/wiki/Antithetic_variates">Antithetic Variates Technique</a> in order to reduce variance
     * of experiments using the generated numbers.
     *
     * @param applyAntitheticVariates true if the technique is to be applied, false otherwise
     * @see #isApplyAntitheticVariates()
     */
    public UniformDistr setApplyAntitheticVariates(final boolean applyAntitheticVariates) {
        if(applyAntitheticVariates && random instanceof JDKThreadLocalRandomGenerator){
            throw new IllegalStateException(
                "The Antithetic Variates Technique cannot be applied when using the " +
                JDKThreadLocalRandomGenerator.class.getSimpleName() + " as underlying PRNG, because it doesn't allow setting a seed explicitly.");
        }

        this.applyAntitheticVariates = applyAntitheticVariates;
	    return this;
    }

    @Override
    public void reseedRandomGenerator(final long seed) {
        super.reseedRandomGenerator(seed);
        this.seed = seed;
    }
}
