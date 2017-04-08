package org.cloudsimplus.autoscaling;

import org.cloudbus.cloudsim.vms.Vm;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A class that implements the Null Object Design Pattern for {@link HorizontalVmScaling}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see HorizontalVmScaling#NULL
 */
final class HorizontalVmScalingNull implements HorizontalVmScaling {
    @Override public Supplier<Vm> getVmSupplier() {
        return () -> Vm.NULL;
    }
    @Override public HorizontalVmScaling setVmSupplier(Supplier<Vm> supplier) {
        return this;
    }
    @Override public boolean requestScalingIfPredicateMatch(double time) {
        return false;
    }
    @Override public Predicate<Vm> getOverloadPredicate() { return vm -> false; }
    @Override  public VmScaling setOverloadPredicate(Predicate<Vm> predicate) { return this; }
    @Override  public Predicate<Vm> getUnderloadPredicate() { return vm -> false; }
    @Override  public VmScaling setUnderloadPredicate(Predicate<Vm> predicate) { return this; }
    @Override public Vm getVm() {
        return Vm.NULL;
    }
    @Override public VmScaling setVm(Vm vm) {
        return this;
    }
}
