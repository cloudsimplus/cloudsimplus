package org.cloudbus.cloudsim.schedulers.vm;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Collections;
import java.util.List;

/**
 * A class that implements the Null Object Design Pattern for {@link VmScheduler}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see VmScheduler#NULL
 */
final class VmSchedulerNull implements VmScheduler {
    @Override public boolean allocatePesForVm(Vm vm, List<Double> requestedMips) {
        return false;
    }
    @Override public boolean allocatePesForVm(Vm vm) { return false; }
    @Override public List<Double> getAllocatedMips(Vm vm) {
        return Collections.emptyList();
    }
    @Override public double getAvailableMips() {
        return 0.0;
    }
    @Override public List<Double> getRequestedMips(Vm vm) { return Collections.EMPTY_LIST; }
    @Override public double getMaxAvailableMips() {
        return 0.0;
    }
    @Override public long getPeCapacity() {
        return 0;
    }
    @Override public <T extends Pe> List<T> getWorkingPeList() {
        return Collections.emptyList();
    }
    @Override public double getTotalAllocatedMipsForVm(Vm vm) {
        return 0.0;
    }
    @Override public double getMaxCpuUsagePercentDuringOutMigration() { return 0; }
    @Override public boolean isSuitableForVm(Vm vm) {
        return false;
    }
    @Override public boolean isSuitableForVm(Vm vm, boolean showLog) { return false; }
    @Override public boolean isSuitableForVm(Vm vm, List<Double> requestedMips) { return false; }
    @Override public boolean isSuitableForVm(Vm vm, List<Double> requestedMips, boolean showLog) { return false; }
    @Override public double getVmMigrationCpuOverhead() {
        return 0.0;
    }
    @Override public Host getHost() {
        return Host.NULL;
    }
    @Override public VmScheduler setHost(Host host) {
        return this;
    }
    @Override public void deallocatePesForAllVms() {/**/}
    @Override public void deallocatePesFromVm(Vm vm) {/**/}
    @Override public void deallocatePesFromVm(Vm vm, int pesToRemove) {/**/}
}
