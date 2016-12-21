/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.cloudlets;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.cloudbus.cloudsim.core.UniquelyIdentificable;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.CloudletVmEventInfo;

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
    private final int id;
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
     * it starts and finishes executing in a single switches, without
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
    private long cloudletLength;
    /**
     * @see #getNumberOfPes()
     */
    private int numberOfPes;
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
    private int lastExecutedDatacenterIndex;
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
     * @see #getReservationId()
     */
    private int reservationId = NOT_ASSIGNED;
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

    private List<EventListener<CloudletVmEventInfo>> onCloudletFinishListeners;
    private List<EventListener<CloudletVmEventInfo>> onUpdateCloudletProcessingListeners;
    /**
     * @see #getSubmissionDelay()
     */
    private double submissionDelay;

    /**
     * @see #getSimulation()
     */
    private Simulation simulation;

    /**
     * Creates a Cloudlet with no priority and file size and output size equal to 1.
     *
     * @param cloudletId     id of the Cloudlet
     * @param cloudletLength the length or size (in MI) of this cloudlet to be executed in a VM
     * @param pesNumber      number of PEs that Cloudlet will require
     */
    protected CloudletAbstract(final int cloudletId, final long cloudletLength, final int pesNumber) {
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

        this.lastExecutedDatacenterIndex = NOT_ASSIGNED;
        setBroker(DatacenterBroker.NULL);
        this.simulation = Simulation.NULL;
        setFinishTime(NOT_ASSIGNED);    // meaning this Cloudlet hasn't finished yet
        setVm(Vm.NULL);

        this.setLength(cloudletLength);
        this.setFileSize(1);
        this.setOutputSize(1);

        setAccumulatedBwCost(0.0);
        setCostPerBw(0.0);
        setSubmissionDelay(0.0);

        setUtilizationModelCpu(UtilizationModel.NULL);
        setUtilizationModelRam(UtilizationModel.NULL);
        setUtilizationModelBw(UtilizationModel.NULL);
        onCloudletFinishListeners = new ArrayList<>();
        onUpdateCloudletProcessingListeners = new ArrayList<>();
    }

    protected int getLastExecutedDatacenterIndex() {
        return lastExecutedDatacenterIndex;
    }

    protected void setLastExecutedDatacenterIndex(int lastExecutedDatacenterIndex) {
        this.lastExecutedDatacenterIndex = lastExecutedDatacenterIndex;
    }

    @Override
    public Cloudlet setUtilizationModel(UtilizationModel utilizationModel) {
        setUtilizationModelBw(utilizationModel);
        setUtilizationModelRam(utilizationModel);
        setUtilizationModelCpu(utilizationModel);
        return this;
    }

    @Override
    public boolean removeOnUpdateCloudletProcessingListener(EventListener<CloudletVmEventInfo> listener) {
        return this.onUpdateCloudletProcessingListeners.remove(listener);
    }

    @Override
    public Cloudlet addOnUpdateCloudletProcessingListener(EventListener<CloudletVmEventInfo> listener) {
        if (!Objects.isNull(listener)) {
            this.onUpdateCloudletProcessingListeners.add(listener);
        }

        return this;
    }

    @Override
    public boolean removeOnCloudletFinishListener(EventListener<CloudletVmEventInfo> listener) {
        return onCloudletFinishListeners.remove(listener);
    }

    @Override
    public Cloudlet addOnCloudletFinishListener(EventListener<CloudletVmEventInfo> listener) {
        if (!Objects.isNull(listener)) {
            this.onCloudletFinishListeners.add(listener);
        }

        return this;
    }

    @Override
    public void notifyOnCloudletProcessingListeners(double time) {
        CloudletVmEventInfo info = CloudletVmEventInfo.of(time, this);
        onUpdateCloudletProcessingListeners.forEach(l -> l.update(info));
    }

    @Override
    public boolean setReservationId(final int reservationId) {
        if (reservationId <= 0) {
            return false;
        }
        this.reservationId = reservationId;
        return true;
    }

    @Override
    public int getReservationId() {
        return reservationId;
    }

    @Override
    public boolean isReserved() {
        return reservationId > NOT_ASSIGNED;
    }

    @Override
    public final Cloudlet setLength(final long length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Cloudlet length has to be greater than zero.");
        }

        this.cloudletLength = length;
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
        final double subTime = getLastExecutionInDatacenterInfo().arrivalTime;
        return execStartTime - subTime;
    }

    @Override
    public void setPriority(final int priority) {
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public final Cloudlet setNumberOfPes(final int numberOfPes) {
        if (numberOfPes <= 0) {
            throw new IllegalArgumentException("Cloudlet number of PEs has to be greater than zero.");
        }
        this.numberOfPes = numberOfPes;
        return this;
    }

    @Override
    public int getNumberOfPes() {
        return numberOfPes;
    }

    @Override
    public String getHistory() {
        if (Objects.isNull(history))
            return String.format(NO_HISTORY_IS_RECORDED_FOR_CLOUDLET, id);

        return history.toString();
    }

    @Override
    public long getFinishedLengthSoFar() {
        if (executionInDatacenterInfoList.isEmpty()) {
            return 0;
        }

        return Math.min(getLastExecutionInDatacenterInfo().finishedSoFar, getLength());
    }

    @Override
    public boolean isFinished() {
        if (executionInDatacenterInfoList.isEmpty()) {
            return false;
        }

        return getLastExecutionInDatacenterInfo().finishedSoFar >= getLength();
    }

    @Override
    public boolean setFinishedLengthSoFar(final long length) {
        if (length > this.getLength())
            throw new IllegalArgumentException(
                String.format(
                    "The length parameter (%d) cannot be greater than the cloudletLength attribute (%d).",
                    length, this.getLength()));

        if (length < 0.0 || executionInDatacenterInfoList.isEmpty()) {
            return false;
        }

        getLastExecutionInDatacenterInfo().finishedSoFar = length;

        write("Set the length's finished so far to %d", length);

        notifyListenersIfCloudletIsFinished();
        return true;
    }

    /**
     * Notifies all registered listeners about the termination of the Cloudlet
     * if it in fact has finished.
     */
    private void notifyListenersIfCloudletIsFinished() {
        if(isFinished()) {
            CloudletVmEventInfo info = CloudletVmEventInfo.of(this);
            onCloudletFinishListeners.forEach(l -> l.update(info));
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
        return getLastExecutionInDatacenterInfo().dc;
    }

    private ExecutionInDatacenterInfo getLastExecutionInDatacenterInfo() {
        if(executionInDatacenterInfoList.isEmpty()) {
            return ExecutionInDatacenterInfo.NULL;
        }

        return executionInDatacenterInfoList.get(executionInDatacenterInfoList.size()-1);
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
    public void setExecStartTime(final double clockTime) {
        this.execStartTime = clockTime;
        write("Sets the execution start time to %s", num.format(clockTime));
    }

    @Override
    public double getExecStartTime() {
        return execStartTime;
    }

    @Override
    public boolean setWallClockTime(final double wallTime, final double actualCpuTime) {
        if (wallTime < 0.0 || actualCpuTime < 0.0 || executionInDatacenterInfoList.isEmpty()) {
            return false;
        }

        final ExecutionInDatacenterInfo datacenter = getLastExecutionInDatacenterInfo();
        datacenter.wallClockTime = wallTime;
        datacenter.actualCpuTime = actualCpuTime;

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
            setFinishTime(simulation.clock());
        }

        write("Sets Cloudlet status from %s to %s", status.name(), newStatus.name());

        this.status = newStatus;
        return true;
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
    public long getLength() {
        return cloudletLength;
    }

    @Override
    public long getTotalLength() {
        return getLength() * getNumberOfPes();
    }

    @Override
    public double getCostPerSec() {
        return getLastExecutionInDatacenterInfo().costPerSec;
    }

    @Override
    public double getWallClockTimeInLastExecutedDatacenter() {
        return getLastExecutionInDatacenterInfo().wallClockTime;
    }

    @Override
    public double getActualCpuTime(final Datacenter datacenter) {
        return getDatacenterInfo(datacenter).actualCpuTime;
    }

    @Override
    public double getCostPerSec(final Datacenter datacenter) {
        return getDatacenterInfo(datacenter).costPerSec;
    }

    @Override
    public long getFinishedLengthSoFar(final Datacenter datacenter) {
        return getDatacenterInfo(datacenter).finishedSoFar;
    }

    @Override
    public double getArrivalTime(final Datacenter datacenter) {
        return getDatacenterInfo(datacenter).arrivalTime;
    }

    @Override
    public double getWallClockTime(final Datacenter datacenter) {
        return getDatacenterInfo(datacenter).wallClockTime;
    }

    /**
     * Gets information about the cloudlet execution on a given Datacenter.
     *
     * @param datacenterId the Datacenter entity ID
     * @return the Cloudlet execution information on the given switches
     * or {@link ExecutionInDatacenterInfo#NULL} if the Cloudlet has never been executed there
     * @pre dc >= 0
     * @post $none
     */
    private ExecutionInDatacenterInfo getDatacenterInfo(final int datacenterId) {
        return executionInDatacenterInfoList.stream()
            .filter(info -> info.dc.getId() == datacenterId)
            .findFirst().orElse(ExecutionInDatacenterInfo.NULL);
    }

    /**
     * Gets information about the cloudlet execution on a given Datacenter.
     *
     * @param datacenter the Datacenter entity
     * @return the Cloudlet execution information on the given switches
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
     * Writes a particular history transaction of this Cloudlet into a log.
     *
     * @param str a history transaction of this Cloudlet
     * @pre str != null
     * @post $none
     */
    protected void write(final String str) {
        if(Objects.isNull(str)){
            return;
        }

        if (!recordTransactionHistory) {
            return;
        }

        if (Objects.isNull(history)) {
            // Creates the transaction history of this Cloudlet
            history = new StringBuffer(1000);
            history.append("Time below denotes the simulation time.");
            history.append(System.getProperty("line.separator"));
            history.append("Time (sec)       Description Cloudlet #").append(id);
            history.append(System.getProperty("line.separator"));
            history.append("------------------------------------------");
            history.append(System.getProperty("line.separator"));
            history.append(num.format(simulation.clock()));
            history.append("   Creates Cloudlet ID #").append(id);
            history.append(System.getProperty("line.separator"));
        }

        history.append(num.format(simulation.clock()));
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
    public Vm getVm() {
        return vm;
    }

    @Override
    public final Cloudlet setVm(final Vm vm) {
        this.vm = vm;
        return this;
    }

    @Override
    public double getActualCpuTime() {
        if (getFinishTime() == NOT_ASSIGNED) {
            return NOT_ASSIGNED;
        }

        return getFinishTime() - getExecStartTime();
    }

    @Override
    public double getTotalCost() {
        // cloudlet cost: execution cost...
        double totalCost = getTotalCpuCostForAllDatacenters();

        // ... plus input data transfer cost...
        totalCost += getAccumulatedBwCost();

        // ... plus output cost
        totalCost += getCostPerBw() * getOutputSize();
        return totalCost;
    }

    /**
     * Gets the total cost for using CPU on every Datacenter where the Cloudlet has executed.
     *
     * @return
     */
    private double getTotalCpuCostForAllDatacenters() {
        return executionInDatacenterInfoList.stream()
            .mapToDouble(dcInfo -> dcInfo.actualCpuTime * dcInfo.costPerSec)
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

        getRequiredFiles().add(fileName);
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
            final String temp = getRequiredFiles().get(i);

            if (temp.equals(filename)) {
                getRequiredFiles().remove(i);
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
        if (Objects.isNull(utilizationModelCpu)) {
            this.utilizationModelCpu = UtilizationModel.NULL;
        }
        else this.utilizationModelCpu = utilizationModelCpu;

        return this;
    }

    @Override
    public UtilizationModel getUtilizationModelRam() {
        return utilizationModelRam;
    }

    @Override
    public final Cloudlet setUtilizationModelRam(final UtilizationModel utilizationModelRam) {
        if (Objects.isNull(utilizationModelRam)) {
            this.utilizationModelRam = UtilizationModel.NULL;
        }
        else this.utilizationModelRam = utilizationModelRam;

        return this;
    }

    @Override
    public UtilizationModel getUtilizationModelBw() {
        return utilizationModelBw;
    }

    @Override
    public final Cloudlet setUtilizationModelBw(final UtilizationModel utilizationModelBw) {
        if (Objects.isNull(utilizationModelBw)) {
            this.utilizationModelBw = UtilizationModel.NULL;
        }
        else this.utilizationModelBw = utilizationModelBw;

        return this;
    }

    @Override
    public double getUtilizationOfCpu(final double time) {
        return getUtilizationModelCpu().getUtilization(time);
    }

    @Override
    public double getUtilizationOfRam(final double time) {
        return getUtilizationModelRam().getUtilization(time);
    }

    @Override
    public double getUtilizationOfBw(final double time) {
        return getUtilizationModelBw().getUtilization(time);
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
        dcInfo.dc = datacenter;
        dcInfo.costPerSec = datacenter.getCharacteristics().getCostPerSecond();

        // add into a list if moving to a new cloud switches
        executionInDatacenterInfoList.add(dcInfo);

        if (isRecordTransactionHistory()) {
            if (isAssignedToDatacenter()) {
                Datacenter oldDc = getLastExecutionInDatacenterInfo().dc;
                write("Moves Cloudlet from %s (ID #%d) to %s (ID #%d) with cost = $%.2f/sec",
                    oldDc.getName(), oldDc.getId(), dcInfo.dc.getName(), dcInfo.dc.getId(), dcInfo.costPerSec);

            } else {
                write("Allocates this Cloudlet to %s (ID #%d) with cost = $%.2f/sec",
                    dcInfo.dc.getName(), dcInfo.dc.getId(), dcInfo.costPerSec);
            }
        }

        setLastExecutedDatacenterIndex(getLastExecutedDatacenterIndex() + 1);

        this.setCostPerBw(datacenter.getCharacteristics().getCostPerBw());
        setAccumulatedBwCost(this.costPerBw * getFileSize());
    }

    @Override
    public double registerArrivalInDatacenter() {
        if (!isAssignedToDatacenter()) {
            return NOT_ASSIGNED;
        }

        final ExecutionInDatacenterInfo dcInfo = executionInDatacenterInfoList.get(lastExecutedDatacenterIndex);
        dcInfo.arrivalTime = simulation.clock();

        return dcInfo.arrivalTime;
    }

    @Override
    public boolean isAssignedToDatacenter() {
        return !executionInDatacenterInfoList.isEmpty();
    }

    @Override
    public double getLastDatacenterArrivalTime() {
        return getLastExecutionInDatacenterInfo().arrivalTime;
    }

    @Override
    public String getUid() {
        return UniquelyIdentificable.getUid(broker.getId(), id);
    }

    @Override
    public Simulation getSimulation() {
        return this.simulation;
    }

    @Override
    public Cloudlet setSimulation(Simulation simulation) {
        this.simulation = simulation;
        return this;
    }

    /**
     * Internal class that keeps track of Cloudlet's movement in different
     * {@link Datacenter Datacenters}. Each time a cloudlet is run on a given Datacenter, the cloudlet's
     * execution history on each Datacenter is registered at {@link #getLastExecutionInDatacenterInfo()}
     */
    protected static class ExecutionInDatacenterInfo {
        /**
         * Cloudlet's submission (arrival) time to a Datacenter
         * or {@link #NOT_ASSIGNED} if the Cloudlet was not assigned to a Datacenter yet.
         */
        public double arrivalTime;

        /**
         * The time this Cloudlet resides in a Datacenter (from arrival time
         * until departure time, that may include waiting time).
         */
        public double wallClockTime;

        /**
         * The total time the Cloudlet spent being executed in a Datacenter.
         */
        public double actualCpuTime;

        /**
         * Cost per second a Datacenter charge to execute this Cloudlet.
         */
        public double costPerSec;

        /**
         * Cloudlet's length finished so far (in MI).
         */
        public long finishedSoFar;

        /**
         * a Datacenter where the Cloudlet will be executed
         */
        public Datacenter dc;


        public ExecutionInDatacenterInfo(){
            this.dc = Datacenter.NULL;
            this.arrivalTime = NOT_ASSIGNED;
        }

        public static final ExecutionInDatacenterInfo NULL = new ExecutionInDatacenterInfo();
    }
}
