/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.distributions;

import org.apache.commons.math3.distribution.GammaDistribution;

/**
 * A pseudo random number generator following the
 * <a href="https://en.wikipedia.org/wiki/Gamma_distribution">Gamma</a>
 * distribution.
 *
 * @author Marcos Dias de Assuncao
 * @since CloudSim Toolkit 1.0
 */
public class GammaDistr extends ContinuousDistributionAbstract {

    /**
     * Instantiates a new Gamma pseudo random number generator.
     *
     * @param seed the seed
     * @param shape the shape
     * @param scale the scale
     */
    public GammaDistr(long seed, int shape, double scale) {
        super(new GammaDistribution(shape, scale), seed);
    }

    /**
     * Instantiates a new Gamma pseudo random number generator.
     *
     * @param shape the shape
     * @param scale the scale
     */
    public GammaDistr(int shape, double scale) {
        this(-1, shape, scale);
    }
}
