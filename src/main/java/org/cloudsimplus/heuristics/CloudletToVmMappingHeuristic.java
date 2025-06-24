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
package org.cloudsimplus.heuristics;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.vms.Vm;

import java.util.List;

/**
 * Provides methods to be used for implementing a {@link Heuristic} to get
 * a suboptimal solution for mapping Cloudlets to VMs.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface CloudletToVmMappingHeuristic extends Heuristic<CloudletToVmMappingSolution> {
    /**
     * A property that implements the Null Object Design Pattern for {@link Heuristic} objects.
     */
    CloudletToVmMappingHeuristic NULL = new CloudletToVmMappingHeuristicNull();

    /**
     * @return the list of cloudlets to be mapped to {@link #getVmList() available VMs}.
     */
    List<Cloudlet> getCloudletList();

    /**
     * @return the list of available VMs to run Cloudlets.
     */
    List<Vm> getVmList();

    /**
     * Sets the list of Cloudlets to be mapped to {@link #getVmList() available VMs}.
     * @param cloudletList the list of Cloudlets to set
     */
    CloudletToVmMappingHeuristic setCloudletList(List<Cloudlet> cloudletList);

    /**
     * Sets the list of available VMs to run Cloudlets.
     * @param vmList the list of VMs to set
     */
    CloudletToVmMappingHeuristic setVmList(List<Vm> vmList);
}

