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

import org.cloudsimplus.allocationpolicies.VmAllocationPolicy;
import org.cloudsimplus.autoscaling.resources.ResourceScaling;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.VmHostEventInfo;
import org.cloudsimplus.resources.*;
import org.cloudsimplus.utilizationmodels.UtilizationModel;
import org.cloudsimplus.vms.Vm;

import java.util.function.Function;

/// A Vm [Vertical Scaling](https://en.wikipedia.org/wiki/Scalability#Horizontal_and_vertical_scaling)
/// mechanism used by a [DatacenterBroker] to request the dynamic scale of
/// VM resources up or down, according to the current resource usage.
/// For each kind of resource to be scaled, a different `VerticalVmScaling`
/// instance should be provided.
/// If a scaling object is going to be set to a Vm, it has to be exclusive of that Vm.
/// Different Vms must have different instances of a scaling object.
///
/// A [Vm] runs a set of [Cloudlet]s. When a `VerticalVmScaling` object
/// is attached to a [Vm], it's required to define which
/// [resource will be scaled][#getResourceClass()] ([Ram], [Bandwidth], etc)
/// when it's [under][#getLowerThresholdFunction()] or
/// [overloaded][#getUpperThresholdFunction()].
///
/// The scaling request follows this path:
///
/// - a [Vm], which has a [VerticalVmScaling] object set, monitors its own
///   resource usage using an [EventListener], to check if an
///   [under][#getLowerThresholdFunction()] or
///   [overload][#getUpperThresholdFunction()] condition is met;
/// - if any of these conditions is met, the Vm uses the VerticalVmScaling
///   to send a scaling request to its [DatacenterBroker];
/// - the DatacenterBroker forwards the request to the [Datacenter]
///   where the Vm is hosted;
/// - the Datacenter delegates the task to its [VmAllocationPolicy];
/// - the VmAllocationPolicy checks if there is resource availability and then
///   finally scale the Vm.
///
/// ## WARNING
///
/// Make sure that the [UtilizationModel] of some of these `Cloudlets`
/// is defined as [ABSOLUTE][UtilizationModel.Unit#ABSOLUTE]. Defining the `UtilizationModel`
/// of all `Cloudlets` running inside the `Vm` as [PERCENTAGE][UtilizationModel.Unit#PERCENTAGE]
/// causes these `Cloudlets` to automatically increase/decrease their resource usage when the
/// `Vm` resource is vertically scaled.
/// This is not a CloudSim Plus issue, but the natural and maybe
/// surprising effect that may trap researchers trying to implement and assess VM scaling policies.
///
/// Consider the following example: a `VerticalVmScaling` is attached to
/// a `Vm` to double its [Ram] when its usage reaches 50%.
/// The `Vm` has 10GB of RAM.
/// All `Cloudlets` running inside this `Vm` have a [UtilizationModel]
/// for their RAM utilization defined in [PERCENTAGE][UtilizationModel.Unit#PERCENTAGE].
/// When the RAM utilization of all these
/// `Cloudlets` reach 50% (5GB), the `Vm` [Ram] will be doubled.
/// However, as the RAM usage of the running `Cloudlets` is defined in percentage, they will
/// continue to use 50% of `Vm`'s RAM, that now represents 10GB from the 20GB capacity.
/// This way, the vertical scaling will have no real benefit.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.1.0
public sealed interface VerticalVmScaling extends VmScaling
    permits VerticalVmScalingAbstract, VerticalVmScalingNull
{

    /**
     * An attribute that implements the Null Object Design Pattern for {@link VerticalVmScaling}
     * objects.
     */
    VerticalVmScaling NULL = new VerticalVmScalingNull();

    /**
     * {@return the class of Vm resource that up or down scaling will be requested}
     * Such a class can be {@link Ram}, {@link Bandwidth} or {@link Pe}.
     * @see #getResource()
     */
    Class<? extends ResourceManageable> getResourceClass();

    /**
     * {@return the factor that will be used to scale a Vm resource up or down,
     * whether such a resource is over or underloaded, according to the
     * defined predicates}
     *
     * <p>If the resource to scale is a {@link Pe}, this is the absolute number of PEs
     * to request adding or removing when the VM is over or underloaded, respectively.
     * For any other kind of resource, this is a percentage value in scale from 0 to 1.
     * Every time the VM needs to be scaled up or down, this factor will be applied
     * to increase or decrease a specific VM allocated resource.</p>
     *
     * @see #getUpperThresholdFunction()
     */
    double getScalingFactor();

    /**
     * Sets the factor that will be used to scale a Vm resource up or down,
     * whether such a resource is over or underloaded, according to the
     * defined predicates.
     *
     * <p>If the resource to scale is a {@link Pe}, this is the absolute number of PEs
     * to request adding or removing when the VM is over or underloaded, respectively.
     * For any other kind of resource, this is a percentage value in scale from 0 to 1.
     * Every time the VM needs to be scaled up or down, this factor will be applied
     * to increase or decrease a specific VM allocated resource.</p>
     *
     * @param scalingFactor the scaling factor to set which may be an absolute value
     *                      (for {@link Pe} scaling) or percentage (for scaling other resources)
     * @see #getUpperThresholdFunction()
     * @return this VerticalVmScaling object
     */
    VerticalVmScaling setScalingFactor(double scalingFactor);

    /**
     * Gets the lower or upper resource utilization threshold {@link Function},
     * depending on if the Vm resource is under or overloaded, respectively.
     *
     * @return the lower resource utilization threshold function if the Vm resource
     * is underloaded; upper resource utilization threshold function if the Vm resource
     * is overloaded; or a function that always returns 0 if the Vm isn't in any of these conditions.
     * @see #getLowerThresholdFunction()
     * @see #getUpperThresholdFunction()
     */
    Function<Vm, Double> getResourceUsageThresholdFunction();

    /**
     * {@return true if the Vm is underloaded, false otherwise}
     * That is based on the {@link #getLowerThresholdFunction()}.
     */
    boolean isVmUnderloaded();

    /**
     * {@return true if the Vm is overloaded, false otherwise}
     * That is based on the {@link #getUpperThresholdFunction()}.
     */
    boolean isVmOverloaded();

    /**
     * {@return the actual Vm Resource this scaling object is in charge of scaling}
     */
    Resource getResource();

    /**
     * {@return the absolute amount of the Vm resource which has to be scaled up or down}
     * That is based on the {@link #getScalingFactor() scaling factor}.
     *
     * @see #getResourceClass()
     */
    double getResourceAmountToScale();

    /**
     * Performs a vertical scale if the Vm is overloaded, according to the
     * {@link #getUpperThresholdFunction()} predicate,
     * increasing the Vm resource to which the scaling object is linked to
     * (that may be RAM, CPU, BW, etc.), according to the {@link #getScalingFactor() scaling factor}.
     *
     * <p>The time interval in which it will be checked if the Vm is overloaded
     * depends on the {@link Datacenter#getSchedulingInterval()} value.
     * Make sure to set such a value to enable the periodic overload verification.</p>
     *
     * @param evt event information containing the Vm to be scaled
     * @see #getScalingFactor()
     */
    @Override
    boolean requestUpScalingIfPredicateMatches(VmHostEventInfo evt);

    /**
     * {@return a Function that defines the upper utilization threshold for a Vm
     * which indicates if it is overloaded or not}
     * If it is overloaded, the Vm's {@link DatacenterBroker} will request to up scale the VM.
     * The up scaling is performed by increasing the amount of the {@link #getResourceClass() resource}
     * the scaling is associated to.
     *
     * <p>This function must receive a {@link Vm} and return the upper utilization threshold
     * for it as a percentage value between 0 and 1 (where 1 is 100%).
     * The VM will be defined as overloaded if the utilization of the {@link Resource}
     * this scaling object is related to is higher than the value returned by the {@link Function}
     * returned by this method.</p>
     *
     * @see #setUpperThresholdFunction(Function)
     */
    Function<Vm, Double> getUpperThresholdFunction();

    /**
     * Sets a {@link Function} that defines the upper utilization threshold for a {@link #getVm() Vm}
     * which indicates if it is overloaded or not.
     * If it is overloaded, the Vm's {@link DatacenterBroker} will request to up scale the VM.
     * The up scaling is performed by increasing the amount of the
     * {@link #getResourceClass() resource} the scaling is associated to.
     *
     * <p>This function must receive a {@link Vm} and return the upper utilization threshold
     * for it as a percentage value between 0 and 1 (where 1 is 100%).</p>
     *
     * <p>By setting the upper threshold as a {@link Function} instead of a directly
     * storing a {@link Double} value which represent the threshold, it is possible
     * to define the threshold dynamically instead of using a static value.
     * Furthermore, the threshold function can be reused for scaling objects of
     * different VMs.</p>
     *
     * @param upperThresholdFunction the upper utilization threshold function to set.
     * The VM will be defined as overloaded if the utilization of the {@link Resource}
     * this scaling object is related to is higher than the value returned by this {@link Function}.
     * @return this VerticalVmScaling object
     */
    VerticalVmScaling setUpperThresholdFunction(Function<Vm, Double> upperThresholdFunction);

    /**
     * {@return a Function that defines the lower utilization threshold for a Vm
     * which indicates if it is underloaded or not}
     * If it is underloaded, the Vm's {@link DatacenterBroker} will request to down scale the VM.
     * The down scaling is performed by decreasing the amount of the
     * {@link #getResourceClass() resource} the scaling is associated to.
     *
     * <p>This function must receive a {@link Vm} and return the lower utilization threshold
     * for it as a percentage value between 0 and 1 (where 1 is 100%).
     * The VM will be defined as underloaded if the utilization of the {@link Resource}
     * this scaling object is related to is lower than the value returned by the {@link Function}
     * returned by this method.</p>
     *
     * @see #setLowerThresholdFunction(Function)
     */
    Function<Vm, Double> getLowerThresholdFunction();

    /**
     * Sets a {@link Function} that defines the lower utilization threshold for a {@link #getVm() Vm}
     * which indicates if it is underloaded or not.
     * If it is underloaded, the Vm's {@link DatacenterBroker} will request to down scale the VM.
     * The down scaling is performed by decreasing the amount of the
     * {@link #getResourceClass() resource} the scaling is associated to.
     *
     * <p>This function must receive a {@link Vm} and return the lower utilization threshold
     * for it as a percentage value between 0 and 1 (where 1 is 100%).
     * By setting the lower threshold as a {@link Function} instead of a directly
     * storing a {@link Double} value which represent the threshold, it is possible
     * to define the threshold dynamically instead of using a static value.
     * Furthermore, the threshold function can be reused for scaling objects of
     * different VMs.</p>
     *
     * @param lowerThresholdFunction the lower utilization threshold function to set.
     * The VM will be defined as underloaded if the utilization of the {@link Resource}
     * this scaling object is related to is lower than the value returned by this {@link Function}.
     * @return this VerticalVmScaling object
     */
    VerticalVmScaling setLowerThresholdFunction(Function<Vm, Double> lowerThresholdFunction);

    /**
     * Sets the {@link ResourceScaling} that defines how the resource has to be resized.
     *
     * @param resourceScaling the {@link ResourceScaling} to set
     * @return this VerticalVmScaling object
     */
    VerticalVmScaling setResourceScaling(ResourceScaling resourceScaling);

    /**
     * Gets the current amount allocated to the {@link #getResource() resource}
     * managed by this scaling object.
     * It is just a shortcut to {@code getVmResourceToScale.getAllocatedResource()}.
     * @return the amount of allocated resource
     */
    long getAllocatedResource();

    /**
     * Tries to allocate more resources for a VM, if there is availability.
     * @return true if resources were allocated to the VM, false otherwise
     */
    boolean allocateResourceForVm();

    void logResourceUnavailable();

    void logDownscaleToZeroNotAllowed();
}
