/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
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

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.autoscaling.resources.ResourceScaling;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.VmHostEventInfo;

import java.util.function.Function;

import static org.cloudbus.cloudsim.utilizationmodels.UtilizationModel.Unit;

/**
 * A Vm <a href="https://en.wikipedia.org/wiki/Scalability#Horizontal_and_vertical_scaling">Vertical Scaling</a> mechanism
 * used by a {@link DatacenterBroker} to request the dynamic scale of VM resources up or down, according to the current resource usage.
 * For each resource supposed to be scaled, a different {@code VerticalVmScaling} instance should be provided.
 * If a scaling object is going to be set to a Vm, it has to be exclusive of that Vm.
 * Different Vms must have different instances of a scaling object.
 *
 * <p>A {@link Vm} runs a set of {@link Cloudlet}s. When a {@code VerticalVmScaling} object is attached to a {@link Vm},
 *    it's required to define which {@link #getResourceClass() resource will be scaled} ({@link Ram}, {@link Bandwidth}, etc)
 *    when it's {@link #getLowerThresholdFunction() under} or {@link #getUpperThresholdFunction() overloaded}.
 * </p>
 *
 * <p>
 *     The scaling request follows this path:
 *     <ul>
 *         <li>a {@link Vm} that has a {@link VerticalVmScaling} object set monitors its own resource usage
 *         using an {@link EventListener}, to check if an {@link #getLowerThresholdFunction() under} or
 *         {@link #getUpperThresholdFunction() overload} condition is met;</li>
 *         <li>if any of these conditions is met, the Vm uses the VerticalVmScaling to send a scaling request to its {@link DatacenterBroker};</li>
 *         <li>the DatacenterBroker forwards the request to the {@link Datacenter} where the Vm is hosted;</li>
 *         <li>the Datacenter delegates the task to its {@link VmAllocationPolicy};</li>
 *         <li>the VmAllocationPolicy checks if there is resource availability and then finally scale the Vm.</li>
 *     </ul>
 * </p>
 *
 * <h1>WARNING</h1>
 * <hr>
 *    Make sure that the {@link UtilizationModel} of some of these {@code Cloudlets}
 *    is defined as {@link Unit#ABSOLUTE ABSOLUTE}. Defining the {@code UtilizationModel}
 *    of all {@code Cloudlets} running inside the {@code Vm} as {@link Unit#PERCENTAGE PERCENTAGE}
 *    causes these {@code Cloudlets} to automatically increase/decrease their resource usage when the
 *    {@code Vm} resource is vertically scaled. This is not a CloudSim Plus issue, but the natural and
 *    maybe surprising effect that may trap researchers trying to implement and assess VM scaling policies.
 *
 *    <p>Consider the following example: a {@code VerticalVmScaling} is attached to a {@code Vm} to double
 *    its {@link Ram} when its usage reaches 50%. The {@code Vm} has 10GB of RAM.
 *    All {@code Cloudlets} running inside this {@code Vm} have a {@link UtilizationModel}
 *    for their RAM utilization define in {@link Unit#PERCENTAGE PERCENTAGE}. When the RAM utilization of all these
 *    {@code Cloudlets} reach the 50% (5GB), the {@code Vm} {@link Ram} will be doubled.
 *    However, as the RAM usage of the running {@code Cloudlets} is defined in percentage, they will
 *    continue to use 50% of {@code Vm}'s RAM, that now represents 10GB from the 20GB capacity.
 *    This way, the vertical scaling will have no real benefit.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.1.0
 */
public interface VerticalVmScaling extends VmScaling {

    /**
     * An attribute that implements the Null Object Design Pattern for {@link VerticalVmScaling}
     * objects.
     */
    VerticalVmScaling NULL = new VerticalVmScalingNull();

    /**
     * Gets the class of Vm resource this scaling object will request up or down scaling.
     * Such a class can be {@link Ram}.class, {@link Bandwidth}.class or {@link Pe}.class.
     * @return
     * @see #getResource()
     */
    Class<? extends ResourceManageable> getResourceClass();

    /**
     * Sets the class of Vm resource that this scaling object will request up or down scaling.
     * Such a class can be {@link Ram}.class, {@link Bandwidth}.class or {@link Pe}.class.
     * @param resourceClass the resource class to set
     * @return
     */
    VerticalVmScaling setResourceClass(Class<? extends ResourceManageable> resourceClass);

    /**
     * Gets the factor that will be used to scale a Vm resource up or down,
     * whether such a resource is over or underloaded, according to the
     * defined predicates.
     *
     * <p>If the resource to scale is a {@link Pe}, this is the number of PEs
     * to request adding or removing when the VM is over or underloaded, respectively.
     * For any other kind of resource, this is a percentage value in scale from 0 to 1. Every time the
     * VM needs to be scaled up or down, this factor will be applied
     * to increase or reduce a specific VM allocated resource.</p>
     *
     * @return the scaling factor to set which may be an absolute value (for {@link Pe} scaling)
     *         or percentage (for scaling other resources)
     * @see #getUpperThresholdFunction()
     */
    double getScalingFactor();

    /**
     * Sets the factor that will be used to scale a Vm resource up or down,
     * whether such a resource is over or underloaded, according to the
     * defined predicates.
     *
     * <p>If the resource to scale is a {@link Pe}, this is the number of PEs
     * to request adding or removing when the VM is over or underloaded, respectively.
     * For any other kind of resource, this is a percentage value in scale from 0 to 1. Every time the
     * VM needs to be scaled up or down, this factor will be applied
     * to increase or reduce a specific VM allocated resource.</p>
     *
     * @param scalingFactor the scaling factor to set which may be an absolute value (for {@link Pe} scaling)
     *                      or percentage (for scaling other resources)
     * @see #getUpperThresholdFunction()
     */
    VerticalVmScaling setScalingFactor(double scalingFactor);

    /**
     * Gets the lower or upper resource utilization threshold {@link Function},
     * depending if the Vm resource is under or overloaded, respectively.
     *
     * @return the lower resource utilization threshold function if the Vm resource
     * is underloaded, upper resource utilization threshold function if the Vm resource
     * is overloaded, or a function that always returns 0 if the Vm isn't in any of these conditions.
     * @see #getLowerThresholdFunction()
     * @see #getUpperThresholdFunction()
     */
    Function<Vm, Double> getResourceUsageThresholdFunction();

    /**
     * Checks if the Vm is underloaded or not, based on the
     * {@link #getLowerThresholdFunction()}.
     * @return true if the Vm is underloaded, false otherwise
     */
    boolean isVmUnderloaded();

    /**
     * Checks if the Vm is overloaded or not, based on the
     * {@link #getUpperThresholdFunction()}.
     * @return true if the Vm is overloaded, false otherwise
     */
    boolean isVmOverloaded();

    /**
     * Gets the actual Vm {@link Resource} this scaling object is in charge of scaling.
     * This resource is defined after calling the {@link #setResourceClass(Class)}.
     * @return
     */
    Resource getResource();

    /**
     * Gets the absolute amount of the Vm resource which has to be
     * scaled up or down, based on the {@link #getScalingFactor() scaling factor}.
     *
     * @return the absolute amount of the Vm resource to scale
     * @see #getResourceClass()
     */
    double getResourceAmountToScale();

    /**
     * Performs the vertical scale if the Vm is overloaded, according to the {@link #getUpperThresholdFunction()} predicate,
     * increasing the Vm resource to which the scaling object is linked to (that may be RAM, CPU, BW, etc),
     * by the factor defined a scaling factor.
     *
     * <p>The time interval in which it will be checked if the Vm is overloaded
     * depends on the {@link Datacenter#getSchedulingInterval()} value.
     * Make sure to set such a value to enable the periodic overload verification.</p>
     *
     * @param evt current simulation time
     * @see #getScalingFactor()
     */
    @Override
    boolean requestUpScalingIfPredicateMatches(VmHostEventInfo evt);

    /**
     * Gets a {@link Function} that defines the upper utilization threshold for a {@link #getVm() Vm}
     * which indicates if it is overloaded or not.
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
     * @return
     * @see #setUpperThresholdFunction(Function)
     */
    Function<Vm, Double> getUpperThresholdFunction();

    /**
     * Sets a {@link Function} that defines the upper utilization threshold for a {@link #getVm() Vm}
     * which indicates if it is overloaded or not.
     * If it is overloaded, the Vm's {@link DatacenterBroker} will request to up scale the VM.
     * The up scaling is performed by increasing the amount of the {@link #getResourceClass() resource}
     * the scaling is associated to.
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
     * @return
     */
    VerticalVmScaling setUpperThresholdFunction(Function<Vm, Double> upperThresholdFunction);

    /**
     * Gets a {@link Function} that defines the lower utilization threshold for a {@link #getVm() Vm}
     * which indicates if it is underloaded or not.
     * If it is underloaded, the Vm's {@link DatacenterBroker} will request to down scale the VM.
     * The down scaling is performed by decreasing the amount of the {@link #getResourceClass() resource}
     * the scaling is associated to.
     *
     *
     * <p>This function must receive a {@link Vm} and return the lower utilization threshold
     * for it as a percentage value between 0 and 1 (where 1 is 100%).
     *
     * The VM will be defined as underloaded if the utilization of the {@link Resource}
     * this scaling object is related to is lower than the value returned by the {@link Function}
     * returned by this method.</p>
     *
     * @return
     * @see #setLowerThresholdFunction(Function)
     */
    Function<Vm, Double> getLowerThresholdFunction();

    /**
     * Sets a {@link Function} that defines the lower utilization threshold for a {@link #getVm() Vm}
     * which indicates if it is underloaded or not.
     * If it is underloaded, the Vm's {@link DatacenterBroker} will request to down scale the VM.
     * The down scaling is performed by decreasing the amount of the {@link #getResourceClass() resource}
     * the scaling is associated to.
     *
     * <p>This function must receive a {@link Vm} and return the lower utilization threshold
     * for it as a percentage value between 0 and 1 (where 1 is 100%).</p>
     *
     * <p>By setting the lower threshold as a {@link Function} instead of a directly
     * storing a {@link Double} value which represent the threshold, it is possible
     * to define the threshold dynamically instead of using a static value.
     * Furthermore, the threshold function can be reused for scaling objects of
     * different VMs.</p>
     *
     * @param lowerThresholdFunction the lower utilization threshold function to set.
     * The VM will be defined as underloaded if the utilization of the {@link Resource}
     * this scaling object is related to is lower than the value returned by this {@link Function}.
     * @return
     */
    VerticalVmScaling setLowerThresholdFunction(Function<Vm, Double> lowerThresholdFunction);

    /**
     * Sets the {@link ResourceScaling} that defines how the resource has to be resized.
     * @param resourceScaling the {@link ResourceScaling} to set
     * @return
     */
    VerticalVmScaling setResourceScaling(ResourceScaling resourceScaling);

    /**
     * Gets the current amount allocated to the {@link #getResource() resource} managed by this scaling object.
     * It is just a shortcut to {@code getVmResourceToScale.getAllocatedResource()}.
     * @return the amount of allocated resource
     */
    long getAllocatedResource();
}
