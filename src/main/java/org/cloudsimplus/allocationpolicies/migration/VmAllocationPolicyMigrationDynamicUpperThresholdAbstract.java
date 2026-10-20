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

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.selectionpolicies.VmSelectionPolicy;

/// An abstract class that is the base for implementation of [VM allocation policies][VmAllocationPolicyMigration]
/// which use a dynamic over-utilization threshold.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
@Accessors @Getter @Setter
public non-sealed abstract class VmAllocationPolicyMigrationDynamicUpperThresholdAbstract extends VmAllocationPolicyMigrationAbstract implements VmAllocationPolicyMigrationDynamicUpperThreshold {
    /// @see VmAllocationPolicyMigrationDynamicUpperThreshold#getSafetyParameter()
    private double safetyParameter;

    /// @see VmAllocationPolicyMigrationDynamicUpperThreshold#getFallbackVmAllocationPolicy()
    @NonNull
    private VmAllocationPolicyMigration fallbackVmAllocationPolicy = VmAllocationPolicyMigration.NULL;

    public VmAllocationPolicyMigrationDynamicUpperThresholdAbstract(final VmSelectionPolicy vmSelectionPolicy) {
        super(vmSelectionPolicy);
    }

    /// Checks if a host is over-utilized based on the CPU over utilization threshold computed using
    /// the statistical method defined in [#computeHostUtilizationMeasure(Host)].
    ///
    /// @param host {@inheritDoc}
    /// @return {@inheritDoc}
    @Override
    public boolean isOverloaded(final Host host) {
        if (getOverUtilizationThreshold(host) == Double.MAX_VALUE) {
            return getFallbackVmAllocationPolicy().isOverloaded(host);
        }

        return super.isOverloaded(host);
    }

    /// Gets a dynamically computed Host over-utilization threshold based on the
    /// Host CPU utilization history.
    ///
    /// @param host {@inheritDoc}
    /// @return {@inheritDoc} or [Double#MAX_VALUE] if the threshold could not be computed
    /// (for instance, because the Host doesn't have enough history to use)
    /// @see VmAllocationPolicyMigrationDynamicUpperThreshold#computeHostUtilizationMeasure(Host)
    @Override
    public double getOverUtilizationThreshold(final Host host) {
        try {
            return 1 - getSafetyParameter() * computeHostUtilizationMeasure(host);
        } catch (IllegalStateException e) {
            return Double.MAX_VALUE;
        }
    }

    /// Sets the safety parameter.
    ///
    /// @param safetyParameter the new safety parameter
    protected final void setSafetyParameter(final double safetyParameter) {
        final var msg = "The safety parameter must be a positive value. It is a percentage value in scale from 0 to 1 where, for instance, 1 means 100% and 1.5 means 150%.";
        if (safetyParameter < 0) {
            throw new IllegalArgumentException(msg);
        }

        this.safetyParameter = safetyParameter;
    }

    @Override
    public final double getSafetyParameter() {
        return this.safetyParameter;
    }

    @Override
    public final VmAllocationPolicyMigration getFallbackVmAllocationPolicy() {
        return this.fallbackVmAllocationPolicy;
    }

    @Override
    public final VmAllocationPolicyMigrationDynamicUpperThresholdAbstract setFallbackVmAllocationPolicy(final VmAllocationPolicyMigration fallbackVmAllocationPolicy) {
        this.fallbackVmAllocationPolicy = fallbackVmAllocationPolicy;
        return this;
    }
}
