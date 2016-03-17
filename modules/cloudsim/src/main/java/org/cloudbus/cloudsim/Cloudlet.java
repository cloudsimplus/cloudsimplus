package org.cloudbus.cloudsim;

import java.util.Collections;
import java.util.List;

/**
 * An interface to be implemented by each class that provides basic 
 * cloudlet features. It also implements the Null Object Design
 * Pattern in order to start avoiding {@link NullPointerException} 
 * when using the {@link Cloudlet#NULL} object instead
 * of attributing {@code null} to {@link Cloudlet} variables.
 * 
 * @author Manoel Campos da Silva Filho
 */
public interface Cloudlet {
  /**
   * Status of Cloudlets
   */
  public enum Status {
        /**
         * The Cloudlet has been created and added to the CloudletList object.
         */
        CREATED, 
        /**
         * The Cloudlet has been assigned to a CloudResource object to be executed
         * as planned.
         */
        READY, 
        /**
         * The Cloudlet has moved to a Cloud node.
         */
        QUEUED, 
        /**
         * The Cloudlet is in execution in a Cloud node.
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
     * @see #reservationId
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
     * The total bandwidth (bw) cost for transferring the cloudlet by the
     * network, according to the {@link #cloudletFileSize}.
     *
     * @return the accumulated bw cost
     */
    double getAccumulatedBwCost();

    /**
     * Gets the total execution time of this Cloudlet in a given Datacenter
     * ID.
     *
     * @param resId the Datacenter entity ID
     * @return the total execution time of this Cloudlet in the given Datacenter
     * or 0 if the Cloudlet was not executed there
     * @pre resId >= 0
     * @post $result >= 0.0
     */
    double getActualCPUTime(final int resId);

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
     * Gets the IDs of all Datacenters that executed this Cloudlet.
     *
     * @return an array of Datacenter IDs where the Cloudlet has being executed
     * @pre $none
     * @post $none
     */
    Integer[] getAllResourceId();

    /**
     * Gets the names of all Datacenters that executed this Cloudlet.
     *
     * @return an array of Datacenter names where the Cloudlet has being executed
     * @pre $none
     * @post $none
     */
    String[] getAllResourceName();

    /**
     * Gets the classType or priority of this Cloudlet for scheduling on a Datacenter.
     *
     * @return classtype of this cloudlet
     * @pre $none
     * @post $none
     */
    int getClassType();

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
     * latest CloudResource (in MI). This method is useful when trying to move this
     * Cloudlet into different CloudResources or to cancel it.
     *
     * @return the length of a partially executed Cloudlet, or the full Cloudlet
     * length if it is completed
     * @pre $none
     * @post $result >= 0.0
     */
    long getCloudletFinishedSoFar();

    /**
     * Gets the length of this Cloudlet that has been executed so far in a given
     * Datacenter. This method is useful when trying to move this Cloudlet
     * into different CloudResources or to cancel it.
     *
     * @param resId the Datacenter entity ID
     * @return the length of a partially executed Cloudlet; the full Cloudlet
     * length if it is completed; or 0 if the Cloudlet has never been executed in the given Datacenter
     * @pre resId >= 0
     * @post $result >= 0.0
     */
    long getCloudletFinishedSoFar(final int resId);

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
     * The ID of this Cloudlet.
     * @return the cloudlet ID
     * @pre $none
     * @post $none
     */
    int getCloudletId();

    /**
     * Gets the execution length of this Cloudlet (Unit: in Million Instructions
     * (MI)). According to this length and the power of the VM processor (in
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
     * {@link #cloudletLength} of the cloudlet will be executed in each Pe defined by
     * {@link #numberOfPes}.<br/>
     *
     * For example, setting the cloudletLenght as 10000 MI and
     * {@link #numberOfPes} to 4, each Pe will execute 10000 MI. Thus, the
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
     * @param resId the Datacenter entity ID
     * @return the cost associated with running this Cloudlet in the given Datacenter
     * or 0 if the Cloudlet was not executed there
     * not found
     * @pre resId >= 0
     * @post $result >= 0.0
     */
    double getCostPerSec(final int resId);

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
     * Gets the total cost of processing or executing this Cloudlet
     * <tt>Processing Cost = input data transfer + processing cost + output
     * transfer cost</tt> .
     *
     * @return the total cost of processing Cloudlet
     * @pre $none
     * @post $result >= 0.0
     */
    double getProcessingCost();

    /**
     * Gets the list of required files to be used by the cloudlet (if any). The time to
     * transfer these files by the network is considered when placing the
     * cloudlet inside a given VM
     *
     * @return the required files
     */
    List<String> getRequiredFiles();

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
    int getResourceId();

    /**
     * Gets the name of a Datacenter where the cloudlet has executed.
     *
     * @param resId the Datacenter entity ID
     * @return the Datacenter name or "" if the Cloudlet has never been executed in the given Datacenter
     * @pre resId >= 0
     * @post $none
     */
    String getResourceName(final int resId);

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
     * Gets the submission (arrival) time of this Cloudlet from the latest
     * CloudResource.
     *
     * @return the submission time or <tt>0.0</tt> if
     * the cloudlet has never been assigned to a datacenter
     * @pre $none
     * @post $result >= 0.0
     */
    double getSubmissionTime();

    /**
     * Gets the submission (arrival) time of this Cloudlet in the given Datacenter.
     *
     * @param resId the Datacenter entity ID
     * @return the submission time or 0 if the Cloudlet has never been executed in the given Datacenter
     * @pre resId >= 0
     * @post $result >= 0.0
     */
    double getSubmissionTime(final int resId);

    /**
     * Gets the ID of the User or Broker that is the owner of the Cloudlet.
     * It is advisable that broker set this ID with its
     * own ID, so that CloudResource returns to it after the execution.
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
     * Gets the utilization percentage of bw.
     *
     * @param time the time
     * @return the utilization of bw
     */
    double getUtilizationOfBw(final double time);

    /**
     * Gets the utilization percentage of cpu.
     *
     * @param time the time
     * @return the utilization of cpu
     */
    double getUtilizationOfCpu(final double time);

    /**
     * Gets the utilization percentage of memory.
     *
     * @param time the time
     * @return the utilization of memory
     */
    double getUtilizationOfRam(final double time);

    /**
     * Gets the id of the VM that is planned to execute the cloudlet.
     *
     * @return the VM Id, or {@link #NOT_ASSIGNED} if the Cloudlet was not assigned to a VM yet
     * @pre $none
     * @post $none
     */
    int getVmId();

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
    double getWallClockTime();

    /**
     * Gets the time of this Cloudlet resides in a given Datacenter (from
     * arrival time until departure time).
     *
     * @param resId a Datacenter entity ID
     * @return the time of this Cloudlet resides in the Datacenter
     * or 0 if the Cloudlet has never been executed there
     * @pre resId >= 0
     * @post $result >= 0.0
     */
    double getWallClockTime(final int resId);

    /**
     * Checks whether this Cloudlet is submitted by reserving or not.
     *
     * @return <tt>true</tt> if this Cloudlet was reserved before,
     * i.e, its {@link #reservationId} is not equals to {@link #NOT_ASSIGNED};
     * <tt>false</tt> otherwise
     */
    boolean hasReserved();

    /**
     * Checks whether this Cloudlet has finished execution or not.
     *
     * @return <tt>true</tt> if this Cloudlet has finished execution,
     * <tt>false</tt> otherwise
     * @pre $none
     * @post $none
     */
    boolean isFinished();

    /**
     * Checks whether this cloudlet requires any files or not.
     *
     * @return <tt>true</tt> if required, <tt>false</tt> otherwise
     */
    boolean requiresFiles();

    /**
     * Sets the {@link #getClassType() classType or priority} of this Cloudlet for scheduling on a
     * Datacenter.
     *
     * @param classType classType of this Cloudlet
     * @return <tt>true</tt> if it is classType is valid, <tt>false</tt> otherwise
     *
     * @pre classType > 0
     * @post $none
     */
    boolean setClassType(final int classType);

    /**
     * Sets the length of this Cloudlet that has been executed so far. This
     * method is used by ResCloudlet class when an application is decided to
     * cancel or to move this Cloudlet into different CloudResources.
     *
     * @param length length of this Cloudlet
     * @return true if the length is valid and the cloudlet already has assigned
     * to a Datacenter, false otherwise
     * @see gridsim.AllocPolicy
     * @see gridsim.ResCloudlet
     * @pre length >= 0.0
     * @post $none
     */
    boolean setCloudletFinishedSoFar(final long length);

    /**
     * Sets the {@link #getCloudletLength() length (in MI)}  of this Cloudlet.
     *
     * @param cloudletLength the length (in MI) of this Cloudlet to be
     * executed in a CloudResource
     * @return <tt>true</tt> if the cloudletLength is valid, <tt>false</tt> otherwise
     *
     * @see #getCloudletTotalLength() }
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
     * Sets the Cloudlet's execution parameters. These parameters are set by the
     * Datacenter before departure or sending back to the original Cloudlet's
     * owner.
     *
     * @param wallTime the time of this Cloudlet resides in a Datacenter
     * (from arrival time until departure time).
     * @param actualTime the total execution time of this Cloudlet in a
     * Datacenter.
     * @return true if the submission time is valid and
     * the cloudlet has already being assigned to a datacenter for execution
     * @see Resource#wallClockTime
     * @see Resource#actualCPUTime
     *
     * @pre wallTime >= 0.0
     * @pre actualTime >= 0.0
     * @post $none
     */
    boolean setExecParam(final double wallTime, final double actualTime);

    /**
     * Sets the {@link #getExecStartTime() latest execution start time} of this Cloudlet.
     * <br/>
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
     * Sets the {@link #getNumberOfPes() number of PEs} required to run this Cloudlet. <br/>
     * NOTE: The Cloudlet length is computed only for 1 PE for simplicity. <br/>
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

    // ////////////////////// End of Internal Class //////////////////////////
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
     * executed. From the second time this method is called, every call make the
     * cloudlet to be migrated to the indicated Datacenter.<br>
     *
     * NOTE: This method <tt>should</tt> be called only by a resource entity,
     * not the user or owner of this Cloudlet.
     *
     * @param resourceID the id of Datacenter where the cloudlet will be executed
     * @param cost the cost per second of running the cloudlet on the given Datacenter
     *
     * @pre resourceID >= 0
     * @pre cost > 0.0
     * @post $none
     * @todo the method may have a better name, such as assignCloudletToDatacenter
     */
    void setResourceParameter(final int resourceID, final double cost);

    /**
     * Sets the parameters of the Datacenter where the Cloudlet is going to be
     * executed. <br>
     * NOTE: This method <tt>should</tt> be called only by a resource entity,
     * not the user or owner of this Cloudlet.
     *
     * @param resourceID the id of Datacenter where the cloudlet will be executed
     * @param costPerCPU the cost per second of running this Cloudlet on the Datacenter
     * @param costPerBw the cost per byte of data transfer of the Datacenter
     *
     * @pre resourceID >= 0
     * @pre cost > 0.0
     * @post $none
     */
    void setResourceParameter(final int resourceID, final double costPerCPU, final double costPerBw);

    /**
     * Sets the submission (arrival) time of this Cloudlet into a CloudResource.
     *
     * @param clockTime the submission time
     * @return true if the submission time is valid and
     * the cloudlet has already being assigned to a datacenter for execution
     * @pre clockTime >= 0.0
     * @post $none
     */
    boolean setSubmissionTime(final double clockTime);

    /**
     * Sets the {@link #getUserId() user ID}.
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
     * Sets the {@link #getVmId() id of the VM} that is planned to execute the cloudlet.
     *
     * @param vmId the vm id
     * @pre id >= 0
     * @post $none
     */
    void setVmId(final int vmId);
 
    /**
     * A property that implements the Null Object Design Pattern for {@link Cloudlet}
     * objects.
     */
    public static final Cloudlet NULL = new Cloudlet() {
      private final Integer[] emptyIntegerArray = new Integer[]{};
      private final String[]  emptyStringArray  = new String[]{};
      
      @Override public boolean addRequiredFile(String fileName) { return false; }
      @Override public boolean deleteRequiredFile(String filename) { return false; }
      @Override public double getAccumulatedBwCost() { return 0.0; }
      @Override public double getActualCPUTime(int resId) { return 0.0; }
      @Override public double getActualCPUTime() { return 0.0; }
      @Override public Integer[] getAllResourceId() { return emptyIntegerArray; }
      @Override public String[] getAllResourceName() { return emptyStringArray; }
      @Override public int getClassType() { return 0; }
      @Override public long getCloudletFileSize() { return 0L; }
      @Override public long getCloudletFinishedSoFar() { return 0L; }
      @Override public long getCloudletFinishedSoFar(int resId) { return 0L; }
      @Override public String getCloudletHistory() { return ""; };
      @Override public int getCloudletId() { return 0; }
      @Override public long getCloudletLength() { return 0L; }
      @Override public long getCloudletOutputSize() { return 0L; }
      @Override public Status getCloudletStatus() { return Status.FAILED; }
      @Override public String getCloudletStatusString() { return ""; }
      @Override public long getCloudletTotalLength() { return 0L; }
      @Override public double getCostPerBw(){ return 0.0; }
      @Override public double getCostPerSec(){ return 0.0; }
      @Override public double getCostPerSec(int resId) { return 0.0; }
      @Override public double getExecStartTime(){ return 0.0; }
      @Override public double getFinishTime(){ return 0.0; }
      @Override public int getNetServiceLevel(){ return 0; }
      @Override public int getNumberOfPes(){ return 0; }
      @Override public double getProcessingCost(){ return 0.0; }
      @Override public List<String> getRequiredFiles() { return Collections.emptyList();}
      @Override public int getReservationId() { return 0; }
      @Override public int getResourceId() { return 0; }
      @Override public String getResourceName(int resId) { return ""; }
      @Override public Status getStatus() { return getCloudletStatus(); }
      @Override public double getSubmissionTime() { return 0.0; }
      @Override public double getSubmissionTime(int resId) { return 0.0; }
      @Override public int getUserId() { return 0; }
      @Override public UtilizationModel getUtilizationModelBw() { return UtilizationModel.NULL; }
      @Override public UtilizationModel getUtilizationModelCpu() { return UtilizationModel.NULL; }
      @Override public UtilizationModel getUtilizationModelRam() { return UtilizationModel.NULL; }
      @Override public double getUtilizationOfBw(double time) { return 0.0; }
      @Override public double getUtilizationOfCpu(double time) { return 0.0; }
      @Override public double getUtilizationOfRam(double time) { return 0.0; }
      @Override public int getVmId() { return 0; }
      @Override public double getWaitingTime() { return 0.0; }
      @Override public double getWallClockTime() { return 0.0; }
      @Override public double getWallClockTime(int resId) { return 0.0; }
      @Override public boolean hasReserved() { return false; }
      @Override public boolean isFinished() { return false; }
      @Override public boolean requiresFiles() { return false; }
      @Override public boolean setClassType(int classType) { return false; }
      @Override public boolean setCloudletFinishedSoFar(long length) { return false; }
      @Override public boolean setCloudletLength(long cloudletLength) { return false; }
      @Override public boolean setCloudletStatus(Status newStatus) { return false; }
      @Override public boolean setExecParam(double wallTime, double actualTime) { return false; }
      @Override public void setExecStartTime(double clockTime) {}
      @Override public boolean setNetServiceLevel(int netServiceLevel) { return false; }
      @Override public boolean setNumberOfPes(int numberOfPes) { return false; }
      @Override public boolean setReservationId(int reservationId) { return false; }
      @Override public void setResourceParameter(int resourceID, double cost) {}
      @Override public void setResourceParameter(int resourceID, double costPerCPU, double costPerBw) {}
      @Override public boolean setSubmissionTime(double clockTime) { return false; }
      @Override public void setUserId(int userId) {}
      @Override public void setUtilizationModelBw(UtilizationModel utilizationModelBw) {}
      @Override public void setUtilizationModelCpu(UtilizationModel utilizationModelCpu) {}
      @Override public void setUtilizationModelRam(UtilizationModel utilizationModelRam) {}
      @Override public void setVmId(int vmId) {}
  };
}
