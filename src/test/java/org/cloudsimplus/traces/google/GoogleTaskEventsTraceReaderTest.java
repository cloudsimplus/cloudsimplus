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
package org.cloudsimplus.traces.google;

import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.resources.PeSimple;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 4.0.0
public class GoogleTaskEventsTraceReaderTest {
    private static final int PES = 4;
    private static final double MIPS = 1000;
    private static final long CLOUDLET_LENGTH = -10_000;
    private static final double FINISH_TIME = 10;
    private static final int TIMEOUT_SECS = 30;

    /// A trace where a task is submitted and scheduled at timestamp 0,
    /// then finished at [#FINISH_TIME] (timestamps are in microseconds).
    private static final String TRACE_STARTING_AT_TIME_ZERO = """
        0,,1,0,0,0,user,3,9,0.5,0.1,0.1,0
        0,,1,0,0,1,user,3,9,0.5,0.1,0.1,0
        10000000,,1,0,0,4,user,3,9,0.5,0.1,0.1,0
        """;

    /// Checks that trace events happening before the first simulation clock tick
    /// are processed instead of being detected as past events.
    /// See [#563](https://github.com/cloudsimplus/cloudsimplus/issues/563).
    @Test @Timeout(TIMEOUT_SECS)
    public void eventsAtTimestampZeroAreProcessed(@TempDir final Path tempDir) throws IOException {
        final var simulation = new CloudSimPlus();
        final var peList = IntStream.range(0, PES).mapToObj(i -> (Pe) new PeSimple(MIPS)).toList();
        new DatacenterSimple(simulation, List.of(new HostSimple(peList)));

        final var traceFile = Files.writeString(tempDir.resolve("task-events.csv"), TRACE_STARTING_AT_TIME_ZERO);
        final var reader = new GoogleTaskEventsTraceReader(simulation, traceFile.toString(), GoogleTaskEventsTraceReaderTest::createCloudlet);
        final var cloudlet = reader.process().iterator().next();
        reader.getBrokerManager().getBrokers().forEach(broker -> broker.submitVm(new VmSimple(MIPS, PES)));
        simulation.start();

        assertAll(
            () -> assertEquals(Cloudlet.Status.SUCCESS, cloudlet.getStatus()),
            () -> assertEquals(FINISH_TIME, cloudlet.getFinishTime(), 1)
        );
    }

    private static Cloudlet createCloudlet(final TaskEvent event) {
        return new CloudletSimple(CLOUDLET_LENGTH, 1)
            .setUtilizationModelRam(new UtilizationModelDynamic(0, event.getResourceRequestForRam()));
    }

    /// Ensures the order or enums is not changed,
    /// because that will cause the enums ordinal values to change.
    /// These values are defined in the Google Cluster Data documentation.
    @Test
    public void testEventType(){
        assertAll(
            () -> assertEquals(0, TaskEventType.SUBMIT.ordinal()),
            () -> assertEquals(1, TaskEventType.SCHEDULE.ordinal()),
            () -> assertEquals(2, TaskEventType.EVICT.ordinal()),
            () -> assertEquals(3, TaskEventType.FAIL.ordinal()),
            () -> assertEquals(4, TaskEventType.FINISH.ordinal()),
            () -> assertEquals(5, TaskEventType.KILL.ordinal()),
            () -> assertEquals(6, TaskEventType.LOST.ordinal()),
            () -> assertEquals(7, TaskEventType.UPDATE_PENDING.ordinal()),
            () -> assertEquals(8, TaskEventType.UPDATE_RUNNING.ordinal())
        );
    }

    /// Ensures the order or enums is not changed,
    /// because that will cause the enums ordinal values to change.
    /// These values are defined in the Google Cluster Data documentation.
    @Test
    public void testMissingInfo(){
        assertAll(
            () -> assertEquals(0, MissingInfo.SNAPSHOT_BUT_NO_TRANSITION.ordinal()),
            () -> assertEquals(1, MissingInfo.NO_SNAPSHOT_OR_TRANSITION.ordinal()),
            () -> assertEquals(2, MissingInfo.EXISTS_BUT_NO_CREATION.ordinal())
        );
    }

    /// Ensures the order or enums is not changed,
    /// because that will cause the enums ordinal values to change.
    /// These values are defined in the Google Cluster Data documentation
    /// and indicate the order of the fields inside the trace file.
    @Test
    public void testFieldIndex(){
        assertAll(
            () -> assertEquals(0, TaskEventField.TIMESTAMP.ordinal()),
            () -> assertEquals(1, TaskEventField.MISSING_INFO.ordinal()),
            () -> assertEquals(2, TaskEventField.JOB_ID.ordinal()),
            () -> assertEquals(3, TaskEventField.TASK_INDEX.ordinal()),
            () -> assertEquals(4, TaskEventField.MACHINE_ID.ordinal()),
            () -> assertEquals(5, TaskEventField.EVENT_TYPE.ordinal()),
            () -> assertEquals(6, TaskEventField.USERNAME.ordinal()),
            () -> assertEquals(7, TaskEventField.SCHEDULING_CLASS.ordinal()),
            () -> assertEquals(8, TaskEventField.PRIORITY.ordinal()),
            () -> assertEquals(9, TaskEventField.RESOURCE_REQUEST_FOR_CPU_CORES.ordinal()),
            () -> assertEquals(10, TaskEventField.RESOURCE_REQUEST_FOR_RAM.ordinal()),
            () -> assertEquals(11, TaskEventField.RESOURCE_REQUEST_FOR_LOCAL_DISK_SPACE.ordinal()),
            () -> assertEquals(12, TaskEventField.DIFFERENT_MACHINE_CONSTRAINT.ordinal())
        );
    }
}
