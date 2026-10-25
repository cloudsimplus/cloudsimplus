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
package org.cloudsimplus.hosts;

import java.util.List;

/// The history of a [Host] state (its CPU utilization and active status) along the simulation time.
///
/// A Host has no history by default: it uses [#NULL], which records nothing and takes no memory.
/// A [HostStateHistorySimple] is created only when the history is
/// [enabled][Host#setStateHistoryEnabled(boolean)].
///
/// @author Manoel Campos da Silva Filho
/// @see Host#getStateHistory()
/// @since CloudSim Plus 9.0.0
public sealed interface HostStateHistory permits HostStateHistorySimple {
    /// An attribute that implements the Null Object Design Pattern for [HostStateHistory] objects.
    HostStateHistory NULL = HostStateHistoryNull.INSTANCE;

    /// @return true if the Host state is being recorded, false otherwise
    boolean isEnabled();

    /// Adds an entry to the history.
    /// If the last entry is for the same time, it is replaced by the given one.
    ///
    /// @param entry the entry to add
    void add(HostStateHistoryEntry entry);

    /// @return a **read-only** list of the history entries.
    /// It's empty if the history was never enabled.
    List<HostStateHistoryEntry> getEntries();
}
