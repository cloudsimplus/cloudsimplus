package org.cloudsimplus.autoscaling;

import org.cloudbus.cloudsim.vms.Vm;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A base class for implementing {@link HorizontalVmScaling} and
 * {@link VerticalVmScaling}.
 *
 * @author Manoel Campos da Silva Filho
 */
public abstract class VmScalingAbstract implements VmScaling {
    /**
     * Last time the scheduler checked for VM overload.
     */
    protected double lastProcessingTime;
    private Vm vm;
    private Predicate<Vm> overloadPredicate;

    protected VmScalingAbstract() {
        this.setOverloadPredicate(VmScaling.FALSE_PREDICATE);
        this.setVm(Vm.NULL);
    }

    @Override
    public Vm getVm() {
        return vm;
    }

    @Override
    public final VmScaling setVm(Vm vm) {
        Objects.requireNonNull(vm);
        this.vm = vm;
        return this;
    }

    @Override
    public Predicate<Vm> getOverloadPredicate() {
        return overloadPredicate;
    }

    @Override
    public final VmScaling setOverloadPredicate(Predicate<Vm> predicate) {
        this.overloadPredicate = Objects.isNull(predicate) ? VmScaling.FALSE_PREDICATE : predicate;
        return this;
    }

    /**
     * Checks if it is time to evaluate the {@link #getOverloadPredicate()} to check
     * if the Vm is overloaded or not.
     *
     * @param time current simulation time
     * @return true if the overload predicate has to be checked, false otherwise
     */
    protected boolean isTimeToCheckOverload(double time) {
        return time > lastProcessingTime && (long) time % getVm().getHost().getDatacenter().getSchedulingInterval() == 0;
    }

    @Override
    public final boolean requestUpScalingIfOverloaded(double time) {
        if(!isTimeToCheckOverload(time)) {
            return false;
        }

        final boolean requested = getOverloadPredicate().test(getVm()) && requestUpScaling(time);
        lastProcessingTime = time;
        return requested;
    }

    /**
     * Performs the actual request to up scale the Vm.
     * This method is automatically called by {@link #requestUpScalingIfOverloaded(double)}
     * when it is verified that the Vm is overloaded.
     *
     * @param time current simulation time
     * @return true if the request was actually sent, false otherwise
     */
    protected abstract boolean requestUpScaling(double time);
}
