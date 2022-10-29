package org.cloudbus.cloudsim.gp.cloudlets.gputasks;

import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.gpucloudsimplus.listeners.GpuTaskVGpuEventInfo;
import org.cloudsimplus.listeners.EventListener;

import java.util.List;
import java.util.Collections;

import org.cloudbus.cloudsim.core.Simulation;

import org.cloudbus.cloudsim.gp.cloudlets.GpuCloudlet;
import org.cloudbus.cloudsim.gp.vgpu.VGpu;
import org.cloudbus.cloudsim.resources.ResourceManageable;

final class GpuTaskNull implements GpuTask {
	
	@Override public void setTaskId (long id) {/**/}
    @Override public long getTaskId () {
        return -1;
    }
    
    /*@Override public boolean addRequiredFile (String fileName) {
        return false;
    }
    
    @Override public boolean addRequiredFiles (List<String> fileNames) {
        return false;
    }
    
    @Override public boolean deleteRequiredFile (String filename) {
        return false;
    }*/
    
    @Override public double getArrivalTime () { return -1; }
    
    @Override public double getActualGpuTime () {
        return 0.0;
    }
    @Override public int getPriority () {
        return 0;
    }
    @Override public long getFileSize () {
        return 0L;
    }
    @Override public long getFinishedLengthSoFar () {
        return 0L;
    }
    @Override public long getBlockLength () {
        return 0L;
    }
    @Override public long getOutputSize () {
        return 0L;
    }
    @Override public long getGpuTaskTotalLength () {
        return 0L;
    }
    @Override public double getExecStartTime () {
        return 0.0;
    }
    @Override public double getFinishTime () {
        return 0.0;
    }
    //@Override public int getNetServiceLevel () {
    //    return 0;
    //}
    @Override public long getNumberOfCores () {
        return 0;
    }
    /*@Override public List<String> getRequiredFiles() {
        return Collections.emptyList ();
    }*/
    @Override public Status getStatus () {
        return Status.FAILED;
    }
    //@Override public boolean isReturnedToBroker() { return false; }
    @Override public UtilizationModel getUtilizationModelBw () {
        return UtilizationModel.NULL;
    }
    @Override public UtilizationModel getUtilizationModelGpu () {
        return UtilizationModel.NULL;
    }
    @Override public UtilizationModel getUtilizationModelGddram () {
        return UtilizationModel.NULL;
    }
    //@Override public UtilizationModel getUtilizationModel(Class<? extends ResourceManageable> resourceClass) { return UtilizationModel.NULL; }
    @Override public double getUtilizationOfBw () {
        return 0;
    }
    @Override public double getUtilizationOfBw (double time) {
        return 0.0;
    }
    @Override public double getUtilizationOfGpu () {
        return 0;
    }
    @Override public double getUtilizationOfGpu (double time) {
        return 0.0;
    }
    @Override public double getUtilizationOfGddram () {
        return 0;
    }
    @Override public double getUtilizationOfGddram (double time) {
        return 0.0;
    }
    @Override public GpuCloudlet getGpuCloudlet () {
        return GpuCloudlet.NULL;
    }
    @Override public double getWaitingTime() {
        return 0.0;
    }
    @Override public boolean isFinished() {
        return false;
    }
    /*@Override public boolean hasRequiresFiles() {
        return false;
    }*/
    @Override public GpuTask setPriority(int priority) { return this; }
    @Override public GpuTask setBlockLength(long length) {
        return GpuTask.NULL;
    }
    @Override public GpuTask setFileSize(long fileSize) {
        return GpuTask.NULL;
    }
    @Override public GpuTask setOutputSize(long outputSize) {
        return GpuTask.NULL;
    }
    @Override public GpuTask setSizes(long size) { return this; }
    @Override public boolean setStatus(Status newStatus) {
        return false;
    }
    //@Override public void setNetServiceLevel(int netServiceLevel) {/**/}
    @Override public GpuTask setNumberOfCores(long numberOfPes) {
        return GpuTask.NULL;
    }
    //@Override public void setBroker(DatacenterBroker broker) {/**/}
    //@Override public DatacenterBroker getBroker() {
    //    return DatacenterBroker.NULL;
    //}
    @Override public GpuTask setUtilizationModel (UtilizationModel utilizationModel) {
        return GpuTask.NULL;
    }
    @Override public GpuTask setUtilizationModelBw (UtilizationModel utilizationModelBw) {
        return GpuTask.NULL;
    }
    @Override public GpuTask setUtilizationModelGpu (UtilizationModel utilizationModelCpu) {
        return GpuTask.NULL;
    }
    @Override public GpuTask setUtilizationModelGddram (UtilizationModel utilizationModelRam) {
        return GpuTask.NULL;
    }
    @Override public void setGpuCloudlet (GpuCloudlet GpuCloudlet) {/**/}
    @Override public boolean removeOnFinishListener (EventListener<GpuTaskVGpuEventInfo> listener) { 
    	return false; }
    @Override public GpuTask addOnFinishListener (EventListener<GpuTaskVGpuEventInfo> listener) { 
    	return GpuTask.NULL; }
    @Override public void notifyOnUpdateProcessingListeners(double time) {/**/}
    @Override public Simulation getSimulation() {
        return Simulation.NULL;
    }
    @Override public boolean removeOnUpdateProcessingListener (
    		EventListener<GpuTaskVGpuEventInfo> listener) { return false; }
    @Override public GpuTask addOnUpdateProcessingListener (
    		EventListener<GpuTaskVGpuEventInfo> listener) { return GpuTask.NULL; }
    //@Override public double getSubmissionDelay() { return 0; }
    //@Override public void setSubmissionDelay(double submissionDelay) {/**/}
    //@Override public boolean isBoundToVGpu() { return false; }
    @Override public int compareTo(GpuTask gpuTask) { return 0; }
    @Override public String toString () {
        return "Cloudlet.NULL";
    }
    @Override public boolean addFinishedLengthSoFar (long partialFinishedMI) {
        return false;
    }
    @Override public void setExecStartTime (double clockTime) {/**/}
    @Override public GpuTask addOnStartListener (EventListener<GpuTaskVGpuEventInfo> listener) { 
    	return this; }
    @Override public boolean removeOnStartListener (EventListener<GpuTaskVGpuEventInfo> listener) { 
    	return false; }
    //@Override public double registerArrivalInDatacenter() {
    //    return -1;
    //}
    @Override public GpuTask reset () { return this; }
    @Override public GpuTask setLifeTime (final double lifeTime) { return this; }
    @Override public double getLifeTime () { return -1; }
	@Override public double registerArrivalInVideocard () { return 0; }
	@Override public VGpu getVGpu () { return VGpu.NULL; }
	@Override public GpuTask setVGpu(VGpu vgpu) { return this; }
	@Override public double getSubmissionDelay () { return 0; }
	@Override public boolean addRequiredFile (String fileName) { return false; }
	@Override public boolean addRequiredFiles (List<String> fileNames) { return false; }
	@Override public boolean deleteRequiredFile (String filename) { return false; }
	@Override public boolean hasRequiresFiles () { return false; }
	@Override public List<String> getRequiredFiles () { return Collections.emptyList(); }
	@Override public UtilizationModel getUtilizationModel (
			Class<? extends ResourceManageable> resourceClass) {  return UtilizationModel.NULL; }

}
