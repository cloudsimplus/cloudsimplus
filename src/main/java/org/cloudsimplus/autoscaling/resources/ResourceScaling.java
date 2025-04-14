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
package org.cloudsimplus.autoscaling.resources;

import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.datacenters.Datacenter;

/**
 * A {@link FunctionalInterface} to define how the capacity of the resource to be scaled by
 * a {@link VerticalVmScaling} will be resized,
 * according to the defined {@link VerticalVmScaling#getScalingFactor() scaling factor}.
 *
 * <p>The interval in which the under and overload conditions are checked
 * is defined by the {@link Datacenter#getSchedulingInterval()}.
 * This way, during one interval and another, there may be some
 * SLA violation if the resource is overloaded between these intervals.</p>
 *
 * <p>There are some implementations of this functional interface such as
 * {@link ResourceScalingGradual} and {@link ResourceScalingInstantaneous}.
 * New ones can be defined using Lambda Expressions.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim 1.2.0
 * @see ResourceScalingGradual
 * @see ResourceScalingInstantaneous
 */
@FunctionalInterface
public interface ResourceScaling {
    /**
     * An attribute that implements the Null Object Design Pattern for {@link ResourceScaling}
     * objects.
     */
    ResourceScaling NULL = scalingInstance -> 0;

    /**
     * {@return the computed amount of resource to scale up or down,
     * depending on if the resource is over or underloaded, respectively}
     * @param vmScaling the {@link VerticalVmScaling} object that is in charge to scale a resource.
     */
    double getResourceAmountToScale(VerticalVmScaling vmScaling);
}
