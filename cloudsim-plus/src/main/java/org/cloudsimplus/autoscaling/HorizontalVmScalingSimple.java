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

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A {@link HorizontalVmScaling} implementation that allows defining the conditions
 * to identify an under or overloaded VM, based on any desired criteria, such as
 * current RAM, CPU and/or Bandwidth utilization.
 * A {@link DatacenterBroker} thus monitors the VMs that have
 * an HorizontalVmScaling object in order to create or destroy VMs on demand.
 *
 * <p>Thes conditions in fact have to be defined by the user of this class,
 * by providing {@link Predicate}s using the {@link #setUnderloadPredicate(Predicate)}
 * and {@link #setOverloadPredicate(Predicate)} methods.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class HorizontalVmScalingSimple extends VmScalingAbstract implements HorizontalVmScaling {
    private Supplier<Vm> vmSupplier;

    /**
     * The last number of cloudlet creation requests
     * received by the broker. This is not related to the VM,
     * but the overall cloudlets creation requests.
     */
    private long cloudletCreationRequests;

    private Predicate<Vm> underloadPredicate;
    private Predicate<Vm> overloadPredicate;

    public HorizontalVmScalingSimple(){
        super();
        this.underloadPredicate = FALSE_PREDICATE;
        this.overloadPredicate = FALSE_PREDICATE;
        this.vmSupplier = () -> Vm.NULL;
    }

    @Override
    public Supplier<Vm> getVmSupplier() {
        return vmSupplier;
    }

    @Override
    public final HorizontalVmScaling setVmSupplier(Supplier<Vm> supplier) {
        this.vmSupplier = (Objects.isNull(supplier) ? () -> Vm.NULL : supplier);
        return this;
    }

    @Override
    public Predicate<Vm> getOverloadPredicate() {
        return overloadPredicate;
    }

    @Override
    public VmScaling setOverloadPredicate(Predicate<Vm> predicate) {
        validateFunctions(underloadPredicate, predicate);
        this.overloadPredicate = predicate;
        return this;
    }

    /**
     * Throws an exception if the under and overload predicates are equal (to make clear
     * that over and underload situations must be defined by different conditions)
     * or if any of them are null.
     *
     * @param underloadPredicate the underload predicate
     * @param overloadPredicate the overload predicate
     * @throws IllegalArgumentException if the two predicates are equal
     * @throws NullPointerException if any of the predicates is null
     */
    private void validateFunctions(Predicate<Vm> underloadPredicate, Predicate<Vm> overloadPredicate) {
        Objects.requireNonNull(underloadPredicate);
        Objects.requireNonNull(overloadPredicate);
        if(overloadPredicate.equals(underloadPredicate)){
            throw new IllegalArgumentException("Under and overload predicates cannot be equal.");
        }
    }

    @Override
    public Predicate<Vm> getUnderloadPredicate() {
        return underloadPredicate;
    }

    @Override
    public VmScaling setUnderloadPredicate(Predicate<Vm> predicate) {
        validateFunctions(predicate, overloadPredicate);
        this.underloadPredicate = predicate;
        return this;
    }

    @Override
    protected boolean requestScaling(double time) {
        return requestUpScaling(time);
    }

    private boolean requestUpScaling(double time) {
        if(!isNewCloudletsArrived()){
            return false;
        }

        final double vmCpuUsagerPercent = getVm().getCpuPercentUsage() * 100;
        Vm newVm = getVmSupplier().get();
        Log.printFormattedLine(
            "\t%.2f: %s%d: Requesting creation of Vm %d to receive new Cloudlets in order to balance load of Vm %d. Vm %d CPU usage is %.2f%%",
            time, getClass().getSimpleName(), getVm().getId(), newVm.getId(), getVm().getId(), getVm().getId(), vmCpuUsagerPercent);
        getVm().getBroker().submitVm(newVm);

        cloudletCreationRequests = getVm().getBroker().getCloudletCreatedList().size();
        return true;
    }

    /**
     * Checks if new Cloudlets were submitted to the broker since the last
     * time this method was called.
     * @return
     */
    private boolean isNewCloudletsArrived(){
        return getVm().getBroker().getCloudletCreatedList().size() > cloudletCreationRequests;
    }

    @Override
    public final boolean requestScalingIfPredicateMatch(double time) {
        if(!isTimeToCheckPredicate(time)) {
            return false;
        }

        final boolean requestedScaling =
            ( getUnderloadPredicate().test(getVm()) || getOverloadPredicate().test(getVm())) && requestScaling(time);
        setLastProcessingTime(time);
        return requestedScaling;
    }
}
