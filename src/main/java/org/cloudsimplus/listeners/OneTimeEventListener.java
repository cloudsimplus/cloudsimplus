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
package org.cloudsimplus.listeners;

/// An [EventListener] that is notified only once and then automatically removed
/// from the listener list of the observed object.
///
/// Pass a lambda or method reference to [EventListener#once(OneTimeEventListener)]
/// (or assign it to a variable of this type) so that the compiler synthesizes the listener
/// directly as a `OneTimeEventListener`, without creating any wrapper object.
///
/// @param <T> the class of the object containing information to be given to the
///            listener when the expected event happens
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
/// @see EventListener#once(OneTimeEventListener)
@FunctionalInterface
public interface OneTimeEventListener<T extends EventInfo> extends EventListener<T> {
    @Override
    default boolean isOneTime() {
        return true;
    }
}
