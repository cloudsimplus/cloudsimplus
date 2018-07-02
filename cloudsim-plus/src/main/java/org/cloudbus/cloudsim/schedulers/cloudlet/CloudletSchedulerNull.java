package org.cloudbus.cloudsim.schedulers.cloudlet;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletExecution;
import org.cloudbus.cloudsim.schedulers.cloudlet.network.CloudletTaskScheduler;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A class that implements the Null Object Design Pattern for {@link CloudletScheduler}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see CloudletScheduler#NULL
 */
final class CloudletSchedulerNull implements CloudletScheduler {
    @Override public Cloudlet cloudletCancel(int cloudletId) {
        return Cloudlet.NULL;
    }
    @Override public void cloudletFinish(CloudletExecution ce) {/**/}
    @Override public boolean cloudletPause(int cloudletId) {
        return false;
    }
    @Override public double cloudletResume(int cloudletId) {
        return 0.0;
    }
    @Override public double cloudletSubmit(Cloudlet cl, double fileTransferTime) {
        return 0.0;
    }
    @Override public double cloudletSubmit(Cloudlet cl) {
        return 0.0;
    }
    @Override public List<CloudletExecution> getCloudletExecList() {
        return Collections.emptyList();
    }
    @Override public int getCloudletStatus(int cloudletId) {
        return 0;
    }
    @Override public List<Double> getCurrentMipsShare() {
        return Collections.emptyList();
    }
    @Override public double getCurrentRequestedBwPercentUtilization() {
        return 0.0;
    }
    @Override public double getCurrentRequestedRamPercentUtilization() {
        return 0.0;
    }
    @Override public double getPreviousTime() {
        return 0.0;
    }
    @Override public double getAllocatedMipsForCloudlet(CloudletExecution ce, double time) {
        return 0.0;
    }
    @Override public double getRequestedMipsForCloudlet(CloudletExecution ce, double time) {
        return 0.0;
    }
    @Override public double getRequestedCpuPercentUtilization(double time) { return 0.0; }
    @Override public boolean hasFinishedCloudlets() {
        return false;
    }
    @Override public CloudletTaskScheduler getTaskScheduler() {
        return CloudletTaskScheduler.NULL;
    }
    @Override public void setTaskScheduler(CloudletTaskScheduler taskScheduler) {/**/}
    @Override public boolean isTherePacketScheduler() {
        return false;
    }
    @Override public Cloudlet getCloudletToMigrate() {
        return Cloudlet.NULL;
    }
    @Override public int runningCloudletsNumber() {
        return 0;
    }
    @Override public double updateProcessing(double currentTime, List<Double> mipsShare) {
        return 0.0;
    }
    @Override public Vm getVm() {
        return Vm.NULL;
    }
    @Override public void setVm(Vm vm) {/**/}
    @Override public long getUsedPes() {
        return 0;
    }
    @Override public long getFreePes() { return 0; }
    @Override public boolean canAddCloudletToExecutionList(CloudletExecution cloudlet) { return false; }
    @Override public Set<Cloudlet> getCloudletReturnedList() { return Collections.EMPTY_SET; }
    @Override public boolean isCloudletReturned(Cloudlet cloudlet) { return false; }
    @Override public void addCloudletToReturnedList(Cloudlet cloudlet) {/**/}
    @Override public List<CloudletExecution> getCloudletFinishedList() { return Collections.emptyList(); }
    @Override public boolean isEmpty() { return false; }
    @Override public List<CloudletExecution> getCloudletWaitingList() { return Collections.EMPTY_LIST; }
    @Override public void deallocatePesFromVm(int pesToRemove) {/**/}
    @Override public List<Cloudlet> getCloudletList() { return Collections.EMPTY_LIST; }
}
