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
package org.cloudsimplus.faultinjection;

import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.distributions.UniformDistr;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.resources.CoreStatus;
import org.cloudsimplus.resources.Processor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;

/// @author Manoel Campos da Silva Filho
class HostFaultInjectionTest {
    private static final int HOST_PES = 8;
    private static final double MIPS = 1000;

    @Test
    void testFailuresOnlyBusyCores() {
        final Host host = createHost();
        final Processor processor = host.getProcessor();
        processor.moveCores(CoreStatus.FREE, CoreStatus.BUSY, HOST_PES);

        createFaultInjection(host, 1).generateHostFault(host, 3);
        assertCoreCounts(processor, 0, HOST_PES - 3, 3);
    }

    @Test
    void testFailuresOnlyFreeCores() {
        final Host host = createHost();
        final Processor processor = host.getProcessor();

        createFaultInjection(host, 1).generateHostFault(host, 3);
        assertCoreCounts(processor, HOST_PES - 3, 0, 3);
    }

    @Test
    void testFailuresLimitedToWorkingCores() {
        final Host host = createHost();
        final Processor processor = host.getProcessor();
        processor.moveCores(CoreStatus.FREE, CoreStatus.BUSY, 2);

        createFaultInjection(host, 1).generateHostFault(host, HOST_PES * 2);
        assertCoreCounts(processor, 0, 0, HOST_PES);
    }

    /// Checks that failed cores are randomly chosen among free and busy ones,
    /// using different seeds, and the total number of cores is kept.
    @Test
    void testFailuresRandomlyChosenAmongFreeAndBusyCores() {
        final int failures = 4;
        final List<Long> busyFailures = LongStream.range(0, 30).mapToObj(seed -> {
            final Host host = createHost();
            final Processor processor = host.getProcessor();
            processor.moveCores(CoreStatus.FREE, CoreStatus.BUSY, HOST_PES / 2);

            createFaultInjection(host, seed).generateHostFault(host, failures);
            assertEquals(failures, processor.getFailedCoresNumber());
            assertEquals(HOST_PES, sumCores(processor));
            return HOST_PES / 2 - processor.getCoresNumber(CoreStatus.BUSY);
        }).toList();

        final long distinctResults = busyFailures.stream().distinct().count();
        assertTrue(distinctResults > 1, "Failed cores should not always be taken from the same status: " + busyFailures);
    }

    private static void assertCoreCounts(final Processor processor, final long free, final long busy, final long failed) {
        assertAll(
            () -> assertEquals(free, processor.getCoresNumber(CoreStatus.FREE), "free"),
            () -> assertEquals(busy, processor.getCoresNumber(CoreStatus.BUSY), "busy"),
            () -> assertEquals(failed, processor.getCoresNumber(CoreStatus.FAILED), "failed")
        );
    }

    private static long sumCores(final Processor processor) {
        long sum = 0;
        for (final CoreStatus status : CoreStatus.values()) {
            sum += processor.getCoresNumber(status);
        }

        return sum;
    }

    private static Host createHost() {
        return new HostSimple(HOST_PES, MIPS);
    }

    private static HostFaultInjection createFaultInjection(final Host host, final long seed) {
        final var simulation = new CloudSimPlus();
        final var datacenter = new DatacenterSimple(simulation, List.of(host));
        return new HostFaultInjection(datacenter, new UniformDistr(seed));
    }
}
