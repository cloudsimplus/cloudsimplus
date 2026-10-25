/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.resources;

/// An interface to represent a physical or virtual resource (like RAM, CPU or Bandwidth)
/// that doesn't provide direct features to change the occupied amount of resource.
/// Objects that directly implement this interface are supposed to define
/// the capacity and occupied amount of resource only in their constructors.
///
/// ## Occupied and free amounts
///
/// A resource only does bookkeeping: it records how much of its capacity is occupied (taken)
/// and how much is free, without deciding, reserving, delivering or measuring anything by itself.
/// It doesn't know why an amount was taken: the meaning of the occupied amount comes from the caller.
/// For instance:
///
/// - in a Host resource, such as the Host [Ram] or [Bandwidth], the occupied amount is
///   updated by a [org.cloudsimplus.allocators.ResourceAllocator] and means the amount
///   **allocated** (reserved) for the VMs, even if they aren't using it;
/// - in a VM resource, the occupied amount is updated by the VM
///   [org.cloudsimplus.schedulers.cloudlet.CloudletScheduler] at each processing update
///   and means the **utilization** of the resource by the VM Cloudlets,
///   according to their [org.cloudsimplus.utilizationmodels.UtilizationModel]s.
///
/// See [org.cloudsimplus.allocators.ResourceAllocator] for the meaning of placement,
/// allocation, provisioning and utilization.
///
/// @author Uros Cibej
/// @author Anthony Sulistio
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.0
public interface Resource extends ResourceCapacity {

    /// An attribute that implements the Null Object Design Pattern for [Resource] objects.
    Resource NULL = new ResourceNull();

    /// Checks if this object is an instance of a given class.
    /// @param classWanted the class to verify if the object is an instance of
    /// @return true if the object is an instance of the given class, false otherwise
    default boolean isSubClassOf(final Class classWanted) {
        return classWanted.isAssignableFrom(this.getClass());
    }

    /// {@return the amount of the resource capacity that is free (not occupied)}
    /// The data unit depends on the implementing classes.
    long getFree();

    /// {@return the current total amount of the resource capacity that is occupied}
    /// Its meaning (allocation or utilization) comes from the caller that occupied it (see the class docs).
    /// The data unit depends on the implementing classes, according to the [#getUnit()].
    long getOccupied();

    /// Checks if the capacity required by a given resource is available (free)
    /// at this resource. This method is commonly used to check if there is a specific
    /// amount of resource free at a physical resource (this Resource instance)
    /// that is required by a virtualized resource (the given Resource).
    ///
    /// @param resource the resource to check if its capacity is available at the current resource
    /// @return true if the capacity required by the given Resource is free; false otherwise
    /// @see #isAmountAvailable(long)
    default boolean isAmountAvailable(Resource resource){
        return isAmountAvailable(resource.getCapacity());
    }

    /// Checks if there is a specific amount of resource available (free),
    /// where such an amount is a double value that will be converted to long.
    ///
    /// This method is just a shorthand to avoid explicitly converting a double to long.
    ///
    /// @param amountToCheck the amount of resource to check if is free.
    /// The data unit depends on the implementing classes, according to the [#getUnit()].
    /// @return true if the specified amount is free; false otherwise
    /// @see #isAmountAvailable(long)
    /// @see #getUnit()
    default boolean isAmountAvailable(double amountToCheck){
        return isAmountAvailable((long)amountToCheck);
    }

    /// Checks if there is a specific amount of resource available (free).
    /// @param amountToCheck the amount of resource to check if is free.
    /// The data unit depends on the implementing classes, according to the [#getUnit()].
    /// @return true if the specified amount is free; false otherwise
    /// @see #getUnit()
    boolean isAmountAvailable(long amountToCheck);

    /// @return the measurement unit of this resource.
    String getUnit();

    /// Checks if the resource is full or not.
    ///
    /// @return true if the storage is full, false otherwise
    default boolean isFull() {
        return getFree() <= 0;
    }

    /// {@return the percentage of the resource capacity that is currently occupied (in scale from 0 to 1)}
    /// Its meaning (allocation or utilization) comes from the caller that occupied it (see the class docs).
    default double getPercentOccupied() {
        return getCapacity() > 0 ? getOccupied() / (double)getCapacity() : 0.0;
    }
}
