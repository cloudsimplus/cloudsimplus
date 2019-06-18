/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.resources;

/**
 * An interface to represent a physical or virtual resource (like RAM, CPU or
 * Bandwidth) with features to manage resource capacity and allocation.
 *
 * @author Uros Cibej
 * @author Anthony Sulistio
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface ResourceManageable extends Resource {

    /**
     * An attribute that implements the Null Object Design Pattern for
     * {@link ResourceManageable} objects.
     */
    ResourceManageable NULL = new ResourceManageableNull();

    /**
     * Try to set the {@link #getCapacity() resource capacity}.
     *
     * @param newCapacity the new resource capacity
     * @return true if capacity is greater or equal to 0 and capacity greater or equal to
     * current allocated resource, false otherwise
     * @see #getAllocatedResource()
     */
    boolean setCapacity(long newCapacity);

    /**
     * Sum a given amount (negative or positive) of capacity to the total
     * resource capacity.
     *
     * @param amountToSum the amount to sum in the current total capacity.
     * If given a positive number, increases the total capacity; otherwise, decreases it.
     * @return true if the total capacity was changed; false otherwise
     */
    boolean sumCapacity(long amountToSum);

    /**
     * Try to add a given amount to the {@link #getCapacity() resource capacity}.
     *
     * @param capacityToAdd the amount to add
     * @return true if capacityToAdd is greater than 0, false otherwise
     * @see #getAllocatedResource()
     * @throws IllegalArgumentException when the capacity to add is negative
     */
    boolean addCapacity(long capacityToAdd);

    /**
     * Try to remove a given amount to the {@link #getCapacity() resource capacity}.
     *
     * @param capacityToRemove the amount to remove
     * @return true if capacityToRemove is greater than 0, the current allocated resource is less or equal
     *         to the expected new capacity and the capacity to remove is not higher than
     *         the current capacity; false otherwise
     * @see #getAllocatedResource()
     * @throws IllegalArgumentException when the capacity to remove is negative
     * @throws IllegalStateException when the capacity to remove is higher than the current total capacity
     */
    boolean removeCapacity(long capacityToRemove);

    /**
     * Try to allocate a given amount of the resource, reducing that amount from the
     * total available resource.
     *
     * @param amountToAllocate the amount of resource to be allocated
     * @return true if amountToAllocate is greater than 0 and there is enough resource to
     * allocate, false otherwise
     */
    boolean allocateResource(long amountToAllocate);

    /**
     * Try to allocate in this resource, the amount of resource specified by the capacity of the given resource.
     * This method is commonly used to allocate a specific
     * amount from a physical resource (this Resource instance)
     * to a virtualized resource (the given Resource).
     *
     * @param resource the resource to try to allocate its capacity from the current resource
     * @return true if required capacity from the given resource is greater than 0 and there is enough resource to
     * allocate, false otherwise
     * @see #allocateResource(long)
     */
    default boolean allocateResource(Resource resource){
        return allocateResource(resource.getCapacity());
    }

    /**
     * Try to set the current total amount of allocated resource, changing it to the
     * given value. It doesn't increase the current allocated resource by the
     * given amount, instead, it changes the allocated resource to that
     * specified amount.
     *
     * @param newTotalAllocatedResource the new total amount of resource to
     * allocate, changing the allocate resource to this new amount.
     * @return true if newTotalAllocatedResource is not negative and there is
     * enough resource to allocate, false otherwise
     */
    boolean setAllocatedResource(long newTotalAllocatedResource);

    /**
     * Try to set the current total amount of allocated resource, changing it to the
     * given value. It doesn't increase the current allocated resource by the
     * given amount, instead, it changes the allocated resource to that
     * specified amount.
     *
     * <p>This method is just a shorthand to avoid explicitly converting
     * a double to long.</p>
     *
     * @param newTotalAllocatedResource the new total amount of resource to
     * allocate, changing the allocate resource to this new amount.
     * @return true if newTotalAllocatedResource is not negative and there is
     * enough resource to allocate, false otherwise
     */
    default boolean setAllocatedResource(double newTotalAllocatedResource){
        return setAllocatedResource((long)newTotalAllocatedResource);
    }

    /**
     * Try to deallocate all the capacity of the given resource from this resource.
     * This method is commonly used to deallocate a specific
     * amount of a physical resource (this Resource instance)
     * that was being used by a virtualized resource (the given Resource).
     *
     * @param resource the resource that its capacity will be deallocated
     * @return true if capacity of the given resource is greater than 0 and there is enough resource to
     * deallocate, false otherwise
     * @see #deallocateResource(long)
     */
    default boolean deallocateResource(Resource resource){
        return deallocateResource(resource.getCapacity());
    }

    /**
     * Try to deallocate a given amount of the resource.
     *
     * @param amountToDeallocate the amount of resource to be deallocated
     * @return true if amountToDeallocate is greater than 0 and there is enough resource to
     * deallocate, false otherwise
     */
    boolean deallocateResource(long amountToDeallocate);

    /**
     * Try to deallocate a given amount of the resource and then
     * remove such amount from the total capacity.
     * If the given amount is greater than the total allocated resource,
     * all the resource will be deallocated and that amount
     * will be removed from the total capacity.
     *
     * @param amountToDeallocate the amount of resource to be deallocated and then removed from
     *                           the total capacity
     * @return true if amountToDeallocate is greater than 0 and there is enough resource to
     * deallocate, false otherwise
     */
    boolean deallocateAndRemoveResource(long amountToDeallocate);

    /**
     * De-allocates all allocated resources, restoring the total available
     * resource to the resource capacity.
     *
     * @return the amount of resource freed
     */
    long deallocateAllResources();

    /**
     * Checks if there is a specific amount of resource being used.
     *
     * @param amountToCheck the amount of resource to check if is used.
     * @return true if the specified amount is being used; false otherwise
     */
    boolean isResourceAmountBeingUsed(long amountToCheck);

    /**
     * Checks if it is possible to change the current allocated resource to a
     * new amount, depending on the available resource remaining.
     *
     * @param newTotalAllocatedResource the new total amount of resource to
     * allocate.
     *
     * @return true, if it is possible to allocate the new total resource; false
     * otherwise
     */
    boolean isSuitable(long newTotalAllocatedResource);
}
