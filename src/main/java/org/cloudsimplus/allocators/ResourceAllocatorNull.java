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
package org.cloudsimplus.allocators;

import org.cloudsimplus.resources.Resource;
import org.cloudsimplus.resources.ResourceManageable;
import org.cloudsimplus.vms.Vm;

import java.util.function.Function;

/// A class that implements the Null Object Design Pattern for [ResourceAllocator] class.
///
/// @author Manoel Campos da Silva Filho
/// @see ResourceAllocator#NULL
final class ResourceAllocatorNull extends ResourceAllocatorAbstract {
    @Override public boolean allocateResourceForVm(Vm vm, long newTotalVmResourceCapacity) { return false; }
    @Override public boolean allocateResourceForVm(Vm vm, double newTotalVmResource) { return false; }
    @Override public long deallocateResourceForVm(Vm vm) { return 0; }
    /// Does nothing, so that this shared object never keeps a reference to the resources of a Host.
    @Override public void setResources(ResourceManageable pmResource, Function<Vm, ResourceManageable> vmResourceFunction) {/**/}
    @Override public long getAllocatedResourceForVm(Vm vm) { return 0; }
    @Override public long getTotalAllocatedResource() { return 0; }
    @Override public boolean isSuitableForVm(Vm vm, long newVmTotalAllocatedResource) { return false; }
    @Override public boolean isSuitableForVm(Vm vm, Resource resource) { return false; }
    @Override public long getCapacity() { return 0; }
    @Override public long getFree() { return 0; }
}
