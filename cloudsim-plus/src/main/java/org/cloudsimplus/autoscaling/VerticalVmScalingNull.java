package org.cloudsimplus.autoscaling;

import org.cloudbus.cloudsim.resources.Resource;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.autoscaling.resources.ResourceScalingType;

import java.util.function.Function;

/**
 * A class that implements the Null Object Design Pattern for {@link VerticalVmScaling}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see VerticalVmScaling#NULL
 * @since CloudSim Plus 1.2.0
 */
final class VerticalVmScalingNull implements VerticalVmScaling {
    @Override public Class<? extends ResourceManageable> getResourceClassToScale() { return ResourceManageable.class; }
    @Override public VerticalVmScaling setResourceClassToScale(Class<? extends ResourceManageable> c) { return this; }
    @Override public double getScalingFactor() {
        return 0;
    }
    @Override public Function<Vm, Double> getResourceUsageThresholdFunction() { return vm -> 0.0; }
    @Override public long getResourceAmountToScale() {
        return 0;
    }
    @Override public VerticalVmScaling setScalingFactor(double scalingFactor) {
        return this;
    }
    @Override public boolean isVmUnderloaded() { return false; }
    @Override public boolean isVmOverloaded() { return false; }
    @Override public Resource getVmResourceToScale() { return Resource.NULL; }
    @Override public boolean requestScalingIfPredicateMatch(double time) {
        return false;
    }
    @Override public Vm getVm() {
        return Vm.NULL;
    }
    @Override public VmScaling setVm(Vm vm) {
        return this;
    }
    @Override public Function<Vm, Double> getUpperThresholdFunction() {
        return vm -> Double.MAX_VALUE;
    }
    @Override public VerticalVmScaling setUpperThresholdFunction(Function<Vm, Double> upperThresholdFunction) { return this; }
    @Override public Function<Vm, Double> getLowerThresholdFunction() { return vm -> Double.MIN_NORMAL; }
    @Override public VerticalVmScaling setLowerThresholdFunction(Function<Vm, Double> lowerThresholdFunction) { return this; }
    @Override public ResourceScalingType getResourceScalingType() { return ResourceScalingType.NULL; }
    @Override public VerticalVmScaling setResourceScalingType(ResourceScalingType resourceScalingType) { return this; }
}
