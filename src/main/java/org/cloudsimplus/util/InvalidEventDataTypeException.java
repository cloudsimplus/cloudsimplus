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
package org.cloudsimplus.util;

import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.core.events.SimEvent;

import java.io.Serial;

/**
 * An exception to be thrown when the type of the {@link SimEvent#getData()}
 * is not as expected for a given tag from {@link CloudSimTag}.
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 7.0.1
 */
public class InvalidEventDataTypeException extends IllegalStateException {
    @Serial
    private static final long serialVersionUID = -3905805615156717344L;

    /// Instantiates the exception
    /// @param evt the event having invalid data type
    /// @param tagName the name of the tag from [CloudSimTag] that generated the event
    /// @param requiredClassName the name of the required class wasn't met. It's String to enable providing
    ///                          specific type names using generics, such `Set<Datacenter>`
    ///                          instead of just Set.
    public InvalidEventDataTypeException(final SimEvent evt, final String tagName, final String requiredClassName) {
        super(formatMsg(evt, tagName, requiredClassName));
    }

    private static String formatMsg(final SimEvent evt, final String tagName, final String requiredClassName) {
        final var fmt = "%s: %s event data must be a %s but it was %s";
        final var clock = evt.getSimulation().clockStr();
        return fmt.formatted(clock, tagName, requiredClassName, evt.getData().getClass().getSimpleName());
    }
}
