/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.allocators;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.*;
import org.cloudsimplus.vms.Vm;

import java.util.function.Function;

/// Allocates (reserves) part of a Host [Resource], such as [Ram] or [Bandwidth], for each VM placed on that Host.
/// No other VM can use that amount, even when the VM is idle.
/// This is done when a VM is placed on a Host (or migrated to it) and when the VM is vertically scaled.
/// The Host then provisions the allocated amount to the VM, making it the VM capacity for the resource
/// (see [Host#provisionResourceForVm(Vm, Class, long)]).
/// Each Host must have its own instance of a ResourceAllocator for each such resource.
/// The Host CPU cores are allocated by the [org.cloudsimplus.schedulers.vm.VmScheduler].
///
/// ## Placement, allocation, provisioning and utilization
///
/// In CloudSim Plus, these steps happen in this order:
///
/// 1. **placement** is choosing the Host for a VM (made by the [org.cloudsimplus.placementpolicies.VmPlacementPolicy]);
/// 2. **allocation** is reserving part of the Host resources for the VM (made by this class and the `VmScheduler`);
/// 3. **provisioning** is delivering the allocated resources to the VM, so it can use them (made by the [Host]);
/// 4. **utilization** is how much of the provisioned resources is actually used at a given time,
///    defined by the [org.cloudsimplus.schedulers.cloudlet.CloudletScheduler] of the VM
///    and the [org.cloudsimplus.utilizationmodels.UtilizationModel]s of its Cloudlets.
///
/// The cloud computing literature often uses provisioning as a synonym of allocation,
/// and some authors use provisioning for what CloudSim Plus calls utilization.
///
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.0
public sealed interface ResourceAllocator
    permits ResourceAllocatorAbstract, ResourceAllocatorNull
{
    /// An attribute that implements the Null Object Design Pattern for ResourceAllocator objects.
    ResourceAllocator NULL = new ResourceAllocatorNull();

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
    boolean allocateResourceForVm(Vm vm, long newTotalVmResourceCapacity);

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
    default boolean allocateResourceForVm(final Vm vm, final double newTotalVmResource){
        return allocateResourceForVm(vm, (long)newTotalVmResource);
    }

    /// Gets the amount of resource allocated to a given VM from the physical resource
    ///
    /// @param vm the VM
    /// @return the allocated resource for the VM
    long getAllocatedResourceForVm(Vm vm);

    /// @return the total amount of resource allocated to all VMs from the physical resource.
    long getTotalAllocatedResource();

    /// Deallocate the resource for the given VM.
    /// @param vm the VM to deallocate the resource
    /// @return the amount of allocated VM resource or zero if VM is not found
    long deallocateResourceForVm(Vm vm);

    /// Checks if it is possible to change the current allocated resource for a given VM
    /// to a new amount, depending on the available physical resource remaining.
    ///
    /// @param vm the VM to check if there is enough available resource capacity on the Host to
    /// change the allocated amount for the VM
    /// @param newVmTotalAllocatedResource the new total amount of resource to be allocated for the VM.
    ///
    /// @return true, if it is possible to allocate the new total VM resource; false otherwise
    boolean isSuitableForVm(Vm vm, long newVmTotalAllocatedResource);

    /// Checks if it is possible to change the current allocated resource for a given VM
    /// to a new amount, depending on the available physical resource remaining.
    ///
    /// @param vm the VM to check if there is enough available resource capacity on the Host to
    /// change the allocated amount for the VM
    /// @param resource the resource where its capacity it to be allocated for the VM.
    ///
    /// @return true, if it is possible to allocate the new total VM resource; false otherwise
    boolean isSuitableForVm(Vm vm, Resource resource);

    /// Gets the physical resource being managed by the allocator, such as [Ram], core, [Bandwidth], etc.
    /// @return the resource managed by this allocator
    ResourceManageable getPmResource();

    /// Sets the physical resource to be managed by the allocator, such as [Ram], core, [Bandwidth], etc.
    /// @param pmResource the resource managed by this allocator
    /// @param vmResourceFunction a [Function] that receives a [Vm] and returns
    /// the virtual resource corresponding to the [PM resource][#getPmResource()]
    void setResources(ResourceManageable pmResource, Function<Vm, ResourceManageable> vmResourceFunction);

    /// Gets the total capacity of the physical resource from the Host that the allocator manages.
    ///
    /// @return the total physical resource capacity
    long getCapacity();

    /// Gets the amount of free available physical resource from the Host that the allocator can allocate to VMs.
    ///
    /// @return the amount of free available physical resource
    long getFree();
}
