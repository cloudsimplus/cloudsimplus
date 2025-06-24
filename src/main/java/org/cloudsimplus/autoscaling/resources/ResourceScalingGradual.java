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

/**
 * A {@link ResourceScaling} for which the capacity of the resource to be scaled will be gradually
 * resized according to the defined {@link VerticalVmScaling#getScalingFactor() scaling factor}.
 * This scaling type may not automatically move a Vm from an under or overload state,
 * since it will increase or decrease the resource capacity the specified fraction
 * at a time.
 *
 * <p>This gradual resize may give the opportunity for the Vm workload to return
 * to the normal state, without requiring further scaling.
 * However, if the workload doesn't return quickly
 * to the normal and expected state, that may cause longer SLA violation time.</p>
 *
 * <p><b>This is the default type of scaling in case one is not defined.</b></p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.2.0
 */
public class ResourceScalingGradual implements ResourceScaling {
    @Override
    public double getResourceAmountToScale(@NonNull VerticalVmScaling vmScaling) {
        return vmScaling.getResource().getCapacity() * vmScaling.getScalingFactor();
    }
}
