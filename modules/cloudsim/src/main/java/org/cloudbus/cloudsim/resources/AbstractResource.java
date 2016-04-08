package org.cloudbus.cloudsim.resources;

import org.cloudbus.cloudsim.util.Calculator;

/**
 * A class that represent simple resources such as RAM, CPU or Bandwidth,
 * storing, for instance, its capacity and amount of free available resource.
 * The class is abstract just to ensure there will be an specific subclass
 * for each kind of resource, allowing to differentiate, for instance,
 * a RAM resource instance from a BW resource instance.
 * The VM class also relies on this differentiation for generically getting a
 * required resource (see {@link org.cloudbus.cloudsim.Vm#getResource(java.lang.Class)}). 
 * 
 * @author Manoel Campos da Silva Filho
 * @param <T> The type of the resource capacity of the provisioner
 * @since CloudSim Toolkit 3.0.4
 */
public abstract class AbstractResource<T extends Number & Comparable<T>> implements ResourceManageable<T>{
    /** A calculator for basic math operations over values extending of the Number class. */
    private final Calculator<T> calc;
    
    private final T ZERO;
    
    /** @see #getCapacity() */
    private T capacity;
    
    /** @see #getAvailableResource() */
    private T availableResource;

    public AbstractResource(final T capacity) {
        calc = new Calculator<>(capacity);
        ZERO = calc.getZero();

        if(!isCapacityValid(capacity))
            throw new IllegalArgumentException("Capacity cannot be null, negative or zero");

        initCapacityAndAvailableResource(capacity);
    }

    private boolean isCapacityValid(final T capacity) throws IllegalArgumentException {
        return (capacity != null && capacity.compareTo(ZERO) > 0);
    }
    
    private void initCapacityAndAvailableResource(final T capacity){
        this.capacity = capacity;
        this.availableResource = capacity;
    }
    
    @Override
    public T getCapacity() {
        return capacity;
    }

    @Override
    public final boolean setCapacity(T newCapacity){
        if(newCapacity == null)
            throw new IllegalArgumentException("New capacity cannot be null");
                
        if(calc.isNegativeOrZero(newCapacity) || 
        getAllocatedResource().compareTo(newCapacity) > 0)
            return false;

        final T capacityDifference = calc.subtract(newCapacity, this.capacity);
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
    protected boolean sumAvailableResource(final T amountToSum){
        final T newTotalAvailableResource = calc.add(getAvailableResource(), amountToSum);
        return setAvailableResource(newTotalAvailableResource);
    }
    
    /**
     * Sets the given amount as available resource.
     * 
     * @param newAvailableResource the new amount of available resource to set
     * @return true if availableResource > 0 and availableResource &lt;= capacity, false otherwise
     */
    protected final boolean setAvailableResource(final T newAvailableResource) {
        if(calc.isNegative(newAvailableResource) || 
        newAvailableResource.compareTo(capacity) > 0)
            return false;
        
        this.availableResource = newAvailableResource;
        return true;
    }    

    @Override
    public T getAvailableResource() {
        return availableResource;
    }    

    protected boolean capacityHasBeenSet() {
        return this.capacity != null;
    }

    @Override
    public T getAllocatedResource() {        
        return calc.subtract(getCapacity(), getAvailableResource());
    }

    @Override
    public boolean allocateResource(final T amountToAllocate) {
        if(calc.isNegativeOrZero(amountToAllocate) || !isResourceAmountAvailable(amountToAllocate))
            return false;
        
        final T newAvailableResource = 
            calc.subtract(getAvailableResource(), amountToAllocate);

        return setAvailableResource(newAvailableResource);
    }

    @Override
    public boolean setAllocatedResource(final T newTotalAllocatedResource) {
        if(calc.isNegative(newTotalAllocatedResource) || !isSuitable(newTotalAllocatedResource))
            return false;
        
        deallocateAllResources();
        return allocateResource(newTotalAllocatedResource);
    }

    @Override
    public boolean deallocateResource(final T amountToDeallocate) {
        if(calc.isNegativeOrZero(amountToDeallocate) || !isResourceAmountBeingUsed(amountToDeallocate))
            return false;
        
        final T newAvailableResource = 
                calc.add(getAvailableResource(), amountToDeallocate);

        return setAvailableResource(newAvailableResource);
    }

    @Override
    public T deallocateAllResources() {
        final T previousAllocated = getAllocatedResource();
        setAvailableResource(getCapacity());
        return previousAllocated;
    }

    @Override
    public boolean isResourceAmountAvailable(final T amountToCheck) {
        return getAvailableResource().compareTo(amountToCheck) >= 0;
    }
    
    @Override
    public boolean isResourceAmountBeingUsed(final T amountToCheck) {
        return getAllocatedResource().compareTo(amountToCheck) >= 0;
    }    

    @Override
    public boolean isSuitable(final T newTotalAllocatedResource) {
        if(newTotalAllocatedResource.compareTo(getAllocatedResource()) <= 0)
            return true;
        
        final T allocationDifference = 
                calc.subtract(newTotalAllocatedResource, getAllocatedResource());
        
        return getAvailableResource().compareTo(allocationDifference) >= 0;        
    }

    @Override
    public boolean isFull() {
        // currentSize == capacity
        return availableResource.doubleValue() < .0000001;
    }
}
