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
/**
 * Provides classes to simulate <b>service interdependencies</b>:
 * a request that fans out into a tree of nested service calls
 * (e.g. {@code Request -> A -> B}, {@code A -> D -> E}), where
 * each {@link org.cloudsimplus.services.Service} is deployed on
 * one or more {@link org.cloudsimplus.vms.Vm}s.
 *
 * <p>The core abstractions are:
 * <ul>
 *   <li>{@link org.cloudsimplus.services.Service} — a logical microservice
 *       (e.g. AuthService) backed by a pool of VMs;</li>
 *   <li>{@link org.cloudsimplus.services.ServiceCall} — a node in the call graph,
 *       describing how much CPU work runs on the target service before and after
 *       the downstream calls, plus the list of children to invoke;</li>
 *   <li>{@link org.cloudsimplus.services.ServiceRequest} — a top-level request
 *       wrapping the root {@link org.cloudsimplus.services.ServiceCall};</li>
 *   <li>{@link org.cloudsimplus.services.ServiceBroker} — a {@link org.cloudsimplus.brokers.DatacenterBroker}
 *       that drives the call chain by creating cloudlets on demand
 *       and chaining them through cloudlet-finish listeners.</li>
 * </ul>
 *
 * <p>This package builds on top of regular {@link org.cloudsimplus.cloudlets.Cloudlet}
 * and {@link org.cloudsimplus.vms.Vm} primitives without modifying their semantics.</p>
 *
 * @since CloudSim Plus 9.0.0
 */
package org.cloudsimplus.services;
