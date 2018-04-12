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
package org.cloudsimplus.heuristics;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Collections;
import java.util.List;

/**
 * A class to allow the implementation of Null Object Design Pattern
 * for {@link CloudletToVmMappingHeuristic} interface and extensions of it.
 *
 * @author Manoel Campos da Silva Filho
 * @see CloudletToVmMappingHeuristic#NULL
 */
final class CloudletToVmMappingHeuristicNull extends HeuristicNull<CloudletToVmMappingSolution> implements CloudletToVmMappingHeuristic {
    @Override public List<Cloudlet> getCloudletList() { return Collections.EMPTY_LIST; }
    @Override public List<Vm> getVmList() { return Collections.EMPTY_LIST; }
    @Override public void setCloudletList(List<Cloudlet> cloudletList) {/**/}
    @Override public void setVmList(List<Vm> vmList) {/**/}
}
