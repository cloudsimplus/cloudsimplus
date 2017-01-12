/*
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
import org.cloudbus.cloudsim.vms.Vm;

import java.util.function.Supplier;

/**
 * A Vm <a href="https://en.wikipedia.org/wiki/Scalability#Horizontal_and_vertical_scaling">Horizontal Scaling</a> mechanism
 * used by a {@link DatacenterBroker} to dynamically create or destroy VMs according to the arrival or termination of
 * Cloudlets, in order to enable load balancing.
 *
 * <p>Since Cloudlets can be created and submitted to a broker in runtime,
 * the number of arrived Cloudlets can be to much to existing VMs,
 * requiring the creation of new VMs to balance the load.
 * Accordingly, as Cloudlets terminates, some created VMs may not
 * be required anymore and should be destroyed.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 * @todo The mechanism to destroy created VMs that are not required anymore is not implemented yet.
 * To implement the down scaling, a new underload predicate and a scaleIfUnderloaded method should
 * be introduced.
 */
public interface HorizontalVmScaling extends VmScaling {

    /**
     * Gets a {@link Supplier} that will be used to create VMs when
     * the Load Balancer detects that the current Broker's VMs are overloaded.
     *
     * @return
     */
    Supplier<Vm> getVmSupplier();

    /**
     * Sets a {@link Supplier} that will be used to create VMs when
     * the Load Balancer detects that the Broker's VMs are overloaded.
     *
     * @param supplier the supplier to set
     * @return
     */
    HorizontalVmScaling setVmSupplier(Supplier<Vm> supplier);

    /**
     * Performs the horizontal scale if the Vm is overloaded, according to the {@link #getOverloadPredicate()} predicate.
     * The scaling is performed by creating a new Vm using the {@link #getVmSupplier()} method
     * and submitting it to the broker.
     *
     * <p>The time interval in which it will be checked if the Vm is overloaded
     * depends on the {@link Datacenter#getSchedulingInterval()} value.
     * Make sure to set such a value to enable the periodic overload verification.</p>
     *
     * @param time current simulation time
     */
    @Override void scaleIfOverloaded(double time);
}
