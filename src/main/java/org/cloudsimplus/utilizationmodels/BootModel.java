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
package org.cloudsimplus.utilizationmodels;

import lombok.NonNull;
import lombok.With;
import org.cloudsimplus.core.CustomerEntity;
import org.cloudsimplus.core.Machine;

/// Defines how a [Machine] uses resources
/// such as CPU and RAM during the boot process.
/// If a model is [UtilizationModel#NULL], the boot process doesn't use that resource,
/// indicating there is no overhead during the machine boot (which is not accurate).
///
/// Since it's immutable, a single instance can be safely shared by all machines with the same boot behaviour,
/// instead of creating one for each machine. To change a model, use the `with` methods,
/// which return a new BootModel (or this one if the model doesn't change),
/// leaving the machines that share this one unaffected.
/// The [UtilizationModel]s must also be stateless to be shared
/// (for instance, a [UtilizationModelDynamic] with an update function keeps its current utilization, so it's not).
/// See [UtilizationModel#isStateful()] for details.
///
/// @param cpuModel the utilization model which defines how the machine uses CPU during the boot process
/// @param ramModel the utilization model which defines how the machine uses RAM during the boot process
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 8.3.0
@With
public record BootModel(@NonNull UtilizationModel cpuModel, @NonNull UtilizationModel ramModel) {
    /// A BootModel where the boot process doesn't use any resources.
    public static final BootModel NULL = new BootModel(UtilizationModel.NULL);

    /// Creates a BootModel setting the same [UtilizationModel] for CPU and RAM.
    /// @param utilizationModel the utilization model to set for CPU and RAM resources
    /// @see BootModel#BootModel(UtilizationModel, UtilizationModel)
    public BootModel(final UtilizationModel utilizationModel) {
        this(utilizationModel, utilizationModel);
    }

    /// Registers the machine using this BootModel as the owner of its [UtilizationModel]s.
    /// @param owner the machine using this BootModel
    /// @return this
    /// @throws IllegalStateException if a stateful model is already used by another VM or Cloudlet
    /// @see UtilizationModel#registerOwner(CustomerEntity)
    public BootModel registerOwner(final CustomerEntity owner) {
        // Validates both models first, so a failure doesn't leave one of them registered
        cpuModel.validateOwner(owner);
        ramModel.validateOwner(owner);
        cpuModel.registerOwner(owner);
        ramModel.registerOwner(owner);
        return this;
    }

    /// @return \[0..1\] to indicate the boot CPU utilization percentage for the current time.
    public double getCpuPercentUtilization(){
        return cpuModel.getUtilization();
    }

    /// @return \[0..1\] to indicate the boot RAM utilization percentage for the current time.
    public double getRamPercentUtilization(){
        return ramModel.getUtilization();
    }
}
