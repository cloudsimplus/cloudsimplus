package org.cloudbus.cloudsim.distributions;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * A Pseudo-Random Number Generator following the
 * <a href="https://en.wikipedia.org/wiki/Normal_distribution">Normal (Gaussian) distribution</a>.
 *
 * @author Manoel Campos da Silva Filho
 */
public class NormalDistr extends NormalDistribution implements ContinuousDistribution{
    /** @see #isApplyAntitheticVariates() */
    private boolean applyAntitheticVariates;
    private long seed;

    /**
     * Creates a Normal (Gaussian) Pseudo-Random Number Generator (RNG) using the current time as seed.
     *
     * <p>Internally, it relies on the {@link JDKRandomGenerator},
     * a wrapper for the {@link java.util.Random} class
     * that doesn't have high-quality randomness properties
     * but is very fast.</p>
     *
     * @param mean the mean for the distribution.
     * @param standardDeviation the standard deviation for the distribution.
     *
     * @see #NormalDistr(double, double, long, RandomGenerator)
     */
    public NormalDistr(final double mean, final double standardDeviation) {
        this(mean, standardDeviation, StatisticalDistribution.defaultSeed());
    }

    /**
     * Creates a Normal (Gaussian) Pseudo-Random Number Generator (RNG).
     *
     * <p>Internally, it relies on the {@link JDKRandomGenerator},
     * a wrapper for the {@link java.util.Random} class
     * that doesn't have high-quality randomness properties
     * but is very fast.</p>
     *
	 * @param mean the mean for the distribution.
     * @param standardDeviation the standard deviation for the distribution.
     * @param seed the seed to be used.
     *
     * @see #NormalDistr(double, double, long, RandomGenerator)
     */
	public NormalDistr(final double mean, final double standardDeviation, final long seed) {
		this(mean, standardDeviation, seed, StatisticalDistribution.newDefaultGen(seed));
	}

    /**
     * Creates a Normal (Gaussian) Pseudo-Random Number Generator (RNG).
     * @param mean the mean for the distribution.
     * @param standardDeviation the standard deviation for the distribution.
     * @param seed the seed <b>already used</b> to initialize the Pseudo-Random Number Generator
     * @param rng the actual Pseudo-Random Number Generator that will be the base
*                 to generate random numbers following a continuous distribution.
     */
    public NormalDistr(final double mean, final double standardDeviation, final long seed, final RandomGenerator rng) {
        super(rng, mean, standardDeviation);
        if(seed < 0){
            throw new IllegalArgumentException("Seed cannot be negative");
        }
        this.seed = seed;
    }

    @Override
    public long getSeed() {
        return seed;
    }

    @Override
    public void reseedRandomGenerator(final long seed) {
        super.reseedRandomGenerator(seed);
        this.seed = seed;
    }

    @Override
    public boolean isApplyAntitheticVariates() {
        return applyAntitheticVariates;
    }

    @Override
    public NormalDistr setApplyAntitheticVariates(final boolean applyAntitheticVariates) {
        this.applyAntitheticVariates = applyAntitheticVariates;
        return this;
    }

    @Override
    public double originalSample() {
        return super.sample();
    }
}
