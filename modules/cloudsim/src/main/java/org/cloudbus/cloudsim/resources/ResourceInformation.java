package org.cloudbus.cloudsim.resources;

/**
 * An interface just to get information such as capacity, allocation and
 * available amount of a physical or virtual resource (like RAM, CPU or Bandwidth).
 *
 * @param <T> The type of the resource capacity of the provisioner
 * @author Manoel Campos da Silva Filho
 */
public interface ResourceInformation<T extends Number> {
    /**
     * Gets the total capacity of the resource from the host that the provisioner can allocate to VMs.
     * 
     * @return the total resource capacity
     */
    T getCapacity();   
    
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
