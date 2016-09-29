/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.distributions;

import java.util.Random;

import org.apache.commons.math3.distribution.UniformRealDistribution;

/**
 * A pseudo random number generator following the
 * <a href="https://en.wikipedia.org/wiki/Uniform_distribution_(continuous)">
 * Uniform continuous distribution</a>.
 *
 * @author Marcos Dias de Assuncao
 * @since CloudSim Toolkit 1.0
 */
public class UniformDistr extends ContinuousDistributionAbstract {
    /**
     * @see #isApplyAntitheticVariatesTechnique()
     */
    private boolean applyAntitheticVariatesTechnique;

    /**
     * Creates new uniform pseudo random number generator.
     *
     * @param min minimum value (inclusive)
     * @param max maximum value (exclusive)
     */
    public UniformDistr(double min, double max) {
        this(min, max, -1);
    }

    /**
     * Creates new uniform pseudo random number generator.
     *
     * @param min minimum value (inclusive)
     * @param max maximum value (exclusive)
     * @param seed simulation seed to be used
     */
    public UniformDistr(double min, double max, long seed) {
        super(new UniformRealDistribution(min, max), seed);
        applyAntitheticVariatesTechnique = false;
    }

    /**
     * Generates a new pseudo random number based on the generator and values
     * provided as parameters.
     *
     * @param rd the random number generator
     * @param min the minimum value
     * @param max the maximum value
     * @return the next random number in the sequence
     */
    public static double sample(Random rd, double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("Maximum must be greater than the minimum.");
        }

        return (rd.nextDouble() * (max - min)) + min;
    }

    /**
     * Indicates if the pseudo random number generator (PRNG) has to apply the
     * "Antithetic Variates Technique" in order to reduce variance
     * of experiments using this PRNG.
     *
     * This technique doesn't work for all the cases. However,
     * in the cases it can be applied, in order to it work one have to
     * perform some actions. Consider an experiment that has to run "n" times.
     * The first half of these experiments has to use the seeds the developer
     * want. However, the second half of the experiments have to
     * set the applyAntitheticVariatesTechnique attribute to true
     * and use the seeds of the first half of experiments.
     *
     * Thus, the first half of experiments are run using PRNGs that return
     * random numbers as U(0, 1)[seed_1], ..., U(0, 1)[seed_n].
     * The second half of experiments then uses the seeds of the first
     * half of experiments, returning random numbers as
     * 1 - U(0, 1)[seed_1], ..., 1 - U(0, 1)[seed_n].
     *
     * @return true if the technique has to be applied, false otherwise
     * @see <a href="https://en.wikipedia.org/wiki/Antithetic_variates">Antithetic variates</a>
     */
    public boolean isApplyAntitheticVariatesTechnique() {
        return applyAntitheticVariatesTechnique;
    }

    /**
     * Defines if the pseudo random number generator (PRNG) has to apply the
     * "Antithetic Variates Technique" in order to reduce variance
     * of experiments using this PRNG.
     *
     * @param applyAntitheticVariatesTechnique true if the technique has to be applied, false otherwise
     * @see #isApplyAntitheticVariatesTechnique()
     */
    public UniformDistr setApplyAntitheticVariatesTechnique(boolean applyAntitheticVariatesTechnique) {
        this.applyAntitheticVariatesTechnique = applyAntitheticVariatesTechnique;
	    return this;
    }

    @Override
    public double sample() {
        return (applyAntitheticVariatesTechnique ? 1 - super.sample() : super.sample());
    }

}
