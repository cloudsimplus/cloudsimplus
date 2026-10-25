/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.placementpolicies.migration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.cloudsimplus.placementpolicies.VmPlacementPolicy;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.selectionpolicies.VmSelectionPolicy;
import org.cloudsimplus.vms.Vm;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jspecify.annotations.Nullable;

/// A [VmPlacementPolicy] that uses a Static CPU utilization Threshold (THR) to
/// detect host [under][#getUnderUtilizationThreshold()] and
/// [over utilization][#getOverUtilizationThreshold(Host)].
///
/// It's a **First Fit policy** which selects the first Host having enough capacity for a given VM,
/// **disregarding energy consumption**.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 7.2.0
public class VmPlacementPolicyMigrationFirstFitStaticThreshold extends VmPlacementPolicyMigrationStaticThreshold {
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private int lastHostIndex;

    /// Creates a VmPlacementPolicy.
    /// It uses a [default over utilization threshold][#DEF_OVER_UTILIZATION_THRESHOLD]
    /// and a [default under utilization threshold][#DEF_UNDER_UTILIZATION_THRESHOLD].
    ///
    /// @param vmSelectionPolicy the [policy][VmPlacementPolicyMigration#getVmSelectionPolicy()]
    /// that defines how VMs are selected for migration
    /// @see #setUnderUtilizationThreshold(double)
    /// @see #setOverUtilizationThreshold(double)
    public VmPlacementPolicyMigrationFirstFitStaticThreshold(final VmSelectionPolicy vmSelectionPolicy) {
        this(vmSelectionPolicy, DEF_OVER_UTILIZATION_THRESHOLD);
    }

    /// Creates a VmPlacementPolicy with a given over utilization threshold.
    ///
    /// @param vmSelectionPolicy the [policy][VmPlacementPolicyMigration#getVmSelectionPolicy()]
    /// that defines how VMs are selected for migration
    /// @param overUtilizationThreshold [the over utilization threshold percent (between 0 and 1)][#setOverUtilizationThreshold(double)]
    /// @see #setUnderUtilizationThreshold(double)
    /// @see #setOverUtilizationThreshold(double)
    public VmPlacementPolicyMigrationFirstFitStaticThreshold(
        final VmSelectionPolicy vmSelectionPolicy,
        final double overUtilizationThreshold)
    {
        this(vmSelectionPolicy, overUtilizationThreshold, null);
    }

    /// Creates a VmPlacementPolicy, changing the [Function] to select a Host for a Vm.
    /// @param vmSelectionPolicy the policy that defines how VMs are selected for migration
    /// @param overUtilizationThreshold [the over utilization threshold percent (between 0 and 1)][#setOverUtilizationThreshold(double)]
    /// @param findHostForVmFunction a [Function] to select a Host for a given Vm.
    /// Passing null makes the Function to be set as the default [#findHostForVm(Vm)].
    /// @see VmPlacementPolicy#setFindHostForVmFunction(BiFunction)
    public VmPlacementPolicyMigrationFirstFitStaticThreshold(
        final VmSelectionPolicy vmSelectionPolicy,
        final double overUtilizationThreshold,
        @Nullable final BiFunction<VmPlacementPolicy, Vm, Optional<Host>> findHostForVmFunction)
    {
        super(vmSelectionPolicy, overUtilizationThreshold, findHostForVmFunction);
    }

    /// Gets the first Host having enough capacity to place the VM.
    ///
    /// This method is ignoring the additional filtering performed by the super class.
    /// This way, Host selection is performed ignoring energy consumption.
    /// However, all the basic filters defined in the super class are ensured, since
    /// this method is called just after they are applied.
    ///
    /// @param vm {@inheritDoc}
    /// @param predicate {@inheritDoc}
    /// @return {@inheritDoc}
    @Override
    protected Optional<Host> findHostForVmInternal(final Vm vm, final Predicate<Host> predicate) {
        /*It's ignoring the super class intentionally to avoid the additional filtering performed there
        * and to apply a different method to select the Host to place the VM.*/

        final List<Host> hostList = getHostList();
        /* The for loop just defines the maximum number of Hosts to try.
         * When a suitable Host is found, the method returns immediately. */
        final int maxTries = hostList.size();
        for (int i = 0; i < maxTries; i++) {
            final Host host = hostList.get(lastHostIndex);
            if (predicate.test(host)) {
                return Optional.of(host);
            }

            lastHostIndex = ++lastHostIndex % getHostList().size();
        }

        return Optional.empty();
    }
}
