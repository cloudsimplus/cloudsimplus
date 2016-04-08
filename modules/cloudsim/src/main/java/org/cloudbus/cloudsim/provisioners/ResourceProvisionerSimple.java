/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.provisioners;

import org.cloudbus.cloudsim.util.Calculator;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.resources.ResourceManageable;

/**
 * ResourceProvisionerSimple is an extension of {@link AbstractResourceProvisioner} 
 * which uses a best-effort policy to allocate a resource to VMs: 
 * if there is available amount of the resource on the host, it allocates; 
 * otherwise, it fails. 
 * 
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @param <T> The type of the resource capacity of the provisioner
 * @since 3.0.4
 * @see AbstractResourceProvisioner
 */
public class ResourceProvisionerSimple<T extends Number & Comparable<T>> extends AbstractResourceProvisioner<T> {
    /** A calculator for basic math operations over values extending of the Number class. */
    private final Calculator<T> calc;

    /**
     * Creates a new ResourceManageable Provisioner.
     * 
     * @param resource The resource to be managed by the provisioner
     * @post $none
     */
    public ResourceProvisionerSimple(ResourceManageable<T> resource) {
        super(resource);
        calc = new Calculator<>(resource.getCapacity());
    }

    @Override
    public boolean allocateResourceForVm(Vm vm, T newTotalVmResource) {
        if (isSuitableForVm(vm, newTotalVmResource)) {
            deallocateResourceForVm(vm);
            getResource().allocateResource(newTotalVmResource);
            getResourceAllocationMap().put(vm, newTotalVmResource);
            vm.getResource(getResourceClass()).allocateResource(newTotalVmResource);
            return true;
        }

        return false;
    }

    @Override
    public boolean deallocateResourceForVm(Vm vm) {
        final T amountFreed = deallocateResourceForVmSettingAllocationMapEntryToZero(vm);
        getResourceAllocationMap().remove(vm);
        return amountFreed.compareTo(calc.getZero()) > 0;
    }
    
    @Override
    protected T deallocateResourceForVmSettingAllocationMapEntryToZero(Vm vm) {
        if (getResourceAllocationMap().containsKey(vm)) {
            final T vmAllocatedResource = getResourceAllocationMap().get(vm);
            getResourceAllocationMap().put(vm, calc.getZero());
            getResource().deallocateResource(vmAllocatedResource);
            vm.getResource(getResourceClass()).deallocateAllResources();
            return vmAllocatedResource;
        }
        
        return calc.getZero();
    }
    

    @Override
    public boolean isSuitableForVm(Vm vm, T newVmTotalAllocatedResource) {
        final T currentAllocatedResource = getAllocatedResourceForVm(vm);
        if(newVmTotalAllocatedResource.compareTo(currentAllocatedResource) <= 0)
            return true;
        
        final T allocationDifference = 
                calc.subtract(newVmTotalAllocatedResource, currentAllocatedResource);
        
        return getResource().getAvailableResource().compareTo(allocationDifference) >= 0;
    }    

}
