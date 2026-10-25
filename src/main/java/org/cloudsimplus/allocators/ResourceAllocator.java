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
/// This interface only has the methods to read the allocator state, which is what [Host#getRamAllocator()]
/// and [Host#getBwAllocator()] expose. The methods that allocate and deallocate resources are in
/// [ResourceAllocatorAbstract], and only the Host calls them, while provisioning resources to VMs
/// ([Host#provisionResourceForVm(Vm, Class, long)]). This way, an allocation can't be changed
/// without also changing the VM capacity, which would put the Host and the VM out of sync.
///
/// @author Rodrigo N. Calheiros
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.0
///
/// [ResourceAllocatorAbstract] is the only class allowed to implement this interface
/// (even [#NULL] extends it). This way, every ResourceAllocator is a ResourceAllocatorAbstract,
/// so the Host can safely store the allocators it receives as such, to allocate resources.
public sealed interface ResourceAllocator
    permits ResourceAllocatorAbstract
{
    /// An attribute that implements the Null Object Design Pattern for ResourceAllocator objects.
    ResourceAllocator NULL = new ResourceAllocatorNull();

    /// Gets the amount of resource allocated to a given VM from the physical resource
    ///
    /// @param vm the VM
    /// @return the allocated resource for the VM
    long getAllocatedResourceForVm(Vm vm);

    /// @return the total amount of resource allocated to all VMs from the physical resource.
    long getTotalAllocatedResource();

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

    /// Gets the physical resource being managed by the allocator, such as [Ram] or [Bandwidth].
    /// It's returned as a read-only [Resource], since its allocated amount must only be changed by the allocator.
    /// @return the resource managed by this allocator
    Resource getPmResource();

    /// Gets the total capacity of the physical resource from the Host that the allocator manages.
    ///
    /// @return the total physical resource capacity
    long getCapacity();

    /// Gets the amount of free available physical resource from the Host that the allocator can allocate to VMs.
    ///
    /// @return the amount of free available physical resource
    long getFree();
}
