/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.distributions;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

/// Interface to be implemented by a Pseudo-Random Number Generator (PRNG)
/// that follows some statistical distribution, even discrete or continuous.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 5.5.1
public sealed interface StatisticalDistribution permits DiscreteDistribution, ContinuousDistribution {
    /// Generate a new pseudo random number
    /// directly from the underlying distribution.
    /// This way, the [Antithetic Variates Technique][#isApplyAntitheticVariates()]
    /// is ignored if enabled.
    ///
    /// Usually you shouldn't call this method but [#sample()] instead.
    ///
    /// @return the next pseudo random number in the sequence, following the
    /// implemented distribution, ignoring the
    /// [Antithetic Variates Technique][#isApplyAntitheticVariates()]
    /// if enabled
    double originalSample();

    /// Generate a new pseudo random number.
    /// If the [Antithetic Variates Technique][#isApplyAntitheticVariates()] is enabled,
    /// the returned value is manipulated to try reducing variance of generated random numbers.
    /// Check link above for details.
    ///
    /// @return the next pseudo random number in the sequence, following the implemented distribution.
    default double sample() {
        return isApplyAntitheticVariates() ? 1 - originalSample() : originalSample();
    }

    /// @return the seed used to initialize the generator
    long getSeed();

    /// Instantiates a [WELL19937c](https://en.wikipedia.org/wiki/Well_equidistributed_long-period_linear)
    /// as the default [Pseudo-Random Number Generator][UniformRandomProvider]
    /// (PRNG) used by implementing classes.
    /// The PRNG is provided by the [Apache Commons RNG](https://commons.apache.org/proper/commons-rng/) library.
    ///
    /// [ContinuousDistribution] implementations rely on the
    /// [Apache Commons Statistics](https://commons.apache.org/proper/commons-statistics/)
    /// library to generate random numbers following some statistical distribution.
    /// Such a library uses a [UniformRandomProvider] as the source of randomness.
    ///
    /// Providing a [UniformRandomProvider] when instantiating a [ContinuousDistribution]
    /// allows the researcher to define any PRNG by calling the appropriate constructor.
    /// For instance, the [UniformDistr#UniformDistr(long,UniformRandomProvider)]
    /// constructor enables providing a different PRNG, while
    /// the [UniformDistr#UniformDistr(long)] uses the PRNG instantiated here.
    /// Any PRNG from [RandomSource] can be used, such as
    /// `RandomSource.XO_RO_SHI_RO_128_PP.create(seed)`.
    ///
    /// By calling a constructor that accepts a [UniformRandomProvider],
    /// the researcher may provide a different PRNG with either higher performance
    /// or better statistical properties
    /// (it's challenging to have both properties on the same PRNG).
    ///
    /// @param seed the seed to initialize the PRNG
    /// @return the new PRNG
    static UniformRandomProvider newDefaultGen(final long seed){
        return RandomSource.WELL_19937_C.create(seed);
    }

    static long defaultSeed(){
        return System.nanoTime();
    }

    /// Indicates if the Pseudo-Random Number Generator (RNG) applies the
    /// [Antithetic Variates Technique](https://en.wikipedia.org/wiki/Antithetic_variates)
    /// to reduce variance of experiments using the generated numbers.
    ///
    /// This technique doesn't work for all the cases. However,
    /// in the cases it can be applied, in order for it to work, you have to
    /// perform some actions. Consider an experiment that has to run "n" times.
    /// The first half of these experiments has to use the seeds you want.
    /// However, the second half of the experiments have to
    /// set the `applyAntitheticVariates` attribute to `true`
    /// and use the seeds of the first half of experiments.
    ///
    /// Thus, the first half of experiments are run using PRNGs, returning
    /// random numbers as U(0, 1)\[seed_1\], ..., U(0, 1)\[seed_n\].
    /// The second half of experiments then uses the seeds of the first
    /// half of experiments, returning random numbers as
    /// 1 - U(0, 1)\[seed_1\], ..., 1 - U(0, 1)\[seed_n\].
    ///
    /// @return true if the technique is applied, false otherwise
    /// @see #setApplyAntitheticVariates(boolean)
    boolean isApplyAntitheticVariates();

    /// Indicates if the Pseudo-Random Number Generator (RNG) applies the
    /// [Antithetic Variates Technique](https://en.wikipedia.org/wiki/Antithetic_variates)
    /// to reduce variance of experiments using the generated numbers.
    ///
    /// @param applyAntitheticVariates true if the technique is to be applied, false otherwise
    /// @see #isApplyAntitheticVariates()
    StatisticalDistribution setApplyAntitheticVariates(boolean applyAntitheticVariates);
}
