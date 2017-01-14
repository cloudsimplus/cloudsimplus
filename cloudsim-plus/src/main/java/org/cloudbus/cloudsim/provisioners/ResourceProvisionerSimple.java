/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.provisioners;

import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.resources.ResourceManageable;

import java.util.Objects;

/**
 * ResourceProvisionerSimple is an extension of {@link AbstractResourceProvisioner}
 * which uses a best-effort policy to allocate a resource to VMs:
 * if there is available amount of the resource on the host, it allocates;
 * otherwise, it fails.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since 3.0.4
 */
public class ResourceProvisionerSimple extends AbstractResourceProvisioner {
    /**
     * Creates a new ResourceProvisionerSimple.
     *
     * @param resource the resource to be managed by the provisioner
     * @post $none
     */
    public ResourceProvisionerSimple(ResourceManageable resource) {
        super(resource);
    }

    @Override
    public boolean allocateResourceForVm(Vm vm, long newTotalVmResource) {
        Objects.requireNonNull(vm);
        if (isSuitableForVm(vm, newTotalVmResource)) {
            deallocateResourceForVm(vm);
            getResource().allocateResource(newTotalVmResource);
            getResourceAllocationMap().put(vm, newTotalVmResource);
            vm.allocateResource(getResourceClass(), newTotalVmResource);
            return true;
        }

        return false;
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
            getResource().deallocateResource(vmAllocatedResource);
            vm.deallocateResource(getResourceClass());
            return vmAllocatedResource;
        }

        return 0;
    }

    @Override
    public boolean isSuitableForVm(Vm vm, long newVmTotalAllocatedResource) {
        final long currentAllocatedResource = getAllocatedResourceForVm(vm);
        if(newVmTotalAllocatedResource  <= currentAllocatedResource) {
            return true;
        }

        final long allocationDifference = newVmTotalAllocatedResource - currentAllocatedResource;
        return getResource().getAvailableResource() >=  allocationDifference;
    }

}
