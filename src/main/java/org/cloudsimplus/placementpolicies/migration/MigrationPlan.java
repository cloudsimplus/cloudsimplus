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
import org.cloudsimplus.vms.Vm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.cloudsimplus.resources.Processor.totalMips;

/// The VM migrations planned during one migration check
/// ([VmPlacementPolicyMigrationAbstract#getOptimizedPlacementMap(java.util.List)]).
/// Hosts are evaluated as if the planned migrations were already done,
/// without changing the real placement of VMs.
///
/// A plan is created for each check and discarded at its end.
/// It only has entries for the Hosts and VMs the check touches,
/// so its size is proportional to the number of planned migrations.
///
/// @author Manoel Campos da Silva Filho
/// @see HostLoad
/// @since CloudSim Plus 9.0.0
final class MigrationPlan {
    /// A plan with no migration, used outside a migration check. It can't be changed.
    static final MigrationPlan NONE = new MigrationPlan(Map.of(), Set.of());

    private final Map<Host, HostLoad> loads;
    private final Set<Vm> vmsOut;

    MigrationPlan() {
        this(new HashMap<>(), new HashSet<>());
    }

    private MigrationPlan(final Map<Host, HostLoad> loads, final Set<Vm> vmsOut) {
        this.loads = loads;
        this.vmsOut = vmsOut;
    }

    /// Plans a VM to migrate out of its Host, before knowing its target Host.
    void addVmOut(final Vm vm) {
        if (vmsOut.add(vm)) {
            load(vm.getHost()).addVmOut(vm.getHost(), vm);
        }
    }

    /// Cancels the migration of a VM out of its Host.
    void removeVmOut(final Vm vm) {
        if (vmsOut.remove(vm)) {
            load(vm.getHost()).removeVmOut(vm.getHost(), vm);
        }
    }

    /// Plans a VM to migrate from its Host to a target Host.
    void addMigration(final Vm vm, final Host targetHost) {
        addVmOut(vm);
        load(targetHost).addVmIn(vm);
    }

    /// Cancels the migration of a VM from its Host to a target Host.
    void removeMigration(final Vm vm, final Host targetHost) {
        load(targetHost).removeVmIn(vm);
        removeVmOut(vm);
    }

    boolean isMigratingOut(final Vm vm) {
        return vmsOut.contains(vm);
    }

    /// {@return the MIPS a Host's VMs will use after the planned migrations}
    double cpuMipsUtilization(final Host host) {
        final var load = loads.get(host);
        return host.getCpuMipsUtilization() + (load == null ? 0 : load.cpuMipsUtilization());
    }

    /// {@return the CPU utilization percentage of a Host after the planned migrations}
    /// It adds the planned change to the Host's current percentage, keeping how the Host rounds it.
    double cpuPercentUtilization(final Host host) {
        final var load = loads.get(host);
        return host.getCpuPercentUtilization() + (load == null ? 0 : load.cpuMipsUtilization() / totalMips(host));
    }

    /// {@return the change in the MIPS a Host's VMs request, due to the planned migrations}
    double cpuMipsRequestedDelta(final Host host) {
        final var load = loads.get(host);
        return load == null ? 0 : load.cpuMipsRequested();
    }

    /// {@return the change in the MIPS a Host's VM scheduler allocates, due to the planned migrations}
    double allocatedMipsDelta(final Host host) {
        final var load = loads.get(host);
        return load == null ? 0 : load.allocatedMips();
    }

    /// {@return the current power consumption of a Host (in Watts), considering the planned migrations}
    double power(final Host host) {
        final var powerModel = host.getPowerModel();
        if (!loads.containsKey(host) || !host.isActive()) {
            return powerModel.getPower();
        }

        // Rounding errors in the planned totals must not take the fraction out of the range the power model accepts
        return powerModel.getPower(Math.clamp(cpuMipsUtilization(host) / totalMips(host), 0.0, 1.0));
    }

    /// {@return true if a Host has enough resources for a VM, besides the VMs already planned to migrate into it}
    boolean isSuitable(final Host host, final Vm vm) {
        final var load = loads.get(host);
        return load == null ? host.isSuitableForVm(vm) : load.isSuitable(host, vm);
    }

    private HostLoad load(final Host host) {
        return loads.computeIfAbsent(host, h -> new HostLoad());
    }
}
