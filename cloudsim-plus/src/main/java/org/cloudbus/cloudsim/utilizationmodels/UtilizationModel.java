/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.utilizationmodels;

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
     * Gets the utilization percentage of a given resource.
     *
     * @param time the time to get the resource usage.
     * @return utilization percentage, from [0 to 1]
     */
    double getUtilization(double time);

    /**
     * A property that implements the Null Object Design Pattern for {@link UtilizationModel}
     * objects.
     */
    UtilizationModel NULL = new UtilizationModel() {
        @Override public double getUtilization(double time) { return 0.0; }
    };
}
