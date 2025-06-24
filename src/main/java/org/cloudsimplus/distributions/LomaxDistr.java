/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.distributions;

import org.apache.commons.math3.random.RandomGenerator;
import org.cloudsimplus.util.MathUtil;

import java.io.Serial;

/**
 * A Pseudo-Random Number Generator following the
 * <a href="https://en.wikipedia.org/wiki/Lomax_distribution">Lomax distribution</a>.
 *
 * @author Marcos Dias de Assuncao
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class LomaxDistr extends ParetoDistr implements ContinuousDistribution {
    @Serial
    private static final long serialVersionUID = 8444069359429898420L;

    private long seed;

    /**
     * The shift parameter of this distribution
     */
    private final double shift;

    /**
     * Creates a Lomax Pseudo-Random Number Generator (PRNG) using the current time as seed.
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
     * Creates a Lomax Pseudo-Random Number Generator (PRNG).
     *
     * @param shape the shape parameter of this distribution
     * @param location the location parameter of this distribution
     * @param shift the shift parameter of this distribution
     * @param seed the seed to initialize the generator
     *
     * @see #LomaxDistr(double, double, double, long, RandomGenerator)
     */
    public LomaxDistr(final double shape, final double location, final double shift, final long seed) {
        this(shape, location, shift, seed, StatisticalDistribution.newDefaultGen(seed));
    }

    /**
     * Creates a Lomax Pseudo-Random Number Generator (PRNG).
     *
     * @param shape the shape parameter of this distribution
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

        this.seed = MathUtil.nonNegative(seed, "Seed");
        this.shift = shift;
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
