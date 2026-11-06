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

import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.core.SimEntity;
import org.cloudsimplus.core.events.SimEvent;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.CoreCapacity;
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
