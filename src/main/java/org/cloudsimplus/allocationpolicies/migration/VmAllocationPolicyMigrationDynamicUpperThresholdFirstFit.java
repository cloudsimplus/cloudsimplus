/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
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
