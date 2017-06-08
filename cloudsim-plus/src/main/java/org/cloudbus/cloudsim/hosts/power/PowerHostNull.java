package org.cloudbus.cloudsim.hosts.power;

import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostStateHistoryEntry;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.provisioners.ResourceProvisioner;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.Resource;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.HostUpdatesVmsProcessingEventInfo;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A class that implements the Null Object Design Pattern for {@link PowerHost}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see PowerHost#NULL
 */
final class PowerHostNull implements PowerHost {
    @Override public List<ResourceManageable> getResources() {
        return Collections.emptyList();
    }
    @Override public int compareTo(Host o) {
        return 0;
    }
    @Override public double getEnergyLinearInterpolation(double from, double to, double time) { return 0; }
    @Override public double getMaxPower() {
        return 0;
    }
    @Override public double getPower() {
        return 0;
    }
    @Override public PowerModel getPowerModel() {
        return PowerModel.NULL;
    }
    @Override public PowerHost setPowerModel(PowerModel powerModel) {
        return PowerHost.NULL;
    }
    @Override public void addStateHistoryEntry(double time, double amips, double rmips, boolean active) {/**/}
    @Override public List<Vm> getFinishedVms() {
        return Collections.EMPTY_LIST;
    }
    @Override public double getMaxUtilization() {
        return 0;
    }
    @Override public double getMaxUtilizationAmongVmsPes(Vm vm) {
        return 0;
    }
    @Override public double getPreviousUtilizationMips() {
        return 0;
    }
    @Override public double getPreviousUtilizationOfCpu() {
        return 0;
    }
    @Override public List<HostStateHistoryEntry> getStateHistory() {
        return Collections.EMPTY_LIST;
    }
    @Override public long getUtilizationOfBw() {
        return 0;
    }
    @Override public double getUtilizationOfCpu() {
        return 0;
    }
    @Override public double getUtilizationOfCpuMips() {
        return 0;
    }
    @Override public long getUtilizationOfRam() {
        return 0;
    }
    @Override public double updateProcessing(double currentTime) {
        return 0;
    }
    @Override public boolean addMigratingInVm(Vm vm) {
        return false;
    }
    @Override public boolean removeVmMigratingIn(Vm vm) {
        return false;
    }
    @Override public Set<Vm> getVmsMigratingOut() {
        return Collections.EMPTY_SET;
    }
    @Override public boolean addVmMigratingOut(Vm vm) {
        return false;
    }
    @Override public boolean removeVmMigratingOut(Vm vm) {
        return false;
    }
    @Override public boolean allocatePesForVm(Vm vm, List<Double> mipsShare) {
        return false;
    }
    @Override public void deallocatePesForVm(Vm vm) {/**/}
    @Override public List<Double> getAllocatedMipsForVm(Vm vm) {
        return Collections.EMPTY_LIST;
    }
    @Override public double getAvailableMips() {
        return 0;
    }
    @Override public long getAvailableStorage() {
        return 0;
    }
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
    @Override public double getMaxAvailableMips() {
        return 0;
    }
    @Override public int getNumberOfFreePes() {
        return 0;
    }
    @Override public long getNumberOfPes() {
        return 0;
    }
    @Override public double getMips() { return 0; }
    @Override public List<Pe> getPeList() {
        return Collections.EMPTY_LIST;
    }
    @Override public Resource getRam() {
        return Resource.NULL;
    }
    @Override public ResourceProvisioner getRamProvisioner() {
        return ResourceProvisioner.NULL;
    }
    @Override public Host setRamProvisioner(ResourceProvisioner ramProvisioner) {
        return Host.NULL;
    }
    @Override public Resource getStorage() {
        return Resource.NULL;
    }
    @Override public double getTotalAllocatedMipsForVm(Vm vm) {
        return 0;
    }
    @Override public Vm getVm(int vmId, int brokerId) {
        return Vm.NULL;
    }
    @Override public <T extends Vm> List<T> getVmList() {
        return Collections.EMPTY_LIST;
    }
    @Override public <T extends Vm> List<T> getVmCreatedList() { return Collections.EMPTY_LIST; }
    @Override public VmScheduler getVmScheduler() {
        return VmScheduler.NULL;
    }
    @Override public Host setVmScheduler(VmScheduler vmScheduler) {
        return Host.NULL;
    }
    @Override public <T extends Vm> Set<T> getVmsMigratingIn() {
        return Collections.EMPTY_SET;
    }
    @Override public boolean isFailed() {
        return false;
    }
    @Override public boolean isSuitableForVm(Vm vm) {
        return false;
    }
    @Override public boolean isActive() { return false; }
    @Override public Host setActive(boolean active) { return this; }
    @Override public void reallocateMigratingInVms() {/**/}
    @Override public void removeMigratingInVm(Vm vm) {/**/}
    @Override public void setDatacenter(Datacenter datacenter) {/**/}
    @Override public boolean setFailed(boolean failed) {
        return false;
    }
    @Override public Simulation getSimulation() {
        return Simulation.NULL;
    }
    @Override public Host setSimulation(Simulation simulation) {
        return this;
    }
    @Override public ResourceProvisioner getProvisioner(Class<? extends ResourceManageable> c) { return ResourceProvisioner.NULL; }
    @Override public boolean setPeStatus(int peId, Pe.Status status) {
        return false;
    }
    @Override public boolean createVm(Vm vm) {
        return false;
    }
    @Override public void destroyVm(Vm vm) {/**/}
    @Override public boolean createTemporaryVm(Vm vm) { return false; }
    @Override public void destroyTemporaryVm(Vm vm) {}
    @Override public void destroyAllVms() {/**/}
    @Override public boolean removeOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> l) { return false; }
    @Override public Host addOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> l) { return Host.NULL; }
    @Override public int getId() {
        return 0;
    }
    @Override public long getNumberOfWorkingPes() {
        return 0L;
    }
    @Override public void setId(int id) {}
    @Override public double getTotalMipsCapacity() { return 0.0; }
    @Override public long getNumberOfFailedPes() { return 0; }
    @Override public List<Pe> getWorkingPeList() { return Collections.EMPTY_LIST; }
}
