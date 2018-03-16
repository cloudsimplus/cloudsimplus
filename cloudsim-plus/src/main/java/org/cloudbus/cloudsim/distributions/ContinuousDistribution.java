/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.distributions;

/**
 * Interface to be implemented by a pseudo random number generator (PRNG)
 * that follows a defined statistical continuous distribution.
 *
 * @author Marcos Dias de Assuncao
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
     * @return the seed used to initialize the generator
     */
    long getSeed();
}
