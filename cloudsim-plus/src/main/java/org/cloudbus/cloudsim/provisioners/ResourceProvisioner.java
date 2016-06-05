/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.provisioners;

import org.cloudbus.cloudsim.Vm;

/**
 * An interface that represents the provisioning policy used by a host 
 * to allocate a given resource to virtual machines inside it. 
 * 
 * Each host has to have its own instance of a ResourceProvisioner for each
 * resource it owns, such as RAM, Bandwidth (BW) and CPU.
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @param <T> The type of the resource capacity of the provisioner
 * @since 3.0.4
 */
public interface ResourceProvisioner<T extends Number & Comparable<T>> {
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
    boolean allocateResourceForVm(Vm vm, T newTotalVmResource);

    /**
     * Gets the amount of allocated resource for a given VM
     * 
     * @param vm the VM
     * 
     * @return the allocated resource for the VM
     */
    T getAllocatedResourceForVm(Vm vm);

    /**
     * Gets the total allocated resource among all VMs
     * 
     * @return the total allocated resource among all VMs
     */
    T getTotalAllocatedResource();

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
    boolean isSuitableForVm(Vm vm, T newVmTotalAllocatedResource);
    
    /**
     * Gets the total capacity of the resource from the host that the provisioner manages.
     * 
     * @return the total resource capacity
     */
    T getCapacity();       
    
    /**
     * Gets the amount of free available resource from the host that the provisioner can allocate to VMs.
     * 
     * @return the amount of free available resource
     */
    T getAvailableResource();    
    
    
    /**
     * A property that implements the Null Object Design Pattern for 
     * ResourceProvisioner&lt;Double&gt; objects.
     */
    ResourceProvisioner<Double> NULL_DOUBLE = new ResourceProvisioner<Double>(){
        @Override public boolean allocateResourceForVm(Vm vm, Double newTotalVmResource) { return false; }
        @Override public Double getAllocatedResourceForVm(Vm vm) { return 0.0; }
        @Override public Double getTotalAllocatedResource() { return 0.0; }
        @Override public boolean deallocateResourceForVm(Vm vm) { return false; }
        @Override public void deallocateResourceForAllVms() {}
        @Override public boolean isSuitableForVm(Vm vm, Double newVmTotalAllocatedResource) { return false; }
        @Override public Double getCapacity() { return 0.0; }
        @Override public Double getAvailableResource() { return 0.0; }
    };    
    
    /**
     * A property that implements the Null Object Design Pattern for 
     * ResourceProvisioner&lt;Long&gt; objects.
     */
    ResourceProvisioner<Long> NULL_LONG = new ResourceProvisioner<Long>(){
        @Override public boolean allocateResourceForVm(Vm vm, Long newTotalVmResource) { return false; }
        @Override public Long getAllocatedResourceForVm(Vm vm) { return 0L; }
        @Override public Long getTotalAllocatedResource() { return 0L; }
        @Override public boolean deallocateResourceForVm(Vm vm) { return false; }
        @Override public void deallocateResourceForAllVms() {}
        @Override public boolean isSuitableForVm(Vm vm, Long newVmTotalAllocatedResource) { return false; }
        @Override public Long getCapacity() { return 0L; }
        @Override public Long getAvailableResource() { return 0L; }
    };        

    /**
     * A property that implements the Null Object Design Pattern for 
     * ResourceProvisioner&lt;Integer&gt; objects.
     */
    ResourceProvisioner<Integer> NULL_INT = new ResourceProvisioner<Integer>(){
        @Override public boolean allocateResourceForVm(Vm vm, Integer newTotalVmResource) { return false; }
        @Override public Integer getAllocatedResourceForVm(Vm vm) { return 0; }
        @Override public Integer getTotalAllocatedResource() { return 0; }
        @Override public boolean deallocateResourceForVm(Vm vm) { return false; }
        @Override public void deallocateResourceForAllVms() {}
        @Override public boolean isSuitableForVm(Vm vm, Integer newVmTotalAllocatedResource) { return false; }
        @Override public Integer getCapacity() { return 0; }
        @Override public Integer getAvailableResource() { return 0; }
    };     
}
