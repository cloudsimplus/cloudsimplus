/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.resources;

import org.cloudbus.cloudsim.provisioners.PeProvisioner;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;

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
    /** @see #setDefaultMips(double) */
    private static double defaultMips = 1000;

    /** @see #getId()  */
    private long id;

    /** @see #getStatus()  */
    private Status status;

    /** @see #getPeProvisioner() */
    private PeProvisioner peProvisioner;

    /**
     * Instantiates a PE object with the {@link #getDefaultMips() default MIPS capacity} and using a {@link PeProvisionerSimple}.
     * The id of the PE is just set when a List of PEs is assigned to a Host.
     *
     * @see #PeSimple(double, PeProvisioner)
     * @see #PeSimple(double)
     * @see #setDefaultMips(double)
     */
    public PeSimple() {
        this(PeSimple.defaultMips);
    }

    /**
     * Instantiates a PE object using a {@link PeProvisionerSimple}.
     * The id of the PE is just set when a List of PEs is assigned to a Host.
     *
     * @param mipsCapacity the capacity of the PE in MIPS (Million Instructions per Second)
     * @see #PeSimple(double, PeProvisioner)
     * @see #PeSimple()
     */
    public PeSimple(final double mipsCapacity) {
        this(mipsCapacity, new PeProvisionerSimple());
    }

    /**
     * Instantiates a PE object.
     * The id of the PE is just set when a List of PEs is assigned to a Host.
     *
     * @param mipsCapacity the capacity of the PE in MIPS (Million Instructions per Second)
     * @param peProvisioner the provisioner that will manage the allocation of this physical Pe for VMs
     * @see #PeSimple(double)
     * @see #PeSimple()
     */
    public PeSimple(final double mipsCapacity, final PeProvisioner peProvisioner) {
        super((long)mipsCapacity, "Unit");
        setId(-1);
        setPeProvisioner(peProvisioner);

        // when created it should be set to FREE, i.e. available for use.
        setStatus(Status.FREE);
    }

    /**
     * Instantiates a PE object defining a given id.
     * The id of the PE is just set when a List of PEs is assigned to a Host.
     *
     * @param id the PE id
     * @param mipsCapacity the capacity of the PE in MIPS (Million Instructions per Second)
     * @param peProvisioner the provisioner that will manage the allocation of this physical Pe for VMs
     * @see #PeSimple(double, PeProvisioner)
     * @see #PeSimple(double)
     * @see #PeSimple()
     */
    public PeSimple(final int id, final double mipsCapacity, final PeProvisioner peProvisioner) {
        this(mipsCapacity, peProvisioner);
        this.setId(id);
    }

    /**
     * Gets the default MIPS capacity to be used to create PEs when the no-args constructor is used.
     * @return
     */
    public static double getDefaultMips() {
        return defaultMips;
    }

    /**
     * Sets the default MIPS capacity to be used to create PEs when the no-args constructor is used.
     * @param defaultMips the new default MIPS capacity to set
     * @return
     */
    public static void setDefaultMips(final double defaultMips) {
        PeSimple.defaultMips = defaultMips;
    }

    @Override
    public final void setId(final long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public final boolean setStatus(final Status status) {
        this.status = status;
        return true;
    }

    @Override
    public boolean setCapacity(final double mipsCapacity) {
        return setCapacity((long)mipsCapacity);
    }

    @Override
    public final Pe setPeProvisioner(final PeProvisioner peProvisioner) {
        this.peProvisioner = Objects.requireNonNull(peProvisioner);
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
        return String.format("%s %d: %s", getClass().getSimpleName(), id, status);
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
    public boolean isBusy() {
        return Status.BUSY.equals(status);
    }
}
