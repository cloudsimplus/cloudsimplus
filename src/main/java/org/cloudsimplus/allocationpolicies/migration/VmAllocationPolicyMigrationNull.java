package org.cloudsimplus.allocationpolicies.migration;

import org.cloudsimplus.allocationpolicies.VmAllocationPolicy;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSuitability;
import org.cloudsimplus.selectionpolicies.VmSelectionPolicy;
import org.cloudsimplus.vms.Vm;

import java.util.*;
import java.util.function.BiFunction;

/**
 * A class that implements the Null Object Design Pattern for {@link VmAllocationPolicyMigration}.
 *
 * @author Manoel Campos da Silva Filho
 * @see VmAllocationPolicyMigration#NULL
 */
final class VmAllocationPolicyMigrationNull implements VmAllocationPolicyMigration {
    @Override public Datacenter getDatacenter() { return Datacenter.NULL; }
    @Override public VmAllocationPolicy setDatacenter(Datacenter datacenter) { return this; }
    @Override public HostSuitability allocateHostForVm(Vm vm) { return HostSuitability.NULL; }
    @Override public Set<HostSuitability> allocateHostForVm(List<Vm> vmList) { return Collections.emptySet(); }
    @Override public HostSuitability allocateHostForVm(Vm vm, Host host) {
        return HostSuitability.NULL;
    }
    @Override public boolean scaleVmVertically(VerticalVmScaling scaling) {
        return false;
    }
    @Override public void deallocateHostForVm(Vm vm) {/**/}
    @Override public Optional<Host> findHostForVm(Vm vm) { return Optional.empty(); }
    @Override public boolean isVmMigrationSupported() { return false; }
    @Override public int getHostCountForParallelSearch() { return 0; }
    @Override public VmAllocationPolicy setHostCountForParallelSearch(int hostCountForParallelSearch) { return this; }
    @Override public <T extends Host> List<T> getHostList() {
        return Collections.emptyList();
    }
    @Override public Map<Vm, Host> getOptimizedAllocationMap(List<? extends Vm> vmList) { return Collections.emptyMap(); }
    @Override public VmAllocationPolicy setFindHostForVmFunction(BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction) { return this; }
    @Override public boolean isUnderloaded() { return false; }
    @Override public boolean isUnderloaded(Host host) { return false; }
    @Override public boolean isOverloaded() { return false; }
    @Override public boolean isOverloaded(Host host) { return false; }
    @Override public double getOverUtilizationThreshold(Host host) { return 0; }
    @Override public VmAllocationPolicy setVmSelectionPolicy(VmSelectionPolicy vmSelectionPolicy) { return this; }
    @Override public VmSelectionPolicy getVmSelectionPolicy() {
        return VmSelectionPolicy.NULL;
    }
    @Override public double getUnderUtilizationThreshold() {
        return 0;
    }
    @Override public void setUnderUtilizationThreshold(double underUtilizationThreshold) {/**/}
}
