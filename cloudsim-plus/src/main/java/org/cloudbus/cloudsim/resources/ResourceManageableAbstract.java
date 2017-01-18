/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.resources;

/**
 * A class that represents simple resources such as RAM, CPU, Bandwidth or Pe,
 * storing, for instance, the resource capacity and amount of free available resource.
 *
 * <p>The class is abstract just to ensure there will be an specific subclass
 * for each kind of resource, allowing to differentiate, for instance,
 * a RAM resource instance from a BW resource instance.
 * The VM class also relies on this differentiation for generically getting a
 * required resource.</p>
 *
 * @author Uros Cibej
 * @author Anthony Sulistio
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public abstract class ResourceManageableAbstract implements ResourceManageable {
    /** @see #getCapacity() */
    private long capacity;

    /** @see #getAvailableResource() */
    private long availableResource;

    public ResourceManageableAbstract(final long capacity) {
        if(!isCapacityValid(capacity))
            throw new IllegalArgumentException("Capacity cannot be negative");

        initCapacityAndAvailableResource(capacity);
    }

    private boolean isCapacityValid(final long capacity) {
        return capacity >= 0;
    }

    private void initCapacityAndAvailableResource(final long capacity){
        this.capacity = capacity;
        this.availableResource = capacity;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public final boolean setCapacity(long newCapacity){
        if(newCapacity <= 0 || getAllocatedResource() > newCapacity) {
            return false;
        }

        final long capacityDifference = newCapacity - this.capacity;
        this.capacity = newCapacity;
        sumAvailableResource(capacityDifference);

        return true;
    }

    /**
     * Sum a given amount (negative or positive) of available (free) resource to the total
     * available resource.
     * @param amountToSum the amount to sum in the current total
     * available resource. If given a positive number, increases the total available
     * resource; otherwise, decreases the total available resource.
     * @return true if the total available resource was changed; false otherwise
     */
    protected boolean sumAvailableResource(final long amountToSum){
        final long newTotalAvailableResource = getAvailableResource() + amountToSum;
        return setAvailableResource(newTotalAvailableResource);
    }

    /**
     * Sets the given amount as available resource.
     *
     * @param newAvailableResource the new amount of available resource to set
     * @return true if {@code availableResource > 0 and availableResource <= capacity}, false otherwise
     */
    protected final boolean setAvailableResource(final long newAvailableResource) {
        if(newAvailableResource < 0 || newAvailableResource > capacity) {
            return false;
        }

        this.availableResource = newAvailableResource;
        return true;
    }

    @Override
    public long getAvailableResource() {
        return availableResource;
    }

    @Override
    public long getAllocatedResource() {
        return getCapacity() - getAvailableResource();
    }

    @Override
    public boolean allocateResource(final long amountToAllocate) {
        if(amountToAllocate <= 0 || !isResourceAmountAvailable(amountToAllocate)) {
            return false;
        }

        final Long newAvailableResource = getAvailableResource() - amountToAllocate;

        return setAvailableResource(newAvailableResource);
    }

    @Override
    public boolean setAllocatedResource(final long newTotalAllocatedResource) {
        if(newTotalAllocatedResource < 0 || !isSuitable(newTotalAllocatedResource)) {
            return false;
        }

        deallocateAllResources();
        return allocateResource(newTotalAllocatedResource);
    }

    @Override
    public boolean deallocateResource(final long amountToDeallocate) {
        if(amountToDeallocate <= 0 || !isResourceAmountBeingUsed(amountToDeallocate)) {
            return false;
        }

        final long newAvailableResource = getAvailableResource() + amountToDeallocate;
        return setAvailableResource(newAvailableResource);
    }

    @Override
    public long deallocateAllResources() {
        final Long previousAllocated = getAllocatedResource();
        setAvailableResource(getCapacity());
        return previousAllocated;
    }

    @Override
    public boolean isResourceAmountAvailable(final long amountToCheck) {
        return getAvailableResource() >= amountToCheck;
    }

    @Override
    public boolean isResourceAmountAvailable(double amountToCheck) {
        return isResourceAmountAvailable((long)amountToCheck);
    }

    @Override
    public boolean isResourceAmountBeingUsed(final long amountToCheck) {
        return getAllocatedResource() >= amountToCheck;
    }

    @Override
    public boolean isSuitable(final long newTotalAllocatedResource) {
        if(newTotalAllocatedResource <= getAllocatedResource()) {
            return true;
        }

        final long allocationDifference = newTotalAllocatedResource - getAllocatedResource();
        return getAvailableResource() >= allocationDifference;
    }

    @Override
    public boolean isFull() {
        return availableResource == 0;
    }
}
