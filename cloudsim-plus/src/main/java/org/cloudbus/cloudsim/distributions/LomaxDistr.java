/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.distributions;

import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * A Pseudo-Random Number Generator following the
 * <a href="https://en.wikipedia.org/wiki/Lomax_distribution">
 * Lomax distribution</a>.
 *
 * @author Marcos Dias de Assuncao
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class LomaxDistr extends ParetoDistr implements ContinuousDistribution {
    private long seed;

    /**
     * The shift parameter of this distribution
     */
    private final double shift;

    /**
     * Creates a lomax Pseudo-Random Number Generator (RNG) using the current time as seed.
     *
     * <p>Internally, it relies on the {@link JDKRandomGenerator},
     * a wrapper for the {@link java.util.Random} class
     * that doesn't have high-quality randomness properties
     * but is very fast.</p>
     *
     * @param shape the shape parameter of this distribution
     * @param location the location parameter of this distribution
     * @param shift the shift parameter of this distribution
     *
     * @see #LomaxDistr(double, double, double, long, RandomGenerator)
     */
    public LomaxDistr(final double shape, final double location, final double shift) {
        this(shape, location, shift, StatisticalDistribution.defaultSeed());
    }

    /**
     * Creates a lomax Pseudo-Random Number Generator (RNG).
     *
     * <p>Internally, it relies on the {@link JDKRandomGenerator},
     * a wrapper for the {@link java.util.Random} class
     * that doesn't have high-quality randomness properties
     * but is very fast.</p>
     *
     * @param shape the shape parameter of this distribution
     * @param location the location parameter of this distribution
     * @param shift the shift parameter of this distribution
     * @param seed the seed
     *
     * @see #LomaxDistr(double, double, double, long, RandomGenerator)
     */
    public LomaxDistr(final double shape, final double location, final double shift, final long seed) {
        this(shape, location, shift, seed, StatisticalDistribution.newDefaultGen(seed));
    }

    /**
     * Creates a lomax Pseudo-Random Number Generator (RNG).
     *
     * <p>Internally, it relies on the {@link JDKRandomGenerator},
     * a wrapper for the {@link java.util.Random} class
     * that doesn't have high-quality randomness properties
     * but is very fast.</p>
     *  @param shape the shape parameter of this distribution
     * @param location the location parameter of this distribution
     * @param shift the shift parameter of this distribution
     * @param seed the seed <b>already used</b> to initialize the Pseudo-Random Number Generator
     * @param rng the actual Pseudo-Random Number Generator that will be the base
*                 to generate random numbers following a continuous distribution.
     */
    public LomaxDistr(final double shape, final double location, final double shift, final long seed, final RandomGenerator rng) {
        super(shape, location, seed, rng);
        if (shift > location) {
            throw new IllegalArgumentException("Shift must be smaller or equal than location");
        }
        if(seed < 0){
            throw new IllegalArgumentException("Seed cannot be negative");
        }

        this.shift = shift;
        this.seed = seed;
    }

    @Override
    public double sample() {
        return super.sample() - shift;
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

}
