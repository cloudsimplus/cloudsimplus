/*
 * Title:        CloudSim Toolkit
 * Description:  Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.distributions;

import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * A Pseudo-Random Number Generator following the
 * <a href="http://en.wikipedia.org/wiki/Zipf's_law">Zipf</a> distribution.
 *
 * @author Marcos Dias de Assuncao
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class ZipfDistr implements DiscreteDistribution {
    /** @see #isApplyAntitheticVariates() */
    private boolean applyAntitheticVariates;

    private final long seed;
    private final RandomGenerator rng;

    /**
     * The shape distribution parameter
     */
    private final double shape;

    /**
     * The den.
     */
    private double den;

    /**
     * Creates a Zipf Pseudo-Random Number Generator (RNG).
     *
     * <p>Internally, it relies on the {@link JDKRandomGenerator},
     * a wrapper for the {@link java.util.Random} class
     * that doesn't have high-quality randomness properties
     * but is very fast.</p>
     *
     * @param shape the shape distribution parameter
     * @param population the population distribution parameter
     *
     * @see #ZipfDistr(double, int, long, RandomGenerator)
     */
    public ZipfDistr(final double shape, final int population) {
        this(shape, population, StatisticalDistribution.defaultSeed());
    }

    /**
     * Creates a Zipf Pseudo-Random Number Generator (RNG).
     *
     * <p>Internally, it relies on the {@link JDKRandomGenerator},
     * a wrapper for the {@link java.util.Random} class
     * that doesn't have high-quality randomness properties
     * but is very fast.</p>
     *
     * @param shape the shape distribution parameter
     * @param population the population distribution parameter
     * @param seed the seed
     *
     * @see #ZipfDistr(double, int, long, RandomGenerator)
     */
    public ZipfDistr(final double shape, final int population, final long seed) {
        this(shape, population, seed, StatisticalDistribution.newDefaultGen(seed));
    }

    /**
     * Creates a Zipf Pseudo-Random Number Generator (RNG).
     * @param shape the shape distribution parameter
     * @param population the population distribution parameter
     * @param seed the seed <b>already used</b> to initialize the Pseudo-Random Number Generator
     * @param rng the actual Pseudo-Random Number Generator that will be the base
     *            to generate random numbers following a continuous distribution.
     */
    public ZipfDistr(final double shape, final int population, final long seed, final RandomGenerator rng) {
        if (shape <= 0.0 || population < 1) {
            throw new IllegalArgumentException("Mean must be greater than 0.0 and population greater than 0");
        }

        if(seed < 0){
            throw new IllegalArgumentException("Seed cannot be negative");
        }

        this.rng = rng;
        this.shape = shape;
        this.seed = seed;
        computeDen(shape, population);
    }

    @Override
    public double sample() {
        final double variate = rng.nextDouble();
        double num = 1;
        double nextNum = 1 + 1 / Math.pow(2, shape);
        double i = 3;

        while (variate > nextNum / den) {
            num = nextNum;
            nextNum += 1 / Math.pow(i, shape);
            i++;
        }

        return num / den;
    }

    @Override
    public long getSeed() {
        return this.seed;
    }

    @Override
    public boolean isApplyAntitheticVariates() {
        return applyAntitheticVariates;
    }

    @Override
    public ZipfDistr setApplyAntitheticVariates(final boolean applyAntitheticVariates) {
        this.applyAntitheticVariates = applyAntitheticVariates;
        return this;
    }

    @Override
    public double originalSample() {
        return rng.nextDouble();
    }

    /**
     * Computes and stores the {@link #den}.
     *
     * @param shape the shape distribution parameter
     * @param population the population distribution parameter
     */
    private void computeDen(final double shape, final int population) {
        this.den = 0.0;
        for (int i = 1; i <= population; i++) {
            this.den += 1 / Math.pow(i, shape);
        }
    }
}
