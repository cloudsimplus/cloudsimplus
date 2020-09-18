/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.distributions;

import org.apache.commons.math3.distribution.RealDistribution;

/**
 * Interface to be implemented by a Pseudo-Random Number Generator (PRNG)
 * that follows a defined statistical continuous distribution.
 *
 * @author Marcos Dias de Assuncao
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 1.0
 */
public interface ContinuousDistribution extends StatisticalDistribution, RealDistribution {
    /**
     * An attribute that implements the Null Object Design Pattern for {@link ContinuousDistribution}
     * objects.
     */
    ContinuousDistribution NULL = new ContinuousDistributionNull();

    @Override
    default double sample() {
        return StatisticalDistribution.super.sample();
    }
}
