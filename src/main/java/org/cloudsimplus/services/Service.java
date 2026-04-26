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
package org.cloudsimplus.services;

import org.cloudsimplus.vms.Vm;

import java.util.List;

/**
 * A logical microservice deployed on one or more {@link Vm}s.
 *
 * <p>A {@link ServiceCall} targets a {@link Service}; when the call is fired,
 * the {@link ServiceBroker} asks the service to {@link #selectVm() pick a VM}
 * (load balancing), then creates a Cloudlet on that VM to run the call's work.</p>
 *
 * <p>The Null Object Design Pattern is supported through {@link #NULL}.</p>
 *
 * @since CloudSim Plus 9.0.0
 */
public interface Service {
    /**
     * A no-op {@link Service} placeholder, to avoid {@code null} references.
     */
    Service NULL = new ServiceNull();

    /**
     * @return the unique id of this service inside the simulation,
     * or a negative value if no id was assigned yet.
     */
    long getId();

    /**
     * @param id the id to assign
     * @return this service
     */
    Service setId(long id);

    /**
     * @return the human-readable name of this service (e.g. {@code "AuthService"}).
     */
    String getName();

    /**
     * @return a read-only list of {@link Vm}s currently registered to this service.
     */
    List<Vm> getVms();

    /**
     * Registers a VM as a backing instance of this service.
     *
     * @param vm the VM to add
     * @return this service
     */
    Service addVm(Vm vm);

    /**
     * Picks a {@link Vm} from the {@link #getVms() VM pool} according to the
     * service's load-balancing strategy (round-robin by default).
     *
     * @return the selected VM, or {@link Vm#NULL} if the pool is empty.
     */
    Vm selectVm();
}
