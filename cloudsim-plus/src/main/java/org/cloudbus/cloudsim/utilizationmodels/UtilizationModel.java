/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.utilizationmodels;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * The UtilizationModel interface needs to be implemented in order to provide a
 * fine-grained control over resource usage by a Cloudlet.
 * It also implements the Null Object Design
 * Pattern in order to start avoiding {@link NullPointerException}
 * when using the {@link UtilizationModel#NULL} object instead
 * of attributing {@code null} to {@link UtilizationModel} variables.
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 2.0
 */
public interface UtilizationModel {
    /**
     * Defines the unit of the resource utilization.
     */
    enum Unit {
        /**
         * Indicate that the resource utilization is defined in percentage values
         * in scale from 0 to 1 (where 1 is 100%).
         */
        PERCENTAGE,
        /**
         * Indicate that the resource utilization is defined in absolute values.
         */
        ABSOLUTE
    }


    /**
     * An attribute that implements the Null Object Design Pattern for {@link UtilizationModel}
     * objects using a Lambda Expression.
     */
    UtilizationModel NULL = new UtilizationModelNull();

    /**
     * Gets the simulation that this UtilizationModel belongs to.
     * @return
     */
    Simulation getSimulation();

    /**
     * Gets the {@link Unit} in which the resource utilization is defined.
     * @return
     */
    Unit getUnit();

    /**
     * Sets the simulation that this UtilizationModel belongs to.
     * @param simulation the Simulation instance to set
     * @return
     */
    UtilizationModel setSimulation(Simulation simulation);

    /**
     * Gets the <b>expected</b> utilization of resource at a given simulation time.
     * Such a value can be a percentage in scale from [0 to 1] or an absolute value,
     * depending on the {@link #getUnit()}.
     *
     * <p><b>It is an expected usage value because the actual {@link Cloudlet} resource usage
     * depends on the available {@link Vm} resource.</b></p>
     *
     * @param time the time to get the resource usage.
     * @return the resource utilization at the given time
     * @see #getUnit()
     */
    double getUtilization(double time);

    /**
     * Gets the <b>expected</b> utilization of resource at the current simulation time.
     * Such a value can be a percentage in scale from [0 to 1] or an absolute value,
     * depending on the {@link #getUnit()}.
     *
     * <p><b>It is an expected usage value because the actual {@link Cloudlet} resource usage
     * depends on the available {@link Vm} resource.</b></p>
     *
     * @return the current resource utilization
     * @see #getUnit()
     */
    double getUtilization();

}
