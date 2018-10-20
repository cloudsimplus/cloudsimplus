/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.allocationpolicies.migration;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.selectionpolicies.power.PowerVmSelectionPolicy;
import org.cloudbus.cloudsim.util.MathUtil;

/**
 * A VM allocation policy that uses Median Absolute Deviation (MAD) to compute
 * a dynamic threshold in order to detect host over utilization.
 * <b>It's a <b>Best Fit policy</b> which selects the Host with most efficient power usage to place a given VM.</b>
 *
 * <p>If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:
 * <ul>
 * <li><a href="https://doi.org/10.1002/cpe.1867">Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012</a>
 * </ul>
 * </p>
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 3.0
 */
public class VmAllocationPolicyMigrationMedianAbsoluteDeviation extends VmAllocationPolicyMigrationDynamicUpperThresholdFirstFit {
    /**
     * The minimum number of history entries required to compute
     * the Median Absolute Deviation (MAD).
     * 12 has been suggested as a safe value.
     */
    private static final int MIN_HISTORY_ENTRIES_FOR_MAD = 12;

    /**
     * Creates a VmAllocationPolicyMigrationMedianAbsoluteDeviation
     * with a {@link #getSafetyParameter() safety parameter} equals to 0
     * and no {@link #getFallbackVmAllocationPolicy() fallback policy}.
     *
     * @param vmSelectionPolicy the policy that defines how VMs are selected for migration
     */
    public VmAllocationPolicyMigrationMedianAbsoluteDeviation(final PowerVmSelectionPolicy vmSelectionPolicy) {
        super(vmSelectionPolicy);
    }

    /**
     * Creates a VmAllocationPolicyMigrationMedianAbsoluteDeviation.
     *
     * @param vmSelectionPolicy          the policy that defines how VMs are selected for migration
     * @param safetyParameter            the safety parameter
     * @param fallbackPolicy the fallback VM allocation policy to be used when
     * the over utilization host detection doesn't have data to be computed
     */
    public VmAllocationPolicyMigrationMedianAbsoluteDeviation(
        final PowerVmSelectionPolicy vmSelectionPolicy,
        final double safetyParameter,
        final VmAllocationPolicyMigration fallbackPolicy)
    {
        super(vmSelectionPolicy, safetyParameter, fallbackPolicy);
    }

    /**
     * Computes the host utilization MAD used for generating the host over utilization threshold.
     *
     * @param host the host
     * @return the host utilization MAD
     * @throws {@inheritDoc}
     */
    @Override
    public double computeHostUtilizationMeasure(final Host host) throws IllegalArgumentException {
        final double[] cpuUsageArray = getHostCpuUsageArray(host);
        if (MathUtil.countNonZeroBeginning(cpuUsageArray) >= MIN_HISTORY_ENTRIES_FOR_MAD) {
            return MathUtil.mad(cpuUsageArray);
        }

        throw new IllegalArgumentException("There is not enough Host history to compute Host utilization MAD");
    }

}
