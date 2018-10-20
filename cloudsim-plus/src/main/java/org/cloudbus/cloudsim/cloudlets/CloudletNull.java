package org.cloudbus.cloudsim.cloudlets;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.Simulation;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.listeners.CloudletVmEventInfo;
import org.cloudsimplus.listeners.EventListener;

import java.util.Collections;
import java.util.List;

/**
 * A class that implements the Null Object Design Pattern for {@link Cloudlet}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see Cloudlet#NULL
 */
final class CloudletNull implements Cloudlet {
    @Override public void setId(long id) {/**/}
    @Override public long getId() {
        return -1;
    }
    @Override public String getUid() {
        return "";
    }
    @Override public boolean addRequiredFile(String fileName) {
        return false;
    }
    @Override public boolean addRequiredFiles(List<String> fileNames) {
        return false;
    }
    @Override public boolean deleteRequiredFile(String filename) {
        return false;
    }
    @Override public double getAccumulatedBwCost() {
        return 0.0;
    }
    @Override public double getActualCpuTime(Datacenter datacenter) {
        return 0.0;
    }
    @Override public double getActualCpuTime() {
        return 0.0;
    }
    @Override public int getPriority() {
        return 0;
    }
    @Override public long getFileSize() {
        return 0L;
    }
    @Override public long getFinishedLengthSoFar() {
        return 0L;
    }
    @Override public long getFinishedLengthSoFar(Datacenter datacenter) {
        return 0L;
    }
    @Override public long getLength() {
        return 0L;
    }
    @Override public long getOutputSize() {
        return 0L;
    }
    @Override public long getTotalLength() {
        return 0L;
    }
    @Override public double getCostPerBw() {
        return 0.0;
    }
    @Override public double getCostPerSec() {
        return 0.0;
    }
    @Override public double getCostPerSec(Datacenter datacenter) {
        return 0.0;
    }
    @Override public double getExecStartTime() {
        return 0.0;
    }
    @Override public double getFinishTime() {
        return 0.0;
    }
    @Override public int getNetServiceLevel() {
        return 0;
    }
    @Override public long getNumberOfPes() {
        return 0;
    }
    @Override public double getTotalCost() {
        return 0.0;
    }
    @Override public List<String> getRequiredFiles() {
        return Collections.emptyList();
    }
    @Override public Datacenter getLastDatacenter() {
        return Datacenter.NULL;
    }
    @Override public Status getStatus() {
        return Status.FAILED;
    }
    @Override public double getLastDatacenterArrivalTime() {
        return 0.0;
    }
    @Override public double getArrivalTime(Datacenter datacenter) {
        return 0.0;
    }
    @Override public long getJobId() { return 0; }
    @Override public void setJobId(long jobId) {/**/}
    @Override public UtilizationModel getUtilizationModelBw() {
        return UtilizationModel.NULL;
    }
    @Override public UtilizationModel getUtilizationModelCpu() {
        return UtilizationModel.NULL;
    }
    @Override public UtilizationModel getUtilizationModelRam() {
        return UtilizationModel.NULL;
    }
    @Override public double getUtilizationOfBw() {
        return 0;
    }
    @Override public double getUtilizationOfBw(double time) {
        return 0.0;
    }
    @Override public double getUtilizationOfCpu() {
        return 0;
    }
    @Override public double getUtilizationOfCpu(double time) {
        return 0.0;
    }
    @Override public double getUtilizationOfRam() {
        return 0;
    }
    @Override public double getUtilizationOfRam(double time) {
        return 0.0;
    }
    @Override public Vm getVm() {
        return Vm.NULL;
    }
    @Override public double getWaitingTime() {
        return 0.0;
    }
    @Override public double getWallClockTimeInLastExecutedDatacenter() {
        return 0.0;
    }
    @Override public double getWallClockTime(Datacenter datacenter) {
        return 0.0;
    }
    @Override public boolean isFinished() {
        return false;
    }
    @Override public boolean requiresFiles() {
        return false;
    }
    @Override public void setPriority(int priority) {/**/}
    @Override public Cloudlet setLength(long length) {
        return Cloudlet.NULL;
    }
    @Override public Cloudlet setFileSize(long fileSize) {
        return Cloudlet.NULL;
    }
    @Override public Cloudlet setOutputSize(long outputSize) {
        return Cloudlet.NULL;
    }
    @Override public boolean setStatus(Status newStatus) {
        return false;
    }
    @Override public boolean setNetServiceLevel(int netServiceLevel) {
        return false;
    }
    @Override public Cloudlet setNumberOfPes(long numberOfPes) {
        return Cloudlet.NULL;
    }
    @Override public void assignToDatacenter(Datacenter datacenter) {/**/}
    @Override public void setBroker(DatacenterBroker broker) {/**/}
    @Override public DatacenterBroker getBroker() {
        return DatacenterBroker.NULL;
    }
    @Override public Cloudlet setUtilizationModel(UtilizationModel utilizationModel) {
        return Cloudlet.NULL;
    }
    @Override public Cloudlet setUtilizationModelBw(UtilizationModel utilizationModelBw) {
        return Cloudlet.NULL;
    }
    @Override public Cloudlet setUtilizationModelCpu(UtilizationModel utilizationModelCpu) {
        return Cloudlet.NULL;
    }
    @Override public Cloudlet setUtilizationModelRam(UtilizationModel utilizationModelRam) {
        return Cloudlet.NULL;
    }
    @Override public Cloudlet setVm(Vm vm) {
        return Cloudlet.NULL;
    }
    @Override public boolean removeOnFinishListener(EventListener<CloudletVmEventInfo> listener) { return false; }
    @Override public Cloudlet addOnFinishListener(EventListener<CloudletVmEventInfo> listener) { return Cloudlet.NULL; }
    @Override public void notifyOnUpdateProcessingListeners(double time) {/**/}
    @Override public Simulation getSimulation() {
        return Simulation.NULL;
    }
    @Override public boolean removeOnUpdateProcessingListener(EventListener<CloudletVmEventInfo> listener) { return false; }
    @Override public Cloudlet addOnUpdateProcessingListener(EventListener<CloudletVmEventInfo> listener) { return Cloudlet.NULL; }
    @Override public double getSubmissionDelay() {
        return 0;
    }
    @Override public void setSubmissionDelay(double submissionDelay) {/**/}
    @Override public boolean isBindToVm() {
        return false;
    }
    @Override public int compareTo(Cloudlet cloudlet) {
        return 0;
    }
    @Override public boolean isAssignedToDatacenter() { return false; }
    @Override public String toString() {
        return "Cloudlet.NULL";
    }
    @Override public boolean addFinishedLengthSoFar(long partialFinishedMI) {
        return false;
    }
    @Override public boolean setWallClockTime(double wallTime, double actualCpuTime) {
        return false;
    }
    @Override public void setExecStartTime(double clockTime) {/**/}
    @Override public double registerArrivalInDatacenter() {
        return -1;
    }
}
