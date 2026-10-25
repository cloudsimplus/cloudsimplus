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

import org.cloudsimplus.placementpolicies.VmPlacementPolicy;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSuitability;
import org.cloudsimplus.selectionpolicies.VmSelectionPolicy;
import org.cloudsimplus.vms.Vm;

import java.util.*;
import java.util.function.BiFunction;
import org.jspecify.annotations.Nullable;

/// A class that implements the Null Object Design Pattern for [VmPlacementPolicyMigration].
///
/// @author Manoel Campos da Silva Filho
/// @see VmPlacementPolicyMigration#NULL
final class VmPlacementPolicyMigrationNull implements VmPlacementPolicyMigration {
    @Override public Datacenter getDatacenter() { return Datacenter.NULL; }
    @Override public VmPlacementPolicy setDatacenter(Datacenter datacenter) { return this; }
    @Override public HostSuitability placeVm(Vm vm) { return HostSuitability.NULL; }
    @Override public Set<HostSuitability> placeVm(List<Vm> vmList) { return Collections.emptySet(); }
    @Override public HostSuitability placeVm(Vm vm, Host host) {
        return HostSuitability.NULL;
    }
    @Override public boolean scaleVmVertically(VerticalVmScaling scaling) {
        return false;
    }
    @Override public void removeVm(Vm vm) {/**/}
    @Override public Optional<Host> findHostForVm(Vm vm) { return Optional.empty(); }
    @Override public boolean isVmMigrationSupported() { return false; }
    @Override public int getHostCountForParallelSearch() { return 0; }
    @Override public VmPlacementPolicy setHostCountForParallelSearch(int hostCountForParallelSearch) { return this; }
    @Override public <T extends Host> List<T> getHostList() {
        return Collections.emptyList();
    }
    @Override public Map<Vm, Host> getOptimizedPlacementMap(List<? extends Vm> vmList) { return Collections.emptyMap(); }
    @Override public VmPlacementPolicy setFindHostForVmFunction(@Nullable BiFunction<VmPlacementPolicy, Vm, Optional<Host>> findHostForVmFunction) { return this; }
    @Override public boolean isUnderloaded() { return false; }
    @Override public boolean isUnderloaded(Host host) { return false; }
    @Override public boolean isOverloaded() { return false; }
    @Override public boolean isOverloaded(Host host) { return false; }
    @Override public double getOverUtilizationThreshold(Host host) { return 0; }
    @Override public VmPlacementPolicy setVmSelectionPolicy(VmSelectionPolicy vmSelectionPolicy) { return this; }
    @Override public VmSelectionPolicy getVmSelectionPolicy() {
        return VmSelectionPolicy.NULL;
    }
    @Override public double getUnderUtilizationThreshold() {
        return 0;
    }
    @Override public void setUnderUtilizationThreshold(double underUtilizationThreshold) {/**/}
}
