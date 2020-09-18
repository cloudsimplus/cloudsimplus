/*
 * Title:        CloudSim Toolkit
 * Description:  Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.distributions;

import org.apache.commons.math3.distribution.ParetoDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * A Pseudo-Random Number Generator following the
 * <a href="https://en.wikipedia.org/wiki/Pareto_distribution">Pareto</a>
 * distribution.
 *
 * @author Marcos Dias de Assuncao
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class ParetoDistr extends ParetoDistribution implements ContinuousDistribution {
    /** @see #isApplyAntitheticVariates() */
    private boolean applyAntitheticVariates;

    private long seed;

    /**
     * Creates a Pareto Pseudo-Random Number Generator (RNG) using the current time as seed.
     *
     * <p>Internally, it relies on the {@link JDKRandomGenerator},
     * a wrapper for the {@link java.util.Random} class
     * that doesn't have high-quality randomness properties
     * but is very fast.</p>
     *
     * @param shape the shape parameter of this distribution
     * @param location the location parameter of this distribution
     *
     * @see #ParetoDistr(double, double, long, RandomGenerator)
     */
    public ParetoDistr(final double shape, final double location) {
        this(shape, location, StatisticalDistribution.defaultSeed());
    }

    /**
     * Creates a Pareto Pseudo-Random Number Generator (RNG).
     *
     * <p>Internally, it relies on the {@link JDKRandomGenerator},
     * a wrapper for the {@link java.util.Random} class
     * that doesn't have high-quality randomness properties
     * but is very fast.</p>
     *
     * @param shape the shape parameter of this distribution
     * @param location the location parameter of this distribution
     *
     * @param seed the seed
     * @see #ParetoDistr(double, double, long, RandomGenerator)
     */
    public ParetoDistr(final double shape, final double location, final long seed) {
        this(shape, location, seed, StatisticalDistribution.newDefaultGen(seed));
    }

    /**
     * Creates a Pareto Pseudo-Random Number Generator (RNG).
     * @param shape the shape parameter of this distribution
     * @param location the location parameter of this distribution
     * @param seed the seed <b>already used</b> to initialize the Pseudo-Random Number Generator
     * @param rng the actual Pseudo-Random Number Generator that will be the base
*                 to generate random numbers following a continuous distribution.
     */
    public ParetoDistr(final double shape, final double location, final long seed, final RandomGenerator rng) {
        super(rng, location, shape);
        if(seed < 0){
            throw new IllegalArgumentException("Seed cannot be negative");
        }
        this.seed = seed;
    }

    @Override
    public void reseedRandomGenerator(final long seed) {
        super.reseedRandomGenerator(seed);
        this.seed = seed;
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
    public ParetoDistr setApplyAntitheticVariates(final boolean applyAntitheticVariates) {
        this.applyAntitheticVariates = applyAntitheticVariates;
        return this;
    }

    @Override
    public double originalSample() {
        return super.sample();
    }
}
