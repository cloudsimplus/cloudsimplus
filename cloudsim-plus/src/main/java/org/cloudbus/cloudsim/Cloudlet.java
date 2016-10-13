package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.Identificable;
import org.cloudbus.cloudsim.schedulers.CloudletScheduler;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import java.util.Collections;
import java.util.List;
import org.cloudbus.cloudsim.listeners.VmToCloudletEventInfo;
import org.cloudbus.cloudsim.listeners.EventListener;

/**
 * An interface to be implemented by each class that provides basic
 * cloudlet features. The interface implements the Null Object Design
 * Pattern in order to start avoiding {@link NullPointerException}
 * when using the {@link Cloudlet#NULL} object instead
 * of attributing {@code null} to {@link Cloudlet} variables.
 *
 * @author Manoel Campos da Silva Filho
 */
public interface Cloudlet extends Identificable, Comparable<Cloudlet> {
  String NO_HISTORY_IS_RECORDED_FOR_CLOUDLET = "No history is recorded for Cloudlet #%d";

  /**
   * Status of Cloudlets
   */
  enum Status {
        /**
         * The Cloudlet has been created and added to the CloudletList object.
         */
        CREATED,
        /**
         * The Cloudlet has been assigned to a Datacenter object to be executed
         * as planned.
         */
        READY,
        /**
         * The Cloudlet has moved to a Vm.
         */
        QUEUED,
        /**
         * The Cloudlet is in execution in a Vm.
         */
        INEXEC,
        /**
         * The Cloudlet has been executed successfully.
         */
        SUCCESS,
        /**
         * The Cloudlet has failed.
         */
        FAILED,
        /**
         * The Cloudlet has been canceled.
         */
        CANCELED,
        /**
         * The Cloudlet has been paused. It can be resumed by changing the status
         * into <tt>RESUMED</tt>.
         */
        PAUSED,
        /**
         * The Cloudlet has been resumed from <tt>PAUSED</tt> state.
         */
        RESUMED,
        /**
         * The cloudlet has failed due to a resource failure.
         */
        FAILED_RESOURCE_UNAVAILABLE
    }

    /**
     * Value to indicate that a given cloudlet resource was not assigned yet,
     * such as a user or a VM to run the cloudlet.
     * @see #getReservationId()
     */
    int NOT_ASSIGNED = -1;

    /**
     * Adds the required filename to the list.
     *
     * @param fileName the required filename
     * @return <tt>true</tt> if the file was added (it didn't exist in the
     * list of required files), <tt>false</tt> otherwise (it did already exist)
     */
    boolean addRequiredFile(final String fileName);

    /**
     * Deletes the given filename from the list.
     *
     * @param filename the given filename to be deleted
     * @return <tt>true</tt> if the file was found and removed, <tt>false</tt>
     * if not found
     */
    boolean deleteRequiredFile(final String filename);

    /**
     * Checks whether this cloudlet requires any files or not.
     *
     * @return <tt>true</tt> if required, <tt>false</tt> otherwise
     */
    boolean requiresFiles();

    /**
     * Gets the list of required files to be used by the cloudlet (if any). The time to
     * transfer these files by the network is considered when placing the
     * cloudlet inside a given VM
     *
     * @return the required files
     */
    List<String> getRequiredFiles();

    /**
     * The total bandwidth (bw) cost for transferring the cloudlet by the
     * network, according to the {@link #getCloudletFileSize()}.
     *
     * @return the accumulated bw cost
     */
    double getAccumulatedBwCost();

    /**
     * Gets the total execution time of this Cloudlet in a given Datacenter
     * ID.
     *
     * @param datacenterId the Datacenter entity ID
     * @return the total execution time of this Cloudlet in the given Datacenter
     * or 0 if the Cloudlet was not executed there
     * @pre resId >= 0
     * @post $result >= 0.0
     */
    double getActualCPUTime(final int datacenterId);

    /**
     * Returns the total execution time of the Cloudlet.
     *
     * @return time in which the Cloudlet was running
     * or {@link #NOT_ASSIGNED} if it hasn't finished yet
     * @pre $none
     * @post $none
     */
    double getActualCPUTime();

    /**
     * Gets the input file size of this Cloudlet before execution (unit: in byte).
     * This size has to be considered the program + input data sizes.
     *
     * @return the input file size of this Cloudlet
     * @pre $none
     * @post $result >= 1
     */
    long getCloudletFileSize();

    /**
     * Gets the length of this Cloudlet that has been executed so far from the
     * latest Datacenter (in MI). This method is useful when trying to move this
     * Cloudlet into different Datacenter or to cancel it.
     *
     * @return the length of a partially executed Cloudlet, or the full Cloudlet
     * length if it is completed
     * @pre $none
     * @post $result >= 0.0
     */
    long getCloudletFinishedSoFar();

    /**
     * Gets the transaction history of this Cloudlet. The layout of this history
     * is in a readable table column with <tt>time</tt> and <tt>description</tt>
     * as headers.
     *
     * @return a String containing the history of this Cloudlet object.
     * @pre $none
     * @post $result != null
     */
    String getCloudletHistory();

    /**
     * Gets the execution length of this Cloudlet (in Million Instructions (MI))
     * that will be executed in each defined PE.
     *
     * According to this length and the power of the VM processor (in
     * Million Instruction Per Second - MIPS) where the cloudlet will be run,
     * the cloudlet will take a given time to finish processing. For instance,
     * for a cloudlet of 10000 MI running on a processor of 2000 MIPS, the
     * cloudlet will spend 5 seconds using the processor in order to be
     * completed (that may be uninterrupted or not, depending on the scheduling
     * policy).
     *
     * @return the length of this Cloudlet
     * @pre $none
     * @post $result >= 0.0
     * @see #getNumberOfPes()
     * @see #getCloudletTotalLength()
     */
    long getCloudletLength();

    /**
     * Gets the output file size of this Cloudlet after execution (unit: in byte).
     * It is the data produced as result of cloudlet execution
     * that needs to be transferred thought the network to
     * simulate sending response data to the user.
     *
     * @todo See
     * <a href="https://groups.google.com/forum/#!topic/cloudsim/MyZ7OnrXuuI">this
     * discussion</a>
     *
     * @return the Cloudlet output file size
     * @pre $none
     * @post $result >= 1
     */
    long getCloudletOutputSize();

    /**
     * Gets the execution status of this Cloudlet.
     *
     * @return the Cloudlet status
     * @pre $none
     * @post $none
     *
     */
    Status getStatus();

    /**
     * Gets the status code of this Cloudlet.
     *
     * @return the status code of this Cloudlet
     * @pre $none
     * @post $result >= 0
     * @deprecated Use the getter {@link #getStatus()} instead
     */
    @Deprecated
    Status getCloudletStatus();

    /**
     * Register the arrival time of this Cloudlet into a Datacenter to the
     * current simulation time and returns this time.
     *
     * @return the arrived time set or {@link #NOT_ASSIGNED} if the cloudlet is not assigned to a datacenter
     * @pre cloudlet is already assigned to a datacenter
     * @post $none
     */
    double registerArrivalOfCloudletIntoDatacenter();

    /**
     * Gets the string representation of the current Cloudlet status code.
     *
     * @return the Cloudlet status code as a string or <tt>null</tt> if the
     * status code is unknown
     * @pre $none
     * @post $none
     */
    String getCloudletStatusString();

    /**
     * Gets the total length (across all PEs) of this Cloudlet (in MI). It considers the
     * {@link #getCloudletLength()} of the cloudlet will be executed in each Pe defined by
     * {@link #getNumberOfPes()}.<br>
     *
     * For example, setting the cloudletLenght as 10000 MI and
     * {@link #getNumberOfPes()} to 4, each Pe will execute 10000 MI. Thus, the
     * entire cloudlet has a total length of 40000 MI.
     *
     *
     * @return the total length of this Cloudlet (in MI)
     *
     * @see #setCloudletLength(long)
     * @pre $none
     * @post $result >= 0.0
     */
    long getCloudletTotalLength();

    /**
     * Gets the cost of each byte of bandwidth (bw) consumed.
     * @return the cost per bw
     */
    double getCostPerBw();

    /**
     * Gets the cost/sec of running the Cloudlet in the latest Datacenter.
     *
     * @return the cost associated with running this Cloudlet or <tt>0.0</tt> if
     * was not assigned to any Datacenter yet
     * @pre $none
     * @post $result >= 0.0
     */
    double getCostPerSec();

    /**
     * Gets the cost running this Cloudlet in a given Datacenter ID.
     *
     * @param datacenterId the Datacenter entity ID
     * @return the cost associated with running this Cloudlet in the given Datacenter
     * or 0 if the Cloudlet was not executed there
     * not found
     * @pre datacenterId >= 0
     * @post $result >= 0.0
     */
    double getCostPerSec(final int datacenterId);


    /**
     * Gets the total cost of processing or executing this Cloudlet.
     * <tt>Processing Cost = input data transfer + processing cost + output
     * transfer cost</tt> .
     *
     * @return the total cost of processing Cloudlet
     * @pre $none
     * @post $result >= 0.0
     */
    double getProcessingCost();

    /**
     * Gets the latest execution start time of this Cloudlet. With new functionalities, such
     * as CANCEL, PAUSED and RESUMED, this attribute only stores the latest
     * execution time. Previous execution time are ignored.
     *
     * @return the latest execution start time
     * @pre $none
     * @post $result >= 0.0
     */
    double getExecStartTime();

    /**
     * Gets the time when this Cloudlet has completed executing in the latest Datacenter.
     *
     * @return the finish or completion time of this Cloudlet; or <tt>-1</tt> if
     * not finished yet.
     * @pre $none
     */
    double getFinishTime();

    /**
     * Gets the arrival time of this Cloudlet from the latest
     * Datacenter where it has executed.
     *
     * @return the arrival time or {@link #NOT_ASSIGNED} if
     * the cloudlet has never been assigned to a datacenter
     * @pre $none
     * @post $result >= 0.0
     */
    double getDatacenterArrivalTime();

    /**
     * Gets the submission (arrival) time of this Cloudlet in the given Datacenter.
     *
     * @param datacenterId the Datacenter entity ID
     * @return the submission time or 0 if the Cloudlet has never been executed in the given Datacenter
     * @pre datacenterId >= 0
     * @post $result >= 0.0
     */
    double getSubmissionTime(final int datacenterId);

    /**
     * Gets the priority of this Cloudlet for scheduling inside a Vm.
     * Each {@link CloudletScheduler} implementation can define if it will
     * use this Cloudlet attribute to impose execution priorities or
     * not.
     *
     * @return priority of this cloudlet
     * @pre $none
     * @post $none
     */
    int getPriority();

    /**
     * Sets the {@link #getPriority() priority} of this Cloudlet for scheduling inside a Vm.
     * How the priority is interpreted depends on the {@link CloudletScheduler}
     * that is being used by the Vm running the Cloudlet.
     *
     * @param priority priority of this Cloudlet
     *
     * @post $none
     */
    void setPriority(final int priority);

    /**
     * Gets the Type of Service (ToS) of IPv4 for sending Cloudlet over the network.
     * It is the ToS this cloudlet receives in the network
     * (applicable to selected PacketScheduler class only).
     *
     * @return the network service level
     * @pre $none
     * @post $none
     */
    int getNetServiceLevel();

    /**
     * Gets the number of Processing Elements (PEs) from the VM, that is
     * required to execute this cloudlet.
     *
     * @return number of PEs
     *
     * @pre $none
     * @post $none
     * @see #getCloudletTotalLength()
     */
    int getNumberOfPes();

    /**
     * Gets the ID of a reservation made for this cloudlet.
     *
     * @return a reservation ID
     * @pre $none
     * @post $none
     * @todo This attribute doesn't appear to be used
     */
    int getReservationId();

    /**
     * Gets the ID of the latest {@link Datacenter} that has processed this Cloudlet.
     *
     * @return the Datacenter ID or <tt>{@link #NOT_ASSIGNED}</tt> if the Cloudlet
     * has not being processed yet.
     * @pre $none
     */
    int getDatacenterId();

    /**
     * Gets the user Id, that represents the broker the cloudlet belongs to.
     *
     * @return the user ID or <tt>{@link #NOT_ASSIGNED}</tt> if the user ID has not been set before
     * @pre $none
     */
    int getUserId();

    /**
     * Gets the utilization model that defines how the cloudlet will use the VM's
     * bandwidth (bw).
     *
     * @return the utilization model of bw
     */
    UtilizationModel getUtilizationModelBw();

    /**
     * Gets the utilization model that defines how the cloudlet will use the VM's CPU.
     *
     * @return the utilization model of cpu
     */
    UtilizationModel getUtilizationModelCpu();

    /**
     * Gets the utilization model that defines how the cloudlet will use the VM's RAM.
     *
     * @return the utilization model of ram
     */
    UtilizationModel getUtilizationModelRam();

    /**
     * Gets the utilization percentage of bw at a given time.
     *
     * @param time the time
     * @return the utilization percentage of bw
     */
    double getUtilizationOfBw(final double time);

    /**
     * Gets the utilization percentage of cpu at a given time.
     *
     * @param time the time
     * @return the utilization percentage of cpu
     */
    double getUtilizationOfCpu(final double time);

    /**
     * Gets the utilization percentage of memory at a given time.
     *
     * @param time the time
     * @return the utilization percentage of memory
     */
    double getUtilizationOfRam(final double time);

    /**
     * Gets the id of Vm that is planned to execute the cloudlet.
     *
     * @return the VM, or {@link #NOT_ASSIGNED} if the Cloudlet was not assigned to a VM yet
     * @pre $none
     * @post $none
     */
    int getVmId();

    /**
     * Indicates if the Cloudlet is bounded to a specific Vm,
     * meaning that the {@link DatacenterBroker} doesn't have to
     * select a VM for it. In this case, the Cloudlet was already
     * bounded to a specific VM and must run on it.
     *
     * @return true if the Cloudlet is bounded to a specific VM, false otherwise
     */
    boolean isBoundedToVm();

    /**
     * Gets the time the cloudlet had to wait before start executing on a
     * resource.
     *
     * @return the waiting time when the cloudlet waited
     * to execute; or 0 if there wasn't any waiting time
     * or the cloudlet hasn't started to execute.
     * @pre $none
     * @post $none
     */
    double getWaitingTime();

    /**
     * Gets the time of this Cloudlet resides in the latest Datacenter (from
     * arrival time until departure time).
     *
     * @return the time of this Cloudlet resides in the latest Datacenter
     * @pre $none
     * @post $result >= 0.0
     */
    double getWallClockTimeInLastExecutedDatacenter();

    /**
     * Gets the time of this Cloudlet resides in a given Datacenter (from
     * arrival time until departure time).
     *
     * @param datacenterId a Datacenter entity ID
     * @return the time of this Cloudlet resides in the Datacenter
     * or 0 if the Cloudlet has never been executed there
     * @pre datacenterId >= 0
     * @post $result >= 0.0
     */
    double getWallClockTime(final int datacenterId);

    /**
     * Checks whether this Cloudlet is submitted by reserving or not.
     *
     * @return <tt>true</tt> if this Cloudlet was reserved before,
     * i.e, its {@link #getReservationId()} is not equals to {@link #NOT_ASSIGNED};
     * <tt>false</tt> otherwise
     */
    boolean hasReserved();

    /**
     * Checks whether this Cloudlet has finished executing or not.
     *
     * @return <tt>true</tt> if this Cloudlet has finished execution,
     * <tt>false</tt> otherwise
     * @pre $none
     * @post $none
     */
    boolean isFinished();

    /**
     * Sets the execution length of this Cloudlet (in Million Instructions (MI))
     * that will be executed in each defined PE.
     *
     * @param cloudletLength the length (in MI) of this Cloudlet to be
     * executed in a CloudResource
     * @return <tt>true</tt> if the cloudletLength is valid, <tt>false</tt> otherwise
     *
     * @see #getCloudletTotalLength()
     * @see #getCloudletLength()
     * @pre cloudletLength > 0
     * @post $none
     */
    boolean setCloudletLength(final long cloudletLength);

    /**
     * Sets the {@link #getStatus() execution status} of this Cloudlet.
     *
     * @param newStatus the status of this Cloudlet
     * @return true if the cloudlet status was changed,
     * i.e, if the newStatus is different from the current status; false otherwise
     * @post $none
     */
    boolean setCloudletStatus(final Status newStatus);

    /**
     * Sets the {@link #getNetServiceLevel() Type of Service (ToS)} for sending this cloudlet over a
     * network.
     *
     * @param netServiceLevel the new type of service (ToS) of this cloudlet
     * @return <code>true</code> if the netServiceLevel is valid, false otherwise.
     * @pre netServiceLevel >= 0
     * @post $none
     */
    boolean setNetServiceLevel(final int netServiceLevel);

    /**
     * Sets the {@link #getNumberOfPes() number of PEs} required to run this Cloudlet. <br>
     * NOTE: The Cloudlet length is computed only for 1 PE for simplicity. <br>
     * For example, consider a Cloudlet that has a length of 500 MI and requires
     * 2 PEs. This means each PE will execute 500 MI of this Cloudlet.
     *
     * @param numberOfPes number of PEs
     * @return <tt>true</tt> if it is numberOfPes is valid, <tt>false</tt> otherwise
     *
     * @pre numPE > 0
     * @post $none
     */
    boolean setNumberOfPes(final int numberOfPes);

    /**
     * Sets the {@link #getReservationId() id of the reservation} made for this cloudlet.
     *
     * @param reservationId the reservation ID
     * @return <tt>true</tt> if the ID has successfully been set or
     * <tt>false</tt> otherwise.
     */
    boolean setReservationId(final int reservationId);

    /**
     * Sets the parameters of the Datacenter where the Cloudlet is going to be
     * executed. <br>
     * NOTE: This method <tt>should</tt> be called only by a resource entity,
     * not the user or owner of this Cloudlet.
     *
     * @param datacenterId the id of Datacenter where the cloudlet will be executed
     * @param costPerCpuSec the cost per second of running this Cloudlet on the Datacenter
     * @param costPerByteOfBw the cost per byte of data transfer of the Datacenter
     *
     * @pre resourceID >= 0
     * @pre cost > 0.0
     * @post $none
     */
    void assignCloudletToDatacenter(
            final int datacenterId, final double costPerCpuSec, final double costPerByteOfBw);

    /**
     * Sets the user Id, that represents the broker the cloudlet belongs to.
     *
     * @param userId the new user ID
     * @pre id >= 0
     * @post $none
     */
    void setUserId(final int userId);

    /**
     * Sets the {@link #getUtilizationModelBw() utilization model of bw}.
     *
     * @param utilizationModelBw the new utilization model of bw
     */
    void setUtilizationModelBw(final UtilizationModel utilizationModelBw);

    /**
     * Sets the {@link #getUtilizationModelCpu() utilization model of cpu}.
     *
     * @param utilizationModelCpu the new utilization model of cpu
     */
    void setUtilizationModelCpu(final UtilizationModel utilizationModelCpu);

    /**
     * Sets the {@link #getUtilizationModelRam() utilization model of ram}.
     *
     * @param utilizationModelRam the new utilization model of ram
     */
    void setUtilizationModelRam(final UtilizationModel utilizationModelRam);

    /**
     * Sets the id of {@link Vm} that is planned to execute the cloudlet.
     *
     * @param vmId the id of vm to run the cloudlet
     */
    void setVmId(final int vmId);

    /**
     * Sets the length of this Cloudlet that has been executed so far (in MI),
     * according to the {@link #getCloudletLength()}.
     * This method is used by ResCloudlet class when an application is decided to
     * cancel or to move this Cloudlet into different Datacenter.
     *
     * @param length executed length of this Cloudlet (in MI)
     * @return true if the length is valid and the cloudlet already has assigned
     * to a Datacenter, false otherwise
     * @see CloudletExecutionInfo
     * @pre length >= 0.0
     * @post $none
     */
    boolean setCloudletFinishedSoFar(final long length);

    /**
     * Gets the listener object that will be notified every time when
     * the processing of the Cloudlet is updated in its {@link Vm}.
     *
     * @return the onUpdateVmProcessingListener
     * @see org.cloudbus.cloudsim.schedulers.CloudletScheduler#updateCloudletProcessing(CloudletExecutionInfo, double)
     */
    EventListener<VmToCloudletEventInfo> getOnUpdateCloudletProcessingListener();

    /**
     * Gets the listener object that will be notified every time when
     * the processing of the Cloudlet is updated in its {@link Vm}.
     *
     * @param onUpdateCloudletProcessingListener the listener to set
     * @see #setCloudletFinishedSoFar(long)
     */
    void setOnUpdateCloudletProcessingListener(EventListener<VmToCloudletEventInfo> onUpdateCloudletProcessingListener);

    /**
     * Gets the length of this Cloudlet that has been executed so far (in MI),
     * according to the {@link #getCloudletLength()}.
     * This method is useful when trying to move this Cloudlet
     * into different Datacenters or to cancel it.
     *
     * @param datacenterId the Datacenter entity ID
     * @return the length of a partially executed Cloudlet; the full Cloudlet
     * length if it is completed; or 0 if the Cloudlet has never been executed
     * in the given Datacenter
     * @pre resId >= 0
     * @post $result >= 0.0
     */
    long getCloudletFinishedSoFar(final int datacenterId);

    /**
     * Sets the wall clock time the cloudlet spent
     * executing on the current Datacenter.
     * The wall clock time is the total time the Cloudlet resides in a Datacenter
     * (from arrival time until departure time, that may include waiting time).
     * This value is set by the Datacenter before departure or sending back to
     * the original Cloudlet's owner.
     *
     * @param wallTime the time of this Cloudlet resides in a Datacenter
     * (from arrival time until departure time).
     * @param actualCPUTime the total execution time of this Cloudlet in a
     * Datacenter.
     * @return true if the submission time is valid and
     * the cloudlet has already being assigned to a datacenter for execution
     *
     * @pre wallTime >= 0.0
     * @pre actualTime >= 0.0
     * @post $none
     */
    boolean setWallClockTime(final double wallTime, final double actualCPUTime);

    /**
     *
     * @return true if the cloudlet has even been assigned to a datacenter
     * in order to run, false otherwise.
     */
    boolean isAssignedToDatacenter();

    /**
     * Sets the {@link #getExecStartTime() latest execution start time} of this Cloudlet.
     * <br>
     * <b>NOTE:</b> With new functionalities, such as being able to cancel / to
     * pause / to resume this Cloudlet, the execution start time only holds the
     * latest one. Meaning, all previous execution start time are ignored.
     *
     * @param clockTime the latest execution start time
     * @pre clockTime >= 0.0
     * @post $none
     */
    void setExecStartTime(final double clockTime);

    /**
     * Gets the delay (in seconds) that a {@link DatacenterBroker} has to include
     * when submitting the Cloudlet, in order that it will be assigned
     * to a VM only after this delay has expired.
     *
     * @return the submission delay
     */
    double getSubmissionDelay();

    /**
     * Sets the delay (in seconds) that a {@link DatacenterBroker} has to include
     * when submitting the Cloudlet, in order that it will be assigned
     * to a VM only after this delay has expired.
     *
     * @param submissionDelay the amount of seconds from the current simulation
     * time that the cloudlet will wait to be submitted to be created and
     * assigned to a VM
     */
    void setSubmissionDelay(double submissionDelay);

    /**
     * Gets the listener object that will be notified when a cloudlet finishes
     * its execution at a given {@link Vm}.
     *
     * @return the onCloudletFinishEventListener
     */
    EventListener<VmToCloudletEventInfo> getOnCloudletFinishEventListener();

    /**
     * Sets the listener object that will be notified when a cloudlet finishes
     * its execution at a given {@link Vm}.
     * @param onCloudletFinishEventListener
     */
    void setOnCloudletFinishEventListener(EventListener<VmToCloudletEventInfo> onCloudletFinishEventListener);

    /**
     * A property that implements the Null Object Design Pattern for {@link Cloudlet}
     * objects.
     */
    Cloudlet NULL = new Cloudlet() {
      @Override public boolean addRequiredFile(String fileName) { return false; }
      @Override public boolean deleteRequiredFile(String filename) { return false; }
      @Override public double getAccumulatedBwCost() { return 0.0; }
      @Override public double getActualCPUTime(int datacenterId) { return 0.0; }
      @Override public double getActualCPUTime() { return 0.0; }
      public int getPriority() { return 0; }
      @Override public long getCloudletFileSize() { return 0L; }
      @Override public long getCloudletFinishedSoFar() { return 0L; }
      @Override public long getCloudletFinishedSoFar(int datacenterId) { return 0L; }
      @Override public String getCloudletHistory() { return ""; };
      @Override public int getId() { return 0; }
      @Override public long getCloudletLength() { return 0L; }
      @Override public long getCloudletOutputSize() { return 0L; }
      @Override public Status getCloudletStatus() { return Status.FAILED; }
      @Override public String getCloudletStatusString() { return ""; }
      @Override public long getCloudletTotalLength() { return 0L; }
      @Override public double getCostPerBw(){ return 0.0; }
      @Override public double getCostPerSec(){ return 0.0; }
      @Override public double getCostPerSec(int datacenterId) { return 0.0; }
      @Override public double getExecStartTime(){ return 0.0; }
      @Override public double getFinishTime(){ return 0.0; }
      @Override public int getNetServiceLevel(){ return 0; }
      @Override public int getNumberOfPes(){ return 0; }
      @Override public double getProcessingCost(){ return 0.0; }
      @Override public List<String> getRequiredFiles() { return Collections.emptyList();}
      @Override public int getReservationId() { return 0; }
      @Override public int getDatacenterId() { return 0; }
      @Override public Status getStatus() { return getCloudletStatus(); }
      @Override public double getDatacenterArrivalTime() { return 0.0; }
      @Override public double getSubmissionTime(int datacenterId) { return 0.0; }
      @Override public int getUserId() { return 0; }
      @Override public UtilizationModel getUtilizationModelBw() { return UtilizationModel.NULL; }
      @Override public UtilizationModel getUtilizationModelCpu() { return UtilizationModel.NULL; }
      @Override public UtilizationModel getUtilizationModelRam() { return UtilizationModel.NULL; }
      @Override public double getUtilizationOfBw(double time) { return 0.0; }
      @Override public double getUtilizationOfCpu(double time) { return 0.0; }
      @Override public double getUtilizationOfRam(double time) { return 0.0; }
      @Override public int getVmId() { return NOT_ASSIGNED; }
      @Override public double getWaitingTime() { return 0.0; }
      @Override public double getWallClockTimeInLastExecutedDatacenter() { return 0.0; }
      @Override public double getWallClockTime(int datacenterId) { return 0.0; }
      @Override public boolean hasReserved() { return false; }
      @Override public boolean isFinished() { return false; }
      @Override public boolean requiresFiles() { return false; }
      @Override public void setPriority(int priority) {}
      @Override public boolean setCloudletLength(long cloudletLength) { return false; }
      @Override public boolean setCloudletStatus(Status newStatus) { return false; }
      @Override public boolean setNetServiceLevel(int netServiceLevel) { return false; }
      @Override public boolean setNumberOfPes(int numberOfPes) { return false; }
      @Override public boolean setReservationId(int reservationId) { return false; }
      @Override public void assignCloudletToDatacenter(int datacenterId, double costPerCpuSec, double costPerByteOfBw) {}
      @Override public void setUserId(int userId) {}
      @Override public void setUtilizationModelBw(UtilizationModel utilizationModelBw) {}
      @Override public void setUtilizationModelCpu(UtilizationModel utilizationModelCpu) {}
      @Override public void setUtilizationModelRam(UtilizationModel utilizationModelRam) {}
      @Override public void setVmId(int vmId) {}
      @Override public EventListener<VmToCloudletEventInfo> getOnCloudletFinishEventListener() { return EventListener.NULL;}
      @Override public void setOnCloudletFinishEventListener(EventListener<VmToCloudletEventInfo> onCloudletFinishEventListener) {}
      @Override public EventListener<VmToCloudletEventInfo> getOnUpdateCloudletProcessingListener() { return EventListener.NULL; }
      @Override public void setOnUpdateCloudletProcessingListener(EventListener<VmToCloudletEventInfo> onUpdateCloudletProcessingListener) {}
      @Override public double getSubmissionDelay() { return 0; }
      @Override public void setSubmissionDelay(double submissionDelay) {}

      /**
       * @todo @author manoelcampos These methods shouldn't be public,
       * but they are used by ResCloudlet class.
       */
      @Override public boolean setCloudletFinishedSoFar(long length) { return false; }
      @Override public boolean setWallClockTime(double wallTime, double actualCPUTime) { return false; }
      @Override public void setExecStartTime(double clockTime) {}
      @Override public boolean isAssignedToDatacenter() { return false; }
      @Override public double registerArrivalOfCloudletIntoDatacenter() { return -1; }
      @Override public boolean isBoundedToVm() { return false; }
      @Override public int compareTo(Cloudlet o) { return 0; }
  };
}
