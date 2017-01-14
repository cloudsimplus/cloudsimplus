/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.resources;

/**
 * An interface to represent a physical or virtual resource (like RAM, CPU
 * or Bandwidth) that doesn't provide direct features to change allocated
 * amount of resource. Objects that directly implement this interface
 * are supposed to define the capacity and amount of allocated resource
 * in their constructors.
 *
 * @author Uros Cibej
 * @author Anthony Sulistio
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface Resource extends ResourceCapacity {
    /**
     * Gets the amount of the resource that is available (free).
     *
     * @return the amount of available resource
     *
     * @pre $none
     * @post $none
     */
    long getAvailableResource();

    /**
     * Gets the current total amount of allocated resource.
     *
     * @return amount of allocated resource
     *
     * @pre $none
     * @post $none
     */
    long getAllocatedResource();

    /**
     * Checks if there is a specific amount of resource available (free).
     * @param amountToCheck the amount of resource to check if is free.
     * @return true if the specified amount is free; false otherwise
     */
    boolean isResourceAmountAvailable(long amountToCheck);

    /**
     * Checks if the resource is full or not.
     *
     * @return <tt>true</tt> if the storage is full, <tt>false</tt> otherwise
     */
    boolean isFull();
}
