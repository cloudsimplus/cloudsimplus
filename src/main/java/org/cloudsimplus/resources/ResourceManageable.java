/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.resources;

/// An interface to represent a physical or virtual resource (like RAM, CPU or
/// Bandwidth) with features to manage its capacity and occupied amount.
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
public interface ResourceManageable extends Resource {

    /// An attribute that implements the Null Object Design Pattern for [ResourceManageable] objects.
    ResourceManageable NULL = new ResourceManageableNull();

    /// Try to set the [resource capacity][#getCapacity()].
    ///
    /// @param newCapacity the new resource capacity
    /// @return true if the new capacity is valid (greater or equal to 0) and is greater or equal to
    /// the current allocated resource; false otherwise
    /// @see #getOccupied()
    boolean setCapacity(long newCapacity);

    /// Sum a given amount (negative or positive) of capacity to the total
    /// resource capacity.
    ///
    /// @param amountToSum the amount to sum in the current total capacity.
    /// If a positive number is given, the total capacity is increased; otherwise, it's decreased.
    /// @return true if the total capacity was changed; false otherwise
    boolean sumCapacity(long amountToSum);

    /// Try to add a given amount to the [resource capacity][#getCapacity()].
    ///
    /// @param capacityToAdd the amount to add
    /// @return true if capacityToAdd is greater than 0, false otherwise
    /// @see #getOccupied()
    /// @throws IllegalArgumentException when the capacity to add is negative
    boolean addCapacity(long capacityToAdd);

    /// Try to remove a given amount to the [resource capacity][#getCapacity()].
    ///
    /// @param capacityToRemove the amount to remove
    /// @return true if the `capacityToRemove` is greater than 0, the current allocated resource is less or equal
    ///         to the expected new capacity, and the capacity to remove is not higher than
    ///         the current capacity; false otherwise
    /// @see #getOccupied()
    /// @throws IllegalArgumentException when the capacity to remove is negative
    /// @throws IllegalStateException when the capacity to remove is higher than the current total capacity
    boolean removeCapacity(long capacityToRemove);

    /// Try to occupy a given amount of the resource, reducing that amount from the free amount.
    /// The meaning of the occupied amount (allocation or utilization) comes from the caller (see the class docs).
    ///
    /// @param amountToOccupy the amount of resource to be occupied
    /// @return true if `amountToOccupy` is greater than 0, and there is enough free resource to occupy;
    ///         false otherwise
    boolean occupy(long amountToOccupy);

    /// Try to occupy in this resource the amount specified by the capacity of the given resource.
    /// This method is commonly used to occupy a specific
    /// amount of a physical resource (this Resource instance)
    /// with a virtualized resource (the given Resource).
    ///
    /// @param resource the resource to try to occupy its capacity from the current resource
    /// @return true if required capacity from the given resource is greater than 0,
    /// and there is enough free resource to occupy; false otherwise
    /// @see #occupy(long)
    default boolean occupy(Resource resource){
        return occupy(resource.getCapacity());
    }

    /// Try to set the current total occupied amount of the resource, changing it to the
    /// given value. It doesn't increase the current occupied amount by the
    /// given amount. Instead, it changes the occupied amount to that
    /// specified amount.
    ///
    /// @param newTotalOccupied the new total occupied amount of the resource
    /// @return true if the `newTotalOccupied` is not negative,
    /// and there is enough capacity for it; false otherwise
    boolean setOccupied(long newTotalOccupied);

    /// Try to set the current total occupied amount of the resource, changing it to the
    /// given value. It doesn't increase the current occupied amount by the
    /// given amount. Instead, it changes the occupied amount to that
    /// specified amount.
    ///
    /// This method is just a shorthand to avoid explicitly converting a double to long.
    ///
    /// @param newTotalOccupied the new total occupied amount of the resource
    /// @return true if the `newTotalOccupied` is not negative,
    /// and there is enough capacity for it; false otherwise
    default boolean setOccupied(final double newTotalOccupied){
        return setOccupied((long)newTotalOccupied);
    }

    /// Try to free the amount specified by the capacity of the given resource from this resource.
    /// This method is commonly used to free a specific
    /// amount of a physical resource (this Resource instance)
    /// that was occupied by a virtualized resource (the given Resource).
    ///
    /// @param resource the resource that its capacity will be freed
    /// @return true if the capacity of the given resource is greater than 0,
    /// and there is enough occupied resource to free; false otherwise
    /// @see #free(long)
    default boolean free(final Resource resource){
        return free(resource.getCapacity());
    }

    /// Try to free a given occupied amount of the resource.
    ///
    /// @param amountToFree the amount of resource to be freed
    /// @return true if `amountToFree` is greater than 0,
    ///         and there is enough occupied resource to free; false otherwise
    boolean free(long amountToFree);

    /// Try to free a given occupied amount of the resource and then
    /// remove such amount from the total capacity.
    /// If the given amount is greater than the total occupied amount,
    /// all the occupied amount will be freed and that amount
    /// will be removed from the total capacity.
    ///
    /// @param amountToFree the amount of resource to be freed and then removed from the total capacity
    /// @return true if `amountToFree` is greater than 0,
    ///         and there is enough occupied resource to free; false otherwise
    boolean freeAndRemove(long amountToFree);

    /// Frees all the occupied amount, making the free amount equal to the resource capacity.
    ///
    /// @return the amount of resource freed
    long freeAll();

    /// Checks if there is a specific amount of resource being used.
    ///
    /// @param amountToCheck the amount of resource to check if it's used.
    /// @return true if the specified amount is being used; false otherwise
    boolean isResourceAmountBeingUsed(long amountToCheck);

    /// Checks if it is possible to change the current occupied amount to a
    /// new amount, depending on the resource capacity.
    ///
    /// @param newTotalOccupied the new total occupied amount of the resource
    /// @return true, if it is possible to occupy the new total amount; false otherwise
    boolean isSuitable(long newTotalOccupied);
}
