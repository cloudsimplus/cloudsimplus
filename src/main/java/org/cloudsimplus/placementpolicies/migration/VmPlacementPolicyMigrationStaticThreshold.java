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
import org.cloudsimplus.placementpolicies.VmPlacementPolicy;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.selectionpolicies.VmSelectionPolicy;
import org.cloudsimplus.vms.Vm;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;

/// A VM placement policy that uses a static CPU utilization threshold to detect
/// host over utilization.
/// **It's a First Fit policy which selects the first Host found with most efficient power usage to place a given VM.**
///
/// @author Anton Beloglazov
/// @since CloudSim Toolkit 3.0
public class VmPlacementPolicyMigrationStaticThreshold extends VmPlacementPolicyMigrationAbstract {
    public static final double DEF_OVER_UTILIZATION_THRESHOLD = 0.9;

    /// @see #getOverUtilizationThreshold(Host)
    @Getter(AccessLevel.NONE)
    private double overUtilizationThreshold;

    /// Creates a VmPlacementPolicy.
    /// It uses a [default over utilization threshold][#DEF_OVER_UTILIZATION_THRESHOLD]
    /// and a [default under utilization threshold][#DEF_UNDER_UTILIZATION_THRESHOLD].
    ///
    /// @param vmSelectionPolicy the [policy][VmPlacementPolicyMigration#getVmSelectionPolicy()]
    /// that defines how VMs are selected for migration
    /// @see #setUnderUtilizationThreshold(double)
    /// @see #setOverUtilizationThreshold(double)
    public VmPlacementPolicyMigrationStaticThreshold(final VmSelectionPolicy vmSelectionPolicy) {
        this(vmSelectionPolicy, DEF_OVER_UTILIZATION_THRESHOLD, null);
    }

    /// Creates a VmPlacementPolicy.
    ///
    /// @param vmSelectionPolicy the [policy][VmPlacementPolicyMigration#getVmSelectionPolicy()]
    /// that defines how VMs are selected for migration
    /// @param overUtilizationThreshold [the over utilization threshold percent (between 0 and 1)][#setOverUtilizationThreshold(double)]
    public VmPlacementPolicyMigrationStaticThreshold(
        final VmSelectionPolicy vmSelectionPolicy,
        final double overUtilizationThreshold)
    {
        this(vmSelectionPolicy, overUtilizationThreshold, null);
    }

    /// Creates a VmPlacementPolicy, changing the [Function] to select a Host for a Vm.
    /// @param vmSelectionPolicy the [policy][VmPlacementPolicyMigration#getVmSelectionPolicy()]
    /// that defines how VMs are selected for migration
    /// @param overUtilizationThreshold [the over utilization threshold percent (between 0 and 1)][#setOverUtilizationThreshold(double)]
    /// @param findHostForVmFunction a [Function] to select a Host for a given Vm.
    /// Passing null makes the Function to be set as the default [#findHostForVm(Vm)].
    /// @see VmPlacementPolicy#setFindHostForVmFunction(java.util.function.BiFunction)
    public VmPlacementPolicyMigrationStaticThreshold(
        final VmSelectionPolicy vmSelectionPolicy,
        final double overUtilizationThreshold,
        @Nullable final BiFunction<VmPlacementPolicy, Vm, Optional<Host>> findHostForVmFunction)
    {
        super(vmSelectionPolicy, findHostForVmFunction);
        setOverUtilizationThreshold(overUtilizationThreshold);
    }

    /// Sets the static host CPU utilization threshold to detect over utilization.
    ///
    /// @param overUtilizationThreshold a percentage value between 0 and 1
    public final void setOverUtilizationThreshold(final double overUtilizationThreshold) {
        if(overUtilizationThreshold <= 0 || overUtilizationThreshold >= 1){
            throw new IllegalArgumentException("Over utilization threshold must be greater than 0 and lower than 1.");
        }

        this.overUtilizationThreshold = overUtilizationThreshold;
    }

    /// Gets the static host CPU utilization threshold to detect over
    /// utilization. It is a percentage value from 0 to 1 that can be changed
    /// when creating an instance of the class.
    ///
    /// **This implementation always returns the same over utilization threshold for any given host.**
    ///
    /// @param host {@inheritDoc}
    /// @return {@inheritDoc} (that is the same for any given host)
    @Override
    public double getOverUtilizationThreshold(final Host host) {
        return overUtilizationThreshold;
    }

}
