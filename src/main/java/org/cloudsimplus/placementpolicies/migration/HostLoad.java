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
import org.cloudsimplus.schedulers.vm.VmSchedulerAbstract;
import org.cloudsimplus.vms.Vm;

/// The change in the load of a [Host] planned during one VM migration check,
/// from the VMs planned to migrate into or out of it.
/// It only keeps totals, so updating and reading it takes constant time,
/// and the Host's real state never changes.
///
/// The CPU usage, requested and allocated MIPS of VMs planned to migrate out are subtracted,
/// so the Host is evaluated as if they had already left it.
/// But their RAM, BW, storage and cores aren't considered free for other VMs,
/// since they keep using them until their migration finishes.
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

    /// Number of VMs planned to migrate in.
    private int vmsIn;

    /// Total RAM, BW, storage, cores and MIPS requested by the VMs planned to migrate in.
    private long ramIn;
    private long bwIn;
    private long storageIn;
    private long coresIn;
    private double mipsIn;

    /// Adds a VM planned to migrate into the Host.
    void addVmIn(final Vm vm) {
        updateVmIn(vm, 1);
    }

    /// Removes a VM that was planned to migrate into the Host.
    void removeVmIn(final Vm vm) {
        updateVmIn(vm, -1);
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
    private void updateVmIn(final Vm vm, final int sign) {
        final var requestedMips = vm.getRequestedMips();
        updateCpu(vm, requestedMips.totalMips(), sign);
        vmsIn += sign;
        ramIn += sign * vm.getRam().getCapacity();
        bwIn += sign * vm.getBw().getCapacity();
        storageIn += sign * vm.getStorage().getCapacity();
        coresIn += sign * requestedMips.cores();
        mipsIn += sign * requestedMips.totalMips();
    }

    /// @param sign 1 to add the VM values, -1 to subtract them
    private void updateCpu(final Vm vm, final double vmAllocatedMips, final int sign) {
        cpuMipsUtilization += sign * vm.getTotalCpuMipsUtilization();
        cpuMipsRequested += sign * vm.getTotalCpuMipsRequested();
        allocatedMips += sign * vmAllocatedMips;
    }

    /// {@return true if the Host has enough resources for a VM, besides the VMs already planned to migrate into it}
    /// The Host's own suitability check is always performed.
    /// The cores are checked by the Host's VM scheduler, which knows how they are shared among VMs.
    boolean isSuitable(final Host host, final Vm vm) {
        if (!host.isSuitableForVm(vm)) {
            return false;
        }

        if (vmsIn == 0) {
            return true;
        }

        return host.getStorage().getFree() - storageIn >= vm.getStorage().getCapacity() &&
               host.getRam().getFree() - ramIn >= vm.getRam().getCapacity() &&
               host.getBw().getFree() - bwIn >= vm.getBw().getCapacity() &&
               host.getVmScheduler() instanceof VmSchedulerAbstract scheduler &&
               scheduler.isCapacityEnoughBesidesReserved(vm, coresIn, mipsIn);
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
