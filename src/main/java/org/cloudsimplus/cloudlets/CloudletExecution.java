/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.cloudlets;

import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.core.events.CloudSimEvent;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.schedulers.cloudlet.CloudletScheduler;
import org.cloudsimplus.util.MathUtil;

import java.util.Objects;

/**
 * Stores execution information about a {@link Cloudlet} submitted to a
 * specific {@link Datacenter} for processing.
 * This class keeps track of the time for all activities in the
 * Datacenter for a specific Cloudlet. Before a Cloudlet exits the Datacenter,
 * it is RECOMMENDED to call this method {@link #finalizeCloudlet()}.
 *
 * <p>
 * It acts as a placeholder for maintaining the amount of resource share allocated
 * at various times for simulating any scheduling using internal events.
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

    /** @see #getCloudlet() */
    private final Cloudlet cloudlet;

	/** @see #getFileTransferTime() */
	private double fileTransferTime;

    /** @see #getCloudletArrivalTime() */
    private final double arrivalTime;

    /** @see #getOverSubscriptionDelay() */
    private double overSubscriptionDelay;

    /**
     * The time a request was sent to the broker to finish the Cloudlet
     * when it has a negative length.
     * @see Cloudlet#getLength()
     */
    private double finishRequestTime;

    /**
     * The length of Cloudlet finished so far, in number of Million Instructions (MI).
     * The attribute stores the execution length of the cloudlet
     * in previous datacenters. Thus, it represents the actual executed
     * length of the cloudlet (not just the executed length in the current Datacenter).
     */
    private long partialFinishedMI;

    /**
     * Latest cloudlet execution start time in the current Datacenter.
     * This attribute will only hold the latest
     * time since a Cloudlet can be canceled, paused or resumed.
     */
    private double startExecTime;

	/** @see #getLastProcessingTime() */
	private double lastProcessingTime;

    /** @see #getVirtualRuntime() */
    private double virtualRuntime;

    /** @see #getTimeSlice() */
    private double timeSlice;

    /** @see #getLastAllocatedMips() */
    private double lastAllocatedMips;

    /** @see #getWallClockTime() */
    private double wallClockTime;

    /**
     * Instantiates a CloudletExecutionInfo object upon the arrival of a Cloudlet inside a Datacenter.
     * The arriving time is determined by {@link CloudSimPlus#clock()}.
     *
     * @param cloudlet the Cloudlet to store execution information from
     */
    public CloudletExecution(final Cloudlet cloudlet) {
        this.cloudlet = cloudlet;
        this.arrivalTime = cloudlet.registerArrivalInDatacenter();
        this.lastProcessingTime = Cloudlet.NOT_ASSIGNED;
        this.startExecTime = 0.0;
        this.virtualRuntime = 0;

        //In case a Cloudlet has been executed partially by some other Host
        this.partialFinishedMI = cloudlet.getFinishedLengthSoFar();
    }

    /**
     * Gets the {@link Cloudlet#getLength() Cloudlet's length} (in MI).
     * @return
     */
    public long getCloudletLength() {
        return cloudlet.getLength();
    }

    public long getPesNumber(){
        return cloudlet.getPesNumber();
    }

    /**
     * Sets the Cloudlet status.
     *
     * @param newStatus the Cloudlet status
     * @return true if the new status has been set, false otherwise
     */
    public boolean setStatus(final Cloudlet.Status newStatus) {
        final var prevStatus = cloudlet.getStatus();

        if (prevStatus.equals(newStatus)) {
            return false;
        }

        cloudlet.setStatus(newStatus);

        if (prevStatus == Cloudlet.Status.INEXEC && isNotRunning(newStatus)) {
            return true;
        }

        if (prevStatus == Cloudlet.Status.RESUMED && newStatus == Cloudlet.Status.SUCCESS) {
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
            if(cloudlet.getStartTime() <= Cloudlet.NOT_ASSIGNED) {
                cloudlet.setStartTime(startExecTime);
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
        return status == Cloudlet.Status.CANCELED ||
               status == Cloudlet.Status.PAUSED ||
               status == Cloudlet.Status.SUCCESS;
    }

    /**
     * Gets the remaining cloudlet length (in MI) that has to be executed yet,
     * considering the {@link Cloudlet#getLength()}.
     *
     * @return remaining cloudlet length in MI
     */
    public long getRemainingCloudletLength() {
        final long absLength = Math.abs(cloudlet.getLength());
        if(cloudlet.getLength() > 0){
            return Math.max(absLength - partialFinishedMI, 0);
        }

        /**
         * If length is negative, that means it is undefined.
         * This way, here it's ensured the remaining length keeps
         * increasing until a {@link CloudSimTag#CLOUDLET_FINISH} message
         * is received by the broker to finish the cloudlet
         *
         * Getting here, it's ensured the length is negative. This way,
         * if the different between the length and the number of executed MI is
         * zero, in a scenario of a regular Cloudlet with a positive length,
         * that means the Cloudlet has finished.
         * If the length is negative, that doesn't mean it is finished.
         * In this case, we just return the absolute length to make the
         * Cloudlet keep running. */
        if(absLength-partialFinishedMI == 0) {
            return absLength;
        }

        /*
         * In case the difference above is not zero, the remaining
         * length of the indefinite-length Cloudlet will be computed
         * to ensure it will return the lower value as possible, so that
         * it execute as little instructions as possible before checking
         * if a message to finish the cloudlet was sent.
         */
        return Math.min(Math.abs(absLength-partialFinishedMI), absLength);
    }

    /**
     * Finalizes all relevant information before <b>exiting</b> the Datacenter
     * entity. This method sets the final data of:
     * <ul>
     *   <li>wall clock time, i.e. the time of this Cloudlet resides in a
     *   Datacenter (from arrival time until departure time);</li>
     *   <li>actual CPU time, i.e. the total execution time of this Cloudlet in a Datacenter;</li>
     *   <li>Cloudlet's finished time so far.</li>
     * </ul>
     */
    public void finalizeCloudlet() {
        // Sets the wall clock time and actual CPU time
        this.wallClockTime = cloudlet.getSimulation().clock() - arrivalTime;

        cloudlet.addFinishedLengthSoFar(partialFinishedMI - cloudlet.getFinishedLengthSoFar());
    }

    /**
     * Updates the length of cloudlet that has executed so far.
     *
     * @param partialFinishedMI the partial amount of Million Instructions (MI) just executed,
     *                          to be added to the {@link #partialFinishedMI}</b>
     */
    public void updateProcessing(final long partialFinishedMI) {
        final var simulation = cloudlet.getSimulation();

        final boolean terminate = simulation.isTimeToTerminateSimulationUnderRequest();
        if(partialFinishedMI == 0 && !terminate){
            return;
        }

        this.partialFinishedMI += partialFinishedMI;
        cloudlet.addFinishedLengthSoFar(partialFinishedMI);

        setLastProcessingTime(simulation.clock());

        /* If a simulation termination time was defined and the length of the Cloudlet is negative
         * (to indicate that they must not finish before the termination time),
         * then sends a request to finish the Cloudlet. */
        if(finishRequestTime <= 0 && terminate && cloudlet.getLength() < 0){
            finishRequestTime = simulation.clock();
            simulation.sendFirst(new CloudSimEvent(cloudlet.getBroker(), CloudSimTag.CLOUDLET_FINISH, cloudlet));
        }
    }

    /**
     * Gets the time the cloudlet arrived for execution inside the Datacenter.
     * @return
     */
    public double getCloudletArrivalTime() {
        return arrivalTime;
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
     *
     * <p>The default value of this attribute is zero. Each scheduler
     * implementation might set a value to such attribute
     * to use it for context switch,
     * preempting running Cloudlets to enable other ones to start executing.
     * This way, the attribute is just used internally by specific CloudletSchedulers.
     * </p>
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
     * Gets the time-slice assigned by a {@link CloudletScheduler} for a Cloudlet, which is
     * the amount of time (in seconds) that such a Cloudlet will have to use the PEs of a Vm.
     *
     * <p>Each CloudletScheduler implementation can make use of this attribute or not.
     * CloudletSchedulers that use it, are in charge to compute the time-slice to
     * assign to each Cloudlet.
     * </p>
     *
     * @return Cloudlet time-slice (in seconds)
     *
     */
    public double getTimeSlice() {
        return timeSlice;
    }

    /**
     * Sets the time-slice assigned by a {@link CloudletScheduler} for a Cloudlet, which is
     * the amount of time (in seconds) that such a Cloudlet will have to use the PEs of a Vm.
     *
     * <p>Each CloudletScheduler implementation can make use of this attribute or not.
     * CloudletSchedulers that use it, are in charge to compute the time-slice to
     * assign to each Cloudlet.
     * </p>
     *
     * @param timeSlice the Cloudlet time-slice to set (in seconds)
     */
    public void setTimeSlice(final double timeSlice) {
        this.timeSlice = timeSlice;
    }

    @Override
    public String toString() {
        return "Cloudlet %d".formatted(cloudlet.getId());
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof CloudletExecution that &&
               that.cloudlet.getId() == this.cloudlet.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(cloudlet.getId());
    }

    /**
     * Gets the last actually allocated MIPS for the Cloudlet.
     * That means if no MIPS was allocated for a given time,
     * it is not stored.
     *
     * <p>This value is used to compute the expected finish time
     * of a Cloudlet. If the allocated MIPS is zero,
     * we don't have how to compute that.
     * </p>
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
     *
     * <p>This value is used to compute the expected finish time
     * of a Cloudlet. If the allocated MIPS is zero,
     * we don't have how to compute that.
     * </p>
     *
     * @param lastAllocatedMips the value to set (if zero or negative, the attribute is not changed)
     */
    public void setLastAllocatedMips(final double lastAllocatedMips) {
        if(lastAllocatedMips > 0) {
            this.lastAllocatedMips = lastAllocatedMips;
        }
    }

    /** Gets the total processing delay imposed to the cloudlet processing
     * due to over-subscription of RAM and/or BW.
     * If there is resource over-subscription,
     * the {@see #getFinishTime finish time} already includes the imposed delay. */
    public double getOverSubscriptionDelay() {
        return overSubscriptionDelay;
    }

    /**
     * Gets the expected cloudlet finish time (in seconds) if no RAM or BW over-subscription occurs.
     * @return
     * @see #getOverSubscriptionDelay()
     */
    public double getExpectedFinishTime() {
        return getCloudlet().getTotalExecutionTime() - overSubscriptionDelay;
    }

    /**
     * Checks if Cloudlet's RAM or BW has been over-subscribed, causing
     * processing delay.
     * @return returns the over-subscription delay or 0 if there was no over-subscription up to now.
     */
    public boolean hasOverSubscription(){
        return overSubscriptionDelay > 0;
    }

    /**
     * Increments the total delay caused by RAM/BW over-subscription
     * @param newDelay the new delay to add (in seconds)
     */
    public void incOverSubscriptionDelay(final double newDelay) {
        this.overSubscriptionDelay += MathUtil.nonNegative(newDelay, "Over-subscription delay");
    }

    /**
     * Gets the remaining lifetime of the Cloudlet (in seconds), if a lifeTime is set.
     * @return the remaining execution time if a lifeTime is set,
     *         or {@link Double#MAX_VALUE} otherwise to indicate no lifeTime is set,
     *         and it isn't known how much longer the Cloudlet will execute.
     * @see Cloudlet#getLifeTime()
     */
    public double getRemainingLifeTime() {
		if (cloudlet.getLifeTime() < 0) {
			return Double.MAX_VALUE;
		}

		return Math.max(cloudlet.getLifeTime() - cloudlet.getTotalExecutionTime(), 0);
	}

    /**
     * Gets the wall clock time the cloudlet spent executing.
     * The wall clock time is the total time the Cloudlet resides in a Datacenter
     * (from arrival time until departure time, that may include waiting time).
     * This value is set by the Datacenter before departure or sending back to
     * the original Cloudlet's owner.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Elapsed_real_time">Elapsed real time (wall-clock time)</a>
     */
    public double getWallClockTime() {
        return wallClockTime;
    }
}
