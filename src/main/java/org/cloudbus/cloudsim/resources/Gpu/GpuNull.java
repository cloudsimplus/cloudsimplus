package org.cloudbus.cloudsim.gp.resources;

import org.cloudbus.cloudsim.gp.vgpu.VGpu;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.gp.core.AbstractGpu;
import org.cloudbus.cloudsim.gp.provisioners.GpuResourceProvisioner;
import org.cloudbus.cloudsim.gp.schedulers.vgpu.VGpuScheduler;
import org.cloudbus.cloudsim.gp.videocards.Videocard;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.Resource;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudsimplus.listeners.EventListener;
import org.gpucloudsimplus.listeners.GpuEventInfo;
import org.gpucloudsimplus.listeners.GpuUpdatesVgpusProcessingEventInfo;

import java.util.List;
import java.util.Set;
import java.util.Collections;


final class GpuNull implements Gpu {
	
	@Override public void setId (long id) {/**/}
	@Override public long getId () {
        return -1;
    }
	
	@Override public List<GpuCore> getGpuCoreList () {
        return Collections.emptyList ();
    }
	
	@Override public Gpu setGpuGddramProvisioner (GpuResourceProvisioner gpuGddramProvisioner) {
        return Gpu.NULL;
	}
	
	@Override public GpuResourceProvisioner getGpuGddramProvisioner () {
        return GpuResourceProvisioner.NULL;
    }
        
    @Override public Gpu setGpuBwProvisioner (GpuResourceProvisioner gpuBwProvisioner) {
    	return Gpu.NULL;
    }
    
    @Override public GpuResourceProvisioner getGpuBwProvisioner () {
        return GpuResourceProvisioner.NULL;
    }
	@Override public Videocard getVideocard () { return Videocard.NULL; }
	@Override public void setVideocard (Videocard videocard) { /**/ }
	@Override public boolean isSuitableForVGpu (VGpu vgpu) { return false; }
	@Override public GpuSuitability getSuitabilityFor(VGpu vgpu) { return GpuSuitability.NULL; }
	@Override public boolean isActive () { return false; }
	@Override public boolean hasEverStarted () { return false; }
	@Override public Gpu setActive (boolean activate) { return this; }
	@Override public <T extends VGpu> Set<T> getVGpusMigratingIn () { 
		return Collections.emptySet();
	}
	@Override public boolean hasMigratingVGpus () { return false; }
	@Override public boolean addMigratingInVGpu (VGpu vgpu) { return false; }
	@Override public Set<VGpu> getVGpusMigratingOut () { return Collections.emptySet(); }
	@Override public boolean addVGpuMigratingOut (VGpu vgpu) { return false; }
	@Override public boolean removeVGpuMigratingOut (VGpu vgpu) { return false; }
	@Override public void reallocateMigratingInVGpus () { /**/ }
	@Override public double getTotalMipsCapacity () { return 0.0; }
	@Override public double getTotalAvailableMips () { return 0.0; }
	@Override public double getTotalAllocatedMips () { return 0.0; }
	@Override public double getTotalAllocatedMipsForVGpu (VGpu vgpu) { return 0.0; }
	@Override public void removeMigratingInVGpu (VGpu vgpu) { /**/ }
	@Override public List<GpuCore> getWorkingCoreList () { return Collections.emptyList(); }
	@Override public List<GpuCore> getBusyCoreList () { return Collections.emptyList(); }
	@Override public List<GpuCore> getFreeCoreList () { return Collections.emptyList(); }
	@Override public int getFreeCoresNumber () { return 0; }
	@Override public int getWorkingCoresNumber () { return 0; }
	@Override public int getBusyCoresNumber () { return 0; }
	@Override public double getBusyCoresPercent () { return 0.0; }
	@Override public double getBusyCoresPercent (boolean hundredScale) { return 0.0; }
	@Override public int getFailedCoresNumber () { return 0; }
	@Override public <T extends VGpu> List<T> getVGpuList () { return Collections.emptyList(); }
	@Override public <T extends VGpu> List<T> getVGpuCreatedList () {
		return Collections.emptyList();
	}
	@Override public VGpuScheduler getVGpuScheduler () { return VGpuScheduler.NULL; }
	@Override public Gpu setVGpuScheduler (VGpuScheduler vgpuScheduler) { return this; }
	@Override public double getFirstStartTime () { return 0.0; }
	@Override
	public double getShutdownTime () { return 0.0; }
	@Override public void setShutdownTime (double shutdownTime) { /**/ }
	@Override public double getIdleShutdownDeadline () { return 0.0; }
	@Override public Gpu setIdleShutdownDeadline (double deadline) { return this; }
	@Override public boolean isFailed () { return false; }
	@Override public boolean setFailed (boolean failed) { return false; }
	@Override public double updateProcessing (double currentTime) { return 0.0; }
	@Override public GpuSuitability createVGpu (VGpu vgpu) { return GpuSuitability.NULL; }
	@Override public void destroyVGpu (VGpu vgpu) { /**/ }
	@Override public GpuSuitability createTemporaryVGpu (VGpu vgpu) { return GpuSuitability.NULL;
	}
	@Override public void destroyTemporaryVGpu (VGpu vgpu) { /**/ }
	@Override public void destroyAllVGpus () { /**/ }
	@Override public Gpu addOnStartupListener (EventListener<GpuEventInfo> listener) { 
		return this;
	}
	@Override public boolean removeOnStartupListener (EventListener<GpuEventInfo> listener) { 
		return false;
	}
	@Override public Gpu addOnShutdownListener (EventListener<GpuEventInfo> listener) { 
		return Gpu.NULL;
	}
	@Override public boolean removeOnShutdownListener (EventListener<GpuEventInfo> listener) {
		return false;
	}
	@Override public Gpu addOnUpdateProcessingListener (
			EventListener<GpuUpdatesVgpusProcessingEventInfo> listener) { return Gpu.NULL; }
	@Override public boolean removeOnUpdateProcessingListener (
			EventListener<GpuUpdatesVgpusProcessingEventInfo> listener) { return false; }
	@Override public GpuResourceProvisioner getProvisioner (
			Class<? extends ResourceManageable> resourceClass) { return GpuResourceProvisioner.NULL; }
	@Override public double getGpuCorePercentUtilization () { return 0.0; }
	@Override public double getGpuCorePercentRequested () { return 0.0; }
	@Override public void enableUtilizationStats () { /**/ }
	@Override public double getGpuCoreMipsUtilization () { return 0.0; }
	@Override public long getBwUtilization () { return 0; }
	@Override public long getGddramUtilization () { return 0; }
	@Override public void enableStateHistory () { /**/ }
	@Override public void disableStateHistory () { /**/ }
	@Override public boolean isStateHistoryEnabled () { return false; }
	@Override public List<GpuStateHistoryEntry> getStateHistory () { return Collections.emptyList(); }
	@Override public List<VGpu> getFinishedVGpus () { return Collections.emptyList(); }
	@Override public List<VGpu> getMigratableVGpus () { return Collections.emptyList(); }
	@Override public boolean isLazySuitabilityEvaluation () { return false; }
	@Override public Gpu setLazySuitabilityEvaluation (boolean lazySuitabilityEvaluation) { 
		return this;
	}
	@Override public int compareTo (Gpu gpu) { return 0; }
	@Override public Resource getBw () { return Resource.NULL; }
	@Override public Resource getGddram () { return Resource.NULL; }
	@Override public long getNumberOfCores () { return 0; }
	@Override public double getMips () { return 0.0; }
	@Override public Simulation getSimulation () { return Simulation.NULL; }
	@Override public double getStartTime () { return 0.0; }
	@Override public AbstractGpu setStartTime (double startTime) { return this; }
	@Override public double getLastBusyTime () { return 0.0; }
	@Override public List<ResourceManageable> getResources () { 
		return Collections.emptyList();
	}
	@Override
	public GpuResourceStats getGpuUtilizationStats () { 
		return new GpuResourceStats(this, host -> 0.0); 
	}
	@Override
	public double getUpTime () { return 0.0; }
	@Override public double getUpTimeHours () { return 0.0; }
	@Override public double getTotalUpTime () { return 0.0; }
	@Override public double getTotalUpTimeHours () { return 0.0; }
	@Override public double getGpuPercentUtilization () { return 0.0; }
	@Override public double getGpuPercentRequested () { return 0.0; }
	@Override public double getGpuMipsUtilization () { return 0.0; }
	@Override public void processActivation (boolean activate) { /**/ }
	@Override public Gpu setSimulation (Simulation simulation) { return this; }
}
