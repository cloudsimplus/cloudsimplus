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

/// A [VmPlacementPolicy] that uses a Static CPU utilization Threshold (THR) to
/// detect host [under][#getUnderUtilizationThreshold()] and
/// [over][#getOverUtilizationThreshold(Host)] utilization.
///
/// It's a **Worst-Fit policy** which selects the Host having the least used amount of CPU
/// MIPS to place a given VM, **disregarding energy consumption**.
///
/// @author Anton Beloglazov
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.0
public class VmPlacementPolicyMigrationWorstFitStaticThreshold extends VmPlacementPolicyMigrationStaticThreshold {

    /// Creates a new VmPlacementPolicy, changing the [Function] to select a Host for a Vm.
    /// @param vmSelectionPolicy the [policy][VmPlacementPolicyMigration#getVmSelectionPolicy()]
    /// that defines how VMs are selected for migration
    /// @param overUtilizationThreshold [the over utilization threshold percent (between 0 and 1)][#setOverUtilizationThreshold(double)]
    public VmPlacementPolicyMigrationWorstFitStaticThreshold(
        final VmSelectionPolicy vmSelectionPolicy,
        final double overUtilizationThreshold)
    {
        super(vmSelectionPolicy, overUtilizationThreshold);
    }

    /// Creates a new VmPlacementPolicy, changing the [Function] to select a Host for a Vm.
    /// @param vmSelectionPolicy the [policy][VmPlacementPolicyMigration#getVmSelectionPolicy()]
    /// that defines how VMs are selected for migration
    /// @param overUtilizationThreshold [the over utilization threshold percent (between 0 and 1)][#setOverUtilizationThreshold(double)]
    /// @param findHostForVmFunction a [Function] to select a Host for a given Vm.
    /// Passing null makes the Function to be set as the default [#findHostForVm(Vm)].
    /// @see VmPlacementPolicy#setFindHostForVmFunction(java.util.function.BiFunction)
    public VmPlacementPolicyMigrationWorstFitStaticThreshold(
        final VmSelectionPolicy vmSelectionPolicy,
        final double overUtilizationThreshold,
        @Nullable final BiFunction<VmPlacementPolicy, Vm, Optional<Host>> findHostForVmFunction)
    {
        super(vmSelectionPolicy, overUtilizationThreshold, findHostForVmFunction);
    }

    /// Gets the Host having the most available MIPS capacity (min used MIPS).
    ///
    /// This method is ignoring the additional filtering performed by the super class.
    /// This way, Host selection is performed ignoring energy consumption.
    /// However, all the basic filters defined in the super class are ensured, since
    /// this method is called just after they are applied.
    ///
    /// {@inheritDoc}
    @Override
    protected Optional<Host> findHostForVmInternal(final Vm vm, final Predicate<Host> predicate) {
        /*It's ignoring the super class to intentionally avoid the additional filtering performed there
        * and to apply a different method to select the Host to place the VM.*/
        return getHostList().stream().filter(predicate).min(comparingDouble(Host::getCpuMipsUtilization));
    }
}
