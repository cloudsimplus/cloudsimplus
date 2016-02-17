/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.provisioners.PeProvisioner;

/**
 * Pe (Processing Element) class represents a CPU core of a physical machine (PM), 
 * defined in terms of Millions Instructions Per Second (MIPS) rating.<p/>
 * 
 * <b>ASSUMPTION:</b> All PEs under the same Machine have the same MIPS rating.
 * @todo This assumption is not being assured on different class (where other TODOs where introduced)
 * 
 * @author Manzur Murshed
 * @author Rajkumar Buyya
 * @since CloudSim Toolkit 1.0
 */
public class Pe {
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

    /** @see #getId()  */
    private int id;

    /** @see #getStatus()  */
    private Status status;

    /** @see #getPeProvisioner() */
    private PeProvisioner peProvisioner;

    /**
     * Instantiates a new PE object.
     * 
     * @param id the PE ID
     * @param peProvisioner the PE provisioner
     * @pre id >= 0
     * @pre peProvisioner != null
     * @post $none
     */
    public Pe(int id, PeProvisioner peProvisioner) {
        setId(id);
        setPeProvisioner(peProvisioner);

        // when created it should be set to FREE, i.e. available for use.
        setStatus(Status.FREE);
    }

    /**
     * Sets the {@link #getId()}.
     * 
     * @param id the new PE id
     */
    protected final void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the PE id.
     * 
     * @return the PE id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the MIPS Rating of this PE.
     * 
     * @param d the mips
     * @return true if MIPS > 0, false otherwise
     * @pre mips >= 0
     * @post $none
     */
    public boolean setMips(double d) {
        return getPeProvisioner().setMips(d);
    }

    /**
     * Gets the MIPS Rating of this Pe.
     * 
     * @return the MIPS Rating
     * @pre $none
     * @post $result >= 0
     */
    public int getMips() {
        return (int) getPeProvisioner().getMips();
    }

    /**
     * Gets the status of the PE.
     * 
     * @return the PE status
     * @pre $none
     * @post $none
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the {@link #getStatus() status} of the PE.
     * 
     * @param status the new PE status
     * @pre $none
     * @post $none
     */
    public final void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Sets the {@link #getPeProvisioner()} that manages the allocation
     * of this physical PE to virtual machines.
     * 
     * @param peProvisioner the new PE provisioner
     */
    protected final void setPeProvisioner(PeProvisioner peProvisioner) {
        if(peProvisioner == null)
            throw new IllegalArgumentException("The peProvisioner of a Pe cannot be null");
        this.peProvisioner = peProvisioner;
    }

    /**
     * Gets the PE provisioner that manages the allocation
     * of this physical PE to virtual machines.
     * 
     * @return the PE provisioner
     */
    public PeProvisioner getPeProvisioner() {
        return peProvisioner;
    }

}
