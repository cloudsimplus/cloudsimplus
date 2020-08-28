package org.cloudbus.cloudsim.hosts;

import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.Resource;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostEventInfo;
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;

import java.util.*;

/**
 * A class that implements the Null Object Design Pattern for {@link Host}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see Host#NULL
 */
final class HostNull implements Host {
    @Override public List<ResourceManageable> getResources() {
        return Collections.emptyList();
    }
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
        return Host.NULL;
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
    @Override public long getNumberOfPes() {
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
        return Host.NULL;
    }
    @Override public Resource getStorage() {
        return Resource.NULL;
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
        return Host.NULL;
    }
    @Override public double getStartTime() { return -1; }
    @Override public double getFirstStartTime() { return -1; }
    @Override public void setStartTime(double startTime) {/**/}
    @Override public double getShutdownTime() { return 0; }
    @Override public boolean isFailed() {
        return false;
    }
    @Override public boolean isSuitableForVm(Vm vm) {
        return false;
    }
    @Override public boolean isActive() { return false; }
    @Override public boolean hasEverStarted() { return false; }
    @Override public Host setActive(boolean activate) { return this; }
    @Override public <T extends Vm> Set<T> getVmsMigratingIn() {
        return Collections.emptySet();
    }
    @Override public void reallocateMigratingInVms() {/**/}
    @Override public void removeMigratingInVm(Vm vm) {/**/}
    @Override public void setDatacenter(Datacenter datacenter) {/**/}
    @Override public double updateProcessing(double currentTime) {
        return 0.0;
    }
    @Override public boolean createVm(Vm vm) {
        return false;
    }
    @Override public boolean createTemporaryVm(Vm vm) { return false; }
    @Override public void destroyTemporaryVm(Vm vm) {/**/}
    @Override public void destroyVm(Vm vm) {/**/}
    @Override public void destroyAllVms() {/**/}
    @Override public Host addOnStartupListener(EventListener<HostEventInfo> listener) { return this; }
    @Override public boolean removeOnStartupListener(EventListener<HostEventInfo> listener) { return false; }
    @Override public Host addOnShutdownListener(EventListener<HostEventInfo> listener) { return this; }
    @Override public boolean removeOnShutdownListener(EventListener<HostEventInfo> listener) { return false; }
    @Override public boolean removeOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> listener) { return false; }
    @Override public Host addOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> listener) { return Host.NULL; }
    @Override public long getAvailableStorage() {
        return 0L;
    }
    @Override public boolean setFailed(boolean failed) {
        return false;
    }
    @Override public Simulation getSimulation() {
        return Simulation.NULL;
    }
    @Override public double getLastBusyTime() { return 0; }
    @Override public boolean isIdle() { return true; }
    @Override public Host setSimulation(Simulation simulation) {
        return this;
    }
    @Override public ResourceProvisioner getProvisioner(Class<? extends ResourceManageable> clazz) { return ResourceProvisioner.NULL; }
    @Override public int getWorkingPesNumber() {
        return 0;
    }
    @Override public String toString() {
        return "Host.NULL";
    }
    @Override public void setId(long id) {/**/}
    @Override public double getTotalMipsCapacity() { return 0.0; }
    @Override public int getFailedPesNumber() { return 0; }
    @Override public List<Pe> getWorkingPeList() { return Collections.emptyList(); }
    @Override public List<Pe> getBusyPeList() { return Collections.emptyList(); }
    @Override public List<Pe> getFreePeList() { return Collections.emptyList(); }
    @Override public double getCpuPercentUtilization() { return 0.0; }
    @Override public double getCpuMipsUtilization() { return 0.0; }
    @Override public long getBwUtilization() { return 0; }
    @Override public long getRamUtilization() { return 0; }
    @Override public SortedMap<Double, DoubleSummaryStatistics> getUtilizationHistory() { return Collections.emptySortedMap(); }
    @Override public SortedMap<Double, Double> getUtilizationHistorySum() { return Collections.emptySortedMap(); }
    @Override public PowerModel getPowerModel() { return PowerModel.NULL; }
    @Override public Host setPowerModel(PowerModel powerModel) { return this; }
    @Override public double getPreviousUtilizationOfCpu() { return 0; }
    @Override public void enableStateHistory() {/**/}
    @Override public void disableStateHistory() {/**/}
    @Override public boolean isStateHistoryEnabled() { return false; }
    @Override public List<HostStateHistoryEntry> getStateHistory() { return Collections.emptyList(); }
    @Override public List<Vm> getFinishedVms() { return Collections.emptyList(); }
    @Override public List<Vm> getMigratableVms() { return Collections.emptyList(); }
    @Override public double getTotalUpTime() { return 0; }
    @Override public double getTotalUpTimeHours() { return 0; }
    @Override public void setShutdownTime(double shutdownTime) {/**/}
    @Override public double getUpTime() { return 0; }
    @Override public double getUpTimeHours() { return 0; }
    @Override public double getIdleShutdownDeadline() { return -1; }
    @Override public Host setIdleShutdownDeadline(double deadline) { return this; }
}
