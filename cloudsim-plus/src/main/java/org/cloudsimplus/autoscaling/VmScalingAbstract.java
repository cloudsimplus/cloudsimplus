/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2016  Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
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
        this.overloadPredicate = FALSE_PREDICATE;
        this.underloadPredicate = FALSE_PREDICATE;
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
        validatePredicates(underloadPredicate, predicate);
        this.overloadPredicate = predicate;
        return this;
    }

    @Override
    public Predicate<Vm> getUnderloadPredicate() {
        return underloadPredicate;
    }

    @Override
    public final VmScaling setUnderloadPredicate(Predicate<Vm> predicate) {
        validatePredicates(predicate, overloadPredicate);
        this.underloadPredicate = predicate;
        return this;
    }

    /**
     * Throws an exception if the under and overload predicates are equal (to make clear
     * that over and underload situations must be defined by different conditions)
     * or if any of them are null.
     *
     * @param underloadPredicate the underload predicate to check
     * @param overloadPredicate the overload predicate to check
     * @throws IllegalArgumentException if the two predicates are equal
     * @throws NullPointerException if any of the predicates are null
     */
    private void validatePredicates(Predicate<Vm> underloadPredicate, Predicate<Vm> overloadPredicate) {
        Objects.requireNonNull(underloadPredicate);
        Objects.requireNonNull(overloadPredicate);
        if(overloadPredicate.equals(underloadPredicate)){
            throw new IllegalArgumentException("Underload and overload predicate cannot be equal");
        }
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
