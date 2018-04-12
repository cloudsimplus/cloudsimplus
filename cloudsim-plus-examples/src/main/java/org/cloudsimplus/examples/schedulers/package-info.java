/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018  Universidade da Beira Interior (UBI, Portugal) and
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
 * Examples showing how to use {@link org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler}
 * and {@link org.cloudbus.cloudsim.schedulers.vm.VmScheduler} in different infrastructure setups.
 * Some examples show how to use the
 * {@link org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeSharedOverSubscription}
 * to enable oversubscription of PM processors and the
 * {@link org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerCompletelyFair}
 * that implements a real scheduler used by recent Linux Kernels.
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudsimplus.examples.schedulers;
