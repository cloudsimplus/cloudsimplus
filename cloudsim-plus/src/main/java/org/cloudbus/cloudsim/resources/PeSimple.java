/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.resources;

import org.cloudbus.cloudsim.provisioners.PeProvisioner;

import java.util.Objects;

/**
 * Pe (Processing Element) class represents a CPU core of a physical machine (PM),
 * defined in terms of Millions Instructions Per Second (MIPS) rating.
 * Such a class allows managing the Pe capacity and allocation.
 *
 * <p>
 * <b>ASSUMPTION:</b> All PEs under the same Machine have the same MIPS rating.
 * </p>
 * @TODO This assumption is not being assured on different classes (where other TODOs were included)
 *
 * @author Manzur Murshed
 * @author Rajkumar Buyya
 * @since CloudSim Toolkit 1.0
 */
public class PeSimple extends ResourceManageableAbstract implements Pe {
    /** @see #getId()  */
    private int id;

    /** @see #getStatus()  */
    private Status status;

    /** @see #getPeProvisioner() */
    private PeProvisioner peProvisioner;

    /**
     * Instantiates a new PE object.
     * The id of the PE is just set when a List of PEs is assigned to a Host.
     *
     * @param mipsCapacity the capacity of the PE in MIPS (Million Instructions per Second)
     * @param peProvisioner the provisioner that will manage the allocation of this physical Pe for VMs
     * @pre peProvisioner != null
     * @post $none
     */
    public PeSimple(double mipsCapacity, PeProvisioner peProvisioner) {
        super((long)mipsCapacity);
        setId(-1);
        setPeProvisioner(peProvisioner);

        // when created it should be set to FREE, i.e. available for use.
        setStatus(Status.FREE);
    }

    /**
     * Instantiates a new PE object defining a given id.
     * The id of the PE is just set when a List of PEs is assigned to a Host.
     *
     * @param id the PE id
     * @param mipsCapacity the capacity of the PE in MIPS (Million Instructions per Second)
     * @param peProvisioner the provisioner that will manage the allocation of this physical Pe for VMs
     * @pre peProvisioner != null
     * @post $none
     */
    public PeSimple(int id, double mipsCapacity, PeProvisioner peProvisioner) {
        this(mipsCapacity, peProvisioner);
        this.setId(id);
    }

    @Override
    public final void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public final boolean setStatus(Status status) {
        this.status = status;
        return true;
    }

    @Override
    public boolean setCapacity(double mipsCapacity) {
        return setCapacity((long)mipsCapacity);
    }

    @Override
    public final Pe setPeProvisioner(PeProvisioner peProvisioner) {
        Objects.requireNonNull(peProvisioner);
        this.peProvisioner = peProvisioner;
        this.peProvisioner.setPe(this);
        return this;
    }

    /**
     * Gets the PE provisioner that manages the allocation
     * of this physical PE to virtual machines.
     *
     * @return the PE provisioner
     */
    @Override
    public PeProvisioner getPeProvisioner() {
        return peProvisioner;
    }

    @Override
    public String toString() {
        return String.valueOf(getId());
    }

    @Override
    public boolean isWorking() {
        return !isFailed();
    }

    @Override
    public boolean isFailed() {
        return Status.FAILED.equals(status);
    }

    @Override
    public boolean isFree() {
        return Status.FREE.equals(status);
    }

    @Override
    public boolean isBuzy() {
        return Status.BUSY.equals(status);
    }
}
