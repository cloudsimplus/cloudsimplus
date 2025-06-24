/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.resources;

import org.cloudsimplus.util.MathUtil;

/**
 * A class that represents simple resources such as RAM, CPU, Bandwidth or Pe.
 * It stores, for instance, the resource capacity and amount of free available resource.
 *
 * <p>The class is abstract just to ensure there will be a specific subclass
 * for each kind of resource, allowing to differentiate, for example,
 * a RAM Resource from a BW Resource.
 * The VM class also relies on this differentiation for generically getting a
 * required resource.</p>
 *
 * @author Uros Cibej
 * @author Anthony Sulistio
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public abstract class ResourceManageableAbstract extends ResourceAbstract implements ResourceManageable {

    /** @see #getAvailableResource() */
    private long availableResource;

    public ResourceManageableAbstract(final long capacity, final String unit) {
        super(capacity, unit);
        this.availableResource = capacity;
    }

    @Override
    public boolean setCapacity(final long newCapacity){
        if(newCapacity < 0 || getAllocatedResource() > newCapacity) {
            return false;
        }

        final long oldCapacity = this.capacity;
        this.capacity = newCapacity;
        sumAvailableResource(newCapacity - oldCapacity);
        return true;
    }

    @Override
    public boolean sumCapacity(final long amountToSum){
        if(amountToSum < 0){
            return removeCapacity(-1 * amountToSum);
        }

        return addCapacity(amountToSum);
    }


    @Override
    public boolean addCapacity(final long capacityToAdd) {
        MathUtil.nonNegative(capacityToAdd, "Number of PEs to add");
        return setCapacity(getCapacity()+capacityToAdd);
    }

    @Override
    public boolean removeCapacity(final long capacityToRemove) {
        MathUtil.nonNegative(capacityToRemove, "Number of PEs to remove");

        if(capacityToRemove > this.getCapacity()){
            throw new IllegalStateException(
                "The number of PEs to remove cannot be higher than the number of existing PEs. "+
                "Requested to remove: " + capacityToRemove + " PEs. Existing: " + this.getCapacity() + " PEs.");
        }
        return setCapacity(getCapacity()-capacityToRemove);
    }

    /**
     * Sum a given amount (negative or positive) of available (free) resource to the total
     * available resource.
     * @param amountToSum the amount to sum in the current total available resource.
     *                    If a positive number is given, it increases the total available resource;
     *                    otherwise decreases it.
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
     * @return true if {@code availableResource > 0 and availableResource <= capacity};
     *         false otherwise
     */
    protected final boolean setAvailableResource(final long newAvailableResource) {
        if(newAvailableResource < 0 || newAvailableResource > getCapacity()) {
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
    public boolean allocateResource(final long amountToAllocate) {
        if(amountToAllocate <= 0 || !isAmountAvailable(amountToAllocate)) {
            return false;
        }

        final long newAvailableResource = getAvailableResource() - amountToAllocate;

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
    public boolean deallocateAndRemoveResource(final long amountToDeallocate) {
        if(!deallocateResource(getActualAmountToDeallocate(amountToDeallocate))){
            return false;
        }

        return removeCapacity(amountToDeallocate);
    }

    /**
     * Gets the actual amount of resource that can be deallocated.
     * If the amount requested to deallocate is greater than the allocated one,
     * returns just the allocated amount. Otherwise, return the exact requested amount.
     * @param amountToDeallocate the amount requested to deallocate
     * @return the actual amount to deallocate
     */
    private long getActualAmountToDeallocate(final long amountToDeallocate) {
        return Math.min(amountToDeallocate, this.getAllocatedResource());
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
        final long previousAllocated = getAllocatedResource();
        setAvailableResource(getCapacity());
        return previousAllocated;
    }

    @Override
    public String toString() {
        return "%s: used %d of %d".formatted(getClass().getSimpleName(), getAllocatedResource(), getCapacity());
    }
}
