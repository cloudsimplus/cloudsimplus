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

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.Ram;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import static org.cloudbus.cloudsim.utilizationmodels.UtilizationModel.Unit;

import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.listeners.EventListener;

import java.util.function.Predicate;

/**
 * A Vm <a href="https://en.wikipedia.org/wiki/Scalability#Horizontal_and_vertical_scaling">Vertical Scaling</a> mechanism
 * used by a {@link DatacenterBroker} to request the dynamic scale of VM resources up or down, according to the current resource usage.
 * For each resource supposed to be scaled, a different {@code VerticalVmScaling} instance should be provided.
 *
 * <p>A {@link Vm} runs a set of {@link Cloudlet}s. When a {@code VerticalVmScaling} object is attached to a {@link Vm},
 *    it's required to define which {@link #getResourceClassToScale() resource will be scaled} ({@link Ram}, {@link Bandwidth}, etc)
 *    when it's {@link #getUnderloadPredicate() under} or {@link #getOverloadPredicate() overloaded}.
 * </p>
 *
 * <p>
 *     The scaling request follows this path:
 *     <ul>
 *         <li>a {@link Vm} that has a {@link VerticalVmScaling} object set monitors its own resource usage
 *         using an {@link EventListener}, to check if an {@link #getUnderloadPredicate() under} or
 *         {@link #getOverloadPredicate() overload} condition is met;</li>
 *         <li>if any of these conditions is met, the Vm uses the VerticalVmScaling to send a scaling request to its {@link DatacenterBroker};</li>
 *         <li>the DatacenterBroker fowards the request to the {@link Datacenter} where the Vm is hosted;</li>
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
 *    causes these {@code Cloudlets} to automatically increase/decrease their resource usage when the {@code Vm} resource
 *    is vertically scaled. This is not a CloudSim Plus issue, but the natural and maybe surprising effect
 *    that may trap researchers trying to implement and assess VM scaling policies.
 *
 *    <p>Consider the following example: a {@code VerticalVmScaling} is attached to a {@code Vm} to double its {@link Ram}
 *    when its usage reaches 50%. The {@code Vm} has 10GB of RAM.
 *    All {@code Cloudlets} running inside this {@code Vm} have a {@link UtilizationModel}
 *    for their RAM utilization define in {@link Unit#PERCENTAGE PERCENTAGE}. When the RAM utilization of all these
 *    {@code Cloudlets} reach the 50% (5GB), the {@code Vm} {@link Ram} will be doubled.
 *    However, as the RAM usage of the running {@code Cloudlets} is defined in percentage, they will
 *    continue to use 50% of {@code Vm}'s RAM, that now represents 10GB from the 20GB capacity.
 *    This way, the vertical scaling will have no real benefit.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.1
 */
public interface VerticalVmScaling extends VmScaling {

    /**
     * An attribute that implements the Null Object Design Pattern for {@link VerticalVmScaling}
     * objects.
     */
    VerticalVmScaling NULL = new VerticalVmScalingNull();

    /**
     * Gets the class of Vm resource that this scaling object will request up or down scaling.
     * Such a class can be {@link Ram}.class, {@link Bandwidth}.class or {@link Pe}.class.
     * @return
     */
    Class<? extends ResourceManageable> getResourceClassToScale();

    /**
     * Sets the class of Vm resource that this scaling object will request up or down scaling.
     * Such a class can be {@link Ram}.class, {@link Bandwidth}.class or {@link Pe}.class.
     * @param resourceClassToScale the resource class to set
     * @return
     */
    VerticalVmScaling setResourceClassToScale(Class<? extends ResourceManageable> resourceClassToScale);

    /**
     * Gets the factor that will be used to scale a Vm resource up or down,
     * whether such a resource is over or underloaded, according to the
     * defined predicates.
     *
     * <p>This is a percentage value in scale from 0 to 1. Every time the
     * VM needs to be scaled up or down, this factor will be applied
     * to increase or reduce a specific VM allocated resource.</p>
     *
     * @return the scaling factor
     * @see #getOverloadPredicate()
     */
    double getScalingFactor();

    /**
     * Gets the absolute amount of the Vm resource, defined
     * by {@link #getResourceClassToScale()}, that has to be
     * scaled up or down, based on the {@link #getScalingFactor() scaling factor}.
     * @return the absolute amount of the Vm resource to scale
     */
    double getResourceAmountToScale();

    /**
     * Sets the factor that will be used to scale a Vm resource up or down,
     * whether such a resource is over or underloaded, according to the
     * defined predicates.
     *
     * <p>This is a percentage value in scale from 0 to 1. Every time the
     * VM needs to be scaled up or down, this factor will be applied
     * to increase or reduce a specific VM allocated resource.</p>
     *
     * @param scalingFactor the scaling factor to set
     * @see #getOverloadPredicate()
     */
    VerticalVmScaling setScalingFactor(double scalingFactor);

    /**
     * Performs the vertical scale if the Vm is overloaded, according to the {@link #getOverloadPredicate()} predicate,
     * increasing the Vm resource to which the scaling object is linked to (that may be RAM, CPU, BW, etc),
     * by the factor defined a scaling factor.
     *
     * <p>The time interval in which it will be checked if the Vm is overloaded
     * depends on the {@link Datacenter#getSchedulingInterval()} value.
     * Make sure to set such a value to enable the periodic overload verification.</p>
     *
     * @param time current simulation time
     * @see #getScalingFactor()
     */
    @Override
    boolean requestScalingIfPredicateMatch(double time);

    /**
     * {@inheritDoc}
     *
     * <p>The up scaling is performed by increasing the amount of the {@link #getResourceClassToScale() resource}
     * the scaling is associated to.</p>
     * @return {@inheritDoc}
     */
    @Override
    Predicate<Vm> getOverloadPredicate();

    /**
     * {@inheritDoc}
     *
     * <p>The up scaling is performed by increasing the amount of the {@link #getResourceClassToScale() resource}
     * the scaling is associated to.</p>
     * @param predicate {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    VmScaling setOverloadPredicate(Predicate<Vm> predicate);

    /**
     * {@inheritDoc}
     *
     * <p>The down scaling is performed by decreasing the amount of the {@link #getResourceClassToScale() resource}
     * the scaling is associated to.</p>
     * @return {@inheritDoc}
     */
    @Override
    Predicate<Vm> getUnderloadPredicate();

    /**
     * {@inheritDoc}
     *
     * <p>The down scaling is performed by decreasing the amount of the {@link #getResourceClassToScale() resource}
     * the scaling is associated to.</p>
     * @param predicate {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    VmScaling setUnderloadPredicate(Predicate<Vm> predicate);
}
