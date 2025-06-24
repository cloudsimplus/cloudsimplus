package org.cloudsimplus.schedulers.vm;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.schedulers.MipsShare;
import org.cloudsimplus.vms.Vm;

/**
 * A class that implements the Null Object Design Pattern for {@link VmScheduler} class.
 *
 * @author Manoel Campos da Silva Filho
 * @see VmScheduler#NULL
 */
final class VmSchedulerNull implements VmScheduler {
    @Override public boolean allocatePesForVm(Vm vm, MipsShare requestedMips) {
        return false;
    }
    @Override public boolean allocatePesForVm(Vm vm) { return false; }
    @Override public MipsShare getAllocatedMips(Vm vm) {
        return MipsShare.NULL;
    }
    @Override public double getTotalAvailableMips() {
        return 0.0;
    }
    @Override public MipsShare getRequestedMips(Vm vm) { return MipsShare.NULL; }
    @Override public double getTotalAllocatedMipsForVm(Vm vm) {
        return 0.0;
    }
    @Override public double getMaxCpuUsagePercentDuringOutMigration() { return 0; }
    @Override public boolean isSuitableForVm(Vm vm) {
        return false;
    }
    @Override public boolean isSuitableForVm(Vm vm, MipsShare requestedMips) { return false; }
    @Override public double getVmMigrationCpuOverhead() { return 0.0; }
    @Override public Host getHost() {
        return Host.NULL;
    }
    @Override public VmScheduler setHost(Host host) {
        return this;
    }
    @Override public void deallocatePesFromVm(Vm vm) {/**/}
    @Override public void deallocatePesFromVm(Vm vm, int pesToRemove) {/**/}
}
