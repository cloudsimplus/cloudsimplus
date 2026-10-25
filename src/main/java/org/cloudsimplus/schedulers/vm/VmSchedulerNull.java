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
package org.cloudsimplus.schedulers.vm;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.vms.Vm;

/// A class that implements the Null Object Design Pattern for [VmScheduler] class.
///
/// @author Manoel Campos da Silva Filho
/// @see VmScheduler#NULL
final class VmSchedulerNull implements VmScheduler {
    @Override public boolean allocateCoresForVm(Vm vm, CoreCapacity requestedMips) {
        return false;
    }
    @Override public CoreCapacity getAllocatedMips(Vm vm) {
        return CoreCapacity.NULL;
    }
    @Override public double getTotalAvailableMips() {
        return 0.0;
    }
    @Override public double getTotalAllocatedMips() { return 0; }
    @Override public CoreCapacity getRequestedMips(Vm vm) { return CoreCapacity.NULL; }
    @Override public double getTotalAllocatedMipsForVm(Vm vm) {
        return 0.0;
    }
    @Override public double getMaxCpuUsagePercentDuringOutMigration() { return 0; }
    @Override public boolean isSuitableForVm(Vm vm) {
        return false;
    }
    @Override public boolean isSuitableForVm(Vm vm, CoreCapacity requestedMips) { return false; }
    @Override public double getVmMigrationCpuOverhead() { return 0.0; }
    @Override public Host getHost() {
        return Host.NULL;
    }
    @Override public VmScheduler setHost(Host host) {
        return this;
    }
    @Override public void deallocateCoresFromVm(Vm vm) {/**/}
    @Override public void updateHostBusyCores() {/**/}
    @Override public boolean resizeVmCores(Vm vm, long delta) { return false; }
}
