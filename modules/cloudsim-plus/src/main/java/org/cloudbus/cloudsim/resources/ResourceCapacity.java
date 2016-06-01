package org.cloudbus.cloudsim.resources;

/**
 * An interface to allow getting the capacity of a given resource.
 * @author Manoel Campos da Silva Filho
 * @param <T> The type of the resource capacity
 */
public interface ResourceCapacity<T extends Number>  {
    /**
     * Gets the total capacity of the resource.
     * 
     * @return the total resource capacity
     */
    T getCapacity();       
}
