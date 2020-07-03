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
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
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
         * in scale from 0..1 (where 1 is 100%).
         */
        PERCENTAGE,
        /**
         * Indicate that the resource utilization is defined in absolute values.
         */
        ABSOLUTE
    }


    /**
     * An attribute that implements the Null Object Design Pattern for {@link UtilizationModel}
     * objects using a Lambda Expression. A {@link Cloudlet} using such a utilization model for one of its resources
     * will not consume any amount of that resource ever.
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
     * Such a value can be a percentage in scale from 0..1 or an absolute value,
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
     * Such a value can be a percentage in scale from 0..1 or an absolute value,
     * depending on the {@link #getUnit()}.
     *
     * <p><b>It is an expected usage value because the actual {@link Cloudlet} resource usage
     * depends on the available {@link Vm} resource.</b></p>
     *
     * @return the current resource utilization
     * @see #getUnit()
     */
    double getUtilization();

    /**
     * Checks if the resource utilization requested by a Cloudlet is allowed to exceed 100% or not.
     * <p><b>WARNING:</b> This attribute is just considered when the {@link #getUnit()}
     * is defined as {@link Unit#PERCENTAGE}.</p>
     *
     * @return true if Cloudlets can request more than 100% of a resource, false otherwise
     * @see #setOverCapacityRequestAllowed(boolean)
     */
    boolean isOverCapacityRequestAllowed();

    /**
     * Allow the resource utilization requested by a Cloudlet to exceed 100% or not.
     *
     * <p>The VM's {@link CloudletScheduler} won't allocate more resources than there is available,
     * showing a warning if such a request is received.
     * While requesting more than 100% of a resource may be useful to try simulating an overloading scenario,
     * in other ones it may not be desired.
     * You may want your Cloudlets to request the maximum of 100% of a given resource.
     * In such a case, you can disable this attribute and the {@link #getUtilization(double)}
     * method will only return values strictly between the closed range [0..1].
     * If a value greater than 1 is generated, it's returned 1.
     * </p>
     *
     * <p>For specific implementations such as
     * the {@link UtilizationModelPlanetLab} (which reads data from a trace file that may be manipulated)
     * and {@link UtilizationModelStochastic} (which generates utilization values randomly),
     * the model may return values greater than 1 (100%).
     * In such cases, you may consider disabling this attribute
     * if you don't want such a behaviour.</p>
     *
     * <p><b>WARNING:</b> This attribute is just considered when the {@link #getUnit()}
     * is defined as {@link Unit#PERCENTAGE}.</p>
     *
     * @param allow true to allow requesting more than 100% of a resource, false to disallow that
     * @return
     */
    UtilizationModel setOverCapacityRequestAllowed(boolean allow);
}
