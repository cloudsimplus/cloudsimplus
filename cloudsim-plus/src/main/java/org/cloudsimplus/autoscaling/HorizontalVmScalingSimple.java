/**
 * CloudSim Plus: A highly-extensible and easier-to-use Framework for
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
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.util.Log;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A {@link HorizontalVmScaling} implementation that allows defining that the VMs from a given
 * {@link DatacenterBroker} are overloaded or not based on the overall resource
 * utilization of all such VMs.
 *
 * <p>The condition in fact has to be defined by the user of this class,
 * by providing a {@link Predicate} using the {@link #setOverloadPredicate(Predicate)} method.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class HorizontalVmScalingSimple implements HorizontalVmScaling {
    /**
     * The last number of cloudlet creation requests
     * received by the broker. This is not related to the VM,
     * but the overall cloudlets creation requests.
     */
    private long cloudletCreationRequests;

    /**
     * Last time the scheduler checked for VM overload.
     */
    private double lastProcessingTime;
    private Vm vm;
    private Predicate<Vm> overloadPredicate;
    private Supplier<Vm> vmSupplier;

    public HorizontalVmScalingSimple(){
        this.overloadPredicate = VmScaling.FALSE_PREDICATE;
        this.vmSupplier = () -> Vm.NULL;
    }

    @Override
    public Vm getVm() {
        return vm;
    }

    @Override
    public VmScaling setVm(Vm vm) {
        this.vm = Objects.isNull(vm) ? Vm.NULL : vm;
        return this;
    }

    @Override
    public Supplier<Vm> getVmSupplier() {
        return vmSupplier;
    }

    @Override
    public final VmScaling setVmSupplier(Supplier<Vm> supplier) {
        this.vmSupplier = (Objects.isNull(supplier) ? () -> Vm.NULL : supplier);
        return this;
    }

    @Override
    public Predicate<Vm> getOverloadPredicate() {
        return overloadPredicate;
    }

    @Override
    public final HorizontalVmScaling setOverloadPredicate(Predicate<Vm> predicate) {
        this.overloadPredicate = Objects.isNull(predicate) ? FALSE_PREDICATE : predicate;
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * <p><b>The method will check the need to create a new
     * VM at the time interval defined by the {@link Datacenter#getSchedulingInterval()}.
     * A VM creation request is only sent when the VM is overloaded and
     * new Cloudlets were submitted to the broker.
     * </b></p>
     * @param time {@inheritDoc}
     */
    @Override
    public void scaleIfOverloaded(double time) {
        if(!isTimeToCheckOverload(time)) {
            return;
        }

        if (overloadPredicate.test(vm) && isNewCloudletsArrived()) {
            final double vmCpuUsagerPercent = vm.getTotalUtilizationOfCpu() * 100;
            Vm newVm = getVmSupplier().get();
            Log.printFormattedLine(
                "\t%.2f: %s%d: Requesting creation of Vm %d to receive new Cloudlets in order to balance load of Vm %d. Vm %d CPU usage is %.2f%%",
                time, getClass().getSimpleName(), vm.getId(), newVm.getId(), vm.getId(), vm.getId(), vmCpuUsagerPercent);
            vm.getBroker().submitVm(newVm);
        }

        cloudletCreationRequests = vm.getBroker().getNumberOfCloudletCreationRequests();
        lastProcessingTime = time;
    }

    /**
     * Checks if new Cloudlets were submitted to the broker since the last
     * time this method was called.
     * @return
     */
    private boolean isNewCloudletsArrived(){
        return vm.getBroker().getNumberOfCloudletCreationRequests() > cloudletCreationRequests;
    }

    private boolean isTimeToCheckOverload(double time) {
        return time > lastProcessingTime && (long) time % vm.getHost().getDatacenter().getSchedulingInterval() == 0;
    }

}
