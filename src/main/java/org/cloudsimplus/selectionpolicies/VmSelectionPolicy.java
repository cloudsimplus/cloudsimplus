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
import java.util.function.Predicate;

/// An interface to be used to implement [Vm] selection policies for a list of migratable VMs
/// (VMs that can be migrated to another [Host]).
/// The selection policy is defined by implementing classes.
///
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Toolkit 3.0
public interface VmSelectionPolicy {
    VmSelectionPolicy NULL = new VmSelectionPolicyNull();

    /// Gets a VM to migrate from a given Host.
    ///
    /// @param host the Host to get a Vm to migrate from
    /// @return a [Optional] containing the selected vm to migrate;
    /// or empty Optional if there is not Vm to migrate
    Optional<Vm> getVmToMigrate(Host host);

    /// Gets a VM to migrate from a given Host, among the VMs accepted by a filter.
    /// It's used to select more VMs from the same Host, ignoring the ones already selected.
    ///
    /// The default implementation returns the VM selected by [#getVmToMigrate(Host)]
    /// only if the filter accepts it.
    /// Implementations should override it to select among the accepted VMs only.
    ///
    /// @param host the Host to get a Vm to migrate from
    /// @param filter a [Predicate] that accepts the VMs that can be selected
    /// @return a [Optional] containing the selected vm to migrate;
    /// or empty Optional if there is no accepted Vm to migrate
    default Optional<Vm> getVmToMigrate(final Host host, final Predicate<Vm> filter) {
        return getVmToMigrate(host).filter(filter);
    }
}
