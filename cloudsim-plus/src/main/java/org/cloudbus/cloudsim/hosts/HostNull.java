package org.cloudbus.cloudsim.hosts;

import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.datacenters.Datacenter;
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
    @Override public int compareTo(Host o) {
        return 0;
    }
    @Override public boolean addMigratingInVm(Vm vm) {
        return false;
    }
    @Override public boolean allocatePesForVm(Vm vm, List<Double> mipsShare) {
        return false;
    }
    @Override public void deallocatePesForVm(Vm vm) {/**/}
    @Override public List<Double> getAllocatedMipsForVm(Vm vm) {
        return Collections.emptyList();
    }
    @Override public double getAvailableMips() {
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
    @Override public int getId() {
        return -1;
    }
    @Override public double getMaxAvailableMips() {
        return 0.0;
    }
    @Override public int getNumberOfFreePes() {
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
        return 0.0;
    }
    @Override public Vm getVm(int vmId, int brokerId) {
        return Vm.NULL;
    }
    @Override public List<Vm> getVmList() {
        return Collections.emptyList();
    }
    @Override public VmScheduler getVmScheduler() {
        return VmScheduler.NULL;
    }
    @Override public Host setVmScheduler(VmScheduler vmScheduler) {
        return Host.NULL;
    }
    @Override public List<Vm> getVmsMigratingIn() {
        return Collections.EMPTY_LIST;
    }
    @Override public boolean isFailed() {
        return false;
    }
    @Override public boolean isSuitableForVm(Vm vm) {
        return false;
    }
    @Override public void reallocateMigratingInVms() {/**/}
    @Override public void removeMigratingInVm(Vm vm) {/**/}
    @Override public void setDatacenter(Datacenter datacenter) {/**/}
    @Override public boolean setPeStatus(int peId, Pe.Status status) {
        return false;
    }
    @Override public double updateProcessing(double currentTime) {
        return 0.0;
    }
    @Override public boolean vmCreate(Vm vm) {
        return false;
    }
    @Override public void destroyVm(Vm vm) {/**/}
    @Override public void destroyAllVms() {/**/}
    @Override public boolean removeOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> l) { return false; }
    @Override public Host addOnUpdateProcessingListener(EventListener<HostUpdatesVmsProcessingEventInfo> l) { return Host.NULL; }
    @Override public long getAvailableStorage() {
        return 0L;
    }
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
    @Override public long getNumberOfWorkingPes() {
        return 0;
    }
    @Override public String toString() {
        return "Host.NULL";
    }
    @Override public void setId(int id) {}
    @Override public double getTotalMipsCapacity() { return 0.0; }
    @Override public long getNumberOfFailedPes() { return 0; }
    @Override public List<Pe> getWorkingPeList() { return Collections.EMPTY_LIST; }
    @Override public double getUtilizationOfCpu() { return 0.0; }
    @Override public double getUtilizationOfCpuMips() { return 0.0; }
    @Override public long getUtilizationOfBw() { return 0; }
    @Override public long getUtilizationOfRam() { return 0; }
}
