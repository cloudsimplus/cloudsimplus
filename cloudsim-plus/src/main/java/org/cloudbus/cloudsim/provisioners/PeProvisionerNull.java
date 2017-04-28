package org.cloudbus.cloudsim.provisioners;

import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * A class that implements the Null Object Design Pattern for
 * {@link PeProvisioner} class.
 *
 * @author Manoel Campos da Silva Filho
 * @see PeProvisioner#NULL
 */
final class PeProvisionerNull implements PeProvisioner {
    @Override public void setPe(Pe pe) {/**/}
    @Override public boolean allocateResourceForVm(Vm vm, long newTotalVmResourceCapacity) {
        return false;
    }
    @Override public boolean allocateResourceForVm(Vm vm, double newTotalVmResource) {
        return false;
    }
    @Override public long getAllocatedResourceForVm(Vm vm) {
        return 0;
    }
    @Override public long getTotalAllocatedResource() {
        return 0;
    }
    @Override public double getUtilization() {
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
    @Override public void setResource(ResourceManageable resource) {}
    @Override public long getCapacity() { return 0; }
    @Override public long getAvailableResource() { return 0; }
    @Override public boolean isResourceAllocatedToVm(Vm vm) { return false; }
}
