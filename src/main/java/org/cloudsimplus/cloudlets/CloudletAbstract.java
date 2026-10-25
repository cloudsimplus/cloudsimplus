/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.cloudlets;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.core.CustomerEntityAbstract;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.Listeners;
import org.cloudsimplus.resources.*;
import org.cloudsimplus.utilizationmodels.UtilizationModel;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/// An abstract class for [Cloudlet] implementations.
///
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
@Accessors(makeFinal = false) @Getter @Setter
public non-sealed abstract class CloudletAbstract extends CustomerEntityAbstract implements Cloudlet {
    private long jobId;
    private long requiredCores;

    @NonNull
    private Status status;

    private long length;

    @Setter(AccessLevel.NONE)
    private long finishedLengthSoFar;

    @Setter(AccessLevel.NONE)
    private boolean returnedToBroker;
    private int priority;
    private int netServiceLevel;
    private long fileSize;
    private long outputSize;
    private double dcArrivalTime;
    private double submissionDelay;

    @NonNull
    private Vm vm;

    @NonNull
    private UtilizationModel utilizationModelCpu = UtilizationModel.NULL;

    @NonNull
    private UtilizationModel utilizationModelRam = UtilizationModel.NULL;

    @NonNull
    private UtilizationModel utilizationModelBw = UtilizationModel.NULL;

    /// The list of required files, which starts as the shared empty `List.of()`
    /// and is only created when the first file is added.
    /// This way, the many Cloudlets that don't require files don't allocate an empty list.
    /// @see #mutableRequiredFiles()
    @Getter(AccessLevel.NONE)
    private List<String> requiredFiles = List.of();

    /// @see #addOnStartListener(EventListener)
    @Getter(AccessLevel.NONE)
    private List<EventListener<CloudletVmEventInfo>> onStartListeners = List.of();

    /// @see #addOnFinishListener(EventListener)
    @Getter(AccessLevel.NONE)
    private List<EventListener<CloudletVmEventInfo>> onFinishListeners = List.of();

    /// @see #addOnUpdateProcessingListener(EventListener)
    @Getter(AccessLevel.NONE)
    private List<EventListener<CloudletVmEventInfo>> onUpdateProcessingListeners = List.of();

    /// Creates a Cloudlet with no priority or id. The id is defined when the Cloudlet is
    /// submitted to a [DatacenterBroker]. The file size and output size are defined as 1.
    ///
    /// @param length the length (in MI) of this cloudlet to be executed in a VM
    /// (check out [#setLength(long)])
    /// @param requiredCores number of CPU cores the Cloudlet will require
    /// @param utilizationModel a [UtilizationModel] to define how the Cloudlet uses CPU, RAM and BW.
    /// To define an independent utilization model for each resource,
    /// call the respective setters.
    ///
    /// @see #setUtilizationModelCpu(UtilizationModel)
    /// @see #setUtilizationModelRam(UtilizationModel)
    /// @see #setUtilizationModelBw(UtilizationModel)
    public CloudletAbstract(final long length, final int requiredCores, final UtilizationModel utilizationModel) {
        this(-1, length, requiredCores);
        setUtilizationModel(utilizationModel);
    }

    /// Creates a Cloudlet with no priority or id.
    /// The id is defined when the Cloudlet is submitted to
    /// a [DatacenterBroker]. The file size and output size are defined as 1.
    ///
    /// **NOTE:** By default, the Cloudlet will use a [UtilizationModel#FULL] to define
    /// CPU utilization and a [UtilizationModel#NULL] for RAM and BW.
    /// To change the default values, use the respective setters.
    ///
    /// @param length the length (in MI) of this cloudlet to be executed in a VM
    /// (check out [#setLength(long)])
    /// @param requiredCores number of CPU cores the Cloudlet will require
    public CloudletAbstract(final long length, final int requiredCores) {
        this(-1, length, requiredCores);
    }

    /// Creates a Cloudlet with no priority or id. The id is defined when the Cloudlet is submitted to
    /// a [DatacenterBroker]. The file size and output size are defined as 1.
    ///
    /// **NOTE:** By default, the Cloudlet will use a [UtilizationModel#FULL] to define
    /// CPU utilization and a [UtilizationModel#NULL] for RAM and BW.
    /// To change the default values, use the respective setters.
    ///
    /// @param length the length (in MI) of this cloudlet to be executed in a VM
    /// (check out [#setLength(long)])
    /// @param requiredCores number of CPU cores the Cloudlet will require
    public CloudletAbstract(final long length, final long requiredCores) {
        this(-1, length, requiredCores);
    }

    /// Creates a Cloudlet with no priority, file size and output size equal to 1.
    ///
    /// **NOTE:** By default, the Cloudlet will use a [UtilizationModel#FULL] to define
    /// CPU utilization and a [UtilizationModel#NULL] for RAM and BW.
    /// To change the default values, use the respective setters.
    ///
    /// @param id     id of the Cloudlet
    /// @param length the length (in MI) of this cloudlet to be executed in a VM
    /// (check out [#setLength(long)])
    /// @param requiredCores number of CPU cores the Cloudlet will require
    public CloudletAbstract(final long id, final long length, final long requiredCores) {
        super();

        this.setId(id);
        this.setJobId(NOT_ASSIGNED);
        this.setRequiredCores(requiredCores);
        this.setLength(length);
        this.setFileSize(1);
        this.setOutputSize(1);
        this.setSubmissionDelay(0.0);
        this.setDcArrivalTime(-1);

        this.reset();

        setUtilizationModelCpu(UtilizationModel.FULL);
        setUtilizationModelRam(UtilizationModel.NULL);
        setUtilizationModelBw(UtilizationModel.NULL);
    }

    @Override
    public final Cloudlet reset() {
        this.netServiceLevel = 0;
        setBroker(DatacenterBroker.NULL);
        this.vm = Vm.NULL;
        this.setStartTime(NOT_ASSIGNED);
        setFinishTime(NOT_ASSIGNED); // meaning this Cloudlet hasn't finished yet
        this.finishedLengthSoFar = 0;
        this.status = Status.INSTANTIATED;
        this.priority = 0;
        this.setBrokerArrivalTime(NOT_ASSIGNED);
        setCreationTime(NOT_ASSIGNED);

        this.setLastTriedDatacenter(Datacenter.NULL);
        return this;
    }

    @Override
    public Cloudlet setUtilizationModel(@NonNull final UtilizationModel utilizationModel) {
        setUtilizationModelBw(utilizationModel);
        setUtilizationModelRam(utilizationModel);
        setUtilizationModelCpu(utilizationModel);
        return this;
    }

    @Override
    public Cloudlet addOnUpdateProcessingListener(@NonNull final EventListener<CloudletVmEventInfo> listener) {
        onUpdateProcessingListeners = Listeners.add(onUpdateProcessingListeners, listener);
        return this;
    }

    @Override
    public boolean removeOnUpdateProcessingListener(@NonNull final EventListener<CloudletVmEventInfo> listener) {
        final var oldList = onUpdateProcessingListeners;
        onUpdateProcessingListeners = Listeners.remove(oldList, listener);
        return onUpdateProcessingListeners.size() != oldList.size();
    }

    @Override
    public Cloudlet addOnStartListener(@NonNull final EventListener<CloudletVmEventInfo> listener) {
        onStartListeners = Listeners.add(onStartListeners, listener);
        return this;
    }

    @Override
    public boolean removeOnStartListener(@NonNull final EventListener<CloudletVmEventInfo> listener) {
        final var oldList = onStartListeners;
        onStartListeners = Listeners.remove(oldList, listener);
        return onStartListeners.size() != oldList.size();
    }

    @Override
    public Cloudlet addOnFinishListener(@NonNull final EventListener<CloudletVmEventInfo> listener) {
        if(listener.equals(EventListener.NULL)){
            return this;
        }

        onFinishListeners = Listeners.add(onFinishListeners, listener);
        return this;
    }

    @Override
    public boolean removeOnFinishListener(final EventListener<CloudletVmEventInfo> listener) {
        final var oldList = onFinishListeners;
        onFinishListeners = Listeners.remove(oldList, listener);
        return onFinishListeners.size() != oldList.size();
    }

    /// Notifies all registered listeners about the update on Cloudlet processing.
    ///
    /// **This method is used just internally and must not be called directly.**
    ///
    /// @param time the time the event happened
    public void notifyOnUpdateProcessingListeners(final double time) {
        for (final var listener : onUpdateProcessingListeners) {
            listener.update(CloudletVmEventInfo.of(listener, time, this));
        }
    }

    private void setFinishTime(){
        final long len = getLength();
        if(isLifeTimeReached() || len > 0 && finishedLengthSoFar >= len)
            setFinishTime(getSimulation().clock());
    }

    @Override
    public Cloudlet setNetServiceLevel(final int netServiceLevel) {
        if (netServiceLevel < 0) {
            throw new IllegalArgumentException("Net Service Level cannot be negative");
        }

        this.netServiceLevel = netServiceLevel;
        return this;
    }

    @Override
    public double getStartWaitTime() {
        return dcArrivalTime > NOT_ASSIGNED ? getStartTime() - dcArrivalTime : NOT_ASSIGNED;
    }

    @Override
    public final Cloudlet setRequiredCores(final long requiredCores) {
        if (requiredCores <= 0) {
            throw new IllegalArgumentException("Cloudlet number of CPU cores has to be greater than zero.");
        }

        this.requiredCores = requiredCores;
        return this;
    }

    @Override
    public long getFinishedLengthSoFar() {
        if(getLength() > 0) {
            return Math.min(finishedLengthSoFar, absLength());
        }

        /// If the length is negative, it means the Cloudlet doesn't have a fixed length.
        /// This way, it keeps running and increasing the executed length
        /// until a [CloudSimTag#CLOUDLET_FINISH] message is sent to the broker
        /// or the simulation is terminated under request (by setting a termination time).
        return finishedLengthSoFar;
    }

    @Override
    public boolean isFinished() {
        return getFinishTime() > NOT_ASSIGNED;
    }

    @Override
    public boolean isLifeTimeReached() {
        return super.isLifeTimeReached() || vm.isLifeTimeReached();
    }

    /// Adds the partial length of this Cloudlet that has executed so far (in MI).
    ///
    /// @param partialFinishedMI the partial executed length of this Cloudlet (in MI) from the
    /// last time span (the last time the Cloudlet execution was updated)
    /// @return true if the length is valid and the cloudlet already has assigned to a Datacenter;
    /// false otherwise
    /// @see CloudletExecution
    public boolean addFinishedLengthSoFar(final long partialFinishedMI) {
        if (partialFinishedMI < 0 || dcArrivalTime <= NOT_ASSIGNED) {
            return false;
        }

        /// If the Cloudlet has a defined length (a positive value), then, the partial
        /// finished length cannot be greater than the actual total length.
        /// If the Cloudlet has a negative length, it means it doesn't have a defined
        /// length, so that its length increases indefinitely until
        /// a [CloudSimTag#CLOUDLET_FINISH] message is sent to the broker.
        final long maxLengthToAdd = getLength() < 0 ?
                                    partialFinishedMI :
                                    Math.min(partialFinishedMI, absLength()-getFinishedLengthSoFar());
        this.finishedLengthSoFar += maxLengthToAdd;
        setFinishTime();
        returnToBrokerIfFinished();
        return true;
    }

    /// Notifies the broker about the end of execution of the Cloudlet,
    /// by returning the Cloudlet to it.
    private void returnToBrokerIfFinished() {
        if(isFinished() && !isReturnedToBroker()){
            returnedToBroker = true;
            final var targetEntity = getSimulation().getCis();
            getSimulation().sendNow(targetEntity, getBroker(), CloudSimTag.CLOUDLET_RETURN, this);
            vm.getCloudletScheduler().addCloudletToReturnedList(this);
        }
    }

    @Override
    protected void onStart(final double time) {
        if(vm != Vm.NULL){
            for (final var listener : onStartListeners) {
                listener.update(CloudletVmEventInfo.of(listener, time, this));
            }
        }
    }

    @Override
    protected void onFinish(final double time) {
        final var listeners = onFinishListeners;
        // Cleared before notifying, so that a listener added during notification is kept for a next finish
        onFinishListeners = List.of();
        for (final var listener : listeners) {
            listener.update(CloudletVmEventInfo.of(listener, this));
        }
    }

    @Override
    public final Cloudlet setLength(final long length) {
        if (length == 0) {
            throw new IllegalArgumentException("Cloudlet length cannot be zero.");
        }

        this.length = length;
        return this;
    }

    /// @return the absolute value of the length (without the signal).
    /// Check out [#getLength()] for details.
    protected long absLength(){
        /*Since the getLength is overridden by classes such as the NetworkCloudlet,
        * we have to call the method instead of directly using the length attribute.*/
        return Math.abs(getLength());
    }

    @Override
    public long getTotalLength() {
        return getLength() * requiredCores;
    }

    /// Sets the time the Cloudlet arrived at a Datacenter to be executed.
    /// A negative value indicates there is no arrival time yet
    /// @param dcArrivalTime the arrival time to set (in seconds)
    protected final void setDcArrivalTime(final double dcArrivalTime) {
        this.dcArrivalTime = dcArrivalTime;
    }

    /// Sets the status of this Cloudlet.
    ///
    /// **WARNING**: This method is just used internally by classes such as
    /// [CloudletScheduler] to update Cloudlet status.
    /// Calling it directly might not get the expected result.
    /// You have to use the CloudletScheduler that controls the execution
    /// of the Cloudlet to change the Cloudlets status.
    /// The method is public due to a design issue.
    ///
    /// @param newStatus the status of this Cloudlet
    /// @return true if the cloudlet status was changed,
    /// i.e, if the newStatus is different from the current status;
    /// false otherwise
    /// TODO Move to abstract class
    /// See https://github.com/cloudsimplus/cloudsimplus/issues/589
    public boolean setStatus(final Status newStatus) {
        if (this.status == newStatus) {
            return false;
        }

        if (newStatus == Status.SUCCESS) {
            setFinishTime(getSimulation().clock());
        }

        this.status = newStatus;
        return true;
    }

    /// Sets the list of [required files][#getRequiredFiles()].
    ///
    /// @param requiredFiles the new list of required files
    public final void setRequiredFiles(@NonNull final List<String> requiredFiles) {
        // Copies the list, since the given one may be immutable and later changes would fail
        this.requiredFiles = requiredFiles.isEmpty() ? List.of() : new ArrayList<>(requiredFiles);
    }

    @Override
    public List<String> getRequiredFiles() {
        return Collections.unmodifiableList(requiredFiles);
    }

    @Override
    public boolean addRequiredFile(final String fileName) {
        if (requiredFiles.contains(fileName)) {
            return false;
        }

        mutableRequiredFiles().add(fileName);
        return true;
    }

    /// {@return the list of required files that can be changed}
    /// Creates the list if the current one is the shared empty `List.of()`.
    private List<String> mutableRequiredFiles() {
        if (requiredFiles.isEmpty()) {
            requiredFiles = new ArrayList<>();
        }

        return requiredFiles;
    }

    @Override
    public boolean addRequiredFiles(final List<String> fileNames) {
        boolean atLeastOneFileAdded = false;
        for (final String fileName : fileNames) {
            atLeastOneFileAdded |= addRequiredFile(fileName);
        }

        return atLeastOneFileAdded;
    }

    @Override
    public boolean deleteRequiredFile(final String filename) {
        // The isEmpty() check avoids calling remove() on the immutable List.of(), which throws an exception
        return !requiredFiles.isEmpty() && requiredFiles.remove(filename);
    }

    @Override
    public boolean hasRequiresFiles() {
        return !requiredFiles.isEmpty();
    }

    @Override
    public UtilizationModel getUtilizationModel(final Class<? extends ResourceManageable> resourceClass) {
        if(resourceClass.isAssignableFrom(VmRam.class)){
            return utilizationModelRam;
        }

        if(resourceClass.isAssignableFrom(Bandwidth.class)){
            return utilizationModelBw;
        }

        if(resourceClass.isAssignableFrom(Processor.class)){
            return utilizationModelCpu;
        }

        throw new UnsupportedOperationException("This class doesn't support " + resourceClass.getSimpleName() + " resources");
    }

    @Override
    public double getUtilizationOfCpu() {
        return getUtilizationOfCpu(getSimulation().clock());
    }

    @Override
    public double getUtilizationOfCpu(final double time) {
        return getUtilizationModelCpu().getUtilization(time);
    }

    @Override
    public double getUtilizationOfBw() {
        return getUtilizationOfBw(getSimulation().clock());
    }

    @Override
    public double getUtilizationOfBw(final double time) {
        return getUtilizationModelBw().getUtilization(time);
    }

    @Override
    public double getUtilizationOfRam() {
        return getUtilizationOfRam(getSimulation().clock());
    }

    @Override
    public double getUtilizationOfRam(final double time) {
        return getUtilizationModelRam().getUtilization(time);
    }

    @Override
    public final void setSubmissionDelay(final double submissionDelay) {
        if (submissionDelay < 0) {
            return;
        }

        this.submissionDelay = submissionDelay;
    }

    @Override
    public boolean isSubmissionDelayed() {
        return submissionDelay > 0;
    }

    @Override
    public boolean isBoundToVm() {
        return vm != null && vm != Vm.NULL && !(vm instanceof VmGroup) && this.getBroker().equals(this.getVm().getBroker());
    }

    @Override
    public final Cloudlet setFileSize(final long fileSize) {
        if (fileSize <= 0) {
            throw new IllegalArgumentException("Cloudlet file size has to be greater than zero.");
        }

        this.fileSize = fileSize;
        return this;
    }

    @Override
    public final Cloudlet setOutputSize(final long outputSize) {
        if (outputSize <= 0) {
            throw new IllegalArgumentException("Cloudlet output size has to be greater than zero.");
        }

        this.outputSize = outputSize;
        return this;
    }

    @Override
    public Cloudlet setSizes(final long size) {
        setFileSize(size);
        setOutputSize(size);
        return this;
    }

    /// Register the arrival time of this Cloudlet into a Datacenter to the
    /// current simulation time and returns this time.
    ///
    /// @return the arrived time set (in seconds);
    /// or [#NOT_ASSIGNED] if the cloudlet is not assigned to a Datacenter
    public double registerArrivalInDatacenter() {
        setDcArrivalTime(getSimulation().clock());
        return dcArrivalTime;
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException("This method is not implemented for Cloudlet yet");
    }
}
