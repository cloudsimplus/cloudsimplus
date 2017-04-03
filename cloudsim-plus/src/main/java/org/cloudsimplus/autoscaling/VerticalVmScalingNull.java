package org.cloudsimplus.autoscaling;

import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.function.Predicate;

/**
 * A class that implements the Null Object Design Pattern for {@link VerticalVmScaling}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see VerticalVmScaling#NULL
 */
final class VerticalVmScalingNull implements VerticalVmScaling {
    @Override public Class<? extends ResourceManageable> getResourceClassToScale() {
        return ResourceManageable.class;
    }
    @Override public VerticalVmScaling setResourceClassToScale(Class<? extends ResourceManageable> c) { return this; }
    @Override public double getScalingFactor() {
        return 0;
    }
    @Override public double getResourceAmountToScale() {
        return 0;
    }
    @Override public VerticalVmScaling setScalingFactor(double scalingFactor) {
        return this;
    }
    @Override public boolean requestScalingIfPredicateMatch(double time) {
        return false;
    }
    @Override public Vm getVm() {
        return Vm.NULL;
    }
    @Override public VmScaling setVm(Vm vm) {
        return this;
    }
    @Override public Predicate<Vm> getOverloadPredicate() {
        return FALSE_PREDICATE;
    }
    @Override public VmScaling setOverloadPredicate(Predicate<Vm> predicate) {
        return this;
    }
    @Override public Predicate<Vm> getUnderloadPredicate() {
        return FALSE_PREDICATE;
    }
    @Override public VmScaling setUnderloadPredicate(Predicate<Vm> predicate) {
        return this;
    }
}
