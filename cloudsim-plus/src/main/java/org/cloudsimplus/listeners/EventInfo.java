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
package org.cloudsimplus.listeners;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * A general interface that represents data to be passed to
 * {@link EventListener} objects that are registered to be notified when some
 * events happen for a given simulation entity such as a
 * {@link Datacenter}, {@link Host}, {@link Vm}, {@link Cloudlet} and so on.
 *
 * <p>
 * There is not implementing class for such interfaces because instances of them
 * are just Data Type Objects (DTO) that just store data and do not have
 * business rules. Each interface that extends this one has a
 * {@code getInstance()} method to create an object from that interface. Such
 * method uses the JDK8 static methods for interfaces to provide such a feature
 * e reduce the number of classes, providing a simpler design.
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 *
 * @see DatacenterEventInfo
 * @see HostEventInfo
 * @see VmEventInfo
 * @see CloudletEventInfo
 */
public interface EventInfo {

    /**
     * Gets the time the event happened.
     *
     * @return
     */
    double getTime();

    /**
     * Gets the listener that was notified about the event.
     * @return
     */
    EventListener<? extends EventInfo> getListener();

    /**
     * Gets a EventInfo instance from the given parameters.
     *
     * @param listener the listener to be notified about the event
     * @param time the time the event happened
     * @return
     */
    static EventInfo of(final EventListener<? extends EventInfo> listener, final double time) {
        return new EventInfo() {
            @Override public double getTime() { return time; }
            @Override public EventListener<? extends EventInfo> getListener() { return listener; }
        };
    }
}
