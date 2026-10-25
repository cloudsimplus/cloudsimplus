/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.allocators;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.Resource;
import org.cloudsimplus.resources.ResourceManageable;
import org.cloudsimplus.vms.Vm;

import java.util.Objects;
import java.util.function.Function;

/// A best-effort [ResourceAllocator] policy used by a [Host] to provide a resource to VMs:
///
/// - if there is available amount of the resource on the Host;
/// - otherwise, it fails.
///
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since 3.0.4
public class ResourceAllocatorSimple extends ResourceAllocatorAbstract {
    /// Creates a new ResourceAllocatorSimple which the [ResourceManageable]
    /// it will manage have to be set further.
    ///
    /// @see ResourceAllocator#setResources(ResourceManageable, Function)
    public ResourceAllocatorSimple() {
        super(ResourceManageable.NULL, vm -> ResourceManageable.NULL);
    }

    /// Creates a ResourceAllocatorSimple for a given [ResourceManageable].
    ///
    /// @param resource the resource to be managed by the allocator
    /// @param vmResourceFunction a [Function] that receives a [Vm] and returns
    /// the virtual resource corresponding to the [PM resource][#getPmResource()]
    protected ResourceAllocatorSimple(final ResourceManageable resource, final Function<Vm, ResourceManageable> vmResourceFunction) {
        super(resource, vmResourceFunction);
    }

    @Override
    public boolean allocateResourceForVm(final Vm vm, final long newTotalVmResourceCapacity) {
        Objects.requireNonNull(vm);

        if (!isSuitableForVm(vm, newTotalVmResourceCapacity)) {
            return false;
        }

        // Releases any amount previously allocated to the VM, then allocates the new amount
        getPmResource().free(removeAllocatedResourceForVm(vm));
        getPmResource().occupy(newTotalVmResourceCapacity);
        setAllocatedResourceForVm(vm, newTotalVmResourceCapacity);
        return true;
    }

    @Override
    public boolean allocateResourceForVm(final Vm vm, final double newTotalVmResource) {
        return allocateResourceForVm(vm, (long)newTotalVmResource);
    }

    @Override
    public long deallocateResourceForVm(final Vm vm) {
        // The VM is leaving the PM, so it doesn't use any amount of the resource there anymore
        getVmResourceFunction().apply(vm).freeAll();

        // De-allocates the amount allocated to the VM to make it free on the physical machine
        final long vmAllocatedResource = removeAllocatedResourceForVm(vm);
        getPmResource().free(vmAllocatedResource);
        return vmAllocatedResource;
    }

    @Override
    public boolean isSuitableForVm(final Vm vm, final long newVmTotalAllocatedResource) {
        final long currentAllocatedResource = getAllocatedResourceForVm(vm);
        final long allocationDifference = newVmTotalAllocatedResource - currentAllocatedResource;
        return getPmResource().getFree() >=  allocationDifference;
    }

    @Override
    public boolean isSuitableForVm(final Vm vm, final Resource resource) {
        return isSuitableForVm(vm, resource.getCapacity());
    }
}
