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
package org.cloudsimplus.autoscaling;

import org.cloudsimplus.autoscaling.resources.ResourceScaling;
import org.cloudsimplus.autoscaling.resources.ResourceScalingGradual;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.resources.*;
import org.cloudsimplus.vms.Vm;

/// A [VerticalVmScaling] implementation which allows a [DatacenterBroker]
/// to perform on demand up or down scaling for some [Vm] resource, such as [Ram],
/// core or [Bandwidth].
///
/// For each resource that is required to be scaled, a distinct [VerticalVmScaling]
/// instance must be assigned to the VM to be scaled.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.1.0
public class VerticalVmScalingSimple extends VerticalVmScalingAbstract {

    /// Creates a VerticalVmScaling with a [ResourceScalingGradual] scaling type.
    ///
    /// @param resourceClassToScale the class of Vm resource that this scaling object will request
    /// up or down scaling (such as [Ram].class,
    /// [Bandwidth].class or [Processor].class).
    /// @param scalingFactor the factor (a percentage value between 0 and 1)
    /// that will be used to scale a Vm resource up or down,
    /// whether such a resource is over or underloaded, according to the
    /// defined predicates.
    /// In the case of up scaling, the value 1 will scale the resource in 100%,
    /// doubling its capacity.
    /// @see VerticalVmScaling#setResourceScaling(ResourceScaling)
    public VerticalVmScalingSimple(final Class<? extends ResourceManageable> resourceClassToScale, final double scalingFactor){
        super(resourceClassToScale, new ResourceScalingGradual(), scalingFactor);
    }

    @Override
    public boolean isVmUnderloaded() {
        return getResource().getPercentOccupied() < getLowerThresholdFunction().apply(getVm());
    }

    @Override
    public boolean isVmOverloaded() {
        return getResource().getPercentOccupied() > getUpperThresholdFunction().apply(getVm());
    }
}
