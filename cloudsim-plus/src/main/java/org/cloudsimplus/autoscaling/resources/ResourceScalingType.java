package org.cloudsimplus.autoscaling.resources;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudsimplus.autoscaling.VerticalVmScaling;

/**
 * Defines how the capacity of the resource to be scaled by a {@link VerticalVmScaling}
 * will be resized, according to the defined {@link VerticalVmScaling#getScalingFactor() scaling factor}.
 *
 * <p>The interval in which the under and overload conditions are checked
 * is defined by the {@link Datacenter#getSchedulingInterval()}.
 * This way, during one interval and another, there may be some
 * SLA violation if the resource is overloaded between these intervals.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim 1.2.0
 */
public interface ResourceScalingType {
    /**
     * An attribute that implements the Null Object Design Pattern for {@link ResourceScalingType}
     * objects.
     */
    ResourceScalingType NULL = new ResourceScalingNull();

    /**
     * Computes the amount of resource to scale up or down,
     * depending if the resource is over or underloaded, respectively.
     *
     * @return
     */
    long getResourceAmountToScale();

    /**
     * Gets the {@link VerticalVmScaling} object that is in charge to scale a resource.
     * @return
     */
    VerticalVmScaling getVmScaling();

    /**
     * Sets the {@link VerticalVmScaling} object that is in charge to scale a resource.
     * @param vmScaling the scaling object to set
     * @return
     */
    ResourceScalingType setVmScaling(VerticalVmScaling vmScaling);

}
