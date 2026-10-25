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
package org.cloudsimplus.datacenters;

import org.cloudsimplus.placementpolicies.VmPlacementPolicy;
import org.cloudsimplus.hosts.Host;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/// @author Manoel Campos da Silva Filho
public class DatacenterTest {
    @Test
    public void testNullObject() {
        final Datacenter instance = Datacenter.NULL;
        assertAll(
            () -> assertEquals(-1, instance.getId()),
            () -> assertEquals(0, instance.getSchedulingInterval()),
            () -> assertTrue(instance.getHostList().isEmpty()),
            () -> assertEquals(Host.NULL, instance.getHost(0)),
            () -> assertEquals(VmPlacementPolicy.NULL, instance.getVmPlacementPolicy())
        );
    }
}
