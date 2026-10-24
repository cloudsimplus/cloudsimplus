/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.provisioners;

import org.cloudsimplus.hosts.Host;
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
    /// Creates a new ResourceProvisionerSimple which the [ResourceManageable]
    /// it will manage have to be set further.
    ///
    /// @see ResourceProvisioner#setResources(ResourceManageable, Function)
    public ResourceProvisionerSimple() {
        super(ResourceManageable.NULL, vm -> ResourceManageable.NULL);
    }

    /// Creates a ResourceProvisionerSimple for a given [ResourceManageable].
    ///
    /// @param resource the resource to be managed by the provisioner
    /// @param vmResourceFunction a [Function] that receives a [Vm] and returns
    /// the virtual resource corresponding to the [PM resource][#getPmResource()]
    protected ResourceProvisionerSimple(final ResourceManageable resource, final Function<Vm, ResourceManageable> vmResourceFunction) {
        super(resource, vmResourceFunction);
    }

    @Override
    public boolean allocateResourceForVm(final Vm vm, final long newTotalVmResourceCapacity) {
        Objects.requireNonNull(vm);

        if (!isSuitableForVm(vm, newTotalVmResourceCapacity)) {
            return false;
        }

        if (!setVmResourceCapacity(vm, newTotalVmResourceCapacity)) {
            return false;
        }

        // Releases any amount previously provisioned to the VM, then provisions the new amount
        getPmResource().deallocateResource(removeAllocatedResourceForVm(vm));
        getPmResource().allocateResource(newTotalVmResourceCapacity);
        setAllocatedResourceForVm(vm, newTotalVmResourceCapacity);
        return true;
    }

    /// Sets the capacity of the VM resource, keeping its current utilization,
    /// which is the amount used by the VM's Cloudlets (not the amount provisioned to the VM).
    /// If such utilization is higher than the new capacity (e.g., when the VM is downscaled),
    /// it is cleared, since the Cloudlet scheduler recomputes it at the next update.
    ///
    /// @param vm the VM to set the resource capacity
    /// @param newCapacity the new capacity for the VM resource
    /// @return true if the capacity was changed, false otherwise
    private boolean setVmResourceCapacity(final Vm vm, final long newCapacity) {
        final ResourceManageable vmResource = getVmResourceFunction().apply(vm);
        if (vmResource.getAllocatedResource() > newCapacity) {
            vmResource.deallocateAllResources();
        }

        return vmResource.setCapacity(newCapacity);
    }

    @Override
    public boolean allocateResourceForVm(final Vm vm, final double newTotalVmResource) {
        return allocateResourceForVm(vm, (long)newTotalVmResource);
    }

    @Override
    public long deallocateResourceForVm(final Vm vm) {
        // The VM is leaving the PM, so it doesn't use any amount of the resource there anymore
        getVmResourceFunction().apply(vm).deallocateAllResources();

        // De-allocates the amount provisioned to the VM to make it free on the physical machine
        final long vmAllocatedResource = removeAllocatedResourceForVm(vm);
        getPmResource().deallocateResource(vmAllocatedResource);
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
