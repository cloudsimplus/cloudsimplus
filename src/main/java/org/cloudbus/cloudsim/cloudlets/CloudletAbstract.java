/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.cloudlets;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.CloudSimTag;
import org.cloudbus.cloudsim.core.CustomerEntityAbstract;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmGroup;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventListener;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * An abstract class for {@link Cloudlet} implementations.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 */
public abstract class CloudletAbstract extends CustomerEntityAbstract implements Cloudlet {

    /** @see #getJobId() */
    private long jobId;

    /** @see #getLength() */
    private long length;

    /** @see #getFinishedLengthSoFar() */
    private long finishedLengthSoFar;

    /** @see #getNumberOfPes() */
    private long numberOfPes;

    /** @see #getStatus() */
    private Status status;

    /** @see #isReturnedToBroker() */
    private boolean returnedToBroker;

    /** @see #getExecStartTime() */
    private double execStartTime;

    /** @see #getPriority() */
    private int priority;

    /** @see #getNetServiceLevel() */
    private int netServiceLevel;

    /** @see #getVm() */
    private Vm vm;

    /** @see #getRequiredFiles() */
    private List<String> requiredFiles;

    /** @see #getFileSize() */
    private long fileSize;

    /** @see #getOutputSize() */
    private long outputSize;

    /** @see #getFinishTime() */
    private double finishTime;

    /** @see #getUtilizationModelCpu() */
    private UtilizationModel utilizationModelCpu;

    /** @see #getUtilizationModelRam() */
    private UtilizationModel utilizationModelRam;

    /** @see #getUtilizationModelBw() */
    private UtilizationModel utilizationModelBw;

    private final Set<EventListener<CloudletVmEventInfo>> onStartListeners;
    private final Set<EventListener<CloudletVmEventInfo>> onFinishListeners;
    private final Set<EventListener<CloudletVmEventInfo>> onUpdateProcessingListeners;

    /** @see #getSubmissionDelay() */
    private double submissionDelay;

    /** @see #getLifeTime() */
    private double lifeTime;

    /** @see #getArrivalTime() */
    private double arrivalTime;

    /**
     * Creates a Cloudlet with no priority or id. The id is defined when the Cloudlet is
     * submitted to a {@link DatacenterBroker}. The file size and output size is defined as 1.
     *
     * @param length the length or size (in MI) of this cloudlet to be executed in a
     *               VM (check out {@link #setLength(long)})
     * @param pesNumber number of PEs that Cloudlet will require
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
     * a {@link DatacenterBroker}. The file size and output size is defined as 1.
     *
     * <p><b>NOTE:</b> By default, the Cloudlet will use a {@link UtilizationModelFull} to define
     * CPU utilization and a {@link UtilizationModel#NULL} for RAM and BW.
     * To change the default values, use the respective setters.</p>
     *
     * @param length the length or size (in MI) of this cloudlet to be executed in a VM
     *               (check out {@link #setLength(long)})
     * @param pesNumber number of PEs that Cloudlet will require
     */
    public CloudletAbstract(final long length, final int pesNumber) {
        this(-1, length, pesNumber);
    }

    /**
     * Creates a Cloudlet with no priority or id. The id is defined when the Cloudlet is submitted to
     * a {@link DatacenterBroker}. The file size and output size is defined as 1.
     *
     * <p><b>NOTE:</b> By default, the Cloudlet will use a {@link UtilizationModelFull} to define
     * CPU utilization and a {@link UtilizationModel#NULL} for RAM and BW.
     * To change the default values, use the respective setters.</p>
     *
     * @param length the length or size (in MI) of this cloudlet to be executed in a VM
     *               (check out {@link #setLength(long)})
     * @param pesNumber number of PEs that Cloudlet will require
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
     * @param length the length or size (in MI) of this cloudlet to be executed in a VM
     *               (check out {@link #setLength(long)})
     * @param pesNumber number of PEs that Cloudlet will require
     */
    public CloudletAbstract(final long id, final long length, final long pesNumber) {
        super();

        this.requiredFiles = new LinkedList<>();
        this.setId(id);
        this.setJobId(NOT_ASSIGNED);
        this.setNumberOfPes(pesNumber);
        this.setLength(length);
        this.setFileSize(1);
        this.setOutputSize(1);
        this.setSubmissionDelay(0.0);
        this.setArrivalTime(-1);

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
        this.execStartTime = 0.0;
        this.status = Status.INSTANTIATED;
        this.priority = 0;
        setBroker(DatacenterBroker.NULL);
        setFinishTime(NOT_ASSIGNED); // meaning this Cloudlet hasn't finished yet
        this.vm = Vm.NULL;
        setExecStartTime(0.0);
        setArrivedTime(0);
        setCreationTime(0);
        setLifeTime(-1);

        this.setLastTriedDatacenter(Datacenter.NULL);
        return this;
    }

    @Override
    public Cloudlet setUtilizationModel(final UtilizationModel utilizationModel) {
        setUtilizationModelBw(utilizationModel);
        setUtilizationModelRam(utilizationModel);
        setUtilizationModelCpu(utilizationModel);
        return this;
    }

    @Override
    public Cloudlet addOnUpdateProcessingListener(final EventListener<CloudletVmEventInfo> listener) {
        this.onUpdateProcessingListeners.add(requireNonNull(listener));
        return this;
    }

    @Override
    public boolean removeOnUpdateProcessingListener(final EventListener<CloudletVmEventInfo> listener) {
        return this.onUpdateProcessingListeners.remove(listener);
    }

    @Override
    public Cloudlet addOnStartListener(final EventListener<CloudletVmEventInfo> listener) {
        this.onStartListeners.add(requireNonNull(listener));
        return this;
    }

    @Override
    public boolean removeOnStartListener(final EventListener<CloudletVmEventInfo> listener) {
        return onStartListeners.remove(listener);
    }

    @Override
    public Cloudlet addOnFinishListener(final EventListener<CloudletVmEventInfo> listener) {
        if(listener.equals(EventListener.NULL)){
            return this;
        }

        this.onFinishListeners.add(requireNonNull(listener));
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

    @Override
    public final Cloudlet setLength(final long length) {
        if (length == 0) {
            throw new IllegalArgumentException("Cloudlet length cannot be zero.");
        }

        this.length = length;
        return this;
    }

    @Override
    public void setNetServiceLevel(final int netServiceLevel) {
        if (netServiceLevel < 0) {
            throw new IllegalArgumentException("Net Service Level cannot be negative");
        }

        this.netServiceLevel = netServiceLevel;
    }

    @Override
    public int getNetServiceLevel() {
        return netServiceLevel;
    }

    @Override
    public double getWaitingTime() {
        return arrivalTime == -1 ? -1 : execStartTime - arrivalTime;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public Cloudlet setPriority(final int priority) {
        this.priority = priority;
        return this;
    }

    @Override
    public final Cloudlet setNumberOfPes(final long numberOfPes) {
        if (numberOfPes <= 0) {
            throw new IllegalArgumentException("Cloudlet number of PEs has to be greater than zero.");
        }

        this.numberOfPes = numberOfPes;
        return this;
    }

    @Override
    public long getNumberOfPes() {
        return numberOfPes;
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
        return (getLifeTime() > 0 && getActualCpuTime() >= getLifeTime()) ||
               (getLength() > 0 && getFinishedLengthSoFar() >= getLength());
    }

    @Override
    public boolean addFinishedLengthSoFar(final long partialFinishedMI) {
        if (partialFinishedMI < 0.0 || arrivalTime == -1) {
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
        finishedLengthSoFar += maxLengthToAdd;
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
            final var targetEntity = getSimulation().getCloudInfoService();
            getSimulation().sendNow(targetEntity, getBroker(), CloudSimTag.CLOUDLET_RETURN, this);
            vm.getCloudletScheduler().addCloudletToReturnedList(this);
        }
    }

    /**
     * Notifies all registered listeners about the termination of the Cloudlet
     * if it in fact has finished.
     * It then removes the registered listeners to avoid a Listener to be notified
     * multiple times about a Cloudlet termination.
     */
    void notifyOnFinishListeners() {
        if (isFinished()) {
            onFinishListeners.forEach(listener -> listener.update(CloudletVmEventInfo.of(listener, this)));
            onFinishListeners.clear();
        }
    }

    @Override
    public long getFileSize() {
        return fileSize;
    }

    @Override
    public long getOutputSize() {
        return outputSize;
    }

    @Override
    public double getExecStartTime() {
        return execStartTime;
    }

    @Override
    public void setExecStartTime(final double clockTime) {
        final boolean isStartingInSomeVm = this.execStartTime <= 0 && clockTime > 0 && vm != Vm.NULL && vm != null;
        this.execStartTime = clockTime;
        if(isStartingInSomeVm){
            onStartListeners.forEach(listener -> listener.update(CloudletVmEventInfo.of(listener, clockTime, this)));
        }
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

    @Override
    public long getLength() {
        return length;
    }

    /**
     * Gets the absolute value of the length (without the signal).
     * Check out {@link #getLength()} for details.
     * @return
     */
    protected long absLength(){
        /*Since the getLength is overridden by classes such as the NetworkCloudlet,
        * we have to call the method instead of directly using the length attribute.*/
        return Math.abs(getLength());
    }

    @Override
    public long getTotalLength() {
        return length * numberOfPes;
    }

    @Override
    public double getActualCpuTime() {
        final double time = getFinishTime() == NOT_ASSIGNED ? getSimulation().clock() : finishTime;
        return time - execStartTime;
    }

    @Override
    public double getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Sets the time the Cloudlet arrived at a Datacenter to be executed.
     * @param arrivalTime the arrival time to set (in seconds)
     */
    protected final void setArrivalTime(final double arrivalTime) {
        //The only negative value accepted is -1, to indicate not arrival time is set
        if(arrivalTime < 0)
            this.arrivalTime = -1;
        else this.arrivalTime = arrivalTime;
    }

    @Override
    public double getFinishTime() {
        return finishTime;
    }

    /**
     * Sets the {@link #getFinishTime() finish time} of this cloudlet in the latest Datacenter.
     *
     * @param finishTime the finish time
     */
    protected final void setFinishTime(final double finishTime) {
        this.finishTime = finishTime;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public boolean isReturnedToBroker() {
        return returnedToBroker;
    }

    @Override
    public long getJobId() {
        return jobId;
    }

    @Override
    public final void setJobId(final long jobId) {
        this.jobId = jobId;
    }

    @Override
    public Vm getVm() {
        return vm;
    }

    @Override
    public Cloudlet setVm(final Vm vm) {
        this.vm = vm;
        return this;
    }

    @Override
    public List<String> getRequiredFiles() {
        return requiredFiles;
    }

    /**
     * Sets the list of {@link #getRequiredFiles() required files}.
     *
     * @param requiredFiles the new list of required files
     */
    public final void setRequiredFiles(final List<String> requiredFiles) {
        this.requiredFiles = requireNonNull(requiredFiles);
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
    public UtilizationModel getUtilizationModelCpu() {
        return utilizationModelCpu;
    }

    @Override
    public final Cloudlet setUtilizationModelCpu(final UtilizationModel utilizationModelCpu) {
        this.utilizationModelCpu = requireNonNull(utilizationModelCpu);
        return this;
    }

    @Override
    public UtilizationModel getUtilizationModelRam() {
        return utilizationModelRam;
    }

    @Override
    public final Cloudlet setUtilizationModelRam(final UtilizationModel utilizationModelRam) {
        this.utilizationModelRam = requireNonNull(utilizationModelRam);
        return this;
    }

    @Override
    public UtilizationModel getUtilizationModelBw() {
        return utilizationModelBw;
    }

    @Override
    public final Cloudlet setUtilizationModelBw(final UtilizationModel utilizationModelBw) {
        this.utilizationModelBw = requireNonNull(utilizationModelBw);
        return this;
    }

    @Override
    public UtilizationModel getUtilizationModel(final Class<? extends ResourceManageable> resourceClass) {
        if(resourceClass.isAssignableFrom(Ram.class)){
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
    public double getSubmissionDelay() {
        return this.submissionDelay;
    }

    @Override
    public final void setSubmissionDelay(final double submissionDelay) {
        if (submissionDelay < 0) {
            return;
        }

        this.submissionDelay = submissionDelay;
    }

    @Override
    public boolean isDelayed() {
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
        setArrivalTime(getSimulation().clock());
        return arrivalTime;
    }

    @Override
	public Cloudlet setLifeTime(final double lifeTime) {
		if (lifeTime == 0) {
			throw new IllegalArgumentException("Cloudlet lifeTime cannot be zero.");
		}

		this.lifeTime = lifeTime;
		return this;
	}

	@Override
	public double getLifeTime() {
		return this.lifeTime;
	}
}
