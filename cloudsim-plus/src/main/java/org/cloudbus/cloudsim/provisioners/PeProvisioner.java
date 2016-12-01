/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.provisioners;

import java.util.List;
import org.cloudbus.cloudsim.datacenters.DatacenterCharacteristics;

import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;

/**
 * /**
 * PeProvisioner is an abstract class that represents the provisioning policy
 * used by a host to allocate its PEs to virtual machines inside it. It gets a
 * physical PE and manage it in order to provide this PE as virtual PEs for VMs.
 * In that way, a given PE might be shared among different VMs. Each host's PE
 * has to have its own instance of a PeProvisioner. When extending this class,
 * care must be taken to guarantee that the field availableMips will always
 * contain the amount of free mipsCapacity available for future allocations.
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public abstract class PeProvisioner {

    /**
     * @see #getMipsCapacity()
     */
    private double mipsCapacity;

    /**
     * The available mipsCapacity.
     */
    private double availableMips;

    /**
     * Creates a new PeProvisioner.
     *
     * @param mipsCapacity The total mipsCapacity capacity of the PE that the provisioner can
     * allocate to VMs
     *
     * @pre mipsCapacity>=0
     * @post $none
     */
    public PeProvisioner(double mipsCapacity) {
        setMipsCapacity(mipsCapacity);
        setAvailableMips(mipsCapacity);
    }

    /**
     * Allocates a new virtual PE with a specific capacity for a given VM. The
     * virtual PE to be added will use the total or partial mipsCapacity capacity of the
     * physical PE.
     *
     * @param vm the virtual machine for which the new virtual PE is being
     * allocated
     * @param mips the mipsCapacity to be allocated to the virtual PE of the given VM
     *
     * @return $true if the virtual PE could be allocated; $false otherwise
     *
     * @pre $none
     * @post $none
     */
    public abstract boolean allocateMipsForVm(Vm vm, double mips);

    /**
     * Allocates a new virtual PE with a specific capacity for a given VM.
     *
     * @param vmUid the virtual machine for which the new virtual PE is being
     * allocated
     * @param mips the mipsCapacity to be allocated to the virtual PE of the given VM
     *
     * @return $true if the virtual PE could be allocated; $false otherwise
     *
     * @pre $none
     * @post $none
     * @see #allocateMipsForVm(Vm, double)
     */
    public abstract boolean allocateMipsForVm(String vmUid, double mips);

    /**
     * Allocates a new set of virtual PEs with a specific capacity for a given
     * VM. The virtual PE to be added will use the total or partial mipsCapacity
     * capacity of the physical PE.
     *
     * @param vm the virtual machine for which the new virtual PE is being
     * allocated
     * @param mips the list of mipsCapacity capacity of each virtual PE to be allocated
     * to the VM
     *
     * @return $true if the set of virtual PEs could be allocated; $false
     * otherwise
     *
     * @pre $none
     * @post $none
     * @todo In this case, each PE can have a different capacity, what in many
     * places this situation is not considered, such as in the
     * {@link Vm}, {@link Pe} and {@link DatacenterCharacteristics} classes.
     */
    public abstract boolean allocateMipsForVm(Vm vm, List<Double> mips);

    /**
     * Gets the list of allocated virtual PEs' MIPS for a given VM.
     *
     * @param vm the virtual machine the get the list of allocated virtual PEs'
     * MIPS
     *
     * @return list of allocated virtual PEs' MIPS
     *
     * @pre $none
     * @post $none
     */
    public abstract List<Double> getAllocatedMipsForVm(Vm vm);

    /**
     * Gets total allocated MIPS for a given VM for all PEs.
     *
     * @param vm the virtual machine the get the total allocated MIPS capacity
     *
     * @return total allocated MIPS
     *
     * @pre $none
     * @post $none
     */
    public abstract double getTotalAllocatedMipsForVm(Vm vm);

    /**
     * Gets the MIPS capacity of a virtual Pe allocated to a given VM.
     *
     * @param vm virtual machine to get a given virtual PE capacity
     * @param peId the virtual pe id
     *
     * @return allocated MIPS for the virtual PE
     *
     * @pre $none
     * @post $none
     */
    public abstract double getAllocatedMipsForVmByVirtualPeId(Vm vm, int peId);

    /**
     * Releases all virtual PEs allocated to a given VM.
     *
     * @param vm the vm
     *
     * @pre $none
     * @post none
     */
    public abstract void deallocateMipsForVm(Vm vm);

    /**
     * Releases all virtual PEs allocated to all VMs.
     *
     * @pre $none
     * @post none
     */
    public void deallocateMipsForAllVms() {
        setAvailableMips(getMipsCapacity());
    }

    /**
     * Gets the total MIPS capacity of the PE that the provisioner can allocate to
     * VMs.
     *
     * @return
     */
    public double getMipsCapacity() {
        return mipsCapacity;
    }

    /**
     * Sets the total MIPS capacity of the PE that the provisioner can allocate to
     * VMs.
     *
     * @param mipsCapacity the MIPS capacity to set
     * @return true if mipsCapacity > 0, false otherwise
     */
    public final boolean setMipsCapacity(double mipsCapacity) {
        if (mipsCapacity <= 0) {
            return false;
        }

        this.mipsCapacity = mipsCapacity;
        return true;
    }

    /**
     * Gets the available MIPS in the PE.
     *
     * @return available MIPS
     *
     * @pre $none
     * @post $none
     */
    public double getAvailableMips() {
        return availableMips;
    }

    /**
     * Sets the available MIPS in the PE.
     *
     * @param availableMips the availableMips to set
     * @return true if availableMips >= 0, false otherwise
     */
    protected final boolean setAvailableMips(double availableMips) {
        if (availableMips < 0) {
            return false;
        }
        if (availableMips > mipsCapacity) {
            availableMips = mipsCapacity;
        }

        this.availableMips = availableMips;
        return true;
    }

    /**
     * Gets the total allocated MIPS.
     *
     * @return the total allocated MIPS
     */
    public double getTotalAllocatedMips() {
        double totalAllocatedMips = getMipsCapacity() - getAvailableMips();
        if (totalAllocatedMips > 0) {
            return totalAllocatedMips;
        }
        return 0;
    }

    /**
     * Gets the utilization of the Pe in percents.
     *
     * @return the utilization
     */
    public double getUtilization() {
        return getTotalAllocatedMips() / getMipsCapacity();
    }

}
