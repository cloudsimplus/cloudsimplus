/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.distributions;

import org.apache.commons.math3.distribution.LogNormalDistribution;

/**
 * A pseudo random number generator following the
 * <a href="https://en.wikipedia.org/wiki/Log-normal_distribution">Lognormal</a>
 * distribution.
 *
 * @author Marcos Dias de Assuncao
 * @since CloudSim Toolkit 1.0
 */
public class LognormalDistr extends ContinuousDistributionAbstract {

    /**
     * Instantiates a new Log-normal pseudo random number generator.
     *
     * @param seed the seed
     * @param shape the shape
     * @param scale the scale
     */
    public LognormalDistr(long seed, double shape, double scale) {
        super(new LogNormalDistribution(scale, shape), seed);
    }

    /**
     * Instantiates a new Log-normal pseudo random number generator.
     *
     * @param shape the shape
     * @param scale the scale
     */
    public LognormalDistr(double shape, double scale) {
        this(-1, shape, scale);
    }

}
