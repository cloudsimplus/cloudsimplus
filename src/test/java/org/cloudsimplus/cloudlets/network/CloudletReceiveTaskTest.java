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

import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.network.VmPacket;
import org.cloudsimplus.vms.network.NetworkVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// @author Manoel Campos da Silva Filho
class CloudletReceiveTaskTest {
    private static final int TASK_ID = 0;
    private static final long PACKET_BYTES = 100;
    private static final double START_TIME = 5;
    private static final double TIMEOUT = 10;

    private NetworkVm sourceVm;
    private NetworkVm destinationVm;
    private NetworkCloudlet senderA;
    private NetworkCloudlet senderB;
    private NetworkCloudlet receiver;

    @BeforeEach
    void setUp() {
        final var broker = new DatacenterBrokerSimple(new CloudSimPlus());
        sourceVm = new NetworkVm(0, new CoreCapacity(1, 1000));
        destinationVm = new NetworkVm(1, new CoreCapacity(1, 1000));
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

    @Test
    void setTimeoutThrowsWhenNegative() {
        final var task = createTask(senderA);
        assertThrows(IllegalArgumentException.class, () -> task.setTimeout(-1));
    }

    @Test
    void getRemainingTimeoutIsMaxValueWhenThereIsNoTimeout() {
        final var task = createStartedTask(0);
        assertFalse(task.hasTimeout());
        assertEquals(Double.MAX_VALUE, task.getRemainingTimeout(TIMEOUT * 2));
    }

    @Test
    void getRemainingTimeoutIsMaxValueWhenTaskHasNotStarted() {
        final var task = createTask(senderA).setTimeout(TIMEOUT);
        assertEquals(Double.MAX_VALUE, task.getRemainingTimeout(0));
    }

    @Test
    void getRemainingTimeoutDecreasesUntilZero() {
        final var task = createStartedTask(TIMEOUT);
        assertEquals(TIMEOUT, task.getRemainingTimeout(START_TIME));
        assertEquals(TIMEOUT / 2, task.getRemainingTimeout(START_TIME + TIMEOUT / 2));
        assertEquals(0, task.getRemainingTimeout(START_TIME + TIMEOUT * 2));
    }

    @Test
    void finishIfTimedOutDoesNothingBeforeTimeout() {
        final var task = createStartedTask(TIMEOUT);
        assertFalse(task.finishIfTimedOut(START_TIME + TIMEOUT / 2));
        assertFalse(task.isTimedOut());
        assertTrue(task.isActive());
    }

    @Test
    void finishIfTimedOutFinishesTaskWhenTimeoutIsReached() {
        final var task = createStartedTask(TIMEOUT);
        assertTrue(task.finishIfTimedOut(START_TIME + TIMEOUT));
        assertTrue(task.isTimedOut());
        assertTrue(task.isFinished());
    }

    @Test
    void finishIfTimedOutNeverFinishesTaskWithoutTimeout() {
        final var task = createStartedTask(0);
        assertFalse(task.finishIfTimedOut(Double.MAX_VALUE / 2));
        assertTrue(task.isActive());
    }

    @Test
    void finishedTaskDoesNotTimeOut() {
        final var task = createStartedTask(TIMEOUT);
        task.setExpectedPacketsToReceive(1);
        task.receivePacket(createPacket(senderA, receiver));

        assertFalse(task.finishIfTimedOut(START_TIME + TIMEOUT));
        assertFalse(task.isTimedOut());
    }

    @Test
    void getWaitingTimeIsZeroWhenTaskHasNotStarted() {
        final var task = createTask(senderA);
        assertEquals(0, task.getWaitingTime(START_TIME));
    }

    @Test
    void getWaitingTimeGrowsAfterTaskStarts() {
        final var task = createStartedTask(0);
        assertEquals(0, task.getWaitingTime(START_TIME));
        assertEquals(TIMEOUT, task.getWaitingTime(START_TIME + TIMEOUT));
    }

    @Test
    void getWaitingTimeIsZeroWhenTaskIsFinished() {
        final var task = createStartedTask(0);
        task.setExpectedPacketsToReceive(1);
        task.receivePacket(createPacket(senderA, receiver));
        assertEquals(0, task.getWaitingTime(START_TIME + TIMEOUT));
    }

    @Test
    void isWaitingTooLongOnlyAfterWaitWarningTime() {
        final var task = createStartedTask(0);
        final double warningTime = START_TIME + CloudletReceiveTask.WAIT_WARNING_TIME;
        assertFalse(task.isWaitingTooLong(warningTime - 1));
        assertTrue(task.isWaitingTooLong(warningTime));
        assertTrue(task.isWaitingTooLong(warningTime + 1));
    }

    @Test
    void isNeverWaitingTooLongWhenTaskHasTimeout() {
        final var task = createStartedTask(CloudletReceiveTask.WAIT_WARNING_TIME * 2);
        assertFalse(task.isWaitingTooLong(START_TIME + CloudletReceiveTask.WAIT_WARNING_TIME));
        assertEquals(Double.MAX_VALUE, task.getRemainingWaitWarningTime(START_TIME));
    }

    @Test
    void isNotWaitingTooLongWhenTaskIsFinished() {
        final var task = createStartedTask(0);
        task.setExpectedPacketsToReceive(1);
        task.receivePacket(createPacket(senderA, receiver));
        assertFalse(task.isWaitingTooLong(START_TIME + CloudletReceiveTask.WAIT_WARNING_TIME));
    }

    @Test
    void getRemainingWaitWarningTimeDecreasesUntilZero() {
        final var task = createStartedTask(0);
        final double halfWarningTime = CloudletReceiveTask.WAIT_WARNING_TIME / 2;
        assertEquals(CloudletReceiveTask.WAIT_WARNING_TIME, task.getRemainingWaitWarningTime(START_TIME));
        assertEquals(halfWarningTime, task.getRemainingWaitWarningTime(START_TIME + halfWarningTime));
        assertEquals(0, task.getRemainingWaitWarningTime(START_TIME + CloudletReceiveTask.WAIT_WARNING_TIME * 2));
    }

    private CloudletReceiveTask createStartedTask(final double timeout) {
        final var task = createTask(senderA).setTimeout(timeout);
        task.setStartTime(START_TIME);
        return task;
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
