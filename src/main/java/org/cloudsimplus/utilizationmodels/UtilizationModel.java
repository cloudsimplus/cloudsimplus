/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.utilizationmodels;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.core.Simulation;
import org.cloudsimplus.schedulers.cloudlet.CloudletScheduler;
import org.cloudsimplus.vms.Vm;

/**
 * An interface to be implemented to provide a
 * fine-grained control over resource usage by a {@link Cloudlet}.
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 2.0
 */
public sealed interface UtilizationModel permits UtilizationModelAbstract, UtilizationModelNull {
    /**
     * Defines the unit of the resource utilization.
     * @see #getUtilization(double)
     * @see #getUtilization()
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
     * An attribute that implements the Null Object Design Pattern for {@link UtilizationModel} objects.
     * A {@link Cloudlet} using such a utilization model
     * for one of its resources will not consume any amount of that resource ever.
     */
    UtilizationModel NULL = new UtilizationModelNull();

    /**
     * @return the simulation that this UtilizationModel belongs to.
     */
    Simulation getSimulation();

    /**
     * @return the {@link Unit} in which the resource utilization is defined.
     */
    Unit getUnit();

    /**
     * Sets the simulation that this UtilizationModel belongs to.
     *
     * @param simulation the Simulation instance to set
     */
    UtilizationModel setSimulation(Simulation simulation);

    /// Gets the **expected** utilization of resource at a given simulation time.
    /// Such a value can be a percentage in scale from 0..1 or an absolute value,
    /// depending on the [#getUnit()].
    ///
    /// **It is an expected usage value because the actual [Cloudlet] resource usage
    /// depends on the available [Vm] resource.**
    ///
    /// @param time the time to get the resource usage.
    /// @return the resource utilization at the given time
    /// @see #getUnit()
    double getUtilization(double time);

    /// Gets the **expected** utilization of resource at the current simulation time.
    /// Such a value can be a percentage in scale from 0..1 or an absolute value,
    /// depending on the [#getUnit()].
    ///
    /// **It is an expected usage value because the actual [Cloudlet] resource usage
    /// depends on the available [Vm] resource.**
    ///
    /// @return the current resource utilization
    /// @see #getUnit()
    double getUtilization();

    /// Checks if the resource utilization requested by a Cloudlet is allowed to exceed 100% or not.
    ///
    /// **WARNING:** This attribute is just considered when the [#getUnit()]
    /// is defined as [Unit#PERCENTAGE].
    ///
    /// @return true if Cloudlets can request more than 100% of a resource, false otherwise
    /// @see #setOverCapacityRequestAllowed(boolean)
    boolean isOverCapacityRequestAllowed();

    /// Allow the resource utilization requested by a Cloudlet to exceed 100% or not.
    ///
    /// The VM's [CloudletScheduler] won't allocate more resources than there is available,
    /// showing a warning if such a request is received.
    /// While requesting more than 100% of a resource may be useful to try simulating
    /// an overloading scenario, in other ones it may not be desired.
    /// You may want your Cloudlets to request at maximum 100% of a given resource.
    /// In such a case, you can disable this attribute and the [#getUtilization(double)]
    /// method will only return values strictly between the closed range [0..1].
    /// If a value greater than 1 is generated, it's returned 1.
    ///
    /// For specific implementations such as
    /// the [UtilizationModelPlanetLab] (which reads data from a trace file that
    /// may be manipulated) and [UtilizationModelStochastic]
    /// (which generates utilization values randomly),
    /// the model may return values greater than 1 (100%).
    /// In such cases, you may consider disabling this attribute
    /// if you don't want such behavior.
    ///
    /// **WARNING:** This attribute is just considered when the [#getUnit()]
    /// is defined as [Unit#PERCENTAGE].
    ///
    /// @param allow true to allow requesting more than 100% of a resource, false to disallow that
    UtilizationModel setOverCapacityRequestAllowed(boolean allow);
}
