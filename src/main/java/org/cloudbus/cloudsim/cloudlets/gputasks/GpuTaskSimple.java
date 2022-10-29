package org.cloudbus.cloudsim.gp.cloudlets.gputasks;


import org.gpucloudsimplus.listeners.GpuTaskVGpuEventInfo;
import org.cloudsimplus.listeners.EventListener;

import org.cloudbus.cloudsim.gp.vgpu.VGpu;
import org.cloudbus.cloudsim.gp.resources.GpuCore;
import org.cloudbus.cloudsim.gp.resources.VGpuCore;
import org.cloudbus.cloudsim.gp.cloudlets.GpuCloudlet;

import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.resources.Bandwidth;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.resources.Ram;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class GpuTaskSimple implements GpuTask {
	
    private long taskId;
    private GpuCloudlet gpuCloudlet;
    private long blockLength;
    private long finishedLengthSoFar;
    private long numberOfCores;
    private Status status;
    //private boolean returned;
    private double execStartTime;
    private int priority;
    ///private int netServiceLevel;
    private VGpu vgpu;
    private List<String> requiredFiles;
    private long fileSize;
    private long outputSize;
    private double finishTime;
    private UtilizationModel utilizationModelGpu;
    private UtilizationModel utilizationModelGddram;
    private UtilizationModel utilizationModelBw;

    private final Set<EventListener<GpuTaskVGpuEventInfo>> onStartListeners;
    private final Set<EventListener<GpuTaskVGpuEventInfo>> onFinishListeners;
    private final Set<EventListener<GpuTaskVGpuEventInfo>> onUpdateProcessingListeners;

    private double submissionDelay;
    private double lifeTime;
    private double arrivalTime;
    
    public GpuTaskSimple (final long id, final long blockLength, final long numberOfPes) {
    	
    	this.requiredFiles = new LinkedList<>();
        //this.setTaskId(id);
        this.setNumberOfCores(numberOfPes);
        this.setBlockLength(blockLength);
        this.setFileSize(1);
        this.setOutputSize(1);
        this.setSubmissionDelay(0.0);
        this.setArrivalTime(-1);

        this.reset();

        setUtilizationModelGpu(new UtilizationModelFull());
        setUtilizationModelGddram(UtilizationModel.NULL);
        setUtilizationModelBw(UtilizationModel.NULL);
        onStartListeners = new HashSet<>();
        onFinishListeners = new HashSet<>();
        onUpdateProcessingListeners = new HashSet<>();
    }
    
    public GpuTaskSimple (final long length, final int pesNumber, 
    		final UtilizationModel utilizationModel) {
        this(-1, length, pesNumber);
        setUtilizationModel(utilizationModel);
    }
    
    public GpuTaskSimple (final long length, final int pesNumber) {
        this(-1, length, pesNumber);
    }
    
    public GpuTaskSimple (final long length, final long pesNumber) {
        this(-1, length, pesNumber);
    }
    
    @Override
    public String toString() {
        return String.format("GpuCloudlet %d , GpuTask %d ", gpuCloudlet.getId(), getTaskId());
    }
    
    @Override
    public final GpuTask reset() {
        //this.netServiceLevel = 0;
        this.execStartTime = 0.0;
        this.status = Status.INSTANTIATED;
        this.priority = 0;
        //setBroker(DatacenterBroker.NULL);
        setFinishTime(NOT_ASSIGNED); // meaning this GpuTask hasn't finished yet
        this.vgpu = VGpu.NULL;
        setExecStartTime(0.0);
        setArrivalTime(0);
        //setCreationTime(0);
        setLifeTime(-1);

        //this.setLastTriedDatacenter(Datacenter.NULL);//setLastTriedPGpu
        return this;
    }
    
    @Override
    public Simulation getSimulation () {
    	return gpuCloudlet.getSimulation();
    }
    
    @Override
    public final GpuTask setBlockLength (final long length) {
    	if (length == 0) {
            throw new IllegalArgumentException("GpuTask blockLength cannot be zero.");
        }
        this.blockLength = length;
        return this;
    }
    
    @Override
    public long getBlockLength () {
        return blockLength;
    }
    
    @Override
    public final GpuTask setNumberOfCores (final long numberOfCores) {
        if (numberOfCores <= 0) {
            throw new IllegalArgumentException("GpuTask number of Cores has to be greater than zero.");
        }
        this.numberOfCores = numberOfCores;
        return this;
    }

    @Override
    public long getNumberOfCores () {
        return numberOfCores;
    }
    
    @Override
    public GpuTask setPriority (final int priority) {
    	this.priority = priority;
        return this;
    }

    @Override
    public int getPriority () {
        return priority;
    }
    
    @Override
    public double getWaitingTime () {
    	return arrivalTime == -1 ? -1 : execStartTime - arrivalTime;
    }
    
    @Override
    public boolean addFinishedLengthSoFar (final long partialFinishedMI) {
    	if (partialFinishedMI < 0.0 || arrivalTime == -1) {
            return false;
        }

        final long maxLengthToAdd = getBlockLength() < 0 ?
                                    partialFinishedMI :
                                    Math.min(partialFinishedMI, absLength()-getFinishedLengthSoFar());
        finishedLengthSoFar += maxLengthToAdd;
        //returnToGpuCloudletIfFinished ();//returnToBrokerIfFinished
        return true;
    }
    
    /*private void returnToGpuCloudletIfFinished() {
        if(isFinished() && !isReturned()){
            returned = true;
            final var targetEntity = getSimulation().getCloudInfoService();
            getSimulation().sendNow(targetEntity, getBroker(), CloudSimTag.CLOUDLET_RETURN, this);
            vm.getCloudletScheduler().addCloudletToReturnedList(this);
        }
    }*/
    
    @Override
    public long getFinishedLengthSoFar () {
    	if(getBlockLength() > 0) 
            return Math.min(finishedLengthSoFar, absLength());
    	
    	return finishedLengthSoFar;
    }

    @Override
    public boolean isFinished () {
        return (getLifeTime() > 0 && getActualGpuTime() >= getLifeTime()) ||
               (getBlockLength() > 0 && getFinishedLengthSoFar() >= getBlockLength());
    }

    @Override
    public final GpuTask setFileSize (final long fileSize) {
        if (fileSize <= 0) {
            throw new IllegalArgumentException("GpuTask file size has to be greater than zero.");
        }
        this.fileSize = fileSize;
        return this;
    }
    
    @Override
    public long getFileSize () {
        return fileSize;
    }

    @Override
    public final GpuTask setOutputSize (final long outputSize) {
        if (outputSize <= 0) {
            throw new IllegalArgumentException("GpuTask output size has to be greater than zero.");
        }
        this.outputSize = outputSize;
        return this;
    }
    
    @Override
    public long getOutputSize () {
        return outputSize;
    }
    
    protected final void setArrivalTime (final double arrivalTime) {
        if(arrivalTime < 0)
            this.arrivalTime = -1;
        else this.arrivalTime = arrivalTime;
    }
    
    @Override
    public double getArrivalTime () {
        return arrivalTime;
    }

    @Override
    public void setExecStartTime (final double clockTime) {
        final boolean isStartingInVgpu = this.execStartTime <= 0 && clockTime > 0 && 
        		vgpu != VGpu.NULL && vgpu != null;
        this.execStartTime = clockTime;
        if(isStartingInVgpu){
            onStartListeners.forEach(listener -> listener.update(
            		GpuTaskVGpuEventInfo.of(listener, clockTime, this)));
        }
    }
    
    @Override
    public double getExecStartTime () {
        return execStartTime;
    }
    
    @Override
    public boolean setStatus (final Status newStatus) {
        if (this.status == newStatus) {
            return false;
        }

        if (newStatus == Status.SUCCESS) {
            setFinishTime(getSimulation().clock());
        }

        this.status = newStatus;
        return true;
    }

    @Override
    public Status getStatus () {
        return status;
    }

    @Override
    public long getGpuTaskTotalLength () {
        return getBlockLength() * getNumberOfCores();
    }
    
    @Override
    public double getActualGpuTime () {
    	final double time = getFinishTime() == NOT_ASSIGNED ? getSimulation().clock() : 
    		finishTime;
        return time - execStartTime;
    }
    
    protected final void setFinishTime (final double finishTime) {
        this.finishTime = finishTime;
    }
    
    @Override
    public double getFinishTime () {
    	return finishTime;
    }
    
    @Override
    public void setTaskId (final long taskId) {
    	this.taskId = taskId;
    }
    
    @Override
    public long getTaskId () {
    	return taskId;
    }
    
    @Override
    public GpuCloudlet getGpuCloudlet () {
    	return gpuCloudlet;
    }
    
    @Override
	public void setGpuCloudlet (GpuCloudlet gpuCloudlet) {
		this.gpuCloudlet = gpuCloudlet;
		
		if (gpuCloudlet.getGpuTask() == null)
			gpuCloudlet.setGpuTask(this);
	}

    @Override
    public GpuTask setUtilizationModel (UtilizationModel utilizationModel) {
    	setUtilizationModelBw(utilizationModel);
        setUtilizationModelGddram(utilizationModel);
        setUtilizationModelGpu(utilizationModel);
        return this;
    }

    @Override
    public GpuTask setUtilizationModelBw (UtilizationModel utilizationModelBw) {
    	this.utilizationModelBw = requireNonNull(utilizationModelBw);
        return this;
    }
    
    @Override
    public UtilizationModel getUtilizationModelBw() {
        return utilizationModelBw;
    }
    
    @Override
    public double getUtilizationOfBw() {
        return getUtilizationOfBw(getSimulation().clock());
    }
    
    @Override
    public double getUtilizationOfBw(final double time) {
        return getUtilizationModelBw().getUtilization(time);
    }

    @Override
    public GpuTask setUtilizationModelGpu (UtilizationModel utilizationModelGpu) {
    	this.utilizationModelGpu = requireNonNull(utilizationModelGpu);
        return this;
    }
    
    @Override
    public UtilizationModel getUtilizationModelGpu() {
        return utilizationModelGpu;
    }
    
    @Override
    public UtilizationModel getUtilizationModel (
    		final Class<? extends ResourceManageable> resourceClass) {
    	
        if(resourceClass.isAssignableFrom(Ram.class)){
            return utilizationModelGddram;
        }

        if(resourceClass.isAssignableFrom(Bandwidth.class)){
            return utilizationModelBw;
        }

        if(resourceClass.isAssignableFrom(VGpuCore.class) || 
        		resourceClass.isAssignableFrom(GpuCore.class)){
            return utilizationModelGpu;
        }

        throw new UnsupportedOperationException("This class doesn't support " + resourceClass.getSimpleName() + " resources");
    }
    
    @Override
    public double getUtilizationOfGpu() {
        return getUtilizationOfGpu(getSimulation().clock());
    }
    
    @Override
    public double getUtilizationOfGpu(final double time) {
        return getUtilizationModelGpu().getUtilization(time);
    }

    @Override
    public GpuTask setUtilizationModelGddram (UtilizationModel utilizationModelGddram) {
    	this.utilizationModelGddram = requireNonNull(utilizationModelGddram);
        return this;
    }
    
    @Override
    public UtilizationModel getUtilizationModelGddram() {
        return utilizationModelGddram;
    }

    @Override
    public double getUtilizationOfGddram() {
        return getUtilizationOfGddram(getSimulation().clock());
    }

    @Override
    public double getUtilizationOfGddram(final double time) {
        return getUtilizationModelGddram().getUtilization(time);
    }
    
    protected long absLength () {
        return Math.abs(getBlockLength());
    }

	@Override
	public boolean addRequiredFile (String fileName) {
		if (getRequiredFiles().stream().anyMatch(reqFile -> reqFile.equals(fileName))) {
            return false;
        }

        requiredFiles.add(fileName);
        return true;
	}

	@Override
	public boolean addRequiredFiles (List<String> fileNames) {
		boolean atLeastOneFileAdded = false;
        for (final String fileName : fileNames) {
            atLeastOneFileAdded |= addRequiredFile(fileName);
        }

        return atLeastOneFileAdded;
	}

	@Override
	public boolean deleteRequiredFile (String filename) {
		for (int i = 0; i < getRequiredFiles().size(); i++) {
            final String currentFile = requiredFiles.get(i);

            if (currentFile.equals(filename)) {
                requiredFiles.remove(i);
                return true;
            }
        }

        return false;
	}

	@Override
	public boolean hasRequiresFiles () {
        return !getRequiredFiles().isEmpty();
	}

	@Override
	public List<String> getRequiredFiles () {
		return requiredFiles;
	}

	@Override
	public double registerArrivalInVideocard () {
		setArrivalTime(getSimulation().clock());
        return arrivalTime;
	}

	/*@Override
	public boolean isBoundToVGpu () {
	}*/

	@Override
	public GpuTask setSizes (long size) {
		setFileSize(size);
        setOutputSize(size);
        return this;
	}

	@Override
	public GpuTask addOnStartListener (EventListener<GpuTaskVGpuEventInfo> listener) {
		this.onStartListeners.add(requireNonNull(listener));
        return this;
	}

	@Override
	public boolean removeOnStartListener (EventListener<GpuTaskVGpuEventInfo> listener) {
		return onStartListeners.remove(listener);
	}

	@Override
	public GpuTask addOnUpdateProcessingListener (EventListener<GpuTaskVGpuEventInfo> listener) {
		this.onUpdateProcessingListeners.add(requireNonNull(listener));
        return this;
	}

	@Override
	public boolean removeOnUpdateProcessingListener (EventListener<GpuTaskVGpuEventInfo> listener) {
		return this.onUpdateProcessingListeners.remove(listener);
	}

	@Override
	public GpuTask addOnFinishListener (EventListener<GpuTaskVGpuEventInfo> listener) {
		if(listener.equals(EventListener.NULL))
            return this;
		this.onFinishListeners.add(requireNonNull(listener));
        return this;
	}

	@Override
	public boolean removeOnFinishListener (EventListener<GpuTaskVGpuEventInfo> listener) {
		return onFinishListeners.remove(listener);
	}

	@Override
	public void notifyOnUpdateProcessingListeners (double time) {
		onUpdateProcessingListeners.forEach(
				listener -> listener.update(GpuTaskVGpuEventInfo.of(listener, time, this)));		
	}

	@Override
	public GpuTask setLifeTime (double lifeTime) {
		if (lifeTime == 0) 
			throw new IllegalArgumentException("GpuTask lifeTime cannot be zero.");

		this.lifeTime = lifeTime;
		return this;
	}

	@Override
	public double getLifeTime () {
		return this.lifeTime;
	}

	@Override
	public VGpu getVGpu () {
		return vgpu;
	}

	@Override
	public GpuTask setVGpu (VGpu vgpu) {
		this.vgpu = vgpu;
        return this;
	}
	
	/*public boolean isDelayed() {
        return submissionDelay > 0;
    }*/
	
    @Override 
    public int compareTo (GpuTask other) {
    	if(this.equals(Objects.requireNonNull(other))) 
            return 0;

        return Double.compare(getBlockLength(), other.getBlockLength()) +
            Long.compare(this.getTaskId(), other.getTaskId());
            //this.getBroker().compareTo(other.getBroker());
    }
    
    @Override
    public double getSubmissionDelay () {
        return this.submissionDelay;
    }
    
    public final void setSubmissionDelay (final double submissionDelay) {
        if (submissionDelay < 0)
            return;
        this.submissionDelay = submissionDelay;
    }

    void notifyOnFinishListeners() {
        if (isFinished()) {
            onFinishListeners.forEach(listener -> listener.update(
            		GpuTaskVGpuEventInfo.of(listener, this)));
            onFinishListeners.clear();
        }
    }
    
	//@Override public void setLastTriedDatacenter(Datacenter lastTriedDatacenter) {/**/}
    //@Override public Datacenter getLastTriedDatacenter() { return Datacenter.NULL; }
    //@Override public double getArrivedTime() { return 0; }
    //@Override public CustomerEntity setArrivedTime(double time) { return this; }
    //@Override public double getCreationTime() { return 0; }
}
