/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.provisioners;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.*;
import org.cloudsimplus.vms.Vm;

import java.util.function.Function;

/**
 * An interface that represents the provisioning policy used by a {@link Host}
 * to provide a given physical resource to its {@link Vm}s.
 * Each Host must have its own instance of a ResourceProvisioner for each
 * {@link Resource} it owns, such as {@link Ram}, {@link Bandwidth} (BW) and {@link Pe} (CPU).
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 * TODO There is a lot of confusion between Resource Allocation and Resource Provisioning.
 *      This article makes it clear: https://www.researchgate.net/post/what_is_the_difference_between_resource_allocation_and_resource_provisioning.
 *      Allocation is reservation, while Provisioning is actual usage of some part of the allocated resource.
 *      When a VM is created, the Host allocates resources, but the Host.allocateResourcesForVm
 *      uses ResourceProvisioners for that. More confusing yet, it calls vmScheduler.allocatePesForVm at the end.
 *      The terms allocation and provisioning sometimes are used in the same place.
 *      The Host.allocateResourcesForVm method has the word "allocate" in its name,
 *      while internally it uses ResourceProvisioners (quite confusing).
 *      VmScheduler is using the term "allocation", but since it's accountable for running a VM,
 *      it should perform resource provisioning (request the actual amount of the allocated resource to be used in that moment).
 */
public sealed interface ResourceProvisioner
    permits PeProvisioner, ResourceProvisionerAbstract, ResourceProvisionerNull
{
    /**
     * An attribute that implements the Null Object Design Pattern for ResourceProvisioner objects.
     */
    ResourceProvisioner NULL = new ResourceProvisionerNull();

    /**
     * Allocates an amount of the physical resource for a VM, changing the current capacity
     * of the virtual resource to the given amount.
     *
     * @param vm the virtual machine for which the resource is being allocated
     * @param newTotalVmResourceCapacity the new total amount of resource to allocate to the VM,
     * changing the allocated resource to this new amount. It doesn't increase
     * the current allocated VM resource by the given amount, instead,
     * it changes the VM allocated resource to that amount.
     *
     * @return {@code true} if the resource could be allocated; {@code false} otherwise
     */
    boolean allocateResourceForVm(Vm vm, long newTotalVmResourceCapacity);

    /**
     * Allocates an amount of the physical resource for a VM, changing the current capacity
     * of the virtual resource to the given amount.
     *
     * <p>This method is just a shorthand to avoid explicitly converting
     * a double to long.</p>
     *
     * @param vm the virtual machine for which the resource is being allocated
     * @param newTotalVmResource the new total amount of resource to allocate to the VM,
     * changing the allocated resource to this new amount. It doesn't increase
     * the current allocated VM resource by the given amount, instead,
     * it changes the VM allocated resource to that amount
     * @return {@code true} if the resource could be allocated; {@code false} otherwise
     * @see #allocateResourceForVm(Vm, long)
     */
    default boolean allocateResourceForVm(final Vm vm, final double newTotalVmResource){
        return allocateResourceForVm(vm, (long)newTotalVmResource);
    }

    /**
     * Gets the amount of resource allocated to a given VM from the physical resource
     *
     * @param vm the VM
     * @return the allocated resource for the VM
     */
    long getAllocatedResourceForVm(Vm vm);

    /**
     * @return the total amount of resource allocated to all VMs from the physical resource.
     */
    long getTotalAllocatedResource();

    /**
     * Deallocate the resource for the given VM.
     * @param vm the VM to deallocate the resource
     * @return the amount of allocated VM resource or zero if VM is not found
     */
    long deallocateResourceForVm(Vm vm);

    /**
     * Checks if it is possible to change the current allocated resource for a given VM
     * to a new amount, depending on the available physical resource remaining.
     *
     * @param vm the VM to check if there is enough available resource capacity on the Host to
     * change the allocated amount for the VM
     * @param newVmTotalAllocatedResource the new total amount of resource to be allocated for the VM.
     *
     * @return true, if it is possible to allocate the new total VM resource; false otherwise
     */
    boolean isSuitableForVm(Vm vm, long newVmTotalAllocatedResource);

    /**
     * Checks if it is possible to change the current allocated resource for a given VM
     * to a new amount, depending on the available physical resource remaining.
     *
     * @param vm the VM to check if there is enough available resource capacity on the Host to
     * change the allocated amount for the VM
     * @param resource the resource where its capacity it to be allocated for the VM.
     *
     * @return true, if it is possible to allocate the new total VM resource; false otherwise
     */
    boolean isSuitableForVm(Vm vm, Resource resource);

    /**
     * Gets the physical resource being managed by the provisioner, such as {@link Ram}, {@link Pe}, {@link Bandwidth}, etc.
     * @return the resource managed by this provisioner
     */
    ResourceManageable getPmResource();

    /**
     * Sets the physical resource to be managed by the provisioner, such as {@link Ram}, {@link Pe}, {@link Bandwidth}, etc.
     * @param pmResource the resource managed by this provisioner
     * @param vmResourceFunction a {@link Function} that receives a {@link Vm} and returns
     *                           the virtual resource corresponding to the {@link #getPmResource() PM resource}
     */
    void setResources(ResourceManageable pmResource, Function<Vm, ResourceManageable> vmResourceFunction);

    /**
     * Gets the total capacity of the physical resource from the Host that the provisioner manages.
     *
     * @return the total physical resource capacity
     */
    long getCapacity();

    /**
     * Gets the amount of free available physical resource from the Host that the provisioner can allocate to VMs.
     *
     * @return the amount of free available physical resource
     */
    long getAvailableResource();
}
