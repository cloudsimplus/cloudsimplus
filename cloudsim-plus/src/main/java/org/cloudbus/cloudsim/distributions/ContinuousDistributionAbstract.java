package org.cloudbus.cloudsim.distributions;

import org.apache.commons.math3.distribution.RealDistribution;

/**
 * An base class for implementation of {@link ContinuousDistribution}s.
 *
 * @author Manoel Campos da Silva Filho
 */
public abstract class ContinuousDistributionAbstract implements ContinuousDistribution {
    /**
     * @see #getSeed()
     */
    private long seed;

    /**
     * The actual random number generator that will be the base
     * to generate random numbers following a continuous distribution.
     */
    private final RealDistribution numGen;

    /**
     * Creates a new continuous random number generator
     * using the current time as seed.
     *
     * @param numGen the actual random number generator that will be the base
     * to generate random numbers following a continuous distribution.
     */
    protected ContinuousDistributionAbstract(RealDistribution numGen) {
        this(numGen, -1);
    }

    /**
     * Creates a new continuous random number generator.
     *
     * @param numGen the actual random number generator that will be the base
     * to generate random numbers following a continuous distribution.
     * @param seed the seed to initialize the random number generator. If
     * it is passed -1, the current time will be used
     */
    protected ContinuousDistributionAbstract(RealDistribution numGen, long seed) {
        this.numGen = numGen;
        if(seed == -1)
            seed = System.currentTimeMillis();
        setSeed(seed);
    }

    @Override
    public final long getSeed() {
        return seed;
    }

    protected final void setSeed(long seed){
        this.seed = seed;
        numGen.reseedRandomGenerator(seed);
    }

    @Override
    public double sample() {
        return numGen.sample();
    }
}
