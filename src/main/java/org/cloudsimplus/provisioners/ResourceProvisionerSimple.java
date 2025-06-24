/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.provisioners;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.Resource;
import org.cloudsimplus.resources.ResourceManageable;
import org.cloudsimplus.vms.Vm;

import java.util.Objects;
import java.util.function.Function;

/// A best-effort [ResourceProvisioner] policy used by a [Host] to provide a resource to VMs:
///
/// - if there is available amount of the resource on the Host;
/// - otherwise, it fails.
///
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since 3.0.4
public class ResourceProvisionerSimple extends ResourceProvisionerAbstract {
    /**
     * Creates a new ResourceProvisionerSimple which the {@link ResourceManageable}
     * it will manage have to be set further.
     *
     * @see ResourceProvisioner#setResources(ResourceManageable, Function)
     */
    public ResourceProvisionerSimple() {
        super(ResourceManageable.NULL, vm -> ResourceManageable.NULL);
    }

    /**
     * Creates a ResourceProvisionerSimple for a given {@link ResourceManageable}.
     *
     * @param resource the resource to be managed by the provisioner
     * @param vmResourceFunction a {@link Function} that receives a {@link Vm} and returns
     *                           the virtual resource corresponding to the {@link #getPmResource() PM resource}
     */
    protected ResourceProvisionerSimple(final ResourceManageable resource, final Function<Vm, ResourceManageable> vmResourceFunction) {
        super(resource, vmResourceFunction);
    }

    @Override
    public boolean allocateResourceForVm(final Vm vm, final long newTotalVmResourceCapacity) {
        Objects.requireNonNull(vm);

        if (!isSuitableForVm(vm, newTotalVmResourceCapacity)) {
            return false;
        }

        /* Stores the resource allocation before changing the current allocation,
         * this line must be placed here and not at the end where it's in fact used.*/
        final ResourceManageable vmResource = getVmResourceFunction().apply(vm);
        final long prevVmResourceAllocation = vmResource.getAllocatedResource();
        if (prevVmResourceAllocation > 0) {
            //De-allocates any amount of the resource assigned to the Vm to allocate a new capacity
            deallocateResourceForVm(vm);
        }

        /*
        Pe resources are not stored in the VM resource List.
        Only the provisioner keeps track of Pe allocation for VM.
        This way, if the resource is not found inside the VM
        and it is a Pe, it's OK (as it is expected)
        */
        if(!getPmResource().isSubClassOf(Pe.class) && !vmResource.setCapacity(newTotalVmResourceCapacity)){
            return false;
        }

        // Allocates the requested resource from the physical resource
        getPmResource().allocateResource(newTotalVmResourceCapacity);
        vmResource.setCapacity(newTotalVmResourceCapacity);
        vmResource.setAllocatedResource(newTotalVmResourceCapacity);
        return true;
    }

    @Override
    public boolean allocateResourceForVm(final Vm vm, final double newTotalVmResource) {
        return allocateResourceForVm(vm, (long)newTotalVmResource);
    }

    @Override
    public long deallocateResourceForVm(final Vm vm) {
        final ResourceManageable vmResource = getVmResourceFunction().apply(vm);
        final long vmAllocatedResource = vmResource.getAllocatedResource();

        // De-allocates the virtual resource from the VM
        vmResource.deallocateAllResources();

        // De-allocates the virtual resource to make it free on the physical machine
        getPmResource().deallocateResource(vmResource.getCapacity());
        return vmAllocatedResource;
    }

    @Override
    public boolean isSuitableForVm(final Vm vm, final long newVmTotalAllocatedResource) {
        final long currentAllocatedResource = getAllocatedResourceForVm(vm);
        final long allocationDifference = newVmTotalAllocatedResource - currentAllocatedResource;
        return getPmResource().getAvailableResource() >=  allocationDifference;
    }

    @Override
    public boolean isSuitableForVm(final Vm vm, final Resource resource) {
        return isSuitableForVm(vm, resource.getCapacity());
    }
}
