/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.resources;

import org.cloudbus.cloudsim.core.ChangeableId;
import org.cloudbus.cloudsim.provisioners.PeProvisioner;

/**
 * A interface to be implemented by each class that provides
 * the basic features of a virtual or physical Processing Element (PE)
 * of a PM or VM. Each Pe represents a  virtual or physical processor core
 * and its {@link #getCapacity() capacity} is defined in
 * <a href="https://en.wikipedia.org/wiki/Instructions_per_second">MIPS (Million Instructions Per Second)</a>.
 *
 * @author Manzur Murshed
 * @author Rajkumar Buyya
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface Pe extends ChangeableId, ResourceManageable {
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
     * An attribute that implements the Null Object Design Pattern for {@link Pe}
     * objects.
     */
    Pe NULL = new PeNull();

    /**
     * Gets the capacity of this Pe in <a href="https://en.wikipedia.org/wiki/Instructions_per_second">MIPS (Million Instructions Per Second)</a>.
     *
     * @return the MIPS capacity
     */
    @Override
    long getCapacity();

    /**
     * Sets the capacity of this Pe in <a href="https://en.wikipedia.org/wiki/Instructions_per_second">MIPS (Million Instructions Per Second)</a>.
     * If you want to have an idea of the MIPS capacity for different processors, check the link above.
     *
     * @param mipsCapacity the MIPS capacity to set
     * @return true if mipsCapacity is greater than 0, false otherwise
     */
    @Override
    boolean setCapacity(long mipsCapacity);

    /**
     * Sets the capacity of this Pe in <a href="https://en.wikipedia.org/wiki/Instructions_per_second">MIPS (Million Instructions Per Second)</a>.
     *
     * <p>It receives the amount of MIPS as a double value but converts it internally
     * to a long. The method is just provided as a handy-way to define the PE
     * capacity using a double value that usually is generated from some computations.</p>
     *
     * If you want to have an idea of the MIPS capacity for different processors, check the link above.
     *
     * @param mipsCapacity the MIPS capacity to set
     * @return true if mipsCapacity is greater than 0, false otherwise
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
     * @return
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
     */
    Status getStatus();

    /**
     * Sets the {@link #getStatus() status} of the PE.
     *
     * @param status the new PE status
     * @return true if the status was set, false otherwise
     */
    boolean setStatus(Status status);

    /**
     * Checks if the PE is working (not failed).
     * @return
     */
    boolean isWorking();

    /**
     * Checks if the PE is failed.
     * @return
     */
    boolean isFailed();

    /**
     * Checks if the PE is free to be used (it's idle).
     * @return
     */
    boolean isFree();

    /**
     * Checks if the PE is busy to be used (it's being used).
     * @return
     */
    boolean isBusy();
}
