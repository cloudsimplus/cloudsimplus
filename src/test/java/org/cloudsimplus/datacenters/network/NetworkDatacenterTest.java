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
package org.cloudsimplus.datacenters.network;

import org.cloudsimplus.core.Simulation;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.hosts.network.NetworkHost;
import org.cloudsimplus.resources.CoreCapacity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NetworkDatacenterTest {
    private static final long RAM = 1000;
    private static final long BW = 1000;
    private static final long STORAGE = 1000;
    private static final CoreCapacity CAPACITY = new CoreCapacity(1, 1000);

    @Test
    void ofWhenAllHostsAreNetworkHosts() {
        final List<Host> hosts = List.of(NetworkHost.of(RAM, BW, STORAGE, CAPACITY));
        assertDoesNotThrow(() -> NetworkDatacenter.of(Simulation.NULL, hosts));
    }

    @Test
    void ofWhenSomeHostIsNotANetworkHost() {
        final List<Host> hosts = List.of(NetworkHost.of(RAM, BW, STORAGE, CAPACITY), HostSimple.of(RAM, BW, STORAGE, CAPACITY));
        assertThrows(IllegalArgumentException.class, () -> NetworkDatacenter.of(Simulation.NULL, hosts));
    }
}
