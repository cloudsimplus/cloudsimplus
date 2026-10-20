/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.distributions;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.statistics.distribution.GammaDistribution;

/// A Pseudo-Random Number Generator following the
/// [Gamma distribution](https://en.wikipedia.org/wiki/Gamma_distribution).
///
/// @author Marcos Dias de Assuncao
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 1.0
public class GammaDistr extends ContinuousDistributionAbstract {
    /// Creates a Gamma Pseudo-Random Number Generator (PRNG) using the current time as seed.
    ///
    /// Internally, it uses the [default PRNG][StatisticalDistribution#newDefaultGen(long)].
    ///
    /// @param shape the shape parameter of this distribution
    /// @param scale the scale parameter of this distribution
    ///
    /// @see #GammaDistr(int, double, long, UniformRandomProvider)
    public GammaDistr(final int shape, final double scale) {
        this(shape, scale, StatisticalDistribution.defaultSeed());
    }

    /// Creates a Gamma Pseudo-Random Number Generator (PRNG).
    ///
    /// @param shape the shape parameter of this distribution
    /// @param scale the scale parameter of this distribution
    /// @param seed the seed to initialize the generator
    ///
    /// @see #GammaDistr(int, double, long, UniformRandomProvider)
    public GammaDistr(final int shape, final double scale, final long seed) {
        this(shape, scale, seed, StatisticalDistribution.newDefaultGen(seed));
    }

    /// Creates a Gamma Pseudo-Random Number Generator (RNG).
    /// @param shape the shape parameter of this distribution
    /// @param scale the scale parameter of this distribution
    /// @param seed the seed **already used** to initialize the Pseudo-Random Number Generator
    /// @param rng the actual Pseudo-Random Number Generator that will be the base
    /// to generate random numbers following a continuous distribution.
    public GammaDistr(final int shape, final double scale, final long seed, final UniformRandomProvider rng) {
        super(GammaDistribution.of(shape, scale), seed, rng);
    }
}
