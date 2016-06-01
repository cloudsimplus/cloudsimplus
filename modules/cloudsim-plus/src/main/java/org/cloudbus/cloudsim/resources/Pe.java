package org.cloudbus.cloudsim.resources;

import org.cloudbus.cloudsim.provisioners.PeProvisioner;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;

/**
 * <p>A interface to be implemented by each class that provides
 * the basic features of a virtual or physical Processing Element (PE)
 * of a PM or VM. Each Pe represents a  virtual or physical processor core.</p>
 * 
 * It also implements the Null Object
 * Design Pattern in order to start avoiding {@link NullPointerException} when
 * using the {@link Pe#NULL} object instead of attributing {@code null} to
 * {@link Pe} variables.
 * 
 * @author Manoel Campos da Silva Filho
 */
public interface Pe {
    /**
     * Status of PEs.
     */
    public enum Status {
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
     * Gets the PE id.
     *
     * @return the PE id
     */
    int getId();

    /**
     * Gets the MIPS Rating of this Pe.
     *
     * @return the MIPS Rating
     * @pre $none
     * @post $result >= 0
     */
    int getMips();

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
     * Sets the MIPS Rating of this PE.
     *
     * @param d the mips
     * @return true if MIPS > 0, false otherwise
     * @pre mips >= 0
     * @post $none
     */
    boolean setMips(double d);

    /**
     * Sets the {@link #getStatus() status} of the PE.
     *
     * @param status the new PE status
     * @pre $none
     * @post $none
     */
    void setStatus(Status status);
    
    /**
     * A property that implements the Null Object Design Pattern for {@link Pe}
     * objects.
     */
    Pe NULL = new Pe(){
        @Override public int getId(){ return 0; }
        @Override public int getMips() { return 0; }
        @Override public PeProvisioner getPeProvisioner() { return new PeProvisionerSimple(0); }
        @Override public Status getStatus() { return Status.FAILED; }
        @Override public boolean setMips(double d){ return false; }
        @Override public void setStatus(Status status) {}
    };
}
