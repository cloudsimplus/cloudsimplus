package org.cloudbus.cloudsim.provisioners;

import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * An interface that represents the provisioning policy
 * used by a host to provide virtual PEs to its virtual machines.
 * It gets a physical PE and manage it in order to provide this PE as virtual PEs for VMs.
 * In that way, a given PE might be shared among different VMs.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.1
 */
public interface PeProvisioner extends ResourceProvisioner {

    /**
     * An attribute that implements the Null Object Design Pattern for
     * PeProvisioner objects.
     */
    PeProvisioner NULL = new PeProvisionerNull();

    /**
     * Sets the {@link Pe} that this provisioner will manage.
     *
     * @param pe the Pe to set
     */
    void setPe(Pe pe);

    /**
     * Allocates an amount of MIPS from the physical Pe to a new virtual PE for a given VM.
     * The virtual PE to be added will use the total or partial MIPS capacity of the
     * physical PE.
     *
     * @param vm the virtual machine for which the new virtual PE is being allocated
     * @param mipsCapacity the MIPS to be allocated to the virtual PE of the given VM
     * @return $true if the virtual PE could be allocated; $false otherwise
     */
    @Override
    boolean allocateResourceForVm(Vm vm, long mipsCapacity);

    /**
     * Gets the amount of allocated MIPS from the physical Pe to a virtual PE of a VM.
     *
     * @param vm the virtual machine to get the allocated virtual Pe MIPS
     * @return the allocated virtual Pe MIPS
     */
    @Override
    long getAllocatedResourceForVm(Vm vm);

    /**
     * Releases the virtual Pe allocated to a given VM.
     *
     * @param vm the vm to release the virtual Pe
     */
    @Override
    boolean deallocateResourceForVm(Vm vm);

    /**
     * Releases all virtual PEs allocated to all VMs.
     */
    @Override
    void deallocateResourceForAllVms();

    /**
     * Gets the total allocated MIPS from the physical Pe.
     *
     * @return the total allocated MIPS
     */
    @Override
    long getTotalAllocatedResource();

    /**
     * Gets the utilization percentage of the Pe in scale from 0 to 1.
     *
     * @return the utilization percentage from 0 to 1
     */
    double getUtilization();
}
