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
/**
 * An experiment to assess whether SLA contracts for customers are being met or not
 * in case of Host failures. The experiment uses a {@link org.cloudsimplus.faultinjection.HostFaultInjection}
 * object to inject random Host failures. A {@link org.cloudsimplus.faultinjection.VmCloner} is used
 * to recovery failures.
 *
 * <p>The {@link org.cloudsimplus.testbeds.hostfaultinjection.HostFaultInjectionRunner} is the main class that executes
 * the {@link org.cloudsimplus.testbeds.hostfaultinjection.HostFaultInjectionExperiment} multiple
 * times, using different seeds, to assess meeting of SLA Contracts.</p>
 *
 * <p>The HostFaultInjectionExperiment uses the list of SLA Contracts defined
 * in {@link org.cloudsimplus.testbeds.hostfaultinjection.HostFaultInjectionExperiment#SLA_CONTRACTS_LIST}.
 * See the comment into this file for more details.</p>
 *
 * @author raysaoliveira
 */
package org.cloudsimplus.testbeds.hostfaultinjection;
