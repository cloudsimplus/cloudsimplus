/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.cloudlets;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.core.events.CloudSimEvent;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.util.Conversion;

import java.util.Objects;

/**
 * Stores execution information about a {@link Cloudlet} submitted to a specific {@link Datacenter} for
 * processing. This class keeps track of the time for all activities in the
 * Datacenter for a specific Cloudlet. Before a Cloudlet exits the Datacenter,
 * it is RECOMMENDED to call this method {@link #finalizeCloudlet()}.
 * <p>
 * It acts as a
 * placeholder for maintaining the amount of resource share allocated at various
 * times for simulating any scheduling using internal events.
 * </p>
 *
 * <p>As the VM where the Cloudlet is running might migrate to another
 * Datacenter, each CloudletExecutionInfo object represents the data about
 * execution of the cloudlet when the Vm was in a given Datacenter.</p>
 *
 * @author Manzur Murshed
 * @author Rajkumar Buyya
 * @since CloudSim Toolkit 1.0
 */
public class CloudletExecution {
    /**
     * A property that implements the Null Object Design Pattern for {@link CloudletExecution}
     * objects.
     */
    public static final CloudletExecution NULL = new CloudletExecution(Cloudlet.NULL);

    /**
     * @see #getCloudlet()
     */
    private final Cloudlet cloudlet;

	/**
	 * @see #getFileTransferTime()
	 */
	private double fileTransferTime;

    /**
     * @see #getCloudletArrivalTime()
     */
    private final double arrivalTime;

    /**
     * @see #getFinishTime()
     */
    private double finishedTime;

    /**
     * The time a request was sent to the broker to finish the Cloudlet
     * when it has a negative length.
     * @see Cloudlet#getLength()
     */
    private double finishRequestTime;

    /**
     * The length of Cloudlet finished so far in number of Instructions (I).
     * The attribute stores the execution length of the cloudlet
     * in previous datacenters. Thus, it represents the actual executed
     * length of the cloudlet (not just the executed length in the current Datacenter).
     */
    private long instructionsFinishedSoFar;

    /**
     * Latest cloudlet execution start time in the current Datacenter.
     * This attribute will only hold the latest
     * time since a Cloudlet can be canceled, paused or resumed.
     */
    private double startExecTime;

	/**
	 * @see #getLastProcessingTime()
	 */
	private double lastProcessingTime;

    /**
     * The total time the Cloudlet spent in the last state
     * at the current Datacenter.
     * For instance, if the last state was paused and now
     * the cloudlet was resumed (it is running),
     * this time represents the time the cloudlet
     * stayed paused.
     */
    private double totalCompletionTime;

    /**
     * @see #getVirtualRuntime()
     */
    private double virtualRuntime;

    /**
     * @see #getTimeSlice()
     */
    private double timeSlice;

    /** @see #getLastAllocatedMips() */
    private double lastAllocatedMips;

    /**
     * Instantiates a CloudletExecutionInfo object upon the arrival of a Cloudlet inside a Datacenter.
     * The arriving time is determined by {@link CloudSim#clock()}.
     *
     * @param cloudlet the Cloudlet to store execution information from
     */
    public CloudletExecution(final Cloudlet cloudlet) {
        this.cloudlet = cloudlet;
        this.arrivalTime = cloudlet.registerArrivalInDatacenter();
        this.finishedTime = Cloudlet.NOT_ASSIGNED;
        this.lastProcessingTime = Cloudlet.NOT_ASSIGNED;
        this.totalCompletionTime = 0.0;
        this.startExecTime = 0.0;
        this.virtualRuntime = 0;

        //In case a Cloudlet has been executed partially by some other Host
        this.instructionsFinishedSoFar = cloudlet.getFinishedLengthSoFar() * Conversion.MILLION;
    }

    /**
     * Gets the {@link Cloudlet#getLength() Cloudlet's length}.
     *
     * @return Cloudlet's length
     */
    public long getCloudletLength() {
        return cloudlet.getLength();
    }

    public long getNumberOfPes(){
        return cloudlet.getNumberOfPes();
    }

    /**
     * Sets the Cloudlet status.
     *
     * @param newStatus the Cloudlet status
     * @return true if the new status has been set, false
     * otherwise
     */
    public boolean setStatus(final Cloudlet.Status newStatus) {
        // gets Cloudlet's previous status
        final Cloudlet.Status prevStatus = cloudlet.getStatus();

        // if the status of a Cloudlet is the same as last time, then ignore
        if (prevStatus.equals(newStatus)) {
            return false;
        }

        final double clock = cloudlet.getSimulation().clock();

        cloudlet.setStatus(newStatus);

        if (prevStatus == Cloudlet.Status.INEXEC && isNotRunning(newStatus)) {
            totalCompletionTime += clock - startExecTime;
            return true;
        }

        if (prevStatus == Cloudlet.Status.RESUMED && newStatus == Cloudlet.Status.SUCCESS) {
            totalCompletionTime += clock - startExecTime;
            return true;
        }

        startOrResumeCloudlet(newStatus, prevStatus);

        return true;
    }

    /**
     * Starts or resumes the Cloudlet if the new status is requesting that.
     *
     * @param newStatus the new status that will be checked to start or resume the Cloudlet
     * @param oldStatus the old Cloudlet status
     */
    private void startOrResumeCloudlet(final Cloudlet.Status newStatus, final Cloudlet.Status oldStatus) {
        final double clock = cloudlet.getSimulation().clock();
        if (newStatus == Cloudlet.Status.INEXEC || isTryingToResumePausedCloudlet(newStatus, oldStatus)) {
            startExecTime = clock;
            if(cloudlet.getExecStartTime() == 0) {
                cloudlet.setExecStartTime(startExecTime);
            }
        }
    }

    private boolean isTryingToResumePausedCloudlet(final Cloudlet.Status newStatus, final Cloudlet.Status oldStatus) {
        return newStatus == Cloudlet.Status.RESUMED && oldStatus == Cloudlet.Status.PAUSED;
    }

    /**
     * Checks if the cloudlet is NOT in a running state.
     *
     * @param status The current cloudlet status
     * @return true if the cloudlet is NOT running, false if it is.
     */
    private static boolean isNotRunning(final Cloudlet.Status status) {
        return status == Cloudlet.Status.CANCELED || status == Cloudlet.Status.PAUSED || status == Cloudlet.Status.SUCCESS;
    }

    /**
     * Gets the remaining cloudlet length (in MI) that has to be execute yet,
     * considering the {@link Cloudlet#getLength()}.
     *
     * @return remaining cloudlet length in MI
     */
    public long getRemainingCloudletLength() {
        final long absLength = Math.abs(cloudlet.getLength());
        final double miFinishedSoFar = instructionsFinishedSoFar / (double) Conversion.MILLION;

        if(cloudlet.getLength() > 0){
            return (long)Math.max(absLength - miFinishedSoFar, 0);
        }

        /**
         * If length is negative, that means it is undefined.
         * This way, here it's ensured the remaining length keeps
         * increasing until a {@link CloudSimTags#CLOUDLET_FINISH} message
         * is received by the broker to finish the cloudlet
         *
         * Getting here, it's ensured the length is negative. This way,
         * if the different between the length and the number of executed MI is
         * zero, in a scenario of a regular Cloudlet with a positive length,
         * that means the Cloudlet has finished.
         * If the length is negative, that doesn't mean it is finished.
         * In this case, we just return the absolute length to make the
         * Cloudlet keep running. */
        if(absLength-miFinishedSoFar == 0) {
            return absLength;
        }

        /*
         * In case the difference above is not zero, the remaining
         * length of the indefinite-length Cloudlet will be computed
         * to ensure it will return the lower value as possible, so that
         * it execute as little instructions as possible before checking
         * if a message to finish the cloudlet was sent.
         */
        return (long)Math.min(Math.abs(absLength-miFinishedSoFar), absLength);
    }

    /**
     * Finalizes all relevant information before <b>exiting</b> the Datacenter
     * entity. This method sets the final data of:
     * <ul>
     * <li>wall clock time, i.e. the time of this Cloudlet resides in a
     * Datacenter (from arrival time until departure time).
     * <li>actual CPU time, i.e. the total execution time of this Cloudlet in a
     * Datacenter.
     * <li>Cloudlet's finished time so far
     * </ul>
     */
    public void finalizeCloudlet() {
        // Sets the wall clock time and actual CPU time
        final double wallClockTime = cloudlet.getSimulation().clock() - arrivalTime;
        cloudlet.setWallClockTime(wallClockTime, totalCompletionTime);

        final long finishedLengthMI =
            cloudlet.getStatus() == Cloudlet.Status.SUCCESS ?
                cloudlet.getLength() :
                instructionsFinishedSoFar / Conversion.MILLION;

        cloudlet.addFinishedLengthSoFar(finishedLengthMI);
    }

    /**
     * Updates the length of cloudlet that has executed so far.
     *
     * @param partialFinishedInstructions the partial amount of instructions just executed, to be
     * added to the {@link #instructionsFinishedSoFar}, in <b>Number of Instructions (instead of Million Instructions)</b>
     */
    public void updateProcessing(final double partialFinishedInstructions) {
        final Simulation simulation = cloudlet.getSimulation();
        setLastProcessingTime(simulation.clock());

        final boolean terminate = simulation.isTimeToTerminateSimulationUnderRequest();
        if(partialFinishedInstructions == 0 && !terminate){
            return;
        }

        this.instructionsFinishedSoFar += partialFinishedInstructions;
        final double partialFinishedMI = partialFinishedInstructions / Conversion.MILLION;
        cloudlet.addFinishedLengthSoFar((long)partialFinishedMI);

        /* If a simulation termination time was defined and the length of the Cloudlet is negative
         * (to indicate that they must not finish before the termination time),
         * then sends a request to finish the Cloudlet. */
        if(finishRequestTime <= 0 && terminate && cloudlet.getLength() < 0){
            finishRequestTime = simulation.clock();
            simulation.sendFirst(new CloudSimEvent(cloudlet.getBroker(), CloudSimTags.CLOUDLET_FINISH, cloudlet));
        }
    }

    /**
     * Gets the time the cloudlet arrived for execution inside the Datacenter.
     *
     * @return arrival time
     */
    public double getCloudletArrivalTime() {
        return arrivalTime;
    }

    /**
     * Gets the time when the Cloudlet has finished completely
     * (not just in a given Datacenter, but finished at all).
     * If the cloudlet wasn't finished completely yet,
     * the value is equals to {@link Cloudlet#NOT_ASSIGNED}.
     *
     * @return finish time of a cloudlet or <b>-1.0</b> if it cannot finish in
     * this hourly slot
     */
    public double getFinishTime() {
        return finishedTime;
    }

    /**
     * Sets the finish time for this Cloudlet. If time is negative, then it will be ignored.
     *
     * @param time finish time
     */
    public void setFinishTime(final double time) {
        if (time < 0) {
            return;
        }

        finishedTime = time;
    }

    /**
     * Gets the Cloudlet for which the execution information is related to.
     *
     * @return cloudlet for this execution information object
     */
    public Cloudlet getCloudlet() {
        return cloudlet;
    }

    /**
     * Gets the ID of the Cloudlet this execution info is related to.
     * @return
     */
    public long getCloudletId(){
        return cloudlet.getId();
    }

	/**
	 * Gets the time to transfer the list of files required by the Cloudlet
	 * from the Datacenter storage (such as a Storage Area Network)
	 * to the Vm of the Cloudlet.
     * @return
	 */
	public double getFileTransferTime() {
		return fileTransferTime;
	}

	/**
	 * Sets the time to transfer the list of files required by the Cloudlet
	 * from the Datacenter storage (such as a Storage Area Network)
	 * to the Vm of the Cloudlet.
	 *
	 * @param fileTransferTime the file transfer time to set
	 */
	public void setFileTransferTime(final double fileTransferTime) {
		this.fileTransferTime = fileTransferTime;
	}

	/**
	 * Gets the last time the Cloudlet was processed at the Datacenter
     * where this execution information is related to.
     *
	 * @return the last time the Cloudlet was processed or zero when it has never been processed yet
	 */
	public double getLastProcessingTime() {
		return lastProcessingTime;
	}

	/**
	 * Sets the last time this Cloudlet was processed at a Datacenter.
	 * @param lastProcessingTime the last processing time to set
	 */
	public void setLastProcessingTime(final double lastProcessingTime) {
		this.lastProcessingTime = lastProcessingTime;
        cloudlet.notifyOnUpdateProcessingListeners(lastProcessingTime);
	}

    /**
     * Gets the virtual runtime (vruntime) that indicates how long the Cloudlet
     * has been executing by a {@link CloudletScheduler} (in seconds).
     * The default value of this attribute is zero. Each scheduler
     * implementation might set a value to such attribute
     * to use it for context switch,
     * preempting running Cloudlets to enable other ones to start executing.
     * This way, the attribute is just used internally by specific CloudletSchedulers.
     *
     * @return
     */
    public double getVirtualRuntime(){
        return virtualRuntime;
    }

    /**
     * Adds a given time to the {@link #getVirtualRuntime() virtual runtime}.
     *
     * @param timeToAdd time to add to the virtual runtime  (in seconds)
     * @return the new virtual runtime  (in seconds)
     */
    public double addVirtualRuntime(final double timeToAdd) {
        if(timeToAdd >= 0) {
            setVirtualRuntime(virtualRuntime + timeToAdd);
        }
        return virtualRuntime;

    }

    /**
     * Sets the virtual runtime (vruntime) that indicates how long the Cloudlet
     * has been executing by a {@link CloudletScheduler}  (in seconds). This attribute is used
     * just internally by specific CloudletSchedulers.
     *
     * @param virtualRuntime the value to set  (in seconds)
     * @see #getVirtualRuntime()
     */
    public void setVirtualRuntime(final double virtualRuntime){
        this.virtualRuntime = virtualRuntime;
    }

    /**
     * Gets the time-slice assigned by a {@link CloudletScheduler} for a Cloudlet, which is the amount
     * of time (in seconds) that such a Cloudlet will have to use the PEs
     * of a Vm. Each CloudletScheduler implementation can make use of this attribute or not.
     * CloudletSchedulers that use it, are in charge to compute the time-slice to
     * assign to each Cloudlet.
     *
     * @return Cloudlet time-slice (in seconds)
     *
     */
    public double getTimeSlice() {
        return timeSlice;
    }

    /**
     * Sets the time-slice assigned by a {@link CloudletScheduler} for a Cloudlet, which is the amount
     * of time (in seconds) that such a Cloudlet will have to use the PEs
     * of a Vm. Each CloudletScheduler implementation can make use of this attribute or not.
     * CloudletSchedulers that use it, are in charge to compute the time-slice to
     * assign to each Cloudlet.
     *
     * @param timeSlice the Cloudlet time-slice to set (in seconds)
     */
    public void setTimeSlice(final double timeSlice) {
        this.timeSlice = timeSlice;
    }

    @Override
    public String toString() {
        return String.format("Cloudlet %d", cloudlet.getId());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CloudletExecution &&
               ((CloudletExecution)obj).cloudlet.getId() == this.cloudlet.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(cloudlet.getId());
    }

    /**
     * Gets the last actually allocated MIPS for the Cloudlet.
     * That means if no MIPS was allocated for a given time,
     * it is not stored.
     * This value is used to compute the expected finish time
     * of a Cloudlet. If the allocated MIPS is zero,
     * we don't have how to compute that.
     *
     * @return
     */
    public double getLastAllocatedMips() {
        return lastAllocatedMips;
    }

    /**
     * Sets the last actually allocated MIPS for the Cloudlet.
     * That means if no MIPS was allocated for a given time,
     * it is not stored.
     * This value is used to compute the expected finish time
     * of a Cloudlet. If the allocated MIPS is zero,
     * we don't have how to compute that.
     *
     * @param lastAllocatedMips the value to set (if zero or negative, the attribute is not changed)
     */
    public void setLastAllocatedMips(final double lastAllocatedMips) {
        if(lastAllocatedMips > 0) {
            this.lastAllocatedMips = lastAllocatedMips;
        }
    }
}
