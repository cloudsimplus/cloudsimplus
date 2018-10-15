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
	 *
	 * @param seed the seed to be used.
	 * @param mean the mean for the distribution.
	 * @param standardDeviation the standard deviation for the distribution.
	 */
	public NormalDistr(final long seed, final double mean, final double standardDeviation) {
		super(new NormalDistribution(mean, standardDeviation), seed);
	}

	/**
	 * Creates a new normal (Gaussian) pseudo random number generator.
	 *
	 * @param mean the mean for the distribution.
	 * @param standardDeviation the standard deviation for the distribution.
	 */
	public NormalDistr(double mean, double standardDeviation) {
		this(-1, mean, standardDeviation);
	}

}
