/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.cloudlets;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.util.Consts;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;

/**
 * Stores execution information about a {@link Cloudlet} submitted to a specific {@link Datacenter} for
 * processing. This class keeps track of the time for all activities in the
 * Datacenter for a specific Cloudlet. Before a Cloudlet exits the Datacenter,
 * it is RECOMMENDED to call this method {@link #finalizeCloudlet()}.
 * <p>
 * It contains a Cloudlet object along with its arrival time and the ID of the
 * machine and the Pe (Processing Element) allocated to it. It acts as a
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
public class CloudletExecutionInfo {
    /**
     * A property that implements the Null Object Design Pattern for {@link CloudletExecutionInfo}
     * objects.
     */
    public static final CloudletExecutionInfo NULL = new CloudletExecutionInfo(Cloudlet.NULL);

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
    private double arrivalTime;

    /**
     * @see #getFinishTime()
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

    /**
     * @see #getVirtualRuntime()
     */
    private double virtualRuntime;

    /**
     * @see #getTimeSlice()
     */
    private double timeSlice;

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
        this(cloudlet, 0);
    }

    /**
     * Instantiates a CloudletExecutionInfo object upon the arrival of a Cloudlet inside a Datacenter.
     *
     * @param cloudlet the Cloudlet to store execution information from
     * @param startTime a reservation start time, that can also be interpreted as
     * starting time to execute this Cloudlet
     *
     * @pre cloudlet != null
     * @pre startTime > 0
     * @post $none
     */
    public CloudletExecutionInfo(Cloudlet cloudlet, long startTime) {
        this.cloudlet = cloudlet;
        this.arrivalTime = cloudlet.registerArrivalInDatacenter();
        this.finishedTime = Cloudlet.NOT_ASSIGNED;
        this.totalCompletionTime = 0.0;
        this.startExecTime = 0.0;
        this.virtualRuntime = 0;

        //In case a Cloudlet has been executed partially by some other Host
        this.cloudletFinishedSoFar = cloudlet.getFinishedLengthSoFar() * Consts.MILLION;
    }

    /**
     * Gets the Cloudlet's length.
     *
     * @return Cloudlet's length
     * @pre $none
     * @post $none
     */
    public long getCloudletLength() {
        return cloudlet.getLength();
    }

    public int getNumberOfPes(){
        return cloudlet.getNumberOfPes();
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
    public boolean setCloudletStatus(Cloudlet.Status status) {
        // gets Cloudlet's previous status
        Cloudlet.Status prevStatus = cloudlet.getStatus();

        // if the status of a Cloudlet is the same as last time, then ignore
        if (prevStatus == status) {
            return false;
        }

        boolean success = true;
        try {
            double clock = cloudlet.getSimulation().clock();   // gets the current clock

            // sets Cloudlet's current status
            cloudlet.setStatus(status);

            if (prevStatus == Cloudlet.Status.INEXEC && isNotRunning(status)) {
                // then update the Cloudlet completion time
                totalCompletionTime += (clock - startExecTime);
                return true;
            }

            if (prevStatus == Cloudlet.Status.RESUMED && status == Cloudlet.Status.SUCCESS) {
                // then update the Cloudlet completion time
                totalCompletionTime += (clock - startExecTime);
                return true;
            }

            // if a Cloudlet is now in execution
            if (status == Cloudlet.Status.INEXEC || (prevStatus == Cloudlet.Status.PAUSED && status == Cloudlet.Status.RESUMED)) {
                startExecTime = clock;
                if(cloudlet.getExecStartTime() == 0) {
                    cloudlet.setExecStartTime(startExecTime);
                }
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
    private static boolean isNotRunning(Cloudlet.Status status) {
        return status == Cloudlet.Status.CANCELED || status == Cloudlet.Status.PAUSED || status == Cloudlet.Status.SUCCESS;
    }

    /**
     * Gets the remaining cloudlet length (in MI) that has to be execute yet,
     * considering the {@link Cloudlet#getTotalLength()}.
     *
     * @return cloudlet length
     * @pre $none
     * @post $result >= 0
     */
    public long getRemainingCloudletLength() {
        long length = cloudlet.getTotalLength() * Consts.MILLION - cloudletFinishedSoFar;

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
        double wallClockTime = cloudlet.getSimulation().clock() - arrivalTime;
        cloudlet.setWallClockTime(wallClockTime, totalCompletionTime);

        long finishedLengthAcrossAllPes;
        //if (cloudlet.getCloudletTotalLength() * Consts.MILLION < cloudletFinishedSoFar) {
        if (cloudlet.getStatus() == Cloudlet.Status.SUCCESS) {
            finishedLengthAcrossAllPes = cloudlet.getLength();
        } else {
            finishedLengthAcrossAllPes = cloudletFinishedSoFar / Consts.MILLION;
        }

        cloudlet.setFinishedLengthSoFar(finishedLengthAcrossAllPes);
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
                        cloudlet.getTotalLength()*Consts.MILLION);

        double finishedSoFarByPeMI = cloudletFinishedSoFar  / cloudlet.getNumberOfPes() / Consts.MILLION;
        cloudlet.setFinishedLengthSoFar((long)finishedSoFarByPeMI);
    }

    /**
     * Gets the time the cloudlet arrived for execution inside the Datacenter
     * where this execution information is related to.
     *
     * @return arrival time
     * @pre $none
     * @post $result >= 0.0
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
     * @return finish time of a cloudlet or <tt>-1.0</tt> if it cannot finish in
     * this hourly slot
     * @pre $none
     * @post $result >= -1.0
     */
    public double getFinishTime() {
        return finishedTime;
    }

    /**
     * Sets the finish time for this Cloudlet. If time is negative, then it will be ignored.
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
     * Gets the Cloudlet for which the execution information is related to.
     *
     * @return cloudlet for this execution information object
     * @pre $none
     * @post $result != null
     */
    public Cloudlet getCloudlet() {
        return cloudlet;
    }

    /**
     * Gets the ID of the Cloudlet this execution info is related to.
     * @return
     */
    public int getCloudletId(){
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
	public void setFileTransferTime(double fileTransferTime) {
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
	public void setLastProcessingTime(double lastProcessingTime) {
		this.lastProcessingTime = lastProcessingTime;
        cloudlet.notifyOnCloudletProcessingListeners(lastProcessingTime);
	}

    /**
     * Gets the virtual runtime (vruntime) that indicates how long the Cloudlet
     * has been executing by a {@link CloudletScheduler} (in seconds).
     * The default value of this attribute is zero and each scheduler
     * implementation might or not set a value to such attribute
     * so that the scheduler might use to perform context switch,
     * preempting running Cloudlets to enable other ones to start executing.
     * By this way, the attribute is just used internally by specific CloudletSchedulers.
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
     * @pre timeToAdd >= 0
     */
    public double addVirtualRuntime(double timeToAdd) {
        if(timeToAdd >= 0) {
            setVirtualRuntime(virtualRuntime + timeToAdd);
        }
        return getVirtualRuntime();

    }

    /**
     * Sets the virtual runtime (vruntime) that indicates how long the Cloudlet
     * has been executing by a {@link CloudletScheduler}  (in seconds). This attribute is used
     * just internally by specific CloudletSchedulers.
     *
     * @param virtualRuntime the value to set  (in seconds)
     * @see #getVirtualRuntime()
     */
    public void setVirtualRuntime(double virtualRuntime){
        this.virtualRuntime = virtualRuntime;
    }

    /**
     * Gets the timeslice assigned by a CloudletScheduler for a Cloudlet, that is the amount
     * of time (in seconds) that such a Cloudlet will have to use the PEs
     * of a Vm. Each CloudletScheduler implementation can make use of this attribute or not.
     * CloudletSchedulers that use it, are in charge to compute the timeslice to
     * assign to each Cloudlet.
     *
     * @return Cloudlet timeslice (in seconds)
     *
     */
    public double getTimeSlice() {
        return timeSlice;
    }

    public void setTimeSlice(double timeSlice) {
        this.timeSlice = timeSlice;
    }

    @Override
    public String toString() {
        return String.format("Cloudlet %d", cloudlet.getId());
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof CloudletExecutionInfo) &&
               ((CloudletExecutionInfo)obj).cloudlet.getId() == this.cloudlet.getId();
    }

}
