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
package org.cloudsimplus.resources;

import org.cloudsimplus.core.Machine;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.vms.Vm;

import java.util.List;

/**
 * An interface to provide a polymorphic way to access a given resource
 * like {@link Ram}, {@link Bandwidth}, {@link SimpleStorage}
 * or {@link Pe} from a List containing such different resources.
 * It is implemented by {@link Machine} such as a {@link Host} or {@link Vm}.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface Resourceful {
    /**
     * Gets a given {@link Resource}, such as {@link Ram} or {@link Bandwidth},
     * from the List of the {@link Machine}'s resources.
     *
     * @param resourceClass the class of resource to get
     * @return the {@link Resource} corresponding to the given class
     */
    default ResourceManageable getResource(final Class<? extends ResourceManageable> resourceClass){
        return getResources()
                .stream()
                .filter(resource -> resource.isSubClassOf(resourceClass))
                .findFirst()
                .orElse(ResourceManageable.NULL);
    }

    /**
     * @return a <b>read-only</b> list of resources the machine has.
     * @see #getResource(Class)
     */
    List<ResourceManageable> getResources();
}
