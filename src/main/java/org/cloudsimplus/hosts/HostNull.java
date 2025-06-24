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
package org.cloudsimplus.hosts;

import org.cloudsimplus.core.ExecDelayable;
import org.cloudsimplus.core.Machine;
import org.cloudsimplus.core.Simulation;
import org.cloudsimplus.core.Startable;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostEventInfo;
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;
import org.cloudsimplus.power.models.PowerModelHost;
import org.cloudsimplus.provisioners.ResourceProvisioner;
import org.cloudsimplus.resources.FileStorage;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.Resource;
import org.cloudsimplus.resources.ResourceManageable;
import org.cloudsimplus.schedulers.vm.VmScheduler;
import org.cloudsimplus.vms.HostResourceStats;
import org.cloudsimplus.vms.Vm;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A class that implements the Null Object Design Pattern for the {@link Host} class.
 *
 * @author Manoel Campos da Silva Filho
 * @see Host#NULL
 */
final class HostNull implements Host {
    @Override public int compareTo(Host host) { return 0; }
    @Override public boolean addMigratingInVm(Vm vm) {
        return false;
    }
    @Override public Set<Vm> getVmsMigratingOut() {
        return Collections.emptySet();
    }
    @Override public boolean addVmMigratingOut(Vm vm) {
        return false;
    }
    @Override public boolean removeVmMigratingOut(Vm vm) {
        return false;
    }
    @Override public double getTotalAvailableMips() {
        return 0;
    }
    @Override public double getTotalAllocatedMips() { return 0; }
    @Override public Resource getBw() {
        return Resource.NULL;
    }
    @Override public ResourceProvisioner getBwProvisioner() {
        return ResourceProvisioner.NULL;
    }
    @Override public Host setBwProvisioner(ResourceProvisioner bwProvisioner) {
        return NULL;
    }
    @Override public Datacenter getDatacenter() {
        return Datacenter.NULL;
    }
    @Override public long getId() {
        return -1;
    }
    @Override public int getFreePesNumber() {
        return 0;
    }
    @Override public long getPesNumber() {
        return 0;
    }
    @Override public double getMips() { return 0; }
    @Override public List<Pe> getPeList() {
        return Collections.emptyList();
    }
    @Override public Resource getRam() {
        return Resource.NULL;
    }
    @Override public ResourceProvisioner getRamProvisioner() { return ResourceProvisioner.NULL; }
    @Override public Host setRamProvisioner(ResourceProvisioner ramProvisioner) {
        return NULL;
    }
    @Override public FileStorage getStorage() {
        return FileStorage.NULL;
    }
    @Override public double getTotalAllocatedMipsForVm(Vm vm) {
        return 0.0;
    }
    @Override public <T extends Vm> List<T> getVmCreatedList() { return Collections.emptyList(); }
    @Override public List<Vm> getVmList() { return Collections.emptyList(); }
    @Override public VmScheduler getVmScheduler() {
        return VmScheduler.NULL;
    }
    @Override public Host setVmScheduler(VmScheduler vmScheduler) {
        return NULL;
    }
    @Override public double getStartTime() { return -1; }
    @Override public Machine setStartTime(double startTime) { return this; }
    @Override public double getFirstStartTime() { return -1; }
    @Override public double getFinishTime() { return 0; }
    @Override public Startable setFinishTime(double stopTime) { return this; }
    @Override public boolean isFailed() { return false; }
    @Override public boolean isSuitableForVm(Vm vm) { return false; }
    @Override public HostSuitability getSuitabilityFor(Vm vm) { return HostSuitability.NULL; }
    @Override public boolean isActive() { return false; }
    @Override public boolean isFinished() { return true; }
    @Override public boolean hasEverStarted() { return false; }
    @Override public Host setActive(boolean activate) { return this; }
    @Override public void shutdown() {/**/}
    @Override public <T extends Vm> Set<T> getVmsMigratingIn() {
        return Collections.emptySet();
    }
    @Override public boolean hasMigratingVms() { return false; }
    @Override public void reallocateMigratingInVms() {/**/}
    @Override public void removeMigratingInVm(Vm vm) {/**/}
    @Override public void setDatacenter(Datacenter datacenter) {/**/}
    @Override public double updateProcessing(double currentTime) {
        return 0.0;
    }
    @Override public HostSuitability createVm(Vm vm) {
        return HostSuitability.NULL;
    }
    @Override public HostSuitability createTemporaryVm(Vm vm) { return HostSuitability.NULL; }
    @Override public Host addOnStartupListener(EventListener<HostEventInfo> listener) { return this; }
    @Override public boolean removeOnStartupListener(EventListener<HostEventInfo> listener) { return false; }
    @Override public Host addOnShutdownListener(EventListener<HostEventInfo> listener) { return this; }
    @Override public boolean removeOnShutdownListener(EventListener<HostEventInfo> listener) { return false; }
    @Override public boolean removeOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> listener) { return false; }
    @Override public Host addOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> listener) { return NULL; }
    @Override public long getAvailableStorage() { return 0L; }
    @Override public boolean setFailed(boolean failed) { return false; }
    @Override public Simulation getSimulation() { return Simulation.NULL; }
    @Override public double getTotalExecutionTime() { return 0; }
    @Override public double getLastBusyTime() { return 0; }
    @Override public Startable setLastBusyTime(double time) { return this; }
    @Override public boolean isIdle() { return true; }

    @Override public double getStartupDelay() { return 0; }
    @Override public boolean isShuttingDown() { return false; }
    @Override  public double getShutdownBeginTime() { return -1; }
    @Override public ExecDelayable setShutdownBeginTime(double shutdownBeginTime) { return this; }
    @Override public ExecDelayable setStartupDelay(double delay) { return this; }
    @Override public double getShutDownDelay() { return 0; }
    @Override public ExecDelayable setShutDownDelay(double delay) { return this; }
    @Override public Host setSimulation(Simulation simulation) { return this; }
    @Override public ResourceProvisioner getProvisioner(Class<? extends ResourceManageable> clazz) { return ResourceProvisioner.NULL; }
    @Override public int getWorkingPesNumber() {
        return 0;
    }
    @Override public int getBusyPesNumber() { return 0; }
    @Override public double getBusyPesPercent(boolean hundredScale) { return 0; }
    @Override public double getBusyPesPercent() { return 0; }
    @Override public String toString() {
        return "Host.NULL";
    }
    @Override public Host setId(long id) { return this; }
    @Override public double getTotalMipsCapacity() { return 0.0; }
    @Override public int getFailedPesNumber() { return 0; }
    @Override public List<Pe> getWorkingPeList() { return Collections.emptyList(); }
    @Override public List<Pe> getBusyPeList() { return Collections.emptyList(); }
    @Override public List<Pe> getFreePeList() { return Collections.emptyList(); }
    @Override public double getCpuPercentUtilization() { return 0.0; }
    @Override public double getCpuPercentRequested() { return 0; }
    @Override public double getCpuMipsUtilization() { return 0.0; }
    @Override public long getBwUtilization() { return 0; }
    @Override public long getRamUtilization() { return 0; }
    @Override public HostResourceStats getCpuUtilizationStats() { return new HostResourceStats(this, host -> 0.0); }
    @Override public void enableUtilizationStats() {/**/}
    @Override public PowerModelHost getPowerModel() { return PowerModelHost.NULL; }
    @Override public Host setPowerModel(PowerModelHost powerModel) { return this; }
    @Override public Host setStateHistoryEnabled(boolean enable) { return this; }
    @Override public boolean isStateHistoryEnabled() { return false; }
    @Override public List<HostStateHistoryEntry> getStateHistory() { return Collections.emptyList(); }
    @Override public List<Vm> getFinishedVms() { return Collections.emptyList(); }
    @Override public List<Vm> getMigratableVms() { return Collections.emptyList(); }
    @Override public boolean isLazySuitabilityEvaluation() { return false; }
    @Override public Host setLazySuitabilityEvaluation(boolean lazySuitabilityEvaluation) { return this; }
    @Override public double getTotalUpTime() { return 0; }
    @Override public double getTotalUpTimeHours() { return 0; }
    @Override public double getUpTime() { return 0; }
    @Override public double getUpTimeHours() { return 0; }
    @Override public double getIdleShutdownDeadline() { return -1; }
    @Override public Host setIdleShutdownDeadline(double deadline) { return this; }
    @Override public List<ResourceManageable> getResources() {return Collections.emptyList(); }
}
