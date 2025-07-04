package org.cloudsimplus.allocationpolicies.migration;

import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.selectionpolicies.VmSelectionPolicy;

/// An abstract class that is the base for implementation of [VM allocation policies][VmAllocationPolicyMigration]
/// which use a dynamic over-utilization threshold.
///
/// @author Manoel Campos da Silva Filho
public class VmAllocationPolicyMigrationDynamicUpperThresholdFirstFit extends VmAllocationPolicyMigrationDynamicUpperThresholdAbstract {

    /**
     * Creates a VmAllocationPolicy with a {@link #getSafetyParameter() safety parameter} equals to 0
     * and no {@link #getFallbackVmAllocationPolicy() fallback policy}.
     *
     * @param vmSelectionPolicy the {@link VmAllocationPolicyMigration#getVmSelectionPolicy() policy}
     *                          that defines how VMs are selected for migration
     */
    public VmAllocationPolicyMigrationDynamicUpperThresholdFirstFit(final VmSelectionPolicy vmSelectionPolicy) {
        this(vmSelectionPolicy, 0, VmAllocationPolicyMigration.NULL);
    }

    /**
     * Creates a VmAllocationPolicy.
     *
     * @param vmSelectionPolicy the {@link VmAllocationPolicyMigration#getVmSelectionPolicy() policy} that defines how VMs are selected for migration
     * @param safetyParameter            {@link VmAllocationPolicyMigrationDynamicUpperThreshold#getSafetyParameter() the safety parameter}
     * @param fallbackVmAllocationPolicy {@link VmAllocationPolicyMigrationDynamicUpperThreshold#getFallbackVmAllocationPolicy() the fallback VM allocation policy} to be used when
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

    @Override
    public double computeHostUtilizationMeasure(final Host host) throws IllegalStateException {
        return 0;
    }
}
