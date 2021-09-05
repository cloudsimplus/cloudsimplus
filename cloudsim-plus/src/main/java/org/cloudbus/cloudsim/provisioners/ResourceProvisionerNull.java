package org.cloudbus.cloudsim.provisioners;

import org.cloudbus.cloudsim.resources.Resource;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.function.Function;

/**
 * A class that implements the Null Object Design Pattern for
 * {@link ResourceProvisioner} class.
 *
 * @author Manoel Campos da Silva Filho
 * @see ResourceProvisioner#NULL
 */
class ResourceProvisionerNull implements ResourceProvisioner {
    @Override public boolean allocateResourceForVm(Vm vm, long newTotalVmResourceCapacity) {
        return false;
    }
    @Override public long getAllocatedResourceForVm(Vm vm) {
        return 0;
    }
    @Override public long getTotalAllocatedResource() {
        return 0;
    }
    @Override public long deallocateResourceForVm(Vm vm) {
        return 0;
    }
    @Override public boolean isSuitableForVm(Vm vm, long newVmTotalAllocatedResource) {
        return false;
    }
    @Override public boolean isSuitableForVm(Vm vm, Resource resource) { return false; }
    @Override public ResourceManageable getPmResource() {
        return ResourceManageable.NULL;
    }
    @Override public void setResources(ResourceManageable pmResource, Function<Vm, ResourceManageable> vmResourceFunction) {/**/}
    @Override public long getCapacity() { return 0; }
    @Override public long getAvailableResource() { return 0; }
}
