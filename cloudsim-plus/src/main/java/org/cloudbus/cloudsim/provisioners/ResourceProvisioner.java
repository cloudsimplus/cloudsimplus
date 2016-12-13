/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.provisioners;

import org.cloudbus.cloudsim.vms.Vm;

/**
 * An interface that represents the provisioning policy used by a host
 * to allocate a given resource to virtual machines inside it.
 *
 * Each host has to have its own instance of a ResourceProvisioner for each
 * resource it owns, such as RAM, Bandwidth (BW) and CPU.
 *
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since 3.0.4
 */
public interface ResourceProvisioner {
    /**
     * Allocates an amount of resource for a given VM (if the resource
     * was never been allocated before) or change the current allocation.
     * If the VM already has any amount of the resource allocated, deallocate
     * if first and allocate the newTotalVmResource amount.
     *
     * @param vm the virtual machine for which the resource is being allocated
     * @param newTotalVmResource the new total amount of resource to allocate to the VM,
     * changing the allocate resource to this new amount. It doesn't increase
     * the current allocated VM resource by the given amount, instead,
     * it changes the VM allocated resource to that specific amount
     *
     * @return $true if the resource could be allocated; $false otherwise
     *
     * @pre $none
     * @post $none
     */
    boolean allocateResourceForVm(Vm vm, long newTotalVmResource);

    /**
     * Gets the amount of allocated resource for a given VM
     *
     * @param vm the VM
     *
     * @return the allocated resource for the VM
     */
    long getAllocatedResourceForVm(Vm vm);

    /**
     * Gets the total allocated resource among all VMs
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
     * to a new amount, depending on the available resource remaining.
     *
     * @param vm the vm to check if there is enough available resource on the host to
     * change the allocated amount for the VM
     * @param newVmTotalAllocatedResource the new total amount of resource to allocate for the VM.
     *
     * @return true, if it is possible to allocate the new total VM resource; false otherwise
     */
    boolean isSuitableForVm(Vm vm, long newVmTotalAllocatedResource);

    /**
     * Gets the total capacity of the resource from the host that the provisioner manages.
     *
     * @return the total resource capacity
     */
    long getCapacity();

    /**
     * Gets the amount of free available resource from the host that the provisioner can allocate to VMs.
     *
     * @return the amount of free available resource
     */
    long getAvailableResource();


    /**
     * A property that implements the Null Object Design Pattern for
     * ResourceProvisioner&lt;long&gt; objects.
     */
    ResourceProvisioner NULL = new ResourceProvisioner(){
        @Override public boolean allocateResourceForVm(Vm vm, long newTotalVmResource) { return false; }
        @Override public long getAllocatedResourceForVm(Vm vm) { return 0; }
        @Override public long getTotalAllocatedResource() { return 0; }
        @Override public boolean deallocateResourceForVm(Vm vm) { return false; }
        @Override public void deallocateResourceForAllVms() {}
        @Override public boolean isSuitableForVm(Vm vm, long newVmTotalAllocatedResource) { return false; }
        @Override public long getCapacity() { return 0; }
        @Override public long getAvailableResource() { return 0; }
    };
}
