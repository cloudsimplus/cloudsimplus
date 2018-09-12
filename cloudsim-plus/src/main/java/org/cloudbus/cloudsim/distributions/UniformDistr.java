/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.distributions;

import org.apache.commons.math3.distribution.UniformRealDistribution;

import java.util.Random;

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
     * @see #isApplyAntitheticVariates()
     */
    private boolean applyAntitheticVariates;

    /**
     * Creates new uniform pseudo random number generator
     * that generates values between [0 and 1[ using the current
     * time as seed.
     *
     */
    public UniformDistr() {
        this(0, 1);
    }

    /**
     * Creates new uniform pseudo random number generator
     * that generates values between [0 and 1[ using a given seed.
     *
     * @param seed simulation seed to be used
     */
    public UniformDistr(long seed) {
        this(0, 1, seed);
    }

    /**
     * Creates new uniform pseudo random number generator
     * that produces values between a 0 (inclusive) and max (exclusive).
     *
     * @param max maximum value (exclusive)
     */
    public UniformDistr(double max) {
        this(0, max, -1);
    }

    /**
     * Creates new uniform pseudo random number generator
     * that produces values between a min (inclusive) and max (exclusive).
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
        applyAntitheticVariates = false;
    }

    @Override
    public double sample() {
        return (applyAntitheticVariates ? 1 - super.sample() : super.sample());
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

        return rd.nextDouble() * (max - min) + min;
    }

    /**
     * Indicates if the pseudo random number generator (PRNG) has to apply the
     * <a href="https://en.wikipedia.org/wiki/Antithetic_variates">Antithetic Variates Technique</a> in order to reduce variance
     * of experiments using this PRNG.
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
     * @return true if the technique has to be applied, false otherwise
     */
    public boolean isApplyAntitheticVariates() {
        return applyAntitheticVariates;
    }

    /**
     * Defines if the pseudo random number generator (PRNG) has to apply the
     * <a href="https://en.wikipedia.org/wiki/Antithetic_variates">Antithetic Variates Technique</a> in order to reduce variance
     * of experiments using this PRNG.
     *
     * @param applyAntitheticVariates true if the technique has to be applied, false otherwise
     * @see #isApplyAntitheticVariates()
     */
    public UniformDistr setApplyAntitheticVariates(boolean applyAntitheticVariates) {
        this.applyAntitheticVariates = applyAntitheticVariates;
	    return this;
    }

}
