package org.cloudbus.cloudsim.gp.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.core.ChangeableId;
import org.cloudbus.cloudsim.core.Simulation;
import org.gpucloudsimplus.listeners.GpuUpdatesVgpusProcessingEventInfo;
import org.gpucloudsimplus.listeners.GpuEventInfo;
import org.cloudsimplus.listeners.EventListener;

import org.cloudbus.cloudsim.gp.provisioners.GpuResourceProvisioner;
import org.cloudbus.cloudsim.gp.core.GpuResourceStatsComputer;
import org.cloudbus.cloudsim.gp.schedulers.vgpu.VGpuScheduler;
import org.cloudbus.cloudsim.gp.videocards.Videocard;
import org.cloudbus.cloudsim.gp.resources.GpuCore;
import org.cloudbus.cloudsim.gp.core.AbstractGpu;
import org.cloudbus.cloudsim.gp.vgpu.VGpu;

public interface Gpu extends ChangeableId, Comparable<Gpu>, AbstractGpu, 
GpuResourceStatsComputer<GpuResourceStats> {
	//, ResourceManageable
    Logger LOGGER = LoggerFactory.getLogger(Gpu.class.getSimpleName());
	
    double DEF_IDLE_SHUTDOWN_DEADLINE = -1;

	Gpu NULL = new GpuNull ();
    
	List<GpuCore> getGpuCoreList ();
	
	GpuResourceProvisioner getGpuGddramProvisioner ();
	
	Gpu setGpuGddramProvisioner (GpuResourceProvisioner gpuGddramProvisioner);
	
	GpuResourceProvisioner getGpuBwProvisioner ();
	
	Gpu setGpuBwProvisioner (GpuResourceProvisioner gpuBwProvisioner);
	
	Videocard getVideocard ();
	
    void setVideocard (Videocard videocard);

    boolean isSuitableForVGpu (VGpu vgpu);

    GpuSuitability getSuitabilityFor (VGpu vgpu);

    boolean isActive();
    
    boolean hasEverStarted ();
    
    Gpu setActive (boolean activate);

    <T extends VGpu> Set<T> getVGpusMigratingIn ();

    boolean hasMigratingVGpus ();
    
    boolean addMigratingInVGpu (VGpu vgpu);

    Set<VGpu> getVGpusMigratingOut ();

    boolean addVGpuMigratingOut (VGpu vgpu);

    boolean removeVGpuMigratingOut (VGpu vgpu);

    void reallocateMigratingInVGpus ();

    @Override
    double getTotalMipsCapacity ();

    double getTotalAvailableMips ();

    double getTotalAllocatedMips ();

    double getTotalAllocatedMipsForVGpu (VGpu vgpu);

    void removeMigratingInVGpu (VGpu vgpu);

    List<GpuCore> getWorkingCoreList ();

    List<GpuCore> getBusyCoreList ();

    List<GpuCore> getFreeCoreList ();

    int getFreeCoresNumber ();

    int getWorkingCoresNumber ();

    int getBusyCoresNumber ();

    double getBusyCoresPercent ();

    double getBusyCoresPercent (boolean hundredScale);

    int getFailedCoresNumber ();

    //long getAvailableStorage();

    <T extends VGpu> List<T> getVGpuList ();

    <T extends VGpu> List<T> getVGpuCreatedList ();

    VGpuScheduler getVGpuScheduler ();

    Gpu setVGpuScheduler (VGpuScheduler vgpuScheduler);

    double getFirstStartTime ();

    double getShutdownTime ();

    void setShutdownTime (double shutdownTime);

    double getUpTime ();

    double getUpTimeHours ();

    double getTotalUpTime();
    
    double getTotalUpTimeHours ();

    double getIdleShutdownDeadline ();

    Gpu setIdleShutdownDeadline (double deadline);

    boolean isFailed ();

    boolean setFailed (boolean failed);

    double updateProcessing (double currentTime);

    GpuSuitability createVGpu (VGpu vgpu);

    void destroyVGpu (VGpu vgpu);

    GpuSuitability createTemporaryVGpu (VGpu vgpu);

    void destroyTemporaryVGpu (VGpu vgpu);

    void destroyAllVGpus ();

    Gpu addOnStartupListener(EventListener<GpuEventInfo> listener);

    boolean removeOnStartupListener (EventListener<GpuEventInfo> listener);

    Gpu addOnShutdownListener (EventListener<GpuEventInfo> listener);

    boolean removeOnShutdownListener (EventListener<GpuEventInfo> listener);

    Gpu addOnUpdateProcessingListener (EventListener<GpuUpdatesVgpusProcessingEventInfo> listener);

    boolean removeOnUpdateProcessingListener (EventListener<GpuUpdatesVgpusProcessingEventInfo> listener);

    //Gpu setSimulation(Simulation simulation);

    GpuResourceProvisioner getProvisioner (Class<? extends ResourceManageable> resourceClass);

    double getGpuCorePercentUtilization ();

    double getGpuCorePercentRequested ();

    //void enableUtilizationStats ();

    double getGpuCoreMipsUtilization ();

    long getBwUtilization ();

    long getGddramUtilization ();

    //PowerModelHost getPowerModel ();

    //void setPowerModel (PowerModelHost powerModel);

    void enableStateHistory ();

    void disableStateHistory ();

    boolean isStateHistoryEnabled ();

    List<GpuStateHistoryEntry> getStateHistory ();

    List<VGpu> getFinishedVGpus ();

    List<VGpu> getMigratableVGpus ();

    boolean isLazySuitabilityEvaluation ();

    void processActivation (boolean activate);
    
    Gpu setLazySuitabilityEvaluation (boolean lazySuitabilityEvaluation);
    
    Gpu setSimulation (Simulation simulation);
    
    //Simulation getSimulation ();
    
    //double getStartTime ();
    
    //Gpu setStartTime (final double startTime);
    
    //double getLastBusyTime ();
    
    //Resource getBw ();
    
    //Resource getGddram ();
    
    double getGpuPercentUtilization ();
    
    double getGpuPercentRequested ();
    
    //GpuResourceStats getGpuUtilizationStats ();
    
    double getGpuMipsUtilization ();
    
    default double getRelativeGpuUtilization (final VGpu vgpu) {
        return getExpectedRelativeGpuUtilization(vgpu, vgpu.getGpuPercentUtilization());
    }

    default double getExpectedRelativeGpuUtilization (final VGpu vgpu, 
    		final double vgpuGpuUtilizationPercent) {
        return vgpuGpuUtilizationPercent * getRelativeMipsCapacityPercent(vgpu);
    }
    
    default double getRelativeMipsCapacityPercent (final VGpu vgpu) {
        return vgpu.getTotalMipsCapacity() / getTotalMipsCapacity();
    }

    default double getRelativeGddramUtilization (final VGpu vgpu) {
        return vgpu.getGddram().getAllocatedResource() / (double)getGddram().getCapacity();
    }

    default double getRelativeBwUtilization(final VGpu vgpu){
        return vgpu.getBw().getAllocatedResource() / (double)getBw().getCapacity();
    }
    
}