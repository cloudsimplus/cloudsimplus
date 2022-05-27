/*
 * Title: CloudSim Toolkit Description: CloudSim (Cloud Simulation) Toolkit for Modeling and
 * Simulation of Clouds Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.cloudlets;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.CloudSimTag;
import org.cloudbus.cloudsim.core.CustomerEntity;
import org.cloudbus.cloudsim.core.UniquelyIdentifiable;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.traces.google.GoogleTaskEventsTraceReader;

import java.util.List;

/**
 * An interface to be implemented by each class that provides basic
 * features for cloud applications, aka Cloudlets.
 *
 * <p>The interface implements the Null Object Design
 * Pattern in order to start avoiding {@link NullPointerException}
 * when using the {@link Cloudlet#NULL} object instead
 * of attributing {@code null} to {@link Cloudlet} variables.
 * </p>
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface Cloudlet extends UniquelyIdentifiable, Comparable<Cloudlet>, CustomerEntity {
    /**
     * Execution Status of Cloudlets.
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
         * The Cloudlet is in the waiting queue, but it won't be automatically moved
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
         * into <b>RESUMED</b>.
         */
        PAUSED,

        /**
         * The Cloudlet has been resumed from <b>PAUSED</b> state.
         */
        RESUMED,

        /**
         * The cloudlet has failed to start in reason of a failure in some resource such as a Host or VM.
         * That may happen too when the VM to run the Cloudlet cannot be created for some reason
         * (such as the lack of a suitable Host).
         */
        FAILED_RESOURCE_UNAVAILABLE
    }

    /**
     * Indicates that the Cloudlet was not assigned to a Datacenter yet.
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
     * @return true if the file was added (it didn't exist in the
     * list of required files), false otherwise (it did already exist)
     */
    boolean addRequiredFile(String fileName);

    /**
     * Adds a list of files to the required files list.
     * Just the files that don't exist yet in the current required list
     * will be added.
     *
     * @param fileNames the list of files to be added
     * @return true if at least one file was added,
     * false if no file was added (in the case that all given files
     * already exist in the current required list)
     */
    boolean addRequiredFiles(List<String> fileNames);

    /**
     * Deletes the given filename from the list.
     *
     * @param filename the given filename to be deleted
     * @return true if the file was found and removed,
     *         false if not found
     */
    boolean deleteRequiredFile(String filename);

    /**
     * Checks whether this cloudlet requires any files or not.
     *
     * @return true if required, false otherwise
     */
    boolean hasRequiresFiles();

    /**
     * Gets the list of required files to be used by the cloudlet (if any).
     * The time to transfer these files by the network is considered when
     * placing the cloudlet inside a given VM
     *
     * @return the required files
     */
    List<String> getRequiredFiles();

    /**
     * Gets the time the Cloudlet arrived at a Datacenter to be executed.
     * @return the arrival time in seconds.
     */
    double getArrivalTime();

    /**
     * Gets the total execution time of the Cloudlet so far (in seconds),
     * if the Cloudlet has finished already or not.
     *
     * @return
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
     * {@inheritDoc}
     *
     * <p>If the VM where the Cloudlet will run is submitted with some delay,
     * the {@link DatacenterBroker} waits the VM creation.
     * Only after the VM is created, the Cloudlet creation is requested
     * with the delay specified here.</p>
     * @return {@inheritDoc}
     */
    @Override
    double getSubmissionDelay();

    /**
     * Checks if the Cloudlet has finished and returned to the broker,
     * so that the broker is aware about the end of execution of the Cloudlet.
     * @return
     */
    boolean isReturnedToBroker();

    /**
     * Register the arrival time of this Cloudlet into a Datacenter to the
     * current simulation time and returns this time.
     *
     * @return the arrived time set;
     *         or {@link #NOT_ASSIGNED} if the cloudlet is not assigned to a Datacenter
     */
    double registerArrivalInDatacenter();

    /**
     * Gets the latest execution start time of this Cloudlet (in seconds).
     * This attribute only stores the latest
     * execution time. Previous execution time are ignored.
     * This time represents the simulation second when the cloudlet started.
     *
     * @return the latest execution start time (in seconds)
     */
    double getExecStartTime();

    /**
     * Gets the time when this Cloudlet has completed executing in the latest Datacenter.
     * This time represents the simulation second when the cloudlet finished.
     *
     * @return the finish or completion time of this Cloudlet (in seconds);
     *         or {@link #NOT_ASSIGNED} if not finished yet.
     */
    double getFinishTime();

    /**
     * Gets the id of the job that this Cloudlet belongs to, if any.
     * This field is just used for classification.
     * If there is an supposed job that multiple Cloudlets belong to,
     * one can set the job id for all Cloudlets of that job
     * in order to classify them.
     * Besides classification, this field doesn't have any effect.
     *
     * @return the job id;
     *         or {@link #NOT_ASSIGNED} if the Cloudlet doesn't belong to a job
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
     * How the priority is interpreted and what is the range of values it accepts
     * depends on the {@link CloudletScheduler}
     * that is being used by the Vm running the Cloudlet.
     *
     * @return priority of this cloudlet
     */
    int getPriority();

    /**
     * Sets the {@link #getPriority() priority} of this Cloudlet for scheduling inside a Vm.
     * Each {@link CloudletScheduler} implementation can define if it will
     * use this attribute to impose execution priorities or not.
     *
     * <p><b>WARNING</b>: How the priority is interpreted and what is the range of values it accepts
     * depends on the {@link CloudletScheduler}
     * that is being used by the Vm running the Cloudlet.
     * </p>
     *
     * @param priority the priority to set
     * @return
     */
    Cloudlet setPriority(int priority);

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
     * Gets the utilization model that defines how the cloudlet will use the VM's
     * bandwidth (bw).
     * @return
     */
    UtilizationModel getUtilizationModelBw();

    /**
     * Gets the utilization model that defines how the cloudlet will use the VM's CPU.
     * @return
     */
    UtilizationModel getUtilizationModelCpu();

    /**
     * Gets the utilization model that defines how the cloudlet will use the VM's RAM.
     * @return
     */
    UtilizationModel getUtilizationModelRam();

    /**
     * Gets the utilization model for a given resource
     *
     * @param resourceClass the kind of resource to get its {@link UtilizationModel}
     * @return
     */
    UtilizationModel getUtilizationModel(Class<? extends ResourceManageable> resourceClass);

    /**
     * Gets the utilization of CPU at the current simulation time, that is defined in
     * percentage (from [0 to 1]) or absolute values, depending on the
     * {@link UtilizationModel#getUnit()} set for the
     * {@link #getUtilizationModelCpu() CPU utilizaton model}.
     *
     * @return the utilization % (from [0 to 1])
     * @see #getUtilizationModelCpu()
     */
    double getUtilizationOfCpu();

    /**
     * Gets the utilization of CPU at a given time, that is defined in
     * percentage (from [0 to 1]) or absolute values, depending on the
     * {@link UtilizationModel#getUnit()} defined for the {@link #getUtilizationModelCpu()}.
     *
     * @param time the time to get the utilization
     * @return the utilization % (from [0 to 1])
     * @see #getUtilizationModelCpu()
     */
    double getUtilizationOfCpu(double time);

    /**
     * Gets the utilization of RAM at the current simulation time, that is defined in
     * percentage (from [0 to 1]) or absolute values, depending on the
     * {@link UtilizationModel#getUnit()} set for the
     * {@link #getUtilizationModelRam() RAM utilizaton model}.
     *
     * @return the utilization % (from [0 to 1])
     * @see #getUtilizationModelRam()
     */
    double getUtilizationOfRam();

    /**
     * Gets the utilization of RAM at a given time, that is defined in
     * percentage (from [0 to 1]) or absolute values, depending on the
     * {@link UtilizationModel#getUnit()} defined for the {@link #getUtilizationModelRam()}.
     *
     * @param time the time to get the utilization
     * @return the utilization % (from [0 to 1])
     * @see #getUtilizationModelRam()
     */
    double getUtilizationOfRam(double time);

    /**
     * Gets the utilization of Bandwidth at the current simulation time, that is defined in
     * percentage (from [0 to 1]) or absolute values, depending on the
     * {@link UtilizationModel#getUnit()} set for the
     * {@link #getUtilizationModelBw() BW utilizaton model}.
     *
     * @return the utilization % (from [0 to 1])
     * @see #getUtilizationModelCpu()
     */
    double getUtilizationOfBw();

    /**
     * Gets the utilization of Bandwidth at a given time, that is defined in
     * percentage (from [0 to 1]) or absolute values, depending on the
     * {@link UtilizationModel#getUnit()} defined for the {@link #getUtilizationModelBw()}.
     *
     * @param time the time to get the utilization
     * @return the utilization % (from [0 to 1])
     * @see #getUtilizationModelBw()
     */
    double getUtilizationOfBw(double time);

    /**
     * Gets the Vm that is planned to execute the cloudlet.
     *
     * @return the VM; or {@link Vm#NULL} if the Cloudlet was not assigned to a VM yet
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
    boolean isBoundToVm();

    /**
     * Gets the time (in seconds) the cloudlet had to wait before start executing on a
     * resource.
     *
     * @return the waiting time (in seconds) when the cloudlet waited to execute;
     *         or 0 if there wasn't any waiting time
     *         or the cloudlet hasn't started to execute.
     */
    double getWaitingTime();

    /**
     * Checks whether this Cloudlet has finished executing or not.
     *
     * @return true if this Cloudlet has finished execution;
     *         false otherwise
     */
    boolean isFinished();

    /**
     * Sets the input file size of this Cloudlet before execution (in bytes).
     * This size has to be considered the program + input data sizes.
     *
     * @param fileSize the size to set (in bytes)
     * @return
     * @throws IllegalArgumentException when the given size is lower or equal to zero
     * @see #setSizes(long)
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
     * @see #setSizes(long)
     */
    Cloudlet setOutputSize(long outputSize);

    /**
     * Sets the input and output file sizes of this Cloudlet to <b>the same value (in bytes)</b>.
     *
     * @param size the value to set (in bytes) for input and output size
     * @return
     * @throws IllegalArgumentException when the given size is lower or equal to zero
     *
     * @see #setFileSize(long)
     * @see #setOutputSize(long)
     */
    Cloudlet setSizes(long size);

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
     *         i.e, if the newStatus is different from the current status;
     *         false otherwise
     */
    boolean setStatus(Status newStatus);

    /**
     * Sets the Type of Service (ToS) for sending this cloudlet over a network.
     *
     * @param netServiceLevel the new type of service (ToS) of this cloudlet
     */
    void setNetServiceLevel(int netServiceLevel);

    /**
     * Sets the number of PEs required to run this Cloudlet. <br>
     * NOTE: The Cloudlet length is computed only for 1 PE for simplicity. <br>
     * For example, consider a Cloudlet that has a length of 500 MI and requires
     * 2 PEs. This means each PE will execute 500 MI of this Cloudlet.
     *
     * @param numberOfPes number of PEs
     * @return
     * @throws IllegalArgumentException when the number of PEs is lower or equal to zero
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
     * @param utilizationModelBw the new utilization model of bw
     */
    Cloudlet setUtilizationModelBw(UtilizationModel utilizationModelBw);

    /**
     * Sets the {@link #getUtilizationModelCpu() utilization model of cpu}.
     * @param utilizationModelCpu the new utilization model of cpu
     */
    Cloudlet setUtilizationModelCpu(UtilizationModel utilizationModelCpu);

    /**
     * Sets the {@link #getUtilizationModelRam() utilization model of ram}.
     * @param utilizationModelRam the new utilization model of ram
     */
    Cloudlet setUtilizationModelRam(UtilizationModel utilizationModelRam);

    /**
     * Sets the id of {@link Vm} that is planned to execute the cloudlet.
     * @param vm the id of vm to run the cloudlet
     */
    Cloudlet setVm(Vm vm);

    /**
     * Gets the execution length of this Cloudlet (in Million Instructions (MI))
     * that will be executed in each defined PE.
     *
     * <p>In case the length is a negative value, it means
     * the Cloudlet doesn't have a defined length, this way,
     * it keeps running until a {@link CloudSimTag#CLOUDLET_FINISH}
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
     * @return the length of this Cloudlet (in MI)
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
     * it keeps running until a {@link CloudSimTag#CLOUDLET_FINISH}
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
     * <p>For example, setting the cloudletLength as 10000 MI and
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
     * @return the length of a partially executed Cloudlet,
     *         or the full Cloudlet length if it is completed (in MI)
     */
    long getFinishedLengthSoFar();

    /**
     * Adds the partial length of this Cloudlet that has executed so far (in MI).
     *
     * @param partialFinishedMI the partial executed length of this Cloudlet (in MI) from the
     *                          last time span (the last time the Cloudlet execution was updated)
     * @return true if the length is valid and the cloudlet already has assigned to a Datacenter;
     *         false otherwise
     * @see CloudletExecution
     */
    boolean addFinishedLengthSoFar(long partialFinishedMI);

    /**
     * Sets the {@link #getExecStartTime() latest execution start time} of this Cloudlet.
     *
     * <p>
     * <b>NOTE:</b> The execution start time only holds the
     * latest one. Meaning all previous execution start times are ignored.
     * </p>
     *
     * @param clockTime the latest execution start time
     */
    void setExecStartTime(double clockTime);

    /**
     * Adds a Listener object that will be notified when
     * the Cloudlet starts executing in some {@link Vm}.
     *
     * @param listener the listener to add
     */
    Cloudlet addOnStartListener(EventListener<CloudletVmEventInfo> listener);

    /**
     * Removes a listener from the onStartListener List.
     *
     * @param listener the listener to remove
     * @return true if the listener was found and removed; false otherwise
     */
    boolean removeOnStartListener(EventListener<CloudletVmEventInfo> listener);

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
     * @return true if the listener was found and removed; false otherwise
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
     * @return true if the listener was found and removed; false otherwise
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
     * @return the broker or <b>{@link DatacenterBroker#NULL}</b> if a broker has not been set yet
     */
    @Override
    DatacenterBroker getBroker();

    /**
     * Sets a {@link DatacenterBroker} that represents the owner of this Cloudlet.
     * @param broker the {@link DatacenterBroker} to set
     */
    @Override
    void setBroker(DatacenterBroker broker);

    /**
     * Resets the state of the Cloudlet.
     * @return
     */
    Cloudlet reset();

    /**
     * Sets the lifeTime of this cloudlet,
     * which indicates its maximum {@link #getActualCpuTime() execution time},
     * regardless of its length (in MI).
     *
     * <p>The cloudlet will finish execution as soon as possible, after the given lifeTime has passed,
     * since its {@link #getExecStartTime() exec start time}.
     * </p>
     *
     * <b>IMPORTANT</b>: Currently lifeTime must be larger than {@link Datacenter#getSchedulingInterval()}.
     * @param lifeTime lifeTime of this Cloudlet (in seconds)
     */
	Cloudlet setLifeTime(double lifeTime);

    /**
     * Gets the lifeTime of this cloudlet,
     * which indicates its maximum {@link #getActualCpuTime()} execution time},
     * regardless of its length (in MI).
     *
     * @return lifeTime of this cloudlet (in seconds).
     * @see #setLifeTime(double)
     */
	double getLifeTime();
}
