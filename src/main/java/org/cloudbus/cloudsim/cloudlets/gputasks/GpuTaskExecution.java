package org.cloudbus.cloudsim.gp.cloudlets.gputasks;

import org.cloudbus.cloudsim.util.Conversion;
import org.cloudbus.cloudsim.core.Simulation;

public class GpuTaskExecution {
	
	public static final GpuTaskExecution NULL = new GpuTaskExecution(GpuTask.NULL);
	
    private final GpuTask gpuTask;
	private double fileTransferTime;
    private final double arrivalTime;
    private double finishedTime;
    private double overSubscriptionDelay;
    private double finishRequestTime;
    private long taskFinishedSoFar;
    private double startExecTime;
	private double lastProcessingTime;
    private double totalCompletionTime;
    private double virtualRuntime;
    private double timeSlice;
    private double lastAllocatedMips;
    private double wallClockTime;
    
    public GpuTaskExecution (final GpuTask gpuTask) {
        this.gpuTask = gpuTask;
        this.arrivalTime = gpuTask.registerArrivalInVideocard();
        this.finishedTime = gpuTask.NOT_ASSIGNED;
        this.lastProcessingTime = gpuTask.NOT_ASSIGNED;
        this.totalCompletionTime = 0.0;
        this.startExecTime = 0.0;
        this.virtualRuntime = 0;
        this.taskFinishedSoFar = gpuTask.getFinishedLengthSoFar() * Conversion.MILLION;
    }
    
    public long getGpuTaskLength () {
        return gpuTask.getBlockLength();
    }
    
    public long getNumberOfCores (){
        return gpuTask.getNumberOfCores ();
    }
    
    public boolean setStatus (final GpuTask.Status newStatus) {
        final GpuTask.Status prevStatus = gpuTask.getStatus();

        if (prevStatus.equals(newStatus)) {
            return false;
        }

        final double clock = gpuTask.getSimulation().clock();
        gpuTask.setStatus(newStatus);

        if (prevStatus == GpuTask.Status.INEXEC && isNotRunning(newStatus)) {
            totalCompletionTime += clock - startExecTime;
            return true;
        }

        if (prevStatus == GpuTask.Status.RESUMED && newStatus == GpuTask.Status.SUCCESS) {
            totalCompletionTime += clock - startExecTime;
            return true;
        }

        startOrResumeGpuTask(newStatus, prevStatus);
        return true;
    }
    
    private void startOrResumeGpuTask (final GpuTask.Status newStatus, 
    		final GpuTask.Status oldStatus) {
    	
        final double clock = gpuTask.getSimulation().clock();
        if (newStatus == GpuTask.Status.INEXEC || 
        		isTryingToResumePausedGpuTask(newStatus, oldStatus)) {
            startExecTime = clock;
            if(gpuTask.getExecStartTime() == 0) {
            	gpuTask.setExecStartTime(startExecTime);
            }
        }
    }
    
    private boolean isTryingToResumePausedGpuTask (final GpuTask.Status newStatus, 
    		final GpuTask.Status oldStatus) {
    	
        return newStatus == GpuTask.Status.RESUMED && oldStatus == GpuTask.Status.PAUSED;
    }
    
    private static boolean isNotRunning (final GpuTask.Status status) {
        return status == GpuTask.Status.CANCELED ||
               status == GpuTask.Status.PAUSED ||
               status == GpuTask.Status.SUCCESS;
    }
    
    public long getRemainingCloudletLength() {
        final long absLength = Math.abs(gpuTask.getBlockLength());
        final double miFinishedSoFar = taskFinishedSoFar / (double) Conversion.MILLION;

        if(gpuTask.getBlockLength() > 0){
            return (long)Math.max(absLength - miFinishedSoFar, 0);
        }
        
        if(absLength-miFinishedSoFar == 0) {
            return absLength;
        }
        return (long)Math.min(Math.abs(absLength-miFinishedSoFar), absLength);
    }
    
    public void finalizeCloudlet() {
        this.wallClockTime = gpuTask.getSimulation().clock() - arrivalTime;

        final long finishedLengthMI = taskFinishedSoFar / Conversion.MILLION;
        gpuTask.addFinishedLengthSoFar(finishedLengthMI - gpuTask.getFinishedLengthSoFar());
    }

    public void updateProcessing (final double partialFinishedInstructions) {
        final Simulation simulation = gpuTask.getSimulation();
        setLastProcessingTime(simulation.clock());

        final boolean terminate = simulation.isTimeToTerminateSimulationUnderRequest();
        if(partialFinishedInstructions == 0 && !terminate){
            return;
        }

        this.taskFinishedSoFar += partialFinishedInstructions;
        final double partialFinishedMI = partialFinishedInstructions / Conversion.MILLION;
        gpuTask.addFinishedLengthSoFar((long)partialFinishedMI);

        
        if(finishRequestTime <= 0 && terminate && gpuTask.getBlockLength() < 0){
            finishRequestTime = simulation.clock();
            //simulation.sendFirst(new CloudSimEvent(cloudlet.getBroker(), CloudSimTag.CLOUDLET_FINISH, cloudlet));
        }
    }
    
    public double getGpuTaskArrivalTime() {
        return arrivalTime;
    }
    
    public double getFinishTime() {
        return finishedTime;
    }
    
    public void setFinishTime (final double time) {
        if (time < 0) {
            return;
        }

        finishedTime = time;
        ((GpuTaskSimple)this.gpuTask).notifyOnFinishListeners();
    }
    
    public GpuTask getGpuTask () {
        return gpuTask;
    }
    
    public long getGpuTaskId(){
        return gpuTask.getTaskId();
    }

	public double getFileTransferTime() {
		return fileTransferTime;
	}
    
	public void setFileTransferTime(final double fileTransferTime) {
		this.fileTransferTime = fileTransferTime;
	}

	public double getLastProcessingTime() {
		return lastProcessingTime;
	}

	public void setLastProcessingTime (final double lastProcessingTime) {
		this.lastProcessingTime = lastProcessingTime;
        gpuTask.notifyOnUpdateProcessingListeners(lastProcessingTime);
	}
	
	public double getVirtualRuntime (){
        return virtualRuntime;
    }

    public double addVirtualRuntime (final double timeToAdd) {
        if(timeToAdd >= 0) {
            setVirtualRuntime(virtualRuntime + timeToAdd);
        }
        return virtualRuntime;

    }
    
    public void setVirtualRuntime (final double virtualRuntime) {
        this.virtualRuntime = virtualRuntime;
    }

    public double getTimeSlice () {
        return timeSlice;
    }

    public void setTimeSlice (final double timeSlice) {
        this.timeSlice = timeSlice;
    }

    /*@Override
    public String toString() {
        return String.format("Cloudlet %d", cloudlet.getId());
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof CloudletExecution that &&
               that.cloudlet.getId() == this.cloudlet.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(cloudlet.getId());
    }*/
    
    public double getLastAllocatedMips () {
        return lastAllocatedMips;
    }
    
    public void setLastAllocatedMips(final double lastAllocatedMips) {
        if(lastAllocatedMips > 0) {
            this.lastAllocatedMips = lastAllocatedMips;
        }
    }

    public double getOverSubscriptionDelay() {
        return overSubscriptionDelay;
    }

    public double getExpectedFinishTime() {
        return getGpuTask().getActualGpuTime() - overSubscriptionDelay;
    }
    
    public boolean hasOverSubscription(){
        return overSubscriptionDelay > 0;
    }

    public void incOverSubscriptionDelay(final double newDelay) {
        if(newDelay < 0)
            throw new IllegalArgumentException("Over-subscription delay cannot be negative");

        this.overSubscriptionDelay += newDelay;
    }
    
    public double getRemainingLifeTime() {
		if (gpuTask.getLifeTime() < 0) {
			return Double.MAX_VALUE;
		}

		return Math.max(gpuTask.getLifeTime() - gpuTask.getActualGpuTime(), 0);
	}

    public double getWallClockTime() {
        return wallClockTime;
    }
}
