/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.allocationpolicies.migration;

import org.cloudbus.cloudsim.selectionpolicies.power.PowerVmSelectionPolicy;
import org.cloudbus.cloudsim.util.MathUtil;

/**
 * A VM allocation policy that uses Local Regression Robust (LRR) to predict host utilization (load)
 * and define if a host is overloaded or not.
 * <b>It's a Best Fit policy which selects the Host with most efficient power usage to place a given VM.</b>
 *
 * <p>If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:</p>
 *
 * <ul>
 * <li><a href="https://doi.org/10.1002/cpe.1867">Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012</a>
 * </ul>
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 3.0
 */
public class VmAllocationPolicyMigrationLocalRegressionRobust extends VmAllocationPolicyMigrationLocalRegression {

    /**
     * Creates a VmAllocationPolicyMigrationLocalRegressionRobust
     * with a {@link #getSafetyParameter() safety parameter} equals to 0
     * and no {@link #getFallbackVmAllocationPolicy() fallback policy}.
     *
     * @param vmSelectionPolicy the policy that defines how VMs are selected for migration
     */
    public VmAllocationPolicyMigrationLocalRegressionRobust(final PowerVmSelectionPolicy vmSelectionPolicy) {
        super(vmSelectionPolicy);
    }

    /**
     * Creates a VmAllocationPolicyMigrationLocalRegressionRobust.
     *
     * @param vmSelectionPolicy          the policy that defines how VMs are selected for migration
     * @param safetyParameter            the safety parameter
     * @param fallbackVmAllocationPolicy the fallback VM allocation policy to be used when
     * the over utilization host detection doesn't have data to be computed
     */
    public VmAllocationPolicyMigrationLocalRegressionRobust(
        final PowerVmSelectionPolicy vmSelectionPolicy,
        final double safetyParameter,
        final VmAllocationPolicyMigration fallbackVmAllocationPolicy)
    {
        super(vmSelectionPolicy, safetyParameter, fallbackVmAllocationPolicy);
    }

    /**
	 * Gets the utilization estimates.
	 *
	 * @param reversedUsageHistory the utilization history in reverse order
	 * @return the utilization estimates
	 */
	@Override
	protected double[] getParameterEstimates(final double[] reversedUsageHistory) {
		return MathUtil.getRobustLoessParameterEstimates(reversedUsageHistory);
	}

}
