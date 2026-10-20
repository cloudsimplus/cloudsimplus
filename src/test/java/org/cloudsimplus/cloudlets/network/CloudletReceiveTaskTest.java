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
package org.cloudsimplus.cloudlets.network;

import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.network.VmPacket;
import org.cloudsimplus.vms.network.NetworkVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// @author Manoel Campos da Silva Filho
class CloudletReceiveTaskTest {
    private static final int TASK_ID = 0;
    private static final long PACKET_BYTES = 100;

    private NetworkVm sourceVm;
    private NetworkVm destinationVm;
    private NetworkCloudlet senderA;
    private NetworkCloudlet senderB;
    private NetworkCloudlet receiver;

    @BeforeEach
    void setUp() {
        final var broker = new DatacenterBrokerSimple(new CloudSimPlus());
        sourceVm = new NetworkVm(0, 1000, 1);
        destinationVm = new NetworkVm(1, 1000, 1);
        senderA = createCloudlet(0, sourceVm, broker);
        senderB = createCloudlet(1, sourceVm, broker);
        receiver = createCloudlet(2, destinationVm, broker);
    }

    private static NetworkCloudlet createCloudlet(final int id, final NetworkVm vm, final DatacenterBroker broker) {
        final var cloudlet = new NetworkCloudlet(id, 1);
        cloudlet.setVm(vm).setBroker(broker);
        return cloudlet;
    }

    @Test
    void isPacketExpectedOnlyFromGivenSenderCloudlet() {
        final var task = createTask(senderA);
        assertTrue(task.isPacketExpected(createPacket(senderA, receiver)));
        assertFalse(task.isPacketExpected(createPacket(senderB, receiver)));
    }

    @Test
    void isPacketExpectedFromAnyCloudletInSourceVmWhenNoSenderIsGiven() {
        final var task = createTask(null);
        assertTrue(task.isPacketExpected(createPacket(senderA, receiver)));
        assertTrue(task.isPacketExpected(createPacket(senderB, receiver)));
    }

    @Test
    void isPacketNotExpectedWhenTargetingAnotherCloudlet() {
        final var task = createTask(senderA);
        assertFalse(task.isPacketExpected(createPacket(senderA, senderB)));
    }

    @Test
    void getRemainingPacketsToReceiveDecreasesUntilZero() {
        final var task = createTask(senderA);
        task.setExpectedPacketsToReceive(1);
        assertEquals(1, task.getRemainingPacketsToReceive());

        task.receivePacket(createPacket(senderA, receiver));
        assertEquals(0, task.getRemainingPacketsToReceive());

        task.receivePacket(createPacket(senderA, receiver));
        assertEquals(0, task.getRemainingPacketsToReceive());
    }

    private CloudletReceiveTask createTask(final NetworkCloudlet sender) {
        final var task = new CloudletReceiveTask(TASK_ID, sourceVm, sender);
        receiver.addTask(task);
        return task;
    }

    private static VmPacket createPacket(final NetworkCloudlet sender, final NetworkCloudlet target) {
        return new VmPacket(sender.getVm(), target.getVm(), PACKET_BYTES, sender, target);
    }
}
