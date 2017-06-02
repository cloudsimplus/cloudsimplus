/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.cloudlets;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.core.UniquelyIdentificable;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventListener;

import java.text.DecimalFormat;
import java.util.*;

/**
 * A base class for {@link Cloudlet} implementations.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 */
public abstract class CloudletAbstract implements Cloudlet {
    /**
     * @see #getId()
     */
    private int id;
    /**
     * Stores the operating system line separator.
     */
    private final String newline;
    /**
     * The format of decimal numbers.
     */
    private final DecimalFormat num;
    /**
     * The list of every {@link Datacenter} where the cloudlet has been executed. In case
     * it starts and finishes executing in a single Datacenter, without
     * being migrated, this list will have only one item.
     */
    private final List<ExecutionInDatacenterInfo> executionInDatacenterInfoList;
    /**
     * @see #getBroker()
     */
    private DatacenterBroker broker;
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
     * @see #isRecordTransactionHistory()
     */
    private boolean recordTransactionHistory;
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
     * The cloudlet transaction history.
     */
    private StringBuffer history;
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

    private Set<EventListener<CloudletVmEventInfo>> onFinishListeners;
    private Set<EventListener<CloudletVmEventInfo>> onUpdateProcessingListeners;

    /**
     * @see #getSubmissionDelay()
     */
    private double submissionDelay;

    /**
     * Creates a Cloudlet with no priority and file size and output size equal to 1.
     *  @param cloudletId     id of the Cloudlet
     * @param length the length or size (in MI) of this cloudlet to be executed in a VM
     * @param pesNumber      number of PEs that Cloudlet will require
     */
    public CloudletAbstract(final int cloudletId, final long length, final long pesNumber) {
        /*
        Normally, a Cloudlet is only executed on a Datacenter without being
        migrated to others. Hence, to reduce memory consumption, set the
        size of this ArrayList to be less than the default one.
        */
        this.executionInDatacenterInfoList = new ArrayList<>(2);
        this.requiredFiles = new LinkedList<>();

        this.num = new DecimalFormat("#0.00#");
        this.newline = System.getProperty("line.separator");

        this.id = cloudletId;
        this.netServiceLevel = 0;
        this.execStartTime = 0.0;
        this.status = Status.INSTANTIATED;
        this.priority = 0;
        this.setNumberOfPes(pesNumber);
        this.recordTransactionHistory = false;

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
     * @param length the length or size (in MI) of this cloudlet to be executed in a VM
     * @param pesNumber      number of PEs that Cloudlet will require
     */
    public CloudletAbstract(final long length, final int pesNumber) {
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
        Objects.requireNonNull(listener);
        this.onUpdateProcessingListeners.add(listener);
        return this;
    }

    @Override
    public boolean removeOnUpdateProcessingListener(EventListener<CloudletVmEventInfo> listener) {
        return this.onUpdateProcessingListeners.remove(listener);
    }

    @Override
    public Cloudlet addOnFinishListener(EventListener<CloudletVmEventInfo> listener) {
        Objects.requireNonNull(listener);
        this.onFinishListeners.add(listener);
        return this;
    }

    @Override
    public boolean removeOnFinishListener(EventListener<CloudletVmEventInfo> listener) {
        return onFinishListeners.remove(listener);
    }

    @Override
    public void notifyOnUpdateProcessingListeners(double time) {
        CloudletVmEventInfo info = CloudletVmEventInfo.of(time, this);
        onUpdateProcessingListeners.forEach(l -> l.update(info));
    }

    @Override
    public final Cloudlet setLength(final long length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Cloudlet length has to be greater than zero.");
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
        if (executionInDatacenterInfoList.isEmpty()) {
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
    public String getHistory() {
        if (Objects.isNull(history))
            return String.format(NO_HISTORY_IS_RECORDED_FOR_CLOUDLET, id);

        return history.toString();
    }

    @Override
    public long getFinishedLengthSoFar(final Datacenter datacenter) {
        return getDatacenterInfo(datacenter).getFinishedSoFar();
    }

    @Override
    public long getFinishedLengthSoFar() {
        if (executionInDatacenterInfoList.isEmpty()) {
            return 0;
        }

        return Math.min(getLastExecutionInDatacenterInfo().getFinishedSoFar(), getLength());
    }

    @Override
    public boolean isFinished() {
        if (executionInDatacenterInfoList.isEmpty()) {
            return false;
        }

        return getLastExecutionInDatacenterInfo().getFinishedSoFar() >= getLength();
    }

    @Override
    public boolean setFinishedLengthSoFar(final long length) {
        if (length < 0.0 || executionInDatacenterInfoList.isEmpty()) {
            return false;
        }

        getLastExecutionInDatacenterInfo().setFinishedSoFar(Math.min(length, this.getLength()));
        write("Set the length's finished so far to %d", length);
        notifyListenersIfCloudletIsFinished();
        return true;
    }

    /**
     * Notifies all registered listeners about the termination of the Cloudlet
     * if it in fact has finished.
     */
    private void notifyListenersIfCloudletIsFinished() {
        if (isFinished()) {
            final CloudletVmEventInfo info = CloudletVmEventInfo.of(this);
            onFinishListeners.forEach(l -> l.update(info));
        }
    }

    @Override
    public final Cloudlet setBroker(DatacenterBroker broker) {
        if (Objects.isNull(broker)) {
            broker = DatacenterBroker.NULL;
        }
        this.broker = broker;
        return this;
    }

    @Override
    public DatacenterBroker getBroker() {
        return broker;
    }

    @Override
    public Datacenter getLastDatacenter() {
        return getLastExecutionInDatacenterInfo().getDatacenter();
    }

    private ExecutionInDatacenterInfo getLastExecutionInDatacenterInfo() {
        if (executionInDatacenterInfoList.isEmpty()) {
            return ExecutionInDatacenterInfo.NULL;
        }

        return executionInDatacenterInfoList.get(executionInDatacenterInfoList.size() - 1);
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
        write("Sets the execution start time to %s", num.format(clockTime));
    }

    @Override
    public boolean setWallClockTime(final double wallTime, final double actualCpuTime) {
        if (wallTime < 0.0 || actualCpuTime < 0.0 || executionInDatacenterInfoList.isEmpty()) {
            return false;
        }

        final ExecutionInDatacenterInfo datacenter = getLastExecutionInDatacenterInfo();
        datacenter.setWallClockTime(wallTime);
        datacenter.setActualCpuTime(actualCpuTime);

        write("Sets the wall clock time to %s and the actual CPU time to %s",
            num.format(wallTime), num.format(actualCpuTime));

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

        write("Sets Cloudlet status from %s to %s", status.name(), newStatus.name());

        this.status = newStatus;
        return true;
    }

    @Override
    public long getLength() {
        return length;
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
     * or {@link ExecutionInDatacenterInfo#NULL} if the Cloudlet has never been executed there
     * @pre dc >= 0
     * @post $none
     */
    private ExecutionInDatacenterInfo getDatacenterInfo(final int datacenterId) {
        return executionInDatacenterInfoList.stream()
            .filter(info -> info.getDatacenter().getId() == datacenterId)
            .findFirst().orElse(ExecutionInDatacenterInfo.NULL);
    }

    /**
     * Gets information about the cloudlet execution on a given Datacenter.
     *
     * @param datacenter the Datacenter entity
     * @return the Cloudlet execution information on the given Datacenter
     * or {@link ExecutionInDatacenterInfo#NULL} if the Cloudlet has never been executed there
     * @pre dc >= 0
     * @post $none
     */
    private ExecutionInDatacenterInfo getDatacenterInfo(final Datacenter datacenter) {
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

    /**
     * Writes a particular history transaction of this Cloudlet into a log.
     *
     * @param str a history transaction of this Cloudlet
     * @pre str != null
     * @post $none
     */
    protected void write(final String str) {
        if (Objects.isNull(str)) {
            return;
        }

        if (!recordTransactionHistory) {
            return;
        }

        if (Objects.isNull(history)) {
            // Creates the transaction history of this Cloudlet
            history = new StringBuffer(1000);
            history.append("Time below denotes the simulation time.");
            history.append(this.newline);
            history.append("Time (sec)       Description Cloudlet #").append(id);
            history.append(this.newline);
            history.append("------------------------------------------");
            history.append(this.newline);
            history.append(num.format(getSimulation().clock()));
            history.append("   Creates Cloudlet ID #").append(id);
            history.append(this.newline);
        }

        history.append(num.format(getSimulation().clock()));
        history.append("   ").append(str).append(newline);
    }

    /**
     * Writes a formatted particular history transaction of this Cloudlet into a log.
     *
     * @param format the format of the Cloudlet's history transaction, according
     *               to the format parameter of {@link String#format(String, Object...)}
     * @param args   The list of values to be shown in the history,
     *               that are referenced by the format.
     * @pre format != null
     * @post $none
     * @see #write(String)
     */
    protected void write(final String format, Object... args) {
        write(String.format(format, args));
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
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
        return executionInDatacenterInfoList.stream()
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
        if (Objects.isNull(requiredFiles)) {
            this.requiredFiles = new LinkedList<>();
        } else {
            this.requiredFiles = requiredFiles;
        }
    }

    @Override
    public boolean addRequiredFile(final String fileName) {
        if (getRequiredFiles().stream().anyMatch(s -> s.equals(fileName))) {
            return false;
        }

        requiredFiles.add(fileName);
        return true;
    }

    @Override
    public boolean addRequiredFiles(List<String> fileNames) {
        boolean atLeastOneFileAdded = false;
        for (String fileName : fileNames) {
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
        boolean result = false;
        if (getRequiredFiles().size() > 0) {
            result = true;
        }

        return result;
    }

    @Override
    public UtilizationModel getUtilizationModelCpu() {
        return utilizationModelCpu;
    }

    @Override
    public final Cloudlet setUtilizationModelCpu(final UtilizationModel utilizationModelCpu) {
        this.utilizationModelCpu = Objects.isNull(utilizationModelCpu) ? UtilizationModel.NULL : utilizationModelCpu;
        return this;
    }

    @Override
    public UtilizationModel getUtilizationModelRam() {
        return utilizationModelRam;
    }

    @Override
    public final Cloudlet setUtilizationModelRam(final UtilizationModel utilizationModelRam) {
        this.utilizationModelRam = Objects.isNull(utilizationModelRam) ? UtilizationModel.NULL : utilizationModelRam;
        return this;
    }

    @Override
    public UtilizationModel getUtilizationModelBw() {
        return utilizationModelBw;
    }

    @Override
    public final Cloudlet setUtilizationModelBw(final UtilizationModel utilizationModelBw) {
        this.utilizationModelBw = Objects.isNull(utilizationModelBw) ? UtilizationModel.NULL : utilizationModelBw;
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
    public final void setSubmissionDelay(double submissionDelay) {
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
    public final Cloudlet setFileSize(long fileSize) {
        if (fileSize <= 0) {
            throw new IllegalArgumentException("Cloudlet fize size has to be greater than zero.");
        }

        this.fileSize = fileSize;
        return this;
    }

    @Override
    public final Cloudlet setOutputSize(long outputSize) {
        if (outputSize <= 0) {
            throw new IllegalArgumentException("Cloudlet output size has to be greater than zero.");
        }
        this.outputSize = outputSize;
        return this;
    }

    /**
     * Indicates if Cloudlet transaction history is to be recorded or not.
     *
     * @return
     * @see #getHistory()
     */
    public boolean isRecordTransactionHistory() {
        return recordTransactionHistory;
    }

    /**
     * Sets the Cloudlet transaction history writing.
     *
     * @param recordTransactionHistory true enables transaction history writing,
     *                                 false disables.
     */
    public void setRecordTransactionHistory(boolean recordTransactionHistory) {
        this.recordTransactionHistory = recordTransactionHistory;
    }

    @Override
    public void assignToDatacenter(final Datacenter datacenter) {
        final ExecutionInDatacenterInfo dcInfo = new ExecutionInDatacenterInfo();
        dcInfo.setDatacenter(datacenter);
        dcInfo.setCostPerSec(datacenter.getCharacteristics().getCostPerSecond());

        // add into a list if moving to a new cloud Datacenter
        executionInDatacenterInfoList.add(dcInfo);

        if (isRecordTransactionHistory()) {
            if (isAssignedToDatacenter()) {
                final Datacenter oldDc = getLastExecutionInDatacenterInfo().getDatacenter();
                write("Moves Cloudlet from %s (ID #%d) to %s (ID #%d) with cost = $%.2f/sec",
                    oldDc.getName(), oldDc.getId(), dcInfo.getDatacenter().getName(), dcInfo.getDatacenter().getId(), dcInfo.getCostPerSec());

            } else {
                write("Allocates this Cloudlet to %s (ID #%d) with cost = $%.2f/sec",
                    dcInfo.getDatacenter().getName(), dcInfo.getDatacenter().getId(), dcInfo.getCostPerSec());
            }
        }

        setLastExecutedDatacenterIdx(getLastExecutedDatacenterIdx() + 1);

        this.setCostPerBw(datacenter.getCharacteristics().getCostPerBw());
        setAccumulatedBwCost(this.costPerBw * fileSize);
    }

    @Override
    public double registerArrivalInDatacenter() {
        if (!isAssignedToDatacenter()) {
            return NOT_ASSIGNED;
        }

        final ExecutionInDatacenterInfo dcInfo = executionInDatacenterInfoList.get(lastExecutedDatacenterIdx);
        dcInfo.setArrivalTime(getSimulation().clock());

        return dcInfo.getArrivalTime();
    }

    @Override
    public boolean isAssignedToDatacenter() {
        return !executionInDatacenterInfoList.isEmpty();
    }

    @Override
    public double getLastDatacenterArrivalTime() {
        return getLastExecutionInDatacenterInfo().getArrivalTime();
    }

    @Override
    public String getUid() {
        return UniquelyIdentificable.getUid(broker.getId(), id);
    }

    @Override
    public Simulation getSimulation() {
        return broker.getSimulation();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CloudletAbstract)) return false;

        CloudletAbstract that = (CloudletAbstract) o;

        if (id != that.id) return false;
        return broker.equals(that.broker);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + broker.hashCode();
        return result;
    }

}
