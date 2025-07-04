/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.provisioners;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.ResourceManageable;
import org.cloudsimplus.vms.Vm;

/// A best-effort [PeProvisioner] policy used by a [Host] to provide its physical [Pe]s as virtual PEs to [Vm]s:
///
/// - if there are MIPS available on the physical PE, it allocates to a virtual PE;
/// - otherwise, it fails.
///
/// Each Host's PE must have its own instance of a PeProvisioner. When extending this class,
/// you must ensure that the field `availableMips` will always
/// contain the amount of free MIPS available for future allocations.
///
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 2.0
public class PeProvisionerSimple extends PeProvisionerAbstract {

    /**
     * Instantiates a new PeProvisionerSimple. The {@link Pe} it will manage will be set
     * just at the Pe instantiation.
     */
    public PeProvisionerSimple() {
        super(Pe.NULL, vm -> ResourceManageable.NULL);
    }

    /**
     * Instantiates a new PeProvisionerSimple for a given {@link Pe}.
     *
     * @param pe the PE that will be managed by the provisioner
     */
    public PeProvisionerSimple(final Pe pe){
        super(pe, Vm::getProcessor);
        pe.setPeProvisioner(this);
    }

    @Override
    public double getUtilization() {
        return getTotalAllocatedResource() / (double)getCapacity();
    }
}
