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

import java.util.List;

/**
 * Service-aware extension of the {@link org.cloudsimplus.brokers.DatacenterBroker}
 * contract: registers {@link Service}s, accepts {@link ServiceRequest}s and drives
 * each request's call graph to completion by chaining cloudlets together.
 *
 * <p>{@link DatacenterBroker} is a {@code sealed} interface, so this is intentionally
 * <b>not</b> a subtype of it; the canonical implementation
 * ({@link ServiceBrokerSimple}) extends {@link org.cloudsimplus.brokers.DatacenterBrokerSimple}
 * and additionally implements this interface.</p>
 *
 * @since CloudSim Plus 9.0.0
 */
public interface ServiceBroker {
    /**
     * Registers a {@link Service} so the broker can resolve {@link ServiceCall#getService() targets}
     * to actual VMs at execution time.
     *
     * @param service the service to register
     * @return this broker
     */
    ServiceBroker addService(Service service);

    /**
     * @return read-only list of all registered services.
     */
    List<Service> getServices();

    /**
     * Submits a {@link ServiceRequest}. If the simulation is already running, the
     * request fires immediately (subject to {@link ServiceRequest#getSubmissionDelay()});
     * otherwise it is queued and dispatched as soon as the broker starts.
     *
     * @param request the request to submit
     * @return this broker
     */
    ServiceBroker submitRequest(ServiceRequest request);

    /**
     * Convenience overload of {@link #submitRequest(ServiceRequest)} that accepts a list.
     */
    ServiceBroker submitRequests(List<ServiceRequest> requests);

    /**
     * @return read-only list of all requests submitted to the broker so far.
     */
    List<ServiceRequest> getRequests();

    /**
     * @return read-only list of requests that have already finished
     * (i.e. their root call has completed).
     */
    List<ServiceRequest> getFinishedRequests();
}
