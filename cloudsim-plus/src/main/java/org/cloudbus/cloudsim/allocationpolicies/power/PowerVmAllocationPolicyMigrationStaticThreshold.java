/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.allocationpolicies.power;

import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.selectionpolicies.power.PowerVmSelectionPolicy;

/**
 * A VM allocation policy that uses a static CPU utilization threshold to detect
 * host over utilization.
 * <b>It's a First Fit policy which selects the first found Host with most efficient power usage to place a given VM.</b>
 *
 * <p>
 * If you are using any algorithms, policies or workload included in the power
 * package please cite the following paper:
 * <ul>
 * <li><a href="http://dx.doi.org/10.1002/cpe.1867">Anton Beloglazov, and
 * Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of
 * Virtual Machines in Cloud Data Centers", Concurrency and Computation:
 * Practice and Experience (CCPE), Volume 24, Issue 13, Pages: 1397-1420, John
 * Wiley & Sons, Ltd, New York, USA, 2012</a>
 * </ul>
 * </p>
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 3.0
 */
public class PowerVmAllocationPolicyMigrationStaticThreshold extends PowerVmAllocationPolicyMigrationAbstract {

    /**
     * @see #getOverUtilizationThreshold(PowerHost)
     */
    private double overUtilizationThreshold = 0.9;

    /**
     * Creates a PowerVmAllocationPolicyMigrationStaticThreshold.
     *
     * @param vmSelectionPolicy the policy that defines how VMs are selected for
     * migration
     * @param overUtilizationThreshold the over utilization threshold
     */
    public PowerVmAllocationPolicyMigrationStaticThreshold(
            PowerVmSelectionPolicy vmSelectionPolicy,
            double overUtilizationThreshold) {
        super(vmSelectionPolicy);
        setOverUtilizationThreshold(overUtilizationThreshold);
    }

    /**
     * Sets the static host CPU utilization threshold to detect over
     * utilization.
     *
     * @param overUtilizationThreshold the overUtilizationThreshold to set
     */
    public final void setOverUtilizationThreshold(double overUtilizationThreshold) {
        this.overUtilizationThreshold = overUtilizationThreshold;
    }

    /**
     * Gets the static host CPU utilization threshold to detect over
     * utilization. It is a percentage value from 0 to 1 that can be changed
     * when creating an instance of the class.
     *
     * <p>
     * <b>This method always return the same over utilization threshold for any
     * given host</b></p>
     *
     * @param host {@inheritDoc}
     * @return {@inheritDoc} (that is the same for any given host)
     */
    @Override
    public double getOverUtilizationThreshold(PowerHost host) {
        return overUtilizationThreshold;
    }

}
