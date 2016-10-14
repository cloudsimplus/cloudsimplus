/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.Cloudlet.Status;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.resources.Pe;

/**
 * Stores execution information about a Cloudlet submitted to a Datacenter for
 * processing. This class keeps track of the time for all activities in the
 * Datacenter for a specific Cloudlet. Before a Cloudlet exits the Datacenter,
 * it is RECOMMENDED to call this method {@link #finalizeCloudlet()}.
 * <p>
 * It contains a Cloudlet object along with its arrival time and the ID of the
 * machine and the Pe (Processing Element) allocated to it. It acts as a
 * placeholder for maintaining the amount of resource share allocated at various
 * times for simulating any scheduling using internal events.
 * <p>
 As the VM where the Cloudlet is running might migrate to another
 Datacenter, each CloudletExecutionInfo object represents the data about
 execution of the cloudlet when the Vm was in a given Datacenter.
 *
 * @author Manzur Murshed
 * @author Rajkumar Buyya
 * @since CloudSim Toolkit 1.0
 */
public class CloudletExecutionInfo {

    /**
     * The Cloudlet object.
     */
    private final Cloudlet cloudlet;

	/**
	 * @see #getFileTransferTime()
	 */
	private double fileTransferTime;

    /**
     * The time the cloudlet arrived for execution
     * inside a given Datacenter.
     */
    private double arrivalTime;

    /**
     * The time when the Cloudlet has finished completely
     * (not just in a given Datacenter, but finished at all).
     * If the cloudlet wasn't finished completely yet,
     * the value is equals to {@link #NOT_FOUND}.
     */
    private double finishedTime;

    /**
     * The total length of Cloudlet finished so far in number of Instructions (I).
     * The attribute stores past the execution length of the cloudlet
     * in previous datacenters. Thus, it represents the actual executed
     * length of the cloudlet (not just the executed length
     * in the current Datacenter).
     * It considers the sum of instructions executed in every
     * PE of the cloudlet.
     */
    private long cloudletFinishedSoFar;

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

    /*
     * The below attributes are only to be used by the CloudletSchedulerSpaceShared policy.
     * @todo @manoelcampos If the attributes have to be used only for a
     * specific scheduler, they shouldn't be here
     * in order to follow the ISP principle.
    */

    /**
     * The number of PEs needed to execute this Cloudlet.
     */
    private int pesNumber;

    // NOTE: Below attributes are related to Advanced Reservation (AR) stuff
    /**
     * Defines a values for fields that haven't been
     * initialized yet.
     */
    private static final int NOT_FOUND = -1;

    /**
     * The reservation start time.
     */
    private final long reservationStartTime;

    /**
     * The reservation duration time.
     */
    private final int reservationDuration;

    /**
     * The reservation id.
     */
    private final int reservationId;

    /**
     * Instantiates a new CloudletExecutionInfo object upon the arrival of a Cloudlet object.
     * The arriving time is determined by
     * {@link org.cloudbus.cloudsim.core.CloudSim#clock()}.
     *
     * @param cloudlet a cloudlet object
     *
     * @see CloudSim#clock()
     * @pre cloudlet != null
     * @post $none
     */
    public CloudletExecutionInfo(Cloudlet cloudlet) {
        this(cloudlet, 0, 0, NOT_FOUND);
    }

    /**
     * Instantiates a new CloudletExecutionInfo object upon the arrival of a Cloudlet object.
     * Use this constructor to store reserved Cloudlets, i.e. Cloudlets that
     * done reservation before. The arriving time is determined by
     * {@link org.cloudbus.cloudsim.core.CloudSim#clock()}.
     *
     * @param cloudlet a cloudlet object
     * @param startTime a reservation start time. Can also be interpreted as
     * starting time to execute this Cloudlet.
     * @param duration a reservation reservationDuration time.
     * Can also be interpreted as how long to execute this Cloudlet.
     * @param reservationId a reservation ID that owns this Cloudlet
     *
     * @see CloudSim#clock()
     * @pre cloudlet != null
     * @pre startTime > 0
     * @pre duration > 0
     * @pre reservID > 0
     * @post $none
     */
    public CloudletExecutionInfo(Cloudlet cloudlet, long startTime, int duration, int reservationId) {
        this.cloudlet = cloudlet;
        this.reservationStartTime = startTime;
        this.reservationDuration = duration;
        this.reservationId = reservationId;
        this.pesNumber = cloudlet.getNumberOfPes();
        this.arrivalTime = cloudlet.registerArrivalOfCloudletIntoDatacenter();
        this.finishedTime = NOT_FOUND;  // Cannot finish in this hourly slot.
        this.totalCompletionTime = 0.0;
        this.startExecTime = 0.0;

        //In case a Cloudlet has been executed partially by some other host
        this.cloudletFinishedSoFar = cloudlet.getCloudletFinishedSoFar() * Consts.MILLION;
    }

    /**
     * Gets the Cloudlet or reservation start time.
     *
     * @return Cloudlet's starting time
     * @pre $none
     * @post $none
     */
    public long getReservationStartTime() {
        return reservationStartTime;
    }

    /**
     * Gets the reservation reservationDuration time.
     *
     * @return reservation reservationDuration time
     * @pre $none
     * @post $none
     */
    public int getDurationTime() {
        return reservationDuration;
    }

    /**
     * Gets the number of PEs required to execute this Cloudlet.
     *
     * @return number of Pe
     * @pre $none
     * @post $none
     */
    public int getNumberOfPes() {
        return pesNumber;
    }

    /**
     * Gets the reservation ID that owns this Cloudlet.
     *
     * @return a reservation ID
     * @pre $none
     * @post $none
     */
    public int getReservationID() {
        return reservationId;
    }

    /**
     * Checks whether this Cloudlet is submitted by reserving or not.
     *
     * @return <tt>true</tt> if this Cloudlet has reserved before,
     * <tt>false</tt> otherwise
     * @pre $none
     * @post $none
     */
    public boolean hasReserved() {
        return (reservationId != NOT_FOUND);
    }

    /**
     * Gets this Cloudlet entity Id.
     *
     * @return the Cloudlet entity Id
     * @pre $none
     * @post $none
     */
    public int getCloudletId() {
        return cloudlet.getId();
    }

    /**
     * Gets the user or owner of this Cloudlet.
     *
     * @return the Cloudlet's user Id
     * @pre $none
     * @post $none
     */
    public int getUserId() {
        return cloudlet.getUserId();
    }

    /**
     * Gets the Cloudlet's length.
     *
     * @return Cloudlet's length
     * @pre $none
     * @post $none
     */
    public long getCloudletLength() {
        return cloudlet.getCloudletLength();
    }

    /**
     * Gets the total Cloudlet's length (across all PEs).
     *
     * @return total Cloudlet's length
     * @pre $none
     * @post $none
     */
    public long getCloudletTotalLength() {
        return cloudlet.getCloudletTotalLength();
    }

    /**
     * Gets the Cloudlet's class type.
     *
     * @return class type of the Cloudlet
     * @pre $none
     * @post $none
     */
    public int getCloudletClassType() {
        return cloudlet.getPriority();
    }

    /**
     * Sets the Cloudlet status.
     *
     * @param status the Cloudlet status
     * @return <tt>true</tt> if the new status has been set, <tt>false</tt>
     * otherwise
     * @pre status >= 0
     * @post $none
     */
    public boolean setCloudletStatus(Status status) {
        // gets Cloudlet's previous status
        Status prevStatus = cloudlet.getStatus();

        // if the status of a Cloudlet is the same as last time, then ignore
        if (prevStatus == status) {
            return false;
        }

        boolean success = true;
        try {
            double clock = CloudSim.clock();   // gets the current clock

            // sets Cloudlet's current status
            cloudlet.setCloudletStatus(status);

            if (prevStatus == Status.INEXEC && isNotRunning(status)) {
                // then update the Cloudlet completion time
                totalCompletionTime += (clock - startExecTime);
                return true;
            }

            if (prevStatus == Status.RESUMED && status == Status.SUCCESS) {
                // then update the Cloudlet completion time
                totalCompletionTime += (clock - startExecTime);
                return true;
            }

            // if a Cloudlet is now in execution
            if (status == Status.INEXEC || (prevStatus == Status.PAUSED && status == Status.RESUMED)) {
                startExecTime = clock;
                cloudlet.setExecStartTime(startExecTime);
            }
        } catch (Exception e) {
            success = false;
        }

        return success;
    }

    /**
     * Checks if the cloudlet is NOT in a running state.
     *
     * @param status The current cloudlet status
     * @return true if the cloudlet is NOT running, false if it is.
     */
    protected static boolean isNotRunning(Status status) {
        return status == Status.CANCELED || status == Status.PAUSED || status == Status.SUCCESS;
    }

    /**
     * Gets the Cloudlet's execution start time.
     *
     * @return Cloudlet's execution start time
     * @pre $none
     * @post $none
     */
    public double getExecStartTime() {
        return cloudlet.getExecStartTime();
    }

    /**
     * Sets this Cloudlet's execution parameters. These parameters are set by
     * the Datacenter before departure or sending back to the original
     * Cloudlet's owner.
     *
     * @param wallClockTime the time of this Cloudlet resides in a Datacenter
     * (from arrival time until departure time).
     * @param actualCPUTime the total execution time of this Cloudlet in a
     * Datacenter.
     * @pre wallClockTime >= 0.0
     * @pre actualCPUTime >= 0.0
     * @post $none
     */
    public void setExecParam(double wallClockTime, double actualCPUTime) {
        cloudlet.setWallClockTime(wallClockTime, actualCPUTime);
    }

    /**
     * Gets the remaining cloudlet length (in MI) that has to be execute yet,
     * considering the {@link #getCloudletTotalLength()}.
     *
     * @return cloudlet length
     * @pre $none
     * @post $result >= 0
     */
    public long getRemainingCloudletLength() {
        long length = cloudlet.getCloudletTotalLength() * Consts.MILLION - cloudletFinishedSoFar;

        // Remaining Cloudlet length can't be negative number.
        if (length < 0) {
            return 0;
        }

        return (long) Math.floor(length / Consts.MILLION);
    }

    /**
     * Finalizes all relevant information before <tt>exiting</tt> the Datacenter
     * entity. This method sets the final data of:
     * <ul>
     * <li>wall clock time, i.e. the time of this Cloudlet resides in a
     * Datacenter (from arrival time until departure time).
     * <li>actual CPU time, i.e. the total execution time of this Cloudlet in a
     * Datacenter.
     * <li>Cloudlet's finished time so far
     * </ul>
     *
     * @pre $none
     * @post $none
     */
    public void finalizeCloudlet() {
        // Sets the wall clock time and actual CPU time
        double wallClockTime = CloudSim.clock() - arrivalTime;
        cloudlet.setWallClockTime(wallClockTime, totalCompletionTime);

        long finishedLengthAcrossAllPes = 0;
        //if (cloudlet.getCloudletTotalLength() * Consts.MILLION < cloudletFinishedSoFar) {
        if (cloudlet.getStatus() == Status.SUCCESS) {
            finishedLengthAcrossAllPes = cloudlet.getCloudletLength();
        } else {
            finishedLengthAcrossAllPes = cloudletFinishedSoFar / Consts.MILLION;
        }

        cloudlet.setCloudletFinishedSoFar(finishedLengthAcrossAllPes);
    }

    /**
     * Updates the length of cloudlet that has already been completed.
     *
     * @param numberOfExecutedInstructions amount of instructions just executed, to be
     * added to the {@link #cloudletFinishedSoFar}, in number of Instructions (I)
     * @pre length >= 0.0
     * @post $none
     */
    public void updateCloudletFinishedSoFar(long numberOfExecutedInstructions) {
        if(numberOfExecutedInstructions <= 0)
            return;

        this.cloudletFinishedSoFar += numberOfExecutedInstructions;
        this.cloudletFinishedSoFar =
                Math.min(this.cloudletFinishedSoFar,
                        cloudlet.getCloudletTotalLength()*Consts.MILLION);

        double finishedSoFarByPeMI = cloudletFinishedSoFar  / pesNumber / Consts.MILLION;
        cloudlet.setCloudletFinishedSoFar((long)finishedSoFarByPeMI);
    }

    /**
     * Gets arrival time of a cloudlet.
     *
     * @return arrival time
     * @pre $none
     * @post $result >= 0.0
     *
     * @todo It is being used different words for the same term. Here it is used
     * arrival time while at Resource inner classe of the Cloudlet class it is
     * being used submissionTime. It needs to be checked if they are the same
     * term or different ones in fact.
     */
    public double getCloudletArrivalTime() {
        return arrivalTime;
    }

    /**
     * Sets the finish time for this Cloudlet. If time is negative, then it is
     * being ignored.
     *
     * @param time finish time
     * @pre time >= 0.0
     * @post $none
     */
    public void setFinishTime(double time) {
        if (time < 0.0) {
            return;
        }

        finishedTime = time;
    }

    /**
     * Gets the Cloudlet's finish time.
     *
     * @return finish time of a cloudlet or <tt>-1.0</tt> if it cannot finish in
     * this hourly slot
     * @pre $none
     * @post $result >= -1.0
     */
    public double getFinishTime() {
        return finishedTime;
    }

    /**
     * Gets the related Cloudlet object.
     *
     * @return cloudlet object
     * @pre $none
     * @post $result != null
     */
    public Cloudlet getCloudlet() {
        return cloudlet;
    }

    /**
     * Gets the Cloudlet status.
     *
     * @return Cloudlet status
     * @pre $none
     * @post $none
     */
    public Status getCloudletStatus() {
        return cloudlet.getStatus();
    }

    /**
     * Get am Unique Identifier (UID) of the cloudlet.
     *
     * @return The UID
     */
    public String getUid() {
        return getUserId() + "-" + getCloudletId();
    }

	/**
	 * Gets the time to transfer the list of files required by the Cloudlet
	 * from the Datacenter storage (such as a Storage Area Network)
	 * to the Vm of the Cloudlet.
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
	public void setFileTransferTime(double fileTransferTime) {
		this.fileTransferTime = fileTransferTime;
	}

	/**
	 * Gets the last time this Cloudlet was processed in some CPU ({@link Pe}).
	 * @return the last time this Cloudlet was processed or zero when it has never been processed yet
	 */
	public double getLastProcessingTime() {
		return lastProcessingTime;
	}

	/**
	 * Sets the last time this Cloudlet was processed in some CPU ({@link Pe}).
	 * @param lastProcessingTime the last processing time to set
	 */
	public void setLastProcessingTime(double lastProcessingTime) {
		this.lastProcessingTime = lastProcessingTime;
	}
}
