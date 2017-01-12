package org.cloudbus.cloudsim.resources;

/**
 * An interface to allow getting the capacity of a given resource.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface ResourceCapacity  {
    /**
     * Gets the total capacity of the resource.
     *
     * @return the total resource capacity
     */
    long getCapacity();
}
