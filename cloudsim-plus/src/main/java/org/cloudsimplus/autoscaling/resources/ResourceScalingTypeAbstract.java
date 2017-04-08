package org.cloudsimplus.autoscaling.resources;

import org.cloudsimplus.autoscaling.VerticalVmScaling;

import java.util.Objects;

/**
 * An abstract implementation of {@link ResourceScalingType}.
 * @author Manoel Campos da Silva Filho
 */
public abstract class ResourceScalingTypeAbstract implements ResourceScalingType {
    private VerticalVmScaling vmScaling;

    @Override
    public VerticalVmScaling getVmScaling() {
        return vmScaling;
    }

    @Override
    public ResourceScalingType setVmScaling(VerticalVmScaling vmScaling) {
        Objects.requireNonNull(vmScaling);
        this.vmScaling = vmScaling;
        return this;
    }
}
