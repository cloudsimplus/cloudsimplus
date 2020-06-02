/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.cloudlets;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.CustomerEntityAbstract;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmGroup;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventListener;

import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * A base class for {@link Cloudlet} implementations.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 */
public abstract class CloudletAbstract extends CustomerEntityAbstract implements Cloudlet {

    /** @see #getJobId() */
    private long jobId;

    /**
     * The list of every {@link Datacenter} where the cloudlet has been executed.
     * In case it starts and finishes executing in a single Datacenter, without
     * being migrated, this list will have only one item.
     *
     * @TODO There isn't Cloudlet migration, so this attribute doesn't make sense.
     *       But since a lot of methods uses it, it's removal has to be carefully assessed.
     */
    private final List<CloudletDatacenterExecution> datacenterExecutionList;

    /**
     * @see #getLength()
     */
    private long length;

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

    /**
     * The index of the last Datacenter where the cloudlet was executed. If the
     * cloudlet is migrated during its execution, this index is updated. The
     * value {@link #NOT_ASSIGNED} indicates the cloudlet has not been executed yet.
     */
    private int lastExecutedDatacenterIdx;

    /** @see #getFileSize() */
    private long fileSize;

    /** @see #getOutputSize() */
    private long outputSize;

    /** @see #getFinishTime() */
    private double finishTime;

    /** @see #getCostPerBw() */
    private double costPerBw;

    /** @see #getAccumulatedBwCost() */
    private double accumulatedBwCost;

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

    /**
     * Creates a Cloudlet with no priority or id. The id is defined when the Cloudlet is submitted to
     * a {@link DatacenterBroker}. The file size and output size is defined as 1.
     *
     * @param length the length or size (in MI) of this cloudlet to be executed in a VM (check out {@link #setLength(long)})
     * @param pesNumber number of PEs that Cloudlet will require
     * @param utilizationModel a {@link UtilizationModel} to define how the Cloudlet uses CPU, RAM and BW.
     *                         To define an independent utilization model for each resource, call the respective setters.
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
     * Creates a Cloudlet with no priority or id. The id is defined when the Cloudlet is submitted to
     * a {@link DatacenterBroker}. The file size and output size is defined as 1.
     *
     * <p><b>NOTE:</b> By default, the Cloudlet will use a {@link UtilizationModelFull} to define
     * CPU utilization and a {@link UtilizationModel#NULL} for RAM and BW.
     * To change the default values, use the respective setters.</p>
     *
     * @param length the length or size (in MI) of this cloudlet to be executed in a VM (check out {@link #setLength(long)})
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
     * @param length the length or size (in MI) of this cloudlet to be executed in a VM (check out {@link #setLength(long)})
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
     * @param length the length or size (in MI) of this cloudlet to be executed in a VM (check out {@link #setLength(long)})
     * @param pesNumber number of PEs that Cloudlet will require
     */
    public CloudletAbstract(final long id, final long length, final long pesNumber) {
        /*
        Normally, a Cloudlet is only executed on a Datacenter without being
        migrated to others. Hence, to reduce memory consumption, set the
        size of this ArrayList to be less than the default one.
        */
        this.datacenterExecutionList = new ArrayList<>(2);
        this.requiredFiles = new LinkedList<>();
        this.setId(id);
        this.setJobId(NOT_ASSIGNED);
        this.setNumberOfPes(pesNumber);
        this.setLength(length);
        this.setFileSize(1);
        this.setOutputSize(1);
        this.setSubmissionDelay(0.0);
        this.setAccumulatedBwCost(0.0);
        this.setCostPerBw(0.0);

        this.reset();

        setUtilizationModelCpu(new UtilizationModelFull());
        setUtilizationModelRam(UtilizationModel.NULL);
        setUtilizationModelBw(UtilizationModel.NULL);
        onStartListeners = new HashSet<>();
        onFinishListeners = new HashSet<>();
        onUpdateProcessingListeners = new HashSet<>();
    }

    public final Cloudlet reset() {
        this.netServiceLevel = 0;
        this.execStartTime = 0.0;
        this.status = Status.INSTANTIATED;
        this.priority = 0;

        this.lastExecutedDatacenterIdx = NOT_ASSIGNED;
        setBroker(DatacenterBroker.NULL);
        setFinishTime(NOT_ASSIGNED); // meaning this Cloudlet hasn't finished yet
        setVm(Vm.NULL);
        setExecStartTime(0.0);

        datacenterExecutionList.clear();

        this.setLastTriedDatacenter(Datacenter.NULL);
        return this;
    }

    protected int getLastExecutedDatacenterIdx() {
        return lastExecutedDatacenterIdx;
    }

    protected void setLastExecutedDatacenterIdx(final int lastExecutedDatacenterIdx) {
        this.lastExecutedDatacenterIdx = lastExecutedDatacenterIdx;
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
    public boolean setNetServiceLevel(final int netServiceLevel) {
        if (netServiceLevel > 0) {
            this.netServiceLevel = netServiceLevel;
            return true;
        }

        return false;
    }

    @Override
    public int getNetServiceLevel() {
        return netServiceLevel;
    }

    @Override
    public double getWaitingTime() {
        if (datacenterExecutionList.isEmpty()) {
            return 0;
        }

        // use the latest resource submission time
        final double subTime = getLastExecutionInDatacenterInfo().getArrivalTime();
        return execStartTime - subTime;
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
    public long getFinishedLengthSoFar(final Datacenter datacenter) {
        return getDatacenterInfo(datacenter).getFinishedSoFar();
    }

    @Override
    public long getFinishedLengthSoFar() {
        if (datacenterExecutionList.isEmpty()) {
            return 0;
        }

        if(getLength() > 0) {
            return Math.min(getLastExecutionInDatacenterInfo().getFinishedSoFar(), absLength());
        }

        /**
         * If the length is negative, it means the Cloudlet doesn't have a fixed length.
         * This way, it keeps running and increasing the executed length
         * until a {@link CloudSimTags#CLOUDLET_FINISH} message is sent to the broker
         * or the simulation is terminated under request (by setting a termination time).*/
        return getLastExecutionInDatacenterInfo().getFinishedSoFar();
    }

    @Override
    public boolean isFinished() {
        if (datacenterExecutionList.isEmpty()) {
            return false;
        }

        /**
         * If length is negative, it means it is undefined.
         * Check {@link CloudSimTags#CLOUDLET_FINISH} for details.
         */
        return getLength() > 0 && getLastExecutionInDatacenterInfo().getFinishedSoFar() >= getLength();
    }

    @Override
    public boolean addFinishedLengthSoFar(final long partialFinishedMI) {
        if (partialFinishedMI < 0.0 || datacenterExecutionList.isEmpty()) {
            return false;
        }

        /**
         * If the Cloudlet has a defined length (a positive value), then, the partial
         * finished length cannot be greater than the actual total length.
         * If the Cloudlet has a negative length, it means it doesn't have a defined
         * length, so that its length increases indefinitely until
         * a {@link CloudSimTags#CLOUDLET_FINISH} message is sent to the broker. */
        final long maxLengthToAdd = getLength() < 0 ?
                                    partialFinishedMI :
                                    Math.min(partialFinishedMI, absLength()-getFinishedLengthSoFar());
        getLastExecutionInDatacenterInfo().addFinishedSoFar(maxLengthToAdd);
        returnToBrokerIfFinished();
        notifyListenersIfCloudletIsFinished();
        return true;
    }

    /**
     * Notifies the broker about the end of execution of the Cloudlet,
     * by returning the Cloudlet to it.
     */
    private void returnToBrokerIfFinished() {
        if(isFinished() && !isReturnedToBroker()){
            returnedToBroker = true;
            getSimulation().sendNow(getSimulation().getCloudInfoService(), getBroker(), CloudSimTags.CLOUDLET_RETURN, this);
            vm.getCloudletScheduler().addCloudletToReturnedList(this);
        }
    }

    /**
     * Notifies all registered listeners about the termination of the Cloudlet
     * if it in fact has finished.
     * It then removes the registered listeners to avoid a Listener to be notified
     * multiple times about a Cloudlet termination.
     */
    private void notifyListenersIfCloudletIsFinished() {
        if (isFinished()) {
            onFinishListeners.forEach(listener -> listener.update(CloudletVmEventInfo.of(listener, this)));
            onFinishListeners.clear();
        }
    }

    private CloudletDatacenterExecution getLastExecutionInDatacenterInfo() {
        if (datacenterExecutionList.isEmpty()) {
            return CloudletDatacenterExecution.NULL;
        }

        return datacenterExecutionList.get(datacenterExecutionList.size() - 1);
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
    public boolean setWallClockTime(final double wallTime, final double actualCpuTime) {
        if (wallTime < 0.0 || actualCpuTime < 0.0 || datacenterExecutionList.isEmpty()) {
            return false;
        }

        final CloudletDatacenterExecution datacenter = getLastExecutionInDatacenterInfo();
        datacenter.setWallClockTime(wallTime);
        datacenter.setActualCpuTime(actualCpuTime);

        return true;
    }

    @Override
    public boolean setStatus(final Status newStatus) {
        // if the new status is same as current one, then ignore the rest
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
    public double getCostPerSec() {
        return getLastExecutionInDatacenterInfo().getCostPerSec();
    }

    @Override
    public double getCostPerSec(final Datacenter datacenter) {
        return getDatacenterInfo(datacenter).getCostPerSec();
    }

    /**
     * Gets the total execution time of this Cloudlet in a given Datacenter
     * ID.
     *
     * @param datacenter the Datacenter entity
     * @return the total execution time of this Cloudlet in the given Datacenter
     * or 0 if the Cloudlet was not executed there
     */
    protected double getActualCpuTime(final Datacenter datacenter) {
        return getDatacenterInfo(datacenter).getActualCpuTime();
    }

    @Override
    public double getActualCpuTime() {
        if (getFinishTime() == NOT_ASSIGNED) {
            return NOT_ASSIGNED;
        }

        return finishTime - execStartTime;
    }

    @Override
    public double getArrivalTime(final Datacenter datacenter) {
        return getDatacenterInfo(datacenter).getArrivalTime();
    }

    /**
     * Gets the time of this Cloudlet resides in a given Datacenter (from
     * arrival time until departure time).
     *
     * @param datacenter a Datacenter entity
     * @return the wall-clock time or 0 if the Cloudlet has never been executed there
     * @see <a href="https://en.wikipedia.org/wiki/Elapsed_real_time">Elapsed real time (wall-clock time)</a>
     */
    protected double getWallClockTime(final Datacenter datacenter) {
        return getDatacenterInfo(datacenter).getWallClockTime();
    }

    /**
     * Gets information about the cloudlet execution on a given Datacenter.
     *
     * @param datacenterId the Datacenter entity ID
     * @return the Cloudlet execution information on the given Datacenter
     * or {@link CloudletDatacenterExecution#NULL} if the Cloudlet has never been executed there
     */
    private CloudletDatacenterExecution getDatacenterInfo(final long datacenterId) {
        return datacenterExecutionList.stream()
            .filter(info -> info.getDatacenter().getId() == datacenterId)
            .findFirst().orElse(CloudletDatacenterExecution.NULL);
    }

    /**
     * Gets information about the cloudlet execution on a given Datacenter.
     *
     * @param datacenter the Datacenter entity
     * @return the Cloudlet execution information on the given Datacenter
     * or {@link CloudletDatacenterExecution#NULL} if the Cloudlet has never been executed there
     */
    private CloudletDatacenterExecution getDatacenterInfo(final Datacenter datacenter) {
        return getDatacenterInfo(datacenter.getId());
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
    public final Cloudlet setVm(final Vm vm) {
        this.vm = vm;
        return this;
    }

    @Override
    public double getTotalCost() {
        // cloudlet cost: execution cost...
        double totalCost = getTotalCpuCostForAllDatacenters();

        // ... plus input data transfer cost...
        totalCost += accumulatedBwCost;

        // ... plus output cost
        totalCost += costPerBw * outputSize;
        return totalCost;
    }

    /**
     * Gets the total cost for using CPU on every Datacenter where the Cloudlet has executed.
     *
     * @return
     */
    private double getTotalCpuCostForAllDatacenters() {
        return datacenterExecutionList
                .stream()
                .mapToDouble(dcInfo -> dcInfo.getActualCpuTime() * dcInfo.getCostPerSec())
                .sum();
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
            final String temp = requiredFiles.get(i);

            if (temp.equals(filename)) {
                requiredFiles.remove(i);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean requiresFiles() {
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
    public double getCostPerBw() {
        return costPerBw;
    }

    /**
     * Sets {@link #getCostPerBw() the cost of each byte of bandwidth (bw)} consumed.
     *
     * @param costPerBw the new cost per bw to set
     */
    protected final void setCostPerBw(final double costPerBw) {
        this.costPerBw = costPerBw;
    }

    @Override
    public double getAccumulatedBwCost() {
        return accumulatedBwCost;
    }

    /**
     * Sets the {@link #getAccumulatedBwCost() accumulated bw cost}.
     *
     * @param accumulatedBwCost the accumulated bw cost to set
     */
    protected final void setAccumulatedBwCost(final double accumulatedBwCost) {
        this.accumulatedBwCost = accumulatedBwCost;
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
    public void assignToDatacenter(final Datacenter datacenter) {
        final CloudletDatacenterExecution dcInfo = new CloudletDatacenterExecution();
        dcInfo.setDatacenter(datacenter);
        dcInfo.setCostPerSec(datacenter.getCharacteristics().getCostPerSecond());

        // add into a list if moving to a new cloud Datacenter
        datacenterExecutionList.add(dcInfo);

        setLastExecutedDatacenterIdx(getLastExecutedDatacenterIdx() + 1);

        this.setCostPerBw(datacenter.getCharacteristics().getCostPerBw());
        setAccumulatedBwCost(this.costPerBw * fileSize);
    }

    @Override
    public double registerArrivalInDatacenter() {
        if (!isAssignedToDatacenter()) {
            return NOT_ASSIGNED;
        }

        final CloudletDatacenterExecution dcInfo = datacenterExecutionList.get(lastExecutedDatacenterIdx);
        dcInfo.setArrivalTime(getSimulation().clock());

        return dcInfo.getArrivalTime();
    }

    /**
     * @return true if the cloudlet has even been assigned to a Datacenter
     * in order to run, false otherwise.
     */
    private boolean isAssignedToDatacenter() {
        return !datacenterExecutionList.isEmpty();
    }

    @Override
    public double getLastDatacenterArrivalTime() {
        return getLastExecutionInDatacenterInfo().getArrivalTime();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (!(other instanceof CloudletAbstract)) return false;

        final CloudletAbstract that = (CloudletAbstract) other;

        if (getId() != that.getId()) return false;
        return getBroker().equals(that.getBroker());
    }
}
