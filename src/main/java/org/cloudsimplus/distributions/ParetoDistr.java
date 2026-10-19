/*
 * Title:        CloudSim Toolkit
 * Description:  Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.distributions;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.statistics.distribution.ParetoDistribution;

/**
 * A Pseudo-Random Number Generator following the
 * <a href="https://en.wikipedia.org/wiki/Pareto_distribution">Pareto distribution</a>.
 *
 * @author Marcos Dias de Assuncao
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public class ParetoDistr extends ContinuousDistributionAbstract {
    /**
     * Creates a Pareto Pseudo-Random Number Generator (PRNG) using the current time as seed.
     *
     * @param shape the shape parameter of this distribution
     * @param location the location parameter of this distribution
     *
     * @see #ParetoDistr(double, double, long, UniformRandomProvider)
     */
    public ParetoDistr(final double shape, final double location) {
        this(shape, location, StatisticalDistribution.defaultSeed());
    }

    /**
     * Creates a Pareto Pseudo-Random Number Generator (PRNG).
     *
     * @param shape the shape parameter of this distribution
     * @param location the location parameter of this distribution
     * @param seed the seed to initialize the generator
     * @see #ParetoDistr(double, double, long, UniformRandomProvider)
     */
    public ParetoDistr(final double shape, final double location, final long seed) {
        this(shape, location, seed, StatisticalDistribution.newDefaultGen(seed));
    }

    /**
     * Creates a Pareto Pseudo-Random Number Generator (PRNG).
     *
     * @param shape the shape parameter of this distribution
     * @param location the location parameter of this distribution
     * @param seed the seed <b>already used</b> to initialize the Pseudo-Random Number Generator
     * @param rng the actual Pseudo-Random Number Generator that will be the base
     *            to generate random numbers following a continuous distribution.
     */
    public ParetoDistr(final double shape, final double location, final long seed, final UniformRandomProvider rng) {
        super(ParetoDistribution.of(location, shape), seed, rng);
    }
}
