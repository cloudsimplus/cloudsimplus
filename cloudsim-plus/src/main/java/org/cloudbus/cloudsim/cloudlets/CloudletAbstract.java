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
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.vms.Vm;
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
     * @todo There isn't Cloudlet migration, so this attribute doesn't make sense.
     * But since a lot of methods uses this attribute, it's removal has to be carefully assessed.
     */
    private final List<CloudletDatacenterExecution> datacenterExecutionList;
    /**
     * @see #getLength()
     */
    private long length;
    /**
     * @see #getNumberOfPes()
     */
    private long numberOfPes;
    /**
     * @see #getStatus()
     */
    private Status status;
    /**
     * @see #getExecStartTime()
     */
    private double execStartTime;
    /**
     * @see #getPriority()
     */
    private int priority;
    /**
     * @see #getNetServiceLevel()
     */
    private int netServiceLevel;
    /**
     * @see #getVm()
     */
    private Vm vm;
    /**
     * @see #getRequiredFiles()
     */
    private List<String> requiredFiles;

    /**
     * The index of the last Datacenter where the cloudlet was executed. If the
     * cloudlet is migrated during its execution, this index is updated. The
     * value {@link #NOT_ASSIGNED} indicates the cloudlet has not been executed yet.
     */
    private int lastExecutedDatacenterIdx;

    /**
     * @see #getFileSize()
     */
    private long fileSize;
    /**
     * @see #getOutputSize()
     */
    private long outputSize;
    /**
     * @see #getFinishTime()
     */
    private double finishTime;
    /**
     * @see #getCostPerBw()
     */
    private double costPerBw;
    /**
     * @see #getAccumulatedBwCost()
     */
    private double accumulatedBwCost;
    /**
     * @see #getUtilizationModelCpu()
     */
    private UtilizationModel utilizationModelCpu;
    /**
     * @see #getUtilizationModelRam()
     */
    private UtilizationModel utilizationModelRam;
    /**
     * @see #getUtilizationModelBw()
     */
    private UtilizationModel utilizationModelBw;

    private final Set<EventListener<CloudletVmEventInfo>> onFinishListeners;
    private final Set<EventListener<CloudletVmEventInfo>> onUpdateProcessingListeners;

    /**
     * @see #getSubmissionDelay()
     */
    private double submissionDelay;

    /**
     * Creates a Cloudlet with no priority and file size and output size equal to 1.
     *  @param id     id of the Cloudlet
     * @param length the length or size (in MI) of this cloudlet to be executed in a VM (check out {@link #setLength(long)})
     * @param pesNumber      number of PEs that Cloudlet will require
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

        this.netServiceLevel = 0;
        this.execStartTime = 0.0;
        this.status = Status.INSTANTIATED;
        this.priority = 0;
        this.setNumberOfPes(pesNumber);

        this.lastExecutedDatacenterIdx = NOT_ASSIGNED;
        setBroker(DatacenterBroker.NULL);
        setFinishTime(NOT_ASSIGNED);    // meaning this Cloudlet hasn't finished yet
        setVm(Vm.NULL);

        this.setLength(length);
        this.setFileSize(1);
        this.setOutputSize(1);

        setAccumulatedBwCost(0.0);
        setCostPerBw(0.0);
        setSubmissionDelay(0.0);

        setUtilizationModelCpu(UtilizationModel.NULL);
        setUtilizationModelRam(UtilizationModel.NULL);
        setUtilizationModelBw(UtilizationModel.NULL);
        onFinishListeners = new HashSet<>();
        onUpdateProcessingListeners = new HashSet<>();
    }

    /**
     * Creates a Cloudlet with no priority or id. The id is defined when the Cloudlet is submitted to
     * a {@link DatacenterBroker}. The file size and output size is defined as 1.
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
     * @param length the length or size (in MI) of this cloudlet to be executed in a VM (check out {@link #setLength(long)})
     * @param pesNumber number of PEs that Cloudlet will require
     */
    public CloudletAbstract(final long length, final long pesNumber) {
        this(-1, length, pesNumber);
    }

    protected int getLastExecutedDatacenterIdx() {
        return lastExecutedDatacenterIdx;
    }

    protected void setLastExecutedDatacenterIdx(int lastExecutedDatacenterIdx) {
        this.lastExecutedDatacenterIdx = lastExecutedDatacenterIdx;
    }

    @Override
    public Cloudlet setUtilizationModel(UtilizationModel utilizationModel) {
        setUtilizationModelBw(utilizationModel);
        setUtilizationModelRam(utilizationModel);
        setUtilizationModelCpu(utilizationModel);
        return this;
    }

    @Override
    public Cloudlet addOnUpdateProcessingListener(EventListener<CloudletVmEventInfo> listener) {
        this.onUpdateProcessingListeners.add(requireNonNull(listener));
        return this;
    }

    @Override
    public boolean removeOnUpdateProcessingListener(EventListener<CloudletVmEventInfo> listener) {
        return this.onUpdateProcessingListeners.remove(listener);
    }

    @Override
    public Cloudlet addOnFinishListener(EventListener<CloudletVmEventInfo> listener) {
        this.onFinishListeners.add(requireNonNull(listener));
        return this;
    }

    @Override
    public boolean removeOnFinishListener(EventListener<CloudletVmEventInfo> listener) {
        return onFinishListeners.remove(listener);
    }

    @Override
    public void notifyOnUpdateProcessingListeners(double time) {
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
    public void setPriority(final int priority) {
        this.priority = priority;
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
         * If the length is negative, it means the Cloudlet doesn't have a defined length.
         * This way, it keeps running and increasing the executed length
         * until a {@link CloudSimTags#CLOUDLET_FINISH} message is sent to the broker. */
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
        notifyListenersIfCloudletIsFinished();
        return true;
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

    @Override
    public Datacenter getLastDatacenter() {
        return getLastExecutionInDatacenterInfo().getDatacenter();
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
        this.execStartTime = clockTime;
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

    @Override
    public double getWallClockTimeInLastExecutedDatacenter() {
        return getLastExecutionInDatacenterInfo().getWallClockTime();
    }

    @Override
    public double getActualCpuTime(final Datacenter datacenter) {
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

    @Override
    public double getWallClockTime(final Datacenter datacenter) {
        return getDatacenterInfo(datacenter).getWallClockTime();
    }

    /**
     * Gets information about the cloudlet execution on a given Datacenter.
     *
     * @param datacenterId the Datacenter entity ID
     * @return the Cloudlet execution information on the given Datacenter
     * or {@link CloudletDatacenterExecution#NULL} if the Cloudlet has never been executed there
     * @pre dc >= 0
     * @post $none
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
     * @pre dc >= 0
     * @post $none
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
        return datacenterExecutionList.stream()
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
    protected final void setCostPerBw(double costPerBw) {
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
    protected final void setAccumulatedBwCost(double accumulatedBwCost) {
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
    public boolean isBindToVm() {
        return vm != null && vm != Vm.NULL;
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

    @Override
    public boolean isAssignedToDatacenter() {
        return !datacenterExecutionList.isEmpty();
    }

    @Override
    public double getLastDatacenterArrivalTime() {
        return getLastExecutionInDatacenterInfo().getArrivalTime();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof CloudletAbstract)) return false;

        final CloudletAbstract that = (CloudletAbstract) other;

        if (getId() != that.getId()) return false;
        return getBroker().equals(that.getBroker());
    }
}
