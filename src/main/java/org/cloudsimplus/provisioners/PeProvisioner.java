package org.cloudsimplus.provisioners;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.vms.Vm;

/**
 * An interface that represents the provisioning policy
 * used by a host to provide virtual {@link Pe}s to its virtual machines.
 * It gets a physical PE from a {@link Host} and manages it to provide this PE as a virtual PE for
 * {@link Vm}s. That way, a given PE might be shared between different VMs.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.1
 */
public sealed interface PeProvisioner
    extends ResourceProvisioner permits PeProvisionerAbstract, PeProvisionerNull
{

    /**
     * An attribute that implements the Null Object Design Pattern for PeProvisioner objects.
     */
    PeProvisioner NULL = new PeProvisionerNull();

    /**
     * Sets the {@link Pe} that this provisioner will manage.
     *
     * @param pe the Pe to set
     */
    void setPe(Pe pe);

    /**
     * Allocates an amount of MIPS from the physical PE to a new virtual PE for a given VM.
     * The virtual PE to be added will use the total or partial MIPS capacity of the
     * physical PE.
     *
     * @param vm the virtual machine for which the new virtual PE is being allocated
     * @param mipsCapacity the MIPS to be allocated to the virtual PE of the given VM
     * @return {@code true} if the virtual PE could be allocated; {@code false} otherwise
     */
    @Override
    boolean allocateResourceForVm(Vm vm, long mipsCapacity);

    /**
     * Gets the amount of allocated MIPS from the physical PE to a virtual PE of a VM.
     *
     * @param vm the virtual machine to get the allocated virtual PE MIPS
     * @return the allocated virtual PE MIPS
     */
    @Override
    long getAllocatedResourceForVm(Vm vm);

    /**
     * Releases the virtual PE allocated to a given VM.
     *
     * @param vm the vm to release the virtual PE
     * @return the previously allocated MIPS for the VM's virtual PE
     */
    @Override
    long deallocateResourceForVm(Vm vm);

    /**
     * @return the total allocated MIPS from the physical PE.
     */
    @Override
    long getTotalAllocatedResource();

    /**
     * @return the utilization percentage of the PE (in scale from 0 to 1).
     */
    double getUtilization();
}
