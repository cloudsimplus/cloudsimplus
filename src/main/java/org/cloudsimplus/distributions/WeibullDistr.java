/*
 * Title:        CloudSim Toolkit
 * Description:  Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.distributions;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.statistics.distribution.WeibullDistribution;

/// A Pseudo-Random Number Generator following the
/// [Weibull distribution](https://en.wikipedia.org/wiki/Weibull_distribution).
///
/// @author Marcos Dias de Assuncao
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 1.0
public class WeibullDistr extends ContinuousDistributionAbstract {
    /// Creates a Weibull Pseudo-Random Number Generator (PRNG) using a given seed.
    ///
    /// Internally, it uses the [default PRNG][StatisticalDistribution#newDefaultGen(long)].
    ///
    /// @param alpha the alpha distribution parameter
    /// @param beta the beta distribution parameter
    ///
    /// @see #WeibullDistr(double, double, long, UniformRandomProvider)
    public WeibullDistr(final double alpha, final double beta) {
        this(alpha, beta, StatisticalDistribution.defaultSeed());
    }

    /// Creates a Weibull Pseudo-Random Number Generator (PRNG).
    ///
    /// Internally, it uses the [default PRNG][StatisticalDistribution#newDefaultGen(long)].
    ///
    /// @param alpha the alpha distribution parameter
    /// @param beta the beta distribution parameter
    /// @param seed the seed
    ///
    /// @see #WeibullDistr(double, double, long, UniformRandomProvider)
    public WeibullDistr(final double alpha, final double beta, final long seed) {
        this(alpha, beta, seed, StatisticalDistribution.newDefaultGen(seed));
    }

    /// Creates a Weibull Pseudo-Random Number Generator (PRNG).
    /// @param alpha the alpha distribution parameter
    /// @param beta the beta distribution parameter
    /// @param seed the seed **already used** to initialize the Pseudo-Random Number Generator
    /// @param rng the actual Pseudo-Random Number Generator that will be the base
    /// to generate random numbers following a continuous distribution.
    public WeibullDistr(final double alpha, final double beta, final long seed, final UniformRandomProvider rng) {
        super(WeibullDistribution.of(alpha, beta), seed, rng);
    }
}
