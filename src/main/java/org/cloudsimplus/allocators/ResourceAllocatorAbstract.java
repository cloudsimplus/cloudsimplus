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
    /// @see ResourceAllocator#setResources(ResourceManageable, Function)
    protected ResourceAllocatorAbstract() {
        this(ResourceManageable.NULL, vm -> ResourceManageable.NULL);
    }

    /// Creates a ResourceManageable Allocator.
    ///
    /// @param pmResource the physical resource to be managed by the allocator
    /// @param vmResourceFunction a [Function] that receives a [Vm] and returns
    /// the virtual resource corresponding to the [PM resource][#getPmResource()]
    public ResourceAllocatorAbstract(final ResourceManageable pmResource, final Function<Vm, ResourceManageable> vmResourceFunction) {
        setResources(pmResource, vmResourceFunction);
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

    @Override
    public final void setResources(final ResourceManageable pmResource, final Function<Vm, ResourceManageable> vmResourceFunction) {
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
