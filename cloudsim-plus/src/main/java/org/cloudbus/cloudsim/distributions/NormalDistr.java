package org.cloudbus.cloudsim.distributions;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * A pseudo random number generator following the
 * <a href="https://en.wikipedia.org/wiki/Normal_distribution">Normal (Gaussian) distribution</a>.
 *
 * @author Manoel Campos da Silva Filho
 */
public class NormalDistr extends ContinuousDistributionAbstract {
	/**
	 * Creates a new normal (Gaussian) pseudo random number generator.
	 * @param mean the mean for the distribution.
	 * @param standardDeviation the standard deviation for the distribution.
     * @param seed the seed to be used.
     */
	public NormalDistr(final double mean, final double standardDeviation, final long seed) {
		super(new NormalDistribution(mean, standardDeviation), seed);
	}

	/**
	 * Creates a new normal (Gaussian) pseudo random number generator.
	 *
	 * @param mean the mean for the distribution.
	 * @param standardDeviation the standard deviation for the distribution.
	 */
	public NormalDistr(final double mean, final double standardDeviation) {
		this(mean, standardDeviation, -1);
	}

}
