/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.placementpolicies.migration;

import org.cloudsimplus.placementpolicies.VmPlacementPolicy;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.selectionpolicies.VmSelectionPolicy;
import org.cloudsimplus.vms.Vm;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Comparator.comparingDouble;
import org.jspecify.annotations.Nullable;

/// A [VmPlacementPolicyMigration] that uses a Static CPU utilization Threshold (THR) to
/// detect host [under][#getUnderUtilizationThreshold()] and
/// [over][#getOverUtilizationThreshold(Host)] utilization.
///
/// It's a **Best Fit policy** which selects the Host having the most used amount of CPU
/// MIPS to place a given VM, **disregarding energy consumption**.
///
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.0
public class VmPlacementPolicyMigrationBestFitStaticThreshold extends VmPlacementPolicyMigrationStaticThreshold {

    /// Creates a VmPlacementPolicy.
    /// It uses a [default over utilization threshold][#DEF_OVER_UTILIZATION_THRESHOLD]
    /// and a [default under utilization threshold][#DEF_UNDER_UTILIZATION_THRESHOLD].
    ///
    /// @param vmSelectionPolicy the [policy][VmPlacementPolicyMigration#getVmSelectionPolicy()]
    /// that defines how VMs are selected for migration
    /// @see #setUnderUtilizationThreshold(double)
    /// @see #setOverUtilizationThreshold(double)
    public VmPlacementPolicyMigrationBestFitStaticThreshold(final VmSelectionPolicy vmSelectionPolicy) {
        this(vmSelectionPolicy, DEF_OVER_UTILIZATION_THRESHOLD);
    }

    /// Creates a VmPlacementPolicy with a given over utilization threshold.
    ///
    /// @param vmSelectionPolicy the [policy][VmPlacementPolicyMigration#getVmSelectionPolicy()]
    /// that defines how VMs are selected for migration
    /// @param overUtilizationThreshold [the over utilization threshold percent (between 0 and 1)][#setOverUtilizationThreshold(double)]
    public VmPlacementPolicyMigrationBestFitStaticThreshold(
        final VmSelectionPolicy vmSelectionPolicy,
        final double overUtilizationThreshold)
    {
        this(vmSelectionPolicy, overUtilizationThreshold, null);
    }

    /// Creates a new VmPlacementPolicy, changing the [Function] to select a Host for a Vm.
    /// @param vmSelectionPolicy vmSelectionPolicy the [policy][VmPlacementPolicyMigration#getVmSelectionPolicy()]
    /// that defines how VMs are selected for migration
    /// @param overUtilizationThreshold [the over utilization threshold percent (between 0 and 1)][#setOverUtilizationThreshold(double)]
    /// @param findHostForVmFunction a [Function] to select a Host for a given Vm.
    /// Passing null makes the Function to be set as the default [#findHostForVm(Vm)].
    /// @see VmPlacementPolicy#setFindHostForVmFunction(java.util.function.BiFunction)
    public VmPlacementPolicyMigrationBestFitStaticThreshold(
        final VmSelectionPolicy vmSelectionPolicy,
        final double overUtilizationThreshold,
        @Nullable final BiFunction<VmPlacementPolicy, Vm, Optional<Host>> findHostForVmFunction)
    {
        super(vmSelectionPolicy, overUtilizationThreshold, findHostForVmFunction);
    }

    /// Gets the Host having the least available MIPS capacity (max used MIPS).
    ///
    /// This method doesn't apply the additional filters from the super class.
    /// This way, Host selection is performed ignoring energy consumption.
    /// However, all the basic filters defined in the super class are ensured, since
    /// this method is called just after they are applied.
    ///
    /// @param vm {@inheritDoc}
    /// @param predicate {@inheritDoc}
    /// @return {@inheritDoc}
    @Override
    protected Optional<Host> findHostForVmInternal(final Vm vm, final Predicate<Host> predicate) {
        /* It's ignoring the super class intentionally to avoid the additional filtering performed there
         * and to apply a different method to select the Host to place the VM.*/
        return getHostList().stream().filter(predicate).max(comparingDouble(Host::getCpuMipsUtilization));
    }
}
