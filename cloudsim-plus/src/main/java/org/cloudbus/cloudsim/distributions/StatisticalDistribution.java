package org.cloudbus.cloudsim.distributions;

import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

/**
 * Interface to be implemented by a Pseudo-Random Number Generator (PRNG)
 * that follows some statistical distribution, even discrete or continuous.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 5.5.1
 */
public interface StatisticalDistribution {
    /**
     * Generate a new pseudo random number
     * directly from the {@link RealDistribution#sample()} method.
     * This way, the {@link #isApplyAntitheticVariates() Antithetic Variates Technique}
     * is ignored if enabled.
     *
     * <p>Usually you shouldn't call this method but {@link #sample()}
     * instead.</p>
     *
     * @return the next pseudo random number in the sequence, following the
     * implemented distribution, ignoring the {@link #isApplyAntitheticVariates() Antithetic Variates Technique}
     * if enabled
     */
    double originalSample();

    /**
     * Generate a new pseudo random number.
     * If the {@link #isApplyAntitheticVariates() Antithetic Variates Technique} is enabled,
     * the returned value is manipulated to try reducing variance or generated random numbers.
     * Check the provided link for details.
     *
     * @return the next pseudo random number in the sequence, following the
     * implemented distribution.
     */
    default double sample() {
        return isApplyAntitheticVariates() ? 1 - originalSample() : originalSample();
    }

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

    /**
     * Indicates if the Pseudo-Random Number Generator (RNG) applies the
     * <a href="https://en.wikipedia.org/wiki/Antithetic_variates">Antithetic Variates Technique</a> in order to reduce variance
     * of experiments using the generated numbers.
     *
     * This technique doesn't work for all the cases. However,
     * in the cases it can be applied, in order to it work, one have to
     * perform some actions. Consider an experiment that has to run "n" times.
     * The first half of these experiments has to use the seeds the developer
     * want. However, the second half of the experiments have to
     * set the applyAntitheticVariates attribute to true
     * and use the seeds of the first half of experiments.
     *
     * Thus, the first half of experiments are run using PRNGs that return
     * random numbers as U(0, 1)[seed_1], ..., U(0, 1)[seed_n].
     * The second half of experiments then uses the seeds of the first
     * half of experiments, returning random numbers as
     * 1 - U(0, 1)[seed_1], ..., 1 - U(0, 1)[seed_n].
     *
     * @return true if the technique is applied, false otherwise
     * @see #setApplyAntitheticVariates(boolean)
     */
    boolean isApplyAntitheticVariates();

    /**
     * Indicates if the Pseudo-Random Number Generator (RNG) applies the
     * <a href="https://en.wikipedia.org/wiki/Antithetic_variates">Antithetic Variates Technique</a> in order to reduce variance
     * of experiments using the generated numbers.
     *
     * @param applyAntitheticVariates true if the technique is to be applied, false otherwise
     * @see #isApplyAntitheticVariates()
     */
    StatisticalDistribution setApplyAntitheticVariates(boolean applyAntitheticVariates);
}
