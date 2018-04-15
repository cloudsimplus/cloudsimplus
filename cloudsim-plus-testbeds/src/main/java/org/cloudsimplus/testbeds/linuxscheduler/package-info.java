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
 * The package contains a set of experiments to compare the {@link org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared},
 * that has an oversimplified implementation of a time-shared scheduler, and the new CloudSim Plus
 * {@link org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerCompletelyFair} class that provides
 * a basic implementation of the
 * <a href="https://en.wikipedia.org/wiki/Completely_Fair_Scheduler">Completely Fair Scheduler (CFS)</a> used by Linux Kernel.
 *
 * <p>The package provides two {@link org.cloudsimplus.testbeds.ExperimentRunner},
 * one for each of the experiments. Each runner has a main method
 * that allows to start a specific testbed. A testbed is a set of experiments executed
 * a given number of times defined by the runner class.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudsimplus.testbeds.linuxscheduler;
