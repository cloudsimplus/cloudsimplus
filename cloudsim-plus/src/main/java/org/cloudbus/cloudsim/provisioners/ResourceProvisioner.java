/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.provisioners;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.*;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * An interface that represents the provisioning policy used by a {@link Host}
 * to allocate a given physical resource to {@link Vm}s inside it.
 *
 * Each host has to have its own instance of a ResourceProvisioner for each
 * {@link Resource} it owns, such as {@link Ram}, {@link Bandwidth} (BW) and {@link Pe} (CPU).
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface ResourceProvisioner {
    /**
     * An attribute that implements the Null Object Design Pattern for
     * ResourceProvisioner objects.
     */
    ResourceProvisioner NULL = new ResourceProvisionerNull();

    /**
     * Allocates an amount of the physical resource for a given VM, changing the current capacity
     * of the virtual resource to the given amount.
     *
     * @param vm the virtual machine for which the resource is being allocated
     * @param newTotalVmResourceCapacity the new total amount of resource to allocate to the VM,
     * changing the allocate resource to this new amount. It doesn't increase
     * the current allocated VM resource by the given amount, instead,
     * it changes the VM allocated resource to that specific amount
     *
     * @return $true if the resource could be allocated; $false otherwise
     *
     * @pre $none
     * @post $none
     */
    boolean allocateResourceForVm(Vm vm, long newTotalVmResourceCapacity);

    /**
     * Allocates an amount of the physical resource for a given VM, changing the current capacity
     * of the virtual resource to the given amount.
     *
     * <p>This method is just a shorthand to avoid explicitly converting
     * a double to long.</p>
     *
     * @param vm the virtual machine for which the resource is being allocated
     * @param newTotalVmResource the new total amount of resource to allocate to the VM,
     * changing the allocate resource to this new amount. It doesn't increase
     * the current allocated VM resource by the given amount, instead,
     * it changes the VM allocated resource to that specific amount
     * @return $true if the resource could be allocated; $false otherwise
     * @see #allocateResourceForVm(Vm, long)
     */
    default boolean allocateResourceForVm(Vm vm, double newTotalVmResource){
        return allocateResourceForVm(vm, (long)newTotalVmResource);
    }

    /**
     * Gets the amount of resource allocated to a given VM from the physical resource
     *
     * @param vm the VM
     *
     * @return the allocated resource for the VM
     */
    long getAllocatedResourceForVm(Vm vm);

    /**
     * Gets the total amount of resource allocated to all VMs from the physical resource
     *
     * @return the total allocated resource among all VMs
     */
    long getTotalAllocatedResource();

    /**
     * Releases all the allocated amount of the resource used by a VM.
     *
     * @param vm the vm
     * @return true if the resource was deallocated; false if the related resource
     * has never been allocated to the given VM.
     *
     * @pre $none
     * @post none
     */
    boolean deallocateResourceForVm(Vm vm);

    /**
     * Releases all the allocated amount of the resource used by all VMs.
     *
     * @pre $none
     * @post none
     */
    void deallocateResourceForAllVms();

    /**
     * Checks if it is possible to change the current allocated resource for a given VM
     * to a new amount, depending on the available physical resource remaining.
     *
     * @param vm the vm to check if there is enough available resource on the host to
     * change the allocated amount for the VM
     * @param newVmTotalAllocatedResource the new total amount of resource to allocate for the VM.
     *
     * @return true, if it is possible to allocate the new total VM resource; false otherwise
     */
    boolean isSuitableForVm(Vm vm, long newVmTotalAllocatedResource);

    /**
     * Gets the resource being managed for the provisioner, such as {@link Ram}, {@link Pe}, {@link Bandwidth}, etc.
     * @return the resource managed by this provisioner
     */
    ResourceManageable getResource();

    /**
     * Sets the resource to be managed for the provisioner, such as {@link Ram}, {@link Pe}, {@link Bandwidth}, etc.
     * @param resource the resource managed by this provisioner
     */
    void setResource(ResourceManageable resource);

    /**
     * Gets the total capacity of the physical resource from the Host that the provisioner manages.
     *
     * @return the total physical resource capacity
     */
    long getCapacity();

    /**
     * Gets the amount of free available physical resource from the host that the provisioner can allocate to VMs.
     *
     * @return the amount of free available physical resource
     */
    long getAvailableResource();
    
    /**
     * Checks if the resource the provisioner manages is allocated to a given Vm.
     * @param vm the VM to check if the resource is allocated to
     * @return true if the resource is allocated to the VM, false otherwise
     */
    boolean isResourceAllocatedToVm(Vm vm);
}
