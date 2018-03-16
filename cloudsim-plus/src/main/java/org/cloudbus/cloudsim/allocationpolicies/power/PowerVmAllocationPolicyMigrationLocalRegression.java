/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.allocationpolicies.power;

import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.hosts.power.PowerHostUtilizationHistory;
import org.cloudbus.cloudsim.resources.Resource;
import org.cloudbus.cloudsim.selectionpolicies.power.PowerVmSelectionPolicy;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.util.MathUtil;

/**
 * A VM allocation policy that uses Local Regression (LR) to predict host utilization (load)
 * and define if a host is overloaded or not.
 * <b>It's a Best Fit policy which selects the Host with most efficient power usage to place a given VM.</b>
 * Such a behaviour can be overridden by sub-classes.
 *
 * <p>If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:
 * <ul>
 * <li><a href="http://dx.doi.org/10.1002/cpe.1867">Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012</a>
 * </ul>
 * </p>
 *
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 3.0
 */
public class PowerVmAllocationPolicyMigrationLocalRegression extends PowerVmAllocationPolicyMigrationDynamicUpperThresholdAbstract{

    /**
     * @see #getSchedulingInterval()
     */
    private double schedulingInterval;

    /**
     * Creates a PowerVmAllocationPolicyMigrationLocalRegression
     * with a {@link #getSafetyParameter() safety parameter} equals to 0
     * and no {@link #getFallbackVmAllocationPolicy() fallback policy}.
     *
     * @param vmSelectionPolicy the policy that defines how VMs are selected for migration
     */
    public PowerVmAllocationPolicyMigrationLocalRegression(PowerVmSelectionPolicy vmSelectionPolicy) {
        super(vmSelectionPolicy);
    }

    /**
     * Creates a PowerVmAllocationPolicyMigrationLocalRegression.
     *
     * @param vmSelectionPolicy          the policy that defines how VMs are selected for migration
     * @param safetyParameter            the safety parameter
     * @param fallbackVmAllocationPolicy the fallback VM allocation policy to be used when
     * the over utilization host detection doesn't have data to be computed
     */
    public PowerVmAllocationPolicyMigrationLocalRegression(
        PowerVmSelectionPolicy vmSelectionPolicy,
        double safetyParameter,
        PowerVmAllocationPolicyMigration fallbackVmAllocationPolicy)
    {
        super(vmSelectionPolicy, safetyParameter, fallbackVmAllocationPolicy);
    }

    /**
     * Checks if a host is over utilized based on estimation of CPU over utilization threshold computed
     * using Local Regression.
     *
     * @param host the host
     * @return true, if is host over utilized; false otherwise
     */
    @Override
    public boolean isHostOverloaded(PowerHost host) {
        final double predictedUsageThreshold = getOverUtilizationThreshold(host);
        if(predictedUsageThreshold == Double.MAX_VALUE){
            return getFallbackVmAllocationPolicy().isHostOverloaded(host);
        }

        addHistoryEntryIfAbsent(host, predictedUsageThreshold);
        return predictedUsageThreshold >= 1;
    }

    /**
     * {@inheritDoc}.
     * <b>In this case, this is a predicted value based on Local Regression of the utilization history.</b>
     * @param host the host to get the over utilization threshold <b>prediction</b>
     * @return {@inheritDoc} or {@link Double#MAX_VALUE} if the threshold could not be computed
     */
    @Override
    public double getOverUtilizationThreshold(PowerHost host) {
        try {
            //@todo uncheck typecast
            final double predictedUtilization = computeHostUtilizationMeasure((PowerHostUtilizationHistory) host);
            return predictedUtilization * getSafetyParameter();
        } catch (IllegalArgumentException | ClassCastException e) {
            return Double.MAX_VALUE;
        }
    }

    /**
     * Computes a Local Regression of the host utilization history to <b>estimate</b> the current host utilization.
     * Such a value is used to generate the host over utilization threshold.
     *
     * @param host the host
     * @return the host utilization Local Regression
     * @throws {@inheritDoc}
     */
    @Override
    public double computeHostUtilizationMeasure(PowerHostUtilizationHistory host) throws IllegalArgumentException{
        double[] utilizationHistory = host.getUtilizationHistory();
        final int length = 10; // we use 10 to make the regression responsive enough to latest values
        if (utilizationHistory.length < length) {
            throw new IllegalArgumentException("There is not enough Host history to estimate its utilization using Local Regression");
        }

        double[] utilizationHistoryReversed = new double[length];
        for (int i = 0; i < length; i++) {
            utilizationHistoryReversed[i] = utilizationHistory[length - i - 1];
        }
        double[] estimates = getParameterEstimates(utilizationHistoryReversed);
        double migrationIntervals = Math.ceil(getMaximumVmMigrationTime(host) / getSchedulingInterval());
        return estimates[0] + estimates[1] * (length + migrationIntervals);
    }

    /**
     * Gets utilization estimates.
     *
     * @param utilizationHistoryReversed the utilization history in reverse order
     * @return the utilization estimates
     */
    protected double[] getParameterEstimates(double[] utilizationHistoryReversed) {
        return MathUtil.getLoessParameterEstimates(utilizationHistoryReversed);
    }

    /**
     * Gets the maximum vm migration time.
     *
     * @param host the host
     * @return the maximum vm migration time
     */
    protected double getMaximumVmMigrationTime(PowerHost host) {
        //@todo It must compute the migration time based on the current RAM usage, not the capacity.
        final double maxRam = host.getVmList().stream()
            .map(Vm::getRam)
            .mapToDouble(Resource::getCapacity).max().orElse(0);
        return maxRam / (host.getBw().getCapacity() / (2 * 8));
    }

    /**
     * Sets the scheduling interval that defines the periodicity of VM migrations.
     *
     * @param schedulingInterval the new scheduling interval
     * @return
     */
    public final PowerVmAllocationPolicyMigrationLocalRegression setSchedulingInterval(double schedulingInterval) {
        this.schedulingInterval = schedulingInterval;
        return this;
    }

    /**
     * Gets the scheduling interval that defines the periodicity of VM migrations.
     *
     * @return the scheduling interval
     */
    public double getSchedulingInterval() {
        return schedulingInterval;
    }

}
