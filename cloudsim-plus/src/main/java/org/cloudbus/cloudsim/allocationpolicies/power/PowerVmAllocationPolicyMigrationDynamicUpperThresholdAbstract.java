package org.cloudbus.cloudsim.allocationpolicies.power;

import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.hosts.power.PowerHostUtilizationHistory;
import org.cloudbus.cloudsim.selectionpolicies.power.PowerVmSelectionPolicy;

import java.util.Objects;

/**
 * An abstract class that is the base for implementation of Power-aware VM allocation policies which use
 * a dynamic over utilization threshold.
 * <b>It's a Best Fit policy which selects the Host with most efficient power usage to place a given VM.</b>
 * Such a behaviour can be overridden by sub-classes.
 *
 * @author Manoel Campos da Silva Filho
 */
public abstract class PowerVmAllocationPolicyMigrationDynamicUpperThresholdAbstract extends PowerVmAllocationPolicyMigrationAbstract
    implements PowerVmAllocationPolicyMigrationDynamicUpperThreshold {

    /**
     * @see #getSafetyParameter()
     */
    private double safetyParameter;

    /**
     * @see #getFallbackVmAllocationPolicy()
     */
    private PowerVmAllocationPolicyMigration fallbackVmAllocationPolicy;

    /**
     * Creates a PowerVmAllocationPolicyMigrationDynamicUpperThreshold
     * with a {@link #getSafetyParameter() safety parameter} equals to 0
     * and no {@link #getFallbackVmAllocationPolicy() fallback policy}.
     *
     * @param vmSelectionPolicy the policy that defines how VMs are selected for migration
     */
    public PowerVmAllocationPolicyMigrationDynamicUpperThresholdAbstract(PowerVmSelectionPolicy vmSelectionPolicy) {
        this(vmSelectionPolicy, 0, PowerVmAllocationPolicyMigration.NULL);
    }

    /**
     * Creates a PowerVmAllocationPolicyMigrationDynamicUpperThreshold.
     *
     * @param vmSelectionPolicy          the policy that defines how VMs are selected for migration
     * @param safetyParameter            the safety parameter
     * @param fallbackVmAllocationPolicy the fallback VM allocation policy to be used when
     * the over utilization host detection doesn't have data to be computed
     */
    public PowerVmAllocationPolicyMigrationDynamicUpperThresholdAbstract(
        PowerVmSelectionPolicy vmSelectionPolicy,
        double safetyParameter,
        PowerVmAllocationPolicyMigration fallbackVmAllocationPolicy)
    {
        super(vmSelectionPolicy);
        setSafetyParameter(safetyParameter);
        setFallbackVmAllocationPolicy(fallbackVmAllocationPolicy);
    }

    /**
     * Checks if a host is over utilized based on the CPU over utilization threshold computed using
     * the statistical method defined in {@link #computeHostUtilizationMeasure(PowerHostUtilizationHistory)}.
     *
     * @param host {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean isHostOverloaded(PowerHost host) {
        if(getOverUtilizationThreshold(host) == Double.MAX_VALUE) {
            return getFallbackVmAllocationPolicy().isHostOverloaded(host);
        }

        return super.isHostOverloaded(host);
    }


    /**
     * Gets a dynamically computed Host over utilization threshold based on the
     * Host CPU utilization history.
     *
     * @param host {@inheritDoc}
     * @return {@inheritDoc} or {@link Double#MAX_VALUE} if the threshold could not be computed
     * (for instance, because the Host doesn't have enought history to use)
     * @see #computeHostUtilizationMeasure(PowerHostUtilizationHistory)
     */
    @Override
    public double getOverUtilizationThreshold(PowerHost host) {
        try {
            //@todo unchecked typecast
            return 1 - getSafetyParameter() * computeHostUtilizationMeasure((PowerHostUtilizationHistory) host);
        } catch (IllegalArgumentException | ClassCastException e) {
            return Double.MAX_VALUE;
        }
    }

    /**
     * Sets the safety parameter.
     *
     * @param safetyParameter the new safety parameter
     */
    protected final void setSafetyParameter(double safetyParameter) {
        if (safetyParameter < 0) {
            throw new IllegalArgumentException(
                "The safety parameter must be a positive value. It is a percentage value in scale from 0 to 1 where, for instance, 1 means 100% and 1.5 means 150%.");
        }

        this.safetyParameter = safetyParameter;
    }

    @Override
    public double getSafetyParameter() {
        return safetyParameter;
    }

    @Override
    public void setFallbackVmAllocationPolicy(PowerVmAllocationPolicyMigration fallbackPolicy) {
        this.fallbackVmAllocationPolicy = Objects.isNull(fallbackPolicy) ? PowerVmAllocationPolicyMigration.NULL : fallbackPolicy;
    }

    @Override
    public PowerVmAllocationPolicyMigration getFallbackVmAllocationPolicy() {
        return fallbackVmAllocationPolicy;
    }
}
