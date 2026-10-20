/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.distributions;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.statistics.distribution.ExponentialDistribution;

/// A Pseudo-Random Number Generator following the
/// [Exponential distribution](https://en.wikipedia.org/wiki/Exponential_distribution).
///
/// @author Marcos Dias de Assuncao
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 1.0
public class ExponentialDistr extends ContinuousDistributionAbstract {
    /// Creates a exponential Pseudo-Random Number Generator (PRNG).
    ///
    /// @param mean the mean for the distribution.
    /// @see #ExponentialDistr(double, long, UniformRandomProvider)
    public ExponentialDistr(final double mean) {
        this(mean, StatisticalDistribution.defaultSeed());
    }

    /// Creates a exponential Pseudo-Random Number Generator (PRNG).
    ///
    /// @param mean the mean for the distribution.
    /// @param seed the seed to be used.
    /// @see #ExponentialDistr(double, long, UniformRandomProvider)
    public ExponentialDistr(final double mean, final long seed) {
        this(mean, seed, StatisticalDistribution.newDefaultGen(seed));
    }

    /// Creates a exponential Pseudo-Random Number Generator (PRNG).
    /// @param mean the mean for the distribution.
    /// @param seed the seed **already used** to initialize the Pseudo-Random Number Generator
    /// @param rng the actual Pseudo-Random Number Generator that will be the base
    /// to generate random numbers following a continuous distribution.
    public ExponentialDistr(final double mean, final long seed, final UniformRandomProvider rng) {
        super(ExponentialDistribution.of(mean), seed, rng);
    }
}
