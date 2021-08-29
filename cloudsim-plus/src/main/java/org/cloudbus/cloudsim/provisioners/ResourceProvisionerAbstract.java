/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.provisioners;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.ResourceManageable;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Objects;
import java.util.function.Function;

/**
 * An abstract class that implements the basic features of a provisioning policy used by a {@link Host}
 * to provide a given resource to its virtual machines.
 *
 * @see ResourceProvisioner
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since 3.0.4
 */
public abstract class ResourceProvisionerAbstract implements ResourceProvisioner {
    /**
     * @see #getPmResource()
     */
    private ResourceManageable pmResource;

    /** @see #getVmResourceFunction() */
    private Function<Vm, ResourceManageable> vmResourceFunction;

    /**
     * Creates a new ResourceManageable Provisioner for which the {@link #getPmResource() resource}
     * must be set further.
     * @see ResourceProvisioner#setResources(ResourceManageable, Function)
     */
    protected ResourceProvisionerAbstract() {
        this(ResourceManageable.NULL, vm -> ResourceManageable.NULL);
    }

    /**
     * Creates a new ResourceManageable Provisioner.
     *
     * @param pmResource The physical resource to be managed by the provisioner
     * @param vmResourceFunction a {@link Function} that receives a {@link Vm} and returns
     *                           the virtual resource corresponding to the {@link #getPmResource() PM resource}
     */
    public ResourceProvisionerAbstract(final ResourceManageable pmResource, final Function<Vm, ResourceManageable> vmResourceFunction) {
        setResources(pmResource, vmResourceFunction);
    }

    @Override
    public long getAllocatedResourceForVm(final Vm vm) {
        return vmResourceFunction.apply(vm).getAllocatedResource();
    }

    @Override
    public ResourceManageable getPmResource() {
        return pmResource;
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
        return pmResource.getAllocatedResource();
    }

    @Override
    public long getAvailableResource() {
        return pmResource.getAvailableResource();
    }

    /**
     * A {@link Function} that receives a {@link Vm} and returns
     * the virtual resource corresponding to the {@link #pmResource}.
     */
    protected Function<Vm, ResourceManageable> getVmResourceFunction() {
        return vmResourceFunction;
    }
}
