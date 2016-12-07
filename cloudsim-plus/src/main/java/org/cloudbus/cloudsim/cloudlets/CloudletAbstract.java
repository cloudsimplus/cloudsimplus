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

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.listeners.VmToCloudletEventInfo;

/**
 * A base class for {@link Cloudlet} implementations.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 */
public abstract class CloudletAbstract implements Cloudlet {
    /** @see #getId() */
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
    /** @see #getBrokerId() */
    private DatacenterBroker broker;
    /** @see #getCloudletLength() */
    private long cloudletLength;
    /** @see #getNumberOfPes() */
    private int numberOfPes;
    /** @see #getStatus() */
    private Status status;
    /** @see #getExecStartTime() */
    private double execStartTime;
    /**
     * @see #isRecordTransactionHistory()
     */
    private boolean recordTransactionHistory;
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
    private int lastExecutedDatacenterIndex;
    /** @see #getCloudletFileSize() */
    private long cloudletFileSize;
    /** @see #getCloudletOutputSize() */
    private long cloudletOutputSize;
    /** @see #getFinishTime() */
    private double finishTime;
    /** @see #getReservationId() */
    private int reservationId = NOT_ASSIGNED;
    /**
     * The cloudlet transaction history.
     */
    private StringBuffer history;
    /** @see #getCostPerBw() */
    private double costPerBw;
    /** @see #getAccumulatedBwCost()  */
    private double accumulatedBwCost;
    /** @see #getUtilizationModelCpu() */
    private UtilizationModel utilizationModelCpu;
    /** @see #getUtilizationModelRam() */
    private UtilizationModel utilizationModelRam;
    /** @see #getUtilizationModelBw() */
    private UtilizationModel utilizationModelBw;
    /**@see #getOnCloudletFinishEventListener() */
    private EventListener<VmToCloudletEventInfo> onCloudletFinishEventListener = EventListener.NULL;
    /**@see #getOnUpdateCloudletProcessingListener() () */
    private EventListener<VmToCloudletEventInfo> onUpdateCloudletProcessingListener = EventListener.NULL;
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
     * @param cloudletId id of the Cloudlet
     * @param cloudletLength the length or size (in MI) of this cloudlet to be executed in a VM
     * @param pesNumber number of PEs that Cloudlet will require
     */
    protected CloudletAbstract(final int cloudletId, final long cloudletLength, final int pesNumber){
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
        this.status = Status.CREATED;
        this.priority = 0;
        this.setNumberOfPes(pesNumber);
        this.recordTransactionHistory = false;

        this.lastExecutedDatacenterIndex = NOT_ASSIGNED;
        setBroker(DatacenterBroker.NULL);
        this.simulation = Simulation.NULL;
        setFinishTime(NOT_ASSIGNED);    // meaning this Cloudlet hasn't finished yet
        setVm(Vm.NULL);

        this.setCloudletLength(cloudletLength);
        this.setCloudletFileSize(1);
        this.setCloudletOutputSize(1);

        setAccumulatedBwCost(0.0);
        setCostPerBw(0.0);
        setSubmissionDelay(0.0);

        setUtilizationModelCpu(UtilizationModel.NULL);
        setUtilizationModelRam(UtilizationModel.NULL);
        setUtilizationModelBw(UtilizationModel.NULL);
    }

    /**
     * Gets the string representation of the given Cloudlet status code.
     *
     * @param status The status to get a string representation
     * @return the Cloudlet status code as a string or <tt>null</tt> if the
     * status code is unknown
     * @pre $none
     * @post $none
     */
    public static String getCloudletStatusString(Status status) {
        return status.name();
    }

    @Override
    public double registerArrivalOfCloudletIntoDatacenter() {
        if (!isAssignedToDatacenter()) {
            return NOT_ASSIGNED;
        }

        final ExecutionInDatacenterInfo dcInfo = executionInDatacenterInfoList.get(lastExecutedDatacenterIndex);
        dcInfo.arrivalTime = simulation.clock();

        return dcInfo.arrivalTime;
    }

    @Override
    public boolean isAssignedToDatacenter() {
        return getLastExecutedDatacenterIndex() > NOT_ASSIGNED;
    }

    protected int getLastExecutedDatacenterIndex() {
        return lastExecutedDatacenterIndex;
    }

    protected void setLastExecutedDatacenterIndex(int lastExecutedDatacenterIndex) {
        this.lastExecutedDatacenterIndex = lastExecutedDatacenterIndex;
    }

    protected List<ExecutionInDatacenterInfo> getExecutionInDatacenterInfoList() {
        return executionInDatacenterInfoList;
    }

    @Override
    public Cloudlet setUtilizationModel(UtilizationModel utilizationModel) {
        setUtilizationModelBw(utilizationModel);
        setUtilizationModelRam(utilizationModel);
        setUtilizationModelCpu(utilizationModel);
        return this;
    }

    @Override
    public EventListener<VmToCloudletEventInfo> getOnCloudletFinishEventListener() {
        return onCloudletFinishEventListener;
    }

    @Override
    public Cloudlet setOnCloudletFinishEventListener(EventListener<VmToCloudletEventInfo> onCloudletFinishEventListener) {
        if(Objects.isNull(onCloudletFinishEventListener)) {
            onCloudletFinishEventListener = EventListener.NULL;
        }

        this.onCloudletFinishEventListener = onCloudletFinishEventListener;
        return this;
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
    public final Cloudlet setCloudletLength(final long cloudletLength) {
        if (cloudletLength <= 0) {
            throw new IllegalArgumentException("Cloudlet length has to be greater than zero.");
        }

        this.cloudletLength = cloudletLength;
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
        if (getLastExecutedDatacenterIndex() == NOT_ASSIGNED) {
            return 0;
        }

        // use the latest resource submission time
        final double subTime = getExecutionInDatacenterInfoList().get(getLastExecutedDatacenterIndex()).arrivalTime;
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
    public String getCloudletHistory() {
        if (Objects.isNull(history))
            return String.format(NO_HISTORY_IS_RECORDED_FOR_CLOUDLET, id);

        return history.toString();
    }

    @Override
    public long getCloudletFinishedSoFar() {
        if (getLastExecutedDatacenterIndex() == NOT_ASSIGNED) {
            return 0;
        }

        final long finishedMI = getExecutionInDatacenterInfoList().get(getLastExecutedDatacenterIndex()).finishedSoFar;
        return Math.min(finishedMI, getCloudletLength());
    }

    @Override
    public boolean isFinished() {
        if (getLastExecutedDatacenterIndex() == NOT_ASSIGNED) {
            return false;
        }

        final long finishedMI = getExecutionInDatacenterInfoList().get(getLastExecutedDatacenterIndex()).finishedSoFar;
        final long remainingMI = getCloudletLength() - finishedMI;

        return remainingMI <= 0.0;
    }

    @Override
    public boolean setCloudletFinishedSoFar(final long length) {
        if(length > this.getCloudletLength())
            throw new IllegalArgumentException(
                String.format(
                    "The length parameter (%d) cannot be greater than the cloudletLength attribute (%d).",
                    length, this.getCloudletLength()));

        if (length < 0.0 || getLastExecutedDatacenterIndex() <= NOT_ASSIGNED) {
            return false;
        }

        final ExecutionInDatacenterInfo res = getExecutionInDatacenterInfoList().get(getLastExecutedDatacenterIndex());
        res.finishedSoFar = length;

        write("Set the length's finished so far to %d", length);
        return true;
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
    public int getBrokerId() {
        return broker.getId();
    }

    @Override
    public int getDatacenterId() {
        if (getLastExecutedDatacenterIndex() == NOT_ASSIGNED) {
            return NOT_ASSIGNED;
        }
        return getExecutionInDatacenterInfoList().get(getLastExecutedDatacenterIndex()).datacenterId;
    }

    @Override
    public long getCloudletFileSize() {
        return cloudletFileSize;
    }

    @Override
    public long getCloudletOutputSize() {
        return cloudletOutputSize;
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
    public boolean setWallClockTime(final double wallTime, final double actualCPUTime) {
        if (wallTime < 0.0 || actualCPUTime < 0.0 || getLastExecutedDatacenterIndex() <= NOT_ASSIGNED) {
            return false;
        }

        final ExecutionInDatacenterInfo datacenter = getExecutionInDatacenterInfoList().get(getLastExecutedDatacenterIndex());
        datacenter.wallClockTime = wallTime;
        datacenter.actualCPUTime = actualCPUTime;

        write("Sets the wall clock time to %s and the actual CPU time to %s",
              num.format(wallTime), num.format(actualCPUTime));

        return true;
    }

    @Override
    public boolean setCloudletStatus(final Status newStatus) {
        // if the new status is same as current one, then ignore the rest
        if (this.status == newStatus) {
            return false;
        }

        if (newStatus == Status.SUCCESS) {
            setFinishTime(simulation.clock());
        }

        write("Sets Cloudlet status from %s to %s",
              getCloudletStatusString(),
              CloudletAbstract.getCloudletStatusString(newStatus));

        this.status = newStatus;
        return true;
    }

    /**
     * Sets the {@link #getFinishTime() finish time} of this cloudlet in the latest Datacenter.
     * @param finishTime the finish time
     */
    protected final void setFinishTime(final double finishTime) {
        this.finishTime = finishTime;
    }

    @Deprecated @Override
    public Status getCloudletStatus() {
        return status;
    }

    @Override
    public String getCloudletStatusString() {
        return CloudletAbstract.getCloudletStatusString(status);
    }

    @Override
    public long getCloudletLength() {
        return cloudletLength;
    }

    @Override
    public long getCloudletTotalLength() {
        return getCloudletLength() * getNumberOfPes();
    }

    @Override
    public double getCostPerSec() {
        if (getLastExecutedDatacenterIndex() == NOT_ASSIGNED) {
            return 0.0;
        }

        return getExecutionInDatacenterInfoList().get(getLastExecutedDatacenterIndex()).costPerSec;
    }

    @Override
    public double getWallClockTimeInLastExecutedDatacenter() {
        if (getLastExecutedDatacenterIndex() == NOT_ASSIGNED) {
            return 0.0;
        }
        return getExecutionInDatacenterInfoList().get(getLastExecutedDatacenterIndex()).wallClockTime;
    }

    @Override
    public double getActualCPUTime(final int datacenterId) {
        ExecutionInDatacenterInfo datacenter = getDatacenterInfo(datacenterId);
        return datacenter.actualCPUTime;
    }

    @Override
    public double getCostPerSec(final int datacenterId) {
        ExecutionInDatacenterInfo resource = getDatacenterInfo(datacenterId);
        return resource.costPerSec;
    }

    @Override
    public long getCloudletFinishedSoFar(final int datacenterId) {
        ExecutionInDatacenterInfo datacenter = getDatacenterInfo(datacenterId);
        return datacenter.finishedSoFar;
    }

    @Override
    public double getArrivalTime(final int datacenterId) {
        ExecutionInDatacenterInfo datacenterInfo = getDatacenterInfo(datacenterId);
        return datacenterInfo.arrivalTime;
    }

    @Override
    public double getWallClockTime(final int datacenterId) {
        ExecutionInDatacenterInfo datacenter = getDatacenterInfo(datacenterId);
        return datacenter.wallClockTime;
    }

    /**
     * Gets information about the cloudlet execution on a given Datacenter.
     *
     * @param datacenterId the Datacenter entity ID
     * @return the Cloudlet execution information on the given switches
     * or {@link ExecutionInDatacenterInfo#NULL} if the Cloudlet has never been executed there
     * @pre datacenterId >= 0
     * @post $none
     */
    private ExecutionInDatacenterInfo getDatacenterInfo(final int datacenterId) {
        return getExecutionInDatacenterInfoList().stream()
            .filter(info -> info.datacenterId == datacenterId)
            .findFirst().orElse(ExecutionInDatacenterInfo.NULL);
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
     * to the format parameter of {@link String#format(String, Object...)}
     * @param args The list of values to be shown in the history,
     * that are referenced by the format.
     * @pre format != null
     * @post $none
     * @see #write(String)
     */
    protected void write(final String format, Object... args) {
        final String str = String.format(format, args);
        write(str);
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
    public double getActualCPUTime() {
        if(getFinishTime() == NOT_ASSIGNED)
            return NOT_ASSIGNED;

        return getFinishTime() - getExecStartTime();
    }

    @Override
    public double getTotalCost() {
        // cloudlet cost: execution cost...
        double totalCost = getTotalCpuCostForAllDatacenters();

        // ... plus input data transfer cost...
        totalCost += getAccumulatedBwCost();

        // ... plus output cost
        totalCost += getCostPerBw() * getCloudletOutputSize();
        return totalCost;
    }

    /**
     * Gets the total cost for using CPU on every Datacenter where the Cloudlet has executed.
     * @return
     */
    private double getTotalCpuCostForAllDatacenters() {
        return getExecutionInDatacenterInfoList().stream()
            .mapToDouble(dcInfo -> dcInfo.actualCPUTime * dcInfo.costPerSec)
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
        if(Objects.isNull(requiredFiles)) {
            this.requiredFiles = new LinkedList<>();
        }
        else {
            this.requiredFiles = requiredFiles;
        }
    }

    @Override
    public boolean addRequiredFile(final String fileName) {
        if(getRequiredFiles().stream().anyMatch(s -> s.equals(fileName))){
            return false;
        }

        getRequiredFiles().add(fileName);
        return true;
    }

    @Override
    public boolean addRequiredFiles(List<String> fileNames) {
        boolean atLeastOneFileAdded = false;
        for(String fileName: fileNames){
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
        if(Objects.isNull(utilizationModelCpu)) {
            throw new IllegalArgumentException("The CPU utilization model cannot be null");
        }
        this.utilizationModelCpu = utilizationModelCpu;
        return this;
    }

    @Override
    public UtilizationModel getUtilizationModelRam() {
        return utilizationModelRam;
    }

    @Override
    public final Cloudlet setUtilizationModelRam(final UtilizationModel utilizationModelRam) {
        if(Objects.isNull(utilizationModelRam)) {
            throw new IllegalArgumentException("The RAM utilization model cannot be null");
        }
        this.utilizationModelRam = utilizationModelRam;
        return this;
    }

    @Override
    public UtilizationModel getUtilizationModelBw() {
        return utilizationModelBw;
    }

    @Override
    public final Cloudlet setUtilizationModelBw(final UtilizationModel utilizationModelBw) {
        if(Objects.isNull(utilizationModelBw)) {
            throw new IllegalArgumentException("The BW utilization model cannot be null");
        }
        this.utilizationModelBw = utilizationModelBw;
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
     * @param accumulatedBwCost the accumulated bw cost to set
     */
    protected final void setAccumulatedBwCost(double accumulatedBwCost) {
        this.accumulatedBwCost = accumulatedBwCost;
    }

    @Override
    public EventListener<VmToCloudletEventInfo> getOnUpdateCloudletProcessingListener() {
        return this.onUpdateCloudletProcessingListener;
    }

    @Override
    public Cloudlet setOnUpdateCloudletProcessingListener(EventListener<VmToCloudletEventInfo> onUpdateCloudletProcessingListener) {
        this.onUpdateCloudletProcessingListener = onUpdateCloudletProcessingListener;
        return this;
    }

    @Override
    public double getSubmissionDelay() {
       return this.submissionDelay;
    }

    @Override
    public final void setSubmissionDelay(double submissionDelay) {
        this.submissionDelay = submissionDelay;
    }

    @Override
    public boolean isBindToVm() {
        return vm != Vm.NULL;
    }

    @Override
    public final Cloudlet setCloudletFileSize(long cloudletFileSize) {
        if (cloudletFileSize <= 0) {
            throw new IllegalArgumentException("Cloudlet fize size has to be greater than zero.");
        }

        this.cloudletFileSize = cloudletFileSize;
        return this;
    }

    @Override
    public final Cloudlet setCloudletOutputSize(long cloudletOutputSize) {
        if (cloudletOutputSize <= 0) {
            throw new IllegalArgumentException("Cloudlet output size has to be greater than zero.");
        }
        this.cloudletOutputSize = cloudletOutputSize;
        return this;
    }

    /**
     * Indicates if Cloudlet transaction history is to be recorded or not.
     * @see #getCloudletHistory()
     * @return
     */
    public boolean isRecordTransactionHistory() {
        return recordTransactionHistory;
    }

    /**
     * Sets the Cloudlet transaction history writing.
     *
     * @param recordTransactionHistory true enables transaction history writing,
     * false disables.
     */
    public void setRecordTransactionHistory(boolean recordTransactionHistory) {
        this.recordTransactionHistory = recordTransactionHistory;
    }

    /**
     * Sets the parameters of the Datacenter where the Cloudlet is going to be
     * executed. From the second time this method is called, every call make the
     * cloudlet to be migrated to the indicated Datacenter.<br>
     *
     * NOTE: This method <tt>should</tt> be called only by a resource entity,
     * not the user or owner of this Cloudlet.
     *
     * @param datacenterId the id of Datacenter where the cloudlet will be executed
     * @param costPerCpuSec the cost per second of running the cloudlet on the given Datacenter
     *
     * @pre resourceID >= 0
     * @pre cost > 0.0
     * @post $none
     */
    protected void assignCloudletToDatacenter(final int datacenterId, final double costPerCpuSec) {
        final ExecutionInDatacenterInfo datacenterInfo = new ExecutionInDatacenterInfo();
        datacenterInfo.datacenterId = datacenterId;
        datacenterInfo.costPerSec = costPerCpuSec;
        datacenterInfo.datacenterName = simulation.getEntityName(datacenterId);

        // add into a list if moving to a new cloud switches
        getExecutionInDatacenterInfoList().add(datacenterInfo);

        if(isRecordTransactionHistory()){
            if (isAssignedToDatacenter()) {
                final int id = getExecutionInDatacenterInfoList().get(getLastExecutedDatacenterIndex()).datacenterId;
                final String name = getExecutionInDatacenterInfoList().get(getLastExecutedDatacenterIndex()).datacenterName;
                write("Moves Cloudlet from %s (ID #%d) to %s (ID #%d) with cost = $%.2f/sec",
                    name, id, datacenterInfo.datacenterName, datacenterId, costPerCpuSec);

            } else {
                write("Allocates this Cloudlet to %s (ID #%d) with cost = $%.2f/sec",
                    datacenterInfo.datacenterName,  datacenterId, costPerCpuSec);
            }
        }

        setLastExecutedDatacenterIndex(getLastExecutedDatacenterIndex() + 1);
    }

    @Override
    public void assignCloudletToDatacenter(
            final int datacenterId, final double costPerCpuSec, final double costPerByteOfBw) {
        assignCloudletToDatacenter(datacenterId, costPerCpuSec);
        this.setCostPerBw(costPerByteOfBw);
        setAccumulatedBwCost(costPerByteOfBw * getCloudletFileSize());
    }

    @Override
    public double getDatacenterArrivalTime() {
        if (getLastExecutedDatacenterIndex() == NOT_ASSIGNED) {
            return NOT_ASSIGNED;
        }
        return getExecutionInDatacenterInfoList().get(getLastExecutedDatacenterIndex()).arrivalTime;
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
     * execution history on each Datacenter is registered at {@link #getExecutionInDatacenterInfoList()}
     */
    protected static class ExecutionInDatacenterInfo {
        /**
         * Cloudlet's submission (arrival) time to a Datacenter.
         */
        public double arrivalTime = 0.0;

        /**
         * The time this Cloudlet resides in a Datacenter (from arrival time
         * until departure time, that may include waiting time).
         */
        public double wallClockTime = 0.0;

        /**
         * The total time the Cloudlet spent being executed in a Datacenter.
         */
        public double actualCPUTime = 0.0;

        /**
         * Cost per second a Datacenter charge to execute this Cloudlet.
         */
        public double costPerSec = 0.0;

        /**
         * Cloudlet's length finished so far (in MI).
         */
        public long finishedSoFar = 0;

        /**
         * a Datacenter id.
         */
        public int datacenterId = NOT_ASSIGNED;

        /**
         * The Datacenter datacenterName.
         */
        public String datacenterName = "";

        public static final ExecutionInDatacenterInfo NULL = new ExecutionInDatacenterInfo();
    }
}
