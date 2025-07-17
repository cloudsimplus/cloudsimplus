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

import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.listeners.VmHostEventInfo;
import org.cloudsimplus.vms.Vm;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/// A Vm [Horizontal Scaling](https://en.wikipedia.org/wiki/Scalability#Horizontal_and_vertical_scaling)
/// mechanism used by a [DatacenterBroker] to dynamically create VMs according to the arrival of
/// Cloudlets, to enable load balancing.
///
/// Since Cloudlets can be created and submitted to a broker in runtime,
/// the number of arrived Cloudlets can be too much to the existing VMs,
/// requiring the creation of new VMs to balance the load.
/// A HorizontalVmScaling implementation performs
/// such upscaling by creating VMs as needed.
///
///
/// To enable horizontal down-scaling to destroy idle VMs, the [DatacenterBroker] has to be used
/// by setting a [DatacenterBroker#setVmDestructionDelayFunction(Function)].
/// Since there is no Cloudlet migration mechanism (and it isn't intended to have),
/// if a VM becomes underloaded, there is nothing that can be done until all Cloudlets
/// finish executing. When that happens, the vmDestructionDelayFunction
/// will handle such a situation.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.0.0
public sealed interface HorizontalVmScaling extends VmScaling
    permits HorizontalVmScalingNull, HorizontalVmScalingAbstract
{
    Predicate<Vm> FALSE_PREDICATE = vm -> false;

    /**
     * An attribute that implements the Null Object Design Pattern for {@link HorizontalVmScaling}
     * objects.
     */
    HorizontalVmScaling NULL = new HorizontalVmScalingNull();

    /**
     * @return a Supplier that will be used to create VMs when
     * the Load Balancer detects that the current Broker's VMs are overloaded
     */
    Supplier<Vm> getVmSupplier();

    /**
     * Sets a {@link Supplier} that will be used to create VMs when
     * the Load Balancer detects that the current Broker's VMs are overloaded.
     *
     * @param supplier the supplier to set
     */
    HorizontalVmScaling setVmSupplier(Supplier<Vm> supplier);

    /// Requests a horizontal scale if a Vm is overloaded, according to the
    /// [#getOverloadPredicate()] predicate.
    /// The scaling is performed by creating a new Vm using the [#getVmSupplier()] method
    /// and submitting it to the broker.
    ///
    /// The time interval in which it will be checked if the Vm is overloaded
    /// depends on the [#getSchedulingInterval()] value.
    /// Make sure to set such a value to enable the periodic overload verification.
    ///
    /// **The method will check the need to create a new
    /// VM at the time interval defined by the [#getSchedulingInterval()].
    /// A VM creation request is only sent when the VM is overloaded and
    /// new Cloudlets were submitted to the broker.
    /// **
    ///
    /// @param evt event information, including the current simulation time and the VM to be scaled
    /// @return {@inheritDoc}
    @Override
    boolean requestUpScalingIfPredicateMatches(VmHostEventInfo evt);

    /**
     * @return the {@link Predicate} that defines when a {@link #getVm() Vm} is overloaded or not,
     * that will make the Vm's {@link DatacenterBroker} to upscale the VM.
     * The upscaling is performed by creating new VMs to run new arrived Cloudlets
     * and then balance the load.
     *
     * @see #setOverloadPredicate(Predicate)
     */
    Predicate<Vm> getOverloadPredicate();

    /**
     * Sets a {@link Predicate} that defines when a {@link #getVm() Vm} is overloaded or not,
     * making the {@link DatacenterBroker} to upscale the VM.
     * The upscaling is performed by creating new VMs to run new arrived Cloudlets
     * to balance the load.
     *
     * @param predicate a predicate that checks certain conditions
     *                  to define a {@link #getVm() Vm} as overloaded.
     *                  The predicate receives the Vm that has to be checked.
     *                  Such a condition can be defined, for instance,
     *                  based on Vm's {@link Vm#getCpuPercentUtilization(double) CPU usage}
     *                  and/or any other VM resource usage.
     *                  Despite the VmScaling is already linked to a {@link #getVm() Vm},
     *                  the Vm parameter for the {@link Predicate} enables reusing the same predicate
     *                  to detect overload of different VMs.
     */
    HorizontalVmScaling setOverloadPredicate(Predicate<Vm> predicate);
}
