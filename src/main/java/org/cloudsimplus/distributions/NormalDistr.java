package org.cloudsimplus.distributions;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.statistics.distribution.NormalDistribution;

/**
 * A Pseudo-Random Number Generator following the
 * <a href="https://en.wikipedia.org/wiki/Normal_distribution">Normal (Gaussian) distribution</a>.
 *
 * @author Manoel Campos da Silva Filho
 */
public class NormalDistr extends ContinuousDistributionAbstract{
    /**
     * Creates a Normal (Gaussian) Pseudo-Random Number Generator (PRNG) using the current time as seed.
     *
     * @param mean the mean for the distribution.
     * @param standardDeviation the standard deviation for the distribution.
     *
     * @see #NormalDistr(double, double, long, UniformRandomProvider)
     */
    public NormalDistr(final double mean, final double standardDeviation) {
        this(mean, standardDeviation, StatisticalDistribution.defaultSeed());
    }

    /**
     * Creates a Normal (Gaussian) Pseudo-Random Number Generator (PRNG).
     *
	 * @param mean the mean for the distribution.
     * @param standardDeviation the standard deviation for the distribution.
     * @param seed the seed to initialize the generator
     *
     * @see #NormalDistr(double, double, long, UniformRandomProvider)
     */
	public NormalDistr(final double mean, final double standardDeviation, final long seed) {
		this(mean, standardDeviation, seed, StatisticalDistribution.newDefaultGen(seed));
	}

    /**
     * Creates a Normal (Gaussian) Pseudo-Random Number Generator (PRNG).
     *
     * @param mean the mean for the distribution.
     * @param standardDeviation the standard deviation for the distribution.
     * @param seed the seed <b>already used</b> to initialize the Pseudo-Random Number Generator
     * @param rng the actual Pseudo-Random Number Generator that will be the base
     *            to generate random numbers following a continuous distribution.
     */
    public NormalDistr(final double mean, final double standardDeviation, final long seed, final UniformRandomProvider rng) {
        super(NormalDistribution.of(mean, standardDeviation), seed, rng);
    }
}
