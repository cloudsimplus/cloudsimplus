/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.resources;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.provisioners.PeProvisioner;
import org.cloudsimplus.provisioners.PeProvisionerSimple;

/// A [Pe] (Processing Element) implementation representing a **CPU core** of a physical machine
/// ([Host]), defined in terms of [Millions Instructions Per Second (MIPS)](https://en.wikipedia.org/wiki/Instructions_per_second).
/// Such a class allows managing the Pe capacity and allocation.
///
/// **ASSUMPTION:** All PEs under the same Machine have the same MIPS rating.
///
/// TODO: This assumption is not being assured on different classes (where other TODOs were included)
///
/// @author Manzur Murshed
/// @author Rajkumar Buyya
/// @since CloudSim Toolkit 1.0
@Accessors @Getter @Setter
public class PeSimple extends ResourceManageableAbstract implements Pe {
    /** The default MIPS capacity to be used to create PEs when the no-args constructor is called. */
    private static double defaultMips = 1000;

    private long id;
    private Status status;
    private PeProvisioner peProvisioner;

    /// Creates a PE object with the [default MIPS capacity][#getDefaultMips()] and using a [PeProvisionerSimple].
    /// The id of the PE is just set when a List of PEs is assigned to a Host.
    ///
    /// @see #PeSimple(double, PeProvisioner)
    /// @see #PeSimple(double)
    /// @see #setDefaultMips(double)
    public PeSimple() {
        this(PeSimple.defaultMips);
    }

    /// Creates a PE object using a [PeProvisionerSimple].
    /// The id of the PE is just set when a List of PEs is assigned to a Host.
    ///
    /// @param mipsCapacity the capacity of the PE in MIPS (Million Instructions per Second)
    /// @see #PeSimple(double, PeProvisioner)
    /// @see #PeSimple()
    public PeSimple(final double mipsCapacity) {
        this(mipsCapacity, new PeProvisionerSimple());
    }

    /**
     * Creates a PE object.
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
        setStatus(Status.FREE);
    }

    /**
     * Creates a PE object defining a given id.
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
     * Sets the default MIPS capacity to be used to create PEs when the no-args constructor is used.
     * @param defaultMips the new default MIPS capacity to set
     */
    public static void setDefaultMips(final double defaultMips) {
        PeSimple.defaultMips = defaultMips;
    }

    @Override
    public boolean setCapacity(final double mipsCapacity) {
        return setCapacity((long)mipsCapacity);
    }

    @Override
    public final Pe setPeProvisioner(@NonNull final PeProvisioner peProvisioner) {
        this.peProvisioner = peProvisioner;
        this.peProvisioner.setPe(this);
        return this;
    }

    @Override
    public String toString() {
        return "%s %d: %s".formatted(getClass().getSimpleName(), id, status);
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
