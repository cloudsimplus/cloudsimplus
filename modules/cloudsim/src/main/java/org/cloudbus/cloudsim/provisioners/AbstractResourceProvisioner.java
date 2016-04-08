/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.provisioners;

import java.util.HashMap;
import java.util.Map;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.util.Calculator;
import org.cloudbus.cloudsim.resources.ResourceManageable;

/**
 * An abstract class that implements the basic features of a provisioning policy used by a host 
 * to allocate a given resource to virtual machines inside it. 
 * 
 * @see ResourceProvisioner
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @param <T> The type of the resource capacity of the provisioner
 * @since 3.0.4
 */
public abstract class AbstractResourceProvisioner<T extends Number & Comparable<T>> implements ResourceProvisioner<T> {
    /**
     * The resource being managed for the provisioner, such as RAM, BW or CPU.
     */
    private final ResourceManageable<T> resource;
    
    /**
     * The class of the resource being managed for the provisioner,
     * such as {@link ResourceCpu}, {@link ResourceRam} or {@link ResourceBandwidth}.
     * @see #resource
     */
    private final Class<? extends ResourceManageable<T>> resourceClass;
    
    /** The VM resource allocation map, where each key is a VM and each value
     * is the amount of resource allocated to that VM. */
    private final Map<Vm, T> resourceAllocationMap;
    
    /** A calculator for basic math operations over values extending of the Number class. */
    private final Calculator<T> calc;    

    /**
     * Creates a new ResourceManageable Provisioner.
     * 
     * @param resource The resource to be managed by the provisioner
     * @post $none
     */
    public AbstractResourceProvisioner(final ResourceManageable<T> resource) {
        if(resource == null)
            throw new IllegalArgumentException("Resource cannot be null");
        calc = new Calculator<>(resource.getCapacity());
        this.resource = resource;
        this.resourceClass = (Class<ResourceManageable<T>>)resource.getClass();
        this.resourceAllocationMap = new HashMap<>();
    }

    @Override
    public T getAllocatedResourceForVm(Vm vm) {
        if (getResourceAllocationMap().containsKey(vm)) {
                return getResourceAllocationMap().get(vm);
        }
        return calc.getZero();
    }
    
    @Override
    public void deallocateResourceForAllVms() {
        for(Vm vm: getResourceAllocationMap().keySet()){
            deallocateResourceForVmSettingAllocationMapEntryToZero(vm);
        }
        getResourceAllocationMap().clear();
    }    

    /**
     * Deallocate the resource for the given VM, without removing
     * the VM fro the allocation map. The resource usage of the VM entry on the allocation map
     * is just set to 0.
     * @param vm the VM to deallocate resource
     * @return the amount of allocated VM resource or zero if VM is not found
     */
    protected abstract T deallocateResourceForVmSettingAllocationMapEntryToZero(Vm vm);

    /**
     * @return the resource
     */
    protected ResourceManageable<T> getResource() {
        return resource;
    }

    /**
     * @return the resourceClass
     */
    protected Class<? extends ResourceManageable<T>> getResourceClass() {
        return resourceClass;
    }

    /**
     * @return the resourceAllocationMap
     */
    protected Map<Vm, T> getResourceAllocationMap() {
        return resourceAllocationMap;
    }

    @Override
    public T getCapacity() {
        return resource.getCapacity();
    }

    @Override
    public T getTotalAllocatedResource() {
        return resource.getAllocatedResource();
    }

    @Override
    public T getAvailableResource() {
        return resource.getAvailableResource();
    }
}
