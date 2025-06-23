/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudsimplus.selectionpolicies;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.vms.Vm;

import java.util.Optional;

/**
 * An interface to be used to implement {@link Vm} selection policies for a list of migratable VMs
 * (VMs that can be migrated to another {@link Host}).
 * The selection policy is defined by implementing classes.
 *
 * @author Anton Beloglazov
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Toolkit 3.0
 */
public interface VmSelectionPolicy {
    VmSelectionPolicy NULL = new VmSelectionPolicyNull();

    /**
     * Gets a VM to migrate from a given Host.
     *
     * @param host the Host to get a Vm to migrate from
     * @return a {@link Optional} containing the selected vm to migrate;
     *         or empty Optional if there is not Vm to migrate
     */
    Optional<Vm> getVmToMigrate(Host host);
}
