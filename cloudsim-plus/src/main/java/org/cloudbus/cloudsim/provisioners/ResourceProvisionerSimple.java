/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.provisioners;

import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.resources.ResourceManageable;

import java.util.Objects;

/**
 * ResourceProvisionerSimple is a {@link ResourceProvisioner} implementation
 * which uses a best-effort policy to allocate a resource to VMs:
 * if there is available amount of the resource on the host, it allocates;
 * otherwise, it fails.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since 3.0.4
 */
public class ResourceProvisionerSimple extends ResourceProvisionerAbstract {
    /**
     * Creates a new ResourceProvisionerSimple which the {@link ResourceManageable} it will manage
     * have to be set further.
     *
     * @post $none
     * @see #setResource(ResourceManageable)
     */
    public ResourceProvisionerSimple() {
        super(ResourceManageable.NULL);
    }

    /**
     * Creates a new ResourceProvisionerSimple.
     *
     * @param resource the resource to be managed by the provisioner
     * @post $none
     */
    protected ResourceProvisionerSimple(ResourceManageable resource) {
        super(resource);
    }

    @Override
    public boolean allocateResourceForVm(Vm vm, long newTotalVmResourceCapacity) {
        Objects.requireNonNull(vm);
        if (isSuitableForVm(vm, newTotalVmResourceCapacity)) {
            final long prevVmResourceAllocation = vm.getResource(getResourceClass()).getAllocatedResource();
            if (getResourceAllocationMap().containsKey(vm)) {
                //Deallocates any amount of the resource assigned to the Vm in order to allocate a new capacity
                deallocateResourceForVm(vm);
            }

            /*
            Pe resources are not stored in the VM resource List. Only the provisioner that keeps track
            of Pe allocation for VM. By this way, if the resource is not found inside the VM
            and it is a Pe, it's OK (as it is expected)
            */
            if(!getResource().isObjectSubClassOf(Pe.class) && !vm.getResource(getResourceClass()).setCapacity(newTotalVmResourceCapacity)){
                return false;
            }

            //Allocates the requested resource from the physical resource
            getResource().allocateResource(newTotalVmResourceCapacity);
            getResourceAllocationMap().put(vm, newTotalVmResourceCapacity);
            vm.getResource(getResourceClass()).setAllocatedResource(prevVmResourceAllocation);
            return true;
        }

        return false;
    }

    @Override
    public boolean allocateResourceForVm(Vm vm, double newTotalVmResource) {
        return allocateResourceForVm(vm, (long)newTotalVmResource);
    }

    @Override
    public boolean deallocateResourceForVm(Vm vm) {
        final long amountFreed = deallocateResourceForVmSettingAllocationMapEntryToZero(vm);
        getResourceAllocationMap().remove(vm);
        return amountFreed > 0;
    }

    @Override
    protected long deallocateResourceForVmSettingAllocationMapEntryToZero(Vm vm) {
        if (getResourceAllocationMap().containsKey(vm)) {
            final long vmAllocatedResource = getResourceAllocationMap().get(vm);
            getResourceAllocationMap().put(vm, 0L);
            //Deallocates the virtual resource the VM was using
            vm.deallocateResource(getResourceClass());

            //Deallocates the virtual resource from the physical resource
            getResource().deallocateResource(vmAllocatedResource);
            return vmAllocatedResource;
        }

        return 0;
    }

    @Override
    public boolean isSuitableForVm(Vm vm, long newVmTotalAllocatedResource) {
        final long currentAllocatedResource = getAllocatedResourceForVm(vm);
        final long allocationDifference = newVmTotalAllocatedResource - currentAllocatedResource;
        return getResource().getAvailableResource() >=  allocationDifference;
    }

}
