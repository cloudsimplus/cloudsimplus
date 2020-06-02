package org.cloudbus.cloudsim.allocationpolicies.migration;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.selectionpolicies.VmSelectionPolicy;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.autoscaling.VerticalVmScaling;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * A class that implements the Null Object Design Pattern for {@link VmAllocationPolicyMigration}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see VmAllocationPolicyMigration#NULL
 */
final class VmAllocationPolicyMigrationNull implements VmAllocationPolicyMigration {
    @Override public Datacenter getDatacenter() { return Datacenter.NULL; }
    @Override public void setDatacenter(Datacenter datacenter) {/**/}
    @Override public boolean allocateHostForVm(Vm vm) {
        return false;
    }
    @Override public boolean allocateHostForVm(Vm vm, Host host) {
        return false;
    }
    @Override public boolean scaleVmVertically(VerticalVmScaling scaling) {
        return false;
    }
    @Override public void deallocateHostForVm(Vm vm) {/**/}
    @Override public Optional<Host> findHostForVm(Vm vm) { return Optional.empty(); }
    @Override public boolean isVmMigrationSupported() { return false; }
    @Override public int getHostCountForParallelSearch() { return 0; }
    @Override public void setHostCountForParallelSearch(int hostCountForParallelSearch) {/**/}
    @Override public <T extends Host> List<T> getHostList() {
        return Collections.emptyList();
    }
    @Override public Map<Vm, Host> getOptimizedAllocationMap(List<? extends Vm> vmList) { return Collections.emptyMap(); }
    @Override public void setFindHostForVmFunction(BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction) {/**/}
    @Override public boolean isHostOverloaded(Host host) { return false; }
    @Override public boolean isHostUnderloaded(Host host) { return false; }
    @Override public double getOverUtilizationThreshold(Host host) { return 0; }
    @Override public void setVmSelectionPolicy(VmSelectionPolicy vmSelectionPolicy) { }
    @Override public VmSelectionPolicy getVmSelectionPolicy() {
        return VmSelectionPolicy.NULL;
    }
    @Override public double getUnderUtilizationThreshold() {
        return 0;
    }
    @Override public void setUnderUtilizationThreshold(double underUtilizationThreshold) {/**/}
    @Override public boolean areHostsUnderloaded() { return false; }
    @Override public boolean areHostsOverloaded() { return false; }
}
