/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.allocators;

import lombok.Getter;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.ResourceManageable;
import org.cloudsimplus.vms.Vm;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/// An abstract class that implements the basic features of a provisioning policy used by a [Host]
/// to provide a given resource to its Virtual Machines ([Vm]s).
///
/// @see ResourceAllocator
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since 3.0.4
public abstract non-sealed class ResourceAllocatorAbstract implements ResourceAllocator {
    /// The physical resource managed by this allocator.
    /// It's returned as a [ResourceManageable] here (instead of a read-only [org.cloudsimplus.resources.Resource]
    /// as in [ResourceAllocator#getPmResource()]), so that allocators can change its allocated amount.
    @Getter
    private ResourceManageable pmResource;

    /// @see #getVmResourceFunction()
    private Function<Vm, ResourceManageable> vmResourceFunction;

    /// A map where each key is a VM and each value is the amount of the
    /// [PM resource][#getPmResource()] provisioned to that VM.
    /// It's kept apart from the VM resource, since that one stores the amount
    /// actually used by the VM's Cloudlets, not the amount provisioned to the VM.
    private final Map<Vm, Long> vmAllocationMap = new HashMap<>();

    /// Creates a ResourceManageable Allocator for which the [resource][#getPmResource()]
    /// must be set further.
    /// @see #setResources(ResourceManageable, Function)
    protected ResourceAllocatorAbstract() {
        this(ResourceManageable.NULL, vm -> ResourceManageable.NULL);
    }

    /// Creates a ResourceManageable Allocator.
    ///
    /// @param pmResource the physical resource to be managed by the allocator
    /// @param vmResourceFunction a [Function] that receives a [Vm] and returns
    /// the virtual resource corresponding to the [PM resource][#getPmResource()]
    public ResourceAllocatorAbstract(final ResourceManageable pmResource, final Function<Vm, ResourceManageable> vmResourceFunction) {
        // Assigns the fields directly, since setResources() can be overridden
        this.pmResource = Objects.requireNonNull(pmResource);
        this.vmResourceFunction = Objects.requireNonNull(vmResourceFunction);
    }

    @Override
    public long getAllocatedResourceForVm(final Vm vm) {
        return vmAllocationMap.getOrDefault(vm, 0L);
    }

    /// Stores the amount of the [PM resource][#getPmResource()] provisioned to a given VM.
    /// @param vm the VM to set the provisioned amount
    /// @param amount the total amount provisioned to the VM
    protected void setAllocatedResourceForVm(final Vm vm, final long amount) {
        vmAllocationMap.put(vm, amount);
    }

    /// Removes the amount of the [PM resource][#getPmResource()] provisioned to a given VM.
    /// @param vm the VM to remove the provisioned amount
    /// @return the amount that was provisioned to the VM (0 if none)
    protected long removeAllocatedResourceForVm(final Vm vm) {
        final Long amount = vmAllocationMap.remove(vm);
        return amount == null ? 0 : amount;
    }

    /// Allocates (reserves) an amount of the physical resource for a VM.
    /// It doesn't change the VM capacity: that's done when the Host provisions the allocated amount to the VM.
    ///
    /// @param vm the virtual machine for which the resource is being allocated
    /// @param newTotalVmResourceCapacity the new total amount of resource to allocate to the VM,
    /// changing the allocated resource to this new amount. It doesn't increase
    /// the current allocated VM resource by the given amount, instead,
    /// it changes the VM allocated resource to that amount.
    ///
    /// @return `true` if the resource could be allocated; `false` otherwise
    public abstract boolean allocateResourceForVm(Vm vm, long newTotalVmResourceCapacity);

    /// Allocates (reserves) an amount of the physical resource for a VM.
    /// It doesn't change the VM capacity: that's done when the Host provisions the allocated amount to the VM.
    ///
    /// This method is just a shorthand to avoid explicitly converting
    /// a double to long.
    ///
    /// @param vm the virtual machine for which the resource is being allocated
    /// @param newTotalVmResource the new total amount of resource to allocate to the VM,
    /// changing the allocated resource to this new amount. It doesn't increase
    /// the current allocated VM resource by the given amount, instead,
    /// it changes the VM allocated resource to that amount
    /// @return `true` if the resource could be allocated; `false` otherwise
    /// @see #allocateResourceForVm(Vm, long)
    public boolean allocateResourceForVm(final Vm vm, final double newTotalVmResource){
        return allocateResourceForVm(vm, (long)newTotalVmResource);
    }

    /// Deallocate the resource for the given VM.
    /// @param vm the VM to deallocate the resource
    /// @return the amount of allocated VM resource or zero if VM is not found
    public abstract long deallocateResourceForVm(Vm vm);

    /// Sets the physical resource to be managed by the allocator, such as [Ram], core, [Bandwidth], etc.
    /// @param pmResource the resource managed by this allocator
    /// @param vmResourceFunction a [Function] that receives a [Vm] and returns
    /// the virtual resource corresponding to the [PM resource][#getPmResource()]
    public void setResources(final ResourceManageable pmResource, final Function<Vm, ResourceManageable> vmResourceFunction) {
        this.pmResource = Objects.requireNonNull(pmResource);
        this.vmResourceFunction = Objects.requireNonNull(vmResourceFunction);
    }

    @Override
    public long getCapacity() {
        return pmResource.getCapacity();
    }

    @Override
    public long getTotalAllocatedResource() {
        return pmResource.getOccupied();
    }

    @Override
    public long getFree() {
        return pmResource.getFree();
    }

    /// A [Function] that receives a [Vm] and returns
    /// the virtual resource corresponding to the [PM resource][#getPmResource()].
    protected Function<Vm, ResourceManageable> getVmResourceFunction() {
        return vmResourceFunction;
    }
}
