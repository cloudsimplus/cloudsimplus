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

import org.cloudsimplus.vms.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// An interface to allow implementing
/// [horizontal and vertical scaling](https://en.wikipedia.org/wiki/Scalability#Horizontal_and_vertical_scaling)
/// of [Vm]s.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 1.0.0
public sealed interface VmScaling permits HorizontalVmScaling, VerticalVmScaling, VmScalingAbstract, VmScalingNull {
    Logger LOGGER = LoggerFactory.getLogger(VmScaling.class.getSimpleName());

    /// An attribute that implements the Null Object Design Pattern for [VmScaling]
    /// objects.
    VmScaling NULL = new VmScalingNull();

    /// {@return the Vm that this VmScaling is linked to}
    Vm getVm();

    /// Sets a [Vm] to this VmScaling. The broker will call this VmScaling
    /// in order to balance load when its Vm is over utilized.
    ///
    /// **When the VmScaling is assigned to a Vm, the Vm sets itself to the VmScaling object,
    /// creating an association between the two objects.**
    ///
    /// @param vm the Vm to set
    VmScaling setVm(Vm vm);
}
