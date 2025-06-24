/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.resources;

import org.cloudsimplus.core.ChangeableId;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.provisioners.PeProvisioner;
import org.cloudsimplus.vms.Vm;

/// An interface to be implemented by each class that provides
/// the basic features for a virtual or physical Processing Element (PE)
/// of a [Host] or [Vm]. **Each Pe represents a virtual or physical processor core**
/// and its [capacity][#getCapacity()] is defined in
/// [MIPS (Million Instructions Per Second)](https://en.wikipedia.org/wiki/Instructions_per_second).
///
/// @author Manzur Murshed
/// @author Rajkumar Buyya
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.0
public interface Pe extends ChangeableId, ResourceManageable {
    /**
     * Status of PEs.
     */
    enum Status {
        /** Indicates the PE is FREE for allocation. */
        FREE,

        /** Indicates the PE is allocated and hence busy processing some Cloudlet. */
        BUSY,

        /**
         * Indicates the PE is failed and hence it can't process any Cloudlet at this moment.
         * This PE is failed because it belongs to a machine which is also failed.
         */
        FAILED
    }

    /**
     * An attribute that implements the Null Object Design Pattern for {@link Pe} objects.
     */
    Pe NULL = new PeNull();

    /// Gets the capacity of this Pe in [MIPS (Million Instructions Per Second)](https://en.wikipedia.org/wiki/Instructions_per_second).
    ///
    /// @return the MIPS capacity
    @Override
    long getCapacity();

    /// Sets the capacity of this Pe in [MIPS (Million Instructions Per Second)](https://en.wikipedia.org/wiki/Instructions_per_second).
    /// If you want to have an idea of the MIPS capacity for different processors, check the link above.
    ///
    /// @param mipsCapacity the MIPS capacity to set
    /// @return true if mipsCapacity is greater than 0, false otherwise
    @Override
    boolean setCapacity(long mipsCapacity);

    /// Sets the capacity of this Pe in [MIPS (Million Instructions Per Second)](https://en.wikipedia.org/wiki/Instructions_per_second).
    ///
    /// It receives the amount of MIPS as a `double` value but converts it internally to a `long`.
    /// The method is just provided as a handy way to define the PE
    /// capacity using a `double` value that usually is generated from some computations.
    /// If you want to have an idea of the MIPS capacity for different processors, check the link above.
    ///
    /// @param mipsCapacity the MIPS capacity to set
    /// @return true if `mipsCapacity` is greater than 0, false otherwise
    boolean setCapacity(double mipsCapacity);

    /// Sets the [#getPeProvisioner()] that manages the allocation
    /// of this physical PE to [Vm]s.
    /// This method is automatically called when a [PeProvisioner] is created
    /// passing a Pe instance. Thus, the PeProvisioner for a Pe doesn't have to be
    /// set manually.
    ///
    /// @param peProvisioner the new PE provisioner
    /// @return this instance
    Pe setPeProvisioner(PeProvisioner peProvisioner);

    /// Gets the [PeProvisioner] that manages the allocation
    /// of this physical PE to [Vm]s.
    ///
    /// @return the PE provisioner
    PeProvisioner getPeProvisioner();

    /**
     * @return the status of the PE.
     */
    Status getStatus();

    /// Sets the [status][#getStatus()] of the PE.
    /// @param status the new PE status
    Pe setStatus(Status status);

    /**
     * Checks if the PE is working (not failed).
     * @return true if the PE is working, false otherwise
     */
    boolean isWorking();

    /**
     * Checks if the PE is failed.
     * @return true if the PE is failed, false otherwise
     */
    boolean isFailed();

    /**
     * Checks if the PE is free to be used (it's idle).
     * @return true if the PE is free, false otherwise
     */
    boolean isFree();

    /**
     * Checks if the PE is busy to be used (it's being used).
     * @return true if the PE is busy, false otherwise
     */
    boolean isBusy();
}
