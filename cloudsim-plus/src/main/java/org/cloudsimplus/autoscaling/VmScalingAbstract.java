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
    private Predicate<Vm> underloadPredicate;

    protected VmScalingAbstract() {
        this.setOverloadPredicate(FALSE_PREDICATE);
        this.setUnderloadPredicate(FALSE_PREDICATE);
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
        this.overloadPredicate = Objects.isNull(predicate) ? FALSE_PREDICATE : predicate;
        return this;
    }

    @Override
    public Predicate<Vm> getUnderloadPredicate() {
        return underloadPredicate;
    }

    @Override
    public final VmScaling setUnderloadPredicate(Predicate<Vm> predicate) {
        this.underloadPredicate = Objects.isNull(predicate) ? FALSE_PREDICATE : predicate;
        return this;
    }
    /**
     * Checks if it is time to evaluate the {@link #getOverloadPredicate()}
     * and {@link #getUnderloadPredicate()} to check
     * if the Vm is over or underloaded, respectively.
     *
     * @param time current simulation time
     * @return true if the over and underload predicate has to be checked, false otherwise
     */
    protected boolean isTimeToCheckPredicate(double time) {
        return time > lastProcessingTime && (long) time % getVm().getHost().getDatacenter().getSchedulingInterval() == 0;
    }

    @Override
    public final boolean requestScalingIfPredicateMatch(double time) {
        if(!isTimeToCheckPredicate(time)) {
            return false;
        }

        final boolean requestedScaling =
            (getOverloadPredicate().test(getVm()) || getUnderloadPredicate().test(getVm())) && requestScaling(time);
        lastProcessingTime = time;
        return requestedScaling;
    }

    /**
     * Performs the actual request to scale the Vm up or down,
     * depending if it is over or underloaded, respectively.
     * This method is automatically called by {@link #requestScalingIfPredicateMatch(double)}
     * when it is verified that the Vm is over or underloaded.
     *
     * @param time current simulation time
     * @return true if the request was actually sent, false otherwise
     */
    protected abstract boolean requestScaling(double time);


}
