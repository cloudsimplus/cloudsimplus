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
import org.cloudsimplus.resources.*;
import org.cloudsimplus.utilizationmodels.UtilizationModel;
import org.cloudsimplus.utilizationmodels.UtilizationModelFull;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmGroup;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * An abstract class for {@link Cloudlet} implementations.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 */
@Accessors(makeFinal = false) @Getter @Setter
public non-sealed abstract class CloudletAbstract extends CustomerEntityAbstract implements Cloudlet {
    private long jobId;
    private long pesNumber;

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
    private UtilizationModel utilizationModelCpu;

    @NonNull
    private UtilizationModel utilizationModelRam;

    @NonNull
    private UtilizationModel utilizationModelBw;

    private List<String> requiredFiles;

    @Getter(AccessLevel.NONE)
    private final Set<EventListener<CloudletVmEventInfo>> onStartListeners;
    @Getter(AccessLevel.NONE)
    private final Set<EventListener<CloudletVmEventInfo>> onFinishListeners;
    @Getter(AccessLevel.NONE)
    private final Set<EventListener<CloudletVmEventInfo>> onUpdateProcessingListeners;

    /**
     * Creates a Cloudlet with no priority or id. The id is defined when the Cloudlet is
     * submitted to a {@link DatacenterBroker}. The file size and output size are defined as 1.
     *
     * @param length the length (in MI) of this cloudlet to be executed in a VM
     *               (check out {@link #setLength(long)})
     * @param pesNumber number of {@link Pe}s the Cloudlet will require
     * @param utilizationModel a {@link UtilizationModel} to define how the Cloudlet uses CPU, RAM and BW.
     *                         To define an independent utilization model for each resource,
     *                         call the respective setters.
     *
     * @see #setUtilizationModelCpu(UtilizationModel)
     * @see #setUtilizationModelRam(UtilizationModel)
     * @see #setUtilizationModelBw(UtilizationModel)
     */
    public CloudletAbstract(final long length, final int pesNumber, final UtilizationModel utilizationModel) {
        this(-1, length, pesNumber);
        setUtilizationModel(utilizationModel);
    }

    /**
     * Creates a Cloudlet with no priority or id.
     * The id is defined when the Cloudlet is submitted to
     * a {@link DatacenterBroker}. The file size and output size are defined as 1.
     *
     * <p><b>NOTE:</b> By default, the Cloudlet will use a {@link UtilizationModelFull} to define
     * CPU utilization and a {@link UtilizationModel#NULL} for RAM and BW.
     * To change the default values, use the respective setters.</p>
     *
     * @param length the length (in MI) of this cloudlet to be executed in a VM
     *               (check out {@link #setLength(long)})
     * @param pesNumber number of {@link Pe}s the Cloudlet will require
     */
    public CloudletAbstract(final long length, final int pesNumber) {
        this(-1, length, pesNumber);
    }

    /**
     * Creates a Cloudlet with no priority or id. The id is defined when the Cloudlet is submitted to
     * a {@link DatacenterBroker}. The file size and output size are defined as 1.
     *
     * <p><b>NOTE:</b> By default, the Cloudlet will use a {@link UtilizationModelFull} to define
     * CPU utilization and a {@link UtilizationModel#NULL} for RAM and BW.
     * To change the default values, use the respective setters.</p>
     *
     * @param length the length (in MI) of this cloudlet to be executed in a VM
     *               (check out {@link #setLength(long)})
     * @param pesNumber number of {@link Pe}s the Cloudlet will require
     */
    public CloudletAbstract(final long length, final long pesNumber) {
        this(-1, length, pesNumber);
    }

    /**
     * Creates a Cloudlet with no priority, file size and output size equal to 1.
     *
     * <p><b>NOTE:</b> By default, the Cloudlet will use a {@link UtilizationModelFull} to define
     * CPU utilization and a {@link UtilizationModel#NULL} for RAM and BW.
     * To change the default values, use the respective setters.</p>
     *
     * @param id     id of the Cloudlet
     * @param length the length (in MI) of this cloudlet to be executed in a VM
     *               (check out {@link #setLength(long)})
     * @param pesNumber number of {@link Pe}s the Cloudlet will require
     */
    public CloudletAbstract(final long id, final long length, final long pesNumber) {
        super();

        this.requiredFiles = new LinkedList<>();
        this.setId(id);
        this.setJobId(NOT_ASSIGNED);
        this.setPesNumber(pesNumber);
        this.setLength(length);
        this.setFileSize(1);
        this.setOutputSize(1);
        this.setSubmissionDelay(0.0);
        this.setDcArrivalTime(-1);

        this.reset();

        setUtilizationModelCpu(new UtilizationModelFull());
        setUtilizationModelRam(UtilizationModel.NULL);
        setUtilizationModelBw(UtilizationModel.NULL);
        onStartListeners = new HashSet<>();
        onFinishListeners = new HashSet<>();
        onUpdateProcessingListeners = new HashSet<>();
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
        this.onUpdateProcessingListeners.add(listener);
        return this;
    }

    @Override
    public boolean removeOnUpdateProcessingListener(@NonNull final EventListener<CloudletVmEventInfo> listener) {
        return this.onUpdateProcessingListeners.remove(listener);
    }

    @Override
    public Cloudlet addOnStartListener(@NonNull final EventListener<CloudletVmEventInfo> listener) {
        this.onStartListeners.add(listener);
        return this;
    }

    @Override
    public boolean removeOnStartListener(@NonNull final EventListener<CloudletVmEventInfo> listener) {
        return onStartListeners.remove(listener);
    }

    @Override
    public Cloudlet addOnFinishListener(@NonNull final EventListener<CloudletVmEventInfo> listener) {
        if(listener.equals(EventListener.NULL)){
            return this;
        }

        this.onFinishListeners.add(listener);
        return this;
    }

    @Override
    public boolean removeOnFinishListener(final EventListener<CloudletVmEventInfo> listener) {
        return onFinishListeners.remove(listener);
    }

    @Override
    public void notifyOnUpdateProcessingListeners(final double time) {
        onUpdateProcessingListeners.forEach(listener -> listener.update(CloudletVmEventInfo.of(listener, time, this)));
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
    public final Cloudlet setPesNumber(final long pesNumber) {
        if (pesNumber <= 0) {
            throw new IllegalArgumentException("Cloudlet number of PEs has to be greater than zero.");
        }

        this.pesNumber = pesNumber;
        return this;
    }

    @Override
    public long getFinishedLengthSoFar() {
        if(getLength() > 0) {
            return Math.min(finishedLengthSoFar, absLength());
        }

        /**
         * If the length is negative, it means the Cloudlet doesn't have a fixed length.
         * This way, it keeps running and increasing the executed length
         * until a {@link CloudSimTag#CLOUDLET_FINISH} message is sent to the broker
         * or the simulation is terminated under request (by setting a termination time).*/
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

    @Override
    public boolean addFinishedLengthSoFar(final long partialFinishedMI) {
        if (partialFinishedMI < 0 || dcArrivalTime <= NOT_ASSIGNED) {
            return false;
        }

        /**
         * If the Cloudlet has a defined length (a positive value), then, the partial
         * finished length cannot be greater than the actual total length.
         * If the Cloudlet has a negative length, it means it doesn't have a defined
         * length, so that its length increases indefinitely until
         * a {@link CloudSimTag#CLOUDLET_FINISH} message is sent to the broker. */
        final long maxLengthToAdd = getLength() < 0 ?
                                    partialFinishedMI :
                                    Math.min(partialFinishedMI, absLength()-getFinishedLengthSoFar());
        this.finishedLengthSoFar += maxLengthToAdd;
        setFinishTime();
        returnToBrokerIfFinished();
        return true;
    }

    /**
     * Notifies the broker about the end of execution of the Cloudlet,
     * by returning the Cloudlet to it.
     */
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
            onStartListeners.forEach(listener -> listener.update(CloudletVmEventInfo.of(listener, time, this)));
        }
    }

    @Override
    protected void onFinish(final double time) {
        onFinishListeners.forEach(listener -> listener.update(CloudletVmEventInfo.of(listener, this)));
        onFinishListeners.clear();
    }

    @Override
    public final Cloudlet setLength(final long length) {
        if (length == 0) {
            throw new IllegalArgumentException("Cloudlet length cannot be zero.");
        }

        this.length = length;
        return this;
    }

    /**
     * @return the absolute value of the length (without the signal).
     * Check out {@link #getLength()} for details.
     */
    protected long absLength(){
        /*Since the getLength is overridden by classes such as the NetworkCloudlet,
        * we have to call the method instead of directly using the length attribute.*/
        return Math.abs(getLength());
    }

    @Override
    public long getTotalLength() {
        return getLength() * pesNumber;
    }

    /**
     * Sets the time the Cloudlet arrived at a Datacenter to be executed.
     * A negative value indicates there is no arrival time yet
     * @param dcArrivalTime the arrival time to set (in seconds)
     */
    protected final void setDcArrivalTime(final double dcArrivalTime) {
        this.dcArrivalTime = dcArrivalTime;
    }

    @Override
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

    /**
     * Sets the list of {@link #getRequiredFiles() required files}.
     *
     * @param requiredFiles the new list of required files
     */
    public final void setRequiredFiles(@NonNull final List<String> requiredFiles) {
        this.requiredFiles = requiredFiles;
    }

    @Override
    public boolean addRequiredFile(final String fileName) {
        if (getRequiredFiles().stream().anyMatch(reqFile -> reqFile.equals(fileName))) {
            return false;
        }

        requiredFiles.add(fileName);
        return true;
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
        for (int i = 0; i < getRequiredFiles().size(); i++) {
            final String currentFile = requiredFiles.get(i);

            if (currentFile.equals(filename)) {
                requiredFiles.remove(i);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasRequiresFiles() {
        return !getRequiredFiles().isEmpty();
    }

    @Override
    public UtilizationModel getUtilizationModel(final Class<? extends ResourceManageable> resourceClass) {
        if(resourceClass.isAssignableFrom(VmRam.class)){
            return utilizationModelRam;
        }

        if(resourceClass.isAssignableFrom(Bandwidth.class)){
            return utilizationModelBw;
        }

        if(resourceClass.isAssignableFrom(Processor.class) || resourceClass.isAssignableFrom(Pe.class)){
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

    @Override
    public double registerArrivalInDatacenter() {
        setDcArrivalTime(getSimulation().clock());
        return dcArrivalTime;
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException("This method is not implemented for Cloudlet yet");
    }
}
