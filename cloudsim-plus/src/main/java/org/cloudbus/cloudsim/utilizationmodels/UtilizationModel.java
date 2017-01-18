/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.utilizationmodels;

import org.cloudbus.cloudsim.core.Simulation;

/**
 * The UtilizationModel interface needs to be implemented in order to provide a
 * fine-grained control over resource usage by a Cloudlet.
 * It also implements the Null Object Design
 * Pattern in order to start avoiding {@link NullPointerException}
 * when using the {@link UtilizationModel#NULL} object instead
 * of attributing {@code null} to {@link UtilizationModel} variables.
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public interface UtilizationModel {
    /**
     * Gets the simulation that this UtilizationModel belongs to.
     * @return
     */
    Simulation getSimulation();

    /**
     * Sets the simulation that this UtilizationModel belongs to.
     * @param simulation the Simulation instance to set
     * @return
     */
    UtilizationModel setSimulation(Simulation simulation);

    /**
     * Gets the utilization percentage of a given resource (in scale from [0 to 1]).
     *
     * @param time the time to get the resource usage.
     * @return utilization percentage, from [0 to 1]
     */
    double getUtilization(double time);

    /**
     * Gets the utilization percentage of a given resource (in scale from [0 to 1])
     * at the current simulation time.
     *
     * @return utilization percentage, from [0 to 1]
     */
    double getUtilization();

    /**
     * A property that implements the Null Object Design Pattern for {@link UtilizationModel}
     * objects using a Lambda Expression.
     */
    UtilizationModel NULL = new UtilizationModel() {
        @Override public Simulation getSimulation() { return Simulation.NULL; }
        @Override public UtilizationModel setSimulation(Simulation simulation) { return this; }
        @Override public double getUtilization(double time) { return 0; }
        @Override public double getUtilization() { return 0; }
    };
}
