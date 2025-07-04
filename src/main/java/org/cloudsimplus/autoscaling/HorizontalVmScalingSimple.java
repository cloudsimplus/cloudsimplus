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

import lombok.experimental.Accessors;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.resources.Bandwidth;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.Ram;

import java.util.function.Predicate;

///
/// A HorizontalVmScaling implementation that allows defining the condition
/// to identify an overloaded VM, based on any desired criteria, such as
/// current [Ram], [CPU][Pe] or [Bandwidth] utilization.
/// A [DatacenterBroker] monitors the VMs that have
/// an HorizontalVmScaling object to create or destroy VMs on demand.
///
/// The overload condition has to be defined
/// by providing a [Predicate] using the [#setOverloadPredicate(Predicate)] method.
/// Check the [HorizontalVmScaling] documentation for details on how to enable
/// horizontal downscaling using the [DatacenterBroker].
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.0
/// @see HorizontalVmScaling
@Accessors
public class HorizontalVmScalingSimple extends HorizontalVmScalingAbstract {
    public HorizontalVmScalingSimple(){
        super();
    }
}
