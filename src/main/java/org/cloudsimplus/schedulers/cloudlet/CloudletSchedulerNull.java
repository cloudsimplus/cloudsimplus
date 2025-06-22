package org.cloudsimplus.schedulers.cloudlet;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletExecution;
import org.cloudsimplus.listeners.CloudletResourceAllocationFailEventInfo;
import org.cloudsimplus.listeners.EventListener;
import org.cloudsimplus.schedulers.MipsShare;
import org.cloudsimplus.schedulers.cloudlet.network.CloudletTaskScheduler;
import org.cloudsimplus.vms.Vm;

import java.io.Serial;
import java.util.Collections;
import java.util.List;

/**
 * A class that implements the Null Object Design Pattern for {@link CloudletScheduler} class.
 *
 * @author Manoel Campos da Silva Filho
 * @see CloudletScheduler#NULL
 */
final class CloudletSchedulerNull implements CloudletScheduler {
    @Serial   private static final long serialVersionUID = -3167964772291527087L;
    @Override public Cloudlet cloudletFail(Cloudlet cloudlet) { return Cloudlet.NULL; }
    @Override public Cloudlet cloudletCancel(Cloudlet cloudlet) {
        return Cloudlet.NULL;
    }
    @Override public boolean cloudletReady(Cloudlet cloudlet) { return false; }
    @Override public boolean cloudletPause(Cloudlet cloudlet) {
        return false;
    }
    @Override public double cloudletResume(Cloudlet cloudlet) {
        return 0.0;
    }
    @Override public double cloudletSubmit(Cloudlet cloudlet, double fileTransferTime) {
        return 0.0;
    }
    @Override public double cloudletSubmit(Cloudlet cloudlet) {
        return 0.0;
    }
    @Override public void setPreviousTime(double previousTime) {/**/}
    @Override public List<CloudletExecution> getCloudletExecList() {
        return Collections.emptyList();
    }
    @Override public <T extends Cloudlet> List<T> getCloudletSubmittedList() { return Collections.emptyList(); }
    @Override public boolean isCloudletSubmittedListEnabled() { return false; }
    @Override public CloudletScheduler enableCloudletSubmittedList() { return this; }
    @Override public double getCurrentRequestedBwPercentUtilization() { return 0.0; }
    @Override public double getCurrentRequestedRamPercentUtilization() { return 0.0; }
    @Override public double getPreviousTime() {
        return 0.0;
    }
    @Override public double getRequestedCpuPercent(double time) { return 0.0; }
    @Override public double getAllocatedCpuPercent(double time) { return 0; }
    @Override public boolean hasFinishedCloudlets() {
        return false;
    }
    @Override public CloudletTaskScheduler getTaskScheduler() {
        return CloudletTaskScheduler.NULL;
    }
    @Override public void setTaskScheduler(CloudletTaskScheduler taskScheduler) {/**/}
    @Override public boolean isThereTaskScheduler() {
        return false;
    }
    @Override public double updateProcessing(double currentTime, MipsShare mipsShare) {
        return 0.0;
    }
    @Override public Vm getVm() { return Vm.NULL; }
    @Override public void setVm(Vm vm) {/**/}
    @Override public long getUsedPes() {
        return 0;
    }
    @Override public long getFreePes() { return 0; }
    @Override public void addCloudletToReturnedList(Cloudlet cloudlet) {/**/}
    @Override public List<CloudletExecution> getCloudletFinishedList() { return Collections.emptyList(); }
    @Override public boolean isEmpty() { return false; }
    @Override public List<CloudletExecution> getCloudletWaitingList() { return Collections.emptyList(); }
    @Override public void deallocatePesFromVm(long pesToRemove) {/**/}
    @Override public List<Cloudlet> getCloudletList() { return Collections.emptyList(); }
    @Override public void clear() {/**/}
    @Override public CloudletScheduler addOnCloudletResourceAllocationFail(EventListener<CloudletResourceAllocationFailEventInfo> listener) { return this; }
    @Override public boolean removeOnCloudletResourceAllocationFail(EventListener<CloudletResourceAllocationFailEventInfo> listener) { return false; }
}
