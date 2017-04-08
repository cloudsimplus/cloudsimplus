package org.cloudsimplus.autoscaling.resources;

import org.cloudsimplus.autoscaling.VerticalVmScaling;

/**
 * A class that implements the Null Object Design Pattern for {@link ResourceScalingType}
 * objects.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.2.0
 */
final class ResourceScalingNull implements ResourceScalingType {
    @Override public long getResourceAmountToScale() { return 0; }
    @Override public VerticalVmScaling getVmScaling() { return VerticalVmScaling.NULL; }
    @Override public ResourceScalingType setVmScaling(VerticalVmScaling vmScaling) { return this; }
}
