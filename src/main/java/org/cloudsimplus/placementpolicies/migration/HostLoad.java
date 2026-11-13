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
package org.cloudsimplus.placementpolicies.migration;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.vms.Vm;

/// The change in the load of a [Host] planned during one VM migration check,
/// from the VMs planned to migrate into or out of it.
/// It only keeps totals, so updating and reading it takes constant time,
/// and the Host's real state never changes.
///
/// Resources of VMs planned to migrate out aren't considered free,
/// since such VMs keep using them until their migration finishes.
///
/// @author Manoel Campos da Silva Filho
/// @see MigrationPlan
/// @since CloudSim Plus 9.0.0
final class HostLoad {
    /// Change in the MIPS used by the Host's VMs.
    private double cpuMipsUtilization;

    /// Change in the MIPS requested by the Host's VMs.
    private double cpuMipsRequested;

    /// Change in the MIPS the Host's VM scheduler allocates to its VMs.
    private double allocatedMips;

    /// Total RAM, BW, storage and cores of the VMs planned to migrate in.
    private long ramIn;
    private long bwIn;
    private long storageIn;
    private long coresIn;

    /// The highest MIPS per core requested by a VM planned to migrate in.
    /// It isn't reduced when a VM is removed from the plan, so suitability checks stay on the safe side.
    private double maxCoreMipsIn;

    /// Adds a VM planned to migrate into the Host.
    void addVmIn(final Vm vm) {
        final var requestedMips = vm.getRequestedMips();
        updateCpu(vm, requestedMips.totalMips(), 1);
        updateResourcesIn(vm, requestedMips.cores(), 1);
        maxCoreMipsIn = Math.max(maxCoreMipsIn, requestedMips.mips());
    }

    /// Removes a VM that was planned to migrate into the Host.
    void removeVmIn(final Vm vm) {
        final var requestedMips = vm.getRequestedMips();
        updateCpu(vm, requestedMips.totalMips(), -1);
        updateResourcesIn(vm, requestedMips.cores(), -1);
    }

    /// Adds a VM planned to migrate out of the Host.
    /// @param host the Host the VM is migrating out of
    void addVmOut(final Host host, final Vm vm) {
        updateCpu(vm, host.getVmScheduler().getTotalAllocatedMipsForVm(vm), -1);
    }

    /// Removes a VM that was planned to migrate out of the Host.
    /// @param host the Host the VM was planned to migrate out of
    void removeVmOut(final Host host, final Vm vm) {
        updateCpu(vm, host.getVmScheduler().getTotalAllocatedMipsForVm(vm), 1);
    }

    /// @param sign 1 to add the VM values, -1 to subtract them
    private void updateCpu(final Vm vm, final double vmAllocatedMips, final int sign) {
        cpuMipsUtilization += sign * vm.getTotalCpuMipsUtilization();
        cpuMipsRequested += sign * vm.getTotalCpuMipsRequested();
        allocatedMips += sign * vmAllocatedMips;
    }

    /// @param sign 1 to add the VM values, -1 to subtract them
    private void updateResourcesIn(final Vm vm, final long cores, final int sign) {
        ramIn += sign * vm.getRam().getCapacity();
        bwIn += sign * vm.getBw().getCapacity();
        storageIn += sign * vm.getStorage().getCapacity();
        coresIn += sign * cores;
    }

    /// {@return true if the Host has enough resources for a VM, besides the VMs already planned to migrate into it}
    /// The cores are checked by the Host's VM scheduler, as if a single VM requested
    /// the cores of all those VMs, with the highest MIPS per core among them.
    boolean isSuitable(final Host host, final Vm vm) {
        if (coresIn == 0) {
            return host.isSuitableForVm(vm);
        }

        final var requestedMips = vm.getRequestedMips();
        final var totalRequest = new CoreCapacity(coresIn + requestedMips.cores(), Math.max(maxCoreMipsIn, requestedMips.mips()));
        return host.getStorage().getFree() - storageIn >= vm.getStorage().getCapacity() &&
               host.getRam().getFree() - ramIn >= vm.getRam().getCapacity() &&
               host.getBw().getFree() - bwIn >= vm.getBw().getCapacity() &&
               host.getVmScheduler().isSuitableForVm(vm, totalRequest);
    }

    double cpuMipsUtilization() {
        return cpuMipsUtilization;
    }

    double cpuMipsRequested() {
        return cpuMipsRequested;
    }

    double allocatedMips() {
        return allocatedMips;
    }
}
