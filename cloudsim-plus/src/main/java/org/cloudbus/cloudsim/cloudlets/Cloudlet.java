/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.cloudlets;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.CustomerEntity;
import org.cloudbus.cloudsim.core.UniquelyIdentifiable;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.traces.google.GoogleTaskEventsTraceReader;

import java.util.List;

/**
 * An interface to be implemented by each class that provides basic
 * cloudlet features. The interface implements the Null Object Design
 * Pattern in order to start avoiding {@link NullPointerException}
 * when using the {@link Cloudlet#NULL} object instead
 * of attributing {@code null} to {@link Cloudlet} variables.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface Cloudlet extends UniquelyIdentifiable, Comparable<Cloudlet>, CustomerEntity {
    /**
     * Status of Cloudlets
     */
    enum Status {
        /**
         * The Cloudlet has been just instantiated but not assigned to a Datacenter yet.
         */
        INSTANTIATED,

        /**
         * The Cloudlet has been assigned to a Datacenter to be executed as planned.
         */
        READY,

        /**
         * The Cloudlet has moved to a Vm but it is in the waiting queue.
         */
        QUEUED,

        /**
         * The Cloudlet is in the waiting queue but it won't be automatically moved
         * to the execution list (even if there are available PEs) until
         * its status is changed to {@link #QUEUED}.
         * This status is used specifically for Cloudlets created from a
         * trace file, such as a {@link GoogleTaskEventsTraceReader Google Cluster trace},
         * that explicitly defines when tasks must start running.
         */
        FROZEN,

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
     * Value to indicate that the cloudlet was not assigned to a Datacenter yet.
     */
    int NOT_ASSIGNED = -1;

    /**
     * An attribute that implements the Null Object Design Pattern for {@link Cloudlet}
     * objects.
     */
    Cloudlet NULL = new CloudletNull();

    /**
     * Adds a file to the list or required files.
     *
     * @param fileName the name of the required file
     * @return <tt>true</tt> if the file was added (it didn't exist in the
     * list of required files), <tt>false</tt> otherwise (it did already exist)
     */
    boolean addRequiredFile(String fileName);

    /**
     * Adds a list of files to the required files list.
     * Just the files that don't exist yet in the current required list
     * will be added.
     *
     * @param fileNames the list of files to be added
     * @return <tt>true</tt> if at leat one file was added,
     * false if no file was added (in the case that all given files
     * already exist in the current required list)
     */
    boolean addRequiredFiles(List<String> fileNames);

    /**
     * Deletes the given filename from the list.
     *
     * @param filename the given filename to be deleted
     * @return <tt>true</tt> if the file was found and removed, <tt>false</tt>
     * if not found
     */
    boolean deleteRequiredFile(String filename);

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
     * network, according to the {@link #getFileSize()}.
     *
     * @return the accumulated bw cost
     */
    double getAccumulatedBwCost();

    /**
     * Gets the total execution time of this Cloudlet in a given Datacenter
     * ID.
     *
     * @param datacenter the Datacenter entity
     * @return the total execution time of this Cloudlet in the given Datacenter
     * or 0 if the Cloudlet was not executed there
     */
    double getActualCpuTime(Datacenter datacenter);

    /**
     * Returns the total execution time of the Cloudlet in seconds.
     *
     * @return time in which the Cloudlet was running
     * or {@link #NOT_ASSIGNED} if it hasn't finished yet
     */
    double getActualCpuTime();

    /**
     * Gets the input file size of this Cloudlet before execution (in bytes).
     * This size has to be considered the program + input data sizes.
     *
     * @return the input file size of this Cloudlet (in bytes)
     */
    long getFileSize();

    /**
     * Gets the output file size of this Cloudlet after execution (in bytes).
     * It is the data produced as result of cloudlet execution
     * that needs to be transferred thought the network to
     * simulate sending response data to the user.
     *
     * @return the Cloudlet output file size (in bytes)
     */
    long getOutputSize();

    /**
     * Gets the execution status of this Cloudlet.
     *
     * @return the Cloudlet status
     */
    Status getStatus();

    /**
     * Sets the parameters of the Datacenter where the Cloudlet is going to be
     * executed. From the second time this method is called, every call makes the
     * cloudlet to be migrated to the indicated Datacenter.
     *
     * <p><b>NOTE</b>: This method <tt>should</tt> be called only by a {@link Datacenter} entity.</p>
     *
     * @param datacenter the Datacenter where the cloudlet will be executed
     */
    void assignToDatacenter(Datacenter datacenter);

    /**
     * Register the arrival time of this Cloudlet into a Datacenter to the
     * current simulation time and returns this time.
     *
     * @return the arrived time set or {@link #NOT_ASSIGNED} if the cloudlet is not assigned to a Datacenter
     */
    double registerArrivalInDatacenter();

    /**
     * @return true if the cloudlet has even been assigned to a Datacenter
     * in order to run, false otherwise.
     */
    boolean isAssignedToDatacenter();

    /**
     * Gets the cost of each byte of bandwidth (bw) consumed.
     * <p>Realize costs must be defined for Datacenters by accessing the {@link DatacenterCharacteristics}
     * object from each {@link Datacenter} instance and setting the bandwidth cost.</p>
     *
     * @return the cost per bw
     * @see DatacenterCharacteristics#setCostPerBw(double)
     */
    double getCostPerBw();

    /**
     * Gets the cost/sec of running the Cloudlet in the latest Datacenter.
     * <p>Realize costs must be defined for Datacenters by accessing the {@link DatacenterCharacteristics}
     * object from each {@link Datacenter} instance and setting the CPU cost.</p>
     *
     * @return the cost associated with running this Cloudlet or <tt>0.0</tt> if
     * was not assigned to any Datacenter yet
     * @see DatacenterCharacteristics#setCostPerSecond(double)
     */
    double getCostPerSec();

    /**
     * Gets the cost running this Cloudlet in a given Datacenter.
     * <p>Realize costs must be defined for Datacenters by accessing the {@link DatacenterCharacteristics}
     * object from each {@link Datacenter} instance and setting the CPU cost.</p>
     *
     * @param datacenter the Datacenter entity
     * @return the cost associated with running this Cloudlet in the given Datacenter
     * or 0 if the Cloudlet was not executed there
     * not found
     * @see DatacenterCharacteristics#setCostPerSecond(double)
     */
    double getCostPerSec(Datacenter datacenter);


    /**
     * Gets the total cost of executing this Cloudlet.
     * <tt>Total Cost = input data transfer + processing cost + output transfer cost</tt> .
     * <p>Realize costs must be defined for Datacenters by accessing the {@link DatacenterCharacteristics}
     * object from each {@link Datacenter} instance and setting costs for each resource.</p>
     *
     * @return the total cost of executing the Cloudlet
     * @see DatacenterCharacteristics#setCostPerSecond(double)
     * @see DatacenterCharacteristics#setCostPerBw(double)
     */
    double getTotalCost();

    /**
     * Gets the latest execution start time of this Cloudlet. With new functionalities, such
     * as CANCEL, PAUSED and RESUMED, this attribute only stores the latest
     * execution time. Previous execution time are ignored.
     * This time represents the simulation second when the cloudlet started.
     *
     * @return the latest execution start time
     */
    double getExecStartTime();

    /**
     * Gets the time when this Cloudlet has completed executing in the latest Datacenter.
     * This time represents the simulation second when the cloudlet finished.
     *
     * @return the finish or completion time of this Cloudlet; or {@link #NOT_ASSIGNED} if
     * not finished yet.
     */
    double getFinishTime();

    /**
     * Gets the arrival time of this Cloudlet from the latest
     * Datacenter where it has executed.
     *
     * @return the arrival time or {@link #NOT_ASSIGNED} if
     * the cloudlet has never been assigned to a Datacenter
     */
    double getLastDatacenterArrivalTime();

    /**
     * Gets the arrival time of this Cloudlet in the given Datacenter.
     *
     * @param datacenter the Datacenter entity
     * @return the arrival time or {@link #NOT_ASSIGNED} if
     * the cloudlet has never been assigned to a Datacenter
     */
    double getArrivalTime(Datacenter datacenter);

    /**
     * Gets the id of the job that this Cloudlet belongs to, if any.
     * This field is just used for classification.
     * If there is an supposed job that multiple Cloudlets belong to,
     * one can set the job id for all Cloudlets of that job
     * in order to classify them.
     * Besides classification, this field doesn't have any effect.
     *
     * @return the job id or {@link #NOT_ASSIGNED} if the Cloudlet doesn't belong to a job
     */
    long getJobId();

    /**
     * Sets the id of the job that this Cloudlet belongs to, if any.
     * This field is just used for classification.
     * If there is an supposed job that multiple Cloudlets belong to,
     * one can set the job id for all Cloudlets of that job
     * in order to classify them.
     * Besides classification, this field doesn't have any effect.
     *
     * @param jobId the job id to set
     */
    void setJobId(long jobId);

    /**
     * Gets the priority of this Cloudlet for scheduling inside a Vm.
     * Each {@link CloudletScheduler} implementation can define if it will
     * use this attribute to impose execution priorities or not.
     * How the priority is interpreted and what is the range of values it accepts depends on the {@link CloudletScheduler}
     * that is being used by the Vm running the Cloudlet.
     *
     * @return priority of this cloudlet
     */
    int getPriority();

    /**
     * Sets the {@link #getPriority() priority} of this Cloudlet for scheduling inside a Vm.
     * Each {@link CloudletScheduler} implementation can define if it will
     * use this attribute to impose execution priorities or not.
     * How the priority is interpreted and what is the range of values it accepts depends on the {@link CloudletScheduler}
     * that is being used by the Vm running the Cloudlet.
     *
     * @param priority priority of this Cloudlet
     */
    void setPriority(int priority);

    /**
     * Gets the Type of Service (ToS) of IPv4 for sending Cloudlet over the network.
     * It is the ToS this cloudlet receives in the network
     * (applicable to selected CloudletTaskScheduler class only).
     *
     * @return the network service level
     */
    int getNetServiceLevel();

    /**
     * Gets the number of Processing Elements (PEs) from the VM, that is
     * required to execute this cloudlet.
     *
     * @return number of PEs
     * @see #getTotalLength()
     */
    long getNumberOfPes();

    /**
     * Gets the latest {@link Datacenter} where the Cloudlet was processed.
     *
     * @return the Datacenter or <tt>{@link Datacenter#NULL}</tt> if the Cloudlet
     * has not being processed yet.
     */
    Datacenter getLastDatacenter();

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
     * Gets the utilization of CPU at the current simulation time, that is defined in
     * percentage (in scale from [0 to 1]) or absolute values, depending of the {@link UtilizationModel#getUnit()}
     * set for the {@link #getUtilizationModelCpu() CPU utilizaton model}.
     *
     * @return the utilization value
     * @see #getUtilizationModelCpu()
     */
    double getUtilizationOfCpu();

    /**
     * Gets the utilization of CPU at a given time, that is defined in
     * percentage (in scale from [0 to 1]) or absolute values, depending of the {@link UtilizationModel#getUnit()}
     * defined for the {@link #getUtilizationModelCpu()}.
     *
     * @param time the time to get the utilization
     * @return the utilization value
     * @see #getUtilizationModelCpu()
     */
    double getUtilizationOfCpu(double time);

    /**
     * Gets the utilization of RAM at the current simulation time, that is defined in
     * percentage (in scale from [0 to 1]) or absolute values, depending of the {@link UtilizationModel#getUnit()}
     * set for the {@link #getUtilizationModelRam() RAM utilizaton model}.
     *
     * @return the utilization value
     * @see #getUtilizationModelRam()
     */
    double getUtilizationOfRam();

    /**
     * Gets the utilization of RAM at a given time, that is defined in
     * percentage (in scale from [0 to 1]) or absolute values, depending of the {@link UtilizationModel#getUnit()}
     * defined for the {@link #getUtilizationModelRam()} ()}.
     *
     * @param time the time to get the utilization
     * @return the utilization value
     * @see #getUtilizationModelRam() ()
     */
    double getUtilizationOfRam(double time);

    /**
     * Gets the utilization of Bandwidth at the current simulation time, that is defined in
     * percentage (in scale from [0 to 1]) or absolute values, depending of the {@link UtilizationModel#getUnit()}
     * set for the {@link #getUtilizationModelBw() BW utilizaton model}.
     *
     * @return the utilization value
     * @see #getUtilizationModelCpu()
     */
    double getUtilizationOfBw();

    /**
     * Gets the utilization of Bandwidth at a given time, that is defined in
     * percentage (in scale from [0 to 1]) or absolute values, depending of the {@link UtilizationModel#getUnit()}
     * defined for the {@link #getUtilizationModelBw()} ()}.
     *
     * @param time the time to get the utilization
     * @return the utilization value
     * @see #getUtilizationModelBw() ()
     */
    double getUtilizationOfBw(double time);

    /**
     * Gets the id of Vm that is planned to execute the cloudlet.
     *
     * @return the VM, or {@link #NOT_ASSIGNED} if the Cloudlet was not assigned to a VM yet
     */
    Vm getVm();

    /**
     * Indicates if the Cloudlet is bounded to a specific Vm,
     * meaning that the {@link DatacenterBroker} doesn't have to
     * select a VM for it. In this case, the Cloudlet was already
     * bounded to a specific VM and must run on it.
     *
     * @return true if the Cloudlet is bounded to a specific VM, false otherwise
     */
    boolean isBindToVm();

    /**
     * Gets the time the cloudlet had to wait before start executing on a
     * resource.
     *
     * @return the waiting time when the cloudlet waited
     * to execute; or 0 if there wasn't any waiting time
     * or the cloudlet hasn't started to execute.
     */
    double getWaitingTime();

    /**
     * Gets the time of this Cloudlet resides in the latest Datacenter (from
     * arrival time until departure time).
     *
     * @return the wall-clock time in the latest Datacenter or 0 if the Cloudlet has never been executed
     * @see <a href="https://en.wikipedia.org/wiki/Elapsed_real_time">Elapsed real time (wall-clock time)</a>
     */
    double getWallClockTimeInLastExecutedDatacenter();

    /**
     * Gets the time of this Cloudlet resides in a given Datacenter (from
     * arrival time until departure time).
     *
     * @param datacenter a Datacenter entity
     * @return the wall-clock time or 0 if the Cloudlet has never been executed there
     * @see <a href="https://en.wikipedia.org/wiki/Elapsed_real_time">Elapsed real time (wall-clock time)</a>
     */
    double getWallClockTime(Datacenter datacenter);

    /**
     * Checks whether this Cloudlet has finished executing or not.
     *
     * @return <tt>true</tt> if this Cloudlet has finished execution,
     * <tt>false</tt> otherwise
     */
    boolean isFinished();

    /**
     * Sets the input file size of this Cloudlet before execution (in bytes).
     * This size has to be considered the program + input data sizes.
     *
     * @param fileSize the size to set (in bytes)
     * @return
     * @throws IllegalArgumentException when the given size is lower or equal to zero
     */
    Cloudlet setFileSize(long fileSize);

    /**
     * Sets the output file size of this Cloudlet after execution (in bytes).
     * It is the data produced as result of cloudlet execution
     * that needs to be transferred thought the network to
     * simulate sending response data to the user.
     *
     * @param outputSize the output size to set (in bytes)
     * @return
     * @throws IllegalArgumentException when the given size is lower or equal to zero
     */
    Cloudlet setOutputSize(long outputSize);

    /**
     * Sets the status of this Cloudlet.
     *
     * <p><b>WARNING</b>: This method is just used internally by classes such as
     * {@link CloudletScheduler} to update Cloudlet status.
     * Calling it directly might not get the expected result.
     * You have to use the CloudletScheduler that controls the execution
     * of the Cloudlet to change the Cloudlets status.
     * The method is public due to a design issue.
     * </p>
     *
     * @param newStatus the status of this Cloudlet
     * @return true if the cloudlet status was changed,
     * i.e, if the newStatus is different from the current status; false otherwise
     */
    boolean setStatus(Status newStatus);

    /**
     * Sets the Type of Service (ToS) for sending this cloudlet over a
     * network.
     *
     * @param netServiceLevel the new type of service (ToS) of this cloudlet
     * @return <code>true</code> if the netServiceLevel is valid, false otherwise.
     */
    boolean setNetServiceLevel(int netServiceLevel);

    /**
     * Sets the number of PEs required to run this Cloudlet. <br>
     * NOTE: The Cloudlet length is computed only for 1 PE for simplicity. <br>
     * For example, consider a Cloudlet that has a length of 500 MI and requires
     * 2 PEs. This means each PE will execute 500 MI of this Cloudlet.
     *
     * @param numberOfPes number of PEs
     * @return
     * @throw IllegalArgumentException when the number of PEs is lower or equal to zero
     */
    Cloudlet setNumberOfPes(long numberOfPes);

    /**
     * Sets the <b>same utilization model</b> for defining the usage of Bandwidth, CPU and RAM.
     * To set different utilization models for each one of these resources, use the
     * respective setters.
     *
     * @param utilizationModel the new utilization model for BW, CPU and RAM
     * @return
     * @see #setUtilizationModelBw(UtilizationModel)
     * @see #setUtilizationModelCpu(UtilizationModel)
     * @see #setUtilizationModelRam(UtilizationModel)
     */
    Cloudlet setUtilizationModel(UtilizationModel utilizationModel);

    /**
     * Sets the {@link #getUtilizationModelBw() utilization model of bw}.
     *
     * @param utilizationModelBw the new utilization model of bw
     */
    Cloudlet setUtilizationModelBw(UtilizationModel utilizationModelBw);

    /**
     * Sets the {@link #getUtilizationModelCpu() utilization model of cpu}.
     *
     * @param utilizationModelCpu the new utilization model of cpu
     */
    Cloudlet setUtilizationModelCpu(UtilizationModel utilizationModelCpu);

    /**
     * Sets the {@link #getUtilizationModelRam() utilization model of ram}.
     *
     * @param utilizationModelRam the new utilization model of ram
     */
    Cloudlet setUtilizationModelRam(UtilizationModel utilizationModelRam);

    /**
     * Sets the id of {@link Vm} that is planned to execute the cloudlet.
     *
     * @param vm the id of vm to run the cloudlet
     */
    Cloudlet setVm(Vm vm);

    /**
     * Gets the execution length of this Cloudlet (in Million Instructions (MI))
     * that will be executed in each defined PE.
     *
     * <p>In case the length is a negative value, it means
     * the Cloudlet doesn't have a defined length, this way,
     * it keeps running until a {@link CloudSimTags#CLOUDLET_FINISH}
     * message is sent to the {@link DatacenterBroker}.</p>
     *
     * <p>According to this length and the power of the VM processor (in
     * Million Instruction Per Second - MIPS) where the cloudlet will be run,
     * the cloudlet will take a given time to finish processing. For instance,
     * for a cloudlet of 10000 MI running on a processor of 2000 MIPS, the
     * cloudlet will spend 5 seconds using the processor in order to be
     * completed (that may be uninterrupted or not, depending on the scheduling
     * policy).
     * </p>
     *
     * @return the length of this Cloudlet
     * @see #getTotalLength()
     * @see #getNumberOfPes()
     */
    long getLength();

    /**
     * Sets the execution length of this Cloudlet (in Million Instructions (MI))
     * that will be executed in each defined PE.
     *
     * <p>In case the length is a negative value, it means
     * the Cloudlet doesn't have a defined length, this way,
     * it keeps running until a {@link CloudSimTags#CLOUDLET_FINISH}
     * message is sent to the {@link DatacenterBroker}.</p>

     * <p>According to this length and the power of the VM processor (in
     * Million Instruction Per Second - MIPS) where the cloudlet will be run,
     * the cloudlet will take a given time to finish processing. For instance,
     * for a cloudlet of 10000 MI running on a processor of 2000 MIPS, the
     * cloudlet will spend 5 seconds using the processor in order to be
     * completed (that may be uninterrupted or not, depending on the scheduling
     * policy).
     * </p>
     *
     * @param length the length (in MI) of this Cloudlet to be executed in a Vm
     * @return
     * @throws IllegalArgumentException when the given length is lower or equal to zero
     *
     * @see #getLength()
     * @see #getTotalLength()
     */
    Cloudlet setLength(long length);

    /**
     * Gets the total length (across all PEs) of this Cloudlet (in MI). It considers the
     * {@link #getLength()} of the cloudlet will be executed in each Pe defined by
     * {@link #getNumberOfPes()}.
     *
     * <p>For example, setting the cloudletLenght as 10000 MI and
     * {@link #getNumberOfPes()} to 4, each Pe will execute 10000 MI.
     * Thus, the entire Cloudlet has a total length of 40000 MI.
     * </p>
     *
     * @return the total length of this Cloudlet (in MI)
     * @see #getNumberOfPes()
     * @see #getLength()
     */
    long getTotalLength();

    /**
     * Gets the length of this Cloudlet that has been executed so far from the
     * latest Datacenter (in MI). This method is useful when trying to move this
     * Cloudlet into different Datacenter or to cancel it.
     *
     * @return the length of a partially executed Cloudlet, or the full Cloudlet
     * length if it is completed
     */
    long getFinishedLengthSoFar();

    /**
     * Gets the length of this Cloudlet that has been executed so far (in MI),
     * according to the {@link #getLength()}.
     * This method is useful when trying to move this Cloudlet
     * into different Datacenters or to cancel it.
     *
     * @param datacenter the Datacenter entity
     * @return the length of a partially executed Cloudlet; the full Cloudlet
     * length if it is completed; or 0 if the Cloudlet has never been executed
     * in the given Datacenter
     */
    long getFinishedLengthSoFar(Datacenter datacenter);

    /**
     * Adds the partial length of this Cloudlet that has executed so far (in MI).
     *
     * @param partialFinishedMI the partial executed length of this Cloudlet (in MI)
     *                          from the last time span (the last time the Cloudlet execution was updated)
     * @return true if the length is valid and the cloudlet already has assigned
     * to a Datacenter, false otherwise
     * @see CloudletExecution
     */
    boolean addFinishedLengthSoFar(long partialFinishedMI);

    /**
     * Sets the wall clock time the cloudlet spent
     * executing on the current Datacenter.
     * The wall clock time is the total time the Cloudlet resides in a Datacenter
     * (from arrival time until departure time, that may include waiting time).
     * This value is set by the Datacenter before departure or sending back to
     * the original Cloudlet's owner.
     *
     * @param wallTime      the time of this Cloudlet resides in a Datacenter
     *                      (from arrival time until departure time).
     * @param actualCpuTime the total execution time of this Cloudlet in a
     *                      Datacenter.
     * @return true if the submission time is valid and
     * the cloudlet has already being assigned to a Datacenter for execution
     */
    boolean setWallClockTime(double wallTime, double actualCpuTime);

    /**
     * Sets the {@link #getExecStartTime() latest execution start time} of this Cloudlet.
     * <br>
     * <b>NOTE:</b> With new functionalities, such as being able to cancel / to
     * pause / to resume this Cloudlet, the execution start time only holds the
     * latest one. Meaning, all previous execution start time are ignored.
     *
     * @param clockTime the latest execution start time
     */
    void setExecStartTime(double clockTime);

    /**
     * Adds a Listener object that will be notified every time
     * the processing of the Cloudlet is updated in its {@link Vm}.
     *
     * @param listener the listener to add
     * @see #getFinishedLengthSoFar()
     */
    Cloudlet addOnUpdateProcessingListener(EventListener<CloudletVmEventInfo> listener);

    /**
     * Removes a listener from the onUpdateCloudletProcessingListener List.
     *
     * @param listener the listener to remove
     * @return true if the listener was found and removed, false otherwise
     */
    boolean removeOnUpdateProcessingListener(EventListener<CloudletVmEventInfo> listener);

    /**
     * Adds a Listener object that will be notified when a cloudlet finishes
     * its execution at a given {@link Vm}.
     *
     * @param listener the listener to add
     * @return
     */
    Cloudlet addOnFinishListener(EventListener<CloudletVmEventInfo> listener);

    /**
     * Removes a listener from the onCloudletFinishEventListener List
     *
     * @param listener the listener to remove
     * @return true if the listener was found and removed, false otherwise
     * @see #addOnFinishListener(EventListener)
     */
    boolean removeOnFinishListener(EventListener<CloudletVmEventInfo> listener);

    /**
     * Notifies all registered listeners about the update on Cloudlet processing.
     *
     * <p><b>This method is used just internally and must not be called directly.</b></p>
     *
     * @param time the time the event happened
     */
    void notifyOnUpdateProcessingListeners(double time);

    /**
     * Gets the {@link DatacenterBroker} that represents the owner of this Cloudlet.
     *
     * @return the broker or <tt>{@link DatacenterBroker#NULL}</tt> if a broker has not been set yet
     */
    @Override
    DatacenterBroker getBroker();

    /**
     * Sets a {@link DatacenterBroker} that represents the owner of this Cloudlet.
     *
     * @param broker the {@link DatacenterBroker} to set
     */
    @Override
    void setBroker(DatacenterBroker broker);
}
