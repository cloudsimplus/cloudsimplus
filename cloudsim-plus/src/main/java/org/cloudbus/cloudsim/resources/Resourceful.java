/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudbus.cloudsim.resources;

import org.cloudbus.cloudsim.core.AbstractMachine;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.List;

/**
 * An interface to be implemented by a machine such as a {@link Host} or {@link Vm},
 * that provides a polymorphic way to access a given resource
 * like {@link Ram}, {@link Bandwidth}, {@link Storage}
 * or {@link Pe} from a List containing such different resources.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public interface Resourceful {
    /**
     * Gets a given {@link AbstractMachine} {@link Resource}, such as {@link Ram} or {@link Bandwidth},
     * from the List of machine resources.
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
     * Gets a <b>read-only</b> list of resources the machine has.
     *
     * @see #getResource(Class)
     * @return a read-only list of resources
     */
    List<ResourceManageable> getResources();
}
