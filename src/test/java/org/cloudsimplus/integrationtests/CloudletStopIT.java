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
package org.cloudsimplus.integrationtests;

import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.schedulers.cloudlet.CloudletSchedulerSpaceShared;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/// Checks that a Cloudlet stopped before finishing (failed or canceled)
/// is returned to the broker once, whatever is its state or the way it's stopped.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
@SuppressWarnings("PMD.ClassNamingConventions") // The IT suffix is what makes maven-failsafe-plugin run the class.
class CloudletStopIT {
    private static final CoreCapacity CAPACITY = new CoreCapacity(1, 1000);
    private static final long LENGTH = 10_000;
    private static final double STOP_TIME = 2;
    private static final double START_TIME_DELTA = 0.5;

    private CloudSimPlus simulation;
    private DatacenterBroker broker;

    /// Starts running as soon as it's submitted, since the VM has a single core.
    private Cloudlet running;

    /// Stays in the waiting list while the other Cloudlet is running.
    private Cloudlet waiting;

    @BeforeEach
    void setUp() {
        simulation = new CloudSimPlus();
        DatacenterSimple.of(simulation, List.of(HostSimple.of(CAPACITY)));
        broker = new DatacenterBrokerSimple(simulation);
        broker.submitVm(VmSimple.of(CAPACITY).setCloudletScheduler(new CloudletSchedulerSpaceShared()));
        running = CloudletSimple.of(LENGTH, 1);
        waiting = CloudletSimple.of(LENGTH, 1);
        broker.submitCloudletList(List.of(running, waiting));
    }

    @Test
    void failedRunningCloudletIsReturnedAndReleasesTheVm() {
        broker.schedule(STOP_TIME, CloudSimTag.CLOUDLET_FAIL, running);
        simulation.start();

        assertAll(
            () -> assertEquals(Cloudlet.Status.FAILED, running.getStatus()),
            () -> assertEquals(STOP_TIME, running.getFinishTime()),
            () -> assertEquals(Cloudlet.Status.SUCCESS, waiting.getStatus()),
            () -> assertEquals(1, Collections.frequency(broker.getCloudletFinishedList(), running))
        );
    }

    /// The waiting Cloudlet must start when the running one stops, instead of when the stopped one would finish.
    @Test
    void waitingCloudletStartsWhenTheRunningOneIsStopped() {
        broker.schedule(STOP_TIME, CloudSimTag.CLOUDLET_FAIL, running);
        simulation.start();

        assertEquals(STOP_TIME, waiting.getStartTime(), START_TIME_DELTA);
    }

    @Test
    void canceledRunningCloudletIsReturned() {
        broker.schedule(STOP_TIME, CloudSimTag.CLOUDLET_CANCEL, running);
        simulation.start();

        assertAll(
            () -> assertEquals(Cloudlet.Status.CANCELED, running.getStatus()),
            () -> assertEquals(1, Collections.frequency(broker.getCloudletFinishedList(), running))
        );
    }

    /// Checks the Cloudlet is found in the waiting list and returned when the scheduler is called directly.
    @Test
    void canceledWaitingCloudletIsReturned() {
        final List<Cloudlet> stopped = new ArrayList<>();
        final Runnable cancel = () -> stopped.add(waiting.getVm().getCloudletScheduler().cloudletCancel(waiting));
        broker.schedule(STOP_TIME, CloudSimTag.CLOUDLET_UPDATE_ATTRIBUTES, cancel);
        simulation.start();

        assertAll(
            () -> assertEquals(List.of(waiting), stopped),
            () -> assertEquals(Cloudlet.Status.CANCELED, waiting.getStatus()),
            () -> assertEquals(1, Collections.frequency(broker.getCloudletFinishedList(), waiting))
        );
    }

    @Test
    void cloudletStoppedTwiceIsReturnedOnce() {
        broker.schedule(STOP_TIME, CloudSimTag.CLOUDLET_FAIL, running);
        broker.schedule(STOP_TIME + 1, CloudSimTag.CLOUDLET_FAIL, running);
        simulation.start();

        assertEquals(1, Collections.frequency(broker.getCloudletFinishedList(), running));
    }

    @Test
    void finishedCloudletIsNotChangedByALaterFailure() {
        broker.schedule(LENGTH, CloudSimTag.CLOUDLET_FAIL, running);
        simulation.start();

        assertAll(
            () -> assertEquals(Cloudlet.Status.SUCCESS, running.getStatus()),
            () -> assertEquals(1, Collections.frequency(broker.getCloudletFinishedList(), running))
        );
    }
}
