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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/// A [HostStateHistory] that stores its entries in memory.
/// It's created by the [Host] only when the history is [enabled][Host#setStateHistoryEnabled(boolean)].
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
public sealed class HostStateHistorySimple implements HostStateHistory permits HostStateHistoryNull {
    /// Starts as the shared empty [List#of()] and is only replaced by a new list when the first entry is added,
    /// so that an enabled history that never got an entry doesn't use memory for a list.
    private List<HostStateHistoryEntry> entries = List.of();

    private boolean enabled = true;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /// Enables or disables the recording of new entries.
    /// Disabling the history keeps the entries already recorded.
    /// @param enabled true to enable, false to disable
    void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void add(final HostStateHistoryEntry entry) {
        if (entries.isEmpty()) {
            entries = new ArrayList<>();
        } else if (entries.getLast().time() == entry.time()) {
            entries.set(entries.size() - 1, entry);
            return;
        }

        entries.add(entry);
    }

    @Override
    public List<HostStateHistoryEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }
}
