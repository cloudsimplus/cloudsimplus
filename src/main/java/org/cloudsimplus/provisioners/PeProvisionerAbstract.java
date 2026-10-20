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
package org.cloudsimplus.provisioners;

import lombok.NonNull;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.ResourceManageable;
import org.cloudsimplus.vms.Vm;

import java.util.function.Function;

/// A base class for implementing [Pe] [ResourceProvisioner]s.
/// @author Manoel Campos
public abstract non-sealed class PeProvisionerAbstract extends ResourceProvisionerSimple implements PeProvisioner {
    public PeProvisionerAbstract(ResourceManageable resource, Function<Vm, ResourceManageable> vmResourceFunction) {
        super(resource, vmResourceFunction);
    }

    public void setPe(final Pe pe) {
        final var msg = "Pe already has a PeProvisioner assigned to it. Each Pe must have its own PeProvisioner instance.";
        if (isOtherProvisionerAssignedToPe(pe)) {
            throw new IllegalArgumentException(msg);
        }
        setResources(pe, Vm::getProcessor);
    }

    /// Checks if the [Pe] has a [PeProvisioner] assigned that is
    /// different from the current one.
    ///
    /// @param pe the Pe to check
    /// @return `true` if the Pe has a PeProvisioner different then the current one,
    /// `false` otherwise.
    private boolean isOtherProvisionerAssignedToPe(@NonNull final Pe pe) {
        return pe.getPeProvisioner() != null &&
            pe.getPeProvisioner() != PeProvisioner.NULL &&
            !pe.getPeProvisioner().equals(this);
    }
}
