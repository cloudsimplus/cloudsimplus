package org.cloudbus.cloudsim.allocationpolicies.power;

import org.cloudbus.cloudsim.hosts.power.PowerHost;
import org.cloudbus.cloudsim.hosts.power.PowerHostUtilizationHistory;
import org.cloudbus.cloudsim.selectionpolicies.power.PowerVmSelectionPolicy;

/**
 * An abstract class that is the base for implementation of Power-aware VM allocation policies that use
 * a dynamic over utilization threshold.
 *
 * @author Manoel Campos da Silva Filho
 */
public abstract class PowerVmAllocationPolicyMigrationDynamicUpperThresholdAbstract extends PowerVmAllocationPolicyMigrationAbstract
    implements PowerVmAllocationPolicyMigrationDynamicUpperThreshold {

    /**
     * @see #getSafetyParameter()
     */
    private double safetyParameter = 0;

    /**
     * @see #getFallbackVmAllocationPolicy()
     */
    private PowerVmAllocationPolicyMigration fallbackVmAllocationPolicy;

    /**
     * Creates a PowerVmAllocationPolicyMigration.
     *
     * @param vmSelectionPolicy the policy that defines how VMs are selected for migration
     */
    public PowerVmAllocationPolicyMigrationDynamicUpperThresholdAbstract(PowerVmSelectionPolicy vmSelectionPolicy) {
        super(vmSelectionPolicy);
        this.fallbackVmAllocationPolicy = PowerVmAllocationPolicyMigration.NULL;
    }

    /**
     * Checks if a host is over utilized based on the CPU over utilization threshold computed using
     * the statistical method defined in {@link #computeHostUtilizationMeasure(PowerHostUtilizationHistory)}.
     *
     * @param host the host
     * @return true, if the host is over utilized; false otherwise
     */
    @Override
    public boolean isHostOverUtilized(PowerHost host) {
        if(getOverUtilizationThreshold(host) == Double.MAX_VALUE) {
            return getFallbackVmAllocationPolicy().isHostOverUtilized(host);
        }

        return super.isHostOverUtilized(host);
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
                "The safety parameter cannot be less than zero.");
        }
        this.safetyParameter = safetyParameter;
    }

    @Override
    public double getSafetyParameter() {
        return safetyParameter;
    }

    @Override
    public void setFallbackVmAllocationPolicy(PowerVmAllocationPolicyMigration fallbackVmAllocationPolicy) {
        this.fallbackVmAllocationPolicy = fallbackVmAllocationPolicy;
    }

    @Override
    public PowerVmAllocationPolicyMigration getFallbackVmAllocationPolicy() {
        return fallbackVmAllocationPolicy;
    }
}
