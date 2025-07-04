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
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.VmHostEventInfo;
import org.cloudsimplus.vms.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An interface to allow implementing
 * <a href="https://en.wikipedia.org/wiki/Scalability#Horizontal_and_vertical_scaling">horizontal and vertical scaling</a>
 * of {@link Vm}s.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0.0
 */
public sealed interface VmScaling permits HorizontalVmScaling, VerticalVmScaling, VmScalingAbstract, VmScalingNull {
    Logger LOGGER = LoggerFactory.getLogger(VmScaling.class.getSimpleName());

    /**
     * An attribute that implements the Null Object Design Pattern for {@link VmScaling}
     * objects.
     */
    VmScaling NULL = new VmScalingNull();

    /**
     * {@return the Vm that this VmScaling is linked to}
     */
    Vm getVm();

    /**
     * Sets a {@link Vm} to this VmScaling. The broker will call this VmScaling
     * in order to balance load when its Vm is over utilized.
     *
     * <p><b>When the VmScaling is assigned to a Vm, the Vm sets itself to the VmScaling object,
     * creating an association between the two objects.</b></p>
     * @param vm the Vm to set
     */
    VmScaling setVm(Vm vm);

    /**
     * Requests the Vm to be scaled up or down if it is over or underloaded, respectively.
     * The scaling request will be sent to the {@link DatacenterBroker} only
     * if the under or overload condition is met, that depends on the implementation
     * of the scaling mechanism.
     *
     * <p>The Vm to which this VmScaling is related to, creates an
     * {@link Vm#addOnUpdateProcessingListener(EventListener) UpdateProcessingListener}
     * that will call this method to check if it's time to perform a down or up scaling,
     * every time the Vm processing is updated.</p>
     *
     * @param evt event information, including the current simulation time and the VM to be scaled
     * @return true if the Vm is over or underloaded and up or down scaling request was sent to the broker;
     *         false otherwise
     */
    boolean requestUpScalingIfPredicateMatches(VmHostEventInfo evt);
}
