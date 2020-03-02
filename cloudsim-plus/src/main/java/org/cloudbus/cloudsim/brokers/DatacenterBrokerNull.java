package org.cloudbus.cloudsim.brokers;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEntityNullBase;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.listeners.DatacenterBrokerEventInfo;
import org.cloudsimplus.listeners.EventInfo;
import org.cloudsimplus.listeners.EventListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A class that implements the Null Object Design Pattern for {@link DatacenterBroker}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see DatacenterBroker#NULL
 */
final class DatacenterBrokerNull implements DatacenterBroker, SimEntityNullBase {
    @Override public int compareTo(SimEntity entity) { return 0; }
    @Override public boolean bindCloudletToVm(Cloudlet cloudlet, Vm vm) {
        return false;
    }
    @Override public <T extends Cloudlet> List<T> getCloudletWaitingList() {
        return Collections.emptyList();
    }
    @Override public <T extends Cloudlet> List<T> getCloudletFinishedList() {
        return Collections.emptyList();
    }
    @Override public Vm getWaitingVm(int index) {
        return Vm.NULL;
    }
    @Override public <T extends Vm> List<T> getVmWaitingList() {
        return Collections.emptyList();
    }
    @Override public <T extends Vm> List<T> getVmExecList() {
        return Collections.emptyList();
    }
    @Override public DatacenterBroker requestIdleVmDestruction(Vm vm) { return this; }
    @Override public List<Cloudlet> destroyVm(Vm vm) { return Collections.emptyList(); }
    @Override public <T extends Vm> List<T> getVmCreatedList() { return Collections.emptyList(); }
    @Override public DatacenterBroker setDatacenterMapper(BiFunction<Datacenter, Vm, Datacenter> datacenterMapper) { return this; }
    @Override public DatacenterBroker setVmMapper(Function<Cloudlet, Vm> vmMapper) { return this; }
    @Override public DatacenterBroker setSelectClosestDatacenter(boolean select) { return this; }
    @Override public boolean isSelectClosestDatacenter() { return false; }
    @Override public List<Cloudlet> getCloudletCreatedList() { return Collections.emptyList(); }
    @Override public DatacenterBroker addOnVmsCreatedListener(EventListener<DatacenterBrokerEventInfo> listener) { return this; }
    @Override public DatacenterBroker removeOnVmsCreatedListener(EventListener<? extends EventInfo> listener) { return this; }
    @Override public Function<Vm, Double> getVmDestructionDelayFunction() { return vm -> 0.0; }
    @Override public DatacenterBroker setVmDestructionDelayFunction(Function<Vm, Double> function) { return this; }
    @Override public DatacenterBroker setVmDestructionDelay(double delay) { return this; }
    @Override public List<Cloudlet> getCloudletSubmittedList() { return Collections.emptyList(); }
    @Override public <T extends Vm> List<T> getVmFailedList() { return Collections.emptyList(); }
    @Override public boolean isRetryFailedVms() { return false; }
    @Override public void setRetryFailedVms(boolean retryFailedVms) {/**/}
    @Override public DatacenterBroker setVmComparator(Comparator<Vm> comparator) { return this; }
    @Override public void setCloudletComparator(Comparator<Cloudlet> comparator) {/**/}
    @Override public DatacenterBroker submitCloudlet(Cloudlet cloudlet) { return this; }
    @Override public DatacenterBroker submitCloudletList(List<? extends Cloudlet> list) { return this; }
    @Override public DatacenterBroker submitCloudletList(List<? extends Cloudlet> list, double submissionDelay) { return this; }
    @Override public DatacenterBroker submitCloudletList(List<? extends Cloudlet> list, Vm vm) { return this; }
    @Override public DatacenterBroker submitCloudletList(List<? extends Cloudlet> list, Vm vm, double submissionDelay) { return this; }
    @Override public DatacenterBroker submitVm(Vm vm) { return this; }
    @Override public DatacenterBroker submitVmList(List<? extends Vm> list) { return this; }
    @Override public DatacenterBroker submitVmList(List<? extends Vm> list, double submissionDelay) { return this; }
}
