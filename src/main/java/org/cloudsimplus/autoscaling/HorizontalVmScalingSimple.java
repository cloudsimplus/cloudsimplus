/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
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

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.listeners.VmHostEventInfo;
import org.cloudsimplus.vms.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * <p>
 * A {@link HorizontalVmScaling} implementation that allows defining the condition
 * to identify an overloaded VM, based on any desired criteria, such as
 * current RAM, CPU and/or Bandwidth utilization.
 * A {@link DatacenterBroker} monitors the VMs that have
 * an HorizontalVmScaling object in order to create or destroy VMs on demand.
 * </p>
 *
 * <br>
 * <p>The overload condition has to be defined
 * by providing a {@link Predicate} using the {@link #setOverloadPredicate(Predicate)} method.
 * Check the {@link HorizontalVmScaling} documentation for details on how to enable
 * horizontal down scaling using the {@link DatacenterBroker}.
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 * @see HorizontalVmScaling
 */
@Accessors
public class HorizontalVmScalingSimple extends VmScalingAbstract implements HorizontalVmScaling {
    private static final Logger LOGGER = LoggerFactory.getLogger(HorizontalVmScalingSimple.class.getSimpleName());

    @Getter @Setter @NonNull
    private Supplier<Vm> vmSupplier;

    /**
     * The last number of cloudlet creation requests
     * received by the broker. This is not related to the VM,
     * but the overall Cloudlets creation requests.
     */
    private long cloudletCreationRequests;

    @Getter @Setter @NonNull
    private Predicate<Vm> overloadPredicate;

    public HorizontalVmScalingSimple(){
        super();
        this.overloadPredicate = FALSE_PREDICATE;
        this.vmSupplier = () -> Vm.NULL;
    }

    @Override
    protected boolean requestScaling(final double time) {
        if(!haveNewCloudletsArrived()){
            return false;
        }

        final double vmCpuUsagePercent = getVm().getCpuPercentUtilization() * 100;
        final Vm newVm = getVmSupplier().get();
        final String timeStr = "%.2f".formatted(time);
        LOGGER.info(
            "{}: {}{}: Requesting creation of {} to receive new Cloudlets in order to balance load of {}. {} CPU usage is {}%",
            timeStr, getClass().getSimpleName(), getVm(), newVm, getVm(), getVm().getId(), vmCpuUsagePercent);
        getVm().getBroker().submitVm(newVm);

        cloudletCreationRequests = getVm().getBroker().getCloudletCreatedList().size();
        return true;
    }

    /**
     * {@return true or false} to indicate if new Cloudlets were submitted
     * to the broker since the last time this method was called.
     */
    private boolean haveNewCloudletsArrived(){
        return getVm().getBroker().getCloudletCreatedList().size() > cloudletCreationRequests;
    }

    @Override
    public final boolean requestUpScalingIfPredicateMatches(final VmHostEventInfo evt) {
        if (isTimeToCheckPredicate(evt.getTime())) {
            setLastProcessingTime(evt.getTime());
            return overloadPredicate.test(getVm()) && requestScaling(evt.getTime());
        }

        return false;
    }
}
