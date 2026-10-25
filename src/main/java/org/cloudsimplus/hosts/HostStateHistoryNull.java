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

/// A class that implements the Null Object Design Pattern for [HostStateHistory] objects.
/// It extends [HostStateHistorySimple], so that a Host can keep its history in a field of that type
/// (which may hold [HostStateHistory#NULL]) and change it without casts.
///
/// @author Manoel Campos da Silva Filho
/// @see HostStateHistory#NULL
/// @since CloudSim Plus 9.0.0
final class HostStateHistoryNull extends HostStateHistorySimple {
    /// The single instance, which is [HostStateHistory#NULL].
    /// It has this type so that classes in this package can use it where a [HostStateHistorySimple] is expected.
    static final HostStateHistoryNull INSTANCE = new HostStateHistoryNull();

    private HostStateHistoryNull() {/**/}

    @Override public boolean isEnabled() { return false; }
    @Override void setEnabled(boolean enabled) {/**/}
    @Override public void add(HostStateHistoryEntry entry) {/**/}
    @Override public List<HostStateHistoryEntry> getEntries() { return List.of(); }
}
