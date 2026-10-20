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
package org.cloudsimplus.power.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.datacenters.Datacenter;

/// Abstract implementation of a [Datacenter] [PowerModel].
/// @since CloudSim Plus 6.0.0
@Accessors(makeFinal = false)
public abstract class PowerModelDatacenter implements PowerModel {
    /// An attribute that implements the Null Object Design Pattern for [PowerModelDatacenter] objects.
    public static final PowerModelDatacenterNull NULL = new PowerModelDatacenterNull();

    /// The Datacenter this PowerModel is collecting power consumption measurements from.
    @Getter @Setter @NonNull
    private Datacenter datacenter = Datacenter.NULL;
}
