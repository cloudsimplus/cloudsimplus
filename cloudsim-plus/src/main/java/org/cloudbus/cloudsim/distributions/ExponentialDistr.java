/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.distributions;

import org.apache.commons.math3.distribution.ExponentialDistribution;

/**
 * A pseudo random number generator following the
 * <a href="https://en.wikipedia.org/wiki/Exponential_distribution">Exponential
 * distribution</a>.
 *
 * @author Marcos Dias de Assuncao
 * @since CloudSim Toolkit 1.0
 */
public class ExponentialDistr extends ContinuousDistributionAbstract {
    /**
     * Creates a new exponential pseudo random number generator.
     *
     * @param seed the seed to be used.
     * @param mean the mean for the distribution.
     */
    public ExponentialDistr(long seed, double mean) {
        super(new ExponentialDistribution(mean), seed);
    }

    /**
     * Creates a new exponential pseudo random number generator.
     *
     * @param mean the mean for the distribution.
     */
    public ExponentialDistr(double mean) {
        this(-1, mean);
    }
}
