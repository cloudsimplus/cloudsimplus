/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.distributions;

import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

/**
 * Interface to be implemented by a Pseudo-Random Number Generator (PRNG)
 * that follows a defined statistical continuous distribution.
 *
 * @author Marcos Dias de Assuncao
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public interface ContinuousDistribution {
    /**
     * An attribute that implements the Null Object Design Pattern for {@link ContinuousDistribution}
     * objects.
     */
    ContinuousDistribution NULL = new ContinuousDistributionNull();

    /**
     * Generate a new pseudo random number.
     *
     * @return the next pseudo random number in the sequence, following the
     * implemented distribution.
     */
    double sample();

    /**
     * Gets the seed used to initialize the generator
     * @return
     */
    long getSeed();

    /**
     * Instantiates a {@link Well19937c} as the default
     * {@link RandomGenerator Pseudo-Random Number Generator}
     * (PRNG) used by {@code ContinuousDistribution}.
     *
     * <p>{@link Well19937c} is the PRNG used by {@link RealDistribution}
     * implementations of the {@link org.apache.commons.math3}.
     * Classes in such a library are used internally by
     * {@code ContinuousDistribution} implementations to provide
     * PRNGs following some statistical distributions.
     * </p>
     *
     * <p>
     * Despite the classes from {@link org.apache.commons.math3}
     * use the same {@link RandomGenerator} defined here,
     * providing a {@link RandomGenerator} when instantiate a {@code ContinuousDistribution}
     * allow the researcher to define any PRNG by calling the appropriate
     * {@code ContinuousDistribution} constructor.
     * For instance, the {@link UniformDistr#UniformDistr(long, RandomGenerator)}
     * constructor enables providing a different PRNG, while
     * the {@link UniformDistr#UniformDistr(long)} uses the PRNG instantiated here.
     * </p>
     *
     * <p>By calling a constructor that accepts a {@link RandomGenerator},
     * the researcher may provide a different PRNG with either higher performance
     * or better statistical properties
     * (it's difficult to have both properties on the same PRNG).</p>
     *
     * @param seed the seed to set
     */
    static RandomGenerator newDefaultGen(final long seed){
        return new Well19937c(seed);
    }

    static long defaultSeed(){
        return System.nanoTime();
    }
}
