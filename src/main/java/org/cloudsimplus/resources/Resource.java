/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.resources;

/**
 * An interface to represent a physical or virtual resource (like RAM, CPU or Bandwidth)
 * that doesn't provide direct features to change the allocated amount of resource.
 * Objects that directly implement this interface are supposed to define
 * the capacity and amount of allocated resource only in their constructors.
 *
 * @author Uros Cibej
 * @author Anthony Sulistio
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface Resource extends ResourceCapacity {

    /**
     * An attribute that implements the Null Object Design Pattern for {@link Resource} objects.
     */
    Resource NULL = new ResourceNull();

    /**
     * Checks if this object is an instance of a given class.
     * @param classWanted the class to verify if the object is an instance of
     * @return true if the object is an instance of the given class, false otherwise
     */
    default boolean isSubClassOf(final Class classWanted) {
        return classWanted.isAssignableFrom(this.getClass());
    }

    /**
     * {@return the available amount of the resource that is free to be used}
     * The data unit depends on the implementing classes.
     */
    long getAvailableResource();

    /**
     * {@return the current total amount of allocated resource}
     * The data unit depends on the implementing classes, according to the {@link #getUnit()}.
     */
    long getAllocatedResource();

    /**
     * Checks if the capacity required by a given resource is available (free)
     * at this resource. This method is commonly used to check if there is a specific
     * amount of resource free at a physical resource (this Resource instance)
     * that is required by a virtualized resource (the given Resource).
     *
     * @param resource the resource to check if its capacity is available at the current resource
     * @return true if the capacity required by the given Resource is free; false otherwise
     * @see #isAmountAvailable(long)
     */
    default boolean isAmountAvailable(Resource resource){
        return isAmountAvailable(resource.getCapacity());
    }

    /**
     * Checks if there is a specific amount of resource available (free),
     * where such an amount is a double value that will be converted to long.
     *
     * <p>This method is just a shorthand to avoid explicitly converting a double to long.</p>
     *
     * @param amountToCheck the amount of resource to check if is free.
     *                      The data unit depends on the implementing classes, according to the {@link #getUnit()}.
     * @return true if the specified amount is free; false otherwise
     * @see #isAmountAvailable(long)
     * @see #getUnit()
     */
    default boolean isAmountAvailable(double amountToCheck){
        return isAmountAvailable((long)amountToCheck);
    }

    /**
     * Checks if there is a specific amount of resource available (free).
     * @param amountToCheck the amount of resource to check if is free.
     *                      The data unit depends on the implementing classes, according to the {@link #getUnit()}.
     * @return true if the specified amount is free; false otherwise
     * @see #getUnit()
     */
    boolean isAmountAvailable(long amountToCheck);

    /**
     * @return the measurement unit of this resource.
     */
    String getUnit();

    /**
     * Checks if the resource is full or not.
     *
     * @return true if the storage is full, false otherwise
     */
    default boolean isFull() {
        return getAvailableResource() <= 0;
    }

    /**
     * Gets the current percentage of resource utilization.
     * It is the percentage of the total resource capacity that is currently allocated.
     * @return current resource utilization (allocation) percentage (in scale from 0 to 1)
     */
    default double getPercentUtilization() {
        return getCapacity() > 0 ? getAllocatedResource() / (double)getCapacity() : 0.0;
    }
}
