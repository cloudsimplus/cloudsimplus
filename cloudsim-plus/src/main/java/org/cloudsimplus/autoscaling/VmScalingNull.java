package org.cloudsimplus.autoscaling;

import org.cloudbus.cloudsim.vms.Vm;

import java.util.function.Function;

/**
 * A class that implements the Null Object Design Pattern for {@link VmScaling} class.
 *
 * @author Manoel Campos da Silva Filho
 * @see VmScaling#NULL
 */
final class VmScalingNull implements VmScaling {
    @Override public Vm getVm() {
        return Vm.NULL;
    }
    @Override public VmScaling setVm(Vm vm) {
        return this;
    }
    @Override public boolean requestScalingIfPredicateMatch(double time) {
        return false;
    }
}
