/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.resources;

import org.cloudbus.cloudsim.core.Identificable;
import org.cloudbus.cloudsim.provisioners.PeProvisioner;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;

/**
 * A interface to be implemented by each class that provides
 * the basic features of a virtual or physical Processing Element (PE)
 * of a PM or VM. Each Pe represents a  virtual or physical processor core.
 *
 * <p>It also implements the Null Object
 * Design Pattern in order to start avoiding {@link NullPointerException} when
 * using the {@link Pe#NULL} object instead of attributing {@code null} to
 * {@link Pe} variables.</p>
 *
 * @author Manzur Murshed
 * @author Rajkumar Buyya
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface Pe extends Identificable, ResourceManageable {
    /**
     * Status of PEs.
     */
    enum Status {
        /** Denotes PE is FREE for allocation. */
        FREE,

        /** Denotes PE is allocated and hence busy processing some Cloudlet. */
        BUSY,

        /**
         * Denotes PE is failed and hence it can't process any Cloudlet at this moment.
         * This PE is failed because it belongs to a machine which is also failed.
         */
        FAILED
    }

    /**
     * Gets the capacity of this Pe in MIPS (Million Instructions Per Second).
     *
     * @return the MIPS capacity
     * @pre $none
     * @post $result >= 0
     */
    @Override
    long getCapacity();

    /**
     * Sets the capacity of this Pe in MIPS (Million Instructions Per Second).
     *
     * @param mipsCapacity the MIPS capacity to set
     * @return true if mipsCapacity > 0, false otherwise
     * @pre mipsCapacity >= 0
     * @post $none
     */
    @Override
    boolean setCapacity(long mipsCapacity);

    /**
     * Sets the capacity of this Pe in MIPS (Million Instructions Per Second).
     *
     * <p>It receives the amount of MIPS as a double value but converts it internally
     * to a long. The method is just provided as a handy-way to define the PE
     * capacity using a double value that usually is generated from some computations.</p>
     *
     * @param mipsCapacity the MIPS capacity to set
     * @return true if mipsCapacity > 0, false otherwise
     * @pre mipsCapacity >= 0
     * @post $none
     */
    boolean setCapacity(double mipsCapacity);

    /**
     * Sets the {@link #getPeProvisioner()} that manages the allocation
     * of this physical PE to virtual machines.
     * This method is automatically called when a {@link PeProvisioner} is created
     * passing a Pe instance. Thus, the PeProvisioner for a Pe doesn't have to be
     * set manually.
     *
     * @param peProvisioner the new PE provisioner
     */
    Pe setPeProvisioner(PeProvisioner peProvisioner);

    /**
     * Gets the PE provisioner that manages the allocation
     * of this physical PE to virtual machines.
     *
     * @return the PE provisioner
     */
    PeProvisioner getPeProvisioner();

    /**
     * Gets the status of the PE.
     *
     * @return the PE status
     * @pre $none
     * @post $none
     */
    Status getStatus();

    /**
     * Sets the {@link #getId()}.
     *
     * @param id the new PE id
     */
    void setId(int id);

    /**
     * Sets the {@link #getStatus() status} of the PE.
     *
     * @param status the new PE status
     * @return true if the status was set, false otherwise
     * @pre $none
     * @post $none
     */
    boolean setStatus(Status status);

    /**
     * An attribute that implements the Null Object Design Pattern for {@link Pe}
     * objects.
     */
    Pe NULL = new Pe(){
        @Override public long getAvailableResource() { return 0; }
        @Override public long getAllocatedResource() { return 0; }
        @Override public boolean isResourceAmountAvailable(long amountToCheck) { return false; }
        @Override public boolean isResourceAmountAvailable(double amountToCheck) { return false; }
        @Override public boolean isFull() { return false; }
        @Override public int getId(){ return -1; }
        @Override public long getCapacity() { return 0; }
        @Override public PeProvisioner getPeProvisioner() { return new PeProvisionerSimple(); }
        @Override public Status getStatus() { return Status.FAILED; }
        @Override public void setId(int id) {}
        @Override public boolean setCapacity(long mipsCapacity){ return false; }
        @Override public boolean setCapacity(double mipsCapacity) { return false; }
        @Override public Pe setPeProvisioner(PeProvisioner peProvisioner) { return Pe.NULL; }
        @Override public boolean allocateResource(long amountToAllocate) { return false; }
        @Override public boolean setAllocatedResource(long newTotalAllocatedResource) { return false; }
        @Override public boolean deallocateResource(long amountToDeallocate) { return false; }
        @Override public long deallocateAllResources() { return 0; }
        @Override public boolean isResourceAmountBeingUsed(long amountToCheck) { return false; }
        @Override public boolean isSuitable(long newTotalAllocatedResource) { return false; }
        @Override public boolean setStatus(Status status) { return false; }
    };
}
