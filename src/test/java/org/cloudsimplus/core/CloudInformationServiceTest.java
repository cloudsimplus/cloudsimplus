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
package org.cloudsimplus.core;

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/// Checks that Datacenters are in the [CloudInformationService] as soon as they are created.
/// See [#618](https://github.com/cloudsimplus/cloudsimplus/issues/618).
///
/// @author Manoel Campos da Silva Filho
class CloudInformationServiceTest {
    private static final CoreCapacity CAPACITY = new CoreCapacity(1, 1000);

    @Test
    void datacenterIsInCisListBeforeSimulationStarts() {
        final var simulation = new CloudSimPlus();
        final var dc = newDatacenter(simulation);
        assertEquals(List.of(dc), simulation.getCis().getDatacenterList());
    }

    @Test
    void brokerCreatedBeforeDatacenterFindsIt() {
        final var simulation = new CloudSimPlus();
        final var broker = new DatacenterBrokerSimple(simulation);
        final var dc = newDatacenter(simulation);
        final var vm = VmSimple.of(CAPACITY);
        broker.submitVmList(List.of(vm));

        simulation.start();
        assertEquals(List.of(vm), broker.getVmCreatedList());
        assertEquals(dc, vm.getHost().getDatacenter());
    }

    private static Datacenter newDatacenter(final Simulation simulation) {
        return DatacenterSimple.of(simulation, List.of(HostSimple.of(CAPACITY)));
    }
}
