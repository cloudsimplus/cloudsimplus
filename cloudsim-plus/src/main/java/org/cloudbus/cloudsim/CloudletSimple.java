/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.listeners.VmToCloudletEventInfo;
import org.cloudbus.cloudsim.listeners.EventListener;

/**
 * Cloudlet implements the basic features of an application/job/task to be executed
 * by a {@link Vm} on behalf of a given user. It stores, despite all the
 * information encapsulated in the Cloudlet, the ID of the VM running it.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 * @see DatacenterBroker
 *
 * @todo @author manoelcampos Cloudlets doesn't have a priority attribute
 * to define which of them will be executed first.
 * For instance, considering a VM with just one PE
 * and several cloudlets, the execution order of cloudlets can be defined by
 * their priorities.
 */
public class CloudletSimple extends CloudletAbstract {
    /** @see #getId() */
    private final int id;

    /** @see #getUserId() */
    private int userId;

    /** @see #getCloudletLength() */
    protected long cloudletLength;

    /** @see #getCloudletFileSize() */
    private final long cloudletFileSize;

    /** @see #getCloudletOutputSize() */
    private final long cloudletOutputSize;


    /** @see #getNumberOfPes() */
    private int numberOfPes;

    /** @see #getStatus() */
    private Status status;

    /** @see #getExecStartTime() */
    private double execStartTime;

    /** @see #getFinishTime() */
    private double finishTime;

    /** @see #getReservationId() */
    private int reservationId = NOT_ASSIGNED;

    /**
     * @see #isRecordTransactionHistory()
     */
    private boolean recordTransactionHistory;

    /**
     * Stores the operating system line separator.
     */
    private final String newline;

    /**
     * The cloudlet transaction history.
     */
    private StringBuffer history;

    /** @see #getPriority() */
    private int priority;

    /** @see #getNetServiceLevel() */
    private int netServiceLevel;

    /**
     * The format of decimal numbers.
     */
    private final DecimalFormat num;

    /** @see #getVmId() */
    protected int vmId;

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

    /** @see #getRequiredFiles() */
    private List<String> requiredFiles;

    /**@see #getOnCloudletFinishEventListener() */
    private EventListener<VmToCloudletEventInfo> onCloudletFinishEventListener = EventListener.NULL;

    /**@see #getOnUpdateCloudletProcessingListener() () */
    private EventListener<VmToCloudletEventInfo> onUpdateCloudletProcessingListener = EventListener.NULL;

    /**
     * @see #getSubmissionDelay()
     */
    private double submissionDelay;

    /**
     * Instantiates a new Cloudlet object. The Cloudlet length, input and output
     * file sizes should be greater than or equal to 1.
     *
     * @param cloudletId the unique ID of this cloudlet
     * @param cloudletLength the length or size (in MI) of this cloudlet to be
     * executed in a VM
     * @param cloudletFileSize the file size (in byte) of this cloudlet
     * <tt>BEFORE</tt> submitting to a Datacenter
     * @param cloudletOutputSize the file size (in byte) of this cloudlet
     * <tt>AFTER</tt> finish executing by a VM
     * @param pesNumber the pes number
     * @param utilizationModelCpu the utilization model of cpu
     * @param utilizationModelRam the utilization model of ram
     * @param utilizationModelBw the utilization model of bw
     *
     * @pre cloudletID >= 0
     * @pre cloudletLength >= 0.0
     * @pre cloudletFileSize >= 1
     * @pre cloudletOutputSize >= 1
     * @post $none
     */
    public CloudletSimple(
            final int cloudletId,
            final long cloudletLength,
            final int pesNumber,
            final long cloudletFileSize,
            final long cloudletOutputSize,
            final UtilizationModel utilizationModelCpu,
            final UtilizationModel utilizationModelRam,
            final UtilizationModel utilizationModelBw) {
        super();
        num = new DecimalFormat("#0.00#");
        newline = System.getProperty("line.separator");
        userId = NOT_ASSIGNED;          // to be set by a Broker or user
        status = Status.CREATED;
        this.id = cloudletId;
        numberOfPes = pesNumber;
        execStartTime = 0.0;
        setFinishTime(NOT_ASSIGNED);    // meaning this Cloudlet hasn't finished yet
        priority = 0;
        netServiceLevel = 0;

        // Cloudlet length, Input and Output size should be at least 1 byte.
        this.cloudletLength = Math.max(1, cloudletLength);
        this.cloudletFileSize = Math.max(1, cloudletFileSize);
        this.cloudletOutputSize = Math.max(1, cloudletOutputSize);

        this.recordTransactionHistory = false;

        vmId = NOT_ASSIGNED;
        setAccumulatedBwCost(0.0);
        setCostPerBw(0.0);
        setSubmissionDelay(0.0);

        requiredFiles = new LinkedList<>();

        setUtilizationModelCpu(utilizationModelCpu);
        setUtilizationModelRam(utilizationModelRam);
        setUtilizationModelBw(utilizationModelBw);
    }

    @Override
    public EventListener<VmToCloudletEventInfo> getOnCloudletFinishEventListener() {
        return onCloudletFinishEventListener;
    }

    @Override
    public void setOnCloudletFinishEventListener(EventListener<VmToCloudletEventInfo> onCloudletFinishEventListener) {
        if(onCloudletFinishEventListener == null)
            onCloudletFinishEventListener = EventListener.NULL;

        this.onCloudletFinishEventListener = onCloudletFinishEventListener;
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
    public boolean hasReserved() {
        return reservationId > NOT_ASSIGNED;
    }

    @Override
    public boolean setCloudletLength(final long cloudletLength) {
        if (cloudletLength <= 0) {
            return false;
        }

        this.cloudletLength = cloudletLength;
        return true;
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
    public boolean setNumberOfPes(final int numberOfPes) {
        if (numberOfPes > 0) {
            this.numberOfPes = numberOfPes;
            return true;
        }
        return false;
    }

    @Override
    public int getNumberOfPes() {
        return numberOfPes;
    }

    @Override
    public String getCloudletHistory() {
        if (history == null)
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
    public void setUserId(final int userId) {
        this.userId = userId;
        write("Assigned the Cloudlet to %s (ID #%d)", CloudSim.getEntityName(userId), userId);
    }

    @Override
    public int getUserId() {
        return userId;
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
    public void assignCloudletToDatacenter(
            final int datacenterId, final double costPerCpuSec, final double costPerByteOfBw) {
        assignCloudletToDatacenter(datacenterId, costPerCpuSec);
        this.setCostPerBw(costPerByteOfBw);
        setAccumulatedBwCost(costPerByteOfBw * getCloudletFileSize());
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
        final ExecutionInDatacenterInfo datacenter = new ExecutionInDatacenterInfo();
        datacenter.datacenterId = datacenterId;
        datacenter.costPerSec = costPerCpuSec;
        datacenter.name = CloudSim.getEntityName(datacenterId);

        // add into a list if moving to a new cloud datacenter
        getExecutionInDatacenterInfoList().add(datacenter);

        if(recordTransactionHistory){
            if (isAssignedToDatacenter()) {
                final int id = getExecutionInDatacenterInfoList().get(getLastExecutedDatacenterIndex()).datacenterId;
                final String name = getExecutionInDatacenterInfoList().get(getLastExecutedDatacenterIndex()).name;
                write(
                    "Moves Cloudlet from %s (ID #%d) to %s (ID #%d) with cost = $%s/sec",
                    name, id, datacenter.name, datacenterId, num.format(costPerCpuSec));

            } else {
                write(
                    "Allocates this Cloudlet to %s (ID #%d) with cost = $%s/sec",
                    datacenter.name,  datacenterId, num.format(costPerCpuSec));
            }
        }

        setLastExecutedDatacenterIndex(getLastExecutedDatacenterIndex() + 1);
    }

    @Override
    public double getDatacenterArrivalTime() {
        if (getLastExecutedDatacenterIndex() == NOT_ASSIGNED) {
            return NOT_ASSIGNED;
        }
        return getExecutionInDatacenterInfoList().get(getLastExecutedDatacenterIndex()).arrivalTime;
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
            setFinishTime(CloudSim.clock());
        }

        write("Sets Cloudlet status from %s to %s",
              getCloudletStatusString(),
              CloudletSimple.getCloudletStatusString(newStatus));

        this.status = newStatus;
        return true;
    }

    /**
     * Sets the {@link #getFinishTime() finish time} of this cloudlet in the latest Datacenter.
     * @param finishTime the finish time
     */
    private void setFinishTime(final double finishTime) {
        this.finishTime = finishTime;
    }

    @Deprecated @Override
    public Status getCloudletStatus() {
        return status;
    }

    @Override
    public String getCloudletStatusString() {
        return CloudletSimple.getCloudletStatusString(status);
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
        if (datacenter != null) {
            return datacenter.actualCPUTime;
        }
        return 0.0;
    }

    @Override
    public double getCostPerSec(final int datacenterId) {
        ExecutionInDatacenterInfo resource = getDatacenterInfo(datacenterId);
        if (resource != null) {
            return resource.costPerSec;
        }
        return 0.0;
    }

    @Override
    public long getCloudletFinishedSoFar(final int datacenterId) {
        ExecutionInDatacenterInfo datacenter = getDatacenterInfo(datacenterId);
        if (datacenter != null) {
            return datacenter.finishedSoFar;
        }

        return 0;
    }

    @Override
    public double getSubmissionTime(final int datacenterId) {
        ExecutionInDatacenterInfo datacenter = getDatacenterInfo(datacenterId);
        if (datacenter != null) {
            return datacenter.arrivalTime;
        }
        return 0.0;
    }

    @Override
    public double getWallClockTime(final int datacenterId) {
        ExecutionInDatacenterInfo datacenter = getDatacenterInfo(datacenterId);
        if (datacenter != null) {
            return datacenter.wallClockTime;
        }
        return 0.0;
    }

    /**
     * Gets information about the cloudlet execution on a given Datacenter.
     *
     * @param datacenterId the Datacenter entity ID
     * @return the Cloudlet execution information on the given datacenter
     * or null if the Cloudlet has never been executed there
     * @pre datacenterId >= 0
     * @post $none
     */
    private ExecutionInDatacenterInfo getDatacenterInfo(final int datacenterId) {
        for (ExecutionInDatacenterInfo datacenter : getExecutionInDatacenterInfoList()) {
            if (datacenter.datacenterId == datacenterId) {
                return datacenter;
            }
        }
        return null;
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

        if (history == null) {
            // Creates the transaction history of this Cloudlet
            history = new StringBuffer(1000);
            history.append("Time below denotes the simulation time.");
            history.append(System.getProperty("line.separator"));
            history.append("Time (sec)       Description Cloudlet #").append(id);
            history.append(System.getProperty("line.separator"));
            history.append("------------------------------------------");
            history.append(System.getProperty("line.separator"));
            history.append(num.format(CloudSim.clock()));
            history.append("   Creates Cloudlet ID #").append(id);
            history.append(System.getProperty("line.separator"));
        }

        history.append(num.format(CloudSim.clock()));
        history.append("   ").append(str).append(newline);
    }


    /**
     * Writes a formatted particular history transaction of this Cloudlet into a log.
     *
     * @param format the format of the Cloudlet's history transaction, according
     * to the format parameter of {@link String#format(java.lang.String, java.lang.Object...)}
     * @param args The list of values to be shown in the history,
     * that are referenced by the format.
     * @pre format != null
     * @post $none
     * @see #write(java.lang.String)
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
    public int getVmId() {
        return vmId;
    }

    @Override
    public void setVmId(final int vmId) {
        this.vmId = vmId;
    }

    @Override
    public double getActualCPUTime() {
        if(getFinishTime() == NOT_ASSIGNED)
            return NOT_ASSIGNED;

        return getFinishTime() - getExecStartTime();
    }

    @Override
    public double getProcessingCost() {
        /**
         * @todo @author manoelcampos It is not computing the processing
         * cost, that depends on cloudlet length and the
         * CloudletScheduler that the VM will use to execute the cloudlet.
         * Thus, it may be more complex to estimate that value.
         */

        // cloudlet cost: execution cost...
        // double cost = getProcessingCost();
        double cost = 0;

        // ... plus input data transfer cost...
        cost += getAccumulatedBwCost();

        // ... plus output cost
        cost += getCostPerBw() * getCloudletOutputSize();
        return cost;
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
        if(requiredFiles == null)
            this.requiredFiles = new LinkedList<>();
        else this.requiredFiles = requiredFiles;
    }

    @Override
    public boolean addRequiredFile(final String fileName) {
        //check whether filename already exists or not
        for (int i = 0; i < getRequiredFiles().size(); i++) {
            final String temp = getRequiredFiles().get(i);
            if (temp.equals(fileName)) {
                return false;
            }
        }

        getRequiredFiles().add(fileName);
        return true;
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
        if (getRequiredFiles() != null && getRequiredFiles().size() > 0) {
            result = true;
        }

        return result;
    }

    @Override
    public UtilizationModel getUtilizationModelCpu() {
        return utilizationModelCpu;
    }

    @Override
    public final void setUtilizationModelCpu(final UtilizationModel utilizationModelCpu) {
        if(utilizationModelCpu == null)
            throw new IllegalArgumentException("The CPU utilization model cannot be null");
        this.utilizationModelCpu = utilizationModelCpu;
    }

    @Override
    public UtilizationModel getUtilizationModelRam() {
        return utilizationModelRam;
    }

    @Override
    public final void setUtilizationModelRam(final UtilizationModel utilizationModelRam) {
        if(utilizationModelRam == null)
            throw new IllegalArgumentException("The RAM utilization model cannot be null");
        this.utilizationModelRam = utilizationModelRam;
    }

    @Override
    public UtilizationModel getUtilizationModelBw() {
        return utilizationModelBw;
    }

    @Override
    public final void setUtilizationModelBw(final UtilizationModel utilizationModelBw) {
        if(utilizationModelBw == null)
            throw new IllegalArgumentException("The BW utilization model cannot be null");
        this.utilizationModelBw = utilizationModelBw;
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
    private void setCostPerBw(double costPerBw) {
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
    private void setAccumulatedBwCost(double accumulatedBwCost) {
        this.accumulatedBwCost = accumulatedBwCost;
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

    @Override
    public EventListener<VmToCloudletEventInfo> getOnUpdateCloudletProcessingListener() {
        return this.onUpdateCloudletProcessingListener;
    }

    @Override
    public void setOnUpdateCloudletProcessingListener(EventListener<VmToCloudletEventInfo> onUpdateCloudletProcessingListener) {
        this.onUpdateCloudletProcessingListener = onUpdateCloudletProcessingListener;
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
    public boolean isBoundedToVm() {
        return vmId != Cloudlet.NOT_ASSIGNED;
    }

    @Override
    public String toString() {
        return String.format("Cloudlet %d of User %d", id, userId);
    }

    /**
     * <p>Compares this Cloudlet with another one, considering
     * the {@link #getCloudletTotalLength() total length of the Cloudlets's}.</p>
     *
     * @param o the Vm to be compared to
     * @return {@inheritDoc }
     * @see #getCloudletTotalLength()
     */
    @Override
    public int compareTo(Cloudlet o) {
        return Long.compare(this.getCloudletTotalLength(), o.getCloudletTotalLength());
    }


}
