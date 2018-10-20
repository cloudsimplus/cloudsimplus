package org.cloudbus.cloudsim.provisioners;

import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.vms.Vm;

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
    @Override public boolean deallocateResourceForVm(Vm vm) {
        return false;
    }
    @Override public void deallocateResourceForAllVms() {/**/}
    @Override public boolean isSuitableForVm(Vm vm, long newVmTotalAllocatedResource) {
        return false;
    }
    @Override public ResourceManageable getResource() {
        return ResourceManageable.NULL;
    }
    @Override public void setResource(ResourceManageable resource) {/**/}
    @Override public long getCapacity() { return 0; }
    @Override public long getAvailableResource() { return 0; }
    @Override public boolean isResourceAllocatedToVm(Vm vm) { return false; }
}
