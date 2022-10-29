package org.cloudbus.cloudsim.gp.vgpu;

import org.cloudbus.cloudsim.gp.vms.GpuVm;
import org.cloudbus.cloudsim.gp.resources.Gpu;
import org.cloudbus.cloudsim.gp.resources.VGpuCore;
import org.cloudbus.cloudsim.gp.videocards.Videocard;
import org.cloudbus.cloudsim.gp.cloudlets.gputasks.GpuTask;
import org.cloudbus.cloudsim.gp.schedulers.gputask.GpuTaskScheduler;

import org.gpucloudsimplus.listeners.VGpuGpuEventInfo;
import org.gpucloudsimplus.listeners.VGpuVideocardEventInfo;
import org.cloudsimplus.listeners.EventListener;

import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.resources.Resource;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.schedulers.MipsShare;

import java.util.*;

public class VGpuNull implements VGpu {
	
	@Override public void setId (long id) {/**/}
    @Override public long getId () {
        return -1;
    }
    @Override public void addStateHistoryEntry (VGpuStateHistoryEntry entry) {/**/}
    @Override public Resource getBw () {
        return Resource.NULL;
    }
    @Override public GpuTaskScheduler getGpuTaskScheduler () { return GpuTaskScheduler.NULL; }
    @Override public long getFreeCoresNumber () { return 0; }
    @Override public long getExpectedFreeCoresNumber () { return 0; }
    @Override public long getCurrentRequestedBw () {
        return 0;
    }
    @Override public MipsShare getCurrentRequestedMips () {
        return MipsShare.NULL;
    }
    @Override public long getCurrentRequestedGddram () {
        return 0;
    }
    @Override public double getTotalGpuMipsRequested () {
        return 0.0;
    }
    @Override public Gpu getGpu () {
        return Gpu.NULL;
    }
    @Override public double getMips () {
        return 0;
    }
    @Override public long getNumberOfCores () {
        return 0;
    }
    @Override public VGpu addOnGpuAllocationListener (
    		EventListener<VGpuGpuEventInfo> listener) {
        return this;
    }
    @Override public VGpu addOnMigrationStartListener (
    		EventListener<VGpuGpuEventInfo> listener) { return this; }
    @Override public VGpu addOnMigrationFinishListener (
    		EventListener<VGpuGpuEventInfo> listener) { return this; }
    @Override public VGpu addOnGpuDeallocationListener (
    		EventListener<VGpuGpuEventInfo> listener) { return this; }
    
    @Override public VGpu addOnUpdateProcessingListener (
    		EventListener<VGpuGpuEventInfo> listener) {
        return this;
    }
    @Override public void notifyOnGpuAllocationListeners () {/**/}
    @Override public void notifyOnGpuDeallocationListeners ( Gpu deallocatedGpu) {/**/}
    @Override public boolean removeOnMigrationStartListener (
    		EventListener<VGpuGpuEventInfo> listener) { return false; }
    @Override public boolean removeOnMigrationFinishListener (
    		EventListener<VGpuGpuEventInfo> listener) { return false; }
    @Override public boolean removeOnUpdateProcessingListener (
    		EventListener<VGpuGpuEventInfo> listener) { return false; 
    		}
    @Override public boolean removeOnGpuAllocationListener (
    		EventListener<VGpuGpuEventInfo> listener) { return false; }
    
    @Override public boolean removeOnGpuDeallocationListener (
    		EventListener<VGpuGpuEventInfo> listener) {
        return false;
    }
    @Override public Resource getGddram () {
        return Resource.NULL;
    }
    
    @Override public List<VGpuStateHistoryEntry> getStateHistory () {
        return Collections.emptyList();
    }
    @Override public double getCorePercentUtilization () { return 0; }
    @Override public double getCorePercentUtilization (double time) {
        return 0.0;
    }
    @Override public double getCorePercentRequested () { return 0; }
    @Override public double getCorePercentRequested (double time) { return 0; }
    @Override public double getGpuCoreUtilization (double time) { return 0; }
    @Override public double getExpectedGpuCoreUtilization (double vmCpuUtilizationPercent) { 
    	return 0; 
    }
    @Override public double getGpuGddramUtilization () { return 0; }
    @Override public double getGpuBwUtilization () { return 0; }
    @Override public double getTotalCoreMipsUtilization () { return 0; }
    @Override public double getTotalCoreMipsUtilization (double time) {
        return 0.0;
    }
    
    @Override public double getStopTime () { return 0; }
    @Override public double getTotalExecutionTime () { return 0; }
    @Override public VGpu setStopTime (double stopTime) { return this; }
    @Override public boolean isCreated () {
        return false;
    }
    @Override public boolean isSuitableForGpuTask (GpuTask gpuTask) { return false; }
    @Override public boolean isInMigration () {
        return false;
    }
    @Override public void setCreated (boolean created) {/**/}
    @Override public VGpu setBw (long bwCapacity) {
        return this;
    }
    @Override public VGpu setGpu (Gpu gpu) { return this; }
    @Override public void setInMigration (boolean migrating) {/**/}
    @Override public VGpu setGddram (long ram) {
        return this;
    }
    
    @Override public double updateGpuTaskProcessing (double currentTime, MipsShare mipsShare) { 
    	return 0.0; 
    }
    @Override public double updateGpuTaskProcessing (MipsShare mipsShare) { return 0; }
    @Override public VGpu setGpuTaskScheduler(GpuTaskScheduler gpuTaskScheduler) {
        return this;
    }
    
    @Override public void setFailed(boolean failed) {/**/}
    @Override public boolean isFailed() {
        return true;
    }
    @Override public boolean isWorking() { return false; }
    @Override public Simulation getSimulation() {
        return Simulation.NULL;
    }
    @Override public String toString() { return "Vm.NULL"; }
    
    @Override public VGpuCore getVGpuCore() { return VGpuCore.NULL; }
    @Override public String getDescription() { return ""; }
    @Override public VGpu setDescription (String description) { return this; }
    @Override public String getType () { return ""; }
    @Override public void setType (String type) { /**/ }
    @Override public GpuVm getGpuVm () { return GpuVm.NULL; } 
    @Override public VGpu setGpuVm (GpuVm gpuVm) { return this; } 
    @Override public int getPCIeBw () {
    	return -1;
    }
    @Override public void setPCIeBw (int PCIeBw) { /**/ } 
    @Override public String getTenancy () { return ""; } 
    @Override public void setTenancy (String tenancy) { /**/ }
	@Override public double getGpuPercentUtilization(double time) { return 0; }
	@Override public double getGpuPercentUtilization() { return 0; }
	@Override public double getTotalMipsCapacity () { return 0.0; }
	@Override public double getStartTime () { return 0; }
	@Override public double getLastBusyTime () { return 0; }
	@Override public List<ResourceManageable> getResources () { return Collections.emptyList(); }
	@Override public int compareTo (VGpu o) { return 0; }
	@Override public VGpuResourceStats getGpuUtilizationStats () {
		return new VGpuResourceStats(VGpu.NULL, vgpu -> 0.0); 
	}
	@Override public void enableUtilizationStats () { /**/ }
	@Override public double getTotalGpuMipsUtilization () { return 0.0; }
	@Override public double getTotalGpuMipsUtilization (double time) { return 0.0; }
	@Override public VGpu addOnCreationFailureListener (
			EventListener<VGpuVideocardEventInfo> listener) { return this; }
	@Override public void notifyOnCreationFailureListeners (Videocard failedVideocard) { /**/ }
	@Override public boolean removeOnCreationFailureListener (
			EventListener<VGpuVideocardEventInfo> listener) { return false; }
	@Override public VGpu setStartTime (double startTime) { return this; }
    
}

