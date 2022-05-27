package org.cloudbus.cloudsim.allocationpolicies.migration;

import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.selectionpolicies.VmSelectionPolicy;

import java.util.Objects;

/**
 * An abstract class that is the base for implementation of VM allocation policies which use
 * a dynamic over utilization threshold.
 *
 * @author Manoel Campos da Silva Filho
 */
public abstract class VmAllocationPolicyMigrationDynamicUpperThresholdFirstFit extends VmAllocationPolicyMigrationAbstract
    implements VmAllocationPolicyMigrationDynamicUpperThreshold {

    /**
     * @see #getSafetyParameter()
     */
    private double safetyParameter;

    /**
     * @see #getFallbackVmAllocationPolicy()
     */
    private VmAllocationPolicyMigration fallbackVmAllocationPolicy;

    /**
     * Creates a VmAllocationPolicyMigrationDynamicUpperThreshold
     * with a {@link #getSafetyParameter() safety parameter} equals to 0
     * and no {@link #getFallbackVmAllocationPolicy() fallback policy}.
     *
     * @param vmSelectionPolicy the policy that defines how VMs are selected for migration
     */
    public VmAllocationPolicyMigrationDynamicUpperThresholdFirstFit(final VmSelectionPolicy vmSelectionPolicy) {
        this(vmSelectionPolicy, 0, VmAllocationPolicyMigration.NULL);
    }

    /**
     * Creates a VmAllocationPolicyMigrationDynamicUpperThreshold.
     *
     * @param vmSelectionPolicy          the policy that defines how VMs are selected for migration
     * @param safetyParameter            the safety parameter
     * @param fallbackVmAllocationPolicy the fallback VM allocation policy to be used when
     * the over utilization host detection doesn't have data to be computed
     */
    public VmAllocationPolicyMigrationDynamicUpperThresholdFirstFit(
        final VmSelectionPolicy vmSelectionPolicy,
        final double safetyParameter,
        final VmAllocationPolicyMigration fallbackVmAllocationPolicy)
    {
        super(vmSelectionPolicy);
        setSafetyParameter(safetyParameter);
        setFallbackVmAllocationPolicy(fallbackVmAllocationPolicy);
    }

    /**
     * Checks if a host is over utilized based on the CPU over utilization threshold computed using
     * the statistical method defined in {@link #computeHostUtilizationMeasure(Host)}.
     *
     * @param host {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean isHostOverloaded(final Host host) {
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
     * (for instance, because the Host doesn't have enough history to use)
     * @see VmAllocationPolicyMigrationDynamicUpperThreshold#computeHostUtilizationMeasure(Host)
     */
    @Override
    public double getOverUtilizationThreshold(final Host host) {
        try {
            return 1 - getSafetyParameter() * computeHostUtilizationMeasure(host);
        } catch (IllegalStateException e) {
            return Double.MAX_VALUE;
        }
    }

    /**
     * Sets the safety parameter.
     *
     * @param safetyParameter the new safety parameter
     */
    protected final void setSafetyParameter(final double safetyParameter) {
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
    public final void setFallbackVmAllocationPolicy(final VmAllocationPolicyMigration fallbackPolicy) {
        this.fallbackVmAllocationPolicy = Objects.requireNonNull(fallbackPolicy);
    }

    @Override
    public VmAllocationPolicyMigration getFallbackVmAllocationPolicy() {
        return fallbackVmAllocationPolicy;
    }
}
