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

import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.cloudlets.Cloudlet;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.utilizationmodels.UtilizationModelDynamic;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 4.0.0
public class GoogleTaskEventsTraceReaderTest {
    private static final int CORES = 4;
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

    private static final double SCHEDULE_TIME = 5;
    private static final double TIME_DELTA = 1;

    /// A trace where a task is submitted at timestamp 0, but only scheduled at [#SCHEDULE_TIME].
    private static final String TRACE_SCHEDULED_AFTER_TIME_ZERO = """
        0,,1,0,0,0,user,3,9,0.5,0.1,0.1,0
        5000000,,1,0,0,1,user,3,9,0.5,0.1,0.1,0
        10000000,,1,0,0,4,user,3,9,0.5,0.1,0.1,0
        """;

    private static final int UPDATED_PRIORITY = 5;
    private static final long FILE_SIZE_SCALE = 1000;
    private static final long UPDATED_FILE_SIZE = 200;

    /// A trace where the task priority and disk request change when it's scheduled.
    private static final String TRACE_CHANGING_ATTRIBUTES = """
        0,,1,0,0,0,user,3,9,0.5,0.1,0.1,0
        5000000,,1,0,0,1,user,3,%d,0.5,0.1,0.2,0
        10000000,,1,0,0,4,user,3,%1$d,0.5,0.1,0.2,0
        """.formatted(UPDATED_PRIORITY);

    private static final long MACHINE_1 = 1;
    private static final long MACHINE_2 = 2;
    private static final long HOST_RAM = 10_000;
    private static final long HOST_BW = 10_000;
    private static final long HOST_STORAGE = 100_000;
    private static final long VM_RAM = 100;
    private static final long VM_BW = 100;
    private static final long VM_SIZE = 1000;
    private static final double DOUBLE_DELTA = 0.0001;

    /// Tasks 10, 11 and 40 are from userA in machine 1, where task 40 starts after the others finish.
    /// Task 20 is from userB in machine 1, task 30 from userA in machine 2 and task 50 has no machine.
    private static final String TRACE_WITH_MACHINES = """
        0,,1,0,,0,userA,3,9,0.5,0.1,0.2,0
        0,,1,1,,0,userA,3,9,0.5,0.1,0.2,0
        0,,2,0,,0,userB,3,9,0.25,0.1,0.1,0
        0,,3,0,,0,userA,3,9,0.25,0.1,0.1,0
        0,,4,0,,0,userA,3,9,0.125,0.1,0.1,0
        0,,5,0,,0,userA,3,9,0.125,0.1,0.1,0
        1000000,,1,0,1,1,userA,3,9,0.5,0.1,0.2,0
        1000000,,1,1,1,1,userA,3,9,0.5,0.1,0.2,0
        1000000,,2,0,1,1,userB,3,9,0.25,0.1,0.1,0
        1000000,,3,0,2,1,userA,3,9,0.25,0.1,0.1,0
        2000000,,5,0,,1,userA,3,9,0.125,0.1,0.1,0
        5000000,,1,0,1,4,userA,3,9,0.5,0.1,0.2,0
        5000000,,1,1,1,4,userA,3,9,0.5,0.1,0.2,0
        5000000,,2,0,1,4,userB,3,9,0.25,0.1,0.1,0
        5000000,,3,0,2,4,userA,3,9,0.25,0.1,0.1,0
        6000000,,4,0,1,1,userA,3,9,0.125,0.1,0.1,0
        7000000,,4,0,1,4,userA,3,9,0.125,0.1,0.1,0
        8000000,,5,0,,4,userA,3,9,0.125,0.1,0.1,0
        """;

    /// Checks that trace events happening before the first simulation clock tick
    /// are processed instead of being detected as past events.
    /// See [#563](https://github.com/cloudsimplus/cloudsimplus/issues/563).
    @Test @Timeout(TIMEOUT_SECS)
    public void eventsAtTimestampZeroAreProcessed(@TempDir final Path tempDir) throws IOException {
        final var simulation = new CloudSimPlus();
        DatacenterSimple.of(simulation, List.of(HostSimple.of(new CoreCapacity(CORES, MIPS))));

        final var traceFile = Files.writeString(tempDir.resolve("task-events.csv"), TRACE_STARTING_AT_TIME_ZERO);
        final var reader = new GoogleTaskEventsTraceReader(simulation, traceFile.toString(), GoogleTaskEventsTraceReaderTest::createCloudlet);
        final var cloudlet = reader.process().iterator().next();
        reader.getBrokerManager().getBrokers().forEach(broker -> broker.submitVm(VmSimple.of(new CoreCapacity(CORES, MIPS))));
        simulation.start();

        assertAll(
            () -> assertEquals(Cloudlet.Status.SUCCESS, cloudlet.getStatus()),
            () -> assertEquals(FINISH_TIME, cloudlet.getFinishTime(), 1)
        );
    }

    private static Cloudlet createCloudlet(final TaskEvent event) {
        return CloudletSimple.of(CLOUDLET_LENGTH, 1)
            .setUtilizationModelRam(new UtilizationModelDynamic(0, event.getResourceRequestForRam()));
    }

    /// Checks that a task submitted at timestamp 0 waits for its SCHEDULE event to start running.
    @Test @Timeout(TIMEOUT_SECS)
    public void taskSubmittedAtTimeZeroStartsOnlyWhenScheduled(@TempDir final Path tempDir) throws IOException {
        final var simulation = new CloudSimPlus();
        DatacenterSimple.of(simulation, List.of(HostSimple.of(new CoreCapacity(CORES, MIPS))));

        final var traceFile = Files.writeString(tempDir.resolve("task-events.csv"), TRACE_SCHEDULED_AFTER_TIME_ZERO);
        final var reader = new GoogleTaskEventsTraceReader(simulation, traceFile.toString(), GoogleTaskEventsTraceReaderTest::createCloudlet);
        final var cloudlet = reader.process().iterator().next();
        reader.getBrokerManager().getBrokers().forEach(broker -> broker.submitVm(VmSimple.of(new CoreCapacity(CORES, MIPS))));
        simulation.start();

        assertEquals(SCHEDULE_TIME, cloudlet.getStartTime(), TIME_DELTA);
    }

    /// Checks that changes in the priority and in the resource requests are applied to the Cloudlet.
    @Test @Timeout(TIMEOUT_SECS)
    public void cloudletAttributesAreUpdatedFromTrace(@TempDir final Path tempDir) throws IOException {
        final var simulation = new CloudSimPlus();
        DatacenterSimple.of(simulation, List.of(HostSimple.of(new CoreCapacity(CORES, MIPS))));

        final var traceFile = Files.writeString(tempDir.resolve("task-events.csv"), TRACE_CHANGING_ATTRIBUTES);
        final var reader = new GoogleTaskEventsTraceReader(simulation, traceFile.toString(), GoogleTaskEventsTraceReaderTest::createCloudletWithAttributes);
        final var cloudlet = reader.process().iterator().next();
        reader.getBrokerManager().getBrokers().forEach(broker -> broker.submitVm(VmSimple.of(new CoreCapacity(CORES, MIPS))));
        simulation.start();

        assertAll(
            () -> assertEquals(UPDATED_PRIORITY, cloudlet.getPriority()),
            () -> assertEquals(UPDATED_FILE_SIZE, cloudlet.getFileSize())
        );
    }

    private static Cloudlet createCloudletWithAttributes(final TaskEvent event) {
        return createCloudlet(event)
            .setPriority(event.getPriority())
            .setFileSize(Math.round(event.getResourceRequestForLocalDiskSpace() * FILE_SIZE_SCALE));
    }

    /// Tasks of a user in the same machine run in the same VM, placed in the Host with that machine ID.
    /// The machine only appears in the SCHEDULE lines, as it happens in actual traces.
    @Test @Timeout(TIMEOUT_SECS)
    public void tasksRunInTheVmOfTheirUserAndMachine(@TempDir final Path tempDir) throws IOException {
        final var scenario = new TaskMappingScenario(tempDir, TRACE_WITH_MACHINES);
        scenario.simulation.start();

        assertAll(
            () -> assertEquals(MACHINE_1, scenario.machineOf(10)),
            () -> assertEquals(scenario.vmOf(10), scenario.vmOf(11)),
            () -> assertEquals(scenario.vmOf(10), scenario.vmOf(40)),
            () -> assertNotEquals(scenario.vmOf(10), scenario.vmOf(20)),
            () -> assertEquals(MACHINE_1, scenario.machineOf(20)),
            () -> assertEquals(MACHINE_2, scenario.machineOf(30)),
            () -> assertEquals(3, scenario.requests.size())
        );
    }

    /// The peak is what runs at the same time: task 40 only starts after tasks 10 and 11 finish.
    @Test @Timeout(TIMEOUT_SECS)
    public void vmIsRequestedWithThePeakOfConcurrentTasks(@TempDir final Path tempDir) throws IOException {
        final var scenario = new TaskMappingScenario(tempDir, TRACE_WITH_MACHINES);

        final var request = scenario.requests.stream()
            .filter(r -> r.userName().equals("userA") && r.machineId() == MACHINE_1).findFirst().orElseThrow();

        assertAll(
            () -> assertEquals(1.0, request.peakCpuCores(), DOUBLE_DELTA),
            () -> assertEquals(0.2, request.peakRam(), DOUBLE_DELTA),
            () -> assertEquals(0.4, request.peakLocalDiskSpace(), DOUBLE_DELTA)
        );
    }

    /// A running task that has its requests updated counts with the new values for the peak.
    /// When it stops, the new values are the ones removed, so task 30 doesn't raise the peak.
    @Test
    public void updateOfRunningTaskChangesThePeak(@TempDir final Path tempDir) throws IOException {
        final var scenario = new TaskMappingScenario(tempDir, """
            0,,1,0,,0,userA,3,9,0.25,0.1,0.1,0
            0,,2,0,,0,userA,3,9,0.25,0.1,0.1,0
            0,,3,0,,0,userA,3,9,0.25,0.1,0.1,0
            1000000,,1,0,1,1,userA,3,9,0.25,0.1,0.1,0
            2000000,,1,0,1,8,userA,3,9,0.5,0.2,0.3,0
            3000000,,2,0,1,1,userA,3,9,0.25,0.1,0.1,0
            4000000,,1,0,1,4,userA,3,9,0.5,0.2,0.3,0
            5000000,,3,0,1,1,userA,3,9,0.25,0.1,0.1,0
            6000000,,2,0,1,4,userA,3,9,0.25,0.1,0.1,0
            6000000,,3,0,1,4,userA,3,9,0.25,0.1,0.1,0
            """);

        final var request = scenario.requests.getFirst();
        assertAll(
            () -> assertEquals(0.75, request.peakCpuCores(), DOUBLE_DELTA),
            () -> assertEquals(0.3, request.peakRam(), DOUBLE_DELTA),
            () -> assertEquals(0.4, request.peakLocalDiskSpace(), DOUBLE_DELTA)
        );
    }

    /// An update of a task that isn't running doesn't change the peak.
    @Test
    public void updateOfTaskNotRunningDoesNotChangeThePeak(@TempDir final Path tempDir) throws IOException {
        final var scenario = new TaskMappingScenario(tempDir, """
            0,,1,0,,0,userA,3,9,0.25,0.1,0.1,0
            1000000,,1,0,1,1,userA,3,9,0.25,0.1,0.1,0
            2000000,,1,0,1,4,userA,3,9,0.25,0.1,0.1,0
            3000000,,1,0,1,8,userA,3,9,0.5,0.2,0.3,0
            """);

        assertEquals(0.25, scenario.requests.getFirst().peakCpuCores(), DOUBLE_DELTA);
    }

    /// A task without a machine isn't bound to a VM, so its broker selects one.
    @Test @Timeout(TIMEOUT_SECS)
    public void taskWithoutMachineIsMappedByTheBroker(@TempDir final Path tempDir) throws IOException {
        final var scenario = new TaskMappingScenario(tempDir, TRACE_WITH_MACHINES);
        assertFalse(scenario.cloudlet(50).isBoundToVm());

        scenario.simulation.start();
        assertEquals(Cloudlet.Status.SUCCESS, scenario.cloudlet(50).getStatus());
    }

    /// A task stays in its first machine, even if it's scheduled on another one later.
    @Test @Timeout(TIMEOUT_SECS)
    public void taskRescheduledInOtherMachineStaysInTheFirstOne(@TempDir final Path tempDir) throws IOException {
        final var scenario = new TaskMappingScenario(tempDir, """
            0,,1,0,,0,userA,3,9,0.5,0.1,0.1,0
            1000000,,1,0,1,1,userA,3,9,0.5,0.1,0.1,0
            2000000,,1,0,1,2,userA,3,9,0.5,0.1,0.1,0
            3000000,,1,0,2,1,userA,3,9,0.5,0.1,0.1,0
            4000000,,1,0,2,4,userA,3,9,0.5,0.1,0.1,0
            """);
        scenario.simulation.start();

        assertAll(
            () -> assertEquals(MACHINE_1, scenario.machineOf(10)),
            () -> assertEquals(1, scenario.requests.size())
        );
    }

    /// A Host that doesn't have enough capacity isn't selected for the VM of its machine.
    @Test
    public void hostWithoutCapacityIsNotSelected(@TempDir final Path tempDir) throws IOException {
        final var scenario = new TaskMappingScenario(tempDir, TRACE_WITH_MACHINES);
        final var policy = scenario.datacenter.getVmPlacementPolicy();
        final Vm tooLargeVm = scenario.cloudlet(10).getVm().setRam(HOST_RAM * 2);

        assertTrue(scenario.reader.findHostForVm(policy, tooLargeVm).isEmpty());
    }

    /// Lines of tasks beyond the maximum number of Cloudlets don't count to the peak nor create VMs.
    @Test @Timeout(TIMEOUT_SECS)
    public void tasksWithoutCloudletAreIgnored(@TempDir final Path tempDir) throws IOException {
        final var scenario = new TaskMappingScenario(tempDir, TRACE_WITH_MACHINES, 1);

        assertAll(
            () -> assertEquals(1, scenario.requests.size()),
            () -> assertEquals(0.5, scenario.requests.getFirst().peakCpuCores(), DOUBLE_DELTA)
        );
    }

    /// A simulation with a reader that maps the tasks in [#TRACE_WITH_MACHINES] to VMs.
    private static final class TaskMappingScenario {
        private final CloudSimPlus simulation = new CloudSimPlus();
        private final List<TaskVmRequest> requests = new ArrayList<>();
        private final Datacenter datacenter;
        private final GoogleTaskEventsTraceReader reader;
        private final Collection<Cloudlet> cloudlets;

        TaskMappingScenario(final Path tempDir, final String trace) throws IOException {
            this(tempDir, trace, Integer.MAX_VALUE);
        }

        TaskMappingScenario(final Path tempDir, final String trace, final int maxCloudlets) throws IOException {
            datacenter = DatacenterSimple.of(simulation, List.of(newHost(MACHINE_1), newHost(MACHINE_2)));
            final var traceFile = Files.writeString(tempDir.resolve("task-events.csv"), trace);
            reader = new GoogleTaskEventsTraceReader(simulation, traceFile.toString(), GoogleTaskEventsTraceReaderTest::createCloudlet)
                .setMaxCloudletsToCreate(maxCloudlets)
                .mapTasksToHosts(this::createVm);
            datacenter.getVmPlacementPolicy().setFindHostForVmFunction(reader::findHostForVm);
            cloudlets = reader.process();
        }

        private static Host newHost(final long id) {
            final Host host = HostSimple.of(HOST_RAM, HOST_BW, HOST_STORAGE, new CoreCapacity(CORES, MIPS));
            host.setId(id);
            return host;
        }

        private Vm createVm(final TaskVmRequest request) {
            requests.add(request);
            return VmSimple.of(new CoreCapacity(1, MIPS)).setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE);
        }

        Cloudlet cloudlet(final long id) {
            return cloudlets.stream().filter(c -> c.getId() == id).findFirst().orElseThrow();
        }

        Vm vmOf(final long cloudletId) {
            return cloudlet(cloudletId).getVm();
        }

        long machineOf(final long cloudletId) {
            return vmOf(cloudletId).getHost().getId();
        }
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
