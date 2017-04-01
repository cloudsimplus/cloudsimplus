package org.cloudbus.cloudsim.allocationpolicies;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudsimplus.autoscaling.VerticalVmScaling;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A class that implements the Null Object Design Pattern for the {@link VmAllocationPolicy}
 * class.
 *
 * @author Manoel Campos da Silva Filho
 * @see VmAllocationPolicy#NULL
 */
final class VmAllocationPolicyNull implements VmAllocationPolicy {
    @Override public Datacenter getDatacenter() {
        return Datacenter.NULL;
    }
    @Override public void setDatacenter(Datacenter datacenter) {/**/}
    @Override public boolean scaleVmVertically(VerticalVmScaling scaling) {
        return false;
    }
    @Override public boolean allocateHostForVm(Vm vm) {
        return false;
    }
    @Override public boolean allocateHostForVm(Vm vm, Host host) {
        return false;
    }
    @Override public void deallocateHostForVm(Vm vm) {/**/}
    @Override public List<Host> getHostList() {
        return Collections.emptyList();
    }
    @Override public Map<Vm, Host> optimizeAllocation(List<? extends Vm> vmList) {
        return Collections.emptyMap();
    }
}
