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

import org.cloudsimplus.brokers.DatacenterBrokerSimple;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.core.SimEntity;
import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.CoreCapacity;
import org.cloudsimplus.resources.Processor;
import org.cloudsimplus.vms.VmSimple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.cloudsimplus.resources.Processor.workingCores;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 4.0.0
public class GoogleMachineEventsTraceReaderTest {
    private static final long HOST_ID = 10;
    private static final double REMOVAL_TIME = 5.0;

    private static final int MAX_CORES = 8;
    private static final long MAX_RAM = 1000;
    private static final double MIPS = 1000;
    private static final long UPDATE_CORES = 2;
    private static final long UPDATE_RAM = 750;
    private static final long HOST_BW = 1000;
    private static final long HOST_STORAGE = 10000;
    private static final long VM_RAM = 100;
    private static final long VM_BW = 100;
    private static final long VM_SIZE = 1000;
    private static final long CLOUDLET_LENGTH = 40000;

    /// The Host is added at time 0 and removed at [#REMOVAL_TIME] (5 seconds in microseconds).
    private static final String TRACE = """
        0,%1$d,0,platform,0.5,0.5
        5000000,%1$d,1,platform,0.5,0.5
        """.formatted(HOST_ID);

    /// The Datacenter that has the Host is created after the trace is processed
    /// and has a non-zero ID, which must not change the removal time.
    @Test
    public void hostRemovalIsSentOnceToTheCisAndForwardedToTheHostDatacenter(@TempDir final Path dir) throws IOException {
        final var simulation = new CloudSimPlus();
        final var capacity = new CoreCapacity(1, 1000);
        final var otherDc = DatacenterSimple.of(simulation, List.of(HostSimple.of(capacity)));

        final var traceFile = Files.writeString(dir.resolve("machine-events.csv"), TRACE);
        final var reader = new GoogleMachineEventsTraceReader(traceFile.toString(), evt -> HostSimple.of(capacity));
        reader.setDatacenterForLaterHosts(otherDc);
        final List<Host> traceHosts = new ArrayList<>(reader.process());
        final var hostDc = DatacenterSimple.of(simulation, traceHosts);

        final List<SimEvent> removalEvents = new ArrayList<>();
        simulation.listeners().addOnEventProcessing(evt -> {
            if (evt.getTag() == CloudSimTag.HOST_REMOVE) {
                removalEvents.add(evt);
            }
        });

        simulation.start();

        final List<SimEntity> destinations = removalEvents.stream().map(SimEvent::getDestination).toList();
        assertEquals(List.of(simulation.getCis(), hostDc), destinations);
        removalEvents.forEach(evt -> assertEquals(REMOVAL_TIME, evt.getTime()));
        assertEquals(0, workingCores(hostDc.getHostById(HOST_ID)));
    }

    /// Checks that each capacity field is computed from its own maximum value.
    @Test
    public void capacitiesAreComputedFromTheirOwnMaximumValues(@TempDir final Path dir) throws IOException {
        final var simulation = new CloudSimPlus();
        final var capacity = new CoreCapacity(1, 1000);
        final var traceFile = Files.writeString(dir.resolve("machine-events.csv"), "0,1,0,platform,0.5,0.25\n");
        final List<MachineEvent> events = new ArrayList<>();
        final var reader = new GoogleMachineEventsTraceReader(traceFile.toString(), evt -> {
            events.add(evt);
            return HostSimple.of(capacity);
        });
        reader.setDatacenterForLaterHosts(DatacenterSimple.of(simulation, List.of(HostSimple.of(capacity))));
        reader.setMaxCpuCores(10);
        reader.setMaxRamCapacity(1000);
        reader.process();

        assertAll(
            () -> assertEquals(5, events.getFirst().getCpuCores()),
            () -> assertEquals(250, events.getFirst().getRam())
        );
    }

    @Test
    public void hostCapacityIsUpdatedAtTheTraceTime(@TempDir final Path dir) throws IOException {
        final var simulation = new CloudSimPlus();
        final var otherDc = newDatacenter(simulation);
        final var reader = newReader(dir, otherDc, """
            0,10,0,platform,0.5,0.5
            5000000,10,2,platform,0.25,0.75
            """);
        final var host = new ArrayList<Host>(reader.process()).getFirst();
        final var hostDc = DatacenterSimple.of(simulation, List.of(host));

        final List<SimEvent> updateEvents = new ArrayList<>();
        simulation.listeners().addOnEventProcessing(evt -> {
            if (evt.getTag() == CloudSimTag.HOST_UPDATE) {
                updateEvents.add(evt);
            }
        });
        simulation.start();

        final List<SimEntity> destinations = updateEvents.stream().map(SimEvent::getDestination).toList();
        assertAll(
            () -> assertEquals(List.of(simulation.getCis(), hostDc), destinations),
            () -> updateEvents.forEach(evt -> assertEquals(REMOVAL_TIME, evt.getTime())),
            () -> assertEquals(UPDATE_CORES, Processor.cores(host)),
            () -> assertEquals(UPDATE_RAM, host.getRam().getCapacity()),
            () -> assertEquals(UPDATE_CORES, Processor.freeCores(host))
        );
    }

    /// A blank capacity field means that capacity doesn't change.
    @Test
    public void blankCapacityFieldKeepsCurrentCapacity(@TempDir final Path dir) throws IOException {
        final var simulation = new CloudSimPlus();
        final var reader = newReader(dir, newDatacenter(simulation), """
            0,10,0,platform,0.5,0.5
            0,20,0,platform,0.5,0.5
            5000000,10,2,platform,,0.75
            5000000,20,2,platform,0.25,
            """);
        final List<Host> hosts = new ArrayList<>(reader.process());
        final var dc = DatacenterSimple.of(simulation, hosts);
        simulation.start();

        final Host cpuBlank = dc.getHostById(10);
        final Host ramBlank = dc.getHostById(20);
        assertAll(
            () -> assertEquals(MAX_CORES / 2, Processor.cores(cpuBlank)),
            () -> assertEquals(UPDATE_RAM, cpuBlank.getRam().getCapacity()),
            () -> assertEquals(UPDATE_CORES, Processor.cores(ramBlank)),
            () -> assertEquals(MAX_RAM / 2, ramBlank.getRam().getCapacity())
        );
    }

    /// A capacity lower than what running VMs use is refused, since VMs aren't evicted.
    /// Each resource is handled on its own, so the RAM change is still applied.
    @Test
    public void capacityLowerThanInUseIsRefused(@TempDir final Path dir) throws IOException {
        final var simulation = new CloudSimPlus();
        final var reader = newReader(dir, newDatacenter(simulation), """
            0,10,0,platform,0.5,0.5
            5000000,10,2,platform,0.125,0.75
            """);
        final var host = new ArrayList<Host>(reader.process()).getFirst();
        DatacenterSimple.of(simulation, List.of(host));

        final var broker = new DatacenterBrokerSimple(simulation);
        broker.submitVm(VmSimple.of(new CoreCapacity(MAX_CORES / 2, MIPS)).setRam(VM_RAM).setBw(VM_BW).setSize(VM_SIZE));
        broker.submitCloudlet(CloudletSimple.of(CLOUDLET_LENGTH, MAX_CORES / 2));
        simulation.start();

        assertAll(
            () -> assertEquals(MAX_CORES / 2, Processor.cores(host)),
            () -> assertEquals(UPDATE_RAM, host.getRam().getCapacity())
        );
    }

    /// A Host removed and added again has a new instance, in the Datacenter for later Hosts.
    /// Requests that come later must reach this instance, not the removed one.
    /// The Datacenter of the removed Host is created first, so that it's the first one the CIS checks.
    @Test
    public void requestsAfterHostIsAddedAgainReachTheNewInstance(@TempDir final Path dir) throws IOException {
        final var simulation = new CloudSimPlus();
        final var firstDc = newDatacenter(simulation);
        final var laterDc = newDatacenter(simulation);
        final var reader = newReader(dir, laterDc, """
            0,10,0,platform,0.5,0.5
            1000000,10,1,platform,0.5,0.5
            2000000,10,0,platform,0.5,0.5
            3000000,10,2,platform,0.25,0.75
            4000000,10,1,platform,0.25,0.75
            """);
        final var firstHost = new ArrayList<Host>(reader.process()).getFirst();
        firstDc.addHost(firstHost);
        final Host secondHost = reader.getLaterAvailableHosts().getFirst();
        simulation.start();

        assertAll(
            () -> assertEquals(MAX_CORES / 2, Processor.cores(firstHost)),
            () -> assertEquals(UPDATE_CORES, Processor.cores(secondHost)),
            () -> assertEquals(UPDATE_RAM, secondHost.getRam().getCapacity()),
            () -> assertEquals(0, workingCores(secondHost))
        );
    }

    /// Creates a Datacenter with a mutable Host list, since Hosts may be added to it during the simulation.
    private static Datacenter newDatacenter(final CloudSimPlus simulation) {
        final List<Host> hosts = new ArrayList<>();
        hosts.add(HostSimple.of(new CoreCapacity(1, MIPS)));
        return DatacenterSimple.of(simulation, hosts);
    }

    private static GoogleMachineEventsTraceReader newReader(final Path dir, final Datacenter laterDc, final String trace) throws IOException {
        final var traceFile = Files.writeString(dir.resolve("machine-events.csv"), trace);
        final var reader = new GoogleMachineEventsTraceReader(traceFile.toString(), GoogleMachineEventsTraceReaderTest::createHost);
        reader.setDatacenterForLaterHosts(laterDc);
        reader.setMaxCpuCores(MAX_CORES);
        reader.setMaxRamCapacity(MAX_RAM);
        return reader;
    }

    private static Host createHost(final MachineEvent evt) {
        return HostSimple.of(evt.getRam(), HOST_BW, HOST_STORAGE, new CoreCapacity(evt.getCpuCores(), MIPS));
    }

    /// Ensures the order or enums is not changed,
    /// because that will cause the enums ordinal values to change.
    /// These values are defined in the Google Cluster Data documentation.
    @Test
    public void testEventType(){
        assertAll(
            () -> assertEquals(0, MachineEventType.ADD.ordinal()),
            () -> assertEquals(1, MachineEventType.REMOVE.ordinal()),
            () -> assertEquals(2, MachineEventType.UPDATE.ordinal())
        );
    }

    /// Ensures the order or enums is not changed,
    /// because that will cause the enums ordinal values to change.
    /// These values are defined in the Google Cluster Data documentation.
    @Test
    public void testFieldIndex(){
        assertAll(
            () -> assertEquals(0, MachineEventField.TIMESTAMP.ordinal()),
            () -> assertEquals(1, MachineEventField.MACHINE_ID.ordinal()),
            () -> assertEquals(2, MachineEventField.EVENT_TYPE.ordinal()),
            () -> assertEquals(3, MachineEventField.PLATFORM_ID.ordinal()),
            () -> assertEquals(4, MachineEventField.CPU_CAPACITY.ordinal()),
            () -> assertEquals(5, MachineEventField.RAM_CAPACITY.ordinal())
        );
    }

}
