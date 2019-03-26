/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.allocationpolicies.migration;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.selectionpolicies.VmSelectionPolicy;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A VM allocation policy that uses a static CPU utilization threshold to detect
 * host over utilization.
 * <b>It's a First Fit policy which selects the first Host found with most efficient power usage to place a given VM.</b>
 *
 * <p>If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:
 * <ul>
 * <li><a href="https://doi.org/10.1002/cpe.1867">Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley and Sons, Ltd, New York, USA, 2012</a></li>
 * </ul>
 * </p>
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 3.0
 */
public class VmAllocationPolicyMigrationStaticThreshold extends VmAllocationPolicyMigrationAbstract {
    public static final double DEF_OVER_UTILIZATION_THRESHOLD = 0.9;

    /**
     * @see #getOverUtilizationThreshold(Host)
     */
    private double overUtilizationThreshold;

    /**
     * Creates a VmAllocationPolicyMigrationStaticThreshold.
     * It uses a {@link #DEF_OVER_UTILIZATION_THRESHOLD default over utilization threshold}
     * and a {@link #DEF_UNDER_UTILIZATION_THRESHOLD default under utilization threshold}.
     *
     * @param vmSelectionPolicy the policy that defines how VMs are selected for migration
     * @see #setUnderUtilizationThreshold(double)
     * @see #setOverUtilizationThreshold(double)
     */
    public VmAllocationPolicyMigrationStaticThreshold(final VmSelectionPolicy vmSelectionPolicy)
    {
        this(vmSelectionPolicy, DEF_OVER_UTILIZATION_THRESHOLD, null);
    }

    /**
     * Creates a VmAllocationPolicyMigrationStaticThreshold.
     *
     * @param vmSelectionPolicy the policy that defines how VMs are selected for migration
     * @param overUtilizationThreshold the over utilization threshold
     */
    public VmAllocationPolicyMigrationStaticThreshold(
        final VmSelectionPolicy vmSelectionPolicy,
        final double overUtilizationThreshold)
    {
        this(vmSelectionPolicy, overUtilizationThreshold, null);
    }

    /**
     * Creates a VmAllocationPolicyMigrationStaticThreshold, changing the {@link Function} to select a Host for a Vm.
     * @param vmSelectionPolicy the policy that defines how VMs are selected for migration
     * @param overUtilizationThreshold the over utilization threshold
     * @param findHostForVmFunction a {@link Function} to select a Host for a given Vm.
     *                              Passing null makes the Function to be set as the default {@link #findHostForVm(Vm)}.
     * @see VmAllocationPolicy#setFindHostForVmFunction(java.util.function.BiFunction)
     */
    public VmAllocationPolicyMigrationStaticThreshold(
        final VmSelectionPolicy vmSelectionPolicy,
        final double overUtilizationThreshold,
        final BiFunction<VmAllocationPolicy, Vm, Optional<Host>> findHostForVmFunction)
    {
        super(vmSelectionPolicy, findHostForVmFunction);
        setOverUtilizationThreshold(overUtilizationThreshold);
    }

    /**
     * Sets the static host CPU utilization threshold to detect over
     * utilization.
     *
     * @param overUtilizationThreshold the overUtilizationThreshold to set
     */
    public final void setOverUtilizationThreshold(final double overUtilizationThreshold) {
        if(overUtilizationThreshold <= 0 || overUtilizationThreshold >= 1){
            throw new IllegalArgumentException("Over utilization threshold must be greater than 0 and lower than 1.");
        }

        this.overUtilizationThreshold = overUtilizationThreshold;
    }

    /**
     * Gets the static host CPU utilization threshold to detect over
     * utilization. It is a percentage value from 0 to 1 that can be changed
     * when creating an instance of the class.
     *
     * <p>
     * <b>This implementation always returns the same over utilization threshold for any
     * given host</b></p>
     *
     * @param host {@inheritDoc}
     * @return {@inheritDoc} (that is the same for any given host)
     */
    @Override
    public double getOverUtilizationThreshold(final Host host) {
        return overUtilizationThreshold;
    }

}
