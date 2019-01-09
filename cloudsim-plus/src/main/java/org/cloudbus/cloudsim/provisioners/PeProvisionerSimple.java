/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.provisioners;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.Pe;

import java.util.Objects;

/**
 * A best-effort {@link PeProvisioner} policy used by a {@link Host} to provide virtual PEs to VMs from its physical PEs:
 * <ul>
 *   <li>if there is available MIPS on the physical PE, it allocates to a virtual PE;</li>
 *   <li>otherwise, it fails.</li>
 * </ul>
 *
 * <p>
 * Each host's PE must have its own instance of a PeProvisioner. When extending this class,
 * care must be taken to guarantee that the field availableMips will always
 * contain the amount of free MIPS available for future allocations.
 * </p>
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 2.0
 */
public class PeProvisionerSimple extends ResourceProvisionerSimple implements PeProvisioner {

    /**
     * Instantiates a new PeProvisionerSimple. The {@link Pe} it will manage will be set
     * just at Pe instantiation.
     */
    public PeProvisionerSimple() {
        super(Pe.NULL);
    }

    /**
     * Instantiates a new PeProvisionerSimple for a given {@link Pe}.
     *
     * @param pe
     */
    public PeProvisionerSimple(Pe pe){
        super(pe);
        pe.setPeProvisioner(this);
    }

    @Override
    public void setPe(Pe pe){
        if(isOtherProvisionerAssignedToPe(pe)){
            throw new IllegalArgumentException("Pe already has a PeProvisioner assigned to it. Each Pe must have its own PeProvisioner instance.");
        }
        setResource(pe);
    }

    @Override
    public double getUtilization() {
        return getTotalAllocatedResource() / (double)getCapacity();
    }

    /**
     * Checks if the {@link Pe} has a {@link PeProvisioner} assigned that is
     * different from the current one.
     *
     * @param pe the Pe to check
     * @return
     */
    private boolean isOtherProvisionerAssignedToPe(final Pe pe) {
        Objects.requireNonNull(pe);
        return pe.getPeProvisioner() != null &&
               pe.getPeProvisioner() != PeProvisioner.NULL &&
               !pe.getPeProvisioner().equals(this);
    }
}
