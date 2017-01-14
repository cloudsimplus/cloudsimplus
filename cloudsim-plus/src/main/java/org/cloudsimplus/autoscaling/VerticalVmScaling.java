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
import org.cloudbus.cloudsim.datacenters.Datacenter;

/**
 * A Vm <a href="https://en.wikipedia.org/wiki/Scalability#Horizontal_and_vertical_scaling">Vertical Scaling</a> mechanism
 * used by a {@link DatacenterBroker} to dynamically scale VM resources up or down, according to the current resource usage.
 * For each resource that is supposed to be scaled, such as RAM, CPU and Bandwidth,
 * a different VerticalVmScaling instance should be provided.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.1
 */
public interface VerticalVmScaling extends VmScaling {
    /**
     * Gets the factor that will be used to scale a Vm resource up or down,
     * whether if such a resource is over or underloaded, according to the
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
     * Sets the factor that will be used to scale a Vm resource up or down,
     * whether if such a resource is over or underloaded, according to the
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
    @Override void scaleIfOverloaded(double time);

}
