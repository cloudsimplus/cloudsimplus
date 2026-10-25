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
package org.cloudsimplus.vms;

import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.schedulers.cloudlet.CloudletScheduler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/// @author Manoel Campos da Silva Filho
public class VmTest {
    @Test
    public void testVmNullObject(){
        final Vm instance = Vm.NULL;
        assertAll(
            () -> assertTrue(instance.getStateHistory().isEmpty()),
            () -> assertEquals(CloudletScheduler.NULL, instance.getCloudletScheduler()),
            () -> assertFalse(instance.isInMigration()),
            () -> assertFalse(instance.isCreated()),
            () -> assertEquals(Host.NULL, instance.getHost()),
            () -> assertEquals("", instance.getUid()),
            () -> assertEquals(-1, instance.getId()),
            () -> assertSame(DatacenterBroker.NULL, instance.getBroker()),
            () -> assertEquals("", instance.getVmm())
        );
    }

    @Test
    public void testVmNullObjectListeners(){
        final Vm instance = Vm.NULL;
        instance.addOnHostPlacementListener(null);
        assertFalse(instance.removeOnHostPlacementListener(null));

        instance.addOnHostRemovalListener(null);
        assertFalse(instance.removeOnHostRemovalListener(null));

        instance.addOnCreationFailureListener(null);
        assertFalse(instance.removeOnCreationFailureListener(null));

        instance.addOnUpdateProcessingListener(null);
        assertFalse(instance.removeOnUpdateProcessingListener(null));
    }

    @Test
    public void testVmNullObjectResources(){
        final Vm instance = Vm.NULL;
        assertAll(
            () -> assertEquals(0, instance.getBw().getOccupied()),
            () -> assertEquals(0, instance.getProcessor().getMips()),
            () -> assertEquals(0, instance.getProcessor().getCores()),
            () -> assertEquals(0, instance.getCpuPercentUtilization(0)),
            () -> assertEquals(0, instance.getTotalCpuMipsUtilization(0)),
            () -> assertEquals(0, instance.getBw().getCapacity()),
            () -> assertEquals(0, instance.getBw().getOccupied()),
            () -> assertEquals(0, instance.getCurrentRequestedBw()),
            () -> assertTrue(instance.getCurrentRequestedMips().isEmpty()),
            () -> assertEquals(0, instance.getCurrentRequestedRam()),
            () -> assertEquals(0, instance.getRam().getCapacity()),
            () -> assertEquals(0, instance.getRam().getOccupied()),
            () -> assertEquals(0, instance.getStorage().getCapacity())
        );
    }

}
