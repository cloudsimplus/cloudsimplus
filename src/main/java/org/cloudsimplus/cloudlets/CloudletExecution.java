/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.cloudlets;

import lombok.Getter;
import lombok.Setter;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.core.events.CloudSimEvent;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.resources.SanStorage;
import org.cloudsimplus.schedulers.cloudlet.CloudletScheduler;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerCompletelyFair;
import org.cloudsimplus.util.MathUtil;

import java.util.Objects;

/**
 * Stores execution information about a {@link Cloudlet} submitted to a
 * specific {@link Datacenter} for processing.
 * This class keeps track of the time for all activities in the
 * Datacenter for a specific Cloudlet.
 *
 * <p>
 * It acts as a data holder for maintaining the amount of resource share allocated at different times.
 * As the VM where the Cloudlet is running might migrate to another
 * Datacenter, each CloudletExecution object represents the data about
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
     * {@return the Cloudlet for which the execution information is related to}
     * @see #getCloudlet()
     */
    @Getter
    private final Cloudlet cloudlet;

	/**
     *
     * The time to transfer the list of files required by the Cloudlet
     * from the Datacenter storage (such as a {@link SanStorage Storage Area Network (SAN)})
     * to the Vm of the Cloudlet
     */
	@Setter @Getter
    private double fileTransferTime;

    /**
     *  {@return the time the cloudlet arrived for execution inside the Datacenter}
     */
    @Getter
    private final double arrivalTime;

    /**
     *  {@return the total processing delay imposed to the cloudlet processing
     *  due to over-subscription of RAM and/or BW}.
     *  If there is resource over-subscription,
     *  that already includes the imposed delay.
     */
    @Getter
    private double totalOverSubscriptionDelay;

    @Getter
    private double lastOverSubscriptionDelay;

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

	/**
     *  {@return the last time the Cloudlet was processed at the Datacenter
     *  where this execution information is related to}
     *  It returns zero when it has never been processed yet.
     */
	@Getter
    private double lastProcessingTime;

    /**
     *  The virtual runtime (vruntime) that indicates how long the Cloudlet
     *  has been executing by a scheduler (in seconds).
     *
     *  <p>The default value of this attribute is zero. Each {@link CloudletScheduler}
     *  implementation might set a value to such an attribute
     *  to use it for context switch,
     *  preempting running Cloudlets to enable other ones to start executing.
     *  This way, the attribute is just used internally by specific schedulers such as
     *  the {@link CloudletSchedulerCompletelyFair}.
     *  </p>
     */
    @Setter @Getter
    private double virtualRuntime;

    /**
     *  The time-slice assigned by a scheduler for a Cloudlet, which is
     *  the amount of time (in seconds) that such a Cloudlet will have to use the PEs of a Vm.
     *
     *  <p>Each {@link CloudletScheduler} implementation can make use of this attribute or not.
     *  Schedulers that use it are in charge to compute the time-slice to
     *  assign to each Cloudlet, such as the {@link CloudletSchedulerCompletelyFair}.
     *  CloudletSchedulers that use it must compute the time-slice to assign to each Cloudlet.
     *  </p>
     */
    @Setter @Getter
    private double timeSlice;

    /**
     *  {@return the last actually allocated MIPS for the Cloudlet}
     *  If no MIPS was allocated for a given time, the value is zero.
     *  <p>This value is used to compute the expected finish time
     *  of a Cloudlet. If the allocated MIPS is zero,
     *  we don't have how to compute that.
     *  </p>
     */
    @Getter
    private double lastAllocatedMips;

    /**
     *  {@return the wall clock time the cloudlet spent executing}
     *  The <a href="https://en.wikipedia.org/wiki/Elapsed_real_time">wall clock time</a>
     *  is the total time the Cloudlet resides in a Datacenter
     *  (from arrival time until departure time, that may include waiting time).
     *  This value is set by the Datacenter before departure or sending back to
     *  the original Cloudlet's owner.
     */
    @Getter
    private double wallClockTime;

    /**
     * Instantiates a CloudletExecution object upon the arrival of a Cloudlet inside a Datacenter.
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
     * @return the {@link Cloudlet#getLength() Cloudlet's length} (in MI).
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
     * @param newStatus the new Cloudlet status to set
     * @return true if the new status has been set, false if the status was the same as before
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
     * @param status the current cloudlet status
     * @return true if the cloudlet is NOT running, false if it is.
     */
    private static boolean isNotRunning(final Cloudlet.Status status) {
        return status == Cloudlet.Status.CANCELED ||
               status == Cloudlet.Status.PAUSED ||
               status == Cloudlet.Status.SUCCESS;
    }

    /**
     * Gets the remaining cloudlet length (in MI) that hasn't been executed yet,
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
         * If the length is negative, that means it is undefined.
         * This way, here it's ensured the remaining length keeps
         * increasing until a {@link CloudSimTag#CLOUDLET_FINISH} message
         * is received by the broker to finish the cloudlet
         *
         * Getting here, it's ensured the length is negative. This way,
         * if the difference between the length and the number of executed MI is
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
         * to ensure it will return the lowest value. This way,
         * it executes as little instructions as possible, before checking
         * if a message to finish the cloudlet was sent.
         */
        return Math.min(Math.abs(absLength-partialFinishedMI), absLength);
    }

    /**
     * Finalizes all relevant information before <b>exiting</b> the Datacenter. This method sets the final data of:
     * <ul>
     *   <li>wall clock time, i.e., the time of this Cloudlet resides in a
     *   Datacenter (from arrival to departure time);</li>
     *   <li>actual CPU time, i.e., the total execution time of this Cloudlet in a Datacenter;</li>
     *   <li>Cloudlet's finished time so far.</li>
     * </ul>
     */
    public void finalizeCloudlet() {
        // Sets the wall clock time and actual CPU time
        this.wallClockTime = cloudlet.getSimulation().clock() - arrivalTime;

        cloudlet.addFinishedLengthSoFar(partialFinishedMI - cloudlet.getFinishedLengthSoFar());
    }

    /**
     * Updates the length of the Cloudlet that has executed so far.
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
     * @return the ID of the executing Cloudlet.
     */
    public long getId(){
        return cloudlet.getId();
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
     * Computes the actual processing time span between the current simulation time and the last
     * time the processing of a cloudlet was updated,
     * subtracting the oversubscription wait time.
     *
     * @param currentTime the current simulation time
     * @return the actual processing time span
     */
    public double processingTimeSpan(final double currentTime) {
        final double totalTimeSpan = currentTime - lastProcessingTime;
        return hasCloudletFileTransferTimePassed(currentTime) ? totalTimeSpan - getLastOverSubscriptionDelay(): 0;
    }

    /**
     * Checks if the time to transfer the files required by a Cloudlet
     * has already passed to start executing the Cloudlet.
     *
     * @param currentTime the current simulation time
     * @return true if the time to transfer the files has passed, false
     * otherwise
     */
    public boolean hasCloudletFileTransferTimePassed(final double currentTime) {
        return fileTransferTime == 0 || currentTime - arrivalTime > fileTransferTime || cloudlet.getFinishedLengthSoFar() > 0;
    }

    /**
     * Adds a given time to the {@link #getVirtualRuntime() virtual runtime}.
     *
     * @param timeToAdd time to add to the virtual runtime (in seconds)
     * @return the new virtual runtime (in seconds)
     */
    public double addVirtualRuntime(final double timeToAdd) {
        if(timeToAdd >= 0) {
            setVirtualRuntime(virtualRuntime + timeToAdd);
        }
        return virtualRuntime;

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
     * Sets the last actually allocated MIPS for the Cloudlet.
     * If no MIPS was allocated for a given time, the value is zero.
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

    /**
     * {@return the expected cloudlet finish time (in seconds) if no RAM or BW over-subscription occurs}
     * @see #getTotalOverSubscriptionDelay()
     */
    public double getExpectedFinishTime() {
        return cloudlet.getTotalExecutionTime() - totalOverSubscriptionDelay;
    }

    /**
     * Checks if Cloudlet's RAM or BW has been oversubscribed, causing
     * processing delay.
     * @return returns the over-subscription delay or 0 if there was no over-subscription up to now.
     */
    public boolean hasOverSubscription(){
        return totalOverSubscriptionDelay > 0;
    }

    /**
     * Gets the remaining lifetime of the Cloudlet (in seconds) if a lifeTime is set.
     * @return the remaining execution time if a lifeTime is set;
     *         or {@link Double#MAX_VALUE} otherwise to indicate no lifeTime was set,
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
     * Checks if the last BW or RAM oversubscription delay has passed,
     * so that the cloudlet can start processing again.
     * @param currentTime the current simulation time
     * @return true if the delay has passed, false otherwise
     */
    public boolean lastOverSubscriptionDelayNotPassed(final double currentTime){
        return remainingOverSubscriptionDelay(currentTime) > 0;
    }

    /**
     * {@return the time remaining to finish the last oversubscription delay}
     * After that, the Cloudlet will have the required resources
     * (network and RAM data) to start processing again.
     * @param currentTime the current simulation time
     *
     */
    public double remainingOverSubscriptionDelay(final double currentTime){
        return Math.max(lastOverSubscriptionDelay - (currentTime - lastProcessingTime), 0);
    }

    public void setLastOverSubscriptionDelay(final double overSubscriptionDelay) {
        if(overSubscriptionDelay == 0){
            lastProcessingTime = cloudlet.getSimulation().clock();
        }
        this.lastOverSubscriptionDelay = MathUtil.nonNegative(overSubscriptionDelay, "Over-subscription delay");
        this.totalOverSubscriptionDelay += this.lastOverSubscriptionDelay;
    }

    /**
     * {@return true to indicate there is some BW or RAM oversubscription delay, false otherwise}
     */
    public boolean hasLastOverSubscriptionDelay(){ return lastOverSubscriptionDelay > 0; }
}
