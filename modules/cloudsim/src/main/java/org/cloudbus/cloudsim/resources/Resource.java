package org.cloudbus.cloudsim.resources;

/**
 * An interface to represent a physical or virtual resource (like RAM, CPU
 * or Bandwidth) that doesn't provide direct features to change allocated 
 * amount of resource. Objects that directly implement this interface
 * are supposed to define the capacity and amount of allocated resource
 * in their constructors.
 *
 * @param <T> The type of the resource capacity
 * @author Manoel Campos da Silva Filho
 */
public interface Resource<T extends Number> extends ResourceCapacity<T> {
    /**
     * Gets the amount of the resource that is available (free).
     * 
     * @return the amount of available resource 
     * 
     * @pre $none
     * @post $none
     */
    T getAvailableResource();
    
    /**
     * Gets the current total amount of allocated resource.
     * 
     * @return amount of allocated resource
     * 
     * @pre $none
     * @post $none
     */
    T getAllocatedResource();  
    
    /**
     * Checks if there is a specific amount of resource available (free).
     * @param amountToCheck the amount of resource to check if is free.
     * @return true if the specified amount is free; false otherwise
     */
    boolean isResourceAmountAvailable(T amountToCheck);            
    
    /**
     * Checks if the storage is full or not.
     * 
     * @return <tt>true</tt> if the storage is full, <tt>false</tt> otherwise
     */
    boolean isFull();
}
