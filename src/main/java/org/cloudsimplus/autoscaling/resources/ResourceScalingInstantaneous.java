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
package org.cloudsimplus.autoscaling.resources;

import lombok.NonNull;
import org.cloudsimplus.autoscaling.VerticalVmScaling;
import org.cloudsimplus.resources.Resource;
import org.cloudsimplus.vms.Vm;

import java.util.function.Function;

/**
 * A {@link ResourceScaling} for which the capacity of the resource to be scaled will be
 * instantaneously resized to move the Vm from the under or overload state.
 * This way, the SLA violation time will be reduced.
 *
 * <p>This scaling type will resize the resource capacity in the following way:
 * <ul>
 *     <li>in underload conditions: it decreases the resource capacity to be equal to
 *     the current load of the resource being scaled;</li>
 *     <li>in overload conditions: it increases the resource capacity to be equal to
 *     the current load of the resource being scaled.</li>
 * </ul>
 *
 * Finally it adds an extra amount of resource, defined by the
 * {@link VerticalVmScaling#getScalingFactor() scaling factor}, for safety.
 * This extra amount added is to enable the resource usage to grow up to the scaling factor
 * without needing to resize the resource again. If it grows up to the scaling factor,
 * a new up scaling request will be sent.
 * </p>
 *
 * <p><b>If the scaling factor for this type of scaling is zero, it means that the scaling object
 * will always resize the resource to the exact amount that is being used.</b></p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.2.0
 */
public class ResourceScalingInstantaneous implements ResourceScaling {
    private static final ResourceScaling GRADUAL = new ResourceScalingGradual();

    @Override
    public double getResourceAmountToScale(@NonNull final VerticalVmScaling vmScaling) {
        final Function<Vm, Double> thresholdFunc = vmScaling.getResourceUsageThresholdFunction();
        /* Computes the size to which the resource has to be scaled to move it from the
        * under or overload state.*/
        final Resource res = vmScaling.getResource();
        //The new total capacity to move the VM resource from under/overloaded.
        final double newTotalCapacity = Math.ceil(res.getAllocatedResource() / thresholdFunc.apply(vmScaling.getVm()));
        //The difference to add/remove from the current capacity so that the resource capacity will be equal to that just computed.
        final double scaleCapacity = newTotalCapacity - res.getCapacity();

        /*Includes and additional resource amount for safety, according to the scaling factor.
        * This way, if the resource usage increases again up to this extra amount,
        * there is no need to re-scale the resource.
        * If the scale factor is zero, no extra safety amount is included.*/
        final double extraSafetyCapacity = GRADUAL.getResourceAmountToScale(vmScaling);
        return scaleCapacity + extraSafetyCapacity;
    }
}
